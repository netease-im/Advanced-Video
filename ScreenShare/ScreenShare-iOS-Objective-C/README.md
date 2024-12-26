# ScreenShare-iOS-Objective-C

## 功能介绍

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现屏幕共享。
- 将屏幕录制作为采集源
- 使用云信提供的封装NERtcReplayKit.framework，实现与RTC SDK的交互进而实现实时屏幕共享

## 环境准备，运行示例项目，一对一视频通话功能实现

这个开源示例项目基于多人视频通话，关于**环境准备**，**运行示例项目**，**功能实现**章节请参考[一对一视频通话](https://github.com/netease-im/Basic-Video-Call/blob/master/One-to-One-Video/NERtcSample-1to1-iOS-Objective-C/README.md)

## <span id="实现方法">实现方法</span>

       iOS实现屏幕分享的基本原理是利用iOS系统的 ReplayKit 特性。ReplayKit 仅支持iOS 11.0 以上共享系统屏幕。
    云信SDK提供了对ReplayKit的封装NERtcReplayKit.framework，可以方便的进行使用。

具体实现分为两部分(扩展Extension程序, 主工程)。

### 添加ReplayKit Extension

1. Broadcast Upload Extension

选择Target，添加 Extension

![image](https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2Fadd_ext.jpg)

![image](https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2Fchose_ext.jpg)

2. 建立同名 AppGroup 数据池，使 NERtcReplayKit 可在Extension程序和主工程之间通信。Debug调试状态下可以不建立AppGroup，直接使用NERtcReplayKit进行屏幕共享调试。具体使用见示例代码

3. 在扩展程序中集成NERtcReplayKit.framework，可使用手动导入，也可使用pod导入，pod导入时，可使用`pod 'NERtcSDK/ScreenShare'`， 版本需要高于`5.0.0`

### 屏幕分享主程序

1. 初始化云信NERTCSDK，配置允许使用外部视频源，确保视频通话功能正常
2. 在 RPSystemBroadcastPickerView 中添加扩展程序
3. 初始化同AppGroup名资料夹, 并初始化设置NERtcReplayKit

具体代码及流程请参照示例代码和[Sample工程](https://github.com/netease-im/Advanced-Video/tree/master/ScreenShare/ScreenShare-iOS-Objective-C)。

## <span id="示例代码">示例代码</span>

### ReplayKit Extension部分

1. 建立同名 AppGroup 数据池，用于扩展 ReplayKit 程序和主工程之间通信
2. 在扩展程序中集成NERtcReplayKit.framework，可使用手动导入，也可使用pod导入，pod导入时，可使用`pod 'NERtcSDK/ScreenShare'`， 版本需要高于`5.0.0`
3. 在扩展程序中实现NERtcReplayKit的代理方法，实现与主工程的交互

```objc
- (void)broadcastStartedWithSetupInfo:(NSDictionary<NSString *,NSObject *> *)setupInfo {
  NEScreenShareBroadcasterOptions *options = [[NEScreenShareBroadcasterOptions alloc]init];
  options.appGroup = <#请输入您的AppKey#>;
#if DEBUG
  options.enableDebug = YES;
#endif
  //开启音频共享，默认不开启
  options.needAudioSampleBuffer = YES;
  options.needMicAudioSampleBuffer = YES;
  [NEScreenShareSampleHandler sharedInstance].delegate = self;
  [[NEScreenShareSampleHandler sharedInstance] broadcastStartedWithSetupInfo:options];
}

- (void)broadcastPaused {
  // User has requested to pause the broadcast. Samples will stop being delivered.
  [[NEScreenShareSampleHandler sharedInstance] broadcastPaused];
}

- (void)broadcastResumed {
  // User has requested to resume the broadcast. Samples delivery will resume.
  [[NEScreenShareSampleHandler sharedInstance] broadcastResumed];
}

- (void)broadcastFinished {
  // User has requested to finish the broadcast.
  [[NEScreenShareSampleHandler sharedInstance] broadcastFinished];
}

- (void)processSampleBuffer:(CMSampleBufferRef)sampleBuffer withType:(RPSampleBufferType)sampleBufferType {
  [[NEScreenShareSampleHandler sharedInstance] processSampleBuffer:sampleBuffer withType:sampleBufferType];
}
```

### 屏幕分享主程序

1. 初始化SDK，配置允许使用外部视频源，确保视频通话功能正常

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

3. 初始化同AppGroup名资料夹, 并初始化设置NERtcReplayKit。

```objc
    NEScreenShareHostOptions *options = [[NEScreenShareHostOptions alloc] init];
#if DEBUG
    options.enableDebug = YES;;
#endif
    options.appGroup = <#请输入您的AppKey#>;
    options.delegate = self;
    //定制参数，具体见API文档
    options.extraInfoDict = @{};
    [[NEScreenShareHost sharedInstance] setupScreenshareOptions:options];
```

4. 实现NERtcReplayKit的代理方法，推送外部视频帧到RTCSDK

```objc
/// 视频帧回调
- (void)onReceiveVideoFrame:(NEScreenShareVideoFrame *)videoFrame {
    NERtcVideoFrame *frame = [[NERtcVideoFrame alloc] init];
    frame.format = kNERtcVideoFormatI420;
    frame.width = videoFrame.width;
    frame.height = videoFrame.height;
    frame.buffer = (void *)[videoFrame.videoData bytes];
    frame.timestamp = videoFrame.timeStamp;
    frame.rotation = (NERtcVideoRotationType)videoFrame.rotation;
    int ret = [NERtcEngine.sharedEngine pushExternalVideoFrame:frame];
    if (ret != 0) {
        NSLog(@"发送视频流失败:%d", ret);
    }
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