//
//  NEBeautyConfigView.m
//  Beauty-iOS-ObjC
//
//  Created by zhangchenliang on 2021/10/12.
//  Copyright © 2021 NetEase. All rights reserved.
//

#import "NEBeautyConfigView.h"
#import "NEScrollTitleView.h"
#import "NEBeautySliderView.h"
#import "NEBeautySliderTableView.h"

#define kNEBeautyTitleViewHeight 40
#define kNEBeautyContentHeight 200

@interface NEBeautyConfigView ()

@property (nonatomic, assign) NEBeautyConfigViewType type;
@property (nonatomic, assign) NEBeautyConfigViewType containerType;
@property (nonatomic, weak) id<NEBeautyConfigViewDataSource> dataSource;
@property (nonatomic, weak) id<NEBeautyConfigViewDelegate> delegate;

@property (nonatomic, strong) UIButton *closeButton;
@property (nonatomic, strong) UIButton *resetButton;

// 美颜
@property (nonatomic, strong) NEBeautySliderTableView *beautyView;

// 滤镜
@property (nonatomic, strong) NEBeautySliderView *filterStrengthView;
@property (nonatomic, strong) NEFilterCollectionView *filterView;

// 贴纸
@property (nonatomic, strong) NECollectionView *stickerView;

// 美妆
@property (nonatomic, strong) NECollectionView *makeupView;

@end

@implementation NEBeautyConfigView

#pragma mark - Life Cycle

- (void)dealloc {
    [self clearSelection];
}

#pragma mark - Public

- (instancetype)initWithType:(NEBeautyConfigViewType)type
                  dataSource:(id<NEBeautyConfigViewDataSource>)dataSource
                    delegate:(id<NEBeautyConfigViewDelegate>)delegate {
    self = [super initWithFrame:CGRectMake(0, SCREEN_HEIGHT, SCREEN_WIDTH, kNEBeautyTitleViewHeight + kNEBeautyContentHeight)];
    if (self) {
        self.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.8];
        
        _type = type;
        _containerType = NEBeautyConfigViewTypeBeauty;
        _dataSource = dataSource;
        _delegate = delegate;
        
        [self setupSubviews];
    }
    
    return self;
}

- (void)displayWithContainer:(UIView *)container {
    [container addSubview:self];
    
    [UIView animateWithDuration:0.1 delay:0 options:UIViewAnimationOptionCurveEaseInOut animations:^{
        CGRect frame = self.frame;
        frame.origin.y = SCREEN_HEIGHT - kNEBeautyTitleViewHeight - kNEBeautyContentHeight;
        self.frame = frame;
    } completion:^(BOOL finished) {
        
    }];
}

- (void)dismiss {
    if (!self.superview) {
        return;
    }
    
    [UIView animateWithDuration:0.1 delay:0 options:UIViewAnimationOptionCurveEaseInOut animations:^{
        CGRect frame = self.frame;
        frame.origin.y = SCREEN_HEIGHT;
        self.frame = frame;
    } completion:^(BOOL finished) {
        [self removeFromSuperview];
    }];
}

- (void)reloadData {
    if (_type & NEBeautyConfigViewTypeBeauty) {
        if (_dataSource && [_dataSource respondsToSelector:@selector(sliderModelArrayForTitleType:)]) {
            NSArray<NEBeautySliderDisplayModel *> *modelArray = [_dataSource sliderModelArrayForTitleType:NEBeautyEffectTypeBeautyBase];
            [self.beautyView reloadWithSliderModelArray:modelArray];
        }
    }
    
    if (_type & NEBeautyConfigViewTypeFilter) {
        if (_dataSource && [_dataSource respondsToSelector:@selector(sliderModelForFilterStrength)]) {
            NEBeautySliderDisplayModel *model = [_dataSource sliderModelForFilterStrength];
            [self.filterStrengthView updateWithModel:model];
        }
        
        if (_dataSource && [_dataSource respondsToSelector:@selector(itemModelArrayForConfigViewWithType:effectType:)]) {
            NSArray *itemModelArray = [_dataSource itemModelArrayForConfigViewWithType:NEBeautyConfigViewTypeFilter effectType:NEBeautyEffectTypeFilter];
            [self.filterView reloadWithModelArray:itemModelArray];
        }
    }
    
    if (_type & NEBeautyConfigViewTypeSticker) {
        if (_dataSource && [_dataSource respondsToSelector:@selector(itemModelArrayForConfigViewWithType:effectType:)]) {
            NSArray *itemModelArray = [_dataSource itemModelArrayForConfigViewWithType:NEBeautyConfigViewTypeSticker effectType:NEBeautyEffectTypeSticker2D];
            [self.stickerView reloadWithModelArray:itemModelArray];
        }
    }
    
    if (_type & NEBeautyConfigViewTypeMakeup) {
        if (_dataSource && [_dataSource respondsToSelector:@selector(itemModelArrayForConfigViewWithType:effectType:)]) {
            NSArray *itemModelArray = [_dataSource itemModelArrayForConfigViewWithType:NEBeautyConfigViewTypeMakeup effectType:NEBeautyEffectTypeMakeup];
            [self.makeupView reloadWithModelArray:itemModelArray];
        }
    }
}

