//
//  NTESDemoP2PViewController.h
//  MediaStats-iOS-ObjC
//
//  Created by NetEase on 2020/08/01.
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//  点对点会议页面

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface NTESDemoP2PViewController : UIViewController

/**
 * 房间号
 */
@property (nonatomic, copy) NSString *roomId;

/**
 * 用户ID
 */
@property (nonatomic, assign) uint64_t userId;

@end

NS_ASSUME_NONNULL_END
