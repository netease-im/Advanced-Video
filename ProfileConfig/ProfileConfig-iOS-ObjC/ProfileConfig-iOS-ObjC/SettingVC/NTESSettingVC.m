//
//  NTESSettingVC.m
//  DeviceManager-iOS-ObjC
//
//  Created by Think on 2020/9/15.
//  Copyright © 2020 Wenchao Ding. All rights reserved.
//

#import "NTESSettingVC.h"
#import "UIView+NTES.h"
#import "NTESGlobalMacro.h"
#import "NTESPickView.h"
#import <NERtcSDK/NERtcEngineEnum.h>
#import "NTESPickViewSettingModel.h"
#import <NERtcSDK/NERtcSDK.h>

@interface NTESSettingVC () <NTESPickViewDelegate>

@property (nonatomic, strong)   UILabel         *videoQualityTit;
@property (nonatomic, strong)   UIButton        *videoQualityVal;
@property (nonatomic, strong)   UILabel         *audioQualityTit;
@property (nonatomic, strong)   UIButton        *audioQualityVal;
@property (nonatomic, strong)   UILabel         *audioModeTit;
@property (nonatomic, strong)   UIButton        *audioModeVal;

@property (nonatomic, assign)   BOOL            changeConfig;

@end

@implementation NTESSettingVC

- (void)viewDidLoad {
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
        NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
        
        NERtcVideoEncodeConfiguration *config = [[NERtcVideoEncodeConfiguration alloc] init];
        config.maxProfile = _videoConfig.value;
        [coreEngine setLocalVideoConfig:config];
        [coreEngine setAudioProfile:_audioProfile.value scenario:_audioScenario.value];
        
        // 重启本地音视频能力使配置生效
        [coreEngine enableLocalAudio:NO];
        [coreEngine enableLocalVideo:NO];
        
        [coreEngine enableLocalAudio:YES];
        [coreEngine enableLocalVideo:YES];
    }
    
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)clickAction:(UIButton *)sender
{
    NSArray *configs = nil;
    if (sender == self.videoQualityVal) {
        NTESPickViewSettingModel *item0 = [[NTESPickViewSettingModel alloc] initWithTitle:@"320x180/240 @15fps" value:kNERtcVideoProfileLow type:NTESPickViewSettingVideo];
        NTESPickViewSettingModel *item1 = [[NTESPickViewSettingModel alloc] initWithTitle:@"640x360/480 @30fps" value:kNERtcVideoProfileStandard type:NTESPickViewSettingVideo];
        NTESPickViewSettingModel *item2 = [[NTESPickViewSettingModel alloc] initWithTitle:@"1280x720 @30 fps" value:kNERtcVideoProfileHD720P type:NTESPickViewSettingVideo];
        NTESPickViewSettingModel *item3 = [[NTESPickViewSettingModel alloc] initWithTitle:@"1920x1080 @30fps" value:kNERtcVideoProfileHD1080P type:NTESPickViewSettingVideo];
        configs = @[item0, item1, item2, item3];
    }
    
    if (sender == self.audioQualityVal) {
        NTESPickViewSettingModel *item0 = [[NTESPickViewSettingModel alloc] initWithTitle:@"普通 16000Hz,20Kbps" value:kNERtcAudioProfileStandard type:NTESPickViewSettingAudio];
        NTESPickViewSettingModel *item1 = [[NTESPickViewSettingModel alloc] initWithTitle:@"中等 48000Hz,32Kbps" value:kNERtcAudioProfileMiddleQuality type:NTESPickViewSettingAudio];
        NTESPickViewSettingModel *item2 = [[NTESPickViewSettingModel alloc] initWithTitle:@"中等立体声 48000Hz*2,64Kbps" value:kNERtcAudioProfileMiddleQualityStereo type:NTESPickViewSettingAudio];
        NTESPickViewSettingModel *item3 = [[NTESPickViewSettingModel alloc] initWithTitle:@"高质量 48000Hz,64Kbps" value:kNERtcAudioProfileHighQuality type:NTESPickViewSettingAudio];
        NTESPickViewSettingModel *item4 = [[NTESPickViewSettingModel alloc] initWithTitle:@"高质量立体声 48000Hz*2,128Kbps" value:kNERtcAudioProfileHighQualityStereo type:NTESPickViewSettingAudio];
        configs = @[item0, item1, item2, item3, item4];
    }
    
    if (sender == self.audioModeVal) {
        NTESPickViewSettingModel *item0 = [[NTESPickViewSettingModel alloc] initWithTitle:@"语音场景" value:kNERtcAudioScenarioSpeech type:NTESPickViewSettingAudioMode];
        NTESPickViewSettingModel *item1 = [[NTESPickViewSettingModel alloc] initWithTitle:@"音乐场景" value:kNERtcAudioScenarioMusic type:NTESPickViewSettingAudioMode];
        configs = @[item0, item1];
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
            }
        }
            break;
            
        default:
        {
            target = self.videoQualityVal;
            if (_videoConfig.value != model.value) {
                _videoConfig = model;
                _changeConfig = YES;
            }
        }
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

@end
