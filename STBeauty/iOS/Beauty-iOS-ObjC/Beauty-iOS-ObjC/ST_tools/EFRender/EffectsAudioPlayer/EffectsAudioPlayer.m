//
//  STEffectsAudioPlayer.m
//
//  Created by sluin on 2017/8/16.
//  Copyright © 2017年 SoftSugar. All rights reserved.
//

#import "EffectsAudioPlayer.h"
#import <AVFoundation/AVFoundation.h>

@interface EffectsAudioPlayer () <AVAudioPlayerDelegate>

@property (nonatomic, strong) NSMutableDictionary *players;

@end



@implementation EffectsAudioPlayer

- (void)dealloc {
    [_players removeAllObjects];
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _players = [NSMutableDictionary dictionary];
    }
    return self;
}

- (BOOL)loadSound:(NSData *)soundData name:(NSString *)strName {
    
    NSError *error = nil;
    
    AVAudioPlayer *audioPlayer = [[AVAudioPlayer alloc] initWithData:soundData fileTypeHint:AVFileTypeMPEGLayer3 error:&error];
    
    if (error || !audioPlayer) {
        
        NSLog(@"STEffectsAudioPlayer loadSound failed : %@" , [error localizedDescription]);
        
        return NO;
    }
    
    [_players setObject:audioPlayer forKey:strName];
    [_players setObject:strName forKey:@(audioPlayer.hash)];
    
    audioPlayer.delegate = self;
    
    BOOL isReadyToPlay = [audioPlayer prepareToPlay];
    
    if (!isReadyToPlay) {
        
        NSLog(@"STEffectsAudioPlayer is not ready to play.");
        
        return NO;
    }
    
    return isReadyToPlay;
}

- (BOOL)playSound:(NSString *)strName loop:(int)iLoop {
    int iNumberOfLoop = iLoop - 1;
    
    AVAudioPlayer *audioPlayer = (AVAudioPlayer *)[_players objectForKey:strName];
    
    [audioPlayer setNumberOfLoops:iNumberOfLoop];
    [audioPlayer setCurrentTime:0];
    
    return [audioPlayer play];
}

- (void)pauseSound:(NSString *)strName {
    [(AVAudioPlayer *)[_players objectForKey:strName] pause];
}

- (void)resumeSound:(NSString *)strName {
    [(AVAudioPlayer *)[_players objectForKey:strName] play];
}

- (void)stopSound:(NSString *)strName {
    [(AVAudioPlayer *)[_players objectForKey:strName] stop];
}

- (void)unloadSound:(NSString *)strName {
    AVAudioPlayer *audioPlayer = (AVAudioPlayer *)[_players objectForKey:strName];
    if (audioPlayer) {
        [audioPlayer stop];
        [_players removeObjectForKey:strName];
        [_players removeObjectForKey:@(audioPlayer.hash)];
        NSLog(@"remove %@ player...", strName);
    }
}

- (void)clearAll {
    [_players removeAllObjects];
}

#pragma - mark -
#pragma - mark AVAudioPlayerDelegate

- (void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag {
    
    NSString *strName = (NSString *)[_players objectForKey:@(player.hash)];
    if (!strName) {
        return;
    }
    
    if (self.delegate
        &&
        [self.delegate respondsToSelector:@selector(audioPlayerDidFinishPlaying:successfully:name:)])
    {
        [self.delegate audioPlayerDidFinishPlaying:self successfully:flag name:strName];
    }
}


@end
