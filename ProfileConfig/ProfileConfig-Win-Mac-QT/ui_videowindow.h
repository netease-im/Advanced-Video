/********************************************************************************
** Form generated from reading UI file 'videowindow.ui'
**
** Created by: Qt User Interface Compiler version 5.14.2
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_VIDEOWINDOW_H
#define UI_VIDEOWINDOW_H

#include <QtCore/QVariant>
#include <QtWidgets/QApplication>
#include <QtWidgets/QComboBox>
#include <QtWidgets/QGridLayout>
#include <QtWidgets/QLabel>
#include <QtWidgets/QMainWindow>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QWidget>
#include <videowidget.h>

QT_BEGIN_NAMESPACE

class Ui_MainWindow
{
public:
    QWidget *centralwidget;
    QPushButton *disconnectBtn;
    QWidget *layoutWidget;
    QGridLayout *gridLayout;
    VideoWidget *video_1;
    VideoWidget *video_3;
    VideoWidget *video_4;
    VideoWidget *video_2;
    QComboBox *videocomboBox;
    QComboBox *audiocomboBox;
    QLabel *label;
    QLabel *label_2;

    void setupUi(QMainWindow *MainWindow)
    {
        if (MainWindow->objectName().isEmpty())
            MainWindow->setObjectName(QString::fromUtf8("MainWindow"));
        MainWindow->resize(800, 620);
        MainWindow->setMinimumSize(QSize(800, 620));
        MainWindow->setMaximumSize(QSize(800, 620));
        centralwidget = new QWidget(MainWindow);
        centralwidget->setObjectName(QString::fromUtf8("centralwidget"));
        disconnectBtn = new QPushButton(centralwidget);
        disconnectBtn->setObjectName(QString::fromUtf8("disconnectBtn"));
        disconnectBtn->setGeometry(QRect(710, 580, 75, 23));
        layoutWidget = new QWidget(centralwidget);
        layoutWidget->setObjectName(QString::fromUtf8("layoutWidget"));
        layoutWidget->setGeometry(QRect(0, 0, 801, 561));
        gridLayout = new QGridLayout(layoutWidget);
        gridLayout->setSpacing(2);
        gridLayout->setObjectName(QString::fromUtf8("gridLayout"));
        gridLayout->setContentsMargins(0, 0, 0, 0);
        video_1 = new VideoWidget(layoutWidget);
        video_1->setObjectName(QString::fromUtf8("video_1"));
        video_1->setEnabled(true);

        gridLayout->addWidget(video_1, 0, 0, 1, 1);

        video_3 = new VideoWidget(layoutWidget);
        video_3->setObjectName(QString::fromUtf8("video_3"));

        gridLayout->addWidget(video_3, 1, 0, 1, 1);

        video_4 = new VideoWidget(layoutWidget);
        video_4->setObjectName(QString::fromUtf8("video_4"));

        gridLayout->addWidget(video_4, 1, 1, 1, 1);

        video_2 = new VideoWidget(layoutWidget);
        video_2->setObjectName(QString::fromUtf8("video_2"));

        gridLayout->addWidget(video_2, 0, 1, 1, 1);

        videocomboBox = new QComboBox(centralwidget);
        videocomboBox->setObjectName(QString::fromUtf8("videocomboBox"));
        videocomboBox->setGeometry(QRect(85, 581, 231, 30));
        audiocomboBox = new QComboBox(centralwidget);
        audiocomboBox->setObjectName(QString::fromUtf8("audiocomboBox"));
        audiocomboBox->setGeometry(QRect(420, 581, 231, 30));
        label = new QLabel(centralwidget);
        label->setObjectName(QString::fromUtf8("label"));
        label->setGeometry(QRect(31, 581, 48, 30));
        label_2 = new QLabel(centralwidget);
        label_2->setObjectName(QString::fromUtf8("label_2"));
        label_2->setGeometry(QRect(360, 580, 48, 30));
        MainWindow->setCentralWidget(centralwidget);

        retranslateUi(MainWindow);

        QMetaObject::connectSlotsByName(MainWindow);
    } // setupUi

    void retranslateUi(QMainWindow *MainWindow)
    {
        MainWindow->setWindowTitle(QCoreApplication::translate("MainWindow", "MainWindow", nullptr));
        disconnectBtn->setText(QCoreApplication::translate("MainWindow", "\346\226\255\345\274\200", nullptr));
        label->setText(QCoreApplication::translate("MainWindow", "\350\247\206\351\242\221\350\264\250\351\207\217", nullptr));
        label_2->setText(QCoreApplication::translate("MainWindow", "\351\237\263\351\242\221\350\264\250\351\207\217", nullptr));
    } // retranslateUi

};

namespace Ui {
    class MainWindow: public Ui_MainWindow {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_VIDEOWINDOW_H
