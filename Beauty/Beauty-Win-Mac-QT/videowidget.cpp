#include "videowidget.h"
#include "CNamaSDK.h"					//nama SDK 的头文件
#include "authpack.h"
#include <QDebug>
#include <sstream>
#include <fstream>
#include <iostream>
#include <QWindow>
#include "macxhelper.h"

const std::string g_faceBeautification = "face_beautification.bundle";

#define MAX_BEAUTYFACEPARAMTER 7
#define MAX_FACESHAPEPARAMTER 15

const std::string g_faceBeautyParamName[MAX_BEAUTYFACEPARAMTER] = { "blur_level","color_level", "red_level", "eye_bright", "tooth_whiten" ,"remove_pouch_strength", "remove_nasolabial_folds_strength" };

const std::string g_faceShapeParamName[MAX_FACESHAPEPARAMTER] = { "cheek_thinning","eye_enlarging", "intensity_chin", "intensity_forehead", "intensity_nose","intensity_mouth",
                                                                  "cheek_v","cheek_narrow","cheek_small",
                                                                  "intensity_canthus", "intensity_eye_space", "intensity_eye_rotate", "intensity_long_nose",
                                                                  "intensity_philtrum", "intensity_smile" };




static size_t FileSize(std::ifstream& file)
{
    std::streampos oldPos = file.tellg();
    file.seekg(0, std::ios::beg);
    std::streampos beg = file.tellg();
    file.seekg(0, std::ios::end);
    std::streampos end = file.tellg();
    file.seekg(oldPos, std::ios::beg);
    return static_cast<size_t>(end - beg);
}

VideoWidget::VideoWidget(QWidget *parent /*= Q_NULLPTR*/)
    :QOpenGLWidget(parent),
     m_pInvoker(new Invoker)
{

    this->setAttribute(Qt::WA_StyledBackground, true);
    
    ui.setupUi(this);
    QFont font;
    font.setPixelSize(12);
    font.setBold(true);
    ui.uid->setFont(font);
    
}

VideoWidget::~VideoWidget()
{
    fuDestroyAllItems();
}

void VideoWidget::setUsrID(QString strid)
{
    QString str = "UsrId : " + strid;
    ui.uid->setText(str);
    ui.uid->adjustSize();

}

void VideoWidget::closeRender()
{
    ui.uid->setText("");
    ui.uid->adjustSize();
    repaint();
}

void* VideoWidget::getVideoHwnd()
{
    return (void*)ui.video->winId();
}

void VideoWidget::initFU()
{
    auto ret = fuSetup(nullptr, 0, nullptr, g_auth_package, sizeof(g_auth_package));

    qDebug()<<fuGetVersion();

    std::vector<char> propData;
    if (false == LoadBundleInner(g_faceBeautification, propData))
    {
        qDebug() << "load face beautification data failed.";
        return;
    }
    qDebug() << "load face beautification data.";

    m_BeautyHandles = fuCreateItemFromPackage(&propData[0], propData.size());


    for (int i=0;i<MAX_BEAUTYFACEPARAMTER;i++)
    {
        if (i==0)
        {
            ret = fuItemSetParamd(m_BeautyHandles, const_cast<char*>(g_faceBeautyParamName[i].c_str()), 6.0f);
        }
        else
        {
            ret = fuItemSetParamd(m_BeautyHandles, const_cast<char*>(g_faceBeautyParamName[i].c_str()), 1.0f);
        }
        qDebug()<<"i : "<<i<<" ret : " <<ret;
    }

}



void VideoWidget::initializeGL()
{

    glEnable(GL_MULTISAMPLE);
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glDisable(GL_DEPTH_TEST);
    //glEnable(GL_BLEND);
    glDisable(GL_BLEND);
    //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);
    //glBlendFunc(GL_DST_ALPHA, GL_DST_ALPHA);
    glEnable(GL_TEXTURE_2D);
}

void VideoWidget::resizeGL(int w, int h)
{

}

void VideoWidget::paintGL()
{

}

bool VideoWidget::LoadBundleInner(const std::string &filepath, std::vector<char> &data)
{
    std::ifstream fin(filepath, std::ios::binary);
    if (false == fin.good())
    {
        fin.close();
        return false;
    }
    size_t size = FileSize(fin);
    if (0 == size)
    {
        fin.close();
        return false;
    }
    data.resize(size);
    fin.read(reinterpret_cast<char*>(&data[0]), size);

    fin.close();
    return true;
}

void VideoWidget::onRenderFrame(int type, void *data, int w, int h, int frameid)
{
    int handle[] = { m_BeautyHandles };
    int handleSize = sizeof(handle) / sizeof(handle[0]);
    //支持的格式有FU_FORMAT_BGRA_BUFFER 、 FU_FORMAT_NV21_BUFFER 、FU_FORMAT_I420_BUFFER 、FU_FORMAT_RGBA_BUFFER
    int n = fuRenderItemsEx2(FU_FORMAT_I420_BUFFER, reinterpret_cast<int*>(data), FU_FORMAT_I420_BUFFER, reinterpret_cast<int*>(data),w, h, m_FrameID, handle, handleSize, NAMA_RENDER_FEATURE_FULL, NULL);

    qDebug()<<n;
}





