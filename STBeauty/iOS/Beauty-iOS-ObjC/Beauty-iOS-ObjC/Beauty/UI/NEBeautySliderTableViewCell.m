//
//  NEBeautySliderTableViewCell.m
//  Beauty-iOS-ObjC
//
//  Created by zhangchenliang on 2021/10/15.
//  Copyright © 2021 NetEase. All rights reserved.
//

#import "NEBeautySliderTableViewCell.h"

@interface NEBeautySliderTableViewCell ()

@property (nonatomic, strong) NEBeautySliderView *sliderView;

@end

@implementation NEBeautySliderTableViewCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        [self.contentView addSubview:self.sliderView];
    }
    
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    _sliderView.frame = self.bounds;
}

- (void)updateContentWithModel:(NEBeautySliderDisplayModel *)model {
    [_sliderView updateWithModel:model];
}

- (void)updateValueChangeBlock:(NEBeautySliderValueChangeBlock)valueChangeBlock {
    [_sliderView updateValueChangeBlock:valueChangeBlock];
}

- (NEBeautySliderView *)sliderView {
    if (!_sliderView) {
        _sliderView = [[NEBeautySliderView alloc] initWithFrame:CGRectZero];
    }
    
    return _sliderView;
}

@end
