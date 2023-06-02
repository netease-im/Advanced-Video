//
//  EffectsStickerAudioPlayer.h
//  SenseMeEffects
//
//  Created by sunjian on 2021/6/24.
//  Copyright © 2021 SoftSugar. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "st_mobile_effect.h"

st_result_t _audio_modul_state_change_callback(st_handle_t handle,
                                         st_effect_module_info_t* p_module_info);
st_result_t _package_state_change_callback(st_handle_t handle,
                                           st_effect_package_info_t* p_package_info);

NS_ASSUME_NONNULL_BEGIN

@protocol EffectsAudioPlayerManagerDelegate <NSObject>
- (void)audioPlayerDidFinishPlayingWithAudioName:(NSString *)audioName;
@end

#pragma mark - EffectsAudioPlayerManager
@interface EffectsAudioPlayerManager : NSObject
@property (nonatomic, weak) id<EffectsAudioPlayerManagerDelegate> delegate;
@end

NS_ASSUME_NONNULL_END
