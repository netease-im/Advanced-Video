//
//  NETSDemoSettingVM.h
//  VoiceChanger-iOS-ObjC
//
//  Created by Think on 2020/11/5.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NETSDemoSettingModel.h"

NS_ASSUME_NONNULL_BEGIN

///
/// 音效配置ViewModel
///

typedef NS_ENUM(NSUInteger, NETSDemoSettingType) {
    NETSDemoSettingEqualizer = 0,       // 均衡器
    NETSDemoSettingCustomEqualizer,     // 自定义均衡器
    NETSDemoSettingMixing,              // 混音
    NETSDemoSettingChange               // 变声
};

@interface NETSDemoSettingVM : NSObject

/// 类型
@property (nonatomic, assign, readonly) NETSDemoSettingType type;
/// 数据集合
@property (nonatomic, strong, readonly) NSArray     *datas;
/// 页面标题
@property (nonatomic, copy, readonly)   NSString    *title;

/// 实例化viewModel
- (instancetype)initWithType:(NETSDemoSettingType)type;

@end

NS_ASSUME_NONNULL_END
