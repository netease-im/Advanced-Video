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
@property (nonatomic, assign) BOOL isInChannel; //!< 是否已经成功加入了频道

@property (strong, nonatomic) IBOutlet UITextField *userIDTextField;
@property (strong, nonatomic) IBOutlet UITextField *roomIDTextField;
@property (strong, nonatomic) IBOutlet UIButton *joinButton;

@property (strong, nonatomic) IBOutlet UIView *localUserView;
@property (strong, nonatomic) IBOutlet UIView *remoteUserView;


- (IBAction)onJoinClick:(id)sender;
- (IBAction)onLeaveClick:(id)sender;

@end

@implementation NTESDemoViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.userIDTextField.text = [self randomUserID];
    self.roomIDTextField.text = @"100";
    [self setupUserDefaults];
    [self setupRTCEngine];
}

- (void)dealloc
{
    [self.userDefaults removeObserver:self forKeyPath:@"frame"];
    [NERtcEngine.sharedEngine leaveChannel];
    [NERtcEngine destroyEngine];
}


- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context
{
    if ([keyPath isEqualToString:@"frame"]) {
        if (self.isInChannel) {
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
    NERtcVideoCanvas *canvas = [[NERtcVideoCanvas alloc] init];
    canvas.container = self.remoteUserView;
    [NERtcEngine.sharedEngine setupRemoteVideoCanvas:canvas forUserID:userID];
}

- (void)onNERtcEngineUserVideoDidStartWithUserID:(uint64_t)userID videoProfile:(NERtcVideoProfileType)profile
{
    [NERtcEngine.sharedEngine subscribeRemoteVideo:YES forUserID:userID streamType:kNERtcRemoteVideoStreamTypeHigh];
}

- (IBAction)onJoinClick:(id)sender
{
    if (self.userIDTextField.text.length == 0 || self.roomIDTextField.text.length == 0) {
        return;
    }
    [self.view endEditing:YES];
    uint64_t userID = self.userIDTextField.text.longLongValue;
    NSString *roomID = self.roomIDTextField.text;
    __weak typeof(self) wself = self;
    [NERtcEngine.sharedEngine joinChannelWithToken:@"" channelName:roomID myUid:userID  completion:^(NSError * _Nullable error, uint64_t channelId, uint64_t elapesd) {
        __strong typeof(wself) sself = wself;
        if (!sself) return;
        sself.isInChannel = YES;
        NERtcVideoCanvas *canvas = [[NERtcVideoCanvas alloc] init];
        canvas.container = sself.localUserView;
        [NERtcEngine.sharedEngine setupLocalVideoCanvas:canvas];
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
            [button setTitleColor:self.navigationController.navigationBar.tintColor forState:UIControlStateNormal];
            UIBarButtonItem *leftItem = [[UIBarButtonItem alloc] initWithCustomView:picker];
            self.navigationItem.leftBarButtonItem = leftItem;
            
        } else {
            UIAlertController *alertController = [UIAlertController alertControllerWithTitle:@"Tip" message:@"Join success!" preferredStyle:UIAlertControllerStyleAlert];
            [alertController addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
            [sself presentViewController:alertController animated:YES completion:nil];
        }
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
    [coreEngine setExternalVideoSource:YES];
    [coreEngine enableLocalAudio:YES];
    [coreEngine enableLocalVideo:YES];
}

@end
