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
    void SetBeautyStartState(const bool& beauty_state);

private:
    void setUi();

private Q_SLOTS:
    void onBeautyStart();

Q_SIGNALS:
    void sigBeautyChanged(const int &id, const int &val);
    void sigFilterChanged(const QString &id, const int &val);
    void sigItemStickerChanged(const std::string &str);

    //
    void sigBeautyStart(const bool& enable);
    void sigBautyEnable(const bool &enable);
    void sigBeautyMirror(const bool& enable);
    void sigBeautyMakeup(const bool& enable);

private:
    QTabWidget* tabwidget_;
    SkinBeautyWidget* skin_beauty_widget_;
    FaceBeautyWidget* face_beauty_widget_;
    FilterBeautyWidget* filter_beauty_widget_;
    OtherBeautyWidget* other_beauty_widget_;
    ItemStickerWidget* item_sticker_widget_;
    bool beauty_state_ = false;
};

#endif
