#include <QTabWidget>
#include <QHBoxLayout>
#include <QVBoxLayout>
#include <QDebug>
#include "beauty_tabwidget.h"
#include "skin_beauty_widget.h"
#include "face_beauty_widget.h"
#include "filter_beauty_widget.h"
#include "other_beauty_widget.h"

BeautyTabWidget::BeautyTabWidget(QWidget* parent /*= nullptr*/)
:QDialog(parent)
{
    Qt::WindowFlags flags = Qt::Dialog;
    flags |= Qt::WindowCloseButtonHint;
    this->setWindowFlags(flags);
    setWindowTitle(u8"美颜参数");

    this->setFixedSize(QSize(422, 397));
    this->setStyleSheet("background-color: rgb(255, 255, 255);");

    setUi();

    connect(skin_beauty_widget_, &SkinBeautyWidget::sigSkinBeautyChanged, this, &BeautyTabWidget::sigBeautyChanged);
    connect(face_beauty_widget_, &FaceBeautyWidget::sigFaceBeautyChanged, this, &BeautyTabWidget::sigBeautyChanged);

    //
    connect(other_beauty_widget_, &OtherBeautyWidget::sigBeautyStart, this, &BeautyTabWidget::sigBeautyStart);
    connect(other_beauty_widget_, &OtherBeautyWidget::sigBautyEnable, this, &BeautyTabWidget::sigBautyEnable);

    //
    connect(filter_beauty_widget_, &FilterBeautyWidget::sigFilterPathChanged, this, &BeautyTabWidget::sigFilterChanged);

}

BeautyTabWidget::~BeautyTabWidget()
{
    qDebug() << "BeautyTabWidget::~BeautyTabWidget";
}

QString BeautyTabWidget::GetBeautyPath()
{
    return other_beauty_widget_->GetBeautyPath();
}

void BeautyTabWidget::DisEnableBeauty()
{
    other_beauty_widget_->DisEnableBeauty();
}

void BeautyTabWidget::GetFaceBeautyParams(std::map<int, int> &parmas_map)
{
    face_beauty_widget_->GetFaceBeautyParams(parmas_map);
}

void BeautyTabWidget::GetSkinBeautyParams(std::map<int, int> &parmas_map)
{
    skin_beauty_widget_->GetSkinBeautyParams(parmas_map);
}

void BeautyTabWidget::GetFilterParams(QString& path, int& val)
{
    filter_beauty_widget_->GetFilterParams(path, val);
}

void BeautyTabWidget::setUi()
{
    tabwidget_ = new QTabWidget(this);

    QString style = "QTabWidget{background-color:rgb(255,255,255);}\
                     QTabWidget::pane{top:20px;border:none;}\
                     QTabBar::tab{background-color: rgb(255, 255, 255);color:#333333;font-family:Microsoft YaHei;font-size:14px;\
                                  padding-left:9px;padding-right:9px;width:60px;height:30px;margin-left:0px;margin-right:0px;}\
                     QTabBar::tab:selected{color:#618BE5;font-family:Microsoft YaHei;font-size:14px;\
                     border-bottom:2px solid #618BE5;}\
                     QTabBar::tab:hover{color:#618BE5;font-family:Microsoft YaHei;font-size:14px;}\
                     QTabWidget::tab-bar{alignment:left;top:10px;left:10px;}";
    tabwidget_->setStyleSheet(style);

    other_beauty_widget_ = new OtherBeautyWidget();
    tabwidget_->addTab(other_beauty_widget_, u8"其他");

    skin_beauty_widget_ = new SkinBeautyWidget();
    tabwidget_->addTab(skin_beauty_widget_, u8"美肤");

    face_beauty_widget_ = new FaceBeautyWidget();
    tabwidget_->addTab(face_beauty_widget_, u8"美型");

    filter_beauty_widget_ = new FilterBeautyWidget();
    tabwidget_->addTab(filter_beauty_widget_, u8"滤镜");

    QHBoxLayout* h_layout = new QHBoxLayout(this);
    h_layout->setSpacing(0);
    h_layout->setContentsMargins(0, 0, 0, 0);
    h_layout->addWidget(tabwidget_);
}
