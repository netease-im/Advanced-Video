# NERtcSample-ScreenShare-Android-Java

## 功能介绍

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现屏幕共享。
- 将屏幕录制作为采集源

注意，该功能要求最小API版本为21

## 环境准备，运行示例项目，多人视频通话功能实现

这个开源示例项目基于多人视频通话，关于**环境准备**，**运行示例项目**，**功能实现**章节请参考[README.md](https://github.com/netease-im/Basic-Video-Call/blob/master/Group-Video/NERtcSample-GroupVideoCall-Android-Java/README.md)

## 功能实现

### 开启屏幕共享

- 选择清晰度，**NERtcConstants.ScreenProfile** 提供清晰度定义
- 获得屏幕录制请求结果Intent，具体查看**请求屏幕录制**章节
- 设置屏幕录制回调，用于接收录制结束事件

```java
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startScreenCapture(Intent mediaProjectionPermissionResultData) {
        // 屏幕录制回调
        final MediaProjection.Callback mediaProjectionCallback = new MediaProjection.Callback() {
            @Override
            public void onStop() {
                super.onStop();
            }
        };
        // 选择屏幕共享清晰度
        int screenProfile = ScreenProfile.HD1080p;
        // 开启屏幕共享
        int result = NERtcEx.getInstance().startScreenCapture(screenProfile,
                mediaProjectionPermissionResultData, // 屏幕录制请求返回的Intent
                mediaProjectionCallback);
    }
```

注意，调用 **startScreenCapture** API不需要先调用 **enableLocalVideo(false)**，如果启动成功，SDK会自动停止本地视频采集。

### 停止屏幕共享

```java
    private void stopScreenCapture() {
        // 停止屏幕共享
        NERtcEx.getInstance().stopScreenCapture();
        // 开启本地视频采集以及发送
        NERtcEx.getInstance().enableLocalVideo(true);
    }
```

注意，调用 **stopScreenCapture** API后需要调用 **enableLocalVideo(true)**，重新启动本地视频采集。

### 请求屏幕录制

创建屏幕录制Intent

```java
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Intent createScreenCaptureIntent() {
        MediaProjectionManager manager =
                (MediaProjectionManager) getApplication().getSystemService(
                        Context.MEDIA_PROJECTION_SERVICE);
        return manager.createScreenCaptureIntent();
    }
```

请求屏幕录制，并获取结果，如果成功，调用 **startScreenCapture** 传入结果Intent

```java
    startActivityForResult(createScreenCaptureIntent(), REQUEST_CODE_SCREEN_CAPTURE);

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SCREEN_CAPTURE) {
            if (resultCode == RESULT_OK) {
                startScreenCapture(data);
            }
        }
    }
```