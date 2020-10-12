package com.netease.nmc.nertcsample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.video.NERtcVideoFrame;
import com.netease.nmc.nertcsample.externalvideo.ExternalTextureVideoSource;
import com.netease.nmc.nertcsample.externalvideo.ExternalVideoSource;
import com.netease.nmc.nertcsample.externalvideo.FileUtil;

public class ExternalVideoActivity extends BasicActivity {
    private static final int REQUEST_CODE_CHOOSE_VIDEO_FILE = 10000;
    private static final int REQUEST_CODE_REQUEST_PERMISSION = 10001;

    private boolean started;

    private String videoPath;

    private ExternalVideoSource externalVideoSource;

    private Button button;

    @Override
    protected int getPanelLayoutId() {
        return R.layout.panel_external_video;
    }

    @Override
    protected void initPanelViews(View panel) {
        super.initPanelViews(panel);

        button = panel.findViewById(R.id.btn_external_video);
        button.setOnClickListener(view -> toggleExternalVideo());
        panel.findViewById(R.id.btn_choose_video_file).setOnClickListener(view -> chooseVideoFile());
    }

    @Override
    protected void leaveChannel() {
        if (started) {
            toggleExternalVideo();
        }

        super.leaveChannel();
    }

    private void toggleExternalVideo() {
        if (!started && TextUtils.isEmpty(videoPath)) {
            Toast.makeText(this, R.string.choose_video_file, Toast.LENGTH_SHORT).show();
            chooseVideoFile();
            return;
        }
        if (!started) {
            // 创建外部视频源，输入文件路径和视频帧处理回调
            externalVideoSource = ExternalTextureVideoSource.create(videoPath, this::pushExternalVideoFrame);
            if (externalVideoSource == null) {
                return;
            }
            // 设置为外部视频源
            setExternalVideoSource(true);
            // 开始发送视频数据
            if (!externalVideoSource.start()) {
                return;
            }
        } else {
            // 停止发送视频数据
            if (externalVideoSource != null) {
                externalVideoSource.stop();
                externalVideoSource = null;
            }
            // 取消外部视频源
            setExternalVideoSource(false);
        }
        started = !started;
        button.setText(started ? R.string.stop_external_video : R.string.start_external_video);
    }

    private void setExternalVideoSource(boolean enable) {
        // 关闭本地视频采集以及发送
        NERtcEx.getInstance().enableLocalVideo(false);
        // 使用外部视频源
        NERtcEx.getInstance().setExternalVideoSource(enable);
        // 开启本地视频采集以及发送
        NERtcEx.getInstance().enableLocalVideo(true);
    }

    private void pushExternalVideoFrame(NERtcVideoFrame videoFrame) {
        // 推送外部视频帧
        NERtcEx.getInstance().pushExternalVideoFrame(videoFrame);
    }

    private void chooseVideoFile() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            openFileChoose(REQUEST_CODE_CHOOSE_VIDEO_FILE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_CODE_REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseVideoFile();
            }
        }
    }

    private void openFileChoose(int requestCode){
        try {
            Intent intent = new Intent();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            }else {
                intent.setAction(Intent.ACTION_GET_CONTENT);
            }
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, requestCode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHOOSE_VIDEO_FILE) {
            if (resultCode == RESULT_OK) {
                videoPath = FileUtil.getPath(getApplicationContext(), data.getData());
            }
        }
    }
}
