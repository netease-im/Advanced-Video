//
//  NEScrollTitleView.m
//  NeCamera
//
//  Created by Ycq on 05/10/2019.
//  Copyright © 2019 NetEase Audio Lab. All rights reserved.
//

#import "NEScrollTitleView.h"


@interface NETitleViewItem : UIView

@property (nonatomic, readwrite, assign) NETitleViewStyle titleViewStyle;
@property (nonatomic, readwrite, assign) NEBeautyEffectType effectsType;

@property (nonatomic, readwrite, assign, getter=isSelected) BOOL selected;

@property (nonatomic, readwrite, strong) UIImage *normalImage;
@property (nonatomic, readwrite, strong) UIImage *selectedImage;
@property (nonatomic, readwrite, strong) UIImageView *imageView;

@property (nonatomic, readwrite, strong) UILabel *titleLabel;
@property (nonatomic, readwrite, copy) NSString *strTitle;
@property (nonatomic, readwrite, strong) UIColor *titleColor;
@property (nonatomic, readwrite, strong) UIColor *selectedTitleColor;
@property (nonatomic, readwrite, strong) UIFont *titleFont;

- (CGFloat)titleViewWidth;
- (void)adjustSubviewFrame;

@end

@interface NETitleViewItem ()

@property (nonatomic, readwrite, assign) CGSize titleSize;
@property (nonatomic, readwrite, assign) CGFloat imageHeight;
@property (nonatomic, readwrite, assign) CGFloat imageWidth;
@property (nonatomic, readwrite, strong) UIView *contentView;

@property (nonatomic, readwrite, strong) UIView *pointView;

@end

@implementation NETitleViewItem

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.userInteractionEnabled = YES;
        self.backgroundColor = [UIColor clearColor];
    }
    
    return self;
}

- (void)adjustSubviewFrame {
    CGRect contentViewFrame = self.bounds;
    contentViewFrame.size.width = [self titleViewWidth];
    contentViewFrame.origin.x = (self.frame.size.width - contentViewFrame.size.width) / 2;
    self.contentView.frame = contentViewFrame;
    
    [self addSubview:self.contentView];
    
    switch (self.titleViewStyle) {
        case NETitleViewStyleOnlyImage: {
            self.imageView.frame = self.contentView.bounds;
            [self.contentView addSubview:self.imageView];
            
            break;
        }
            
        case NETitleViewStyleOnlyCharacter: {
            self.titleLabel.frame = self.contentView.bounds;
            [self.contentView addSubview:self.titleLabel];
            
            break;
        }
            
        default:
            break;
    }
    
    self.pointView.center = CGPointMake(self.contentView.center.x, CGRectGetMaxY(self.contentView.frame) - 3);
    [self addSubview:self.pointView];
    self.pointView.hidden = YES;
}

- (CGFloat)titleViewWidth {
    CGFloat width = 0.0f;
    
    switch (self.titleViewStyle) {
        case NETitleViewStyleOnlyImage: {
            width = _imageWidth;
            
            break;
        }
            
        case NETitleViewStyleOnlyCharacter: {
            width = _titleSize.width;
            
            break;
        }
            
        default:
            break;
    }
    
    return width;
}

- (void)setNormalImage:(UIImage *)normalImage {
    _normalImage = normalImage;
    _imageWidth = normalImage.size.width;
    _imageHeight = normalImage.size.height;
    
    self.imageView.image = normalImage;
}

- (void)setSelectedImage:(UIImage *)selectedImage {
    _selectedImage = selectedImage;
    self.imageView.highlightedImage = selectedImage;
}

- (void)setTitleFont:(UIFont *)titleFont {
    _titleFont = titleFont;
    self.titleLabel.font = titleFont;
}

- (void)setStrTitle:(NSString *)strTitle {
    _strTitle = strTitle;
    self.titleLabel.text = strTitle;
    
    CGRect bounds = [strTitle boundingRectWithSize:CGSizeMake(MAXFLOAT, 0.0) options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName: self.titleLabel.font} context:nil];
    _titleSize = bounds.size;
}

- (void)setTitleColor:(UIColor *)titleColor {
    _titleColor = titleColor;
    self.titleLabel.textColor = titleColor;
}

