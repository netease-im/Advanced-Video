//
//  NEBeautySliderTableView.h
//  Beauty-iOS-ObjC
//
//  Created by zhangchenliang on 2021/10/15.
//  Copyright © 2021 NetEase. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NEBeautySliderTableViewCell.h"

@interface NEBeautySliderTableView : UITableView

- (instancetype)initWithFrame:(CGRect)frame valueChangeBlock:(NEBeautySliderValueChangeBlock)valueChangeBlock;

- (void)reloadWithSliderModelArray:(NSArray<NEBeautySliderDisplayModel *> *)modelArray;

@end
