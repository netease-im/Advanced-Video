//
//  NEBeautyManager.h
//  Beauty-iOS-ObjC
//
//  Created by zhangchenliang on 2021/10/12.
//  Copyright © 2021 NetEase. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NEBeautyMacro.h"
#import <NERtcSDK/NERtcSDK.h>

@interface NEBeautyManager : NSObject

+ (instancetype)sharedManager;

//- (void)prepareResource;

//- (void)initNEBeauty;

//- (void)destroyNEBeauty;

- (void)enableBeauty:(BOOL)enable;

/// 展示菜单
/// @param type 菜单类型（可以组合）
/// @param container 父视图
- (void)displayMenuWithType:(NEBeautyConfigViewType)type container:(UIView *)container;

/// 关闭菜单
- (void)dismissMenuWithType:(NEBeautyConfigViewType)type;

// ST_SDK
- (void)initSTSDK;
- (void)destroySTSDK;
- (void)processCapturedVideoFrameWithPixelBuffer:(CVPixelBufferRef)pixelBuffer
                                        rotation:(NERtcVideoRotationType)rotation;

@end
