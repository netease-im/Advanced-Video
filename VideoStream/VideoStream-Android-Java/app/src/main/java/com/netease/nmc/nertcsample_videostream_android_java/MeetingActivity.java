package com.netease.nmc.nertcsample_videostream_android_java;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.netease.lava.nertc.sdk.NERtc;
import com.netease.lava.nertc.sdk.NERtcCallbackEx;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.NERtcOption;
import com.netease.lava.nertc.sdk.NERtcParameters;
import com.netease.lava.nertc.sdk.live.NERtcLiveStreamLayout;
import com.netease.lava.nertc.sdk.live.NERtcLiveStreamTaskInfo;
import com.netease.lava.nertc.sdk.live.NERtcLiveStreamUserTranscoding;
import com.netease.lava.nertc.sdk.stats.NERtcAudioVolumeInfo;
import com.netease.lava.nertc.sdk.video.NERtcRemoteVideoStreamType;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;

import java.util.ArrayList;
import java.util.List;

import static com.netease.lava.nertc.sdk.live.NERtcLiveStreamTaskInfo.NERtcLiveStreamMode.kNERtcLsModeVideo;
import static com.netease.lava.nertc.sdk.live.NERtcLiveStreamUserTranscoding.NERtcLiveStreamVideoScaleMode.kNERtcLsModeVideoScaleCropFill;

public class MeetingActivity extends AppCompatActivity implements NERtcCallbackEx {

    private static final String LOG_TAG = MeetingActivity.class.getCanonicalName();

    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_ROOM_ID = "extra_room_id";

    private ConstraintLayout mContainer;
    private NERtcVideoView mLocalUserVv;
    private List<NERtcVideoView> mRemoteUserVvList = new ArrayList<>();

    private List<Long> mUserIDList = new ArrayList<>();
    private String mRoomID;

    private NERtcLiveStreamTaskInfo mLiveStreamTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        initViews();
        setupNERtc();

        long userID = getIntent().getLongExtra(EXTRA_USER_ID, -1);
        mRoomID = getIntent().getStringExtra(EXTRA_ROOM_ID);
        joinChannel(userID, mRoomID);