#pragma mark - Private

- (void)setupSubviews {
    [self addSubview:self.closeButton];
    [self addSubview:self.resetButton];
    
    NSMutableArray *titleModels = [NSMutableArray array];
    
    if (_type & NEBeautyConfigViewTypeFilter) {
        _containerType = NEBeautyConfigViewTypeFilter;
        
        if (_dataSource && [_dataSource respondsToSelector:@selector(titleModelArrayForConfigViewWithType:)]) {
            NSArray *titleModelArray = [_dataSource titleModelArrayForConfigViewWithType:NEBeautyConfigViewTypeFilter];
            [titleModels addObjectsFromArray:titleModelArray];
        }
        
        if (_dataSource && [_dataSource respondsToSelector:@selector(sliderModelForFilterStrength)]) {
            NEBeautySliderDisplayModel *model = [_dataSource sliderModelForFilterStrength];
            [self.filterStrengthView updateWithModel:model];
        }
        
        if (_dataSource && [_dataSource respondsToSelector:@selector(itemModelArrayForConfigViewWithType:effectType:)]) {
            NSArray *itemModelArray = [_dataSource itemModelArrayForConfigViewWithType:NEBeautyConfigViewTypeFilter effectType:NEBeautyEffectTypeFilter];
            [self.filterView reloadWithModelArray:itemModelArray];
        }
    }
    
    if (_type & NEBeautyConfigViewTypeBeauty) {
        _containerType = NEBeautyConfigViewTypeBeauty;
        
        if (_dataSource && [_dataSource respondsToSelector:@selector(titleModelArrayForConfigViewWithType:)]) {
            NSArray *titleModelArray = [_dataSource titleModelArrayForConfigViewWithType:NEBeautyConfigViewTypeBeauty];
            [titleModels addObjectsFromArray:titleModelArray];
        }
        
        if (_dataSource && [_dataSource respondsToSelector:@selector(sliderModelArrayForTitleType:)]) {
            NSArray<NEBeautySliderDisplayModel *> *modelArray = [_dataSource sliderModelArrayForTitleType:NEBeautyEffectTypeBeautyBase];
            [self.beautyView reloadWithSliderModelArray:modelArray];
        }
    }
    
    if (_type & NEBeautyConfigViewTypeSticker) {
        _containerType = NEBeautyConfigViewTypeSticker;
        
        if (_dataSource && [_dataSource respondsToSelector:@selector(titleModelArrayForConfigViewWithType:)]) {
            NSArray *titleModelArray = [_dataSource titleModelArrayForConfigViewWithType:NEBeautyConfigViewTypeSticker];
            [titleModels addObjectsFromArray:titleModelArray];
        }
        
        if (_dataSource && [_dataSource respondsToSelector:@selector(itemModelArrayForConfigViewWithType:effectType:)]) {
            NSArray *itemModelArray = [_dataSource itemModelArrayForConfigViewWithType:NEBeautyConfigViewTypeSticker effectType:NEBeautyEffectTypeSticker2D];
            [self.stickerView reloadWithModelArray:itemModelArray];
        }
    }
    
    if (_type & NEBeautyConfigViewTypeMakeup) {
        _containerType = NEBeautyConfigViewTypeMakeup;
        
        if (_dataSource && [_dataSource respondsToSelector:@selector(titleModelArrayForConfigViewWithType:)]) {
            NSArray *titleModelArray = [_dataSource titleModelArrayForConfigViewWithType:NEBeautyConfigViewTypeMakeup];
            [titleModels addObjectsFromArray:titleModelArray];
        }
        
        if (_dataSource && [_dataSource respondsToSelector:@selector(itemModelArrayForConfigViewWithType:effectType:)]) {
            NSArray *itemModelArray = [_dataSource itemModelArrayForConfigViewWithType:NEBeautyConfigViewTypeMakeup effectType:NEBeautyEffectTypeMakeup];
            [self.makeupView reloadWithModelArray:itemModelArray];
        }
    }
    
    NEScrollTitleView *titleView = [self generateTitleViewWithModelArray:titleModels];
    [self addSubview:titleView];
}

