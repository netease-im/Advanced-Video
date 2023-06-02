//
//  NEBeautySliderView.m
//  Beauty-iOS-ObjC
//
//  Created by zhangchenliang on 2021/10/18.
//  Copyright © 2021 NetEase. All rights reserved.
//

#import "NEBeautySliderView.h"
#import <Masonry/Masonry.h>

@interface NEBeautySliderView ()

@property (nonatomic, strong) NEBeautySliderDisplayModel *model;
@property (nonatomic, copy) NEBeautySliderValueChangeBlock valueChangeBlock;

@property (nonatomic, strong) UIImageView *thumbImageView;
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UISlider *slider;
@property (nonatomic, strong) UILabel *valueLabel;

@end

@implementation NEBeautySliderView

#pragma mark - Life Cycle

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        
        [self setupSubviews];
    }
    
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    [_thumbImageView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self);
        make.left.equalTo(self.mas_left).offset(20);
        make.size.mas_equalTo(CGSizeMake(30, 30));
    }];
    
    [_titleLabel mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self);
        make.left.equalTo(_thumbImageView.mas_right).offset(5);
        make.size.mas_equalTo(CGSizeMake(40, 20));
    }];
    
    [_slider mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self);
        make.left.equalTo(_titleLabel.mas_right).offset(5);
        make.right.equalTo(_valueLabel.mas_left).offset(-5);
    }];
    
    [_valueLabel mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self);
        make.right.equalTo(self.mas_right).offset(-20);
        make.size.mas_equalTo(CGSizeMake(30, 20));
    }];
}

#pragma mark - Public

- (void)updateWithModel:(NEBeautySliderDisplayModel *)model {
    _model = model;
    
    _thumbImageView.image = [UIImage imageNamed:model.imageName];
    _titleLabel.text = model.title;
    _slider.value = model.value;
    _valueLabel.text = [NSString stringWithFormat:@"%d", (int)(model.value * 100)];
}

- (void)updateValueChangeBlock:(NEBeautySliderValueChangeBlock)valueChangeBlock {
    _valueChangeBlock = valueChangeBlock;
}

#pragma mark - Private

- (void)setupSubviews {
    [self addSubview:self.thumbImageView];
    [self addSubview:self.titleLabel];
    [self addSubview:self.slider];
    [self addSubview:self.valueLabel];
}

#pragma mark - Action

- (void)onSliderValueChanged:(UISlider *)slider {
    _model.value = slider.value;
    _valueLabel.text = [NSString stringWithFormat:@"%d", (int)(slider.value * 100)];
    
    if (_valueChangeBlock) {
        _valueChangeBlock(_model.type, slider.value);
    }
}

#pragma mark - Getter

- (UIImageView *)thumbImageView {
    if (!_thumbImageView) {
        _thumbImageView = [[UIImageView alloc] initWithFrame:CGRectZero];
        _thumbImageView.contentMode = UIViewContentModeScaleAspectFit;
    }
    
    return _thumbImageView;
}

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        _titleLabel.textColor = [UIColor whiteColor];
        _titleLabel.font = [UIFont systemFontOfSize:11];
        _titleLabel.textAlignment = NSTextAlignmentCenter;
        _titleLabel.lineBreakMode = NSLineBreakByTruncatingMiddle;
    }
    
    return _titleLabel;
}

- (UISlider *)slider {
    if (!_slider) {
        _slider = [[UISlider alloc] initWithFrame:CGRectZero];
        _slider.thumbTintColor = UIColorFromRGB(0x9e4fcb);
        _slider.minimumTrackTintColor = UIColorFromRGB(0x9e4fcb);
        [_slider addTarget:self action:@selector(onSliderValueChanged:) forControlEvents:UIControlEventValueChanged];
    }
    
    return _slider;
}

- (UILabel *)valueLabel {
    if (!_valueLabel) {
        _valueLabel = [[UILabel alloc] init];
        _valueLabel.textColor = [UIColor whiteColor];
        _valueLabel.font = [UIFont systemFontOfSize:15];
        _valueLabel.textAlignment = NSTextAlignmentLeft;
    }
    
    return _valueLabel;
}

@end
