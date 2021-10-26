//
//  NTESGlobalMacro.h
//  NERtcAudioChatroom
//
//  Created by Simon Blue on 2019/1/18.
//  Copyright © 2019年 netease. All rights reserved.
//

#import <Foundation/Foundation.h>

#ifndef NTESGlobalMacro_h
#define NTESGlobalMacro_h

#define UIScreenWidth                              ([UIScreen mainScreen].bounds.size.width)
#define UIScreenHeight                             ([UIScreen mainScreen].bounds.size.height)
#define UIWidthAdapter(x) ((x) * UIScreenWidth / 375.0)
#define UIHeightAdapter(x) ((x) * UIScreenHeight / 667.0)
#define UIMinAdapter(x) (UIScreenWidth > UIScreenHeight ? UIHeightAdapter(x) : UIWidthAdapter(x))
#define UIMaxAdapter(x) (UIScreenWidth < UIScreenHeight ? UIHeightAdapter(x) : UIWidthAdapter(x))

#define statusBarHeight         [UIApplication sharedApplication].statusBarFrame.size.height

#define IPHONE_X_HairHeight         44

#define NELP_AUTHORITY_CHECK [NTESAuthorityHelper checkMicAuthority]

#define IPHONE_X \
({BOOL isPhoneX = NO;\
if (@available(iOS 11.0, *)) {\
isPhoneX = [[UIApplication sharedApplication] delegate].window.safeAreaInsets.bottom > 0.0;\
}\
(isPhoneX);})

#define IS_IPAD     ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad)

#pragma mark - UIColor宏定义
#define UIColorFromRGBA(rgbValue, alphaValue) [UIColor \
colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 \
green:((float)((rgbValue & 0x00FF00) >> 8))/255.0 \
blue:((float)(rgbValue & 0x0000FF))/255.0 \
alpha:alphaValue]

#define UIColorFromRGB(rgbValue) UIColorFromRGBA(rgbValue, 1.0)

#pragma mark - 日志打印

#define NELPLogDebug(frmt, ...)     NSLog(@"Debug >> Function:%s Line:%d Content:%@\n", __FUNCTION__, __LINE__, [NSString stringWithFormat:frmt, ##__VA_ARGS__])
#define NELPLogInfo(frmt, ...)      NSLog(@"Info >> Function:%s Line:%d Content:%@\n", __FUNCTION__, __LINE__, [NSString stringWithFormat:frmt, ##__VA_ARGS__])
#define NELPLogWarn(frmt, ...)      NSLog(@"Warn >> Function:%s Line:%d Content:%@\n", __FUNCTION__, __LINE__, [NSString stringWithFormat:frmt, ##__VA_ARGS__])
#define NELPLogError(frmt, ...)     NSLog(@"Error >> Function:%s Line:%d Content:%@\n", __FUNCTION__, __LINE__, [NSString stringWithFormat:frmt, ##__VA_ARGS__])

//线程
void ntes_main_sync_safe(dispatch_block_t block);
void ntes_main_async_safe(dispatch_block_t block);

#endif /* NTESGlobalMacro_h */