- (void)changeContentWithEffectType:(NEBeautyEffectType)type {
    [_beautyView removeFromSuperview];
    [_filterStrengthView removeFromSuperview];
    [_filterView removeFromSuperview];
    [_stickerView removeFromSuperview];
    [_makeupView removeFromSuperview];
    
    switch (type) {
        case NEBeautyEffectTypeBeautyBase:
        case NEBeautyEffectTypeBeautyShape:
        case NEBeautyEffectTypeBeautyAdv:
        case NEBeautyEffectTypeBeautyAdv2:
        case NEBeautyEffectTypeBeautyAdv3: {
            if (_dataSource && [_dataSource respondsToSelector:@selector(sliderModelArrayForTitleType:)]) {
                NSArray<NEBeautySliderDisplayModel *> *modelArray = [_dataSource sliderModelArrayForTitleType:type];
                [_beautyView reloadWithSliderModelArray:modelArray];
            }
            
            [self addSubview:_beautyView];
            _containerType = NEBeautyConfigViewTypeBeauty;
            
            break;
        }
        case NEBeautyEffectTypeFilter: {
            [self addSubview:_filterStrengthView];
            [self addSubview:_filterView];
            _containerType = NEBeautyConfigViewTypeFilter;
            
            break;
        }
        case NEBeautyEffectTypeSticker2D:
        case NEBeautyEffectTypeSticker3D:
        case NEBeautyEffectTypeStickerFaceChange:
        case NEBeautyEffectTypeStickerParticle: {
            if (_dataSource && [_dataSource respondsToSelector:@selector(itemModelArrayForConfigViewWithType:effectType:)]) {
                NSArray *itemModelArray = [_dataSource itemModelArrayForConfigViewWithType:NEBeautyConfigViewTypeSticker effectType:type];
                [_stickerView reloadWithModelArray:itemModelArray];
            }
            
            [self addSubview:_stickerView];
            _containerType = NEBeautyConfigViewTypeSticker;
            
            break;
        }
        case NEBeautyEffectTypeMakeup: {
            [self addSubview:_makeupView];
            _containerType = NEBeautyConfigViewTypeMakeup;
            
            break;
        }
            
        default:
            break;
    }
}

- (void)changeFilterWithItemModel:(NECollectionViewDisplayModel *)model {
    if (_delegate && [_delegate respondsToSelector:@selector(didSelectItemWithConfigViewType:model:)]) {
        [_delegate didSelectItemWithConfigViewType:NEBeautyConfigViewTypeFilter model:model];
    }
}

- (void)changeStickerWithItemModel:(NECollectionViewDisplayModel *)model {
    if (_delegate && [_delegate respondsToSelector:@selector(didSelectItemWithConfigViewType:model:)]) {
        [_delegate didSelectItemWithConfigViewType:NEBeautyConfigViewTypeSticker model:model];
    }
}

- (void)changeMakeupWithItemModel:(NECollectionViewDisplayModel *)model {
    if (_delegate && [_delegate respondsToSelector:@selector(didSelectItemWithConfigViewType:model:)]) {
        [_delegate didSelectItemWithConfigViewType:NEBeautyConfigViewTypeMakeup model:model];
    }
}

- (void)adjustBeautyEffectWithType:(NEBeautySliderType)type value:(float)value {
    if (_delegate && [_delegate respondsToSelector:@selector(didChangeSliderValueWithType:value:)]) {
        [_delegate didChangeSliderValueWithType:type value:value];
    }
}

- (NEScrollTitleView *)generateTitleViewWithModelArray:(NSArray<NETitleDisplayModel *> *)modelArray {
    NSMutableArray *titleArray = [NSMutableArray array];
    NSMutableArray *typeArray = [NSMutableArray array];
    for (NETitleDisplayModel *model in modelArray) {
        [titleArray addObject:model.title ?: @"unknown"];
        [typeArray addObject:@(model.type)];
    }
    
    __weak typeof(self) weakSelf = self;
    CGFloat leftOffset = 60 + 30;
    NEScrollTitleView *titleView = [[NEScrollTitleView alloc] initWithFrame:CGRectMake(leftOffset, 0, SCREEN_WIDTH - leftOffset, kNEBeautyTitleViewHeight) titles:titleArray effectsType:typeArray titleOnClick:^(NETitleViewItem *titleView, NSInteger index, NEBeautyEffectType type) {
        [weakSelf changeContentWithEffectType:type];
    }];
    
    return titleView;
}

- (void)clearSelection {
    switch (_containerType) {
        case NEBeautyConfigViewTypeFilter: {
            [_filterView clearSelection];
            
            break;
        }
        case NEBeautyConfigViewTypeSticker: {
            [_stickerView clearSelection];
            
            break;
        }
        case NEBeautyConfigViewTypeMakeup: {
            [_makeupView clearSelection];
            
            break;
        }
            
        default:
            break;
    }
}

#pragma mark - Action

