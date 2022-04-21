#include <QLabel>
#include <QComboBox>
#include <QPushButton>
#include <QFormLayout>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QDebug>
#include "beauty_item_widget.h"
#include "filter_beauty_widget.h"

FilterBeautyWidget::FilterBeautyWidget(QWidget* parent /*= nullptr*/)
{
    label_name_.push_back(u8"滤镜");
    label_name_.push_back(u8"滤镜值");

    type_.push_back("combobox");
    type_.push_back("slider");

    //value_.push_back(u8"原图;白亮;粉嫩;小清晰;冷色调;暖色调;原图");

    QString str = u8"原图;";
    str.append(u8"filter_style_f1;");
    str.append(u8"filter_style_f2;");
    str.append(u8"filter_style_f3;");
    str.append(u8"filter_style_f4;");
    str.append(u8"filter_style_n2;");
    str.append(u8"filter_style_巴黎09;");
    str.append(u8"filter_style_白皙;");
    str.append(u8"filter_style_白皙1;");
    str.append(u8"filter_style_白皙2;");
    str.append(u8"filter_style_白皙3;");
    str.append(u8"filter_style_白皙4;");
    str.append(u8"filter_style_白皙5;");
    str.append(u8"filter_style_白皙6;");
    str.append(u8"filter_style_个性1;");
    str.append(u8"filter_style_个性2;");
    str.append(u8"filter_style_个性3;");
    str.append(u8"filter_style_个性4;");
    str.append(u8"filter_style_个性5;");
    str.append(u8"filter_style_个性6;");
    str.append(u8"filter_style_寒冬;");
    str.append(u8"filter_style_花蕊;");
    str.append(u8"filter_style_慕斯;");
    str.append(u8"filter_style_汽水;");
    str.append(u8"filter_style_青柠;");
    str.append(u8"filter_style_清新1;");
    str.append(u8"filter_style_清新2;");
    str.append(u8"filter_style_清新3;");
    str.append(u8"filter_style_清新4;");
    str.append(u8"filter_style_清新5;");
    str.append(u8"filter_style_清新6;");
    str.append(u8"filter_style_秋分;");
    str.append(u8"filter_style_日系;");
    str.append(u8"filter_style_柔美;");
    str.append(u8"filter_style_森林;");
    str.append(u8"filter_style_少女;");
    str.append(u8"filter_style_水光;");
    str.append(u8"filter_style_甜美;");
    str.append(u8"filter_style_微光;");
    str.append(u8"filter_style_夏日;");
    str.append(u8"filter_style_质感1;");
    str.append(u8"filter_style_质感2;");
    str.append(u8"filter_style_质感3;");
    str.append(u8"filter_style_质感4;");
    str.append(u8"filter_style_质感5;");
    str.append(u8"filter_style_质感6;");
    str.append(u8"filter_style_自然;");
    str.append(u8"filter_style_自然1;");
    str.append(u8"filter_style_自然2;");
    str.append(u8"filter_style_自然3;");
    str.append(u8"filter_style_自然4;");
    str.append(u8"filter_style_自然5;");
    str.append(u8"filter_style_自然6;");
    str.append(u8"原图");
    value_.push_back(str);
    value_.push_back("0;100;50");

    {
        filter_path_list_ = str.split(";");
        filter_path_list_.removeLast();

        for (auto &item: filter_path_list_)
        {
            item += "\\template.json";
            default_.push_back(50);
        }
        filter_path_list_[0] = "none";
    }

    setUi();
}

FilterBeautyWidget::~FilterBeautyWidget()
{
    qDebug() << "FilterBeautyWidget::~FilterBeautyWidget";
}

void FilterBeautyWidget::GetFilterParams(QString& path, int& val)
{
    path = filter_path_list_[index_];
    val = default_[index_];
}

void FilterBeautyWidget::setUi()
{
    QFormLayout *form_layout = new QFormLayout();
    form_layout->setHorizontalSpacing(20);
    form_layout->setVerticalSpacing(20);

    for (int i = 0; i < label_name_.size(); ++i)
    {
        QLabel *label = new QLabel(label_name_[i]);
        label->setStyleSheet(QString::fromUtf8("font-size: 14px"));
        labels_.push_back(label);
        BeautyItemWidget *widget = new BeautyItemWidget(i, type_[i], value_[i]);
        items_.push_back(widget);
        form_layout->setWidget(i, QFormLayout::FieldRole, widget);
        form_layout->setWidget(i, QFormLayout::LabelRole, label);
        connect(widget, &BeautyItemWidget::sigBeautyItemChanged, this, &FilterBeautyWidget::onBeautyItemChanged);
    }

    QVBoxLayout *verticalLayout = new QVBoxLayout(this);
    verticalLayout->addLayout(form_layout);
}

void FilterBeautyWidget::onBeautyItemChanged(const int& id, const int &val)
{
    if (0 == id)
    {
        index_ = val;
        items_[1]->SetValue(default_[val]);
    }
    else
    {
        default_[index_] = val;
    }

    Q_EMIT sigFilterPathChanged(filter_path_list_[index_], default_[index_]);
}
