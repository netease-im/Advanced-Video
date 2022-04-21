#include <QLabel>
#include <QComboBox>
#include <QPushButton>
#include <QFormLayout>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QScrollArea>
#include <QDebug>
#include "face_beauty_widget.h"
#include "beauty_item_widget.h"

FaceBeautyWidget::FaceBeautyWidget(QWidget* parent /*= nullptr*/)
:QWidget(parent)
{
    label_name_.push_back(u8"瘦脸");
    label_name_.push_back(u8"小脸");
    label_name_.push_back(u8"下巴");
    label_name_.push_back(u8"眼距");
    label_name_.push_back(u8"嘴型");
    label_name_.push_back(u8"大眼");
    label_name_.push_back(u8"小鼻");
    label_name_.push_back(u8"开眼角");
    label_name_.push_back(u8"长鼻");
    label_name_.push_back(u8"微笑嘴角");
    label_name_.push_back(u8"缩人中");
    label_name_.push_back(u8"眼角调整");
    label_name_.push_back(u8"圆眼");
    label_name_.push_back(u8"V脸");
    label_name_.push_back(u8"瘦下颌骨");
    label_name_.push_back(u8"窄脸");
    label_name_.push_back(u8"瘦颧骨");

    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");
    type_.push_back("slider");

    value_.push_back("0;100;35");  //瘦脸
    value_.push_back("0;100;10");  //小脸
    value_.push_back("0;100;40");  //下巴
    value_.push_back("0;100;40"); //眼距
    value_.push_back("0;100;80"); //嘴型
    value_.push_back("0;100;30");  //大眼
    value_.push_back("0;100;40");  //小鼻
    value_.push_back("0;100;0");  //开眼角
    value_.push_back("0;100;0"); //长鼻
    value_.push_back("0;100;50"); //微笑嘴角
    value_.push_back("0;100;50"); //缩人中
    value_.push_back("0;100;50"); //眼角调整
    value_.push_back("0;100;80");  //圆眼
    value_.push_back("0;100;0");  //V脸
    value_.push_back("0;100;0");  //瘦下颌骨
    value_.push_back("0;100;0");  //窄脸
    value_.push_back("0;100;0");  //瘦颧骨

    setUi();
}

FaceBeautyWidget::~FaceBeautyWidget()
{
    qDebug() << "FaceBeautyWidget::~FaceBeautyWidget";
}

void FaceBeautyWidget::GetFaceBeautyParams(std::map<int, int> &parmas_map)
{
    for (int i = 0; i < items_.size(); ++i)
    {
        int key = param_types_beauty_[i];
        int val = items_[i]->GetValue();
        parmas_map.emplace(key, val);
    }
}

void FaceBeautyWidget::setUi()
{
    QFormLayout *form_layout = new QFormLayout();
    form_layout->setHorizontalSpacing(20);
    form_layout->setVerticalSpacing(20);

    QScrollArea *scroll_area = new QScrollArea(this);
    scroll_area->setSizePolicy(QSizePolicy(QSizePolicy::Expanding, QSizePolicy::Expanding));
    scroll_area->setWidgetResizable(true);
    scroll_area->setStyleSheet("QScrollArea{border:0px;}\
                               QScrollBar:vertical{border:0px;background: #f5f5f7;width: 8px;margin: 0px 0 0px 0;}\
                               QScrollBar::handle:vertical{background: Gainsboro;min-height: 20px;border: none;}");

    QWidget *widget = new QWidget(this);
    for (int i = 0; i < label_name_.size(); ++i)
    {
        QLabel *label = new QLabel(label_name_[i], widget);
        label->setStyleSheet(QString::fromUtf8("font-size: 14px"));
        BeautyItemWidget *beauty_item = new BeautyItemWidget(i, type_[i], value_[i], widget);
        items_.push_back(beauty_item);
        form_layout->setWidget(i, QFormLayout::FieldRole, beauty_item);
        form_layout->setWidget(i, QFormLayout::LabelRole, label);
        connect(beauty_item, &BeautyItemWidget::sigBeautyItemChanged, this, &FaceBeautyWidget::onBeautyItemChanged);
    }

    QVBoxLayout *vlayout = new QVBoxLayout(this);
    vlayout->setSpacing(100);
    vlayout->addLayout(form_layout);

    widget->setLayout(vlayout);
    scroll_area->setWidget(widget);

    QVBoxLayout *main_layout = new QVBoxLayout(this);
    main_layout->addWidget(scroll_area);
}

void FaceBeautyWidget::onBeautyItemChanged(const int& id, const int &val)
{
    Q_EMIT sigFaceBeautyChanged(param_types_beauty_[id], val);
}
