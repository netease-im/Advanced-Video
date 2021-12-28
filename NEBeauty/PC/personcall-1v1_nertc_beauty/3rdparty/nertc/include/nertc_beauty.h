#ifndef NERTC_BEAUTY_H
#define NERTC_BEAUTY_H

#import <Foundation/Foundation.h>

@interface NERtcBeauty : NSObject

#pragma mark - Attribute

/** @if English
 * @if Chinese
 * 美牙
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float teeth;

/** @if English
 * @if Chinese
 * 亮眼
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float brightEye;

/** @if English
 * @if Chinese
 * 美白
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float whiteSkin;

/** @if English
 * @if Chinese
 * 磨皮
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float smoothSkin;

/** @if English
 * @if Chinese
 * 小鼻
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float smallNose;

/** @if English
 * @if Chinese
 * 眼距调整
 * 取值范围:[0.0, 1.0]  0.5为无效果，0.0和1.0最大效果，默认值0.5
 */
@property(nonatomic, assign) float eyesDistance;

/** @if English
 * @if Chinese
 * 眼角调整
 * 取值范围:[0.0, 1.0]  0.5为无效果，0.0和1.0最大效果，默认值0.5
 */
@property(nonatomic, assign) float eyesAngle;

/** @if English
 * @if Chinese
 * 嘴型调整
 * 取值范围:[0.0, 1.0]  0.5为无效果，0.0和1.0最大效果，默认值0.5
 */
@property(nonatomic, assign) float mouth;

/** @if English
 * @if Chinese
 * 大眼
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float bigEye;

/** @if English
 * @if Chinese
 * 小脸
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float smallFace;

/** @if English
 * @if Chinese
 * 下巴调整
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float jaw;

/** @if English
 * @if Chinese
 * 瘦脸
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float thinFace;

/** @if English
 * @if Chinese
 * 红润
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float faceRuddyStrength;

/** @if English
 * @if Chinese
 * 长鼻
 * 取值范围:[0.0, 1.0]  0.5为无效果，0.0和1.0最大效果，默认值0.5
 */
@property(nonatomic, assign) float longNoseStrength;

/** @if English
 * @if Chinese
 * 人中
 * 取值范围:[0.0, 1.0]  0.5为无效果，0.0和1.0最大效果，默认值0.5
 */
@property(nonatomic, assign) float renZhongStrength;

/** @if English
 * @if Chinese
 * 嘴角
 * 取值范围:[0.0, 1.0]  0.5为无效果，0.0和1.0最大效果，默认值0.5
 */
@property(nonatomic, assign) float mouthAngle;

/** @if English
 * @if Chinese
 * 圆眼
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float roundEyeStrength;

/** @if English
 * @if Chinese
 * 开眼角
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float openEyeAngleStrength;

/** @if English
 * @if Chinese
 * V脸
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float vFaceStrength;

/** @if English
 * @if Chinese
 * 瘦下颌
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float thinUnderjawStrength;

/** @if English
 * @if Chinese
 * 窄脸
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float narrowFaceStrength;

/** @if English
 * @if Chinese
 * 瘦颧骨
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float cheekBoneStrength;

/** @if English
 * @if Chinese
 * 锐化
 * 取值范围:[0.0, 1.0]  0.0为无效果，1.0为最大效果，默认值0.0
 */
@property(nonatomic, assign) float faceSharpenStrength;

/**
 * @if English
 * @if Chinese
 * 设置滤镜强度。
 * 
 * 取值范围为 [0 - 1]，默认值为 0.0。取值越大，滤镜强度越大，开发者可以根据业务需求自定义设置滤镜强度。
 * 
 * 滤镜强度设置实时生效，更换滤镜后滤镜强度不变，如需调整，可以再次通过此参数设置滤镜强度。
 */
@property(nonatomic, assign) float filterStrength;

/**
 * @if English
 * @if Chinese
 * 启用美颜时，启用或关闭镜像模式。
 * 
 * 默认为 false，表示美颜时启用镜像模式。
 * 
 * - 美颜功能启用时，此接口用于开启或关闭镜像模式。默认为关闭状态。美颜功能暂停或结束后，此接口不再生效。
 * - 启用镜像模式之后，本端画面会呈现为左右翻转的视觉效果。
 */
@property(nonatomic, assign) BOOL flipX;

/**
 * @if English
 * @if Chinese
 * 开启或关闭美颜功能。
 * 
 * 默认为 false，表示不启用美颜功能。您可以调用此接口开启或关闭美颜功能。启用美颜功能之后，默认开启全局美颜效果，您也可以通过 `setBeautyEffectWithValue` 调整美颜效果，或通过相关方法增加滤镜、贴纸、美妆等效果。
 * 
 * 美颜功能关闭后，包括全局美颜、滤镜、贴纸和美妆在内的所有美颜效果都会暂时关闭，直至重新启用美颜功能。
 * 
 * @note 该方法需要在 startBeauty 之后调用。
 */
@property(nonatomic, assign) BOOL isOpenBeauty;

#pragma mark - Method

+ (NERtcBeauty *)shareInstance;

