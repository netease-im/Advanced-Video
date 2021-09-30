//
//  NTESSettingVC.m
//  DeviceManager-iOS-ObjC
//
//  Created by Think on 2020/9/15.
//  Copyright © 2020 Wenchao Ding. All rights reserved.
//

#import "NTESSettingVC.h"
#import "UIView+NTES.h"
#import "NSDictionary+NTES.h"
#import "NTESGlobalMacro.h"
#import "NTESPickView.h"
#import "NTESPickViewSettingModel.h"

@interface NTESSettingVC () <NTESPickViewDelegate>

@property (nonatomic, strong)   UILabel         *videoQualityTit;
@property (nonatomic, strong)   UIButton        *videoQualityVal;
@property (nonatomic, strong)   UILabel         *audioQualityTit;
@property (nonatomic, strong)   UIButton        *audioQualityVal;
@property (nonatomic, strong)   UILabel         *audioModeTit;
@property (nonatomic, strong)   UIButton        *audioModeVal;

@property (nonatomic, assign)   BOOL            changeConfig;

@property (nonatomic, strong) NTESPickViewSettingModel *videoConfig;
@property (nonatomic, strong) NTESPickViewSettingModel *audioProfile;
@property (nonatomic, strong) NTESPickViewSettingModel *audioScenario;

@property (nonatomic, strong) NSDictionary *videoProfileMap;
@property (nonatomic, strong) NSDictionary *audioProfileMap;
@property (nonatomic, strong) NSDictionary *audioScenarioMap;

@end

@implementation NTESSettingVC

- (void)setCurrentVideoProfile:(NERtcVideoProfileType)videoProfile
{
    _videoConfig = [[NTESPickViewSettingModel alloc] initWithTitle:[self.videoProfileMap objectForKey:@(videoProfile)] value:videoProfile type:NTESPickViewSettingVideo];
}

- (void)setCurrentAudioProfile:(NERtcAudioProfileType)audioProfile
{
    _audioProfile = [[NTESPickViewSettingModel alloc] initWithTitle:[self.audioProfileMap objectForKey:@(audioProfile)] value:audioProfile type:NTESPickViewSettingAudio];
}

- (void)setCurrentAudioScenario:(NERtcAudioScenarioType)audioScenario
{
    _audioScenario = [[NTESPickViewSettingModel alloc] initWithTitle:[self.audioScenarioMap objectForKey:@(audioScenario)] value:audioScenario type:NTESPickViewSettingAudioMode];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self setupViews];
}

- (void)addCustomBackBtn
{
    UIBarButtonItem* item = [[UIBarButtonItem alloc] initWithTitle:@"返回" style:UIBarButtonItemStylePlain target:self action:@selector(backAction)];
    self.navigationItem.backBarButtonItem = item;
    self.navigationItem.leftBarButtonItem = self.navigationItem.backBarButtonItem;
    self.navigationController.interactivePopGestureRecognizer.delegate = nil;
}

- (void)setupViews
{
    [self.navigationController.navigationBar setTranslucent:NO];
    self.view.backgroundColor = [UIColor whiteColor];
    [self addCustomBackBtn];
    
    [self.view addSubview:self.videoQualityTit];
    [self.view addSubview:self.videoQualityVal];
    [self.view addSubview:self.audioQualityTit];
    [self.view addSubview:self.audioQualityVal];
    [self.view addSubview:self.audioModeTit];
    [self.view addSubview:self.audioModeVal];
    
    self.videoQualityTit.frame = CGRectMake(20, 30, 70, 40);
    self.videoQualityVal.frame = CGRectMake(self.videoQualityTit.right + 15, self.videoQualityTit.top, UIScreenWidth - 80 - 55, self.videoQualityTit.height);
    self.audioQualityTit.frame = CGRectMake(self.videoQualityTit.left, self.videoQualityTit.bottom + 5, self.videoQualityTit.width, self.videoQualityTit.height);
    self.audioQualityVal.frame = CGRectMake(self.videoQualityTit.right + 15, self.audioQualityTit.top, UIScreenWidth - 80 - 55, self.videoQualityTit.height);
    self.audioModeTit.frame = CGRectMake(self.videoQualityTit.left, self.audioQualityTit.bottom + 5, self.videoQualityTit.width, self.videoQualityTit.height);
    self.audioModeVal.frame = CGRectMake(self.videoQualityTit.right + 15, self.audioModeTit.top, UIScreenWidth - 80 - 55, self.videoQualityTit.height);
}

