package com.netease.audiomixing;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.netease.lava.nertc.sdk.NERtc;
import com.netease.lava.nertc.sdk.NERtcCallback;
import com.netease.lava.nertc.sdk.NERtcCallbackEx;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.NERtcParameters;
import com.netease.lava.nertc.sdk.stats.NERtcAudioVolumeInfo;
import com.netease.lava.nertc.sdk.video.NERtcRemoteVideoStreamType;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;

import java.util.ArrayList;
import java.util.List;

public class BasicActivity extends AppCompatActivity implements NERtcCallbackEx {

    public static final String ROOM_ID = "room_id";
    public static final String USER_ID = "user_id";

    private NERtcVideoView localVideoView;
    private List<NERtcVideoView> remoteViewViews = new ArrayList<>();
    
    protected NERtcEx neRtcEx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String roomId = getIntent().getStringExtra(ROOM_ID);
        long userId = getIntent().getLongExtra(USER_ID, -1);

        setContentView(R.layout.activity_group_video_call);
        neRtcEx = NERtcEx.getInstance();

        initVideoViews();
        initPanelViews();

        setupNERtc();
        setupLocalVideo(localVideoView);
        joinChannel(userId, roomId);
        initOptions();
    }

    @Override
    public void onBackPressed() {
        leaveChannel();
    }

    private void initVideoViews() {
        View container = findViewById(R.id.vv_container);
        localVideoView = container.findViewById(R.id.vv_local_user);
        remoteViewViews.add(container.findViewById(R.id.vv_remote_user1));
        remoteViewViews.add(container.findViewById(R.id.vv_remote_user2));
        remoteViewViews.add(container.findViewById(R.id.vv_remote_user3));
    }

    protected int getPanelLayoutId() {
        return 0;
    }

    private void initPanelViews() {
        ViewStub viewStub = findViewById(R.id.panel);
        int layoutId = getPanelLayoutId();
        if (layoutId != 0) {
            viewStub.setLayoutResource(layoutId);
        }
        View panel = viewStub.inflate();
        initPanelViews(panel);

    }

    protected void initPanelViews(View panel) {
        panel.findViewById(R.id.tv_leave).setOnClickListener(view -> leaveChannel());
    }

    /**
     * 初始化业务操作
     */
    protected void initOptions(){

    }

    private void setupNERtc() {
        NERtcParameters parameters = new NERtcParameters();
        neRtcEx.setParameters(parameters); //先设置参数，后初始化
        try {
            neRtcEx.init(getApplicationContext(), getString(R.string.app_key), this, null);
            neRtcEx.enableLocalAudio(true);
            neRtcEx.enableLocalVideo(true);
        } catch (Exception e) {
            Toast.makeText(this, "SDK初始化失败", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    protected void joinChannel(long userId, String roomId) {
        neRtcEx.joinChannel("", roomId, userId);
    }

    protected void leaveChannel() {
        neRtcEx.leaveChannel();
    }

    protected void setupLocalVideo(NERtcVideoView videoView) {
        videoView.setZOrderMediaOverlay(true);
        videoView.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_BALANCED);
        neRtcEx.setupLocalVideoCanvas(videoView);
        videoView.setVisibility(View.VISIBLE);
    }

    protected void setupRemoteVideo(NERtcVideoView videoView, long userId) {
        videoView.setZOrderMediaOverlay(true);
        videoView.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_BALANCED);
        neRtcEx.setupRemoteVideoCanvas(videoView, userId);
        videoView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUserJoined(long userId) {
        for (NERtcVideoView videoView : remoteViewViews) {
            if (videoView.getTag() == null) {
                videoView.setTag(userId);
                setupRemoteVideo(videoView, userId);
                break;
            }
        }
    }

    @Override
    public void onUserLeave(long userId, int i) {
        for (NERtcVideoView videoView : remoteViewViews) {
            Object tag = videoView.getTag();
            if (tag != null && tag.equals(userId)) {
                videoView.setTag(null);
                videoView.setVisibility(View.INVISIBLE);
                break;
            }
        }
    }

    @Override
    public void onUserVideoStart(long userId, int profile) {
        neRtcEx.subscribeRemoteVideoStream(userId, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true);
    }

    @Override
    public void onJoinChannel(int i, long l, long l1) {

    }

    @Override
    public void onLeaveChannel(int i) {
        neRtcEx.release();
        finish();
    }

    @Override
    public void onUserAudioStart(long l) {

    }

    @Override
    public void onUserAudioStop(long l) {

    }

    @Override
    public void onUserVideoStop(long l) {

    }

    @Override
    public void onDisconnect(int i) {

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
    public void onLiveStreamState(String s, String s1, int i) {

    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onWarning(int i) {

    }
}
