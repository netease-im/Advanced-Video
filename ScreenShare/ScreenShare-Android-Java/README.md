# NERtcSample-ScreenShare-Android-Java

## 功能介绍

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现屏幕共享。
- 将屏幕录制作为采集源

## 环境准备，运行示例项目，多人视频通话功能实现

这个开源示例项目基于多人视频通话，关于**环境准备**，**运行示例项目**，**功能实现**章节请参考[多人视频通话](https://github.com/netease-im/Basic-Video-Call/blob/master/Group-Video/NERtcSample-GroupVideoCall-Android-Java/README.md)

### 开发注意事项

- **MediaProjection** 等 API 需要 Android API level 21+，相关的使用方法请参考[Google MediaProjection API 文档](https://developer.android.com/reference/android/media/projection/MediaProjection)。
- **Android 10** 及以后的版本屏幕共享系统要求开启一个前台服务，因此需要在AndroidManifest.xml中添加以下service，同时将 **compileSdkVersion** 设置为 29及以上。

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" /><!-- 添加前台服务权限-->

<application>
    <service
        android:name="com.netease.lava.video.device.screencapture.ScreenShareService"
        android:foregroundServiceType="mediaProjection">
        <intent-filter>
            <action android:name="com.netease.Yunxin.ScreenShare" />
        </intent-filter>
    </service>
</application>
```

## 功能实现

### 开启屏幕共享

- 选择清晰度，**NERtcConstants.ScreenProfile** 提供清晰度定义
- 获得屏幕录制请求结果Intent，具体查看[请求屏幕录制](#request_screen_capture)
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

<span id="request_screen_capture"></span>
### 请求屏幕录制

创建屏幕录制Intent

```java
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Intent createScreenCaptureIntent(Context context) {
        MediaProjectionManager manager =
                (MediaProjectionManager) context.getSystemService(
                        Context.MEDIA_PROJECTION_SERVICE);
        return manager.createScreenCaptureIntent();
    }
```

请求屏幕录制，并获取结果，如果成功，调用 **startScreenCapture** 传入结果Intent

```java
    startActivityForResult(createScreenCaptureIntent(context), REQUEST_CODE_SCREEN_CAPTURE);

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