package com.netease.nmc.nertcsample.beauty.helper;

import com.sensetime.stmobile.STMobileAnimalNative;
import com.sensetime.stmobile.STMobileEffectNative;
import com.sensetime.stmobile.STMobileFaceAttributeNative;
import com.sensetime.stmobile.STMobileFaceVerifyNative;
import com.sensetime.stmobile.STMobileHumanActionNative;
import com.sensetime.stmobile.STMobileObjectTrackNative;

public interface NativeManagerI {

    void createAnimalHandle(int config);    // 创建动物检测Handle
    void destroyAnimalHandle();             // 销毁动物检测Handle

    void createVerifyHandle();              // 创建VerifyHandle
    void destroyVerifyHandle();             // 销毁VerifyHandle

    void createEffectNative(int config);    // 创建EffectHandle
    void destroyEffectNative();             // 销毁EffectHandle

    void createObjectTrackHandle();         // 创建通用物体追踪Handle
    void destroyObjectTrackHandle();        // 销毁通用物体追踪Handle

    void createFaceAttributeHandle();       // 创建人脸属性检测Handle
    void destroyFaceAttributeHandle();      // 销毁人脸属性检测Handle

    STMobileAnimalNative getAnimalNative();               // STMobileAnimalNative instance
    STMobileObjectTrackNative getObjectTrackNative();     // STMobileObjectTrackNative instance
    STMobileFaceVerifyNative getFaceVerifyNative();       // STMobileFaceVerifyNative instance
    STMobileFaceAttributeNative getFaceAttributeNative(); // STMobileFaceAttributeNative instance
    STMobileEffectNative getEffectNative();               // STMobileEffectNative instance
    STMobileHumanActionNative getHumanActionNative();     // STMobileHumanActionNative instance
}
