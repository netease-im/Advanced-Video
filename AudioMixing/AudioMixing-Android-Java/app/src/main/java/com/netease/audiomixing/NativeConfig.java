package com.netease.audiomixing;

public class NativeConfig {
    static {
        System.loadLibrary("config");
    }

    public static native String getAppKey();
}
