//
//  NSDictionary+NTES.m
//  ProfileConfig-iOS-ObjC
//
//  Created by zhangchenliang on 2021/9/28.
//  Copyright © 2021 Netease. All rights reserved.
//

#import "NSDictionary+NTES.h"

@implementation NSDictionary (NTES)

- (NSArray *)getAscendingKeys {
    NSArray *keyArray = self.allKeys;
    NSArray *ascendingKeys = [keyArray sortedArrayUsingComparator:^NSComparisonResult(id  _Nonnull obj1, id  _Nonnull obj2) {
        if ([obj1 intValue] > [obj2 intValue]) {
            return NSOrderedDescending;
        }

        if ([obj1 intValue] < [obj2 intValue]) {
            return NSOrderedAscending;
        }

        return NSOrderedSame;
    }];
    
    return ascendingKeys;
}

@end
