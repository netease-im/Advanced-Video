//
//  NETSDemoSettingModel.h
//  VoiceChanger-iOS-ObjC
//
//  Created by Think on 2020/11/5.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

///
/// 音效配置check模型
///
@interface NETSDemoSettingCheckModel : NSObject

@property (nonatomic, copy)     NSString    *title;     // 设置标题
@property (nonatomic, assign)   NSInteger    value;     // 选中值

- (instancetype)initWithTitle:(NSString *)title value:(NSInteger)value;

@end

///
/// 音效配置slide模型
///
@interface NETSDemoSettingSlideModel : NSObject

@property (nonatomic, copy)     NSString    *title; // 设置标题
@property (nonatomic, assign)   CGFloat     value;  // 滑动值

- (instancetype)initWithTitle:(NSString *)title value:(CGFloat)value;

@end

NS_ASSUME_NONNULL_END
