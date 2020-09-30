package com.netease.nmc.nertcsample;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.netease.lava.nertc.sdk.NERtcConstants.ScreenProfile;
import com.netease.lava.nertc.sdk.NERtcEx;

public class ScreenShareActivity extends BasicActivity {
    private static final int REQUEST_CODE_SCREEN_CAPTURE = 10000;

    private boolean started;

    private Button button;

    @Override
    protected int getPanelLayoutId() {
        return R.layout.panel_screen_share;
    }

    @Override
    protected void initPanelViews(View panel) {
        super.initPanelViews(panel);

        button = panel.findViewById(R.id.btn_screen_share);

        button.setOnClickListener(view -> toggleScreenCapture());
    }

    private void updateUI() {
        button.setText(started ? R.string.stop_screen_share : R.string.start_screen_share);
    }

    private void toggleScreenCapture() {
        if (!started) {
            requestScreenCapture();
        } else {
            stopScreenCapture();
            started = false;
            updateUI();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startScreenCapture(Intent mediaProjectionPermissionResultData) {
        // 屏幕录制回调
        final MediaProjection.Callback mediaProjectionCallback = new MediaProjection.Callback() {
            @Override
            public void onStop() {
                super.onStop();
            }
        };
        // 选择屏幕共享清晰度
        int screenProfile = ScreenProfile.HD1080p;
        // 开启屏幕共享
        int result = NERtcEx.getInstance().startScreenCapture(screenProfile,
                mediaProjectionPermissionResultData, // 屏幕录制请求返回的Intent
                mediaProjectionCallback);
        if (result == 0) {
            started = true;
            updateUI();
        }
    }

    private void stopScreenCapture() {
        // 停止屏幕共享
        NERtcEx.getInstance().stopScreenCapture();
        // 开启本地视频采集以及发送
        NERtcEx.getInstance().enableLocalVideo(true);
    }

    private void requestScreenCapture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(createScreenCaptureIntent(), REQUEST_CODE_SCREEN_CAPTURE);
        } else {
            Toast.makeText(this, R.string.screen_capture_min_sdk_version, Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Intent createScreenCaptureIntent() {
        MediaProjectionManager manager =
                (MediaProjectionManager) getApplication().getSystemService(
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
}
