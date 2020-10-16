/********************************************************************************
** Form generated from reading UI file 'audiomixsetting.ui'
**
** Created by: Qt User Interface Compiler version 5.14.2
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_AUDIOMIXSETTING_H
#define UI_AUDIOMIXSETTING_H

#include <QtCore/QVariant>
#include <QtWidgets/QApplication>
#include <QtWidgets/QGroupBox>
#include <QtWidgets/QHBoxLayout>
#include <QtWidgets/QLabel>
#include <QtWidgets/QLineEdit>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QSlider>
#include <QtWidgets/QWidget>

QT_BEGIN_NAMESPACE

class Ui_AudioMixSetting
{
public:
    QGroupBox *groupBox;
    QSlider *audioadjust;
    QWidget *widget;
    QHBoxLayout *horizontalLayout;
    QLabel *label_2;
    QLineEdit *audiopath;
    QPushButton *pushButton;
    QLabel *label_3;
    QPushButton *playBtn;
    QGroupBox *groupBox_2;
    QLabel *label;
    QPushButton *audiomix1;
    QPushButton *audiomix2;
    QLabel *label_4;
    QSlider *audioadjust2;

    void setupUi(QWidget *AudioMixSetting)
    {
        if (AudioMixSetting->objectName().isEmpty())
            AudioMixSetting->setObjectName(QString::fromUtf8("AudioMixSetting"));
        AudioMixSetting->resize(400, 300);
        groupBox = new QGroupBox(AudioMixSetting);
        groupBox->setObjectName(QString::fromUtf8("groupBox"));
        groupBox->setGeometry(QRect(10, 20, 321, 131));
        audioadjust = new QSlider(groupBox);
        audioadjust->setObjectName(QString::fromUtf8("audioadjust"));
        audioadjust->setGeometry(QRect(70, 60, 211, 25));
        audioadjust->setMaximum(100);
        audioadjust->setOrientation(Qt::Horizontal);
        widget = new QWidget(groupBox);
        widget->setObjectName(QString::fromUtf8("widget"));
        widget->setGeometry(QRect(15, 19, 270, 25));
        horizontalLayout = new QHBoxLayout(widget);
        horizontalLayout->setObjectName(QString::fromUtf8("horizontalLayout"));
        horizontalLayout->setContentsMargins(0, 0, 0, 0);
        label_2 = new QLabel(widget);
        label_2->setObjectName(QString::fromUtf8("label_2"));

        horizontalLayout->addWidget(label_2);

        audiopath = new QLineEdit(widget);
        audiopath->setObjectName(QString::fromUtf8("audiopath"));

        horizontalLayout->addWidget(audiopath);

        pushButton = new QPushButton(widget);
        pushButton->setObjectName(QString::fromUtf8("pushButton"));

        horizontalLayout->addWidget(pushButton);

        label_3 = new QLabel(groupBox);
        label_3->setObjectName(QString::fromUtf8("label_3"));
        label_3->setGeometry(QRect(16, 60, 48, 25));
        playBtn = new QPushButton(groupBox);
        playBtn->setObjectName(QString::fromUtf8("playBtn"));
        playBtn->setGeometry(QRect(120, 90, 91, 23));
        groupBox_2 = new QGroupBox(AudioMixSetting);
        groupBox_2->setObjectName(QString::fromUtf8("groupBox_2"));
        groupBox_2->setGeometry(QRect(10, 160, 321, 111));
        label = new QLabel(groupBox_2);
        label->setObjectName(QString::fromUtf8("label"));
        label->setGeometry(QRect(10, 30, 54, 25));
        audiomix1 = new QPushButton(groupBox_2);
        audiomix1->setObjectName(QString::fromUtf8("audiomix1"));
        audiomix1->setGeometry(QRect(70, 30, 61, 25));
        audiomix1->setCheckable(true);
        audiomix1->setChecked(false);
        audiomix2 = new QPushButton(groupBox_2);
        audiomix2->setObjectName(QString::fromUtf8("audiomix2"));
        audiomix2->setGeometry(QRect(140, 30, 61, 25));
        audiomix2->setCheckable(true);
        audiomix2->setChecked(false);
        label_4 = new QLabel(groupBox_2);
        label_4->setObjectName(QString::fromUtf8("label_4"));
        label_4->setGeometry(QRect(10, 70, 54, 25));
        audioadjust2 = new QSlider(groupBox_2);
        audioadjust2->setObjectName(QString::fromUtf8("audioadjust2"));
        audioadjust2->setGeometry(QRect(70, 70, 211, 25));
        audioadjust2->setMaximum(100);
        audioadjust2->setOrientation(Qt::Horizontal);

        retranslateUi(AudioMixSetting);

        QMetaObject::connectSlotsByName(AudioMixSetting);
    } // setupUi

    void retranslateUi(QWidget *AudioMixSetting)
    {
        AudioMixSetting->setWindowTitle(QCoreApplication::translate("AudioMixSetting", "Form", nullptr));
        groupBox->setTitle(QCoreApplication::translate("AudioMixSetting", "\350\203\214\346\231\257\351\237\263\344\271\220", nullptr));
        label_2->setText(QCoreApplication::translate("AudioMixSetting", "\351\200\211\346\213\251\351\237\263\344\271\220", nullptr));
        pushButton->setText(QCoreApplication::translate("AudioMixSetting", "\351\200\211\346\213\251", nullptr));
        label_3->setText(QCoreApplication::translate("AudioMixSetting", "\350\260\203\350\212\202\351\237\263\351\207\217", nullptr));
        playBtn->setText(QCoreApplication::translate("AudioMixSetting", "\346\222\255\346\224\276", nullptr));
        groupBox_2->setTitle(QCoreApplication::translate("AudioMixSetting", "\351\237\263\346\225\210", nullptr));
        label->setText(QCoreApplication::translate("AudioMixSetting", "\351\200\211\346\213\251\351\237\263\344\271\220", nullptr));
        audiomix1->setText(QCoreApplication::translate("AudioMixSetting", "\351\237\263\346\225\2101", nullptr));
        audiomix2->setText(QCoreApplication::translate("AudioMixSetting", "\351\237\263\346\225\2102", nullptr));
        label_4->setText(QCoreApplication::translate("AudioMixSetting", "\350\260\203\350\212\202\351\237\263\351\207\217", nullptr));
    } // retranslateUi

};

namespace Ui {
    class AudioMixSetting: public Ui_AudioMixSetting {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_AUDIOMIXSETTING_H
