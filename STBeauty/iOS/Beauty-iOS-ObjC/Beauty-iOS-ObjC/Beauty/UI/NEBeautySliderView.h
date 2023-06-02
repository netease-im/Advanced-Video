//
//  NEBeautySliderView.h
//  Beauty-iOS-ObjC
//
//  Created by zhangchenliang on 2021/10/18.
//  Copyright © 2021 NetEase. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NEBeautySliderDisplayModel.h"

typedef void(^NEBeautySliderValueChangeBlock)(NEBeautySliderType type, float value);

@interface NEBeautySliderView : UIView

- (void)updateWithModel:(NEBeautySliderDisplayModel *)model;

- (void)updateValueChangeBlock:(NEBeautySliderValueChangeBlock)valueChangeBlock;

@end
