package com.netease.nmc.nertcsample.beauty.utils;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by sensetime on 16-11-16.
 */

public class STGLRender {

    private final static String TAG = "STGLRender";
    private static final String CAMERA_INPUT_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "	textureCoordinate = inputTextureCoordinate.xy;\n" +
            "	gl_Position = position;\n" +
            "}";

    private static final String YUV_TEXTURE =
                    "precision mediump float;                           \n" +
                    "varying vec2 textureCoordinate;                           \n" +
                    "uniform sampler2D y_texture;                       \n" +
                    "uniform sampler2D uv_texture;                      \n" +

                    "void main (void){                                  \n" +
                    "   float y = texture2D(y_texture, textureCoordinate).r;        \n" +

                    //We had put the Y values of each pixel to the R,G,B components by GL_LUMINANCE,
                    //that's why we're pulling it from the R component, we could also use G or B
                    "   vec2 uv = texture2D(uv_texture, textureCoordinate).xw - 0.5;       \n" +

                    //The numbers are just YUV to RGB conversion constants
                    "   float r = y + 1.370705 * uv.x;\n" +
                    "   float g = y - 0.698001 * uv.x - 0.337633 * uv.y;\n" +
                    "   float b = y + 1.732446 * uv.y;\n                          \n" +

                    //We finally set the RGB color of our pixel
                    "   gl_FragColor = vec4(r, g, b, 1.0);              \n" +
                    "}                                                  \n";

