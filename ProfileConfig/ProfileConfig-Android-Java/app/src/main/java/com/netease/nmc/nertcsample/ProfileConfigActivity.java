package com.netease.nmc.nertcsample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.netease.lava.nertc.sdk.NERtc;
import com.netease.lava.nertc.sdk.video.NERtcVideoConfig;
import com.netease.nmc.nertcsample.settings.SettingsActivity;

public class ProfileConfigActivity extends BasicActivity {
    private static final int REQUEST_CODE_SETTINGS = 10000;

    private int videoProfile;
    private int audioProfile;
    private int audioScenario;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // load before create
        loadConfig(false);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SETTINGS) {
            loadConfig(true);
        }
    }

    @Override
    protected int getPanelLayoutId() {
        return R.layout.panel_profileconfig;
    }

    @Override
    protected void initPanelViews(View panel) {
        super.initPanelViews(panel);

        panel.findViewById(R.id.btn_settings).setOnClickListener(view -> settings());
    }

    private void settings() {
        startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_CODE_SETTINGS);
    }

    private void setAudioProfile() {
        // 设置音频场景与模式，必须在 init 前设置有效。
        NERtc.getInstance().setAudioProfile(audioProfile, audioScenario);
    }

    private void setVideoProfile() {
        NERtcVideoConfig config = new NERtcVideoConfig();
        // 设置视频质量
        config.videoProfile = videoProfile;
        NERtc.getInstance().setLocalVideoConfig(config);
    }

    @Override
    protected void setupNERtc() {
        // audio profile must set before engine init
        setAudioProfile();

        super.setupNERtc();

        setVideoProfile();
    }

    private void onVideoConfigChange() {
        // 关闭本地视频采集以及发送
        NERtc.getInstance().enableLocalVideo(false);
        setVideoProfile();
        // 开启本地视频采集以及发送
        NERtc.getInstance().enableLocalVideo(true);
    }

    private void onAudioConfigChange() {
        Toast.makeText(this, R.string.audio_settings_change_hint, Toast.LENGTH_SHORT).show();
    }

    private void loadConfig(boolean reload) {
        configFromPreference(
                getSharedPreferences(getString(R.string.shared_prefs_profileconfig), Context.MODE_PRIVATE), reload);
    }

    private void configFromPreference(SharedPreferences preferences, boolean reload) {
        int videoProfile = configFromPreferenceInt(preferences, this,
                R.string.setting_video_profile_key,
                R.string.setting_video_profile_value_default);

        int audioProfile = configFromPreferenceInt(preferences, this,
                R.string.setting_audio_profile_key,
                R.string.setting_audio_profile_value_default);

        int audioScenario = configFromPreferenceInt(preferences, this,
                R.string.setting_audio_scenario_key,
                R.string.setting_audio_scenario_value_default);

        boolean videoConfigChange = reload && this.videoProfile != videoProfile;
        boolean audioConfigChange = reload && (this.audioProfile != audioProfile
                || this.audioScenario != audioScenario);

        this.videoProfile = videoProfile;
        this.audioProfile = audioProfile;
        this.audioScenario = audioScenario;

        if (videoConfigChange) {
            onVideoConfigChange();
        }
        if (audioConfigChange) {
            onAudioConfigChange();
        }
    }

    private static int configFromPreferenceInt(SharedPreferences preferences,
                                               Context context,
                                               int keyResId,
                                               int defaultValueResId) {
        String key = context.getString(keyResId);
        String defaultValue = context.getString(defaultValueResId);
        try {
            String value = preferences.getString(key, null);
            if (!TextUtils.isEmpty(value)) {
                return Integer.parseInt(value);
            }
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        return Integer.parseInt(defaultValue);
    }
}
