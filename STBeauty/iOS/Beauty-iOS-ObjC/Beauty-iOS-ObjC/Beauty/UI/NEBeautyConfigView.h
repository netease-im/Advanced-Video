//
//  NEBeautyConfigView.h
//  Beauty-iOS-ObjC
//
//  Created by zhangchenliang on 2021/10/12.
//  Copyright © 2021 NetEase. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NEBeautyMacro.h"
#import "NECollectionView.h"
#import "NETitleDisplayModel.h"
#import "NEBeautySliderDisplayModel.h"

@protocol NEBeautyConfigViewDelegate <NSObject>

@optional

// 重置操作触发的回调
- (void)didTriggerResetActionWithConfigViewType:(NEBeautyConfigViewType)type;

// 滤镜、贴纸和美妆切换的回调
- (void)didSelectItemWithConfigViewType:(NEBeautyConfigViewType)type model:(NECollectionViewDisplayModel *)model;

// 美颜slider滑动时的回调（value的取值范围为0 ~ 1）
- (void)didChangeSliderValueWithType:(NEBeautySliderType)type value:(float)value;

@end

@protocol NEBeautyConfigViewDataSource <NSObject>

@optional

// 标题tab数据源
- (NSArray<NETitleDisplayModel *> *)titleModelArrayForConfigViewWithType:(NEBeautyConfigViewType)type;

// 滤镜、贴纸和美妆的数据源
- (NSArray<NECollectionViewDisplayModel *> *)itemModelArrayForConfigViewWithType:(NEBeautyConfigViewType)type effectType:(NEBeautyEffectType)effectType;

// 美颜的数据源
- (NSArray<NEBeautySliderDisplayModel *> *)sliderModelArrayForTitleType:(NEBeautyEffectType)type;

// 滤镜强度的数据源
- (NEBeautySliderDisplayModel *)sliderModelForFilterStrength;

@end

@interface NEBeautyConfigView : UIView

/// 初始化
/// @param type 菜单类型（可以组合）
/// @param dataSource 数据源
/// @param delegate 代理
- (instancetype)initWithType:(NEBeautyConfigViewType)type
                  dataSource:(id<NEBeautyConfigViewDataSource>)dataSource
                    delegate:(id<NEBeautyConfigViewDelegate>)delegate;

- (void)displayWithContainer:(UIView *)container;

- (void)dismiss;

- (void)reloadData;

@end
