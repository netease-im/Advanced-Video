//
//  NTESSettingVC.h
//  DeviceManager-iOS-ObjC
//
//  Created by Think on 2020/9/15.
//  Copyright © 2020 Wenchao Ding. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class NTESPickViewSettingModel;

@interface NTESSettingVC : UIViewController

@property (nonatomic, strong)   NTESPickViewSettingModel    *videoConfig;
@property (nonatomic, strong)   NTESPickViewSettingModel    *audioProfile;
@property (nonatomic, strong)   NTESPickViewSettingModel    *audioScenario;

@end

NS_ASSUME_NONNULL_END
