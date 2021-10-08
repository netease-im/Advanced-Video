//
//  NTESSettingVC.h
//  DeviceManager-iOS-ObjC
//
//  Created by Think on 2020/9/15.
//  Copyright © 2020 Wenchao Ding. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <NERtcSDK/NERtcSDK.h>

@protocol NTESSettingVCDelegate <NSObject>

- (void)didChangeSettingsWithVideoProfile:(NERtcVideoProfileType)videoProfile
                             audioProfile:(NERtcAudioProfileType)audioProfile
                            audioScenario:(NERtcAudioScenarioType)audioScenario;

@end

@interface NTESSettingVC : UIViewController

@property (nonatomic, weak) id<NTESSettingVCDelegate> delegate;

- (void)setCurrentVideoProfile:(NERtcVideoProfileType)videoProfile;
- (void)setCurrentAudioProfile:(NERtcAudioProfileType)audioProfile;
- (void)setCurrentAudioScenario:(NERtcAudioScenarioType)audioScenario;

@end
