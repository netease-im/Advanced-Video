package com.netease.audiomixing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.netease.lava.nertc.sdk.NERtc;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViews();

        requestPermissionsIfNeeded();
    }

    private void requestPermissionsIfNeeded() {
        final List<String> missedPermissions = NERtc.checkPermission(this);
        if (missedPermissions.size() > 0) {
            ActivityCompat.requestPermissions(this, missedPermissions.toArray(new String[0]), REQUEST_CODE_PERMISSION);
        }
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final EditText editTextUserId = findViewById(R.id.et_user_id);
        editTextUserId.setText(generateRandomUserID());
        final EditText editTextRoomId = findViewById(R.id.et_room_id);
        findViewById(R.id.btn_join).setOnClickListener(v -> {
            String userIdText = editTextUserId.getText() != null ? editTextUserId.getText().toString() : "";
            if (TextUtils.isEmpty(userIdText)) {
                Toast.makeText(this, R.string.please_input_user_id, Toast.LENGTH_SHORT).show();
                return;
            }
            String roomId = editTextRoomId.getText() != null ? editTextRoomId.getText().toString() : "";
            if (TextUtils.isEmpty(roomId)) {
                Toast.makeText(this, R.string.please_input_room_id, Toast.LENGTH_SHORT).show();
                return;
            }
            long userId;
            try {
                userId = Long.parseLong(userIdText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.invalid_user_id, Toast.LENGTH_SHORT).show();
                return;
            }

            DialogRoomActivity.startDialog(MainActivity.this,roomId,userId);

            hideSoftKeyboard();
        });
    }

    private void hideSoftKeyboard() {
        if (getCurrentFocus() == null) return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private String generateRandomUserID() {
        return String.valueOf(new Random().nextInt(100000));
    }
}