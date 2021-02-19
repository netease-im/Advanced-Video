# NERtcSample-ScreenShare-Android-Java

## 功能介绍

这个开源示例项目演示了如何快速集成网易云信新一代（G2）音视频 SDK，并实现屏幕共享。

## 环境准备，运行示例项目，多人视频通话功能实现

这个开源示例项目基于多人视频通话，关于**环境准备**，**运行示例项目**，**功能实现**章节请参考[多人视频通话](https://github.com/netease-im/Basic-Video-Call/blob/master/Group-Video/NERtcSample-GroupVideoCall-Android-Java/README.md)

### 开发注意事项

- **MediaProjection** 等 API 需要 Android API level 21+，相关的使用方法请参考[Google MediaProjection API 文档](https://developer.android.com/reference/android/media/projection/MediaProjection)。
- **Android 10** 及以后的版本屏幕共享系统要求开启一个前台服务，因此需要自行添加一个前台Server(参考工程示例代码`SimpleScreenShareService`)并在AndroidManifest.xml做相关配置，同时将 **compileSdkVersion** 设置为 29及以上。

```xml
<!-- 添加前台服务权限-->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />

<application>
    <service
            android:name="xxx.SimpleScreenShareService"
            android:foregroundServiceType="mediaProjection">
            <intent-filter>
                <action android:name="com.netease.Yunxin.ScreenShare" />
            </intent-filter>
        </service>
</application>
```

## 功能实现

### 开启屏幕共享

- 选择清晰度，**NERtcConstants.VideoProfile** 提供清晰度定义
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
        
        NERtcScreenConfig screenConfig = new NERtcScreenConfig();
        // 选择屏幕共享清晰度
        screenProfile.videoProfile = NERtcConstants.VideoProfile.HD1080p;
        // 选择屏幕共享帧率
        screenProfile.frameRate = NERtcEncodeConfig.NERtcVideoFrameRate.FRAME_RATE_FPS_15;

        // 开启屏幕共享
        int result = NERtcEx.getInstance().startScreenCapture(screenConfig,       mediaProjectionPermissionResultData,mediaProjectionCallback);

    }
```

注意，从3.9.0开始，屏幕共享与视频可以同时开启，两个互不干扰， **startScreenCapture**、**stopScreenCapture** 用于开关屏幕共享，  **enableLocalVideo()**，用于开关视频。

### 停止屏幕共享

```java
    private void stopScreenCapture() {
        // 停止屏幕共享
        NERtcEx.getInstance().stopScreenCapture();
    
    }
```

<span id="request_screen_capture"></span>
### 请求屏幕录制

创建屏幕录制Intent

```java
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Intent createScreenCaptureIntent(Context context) {
        MediaProjectionManager manager =(MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
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