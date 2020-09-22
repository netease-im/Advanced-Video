package com.netease.nmc.nertcsample;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.view.View;
import android.widget.Button;

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

    private void toggleScreenCapture() {
        started = !started;
        button.setText(started ? R.string.stop_screen_share : R.string.start_screen_share);
        if (started) {
            requestScreenCapture();
        } else {
            NERtcEx.getInstance().stopScreenCapture();
            NERtcEx.getInstance().enableLocalVideo(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startScreenCapture(Intent data) {
        NERtcEx.getInstance().enableLocalVideo(false);
        NERtcEx.getInstance().startScreenCapture(ScreenProfile.HD1080p, data, new MediaProjection.Callback() {
            @Override
            public void onStop() {
                super.onStop();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void requestScreenCapture() {
        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager) getApplication().getSystemService(
                        Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_SCREEN_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SCREEN_CAPTURE) {
            if (resultCode == RESULT_OK) {
                startScreenCapture(data);
            }
        }
    }
}
