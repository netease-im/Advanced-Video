package com.netease.nmc.nertcsample.beauty.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.netease.nmc.nertcsample.beauty.utils.ContextHolder;
import com.netease.nmc.nertcsample.beauty.utils.STLicenseUtils;
import com.netease.nmc.nertcsample.beauty.utils.ThreadUtils;

public class NEApplication extends Application {
    private static final String TAG = "NEApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        ContextHolder.initial(this);
        ThreadUtils.getInstance().initThreadPool();
    }
}
