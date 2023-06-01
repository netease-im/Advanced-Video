//
//  NETSDemoP2PViewController.m
//  NERtcP2pSample
//
//  Created by NetEase on 2020/08/01.
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//  

#import "NETSDemoP2PViewController.h"
#import "NTESDemoUserModel.h"
#import "AppKey.h"

#import "NEBeautyManager.h"

#import <NERtcSDK/NERtcSDK.h>
#import <Masonry/Masonry.h>

@interface NETSDemoP2PViewController ()<NERtcEngineDelegateEx>

//渲染视图控件，SDK需要通过设置渲染view来建立canvas
@property (weak, nonatomic) IBOutlet UIView *localRender;  //本地渲染视图
@property (weak, nonatomic) IBOutlet UIView *remoteRender; //远端渲染视图
@property (weak, nonatomic) IBOutlet UILabel *remoteStatLab;

@property (nonatomic, copy) NSString *roomId;  //房间ID
@property (nonatomic, assign) uint64_t userId; //本人uid
@property (nonatomic, assign) uint64_t remoteUserId; // 远端用户uid

//Demo的 canvas 模型，包括uid 和 container, 用来建立sdk canvas
@property (nonatomic, strong) NTESDemoUserModel *localCanvas;  //本地
@property (nonatomic, strong) NTESDemoUserModel *remoteCanvas; //远端

@property (nonatomic, strong) UIView *functionMenu;
@property (nonatomic, strong) UIButton *muteAudioButton;
@property (nonatomic, strong) UIButton *muteVideoButton;
@property (nonatomic, strong) UIButton *hangupButton;

@end

@implementation NETSDemoP2PViewController

#pragma mark - Public

+ (instancetype)instanceWithRoomId:(NSString *)roomId
                            userId:(uint64_t)userId {
    UIStoryboard *storyBoard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
    NETSDemoP2PViewController *ret = [storyBoard instantiateViewControllerWithIdentifier:@"NETSDemoP2PViewController"];
    ret.roomId = roomId;
    ret.userId = userId;
    
    //初始化SDK
    [ret setupRTCEngine];
    
    return ret;
}

#pragma mark - Life Cycle

- (void)dealloc {
    NSLog(@"%s", __FUNCTION__);
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self setupLayout];
    
    //初始化美颜模块
    [[NEBeautyManager sharedManager] initNEBeauty];
    //开启美颜功能
    [[NEBeautyManager sharedManager] enableNEBeauty:YES];
    
    //直接加入channel
    [self joinChannelWithRoomId:_roomId userId:_userId];
}

#pragma mark - Functions

//初始化SDK
- (void)setupRTCEngine {
    //默认情况下日志会存储在App沙盒的Documents目录下
    NERtcLogSetting *logSetting = [[NERtcLogSetting alloc] init];
#if DEBUG
    logSetting.logLevel = kNERtcLogLevelInfo;
#else
    logSetting.logLevel = kNERtcLogLevelWarning;
#endif
    
    NERtcEngineContext *context = [[NERtcEngineContext alloc] init];
    context.engineDelegate = self;
    context.appKey = kAppKey;
    context.logSetting = logSetting;
    [[NERtcEngine sharedEngine] setupEngineWithContext:context];
}

//释放SDK资源
- (void)destroyRTCEngineWithCompletion:(void(^)(void))completion {
    [[NEBeautyManager sharedManager] destroyNEBeauty];
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0), ^{
        [NERtcEngine destroyEngine];
        
        if (completion) {
            completion();
        }
    });
}

//建立本地canvas模型
- (NERtcVideoCanvas *)setupLocalCanvasWithUid:(uint64_t)uid render:(UIView *)render {
    _localCanvas = [[NTESDemoUserModel alloc] init];
    _localCanvas.uid = uid;
    _localCanvas.renderContainer = render;
    return [_localCanvas setupCanvas];
}

