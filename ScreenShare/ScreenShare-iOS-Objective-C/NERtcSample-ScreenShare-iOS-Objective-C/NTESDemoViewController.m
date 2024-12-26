//
//  NTESDemoViewController.m
//  NERtcScreenShareSample
//
//  Created by 丁文超 on 2020/7/13.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NTESDemoViewController.h"
#import <NERtcSDK/NERtcSDK.h>
#import <NERtcReplayKit/NERtcReplayKit.h>
#import "AppKey.h"

static NSString *kAppGroup = @"group.com.netease.nmc.NERtcSample-ScreenShare-iOS-Objective-C"; //!<需要替换自己的App Group

@interface NTESDemoViewController () <NERtcEngineDelegateEx, NEScreenShareHostDelegate>

@property (nonatomic, strong) NEScreenShareHost *shareHost;
@property (nonatomic, strong) NSNumber *currentUserID; //!< 当前用户ID

@property (nonatomic, assign) BOOL cameraOn;
@property (nonatomic, assign) BOOL screenShareOn;

@property (strong, nonatomic) IBOutlet UITextField *userIDTextField;
@property (strong, nonatomic) IBOutlet UITextField *roomIDTextField;
@property (strong, nonatomic) IBOutlet UIButton *joinButton;

@property (strong, nonatomic) IBOutlet UIView *localUserView;
@property (strong, nonatomic) IBOutlet UIView *remoteUserView;

@property (weak, nonatomic) IBOutlet UIView *localScreenShareView;
@property (weak, nonatomic) IBOutlet UIView *remoteScreenShareView;
@property (weak, nonatomic) IBOutlet UIButton *cameraButton;
@property (weak, nonatomic) IBOutlet UIButton *screenShareButton;

- (IBAction)onJoinClick:(id)sender;
- (IBAction)onLeaveClick:(id)sender;

@end

@implementation NTESDemoViewController

#pragma mark - Life Cycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.userIDTextField.text = [self randomUserID];
    [self setupRTCEngine];
    [self setupShareKit];
    [self addSystemBroadcastPickerIfPossible];
}

- (void)dealloc
{
    [NERtcEngine.sharedEngine leaveChannel];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [NERtcEngine destroyEngine];
    });
}

- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [super touchesEnded:touches withEvent:event];
    [self.view endEditing:YES];
}

#pragma mark- ScreenShare

- (void)setupShareKit {
    NEScreenShareHostOptions *options = [[NEScreenShareHostOptions alloc] init];
#if DEBUG
    options.enableDebug = YES;;
#endif
    options.appGroup = kAppGroup;
    options.delegate = self;
    //定制参数，具体见API文档
    options.extraInfoDict = @{};
    self.shareHost = [NEScreenShareHost sharedInstance];
    [self.shareHost setupScreenshareOptions:options];
}

/// 自定义消息通知，可以用于Extension与Host App的信息广播通知
- (void)onNERtcReplayKitNotifyCustomInfo:(NSDictionary *)info {
  NSLog(@"replaykit custom info:%@", info.description);
}

/// 音频帧回调
- (void)onReceiveAudioFrame:(NEScreenShareAudioFrame *)audioFrame {
  //为音频共享逻辑
  //此处省略
}

/// 视频帧回调
- (void)onReceiveVideoFrame:(NEScreenShareVideoFrame *)videoFrame {
    NERtcVideoFrame *frame = [[NERtcVideoFrame alloc] init];
    frame.format = kNERtcVideoFormatI420;
    frame.width = videoFrame.width;
    frame.height = videoFrame.height;
    frame.buffer = (void *)[videoFrame.videoData bytes];
    frame.timestamp = videoFrame.timeStamp;
    frame.rotation = (NERtcVideoRotationType)videoFrame.rotation;
    int ret = [NERtcEngine.sharedEngine pushExternalVideoFrame:frame];
    if (ret != 0) {
        NSLog(@"发送视频流失败:%d", ret);
    }
}

#pragma mark - SDK回调（含义请参考NERtcEngineDelegateEx定义）

- (void)onNERtcEngineUserDidJoinWithUserID:(uint64_t)userID userName:(NSString *)userName
{
    //设置对方摄像头画布
    NERtcVideoCanvas *canvas = [[NERtcVideoCanvas alloc] init];
    canvas.container = self.remoteUserView;
    [NERtcEngine.sharedEngine setupRemoteVideoCanvas:canvas forUserID:userID];
    
    //设置对方屏幕共享画布
    NERtcVideoCanvas *subCanvas = [[NERtcVideoCanvas alloc] init];
    subCanvas.container = self.remoteScreenShareView;
    [NERtcEngine.sharedEngine setupRemoteSubStreamVideoCanvas:subCanvas forUserID:userID];
}

- (void)onNERtcEngineUserVideoDidStartWithUserID:(uint64_t)userID videoProfile:(NERtcVideoProfileType)profile
{
    [NERtcEngine.sharedEngine subscribeRemoteVideo:YES forUserID:userID streamType:kNERtcRemoteVideoStreamTypeHigh];
}

- (void)onNERtcEngineUserSubStreamDidStartWithUserID:(uint64_t)userID subStreamProfile:(NERtcVideoProfileType)profile
{
    //订阅对方屏幕共享流
    [NERtcEngine.sharedEngine subscribeRemoteSubStreamVideo:YES forUserID:userID];
}

- (void)onNERtcEngineDidDisconnectWithReason:(NERtcError)reason {
    //网络连接中断时会触发该回调，触发之后的操作则由开发者按需实现
    //此时已与房间断开连接，如果需要重新加入房间，必须再次调用join接口
}

