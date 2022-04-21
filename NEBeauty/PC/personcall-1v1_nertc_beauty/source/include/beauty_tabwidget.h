#ifndef _BEAUTY_TABWIDGET_H
#define _BEAUTY_TABWIDGET_H

#include <QDialog>

class QTabWidget;
class SkinBeautyWidget;
class FaceBeautyWidget;
class FilterBeautyWidget;
class OtherBeautyWidget;
class ItemStickerWidget;
class BeautyTabWidget: public QDialog
{
    Q_OBJECT

public:
    BeautyTabWidget(QWidget* parent = nullptr);
    ~BeautyTabWidget();

public:
    QString GetBeautyPath();
    void DisEnableBeauty();
    void GetFaceBeautyParams(std::map<int, int> &parmas_map);
    void GetSkinBeautyParams(std::map<int, int> &parmas_map);
    void GetFilterParams(QString& path, int& val);

private:
    void setUi();

Q_SIGNALS:
    void sigBeautyChanged(const int &id, const int &val);
    void sigFilterChanged(const QString &id, const int &val);

    //
    void sigBeautyStart(const bool& enable);
    void sigBautyEnable(const bool &enable);

private:
    QTabWidget* tabwidget_;
    SkinBeautyWidget* skin_beauty_widget_;
    FaceBeautyWidget* face_beauty_widget_;
    FilterBeautyWidget* filter_beauty_widget_;
    OtherBeautyWidget* other_beauty_widget_;
};

#endif