/**
 * @if English
 * @if Chinese
 * 开启美颜功能模块。
 * 
 * - 调用此接口后，开启美颜引擎。如果后续不再需要使用美颜功能，可以调用 `stopBeauty` 结束美颜功能模块，销毁美颜引擎并释放资源。
 * - 开启美颜功能模块后，需要通过 `isOpenBeauty` 开启美颜功能，此时默认使用全局美颜效果。
 *
 * @note 该方法需要在 `enableLocalVideo` 之前设置。
 * @since V4.2.202
 * @return
 * - 0: 方法调用成功；
 * - 其他: 方法调用失败。
 */
- (int)startBeauty;

/**
 * @if English
 * @if Chinese
 * 结束美颜功能模块。
 *
 * 如果后续不再需要使用美颜功能，可以调用 stopBeauty 结束美颜功能模块，SDK 会自动销毁美颜引擎并释放资源。
 * @since V4.2.202
 */
- (void)stopBeauty;

/**
 * @if English
 * @if Chinese
 * 设置美颜类型和强度。
 * 
 * - 此方法可用于设置磨皮、美白、大眼等多种全局美颜类型。
 * - 多次调用此接口可以叠加多种全局美颜效果，也可以通过相关方法叠加滤镜、贴纸、美妆等自定义效果。
 * @since V4.2.202
 * @param type 美颜类型。详细信息请参考 {@link NERtcBeautyEffectType}。
 * @param value 对应美颜类型的强度。取值范围为 [0, 1]，各种美颜效果的默认值不同。
 */
- (void)setBeautyEffectWithValue:(float)value atType:(int)type;

/**
 * @if English
 * @if Chinese
 * 导入美颜资源或模型。
 *
 * 在 iOS/macOS 平台中使用自定义美颜效果之前，需要先通过此方法导入美颜资源或模型。
 * 
 * @note 美颜功能模块开启过程中，如果资源路径或名称没有变更，则只需导入一次。如需更换资源，需要调用此接口重新导入。
 * @since V4.2.202
 * @param path 美颜资源或模型所在的路径。应指定为绝对路径。
 * @param name 美颜资源或模型文件的名称。
 */
- (int)addTempleteWithPath:(NSString *)path andName:(NSString *)name;

/**
 * @if English
 * @if Chinese
 * 添加滤镜效果。
 *
 * 此接口用于加载滤镜资源，并添加对应的滤镜效果。需要更换滤镜时，重复调用此接口使用新的滤镜资源即可。
 * 
 * @note 
 * - 在 iOS/macOS 平台中使用滤镜、贴纸和美妆等自定义美颜效果之前，需要先通过方法 `addTempleteWithPath` 导入美颜资源或模型。
 * - 滤镜效果可以和全局美颜、贴纸、美妆等效果互相叠加，但是不支持叠加多个滤镜。
 * @since V4.2.202
 * @param path 滤镜资源或模型所在的路径。应指定为绝对路径。
 * @param name 滤镜资源或模型文件的名称。
 */
- (void)addBeautyFilterWithPath:(NSString *)path andName:(NSString *)name;

/**
 * @if English
 * @if Chinese
 * 取消滤镜效果。
 * @since V4.2.202
 */
- (void)removeBeautyFilter;

/**
 * @if English
 * @if Chinese
 * 添加贴纸效果。
 *
 * 此接口用于加载贴纸资源，添加对应的贴纸效果。需要更换贴纸时，重复调用此接口使用新的贴纸资源即可。
 *
 * @note 
 * - 在 iOS/macOS 平台中使用滤镜、贴纸和美妆等自定义美颜效果之前，需要先通过方法 `addTempleteWithPath` 导入美颜资源或模型。
 * - 贴纸效果可以和全局美颜、滤镜、美妆等效果互相叠加，但是不支持叠加多个贴纸。
 * @since V4.2.202
 * @param path 贴纸资源或模型所在的路径。应指定为绝对路径。
 * @param name 贴纸资源或模型文件的名称。
 */
- (void)addBeautyStickerWithPath:(NSString *)path andName:(NSString *)name;

/**
 * @if English
 * @if Chinese
 * 取消贴纸效果。
 * @since V4.2.202
 */
- (void)removeBeautySticker;

/**
 * @if English
 * @if Chinese
 * 添加美妆效果。
 *
 * 此接口用于加载美妆模型，添加对应的贴纸效果。需要更换美妆效果时，重复调用此接口使用新的美妆模型即可。
 *
 * @note 
 * - 在 iOS/macOS 平台中使用滤镜、贴纸和美妆等自定义美颜效果之前，需要先通过方法 `addTempleteWithPath` 导入美颜资源或模型。
 * - 美妆效果可以和全局美颜、滤镜、贴纸等效果互相叠加，但是不支持叠加多个美妆效果。
 * @since V4.2.202
 * @param path 美妆资源或模型所在的路径。应指定为绝对路径。
 * @param name 美妆资源或模型文件的名称。
 */
- (void)addBeautyMakeupWithPath:(NSString *)path andName:(NSString *)name;

/**
 * @if English
 * @if Chinese
 * 取消美妆效果。
 * @since V4.2.202
 */
- (void)removeBeautyMakeup;

/** 
 * @if English
 * @if Chinese
 * 获取美颜相关的错误信息。
 * @since V4.2.202
 * @return 错误码。
 * - 0：调用成功。
 * - 1：权限不足。请联系商务经理了解计费策略，并开通美颜功能。
 * - 100：引擎内部错误。请联系技术支持排查。
 * */
- (NSString *)getError;

@end

#endif
