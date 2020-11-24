package com.netease.nmc.nertcsample.voicechanger;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.netease.lava.nertc.sdk.NERtc;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private TextView hintTv;
    private EditText roomIdEt;
    private ImageView clearInputImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        requestPermissionsIfNeeded();

    }

    /**
     * 此处申请必要权限如下
     * android.permission.RECORD_AUDIO
     * android.permission.CAMERA
     * android.permission.WRITE_EXTERNAL_STORAGE
     */
    private void requestPermissionsIfNeeded() {
        final List<String> missedPermissions = NERtc.checkPermission(this);
        if (missedPermissions.size() > 0) {
            ActivityCompat.requestPermissions(this, missedPermissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    private void initViews() {
        hintTv = findViewById(R.id.tv_hint);
        roomIdEt = findViewById(R.id.et_room_id);
        clearInputImg = findViewById(R.id.img_clear_input);

        // 输入框为空时才显示清除内容的图标
        roomIdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                clearInputImg.setVisibility(s == null || s.length() <= 0 ? View.GONE : View.VISIBLE);
            }
        });

        clearInputImg.setOnClickListener(v -> roomIdEt.setText(""));

        Button joinBtn = findViewById(R.id.btn_join);
        joinBtn.setOnClickListener(v -> {
            Editable roomIdEdit = roomIdEt.getText();
            if (roomIdEdit == null || roomIdEt.length() <= 0) {
                hintTv.setVisibility(View.VISIBLE);
                return;
            }
            hintTv.setVisibility(View.GONE);
            MeetingActivity.startActivity(this, roomIdEt.getText().toString());
            hideSoftKeyboard();
        });
    }

    private void hideSoftKeyboard() {
        if (getCurrentFocus() == null) return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}