- (void)backAction
{
    if (_changeConfig) {
        if (_delegate && [_delegate respondsToSelector:@selector(didChangeSettingsWithVideoProfile:audioProfile:audioScenario:)]) {
            [_delegate didChangeSettingsWithVideoProfile:_videoConfig.value
                                            audioProfile:_audioProfile.value
                                           audioScenario:_audioScenario.value];
        }
    }
    
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)clickAction:(UIButton *)sender
{
    NSMutableArray *configs = [NSMutableArray array];
    
    if (sender == self.videoQualityVal) {
        NSArray *ascendingKeys = [self.videoProfileMap getAscendingKeys];
        for (NSNumber *key in ascendingKeys) {
            NERtcVideoProfileType videoProfile = [key intValue];
            NTESPickViewSettingModel *item = [[NTESPickViewSettingModel alloc] initWithTitle:[self.videoProfileMap objectForKey:key] value:videoProfile type:NTESPickViewSettingVideo];
            [configs addObject:item];
        }
    }
    
    if (sender == self.audioQualityVal) {
        NSArray *ascendingKeys = [self.audioProfileMap getAscendingKeys];
        for (NSNumber *key in ascendingKeys) {
            NERtcAudioProfileType audioProfile = [key intValue];
            NTESPickViewSettingModel *item = [[NTESPickViewSettingModel alloc] initWithTitle:[self.audioProfileMap objectForKey:key] value:audioProfile type:NTESPickViewSettingAudio];
            [configs addObject:item];
        }
    }

    if (sender == self.audioModeVal) {
        NSArray *ascendingKeys = [self.audioScenarioMap getAscendingKeys];
        for (NSNumber *key in ascendingKeys) {
            NERtcAudioScenarioType audioScenario = [key intValue];
            NTESPickViewSettingModel *item = [[NTESPickViewSettingModel alloc] initWithTitle:[self.audioScenarioMap objectForKey:key] value:audioScenario type:NTESPickViewSettingAudioMode];
            [configs addObject:item];
        }
    }
    
    if ([configs count] > 0) {
        [NTESPickView showWithModels:configs delegate:self];
    }
}

#pragma mark - NTESPickViewDelegate

- (void)choseSettingModel:(NTESPickViewSettingModel *)model
{
    if (model == nil) {
        return;
    }
    UIButton *target = nil;
    switch (model.type) {
        case NTESPickViewSettingVideo:
        {
            target = self.videoQualityVal;
            if (_videoConfig.value != model.value) {
                _videoConfig = model;
                _changeConfig = YES;
            }
        }
            break;
        case NTESPickViewSettingAudio:
        {
            target = self.audioQualityVal;
            if (_audioProfile.value != model.value) {
                _audioProfile = model;
                _changeConfig = YES;
            }
        }
            break;
        case NTESPickViewSettingAudioMode:
        {
            target = self.audioModeVal;
            if (_audioScenario.value != model.value) {
                _audioScenario = model;
                _changeConfig = YES;
            }
        }
            break;
            
        default:
            break;
    }
    if (target) {
        [target setTitle:model.title forState:UIControlStateNormal];
    }
}

#pragma mark - lazy load

- (UILabel *)videoQualityTit
{
    if (!_videoQualityTit) {
        _videoQualityTit = [UILabel new];
        _videoQualityTit.text = @"视频质量";
        _videoQualityTit.textColor = UIColorFromRGB(0x444444);
        _videoQualityTit.font = [UIFont boldSystemFontOfSize:16];
    }
    return _videoQualityTit;
}

