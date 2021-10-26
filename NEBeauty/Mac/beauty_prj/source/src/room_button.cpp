#include <QFrame>
#include <QPushButton>
#include <QHBoxLayout>
#include <QVBoxLayout>
#include <QDebug>
#include "room_button.h"

RoomButton::RoomButton(QWidget* parent /*= nullptr*/)
:QWidget(parent)
{
    this->setWindowFlags(Qt::FramelessWindowHint | Qt::Tool);
    this->setAttribute(Qt::WA_TranslucentBackground);

    setUi();

    //
    connect(nertc_beauty_btn_, &QPushButton::clicked, this, &RoomButton::onNertcBeautyClicked);
    connect(nertc_beauty_setting_btn_, &QPushButton::clicked, this, &RoomButton::onNertcBeautySettingClicked);
}

RoomButton::~RoomButton()
{
    qDebug() << "RoomButtom::~RoomButtom()";
}

void RoomButton::setUi()
{
    QFrame* frame = new QFrame(this);
    frame->setStyleSheet("QFrame{border-radius:30px;background-color: #393947;}");
    frame->setFixedSize(QSize(384, 60));

    //
    nertc_beauty_btn_ = new QPushButton(frame);
    nertc_beauty_btn_->setFixedSize(QSize(24, 24));
    nertc_beauty_btn_->setCursor(QCursor(Qt::PointingHandCursor));
    nertc_beauty_btn_->setStyleSheet("QPushButton{border-image: url(:/image/beauty-off.png);}"
                                     "QPushButton:checked{border-image: url(:/image/beaut-on.png);}");
    nertc_beauty_btn_->setCheckable(true);

    nertc_beauty_setting_btn_ = new QPushButton(frame);
    nertc_beauty_setting_btn_->setFixedSize(QSize(12, 24));
    nertc_beauty_setting_btn_->setStyleSheet("QPushButton{image: url(:/image/btn_show_device_normal.png);\
                                                   image-position:center;background-color: transparent;\
                                                   padding-left: 2px;padding-right: 2px;}"
                                             "QPushButton:pressed{background-color: rgba(0, 0, 0, 100);}"
                                             "QPushButton:open{background-color: rgba(0, 0, 0, 100);}");

    QHBoxLayout* btn_nertc_beauty_hlayout = new QHBoxLayout();
    btn_nertc_beauty_hlayout->setSpacing(15);
    btn_nertc_beauty_hlayout->addWidget(nertc_beauty_btn_);
    btn_nertc_beauty_hlayout->addWidget(nertc_beauty_setting_btn_);

     
    QHBoxLayout* main_layout = new QHBoxLayout(frame);
    main_layout->setSpacing(30);
    main_layout->setContentsMargins(32, 0, 32, 0);
    main_layout->addLayout(btn_nertc_beauty_hlayout);
    main_layout->addStretch(0);
}

void RoomButton::onNertcBeautyClicked()
{
    qDebug() << "RoomButton::onNertcBeautyClicked";
    bool check = nertc_beauty_btn_->isChecked();
    Q_EMIT sigStartBeauty(check);
}

void RoomButton::onNertcBeautySettingClicked()
{
    qDebug() << "RoomButton::onNertcBeautySettingClicked";
    Q_EMIT sigNertcBeautySetting();
}
