//
//  NTESAudioStreamMeetingViewController.h
//  AudioMixing-iOS-ObjC
//
//  Created by 丁文超 on 2020/6/23.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface NTESAudioStreamMeetingViewController : UIViewController

@property (assign, nonatomic) uint64_t userID;
@property (copy, nonatomic) NSString *roomID;

@end

NS_ASSUME_NONNULL_END
