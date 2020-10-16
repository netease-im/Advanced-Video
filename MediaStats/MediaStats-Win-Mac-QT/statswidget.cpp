#include "statswidget.h"

StatsWidget::StatsWidget(QWidget *parent) : QWidget(parent)
{
    this->setAttribute(Qt::WA_StyledBackground,true);
    this->setStyleSheet("background-color: rgb(222,222, 222)");
    ui.setupUi(this);
    setVisible(false);
}

void StatsWidget::onRtcStats(const NERtcStats &stats)
{

    ui.duration->setText(QString("%1秒").arg(QString::number(stats.total_duration)));

    ui.recvbytes->setText(QString("%1 字节").arg(QString::number(stats.rx_bytes)));
    ui.recvbitrate->setText(QString("%1 bps").arg(QString::number(stats.rx_video_kbitrate)));

    ui.sendbytes->setText(QString("%1 字节").arg(QString::number(stats.tx_bytes)));
    ui.sendbitrate->setText(QString("%1 bps").arg(QString::number(stats.tx_video_kbitrate)));

    ui.rttdelay->setText(QString("%1 毫秒").arg(QString::number(stats.up_rtt)));
}
