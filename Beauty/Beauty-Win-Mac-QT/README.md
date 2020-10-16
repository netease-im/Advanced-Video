# Beauty-iOS-ObjC

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现美颜的功能。

在这个示例项目中包含了以下功能：

- 加入通话和离开通话
- 打开美颜和关闭美颜
- 切换大小屏显示远端/本地用户视频

## 环境准备

包括云信环境和第三方美颜解决方案相芯环境

### 云信环境

1. 这个开源示例项目基于一对一视频通话，关于云信**环境准备**，**运行示例项目**，**功能实现**章节请参考1对1音视频通话




### 获取相芯SDK 的证书

1. 由于本sample美颜的功能是使用相芯SDK实现的。所以您在使用前需要的获取相芯的证书。

2. 复制authpack.h文件到auth路径下

3.详细参考相芯[集成文档](https://github.com/Faceunity/FULiveDemo/blob/master/docs/iOS_Nama_SDK_%E9%9B%86%E6%88%90%E6%8C%87%E5%AF%BC%E6%96%87%E6%A1%A3.md)

## 功能实现

1. 云信接口提供：

```objc
// 在代理方法中对视频数据进行处理
 void onCaptureVideoFrame1(void *data,
                                     NERtcVideoType type,
                                     uint32_t width,
                                     uint32_t height,
                                     uint32_t count,
                                     uint32_t offset[kNERtcMaxPlaneCount],
                                     uint32_t stride[kNERtcMaxPlaneCount],
                                     NERtcVideoRotation rotation)
    {

        if(m_engine.isSmoothEnable()){
            #ifdef win32
            m_engine.renderFrame(type, data,width,height,0);
            #else
                if(type == kNERtcVideoTypeCVPixelBuffer){
                    void* buf = nullptr;
                    int w = 0;
                    int h = 0;
                    Macxhelper::getCVPixelbufferInfo(data, buf,w, h);

                    m_engine.renderFrame(type, buf, w, h,0);

                }
            #endif
        }
    }
```

2. faceunity model为相芯美颜功能实现，您也可以替换成自己接入的第三方方案。相芯美颜功能具体参看[相芯接入文档](https://github.com/Faceunity/FULiveDemo/blob/master/docs/iOS_Nama_SDK_%E9%9B%86%E6%88%90%E6%8C%87%E5%AF%BC%E6%96%87%E6%A1%A3.md)
   

   


