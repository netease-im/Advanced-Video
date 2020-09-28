package com.netease.nmc.nertcsample.externalvideo;

import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

class MediaMetadataExtractor {
    private static final String TAG = "MediaExtractor";

    public static final String MIMETYPE_AUDIO = "audio/";
    public static final String MIMETYPE_VIDEO = "video/";

    public static MediaMetadata extractVideo(String file) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(file);
        } catch (Throwable tr) {
            tr.printStackTrace();
            return null;
        }

        MediaMetadata metaData = new MediaMetadata();
        metaData.mimeType = extractMetadata(retriever, MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        metaData.hasVideo = extractMetadataBool(retriever, MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO, false);
        metaData.hasAudio = extractMetadataBool(retriever, MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO, false);
        metaData.width = extractMetadataInt(retriever, MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH, 0);
        metaData.height = extractMetadataInt(retriever, MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT, 0);
        metaData.rotation = extractMetadataInt(retriever, MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION, 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            metaData.fps = Math.round(extractMetadataFloat(retriever, MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE, 0f));
        }
        metaData.duration = extractMetadataInt(retriever, MediaMetadataRetriever.METADATA_KEY_DURATION, 0);
        metaData.originDuration = metaData.duration;
        metaData.bitrate = extractMetadataInt(retriever, MediaMetadataRetriever.METADATA_KEY_BITRATE, 0);
        metaData.date = extractMetadata(retriever, MediaMetadataRetriever.METADATA_KEY_DATE);

        try {
            retriever.release();
        } catch (Throwable tr) {
            tr.printStackTrace();
        }

        return metaData;
    }

    private static String extractMetadata(MediaMetadataRetriever retriever, int key) {
        try {
            return retriever.extractMetadata(key);
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        return null;
    }

    private static boolean extractMetadataBool(MediaMetadataRetriever retriever, int key, boolean def) {
        boolean value = def;
        try {
            String metadata = retriever.extractMetadata(key);
            if (!TextUtils.isEmpty(metadata)) {
                value = "yes".equals(metadata);
            }
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        return value;
    }

    private static int extractMetadataInt(MediaMetadataRetriever retriever, int key, int def) {
        int value = def;
        try {
            String metadata = retriever.extractMetadata(key);
            if (!TextUtils.isEmpty(metadata)) {
                value = Integer.parseInt(metadata);
            }
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        return value;
    }

    private static float extractMetadataFloat(MediaMetadataRetriever retriever, int key, float def) {
        float value = def;
        try {
            String metadata = retriever.extractMetadata(key);
            if (!TextUtils.isEmpty(metadata)) {
                value = Float.parseFloat(metadata);
            }
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        return value;
    }
}
