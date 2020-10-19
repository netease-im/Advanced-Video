//
//  NTESPickView.m
//  DeviceManager-iOS-ObjC
//
//  Created by Think on 2020/9/15.
//  Copyright © 2020 Wenchao Ding. All rights reserved.
//

#import "NTESPickView.h"
#import "UIView+NTES.h"
#import "NTESGlobalMacro.h"
#import "NTESPickViewSettingModel.h"

@interface NTESPickView () <UIPickerViewDelegate, UIPickerViewDataSource>

@property (nonatomic, strong)   UIPickerView    *pickerView;
@property (nonatomic, strong)   UIView          *pickBg;
@property (nonatomic, strong)   NSArray <NTESPickViewSettingModel *>        *datas;
@property (nonatomic, weak)     UIViewController <NTESPickViewDelegate>     *delegate;

@end

@implementation NTESPickView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = UIColorFromRGBA(0x000000, 0.5);
        [self addSubview:self.pickBg];
        [self addSubview:self.pickerView];
        
        self.userInteractionEnabled = YES;
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(dismiss)];
        [self addGestureRecognizer:tap];
    }
    return self;
}

- (void)layoutSubviews
{
    CGFloat height = 216;
    CGFloat topOffset = self.height - height;
    if (IPHONE_X) {
        topOffset -= 34;
    }
    self.pickerView.frame = CGRectMake(0, topOffset, UIScreenWidth, height);
    self.pickBg.frame = CGRectMake(0, topOffset, UIScreenWidth, self.height - topOffset);
}

- (void)setDatas:(NSArray<NTESPickViewSettingModel *> *)datas
{
    _datas = datas;
    [self.pickerView reloadAllComponents];
}

+ (void)showWithModels:(NSArray <NTESPickViewSettingModel *> *)models
              delegate:(UIViewController <NTESPickViewDelegate> *)delegate
{
    NTESPickView *view = [[NTESPickView alloc] initWithFrame:CGRectMake(0, 0, UIScreenWidth, UIScreenHeight)];
    view.delegate = delegate;
    view.datas = models;
    [delegate.navigationController.view addSubview:view];
}

- (void)dismiss
{
    self.hidden = YES;
    [self removeFromSuperview];
}

#pragma mark - UIPickerView delegate

- (CGFloat)pickerView:(UIPickerView *)pickerView rowHeightForComponent:(NSInteger)component
{
    return 44;
}

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return [self.datas count];
}

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    NTESPickViewSettingModel *obj = self.datas[row];
    return obj.title;
}

- (UIView *)pickerView:(UIPickerView *)pickerView viewForRow:(NSInteger)row forComponent:(NSInteger)component reusingView:(UIView *)view
{
    UILabel* pickerLabel = (UILabel*)view;
    if (!pickerLabel){
        pickerLabel = [[UILabel alloc] init];
        pickerLabel.adjustsFontSizeToFitWidth = YES;
        [pickerLabel setTextAlignment:NSTextAlignmentCenter];
        [pickerLabel setTextColor:UIColorFromRGB(0x444444)];
        [pickerLabel setFont:[UIFont boldSystemFontOfSize:16]];
    }
    pickerLabel.text = [self pickerView:pickerView titleForRow:row forComponent:component];
    return pickerLabel;
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    if (self.delegate && [self.delegate respondsToSelector:@selector(choseSettingModel:)]) {
        NTESPickViewSettingModel *obj = self.datas[row];
        [self.delegate choseSettingModel:obj];
    }
}

#pragma mark - lazy load

- (UIPickerView *)pickerView
{
    if (!_pickerView) {
        _pickerView = [[UIPickerView alloc] init];
        _pickerView.delegate = self;
        _pickerView.dataSource = self;
    }
    return _pickerView;
}

- (UIView *)pickBg
{
    if (!_pickBg) {
        _pickBg = [[UIView alloc] init];
        _pickBg.backgroundColor = [UIColor whiteColor];
    }
    return _pickBg;
}

@end
