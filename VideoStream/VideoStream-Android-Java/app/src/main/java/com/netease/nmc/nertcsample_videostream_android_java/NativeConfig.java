package com.netease.nmc.nertcsample_videostream_android_java;

public class NativeConfig {

    static {
        System.loadLibrary("config");
    }

    public static native String getAppKey();
    public static native String getStreamURL();

}
