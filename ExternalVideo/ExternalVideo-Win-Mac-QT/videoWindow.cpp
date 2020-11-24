#include "videoWindow.h"
#include <QDebug>
#include <QFileDialog>
#include <QMediaPlayer>
#include "libyuv.h"
#include "nrtc_engine.h"
#include "videosurface.h"

VideoWindow::VideoWindow(QWidget* parent /*= Q_NULLPTR*/) : QMainWindow(parent) {
    setWindowFlags(Qt::WindowCloseButtonHint | Qt::WindowMinimizeButtonHint);

    ui.setupUi(this);

    this->setWindowTitle("NERtcSample-VideoStream");

    setVisible(false);

    m_videoWindowMap[1] = ui.video_1;
    m_videoWindowMap[2] = ui.video_2;
    m_videoWindowMap[3] = ui.video_3;
    m_videoWindowMap[4] = ui.video_4;
}

VideoWindow::~VideoWindow() {}

void VideoWindow::closeEvent(QCloseEvent* event) {
    Q_UNUSED(event);
    on_disconnectBtn_clicked(false);
}

void VideoWindow::setNeRtcEngine(std::shared_ptr<NRTCEngine> ptr) {
    m_engine = ptr;

    connect(m_engine.get(), &NRTCEngine::userJoined, this, &VideoWindow::onUserJoined);
    connect(m_engine.get(), &NRTCEngine::userLeft, this, &VideoWindow::onUserLeft);
}

void VideoWindow::onJoinChannel(QString& roomid, QString& usrId)
{
    this->show();

    void* hwnd = ui.video_1->getVideoHwnd();
    ui.video_1->setUsrID(usrId);
    m_engine->setupLocalVideo(hwnd);
    m_engine->joinChannel("", roomid, usrId, true, true, 2);
    //开启camera
    m_engine->enableVideo(true);
}

void VideoWindow::on_disconnectBtn_clicked(bool checked) {
    this->hide();
    m_videoCount = 1;
    for (auto item : m_videoWindowMap) {
        item.second->closeRender();
    }
    m_engine->stopLiveStream(m_currentTaskId.toUtf8());
    m_engine->leaveChannel();
    ui.fileLabel->setText("");

    disconnect(videoSurface, &VideoSurface::frameAvailable, 0, 0);
    mediaPlayer->stop();
    emit closeVideoWindowSignal();
}

void VideoWindow::onUserJoined(quint64 uid) {
    //暂定最大4人，可以自己开放房间最大人数
    m_videoCount++;
    if (m_videoCount >= 4) {
        return;
    }

    void* hwnd = m_videoWindowMap[m_videoCount]->getVideoHwnd();
    m_videoWindowMap[m_videoCount]->setUsrID(QString::number(uid));
    m_engine->setupRemoteVideo(uid, hwnd);
}

void VideoWindow::onUserLeft(quint64 uid) {
    m_engine->stopRemoteVideo(uid);
    m_videoWindowMap[m_videoCount]->closeRender();
    m_videoCount--;
}

void VideoWindow::onFrameReady(QVideoFrame& frame) {

    frame.map(QAbstractVideoBuffer::ReadOnly);
    auto format = frame.pixelFormat();
    auto w = frame.width();
    auto h = frame.height();


    if(frame.handleType() == QAbstractVideoBuffer::NoHandle){

        if(format == QVideoFrame::Format_ARGB32){

            nertc::NERtcVideoFrame tmpFrame;
            tmpFrame.width = w;
            tmpFrame.height = h;
            tmpFrame.rotation = nertc::kNERtcVideoRotation_0;
            int yuvBufSize = w * h * 3 / 2;
            uint8_t* yuvBuf = new uint8_t[yuvBufSize];

            // source-stride
            int Dst_Stride_Y = w;
            const qint32 uv_stride = (w + 1) / 2;

            // source-length
            const int y_length = w * h;
            int uv_length = uv_stride * ((h + 1) / 2);

            // source-data
            unsigned char* Y_data_Dst = yuvBuf;
            unsigned char* U_data_Dst = yuvBuf + y_length;
            unsigned char* V_data_Dst = U_data_Dst + uv_length;
            libyuv::ARGBToI420(frame.bits(), w * 4, Y_data_Dst, Dst_Stride_Y, U_data_Dst, uv_stride, V_data_Dst, uv_stride, w, h);
            tmpFrame.buffer = yuvBuf;
            tmpFrame.format = nertc::kNERtcVideoTypeI420;
            m_engine->pushExternalVideoFrame(&tmpFrame);
            delete[] yuvBuf;
        }

    }


    frame.unmap();





}



void VideoWindow::on_VideoStreamBtn_clicked(bool checked) {
    QString fileName = QFileDialog::getOpenFileName(this, tr("Open MP4 File"), ".", tr("Media Files (*.mp4)"));
    ui.fileLabel->setText(fileName);

    if (fileName != "") {
        auto ret = m_engine->setExternalVideoSource(true);  //设置使用自定义数据源
        m_engine->enableVideo(true);

        mediaPlayer->setVideoOutput(videoSurface);
        mediaPlayer->setMedia(QUrl::fromLocalFile(fileName));
        connect(videoSurface, &VideoSurface::frameAvailable, this, &VideoWindow::onFrameReady);
        mediaPlayer->play();
    }
}
