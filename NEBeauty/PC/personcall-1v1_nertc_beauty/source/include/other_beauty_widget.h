#ifndef _OTHER_BEAUTY_WIDGET_H
#define _OTHER_BEAUTY_WIDGET_H

#include <QWidget>

class QCheckBox;
class QLineEdit;
class OtherBeautyWidget: public QWidget
{
    Q_OBJECT

public:
    OtherBeautyWidget(QWidget* parent = nullptr);
    ~OtherBeautyWidget();

public:
    QString GetBeautyPath();
    void DisEnableBeauty();

private:
    void setUi();

private Q_SLOTS:
    void onOpenBeautyDir();

Q_SIGNALS:
    void sigBeautyStart(const bool& enable);
    void sigBautyEnable(const bool& enbale);

private:
    QLineEdit* beauty_path_edit_;
    QCheckBox* start_check_;
    QCheckBox* enable_check_;
};


#endif //_OTHER_BEAUTY_WIDGET_H