- (UIButton *)videoQualityVal
{
    if (!_videoQualityVal) {
        _videoQualityVal = [[UIButton alloc] init];
        _videoQualityVal.titleLabel.font = [UIFont systemFontOfSize:14];
        NSString *title = (_videoConfig != nil) ? _videoConfig.title : @"点击选择视频质量";
        [_videoQualityVal setTitle:title forState:UIControlStateNormal];
        [_videoQualityVal setTitleColor:UIColorFromRGB(0x999999) forState:UIControlStateNormal];
        _videoQualityVal.backgroundColor = UIColorFromRGB(0xf4f4f4);
        _videoQualityVal.layer.borderColor = UIColorFromRGB(0xcccccc).CGColor;
        _videoQualityVal.layer.borderWidth = 0.5;
        _videoQualityVal.layer.cornerRadius = 4.0;
        _videoQualityVal.layer.masksToBounds = YES;
        [_videoQualityVal addTarget:self action:@selector(clickAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _videoQualityVal;
}

- (UILabel *)audioQualityTit
{
    if (!_audioQualityTit) {
        _audioQualityTit = [UILabel new];
        _audioQualityTit.text = @"音频质量";
        _audioQualityTit.textColor = UIColorFromRGB(0x444444);
        _audioQualityTit.font = [UIFont boldSystemFontOfSize:16];
    }
    return _audioQualityTit;
}

- (UIButton *)audioQualityVal
{
    if (!_audioQualityVal) {
        _audioQualityVal = [[UIButton alloc] init];
        _audioQualityVal.titleLabel.font = [UIFont systemFontOfSize:14];
        NSString *title = (_audioProfile != nil) ? _audioProfile.title : @"点击选择音频质量";
        [_audioQualityVal setTitle:title forState:UIControlStateNormal];
        [_audioQualityVal setTitleColor:UIColorFromRGB(0x999999) forState:UIControlStateNormal];
        _audioQualityVal.backgroundColor = UIColorFromRGB(0xf4f4f4);
        _audioQualityVal.layer.borderColor = UIColorFromRGB(0xcccccc).CGColor;
        _audioQualityVal.layer.borderWidth = 0.5;
        _audioQualityVal.layer.cornerRadius = 4.0;
        _audioQualityVal.layer.masksToBounds = YES;
        [_audioQualityVal addTarget:self action:@selector(clickAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _audioQualityVal;
}

- (UILabel *)audioModeTit
{
    if (!_audioModeTit) {
        _audioModeTit = [UILabel new];
        _audioModeTit.text = @"音频场景";
        _audioModeTit.textColor = UIColorFromRGB(0x444444);
        _audioModeTit.font = [UIFont boldSystemFontOfSize:16];
    }
    return _audioModeTit;
}

- (UIButton *)audioModeVal
{
    if (!_audioModeVal) {
        _audioModeVal = [[UIButton alloc] init];
        _audioModeVal.titleLabel.font = [UIFont systemFontOfSize:14];
        NSString *title = (_audioScenario != nil) ? _audioScenario.title : @"点击选择音频模式";
        [_audioModeVal setTitle:title forState:UIControlStateNormal];
        [_audioModeVal setTitleColor:UIColorFromRGB(0x999999) forState:UIControlStateNormal];
        _audioModeVal.backgroundColor = UIColorFromRGB(0xf4f4f4);
        _audioModeVal.layer.borderColor = UIColorFromRGB(0xcccccc).CGColor;
        _audioModeVal.layer.borderWidth = 0.5;
        _audioModeVal.layer.cornerRadius = 4.0;
        _audioModeVal.layer.masksToBounds = YES;
        [_audioModeVal addTarget:self action:@selector(clickAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _audioModeVal;
}

- (NSDictionary *)videoProfileMap
{
    if (!_videoProfileMap) {
        _videoProfileMap = @{@(kNERtcVideoProfileLowest) : @"160x90/120 @15fps",
                             @(kNERtcVideoProfileLow) : @"320x180/240 @15fps",
                             @(kNERtcVideoProfileStandard) : @"640x360/480 @30fps",
                             @(kNERtcVideoProfileHD720P) : @"1280x720 @30 fps",
                             @(kNERtcVideoProfileHD1080P) : @"1920x1080 @30fps"};
    }
    
    return _videoProfileMap;
}

- (NSDictionary *)audioProfileMap
{
    if (!_audioProfileMap) {
        _audioProfileMap = @{@(kNERtcAudioProfileStandard) : @"标准 16000Hz,20Kbps",
                             @(kNERtcAudioProfileStandardExtend) : @"标准扩展 16000Hz,32Kbps",
                             @(kNERtcAudioProfileMiddleQuality) : @"中等 48000Hz,32Kbps",
                             @(kNERtcAudioProfileMiddleQualityStereo) : @"中等立体声 48000Hz*2,64Kbps",
                             @(kNERtcAudioProfileHighQuality) : @"高质量 48000Hz,64Kbps",
                             @(kNERtcAudioProfileHighQualityStereo) : @"高质量立体声 48000Hz*2,128Kbps"};
    }
    
    return _audioProfileMap;
}

- (NSDictionary *)audioScenarioMap
{
    if (!_audioScenarioMap) {
        _audioScenarioMap = @{@(kNERtcAudioScenarioSpeech) : @"语音场景",
                              @(kNERtcAudioScenarioMusic) : @"音乐场景",
                              @(kNERtcAudioScenarioChatRoom) : @"语音聊天室场景"};
    }
    
    return _audioScenarioMap;
}

@end