        mUserIDList.add(userID);
    }

    private void joinChannel(long userID, String roomID) {
        NERtc.getInstance().joinChannel("", roomID, userID);
        mLocalUserVv.setZOrderMediaOverlay(true);
        mLocalUserVv.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_BALANCED);
        NERtc.getInstance().setupLocalVideoCanvas(mLocalUserVv);
    }

    private void addLiveStreamTask(String roomID) {
        mLiveStreamTask = new NERtcLiveStreamTaskInfo();
        mLiveStreamTask.taskId = roomID;
        mLiveStreamTask.url = NativeConfig.getStreamURL();
        mLiveStreamTask.liveMode = kNERtcLsModeVideo;

        int layoutWidth = 720;
        int layoutHeight = 1280;

        //设置整体布局
        NERtcLiveStreamLayout layout = new NERtcLiveStreamLayout();
        layout.width = layoutWidth; //整体布局宽度
        layout.height = layoutHeight; //整体布局高度
        // layout.backgroundImg = <在这里设置背景图片(可选)>
        // layout.backgroundColor = <在这里设置背景色(可选)>
        mLiveStreamTask.layout = layout;

        reloadUsers();

        int ret = NERtcEx.getInstance().addLiveStreamTask(mLiveStreamTask, (s, i) -> {
            if (i == 0) {
                Log.i(LOG_TAG, "添加成功");
            } else {
                Log.e(LOG_TAG, "添加任务失败, error=" + i);
            }
        });
        if (ret != 0) {
            Log.e(LOG_TAG, "添加推流任务失败error=" + ret);
        }
    }

    private void updateLiveStreamTask() {
        int ret = NERtcEx.getInstance().updateLiveStreamTask(mLiveStreamTask, (s, i) -> {
            if (i == 0) {
                Log.i(LOG_TAG, "更新成功");
            } else {
                Log.e(LOG_TAG, "更新任务失败, error=" + i);
            }
        });
        if (ret != 0) {
            Log.d(LOG_TAG, "更新推流任务失败error=" + ret);
        }
    }


    private void reloadUsers() {
        int layoutWidth = mLiveStreamTask.layout.width;
        int userWidth = 320;
        int userHeight = 480;
        int horizPadding = (layoutWidth-userWidth*2)/3;
        int vertPadding = 15;
        ArrayList<NERtcLiveStreamUserTranscoding> transcodingList = new ArrayList<>();
        for (int i = 0; i < mUserIDList.size(); i++) {
            int column = i % 2;
            int row = i / 2;
            long userID = mUserIDList.get(i);
            NERtcLiveStreamUserTranscoding userTranscoding = new NERtcLiveStreamUserTranscoding();
            userTranscoding.uid = userID;
            userTranscoding.audioPush = true;
            userTranscoding.videoPush = true;
            userTranscoding.x = column == 0 ? horizPadding : horizPadding * 2 + userWidth;
            userTranscoding.y = vertPadding * (row + 1) + userHeight * row;
            userTranscoding.width = userWidth;
            userTranscoding.height = userHeight;
            userTranscoding.adaption = kNERtcLsModeVideoScaleCropFill;
            transcodingList.add(userTranscoding);
        }
        mLiveStreamTask.layout.userTranscodingList = transcodingList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLiveStreamTask != null) {
            int ret = NERtcEx.getInstance().removeLiveStreamTask(mLiveStreamTask.taskId, (s, i) -> {
                if (i == 0) {
                    Log.i(LOG_TAG, "移除成功");
                } else {
                    Log.e(LOG_TAG, "移除任务失败, error=" + i);
                }
            });
            if (ret != 0) {
                Log.e(LOG_TAG, "移除任务失败, error=" + ret);
            }
        }
        NERtc.getInstance().leaveChannel();
        NERtc.getInstance().release();
    }

    private void initViews() {
        mContainer = findViewById(R.id.container);
        mLocalUserVv = findViewById(R.id.vv_local_user);
        mRemoteUserVvList.add(findViewById(R.id.vv_remote_user1));
        mRemoteUserVvList.add(findViewById(R.id.vv_remote_user2));
        mRemoteUserVvList.add(findViewById(R.id.vv_remote_user3));
    }

    private void setupNERtc() {
        NERtcParameters parameters = new NERtcParameters();
        parameters.set(NERtcParameters.KEY_PUBLISH_SELF_STREAM, true);
        NERtc.getInstance().setParameters(parameters); //先设置参数，后初始化
        try {
            NERtc.getInstance().init(getApplicationContext(), NativeConfig.getAppKey(), this, null);
            NERtc.getInstance().enableLocalAudio(true);
            NERtc.getInstance().enableLocalVideo(true);
        } catch (Exception e) {
            Toast.makeText(this, "SDK初始化失败", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onUserJoined(long userID) {
        for (NERtcVideoView videoView : mRemoteUserVvList) {
            if (videoView.getTag() == null) {
                videoView.setZOrderMediaOverlay(true);
                videoView.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_BALANCED);
                NERtc.getInstance().setupRemoteVideoCanvas(videoView, userID);
                videoView.setTag(userID);
                break;
            }
        }
    }

    @Override
    public void onUserLeave(long userID, int i) {
        NERtcVideoView userView = mContainer.findViewWithTag(userID);
        if (userView != null) {
            userView.setTag(null);
        }
    }

    @Override
    public void onUserVideoStart(long userID, int profile) {
        NERtc.getInstance().subscribeRemoteVideoStream(userID, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true);
        if (!mUserIDList.contains(userID)) {
            mUserIDList.add(userID);
            reloadUsers();
            updateLiveStreamTask();
        }
    }

    @Override
    public void onUserVideoStop(long l) {
        if (mUserIDList.contains(l)) {
            mUserIDList.remove(l);
            reloadUsers();
            updateLiveStreamTask();
        }
    }

    @Override
    public void onJoinChannel(int result, long l, long l1) {
        if (result == 0) {
            addLiveStreamTask(mRoomID);
        } else {
            Log.e(LOG_TAG, String.format("加入频道失败 error=[%d]", result));
        }
    }

    @Override
    public void onLiveStreamState(String taskID, String url, int state) {
        switch (state) {
            case NERtcConstants.LiveStreamState.STATE_PUSHING:
                Log.i(LOG_TAG, String.format("Pushing stream for task [%s]", taskID));
                break;
            case NERtcConstants.LiveStreamState.STATE_PUSH_STOPPED:
                Log.i(LOG_TAG, String.format("Stream for task [%s] stopped", taskID));
                break;
            case NERtcConstants.LiveStreamState.STATE_PUSH_FAIL:
                Log.e(LOG_TAG, String.format("Stream for task [%s] failed", taskID));
                break;
            default:
                Log.e(LOG_TAG, String.format("Unknown state for task [%s]", taskID));
                break;
        }
    }

    @Override
    public void onLeaveChannel(int i) {

    }

    @Override
    public void onUserAudioStart(long l) {

    }

    @Override
    public void onUserAudioStop(long l) {

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
    public void onError(int i) {

    }

    @Override
    public void onWarning(int i) {

    }
}
