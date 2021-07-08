//
//  NETSDemoSettingModel.m
//  VoiceChanger-iOS-ObjC
//
//  Created by Think on 2020/11/5.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NETSDemoSettingModel.h"

@implementation NETSDemoSettingCheckModel

- (instancetype)initWithTitle:(NSString *)title value:(NSInteger)value
{
    self = [super init];
    if (self) {
        _title = title;
        _value = value;
    }
    return self;
}

@end


@implementation NETSDemoSettingSlideModel

- (instancetype)initWithTitle:(NSString *)title value:(CGFloat)value
{
    self = [super init];
    if (self) {
        _title = title;
        _value = value;
    }
    return self;
}

@end
