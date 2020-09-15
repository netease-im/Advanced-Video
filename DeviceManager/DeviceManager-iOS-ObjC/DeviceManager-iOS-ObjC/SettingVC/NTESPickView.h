//
//  NTESPickView.h
//  DeviceManager-iOS-ObjC
//
//  Created by Think on 2020/9/15.
//  Copyright © 2020 Wenchao Ding. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class NTESPickViewSettingModel;

@protocol NTESPickViewDelegate <NSObject>

- (void)choseSettingModel:(NTESPickViewSettingModel *)model;

@end

@interface NTESPickView : UIView

+ (void)showWithModels:(NSArray <NTESPickViewSettingModel *> *)models
              delegate:(UIViewController <NTESPickViewDelegate> *)delegate;

@end

NS_ASSUME_NONNULL_END
