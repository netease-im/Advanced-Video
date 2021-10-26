//
//  NEBeautyManager.h
//  Beauty-iOS-ObjC
//
//  Created by zhangchenliang on 2021/10/12.
//  Copyright © 2021 NetEase. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface NEBeautyManager : NSObject

+ (instancetype)sharedManager;

- (void)prepareResource;

- (void)initNEBeauty;

- (void)destroyNEBeauty;

- (void)enableNEBeauty:(BOOL)enable;

- (void)displayBeautyMenuWithContainer:(UIView *)container;

- (void)displayFilterMenuWithContainer:(UIView *)container;

- (void)displayStickerMenuWithContainer:(UIView *)container;

- (void)displayMakeupMenuWithContainer:(UIView *)container;

@end
