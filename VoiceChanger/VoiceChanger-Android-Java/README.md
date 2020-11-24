# VoiceChanger-Android-Java

这个开源示例项目演示了如何快速集成 网易云信 新一代（NERtc）音视频 SDK，实现变声&美声的功能，此功能已经内置到音视频 SDK 中，不需要引入三方包。

 在这个示例项目中包含了以下功能：

- 加入通话和离开通话
-  设置变声，混响以及均衡器，并支持自定义均衡器
- 切换前置摄像头和后置摄像头
## 云信环境准备
1. 这个开源示例项目基于一对一视频通话，关于云信**环境准备**，**运行示例项目**，**功能实现**章节请参考[1对1音视频通话](https://github.com/netease-im/Basic-Video-Call/tree/master/One-to-One-Video/NERtcSample-1to1-Android-Java)

2. 本例中将 `AppKey`填写在`config.cpp`文件中

   ```c++
   std::string appKey = "set you APP key here";
   ```
## 涉及 NERtc Sdk 接口

**表中接口都通过 NERtcEx.getInstance() 调用**

| NERtc Sdk 接口                  | 参数说明                           | 返回说明         | 接口说明         |
| ------------------------------- | ---------------------------------- | ---------------- | ---------------- |
| setLocalVoiceChangerPreset      | NERtcVoiceChangerType（枚举）      | 0 成功，否则失败 | 设置预置变声     |
| setLocalVoiceReverbPreset       | NERtcVoiceReverbType（枚举）       | 0 成功，否则失败 | 设置预置混响     |
| setLocalVoiceEqualizationPreset | NERtcVoiceEqualizationType（枚举） | 0 成功，否则失败 | 设置预置均衡器   |
| setLocalVoiceEqualizations      | int[] (int 数组，大小固定为 10)    | 0 成功，否则失败 | 设置自定义均衡器 |

## 功能实现

1. 应用在启动后（`MainActivity` 页面）申请必要权限如下：

    ```java
    android.permission.RECORD_AUDIO // 录音，用户音频采集
    android.permission.CAMERA // 相机，用于视频采集
    android.permission.WRITE_EXTERNAL_STORAGE // 读写外部存储，用于本地缓存
    ```

2. 具体变声功能使用可以直接参考项目 `com.netease.nmc.nertcsample.voicechanger.voice.VoiceChangerHelper` 实现，其中封装了四个接口，分别对应上述 sdk 接口；

2. 其中 UI 包内容为 demo 中UI展示内容，不涉及到具体 sdk 使用逻辑。

## 页面主要流程

`MainActivity`(申请权限) -> `MeetingActivity`(调用Nertc通话) -> `VoiceConfigDialog`(调出变声&美声页面) -> `VoiceChangerHelper`(设置变声&美声)



