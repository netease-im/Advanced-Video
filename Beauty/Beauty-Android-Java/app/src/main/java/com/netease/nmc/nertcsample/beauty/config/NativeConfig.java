package com.netease.nmc.nertcsample.beauty.config;

public class NativeConfig {
    static {
        System.loadLibrary("config");
    }

    public static native String getAppKey();
}
