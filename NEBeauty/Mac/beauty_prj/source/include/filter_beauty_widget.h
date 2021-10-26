#ifndef _FILTER_BEAUTY_WIDGET_H
#define _FILTER_BEAUTY_WIDGET_H

#include <QWidget>
#include <QLabel>
#include <QVector>

class BeautyItemWidget;
class FilterBeautyWidget :public QWidget
{
    Q_OBJECT

public:
    FilterBeautyWidget(QWidget* parent = nullptr);
    ~FilterBeautyWidget();

private:
    void setUi();

Q_SIGNALS:
    void sigFilterBeautyChanged(const int &id, const int &val);
    void sigFilterPathChanged(const QString &path, const int &val);

private Q_SLOTS:
    void onBeautyItemChanged(const int& id, const int &val);

private:
    QVector<QString>           label_name_;
    QVector<QString>           type_;
    QVector<QString>           value_;
    QVector<BeautyItemWidget*> items_;
    QVector<QLabel*>           labels_;
    QVector<int>               default_;
    int                        index_ = 0;
    QStringList                filter_path_list_;
};


#endif //_FILTER_BEAUTY_WIDGET_H
