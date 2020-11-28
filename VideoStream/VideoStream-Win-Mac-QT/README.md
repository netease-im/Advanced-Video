# VideoStream-Win-Mac-QT

该示例项目演示了如何快速集成[网易云信](https://yunxin.163.com)新一代（G2）音视频SDK，实现旁路推流功能。

**旁路推流**：

将正在进行实时音视频通话时频道的画面同步到云端进行云端混流，并将混流后的频道直播流推流给第三方CDN或者云信视频直播。

 **在这个示例项目中包含了以下功能**：

- 加入房间
- 配置推流地址
- 开始、停止旁路推流
- 离开房间

## 环境准备

- Qt5.14.2  QCreator
- VS2017
- MacOS 10.15

## 运行示例项目

获取**APPkey**

- 首先在 [网易云信](https://yunxin.163.com) 注册账号

- 于「应用」一栏中创建您的项目

- 于应用详情页中找到「App Key」管理即可查看Appkey

- 此时您的Appkey缺乏权限，申请试用「音视频通话」，专属客户经理会联系您并为您开通权限

- 将AppKey填写进nrtc_engine.h

```objective-c
#define APP_KEY ""    // put your app key here, testing
```

**获取推流地址**

- 申请试用「直播」产品，专户客户经理会为您提供用于体验的推拉流地址





## 功能实现

1. 引擎初始化，配置音视频相关参数。

   ```c++
   bool NRTCEngine::Init(const char *app_key, const char *log_dir_path, uint32_t log_file_max_size_KBytes){
       rtc_engine_ = static_cast<IRtcEngineEx *>(createNERtcEngine());
       rtc_engine_context_.app_key = app_key;
       rtc_engine_context_.log_dir_path = log_dir_path;
       rtc_engine_context_.log_level = kNERtcLogLevelInfo;
       rtc_engine_context_.log_file_max_size_KBytes = log_file_max_size_KBytes;
       rtc_engine_context_.event_handler = rtc_engine_handler_.get();
       rtc_engine_context_.video_use_exnternal_render = false;
   
       //Init NERTC engine
       if (kNERtcNoError != rtc_engine_->initialize(rtc_engine_context_)){
           qDebug("ERROR: Failed to initialize NERtc Engine\n");
           return false;
       }
   
       //Init audio/video device manager
       auto ret = rtc_engine_->queryInterface(kNERtcIIDAudioDeviceManager, (void**)&audio_device_manager);
       ret = rtc_engine_->queryInterface(kNERtcIIDVideoDeviceManager, (void**)&video_device_manager);
   
       return true;
   }
   ```

2. 加入和离开房间，调用SDK接口加入和退出音视频房间。

   ```c++
   /* ---- 加入房间 ---- */
   int NRTCEngine::joinChannel(const QString& token, const QString& roomid, const QString& uid,
                               bool autoStartVideo, bool autoStartAudio, int video_resolution)
   {
       int ret = kNERtcNoError;
   
       //set video/audio auto starting config
       auto_start_audio_ = autoStartAudio;
       auto_start_video_ = autoStartVideo;
       setLocalVideoProfileType((NERtcVideoProfileType)video_resolution);
   
   //    NRTCParameter param;
   //    param.auto_start_local_audio = autoStartAudio;
   //    param.auto_start_local_video = autoStartVideo;
   //    setParameter(param);
   
       //Join channel
       cur_my_uid_ = uid.toULongLong();
       ret = rtc_engine_->joinChannel(token.toUtf8().data(), roomid.toUtf8().data(), cur_my_uid_);
   
       if(ret == kNERtcNoError){
           emit joiningChannel();
           qDebug() << "[INFO] join channel successfully!";
       }else{
           qDebug("[ERROR] can't join channel, ERROR CODE: %d", ret);
       }
       qDebug("[INFO] current connection state: %d", rtc_engine_->getConnectionState());
       return ret;
   }
   
   /* ---- 离开房间 ---- */
   int NRTCEngine::leaveChannel()
   {
       int ret = kNERtcNoError;
       qDebug("[INFO] current connection state: %d", rtc_engine_->getConnectionState());
       if (rtc_engine_){
           ret = rtc_engine_->enableLocalAudio(false);
           ret = rtc_engine_->enableLocalVideo(false);
           ret = rtc_engine_->leaveChannel();
           if (kNERtcNoError == ret){
               emit leavingChannel();
           }else{
               qDebug("[ERROR] Can't leave channel, ERROR CODE: %d", ret);
           }
       }
       return ret;
   }
   ```

3. 添加推流任务。

   ```c++
   
   QByteArray NRTCEngine::startLiveStream(const LiveStreamUsers &liveStreamUsers, const QString &pushUrl)
   {
       //设置推流用户的UI布局
       auto users = new NERtcLiveStreamUserTranscoding[liveStreamUsers.size()];
       for (uint32_t i = 0; i < liveStreamUsers.size(); i++) {
           auto& liveStreamUser = liveStreamUsers[i];
           users[i].x = 0;
           users[i].y = 0;
           users[i].uid = liveStreamUser.uid;
           users[i].width = liveStreamUser.primaryUser ? LIVE_STREAM_WIDTH : 0;
           users[i].height = liveStreamUser.primaryUser ? LIVE_STREAM_HEIGHT : 0;
           users[i].adaption = kNERtcLsModeVideoScaleFit;
           users[i].audio_push = true;
           users[i].video_push = liveStreamUser.primaryUser;
       }
   	//设置整体布局
       NERtcLiveStreamLayout layout;
       layout.users = users;
       layout.width = LIVE_STREAM_WIDTH;
       layout.height = LIVE_STREAM_HEIGHT;
       layout.user_count = liveStreamUsers.size();
       layout.background_color = 0xFFFFFF;
   
       //添加推流任务
       QByteArray byteTaskId;
       auto tmpTaskId = QUuid::createUuid().toString();
       tmpTaskId.remove("{").remove("}").remove("-");
       byteTaskId = tmpTaskId.toUtf8();
   
       QByteArray bytePushUrl = pushUrl.toUtf8();
       NERtcLiveStreamTaskInfo info;
       memset(&info, 0x00, sizeof(info));
       info.layout = layout;
       info.ls_mode = kNERtcLsModeVideo;
       info.server_record_enabled = false;
       memcpy(info.task_id, byteTaskId.data(), byteTaskId.size());
       memcpy(info.stream_url, bytePushUrl.data(), bytePushUrl.size());
   
       if (rtc_engine_->addLiveStreamTask(info) != 0)
           byteTaskId.clear();
   
       if (users) delete []users;
   
       return byteTaskId;
   }
   
   ```

4. 更新推流任务。

   ```c++
   bool NRTCEngine::updateLiveStream(const QByteArray &byteTaskId, const LiveStreamUsers &liveStreamUsers, const QString &pushUrl)
   {
       //推流用户的UI布局
       auto users = new NERtcLiveStreamUserTranscoding[liveStreamUsers.size()];
       for (uint32_t i = 0; i < liveStreamUsers.size(); i++) {
           auto& liveStreamUser = liveStreamUsers[i];
           users[i].x = 0;
           users[i].y = 0;
           users[i].uid = liveStreamUser.uid;
           users[i].width = liveStreamUser.primaryUser ? LIVE_STREAM_WIDTH : 0;
           users[i].height = liveStreamUser.primaryUser ? LIVE_STREAM_HEIGHT : 0;
           users[i].adaption = kNERtcLsModeVideoScaleFit;
           users[i].audio_push = true;
           users[i].video_push = liveStreamUser.primaryUser;
       }
   	//设置推流主布局
       NERtcLiveStreamLayout layout;
       layout.users = users;
       layout.width = LIVE_STREAM_WIDTH;
       layout.height = LIVE_STREAM_HEIGHT;
       layout.user_count = liveStreamUsers.size();
       layout.background_color = 0xFFFFFF;
   
		//更新任务
       QByteArray bytePushUrl = pushUrl.toUtf8();
       NERtcLiveStreamTaskInfo info;
       memset(&info, 0x00, sizeof(info));
       info.layout = layout;
       info.ls_mode = kNERtcLsModeVideo;
       info.server_record_enabled = false;
       memcpy(info.task_id, byteTaskId.data(), byteTaskId.size());
       memcpy(info.stream_url, bytePushUrl.data(), bytePushUrl.size());
   
       int result = rtc_engine_->updateLiveStreamTask(info);
   
       if (users) delete []users;
   
       return result == 0;
   }
   ```

5. 移除推流任务。

   ```c++
   bool NRTCEngine::stopLiveStream(const QByteArray &taskId)
   {
       return rtc_engine_->removeLiveStreamTask(taskId.data()) == 0;
   }
   ```
   
   
   
   
   
   
   
   
   
   

