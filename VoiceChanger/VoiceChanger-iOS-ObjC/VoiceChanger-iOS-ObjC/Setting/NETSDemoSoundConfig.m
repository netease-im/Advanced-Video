//
//  NETSDemoSoundConfig.m
//  VoiceChanger-iOS-ObjC
//
//  Created by Think on 2020/11/5.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NETSDemoSoundConfig.h"
#import "NETSDemoSettingModel.h"

@implementation NETSDemoSoundConfig

+ (NETSDemoSoundConfig *)shared
{
    static NETSDemoSoundConfig *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[NETSDemoSoundConfig alloc] init];
        
        instance.changer = kNERtcVoiceChangerOff;
        instance.reverb = kNERtcVoiceReverbTypeOff;
        instance.equalization = kNERtcVoiceEqualizationTypeOff;
        
        // 为自定义 EQ 配置初始值
        NSArray *titles = @[@"32Hz", @"63Hz", @"125Hz", @"250Hz", @"500Hz", @"1kHz", @"2kHz", @"4kHz", @"8kHz", @"16kHz"];
        NSMutableArray *tmp = [NSMutableArray arrayWithCapacity:[titles count]];
        for (NSInteger i = 0; i < [titles count]; i++) {
            NETSDemoSettingSlideModel *model = [[NETSDemoSettingSlideModel alloc] initWithTitle:titles[i] value:0];
            [tmp addObject:model];
        }
        instance.customEqualization = [tmp copy];
    });
    return instance;
}

@end
