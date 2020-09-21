//
//  NETSDemoP2PVC.m
//  Beauty-iOS-ObjC
//
//  Created by Think on 2020/9/17.
//  Copyright © 2020 丁文超. All rights reserved.
//

#import "NETSDemoP2PVC.h"
#import "NTESDemoUserModel.h"
#import <NERtcSDK/NERtcSDK.h>
#import "AppKey.h"
#import "NTESGlobalMacro.h"
#import "UIView+NTES.h"
#import "FUManager.h"

@interface NETSDemoP2PVC () <NERtcEngineDelegateEx>

@property (strong, nonatomic) UIView            *localRender;  // 本地渲染视图
@property (strong, nonatomic) UIView            *remoteRender; // 远端渲染视图
@property (strong, nonatomic) UILabel           *remoteStatLab;
@property (strong, nonatomic) UIButton          *beautyBtn;
@property (strong, nonatomic) UIButton          *hangupBtn;

@property (nonatomic, copy) NSString            *roomId;        // 房间ID
@property (nonatomic, assign) uint64_t          userId;         // 本人uid
@property (nonatomic, assign)   BOOL            enableBeauty;   // 是否开启美颜

@property (nonatomic, strong) NTESDemoUserModel *localCanvas;  // 本地 canvas 模型
@property (nonatomic, strong) NTESDemoUserModel *remoteCanvas; // 远端 canvas 模型

@end

@implementation NETSDemoP2PVC

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

- (void)setupRTCEngine
{
    NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
    NSDictionary *params = @{
        kNERtcKeyPublishSelfStreamEnabled: @YES,    // 打开推流
        kNERtcKeyVideoCaptureObserverEnabled: @YES  // 将摄像头采集的数据回调给用户
    };
    [coreEngine setParameters:params];
    
    NERtcEngineContext *context = [[NERtcEngineContext alloc] init];
    context.engineDelegate = self;
    context.appKey = kAppKey;
    [coreEngine setupEngineWithContext:context];
    
    [coreEngine enableLocalAudio:YES];
    [coreEngine enableLocalVideo:YES];
    
    NERtcVideoEncodeConfiguration *config = [[NERtcVideoEncodeConfiguration alloc] init];
    config.maxProfile = kNERtcVideoProfileHD720P;
    [coreEngine setLocalVideoConfig:config];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self setupViews];
    [self joinChannelWithRoomId:_roomId userId:_userId];
    
    [[FUManager shareManager] loadFilter];
}

- (void)setupViews
{
    self.navigationController.navigationBar.translucent = NO;
    self.view.backgroundColor = [UIColor blackColor];
    
    [self.view addSubview:self.localRender];
    [self.view addSubview:self.remoteRender];
    [self.remoteRender addSubview:self.remoteStatLab];
    [self.view addSubview:self.beautyBtn];
    [self.view addSubview:self.hangupBtn];
    
    CGFloat scale = 360 / 640.0;
    CGFloat renderWidth = (UIScreenWidth - 45) / 2.0;
    self.localRender.frame = CGRectMake(15, 15, renderWidth, renderWidth / scale);
    self.remoteRender.frame = CGRectMake(self.localRender.right + 15, self.localRender.top, self.localRender.width, self.localRender.height);
    self.remoteStatLab.frame = CGRectMake(0, self.remoteRender.centerY - 10, self.remoteRender.width, 20);
    self.beautyBtn.frame = CGRectMake(self.localRender.centerX - 35, self.localRender.bottom + 30, 80, 44);
    self.hangupBtn.frame = CGRectMake(self.remoteRender.centerX - 35, self.localRender.bottom + 30, 80, 44);
}

// 建立本地canvas模型
- (NERtcVideoCanvas *)setupLocalCanvas
{
    _localCanvas = [[NTESDemoUserModel alloc] init];
    _localCanvas.uid = _userId;
    _localCanvas.renderContainer = self.localRender;
    return [_localCanvas setupCanvas];
}

// 建立远端canvas模型
- (NERtcVideoCanvas *)setupRemoteCanvasWithUid:(uint64_t)uid
{
    _remoteCanvas = [[NTESDemoUserModel alloc] init];
    _remoteCanvas.uid = uid;
    _remoteCanvas.renderContainer = self.remoteRender;
    return [_remoteCanvas setupCanvas];
}

