#include <QCheckBox>
#include <QFrame>
#include <QVBoxLayout>
#include <QDebug>
#include "other_beauty_widget.h"

OtherBeautyWidget::OtherBeautyWidget(QWidget* parent /*= nullptr*/)
:QWidget(parent)
{
    setUi();

    connect(enable_check_, &QCheckBox::stateChanged, this, &OtherBeautyWidget::sigBautyEnable);
    connect(mirror_check_, &QCheckBox::stateChanged, this, &OtherBeautyWidget::sigBeautyMirror);
    connect(makeup_check_, &QCheckBox::stateChanged, this, &OtherBeautyWidget::sigBeautyMakeup);
}

OtherBeautyWidget::~OtherBeautyWidget()
{
    qDebug() << "OtherBeautyWidget::~OtherBeautyWidget";
}

void OtherBeautyWidget::setUi()
{
    QFrame* frame = new QFrame(this);
    frame->setStyleSheet("background-color: rgb(255, 255, 255);font-family:Microsoft YaHei;font-size: 14px");

    //
    enable_check_ = new QCheckBox(frame);
    enable_check_->setText(u8"美颜启用");

    mirror_check_ = new QCheckBox(frame);
    mirror_check_->setText(u8"镜像");

    makeup_check_ = new QCheckBox(frame);
    makeup_check_->setText(u8"美妆");


    QVBoxLayout* main_layout = new QVBoxLayout(frame);
    main_layout->setContentsMargins(20, 20, 0, 0);
    main_layout->setSpacing(15);
    main_layout->addWidget(enable_check_);
    main_layout->addWidget(mirror_check_);
    main_layout->addWidget(makeup_check_);

}
