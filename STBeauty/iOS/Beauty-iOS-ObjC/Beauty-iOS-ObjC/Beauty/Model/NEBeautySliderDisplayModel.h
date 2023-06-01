//
//  NEBeautySliderDisplayModel.h
//  Beauty-iOS-ObjC
//
//  Created by zhangchenliang on 2021/10/12.
//  Copyright © 2021 NetEase. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NEBeautyMacro.h"

@interface NEBeautySliderDisplayModel : NSObject

@property (nonatomic, assign) NEBeautySliderType type;
@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSString *imageName;
@property (nonatomic, assign) float value;

@end
