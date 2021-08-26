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
#import "NTESAudioPlayerManager.h"
#import "UIView+NTES.h"
#import "NTESGlobalMacro.h"

@interface NTESMeetingViewController () <NERtcEngineDelegateEx>

@property (strong, nonatomic) IBOutlet UIView *localUserView;
@property (strong, nonatomic) IBOutletCollection(UIView) NSArray *remoteUserViews;
@property (nonatomic,strong) NTESAudioPlayerManager *playerManager; //背景音乐播放器

@end

@implementation NTESMeetingViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"Leave" style:UIBarButtonItemStylePlain target:self action:@selector(onBackAction:)];;
    self.title = [NSString stringWithFormat:@"Room %@", self.roomID];
    [self setupRTCEngine];
    [self joinCurrentRoom];
}

- (void)dealloc
{
}

- (void)setupRTCEngine
{
    NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
    NERtcEngineContext *context = [[NERtcEngineContext alloc] init];
    context.engineDelegate = self;
    context.appKey = kAppKey;
    [coreEngine setupEngineWithContext:context];
    [coreEngine enableLocalAudio:YES];
    [coreEngine enableLocalVideo:YES];
}

- (void)joinCurrentRoom
{
    [NERtcEngine.sharedEngine joinChannelWithToken:@"" channelName:self.roomID myUid:self.userID completion:^(NSError * _Nullable error, uint64_t channelId, uint64_t elapesd) {
        NERtcVideoCanvas *canvas = [[NERtcVideoCanvas alloc] init];
        canvas.container = self.localUserView;
        [NERtcEngine.sharedEngine setupLocalVideoCanvas:canvas];
        
        [self addAudioPanel];
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

- (void)addAudioPanel
{
    _playerManager = [[NTESAudioPlayerManager alloc] init];
    
    _playerManager.view.size = CGSizeMake(120.0, 56.0);
    _playerManager.view.right = self.view.width;
    _playerManager.view.bottom = self.view.bottom - 40.0;
    
    _playerManager.audioPanelView.size = CGSizeMake(self.view.width, self.view.width * 0.732);
    _playerManager.audioPanelView.bottom = self.view.height;
    
    _playerManager.maskView.frame = self.view.bounds;
    
    [self.view addSubview:_playerManager.view];
    [self.view addSubview:_playerManager.view];
    [self.view addSubview:_playerManager.maskView];
    [self.view addSubview:_playerManager.audioPanelView];
}


- (void)onBackAction:(id)sender
{
    [self.navigationController popViewControllerAnimated:YES];
    [NERtcEngine.sharedEngine leaveChannel];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [NERtcEngine destroyEngine];
    });
}

@end
