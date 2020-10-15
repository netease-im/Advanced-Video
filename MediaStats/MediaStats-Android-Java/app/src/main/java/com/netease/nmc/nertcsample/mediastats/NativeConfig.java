package com.netease.nmc.nertcsample.mediastats;

public class NativeConfig {
    static {
        System.loadLibrary("config");
    }

    public static native String getAppKey();
}
