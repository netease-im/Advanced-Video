#include <QLabel>
#include <QComboBox>
#include <QPushButton>
#include <QFormLayout>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QDebug>
#include "beauty_item_widget.h"
#include "item_sticker_widget.h"

ItemStickerWidget::ItemStickerWidget(QWidget* parent /*= nullptr*/)
:QWidget(parent)
{
    label_name_.push_back(u8"道具贴纸");

    type_.push_back("combobox");

    value_.push_back("bunny;chick;glass;rabbiteating;snow;spot;none;none");

    setUi();
}

ItemStickerWidget::~ItemStickerWidget()
{
    qDebug() << "ItemStickerWidget::~ItemStickerWidget";
}

void ItemStickerWidget::setUi()
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
        connect(widget, &BeautyItemWidget::sigBeautyItemChanged, this, &ItemStickerWidget::onItemStickerChanged);
    }

    QVBoxLayout *vlayout = new QVBoxLayout(this);
    vlayout->setSpacing(100);
    vlayout->addLayout(form_layout);
}

void ItemStickerWidget::onItemStickerChanged(const int& id, const int &val)
{
    std::string name = 6 > val ? bundle_path_[val] : "none";
    Q_EMIT sigItemStickerChanged(name);
}
