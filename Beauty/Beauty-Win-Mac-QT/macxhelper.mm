#include "macxhelper.h"
#include <CoreVideo/CVPixelBuffer.h>
#import <AppKit/AppKit.h>
Macxhelper::Macxhelper(QObject* parent)
    :QObject(parent)
{

}




void Macxhelper::getCVPixelbufferInfo(void* cvref, void* &data, int &width, int &height)
{
    CVPixelBufferRef cvPixelBufferRef = CVPixelBufferRef(cvref);

    // 如果想要对数据进行修改就必须对向前数据进行锁定
    CVPixelBufferLockBaseAddress(cvPixelBufferRef, kCVPixelBufferLock_ReadOnly);
    // 处理图像数据
    // 图像出来的原始数据是 R G R A 每个像素 4 个字节 32 位的数据
    // 获取宽高
    OSType format = CVPixelBufferGetPixelFormatType(cvPixelBufferRef);
    width = CVPixelBufferGetWidth(cvPixelBufferRef);
    height = CVPixelBufferGetHeight(cvPixelBufferRef);
    // 获取指向数据内容的指针
    void *buffer0 = (void *)CVPixelBufferGetBaseAddress(cvPixelBufferRef);

    data = buffer0;
    CVPixelBufferUnlockBaseAddress(cvPixelBufferRef, kCVPixelBufferLock_ReadOnly);
}


int Macxhelper::getWindowId(WId wid)
{
    NSView *nativeView = reinterpret_cast<NSView *>(wid);


    return 0;
}
