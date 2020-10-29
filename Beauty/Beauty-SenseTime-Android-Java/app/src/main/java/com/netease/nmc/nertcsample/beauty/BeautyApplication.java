package com.netease.nmc.nertcsample.beauty;

import android.app.Application;
import android.content.Context;

public class BeautyApplication extends Application {
    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }
}