- (void)setSelected:(BOOL)selected {
    _selected = selected;
    self.imageView.highlighted = selected;
    self.titleLabel.highlighted = selected;
    self.pointView.hidden = !selected;
}

- (void)setSelectedTitleColor:(UIColor *)selectedTitleColor {
    _selectedTitleColor = selectedTitleColor;
    self.titleLabel.highlightedTextColor = selectedTitleColor;
}

- (UIImageView *)imageView {
    if (!_imageView) {
        _imageView = [[UIImageView alloc] init];
        _imageView.contentMode = UIViewContentModeCenter;
    }
    
    return _imageView;
}

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.textAlignment = NSTextAlignmentCenter;
    }
    
    return _titleLabel;
}

- (UIView *)contentView {
    if (!_contentView) {
        _contentView = [[UIView alloc] init];
    }
    
    return _contentView;
}

- (UIView *)pointView {
    if (!_pointView) {
        
        _pointView = [[UIView alloc] init];
        _pointView.frame = CGRectMake(0, 0, 6, 6);
        _pointView.layer.cornerRadius = 3;
        _pointView.backgroundColor = UIColorFromRGB(0xbc47ff);
        _pointView.alpha = 0.6;
    }
    
    return _pointView;
}

@end


#define TITLE_MARGIN 25

@interface NEScrollTitleView () <UIScrollViewDelegate> {
    CGFloat _currentWidth;
    NSUInteger _currentIndex;
    NSUInteger _oldIndex;
}

@property (nonatomic, readwrite, strong) UIView *pointView;  //选中后title下的小点
@property (nonatomic, readwrite, strong) UIScrollView *scrollView;

//缓存所有标题
@property (nonatomic, readwrite, strong) NSMutableArray *titleViews;
@property (nonatomic, readwrite, strong) NSMutableArray *titleWidths;

@property (nonatomic, readwrite, copy) NETitleOnClickBlock onClickBlock;

@end

@implementation NEScrollTitleView

- (instancetype)initWithFrame:(CGRect)frame normalImages:(NSArray *)normalImages selectedImages:(NSArray *)selectedImages effectsType:(NSArray *)effectsType titleOnClick:(NETitleOnClickBlock)onClickBlock {
    
    return [self initWithFrame:frame normalImages:normalImages selectedImages:selectedImages titles:nil effectsType:effectsType titleOnClick:onClickBlock];
}

- (instancetype)initWithFrame:(CGRect)frame titles:(NSArray *)titles effectsType:(NSArray *)effectsType titleOnClick:(NETitleOnClickBlock)onClickBlock {
    return [self initWithFrame:frame normalImages:nil selectedImages:nil titles:titles effectsType:effectsType titleOnClick:onClickBlock];
}

- (instancetype)initWithFrame:(CGRect)frame normalImages:(NSArray *)normalImages selectedImages:(NSArray *)selectedImages titles:(NSArray *)titles effectsType:(NSArray *)effectsType titleOnClick:(NETitleOnClickBlock)onClickBlock {
    self = [super initWithFrame:frame];
    if (self) {
        _arrNormalImages = normalImages;
        _arrSelectedImages = selectedImages;
        _arrTitles = titles;
        _arrEffectsType = effectsType;
        _onClickBlock = onClickBlock;
        
        _currentIndex = 0;
        _oldIndex = 0;
        _currentWidth = frame.size.width;
        
        _scrollView.delegate = self;
        
        [self addSubview:self.scrollView];
        [self addSubview:self.pointView];
        
        [self setupTitleViews];
        [self layoutTitleViews];
    }
    
    return self;
}