- (void)onCloseAction {
    [UIView animateWithDuration:0.1 delay:0 options:UIViewAnimationOptionCurveEaseInOut animations:^{
        CGRect frame = self.frame;
        frame.origin.y = SCREEN_HEIGHT;
        self.frame = frame;
    } completion:^(BOOL finished) {
        [self removeFromSuperview];
    }];
}

- (void)onResetAction {
    [self clearSelection];
    
    if (_delegate && [_delegate respondsToSelector:@selector(didTriggerResetActionWithConfigViewType:)]) {
        [_delegate didTriggerResetActionWithConfigViewType:_containerType];
    }
}

- (void)onSliderValueChanged:(UISlider *)slider {
    if (_delegate && [_delegate respondsToSelector:@selector(didChangeSliderValueWithType:value:)]) {
        [_delegate didChangeSliderValueWithType:slider.tag value:slider.value];
    }
}

#pragma mark - Getter

- (UIButton *)closeButton {
    if (!_closeButton) {
        _closeButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 60, kNEBeautyTitleViewHeight)];
        [_closeButton setImage:[UIImage imageNamed:@"closeMenu"] forState:UIControlStateNormal];
        [_closeButton.imageView setContentMode:UIViewContentModeScaleAspectFit];
        [_closeButton addTarget:self action:@selector(onCloseAction) forControlEvents:UIControlEventTouchUpInside];
    }
    
    return _closeButton;
}

- (UIButton *)resetButton {
    if (!_resetButton) {
        _resetButton = [[UIButton alloc] initWithFrame:CGRectMake(60, 0, 30, kNEBeautyTitleViewHeight)];
        [_resetButton setImage:[UIImage imageNamed:@"resetItem"] forState:UIControlStateNormal];
        [_resetButton.imageView setContentMode:UIViewContentModeScaleAspectFit];
        [_resetButton addTarget:self action:@selector(onResetAction) forControlEvents:UIControlEventTouchUpInside];
    }
    
    return _resetButton;
}

- (NEBeautySliderTableView *)beautyView {
    if (!_beautyView) {
        __weak typeof (self) weakSelf = self;
        _beautyView = [[NEBeautySliderTableView alloc] initWithFrame:CGRectMake(0, kNEBeautyTitleViewHeight, SCREEN_WIDTH, kNEBeautyContentHeight) valueChangeBlock:^(NEBeautySliderType type, float value) {
            [weakSelf adjustBeautyEffectWithType:type value:value];
        }];
    }
    
    return _beautyView;
}

- (NEBeautySliderView *)filterStrengthView {
    if (!_filterStrengthView) {
        _filterStrengthView = [[NEBeautySliderView alloc] initWithFrame:CGRectMake(0, kNEBeautyTitleViewHeight, SCREEN_WIDTH, 60)];
         
        __weak typeof (self) weakSelf = self;
        [_filterStrengthView updateValueChangeBlock:^(NEBeautySliderType type, float value) {
            if (weakSelf.delegate && [weakSelf.delegate respondsToSelector:@selector(didChangeSliderValueWithType:value:)]) {
                [weakSelf.delegate didChangeSliderValueWithType:type value:value];
            }
        }];
    }
    
    return _filterStrengthView;
}

- (NEFilterCollectionView *)filterView {
    if (!_filterView) {
        __weak typeof (self) weakSelf = self;
        _filterView = [[NEFilterCollectionView alloc] initWithFrame:CGRectMake(0,
                                                                               kNEBeautyTitleViewHeight + 60,
                                                                               SCREEN_WIDTH,
                                                                               kNEBeautyContentHeight - 60)
                                                     selectionBlock:^(NECollectionViewDisplayModel *model) {
            [weakSelf changeFilterWithItemModel:model];
        }];
    }
    
    return _filterView;
}

- (NECollectionView *)stickerView {
    if (!_stickerView) {
        __weak typeof (self) weakSelf = self;
        _stickerView = [[NECollectionView alloc] initWithFrame:CGRectMake(0,
                                                                          kNEBeautyTitleViewHeight,
                                                                          SCREEN_WIDTH,
                                                                          kNEBeautyContentHeight)
                                                selectionBlock:^(NECollectionViewDisplayModel *model) {
            [weakSelf changeStickerWithItemModel:model];
        }];
    }
    
    return _stickerView;
}

- (NECollectionView *)makeupView {
    if (!_makeupView) {
        __weak typeof (self) weakSelf = self;
        _makeupView = [[NECollectionView alloc] initWithFrame:CGRectMake(0,
                                                                         kNEBeautyTitleViewHeight,
                                                                         SCREEN_WIDTH,
                                                                         kNEBeautyContentHeight)
                                               selectionBlock:^(NECollectionViewDisplayModel *model) {
            [weakSelf changeMakeupWithItemModel:model];
        }];
    }
    
    return _makeupView;
}

@end
