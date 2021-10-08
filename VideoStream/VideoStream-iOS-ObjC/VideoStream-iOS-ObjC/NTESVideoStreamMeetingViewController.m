//
//  NTESVideoStreamMeetingViewController.m
//  NERtcVideoStreamSample
//
//  Created by 丁文超 on 2020/3/23.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NTESVideoStreamMeetingViewController.h"
#import <NERtcSDK/NERtcSDK.h>
#import "NTESConfig.h"
#import "NTESVideoConfigViewController.h"
#import <Toast/Toast.h>

@interface NTESVideoStreamMeetingViewController () <NERtcEngineDelegateEx>

@property (strong, nonatomic) IBOutlet UIView *localUserView;
@property (strong, nonatomic) IBOutletCollection(UIView) NSArray *remoteUserViews;

@property (strong, nonatomic) NERtcLiveStreamTaskInfo *liveStreamTask;
@property (strong, nonatomic) NSMutableArray<NSNumber *> *userList;
@property (weak, nonatomic) IBOutlet UIButton *configButton;
@property (weak, nonatomic) IBOutlet UIButton *hungupButton;
/// 是否在推流中
@property(assign,nonatomic)BOOL isPushingStream;

@end

@implementation NTESVideoStreamMeetingViewController

#pragma mark - Life Cycle

- (void)dealloc
{
    NSLog(@"%s", __FUNCTION__);
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    NTESVideoConfigViewController *configVC = segue.destinationViewController;
    configVC.currentURL = self.liveStreamTask.streamURL;
    configVC.isPushingStream = self.isPushingStream;
    configVC.delegate = self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self.navigationItem setHidesBackButton:YES];
    self.configButton.layer.cornerRadius = 8;
    self.hungupButton.layer.cornerRadius = 8;
    self.title = [NSString stringWithFormat:@"Room %@", self.roomID];
    self.userList = [NSMutableArray arrayWithObject:@(self.userID)];
    
    [self setupRTCEngine];
    [self joinCurrentRoom];
}

#pragma mark - Function

- (void)setupRTCEngine
{
    NSAssert(![kAppKey isEqualToString:@"<#AppKey#>"], @"请设置AppKey");
    
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
    
    //互动直播场景的视频推荐配置
    //其他场景下请联系云信技术支持获取配置
    NERtcVideoEncodeConfiguration *config = [[NERtcVideoEncodeConfiguration alloc] init];
    config.width = 960;
    config.height = 540;
    config.frameRate = kNERtcVideoFrameRateFps15;
    [coreEngine setLocalVideoConfig:config];
    
    //互动直播场景的音频推荐配置
    //其他场景下请联系云信技术支持获取配置
    [coreEngine setAudioProfile:kNERtcAudioProfileHighQuality
                       scenario:kNERtcAudioScenarioChatRoom];
    
    //互动直播场景的房间推荐配置
    //其他场景下请联系云信技术支持获取配置
    [coreEngine setChannelProfile:kNERtcChannelProfileLiveBroadcasting];
    
    [coreEngine enableLocalAudio:YES];
    [coreEngine enableLocalVideo:YES];
    
    //打开推流
    [coreEngine setParameters:@{kNERtcKeyPublishSelfStreamEnabled: @YES}];
    
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
            canvas.container = weakSelf.localUserView;
            [coreEngine setupLocalVideoCanvas:canvas];
            
            //添加推流任务
            [weakSelf addLiveStream:kStreamURL];
        }
    }];
}

- (void)addLiveStream:(NSString *)streamURL
{
    NSAssert(![streamURL isEqualToString:@"<#推流地址#>"], @"请设置推流地址");
    
    self.liveStreamTask = [[NERtcLiveStreamTaskInfo alloc] init];
    NSString *taskID = [NSString stringWithFormat:@"%d",arc4random()/100];
    self.liveStreamTask.taskID = taskID;
    self.liveStreamTask.streamURL = streamURL;
    self.liveStreamTask.lsMode = kNERtcLsModeVideo;
    
    NSInteger layoutWidth = 720;
    NSInteger layoutHeight = 1280;
    //设置整体布局
    NERtcLiveStreamLayout *layout = [[NERtcLiveStreamLayout alloc] init];
    layout.width = layoutWidth; //整体布局宽度
    layout.height = layoutHeight; //整体布局高度
    // layout.bgImage = <#在这里设置背景图片(可选)#>
    // layout.backgroundColor = <#在这里设置背景色(可选)#>
    self.liveStreamTask.layout = layout;
    
    [self reloadUsers];
    int ret = [NERtcEngine.sharedEngine addLiveStreamTask:self.liveStreamTask
                                               compeltion:^(NSString * _Nonnull taskId, kNERtcLiveStreamError errorCode) {
        if (errorCode == 0) {
            self.isPushingStream = YES;
        }else {
            self.isPushingStream = NO;
            self.liveStreamTask = nil;
        }
        NSString *message = !errorCode ? @"添加成功" : [NSString stringWithFormat:@"添加失败 error = %@",NERtcErrorDescription(errorCode)];
        NSLog(@"%@", message);
        [self.view makeToast:message];
    }];
    if (ret != 0) {
        self.isPushingStream = NO;
        self.liveStreamTask = nil;
        NSLog(@"添加推流任务失败");
        [self.view makeToast:@"添加推流任务失败"];
    }
}

- (void)updateLiveStreamTask
{
    int ret = [NERtcEngine.sharedEngine updateLiveStreamTask:self.liveStreamTask
                                                  compeltion:^(NSString * _Nonnull taskId, kNERtcLiveStreamError errorCode) {
        self.isPushingStream = !errorCode ? YES:NO;//errorCode == 0表示成功
        NSString *message = !errorCode ? @"更新成功" : [NSString stringWithFormat:@"更新失败 error = %@",NERtcErrorDescription(errorCode)];
        NSLog(@"%@", message);
        [self.view makeToast:message];
    }];
    if (ret != 0) {
        self.isPushingStream = NO;
        NSLog(@"更新推流任务失败");
        [self.view makeToast:@"更新推流任务失败"];
    }
}

