//
//  NETSDemoP2PVC.m
//  Beauty-iOS-ObjC
//
//  Created by Think on 2020/9/17.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NETSDemoP2PVC.h"
#import "NTESDemoUserModel.h"
#import "NTESGlobalMacro.h"
#import "AppKey.h"
#import "UIView+NTES.h"
#import "FUManager.h"

#import <NERtcSDK/NERtcSDK.h>

@interface NETSDemoP2PVC () <NERtcEngineDelegateEx>

@property (strong, nonatomic) UIView            *localRender;  // 本地渲染视图
@property (strong, nonatomic) UIView            *remoteRender; // 远端渲染视图
@property (strong, nonatomic) UIButton          *beautyBtn;
@property (strong, nonatomic) UIButton          *hangupBtn;

@property (nonatomic, copy) NSString            *roomId;        // 房间ID
@property (nonatomic, assign) uint64_t          userId;         // 本人uid
@property (nonatomic, assign) uint64_t          remoteUserId;   // 远端用户uid
@property (nonatomic, assign)   BOOL            enableBeauty;   // 是否开启美颜

@property (nonatomic, strong) NTESDemoUserModel *localCanvas;  // 本地 canvas 模型
@property (nonatomic, strong) NTESDemoUserModel *remoteCanvas; // 远端 canvas 模型

@end

@implementation NETSDemoP2PVC

#pragma mark - Public

- (instancetype)initWithRoomId:(NSString *)roomId userId:(uint64_t)userId
{
    self = [super init];
    if (self) {
        self.roomId = roomId;
        self.userId = userId;
        
        //初始化SDK
        [self setupRTCEngine];
    }
    return self;
}

#pragma mark - Life Cycle

- (void)dealloc
{
    NSLog(@"%s", __FUNCTION__);
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self setupViews];
    [self joinChannelWithRoomId:_roomId userId:_userId];
    
    [[FUManager shareManager] loadFilter];
}

#pragma mark - Functions

- (void)setupRTCEngine
{
    // 默认情况下日志会存储在App沙盒的Documents目录下
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
- (void)destroyRTCEngineWithCompletion:(void(^)(void))completion
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0), ^{
        [NERtcEngine destroyEngine];
        
        if (completion) {
            completion();
        }
    });
}

- (void)setupViews
{
    self.navigationController.navigationBar.translucent = NO;
    self.view.backgroundColor = [UIColor blackColor];
    
    [self.view addSubview:self.localRender];
    [self.view addSubview:self.remoteRender];
    [self.view addSubview:self.beautyBtn];
    [self.view addSubview:self.hangupBtn];
    
    CGFloat scale = 2 / 3.0;
    CGFloat remoteRenderWidth = MIN(UIScreenWidth, UIScreenHeight) / 3.0;
    self.localRender.frame = CGRectMake(0, 0, UIScreenWidth, UIScreenHeight);
    self.remoteRender.frame = CGRectMake(UIScreenWidth - remoteRenderWidth - 15, 15 + (IPHONE_X ? 44 : 0), remoteRenderWidth, remoteRenderWidth / scale);
    self.beautyBtn.frame = CGRectMake(UIScreenWidth * 0.25 - 40, UIScreenHeight - 44 - (IPHONE_X ? 74 : 30), 80, 44);
    self.hangupBtn.frame = CGRectMake(UIScreenWidth * 0.75 - 40, self.beautyBtn.top, self.beautyBtn.width, self.beautyBtn.height);
}

// 建立本地canvas模型
- (NERtcVideoCanvas *)setupLocalCanvasWithUid:(uint64_t)uid render:(UIView *)render
{
    _localCanvas = [[NTESDemoUserModel alloc] init];
    _localCanvas.uid = uid;
    _localCanvas.renderContainer = render;
    return [_localCanvas setupCanvas];
}

// 建立远端canvas模型
- (NERtcVideoCanvas *)setupRemoteCanvasWithUid:(uint64_t)uid render:(UIView *)render
{
    _remoteCanvas = [[NTESDemoUserModel alloc] init];
    _remoteCanvas.uid = uid;
    _remoteCanvas.renderContainer = render;
    return [_remoteCanvas setupCanvas];
}

