package com.netease.nmc.nertcsample.beauty.module;

import com.netease.lava.nertc.sdk.video.NERtcBeautyEffectType;
import com.netease.nertcbeautysample.R;

import java.util.HashMap;

public enum NEEffectEnum {
    WHITETEETH(R.id.rb_effect_whiteteeth, NERtcBeautyEffectType.kNERtcBeautyWhiteTeeth, 0.6f),
    LIGHTEYE(R.id.rb_effect_lighteye, NERtcBeautyEffectType.kNERtcBeautyLightEye, 0.6f),
    WHITEN(R.id.rb_effect_whiten, NERtcBeautyEffectType.kNERtcBeautyWhiten, 0.6f),
    SMOOTH(R.id.rb_effect_smooth, NERtcBeautyEffectType.kNERtcBeautySmooth, 0.8f),
    SMALLNOSE(R.id.rb_effect_smallnose, NERtcBeautyEffectType.kNERtcBeautySmallNose, 0.5f),
    EYEDIS(R.id.rb_effect_eyedis, NERtcBeautyEffectType.kNERtcBeautyEyeDis, 0.5f),
    EYEANGLE(R.id.rb_effect_eyeangle, NERtcBeautyEffectType.kNERtcBeautyEyeAngle, 0.5f),
    MOUTH(R.id.rb_effect_mouth, NERtcBeautyEffectType.kNERtcBeautyMouth, 0.5f),
    BIGEYE(R.id.rb_effect_bigeye, NERtcBeautyEffectType.kNERtcBeautyBigEye, 0.7f),
    SMALLFACE(R.id.rb_effect_smallface, NERtcBeautyEffectType.kNERtcBeautySmallFace, 0.7f),
    JAW(R.id.rb_effect_jaw, NERtcBeautyEffectType.kNERtcBeautyJaw, 0.5f),
    THINFACE(R.id.rb_effect_thinface, NERtcBeautyEffectType.kNERtcBeautyThinFace, 0.5f),
    FACERUDDY(R.id.rb_effect_faceruddy, NERtcBeautyEffectType.kNERtcBeautyFaceRuddy, 0.0f),
    LONGNOSE(R.id.rb_effect_longnose, NERtcBeautyEffectType.kNERtcBeautyLongNose, 0.5f),
    PHILTRUM(R.id.rb_effect_philtrum, NERtcBeautyEffectType.kNERtcBeautyPhiltrum, 0.5f),
    MOUTHANGLE(R.id.rb_effect_mouthangle, NERtcBeautyEffectType.kNERtcBeautyMouthAngle, 0.5f),
    ROUNDEYE(R.id.rb_effect_roundeye, NERtcBeautyEffectType.kNERtcBeautyRoundEye, 0.0f),
    EYECORNER(R.id.rb_effect_eyecorner, NERtcBeautyEffectType.kNERtcBeautyEyeCorner, 0.0f),
    VFACE(R.id.rb_effect_vface, NERtcBeautyEffectType.kNERtcBeautyVFace, 0.0f),
    UNDERJAW(R.id.rb_effect_underjaw, NERtcBeautyEffectType.kNERtcBeautyUnderJaw, 0.0f),
    NARROWFACE(R.id.rb_effect_narrowface, NERtcBeautyEffectType.kNERtcBeautyNarrowFace, 0.0f),
    CHEEKBONE(R.id.rb_effect_cheekbone, NERtcBeautyEffectType.kNERtcBeautyCheekBone, 0.0f),
    FACESHARPEN(R.id.rb_effect_facesharpen, NERtcBeautyEffectType.kNERtcBeautyFaceSharpen, 0.0f);

    private int resId;
    private NERtcBeautyEffectType type;
    private float level;

    NEEffectEnum(int resId, NERtcBeautyEffectType type, float level) {
        this.resId = resId;
        this.type = type;
        this.level = level;
    }

    public static HashMap<Integer, NEEffect> getEffects() {
        NEEffectEnum[] neEffectEnums = NEEffectEnum.values();
        HashMap<Integer, NEEffect> effects = new HashMap<>();
        for (NEEffectEnum beauty : neEffectEnums) {
            effects.put(beauty.resId, new NEEffect(beauty.resId, beauty.type, beauty.level));
        }

        return effects;
    }
}
