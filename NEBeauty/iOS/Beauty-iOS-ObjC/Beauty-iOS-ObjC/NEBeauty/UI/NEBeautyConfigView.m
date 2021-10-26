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

#pragma mark - Private

- (void)setupSubviews {
    [self addSubview:self.closeButton];
    if (_type != NEBeautyConfigViewTypeBeauty) {
        [self addSubview:self.resetButton];
    }
    
    if (_dataSource && [_dataSource respondsToSelector:@selector(titleModelArrayForConfigViewWithType:)]) {
        NSArray *titleModelArray = [_dataSource titleModelArrayForConfigViewWithType:_type];
        NEScrollTitleView *titleView = [self generateTitleViewWithModelArray:titleModelArray];
        [self addSubview:titleView];
    }
    
    switch (_type) {
        case NEBeautyConfigViewTypeBeauty: {
            [self addSubview:self.beautyView];
            if (_dataSource && [_dataSource respondsToSelector:@selector(sliderModelArrayForTitleType:)]) {
                NSArray<NEBeautySliderDisplayModel *> *modelArray = [_dataSource sliderModelArrayForTitleType:NEBeautyEffectTypeBeautyBase];
                [_beautyView reloadWithSliderModelArray:modelArray];
            }
            
            break;
        }
        case NEBeautyConfigViewTypeFilter: {
            [self addSubview:self.filterStrengthView];
            [self addSubview:self.filterView];
            if (_dataSource && [_dataSource respondsToSelector:@selector(itemModelArrayForConfigViewWithType:effectType:)]) {
                NSArray *itemModelArray = [_dataSource itemModelArrayForConfigViewWithType:NEBeautyConfigViewTypeFilter effectType:NEBeautyEffectTypeFilter];
                [_filterView reloadWithModelArray:itemModelArray];
            }
            
            break;
        }
        case NEBeautyConfigViewTypeSticker: {
            [self addSubview:self.stickerView];
            if (_dataSource && [_dataSource respondsToSelector:@selector(itemModelArrayForConfigViewWithType:effectType:)]) {
                NSArray *itemModelArray = [_dataSource itemModelArrayForConfigViewWithType:NEBeautyConfigViewTypeSticker effectType:NEBeautyEffectTypeSticker2D];
                [_stickerView reloadWithModelArray:itemModelArray];
            }
            
            break;
        }
        case NEBeautyConfigViewTypeMakeup: {
            [self addSubview:self.makeupView];
            if (_dataSource && [_dataSource respondsToSelector:@selector(itemModelArrayForConfigViewWithType:effectType:)]) {
                NSArray *itemModelArray = [_dataSource itemModelArrayForConfigViewWithType:NEBeautyConfigViewTypeMakeup effectType:NEBeautyEffectTypeMakeup];
                [_makeupView reloadWithModelArray:itemModelArray];
            }
            
            break;
        }
            
        default:
            break;
    }
}

- (void)changeContentWithEffectType:(NEBeautyEffectType)type {
    switch (_type) {
        case NEBeautyConfigViewTypeBeauty: {
            if (_dataSource && [_dataSource respondsToSelector:@selector(sliderModelArrayForTitleType:)]) {
                NSArray<NEBeautySliderDisplayModel *> *modelArray = [_dataSource sliderModelArrayForTitleType:type];
                [_beautyView reloadWithSliderModelArray:modelArray];
            }
            
            break;
        }
        case NEBeautyConfigViewTypeFilter: {
            break;
        }
        case NEBeautyConfigViewTypeSticker: {
            if (_dataSource && [_dataSource respondsToSelector:@selector(itemModelArrayForConfigViewWithType:effectType:)]) {
                NSArray *itemModelArray = [_dataSource itemModelArrayForConfigViewWithType:_type effectType:type];
                [_stickerView reloadWithModelArray:itemModelArray];
            }
            
            break;
        }
        case NEBeautyConfigViewTypeMakeup: {
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
        [titleArray addObject:model.title ?: @"title"];
        [typeArray addObject:@(model.type)];
    }
    
    __weak typeof(self) weakSelf = self;
    CGFloat leftOffset = (_type != NEBeautyConfigViewTypeBeauty) ? 60 + 30 : 60;
    NEScrollTitleView *titleView = [[NEScrollTitleView alloc] initWithFrame:CGRectMake(leftOffset, 0, SCREEN_WIDTH - leftOffset, kNEBeautyTitleViewHeight) titles:titleArray effectsType:typeArray titleOnClick:^(NETitleViewItem *titleView, NSInteger index, NEBeautyEffectType type) {
        [weakSelf changeContentWithEffectType:type];
    }];
    
    return titleView;
}

- (void)clearSelection {
    switch (_type) {
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
        [_delegate didTriggerResetActionWithConfigViewType:_type];
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
        
        NEBeautySliderDisplayModel *model = [[NEBeautySliderDisplayModel alloc] init];
        model.type = NEBeautySliderTypeFilterStrength;
        model.title = @"强度";
        model.imageName = nil;
        model.value = 0;
        [_filterStrengthView updateWithModel:model];
        
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