// 加入房间
- (void)joinChannelWithRoomId:(NSString *)roomId userId:(uint64_t)userId
{
    NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
    
    //1v1音视频通话+美颜场景的视频推荐配置
    //其他场景下请联系云信技术支持获取配置
    NERtcVideoEncodeConfiguration *config = [[NERtcVideoEncodeConfiguration alloc] init];
    config.width = 640;
    config.height = 360;
    config.frameRate = kNERtcVideoFrameRateFps15;
    [coreEngine setLocalVideoConfig:config];
    
    //1v1音视频通话+美颜场景的音频推荐配置
    //其他场景下请联系云信技术支持获取配置
    [coreEngine setAudioProfile:kNERtcAudioProfileStandard
                       scenario:kNERtcAudioScenarioSpeech];
    
    NSDictionary *params = @{
        kNERtcKeyVideoCaptureObserverEnabled: @YES // 将摄像头采集的数据回调给用户
    };
    [coreEngine setParameters:params];
    
    [coreEngine enableLocalAudio:YES];
    [coreEngine enableLocalVideo:YES];
    
    __weak typeof(self) weakSelf = self;
    [coreEngine joinChannelWithToken:@""
                         channelName:roomId
                               myUid:userId
                          completion:^(NSError * _Nullable error, uint64_t channelId, uint64_t elapesd) {
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

#pragma mark - SDK回调（含义请参考NERtcEngineDelegateEx定义）

- (void)onNERtcEngineDidError:(NERtcError)errCode
{
    NSString *message = [NSString stringWithFormat:@"nertc engine did error.code:%@", @(errCode)];
    [self showDismissAlertWithMessage:message actionBlock:^{
        [[NERtcEngine sharedEngine] leaveChannel];
    }];
}

- (void)onNERtcEngineUserDidJoinWithUserID:(uint64_t)userID userName:(NSString *)userName
{
    // 如果已经setup了一个远端的canvas，则不需要再建立了
    if (_remoteCanvas != nil) {
        return;
    }
    
    // 建立远端canvas，用来渲染远端画面
    _remoteUserId = userID;
    NERtcVideoCanvas *canvas = [self setupRemoteCanvasWithUid:_remoteUserId render:_remoteRender];
    [NERtcEngine.sharedEngine setupRemoteVideoCanvas:canvas
                                           forUserID:_remoteUserId];
}

- (void)onNERtcEngineUserVideoDidStartWithUserID:(uint64_t)userID videoProfile:(NERtcVideoProfileType)profile
{
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

- (void)onNERtcEngineUserDidLeaveWithUserID:(uint64_t)userID reason:(NERtcSessionLeaveReason)reason
{
    // 如果远端的人离开了，重置远端模型和UI
    if (userID == _remoteCanvas.uid) {
        [_remoteCanvas resetCanvas];
        _remoteCanvas = nil;
    }
}

// 在代理方法中对视频数据进行处理
- (void)onNERtcEngineVideoFrameCaptured:(CVPixelBufferRef)bufferRef rotation:(NERtcVideoRotationType)rotation
{
    if (_enableBeauty) {
        [[FUManager shareManager] renderItemsToPixelBuffer:bufferRef];
    }
}

- (void)onNERtcEngineDidDisconnectWithReason:(NERtcError)reason
{
    //网络连接中断时会触发该回调，触发之后的操作则由开发者按需实现
}

- (void)onNERtcEngineDidLeaveChannelWithResult:(NERtcError)result
{
    //调用leaveChannel之后，若需要释放SDK资源，建议在收到该回调之后，再调用destroyEngine
    [self destroyRTCEngineWithCompletion:^{
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.navigationController popViewControllerAnimated:YES];
        });
    }];
}

#pragma mark - Action

- (void)clickBtn:(UIButton *)sender
{
    if (sender == self.beautyBtn) {
        _enableBeauty = !_enableBeauty;
        NSString *title = _enableBeauty ? @"关闭美颜" : @"开启美颜";
        [self.beautyBtn setTitle:title forState:UIControlStateNormal];
    }
    
    if (sender == self.hangupBtn) {
        [[NERtcEngine sharedEngine] leaveChannel];
    }
}

#pragma mark - Helper

- (void)showDismissAlertWithMessage:(NSString *)message actionBlock:(void(^)(void))actionBlock
{
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

- (void)switchCanvas
{
    UIView *localRender = _localRender;
    UIView *remoteRender = _remoteRender;
    if (_localCanvas.renderContainer == _localRender) {
        localRender = _remoteRender;
        remoteRender = _localRender;
    }
    
    NERtcVideoCanvas *localCanvas = [self setupLocalCanvasWithUid:_userId render:localRender];
    [NERtcEngine.sharedEngine setupLocalVideoCanvas:localCanvas];
    
    NERtcVideoCanvas *remoteCanvas = [self setupRemoteCanvasWithUid:_remoteUserId render:remoteRender];
    [NERtcEngine.sharedEngine setupRemoteVideoCanvas:remoteCanvas forUserID:_remoteUserId];
}

#pragma mark - mazy load

- (UIView *)localRender
{
    if (!_localRender) {
        _localRender = [[UIView alloc] init];
        _localRender.backgroundColor = [UIColor grayColor];
    }
    return _localRender;
}

- (UIView *)remoteRender
{
    if (!_remoteRender) {
        _remoteRender = [[UIView alloc] init];
        _remoteRender.backgroundColor = [UIColor grayColor];
        
        _remoteRender.userInteractionEnabled = YES;
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(switchCanvas)];
        [_remoteRender addGestureRecognizer:tap];
    }
    return _remoteRender;
}

- (UIButton *)beautyBtn
{
    if (!_beautyBtn) {
        _beautyBtn = [[UIButton alloc] init];
        [_beautyBtn setTitle:@"开启美颜" forState:UIControlStateNormal];
        _beautyBtn.titleLabel.font = [UIFont systemFontOfSize:14.0];
        _beautyBtn.layer.cornerRadius = 4;
        _beautyBtn.layer.masksToBounds = YES;
        _beautyBtn.backgroundColor = UIColorFromRGB(0x309d40);
        [_beautyBtn addTarget:self action:@selector(clickBtn:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _beautyBtn;
}

- (UIButton *)hangupBtn
{
    if (!_hangupBtn) {
        _hangupBtn = [[UIButton alloc] init];
        [_hangupBtn setTitle:@"挂断" forState:UIControlStateNormal];
        _hangupBtn.titleLabel.font = [UIFont systemFontOfSize:14.0];
        _hangupBtn.layer.cornerRadius = 4;
        _hangupBtn.layer.masksToBounds = YES;
        _hangupBtn.backgroundColor = UIColorFromRGB(0xd4273e);
        [_hangupBtn addTarget:self action:@selector(clickBtn:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _hangupBtn;
}

@end
