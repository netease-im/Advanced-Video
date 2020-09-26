package com.netease.nertcbeautysample.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadHelper {
    private final ThreadPoolExecutor mExecutorService;

    private ThreadHelper() {
        // copy from AsyncTask THREAD_POOL_EXECUTOR
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
            }
        };
        int cpuCount = Runtime.getRuntime().availableProcessors();
        int corePoolSize = Math.max(2, Math.min(cpuCount - 1, 4));
        int maxPoolSize = cpuCount * 2 + 1;
        BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(128);
        mExecutorService = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 30, TimeUnit.SECONDS, blockingQueue, threadFactory);
        mExecutorService.allowCoreThreadTimeOut(true);
    }

    public static ThreadHelper getInstance() {
        return ThreadHelperHolder.instance;
    }


    /**
     * 无返回值的异步任务
     *
     * @param r
     */
    public void execute(Runnable r) {
        if (r != null) {
            mExecutorService.execute(r);
        }
    }


    private static class ThreadHelperHolder {
        private static final ThreadHelper instance = new ThreadHelper();
    }
}
