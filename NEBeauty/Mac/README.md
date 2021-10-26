# NeBeauty-Mac-Sample

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现美颜的功能。

## 环境准备

生产Xcode 工程，Mac/beauty_prj 目录执行命令 qmake -spec macx-xcode beauty_prj.pro，生成 1v1_beauty.xcodeproj

在这个示例项目中包含了以下功能：

- 加入通话和离开通话
- 打开美颜和关闭美颜
- 调整美颜功能和参数

本例中，需要将AppKey填写在engine.h文件中
const std::string app_key_ = "<#请输入您的AppKey#>";

## 功能实现
- 启动美颜模块

```
// 初始化美颜相关资源
[[NERtcBeauty shareInstance] startBeauty];

NSString *strBeauty = [[NSBundle mainBundle] pathForResource:kNEBeautyLocalFilePath ofType:@"json"];
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

- 设置美颜参数（总共 21 种，既可以通过 setBeautyEffectWithValue 方法进行设置

```
[[NERtcBeauty shareInstance] setBeautyEffectWithValue:level atType:type];
.........
```

- 滤镜相关内容

```
// 添加滤镜
[[NERtcBeauty shareInstance] addBeautyFilterWithPath:localModelPath andName:@"template.json"];

// 调整滤镜强度
[NERtcBeauty shareInstance].filterStrength = _filterStrength;

// 移除滤镜
[[NERtcBeauty shareInstance] removeBeautyFilter];
```

- 贴纸相关内容

```
// 添加贴纸
[[NERtcBeauty shareInstance] addBeautyStickerWithPath:localModelPath andName:@"template.json"];

// 移除贴纸
[[NERtcBeauty shareInstance] removeBeautySticker];
```

- 美妆相关内容

```
// 添加美妆
[[NERtcBeauty shareInstance] addBeautyMakeupWithPath:localModelPath andName:@"template.json"];

// 移除美妆
[[NERtcBeauty shareInstance] removeBeautyMakeup];
```

