package com.netease.nmc.nertcsample.externalvideo;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public final class AHandler {
    private static final AtomicInteger num = new AtomicInteger(0);

    private Handler handler;

    public AHandler(Runnable runnable) {
        HandlerThread thread = new HandlerThread("AHandler-Thread#" + num.incrementAndGet());
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

    public final void postAndWait(Runnable runnable) {
        final CountDownLatch wait = new CountDownLatch(1);
        post(() -> {
            runnable.run();
            wait.countDown();
        });
        try {
            wait.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
