#ifndef _BEAUTY_ITEM_WIDGET_H
#define _BEAUTY_ITEM_WIDGET_H

#include <QWidget>

class QSpinBox;
class QSlider;
class QComboBox;
class BeautyItemWidget: public QWidget
{
    Q_OBJECT

public:
    BeautyItemWidget(const int     &id,
                     const QString &type = "slider",
                     const QString &val = "",
                     QWidget* parent = nullptr);
    ~BeautyItemWidget();

public:
    void SetValue(const int &value);
    const int GetValue();

private:
    void setUi(const QString &val);

Q_SIGNALS:
    void sigBeautyItemChanged(const int &id, const int &val);


private Q_SLOTS:
    void onCurrentValueChange(int val);

private:
    QString    type_;
    QString    default_;
    QSpinBox  *spinbox_;
    QSlider   *slider_;
    QComboBox *combobox_;
    int        id_ = -1;

};

#endif //_BEAUTY_ITEM_WIDGET_H