//建立远端canvas模型
- (NERtcVideoCanvas *)setupRemoteCanvasWithUid:(uint64_t)uid render:(UIView *)render {
    _remoteCanvas = [[NTESDemoUserModel alloc] init];
    _remoteCanvas.uid = uid;
    _remoteCanvas.renderContainer = render;
    return [_remoteCanvas setupCanvas];
}

//加入房间
- (void)joinChannelWithRoomId:(NSString *)roomId
                       userId:(uint64_t)userId {
    NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
    
    //1v1音视频通话场景的视频推荐配置
    //其他场景下请联系云信技术支持获取配置
    NERtcVideoEncodeConfiguration *config = [[NERtcVideoEncodeConfiguration alloc] init];
//    config.width = 640;
//    config.height = 360;
    config.width = 1280;
    config.height = 720;
    config.frameRate = kNERtcVideoFrameRateFps15;
    [coreEngine setLocalVideoConfig:config];
    
    //1v1音视频通话场景的音频推荐配置
    //其他场景下请联系云信技术支持获取配置
    [coreEngine setAudioProfile:kNERtcAudioProfileStandard
                       scenario:kNERtcAudioScenarioSpeech];
    
    [coreEngine enableLocalAudio:YES];
    [coreEngine enableLocalVideo:YES];
    
    __weak typeof(self) weakSelf = self;
    [coreEngine joinChannelWithToken:@""
                         channelName:roomId
                               myUid:userId
                          completion:^(NSError * _Nullable error, uint64_t channelId, uint64_t elapesd, uint64_t uid) {
        if (error) {
            //加入失败，弹框之后退出当前页面
            NSString *message = [NSString stringWithFormat:@"join channel fail.code:%@", @(error.code)];
            [weakSelf showDismissAlertWithMessage:message actionBlock:^{
                [weakSelf destroyRTCEngineWithCompletion:^{
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [weakSelf.navigationController popViewControllerAnimated:YES];
                    });
                }];
            }];
        } else {
            //加入成功，建立本地canvas渲染本地视图
            NERtcVideoCanvas *canvas = [weakSelf setupLocalCanvasWithUid:weakSelf.userId render:weakSelf.localRender];
            [coreEngine setupLocalVideoCanvas:canvas];
        }
    }];
}

- (void)setupLayout {
    [self.view addSubview:self.functionMenu];
    [self.functionMenu mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.bottom.equalTo(self.view);
        make.height.mas_equalTo(@120);
    }];
    
    NSMutableArray<UIButton *> *buttonArray = [NSMutableArray array];
    NSArray<NSString *> *titleArray = @[@"滤镜", @"美颜"];
    NSArray<NSString *> *selectorNameArray = @[@"onOpenFilterMenuAction:",
                                               @"onOpenBeautyMenuAction:"];
    for (unsigned int i = 0; i < titleArray.count; i++) {
        NSString *title = titleArray[i];
        NSString *selectorName = selectorNameArray[i];
        
        UIButton *button = [[UIButton alloc] initWithFrame:CGRectZero];
        button.backgroundColor = [UIColor clearColor];
        button.titleLabel.font = [UIFont systemFontOfSize:18];
        button.imageView.contentMode = UIViewContentModeScaleAspectFit;
        [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [button setTitle:title forState:UIControlStateNormal];
        [button addTarget:self action:NSSelectorFromString(selectorName) forControlEvents:UIControlEventTouchUpInside];
        
        [buttonArray addObject:button];
        [self.functionMenu addSubview:button];
    }
    [buttonArray mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.functionMenu).offset(10);
    }];
    [buttonArray mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:30 leadSpacing:0 tailSpacing:0];
    
    [self.functionMenu addSubview:self.muteAudioButton];
    [self.functionMenu addSubview:self.muteVideoButton];
    [self.functionMenu addSubview:self.hangupButton];
    
    NSArray<UIButton *> *functionButtonArray = @[self.muteAudioButton, self.hangupButton, self.muteVideoButton];
    [functionButtonArray mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.functionMenu).offset(25);
    }];
    [functionButtonArray mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:30 leadSpacing:0 tailSpacing:0];
}

