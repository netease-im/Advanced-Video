//
//  NTESMeetingViewController.m
//  NERtcGroupMeetingSample
//
//  Created by 丁文超 on 2020/3/23.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NTESMeetingViewController.h"
#import <NERtcSDK/NERtcSDK.h>
#import "AppKey.h"
#import "NTESGlobalMacro.h"
#import "NTESSettingVC.h"
#import "NTESPickViewSettingModel.h"

@interface NTESMeetingViewController () <NERtcEngineDelegateEx>

@property (strong, nonatomic) IBOutlet UIView *localUserView;
@property (strong, nonatomic) IBOutletCollection(UIView) NSArray *remoteUserViews;

@property (nonatomic, weak) IBOutlet UIButton    *settingBtn;
@property (nonatomic, weak) IBOutlet UIButton    *hangUpBtn;

@end

@implementation NTESMeetingViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self.navigationController.navigationBar setTranslucent:NO];
    self.title = [NSString stringWithFormat:@"Room %@", self.roomID];
    
    // 隐藏返回按钮
    [self.navigationController.navigationItem setHidesBackButton:YES];
    [self.navigationItem setHidesBackButton:YES];
    [self.navigationController.navigationBar.backItem setHidesBackButton:YES];
    
    [self setupRTCEngine];
    [self joinCurrentRoom];
}

- (void)setupRTCEngine
{
    NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
    
    // 设置默认视频质量(3.6.0版本前须在 setupEngine 之前调用有效)
    NERtcVideoEncodeConfiguration *config = [[NERtcVideoEncodeConfiguration alloc] init];
    config.maxProfile = kNERtcVideoProfileStandard;
    [coreEngine setLocalVideoConfig:config];
    // 设置默认音频质量(3.6.0版本前须在 setupEngine 之前调用有效)
    [coreEngine setAudioProfile:kNERtcAudioProfileStandard scenario:kNERtcAudioScenarioSpeech];
    
    NERtcEngineContext *context = [[NERtcEngineContext alloc] init];
    context.engineDelegate = self;
    context.appKey = kAppKey;
    [coreEngine setupEngineWithContext:context];
    
    // 开启本地音视频(须在 setupEngine 之后调用有效)
    [coreEngine enableLocalAudio:YES];
    [coreEngine enableLocalVideo:YES];
}

- (void)joinCurrentRoom
{
    [NERtcEngine.sharedEngine joinChannelWithToken:@"" channelName:self.roomID myUid:self.userID completion:^(NSError * _Nullable error, uint64_t channelId, uint64_t elapesd) {
        if (error) {
            NELPLogError(@"Join channel error: %ld", (long)error.code);
            return;
        }
        NERtcVideoCanvas *canvas = [[NERtcVideoCanvas alloc] init];
        canvas.container = self.localUserView;
        int result = [NERtcEngine.sharedEngine setupLocalVideoCanvas:canvas];
        if (result != 0) {
            NELPLogError(@"Setup local video canvas error: %ld", (long)error.code);
        }
    }];
}

- (IBAction)clickAction:(id)sender
{
    if (sender == self.settingBtn) {
        NTESSettingVC *vc = [[NTESSettingVC alloc] init];
        vc.videoConfig = [[NTESPickViewSettingModel alloc] initWithTitle:@"640x360/480 @30fps" value:kNERtcVideoProfileStandard type:NTESPickViewSettingVideo];
        vc.audioProfile = [[NTESPickViewSettingModel alloc] initWithTitle:@"普通 16000Hz,20Kbps" value:kNERtcAudioProfileStandard type:NTESPickViewSettingAudio];
        vc.audioScenario = [[NTESPickViewSettingModel alloc] initWithTitle:@"语音场景" value:kNERtcAudioScenarioSpeech type:NTESPickViewSettingAudioMode];
        [self.navigationController pushViewController:vc animated:YES];
    } else if (sender == self.hangUpBtn) {
        [NERtcEngine.sharedEngine leaveChannel];
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            int result = [NERtcEngine destroyEngine];
            ntes_main_async_safe(^{
                if (result != 0) {
                    NELPLogError(@"Destory engine error: %d", result);
                }
                [self.navigationController popViewControllerAnimated:YES];
            });
        });
    }
}

#pragma mark - NERtcEngineDelegate

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

@end
