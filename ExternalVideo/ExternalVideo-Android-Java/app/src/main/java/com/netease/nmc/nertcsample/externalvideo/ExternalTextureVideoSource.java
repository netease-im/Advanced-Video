package com.netease.nmc.nertcsample.externalvideo;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.netease.lava.nertc.sdk.video.NERtcVideoFrame;
import com.netease.lava.webrtc.EglBase;

import javax.microedition.khronos.opengles.GL10;

public class ExternalTextureVideoSource extends ExternalVideoSource {
    private final String TAG = "ExternalTexture";

    private final String path;
    private final MediaMetadata metaData;
    private final Callback callback;

    public static ExternalVideoSource create(String path, Callback callback) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        MediaMetadata metaData = MediaMetadataExtractor.extractVideo(path);
        if (metaData == null
                || !metaData.hasVideo
                || metaData.width == 0
                || metaData.height == 0
                || metaData.width * metaData.height > 1920 * 1080) {
            return null;
        }

        return new ExternalTextureVideoSource(path, metaData, callback);
    }

    private ExternalTextureVideoSource(String path, MediaMetadata metaData, Callback callback) {
        this.path = path;
        this.metaData = metaData;
        this.callback = callback;
    }

    private int textureId;
    private SurfaceTexture surfaceTexture;
    private Surface surface;

    private MediaPlayer mediaPlayer;

    private GLHandler handler;

    @Override
    public boolean start() {
        createSurface();
        return playVideo(path, surface);
    }

    @Override
    public void stop() {
        stopVideo();
        destroySurface();
        destroyHandler();
    }

    private void createSurface() {
        textureId = getExternalOESTextureID();
        surfaceTexture = new SurfaceTexture(textureId);
        surfaceTexture.setOnFrameAvailableListener(this::onFrameAvailable);
        surface = new Surface(surfaceTexture);
    }

    private void destroySurface() {
        Surface surface = this.surface;
        this.surface = null;
        if (surface != null) {
            surface.release();
        }
        SurfaceTexture surfaceTexture = this.surfaceTexture;
        this.surfaceTexture = null;
        if (surfaceTexture != null) {
            surfaceTexture.release();
        }
    }

    private boolean playVideo(String path, Surface surface) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(path);
            mediaPlayer.setSurface(surface);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                try {
                    mediaPlayer.start();
                } catch (Exception ex) {
                    Log.e(TAG, "MediaPlayer start: ex=", ex);
                }
            });
            mediaPlayer.setLooping(true); // 设置循环播放
            mediaPlayer.setOnErrorListener((mediaPlayer, what, extra) -> {
                Log.e(TAG, "onError:"
                        + " what=" + what
                        + " extra:=" + extra
                );
                return false; // 如果发生错误，重新播放
            });
            return true;
        } catch (Exception ex) {
            Log.e(TAG, "MediaPlayer: ex=", ex);
        }
        return false;
    }

    private void stopVideo() {
        MediaPlayer mediaPlayer = this.mediaPlayer;
        this.mediaPlayer = null;
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }

    private GLHandler ensureHandler() {
        GLHandler handler = this.handler;
        if (handler == null) {
            handler = new GLHandler(ExternalTextureVideoSource::initGL);
            this.handler = handler;
        }
        return handler;
    }

    private void destroyHandler() {
        GLHandler handler = this.handler;
        this.handler = null;
        if (handler != null) {
            handler.quit();
        }
    }

    private void onFrameAvailable(SurfaceTexture surfaceTexture) {
        ensureHandler().post(() -> {
            pushFrame(surfaceTexture);
        });
    }

    private static void initGL() {
        EglBase eglBase = EglBase.create(null, EglBase.CONFIG_PIXEL_BUFFER);
        eglBase.createDummyPbufferSurface();
        eglBase.makeCurrent();
    }

    private void pushFrame(SurfaceTexture surfaceTexture) {
        surfaceTexture.updateTexImage();

        final float[] matrix = new float[16];
        surfaceTexture.getTransformMatrix(matrix);

        NERtcVideoFrame videoFrame = new NERtcVideoFrame();
        videoFrame.format = NERtcVideoFrame.Format.TEXTURE_OES;
        videoFrame.width = metaData.width;
        videoFrame.height = metaData.height;
        videoFrame.textureId = textureId;
        videoFrame.transformMatrix = matrix;
        videoFrame.rotation = metaData.rotation;

        callback.onVideoFrame(videoFrame);
    }

    private static int getExternalOESTextureID() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }
}
