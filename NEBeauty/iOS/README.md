# Beauty-iOS-ObjC

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现美颜的功能。

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

## 功能实现

与美颜相关的所有功能在 NERtcBeauty.h 中，接口调用规则与 NERtcSDK 其他接口相同，必须在成功调用了 setupEngineWithContext 之后才能调用。

与美颜相关的所有资源在 demo 的 Beauty-iOS-ObjC/NEBeauty/Resource 目录下，具体的使用方式参考 NEBeautyManager.m 即可。

- 启动美颜模块

```
// 初始化美颜相关资源
[[NERtcBeauty shareInstance] startBeauty];
    
// 需要加载beauty目录下的template.json文件
NSString *strBeauty = [[NSBundle mainBundle] pathForResource:@"beauty/template" ofType:@"json"];
NSString *strBeautyPath = [strBeauty stringByDeletingLastPathComponent];
NSString *dir = [strBeautyPath stringByAppendingString:@"/"];
NSString *templateName = @"template.json";
[[NERtcBeauty shareInstance] addTempleteWithPath:dir andName:templateName];
```

- 销毁美颜模块

```
[[NERtcBeauty shareInstance] stopBeauty];
```

- 开启/关闭美颜

```
[NERtcBeauty shareInstance].isOpenBeauty = enable;
```

- 设置美颜参数（总共 21 种，既可以通过 setBeautyEffectWithValue 方法进行设置，也可以通过独立的property 进行设置）

```
// 第1种方式
- (void)setBeautyEffectWithValue:(float)value atType:(NERtcBeautyEffectType)type;

// 第2种方式
[NERtcBeauty shareInstance].teeth = value;
[NERtcBeauty shareInstance].brightEye = value;
[NERtcBeauty shareInstance].whiteSkin = value;
[NERtcBeauty shareInstance].smoothSkin = value;
[NERtcBeauty shareInstance].smallNose = value;
.........
```

- 滤镜相关内容

```
// 添加滤镜
[[NERtcBeauty shareInstance] addBeautyFilterWithPath:model.resourcePath andName:@"template.json"];

// 调整滤镜强度
[NERtcBeauty shareInstance].filterStrength = _filterStrength;

// 移除滤镜
[[NERtcBeauty shareInstance] removeBeautyFilter];
```

- 贴纸相关内容

```
// 添加贴纸
[[NERtcBeauty shareInstance] addBeautyStickerWithPath:model.resourcePath andName:@"template.json"];

// 移除贴纸
[[NERtcBeauty shareInstance] removeBeautySticker];
```

- 美妆相关内容

```
// 添加美妆
[[NERtcBeauty shareInstance] addBeautyMakeupWithPath:model.resourcePath andName:@"template.json"];

// 移除美妆
[[NERtcBeauty shareInstance] removeBeautyMakeup];
```