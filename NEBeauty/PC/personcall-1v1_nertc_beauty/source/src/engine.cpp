#include <iostream>
#include <QDebug>
#include "engine.h"

Engine::Engine(QObject *parent /*= nullptr*/)
:QObject(parent)
{

}

Engine::~Engine()
{
    std::cout << "Engine::~Engine begin" << std::endl;
    uninit();
    std::cout << "Engine::~Engine" << std::endl;
}

bool Engine::Init(const char* log_dir_path)
{
    // 创建RTC引擎
    rtc_engine_ = dynamic_cast<nertc::IRtcEngineEx*>(createNERtcEngine());

    // rtcEngineContext信息初始化
    nertc::NERtcEngineContext engine_context;
    memset(&engine_context, 0, sizeof(nertc::NERtcEngineContext));
    engine_context.app_key = app_key_.c_str();
    engine_context.log_dir_path = log_dir_path;
    engine_context.log_level = nertc::kNERtcLogLevelInfo;
    engine_context.log_file_max_size_KBytes = 1024 * 10;
    engine_context.event_handler = this;
    engine_context.video_use_exnternal_render = false;

    // 初始化RTC引擎，
    //在调用 createNERtcEngine() 方法创建 IRtcEngine 对象后，必须先调用该方法进行初始化，才能使用其他方法。
    if (nertc::kNERtcNoError != rtc_engine_->initialize(engine_context))
    {
        std::cout << "ERROR: Failed to initialize NERtc Engine" << std::endl;
        return false;
    }


    return true;
}

int Engine::EnableVideo(const bool &enabled)
{
    return rtc_engine_->enableLocalVideo(enabled);
}

int Engine::EnableAudio(const bool &enabled)
{
    return rtc_engine_->enableLocalAudio(enabled);
}

int Engine::JoinChannel(const std::string &channel_name, const std::string &uid)
{
    return rtc_engine_->joinChannel("", channel_name.c_str(), std::stoull(uid));
}

int Engine::SetupLocalVideo(void* hwnd)
{
    nertc::NERtcVideoCanvas canvas;
    canvas.cb = nullptr;
    canvas.user_data = nullptr;
    canvas.window = hwnd;
    canvas.scaling_mode = nertc::kNERtcVideoScaleFullFill;

    int ret = rtc_engine_->setupLocalVideoCanvas(&canvas);
    if (ret)
    {
        std::cout << "ERROR: Can not setup local video canvas! ERROR CODE:" << ret << std::endl;
    }

    return ret;
}

int Engine::SetupRemoteVideo(quint64 uid, void* hwnd)
{
    nertc::NERtcVideoCanvas canvas;
    canvas.cb = nullptr;
    canvas.user_data = nullptr;
    canvas.window = hwnd;
    canvas.scaling_mode = nertc::kNERtcVideoScaleFullFill;

    int ret = rtc_engine_->setupRemoteVideoCanvas(uid, &canvas);
    if (ret)
    {
        std::cout << "NERtcEngine::setupRemoteVideo: ret: " << ret << std::endl;
    }

    return ret;
}

int Engine::SubscribeRemoteVideoStream(nertc::uid_t uid, nertc::NERtcRemoteVideoStreamType type, bool subscribe)
{
    int ret = rtc_engine_->subscribeRemoteVideoStream(uid, type, subscribe);
    if (ret)
    {
        std::cout << "NERtcEngine::SubscribeRemoteVideoStream: ret: " << ret << std::endl;
    }

    return ret;
}

int Engine::SetParameters(const char* parameters)
{
    int ret = rtc_engine_->setParameters(parameters);
    if (ret)
    {
        std::cout << "NERtcEngine::SetParameters: ret: " << ret << std::endl;
    }

    return ret;
}


#ifdef _WIN32
int Engine::StartBeauty(const QString &path)
{
    if (rtc_engine_)
    {
        std::string str = path.toStdString();

        //美妆路径
        make_up_path_ = str + "\\StickerZipAndIcons\\makeup_sticker.bundle\\makeup\\template.json";

        //贴纸路径
        sticker_folder_path_ = str + "\\\StickerZipAndIcons\\2d_sticker.bundle\\";

        //滤镜路径
        image_filters_folder_path_ = str + "\\Filters\\filters.bundle\\";

        const char* ch = str.c_str();
        return rtc_engine_->startBeauty(ch);
    }
    return -1;
}

void Engine::StopBeauty()
{
    if (rtc_engine_)
    {
        rtc_engine_->stopBeauty();
    }
}

void Engine::EnableNertcBeauty(const bool &enable)
{
    if (rtc_engine_)
    {
        rtc_engine_->enableBeauty(enable);
    }
}

int Engine::SelectBeautyFilter(const std::string &filter_path, const int &val)
{
    if (rtc_engine_)
    {
        if (0 != strcmp("none", filter_path.c_str()))
        {
            std::string file_path = image_filters_folder_path_ + filter_path;
            int ret = rtc_engine_->addBeautyFilter(file_path.c_str());
            ret = rtc_engine_->setBeautyFilterLevel(val / 100.f);
            return ret;
        }
        else
        {
            int ret = rtc_engine_->removeBeautyFilter();
            return ret;
        }
    }
    return -1;
}

int Engine::SetBeautyEffect(const int &type, float level)
{
    if (rtc_engine_)
    {
        return rtc_engine_->setBeautyEffect((nertc::NERtcBeautyEffectType)type, level);
    }
    return -1;
}
#endif


void Engine::uninit()
{
    if (rtc_engine_)
    {
#ifdef _WIN32
        rtc_engine_->stopBeauty();
#endif
        int ret = rtc_engine_->enableLocalAudio(false);
        ret = rtc_engine_->enableLocalVideo(false);
        ret = rtc_engine_->leaveChannel();

        rtc_engine_->release(true);
        destroyNERtcEngine((void*&)rtc_engine_);
        rtc_engine_ = nullptr;
    }
}


void Engine::onJoinChannel(nertc::channel_id_t   cid,
                           nertc::uid_t          uid, 
                           nertc::NERtcErrorCode result, 
                           uint64_t              elapsed)
{
    qDebug() << "Engine::onJoinChannel";
    Q_EMIT sigJoinChannel(result);

}

void Engine::onUserJoined(nertc::uid_t uid,
                          const char * user_name)
{
    qDebug() << "Engine::onUserJoined";
    Q_EMIT sigUserJoined(uid, user_name);
}

void Engine::onUserVideoStart(nertc::uid_t                 uid, 
                              nertc::NERtcVideoProfileType max_profile)
{
    qDebug() << "Engine::onUserVideoStart";
    Q_EMIT sigUserVideoStart(uid, max_profile);
}

void Engine::onLeaveChannel(nertc::NERtcErrorCode result)
{
    qDebug() << "Engine::onLeaveChannel";
}

void Engine::onUserLeft(nertc::uid_t                   uid, 
                        nertc::NERtcSessionLeaveReason reason)
{
    qDebug() << "Engine::onUserLeft";
    Q_EMIT sigUserLeft(uid, reason);
}
