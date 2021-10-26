#include <QSpinBox>
#include <QSlider>
#include <QHBoxLayout>
#include <QFormLayout>
#include <QLabel>
#include <QComboBox>
#include <QDebug>
#include "beauty_item_widget.h"

BeautyItemWidget::BeautyItemWidget(const int     &id,
                                   const QString &type /*= "slider"*/, 
                                   const QString &val /*= ""*/, 
                                   QWidget       *parent /*= nullptr*/)
:QWidget(parent)
,id_(id)
,type_(type)
{
    setUi(val);
}

BeautyItemWidget::~BeautyItemWidget()
{
    qDebug() << "BeautyItemWidget::~BeautyItemWidget";
}

void BeautyItemWidget::SetValue(const int &value)
{
    slider_->setValue(value);
}

const int BeautyItemWidget::GetValue()
{
    return slider_->value();
}

void BeautyItemWidget::setUi(const QString &val)
{
    QHBoxLayout *hlayout = new QHBoxLayout(this);
    hlayout->setContentsMargins(0, 0, 0, 0);

    QStringList strs = val.split(";");
    default_ = strs.takeLast();
    if ("slider" == type_)
    {
        int min = strs[0].toInt();
        int max = strs[1].toInt();

        spinbox_ = new QSpinBox();
        spinbox_->setMinimum(min);
        spinbox_->setMaximum(max);
        spinbox_->setFixedWidth(50);

        slider_ = new QSlider();
        slider_->setOrientation(Qt::Horizontal);
        slider_->setMinimum(min);
        slider_->setMaximum(max);
        slider_->setFixedWidth(200);
        slider_->setStyleSheet("QSlider::groove:horizontal{border: 1px solid #00B0AE;background:#00B0AE;height:2px;\
                                                           border-radius:1px;padding-left:0px;padding-right:0px;}\
                                QSlider::sub-page:horizontal{background:#00B0AE;border: 1px solid #00B0AE;height:2px;border-radius:2px;}\
                                QSlider::add-page:horizontal {background: #EAEAEA;border: 0px solid #EAEAEA;height:2px;border - radius: 2px;}\
                                QSlider::handle:horizontal{background: qradialgradient(spread:pad,cx:0.5,cy:0.5,radius:0.5,fx:0.5,fy:0.5,stop:0.6 #00B0AE,\
                                                           stop:0.98409 rgba(255, 255, 255, 255));width: 15px;margin-top:-6px;margin-bottom:-6px;border-radius:5px;}\
                                QSlider::handle:horizontal:hover {background: qradialgradient(spread:pad, cx:0.5, cy:0.5, radius:0.5, fx:0.5, fy:0.5,stop:0.6 #00B0AE,\
                                                                  stop:0.98409 rgba(255, 255, 255, 255));width:15px;margin-top: -6px;margin-bottom: -6px;border-radius: 5px;}");

        hlayout->addWidget(slider_);
        hlayout->addSpacing(10);
        hlayout->addWidget(spinbox_);
        hlayout->addStretch(0);


        connect(slider_, SIGNAL(valueChanged(int)), spinbox_, SLOT(setValue(int)));
        connect(spinbox_, SIGNAL(valueChanged(int)), slider_, SLOT(setValue(int)));
        connect(slider_, &QSlider::valueChanged, this, &BeautyItemWidget::onCurrentValueChange);

        slider_->setValue(default_.toInt());
    }
    else if ("combobox" == type_)
    {
        combobox_ = new QComboBox(this);
        combobox_->setFixedSize(QSize(200, 30));
        combobox_->setStyleSheet("QComboBox{border:1px solid #d9d9db;border-radius:2px;padding-left:12px;font-size:14px;}"
                                 "QComboBox::drop-down{subcontrol-origin: padding;subcontrol-position:top right;width:8px;border-left-style:solid;\
                                                       border-left-width: 1px;padding-right:12px;}"
                                 "QComboBox::down-arrow{image: url(:/image/triangle-combox.png);max-width:8px;max-height:4px;}");
        combobox_->addItems(strs);
        combobox_->setCurrentText(default_);
        connect(combobox_, QOverload<int>::of(&QComboBox::currentIndexChanged), this, &BeautyItemWidget::onCurrentValueChange);

        hlayout->addWidget(combobox_);
        hlayout->addStretch(0);
    }

}

void BeautyItemWidget::onCurrentValueChange(int val)
{
    Q_EMIT sigBeautyItemChanged(id_, val);
}
