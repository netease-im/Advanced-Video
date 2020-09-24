package com.netease.mediastats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.netease.lava.nertc.sdk.NERtcCallback;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.NERtcParameters;
import com.netease.lava.nertc.sdk.stats.NERtcAudioRecvStats;
import com.netease.lava.nertc.sdk.stats.NERtcAudioSendStats;
import com.netease.lava.nertc.sdk.stats.NERtcNetworkQualityInfo;
import com.netease.lava.nertc.sdk.stats.NERtcStats;
import com.netease.lava.nertc.sdk.stats.NERtcStatsObserver;
import com.netease.lava.nertc.sdk.stats.NERtcVideoRecvStats;
import com.netease.lava.nertc.sdk.stats.NERtcVideoSendStats;
import com.netease.lava.nertc.sdk.video.NERtcRemoteVideoStreamType;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;

import java.util.Locale;
import java.util.Random;

public class MeetingActivity extends AppCompatActivity implements NERtcCallback, View.OnClickListener, NERtcStatsObserver {

    private static final String TAG = "MeetingActivity";
    private static final String EXTRA_ROOM_ID = "extra_room_id";

    private boolean enableLocalVideo = true;
    private boolean enableLocalAudio = true;
    private boolean joinedChannel = false;

    private NERtcVideoView localUserVv;
    private NERtcVideoView remoteUserVv;
    private TextView waitHintTv;
    private TextView tvLeave;
    private TextView tvShow;
    private View localUserBgV;

    //********* stats *********//
    private LinearLayout llyStats;
    private TextView channelInfo;
    private TextView videoSendStats;
    private TextView audioSendStats;
    private TextView videoRecvStats;
    private TextView audioRecvStats;
    private TextView systemStats;
    private TextView networkRecvStats;
    private TextView networkSendStats;
    //********* stats *********//

