//
// Created by Wenchao Ding on 2020-09-03.
//

#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_netease_nmc_nertcsample_1videostream_1android_1java_NativeConfig_getAppKey__(JNIEnv *env, jclass) {
    std::string appKey = "请输入您的AppKey";
    return env->NewStringUTF(appKey.c_str());
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_netease_nmc_nertcsample_1videostream_1android_1java_NativeConfig_getStreamURL__(JNIEnv *env, jclass) {
    std::string streamURL = "请填写您的推流地址";
    return env->NewStringUTF(streamURL.c_str());
}