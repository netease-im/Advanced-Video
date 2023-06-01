//
//  NEBeautySliderTableViewCell.h
//  Beauty-iOS-ObjC
//
//  Created by zhangchenliang on 2021/10/15.
//  Copyright © 2021 NetEase. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NEBeautySliderView.h"

@interface NEBeautySliderTableViewCell : UITableViewCell

- (void)updateContentWithModel:(NEBeautySliderDisplayModel *)model;

- (void)updateValueChangeBlock:(NEBeautySliderValueChangeBlock)valueChangeBlock;

@end
