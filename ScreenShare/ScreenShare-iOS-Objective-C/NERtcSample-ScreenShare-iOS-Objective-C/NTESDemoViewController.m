//
//  NTESDemoViewController.m
//  NERtcScreenShareSample
//
//  Created by 丁文超 on 2020/7/13.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NTESDemoViewController.h"
#import <ReplayKit/ReplayKit.h>
#import <NERtcSDK/NERtcSDK.h>
#import "AppKey.h"

static NSString *kAppGroup = @"group.com.netease.nmc.NERtcSample-ScreenShare-iOS-Objective-C"; //!<需要替换自己的App Group
static void *KVOContext = &KVOContext;

@interface NTESDemoViewController () <NERtcEngineDelegateEx>

@property (nonatomic, strong) NSUserDefaults *userDefaults;
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

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.userIDTextField.text = [self randomUserID];
    [self setupUserDefaults];
    [self setupRTCEngine];
    [self addSystemBroadcastPickerIfPossible];
}

- (void)dealloc
{
    [self.userDefaults removeObserver:self forKeyPath:@"frame"];
    [NERtcEngine.sharedEngine leaveChannel];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [NERtcEngine destroyEngine];
    });
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context
{
    if ([keyPath isEqualToString:@"frame"]) {
        if (self.currentUserID) {
            NSDictionary *i420Frame = change[NSKeyValueChangeNewKey];
            NERtcVideoFrame *frame = [[NERtcVideoFrame alloc] init];
            frame.format = kNERtcVideoFormatI420;
            frame.width = [i420Frame[@"width"] unsignedIntValue];
            frame.height = [i420Frame[@"height"] unsignedIntValue];
            frame.buffer = (void *)[i420Frame[@"data"] bytes];
            frame.timestamp = [i420Frame[@"timestamp"] unsignedLongLongValue];
            int ret = [NERtcEngine.sharedEngine pushExternalVideoFrame:frame];
            if (ret != 0) {
                NSLog(@"发送视频流失败:%d", ret);
                return;
            }
        }
    }
}

- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [super touchesEnded:touches withEvent:event];
    [self.view endEditing:YES];
}

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

- (void)onNERtcEngineDidLeaveChannelWithResult:(NERtcError)result
{
    if (result == kNERtcNoError) {
        self.currentUserID = nil; // clear user id
    }
}

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
    [NERtcEngine.sharedEngine joinChannelWithToken:@"" channelName:roomID myUid:userID  completion:^(NSError * _Nullable error, uint64_t channelId, uint64_t elapesd) {
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

- (NSString *)randomUserID
{
    uint64_t uid = 10000 + arc4random() % (99999 - 10000);
    return @(uid).stringValue;
}

- (void)setupUserDefaults
{
    // 通过UserDefaults建立数据通道，接收Extension传递来的视频帧
    self.userDefaults = [[NSUserDefaults alloc] initWithSuiteName:kAppGroup];
    [self.userDefaults addObserver:self forKeyPath:@"frame" options:NSKeyValueObservingOptionNew context:KVOContext];
}

- (void)setupRTCEngine
{
    NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
    NERtcEngineContext *context = [[NERtcEngineContext alloc] init];
    context.engineDelegate = self;
    context.appKey = kAppKey;
    [coreEngine setupEngineWithContext:context];
}

- (void)willJoinChannel
{
    NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
    [coreEngine setExternalVideoSource:YES isScreen:YES];
    NERtcVideoSubStreamEncodeConfiguration *config = [[NERtcVideoSubStreamEncodeConfiguration alloc] init];
    [coreEngine startScreenCapture:config];
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