- (void)setupTitleViews {
    [self.titleViews removeAllObjects];
    [self.titleWidths removeAllObjects];
    
    if (_arrTitles) {
        if (_arrTitles.count == 0) {
            return;
        }
        
        NSInteger index = 0;
        
        for (NSString *title in _arrTitles) {
            NETitleViewItem *titleView = [[NETitleViewItem alloc] initWithFrame:CGRectZero];
            titleView.tag = index;
            
            titleView.effectsType = _arrEffectsType[index].integerValue;
            
            titleView.titleFont = [UIFont systemFontOfSize:15.0];
            titleView.strTitle = title;
//            titleView.titleColor = UIColorFromRGB(0x666666);
            titleView.titleColor = [UIColor whiteColor];
            titleView.selectedTitleColor = UIColorFromRGB(0x9e4fcb);
            titleView.titleViewStyle = NETitleViewStyleOnlyCharacter;
            
            UITapGestureRecognizer *tapGes = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(titleViewOnClick:)];
            [titleView addGestureRecognizer:tapGes];
            
            CGFloat titleViewWidth = [titleView titleViewWidth];
            
            [self.titleWidths addObject:@(titleViewWidth)];
            [self.titleViews addObject:titleView];
            [self.scrollView addSubview:titleView];
            ++index;
        }
    } else {
        if (_arrNormalImages.count == 0) {
            return;
        }
        
        NSInteger index = 0;
        for (UIImage *image in _arrNormalImages) {
            
            NETitleViewItem *titleView = [[NETitleViewItem alloc] initWithFrame:CGRectZero];
            titleView.tag = index;
            titleView.effectsType = _arrEffectsType[index].integerValue;
            
            titleView.normalImage = image;
            titleView.selectedImage = _arrSelectedImages[index];
            titleView.titleViewStyle = NETitleViewStyleOnlyImage;
            
            UITapGestureRecognizer *tapGes = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(titleViewOnClick:)];
            [titleView addGestureRecognizer:tapGes];
            
            CGFloat titleViewWidth = [titleView titleViewWidth];
            
            [self.titleWidths addObject:@(titleViewWidth)];
            [self.titleViews addObject:titleView];
            [self.scrollView addSubview:titleView];
            
            ++index;
        }
    }
}

- (void)layoutTitleViews {
    if (self.titleViews.count == 0) {
        return;
    }
    
    self.scrollView.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
    
    CGFloat titleX = 0.0;
    CGFloat titleY = 0.0;
    CGFloat titleW = 0.0;
    CGFloat titleH = self.frame.size.height;
    
    NSInteger index = 0;
    float lastLabelMaxX = TITLE_MARGIN;
    float addedMargin = 0.0;
    
//    float allTitlesWidth = TITLE_MARGIN;
//    for (int i = 0; i < self.titleWidths.count; ++i) {
//        allTitlesWidth = allTitlesWidth + [self.titleWidths[i] floatValue] + TITLE_MARGIN;
//    }
//    addedMargin = allTitlesWidth < self.scrollView.bounds.size.width ? (self.scrollView.bounds.size.width - allTitlesWidth) / self.titleWidths.count : 0;
    
    for (NETitleViewItem *titleView in self.titleViews) {
        titleW = [self.titleWidths[index] floatValue];
        
        if (index == 0) {
            titleX = lastLabelMaxX / 2;
        } else {
            titleX = lastLabelMaxX + addedMargin / 2;
        }
        
        lastLabelMaxX = titleW + titleX + TITLE_MARGIN;
        
        titleView.frame = CGRectMake(titleX, titleY, titleW, titleH);
        
        [titleView adjustSubviewFrame];
        
        ++index;
    }
    
    NETitleViewItem *currentTitleView = (NETitleViewItem *)self.titleViews[_currentIndex];
    if (currentTitleView) {
        currentTitleView.selected = YES;
        
        if (self.onClickBlock) {
            self.onClickBlock(currentTitleView, currentTitleView.tag, currentTitleView.effectsType);
        }
    }
    
    NETitleViewItem *laNETitleView = (NETitleViewItem *)self.titleViews.lastObject;
    if (laNETitleView) {
        self.scrollView.contentSize = CGSizeMake(CGRectGetMaxX(laNETitleView.frame) + 20, 0.0);
    }
}

- (void)titleViewOnClick:(UITapGestureRecognizer *)tapGes {
    NETitleViewItem *currentView = (NETitleViewItem *)tapGes.view;
    
    if (!currentView) {
        return;
    }
    
    _currentIndex = currentView.tag;
    
    [self adjustUIWhenTitleViewTaped:YES animated:YES];
}

