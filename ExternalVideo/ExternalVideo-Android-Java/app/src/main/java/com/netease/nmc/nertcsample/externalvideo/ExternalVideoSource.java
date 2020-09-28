package com.netease.nmc.nertcsample.externalvideo;

import com.netease.lava.nertc.sdk.video.NERtcVideoFrame;

public abstract class ExternalVideoSource {
    public interface Callback {
        void onVideoFrame(NERtcVideoFrame videoFrame);
    }

    public abstract boolean start();

    public abstract void stop();
}
