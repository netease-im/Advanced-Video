#ifndef _OTHER_BEAUTY_WIDGET_H
#define _OTHER_BEAUTY_WIDGET_H

#include <QWidget>

class QCheckBox;
class OtherBeautyWidget: public QWidget
{
    Q_OBJECT

public:
    OtherBeautyWidget(QWidget* parent = nullptr);
    ~OtherBeautyWidget();

private:
    void setUi();

Q_SIGNALS:
    void sigBautyEnable(const bool& enbale);
    void sigBeautyMirror(const bool& enable);
    void sigBeautyMakeup(const bool& enable);

private:
    QCheckBox* enable_check_;
    QCheckBox* mirror_check_;
    QCheckBox* makeup_check_;
};


#endif //_OTHER_BEAUTY_WIDGET_H
