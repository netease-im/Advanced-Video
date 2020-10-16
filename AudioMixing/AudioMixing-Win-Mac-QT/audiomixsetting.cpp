#include "audiomixsetting.h"
#include "ui_audiomixsetting.h"
#include <QFileDialog>
#include "nertc_engine_defines.h"
#include "Toast.h"
#include <QDebug>
AudioMixSetting::AudioMixSetting(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::AudioMixSetting)
{
    ui->setupUi(this);
    ui->audioadjust->setValue(50);
    ui->audioadjust2->setValue(50);
    connect(ui->audioadjust,&QSlider::valueChanged, this,&AudioMixSetting::onAudioValueChanged);
    connect(ui->audioadjust2,&QSlider::valueChanged, this,&AudioMixSetting::onAudioMixValueChanged);
    hide();
}

AudioMixSetting::~AudioMixSetting()
{
    delete ui;
}

void AudioMixSetting::setEngine(std::shared_ptr<NRTCEngine> engine)
{
    m_engine = engine;
}

void AudioMixSetting::on_pushButton_clicked(bool check)
{
    QString fileName = QFileDialog::getOpenFileName(this,tr("音频文件选择对话框"));
    ui->audiopath->setText(fileName);
}

void AudioMixSetting::on_playBtn_clicked(bool check)
{
    QString file = ui->audiopath->text();
     if(file.length() < 0){
         Toast::showTip(QString("没有选择音频文件").toUtf8(), this);
         return;
     }
     if(ui->playBtn->text() == tr("播放"))
     {
         ui->playBtn->setText(tr("暂停"));
         nertc::NERtcCreateAudioMixingOption opt;
         memcpy(opt.path, file.toStdString().c_str(), kNERtcMaxURILength);
         opt.loop_count = -1;
         opt.send_enabled = true;
         opt.send_volume = ui->audioadjust->value();
         opt.playback_enabled = true;
         opt.playback_volume = ui->audioadjust->value();
         auto n = m_engine->startAudioMixing(&opt);
         qDebug()<<n;
     }
     else
     {
         ui->playBtn->setText(tr("播放"));
         m_engine->stopAudioMixing();
     }
 \
}

void AudioMixSetting::on_audiomix1_clicked(bool check)
{
    if(check)
    {
        nertc::NERtcCreateAudioEffectOption opt;
        memcpy(opt.path, "douyin_01.mp3", kNERtcMaxURILength);
        opt.loop_count = -1;
        opt.send_enabled = true;
        opt.send_volume = ui->audioadjust2->value();
        opt.playback_enabled = true;
        opt.playback_volume = ui->audioadjust2->value();
        m_engine->playEffect(1,&opt);
    }
    else
    {
        m_engine->stopEffect(1);
    }

}

void AudioMixSetting::on_audiomix2_clicked(bool check)
{
    if( check )
    {
        nertc::NERtcCreateAudioEffectOption opt;
        memcpy(opt.path, "douyin_02.mp3", kNERtcMaxURILength);
        opt.loop_count = -1;
        opt.send_enabled = true;
        opt.send_volume = ui->audioadjust2->value();
        opt.playback_enabled = true;
        opt.playback_volume = ui->audioadjust2->value();
        m_engine->playEffect(2,&opt);
    }
    else
    {
        m_engine->stopEffect(2);
    }

}

void AudioMixSetting::onAudioValueChanged(int value)
{
    m_engine->setAudioMixingSendVolume(value);
    m_engine->setAudioMixingPlaybackVolume(value);

}

void AudioMixSetting::onAudioMixValueChanged(int value)
{
    //逻辑自行管理，demo仅提供能力
    if(ui->audiomix1->isChecked())
    {
        m_engine->setEffectSendVolume(1, value);

        m_engine->setEffectPlaybackVolume(1,value);
    }

    if(ui->audiomix2->isChecked())
    {
        m_engine->setEffectSendVolume(2, value);

        m_engine->setEffectPlaybackVolume(2,value);
    }

}
