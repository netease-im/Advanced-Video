package com.netease.nmc.nertcsample.voicechanger;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class VoiceChangerApplication extends Application {
    @SuppressLint("StaticFieldLeak")
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
