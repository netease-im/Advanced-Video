//
//  NETSDemoSettingCell.m
//  VoiceChanger-iOS-ObjC
//
//  Created by Think on 2020/11/5.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NETSDemoSettingCell.h"
#import "NTESGlobalMacro.h"
#import "NETSDemoSettingModel.h"
#import "UIView+NTES.h"

@interface NETSDemoSettingCheckCell ()

/// 标题控件
@property (nonatomic, strong)   UILabel     *titleLab;
/// 选中标志控件
@property (nonatomic, strong)   UIImageView *checkView;
/// 选中标志
@property (nonatomic, assign)   BOOL        check;

@end

@implementation NETSDemoSettingCheckCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        self.checkView.frame = CGRectMake(15, (44 - 20) / 2, 20, 20);
        [self.contentView addSubview:self.checkView];
        
        self.titleLab.frame = CGRectMake(self.checkView.right + 6, 0, 100, 44);
        [self.contentView addSubview:self.titleLab];
    }
    return self;
}

/// 安装cell
- (void)installWithModel:(NETSDemoSettingCheckModel *)model indexPath:(NSIndexPath *)indexPath selectVal:(NSInteger)selectVal
{
    self.titleLab.text = model.title;
    self.check = (model.value == selectVal);
    
    if ([model.title isEqualToString:@"自定义"]) {
        // 自定义 EQ 配置时，显示扩展箭头标志
        self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    } else {
        self.accessoryType = UITableViewCellAccessoryNone;
    }
}

/// cell选中状态变更
- (void)setCheck:(BOOL)check
{
    _check = check;
    NSString *imgName = _check ? @"check_ico" : @"uncheck_ico";
    self.checkView.image = [UIImage imageNamed:imgName];
}

+ (NETSDemoSettingCheckCell *)cellWithTableView:(UITableView *)tableView
                                      indexPath:(NSIndexPath *)indexPath
                                          datas:(NSArray <NETSDemoSettingCheckModel *> *)datas
                                      selectVal:(NSInteger)selectVal
{
    if ([datas count] <= indexPath.row) {
        return [NETSDemoSettingCheckCell new];
    }
    
    NETSDemoSettingCheckCell *cell = [tableView dequeueReusableCellWithIdentifier:[NETSDemoSettingCheckCell description]];
    id model = datas[indexPath.row];
    [cell installWithModel:model indexPath:indexPath selectVal:selectVal];
    return cell;
}

+ (CGFloat)heightForTableView:(UITableView *)tableView
                    indexPath:(NSIndexPath *)indexPath
                        datas:(NSArray *)datas
{
    return 44;
}

#pragma mark - lazy load

- (UILabel *)titleLab
{
    if (!_titleLab) {
        _titleLab = [[UILabel alloc] init];
        _titleLab.font = [UIFont systemFontOfSize:14];
    }
    return _titleLab;
}

- (UIImageView *)checkView
{
    if (!_checkView) {
        UIImage *img = [UIImage imageNamed:@"check_ico"];
        _checkView = [[UIImageView alloc] initWithImage:img];
    }
    return _checkView;
}

@end

///

@interface NETSDemoSettingSlideCell ()

/// cell模型
@property (nonatomic, strong)   NETSDemoSettingSlideModel   *model;
/// 标题控件
@property (nonatomic, strong)   UILabel     *titleLab;
/// 最小值显示控件
@property (nonatomic, strong)   UILabel     *minValLab;
/// 滑动控件
@property (nonatomic, strong)   UISlider    *sliderView;
/// 最大值显示控件
@property (nonatomic, strong)   UILabel     *maxValLab;

@end

@implementation NETSDemoSettingSlideCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        self.titleLab.frame = CGRectMake(0, 0, UIScreenWidth, 20);
        [self.contentView addSubview:self.titleLab];
        
        self.minValLab.frame = CGRectMake(15, self.titleLab.bottom, 30, 40);
        [self.contentView addSubview:self.minValLab];
        
        self.sliderView.frame = CGRectMake(self.minValLab.right, self.titleLab.bottom, UIScreenWidth - 30 - 60, 40);
        [self.contentView addSubview:self.sliderView];
        
        self.maxValLab.frame = CGRectMake(UIScreenWidth - 30 - 15, self.titleLab.bottom, 30, 40);
        [self.contentView addSubview:self.maxValLab];
    }
    return self;
}

/// 安装cell
- (void)installWithModel:(NETSDemoSettingSlideModel *)model indexPath:(NSIndexPath *)indexPath
{
    self.model = model;
    
    self.titleLab.text = [NSString stringWithFormat:@"%@(%.3f)", model.title, model.value];
    [self.sliderView setValue:model.value animated:YES];
}

+ (NETSDemoSettingSlideCell *)cellWithTableView:(UITableView *)tableView
                                      indexPath:(NSIndexPath *)indexPath
                                          datas:(NSArray <NETSDemoSettingCheckModel *> *)datas
{
    if ([datas count] <= indexPath.row) {
        return [NETSDemoSettingSlideCell new];
    }
    
    NETSDemoSettingSlideCell *cell = [tableView dequeueReusableCellWithIdentifier:[NETSDemoSettingSlideCell description]];
    id model = datas[indexPath.row];
    [cell installWithModel:model indexPath:indexPath];
    return cell;
}

+ (CGFloat)heightForTableView:(UITableView *)tableView
                    indexPath:(NSIndexPath *)indexPath
                        datas:(NSArray *)datas
{
    return 60;
}

/// 滑块值变化事件
- (void)valueChanged:(UISlider *)slider
{
    _model.value = slider.value;
    _titleLab.text = [NSString stringWithFormat:@"%@(%.3f)", _model.title, _model.value];
}

#pragma mark - lazy load

- (UILabel *)titleLab
{
    if (!_titleLab) {
        _titleLab = [[UILabel alloc] init];
        _titleLab.font = [UIFont systemFontOfSize:12];
        _titleLab.textAlignment = NSTextAlignmentCenter;
    }
    return _titleLab;
}

- (UILabel *)minValLab
{
    if (!_minValLab) {
        _minValLab = [[UILabel alloc] init];
        _minValLab.font = [UIFont systemFontOfSize:12];
        _minValLab.text = @"-15";
        _minValLab.textAlignment = NSTextAlignmentLeft;
    }
    return _minValLab;
}

- (UISlider *)sliderView
{
    if (!_sliderView) {
        _sliderView = [[UISlider alloc] init];
        _sliderView.minimumValue = -15;
        _sliderView.maximumValue = 15;
        _sliderView.value = 0;
        [_sliderView addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
    }
    return _sliderView;
}

- (UILabel *)maxValLab
{
    if (!_maxValLab) {
        _maxValLab = [[UILabel alloc] init];
        _maxValLab.font = [UIFont systemFontOfSize:12];
        _maxValLab.text = @"15";
        _maxValLab.textAlignment = NSTextAlignmentRight;
    }
    return _maxValLab;
}

@end
