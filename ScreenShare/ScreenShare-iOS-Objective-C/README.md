# ScreenShare-iOS-Objective-C

## 功能介绍

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现屏幕共享。
- 将屏幕录制作为采集源

## 环境准备，运行示例项目，一对一视频通话功能实现

这个开源示例项目基于多人视频通话，关于**环境准备**，**运行示例项目**，**功能实现**章节请参考[一对一视频通话](https://github.com/netease-im/Basic-Video-Call/blob/master/One-to-One-Video/NERtcSample-1to1-iOS-Objective-C/README.md)

## <span id="实现方法">实现方法</span>

iOS实现屏幕分享的基本原理是利用iOS的 ReplayKit 特性。ReplayKit 仅支持iOS 11.0 以上共享系统屏幕。基本流程是添加ReplayKit扩展、云信SDK创建加入房间并使用自定义采集、使用 AppGroup 在宿主App(主工程，这里是屏幕共享Sample)和扩展ReplayKit程序之间进行视频数据（音频使用宿主APP中云信SDK采集）和控制指令传输；

具体实现分为两部分(扩展 ReplayKit 程序, 主工程)。

### 添加ReplayKit

1. Broadcast Upload Extension

选择Target，添加 Extension

![image](https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2Fadd_ext.jpg)

![image](https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2Fchose_ext.jpg)

2. 建立同名 AppGroup 数据池，用于扩展 ReplayKit 程序和主工程之间通信
3. ReplayKit 采集到的屏幕视频数据通过 processSampleBuffer:withType:给用户，忽略音频数据回调（我们使用云信SDK音频采集），将视频数据压缩后存入共用资料夹，主程序监测到视频数据变更后，通过SDK自定义视频数据进行发送。

### 屏幕分享主程序

1. 初始化SDK，配置允许使用外部视频源，确保视频通话功能正常;
2. 在 RPSystemBroadcastPickerView 中添加扩展程序;
3. 初始化同AppGroup名资料夹, 并添加监听事件;
4. 监听到数据帧变化, 校验后推送外部视频帧到SDK;

具体代码及流程请参照示例代码和[Sample工程](https://github.com/netease-im/Advanced-Video/tree/master/ScreenShare/ScreenShare-iOS-Objective-C)。

## <span id="示例代码">示例代码</span>

### ReplayKit部分

1. 建立同名 AppGroup 数据池，用于扩展 ReplayKit 程序和主工程之间通信

```objc
- (void)broadcastStartedWithSetupInfo:(NSDictionary<NSString *,NSObject *> *)setupInfo {
    self.userDefautls = [[NSUserDefaults alloc] initWithSuiteName:<#kAppGroupName#>];
}
```

2. 压缩裁剪采集图片，发送到宿主App

ReplayKit 采集到的屏幕视频数据通过 processSampleBuffer:withType:给用户，忽略音频数据回调（我们使用云信SDK音频采集），将视频数据存入共用资料夹，主程序监测倒数据变更后，再通过SDK自定义视频数据进行发送。

```objc
- (void)processSampleBuffer:(CMSampleBufferRef)sampleBuffer withType:(RPSampleBufferType)sampleBufferType {
    
    switch (sampleBufferType) {
        case RPSampleBufferTypeVideo: {
            @autoreleasepool {
                CVImageBufferRef pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer);
                NSDictionary *frame = [self createI420VideoFrameFromPixelBuffer:pixelBuffer];
                [self.userDefautls setObject:frame forKey:<#KeyPath#>];
                [self.userDefautls synchronize];
            }
            break;
        }
        case RPSampleBufferTypeAudioApp:
            // Handle audio sample buffer for app audio
            break;
        case RPSampleBufferTypeAudioMic:
            // Handle audio sample buffer for mic audio
            break;
            
        default:
            break;
    }
}
```

数据压缩采用的是 [libyuv](https://chromium.googlesource.com/libyuv/libyuv/) 第三方工具。

```objc
- (NSDictionary *)createI420VideoFrameFromPixelBuffer:(CVPixelBufferRef)pixelBuffer
{
    CVPixelBufferLockBaseAddress(pixelBuffer, 0);
    
    // 转I420
    int psrc_w = (int)CVPixelBufferGetWidth(pixelBuffer);
    int psrc_h = (int)CVPixelBufferGetHeight(pixelBuffer);
    uint8 *src_y = (uint8 *)CVPixelBufferGetBaseAddressOfPlane(pixelBuffer, 0);
    uint8 *src_uv = (uint8 *)CVPixelBufferGetBaseAddressOfPlane(pixelBuffer, 1);
    int y_stride = (int)CVPixelBufferGetBytesPerRowOfPlane(pixelBuffer, 0);
    int uv_stride = (int)CVPixelBufferGetBytesPerRowOfPlane(pixelBuffer, 1);
    uint8 *i420_buf = (uint8 *)malloc((psrc_w * psrc_h * 3) >> 1);
    
    libyuv::NV12ToI420(&src_y[0],                              y_stride,
                       &src_uv[0],                             uv_stride,
                       &i420_buf[0],                           psrc_w,
                       &i420_buf[psrc_w * psrc_h],             psrc_w >> 1,
                       &i420_buf[(psrc_w * psrc_h * 5) >> 2],  psrc_w >> 1,
                       psrc_w, psrc_h);

    // 缩放至720
    int pdst_w = 720;
    int pdst_h = psrc_h * (pdst_w/(double)psrc_w);
    libyuv::FilterMode filter = libyuv::kFilterNone;
    uint8 *pdst_buf = (uint8 *)malloc((pdst_w * pdst_h * 3) >> 1);
    libyuv::I420Scale(&i420_buf[0],                          psrc_w,
                      &i420_buf[psrc_w * psrc_h],            psrc_w >> 1,
                      &i420_buf[(psrc_w * psrc_h * 5) >> 2], psrc_w >> 1,
                      psrc_w, psrc_h,
                      &pdst_buf[0],                          pdst_w,
                      &pdst_buf[pdst_w * pdst_h],            pdst_w >> 1,
                      &pdst_buf[(pdst_w * pdst_h * 5) >> 2], pdst_w >> 1,
                      pdst_w, pdst_h,
                      filter);

    free(i420_buf);
    
    CVPixelBufferUnlockBaseAddress(pixelBuffer, 0);
    
    NSUInteger dataLength = pdst_w * pdst_h * 3 >> 1;
    NSData *data = [NSData dataWithBytesNoCopy:pdst_buf length:dataLength];
    
    NSDictionary *frame = @{
        @"width": @(pdst_w),
        @"height": @(pdst_h),
        @"data": data,
        @"timestamp": @(CACurrentMediaTime() * 1000)
    };
    return frame;
}
```

### 屏幕分享主程序

1. 初始化SDK，配置允许使用外部视频源，确保视频通话功能正常；

```objc
NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
[coreEngine enableLocalAudio:YES];
[coreEngine enableLocalVideo:YES];
[coreEngine setExternalVideoSource:YES]; // 初始化SDK, 设置允许使用外部视频源
NERtcEngineContext *context = [[NERtcEngineContext alloc] init];
context.engineDelegate = self;
context.appKey = <#请输入您的AppKey#>;
[coreEngine setupEngineWithContext:context];
```

2. 添加扩展程序相关

```objc
- (void)addSystemBroadcastPickerIfPossible
{
    if (@available(iOS 12.0, *)) {
        // Not recommend
        RPSystemBroadcastPickerView *picker = [[RPSystemBroadcastPickerView alloc] initWithFrame:CGRectMake(0, 0, 120, 64)];
        picker.showsMicrophoneButton = NO;
        picker.preferredExtension = <#扩展程序的BundleId#>;
        [self.view addSubview:picker];
        picker.center = self.view.center;
        
        UIButton *button = [picker.subviews filteredArrayUsingPredicate:[NSPredicate predicateWithBlock:^BOOL(id  _Nullable evaluatedObject, NSDictionary<NSString *,id> * _Nullable bindings) {
            return [evaluatedObject isKindOfClass:UIButton.class];
        }]].firstObject;
        [button setImage:nil forState:UIControlStateNormal];
        [button setTitle:@"Start Share" forState:UIControlStateNormal];
        [button setTitleColor:self.navigationController.navigationBar.tintColor forState:UIControlStateNormal];
        
        UIBarButtonItem *leftItem = [[UIBarButtonItem alloc] initWithCustomView:picker];
        self.navigationItem.leftBarButtonItem = leftItem;
    }
}
```

3. 初始化同AppGroup名资料夹, 并添加监听事件。

```objc
- (void)setupUserDefaults
{
    // 通过UserDefaults建立数据通道，接收Extension传递来的视频帧
    self.userDefaults = [[NSUserDefaults alloc] initWithSuiteName:<#AppGroupName#>];
    [self.userDefaults addObserver:self forKeyPath:<#KeyPath#> options:NSKeyValueObservingOptionNew context:KVOContext];
}
```

4. 监听到数据帧变化, 校验后推送外部视频帧到SDK

```objc
- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context
{
    if ([keyPath isEqualToString:<#KeyPath#>]) {
        if (self.currentUserID) {
            NSDictionary *i420Frame = change[NSKeyValueChangeNewKey];
            NERtcVideoFrame *frame = [[NERtcVideoFrame alloc] init];
            frame.format = kNERtcVideoFormatI420;
            frame.width = [i420Frame[@"width"] unsignedIntValue];
            frame.height = [i420Frame[@"height"] unsignedIntValue];
            frame.buffer = (void *)[i420Frame[@"data"] bytes];
            frame.timestamp = [i420Frame[@"timestamp"] unsignedLongLongValue];
            int ret = [NERtcEngine.sharedEngine pushExternalVideoFrame:frame]; // 推送外部视频帧到SDK
            if (ret != 0) {
                NSLog(@"发送视频流失败:%d", ret);
                return;
            }
        }
    }
}
```

5. 不使用该功能时，记得移除观察者;

```objc
- (void)dealloc
{
    [self.userDefaults removeObserver:self forKeyPath:<#KeyPath#>];
    [NERtcEngine.sharedEngine leaveChannel];
}
```

## <span id="API参考">API参考</span>

 **方法** | **功能描述**
:--|:--
[setExternalVideoSource](https://dev.yunxin.163.com/docs/interface/%E9%9F%B3%E8%A7%86%E9%A2%912.0iOS%E7%AB%AF/Protocols/INERtcEngineEx.html#//api/name/setExternalVideoSource:)|配置外部视频源
[pushExternalVideoFrame](https://dev.yunxin.163.com/docs/interface/%E9%9F%B3%E8%A7%86%E9%A2%912.0iOS%E7%AB%AF/Protocols/INERtcEngineEx.html#//api/name/pushExternalVideoFrame:)|推送外部视频帧

## <span id="开发注意事项">开发注意事项</span>

1. 主app和系统录屏需使用相同的AppGroup名;
2. Sample工程支持iOS12及以上唤起系统录屏能力，若系统低于iOS12，需手动唤起系统录屏;