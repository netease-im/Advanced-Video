//
//  NTESPickViewSettingModel.h
//  DeviceManager-iOS-ObjC
//
//  Created by Think on 2020/9/16.
//  Copyright © 2020 Wenchao Ding. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, NTESPickViewSettingType) {
    NTESPickViewSettingVideo = 0,
    NTESPickViewSettingAudio,
    NTESPickViewSettingAudioMode
};

@interface NTESPickViewSettingModel : NSObject

@property (nonatomic, copy)     NSString    *title;
@property (nonatomic, assign)   NSInteger  value;
@property (nonatomic, assign)   NTESPickViewSettingType   type;

- (instancetype)initWithTitle:(NSString *)title value:(NSInteger)value type:(NTESPickViewSettingType)type;

@end

NS_ASSUME_NONNULL_END
