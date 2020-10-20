# DeviceManager-Win-MacOS-QT

## 功能介绍

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现音视频管理。

## 环境准备，运行示例项目，多人视频通话功能实现

这个开源示例项目基于多人视频通话，关于**环境准备**，**运行示例项目**，**功能实现**章节请参考多人视频通话

## 功能实现

### 设置音频质量和模式

```c++
void NRTCEngine::setLocalVideoProfileType(nertc::NERtcVideoProfileType max_profile){
    if (rtc_engine_){
        NERtcVideoConfig video_config_;
        qDebug("max_profile: %d", max_profile);
        video_config_.max_profile = max_profile;
        video_config_.crop_mode_ = kNERtcVideoCropModeDefault;
        int ret = rtc_engine_->setVideoConfig(video_config_);
        if(ret)
            qDebug("[ERROR] Cannot set video config, ERROR CODE: %d", ret);
        else{
            QString profile_str;
            switch(max_profile){
            case 0:
                profile_str = "160x90/120, 15fps";
                break;
            case 1:
                profile_str = "320x180/240, 15fps";
                break;
            case 2:
                profile_str = "640x360/480, 30fps";
                break;
            case 3:
                profile_str = "1280x720, 30fps";
                break;
            case 4:
                profile_str = "1920x1080, 30fps";
                break;
            case 5:
                profile_str = "None";
                break;
            default:
                profile_str = "640x360/480, 30fps";

            }
            qDebug("[INFO] set video config: %s", profile_str.toUtf8().data());
        }
    }
}
```

### 设置音频质量

```c++
void NRTCEngine::setLocalAudioProfileType(NERtcAudioProfileType max_profile)
{
    if (rtc_engine_){
        int ret = rtc_engine_->setAudioProfile(max_profile, kNERtcAudioScenarioDefault);
        if(ret)
            qDebug("[ERROR] Cannot set audio config, ERROR CODE: %d", ret);
    }
}
```