#pragma mark - Actions

//UI 挂断按钮事件
- (void)onHungupAction:(UIButton *)sender {
    [NERtcEngine.sharedEngine leaveChannel];
}

//UI 关闭本地音频按钮事件
- (void)onAudioMuteAction:(UIButton *)sender {
    sender.selected = !sender.selected;
    [NERtcEngine.sharedEngine enableLocalAudio:!sender.selected];
}

//UI 关闭本地视频按钮事件
- (void)onVideoMuteAction:(UIButton *)sender {
    sender.selected = !sender.selected;
    [NERtcEngine.sharedEngine enableLocalVideo:!sender.selected];
}

- (void)onOpenFilterMenuAction:(UIButton *)sender {
    [[NEBeautyManager sharedManager] displayMenuWithType:NEBeautyConfigViewTypeFilter container:self.view];
}

- (void)onOpenBeautyMenuAction:(UIButton *)sender {
    [[NEBeautyManager sharedManager] displayMenuWithType:NEBeautyConfigViewTypeBeauty container:self.view];
}

- (void)onOpenSitckerMenuAction:(UIButton *)sender {
    [[NEBeautyManager sharedManager] displayMenuWithType:NEBeautyConfigViewTypeSticker container:self.view];
}

- (void)onOpenMakeupMenuAction:(UIButton *)sender {
    [[NEBeautyManager sharedManager] displayMenuWithType:NEBeautyConfigViewTypeMakeup container:self.view];
}

//UI 切换摄像头按钮事件
- (IBAction)onSwitchCameraAction:(UIButton *)sender {
    [NERtcEngine.sharedEngine switchCamera];
}

- (IBAction)onCompareButtonTouchDown:(UIButton *)sender {
    [[NEBeautyManager sharedManager] enableNEBeauty:NO];
}

- (IBAction)onCompareButtonTouchUpInside:(UIButton *)sender {
    [[NEBeautyManager sharedManager] enableNEBeauty:YES];
}

#pragma mark - SDK回调（含义请参考NERtcEngineDelegateEx定义）

- (void)onNERtcEngineDidError:(NERtcError)errCode {
    NSString *message = [NSString stringWithFormat:@"nertc engine did error.code:%@", @(errCode)];
    [self showDismissAlertWithMessage:message actionBlock:^{
        [[NERtcEngine sharedEngine] leaveChannel];
    }];
}

- (void)onNERtcEngineUserDidJoinWithUserID:(uint64_t)userID
                                  userName:(NSString *)userName {
    //如果已经setup了一个远端的canvas，则不需要再建立了
    if (_remoteCanvas != nil) {
        return;
    }
    
    //建立远端canvas，用来渲染远端画面
    _remoteUserId = userID;
    NERtcVideoCanvas *canvas = [self setupRemoteCanvasWithUid:userID render:_remoteRender];
    [NERtcEngine.sharedEngine setupRemoteVideoCanvas:canvas
                                           forUserID:userID];
}

- (void)onNERtcEngineUserVideoDidStartWithUserID:(uint64_t)userID
                                    videoProfile:(NERtcVideoProfileType)profile {
    //如果已经订阅过远端视频流，则不需要再订阅了
    if (_remoteCanvas.subscribedVideo) {
        return;
    }
    
    //订阅远端视频流
    _remoteCanvas.subscribedVideo = YES;
    [NERtcEngine.sharedEngine subscribeRemoteVideo:YES
                                         forUserID:userID
                                        streamType:kNERtcRemoteVideoStreamTypeHigh];
}

- (void)onNERtcEngineUserVideoDidStop:(uint64_t)userID {
    if (userID == _remoteCanvas.uid) {
        _remoteStatLab.hidden = YES;
    }
}

