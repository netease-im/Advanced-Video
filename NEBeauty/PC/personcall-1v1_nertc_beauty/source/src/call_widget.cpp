#include <QApplication>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QMoveEvent>
#include <QDebug>
#include "json/json.h"
#include "call_widget.h"
#include "engine.h"
#include "video_widget.h"
#include "room_button.h"
#include "beauty_tabwidget.h"

CallWidget::CallWidget(Engine* engine, QWidget *parent /*= nullptr*/)
:engine_(engine)
,QWidget(parent)
{
    setUi();

    room_button_ = new RoomButton(this);

    nertc_beauty_tabwidget_ = new BeautyTabWidget(this);

    connect(engine_, &Engine::sigJoinChannel, this, &CallWidget::onJoinChannel);
    connect(engine_, &Engine::sigUserJoined, this, &CallWidget::onUserJoin);
    connect(engine_, &Engine::sigUserVideoStart, this, &CallWidget::onUserVideoStart);
    connect(engine_, &Engine::sigUserLeft, this, &CallWidget::onUserLeft);

    //
    connect(room_button_, &RoomButton::sigStartBeauty, this, &CallWidget::onStartBeauty);
    connect(room_button_, &RoomButton::sigNertcBeautySetting, this, [=] {nertc_beauty_tabwidget_->show(); });

    connect(nertc_beauty_tabwidget_, &BeautyTabWidget::sigBeautyStart, this, &CallWidget::onStartBeauty);
    connect(nertc_beauty_tabwidget_, &BeautyTabWidget::sigBautyEnable, this, &CallWidget::onBeautyEnable);
    connect(nertc_beauty_tabwidget_, &BeautyTabWidget::sigBeautyMirror, this, &CallWidget::onBeautyMirror);
    connect(nertc_beauty_tabwidget_, &BeautyTabWidget::sigBeautyMakeup, this, &CallWidget::onBeautyMakeup);

    connect(nertc_beauty_tabwidget_, &BeautyTabWidget::sigItemStickerChanged, this, &CallWidget::onItemStickerChanged);
    connect(nertc_beauty_tabwidget_, &BeautyTabWidget::sigFilterChanged, this, &CallWidget::onFilterChanged);
    connect(nertc_beauty_tabwidget_, &BeautyTabWidget::sigBeautyChanged, this, &CallWidget::onBeautyChanged);
}

CallWidget::~CallWidget()
{
    qDebug() << "~CallWidget~CallWidget~CallWidget";
}

int CallWidget::JoinChannel(const JoinInfo &join_info)
{
    //Init engine
    QString log_dir_path = qApp->applicationDirPath() + "\\NERTC";
    bool init = engine_->Init(log_dir_path.toStdString().c_str());
    if (true == init)
    {
        engine_->EnableVideo(join_info.open_camera_);
        engine_->EnableAudio(join_info.open_mic_);

        if (true == join_info.open_1v1_)
        {
            Json::Value values;
            values["channel_1v1_mode_enabled"] = true; //1v1

            Json::FastWriter writer;
            std::string parameters = writer.write(values);
            engine_->SetParameters(parameters.c_str());
        }

        // 设置自己的渲染窗口
        void* hwnd = (void*)local_video_widget_->GetVideoHwnd();
        engine_->SetupLocalVideo(hwnd);

        return engine_->JoinChannel(join_info.channel_name_.toStdString(), join_info.uid_.toStdString());
    }
}

void CallWidget::resizeEvent(QResizeEvent* event)
{
    QPoint p(this->rect().left(), this->rect().bottom());
    p = mapToGlobal(p);

    room_button_->move(p.x() + this->width() / 2 - room_button_->width() / 2, p.y() - 92);

    return QWidget::resizeEvent(event);
}

void CallWidget::moveEvent(QMoveEvent* event)
{
    room_button_->move(event->pos().x() + this->width() / 2 - room_button_->width() / 2, event->pos().y() + this->rect().bottom() - 92);

    return QWidget::moveEvent(event);
}

