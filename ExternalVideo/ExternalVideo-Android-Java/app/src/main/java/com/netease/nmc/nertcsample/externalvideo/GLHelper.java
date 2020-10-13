package com.netease.nmc.nertcsample.externalvideo;

import android.opengl.GLES20;

import com.netease.lava.webrtc.EglBase;

public class GLHelper {
    public static void initGLContext() {
        EglBase eglBase = EglBase.create(null, EglBase.CONFIG_PIXEL_BUFFER);
        eglBase.createDummyPbufferSurface();
        eglBase.makeCurrent();
    }

    public static int genTexture() {
        int[] texName = new int[1];
        GLES20.glGenTextures(1, texName, 0);
        return texName[0];
    }
}