- (void)onNERtcEngineUserDidLeaveWithUserID:(uint64_t)userID
                                     reason:(NERtcSessionLeaveReason)reason {
    //如果远端的人离开了，重置远端模型和UI
    if (userID == _remoteCanvas.uid) {
        _remoteStatLab.hidden = NO;
        [_remoteCanvas resetCanvas];
        _remoteCanvas = nil;
    }
}

- (void)onNERtcEngineDidDisconnectWithReason:(NERtcError)reason {
    //网络连接中断时会触发该回调，触发之后的操作则由开发者按需实现
    //此时已与房间断开连接，如果需要重新加入房间，必须再次调用join接口
}

- (void)onNERtcEngineDidLeaveChannelWithResult:(NERtcError)result {
    //调用leaveChannel之后，若需要释放SDK资源，建议在收到该回调之后，再调用destroyEngine
    [self destroyRTCEngineWithCompletion:^{
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.navigationController popViewControllerAnimated:YES];
        });
    }];
}

#pragma mark - Helper

- (void)showDismissAlertWithMessage:(NSString *)message actionBlock:(void(^)(void))actionBlock {
    UIAlertController *alertVC = [UIAlertController alertControllerWithTitle:@"退出提示"
                                                                     message:message
                                                              preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *exitAction = [UIAlertAction actionWithTitle:@"退出"
                                                         style:UIAlertActionStyleDefault
                                                       handler:^(UIAlertAction * _Nonnull action) {
        if (actionBlock) {
            actionBlock();
        }
    }];
    [alertVC addAction:exitAction];
    [self presentViewController:alertVC animated:YES completion:nil];
}

#pragma mark - Getter

- (UIView *)functionMenu {
    if (!_functionMenu) {
        _functionMenu = [[UIView alloc] initWithFrame:CGRectZero];
        _functionMenu.backgroundColor = [UIColor colorWithRed:44.0f/255.0f green:44.0f/255.0f blue:54.0f/255.0f alpha:1.0];
    }
    
    return _functionMenu;
}

- (UIButton *)muteAudioButton {
    if (!_muteAudioButton) {
        _muteAudioButton = [[UIButton alloc] initWithFrame:CGRectZero];
        _muteAudioButton.backgroundColor = [UIColor clearColor];
        [_muteAudioButton setImage:[UIImage imageNamed:@"Audio"] forState:UIControlStateNormal];
        [_muteAudioButton setImage:[UIImage imageNamed:@"Audio_s"] forState:UIControlStateSelected];
        [_muteAudioButton addTarget:self action:@selector(onAudioMuteAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    
    return _muteAudioButton;
}

- (UIButton *)muteVideoButton {
    if (!_muteVideoButton) {
        _muteVideoButton = [[UIButton alloc] initWithFrame:CGRectZero];
        _muteVideoButton.backgroundColor = [UIColor clearColor];
        [_muteVideoButton setImage:[UIImage imageNamed:@"Video"] forState:UIControlStateNormal];
        [_muteVideoButton setImage:[UIImage imageNamed:@"Video_s"] forState:UIControlStateSelected];
        [_muteVideoButton addTarget:self action:@selector(onVideoMuteAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    
    return _muteVideoButton;
}

- (UIButton *)hangupButton {
    if (!_hangupButton) {
        _hangupButton = [[UIButton alloc] initWithFrame:CGRectZero];
        _hangupButton.backgroundColor = [UIColor clearColor];
        [_hangupButton setImage:[UIImage imageNamed:@"Hunghp"] forState:UIControlStateNormal];
        [_hangupButton setImage:[UIImage imageNamed:@"Hunghp_h"] forState:UIControlStateHighlighted];
        [_hangupButton addTarget:self action:@selector(onHungupAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    
    return _hangupButton;
}

@end
