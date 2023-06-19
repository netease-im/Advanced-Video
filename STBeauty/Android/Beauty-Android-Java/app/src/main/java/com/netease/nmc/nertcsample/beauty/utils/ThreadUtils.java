package com.netease.nmc.nertcsample.beauty.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description
 * @Author Lu Guoqiang
 * @Time 2021/7/14 1:40 下午
 */
public class ThreadUtils {
    private static final String TAG = "MyThreadUtils";

    private final static Handler mHandler = new Handler(Looper.getMainLooper());

    private ExecutorService mExecutorService;

    private static class ThreadPoolManagerHolder {
        private static ThreadUtils instance = new ThreadUtils();
    }

    private ThreadUtils() {
    }

    public static ThreadUtils getInstance() {
        return ThreadPoolManagerHolder.instance;
    }

    public void initThreadPool() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newCachedThreadPool();
        }
    }

    public void executeRunnable(Runnable runnable) {
        try {
            if (mExecutorService != null && !mExecutorService.isShutdown()) {
                mExecutorService.execute(runnable);
            }
        } catch (RejectedExecutionException e) {
            int threadCount = ((ThreadPoolExecutor) mExecutorService).getActiveCount();
            Log.i(TAG,
                    "[executeRunable]: current alive thread count = " + threadCount);
            e.printStackTrace();
        }
    }

    public void shutdownExecutor() {
        if (mExecutorService != null && !mExecutorService.isShutdown()) {
            mExecutorService.shutdown();
            mExecutorService = null;
        }
    }

    public void runOnUIThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }
    }

    public boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public void runOnSubThread(Runnable runnable) {
        executeRunnable(runnable);
    }
}
