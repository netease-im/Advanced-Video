//
//  EffectsStickerAudioPlayer.m
//  SenseMeEffects
//
//  Created by sunjian on 2021/6/24.
//  Copyright © 2021 SoftSugar. All rights reserved.
//

#import "EffectsAudioPlayerManager.h"
#import "EffectsAudioPlayer.h"

#pragma mark - EffectsMessageManager
@protocol EffectsMessageDelegate <NSObject>
- (void)loadSound:(NSData *)soundData name:(NSString *)strName;
- (void)playSound:(NSString *)strName loop:(int)iLoop;
- (void)pauseSound:(NSString *)strName;
- (void)resumeSound:(NSString *)strName;
- (void)stopSound:(NSString *)strName;
- (void)unloadSound:(NSString *)strName;
- (void)packageEvent:(NSString *)packageName
           packageID:(int)packageID
               event:(int)event
      displayedFrame:(int)displayedFrame;
@end
@interface EffectsMessageManager : NSObject
@property (nonatomic,  weak) id<EffectsMessageDelegate> delegate;
@end
@implementation EffectsMessageManager
@end

EffectsMessageManager *messageManager = nil;

@interface EffectsAudioPlayerManager ()<EffectsMessageDelegate, EffectsAudioPlayerDelegate>
@property (nonatomic, strong) EffectsAudioPlayer *audioPlayer;
@end

@implementation EffectsAudioPlayerManager

- (void)dealloc{
    if (messageManager) {
        messageManager = nil;
    }
}

- (instancetype)init{
    self = [super init];
    messageManager = [[EffectsMessageManager alloc] init];
    messageManager.delegate = self;
    self.audioPlayer = [[EffectsAudioPlayer alloc] init];
    self.audioPlayer.delegate = self;
    return self;
}

#pragma mark - EffectsMessageManagerDelage

- (void)loadSound:(NSData *)soundData name:(NSString *)strName {
    if (!strName) {
        return;
    }
    if ([self.audioPlayer loadSound:soundData name:strName]) {
        NSLog(@"STEffectsAudioPlayer load %@ successfully", strName);
    }
}

- (void)playSound:(NSString *)strName loop:(int)iLoop {
    
    if ([self.audioPlayer playSound:strName loop:iLoop]) {
        NSLog(@"STEffectsAudioPlayer play %@ successfully", strName);
    }
}

- (void)pauseSound:(NSString *)strName {
    [self.audioPlayer pauseSound:strName];
}

- (void)resumeSound:(NSString *)strName {
    [self.audioPlayer resumeSound:strName];
}

- (void)stopSound:(NSString *)strName {
    
    [self.audioPlayer stopSound:strName];
}

- (void)unloadSound:(NSString *)strName {
    [self.audioPlayer unloadSound:strName];
}

- (void)packageEvent:(NSString *)packageName
           packageID:(int)packageID
               event:(int)event
      displayedFrame:(int)displayedFrame
{
//    DLog(@"packageName %@, packageID %d, event %d, displayedFrame %d", packageName, packageID, event, displayedFrame);
}

#pragma mark - STEffectsAudioPlayerDelegate
- (void)audioPlayerDidFinishPlaying:(EffectsAudioPlayer *)player
                       successfully:(BOOL)flag
                               name:(NSString *)strName {
    if (self.delegate && [self.delegate respondsToSelector:@selector(audioPlayerDidFinishPlayingWithAudioName:)]) {
        [self.delegate audioPlayerDidFinishPlayingWithAudioName:strName];
    }
}

@end

st_result_t _audio_modul_state_change_callback(st_handle_t handle,
                                         st_effect_module_info_t* p_module_info){
    switch (p_module_info->state) {
        case EFFECT_MODULE_LOADED:
        {
            st_effect_buffer_t *audioBuffer = (st_effect_buffer_t*)(p_module_info->reserved);
            if ([messageManager.delegate respondsToSelector:@selector(loadSound:name:)]) {
                NSData *audioData = [NSData dataWithBytes:audioBuffer->data_ptr length:audioBuffer->data_len];
                NSString *audioName = [NSString stringWithUTF8String:p_module_info->name];
                [messageManager.delegate loadSound:audioData name:audioName];
            }
        }
            break;
        case EFFECT_MODULE_PAUSED_FIRST_FRAME:
            
            break;
        case EFFECT_MODULE_PLAYING:
        {
            if ([messageManager.delegate respondsToSelector:@selector(playSound:loop:)]) {
                NSString *strName = [NSString stringWithUTF8String:p_module_info->name];
                [messageManager.delegate playSound:strName loop:(int)p_module_info->reserved];
            }
        }
            break;
        case EFFECT_MODULE_PAUSED:
        {
            if ([messageManager.delegate respondsToSelector:@selector(pauseSound:)]) {
                NSString *strName = [NSString stringWithUTF8String:p_module_info->name];
                [messageManager.delegate pauseSound:strName];
            }
        }
            break;
        case EFFECT_MODULE_PAUSED_LAST_FRAME:
        {
            if ([messageManager.delegate respondsToSelector:@selector(pauseSound:)]) {
                NSString *strName = [NSString stringWithUTF8String:p_module_info->name];
                [messageManager.delegate pauseSound:strName];
            }
        }
            break;
        case EFFECT_MODULE_INVISIBLE:
            break;
        case EFFECT_MODULE_RESUMED:
        {
            if ([messageManager.delegate respondsToSelector:@selector(resumeSound:)]) {
                NSString *strName = [NSString stringWithUTF8String:p_module_info->name];
                [messageManager.delegate resumeSound:strName];
            }
        }
            break;
        case EFFECT_MODULE_UNLOADED:
        {
            if ([messageManager.delegate respondsToSelector:@selector(unloadSound:)]) {
                NSString *strName = [NSString stringWithUTF8String:p_module_info->name];
                [messageManager.delegate unloadSound:strName];
            }
        }
            break;
        default:
            break;
    }
    return ST_OK;
}

st_result_t _package_state_change_callback(st_handle_t handle, st_effect_package_info_t* p_package_info){
    if ([messageManager.delegate respondsToSelector:@selector(packageEvent:packageID:event:displayedFrame:)]) {
        NSString *packageName = [NSString stringWithUTF8String:p_package_info->name];
        [messageManager.delegate packageEvent:packageName
                                    packageID:p_package_info->package_id
                                        event:p_package_info->state
                               displayedFrame:p_package_info->displayed_frames];
    }
    return ST_OK;
}
