//
//  NTESVideoConfigViewController.h
//  NEPushStreamBypath
//
//  Created by I am Groot on 2020/9/15.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol NTESVideoConfigVCDelegate <NSObject>
- (void)didGetStreamURL:(NSString *)URLString;
@end

@interface NTESVideoConfigViewController : UIViewController
@property(weak,nonatomic)id delegate;

@end

NS_ASSUME_NONNULL_END
