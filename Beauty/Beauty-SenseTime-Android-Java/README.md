# Beauty-Android-Java

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现美颜的功能。

 在这个示例项目中包含了以下功能：

- 加入通话和离开通话
-  打开美颜和关闭美颜
- 切换前置摄像头和后置摄像头
## 环境准备
包括云信环境和第三方美颜解决方案商汤环境
### 云信环境
1. 这个开源示例项目基于一对一视频通话，关于云信**环境准备**，**运行示例项目**，**功能实现**章节请参考[1对1音视频通话](https://github.com/netease-im/Basic-Video-Call/tree/master/One-to-One-Video/NERtcSample-1to1-Android-Java)
2. 本例中将 `AppKey`填写在`config.cpp`文件中
```c++
std::string appKey = "set you APP key here";
```
### 获取商汤 SDK 的证书

1.由于本 sample 美颜的功能是使用商汤 SDK 实现的。所以您在使用前需要的获取商汤的证书。

2.将商汤授权证书 lic 文件修改名称为 `SenseME.lic` 并复制到工程 `assets` 目录下。

3.详细参考[商汤官网](https://www.sensetime.com/cn)。

## 功能实现

1.云信接口提供：

```java
/**
 * 每帧视频回调，此处对每帧视频进行美颜处理
 */
private NERtcVideoCallback videoCallback = neRtcVideoFrame -> {
    	// 也可以在此处替换成其他美颜库
        if (needBeautify) {
            neRtcVideoFrame.textureId =
                    senseTimeEffect.effect(neRtcVideoFrame.textureId, neRtcVideoFrame.width, neRtcVideoFrame.height);
            neRtcVideoFrame.format = NERtcVideoFrame.Format.TEXTURE_RGB;
        }
        return needBeautify;
    };
```

2. `sensetime` module 为商汤美颜功能部分封装，您也可以替换成自己接入的第三方方案。商汤美颜功能具体参看[商汤官网](https://www.sensetime.com/cn)