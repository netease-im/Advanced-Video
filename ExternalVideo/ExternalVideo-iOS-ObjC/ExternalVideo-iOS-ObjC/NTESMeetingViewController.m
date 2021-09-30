//
//  NTESMeetingViewController.m
//  ExternalVideo-iOS-ObjC
//
//  Created by 丁文超 on 2020/3/23.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NTESMeetingViewController.h"
#import <NERtcSDK/NERtcSDK.h>
#import <CoreServices/CoreServices.h>
#import <CoreMedia/CoreMedia.h>
#import <AVFoundation/AVFoundation.h>
#import "NTESAppConfig.h"
#import "NTESExternalVideoReader.h"

@interface NTESMeetingViewController () <NERtcEngineDelegateEx,UIImagePickerControllerDelegate,UINavigationControllerDelegate, NTESExternalVideoReaderDelegate>

@property (strong, nonatomic) IBOutlet UIView *localUserView;
@property (strong, nonatomic) IBOutletCollection(UIView) NSArray *remoteUserViews;

@property (strong, nonatomic) IBOutlet UIButton *selectVideoButton;
@property (strong, nonatomic) IBOutlet UIButton *hangupButton;
@property (strong, nonatomic) IBOutlet UIProgressView *progressView;

@property (strong, nonatomic) NTESExternalVideoReader *videoReader;

@end

@implementation NTESMeetingViewController

#pragma mark - Life Cycle

- (void)dealloc
{
    NSLog(@"%s", __FUNCTION__);
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self setupUI];
    [self setupRTCEngine];
    [self joinCurrentRoom];
}

#pragma mark - Functions

- (void)setupUI
{
    self.title = [NSString stringWithFormat:@"Room %@", self.roomID];
    self.selectVideoButton.layer.cornerRadius = 8;
    self.selectVideoButton.clipsToBounds = YES;
    self.hangupButton.layer.cornerRadius = 8;
    self.hangupButton.layer.cornerRadius = 8;
}

- (void)setupRTCEngine
{
    //默认情况下日志会存储在App沙盒的Documents目录下
    NERtcLogSetting *logSetting = [[NERtcLogSetting alloc] init];
#if DEBUG
    logSetting.logLevel = kNERtcLogLevelInfo;
#else
    logSetting.logLevel = kNERtcLogLevelWarning;
#endif
    
    NERtcEngineContext *context = [[NERtcEngineContext alloc] init];
    context.engineDelegate = self;
    context.appKey = kAppKey;
    context.logSetting = logSetting;
    [[NERtcEngine sharedEngine] setupEngineWithContext:context];
}

- (void)destroyRTCEngineWithCompletion:(void(^)(void))completion
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0), ^{
        [NERtcEngine destroyEngine];
        
        if (completion) {
            completion();
        }
    });
}

- (void)joinCurrentRoom
{
    NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
    
    //多人音视频通话场景的音频推荐配置
    //其他场景下请联系云信技术支持获取配置
    [coreEngine setAudioProfile:kNERtcAudioProfileStandard
                       scenario:kNERtcAudioScenarioSpeech];
    
    //开启外部视频源
    [coreEngine setExternalVideoSource:YES isScreen:NO];
    
    [coreEngine enableLocalAudio:YES];
    [coreEngine enableLocalVideo:YES];
    
    __weak typeof(self) weakSelf = self;
    [coreEngine joinChannelWithToken:@"" channelName:self.roomID myUid:self.userID completion:^(NSError * _Nullable error, uint64_t channelId, uint64_t elapesd) {
        if (error) {
            //加入失败，弹框之后退出当前页面
            NSString *message = [NSString stringWithFormat:@"join channel fail.code:%@", @(error.code)];
            [weakSelf showDismissAlertWithMessage:message actionBlock:^{
                [weakSelf destroyRTCEngineWithCompletion:^{
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [weakSelf.navigationController popViewControllerAnimated:YES];
                    });
                }];
            }];
        } else {
            //加入成功，建立本地canvas渲染本地视图
            NERtcVideoCanvas *canvas = [[NERtcVideoCanvas alloc] init];
            canvas.container = self.localUserView;
            [coreEngine setupLocalVideoCanvas:canvas];
        }
    }];
}

#pragma mark - SDK回调（含义请参考NERtcEngineDelegateEx定义）

- (void)onNERtcEngineDidError:(NERtcError)errCode
{
    NSString *message = [NSString stringWithFormat:@"nertc engine did error.code:%@", @(errCode)];
    [self showDismissAlertWithMessage:message actionBlock:^{
        [[NERtcEngine sharedEngine] leaveChannel];
    }];
}

