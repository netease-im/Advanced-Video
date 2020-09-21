//
//  NETSDemoP2PVC.h
//  Beauty-iOS-ObjC
//
//  Created by Think on 2020/9/17.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface NETSDemoP2PVC : UIViewController

/**
 初始化一对一聊天室
 @param roomId - 房间ID
 @param userId - 用户ID
 @return 一对一聊天室
 */
- (instancetype)initWithRoomId:(NSString *)roomId
                        userId:(uint64_t)userId;

@end

NS_ASSUME_NONNULL_END
