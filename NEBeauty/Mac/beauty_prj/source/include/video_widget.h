#ifndef _VIDEO_WIDGET_H
#define _VIDEO_WIDGET_H

#include <QWidget>

class QLabel;
class VideoWidget: public QWidget
{
public:
    VideoWidget(QWidget* parent = nullptr);
    ~VideoWidget();

public:
    // 获取窗口句柄
    void* GetVideoHwnd();

};

#endif //_VIDEO_WIDGET_H
