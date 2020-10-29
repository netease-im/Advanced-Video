package com.netease.nmc.nertcsample.sensetime;

import android.opengl.GLES20;

import com.netease.nmc.nertcsample.sensetime.glutils.GlUtil;
import com.netease.nmc.nertcsample.sensetime.utils.NeteaseGLHelper;
import com.sensetime.stmobile.STBeautifyNative;
import com.sensetime.stmobile.STBeautyParamsType;
import com.sensetime.stmobile.STMobileHumanActionNative;
import com.sensetime.stmobile.model.STHumanAction;

import javax.microedition.khronos.opengles.GL10;

/**
 * 商汤滤镜逻辑处理类
 * Created by hzzhujinbo on 2017/7/11.
 */

public class SenseTimeEffect {

    private STBeautifyNative mStBeautifyNative = new STBeautifyNative(); //美颜参数，用户可以根据需要自己调节
    private STMobileHumanActionNative mSTHumanActionNative = new STMobileHumanActionNative();
    private STHumanAction mHumanActionBeautyOutput = new STHumanAction();

    private float[] mBeautifyParams = {0.36f, 0.74f, 0.30f};

    private final Object mHumanActionHandleLock = new Object();

    private int mImageWidth;
    private int mImageHeight;
    private boolean mNeedBeautify = true;

    private int[] mBeautifyTextureId;

    private boolean mInited = false;


    public SenseTimeEffect() {
    }

    public void release() {
        destory();
    }

    public void setBeautifyParam(int type, float value) {
        mStBeautifyNative.setParam(type, value);
    }

    private NeteaseGLHelper mNeteaseGLHelper;

    public int effect(int cameraTextureId, final int width, final int height) {
        mImageWidth = width;
        mImageHeight = height;

        if (!mInited) {
            if (mNeteaseGLHelper == null) {
                mNeteaseGLHelper = new NeteaseGLHelper();
                mNeteaseGLHelper.init(width, height);
            }
            initGLEffect();
        }

        if (mNeteaseGLHelper == null) {
            return -1;
        }

        if (mBeautifyTextureId == null) {
            mBeautifyTextureId = new int[1];
            GlUtil.initEffectTexture(mImageWidth, mImageHeight, mBeautifyTextureId,
                    GLES20.GL_TEXTURE_2D);
        }


        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        int textureId = mNeteaseGLHelper.drawOesTexture2Texture2D(cameraTextureId, width, height);
        int result;

        // 磨皮
        if (mNeedBeautify) {
            result = mStBeautifyNative.processTexture(textureId, mImageWidth, mImageHeight, 0,
                    null, mBeautifyTextureId[0], mHumanActionBeautyOutput);
            if (result == 0) {
                textureId = mBeautifyTextureId[0];
            }
        }

        GLES20.glViewport(0, 0, width, height);
        return textureId;
    }

    //初始化GL相关的句柄，包括美颜
    private void initGLEffect() {
        GLES20.glEnable(GL10.GL_DITHER);
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);
        initBeauty();
        mInited = true;
    }

    private void destory() {
        //必须释放非openGL句柄资源,否则内存泄漏
        deleteInternalTextures();

        synchronized (mHumanActionHandleLock) {
            mSTHumanActionNative.destroyInstance();
        }

        //openGL资源释放
        mStBeautifyNative.destroyBeautify();

        if (mNeteaseGLHelper != null) {
            mNeteaseGLHelper.release();
            mNeteaseGLHelper = null;
        }
    }

    private void deleteInternalTextures() {
        if (mBeautifyTextureId != null) {
            GLES20.glDeleteTextures(1, mBeautifyTextureId, 0);
            mBeautifyTextureId = null;
        }
    }

    private void initBeauty() {
        // 初始化beautify,preview的宽高
        int result = mStBeautifyNative.createInstance();
        if (result == 0) {
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_REDDEN_STRENGTH, mBeautifyParams[0]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_SMOOTH_STRENGTH, mBeautifyParams[1]);
            mStBeautifyNative.setParam(STBeautyParamsType.ST_BEAUTIFY_WHITEN_STRENGTH, mBeautifyParams[2]);
        }
    }
}