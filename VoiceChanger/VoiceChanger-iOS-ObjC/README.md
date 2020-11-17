# VoiceChanger-iOS-ObjC

这个开源示例项目演示了如何快速集成 网易云信 新一代（NERtc）音视频 SDK 3.7.2及以上版本 ，实现变声&美声的功能。

在这个示例项目中包含了以下功能：

- 加入通话和离开通话
- 设置变声，混响以及均衡器，并支持自定义均衡器
- 切换大小屏显示远端/本地用户视频

### 环境准备

1. 这个开源示例项目基于一对一视频通话，关于云信**环境准备**，**运行示例项目**，**功能实现**章节请参考[1对1音视频通话](https://github.com/netease-im/Basic-Video-Call/blob/master/One-to-One-Video/NERtcSample-1to1-iOS-Objective-C/README.md)

2. 本例中 将AppKey填写在AppKey.h文件中

```objc
#define kAppKey @"<#请输入您的AppKey#>";
```
### 功能实现

1、引擎初始化。配置音视频相关参数。

2、设置 EQ

```objc
// 参数为枚举，结果为0表示设置成功
int res = [[NERtcEngine sharedEngine] setLocalVoiceEqualizationPreset:value];
```

3、 设置混音

```objc
// 参数为枚举，结果为0表示设置成功
int res = [[NERtcEngine sharedEngine] setLocalVoiceReverbPreset:value];
```

4、设置 变声/美声

```objc
// 参数为枚举，结果为0表示设置成功
int res = [[NERtcEngine sharedEngine] setLocalVoiceChangerPreset:value];
```

5、设置自定义 EQ

```objc
// 参数是: 元素为NSNumber的数组，依次序分别代表 32Hz、63Hz、125Hz、250Hz、500Hz、1kHz、2kHz、4kHz、8kHz、16kHz的设置值
// 结果为0表示设置成功
int res = [NETSDemoSoundConfig shared].customEqualization = [_viewModel.datas copy];
```

### 相关接口

| NERtc接口 | 参数说明 | 返回说明 | 接口说明 |
| - | - | - | - |
| setLocalVoiceEqualizationPreset: | NERtcVoiceEqualizationType 枚举值 | 0-成功, 否则失败 | EQ 预设值 |
| setLocalVoiceReverbPreset: | NERtcVoiceReverbType 枚举值 | 0-成功, 否则失败 | 设置预设的混响模式 |
| setLocalVoiceChangerPreset: | NERtcVoiceChangerType 枚举值 | 0-成功, 否则失败 | 设置预设的变声模式 |
| setLocalVoiceEqualizations: | 每个 band 的增益，数组大小为10，［０－９］分别代表 10 个频带，对应的中心频率是 [31，63，125，250，500，1k，2k，4k，8k，16k] Hz 单位是 dB，每一个值的范围是 [-15，15]，默认值为 0 | 0-成功, 否则失败 | 设置自定义的EQ |

### 页面主要流程

NETSDemoViewController(申请权限) -> NETSDemoCallVC(调用Nertc通话) -> 点击"设置声效", 调出变声&美声页面 -> NETSDemoSettingVC

### 文件结构及说明

1、NETSDemoCallVC为通话页面，在该页面点击 "设置声效" 唤起设置选项弹窗。点击不同弹窗进入不同的设置页面;

2、所有音效设置交互相关类在 Setting/ 目录下;

3、NETSDemoSettingVC: 音效设置页面，根据传入的 type(枚举值) 不同，实例化不同的设置页面；

4、NETSDemoSettingVM: 音效设置页 viewModel, 根据传入的 type(枚举值) 不同，实例化不同的设置页面的viewModel，用于控制与页面相关的数据逻辑；

5、NETSDemoSettingModel: 音效设置模型类。包含check及slide两种设置类型的模型；

6、NETSDemoSettingCell: 音效设置cell类。包含check及slide两种设置类型的cell视图；

7、NETSDemoSoundConfig: 音效设置配置项类。用户存储用户配置值。