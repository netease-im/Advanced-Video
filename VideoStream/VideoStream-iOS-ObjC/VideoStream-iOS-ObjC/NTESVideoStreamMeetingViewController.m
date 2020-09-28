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

@interface NTESVideoStreamMeetingViewController () <NERtcEngineDelegateEx>

@property (strong, nonatomic) IBOutlet UIView *localUserView;
@property (strong, nonatomic) IBOutletCollection(UIView) NSArray *remoteUserViews;

@property (strong, nonatomic) NERtcLiveStreamTaskInfo *liveStreamTask;
@property (strong, nonatomic) NSMutableArray<NSNumber *> *userList;
@property (weak, nonatomic) IBOutlet UIButton *configButton;
@property (weak, nonatomic) IBOutlet UIButton *hungupButton;
@end

@implementation NTESVideoStreamMeetingViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.configButton.layer.cornerRadius = 8;
    self.hungupButton.layer.cornerRadius = 8;
    self.title = [NSString stringWithFormat:@"Room %@", self.roomID];
    self.userList = [NSMutableArray arrayWithObject:@(self.userID)];
    [self setupRTCEngine];
    [self joinCurrentRoom];
}

- (void)dealloc
{
    if (self.liveStreamTask.streamURL) {
        int ret = [NERtcEngine.sharedEngine removeLiveStreamTask:self.liveStreamTask.taskID compeltion:^(NSString * _Nonnull taskId, kNERtcLiveStreamError errorCode) {
            NSLog(@"移除任务[%@] error = %@",taskId, NERtcErrorDescription(errorCode));
            [NERtcEngine.sharedEngine leaveChannel];
        }];
        if (ret != 0) {
            NSLog(@"移除任务失败");
        }
    }
    
}

- (void)setupRTCEngine
{
    NSAssert(![kAppKey isEqualToString:@"<#AppKey#>"], @"请设置AppKey");
    NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
    NERtcEngineContext *context = [[NERtcEngineContext alloc] init];
    context.engineDelegate = self;
    context.appKey = kAppKey;
    [coreEngine setupEngineWithContext:context];
    [coreEngine enableLocalAudio:YES];
    [coreEngine enableLocalVideo:YES];
    [coreEngine setParameters:@{kNERtcKeyPublishSelfStreamEnabled: @YES}]; // 打开推流
}

- (void)joinCurrentRoom
{
    [NERtcEngine.sharedEngine joinChannelWithToken:@"" channelName:self.roomID myUid:self.userID completion:^(NSError * _Nullable error, uint64_t channelId, uint64_t elapesd) {
        NERtcVideoCanvas *canvas = [[NERtcVideoCanvas alloc] init];
        canvas.container = self.localUserView;
        [NERtcEngine.sharedEngine setupLocalVideoCanvas:canvas];
        [self addLiveStream];
    }];
}

- (void)addLiveStream
{
    NSAssert(![kStreamURL isEqualToString:@"<#推流地址#>"], @"请设置推流地址");
    self.liveStreamTask = [[NERtcLiveStreamTaskInfo alloc] init];
    self.liveStreamTask.taskID = self.roomID;
    self.liveStreamTask.streamURL = kStreamURL;
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
        NSString *message = !errorCode ? @"添加成功" : [NSString stringWithFormat:@"添加失败 error = %@",NERtcErrorDescription(errorCode)];
        NSLog(@"%@", message);
    }];
    if (ret != 0) {
        NSLog(@"添加推流任务失败");
    }
}

- (void)updateLiveStreamTask
{
    int ret = [NERtcEngine.sharedEngine updateLiveStreamTask:self.liveStreamTask
                                               compeltion:^(NSString * _Nonnull taskId, kNERtcLiveStreamError errorCode) {
        NSString *message = !errorCode ? @"更新成功" : [NSString stringWithFormat:@"更新失败 error = %@",NERtcErrorDescription(errorCode)];
        NSLog(@"%@", message);
    }];
    if (ret != 0) {
        NSLog(@"更新推流任务失败");
    }
}

// 根据self.usersForStreaming生成直播成员信息
- (void)reloadUsers
{
    NSInteger layoutWidth = self.liveStreamTask.layout.width;
    NSInteger userWidth = 320;
    NSInteger userHeight = 480;
    NSInteger horizPadding = (layoutWidth-userWidth*2)/3;
    NSInteger vertPadding = 15;
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
#pragma mark - event
- (IBAction)hungupEvent:(id)sender {
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
        
    }
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    NTESVideoConfigViewController *configVC = segue.destinationViewController;
    configVC.delegate = self;
}

#pragma mark - NTESVideoConfigVCDelegate
- (void)didGetStreamURL:(NSString *)URLString
{
    self.liveStreamTask.streamURL = URLString;
    [self updateLiveStreamTask];
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

- (void)onNERtcEngineUserDidLeaveWithUserID:(uint64_t)userID reason:(NERtcSessionLeaveReason)reason
{
    [self.view viewWithTag:(NSInteger)userID].tag = 0;
}

- (void)onNERtcEngineUserVideoDidStartWithUserID:(uint64_t)userID videoProfile:(NERtcVideoProfileType)profile
{
    [NERtcEngine.sharedEngine subscribeRemoteVideo:YES forUserID:userID streamType:kNERtcRemoteVideoStreamTypeHigh];
    if (profile != kNERtcVideoProfileNone && ![self.userList containsObject:@(userID)]) {
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

@end
