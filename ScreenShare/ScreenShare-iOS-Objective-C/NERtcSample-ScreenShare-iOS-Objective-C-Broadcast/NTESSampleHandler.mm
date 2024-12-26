//
//  NTESSampleHandler.m
//  NERtcScreenShareSample-Broadcast
//
//  Created by 丁文超 on 2020/7/14.
//  Copyright © 2020 丁文超. All rights reserved.
//


#import "NTESSampleHandler.h"
#import <NERtcReplayKit/NERtcReplayKit.h>

static NSString * _Nonnull kAppGroup = @"group.com.netease.nmc.NERtcSample-ScreenShare-iOS-Objective-C"; //!< 需要替换成自己的App Group

@interface NTESSampleHandler()<NEScreenShareSampleHandlerDelegate>


@end

@implementation NTESSampleHandler

- (void)broadcastStartedWithSetupInfo:(NSDictionary<NSString *,NSObject *> *)setupInfo {
  NEScreenShareBroadcasterOptions *options = [[NEScreenShareBroadcasterOptions alloc]init];
  options.appGroup = kAppGroup;
#if DEBUG
  options.enableDebug = YES;
#endif
  //开启音频共享
  options.needAudioSampleBuffer = YES;
  options.needMicAudioSampleBuffer = YES;
  [NEScreenShareSampleHandler sharedInstance].delegate = self;
  [[NEScreenShareSampleHandler sharedInstance] broadcastStartedWithSetupInfo:options];
}

- (void)broadcastPaused {
  // User has requested to pause the broadcast. Samples will stop being delivered.
  [[NEScreenShareSampleHandler sharedInstance] broadcastPaused];
}

- (void)broadcastResumed {
  // User has requested to resume the broadcast. Samples delivery will resume.
  [[NEScreenShareSampleHandler sharedInstance] broadcastResumed];
}

- (void)broadcastFinished {
  // User has requested to finish the broadcast.
  [[NEScreenShareSampleHandler sharedInstance] broadcastFinished];
}

- (void)processSampleBuffer:(CMSampleBufferRef)sampleBuffer withType:(RPSampleBufferType)sampleBufferType {
  [[NEScreenShareSampleHandler sharedInstance] processSampleBuffer:sampleBuffer withType:sampleBufferType];
}

#pragma mark- NEScreenShareSampleHandlerDelegate
- (void)onRequestToFinishBroadcastWithError:(NSError *)error {
  [self finishBroadcastWithError:error];
}

@end
