#ifndef AUDIOMIXSETTING_H
#define AUDIOMIXSETTING_H

#include <QWidget>
#include "nrtc_engine.h"
namespace Ui {
class AudioMixSetting;
}

class AudioMixSetting : public QWidget
{
    Q_OBJECT

public:
    explicit AudioMixSetting(QWidget *parent = nullptr);
    ~AudioMixSetting();

    void setEngine(std::shared_ptr<NRTCEngine> engine);

private slots:
    void on_pushButton_clicked(bool check);
    void on_playBtn_clicked(bool check);
    void on_audiomix1_clicked(bool check);
    void on_audiomix2_clicked(bool check);
    void onAudioValueChanged(int value);
    void onAudioMixValueChanged(int value);
private:
    Ui::AudioMixSetting *ui;
    std::shared_ptr<NRTCEngine> m_engine;
};

#endif // AUDIOMIXSETTING_H
