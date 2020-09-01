//
//  NTESAudioStreamMeetingViewController.m
//  AudioStream-iOS-ObjC
//
//  Created by 丁文超 on 2020/6/23.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NTESAudioStreamMeetingViewController.h"
#import "NTESAudioStreamUserCell.h"
#import <NERtcSDK/NERtcSDK.h>
#import "AppKey.h"

@interface NTESAudioStreamMeetingViewController () <NERtcEngineDelegateEx,UICollectionViewDataSource,UICollectionViewDelegate,UICollectionViewDelegateFlowLayout>

@property (weak, nonatomic) IBOutlet UICollectionView *collectionView;

@property (strong, nonatomic) NSMutableArray<NSNumber *> *userList;
@property (strong, nonatomic) NERtcLiveStreamTaskInfo *liveStreamTask;

@end

@implementation NTESAudioStreamMeetingViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.title = [NSString stringWithFormat:@"Room %@", self.roomID];
    self.userList = [NSMutableArray arrayWithObject:@(self.userID)];
    [self setupRTCEngine];
    [self joinCurrentRoom];
}

- (void)dealloc
{
    [NERtcEngine.sharedEngine leaveChannel];
    if (self.liveStreamTask) {
        int ret = [NERtcEngine.sharedEngine removeLiveStreamTask:self.liveStreamTask.taskID compeltion:^(NSString * _Nonnull taskId, kNERtcLiveStreamError errorCode) {
            NSLog(@"移除任务[%@] error = %@",taskId, NERtcErrorDescription(errorCode));
        }];
        if (ret != 0) {
            NSLog(@"移除任务失败");
        }
    }
}

- (void)setupRTCEngine
{
    NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
    NERtcEngineContext *context = [[NERtcEngineContext alloc] init];
    context.engineDelegate = self;
    context.appKey = kAppKey;
    [coreEngine setupEngineWithContext:context];
    [coreEngine enableLocalAudio:YES];
    [coreEngine setParameters:@{kNERtcKeyPublishSelfStreamEnabled: @YES}]; // 打开推流
}

- (void)joinCurrentRoom
{
    [NERtcEngine.sharedEngine joinChannelWithToken:@"" channelName:self.roomID myUid:self.userID completion:^(NSError * _Nullable error, uint64_t channelId, uint64_t elapesd) {
        [self addLiveStream];
    }];
}

- (void)addLiveStream
{
    self.liveStreamTask = [[NERtcLiveStreamTaskInfo alloc] init];
    self.liveStreamTask.taskID = self.roomID;
    self.liveStreamTask.streamURL = kStreamURL;
    self.liveStreamTask.lsMode = kNERtcLsModeAudio;
    
    //设置整体布局
    NERtcLiveStreamLayout *layout = [[NERtcLiveStreamLayout alloc] init];
    layout.width = 720; //整体布局宽度
    layout.height = 1280; //整体布局高度
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

- (void)updateLiveStream
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

// 根据self.userList生成直播成员信息

- (void)reloadUsers
{
    NSMutableArray *res = NSMutableArray.array;
    for (NSInteger i = 0; i < self.userList.count; i++) {
        NSNumber *userID = self.userList[i];
        NERtcLiveStreamUserTranscoding *userTranscoding = [[NERtcLiveStreamUserTranscoding alloc] init];
        userTranscoding.uid = userID.unsignedLongValue;
        userTranscoding.audioPush = YES;
        userTranscoding.width = 16;
        userTranscoding.height = 16;
        [res addObject:userTranscoding];
    }
    self.liveStreamTask.layout.users = [NSArray arrayWithArray:res];
    [self.collectionView reloadData];
}

#pragma mark - UICollectionViewDataSource & UICollectionViewDelegate

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    return 1;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return self.liveStreamTask.layout.users.count;
}

- (__kindof UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    NTESAudioStreamUserCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"cell" forIndexPath:indexPath];
    cell.userIDLabel.text = self.userList[indexPath.item].stringValue;
    return cell;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    UICollectionViewFlowLayout *flowLayout = (UICollectionViewFlowLayout *)collectionViewLayout;
    NSInteger itemSize = (CGRectGetWidth(collectionView.frame) - flowLayout.minimumInteritemSpacing*4)/3;
    return CGSizeMake(itemSize, itemSize);
}


#pragma mark - NERtcEngineDelegate

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

- (void)onNERtcEngineUserAudioDidStart:(uint64_t)userID
{
    [NERtcEngine.sharedEngine subscribeRemoteAudio:YES forUserID:userID];
}

- (void)onNERtcEngineUserDidJoinWithUserID:(uint64_t)userID userName:(NSString *)userName
{
    if (![self.userList containsObject:@(userID)]) {
        // 添加至直播成员
        [self.userList addObject:@(userID)];
        [self reloadUsers];
        [self updateLiveStream];
    }
}

- (void)onNERtcEngineUserDidLeaveWithUserID:(uint64_t)userID reason:(NERtcSessionLeaveReason)reason
{
    if ([self.userList containsObject:@(userID)]) {
        // 从直播成员中移除
        [self.userList removeObject:@(userID)];
        [self reloadUsers];
        [self updateLiveStream];
    }
}

@end
