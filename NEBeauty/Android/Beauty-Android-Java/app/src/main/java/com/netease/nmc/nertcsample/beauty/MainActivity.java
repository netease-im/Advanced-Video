package com.netease.nmc.nertcsample.beauty;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.netease.lava.nertc.sdk.NERtc;
import com.netease.nertcbeautysample.R;
import com.netease.nmc.nertcsample.beauty.module.NEAssetsEnum;
import com.netease.nmc.nertcsample.beauty.utils.AssetUtils;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private TextView hintTv;
    private EditText roomIdEt;
    private ImageView clearInputImg;
    private Button joinBtn;
    private boolean isAssetLoadComplete = false;
    private String extFilesDirPath;
    private BeauyAssetsLoaderTask beauyAssetsLoaderTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        extFilesDirPath = getExternalFilesDir(null).getAbsolutePath();
        initViews();
        requestPermissionsIfNeeded();
        beauyAssetsLoaderTask = new BeauyAssetsLoaderTask();
        beauyAssetsLoaderTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (beauyAssetsLoaderTask != null) {
            beauyAssetsLoaderTask.cancel(true);
            beauyAssetsLoaderTask = null;
        }
    }

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
        joinBtn = findViewById(R.id.btn_join);

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

        clearInputImg.setOnClickListener(v -> {
            roomIdEt.setText("");
        });

        joinBtn.setOnClickListener(v -> {
            Editable roomIdEdit = roomIdEt.getText();
            if (roomIdEdit == null || roomIdEt.length() <= 0) {
                hintTv.setVisibility(View.VISIBLE);
                return;
            }

            if (!isAssetLoadComplete) {
                showToast("assets not load completely");
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

    private class BeauyAssetsLoaderTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            int ret = 0;
            for (NEAssetsEnum type : NEAssetsEnum.values()) {
                ret = AssetUtils.copyAssetRecursive(getAssets(), type.getAssetsPath(), getBeautyAssetPath(type), false);
                if (ret != 0) break;
                if (isCancelled()) break;
            }
            return ret;
        }

        @Override
        protected void onPostExecute(Integer ret) {
            super.onPostExecute(ret);
            if (ret == 0) {
                isAssetLoadComplete = true;
            } else {
                showToast("Load assets failed, ret: " + ret);
                isAssetLoadComplete = false;
            }
        }
    }

    /**
     * 生成滤镜和美妆模板资源文件的路径，资源文件在App启动后会拷贝到的App的外部存储路径
     * @param type @see NEAssetsEnum 对应assets目录下的美颜，滤镜或者美妆资源目录
     * @return 美颜，滤镜或者美妆的App外部存储路径
     */
    private String getBeautyAssetPath(NEAssetsEnum type) {
        String separator = File.separator;
        return String.format(Locale.getDefault(), "%s%s%s", extFilesDirPath, separator, type.getAssetsPath());
    }

    private void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

}