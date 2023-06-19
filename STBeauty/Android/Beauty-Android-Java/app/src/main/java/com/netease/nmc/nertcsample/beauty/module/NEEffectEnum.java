package com.netease.nmc.nertcsample.beauty.module;

import com.netease.nertcbeautysample.R;
import com.sensetime.stmobile.params.STEffectBeautyType;

import java.util.HashMap;

public enum NEEffectEnum {
    WHITETEETH(R.id.rb_effect_whiteteeth, STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_WHITE_TEETH, 0.3f),
    LIGHTEYE(R.id.rb_effect_lighteye, STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_BRIGHT_EYE, 0.6f),
    WHITEN(R.id.rb_effect_whiten, STEffectBeautyType.EFFECT_BEAUTY_BASE_WHITTEN, 0.8f),
    SMOOTH(R.id.rb_effect_smooth, STEffectBeautyType.EFFECT_BEAUTY_BASE_FACE_SMOOTH, 0.65f),
    SMALLNOSE(R.id.rb_effect_smallnose, STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_NARROW_NOSE, 0.4f),
    EYEDIS(R.id.rb_effect_eyedis, STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_EYE_DISTANCE, 0.4f),
    EYEANGLE(R.id.rb_effect_eyeangle, STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_EYE_ANGLE, 0.5f),
    MOUTH(R.id.rb_effect_mouth, STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_MOUTH_SIZE, 0.8f),
    BIGEYE(R.id.rb_effect_bigeye, STEffectBeautyType.EFFECT_BEAUTY_RESHAPE_ENLARGE_EYE, 0.3f),
    SMALLFACE(R.id.rb_effect_smallface, STEffectBeautyType.EFFECT_BEAUTY_RESHAPE_SHRINK_FACE, 0.1f),
    JAW(R.id.rb_effect_jaw, STEffectBeautyType.EFFECT_BEAUTY_RESHAPE_SHRINK_JAW, 0.4f),
    THINFACE(R.id.rb_effect_thinface, STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_THIN_FACE, 0.35f),
    FACERUDDY(R.id.rb_effect_faceruddy, STEffectBeautyType.EFFECT_BEAUTY_BASE_REDDEN, 0.1f),
    LONGNOSE(R.id.rb_effect_longnose, STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_NOSE_LENGTH, 0.0f),
    PHILTRUM(R.id.rb_effect_philtrum, STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_PHILTRUM_LENGTH, 0.5f),
    MOUTHANGLE(R.id.rb_effect_mouthangle, STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_MOUTH_SIZE, 0.5f),
    ROUNDEYE(R.id.rb_effect_roundeye, STEffectBeautyType.EFFECT_BEAUTY_RESHAPE_ROUND_EYE, 0.8f),
    EYECORNER(R.id.rb_effect_eyecorner, STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_EYE_ANGLE, 0.0f),
    VFACE(R.id.rb_effect_vface, STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_SHRINK_LONG_FACE, 0.0f),
    UNDERJAW(R.id.rb_effect_underjaw, STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_SHRINK_JAWBONE, 0.0f),
    NARROWFACE(R.id.rb_effect_narrowface, STEffectBeautyType.EFFECT_BEAUTY_RESHAPE_NARROW_FACE, 0.0f),
    CHEEKBONE(R.id.rb_effect_cheekbone, STEffectBeautyType.EFFECT_BEAUTY_MAKEUP_CHEEK, 0.0f),
    FACESHARPEN(R.id.rb_effect_facesharpen, STEffectBeautyType.EFFECT_BEAUTY_TONE_SHARPEN, 0.1f);

    private int resId;
    private int type;
    private float level;

    NEEffectEnum(int resId, int type, float level) {
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