    private long selfUid;//别人UID

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
        selfUid = generateRandomUserID();
        joinChannel(selfUid, roomId);
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
        localUserVv.setZOrderMediaOverlay(true);
        localUserVv.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_FIT);
        NERtcEx.getInstance().setupLocalVideoCanvas(localUserVv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NERtcEx.getInstance().release();
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void initViews() {
        localUserVv = findViewById(R.id.vv_local_user);
        remoteUserVv = findViewById(R.id.vv_remote_user);
        waitHintTv = findViewById(R.id.tv_wait_hint);
        localUserBgV = findViewById(R.id.v_local_user_bg);
        tvShow = findViewById(R.id.tv_show);
        tvLeave = findViewById(R.id.tv_leave);

        llyStats = findViewById(R.id.lly_stats);
        channelInfo = findViewById(R.id.channel_id);
        videoSendStats = findViewById(R.id.video_send_stats);
        audioSendStats = findViewById(R.id.audio_send_stats);
        videoRecvStats = findViewById(R.id.video_recv_stats);
        audioRecvStats = findViewById(R.id.audio_recv_stats);
        systemStats = findViewById(R.id.system_stats);
        networkRecvStats = findViewById(R.id.network_recv_stats);
        networkSendStats = findViewById(R.id.network_send_stats);

        localUserVv.setVisibility(View.INVISIBLE);
        tvLeave.setOnClickListener(this);
        tvShow.setOnClickListener(this);
    }

    /**
     * 初始化SDK
     */
    private void setupNERtc() {
        NERtcParameters parameters = new NERtcParameters();
        parameters.set(NERtcParameters.KEY_AUTO_SUBSCRIBE_AUDIO, false);
        NERtcEx.getInstance().setParameters(parameters); //先设置参数，后初始化

        try {
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
        NERtcEx.getInstance().setStatsObserver(this);
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
    }

    @Override
    public void onJoinChannel(int result, long channelId, long elapsed) {
        Log.i(TAG, "onJoinChannel result: " + result + " channelId: " + channelId + " elapsed: " + elapsed);
        if (result == NERtcConstants.ErrorCode.OK) {
            joinedChannel = true;
            // 加入房间，准备展示己方视频

            localUserVv.setVisibility(View.VISIBLE);
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
        if (remoteUserVv.getTag() != null) {
            return;
        }
        // 有用户加入，设置Tag，该用户离开前，只订阅和取消订阅此用户
        remoteUserVv.setTag(uid);
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
        remoteUserVv.setTag(null);
        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, false);

        // 显示在等待用户进入房间
        waitHintTv.setVisibility(View.VISIBLE);
        // 不展示远端
        remoteUserVv.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onUserAudioStart(long uid) {
        Log.i(TAG, "onUserAudioStart uid: " + uid);
        if (!isCurrentUser(uid)) {
            return;
        }
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

        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true);
        remoteUserVv.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_FIT);
        NERtcEx.getInstance().setupRemoteVideoCanvas(remoteUserVv, uid);

        // 更新界面
        remoteUserVv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUserVideoStop(long uid) {
        Log.i(TAG, "onUserVideoStop, uid=" + uid);
        if (!isCurrentUser(uid)) {
            return;
        }
        // 不展示远端
        remoteUserVv.setVisibility(View.INVISIBLE);
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
        Object tag = remoteUserVv.getTag();
        Log.i(TAG, "isCurrentUser tag=" + tag);
        return tag != null && tag.equals(uid);
    }

    /**
     * 设置本地音频的可用性
     */
    private void setLocalAudioEnable(boolean enable) {
        enableLocalAudio = enable;
        NERtcEx.getInstance().enableLocalAudio(enableLocalAudio);
    }

    /**
     * 设置本地视频的可用性
     */
    private void setLocalVideoEnable(boolean enable) {
        enableLocalVideo = enable;
        NERtcEx.getInstance().enableLocalVideo(enableLocalVideo);
        localUserVv.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
        localUserBgV.setBackgroundColor(getResources().getColor(enable ? R.color.white : R.color.black));
    }

    private void changeStatsShow(){
        if(llyStats.getVisibility() == View.VISIBLE){
            tvShow.setText(R.string.show_stats);
            llyStats.setVisibility(View.GONE);
        }else {
            tvShow.setText(R.string.hide_stats);
            llyStats.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_leave:
                exit();
                break;
            case R.id.tv_show:
                changeStatsShow();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRtcStats(NERtcStats neRtcStats) {
        String text = String.format(Locale.CHINA, "累计发送: %.2f MB\n累计接受: %.2f MB\n本地上行视频丢包率: %d%%\n本地下行视频丢包率: %d%%\n本地上行音频丢包率: %d%%\n本地下行音频丢包率: %d%%",
                neRtcStats.txBytes / 1024.0f, neRtcStats.rxBytes / 1024.0f, neRtcStats.txVideoPacketLossRate,
                neRtcStats.rxVideoPacketLossRate,neRtcStats.txAudioPacketLossRate,neRtcStats.rxAudioPacketLossRate);
        systemStats.setText(text);
    }

    @Override
    public void onLocalAudioStats(NERtcAudioSendStats stats) {
        String text = String.format(Locale.CHINA, "音频发送码率: %d Kbps\n发送音频丢包率: %d\n音量: %d\n环路延时:%d",
                stats.kbps, stats.lossRate, stats.volume, stats.rtt);
        audioSendStats.setText(text);
    }

    @Override
    public void onRemoteAudioStats(NERtcAudioRecvStats[] statsArray) {
        if (statsArray != null && statsArray[0] != null) {
            NERtcAudioRecvStats stats = statsArray[0];
            String text = String.format(Locale.CHINA, "音频接受码率: %d Kbps\n音频丢包率: %d\n 音量: %d",
                    stats.kbps, stats.lossRate, stats.volume);
            audioRecvStats.setText(text);
        }
    }

    @Override
    public void onLocalVideoStats(NERtcVideoSendStats stats) {
        String text = String.format(Locale.CHINA, "uid: %d\n大小: %dx%d\n视频发送帧率: %d\n发送码率: %d Kbps",
                selfUid, stats.width, stats.height, stats.sentFrameRate, stats.sendBitrate);
        videoSendStats.setText(text);
    }

    @Override
    public void onRemoteVideoStats(NERtcVideoRecvStats[] statsArray) {
        if (statsArray != null && statsArray[0] != null) {
            NERtcVideoRecvStats stats = statsArray[0];
            String text = String.format(Locale.CHINA, "对方uid: %d\n大小: %dx%d\n视频接收帧率: %d\n接收到的码率: %d Kbps",
                    stats.uid, stats.width, stats.height, stats.fps, stats.receivedBitrate);
            videoRecvStats.setText(text);
        }
    }

    @Override
    public void onNetworkQuality(NERtcNetworkQualityInfo[] statsArray) {
        if (statsArray != null) {
            for (NERtcNetworkQualityInfo info : statsArray) {
                String text = String.format(Locale.CHINA, "network up: %d,down: %d",
                        info.upStatus, info.downStatus);
                if (info.userId == selfUid) {
                    networkSendStats.setText(text);
                } else {
                    networkRecvStats.setText(text);
                }
            }
        }
    }

}
