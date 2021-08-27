package com.netease.nmc.nertcsample;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.NERtcOption;
import com.netease.lava.nertc.sdk.NERtcParameters;
import com.netease.lava.nertc.sdk.video.NERtcRemoteVideoStreamType;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;

import java.util.ArrayList;
import java.util.List;

public class BasicActivity extends AppCompatActivity {

    private static final String TAG = "BasicActivity";

    private Handler uiHandler;
    private List<NERtcVideoView> rendererViews = new ArrayList<>();
    private long selfUid;
    private String roomId;
    private TextView tvLocalInfo;

    protected NERtcVideoView localVideoRenderer;
    protected NERtcVideoView localScreenRenderer;


    private Runnable updateLocalInfoTask = new Runnable() {
        @Override
        public void run() {
            tvLocalInfo.setText("UID : " + selfUid + ", Thread Time : " + SystemClock.currentThreadTimeMillis());
            uiHandler.postDelayed(updateLocalInfoTask, 1000);
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHandler = new Handler();
        roomId = getIntent().getStringExtra(Extras.ROOM_ID);
        selfUid = getIntent().getLongExtra(Extras.USER_ID, -1);

        setContentView(R.layout.activity_group_video_call);
        initViews();
        setupNERtc();
        joinChannel(selfUid, roomId);
    }

    @Override
    public void onBackPressed() {
        leaveChannel();
    }

    private void initViews() {
        View container = findViewById(R.id.vv_container);
        localVideoRenderer = container.findViewById(R.id.vv_local_video_renderer);
        localScreenRenderer = container.findViewById(R.id.vv_local_screen_renderer);
        rendererViews.clear();
        rendererViews.add(container.findViewById(R.id.vv_remote_renderer_1));
        rendererViews.add(container.findViewById(R.id.vv_remote_renderer_2));
        rendererViews.add(container.findViewById(R.id.vv_remote_renderer_3));
        rendererViews.add(container.findViewById(R.id.vv_remote_renderer_4));

        ViewStub viewStub = findViewById(R.id.panel);
        int layoutId = getPanelLayoutId();
        if (layoutId != 0) {
            viewStub.setLayoutResource(layoutId);
        }
        View panel = viewStub.inflate();
        initPanelViews(panel);

        tvLocalInfo = findViewById(R.id.tv_local_info);
        uiHandler.post(updateLocalInfoTask);
    }

    protected int getPanelLayoutId() {
        return 0;
    }

    protected void initPanelViews(View panel) {
        panel.findViewById(R.id.btn_leave).setOnClickListener(view -> leaveChannel());
    }

    private void setupNERtc() {
        NERtcParameters parameters = new NERtcParameters();
        //先设置参数，后初始化
        NERtcEx.getInstance().setParameters(parameters);

        NERtcOption options = new NERtcOption();

        if (BuildConfig.DEBUG) {
            options.logLevel = NERtcConstants.LogLevel.INFO;
        } else {
            options.logLevel = NERtcConstants.LogLevel.WARNING;
        }

        try {
            NERtcEx.getInstance().init(getApplicationContext(), getString(R.string.app_key), simpleNERtcCallbackEx, options);
            NERtcEx.getInstance().enableLocalAudio(true);
//            enableLocalVideo(true);
        } catch (Exception e) {
            Toast.makeText(this, "SDK初始化失败", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    protected void joinChannel(long userId, String roomId) {
        NERtcEx.getInstance().joinChannel("", roomId, userId);
    }

    protected void leaveChannel() {
        uiHandler.removeCallbacks(updateLocalInfoTask);
        NERtcEx.getInstance().leaveChannel();
    }

    protected void setupRemoteVideoRenderer(NERtcVideoView videoView, long userId) {
        if (videoView != null) {
            videoView.setTag("video#" + userId);
            videoView.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_FIT);
        }
        NERtcEx.getInstance().setupRemoteVideoCanvas(videoView, userId);
    }

    protected void setupRemoteScreenRenderer(NERtcVideoView videoView, long userId) {
        if (videoView != null) {
            videoView.setTag("screen#" + userId);
            videoView.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_FIT);

        }
        NERtcEx.getInstance().setupRemoteSubStreamVideoCanvas(videoView, userId);
    }


    private final SimpleNERtcCallbackEx simpleNERtcCallbackEx = new SimpleNERtcCallbackEx() {

        @Override
        public void onUserJoined(long userId) {

        }

        @Override
        public void onUserLeave(long userId, int reason) {

        }

        /**
         * 远端用户开启视频
         */
        @Override
        public void onUserVideoStart(long userId, int profile) {
            for (NERtcVideoView videoView : rendererViews) {
                //设置画布并订阅
                if (videoView.getTag() == null) {
                    setupRemoteVideoRenderer(videoView, userId);
                    NERtcEx.getInstance().subscribeRemoteVideoStream(userId, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true);
                    return;
                }
            }
            Log.e(TAG, "onUserVideoStart , uid : " + userId + " , but cannot find idle renderer");
        }

        /**
         * 远端用户关闭视频
         */
        @Override
        public void onUserVideoStop(long userId) {
            String videoTag = "video#" + userId;
            for (NERtcVideoView videoView : rendererViews) {
                if (videoTag.equals(videoView.getTag())) {
                    videoView.setTag(null);
                    setupRemoteVideoRenderer(null, userId);
                    return;
                }
            }

        }

        /**
         * 远端用户开启屏幕共享
         */
        @Override
        public void onUserSubStreamVideoStart(long userId, int maxProfile) {
            for (NERtcVideoView videoView : rendererViews) {
                //设置画布并订阅
                if (videoView.getTag() == null) {
                    setupRemoteScreenRenderer(videoView, userId);
                    NERtcEx.getInstance().subscribeRemoteSubStreamVideo(userId, true);
                    return;
                }
            }
            Log.e(TAG, "onUserSubStreamVideoStart , uid : " + userId + " , but cannot find idle renderer");

        }

        /**
         * 远端用户关闭屏幕共享
         */
        @Override
        public void onUserSubStreamVideoStop(long userId) {
            String screenTag = "screen#" + userId;
            for (NERtcVideoView videoView : rendererViews) {
                if (screenTag.equals(videoView.getTag())) {
                    videoView.setTag(null);
                    setupRemoteScreenRenderer(null, userId);
                    return;
                }
            }
        }

        @Override
        public void onJoinChannel(int result, long channelId, long elapsed) {

        }

        @Override
        public void onLeaveChannel(int result) {
            NERtcEx.getInstance().release();
            finish();
        }

        @Override
        public void onUserAudioStart(long userId) {

        }

        @Override
        public void onUserAudioStop(long userId) {

        }


        @Override
        public void onDisconnect(int reason) {
            NERtcEx.getInstance().release();
            finish();
        }

    };
}
