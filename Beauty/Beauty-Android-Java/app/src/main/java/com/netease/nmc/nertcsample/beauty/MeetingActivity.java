package com.netease.nmc.nertcsample.beauty;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.faceunity.FURenderer;
import com.netease.lava.nertc.sdk.NERtcCallback;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.NERtcParameters;
import com.netease.lava.nertc.sdk.video.NERtcRemoteVideoStreamType;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;
import com.netease.nertcbeautysample.R;
import com.netease.nmc.nertcsample.beauty.config.NativeConfig;

import java.util.Random;

public class MeetingActivity  extends AppCompatActivity implements NERtcCallback,
        View.OnClickListener,
        FURenderer.OnFUDebugListener,
        FURenderer.OnTrackingStatusChangedListener{

    private static final String TAG = "MeetingActivity";
    private static final String EXTRA_ROOM_ID = "extra_room_id";

    private boolean enableLocalVideo = true;
    private boolean enableLocalAudio = true;
    private boolean joinedChannel = false;
    private boolean localLarge = false;//local VideoView 是否放大，默认false
    private boolean openFilter = false;//美颜功能，默认关闭

    private NERtcVideoView smallVideoView;
    private NERtcVideoView bigVideoView;
    private TextView waitHintTv;
    private TextView tvLeave;
    private TextView tvOpenBeauty;
    private ImageView cameraFlipImg;
    private View localUserBgV;

    private long remoteUid;//远端用户id

    private FURenderer mFuRender;//美颜效果

    private int cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;//摄像头FACE_BACK = 0, FACE_FRONT = 1

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
        mFuRender = new FURenderer
                .Builder(this)
                .maxFaces(1)
                .inputImageOrientation(getCameraOrientation(Camera.CameraInfo.CAMERA_FACING_FRONT))
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .build();
        mFuRender.onSurfaceCreated();
        mFuRender.setBeautificationOn(true);
        setupNERtc();
        String roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
        long userId = generateRandomUserID();
        joinChannel(userId, roomId);
    }

    private int getCameraOrientation(int cameraFacing) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraId = -1;
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == cameraFacing) {
                cameraId = i;
                break;
            }
        }
        if (cameraId < 0) {
            // no front camera, regard it as back camera
            return 90;
        } else {
            return info.orientation;
        }
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
        if(!localLarge) {
            NERtcEx.getInstance().setupLocalVideoCanvas(smallVideoView);
        }else {
            NERtcEx.getInstance().setupLocalVideoCanvas(bigVideoView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NERtcEx.getInstance().release();
        if(mFuRender != null){
            mFuRender.onSurfaceDestroyed();
        }
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void initViews() {
        smallVideoView = findViewById(R.id.vv_local_user);
        bigVideoView = findViewById(R.id.vv_remote_user);
        waitHintTv = findViewById(R.id.tv_wait_hint);
        cameraFlipImg = findViewById(R.id.img_camera_flip);
        localUserBgV = findViewById(R.id.v_local_user_bg);
        tvLeave = findViewById(R.id.tv_leave);
        tvOpenBeauty = findViewById(R.id.tv_open_beauty);

        smallVideoView.setVisibility(View.INVISIBLE);
        tvLeave.setOnClickListener(this);
        tvOpenBeauty.setOnClickListener(this);
        cameraFlipImg.setOnClickListener(this);
        smallVideoView.setOnClickListener(this);
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
        //设置视频采集数据回调，用于美颜等操作
        NERtcEx.getInstance().setVideoCallback(neRtcVideoFrame -> {
            if(openFilter) {
                //此处可自定义第三方的美颜实现
                neRtcVideoFrame.textureId = mFuRender.onDrawFrame(neRtcVideoFrame.data,neRtcVideoFrame.textureId,
                        neRtcVideoFrame.width,neRtcVideoFrame.height);
            }
            return openFilter;
        },true);
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
        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true);
        bigVideoView.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_FIT);
        if(!localLarge) {
            NERtcEx.getInstance().setupRemoteVideoCanvas(bigVideoView, uid);
        }else {
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
        smallVideoView.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
        localUserBgV.setBackgroundColor(getResources().getColor(enable ? R.color.white : R.color.black));
    }

    /**
     * 切换大小视图
     */
    private void switchVideoView(){
        if(remoteUid == 0){
            return;
        }
        if(!localLarge){
            NERtcEx.getInstance().setupLocalVideoCanvas(bigVideoView);
            NERtcEx.getInstance().setupRemoteVideoCanvas(smallVideoView,remoteUid);
        }else {
            NERtcEx.getInstance().setupLocalVideoCanvas(smallVideoView);
            NERtcEx.getInstance().setupRemoteVideoCanvas(bigVideoView,remoteUid);
        }
        localLarge = !localLarge;
    }

    /**
     * 切换美颜开关
     */
    private void switchFilter(){
        openFilter = !openFilter;
        if(openFilter){
            tvOpenBeauty.setText(R.string.close_beauty);
        } else {
            tvOpenBeauty.setText(R.string.open_beauty);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_leave:
                exit();
                break;
            case R.id.tv_open_beauty:
                switchFilter();
                break;
            case R.id.img_camera_flip:
                NERtcEx.getInstance().switchCamera();
                if(cameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                    cameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
                }else {
                    cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }
                mFuRender.onCameraChange(cameraFacing,getCameraOrientation(cameraFacing));
                break;
            case R.id.vv_local_user:
                switchVideoView();
                break;
            default:
                break;
        }
    }

    @Override
    public void onFpsChange(double fps, double renderTime) {

    }

    @Override
    public void onTrackStatusChanged(int type, int status) {

    }
}
