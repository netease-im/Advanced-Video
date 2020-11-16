package com.netease.nmc.nertcsample.voicechanger.config;

public class NativeConfig {
    static {
        System.loadLibrary("config");
    }

    public static native String getAppKey();
}
