#ifndef _SKIN_BEAUTY_WIDGET_H
#define _SKIN_BEAUTY_WIDGET_H

#include <QWidget>
#include <QVector>
#include "nertc_engine_defines.h"

class BeautyItemWidget;
class SkinBeautyWidget:public QWidget
{
    Q_OBJECT

public:
    SkinBeautyWidget(QWidget* parent = nullptr);
    ~SkinBeautyWidget();

private:
    void setUi();

Q_SIGNALS:
    void sigSkinBeautyChanged(const int &id, const int &val);

private Q_SLOTS:
    void onBeautyItemChanged(const int& id, const int &val);

private:
    QVector<QString>           label_name_;
    QVector<QString>           type_;
    QVector<QString>           value_;
    QVector<BeautyItemWidget*> items_;
    int                        param_types_beauty_[4] = {
                                                         nertc::kNERtcBeautySmooth,
                                                         nertc::kNERtcBeautyWhiten,
                                                         nertc::kNERtcBeautyLightEye,
                                                         nertc::kNERtcBeautyWhiteTeeth };

}; 
#endif //_SKIN_BEAUTY_WIDGET_H
