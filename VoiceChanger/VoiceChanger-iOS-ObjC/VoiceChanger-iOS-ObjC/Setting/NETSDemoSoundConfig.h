//
//  NETSDemoSoundConfig.h
//  VoiceChanger-iOS-ObjC
//
//  Created by Think on 2020/11/5.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <NERtcSDK/NERtcEngineEnum.h>

NS_ASSUME_NONNULL_BEGIN

///
/// 音效配置数据中心
///
@class NETSDemoSettingSlideModel;

@interface NETSDemoSoundConfig : NSObject

/// 变声/美声 配置值
@property (nonatomic, assign)   NERtcVoiceChangerType       changer;
/// 混响 配置值
@property (nonatomic, assign)   NERtcVoiceReverbType        reverb;
/// EQ 配置值
@property (nonatomic, assign)   NERtcVoiceEqualizationType  equalization;
/// 自定义 EQ 配置值
@property (nonatomic, strong)   NSArray<NETSDemoSettingSlideModel *> *customEqualization;

/// 获取配置对象单例
+ (NETSDemoSoundConfig *)shared;

@end

NS_ASSUME_NONNULL_END
