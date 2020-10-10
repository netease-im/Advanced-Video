# NERtcSample-ExternalVideo-Android-Java

## 功能介绍

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现自定义视频采集。
- 将外部视频文件作为采集源

## 环境准备，运行示例项目，多人视频通话功能实现

这个开源示例项目基于多人视频通话，关于**环境准备**，**运行示例项目**，**功能实现**章节请参考[多人视频通话](https://github.com/netease-im/Basic-Video-Call/blob/master/Group-Video/NERtcSample-GroupVideoCall-Android-Java/README.md)

## 功能实现

### 使用外部视频源

在使用或者不使用外部视频源前，需要先关闭本地视频采集以及发送，待切换视频源后再开启本地视频采集以及发送

```java
    private void setExternalVideoSource(boolean enable) {
        // 关闭本地视频采集以及发送
        NERtcEx.getInstance().enableLocalVideo(false);
        // 使用外部视频源
        NERtcEx.getInstance().setExternalVideoSource(enable);
        // 开启本地视频采集以及发送
        NERtcEx.getInstance().enableLocalVideo(true);
    }
```

### 推送外部视频帧

在切换为使用外部视频源后，主动将视频帧数据用 **NERtcVideoFrame** 类封装后传递给 **SDK**

```java
import com.netease.lava.nertc.sdk.video.NERtcVideoFrame;

    private void pushExternalVideoFrame(NERtcVideoFrame videoFrame) {
        // 推送外部视频帧
        NERtcEx.getInstance().pushExternalVideoFrame(videoFrame);
    }
```

注意，当 **NERtcVideoFrame** 格式为 **NERtcVideoFrame.Format.TEXTURE_OES** 时，必须保证调用 **pushExternalVideoFrame** 的线程完成 [eglMakeCurrent](https://www.khronos.org/registry/EGL/sdk/docs/man/html/eglMakeCurrent.xhtml) 调用。
可以使用 **GLHelper.initGLContext** 完成初始化

### 生成外部视频帧

**NERtcVideoFrame** 支持多种格式 **NERtcVideoFrame.Format**，这个开源示例项目展示了 **NERtcVideoFrame.Format.TEXTURE_OES** 格式

#### 使用帮助类 **ExternalTextureVideoSource**

**ExternalTextureVideoSource** 使用文件路径作为输入，以循环模式读取视频文件，通过回调输出**NERtcVideoFrame.Format.TEXTURE_OES** 格式的 **NERtcVideoFrame**

```java
// 创建外部视频源，输入文件路径和视频帧处理回调
ExternalVideoSource externalVideoSource = ExternalTextureVideoSource.create(videoPath, this::pushExternalVideoFrame);
// 开始发送视频数据
externalVideoSource.start()
// 停止发送视频数据
externalVideoSource.stop()
```

#### 帮助类 **ExternalTextureVideoSource** 简要介绍

- **ExternalTextureVideoSource.create** 方法：通过 **MediaMetadataExtractor** 帮助类获得视频文件信息
- **ExternalTextureVideoSource.ensureGLHandler** 方法：创建一个Looper线程，并初始化EGL。
- **ExternalTextureVideoSource.start** 方法：创建一个Surface，并启动一个MediaPlayer循环播放视频
- **ExternalTextureVideoSource.stop** 方法：停止播放视频，销毁Surface，停止GL线程

#### 帮助类 **GLHelper** 说明
- **GLHelper.initGLContext** 方法：初始化当前线程的EGL Context，使用了 **SDK** 提供的帮助类，建议替换为自己的实现。
- **GLHelper.genTexture** 方法：生成纹理，该纹理在视频播放，**SDK** 读取纹理内容时会用到。





