package com.netease.nmc.nertcsample.beauty.module;

import com.sensetime.stmobile.params.STEffectBeautyType;

public class NEEffect {
    private int resId;
    private int type;
    private float level;

    public NEEffect(int resId, int type, float level) {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
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
