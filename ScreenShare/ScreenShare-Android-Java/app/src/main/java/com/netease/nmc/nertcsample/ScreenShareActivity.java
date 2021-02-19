package com.netease.nmc.nertcsample;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.video.NERtcEncodeConfig;
import com.netease.lava.nertc.sdk.video.NERtcScreenConfig;

public class ScreenShareActivity extends BasicActivity {

    private static final int REQUEST_CODE_SCREEN_CAPTURE = 10000;
    private static final String TAG = "ScreenShareActivity_Log";
    private Button btnVideo;
    private Button btnScreen;

    private boolean videoStarted;
    private boolean screenStarted;

    private ScreenShareServiceConnection mServiceConnection;
    private SimpleScreenShareService mScreenService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindScreenService();
    }

    @Override
    protected void onDestroy() {
        unbindScreenService();
        super.onDestroy();
    }

    @Override
    protected int getPanelLayoutId() {
        return R.layout.panel_screen_share;
    }

    @Override
    protected void initPanelViews(View panel) {
        super.initPanelViews(panel);
        btnVideo = panel.findViewById(R.id.btn_start_video);
        btnScreen = panel.findViewById(R.id.btn_screen_share);

        btnVideo.setOnClickListener(view -> toggleLocalVideo());
        btnScreen.setOnClickListener(view -> toggleScreenCapture());
    }

    private void updateBtnUI() {
        btnVideo.setText(videoStarted ? R.string.stop_video : R.string.start_video);
        btnScreen.setText(screenStarted ? R.string.stop_screen_share : R.string.start_screen_share);
    }


    protected void toggleLocalVideo() {
        videoStarted = !videoStarted;
        NERtcEx.getInstance().setupLocalVideoCanvas(videoStarted ? localVideoRenderer : null);
        NERtcEx.getInstance().enableLocalVideo(videoStarted);
        updateBtnUI();
    }

    private void toggleScreenCapture() {
        if (!screenStarted) {
            requestScreenCapture();
        } else {
            stopScreenCapture();
            screenStarted = false;
            NERtcEx.getInstance().setupLocalSubStreamVideoCanvas(null);
            updateBtnUI();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startScreenCapture(Intent mediaProjectionPermissionResultData) {

        if (mScreenService == null) {
            Toast.makeText(this, R.string.screen_capture_server_start_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        //todo 画布尺寸比例与实际屏幕共享分辨率比例不一致，可能存在截断，实际开发中调整好画布尺寸比例即可
        NERtcEx.getInstance().setupLocalSubStreamVideoCanvas(localScreenRenderer);

        // 屏幕录制回调
        final MediaProjection.Callback mediaProjectionCallback = new MediaProjection.Callback() {
            @Override
            public void onStop() {
                super.onStop();
            }
        };

        NERtcScreenConfig screenProfile = new NERtcScreenConfig();
        screenProfile.videoProfile = NERtcConstants.VideoProfile.HD1080p;
        screenProfile.frameRate = NERtcEncodeConfig.NERtcVideoFrameRate.FRAME_RATE_FPS_15;
        // 开启屏幕共享
        int result = mScreenService.startScreenCapture(screenProfile,
                mediaProjectionPermissionResultData, // 屏幕录制请求返回的Intent
                mediaProjectionCallback);

        if (result == NERtcConstants.ErrorCode.OK) {
            screenStarted = true;
            updateBtnUI();
        }
    }

    private void stopScreenCapture() {
        if (mScreenService == null) {
            Toast.makeText(this, R.string.screen_capture_server_is_null, Toast.LENGTH_SHORT).show();
            return;
        }
        // 停止屏幕共享
        mScreenService.stopScreenCapture();
    }

    private void requestScreenCapture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(createScreenCaptureIntent(this), REQUEST_CODE_SCREEN_CAPTURE);
        } else {
            Toast.makeText(this, R.string.screen_capture_min_sdk_version, Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Intent createScreenCaptureIntent(Context context) {
        MediaProjectionManager manager =
                (MediaProjectionManager) context.getSystemService(
                        Context.MEDIA_PROJECTION_SERVICE);
        return manager.createScreenCaptureIntent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SCREEN_CAPTURE) {
            if (resultCode == RESULT_OK) {
                startScreenCapture(data);
            } else {
                Toast.makeText(this, R.string.screen_capture_request_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void bindScreenService() {
        Intent intent = new Intent();
        intent.setClass(this, SimpleScreenShareService.class);
        mServiceConnection = new ScreenShareServiceConnection();
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindScreenService() {
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
    }

    private class ScreenShareServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            if (service instanceof SimpleScreenShareService.ScreenShareBinder) {
                mScreenService = ((SimpleScreenShareService.ScreenShareBinder) service).getService();
                Log.i(TAG, "onServiceConnect");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mScreenService = null;
        }
    }
}
