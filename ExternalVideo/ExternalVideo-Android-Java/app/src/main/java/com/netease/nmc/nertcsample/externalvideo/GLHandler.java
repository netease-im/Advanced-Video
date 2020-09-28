package com.netease.nmc.nertcsample.externalvideo;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;

public final class GLHandler {
    private Handler handler;

    public GLHandler(Runnable runnable) {
        HandlerThread thread = new HandlerThread("externalGLThread");
        thread.start();
        handler = new Handler(thread.getLooper());
        post(runnable);
    }

    public final boolean post(Runnable runnable) {
        return handler != null && handler.post(runnable);
    }

    public final boolean postAtTime(Runnable runnable, int delayMs) {
        return handler != null && handler.postAtTime(runnable,
                SystemClock.uptimeMillis() + delayMs);
    }

    public final void quit() {
        Handler handler = this.handler;
        this.handler = null;

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler.getLooper().quit();
        }
    }
}
