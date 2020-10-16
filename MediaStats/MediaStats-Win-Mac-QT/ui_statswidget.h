/********************************************************************************
** Form generated from reading UI file 'statswidget.ui'
**
** Created by: Qt User Interface Compiler version 5.14.2
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_STATSWIDGET_H
#define UI_STATSWIDGET_H

#include <QtCore/QVariant>
#include <QtWidgets/QApplication>
#include <QtWidgets/QLabel>
#include <QtWidgets/QWidget>

QT_BEGIN_NAMESPACE

class Ui_Form
{
public:
    QLabel *label;
    QLabel *channelPeopleNum;
    QLabel *label_2;
    QLabel *duration;
    QLabel *label_4;
    QLabel *recvbitrate;
    QLabel *label_6;
    QLabel *sendbitrate;
    QLabel *label_8;
    QLabel *recvbytes;
    QLabel *label_10;
    QLabel *sendbytes;
    QLabel *label_12;
    QLabel *bandwidth;
    QLabel *label_14;
    QLabel *rttdelay;

    void setupUi(QWidget *Form)
    {
        if (Form->objectName().isEmpty())
            Form->setObjectName(QString::fromUtf8("Form"));
        Form->resize(300, 180);
        label = new QLabel(Form);
        label->setObjectName(QString::fromUtf8("label"));
        label->setGeometry(QRect(10, 10, 91, 15));
        channelPeopleNum = new QLabel(Form);
        channelPeopleNum->setObjectName(QString::fromUtf8("channelPeopleNum"));
        channelPeopleNum->setGeometry(QRect(110, 10, 141, 15));
        label_2 = new QLabel(Form);
        label_2->setObjectName(QString::fromUtf8("label_2"));
        label_2->setGeometry(QRect(10, 30, 54, 15));
        duration = new QLabel(Form);
        duration->setObjectName(QString::fromUtf8("duration"));
        duration->setGeometry(QRect(70, 30, 54, 15));
        label_4 = new QLabel(Form);
        label_4->setObjectName(QString::fromUtf8("label_4"));
        label_4->setGeometry(QRect(10, 50, 71, 16));
        recvbitrate = new QLabel(Form);
        recvbitrate->setObjectName(QString::fromUtf8("recvbitrate"));
        recvbitrate->setGeometry(QRect(90, 50, 71, 16));
        label_6 = new QLabel(Form);
        label_6->setObjectName(QString::fromUtf8("label_6"));
        label_6->setGeometry(QRect(10, 70, 71, 15));
        sendbitrate = new QLabel(Form);
        sendbitrate->setObjectName(QString::fromUtf8("sendbitrate"));
        sendbitrate->setGeometry(QRect(80, 70, 54, 15));
        label_8 = new QLabel(Form);
        label_8->setObjectName(QString::fromUtf8("label_8"));
        label_8->setGeometry(QRect(10, 90, 71, 15));
        recvbytes = new QLabel(Form);
        recvbytes->setObjectName(QString::fromUtf8("recvbytes"));
        recvbytes->setGeometry(QRect(80, 90, 151, 16));
        label_10 = new QLabel(Form);
        label_10->setObjectName(QString::fromUtf8("label_10"));
        label_10->setGeometry(QRect(10, 110, 71, 15));
        sendbytes = new QLabel(Form);
        sendbytes->setObjectName(QString::fromUtf8("sendbytes"));
        sendbytes->setGeometry(QRect(80, 110, 151, 16));
        label_12 = new QLabel(Form);
        label_12->setObjectName(QString::fromUtf8("label_12"));
        label_12->setGeometry(QRect(10, 130, 81, 15));
        bandwidth = new QLabel(Form);
        bandwidth->setObjectName(QString::fromUtf8("bandwidth"));
        bandwidth->setGeometry(QRect(100, 130, 131, 16));
        label_14 = new QLabel(Form);
        label_14->setObjectName(QString::fromUtf8("label_14"));
        label_14->setGeometry(QRect(10, 150, 191, 16));
        rttdelay = new QLabel(Form);
        rttdelay->setObjectName(QString::fromUtf8("rttdelay"));
        rttdelay->setGeometry(QRect(210, 150, 71, 16));

        retranslateUi(Form);

        QMetaObject::connectSlotsByName(Form);
    } // setupUi

    void retranslateUi(QWidget *Form)
    {
        Form->setWindowTitle(QCoreApplication::translate("Form", "Form", nullptr));
        label->setText(QCoreApplication::translate("Form", "\351\242\221\351\201\223\344\270\255\347\232\204\347\224\250\346\210\267\346\225\260\357\274\232", nullptr));
        channelPeopleNum->setText(QString());
        label_2->setText(QCoreApplication::translate("Form", "\351\242\221\351\201\223\346\227\266\351\225\277\357\274\232", nullptr));
        duration->setText(QString());
        label_4->setText(QCoreApplication::translate("Form", "\346\216\245\346\224\266\346\257\224\347\211\271\347\216\207\357\274\232", nullptr));
        recvbitrate->setText(QString());
        label_6->setText(QCoreApplication::translate("Form", "\345\217\221\351\200\201\346\257\224\347\211\271\347\216\207\357\274\232", nullptr));
        sendbitrate->setText(QString());
        label_8->setText(QCoreApplication::translate("Form", "\346\216\245\346\224\266\346\200\273\345\255\227\350\212\202\357\274\232", nullptr));
        recvbytes->setText(QString());
        label_10->setText(QCoreApplication::translate("Form", "\345\217\221\351\200\201\346\200\273\345\255\227\350\212\202\357\274\232", nullptr));
        sendbytes->setText(QString());
        label_12->setText(QCoreApplication::translate("Form", "\344\274\240\345\207\272\345\217\257\347\224\250\345\270\246\345\256\275\357\274\232", nullptr));
        bandwidth->setText(QString());
        label_14->setText(QCoreApplication::translate("Form", "SDK\345\210\260SD-RTN\350\256\277\351\227\256\350\212\202\347\202\271\347\232\204RTT\357\274\232", nullptr));
        rttdelay->setText(QString());
    } // retranslateUi

};

namespace Ui {
    class Form: public Ui_Form {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_STATSWIDGET_H
