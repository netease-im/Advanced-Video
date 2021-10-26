//
//  NEBeautyMacro.h
//  Beauty-iOS-ObjC
//
//  Created by zhangchenliang on 2021/10/12.
//  Copyright © 2021 NetEase. All rights reserved.
//

#ifndef NEBeautyMacro_h
#define NEBeautyMacro_h

#define SCREEN_WIDTH [UIScreen mainScreen].bounds.size.width
#define SCREEN_HEIGHT [UIScreen mainScreen].bounds.size.height

#define UIColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

typedef NS_ENUM(NSInteger, NEBeautyEffectType) {
    NEBeautyEffectTypeBeautyBase,
    NEBeautyEffectTypeBeautyShape,
    NEBeautyEffectTypeBeautyAdv,
    NEBeautyEffectTypeBeautyAdv2,
    NEBeautyEffectTypeBeautyAdv3,
    
    NEBeautyEffectTypeSticker2D,
    NEBeautyEffectTypeSticker3D,
    NEBeautyEffectTypeStickerFaceChange,
    NEBeautyEffectTypeStickerParticle,
    
    NEBeautyEffectTypeFilter,
    
    NEBeautyEffectTypeMakeup
};

typedef NS_ENUM(NSInteger, NEBeautySliderType) {
    NEBeautySliderTypeWhiteTeeth = 0,
    NEBeautySliderTypeLightEye,
    NEBeautySliderTypeWhiten,
    NEBeautySliderTypeSmooth,
    NEBeautySliderTypeSmallNose,
    NEBeautySliderTypeEyeDis,
    NEBeautySliderTypeEyeAngle,
    NEBeautySliderTypeMouth,
    NEBeautySliderTypeBigEye,
    NEBeautySliderTypeSmallFace,
    NEBeautySliderTypeJaw,
    NEBeautySliderTypeThinFace,
    NEBeautySliderTypeLongNose,
    NEBeautySliderTypeRenZhong,
    NEBeautySliderTypeMouthAngle,
    NEBeautySliderTypeRoundEye,
    NEBeautySliderTypeOpenEyeAngle,
    NEBeautySliderTypeVFace,
    NEBeautySliderTypeThinUnderjaw,
    NEBeautySliderTypeNarrowFace,
    NEBeautySliderTypeCheekBone,
    
    NEBeautySliderTypeFilterStrength
};

#endif /* NEBeautyMacro_h */
