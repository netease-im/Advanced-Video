#ifndef STATSWIDGET_H
#define STATSWIDGET_H

#include <QObject>
#include <QWidget>
#include "nrtc_engine.h"
#include "ui_statswidget.h"

class StatsWidget : public QWidget
{
    Q_OBJECT
public:
    explicit StatsWidget(QWidget *parent = nullptr);

signals:

public:
    void onRtcStats(const nertc::NERtcStats &stats);
private:
    Ui::Form ui;
};

#endif // STATSWIDGET_H
