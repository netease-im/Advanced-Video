//
//  NECollectionViewDisplayModel.h
//  Beauty-iOS-ObjC
//
//  Created by zhangchenliang on 2021/10/18.
//  Copyright © 2021 NetEase. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NEBeautyMacro.h"

@interface NECollectionViewDisplayModel : NSObject

@property (nonatomic, assign) NEBeautyEffectType type;
@property (nonatomic, copy) NSString *resourcePath;
@property (nonatomic, strong) UIImage *image;
@property (nonatomic, copy) NSString *name;
@property (nonatomic, assign) NSInteger index;
@property (nonatomic, assign) BOOL isSelected;

@end
