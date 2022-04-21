#ifndef _CALL_WIDGET_H
#define _CALL_WIDGET_H

#include <QWidget>

struct JoinInfo
{
    bool open_mic_;
    bool open_camera_ ;
    bool open_1v1_;
    QString channel_name_;
    QString uid_;
};

class QHBoxLayout;
class QVBoxLayout;
class VideoWidget;
class Engine;
class RoomButton;
class BeautyTabWidget;
class CallWidget: public QWidget
{
    Q_OBJECT

public:
    CallWidget(Engine* engine, QWidget *parent = nullptr);
    ~CallWidget();

public:
    int JoinChannel(const JoinInfo &join_info);

protected:
    virtual void resizeEvent(QResizeEvent* event) override;
    virtual void moveEvent(QMoveEvent* event) override;

private:
    void setUi();
    void adjustVideoLayout();
    void clearLayout();

Q_SIGNALS:
    void sigEnableNama(const bool &enable);

private Q_SLOTS:
    void onJoinChannel(const int& reson);
    void onUserJoin(const quint64& uid, const QString& name);
    void onUserVideoStart(const quint64& uid, const int& profile);
    void onUserLeft(const quint64& uid, const int& result);

    //
    void onBeautyChanged(const int& id, const int &val);
    void onFilterChanged(const QString& path, const int &val);

    //
    void onStartBeauty(const bool& start_enabled);
    void onBeautyEnable(const bool& enable);

private:
    QHBoxLayout* video_hlayout_;
    QVBoxLayout* vertical_layout_;
    VideoWidget* local_video_widget_ = nullptr;
    VideoWidget* remote_video_widget_ = nullptr;

private:
    Engine          *engine_;
    RoomButton      *room_button_;
    BeautyTabWidget *nertc_beauty_tabwidget_;

private:
    int user_count_ = 1;
};

#endif //_CALL_WIDGET_H
