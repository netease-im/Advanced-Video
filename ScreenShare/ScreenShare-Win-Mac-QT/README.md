# NERtcSample-ScreenShare-Android-Java

## 功能介绍

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现屏幕共享。
- 将屏幕录制作为采集源

## 环境准备，运行示例项目，多人视频通话功能实现

这个开源示例项目基于多人视频通话，关于**环境准备**，**运行示例项目**，**功能实现**章节请参考[多人视频通话](https://github.com/netease-im/Basic-Video-Call/blob/master/Group-Video/NERtcSample-GroupVideoCall-Android-Java/README.md)



## 功能实现

### 开启屏幕共享



```java
bool NRTCEngine::startMonitorShare(const uint32_t &screenIndex)
{
#ifdef Q_OS_WIN32
    auto screens = QGuiApplication::screens();
    auto screen =  screens.at(screenIndex);

    NERtcRectangle sourceRectangle;
    sourceRectangle.x = screen->geometry().x();
    sourceRectangle.y = screen->geometry().y();
    sourceRectangle.width = screen->geometry().width() * screen->devicePixelRatio();
    sourceRectangle.height = screen->geometry().height() * screen->devicePixelRatio();

    NERtcRectangle regionRectangle = { 0, 0, 0, 0 };

    NERtcScreenCaptureParameters params;
    params.bitrate = 0;
    params.frame_rate = 5;
    params.profile = kNERtcScreenProfileMAX;
    params.capture_mouse_cursor = true;
    params.dimensions.width = sourceRectangle.width - sourceRectangle.x;
    params.dimensions.height = sourceRectangle.height - sourceRectangle.y;

    //共享屏幕前 必须关闭视频
    rtc_engine_->enableLocalVideo(false);
    return rtc_engine_->startScreenCaptureByScreenRect(sourceRectangle, regionRectangle, params) == 0;
#else
    NERtcRectangle regionRectangle = { 0, 0, 0, 0 };

    NERtcScreenCaptureParameters params;
    params.bitrate = 0;
    params.frame_rate = 5;
    params.profile = kNERtcScreenProfileMAX;
    params.capture_mouse_cursor = true;
    params.dimensions.width = regionRectangle.width - regionRectangle.x;
    params.dimensions.height = regionRectangle.height - regionRectangle.y;

    //共享屏幕前 必须关闭视频
    rtc_engine_->enableLocalVideo(false);

    return rtc_engine_->startScreenCaptureByDisplayId(screenIndex,regionRectangle,params) == 0;
#endif
}
```

注意，调用 startMonitorShare API不需要先调用 **enableLocalVideo(false)**，如果启动成功，SDK会自动停止本地视频采集。

### 停止屏幕共享

```java
void NRTCEngine::stopShare()
{
    rtc_engine_->stopScreenCapture();
    autoStartVideo();
    autoStartAudio();
}
```

注意，调用 stopShare API后需要调用 **enableLocalVideo(true)**，重新启动本地视频采集。





