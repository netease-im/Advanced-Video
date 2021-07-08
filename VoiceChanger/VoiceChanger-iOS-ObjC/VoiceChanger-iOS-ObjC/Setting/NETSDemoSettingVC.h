//
//  NETSDemoSettingVC.h
//  VoiceChanger-iOS-ObjC
//
//  Created by Think on 2020/11/5.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NETSDemoSettingVM.h"

NS_ASSUME_NONNULL_BEGIN

///
/// 音效配置页面
///
@interface NETSDemoSettingVC : UIViewController

- (instancetype)initWithType:(NETSDemoSettingType)type;

@end

NS_ASSUME_NONNULL_END
