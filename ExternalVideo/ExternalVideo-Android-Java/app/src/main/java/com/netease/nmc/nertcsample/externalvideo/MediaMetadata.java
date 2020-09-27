package com.netease.nmc.nertcsample.externalvideo;

import java.util.Locale;

public final class MediaMetadata {
    /**
     * 类型
     */
    public String mimeType;
    /**
     * 是否有视频
     */
    public boolean hasVideo;
    /**
     * 是否有音频
     */
    public boolean hasAudio;
    /**
     * 视频宽
     */
    public int width;
    /**
     * 视频高
     */
    public int height;
    /**
     * 视频选择角度
     */
    public int rotation;
    /**
     * 帧率 （API 23及以上才能获取）
     */
    public int fps;
    /**
     * 文件原始时长 单位 ms
     */
    protected int originDuration;
    /**
     * 文件时长 单位 ms
     */
    public int duration;
    /**
     * 文件码率
     */
    public int bitrate;
    /**
     * 文件创建或修改时间
     */
    public String date;

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "mimetype = %s,hasVideo = %b,hasAudio = %b,width = %d,height = %d," +
                        "rotation = %d,fps = %d,duration = %d,bitrate = %d,date = %s",
                mimeType, hasVideo, hasAudio, width, height, rotation, fps, duration, bitrate, date);
    }
}
