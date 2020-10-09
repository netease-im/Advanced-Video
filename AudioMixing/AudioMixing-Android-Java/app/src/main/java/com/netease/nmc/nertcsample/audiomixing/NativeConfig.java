package com.netease.nmc.nertcsample.audiomixing;

public class NativeConfig {
    static {
        System.loadLibrary("config");
    }

    public static native String getAppKey();
}