// 加入房间
- (void)joinChannelWithRoomId:(NSString *)roomId userId:(uint64_t)userId
{
    __weak typeof(self) weakSelf = self;
    [NERtcEngine.sharedEngine joinChannelWithToken:@""
                                       channelName:roomId
                                             myUid:userId
                                        completion:^(NSError * _Nullable error, uint64_t channelId, uint64_t elapesd) {
        if (error) {
            //加入失败了，弹框之后退出当前页面
            NSString *msg = [NSString stringWithFormat:@"join channel fail.code:%@", @(error.code)];
            [weakSelf showDismissAlert:msg];
        } else {
            //加入成功，建立本地canvas渲染本地视图
            NERtcVideoCanvas *canvas = [weakSelf setupLocalCanvas];
            [NERtcEngine.sharedEngine setupLocalVideoCanvas:canvas];
        }
    }];
}

#pragma mark - NERtcEngineDelegateEx

- (void)onNERtcEngineDidError:(NERtcError)errCode
{
    NSString *msg = [NSString stringWithFormat:@"nertc engine did error.code:%@", @(errCode)];
    [self showDismissAlert:msg];
}

- (void)onNERtcEngineUserDidJoinWithUserID:(uint64_t)userID userName:(NSString *)userName
{
    // 如果已经setup了一个远端的canvas，则不需要再建立了
    if (_remoteCanvas != nil) {
        return;
    }
    
    // 建立远端canvas，用来渲染远端画面
    NERtcVideoCanvas *canvas = [self setupRemoteCanvasWithUid:userID];
    [NERtcEngine.sharedEngine setupRemoteVideoCanvas:canvas
                                           forUserID:userID];
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

- (void)onNERtcEngineUserVideoDidStop:(uint64_t)userID
{
    if (userID == _remoteCanvas.uid) {
        _remoteStatLab.hidden = YES;
    }
}

- (void)onNERtcEngineUserDidLeaveWithUserID:(uint64_t)userID reason:(NERtcSessionLeaveReason)reason
{
    // 如果远端的人离开了，重置远端模型和UI
    if (userID == _remoteCanvas.uid) {
        _remoteStatLab.hidden = NO;
        [_remoteCanvas resetCanvas];
        _remoteCanvas = nil;
    }
}

// 在代理方法中对视频数据进行处理
- (void)onNERtcEngineVideoFrameCaptured:(CVPixelBufferRef)bufferRef rotation:(NERtcVideoRotationType)rotation
{
//    fuSetDefaultRotationMode([FUManager shareManager].deviceOrientation);
    if (_enableBeauty) {
        [[FUManager shareManager] renderItemsToPixelBuffer:bufferRef];
    }
}

- (void)clickBtn:(UIButton *)sender
{
    if (sender == self.beautyBtn) {
        _enableBeauty = !_enableBeauty;
        NSString *title = _enableBeauty ? @"关闭美颜" : @"开启美颜";
        [self.beautyBtn setTitle:title forState:UIControlStateNormal];
    }
    
    if (sender == self.hangupBtn) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            [[NERtcEngine sharedEngine] leaveChannel];
            int res = [NERtcEngine destroyEngine];
            if (res != 0) {
                NELPLogError(@"Destory engine error: %d", res);
            }
            ntes_main_async_safe(^{
                [self.navigationController popViewControllerAnimated:YES];
            });
        });
    }
}

#pragma mark - Getter
//判断当前房间是否已经满员
- (BOOL)membersIsFull
{
    return (_remoteCanvas != nil);
}

#pragma mark - Helper
- (void)showDismissAlert:(NSString *)msg
{
    UIAlertController *alertVC = [UIAlertController alertControllerWithTitle:@"退出提示"
                                                                     message:msg
                                                              preferredStyle:UIAlertControllerStyleAlert];
    
    __weak typeof(self) weakSelf = self;
    UIAlertAction *sure = [UIAlertAction actionWithTitle:@"退出"
                                                   style:UIAlertActionStyleDefault
                                                 handler:^(UIAlertAction * _Nonnull action) {
        [weakSelf dismiss];
    }];
    [alertVC addAction:sure];
    [self presentViewController:alertVC animated:YES completion:nil];
}

- (void)dismiss
{
    [self.navigationController popViewControllerAnimated:YES];
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
    }
    return _remoteRender;
}

- (UILabel *)remoteStatLab
{
    if (!_remoteStatLab) {
        _remoteStatLab = [[UILabel alloc] init];
        _remoteStatLab.font = [UIFont systemFontOfSize:14.0];
        _remoteStatLab.text = @"等待加入";
        _remoteStatLab.textColor = [UIColor whiteColor];
        _remoteStatLab.textAlignment = NSTextAlignmentCenter;
    }
    return _remoteStatLab;
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
