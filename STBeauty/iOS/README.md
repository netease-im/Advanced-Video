# Beauty-iOS-ObjC

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现商汤美颜的功能。

在这个示例项目中包含了以下功能：

- 加入通话和离开通话
- 打开美颜和关闭美颜
- 调整美颜功能和参数

## 环境准备

- 这个开源示例项目基于一对一视频通话，关于一对一视频通话的**环境准备**，**运行示例项目**，**功能实现**请参考[1对1音视频通话](https://github.com/netease-im/Basic-Video-Call/blob/master/One-to-One-Video/NERtcSample-1to1-iOS-Objective-C/README.md)

- 本例中，需要将AppKey填写在AppKey.h文件中

```
#define kAppKey @"<#请输入您的AppKey#>";
```

- 联系商汤技术支持，获取商汤美颜 SDK 与 license

## 工程配置
- 导入必要资源
	- 将 licence 文件放入 Beauty-iOS-ObjC/ST_SDK/license 目录
	- 将头文件放入 Beauty-iOS-ObjC/ST_SDK/include 目录
	- 将 SDK 放入 Beauty-iOS-ObjC/ST_SDK/libs/ios\_os-universal 目录
	- 将模型文件放入 Beauty-iOS-ObjC/ST_SDK/models 目录
- 在 Beauty-iOS-ObjC 目录执行 pod install 安装依赖项
- 进入 Beauty-iOS-ObjC 目录，双击 Beauty-iOS-ObjC.xcworkspace 打开工程

## 功能实现

与美颜相关的所有功能实现位于 NEBeautyManager.mm

- 初始化美颜模块

```
- (void)initSTSDK {
    // 鉴权
    NSString* licensePath = [[NSBundle mainBundle] pathForResource:@"license" ofType:@"lic"];
    BOOL result = [EffectsProcess authorizeWithLicensePath:licensePath];
    if (!result) {
        NSLog(@"***** error: license is invalid *****");
    }
    
    // 初始化 EAGLContext
    self.glContext = [[EAGLContext alloc] initWithAPI:kEAGLRenderingAPIOpenGLES3];
    
    // 初始化 EffectProcess
    self.stEffectProcess = [[EffectsProcess alloc] initWithType:EffectsTypePreview glContext:self.glContext];
    
    // 添加 model
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        self.stModelLoaded = NO;
        NSString* modelPath = [[NSBundle mainBundle] pathForResource:@"model" ofType:@"bundle"];
        [self.stEffectProcess setModelPath:modelPath];
        self.stModelLoaded = YES;
    });
}
```

- 销毁美颜模块

```
- (void)destroySTSDK {
    self.stModelLoaded = NO;
    self.stEffectProcess = nil;
    self.glContext = nil;
    
    _currentFrameWidth = 0;
    _currentFrameHeight = 0;
    
    if (_outTexture) {
        _outTexture = 0;
        
        CVPixelBufferRelease(_outputPixelBuffer);
        _outputPixelBuffer = NULL;
        
        CFRelease(_outputCVTexture);
        _outputCVTexture = NULL;
    }
    
    if (_outputBuffer) {
        free(_outputBuffer);
        _outputBuffer = NULL;
    }
}
```

- 在 NERtcSDK 的视频采集回调中做美颜处理

```
参考方法 - (void)processCapturedVideoFrameWithPixelBuffer:(CVPixelBufferRef)pixelBuffer rotation:(NERtcVideoRotationType)rotation
```

- 设置美颜参数

```
// 示例：美白
[self.stEffectProcess setEffectType:EFFECT_BEAUTY_BASE_WHITTEN value:value];

// 示例：磨皮
[self.stEffectProcess setEffectType:EFFECT_BEAUTY_BASE_FACE_SMOOTH value:value];
```