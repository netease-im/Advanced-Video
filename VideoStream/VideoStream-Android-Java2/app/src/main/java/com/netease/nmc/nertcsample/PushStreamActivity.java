package com.netease.nmc.nertcsample;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.netease.lava.nertc.sdk.NERtc;
import com.netease.lava.nertc.sdk.NERtcCallbackEx;
import com.netease.lava.nertc.sdk.NERtcParameters;
import com.netease.lava.nertc.sdk.stats.NERtcAudioVolumeInfo;
import com.netease.nmc.nertcsample.pushstream.PushStream;

public class PushStreamActivity extends BasicActivity implements NERtcCallbackEx {
    private static final String TAG = "PushVideoStream";

    private Button button;

    private String pushUrl;

    private PushStream pushStream;

    private final Runnable updateUI = new Runnable() {
        @Override
        public void run() {
            // is push started
            boolean started = pushStream != null && pushStream.isStarted();
            button.setText(started
                    ? R.string.stop_push_stream
                    : R.string.start_push_stream);
        }
    };

    @Override
    protected int getPanelLayoutId() {
        return R.layout.panel_push_stream;
    }

    @Override
    protected void initPanelViews(View panel) {
        super.initPanelViews(panel);

        button = panel.findViewById(R.id.btn_push_stream);

        button.setOnClickListener(view -> togglePushStream());
    }

    private void togglePushStream() {
        if (pushStream == null) {
            return;
        }
        if (!pushStream.isStarted()) {
            if (TextUtils.isEmpty(pushUrl)) {
                return;
            }
            pushStream.start(pushUrl);
        } else {
            pushStream.stop();
        }
    }

    @Override
    protected void setupNERtc() {
        NERtcParameters parameters = new NERtcParameters();
        parameters.set(NERtcParameters.KEY_PUBLISH_SELF_STREAM, true);
        NERtc.getInstance().setParameters(parameters);

        super.setupNERtc();
    }

    @Override
    public void onJoinChannel(int i, long l, long l1) {
        super.onJoinChannel(i, l, l1);

        // delegate to PushStream
        // use room id as task id
        pushStream = new PushStream(this, roomId, userId, updateUI);
    }

    @Override
    public void onLeaveChannel(int i) {
        // stop push when leave
        PushStream pushStream = this.pushStream;
        this.pushStream = null;
        if (pushStream != null) {
            pushStream.stop();
        }

        super.onLeaveChannel(i);
    }

    @Override
    public void onUserVideoStart(long userId, int profile) {
        super.onUserVideoStart(userId, profile);

        // when user start video, update push
        if (pushStream != null) {
            pushStream.updateUser(userId, true);
        }
    }

    @Override
    public void onUserVideoStop(long userId) {
        super.onUserVideoStop(userId);

        // when user stop video, update push
        if (pushStream != null) {
            pushStream.updateUser(userId, false);
        }
    }

    @Override
    public void onUserLeave(long userId, int i) {
        super.onUserLeave(userId, i);

        // when user leave, update push
        if (pushStream != null) {
            pushStream.updateUser(userId, false);
        }
    }

    @Override
    public void onUserAudioMute(long l, boolean b) {

    }

    @Override
    public void onUserVideoMute(long l, boolean b) {

    }

    @Override
    public void onFirstAudioDataReceived(long l) {

    }

    @Override
    public void onFirstVideoDataReceived(long l) {

    }

    @Override
    public void onFirstAudioFrameDecoded(long l) {

    }

    @Override
    public void onFirstVideoFrameDecoded(long l, int i, int i1) {

    }

    @Override
    public void onUserVideoProfileUpdate(long l, int i) {

    }

    @Override
    public void onAudioDeviceChanged(int i) {

    }

    @Override
    public void onAudioDeviceStateChange(int i, int i1) {

    }

    @Override
    public void onVideoDeviceStageChange(int i) {

    }

    @Override
    public void onConnectionTypeChanged(int i) {

    }

    @Override
    public void onReJoinChannel(int i, long l) {

    }

    @Override
    public void onAudioMixingStateChanged(int i) {

    }

    @Override
    public void onAudioMixingTimestampUpdate(long l) {

    }

    @Override
    public void onAudioEffectFinished(int i) {

    }

    @Override
    public void onLocalAudioVolumeIndication(int i) {

    }

    @Override
    public void onRemoteAudioVolumeIndication(NERtcAudioVolumeInfo[] neRtcAudioVolumeInfos, int i) {

    }

    @Override
    public void onLiveStreamState(String taskID, String url, int state) {
        // delegate push state to PushStream
        if (pushStream != null) {
            pushStream.updateState(state);
        }
    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onWarning(int i) {

    }
}
