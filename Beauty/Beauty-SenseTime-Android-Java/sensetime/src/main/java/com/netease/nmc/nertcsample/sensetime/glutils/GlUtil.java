package com.netease.nmc.nertcsample.sensetime.glutils;

import android.opengl.GLES20;

/**
 * Created by jerikc on 16/2/23.
 */
public final class GlUtil {

    private GlUtil() { // do not instantiate
    }

    public static void initEffectTexture(int width, int height, int[] textureId, int type) {
        int len = textureId.length;
        if (len > 0) {
            GLES20.glGenTextures(len, textureId, 0);
        }
        for (int value : textureId) {
            GLES20.glBindTexture(type, value);
            GLES20.glTexParameterf(type,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(type,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(type,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(type,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(type, 0, GLES20.GL_RGBA, width, height, 0,
                    GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        }
    }
}
