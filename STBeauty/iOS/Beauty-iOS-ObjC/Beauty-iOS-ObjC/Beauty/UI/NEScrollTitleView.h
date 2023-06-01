//
//  NEScrollTitleView.h
//  NeCamera
//
//  Created by Ycq on 05/10/2019.
//  Copyright © 2019 NetEase Audio Lab. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NEBeautyMacro.h"

typedef NS_ENUM(NSInteger, NETitleViewStyle) {
    NETitleViewStyleOnlyImage = 0,
    NETitleViewStyleOnlyCharacter
};

@class NETitleViewItem;

typedef void (^NETitleOnClickBlock)(NETitleViewItem *titleView, NSInteger index, NEBeautyEffectType type);

@interface NEScrollTitleView : UIView

@property (nonatomic, readwrite, strong) NSArray<NSString *> *arrTitles;
@property (nonatomic, readwrite, strong) NSArray<UIImage *> *arrNormalImages;
@property (nonatomic, readwrite, strong) NSArray<UIImage *> *arrSelectedImages;
@property (nonatomic, readwrite, strong) NSArray<NSNumber *> *arrEffectsType;

- (instancetype)initWithFrame:(CGRect)frame
                       titles:(NSArray *)titles
                  effectsType:(NSArray *)effectsType
                 titleOnClick:(NETitleOnClickBlock)onClickBlock;

- (instancetype)initWithFrame:(CGRect)frame
                 normalImages:(NSArray *)normalImages
               selectedImages:(NSArray *)selectedImages
                  effectsType:(NSArray *)effectsType
                 titleOnClick:(NETitleOnClickBlock)onClickBlock;

- (void)adjustTitleOffsetToCurrentIndex:(NSInteger)currentIndex;
- (void)setSelectedIndex:(NSInteger)index animated:(BOOL)animated;

- (void)reloadTitlesWithNewTitles:(NSArray *)titles effectsType:(NSArray *)effectsType;
- (void)reloadTitlesWithNewNormalImages:(NSArray *)normalImages selectedImages:(NSArray *)selectedImages effectsType:(NSArray *)effectsType;

@end
