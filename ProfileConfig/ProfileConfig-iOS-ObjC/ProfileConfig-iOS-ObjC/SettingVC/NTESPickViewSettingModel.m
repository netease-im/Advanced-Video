//
//  NTESPickViewSettingModel.m
//  DeviceManager-iOS-ObjC
//
//  Created by Think on 2020/9/16.
//  Copyright © 2020 Wenchao Ding. All rights reserved.
//

#import "NTESPickViewSettingModel.h"

@implementation NTESPickViewSettingModel

- (instancetype)initWithTitle:(NSString *)title value:(NSInteger)value type:(NTESPickViewSettingType)type
{
    self = [super init];
    if (self) {
        self.title = title;
        self.value = value;
        self.type = type;
    }
    return self;
}

@end
