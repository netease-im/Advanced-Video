# 自定义视频采集

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现自定义视频采集功能。

## 环境准备，运行示例项目，自定义视频采集功能实现

这个开源示例项目基于多人视频通话，关于**环境准备**，**运行示例项目**，**功能实现**章节请参考多人通话章节
## 功能实现

参考如下步骤，在你的项目中使用 Push 方式实现自定义视频源功能：

1. 通过调用 `setDevice` 指定特定`kNERtcExternalVideoDeviceID`外部视频采集设备,并且需要调用`setExternalVideoSource`为true，然后通过 `enableLocalVideo` 启动设备。如果原先已经开启了摄像头采集，会自动切换到外部设备采集，不需要再次调用 `enableLocalVideo`。
2. 指定外部采集设备后，开发者自行管理视频数据采集和处理。
3. 完成视频数据处理后，再通过`pushExternalVideoFrame`发送给 SDK 进行后续操作。为满足实际使用需求，你可以在将视频数据发送回 SDK 前，通过 `NERtcVideoFrame` 修改视频数据。比如，设置 `rotation` 为 180，使视频帧顺时针旋转 180 度。
4. window下若出现  [`DirectShowPlayerService::doRender: Unresolved error code 80040266`](https://github.com/qyvlik/Flat.qml/issues/4) ,按照issue解决即可。

## 示例代码

### 

```c++

    QString fileName = QFileDialog::getOpenFileName(this, tr("Open MP4 File"), ".", tr("Media Files (*.mp4)"));
  
    if (fileName != "") {
        auto ret = m_engine->setExternalVideoSource(true);  //设置使用自定义数据源
        m_engine->enableVideo(true);

        mediaPlayer->setVideoOutput(videoSurface);
        mediaPlayer->setMedia(QUrl::fromLocalFile(fileName));
        connect(videoSurface, &VideoSurface::frameAvailable, this, &VideoWindow::onFrameReady);
        mediaPlayer->play();
    }

```

### 






