# AudioMixing-iOS-ObjC

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现通话过程中混音效果功能。

## 环境准备，运行示例项目，多人视频通话功能实现

这个开源示例项目基于多人视频通话，关于**环境准备**，**运行示例项目**，**功能实现**章节请参考[多人视频通话](https://github.com/netease-im/Basic-Video-Call/blob/master/Group-Video/NERtcSample-GroupVideoCall-iOS-Objective-C/README.md)
## 功能实现

如果需要实现混音音效，参考如下步骤，在你的项目中实现播放混音文件：

1. 在加入频道成功后调用 startAudioMixingWithOption 开始混音。
2. 根据 pauseAudioMixing、resumeAudioMixing、stopAudioMixing、setAudioMixingSendVolume、getAudioMixingCurrentPosition 等进行 暂停、恢复、结束、控制音量、定位 等功能。
3. 在离开频道前调用 stopAudioMixing 结束混音。

## 示例代码

### 混音(Mixing)效果实现：

```objc
NERtcCreateAudioMixingOption *opt = [[NERtcCreateAudioMixingOption alloc] init];
opt.path = [[NSBundle mainBundle] pathForResource:@"music" ofType:@"m4a"]; //设置文件地址
opt.playbackVolume = _playVolumn; //播放音量
opt.sendVolume = _sendVolumn; //发送音量
opt.loopCount = 1; //循环次数
int result = [[NERtcEngine sharedEngine] startAudioMixingWithOption:opt];
```

### 音效(Effect)实现

```objc
NERtcCreateAudioEffectOption *opt = [[NERtcCreateAudioEffectOption alloc] init];
opt.path = [[NSBundle mainBundle] pathForResource:@"effect" ofType:@"wav"]; //设置文件地址
opt.playbackVolume = _playVolumn; //播放音量
opt.sendVolume = _sendVolumn; //发送音量
opt.loopCount = 1; //循环次数
[[NERtcEngine sharedEngine] stopAllEffects];
[[NERtcEngine sharedEngine] playEffectWitdId:eid effectOption:opt];
```

## 混音（Mixing）与音效(Effect)区别
混音同一时刻只能播放一首音乐，音效可以播放多首，并设置不同的Id管理。




