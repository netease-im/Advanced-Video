package com.netease.nmc.nertcsample.voicechanger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.netease.lava.nertc.sdk.NERtcCallback;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.NERtcParameters;
import com.netease.lava.nertc.sdk.video.NERtcRemoteVideoStreamType;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;
import com.netease.nmc.nertcsample.voicechanger.config.NativeConfig;
import com.netease.nmc.nertcsample.voicechanger.voice.ui.VoiceConfigDialog;

import java.util.Random;

public class MeetingActivity extends AppCompatActivity implements NERtcCallback,
        View.OnClickListener {

    private static final String TAG = "MeetingActivity";
    private static final String EXTRA_ROOM_ID = "extra_room_id";

    private boolean joinedChannel = false;
    /**
     * local VideoView 是否放大，默认false
     */
    private boolean localLarge = false;

    private NERtcVideoView smallVideoView;
    private NERtcVideoView bigVideoView;
    private TextView waitHintTv;
    private View localUserBgV;
    private VoiceConfigDialog voiceConfigDialog;

    /**
     * 远端用户id
     */
    private long remoteUid;

    public static void startActivity(Activity from, String roomId) {
        Intent intent = new Intent(from, MeetingActivity.class);
        intent.putExtra(EXTRA_ROOM_ID, roomId);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_meeting);
        initViews();
        setupNERtc();
        String roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
        long userId = generateRandomUserID();
        joinChannel(userId, roomId);

        this.voiceConfigDialog = new VoiceConfigDialog(this);
    }

    /**
     * 加入房间
     *
     * @param userID 用户ID
     * @param roomID 房间ID
     */
    private void joinChannel(long userID, String roomID) {
        Log.i(TAG, "joinChannel userId: " + userID);
        NERtcEx.getInstance().joinChannel(null, roomID, userID);
        smallVideoView.setZOrderMediaOverlay(true);
        smallVideoView.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_FIT);
        // setupLocalVideoCanvas 设置本地视频采集流通过对应设置view 完成展示；
        if (!localLarge) {
            NERtcEx.getInstance().setupLocalVideoCanvas(smallVideoView);
        } else {
            NERtcEx.getInstance().setupLocalVideoCanvas(bigVideoView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放 sdk 资源，下次使用仍需重新 init 操作
        NERtcEx.getInstance().release();
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    /**
     * view 初始化，设置监听等
     */
    private void initViews() {
        smallVideoView = findViewById(R.id.vv_local_user);
        bigVideoView = findViewById(R.id.vv_remote_user);
        waitHintTv = findViewById(R.id.tv_wait_hint);
        ImageView cameraFlipImg = findViewById(R.id.img_camera_flip);
        localUserBgV = findViewById(R.id.v_local_user_bg);
        TextView tvLeave = findViewById(R.id.tv_leave);
        TextView tvConfigVoice = findViewById(R.id.tv_config_voice);

        smallVideoView.setVisibility(View.INVISIBLE);
        tvLeave.setOnClickListener(this);
        tvConfigVoice.setOnClickListener(this);
        cameraFlipImg.setOnClickListener(this);
        smallVideoView.setOnClickListener(this);
    }

    /**
     * 初始化SDK
     */
    private void setupNERtc() {
        // 用于演示，此处设置 sdk 初始化后不自动采集发送本地音频流
        NERtcParameters parameters = new NERtcParameters();
        parameters.set(NERtcParameters.KEY_AUTO_SUBSCRIBE_AUDIO, false);
        NERtcEx.getInstance().setParameters(parameters); //先设置参数，后初始化
        try {
            // 填入对应的appkey，初始化回调完成sdk 初始化
            NERtcEx.getInstance().init(getApplicationContext(), NativeConfig.getAppKey(), this, null);
        } catch (Exception e) {
            // 可能由于没有release导致初始化失败，release后再试一次
            NERtcEx.getInstance().release();
            try {
                NERtcEx.getInstance().init(getApplicationContext(), NativeConfig.getAppKey(), this, null);
            } catch (Exception ex) {
                Toast.makeText(this, "SDK初始化失败", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
        setLocalAudioEnable(true);
        setLocalVideoEnable(true);
    }

    /**
     * 随机生成用户ID
     *
     * @return 用户ID
     */
    private int generateRandomUserID() {
        return new Random().nextInt(100000);
    }

    /**
     * 退出房间
     *
     * @return 返回码
     * @see com.netease.lava.nertc.sdk.NERtcConstants.ErrorCode
     */
    private boolean leaveChannel() {
        joinedChannel = false;
        setLocalAudioEnable(false);
        setLocalVideoEnable(false);
        // 相对于 joinChannel，退出房间
        int ret = NERtcEx.getInstance().leaveChannel();
        return ret == NERtcConstants.ErrorCode.OK;
    }

    /**
     * 退出房间并关闭页面
     */
    private void exit() {
        if (joinedChannel) {
            leaveChannel();
        }
        finish();
        // 重置 dialog资源信息
        voiceConfigDialog.release();
    }

    @Override
    public void onJoinChannel(int result, long channelId, long elapsed) {
        Log.i(TAG, "onJoinChannel result: " + result + " channelId: " + channelId + " elapsed: " + elapsed);
        if (result == NERtcConstants.ErrorCode.OK) {
            joinedChannel = true;
            // 加入房间，准备展示己方视频
            smallVideoView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLeaveChannel(int result) {
        Log.i(TAG, "onLeaveChannel result: " + result);
    }

    @Override
    public void onUserJoined(long uid) {
        Log.i(TAG, "onUserJoined uid: " + uid);
        // 已经有订阅，就不要变了
        if (bigVideoView.getTag() != null) {
            return;
        }
        // 有用户加入，设置Tag，该用户离开前，只订阅和取消订阅此用户
        bigVideoView.setTag(uid);
        // 不用等待了
        waitHintTv.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onUserLeave(long uid, int reason) {
        Log.i(TAG, "onUserLeave uid: " + uid + " reason: " + reason);
        // 退出的不是当前订阅的对象，则不作处理
        if (!isCurrentUser(uid)) {
            return;
        }
        // 设置TAG为null，代表当前没有订阅
        bigVideoView.setTag(null);
        // 取消订阅对应用户的远端视频流接收
        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, false);

        // 显示在等待用户进入房间
        waitHintTv.setVisibility(View.VISIBLE);
        // 不展示远端
        bigVideoView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onUserAudioStart(long uid) {
        Log.i(TAG, "onUserAudioStart uid: " + uid);
        if (!isCurrentUser(uid)) {
            return;
        }
        // 通过远端用户 id 订阅对应远端用户音频流，如果不订阅无法听到远端用户声音
        NERtcEx.getInstance().subscribeRemoteAudioStream(uid, true);
    }

    @Override
    public void onUserAudioStop(long uid) {
        Log.i(TAG, "onUserAudioStop, uid=" + uid);
    }

    @Override
    public void onUserVideoStart(long uid, int profile) {
        Log.i(TAG, "onUserVideoStart uid: " + uid + " profile: " + profile);
        if (!isCurrentUser(uid)) {
            return;
        }

        remoteUid = uid;
        // 通过远端用户 id 订阅对应远端用户视频流，如果不订阅无法听到远端用户视频内容
        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true);
        bigVideoView.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_FIT);
        // 设置远端视频流通过哪一个 surfaceView 进行渲染
        if (!localLarge) {
            NERtcEx.getInstance().setupRemoteVideoCanvas(bigVideoView, uid);
        } else {
            NERtcEx.getInstance().setupRemoteVideoCanvas(smallVideoView, uid);
        }

        // 更新界面
        bigVideoView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUserVideoStop(long uid) {
        Log.i(TAG, "onUserVideoStop, uid=" + uid);
        if (!isCurrentUser(uid)) {
            return;
        }
        // 不展示远端
        bigVideoView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDisconnect(int reason) {
        Log.i(TAG, "onDisconnect reason: " + reason);
        if (reason != NERtcConstants.ErrorCode.OK) {
            finish();
        }
    }

    /**
     * 判断是否为onUserJoined中，设置了Tag的用户
     *
     * @param uid 用户ID
     * @return 用户ID是否匹配
     */
    private boolean isCurrentUser(long uid) {
        Object tag = bigVideoView.getTag();
        Log.i(TAG, "isCurrentUser tag=" + tag);
        return tag != null && tag.equals(uid);
    }

    /**
     * 设置本地音频的可用性，设置为true 允许本地采集并发送本地音频至远端，false 不允许
     */
    private void setLocalAudioEnable(boolean enable) {
        NERtcEx.getInstance().enableLocalAudio(enable);
    }

    /**
     * 设置本地视频的可用性，设置为true 允许本地采集并发送本地视频至远端，false 不允许
     */
    private void setLocalVideoEnable(boolean enable) {
        NERtcEx.getInstance().enableLocalVideo(enable);
        smallVideoView.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
        localUserBgV.setBackgroundColor(getResources().getColor(enable ? R.color.white : R.color.black));
    }

    /**
     * 切换大小视图
     */
    private void switchVideoView() {
        if (remoteUid == 0) {
            return;
        }

        /*
            setupLocalVideoCanvas 设置本地视频采集流通过对应设置view 完成展示，用户自己；
            setupRemoteVideoCanvas  设置远端视频流接收后通过对应设置view 完成展示，其他用户；
         */
        if (!localLarge) {
            NERtcEx.getInstance().setupLocalVideoCanvas(bigVideoView);
            NERtcEx.getInstance().setupRemoteVideoCanvas(smallVideoView, remoteUid);
        } else {
            NERtcEx.getInstance().setupLocalVideoCanvas(smallVideoView);
            NERtcEx.getInstance().setupRemoteVideoCanvas(bigVideoView, remoteUid);
        }
        localLarge = !localLarge;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_leave:
                exit();
                break;
            case R.id.tv_config_voice:
                if (!voiceConfigDialog.isShowing()) {
                    voiceConfigDialog.show();
                }
                break;
            case R.id.img_camera_flip:
                // 切换前置/后置摄像头
                NERtcEx.getInstance().switchCamera();
                break;
            case R.id.vv_local_user:
                switchVideoView();
                break;
            default:
                break;
        }
    }
}
