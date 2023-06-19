# Beauty-Android-Java

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现商汤美颜的功能。

 在这个示例项目中包含了以下功能：

- 加入通话和离开通话
-  打开美颜和关闭美颜
- 切换前置摄像头和后置摄像头
## 环境准备
包括云信环境和第三方美颜解决方案商汤环境
### 云信环境
1. 这个开源示例项目基于一对一视频通话，关于云信**环境准备**，**运行示例项目**，**功能实现**章节请参考[1对1音视频通话](https://github.com/netease-im/Basic-Video-Call/tree/master/One-to-One-Video/NERtcSample-1to1-Android-Java)

2. 将App Key填写进 "app/src/main/res/values/app_key.xml"

```
<!-- 替换为你自己的AppKey -->
<string name="app_key">YOUR APP KEY</string>
```

### 获取商汤SDK 的证书

1.由于本sample美颜的功能是使用商汤SDK实现的。所以您在使用前需要的获取商汤的证书。

2.复制文SenseME.lic件到app的assets/license目录下

3.详细参考商汤提供的集成文档

## 功能实现

1.云信接口提供：

```
//设置视频采集数据回调，用于美颜等操作
NERtcEx.getInstance().setVideoCallback(videoFrame -> {
    //此处可自定义第三方的美颜实现
    videoFrame.textureId = preprocess(videoFrame.data,videoFrame.textureId,
            videoFrame.width,videoFrame.height);
    return true;
},true);
```

2.商汤美颜功能实现可参考sample code中MeetingActivity.java中的实现，您也可以替换成自己接入的第三方方案。商汤美颜功能实现具体参看商汤接入文档
