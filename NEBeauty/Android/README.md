# Beauty-Android-Java

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现美颜的功能。

 在这个示例项目中包含了以下功能：

- 加入通话和离开通话
-  打开美颜和关闭美颜
- 切换前置摄像头和后置摄像头
## 环境准备
1. 这个开源示例项目基于一对一视频通话，关于云信**环境准备**，**运行示例项目**，**功能实现**章节请参考[1对1音视频通话](https://github.com/netease-im/Basic-Video-Call/tree/master/One-to-One-Video/NERtcSample-1to1-Android-Java)

2. 将App Key填写进 "app/src/main/res/values/app_key.xml"

```
<!-- 替换为你自己的AppKey -->
<string name="app_key">YOUR APP KEY</string>
```
3. 云信美颜提供美白、磨皮、红润、锐化四种免费特效,滤镜及高级特效提供2分钟的试用体验,如果要使用高级特效及滤镜功能,请联系客户经理开通相关权限.

## 功能实现

1.云信美颜接口提供：

```java
/**
 * 开启美颜功能模块。
 * 
 * - 调用此接口后，开启美颜引擎。如果后续不再需要使用美颜功能，可以调用 stopBeauty 结束美颜功能模块，销毁美颜引擎并释放资源。
 * - 开启美颜功能模块后，默认开启全局美颜效果，您也可以通过 `setBeautyEffect` 或其他滤镜、贴纸相关接口设置美颜、滤镜等效果。
 *
 * @note 该方法需要在 `enableLocalVideo` 之前设置。
 * @return
 * - {@code 0}: 方法调用成功。
 * - 其他: 方法调用失败。
 */
NERtcEx.getInstance().startBeauty();

/**
 * 结束美颜功能模块。
 *
 * 如果后续不再需要使用美颜功能，可以调用 `stopBeauty` 结束美颜功能模块，SDK 会自动销毁美颜引擎并释放资源。
 */
NERtcEx.getInstance().stopBeauty();

/**
 * 开启或关闭美颜功能。
 *
 * 美颜功能默认为禁用状态，您可以调用此接口开启或关闭美颜功能。启用美颜功能之后，默认开启全局美颜效果，您也可以通过 `setBeautyEffect` 调整美颜效果，或通过相关方法增加滤镜、贴纸、美妆等效果。
 *
 * 美颜功能关闭后，包括全局美颜、滤镜、贴纸和美妆在内的所有美颜效果都会暂时关闭，直至重新启用美颜功能。
 * @note 该方法需要在 `startBeauty` 之后调用。
 * @param enabled 是否暂停美颜功能。默认为 `false`，表示不启用美颜功能。`true` 表示启用美颜功能。
 */
NERtcEx.getInstance().enableBeauty(boolean enabled);

/**
 * 设置美颜类型和强度。
 * 
 * - 此方法可用于设置磨皮、美白、大眼等多种全局美颜类型。
 * - 多次调用此接口可以叠加多种全局美颜效果，也可以通过相关方法叠加滤镜、贴纸、美妆等自定义效果。
 * 
 * @param beautyType 美颜类型。详细信息请参考 {@link NERtcBeautyEffectType}。
 * @param level      对应美颜类型的强度。取值范围为 [0, 1]，各种美颜效果的默认值不同。
 * @return
 * - {@code 0}: 方法调用成功。
 * - 其他: 方法调用失败。
 */
NERtcEx.getInstance().setBeautyEffect(NERtcBeautyEffectType beautyType, float level);

/**
 * 添加滤镜效果。
 * 
 * 此接口用于加载滤镜资源，并添加对应的滤镜效果。需要更换滤镜时，重复调用此接口使用新的滤镜资源即可。
 * 
 * @note
 * - 使用滤镜、贴纸和美妆等自定义美颜效果之前，请联系商务经理获取美颜资源或模型。
 * - 滤镜效果可以和全局美颜、贴纸、美妆等效果互相叠加，但是不支持叠加多个滤镜。 
 * 
 * @param path 滤镜资源或模型所在路径。支持 SD 卡上的绝对路径，或 asset 目录下的相对路径。
 * - SD卡："/storage/emulated/0/Android/data/com.netease.lava.nertc.demo/files/filter_portrait/filter_style_FN1"。
 * - asset: "2D/bunny"。
 * 
 * @return
 * - {@code 0}: 方法调用成功。
 * - 其他: 方法调用失败。
 */
NERtcEx.getInstance().addBeautyFilter(String path);

/**
 * 取消滤镜效果。
 */
NERtcEx.getInstance().removeBeautyFilter();

/**
 * 设置滤镜强度。
 * 
 * 取值越大，滤镜强度越大，开发者可以根据业务需求自定义设置滤镜强度。
 * 
 * 滤镜强度设置实时生效，更换滤镜后滤镜强度不变，如需调整，可以再次调用此接口重新设置滤镜强度。
 * @param level 滤镜强度。取值范围为 [0 - 1]，默认值为 0.5。
 * @return 
 * - {@code 0} 方法调用成功
 * - 其他值：调用失败
 */
NERtcEx.getInstance().setBeautyFilterLevel(float level);

/**
 * 添加贴纸效果。
 *
 * 此接口用于加载贴纸资源，添加对应的贴纸效果。需要更换贴纸时，重复调用此接口使用新的贴纸资源即可。
 *
 * @note 
 * - 使用滤镜、贴纸和美妆等自定义美颜效果之前，需要先准备好对应的美颜资源或模型。
 * - 贴纸效果可以和全局美颜、滤镜、美妆等效果互相叠加，但是不支持叠加多个贴纸。
 * 
 * @param path 贴纸资源所在路径。支持 SD 卡上的绝对路径，或 asset 目录下的相对路径。
 * - SD卡："/storage/emulated/0/Android/data/com.netease.lava.nertc.demo/files/filter_portrait/filter_style_FN1"
 * - asset: "2D/bunny"
 */
NERtcEx.getInstance().addBeautySticker(String path);

/**
 * 取消贴纸效果。
 */
NERtcEx.getInstance().removeBeautySticker();

/**
 * 添加美妆效果。
 *
 * 此接口用于加载美妆模型，添加对应的美妆效果。需要更换美妆效果时，重复调用此接口使用新的美妆模型即可。
 * 
 * @note 
 * - 使用滤镜、贴纸和美妆等自定义美颜效果之前，需要先准备好对应的美颜资源或模型。
 * - 美妆效果可以和全局美颜、滤镜、贴纸等效果互相叠加，但是不支持叠加多个美妆效果。
 * @param path 美妆模型所在路径。支持 SD 卡上的绝对路径，或 asset 目录下的相对路径。
 * - SD卡："/storage/emulated/0/Android/data/com.netease.lava.nertc.demo/files/filter_portrait/filter_style_FN1"
 * - asset: "2D/bunny"
 * @param name 美妆模型文件的名称。
 */
NERtcEx.getInstance().addBeautyMakeup(String path);

/**
 * 取消美妆效果。
 */
NERtcEx.getInstance().removeBeautyMakeup();

```
2.初始化美颜引擎
* 将assets下的相关资源拷贝到`/sdcard/Android/data/your.pkg.name`下
* 使用`startBeauty()`接口初始化美颜引擎
* 根据需要调用`setBeautyEffect`，`addBeautyFilter`接口来设置美颜，滤镜效果
* 最终不再使用美颜功能时，通过`stopBeauty`来结束美颜功能，销毁美颜引擎并释放资源。