- (void)onNERtcEngineUserDidJoinWithUserID:(uint64_t)userID userName:(NSString *)userName
{
    NERtcVideoCanvas *canvas = [[NERtcVideoCanvas alloc] init];
    for (UIView *view in self.remoteUserViews) {
        if (view.tag == 0) {
            canvas.container = view;
            [NERtcEngine.sharedEngine setupRemoteVideoCanvas:canvas forUserID:userID];
            view.tag = (NSInteger)userID;
            break;
        }
    }
}

- (void)onNERtcEngineUserVideoDidStartWithUserID:(uint64_t)userID videoProfile:(NERtcVideoProfileType)profile
{
    [NERtcEngine.sharedEngine subscribeRemoteVideo:YES forUserID:userID streamType:kNERtcRemoteVideoStreamTypeHigh];
}

- (void)onNERtcEngineUserDidLeaveWithUserID:(uint64_t)userID reason:(NERtcSessionLeaveReason)reason
{
    [self.view viewWithTag:(NSInteger)userID].tag = 0;
}

- (void)onNERtcEngineDidDisconnectWithReason:(NERtcError)reason
{
    //网络连接中断时会触发该回调，触发之后的操作则由开发者按需实现
}

- (void)onNERtcEngineDidLeaveChannelWithResult:(NERtcError)result
{
    //调用leaveChannel之后，若需要释放SDK资源，建议在收到该回调之后，再调用destroyEngine
    [self destroyRTCEngineWithCompletion:^{
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.navigationController popViewControllerAnimated:YES];
        });
    }];
}

#pragma mark - UIImagePickerControllerDelegate

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<UIImagePickerControllerInfoKey,id> *)info
{
    [self dismissViewControllerAnimated:YES completion:nil];
    NSURL *videoURL = info[UIImagePickerControllerMediaURL];
    if (videoURL) {
        NSError *error;
        self.videoReader = [[NTESExternalVideoReader alloc] initWithURL:videoURL error:&error];
        if (error) {
            NSLog(@"Create video reader error: \n%@", error.localizedDescription);
            return;
        }
        self.videoReader.delegate = self;
        [self.videoReader startReading];
    }
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - NTESExternalVideoReaderDelegate

- (void)videoReader:(NTESExternalVideoReader *)videoReader didReadSampleBuffer:(CMSampleBufferRef)sampleBuffer totalFramesWritten:(NSUInteger)totalFramesWritten totalFrames:(NSUInteger)totalFrames
{
    CVImageBufferRef pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer);
    NERtcVideoFrame *frame = [[NERtcVideoFrame alloc] init];
    frame.format = kNERtcVideoFormatNV12;
    frame.width = (uint32_t)CVPixelBufferGetWidth(pixelBuffer);
    frame.height = (uint32_t)CVPixelBufferGetHeight(pixelBuffer);
    frame.buffer = (void *)pixelBuffer;
    switch (videoReader.rotationDegree) {
        case 90:
            frame.rotation = kNERtcVideoRotation_90;
            break;
        case 180:
            frame.rotation = kNERtcVideoRotation_180;
            break;
        case 270:
            frame.rotation = kNERtcVideoRotation_270;
            break;
        case 0:
        default:
            frame.rotation = kNERtcVideoRotation_0;
            break;
    }
    [NERtcEngine.sharedEngine pushExternalVideoFrame:frame];
    self.progressView.progress = totalFramesWritten/(CGFloat)totalFrames;
}

#pragma mark - Actions

- (IBAction)onClickSelectVideo:(id)sender
{
    UIImagePickerController *imagePicker = [[UIImagePickerController alloc] init];
    imagePicker.delegate = self;
    imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    imagePicker.mediaTypes = @[(__bridge NSString *)kUTTypeMovie, (__bridge NSString *)kUTTypeVideo];
    [self presentViewController:imagePicker animated:YES completion:nil];
}

- (IBAction)onLeaveMeeting:(id)sender
{
    [self.videoReader stopReading];
    [NERtcEngine.sharedEngine leaveChannel];
}

#pragma mark - Helper

- (void)showDismissAlertWithMessage:(NSString *)message actionBlock:(void(^)(void))actionBlock
{
    UIAlertController *alertVC = [UIAlertController alertControllerWithTitle:@"退出提示"
                                                                     message:message
                                                              preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *exitAction = [UIAlertAction actionWithTitle:@"退出"
                                                         style:UIAlertActionStyleDefault
                                                       handler:^(UIAlertAction * _Nonnull action) {
        if (actionBlock) {
            actionBlock();
        }
    }];
    [alertVC addAction:exitAction];
    [self presentViewController:alertVC animated:YES completion:nil];
}

@end