- (void)adjustUIWhenTitleViewTaped:(BOOL)taped animated:(BOOL)animated {
    if (_currentIndex == _oldIndex && taped) {
        return;
    }
    
    NETitleViewItem *oldTitleView = (NETitleViewItem *)self.titleViews[_oldIndex];
    NETitleViewItem *currentTitleView = (NETitleViewItem *)self.titleViews[_currentIndex];
    
    CGFloat animatedTime = animated ? 0.30 : 0.0;
    
    __weak __typeof(self) weakSelf = self;
    [UIView animateWithDuration:animatedTime animations:^{
        oldTitleView.selected = NO;
        currentTitleView.selected = YES;
    } completion:^(BOOL finished) {
        [weakSelf adjustTitleOffsetToCurrentIndex:_currentIndex];
    }];
    
    _oldIndex = _currentIndex;
    
    if (self.onClickBlock) {
        self.onClickBlock(currentTitleView, _currentIndex, currentTitleView.effectsType);
    }
}


- (void)reloadTitlesWithNewTitles:(NSArray *)titles effectsType:(NSArray *)effectsType {
    [self.scrollView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    _currentIndex = 0;
    _oldIndex = 0;
    
    [self.titleViews removeAllObjects];
    [self.titleWidths removeAllObjects];
    self.arrTitles = nil;
    self.arrNormalImages = nil;
    self.arrSelectedImages = nil;
    self.arrTitles = [titles copy];
    
    if (self.arrTitles.count == 0) {
        return;
    }
    
    for (UIView *view in self.subviews) {
        [view removeFromSuperview];
    }
    [self setupTitleViews];
    [self layoutTitleViews];
    [self setSelectedIndex:0 animated:YES];
}

- (void)reloadTitlesWithNewNormalImages:(NSArray *)normalImages selectedImages:(NSArray *)selectedImages effectsType:(NSArray *)effectsType {
    [self.scrollView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    _currentIndex = 0;
    _oldIndex = 0;
    
    [self.titleViews removeAllObjects];
    [self.titleWidths removeAllObjects];
    
    self.arrTitles = nil;
    self.arrNormalImages = nil;
    self.arrSelectedImages = nil;
    
    self.arrNormalImages = [normalImages copy];
    self.arrSelectedImages = [selectedImages copy];
    
    if (self.arrNormalImages.count == 0) {
        return;
    }
    
    for (UIView *view in self.subviews) {
        [view removeFromSuperview];
    }
    
    [self setupTitleViews];
    [self layoutTitleViews];
    [self setSelectedIndex:0 animated:YES];
}

- (void)adjustTitleOffsetToCurrentIndex:(NSInteger)currentIndex {
    _oldIndex = currentIndex;
    
    int index = 0;
    
    for (NETitleViewItem *titleView in _titleViews) {
        if (index != currentIndex) {
            titleView.selected = NO;
        } else {
            titleView.selected = YES;
        }
        ++index;
    }
    
    if (self.scrollView.contentSize.width != self.scrollView.bounds.size.width + 20) {
        NETitleViewItem *currentTitleView = (NETitleViewItem *)_titleViews[currentIndex];
        
        CGFloat offsetX = currentTitleView.center.x - _currentWidth * 0.5;
        
        if (offsetX < 0) {
            offsetX = 0;
        }
        
        CGFloat maxOffsetX = self.scrollView.contentSize.width - _currentWidth;
        
        if (maxOffsetX < 0) {
            maxOffsetX = 0;
        }
        
        if (offsetX > maxOffsetX) {
            offsetX = maxOffsetX;
        }
        
        [self.scrollView setContentOffset:CGPointMake(offsetX, 0) animated:YES];
    }
}

- (void)setSelectedIndex:(NSInteger)index animated:(BOOL)animated {
    _currentIndex = index;
    
    [self adjustUIWhenTitleViewTaped:NO animated:YES];
    
}

- (UIScrollView *)scrollView {
    if(!_scrollView) {
        _scrollView = [[UIScrollView alloc] init];
        _scrollView.alwaysBounceHorizontal = YES;
        _scrollView.showsHorizontalScrollIndicator = NO;
        _scrollView.scrollsToTop = NO;
        _scrollView.pagingEnabled = NO;
        _scrollView.delegate = self;
    }
    
    return _scrollView;
}

- (NSMutableArray *)titleViews {
    if (!_titleViews) {
        _titleViews = [NSMutableArray array];
    }
    
    return _titleViews;
}

- (NSMutableArray *)titleWidths {
    if (!_titleWidths) {
        _titleWidths = [NSMutableArray array];
    }
    
    return _titleWidths;
}

@end

