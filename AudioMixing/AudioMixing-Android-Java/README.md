# AudioMixing-Android-Java

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现通话过程中混音效果功能。

## 环境准备，运行示例项目，多人视频通话功能实现

这个开源示例项目基于多人视频通话，关于**环境准备**，**运行示例项目**，**功能实现**章节请参考[多人视频通话](https://github.com/netease-im/Basic-Video-Call/blob/master/Group-Video/NERtcSample-GroupVideoCall-Android-Java/README.md)
## 功能实现

如果需要实现混音音效，参考如下步骤，在你的项目中实现播放混音文件：

1. 在加入频道成功后调用 startAudioMixing 开始混音。
2. 根据 pauseAudioMixing、resumeAudioMixing 等操作 进行 暂停、恢复、控制音量、定位 等功能。
3. 在离开频道前调用 stopAudioMixing 结束混音。
<br>**目前只支持SD卡上的本地文件路径。**

## 示例代码

### 混音(Mixing)效果实现：
```java
NERtcCreateAudioMixingOption option = new NERtcCreateAudioMixingOption();
option.path = musicPathArray[musicIndex];//设置文件地址
option.playbackVolume = audioMixingVolume;//播放音量
option.sendVolume = audioMixingVolume;//发送音量
option.loopCount = 1;//循环次数
result = NERtcEx.getInstance().startAudioMixing(option);
```

### 音效(Effect)实现
```java
NERtcCreateAudioEffectOption option = new NERtcCreateAudioEffectOption();
option.path = effectPathArray[index];//设置文件地址
option.playbackVolume = audioEffectVolume;//播放音量
option.sendVolume = audioEffectVolume;//发送音量
option.loopCount = 1;//循环次数
neRtcEx.stopEffect(index2Id(index));
return neRtcEx.playEffect(index2Id(index),option) == 0;
```

## 混音（Mixing）与音效(Effect)区别
混音同一时刻只能播放一首音乐，音效可以播放多首，并设置不同的Id管理。




