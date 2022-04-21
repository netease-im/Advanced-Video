#include <QCheckBox>
#include <QLineEdit>
#include <QLabel>
#include <QPushButton>
#include <QFrame>
#include <QHBoxLayout>
#include <QVBoxLayout>
#include <QDebug>
#include <QFileDialog>
#include "other_beauty_widget.h"

OtherBeautyWidget::OtherBeautyWidget(QWidget* parent /*= nullptr*/)
:QWidget(parent)
{
    setUi();

    connect(start_check_, &QCheckBox::stateChanged, this, &OtherBeautyWidget::sigBeautyStart);
    connect(enable_check_, &QCheckBox::stateChanged, this, &OtherBeautyWidget::sigBautyEnable);
}

OtherBeautyWidget::~OtherBeautyWidget()
{
    qDebug() << "OtherBeautyWidget::~OtherBeautyWidget";
}

QString OtherBeautyWidget::GetBeautyPath()
{
    return beauty_path_edit_->text();
}

void OtherBeautyWidget::DisEnableBeauty()
{
    enable_check_->blockSignals(true);
    enable_check_->setChecked(false);
    enable_check_->blockSignals(false);
}

void OtherBeautyWidget::setUi()
{
    QFrame* frame = new QFrame(this);
    frame->setStyleSheet("background-color: rgb(255, 255, 255);font-family:Microsoft YaHei;font-size: 14px");

    //
    QPushButton *path_btn = new QPushButton(u8"...", frame);
    path_btn->setStyleSheet("QPushButton{background-color:rgb(255,255,255);\
                                         border: 1px solid #dcdfe6; padding: 10px;border-radius: 5px;}"
                            "QPushButton:hover{background-color:#ecf5ff; color: #409eff;}"
                            "QPushButton:pressed, QPushButton:checked{border: 1px solid #3a8ee6; color: #409eff; }");
    connect(path_btn, &QPushButton::clicked, this, &OtherBeautyWidget::onOpenBeautyDir);



    beauty_path_edit_ = new QLineEdit(frame);
    beauty_path_edit_->setText("../../../../data/beauty/nebeauty");
    beauty_path_edit_->setMinimumSize(300, 20);

    QHBoxLayout* hlayout = new QHBoxLayout();
    hlayout->addWidget(beauty_path_edit_);
    hlayout->addWidget(path_btn);

    start_check_ = new QCheckBox(frame);
    start_check_->setText(u8"使能");

    enable_check_ = new QCheckBox(frame);
    enable_check_->setText(u8"美颜启用");

    QVBoxLayout* main_layout = new QVBoxLayout(frame);
    main_layout->setContentsMargins(20, 20, 0, 0);
    main_layout->setSpacing(15);
    main_layout->addLayout(hlayout);
    main_layout->addWidget(start_check_);
    main_layout->addWidget(enable_check_);
}

void OtherBeautyWidget::onOpenBeautyDir()
{
    QString dir_path = QFileDialog::getExistingDirectory(this, u8"美颜资源路径", "/");
    if (dir_path.isEmpty())
    {
        return;
    }

    beauty_path_edit_->setText(dir_path);

}
