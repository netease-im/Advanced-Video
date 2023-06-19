package com.netease.nmc.nertcsample.beauty.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.netease.nertcbeautysample.R;

public class NEBeautyRadioGroup extends RadioGroup {
    private static final float DEFAULT_ALPHA = 1.0f;
    private static final float SELECTED_ALPHA = 0.8f;
    private int mLastCheckedId = -1;
    private OnCheckedChangeListener mOnCheckedChangeListener;

    public NEBeautyRadioGroup(Context context) {
        super(context);
        init();
    }

    public NEBeautyRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        super.setOnCheckedChangeListener(new CheckStateListener());
    }

    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    private class CheckStateListener implements OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            if (mLastCheckedId != -1) {
                RadioButton lastCheckBtn = radioGroup.findViewById(mLastCheckedId);
                lastCheckBtn.setTextColor(Color.WHITE);
                lastCheckBtn.setAlpha(DEFAULT_ALPHA);
            }
            mLastCheckedId = checkedId;
            RadioButton currentBtn = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
            if (currentBtn != null) {
                currentBtn.setTextColor(getResources().getColor(R.color.colorAccent));
                currentBtn.setAlpha(SELECTED_ALPHA);
            }
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(radioGroup, checkedId);
            }
        }
    }
}
