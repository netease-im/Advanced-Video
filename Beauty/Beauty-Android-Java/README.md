# Beauty-Android-Java

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现美颜的功能。

 在这个示例项目中包含了以下功能：

- 加入通话和离开通话
-  打开美颜和关闭美颜
- 切换前置摄像头和后置摄像头
## 环境准备
包括云信环境和第三方美颜解决方案相芯环境
### 云信环境
1. 这个开源示例项目基于一对一视频通话，关于云信**环境准备**，**运行示例项目**，**功能实现**章节请参考[1对1音视频通话](https://github.com/netease-im/Basic-Video-Call/tree/master/One-to-One-Video/NERtcSample-1to1-Android-Java)

2. 将App Key填写进 "app/src/main/res/values/app_key.xml"

```
<!-- 替换为你自己的AppKey -->
<string name="app_key">YOUR APP KEY</string>
```

### 获取相芯SDK 的证书

1.由于本sample美颜的功能是使用相芯SDK实现的。所以您在使用前需要的获取相芯的证书。

2.复制authpack.java文件到com.faceunity包下

3.详细参考相芯[集成文档](https://github.com/Faceunity/FULiveDemoDroid/blob/master/docs/Android_Nama_SDK_%E9%9B%86%E6%88%90%E6%8C%87%E5%AF%BC%E6%96%87%E6%A1%A3.md)

## 功能实现

1.云信接口提供：

   ```
//设置视频采集数据回调，用于美颜等操作
        NERtcEx.getInstance().setVideoCallback(neRtcVideoFrame -> {
            if(openFilter) {
                //此处可自定义第三方的美颜实现
                neRtcVideoFrame.textureId = mFuRender.onDrawFrame(neRtcVideoFrame.data,neRtcVideoFrame.textureId,
                        neRtcVideoFrame.width,neRtcVideoFrame.height);
            }
            return openFilter;
        },true);
   ```

2.faceunity model为相芯美颜功能实现，您也可以替换成自己接入的第三方方案。相芯美颜功能具体参看[相芯接入文档](https://github.com/Faceunity/FULiveDemoDroid/blob/master/docs/Android_Nama_SDK_%E9%9B%86%E6%88%90%E6%8C%87%E5%AF%BC%E6%96%87%E6%A1%A3.md)
   

   


