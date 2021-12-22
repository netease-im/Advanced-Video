package com.netease.nmc.nertcsample.beauty.module;

import com.netease.lava.nertc.sdk.video.NERtcBeautyEffectType;

public class NEEffect {
    private int resId;
    private NERtcBeautyEffectType type;
    private float level;

    public NEEffect(int resId, NERtcBeautyEffectType type, float level) {
        this.resId = resId;
        this.type = type;
        this.level = level;
    }

    public NEEffect(NEEffect effect) {
        if (effect != null) {
            resId = effect.resId;
            type = effect.type;
            level = effect.level;
        }
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public NERtcBeautyEffectType getType() {
        return type;
    }

    public void setType(NERtcBeautyEffectType type) {
        this.type = type;
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "NEBeauty{" +
                "resId=" + resId +
                ", type=" + type +
                ", level=" + level +
                '}';
    }
}
