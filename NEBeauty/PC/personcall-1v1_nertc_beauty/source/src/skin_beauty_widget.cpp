#include <QLabel>
#include <QComboBox>
#include <QPushButton>
#include <QFormLayout>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QDebug>
#include "skin_beauty_widget.h"
#include "beauty_item_widget.h"

SkinBeautyWidget::SkinBeautyWidget(QWidget* parent /*= nullptr*/)
:QWidget(parent)
{
    label_name_.push_back(u8"磨皮");
    label_name_.push_back(u8"美白");
    label_name_.push_back(u8"亮眼");
    label_name_.push_back(u8"美牙");

    //
    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");

    //
    value_.push_back("0;100;0");
    value_.push_back("0;100;0");
    value_.push_back("0;100;0");
    value_.push_back("0;100;0");

    setUi();
}

SkinBeautyWidget::~SkinBeautyWidget()
{
    qDebug() << "SkinBeautyWidget::~SkinBeautyWidget";
}

void SkinBeautyWidget::setUi()
{
    QFormLayout *form_layout = new QFormLayout();
    form_layout->setHorizontalSpacing(20);
    form_layout->setVerticalSpacing(20);

    for (int i = 0; i < label_name_.size(); ++i)
    {
        QLabel *label = new QLabel(label_name_[i]);
        label->setStyleSheet(QString::fromUtf8("font-size: 14px"));
        BeautyItemWidget *widget = new BeautyItemWidget(i, type_[i], value_[i]);
        items_.push_back(widget);
        form_layout->setWidget(i, QFormLayout::FieldRole, widget);
        form_layout->setWidget(i, QFormLayout::LabelRole, label);
        connect(widget, &BeautyItemWidget::sigBeautyItemChanged, this, &SkinBeautyWidget::onBeautyItemChanged);
    }

    QVBoxLayout *vlayout = new QVBoxLayout(this);
    vlayout->setSpacing(100);
    vlayout->addLayout(form_layout);

}

void SkinBeautyWidget::onBeautyItemChanged(const int& id, const int &val)
{
    Q_EMIT sigSkinBeautyChanged(param_types_beauty_[id], val);
}
