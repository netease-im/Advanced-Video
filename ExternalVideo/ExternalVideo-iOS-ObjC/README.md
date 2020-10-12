# ExternalVideo-iOS-Objc

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现自定义视频采集。

## 环境准备

- Xcode 10.0+
- iOS真机设备
- 支持模拟器运行，但是部分功能无法使用

## 运行示例项目

这个段落主要讲解了如何编译和运行实例程序。

#### 获取AppKey

在编译和启动实例程序前，您需要首先获取一个可用的App Key：

1. 若您已经与专属客户经理取得联系，可直接向他获取Appkey

2. 若您并未与专属客户经理取得联系那么请按后续步骤获取Appkey

3. 首先在 [网易云信](https://id.163yun.com/register?h=media&t=media&clueFrom=nim&from=bdjjnim0035&referrer=https://app.yunxin.163.com/?clueFrom=nim&from=bdjjnim0035) 注册账号

4. 然后在「应用」一栏中创建您的项目
5. 等待专属客户经理联系您，并向他获取Appkey

6. 将AppKey填写进NTESAppConfig.h

```objective-c
#define kAppKey @"<#请输入您的AppKey#>"
```

#### 集成实时音视频SDK

1. 进入Demo根路径，执行`pod install`
2. 使用Xcode打开NERtcSample-GroupVideoCall-iOS-Objective-C.xcworkspace，连接iPhone/iPad测试设备，设置有效的开发者签名后即可运行

## 功能实现

1. 初始化。

   ```objective-c
   - (void)setupRTCEngine {
       NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
       NERtcEngineContext *context = [[NERtcEngineContext alloc] init];
       context.engineDelegate = self;
       context.appKey = kAppKey;
       [coreEngine setupEngineWithContext:context];
       [coreEngine setExternalVideoSource:YES];
       [coreEngine enableLocalAudio:YES];
       [coreEngine enableLocalVideo:YES];
   }
   ```

   


2. 选取视频源

   ```objective-c
   - (IBAction)onClickSelectVideo:(id)sender {
       UIImagePickerController *imagePicker = [[UIImagePickerController alloc] init];
       imagePicker.delegate = self;
       imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
       imagePicker.mediaTypes = @[(__bridge NSString *)kUTTypeMovie, (__bridge NSString *)kUTTypeVideo];
       [self presentViewController:imagePicker animated:YES completion:nil];
   }
   ```



3. 使用`NTESExternalVideoReader`开始读取视频帧

```objective-c
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<UIImagePickerControllerInfoKey,id> *)info {
    [self dismissViewControllerAnimated:YES completion:nil];
    NSURL *videoURL = info[UIImagePickerControllerMediaURL];
    if (videoURL) {
        NSError *error;
        self.videoReader = [[NTESExternalVideoReader alloc] initWithURL:videoURL error:&error];
        if (error) {
            NSLog(@"Create video reader error: \n%@", error.localizedDescription);
            return;
        }
        self.videoReader.delegate = self;
        [self.videoReader startReading];
    }
}
```



4. 在收到视频帧后使用`pushExternalVideoFrame:`推送视频帧

```objective-c
- (void)videoReader:(NTESExternalVideoReader *)videoReader didReadSampleBuffer:(CMSampleBufferRef)sampleBuffer totalFramesWritten:(NSInteger)totalFramesWritten totalFrames:(NSInteger)totalFrames {
    CVImageBufferRef pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer);
    NERtcVideoFrame *frame = [[NERtcVideoFrame alloc] init];
    frame.format = kNERtcVideoFormatNV12;
    frame.width = (uint32_t)CVPixelBufferGetWidth(pixelBuffer);
    frame.height = (uint32_t)CVPixelBufferGetHeight(pixelBuffer);
    frame.buffer = (void *)pixelBuffer;
    switch (videoReader.rotationDegree) {
        case 90:
            frame.rotation = kNERtcVideoRotation_90;
            break;
        case 180:
            frame.rotation = kNERtcVideoRotation_180;
            break;
        case 270:
            frame.rotation = kNERtcVideoRotation_270;
            break;
        case 0:
        default:
            frame.rotation = kNERtcVideoRotation_0;
            break;
    }
    [NERtcEngine.sharedEngine pushExternalVideoFrame:frame];
}

```

