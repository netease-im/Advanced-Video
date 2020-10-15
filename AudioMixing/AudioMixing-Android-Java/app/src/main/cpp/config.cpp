//
// Created by Wenchao Ding on 2020-09-03.
//

#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_netease_nmc_nertcsample_audiomixing_NativeConfig_getAppKey__(JNIEnv *env, jclass) {
    std::string appKey = "请输入您的AppKey";
    return env->NewStringUTF(appKey.c_str());
}