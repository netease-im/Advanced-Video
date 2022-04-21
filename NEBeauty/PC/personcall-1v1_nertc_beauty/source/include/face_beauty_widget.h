#ifndef _FACE_BEAUTY_WIDGET_H
#define _FACE_BEAUTY_WIDGET_H

#include <QWidget>
#include <QVector>
#include "nertc_engine_defines.h"

class BeautyItemWidget;
class FaceBeautyWidget :public QWidget
{
    Q_OBJECT

public:
    FaceBeautyWidget(QWidget* parent = nullptr);
    ~FaceBeautyWidget();

public:
    void GetFaceBeautyParams(std::map<int, int> &parmas_map);

private:
    void setUi();

Q_SIGNALS:
    void sigFaceBeautyChanged(const int &id, const int &val);

private Q_SLOTS:
    void onBeautyItemChanged(const int& id, const int &val);

private:
    QVector<QString>           label_name_;
    QVector<QString>           type_;
    QVector<QString>           value_;
    QVector<BeautyItemWidget*> items_;
    int                        param_types_beauty_[17] = {nertc::kNERtcBeautyThinFace ,
                                                          nertc::kNERtcBeautySmallFace,
                                                          nertc::kNERtcBeautyJaw,
                                                          nertc::kNERtcBeautyEyeDis,
                                                          nertc::kNERtcBeautyMouth,
                                                          nertc::kNERtcBeautyBigEye,
                                                          nertc::kNERtcBeautySmallNose,
                                                          nertc::kNERtcBeautyOpenEyeAngle,
                                                          nertc::kNERtcBeautyLongNose,
                                                          nertc::kNERtcBeautyMouthAngle,
                                                          nertc::kNERtcBeautyRenZhong,
                                                          nertc::kNERtcBeautyEyeAngle,
                                                          nertc::kNERtcBeautyRoundEye,
                                                          nertc::kNERtcBeautyVFace,
                                                          nertc::kNERtcBeautyThinUnderjaw,
                                                          nertc::kNERtcBeautyNarrowFace,
                                                          nertc::kNERtcBeautyCheekBone};

};
#endif //_FACE_BEAUTY_WIDGET_H
