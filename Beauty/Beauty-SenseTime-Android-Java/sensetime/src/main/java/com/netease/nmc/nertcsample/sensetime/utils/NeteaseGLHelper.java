package com.netease.nmc.nertcsample.sensetime.utils;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.netease.nmc.nertcsample.sensetime.glutils.OpenGLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by hzzhujinbo on 2017/11/22.
 */

public class NeteaseGLHelper {

    private static String VERTEXSHADER = "" +
            "attribute vec4 aPosition;\n" +
            "attribute vec2 aTextureCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main(){\n" +
            "    gl_Position= aPosition;\n" +
            "    vTextureCoord = aTextureCoord;\n" +
            "}";

    private static String FRAGMENTSHADER_CAMERA2D = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying mediump vec2 vTextureCoord;\n" +
            "uniform samplerExternalOES uTexture;\n" +
            "void main(){\n" +
            "    vec4  color = texture2D(uTexture, vTextureCoord);\n" +
            "    gl_FragColor = color;\n" +
            "}";
    private static float[] SquareVertices = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f};
    private static float[] CamTextureVertices = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f};
    private static float[] Cam2dTextureVertices = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f};
    private static short[] drawIndices = {0, 1, 2, 0, 2, 3};
    private static int FLOAT_SIZE_BYTES = 4;
    private static final int SHORT_SIZE_BYTES = 2;
    private static final int COORDS_PER_VERTEX = 2;
    private static final int TEXTURE_COORDS_PER_VERTEX = 2;
    private FloatBuffer shapeVerticesBuffer;
    private ShortBuffer drawIndecesBuffer;
    private final Object syncCameraTextureVerticesBuffer = new Object();
    private FloatBuffer camera2dTextureVerticesBuffer;
    private int sample2DFrameBuffer;
    private int sample2DFrameBufferTexture;
    private int cam2dProgram;
    private int cam2dTextureLoc;
    private int cam2dPostionLoc;
    private int cam2dTextureCoordLoc;


    public void init(int width,int height){
        cam2dProgram = OpenGLUtils.loadProgram(VERTEXSHADER, FRAGMENTSHADER_CAMERA2D);
        GLES20.glUseProgram(cam2dProgram);
        cam2dTextureLoc = GLES20.glGetUniformLocation(cam2dProgram, "uTexture");
        cam2dPostionLoc = GLES20.glGetAttribLocation(cam2dProgram, "aPosition");
        cam2dTextureCoordLoc = GLES20.glGetAttribLocation(cam2dProgram, "aTextureCoord");
        int[] fb = new int[1], fbt = new int[1];
        createCamFrameBuff(fb, fbt, width, height);
        sample2DFrameBuffer = fb[0];
        sample2DFrameBufferTexture = fbt[0];

        shapeVerticesBuffer = getShapeVerticesBuffer();
        camera2dTextureVerticesBuffer = getCamera2DTextureVerticesBuffer();
        drawIndecesBuffer = getDrawIndecesBuffer();
    }

    /**
     * 将Camera的OES Texture绘制成2D Texture
     */
    public int drawOesTexture2Texture2D(int oesTextureId,int width,int height) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, sample2DFrameBuffer);
        GLES20.glUseProgram(cam2dProgram);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, oesTextureId);
        GLES20.glUniform1i(cam2dTextureLoc, 0);
        synchronized (syncCameraTextureVerticesBuffer) {
            enableVertex(cam2dPostionLoc, cam2dTextureCoordLoc,
                    shapeVerticesBuffer, camera2dTextureVerticesBuffer);
        }
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawIndecesBuffer.limit(), GLES20.GL_UNSIGNED_SHORT, drawIndecesBuffer);
        GLES20.glFinish();
        disableVertex(cam2dPostionLoc, cam2dTextureCoordLoc);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glUseProgram(0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return sample2DFrameBufferTexture;
    }

    public void release(){
        GLES20.glDeleteProgram(cam2dProgram);
        GLES20.glDeleteFramebuffers(1, new int[]{sample2DFrameBuffer}, 0);
        GLES20.glDeleteTextures(1, new int[]{sample2DFrameBufferTexture}, 0);
    }

    private FloatBuffer getShapeVerticesBuffer() {
        FloatBuffer result = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * SquareVertices.length).
                order(ByteOrder.nativeOrder()).
                asFloatBuffer();
        result.put(SquareVertices);
        result.position(0);
        return result;
    }

    private FloatBuffer getCamera2DTextureVerticesBuffer() {
        FloatBuffer result = ByteBuffer.allocateDirect(FLOAT_SIZE_BYTES * Cam2dTextureVertices.length).
                order(ByteOrder.nativeOrder()).
                asFloatBuffer();
        result.put(CamTextureVertices);
        result.position(0);
        return result;
    }

    private ShortBuffer getDrawIndecesBuffer() {
        ShortBuffer result = ByteBuffer.allocateDirect(SHORT_SIZE_BYTES * drawIndices.length).
                order(ByteOrder.nativeOrder()).
                asShortBuffer();
        result.put(drawIndices);
        result.position(0);
        return result;
    }

    private void createCamFrameBuff(int[] frameBuffer, int[] frameBufferTex, int width, int height) {
        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        GLES20.glGenTextures(1, frameBufferTex, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTex[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, frameBufferTex[0], 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    private void enableVertex(int posLoc, int texLoc, FloatBuffer shapeBuffer, FloatBuffer texBuffer) {
        GLES20.glEnableVertexAttribArray(posLoc);
        GLES20.glEnableVertexAttribArray(texLoc);
        GLES20.glVertexAttribPointer(posLoc, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                COORDS_PER_VERTEX * 4, shapeBuffer);
        GLES20.glVertexAttribPointer(texLoc, TEXTURE_COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                TEXTURE_COORDS_PER_VERTEX * 4, texBuffer);
    }

    private void disableVertex(int posLoc, int texLoc) {
        GLES20.glDisableVertexAttribArray(posLoc);
        GLES20.glDisableVertexAttribArray(texLoc);
    }
}