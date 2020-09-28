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

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self setupUI];
    [self setupRTCEngine];
    [self joinCurrentRoom];
}

- (void)dealloc
{
    [NERtcEngine.sharedEngine leaveChannel];
    [NERtcEngine destroyEngine];
}

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
    NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
    NERtcEngineContext *context = [[NERtcEngineContext alloc] init];
    context.engineDelegate = self;
    context.appKey = kAppKey;
    [coreEngine setupEngineWithContext:context];
    [coreEngine setExternalVideoSource:YES];
    [coreEngine enableLocalAudio:YES];
    [coreEngine enableLocalVideo:YES];
}

- (void)joinCurrentRoom
{
    [NERtcEngine.sharedEngine joinChannelWithToken:@"" channelName:self.roomID myUid:self.userID completion:^(NSError * _Nullable error, uint64_t channelId, uint64_t elapesd) {
        NERtcVideoCanvas *canvas = [[NERtcVideoCanvas alloc] init];
        canvas.container = self.localUserView;
        [NERtcEngine.sharedEngine setupLocalVideoCanvas:canvas];
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

- (void)videoReader:(NTESExternalVideoReader *)videoReader didReadSampleBuffer:(CMSampleBufferRef)sampleBuffer totalFramesWritten:(NSInteger)totalFramesWritten totalFrames:(NSInteger)totalFrames
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

- (IBAction)onClickSelectVideo:(id)sender
{
    UIImagePickerController *imagePicker = [[UIImagePickerController alloc] init];
    imagePicker.delegate = self;
    imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    imagePicker.mediaTypes = @[(__bridge NSString *)kUTTypeMovie, (__bridge NSString *)kUTTypeVideo];
    [self presentViewController:imagePicker animated:YES completion:nil];
}

@end