void CallWidget::setUi()
{
    this->setMinimumSize(QSize(1366, 718));

    QWidget* video_page = new QWidget();
    QVBoxLayout* main_layout = new QVBoxLayout(video_page);
    main_layout->setSpacing(0);
    main_layout->setContentsMargins(0, 0, 0, 0);

    QWidget* central_widget = new QWidget(video_page);
    central_widget->setStyleSheet("QWidget{background-color: rgb(0, 0, 0);}");

    vertical_layout_ = new QVBoxLayout(central_widget);
    vertical_layout_->setSpacing(2);
    vertical_layout_->setContentsMargins(58, 8, 58, 8);

    video_hlayout_ = new QHBoxLayout();
    vertical_layout_->addLayout(video_hlayout_);

    main_layout->addWidget(central_widget);

    QHBoxLayout* h_layout = new QHBoxLayout(this);
    h_layout->setSpacing(0);
    h_layout->setContentsMargins(0, 0, 0, 0);
    h_layout->addWidget(video_page);

    local_video_widget_ = new VideoWidget(this);
    video_hlayout_->addWidget(local_video_widget_);

    remote_video_widget_ = new VideoWidget(this);
    remote_video_widget_->setVisible(false);
}

void CallWidget::adjustVideoLayout()
{
    clearLayout();
    video_hlayout_->addWidget(local_video_widget_);
    local_video_widget_->setVisible(true);
    if (2 == user_count_)
    {
        video_hlayout_->addWidget(remote_video_widget_);
        remote_video_widget_->setVisible(true);
    }
}

void CallWidget::clearLayout()
{
    while (video_hlayout_->count())
    {
        video_hlayout_->itemAt(0)->widget()->setParent(nullptr);
    }

    local_video_widget_->setVisible(false);
    remote_video_widget_->setVisible(false);
}

void CallWidget::onJoinChannel(const int& reson)
{
    qDebug() << "CallWidget::onJoinChannel:" << reson;

    this->showNormal();
    this->resize(1184, 666);
    room_button_->setVisible(true);
}

void CallWidget::onUserJoin(const quint64& uid, const QString& name)
{
    if (nullptr == remote_video_widget_)
    {
        remote_video_widget_ = new VideoWidget(this);
    }
    void* hwnd = remote_video_widget_->GetVideoHwnd();
    engine_->SetupRemoteVideo(uid, hwnd);
    user_count_++;;
    adjustVideoLayout();
}

void CallWidget::onUserVideoStart(const quint64& uid, const int& profile)
{
    qDebug() << "NECallWidget::onUserVideoStart: uid:" << uid << ", profile: " << profile;

    int ret = engine_->SubscribeRemoteVideoStream(uid, nertc::kNERtcRemoteVideoStreamTypeHigh, true);
    if (ret)
    {
        qDebug() << "can not subscribe remote video stream! ret: " << ret;
    }
}

void CallWidget::onUserLeft(const quint64& uid, const int& result)
{
    qDebug() << "CallWidget::onUserLeft";
    user_count_--;
    adjustVideoLayout();
    engine_->SetupRemoteVideo(uid, nullptr);
}

void CallWidget::onBeautyChanged(const int& id, const int &val)
{
    engine_->SetBeautyEffect(id, val / 100.f);
}

void CallWidget::onFilterChanged(const QString& path, const int &val)
{
    engine_->SelectBeautyFilter(std::string(path.toLocal8Bit()), val);
}

void CallWidget::onItemStickerChanged(const std::string &str)
{
    engine_->SelectBeautySticker(str);
}

void CallWidget::onStartBeauty(const bool& start_enabled)
{
    nertc_beauty_tabwidget_->SetBeautyStartState(start_enabled);
    if (true == start_enabled)
    {
        QString path = nertc_beauty_tabwidget_->GetBeautyPath();

        int ret = engine_->StartBeauty(path);

        qDebug() << "ret:" << ret;
    }
    else
    {
        engine_->StopBeauty();
        nertc_beauty_tabwidget_->DisEnableBeauty();
    }
}

void CallWidget::onBeautyEnable(const bool& enable)
{
    qDebug() << "CallWidget::onBeautyEnable:" << enable;
    engine_->EnableNertcBeauty(enable);
}

void CallWidget::onBeautyMirror(const bool& enable)
{
    qDebug() << "CallWidget::onBeautyMirror:" << enable;
    engine_->EnableNertcMirror(enable);
}

void CallWidget::onBeautyMakeup(const bool& enable)
{
    engine_->EnableNertcMakeup(enable);
}

