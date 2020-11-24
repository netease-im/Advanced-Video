#pragma once
#include <QWidget>
#include "ui_videowidget.h"

class VideoWidget : public QWidget
{
    Q_OBJECT
public:
    VideoWidget(QWidget *parent = Q_NULLPTR);
    ~VideoWidget();
public:
    void setUsrID(QString strid);
    QString getUserID();
    void closeRender();
    void* getVideoHwnd();
protected:
private:
    Ui::videowidget ui;
    QString m_uid;
};