// 根据self.usersForStreaming生成直播成员信息

/// 设置4人视频画面“田”字布局，旁路推流是将多路视频流同步到云端进行混流成一路流，客户端可以通过拉流地址获取到多人画面，此UI配置是指定服务端混流后各个画面的布局。
- (void)reloadUsers
{
    NSInteger layoutWidth = self.liveStreamTask.layout.width;
    NSInteger userWidth = 320;
    NSInteger userHeight = 480;
    NSInteger horizPadding = (layoutWidth-userWidth*2)/3;
    NSInteger vertPadding = 16;
    NSMutableArray *res = NSMutableArray.array;
    for (NSInteger i = 0; i < self.userList.count; i++) {
        NSInteger column = i % 2;
        NSInteger row = i / 2;
        NSNumber *userID = self.userList[i];
        NERtcLiveStreamUserTranscoding *userTranscoding = [[NERtcLiveStreamUserTranscoding alloc] init];
        userTranscoding.uid = userID.unsignedLongValue;
        userTranscoding.audioPush = YES;
        userTranscoding.videoPush = YES;
        userTranscoding.x = column == 0 ? horizPadding : horizPadding * 2 + userWidth;
        userTranscoding.y = vertPadding * (row + 1) + userHeight * row;
        userTranscoding.width = userWidth;
        userTranscoding.height = userHeight;
        userTranscoding.adaption = kNERtcLsModeVideoScaleCropFill;
        [res addObject:userTranscoding];
    }
    self.liveStreamTask.layout.users = [NSArray arrayWithArray:res];
}

#pragma mark - Action

- (IBAction)hungupEvent:(id)sender
{
    if (self.liveStreamTask) {
        __weak typeof(self)weakSelf = self;
        int ret = [NERtcEngine.sharedEngine removeLiveStreamTask:self.liveStreamTask.taskID compeltion:^(NSString * _Nonnull taskId, kNERtcLiveStreamError errorCode) {
            NSLog(@"移除任务[%@] error = %@",taskId, NERtcErrorDescription(errorCode));
            if (errorCode == 0) {
                weakSelf.liveStreamTask = nil;
            }
            [NERtcEngine.sharedEngine leaveChannel];
        }];
        if (ret != 0) {
            NSLog(@"移除任务失败");
        }
    } else {
        [NERtcEngine.sharedEngine leaveChannel];
    }
}

#pragma mark - NTESVideoConfigVCDelegate

- (void)didGetStreamURL:(NSString *)URLString
{
    if (!self.liveStreamTask) {
        [self addLiveStream:URLString];
    }else {
        self.liveStreamTask.streamURL = URLString;
        [self updateLiveStreamTask];
    }
}

- (void)stopPushStream
{
    int res = [NERtcEngine.sharedEngine removeLiveStreamTask:self.liveStreamTask.taskID compeltion:^(NSString * _Nonnull taskId, kNERtcLiveStreamError errorCode) {
        if (errorCode == 0) {
            self.isPushingStream = NO;
            self.liveStreamTask = nil;
            [self.view makeToast:@"移除推流任务成功"];
        }else {
            NSString *errorMsg = [NSString stringWithFormat:@"移除推流任务失败:%@",NERtcErrorDescription(errorCode)];
            [self.view makeToast:errorMsg];
        }
        
    }];
    if (res != 0) {
        NSLog(@"移除推流任务失败");
        [self.view makeToast:@"移除推流任务失败"];
    }
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

- (void)onNERtcEngineUserDidLeaveWithUserID:(uint64_t)userID reason:(NERtcSessionLeaveReason)reason
{
    [self.view viewWithTag:(NSInteger)userID].tag = 0;
}

- (void)onNERtcEngineUserVideoDidStartWithUserID:(uint64_t)userID videoProfile:(NERtcVideoProfileType)profile
{
    [NERtcEngine.sharedEngine subscribeRemoteVideo:YES forUserID:userID streamType:kNERtcRemoteVideoStreamTypeHigh];
    if (![self.userList containsObject:@(userID)]) {
        // 新加入用户，添加至直播成员
        [self.userList addObject:@(userID)];
        [self reloadUsers];
        [self updateLiveStreamTask];
    }
}

- (void)onNERtcEngineUserVideoDidStop:(uint64_t)userID
{
    if ([self.userList containsObject:@(userID)]) {
        // 用户离开，从直播成员中移除
        [self.userList removeObject:@(userID)];
        [self reloadUsers];
        [self updateLiveStreamTask];
    }
}

- (void)onNERTCEngineLiveStreamState:(NERtcLiveStreamStateCode)state taskID:(NSString *)taskID url:(NSString *)url
{
    switch (state) {
        case kNERtcLsStatePushing:
            NSLog(@"Pushing stream for task [%@]", taskID);
            break;
        case kNERtcLsStatePushStopped:
            NSLog(@"Stream for task [%@] stopped", taskID);
            break;
        case kNERtcLsStatePushFail:
            NSLog(@"Stream for task [%@] failed", taskID);
            break;
        default:
            NSLog(@"Unknown state for task [%@]", taskID);
            break;
    }
}

- (void)onNERtcEngineDidDisconnectWithReason:(NERtcError)reason
{
    //网络连接中断时会触发该回调，触发之后的操作则由开发者按需实现
    //此时已与房间断开连接，如果需要重新加入房间，必须再次调用join接口
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