- (void)onNERtcEngineDidLeaveChannelWithResult:(NERtcError)result
{
    if (result == kNERtcNoError) {
        self.currentUserID = nil; // clear user id
    }
}

#pragma mark - Action

- (IBAction)onCameraButtonClick:(id)sender {
    [NERtcEngine.sharedEngine enableLocalVideo:!_cameraOn];
    _cameraOn = !_cameraOn;
}

- (IBAction)onScreenShareButtonClick:(id)sender {
    //开关屏幕共享流，屏幕共享数据源需要自己手动开关，不由代码控制
    if (_screenShareOn) {
        [NERtcEngine.sharedEngine stopScreenCapture];
    }
    else{
        NERtcVideoSubStreamEncodeConfiguration *config = [[NERtcVideoSubStreamEncodeConfiguration alloc] init];
        [NERtcEngine.sharedEngine startScreenCapture:config];
    }
    _screenShareOn = !_screenShareOn;
    NSLog(@"rtc sdk 是否开启屏幕共享：%d", _screenShareOn);
}

- (IBAction)onJoinClick:(id)sender
{
    if (self.userIDTextField.text.length == 0 || self.roomIDTextField.text.length == 0) {
        return;
    }
    [self willJoinChannel];
    
    [self.view endEditing:YES];
    uint64_t userID = self.userIDTextField.text.longLongValue;
    NSString *roomID = self.roomIDTextField.text;
    __weak typeof(self) wself = self;
    [NERtcEngine.sharedEngine joinChannelWithToken:@"" channelName:roomID myUid:userID  completion:^(NSError * _Nullable error, uint64_t channelId, uint64_t elapesd, uint64_t uid) {
        __strong typeof(wself) sself = wself;
        if (!sself) return;
        if (error) {
            [sself showAlertMessage:error.localizedDescription];
            return;
        }
        sself.currentUserID = @(userID);
        //主流摄像头画布
        NERtcVideoCanvas *canvas = [[NERtcVideoCanvas alloc] init];
        canvas.container = sself.localUserView;
        [NERtcEngine.sharedEngine setupLocalVideoCanvas:canvas];
        
        //屏幕共享画布
        NERtcVideoCanvas *subCanvas = [[NERtcVideoCanvas alloc] init];
        subCanvas.container = sself.localScreenShareView;
        [NERtcEngine.sharedEngine setupLocalSubStreamVideoCanvas:subCanvas];
        
        [sself showAlertMessage:@"Success!"];
        
    }];
}

- (IBAction)onLeaveClick:(id)sender
{
    [NERtcEngine.sharedEngine leaveChannel];
}

#pragma mark - Function

- (NSString *)randomUserID
{
    uint64_t uid = 10000 + arc4random() % (99999 - 10000);
    return @(uid).stringValue;
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

- (void)willJoinChannel
{
    NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
    
    //1v1音视频通话场景的视频推荐配置
    //其他场景下请联系云信技术支持获取配置
    NERtcVideoEncodeConfiguration *config = [[NERtcVideoEncodeConfiguration alloc] init];
    config.width = 640;
    config.height = 360;
    config.frameRate = kNERtcVideoFrameRateFps15;
    [coreEngine setLocalVideoConfig:config];
    
    //1v1音视频通话场景的音频推荐配置
    //其他场景下请联系云信技术支持获取配置
    [coreEngine setAudioProfile:kNERtcAudioProfileStandard
                       scenario:kNERtcAudioScenarioSpeech];
    
    [coreEngine setExternalVideoSource:YES isScreen:YES];
    NERtcVideoSubStreamEncodeConfiguration *subStreamConfig = [[NERtcVideoSubStreamEncodeConfiguration alloc] init];
    [coreEngine startScreenCapture:subStreamConfig];
    
    [coreEngine enableLocalAudio:YES];
    [coreEngine enableLocalVideo:YES];
    
    _cameraOn = YES;
    _screenShareOn = YES;
}

- (void)addSystemBroadcastPickerIfPossible
{
    if (@available(iOS 12.0, *)) {
        // Not recommend
        RPSystemBroadcastPickerView *picker = [[RPSystemBroadcastPickerView alloc] initWithFrame:CGRectMake(0, 0, 120, 64)];
        picker.showsMicrophoneButton = NO;
        picker.preferredExtension = @"com.netease.nmc.NERtcSample-ScreenShare-iOS-Objective-C.Broadcast";
        [self.view addSubview:picker];
        picker.center = self.view.center;
        
        UIButton *button = [picker.subviews filteredArrayUsingPredicate:[NSPredicate predicateWithBlock:^BOOL(id  _Nullable evaluatedObject, NSDictionary<NSString *,id> * _Nullable bindings) {
            return [evaluatedObject isKindOfClass:UIButton.class];
        }]].firstObject;
        [button setImage:nil forState:UIControlStateNormal];
        [button setTitle:@"Start Share" forState:UIControlStateNormal];
        button.titleLabel.font = [UIFont systemFontOfSize:14.0];
        [button setTitleColor:self.navigationController.navigationBar.tintColor forState:UIControlStateNormal];
        
        UIBarButtonItem *leftItem = [[UIBarButtonItem alloc] initWithCustomView:picker];
        self.navigationItem.leftBarButtonItem = leftItem;
    }
}

- (void)showAlertMessage:(NSString *)message
{
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:nil message:message preferredStyle:UIAlertControllerStyleAlert];
    [alertController addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
    [self presentViewController:alertController animated:YES completion:nil];
}

@end
