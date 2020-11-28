#include <QDebug>
#include "videoWindow.h"
#include "nrtc_engine.h"

VideoWindow::VideoWindow(QWidget *parent /*= Q_NULLPTR*/)
    :QMainWindow(parent)
{
    setWindowFlags(Qt::WindowCloseButtonHint | Qt::WindowMinimizeButtonHint);

    ui.setupUi(this);
   
    this->setWindowTitle("NERtcSample-Audio&Video Manage");

    setVisible(false);

    m_videoWindowMap[1] = ui.video_1;
    m_videoWindowMap[2] = ui.video_2;
    m_videoWindowMap[3] = ui.video_3;
    m_videoWindowMap[4] = ui.video_4;



    /*视频质量*/
    ui.videocomboBox->addItem("LOWEST 160x90/120 @15fps", 0);
    ui.videocomboBox->addItem("LOW 320x180/240 @15fps", 1);
    ui.videocomboBox->addItem("STANDARD 640x360/480 @30fps", 2);
    ui.videocomboBox->addItem("HD720P 1280x720 @30fps", 3);
    ui.videocomboBox->addItem("HD1080p 1920x1280 @30fps", 4);
    ui.videocomboBox->setCurrentIndex(2);

    /*音频质量*/
    ui.audiocomboBox->addItem(tr("16kHz采样率 单声道 编码码率20Kbps"), 0);
    ui.audiocomboBox->addItem(tr("48kHz采样率 单声道 编码码率32Kbps"), 1);
    ui.audiocomboBox->addItem(tr("48kHz采样率 双声道 编码码率64Kbps"), 2);
    ui.audiocomboBox->addItem(tr("48kHz采样率 单声道 编码码率64Kbps"), 3);
    ui.audiocomboBox->addItem(tr("48kHz采样率 双声道 编码码率128Kbps"), 4);
    ui.audiocomboBox->setCurrentIndex(0);

    connect(ui.videocomboBox, SIGNAL(currentIndexChanged(int)), this, SLOT(onVideoConfigChanged(int)));
    connect(ui.audiocomboBox, SIGNAL(currentIndexChanged(int)), this, SLOT(onAudioConfigChanged(int)));

}

VideoWindow::~VideoWindow()
{

}

void VideoWindow::closeEvent(QCloseEvent *event)
{
    Q_UNUSED(event);
    on_disconnectBtn_clicked(false);
}



void VideoWindow::setNeRtcEngine(std::shared_ptr<NRTCEngine> ptr)
{
    m_engine = ptr;

    connect(m_engine.get(), &NRTCEngine::userJoined, this, &VideoWindow::onUserJoined);
    connect(m_engine.get(), &NRTCEngine::userLeft, this, &VideoWindow::onUserLeft);

}

void VideoWindow::onJoinChannel(QString& roomid, QString& usrId)
{
    this->show();
    auto hwnd = ui.video_1->getVideoHwnd();
    ui.video_1->setUsrID(usrId);
    m_engine->setupLocalVideo(hwnd);
    m_engine->joinChannel("", roomid, usrId, true, true, 2);
    //开启camera
    m_engine->enableVideo(true);
    

}

void VideoWindow::on_disconnectBtn_clicked(bool checked)
{
    this->hide();
    m_videoCount = 1;
    for ( auto item : m_videoWindowMap )
    {   
        item.second->closeRender();
    }
    m_engine->leaveChannel();
    emit closeVideoWindowSignal();
    
    
}

void VideoWindow::onUserJoined(quint64 uid)
{
    //暂定最大4人，可以自己开放房间最大人数
    m_videoCount++;
    if (m_videoCount >= 4) {
        return;
    }
   

    auto hwnd = m_videoWindowMap[m_videoCount]->getVideoHwnd();
    m_videoWindowMap[m_videoCount]->setUsrID(QString::number(uid));
    m_engine->setupRemoteVideo(uid, hwnd);
   
}

void VideoWindow::onUserLeft(quint64 uid)
{  
    m_engine->stopRemoteVideo(uid);
    m_videoWindowMap[m_videoCount]->closeRender();
    m_videoCount--;
}

void VideoWindow::onVideoConfigChanged(int index)
{
    m_engine->enableVideo(false);
    m_engine->setLocalVideoProfileType((nertc::NERtcVideoProfileType)index);
    m_engine->enableVideo(true);
}

void VideoWindow::onAudioConfigChanged(int index)
{
    m_engine->setLocalAudioProfileType((nertc::NERtcAudioProfileType)index);
}

