#pragma once
#include <QMainWindow>
#include <QWidget>
#include <QCloseEvent>
#include <memory>
#include <atomic>
#include <QVideoFrame>
#include "ui_videowindow.h"
#include "nrtc_engine.h"
#include <QMediaPlayer>
#include "videosurface.h"
class VideoWindow : public QMainWindow
{
    Q_OBJECT
public:
    VideoWindow(QWidget *parent = Q_NULLPTR);
    ~VideoWindow();

    void closeEvent(QCloseEvent *event) override;

public: 
    void setNeRtcEngine(std::shared_ptr<NRTCEngine> ptr);
signals:
    void closeVideoWindowSignal();
public slots:
    void onJoinChannel(QString& roomid, QString& usrId);
    void on_disconnectBtn_clicked(bool checked);
    void onUserJoined(quint64 uid);
    void onUserLeft(quint64 uid);
    void on_VideoStreamBtn_clicked(bool checked);
	void onFrameReady(QVideoFrame& frame);

protected:
private:
    Ui::MainWindow ui;
    std::shared_ptr<NRTCEngine> m_engine;

    std::map<int, VideoWidget*> m_videoWindowMap;
    //����ͳ�ƣ���һ����Ϊ�Լ���Ƶ
    std::atomic_short       m_videoCount = {1};

    qint64      m_primaryUser = 0;
    QString     m_currentTaskId;

    QMediaPlayer* mediaPlayer = new QMediaPlayer;
    VideoSurface* videoSurface = new VideoSurface;

};
