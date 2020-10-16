#pragma once
#include <QOpenGLWidget>
#include "ui_videowidget.h"
#include "invoker.h"
class VideoWidget : public QOpenGLWidget
{
    Q_OBJECT
public:
    VideoWidget(QWidget *parent = Q_NULLPTR);
    ~VideoWidget();
public:
    void setUsrID(QString strid);
    void closeRender();
    void* getVideoHwnd();
    void initFU();

protected:
    virtual void initializeGL() override;
    virtual void resizeGL(int w, int h) override;
    virtual void paintGL() override;
public slots:
     void onRenderFrame(int type, void* data, int w, int h,int frameid);
private:
    bool LoadBundleInner(const std::string& filepath, std::vector<char>& data);

private:
    Ui::videowidget ui;
    int m_BeautyHandles;
    int m_FrameID = 0;
     std::unique_ptr<Invoker>        m_pInvoker = nullptr;
};
