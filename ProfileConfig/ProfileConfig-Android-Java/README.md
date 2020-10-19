# NERtcSample-ProfileConfig-Android-Java

## 功能介绍

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现音视频管理。

## 环境准备，运行示例项目，多人视频通话功能实现

这个开源示例项目基于多人视频通话，关于**环境准备**，**运行示例项目**，**功能实现**章节请参考[多人视频通话](https://github.com/netease-im/Basic-Video-Call/blob/master/Group-Video/NERtcSample-GroupVideoCall-Android-Java/README.md)

## 功能实现

### 设置音频质量和模式

注意 **NERtc.setAudioProfile** API必须在 **NERtc.init** API前调用才有效

```java
    private void setAudioProfile() {
        // 设置音频场景与模式，必须在 init 前设置有效。
        NERtc.getInstance().setAudioProfile(audioProfile, audioScenario);
    }
```

### 设置视频质量

注意 **NERtc.setLocalVideoConfig** API在 **NERtc.joinChannel** API前调用

```java
    private void setVideoProfile() {
        NERtcVideoConfig config = new NERtcVideoConfig();
        // 设置视频质量
        config.videoProfile = videoProfile;
        NERtc.getInstance().setLocalVideoConfig(config);
    }
```

如果调整视频质量，需要调用 **NERtc.enableLocalVideo(false)**, 设置，再调用 **NERtc.enableLocalVideo(true)**

```java
    private void onVideoConfigChange() {
        // 关闭本地视频采集以及发送
        NERtc.getInstance().enableLocalVideo(false);
        setVideoProfile();
        // 开启本地视频采集以及发送
        NERtc.getInstance().enableLocalVideo(true);
    }
```