    private static final String CAMERA_INPUT_FRAGMENT_SHADER_OES = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "\n" +
            "precision mediump float;\n" +
            "varying vec2 textureCoordinate;\n" +
            "uniform samplerExternalOES inputImageTexture;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "	gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}";

    public static final String CAMERA_INPUT_FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform sampler2D inputImageTexture;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}"; 

    public static final String DRAW_POINTS_VERTEX_SHADER = "" +
            "attribute vec4 aPosition;\n" +
            "void main() {\n" +
            "  gl_PointSize = 5.0;" +
            "  gl_Position = aPosition;\n" +
            "}";

    public static final String DRAW_POINTS_FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
            "uniform vec4 uColor;\n" +
            "void main() {\n" +
            "  gl_FragColor = uColor;\n" +
            "}";

    //
    private final static String DRAW_POINTS_PROGRAM = "mPointProgram";
    private final static String DRAW_POINTS_COLOR = "uColor";
    private final static String DRAW_POINTS_POSITION = "aPosition";
    private int mDrawPointsProgram = 0;
    private int mColor = -1;
    private int mPosition = -1;
    private int[] mPointsFrameBuffers;

    private final static String PROGRAM_ID = "program";
    private final static String POSITION_COORDINATE = "position";
    private final static String TEXTURE_UNIFORM = "inputImageTexture";
    private final static String TEXTURE_COORDINATE = "inputTextureCoordinate";

    private int YUVToRGBAProgramId = -1;
    private final static String Y_TEXTURE = "y_texture";
    private final static String UV_TEXTURE = "uv_texture";
    private int yTextureLoc = -1;
    private int uvTextureLoc = -1;

    private final FloatBuffer mGLCubeBuffer;
    private final FloatBuffer mGLTextureBuffer;
    private final FloatBuffer mGLSaveTextureBuffer;
    private final FloatBuffer mGLSaveTextureFlipBuffer;

    private FloatBuffer mTextureBuffer;
    private FloatBuffer mVertexBuffer;

    private boolean mIsInitialized;
    private int glError;
    private ArrayList<HashMap<String, Integer>> mArrayPrograms = new ArrayList<HashMap<String, Integer>>(2) {
        {
            for (int i = 0; i < 2; ++i) {
                HashMap<String, Integer> hashMap = new HashMap<>();
                hashMap.put(PROGRAM_ID, 0);
                hashMap.put(POSITION_COORDINATE, -1);
                hashMap.put(TEXTURE_UNIFORM, -1);
                hashMap.put(TEXTURE_COORDINATE, -1);
                add(hashMap);
            }
        }
    };

    public int mViewPortWidth;
    public int mViewPortHeight;

    private int[] mFrameBuffers;
    private int[] mFrameBufferTextures;

    private int[] mSavePictureFrameBuffers;
    private int[] mSavePictureFrameBufferTextures;

    private int[] mFrameBuffersResize;
    private int[] mFrameBufferTexturesResize;

    private boolean mNeedResize = false;
    private int mWidthResize = 180;
    private int mHeightResize = 320;


    public STGLRender() {
        mGLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);

        mGLSaveTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLSaveTextureBuffer.put(TextureRotationUtil.getPhotoRotation(0, false, true)).position(0);

        mGLSaveTextureFlipBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLSaveTextureFlipBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);

        if(Constants.ACTIVITY_MODE_LANDSCAPE){
            mTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mTextureBuffer.put(TextureRotationUtil.TEXTURE_ROTATED_180).position(0);
        }
    }

    public void init(int width, int height) {
        initInner(width, height, -1, -1);
    }

    public void init(int width, int height, int widthResize, int heightResize) {
        initInner(width, height, widthResize, heightResize);
    }

    private void initInner(int width, int height, int widthResize, int heightResize) {
        Log.i(TAG, "initInner() called with: width = [" + width + "], height = [" + height + "], widthResize = [" + widthResize + "], heightResize = [" + heightResize + "]");
        if (mViewPortWidth == width && mViewPortHeight == height) {
            return;
        }
        initProgram(CAMERA_INPUT_FRAGMENT_SHADER_OES, mArrayPrograms.get(0));
        initProgram(CAMERA_INPUT_FRAGMENT_SHADER, mArrayPrograms.get(1));
        initYUVProgram(CAMERA_INPUT_VERTEX_SHADER, YUV_TEXTURE);
        mViewPortWidth = width;
        mViewPortHeight = height;

        mWidthResize = widthResize;
        mHeightResize = heightResize;

        if(mWidthResize > 0 && mHeightResize > 0) {
            mNeedResize = true;
        }

        initFrameBuffers(width, height);
        mIsInitialized = true;
    }

    private void initProgram(String fragment, HashMap<String, Integer> programInfo) {
        int proID = programInfo.get(PROGRAM_ID);
        if (proID == 0) {
            proID = OpenGLUtils.loadProgram(CAMERA_INPUT_VERTEX_SHADER, fragment);
            programInfo.put(PROGRAM_ID, proID);
            programInfo.put(POSITION_COORDINATE, GLES20.glGetAttribLocation(proID, POSITION_COORDINATE));
            programInfo.put(TEXTURE_UNIFORM, GLES20.glGetUniformLocation(proID, TEXTURE_UNIFORM));
            programInfo.put(TEXTURE_COORDINATE, GLES20.glGetAttribLocation(proID, TEXTURE_COORDINATE));
        }
    }

    private void initYUVProgram(String vertext, String fragment) {
        YUVToRGBAProgramId = OpenGLUtils.loadProgram(vertext, fragment);
        yTextureLoc = GLES20.glGetUniformLocation(YUVToRGBAProgramId, Y_TEXTURE);
        uvTextureLoc = GLES20.glGetUniformLocation(YUVToRGBAProgramId, UV_TEXTURE);
    }

    public void initDrawPoints() {
        mDrawPointsProgram = OpenGLUtils.loadProgram(DRAW_POINTS_VERTEX_SHADER, DRAW_POINTS_FRAGMENT_SHADER);
        mColor = GLES20.glGetAttribLocation(mDrawPointsProgram, DRAW_POINTS_POSITION);
        mPosition = GLES20.glGetUniformLocation(mDrawPointsProgram, DRAW_POINTS_COLOR);

        if (mPointsFrameBuffers == null) {
            mPointsFrameBuffers = new int[1];

            GLES20.glGenFramebuffers(1, mPointsFrameBuffers, 0);
        }
    }

    public void adjustTextureBuffer(int orientation,boolean flipHorizontal, boolean flipVertical) {
        float[] textureCords = TextureRotationUtil.getRotation(orientation, flipHorizontal, flipVertical);
        Log.d(TAG, "==========rotation: " + orientation + " flipVertical: " + flipVertical
                + " texturePos: " + Arrays.toString(textureCords));
        if (mTextureBuffer == null) {
            mTextureBuffer = ByteBuffer.allocateDirect(textureCords.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
        }
        mTextureBuffer.clear();
        mTextureBuffer.put(textureCords).position(0);
    }

    public void adjustVideoTextureBuffer(int orientation,boolean flipHorizontal, boolean flipVertical) {
        float[] textureCords = TextureRotationUtil.getVideoRotation(orientation, flipHorizontal, flipVertical);
        Log.d(TAG, "==========rotation: " + orientation + " flipVertical: " + flipVertical
                + " texturePos: " + Arrays.toString(textureCords));
        if (mTextureBuffer == null) {
            mTextureBuffer = ByteBuffer.allocateDirect(textureCords.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
        }
        mTextureBuffer.clear();
        mTextureBuffer.put(textureCords).position(0);
    }

    /**
     * 用来计算贴纸渲染的纹理最终需要的顶点坐标
     */
    public void calculateVertexBuffer(int displayW, int displayH, int imageW, int imageH) {
        Log.i(TAG, "calculateVertexBuffer() called with: displayW = [" + displayW + "], displayH = [" + displayH + "], imageW = [" + imageW + "], imageH = [" + imageH + "]");
        if (displayW == 0) return;
        int outputHeight = displayH;
        int outputWidth = displayW;

        float ratio1 = (float) outputWidth / imageW;
        float ratio2 = (float) outputHeight / imageH;

        float ratioMin = Math.max(ratio1, ratio2);

        if(Constants.ACTIVITY_MODE_LANDSCAPE){
            ratioMin = Math.max(ratio1, ratio2);
        }

        int imageWidthNew = Math.round(imageW * ratioMin);
        int imageHeightNew = Math.round(imageH * ratioMin);

        float ratioWidth = imageWidthNew / (float) outputWidth;
        float ratioHeight = imageHeightNew / (float) outputHeight;

        float[] cube = new float[]{
                TextureRotationUtil.CUBE[0] / ratioHeight, TextureRotationUtil.CUBE[1] / ratioWidth,
                TextureRotationUtil.CUBE[2] / ratioHeight, TextureRotationUtil.CUBE[3] / ratioWidth,
                TextureRotationUtil.CUBE[4] / ratioHeight, TextureRotationUtil.CUBE[5] / ratioWidth,
                TextureRotationUtil.CUBE[6] / ratioHeight, TextureRotationUtil.CUBE[7] / ratioWidth,
        };

        if (mVertexBuffer == null) {
            mVertexBuffer = ByteBuffer.allocateDirect(cube.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
        }
        mVertexBuffer.clear();
        mVertexBuffer.put(cube).position(0);
    }

    /**
     * 此函数有三个功能
     * 1. 将OES的纹理转换为标准的GL_TEXTURE_2D格式
     * 2. 将纹理宽高对换，即将wxh的纹理转换为了hxw的纹理，并且如果是前置摄像头，则需要有水平的翻转
     * 3. 读取上面两个步骤后的纹理内容存储到RGBA格式的buffer中，若初始化时使用resize参数，则读取resize后的纹理内容
     * @param textureId [in] OES的纹理id
     * @param buffer  [out] RGBA的buffer
     * @return 转换后的GL_TEXTURE_2D的纹理id
     */
    public int preProcess(int textureId, ByteBuffer buffer) {
        return preProcess(textureId, buffer, 0);
    }

    /**
     * 此函数有三个功能
     * 1. 将OES的纹理转换为标准的GL_TEXTURE_2D格式
     * 2. 将纹理宽高对换，即将wxh的纹理转换为了hxw的纹理，并且如果是前置摄像头，则需要有水平的翻转
     * 3. 读取上面两个步骤后的纹理内容存储到RGBA格式的buffer中，若初始化时使用resize参数，则读取resize后的纹理内容
     * @param textureId [in] OES的纹理id
     * @param buffer  [out] RGBA的buffer
     * @param bufIndex [in] 使用的buffer索引
     * @return 转换后的GL_TEXTURE_2D的纹理id
     */
    public int preProcess(int textureId, ByteBuffer buffer, int bufIndex) {
        if (mFrameBuffers == null || !mIsInitialized)
            return OpenGLUtils.NO_TEXTURE;

        GLES20.glUseProgram(mArrayPrograms.get(0).get(PROGRAM_ID));
        GlUtil.checkGlError("glUseProgram");

        mGLCubeBuffer.position(0);
        int glAttribPosition = mArrayPrograms.get(0).get(POSITION_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
        GLES20.glEnableVertexAttribArray(glAttribPosition);

        mTextureBuffer.position(0);
        int glAttribTextureCoordinate = mArrayPrograms.get(0).get(TEXTURE_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(glAttribTextureCoordinate);

        if (textureId != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            GLES20.glUniform1i(mArrayPrograms.get(0).get(TEXTURE_UNIFORM), 0);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[bufIndex]);
        GlUtil.checkGlError("glBindFramebuffer");
        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        if (buffer != null) {
            if (mNeedResize) {
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffersResize[bufIndex]);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                        GLES20.GL_TEXTURE_2D, mFrameBufferTexturesResize[bufIndex], 0);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

                GLES20.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, mFrameBuffers[bufIndex]);
                GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT0);
                GLES20.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, mFrameBuffersResize[bufIndex]);
                GLES20.glViewport(0, 0, mWidthResize, mHeightResize);
                GLES30.glBlitFramebuffer(0, 0, mViewPortWidth, mViewPortHeight, 0, 0, mWidthResize, mHeightResize, GLES30.GL_COLOR_BUFFER_BIT, GLES30.GL_NEAREST);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffersResize[bufIndex]);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
                GLES20.glReadPixels(0, 0, mWidthResize, mHeightResize, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

                GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

            } else {
                GLES20.glReadPixels(0, 0, mViewPortWidth, mViewPortHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
            }
        }

        GLES20.glDisableVertexAttribArray(glAttribPosition);
        GLES20.glDisableVertexAttribArray(glAttribTextureCoordinate);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glUseProgram(0);

        return mFrameBufferTextures[bufIndex];
    }

    public int preProcessAndResizeTexture(int textureId, boolean needResize, int[] resizedTextureId, ByteBuffer buffer, int bufferIndex) {
        if (mFrameBuffers == null || mFrameBuffersResize == null
                || !mIsInitialized)
            return -2;

        GLES20.glUseProgram(mArrayPrograms.get(0).get(PROGRAM_ID));
        GlUtil.checkGlError("glUseProgram");

        mGLCubeBuffer.position(0);
        int glAttribPosition = mArrayPrograms.get(0).get(POSITION_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
        GLES20.glEnableVertexAttribArray(glAttribPosition);

        mTextureBuffer.position(0);
        int glAttribTextureCoordinate = mArrayPrograms.get(0).get(TEXTURE_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(glAttribTextureCoordinate);

        if (textureId != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            GLES20.glUniform1i(mArrayPrograms.get(0).get(TEXTURE_UNIFORM), 0);
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[bufferIndex]);
        GlUtil.checkGlError("glBindFramebuffer");
        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        if (needResize) {
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBuffersResize[1]);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, mFrameBufferTexturesResize[1], 0);
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

            GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, mFrameBuffersResize[1]);
            GLES20.glViewport(0, 0, mWidthResize, mHeightResize);

            GLES30.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, mFrameBuffers[bufferIndex]);
            GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT0);
            GLES30.glCheckFramebufferStatus(GLES30.GL_DRAW_FRAMEBUFFER);
            GLES30.glCheckFramebufferStatus(GLES30.GL_READ_FRAMEBUFFER);
            GLES30.glIsTexture(mFrameBufferTexturesResize[1]);

            GLES30.glBlitFramebuffer(0, 0, mViewPortWidth, mViewPortHeight, 0, 0, mWidthResize, mHeightResize, GLES30.GL_COLOR_BUFFER_BIT, GLES30.GL_NEAREST);
            GLES30.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, 0);
            GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, 0);

            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBuffersResize[1]);
            if (buffer != null) {
                GLES20.glReadPixels(0, 0, mWidthResize, mHeightResize, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
            }
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

            GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        } else {
            if (buffer != null) {
                GLES20.glReadPixels(0, 0, mViewPortWidth, mViewPortHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
            }
        }

        GLES20.glDisableVertexAttribArray(glAttribPosition);
        GLES20.glDisableVertexAttribArray(glAttribTextureCoordinate);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glUseProgram(0);

        if(resizedTextureId.length > 0){
            resizedTextureId[0] = mFrameBufferTexturesResize[1];
        }

        return mFrameBufferTextures[bufferIndex];
    }


    /**
     * YUV纹理转RGBA纹理
     * @param textureIdY  [in] Y纹理ID
     * @param textureIdUV  [in] UV纹理ID
     * @param flag [in] 双缓冲标识
     * @return 转换后的RGBA纹理ID
     */
    public int YUV2RGB(int textureIdY, int textureIdUV, boolean flag) {
        if (mFrameBuffers == null || !mIsInitialized)
            return -2;

        GLES20.glUseProgram(YUVToRGBAProgramId);
        GlUtil.checkGlError("glUseProgram");

        mGLCubeBuffer.position(0);
        int glAttribPosition = mArrayPrograms.get(0).get(POSITION_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
        GLES20.glEnableVertexAttribArray(glAttribPosition);

        mTextureBuffer.position(0);
        int glAttribTextureCoordinate = mArrayPrograms.get(0).get(TEXTURE_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(glAttribTextureCoordinate);

        if (textureIdY != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIdY);
            GLES20.glUniform1i(yTextureLoc, 0);
        }

        if (textureIdUV != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIdUV);
            GLES20.glUniform1i(uvTextureLoc, 1);
        }

        if(flag){
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        }else {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[1]);
        }

        GlUtil.checkGlError("glBindFramebuffer");
        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(glAttribPosition);
        GLES20.glDisableVertexAttribArray(glAttribTextureCoordinate);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glUseProgram(0);

        if(flag){
            return mFrameBufferTextures[0];
        }else {
            return mFrameBufferTextures[1];
        }
    }

    public void destroyFrameBuffers() {
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(3, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }

        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(3, mFrameBuffers, 0);
            mFrameBuffers = null;
        }

        if(mSavePictureFrameBufferTextures != null) {
            GLES20.glDeleteTextures(1, mSavePictureFrameBufferTextures, 0);
            mSavePictureFrameBufferTextures = null;
        }

        if(mSavePictureFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(1, mSavePictureFrameBuffers, 0);
            mSavePictureFrameBuffers = null;
        }

        if (mPointsFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(1, mPointsFrameBuffers, 0);
            mPointsFrameBuffers = null;
        }

        if (mOesFrameBuffer != null) {
            GLES20.glDeleteFramebuffers(2, mOesFrameBuffer, 0);
            mOesFrameBuffer = null;
        }

        if (mCopySrcFrameBuffer != null) {
            GLES20.glDeleteFramebuffers(2, mCopySrcFrameBuffer, 0);
            mCopySrcFrameBuffer = null;
        }
    }


    public void destroyResizeFrameBuffers() {
        if (mFrameBufferTexturesResize != null) {
            GLES20.glDeleteTextures(2, mFrameBufferTexturesResize, 0);
            mFrameBufferTexturesResize = null;
        }
        if (mFrameBuffersResize != null) {
            GLES20.glDeleteFramebuffers(2, mFrameBuffersResize, 0);
            mFrameBuffersResize = null;
        }
    }

    public void onDrawPoints(int textureId, float[] points) {
        if (points == null) return;
        if (mDrawPointsProgram == 0) {
            initDrawPoints();
        }

        GLES20.glUseProgram(mDrawPointsProgram);
        GLES20.glUniform4f(mColor, 0.0f, 1.0f, 0.0f, 1.0f);

        FloatBuffer buff = null;

        buff = ByteBuffer.allocateDirect(points.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        buff.clear();
        buff.put(points).position(0);

        GLES20.glVertexAttribPointer(mPosition, 2, GLES20.GL_FLOAT, false, 0, buff);
        GLES20.glEnableVertexAttribArray(mPosition);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mPointsFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, textureId, 0);

        GlUtil.checkGlError("glBindFramebuffer");
        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, points.length/2);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glDisableVertexAttribArray(mPosition);
        glError = GLES20.glGetError();
        if(glError != 0)
            Log.d(TAG, "CatchGLError : " + glError);
    }

    public void onDrawPoints(float[] points) {

        if (mDrawPointsProgram == 0) {
            initDrawPoints();
        }

        GLES20.glUseProgram(mDrawPointsProgram);
        GLES20.glUniform4f(mColor, 0.0f, 1.0f, 0.0f, 1.0f);

        FloatBuffer buff = null;

        buff = ByteBuffer.allocateDirect(points.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        buff.clear();
        buff.put(points).position(0);

        GLES20.glVertexAttribPointer(mPosition, 2, GLES20.GL_FLOAT, false, 0, buff);
        GLES20.glEnableVertexAttribArray(mPosition);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GlUtil.checkGlError("glBindFramebuffer");

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, points.length/2);

        GLES20.glDisableVertexAttribArray(mPosition);
        glError = GLES20.glGetError();
        if(glError != 0)
            Log.d(TAG, "CatchGLError : " + glError);
    }

    public int onDrawFrame(final int textureId) {

        if (!mIsInitialized) {
            return OpenGLUtils.NOT_INIT;
        }

        GLES20.glUseProgram(mArrayPrograms.get(1).get(PROGRAM_ID));

        mVertexBuffer.position(0);
        int glAttribPosition = mArrayPrograms.get(1).get(POSITION_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(glAttribPosition);

        mGLTextureBuffer.position(0);
        int glAttribTextureCoordinate = mArrayPrograms.get(1).get(TEXTURE_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
                mGLTextureBuffer);
        GLES20.glEnableVertexAttribArray(glAttribTextureCoordinate);

        if (textureId != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mArrayPrograms.get(1).get(TEXTURE_UNIFORM), 0);
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(glAttribPosition);
        GLES20.glDisableVertexAttribArray(glAttribTextureCoordinate);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return OpenGLUtils.ON_DRAWN;
    }

    public int saveTextureToFrameBuffer(int textureOutId, ByteBuffer buffer) {
        if(mFrameBuffers == null) {
            return OpenGLUtils.NO_TEXTURE;
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[2]);
        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        GLES20.glUseProgram(mArrayPrograms.get(1).get(PROGRAM_ID));

        if(!mIsInitialized) {
            return OpenGLUtils.NOT_INIT;
        }

        mGLCubeBuffer.position(0);
        int glAttribPosition = mArrayPrograms.get(1).get(POSITION_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
        GLES20.glEnableVertexAttribArray(glAttribPosition);

        mGLSaveTextureBuffer.position(0);
        int glAttribTextureCoordinate = mArrayPrograms.get(1).get(TEXTURE_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mGLSaveTextureBuffer);
        GLES20.glEnableVertexAttribArray(glAttribTextureCoordinate);

        if(textureOutId != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureOutId);
            GLES20.glUniform1i(mArrayPrograms.get(1).get(TEXTURE_UNIFORM), 0);
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        if(buffer != null) {
            GLES20.glReadPixels(0, 0, mViewPortWidth, mViewPortHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
        }

        GLES20.glDisableVertexAttribArray(glAttribPosition);
        GLES20.glDisableVertexAttribArray(glAttribTextureCoordinate);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        return mFrameBufferTextures[2];
    }

    public int saveTextureToFrameBufferFlip(int textureOutId, ByteBuffer buffer) {
        if(mFrameBuffers == null) {
            return OpenGLUtils.NO_TEXTURE;
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[1]);
        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        GLES20.glUseProgram(mArrayPrograms.get(1).get(PROGRAM_ID));

        if(!mIsInitialized) {
            return OpenGLUtils.NOT_INIT;
        }

        mGLCubeBuffer.position(0);
        int glAttribPosition = mArrayPrograms.get(1).get(POSITION_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
        GLES20.glEnableVertexAttribArray(glAttribPosition);

        mGLSaveTextureFlipBuffer.position(0);
        int glAttribTextureCoordinate = mArrayPrograms.get(1).get(TEXTURE_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mGLSaveTextureFlipBuffer);
        GLES20.glEnableVertexAttribArray(glAttribTextureCoordinate);

        if(textureOutId != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureOutId);
            GLES20.glUniform1i(mArrayPrograms.get(1).get(TEXTURE_UNIFORM), 0);
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        if(buffer != null) {
            GLES20.glReadPixels(0, 0, mViewPortWidth, mViewPortHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
        }

        GLES20.glDisableVertexAttribArray(glAttribPosition);
        GLES20.glDisableVertexAttribArray(glAttribTextureCoordinate);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        return mFrameBufferTextures[1];
    }

    public int textureFlip(int textureOutId) {
        if(mFrameBuffers == null) {
            return OpenGLUtils.NO_TEXTURE;
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[2]);
        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        GLES20.glUseProgram(mArrayPrograms.get(1).get(PROGRAM_ID));

        if(!mIsInitialized) {
            return OpenGLUtils.NOT_INIT;
        }

        mGLCubeBuffer.position(0);
        int glAttribPosition = mArrayPrograms.get(1).get(POSITION_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
        GLES20.glEnableVertexAttribArray(glAttribPosition);

        mGLSaveTextureFlipBuffer.position(0);
        int glAttribTextureCoordinate = mArrayPrograms.get(1).get(TEXTURE_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mGLSaveTextureFlipBuffer);
        GLES20.glEnableVertexAttribArray(glAttribTextureCoordinate);

        if(textureOutId != OpenGLUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureOutId);
            GLES20.glUniform1i(mArrayPrograms.get(1).get(TEXTURE_UNIFORM), 0);
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(glAttribPosition);
        GLES20.glDisableVertexAttribArray(glAttribTextureCoordinate);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        return mFrameBufferTextures[2];
    }

    private int[] mOesFrameBuffer;
    private int[] mCopySrcFrameBuffer;
    public void copy2DTextureToOesTexture(int srcTexture, int dstTexture, int width, int height, int index){
        if(mOesFrameBuffer == null){
            mOesFrameBuffer = new int[2];
            GLES20.glGenFramebuffers(2, mOesFrameBuffer, 0);
        }

        if(mCopySrcFrameBuffer == null){
            mCopySrcFrameBuffer = new int[2];
            GLES20.glGenFramebuffers(2, mCopySrcFrameBuffer, 0);
        }

        GLES30.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, mCopySrcFrameBuffer[index]);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, srcTexture);
        GLES30.glFramebufferTexture2D(GLES30.GL_READ_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, srcTexture, 0);
        GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, mOesFrameBuffer[index]);
        GLES30.glFramebufferTexture2D(GLES30.GL_DRAW_FRAMEBUFFER,
                GLES30.GL_COLOR_ATTACHMENT0, GLES11Ext.GL_TEXTURE_EXTERNAL_OES, dstTexture, 0);
        GLES30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GLES30.GL_COLOR_BUFFER_BIT, GLES30.GL_LINEAR);
        GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, 0);
        GLES30.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
    }

    private void initFrameBuffers(int width, int height) {
        destroyFrameBuffers();
        destroyResizeFrameBuffers();

        if (mFrameBuffers == null) {
            mFrameBuffers = new int[3];
            mFrameBufferTextures = new int[3];

            GLES20.glGenFramebuffers(3, mFrameBuffers, 0);
            GLES20.glGenTextures(3, mFrameBufferTextures, 0);

            bindFrameBuffer(mFrameBufferTextures[0], mFrameBuffers[0], width, height);
            bindFrameBuffer(mFrameBufferTextures[1], mFrameBuffers[1], width, height);
            bindFrameBuffer(mFrameBufferTextures[2], mFrameBuffers[2], width, height);

        }

        if (mSavePictureFrameBuffers == null) {
            mSavePictureFrameBuffers = new int[1];
            mSavePictureFrameBufferTextures = new int[1];
            GLES20.glGenFramebuffers(1, mSavePictureFrameBuffers, 0);
            GLES20.glGenTextures(1, mSavePictureFrameBufferTextures, 0);
            bindFrameBuffer(mSavePictureFrameBufferTextures[0], mSavePictureFrameBuffers[0], width, height);
        }

        if (mNeedResize && mFrameBuffersResize == null) {
            mFrameBuffersResize = new int[2];
            mFrameBufferTexturesResize = new int[2];
            GLES20.glGenFramebuffers(2, mFrameBuffersResize, 0);
            GLES20.glGenTextures(2, mFrameBufferTexturesResize, 0);
            bindFrameBuffer(mFrameBufferTexturesResize[0], mFrameBuffersResize[0], mWidthResize, mHeightResize);
            bindFrameBuffer(mFrameBufferTexturesResize[1], mFrameBuffersResize[1], mWidthResize, mHeightResize);
        }
    }

    private void bindFrameBuffer(int textureId, int frameBuffer, int width, int height) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,textureId, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public final void destroy() {
        mIsInitialized = false;
        destroyFrameBuffers();
        GLES20.glDeleteProgram(mArrayPrograms.get(0).get(PROGRAM_ID));
        GLES20.glDeleteProgram(mArrayPrograms.get(1).get(PROGRAM_ID));
//        GLES20.glDeleteProgram(mArrayPrograms.get(2).get(PROGRAM_ID));
    }
}
