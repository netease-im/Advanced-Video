//
//  NETSDemoSettingVM.m
//  VoiceChanger-iOS-ObjC
//
//  Created by Think on 2020/11/5.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NETSDemoSettingVM.h"
#import "NETSDemoSoundConfig.h"

@interface NETSDemoSettingVM ()

@property (nonatomic, assign, readwrite)    NETSDemoSettingType type;
@property (nonatomic, strong, readwrite)    NSArray<NETSDemoSettingCheckModel *>  *datas;
@property (nonatomic, copy, readwrite)      NSString            *title;

@end

@implementation NETSDemoSettingVM

- (instancetype)initWithType:(NETSDemoSettingType)type
{
    self = [super init];
    if (self) {
        _type = type;
        _datas = [self setupDatas];
    }
    return self;
}

- (void)dealloc
{
    NSLog(@"%@ dealloc...", [[self class] description]);
}

- (NSArray *)setupDatas
{
    switch (_type) {
        case NETSDemoSettingCustomEqualizer:
        {
            NSArray<NETSDemoSettingSlideModel *> *setting = [NETSDemoSoundConfig shared].customEqualization;
            NSMutableArray *temp = [NSMutableArray arrayWithCapacity:[setting count]];
            for (NETSDemoSettingSlideModel *obj in setting) {
                NETSDemoSettingSlideModel *newObj = [[NETSDemoSettingSlideModel alloc] initWithTitle:obj.title value:obj.value];
                [temp addObject:newObj];
            }
            return [temp copy];
        }
        case NETSDemoSettingEqualizer:
        {
            NSArray *titles = @[@"无", @"低沉", @"圆润", @"清澈", @"自定义"];
            return [self _datasWithTitles:titles];
        }
        case NETSDemoSettingMixing:
        {
            NSArray *titles = @[@"无", @"KTV", @"朗诵"];
            return [self _datasWithTitles:titles];
        }
        case NETSDemoSettingChange:
        {
            NSArray *titles = @[@"无", @"机器人", @"巨人", @"教堂", @"恐怖", @"低沉", @"男变女", @"女变男", @"男变萝莉", @"女变萝莉"];
            return [self _datasWithTitles:titles];
        }
            
        default:
            return @[];
    }
}

- (NSArray *)_datasWithTitles:(NSArray *)titles
{
    NSMutableArray *res = [NSMutableArray arrayWithCapacity:[titles count]];
    for (NSInteger i = 0; i < [titles count]; i++) {
        NSInteger value = i - 1;
        NSString *title = titles[i];
        NETSDemoSettingCheckModel *item = [[NETSDemoSettingCheckModel alloc] initWithTitle:title value:value];
        [res addObject:item];
    }
    return [res copy];
}

- (NSString *)title
{
    switch (_type) {
        case NETSDemoSettingEqualizer:
            return @"均衡器";
        case NETSDemoSettingCustomEqualizer:
            return @"自定义均衡器";
        case NETSDemoSettingMixing:
            return @"混音";
        case NETSDemoSettingChange:
            return @"变声";
            
        default:
            return @"设置";
    }
}

@end
