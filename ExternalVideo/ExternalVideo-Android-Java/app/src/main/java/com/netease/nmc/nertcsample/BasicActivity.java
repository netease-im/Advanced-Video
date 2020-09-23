package com.netease.nmc.nertcsample;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.netease.lava.nertc.sdk.NERtc;
import com.netease.lava.nertc.sdk.NERtcCallback;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcParameters;
import com.netease.lava.nertc.sdk.video.NERtcRemoteVideoStreamType;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;

import java.util.ArrayList;
import java.util.List;

public class BasicActivity extends AppCompatActivity implements NERtcCallback {
    private NERtcVideoView localVideoView;
    private List<NERtcVideoView> remoteViewViews = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String roomId = getIntent().getStringExtra(Extras.ROOM_ID);
        long userId = getIntent().getLongExtra(Extras.USER_ID, -1);

        setContentView(R.layout.activity_group_video_call);

        initVideoViews();
        initPanelViews();

        setupNERtc();
        setupLocalVideo(localVideoView);
        joinChannel(userId, roomId);
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
        panel.findViewById(R.id.btn_leave).setOnClickListener(view -> leaveChannel());
    }

    private void setupNERtc() {
        NERtcParameters parameters = new NERtcParameters();
        NERtc.getInstance().setParameters(parameters); //先设置参数，后初始化
        try {
            NERtc.getInstance().init(getApplicationContext(), getString(R.string.app_key), this, null);
            NERtc.getInstance().enableLocalAudio(true);
            NERtc.getInstance().enableLocalVideo(true);
        } catch (Exception e) {
            Toast.makeText(this, "SDK初始化失败", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    protected void joinChannel(long userId, String roomId) {
        NERtc.getInstance().joinChannel("", roomId, userId);
    }

    protected void leaveChannel() {
        NERtc.getInstance().leaveChannel();
    }

    protected void setupLocalVideo(NERtcVideoView videoView) {
        videoView.setZOrderMediaOverlay(true);
        videoView.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_BALANCED);
        NERtc.getInstance().setupLocalVideoCanvas(videoView);
    }

    protected void setupRemoteVideo(NERtcVideoView videoView, long userId) {
        videoView.setZOrderMediaOverlay(true);
        videoView.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_BALANCED);
        NERtc.getInstance().setupRemoteVideoCanvas(videoView, userId);
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
                break;
            }
        }
    }

    @Override
    public void onUserVideoStart(long userId, int profile) {
        NERtc.getInstance().subscribeRemoteVideoStream(userId, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true);
    }

    @Override
    public void onJoinChannel(int i, long l, long l1) {

    }

    @Override
    public void onLeaveChannel(int i) {
        NERtc.getInstance().release();
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
}
