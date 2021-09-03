package com.netease.nmc.nertcsample.externalvideo;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.netease.lava.nertc.sdk.video.NERtcVideoFrame;

public class ExternalTextureVideoSource extends ExternalVideoSource {
    private static final String TAG = "ExternalTexture";

    private final String path;
    private final MediaMetadata metaData;
    private final Callback callback;

    public static ExternalVideoSource create(String path, Callback callback) {
        if (TextUtils.isEmpty(path)) {
            Log.e(TAG, "Media file path is empty");
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

    private AHandler glHandler;

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

    /**
     * 1. 等待GL线程生成纹理
     * 2. 创建SurfaceTexture，注册视频帧回调
     * 3. 创建Surface
     */
    private void createSurface() {
        if (textureId <= 0) {
            ensureGLHandler().postAndWait(() ->
                    textureId = GLHelper.genTexture());
        }
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
        this.textureId = 0;
    }

    /**
     * 播放视频，将视频帧输出到Surface
     */
    private boolean playVideo(String path, Surface surface) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setVolume(0, 0);
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
            // 设置循环播放
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnErrorListener((mediaPlayer, what, extra) -> {
                Log.e(TAG, "onError:"
                        + " what=" + what
                        + " extra=" + extra
                );
                // 如果发生错误，重新播放
                return false;
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

    private AHandler ensureGLHandler() {
        AHandler handler = this.glHandler;
        if (handler == null) {
            // 传递线程初始化方法
            handler = new AHandler(GLHelper::initGLContext);
            this.glHandler = handler;
        }
        return handler;
    }

    private void destroyHandler() {
        AHandler handler = this.glHandler;
        this.glHandler = null;
        if (handler != null) {
            handler.quit();
        }
    }

    private void onFrameAvailable(SurfaceTexture surfaceTexture) {
        // 投递到GL线程处理
        ensureGLHandler().post(() ->
            onVideoFrame(surfaceTexture)
        );
    }

    private void onVideoFrame(SurfaceTexture surfaceTexture) {
        // 更新纹理内容
        surfaceTexture.updateTexImage();

        NERtcVideoFrame videoFrame = new NERtcVideoFrame();
        // 外部纹理类型
        videoFrame.format = NERtcVideoFrame.Format.TEXTURE_OES;
        // 视频尺寸
        videoFrame.width = metaData.width;
        videoFrame.height = metaData.height;
        // 纹理名
        videoFrame.textureId = textureId;
        // 纹理矩阵
        final float[] matrix = new float[16];
        surfaceTexture.getTransformMatrix(matrix);
        videoFrame.transformMatrix = matrix;
        // 视频角度
        videoFrame.rotation = metaData.rotation;

        callback.onVideoFrame(videoFrame);
    }
}
