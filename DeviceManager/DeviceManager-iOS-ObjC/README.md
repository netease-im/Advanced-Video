# DeviceManager-iOS-ObjC

## 功能介绍

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现音视频管理。

## 环境准备，运行示例项目，多人视频通话功能实现

这个开源示例项目基于多人视频通话，关于**环境准备**，**运行示例项目**，**功能实现**章节请参考[多人视频通话](https://github.com/netease-im/Basic-Video-Call/blob/master/Group-Video/NERtcSample-GroupVideoCall-iOS-Objective-C/README.md)

## 功能实现

### 设置音频质量和模式

```objc
[[NERtcEngine sharedEngine] setAudioProfile:_profile scenario:_scenario];
```

注意 **setAudioProfile: scenario:** 设置音频质量和模式后，需重启本地音频能力使配置生效

```objc
// 重启本地音频能力使配置生效
NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
[coreEngine enableLocalAudio:NO];
[coreEngine enableLocalAudio:YES];
```

### 设置视频质量

```objc
NERtcVideoEncodeConfiguration *config = [[NERtcVideoEncodeConfiguration alloc] init];
config.maxProfile = _maxProfile;
[[NERtcEngine sharedEngine] setLocalVideoConfig:config];
```

注意 **setLocalVideoConfig:** 设置设置视频质量后，需重启本地视频能力使配置生效

```objc
// 重启本地视频能力使配置生效
NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
[coreEngine enableLocalVideo:NO];
[coreEngine enableLocalVideo:YES];
```




