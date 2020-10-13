package com.netease.nmc.nertcsample.audiomixing;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.netease.audiomixing.R;

public class AudioControlDialog extends DialogFragment {

    public static final String TV_MUSIC_1 = "tvMusic1";

    public static final String TV_MUSIC_2 = "tvMusic2";

    public static final String TV_EFFECT_1 = "tvEffect1";

    public static final String TV_EFFECT_2 = "tvEffect2";

    private TextView tvMusic1;

    private TextView tvMusic2;

    private TextView tvEffect1;

    private TextView tvEffect2;

    private SeekBar sbrMusicVolume;

    private SeekBar sbrEffectVolume;

    private DialogActionsCallBack callBack;

    private int musicIndex = -1;

    private int[] effectIndex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.audio_control_layout, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initParams();
        initData();
    }

    private void initView(View rootView) {
        tvMusic1 = rootView.findViewById(R.id.tv_music_1);
        tvMusic2 = rootView.findViewById(R.id.tv_music_2);
        tvEffect1 = rootView.findViewById(R.id.tv_audio_effect_1);
        tvEffect2 = rootView.findViewById(R.id.tv_audio_effect_2);
        sbrMusicVolume = rootView.findViewById(R.id.music_song_volume_control);
        sbrEffectVolume = rootView.findViewById(R.id.audio_effect_volume_control);
    }

    public void setCallBack(DialogActionsCallBack callBack) {
        this.callBack = callBack;
    }

    public void setInitData(int musicIndex, int[] effectIndex) {
        this.musicIndex = musicIndex;
        this.effectIndex = effectIndex;
    }

    private void initData() {
        //初始化数据设置view
        if (musicIndex == 0) {
            tvMusic1.setSelected(true);
        } else if (musicIndex == 1) {
            tvMusic2.setSelected(true);
        }
        if (effectIndex != null) {
            for (int i = 0; i < effectIndex.length; i++) {
                if (i == 0 && effectIndex[i] == 1) {
                    tvEffect1.setSelected(true);
                }
                if (i == 1 && effectIndex[i] == 1) {
                    tvEffect2.setSelected(true);
                }
            }
        }
        //======================伴音(背景音乐)控制=======================
        tvMusic1.setOnClickListener(v -> {
            if (!tvMusic1.isSelected()) {
                if (callBack != null) {
                    callBack.setMusicPlay(0);
                }
            }
            tvMusic2.setSelected(false);
        });

        tvMusic2.setOnClickListener(v -> {
            if (!tvMusic2.isSelected()) {
                if (callBack != null) {
                    callBack.setMusicPlay(1);
                }
            }

            tvMusic1.setSelected(false);
        });

        sbrMusicVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (callBack != null) {
                    callBack.onMusicVolumeChange(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //====================音效控制======================
        tvEffect1.setOnClickListener(v -> {
            if (!tvEffect1.isSelected()) {
                if (callBack != null) {
                    tvEffect1.setSelected(callBack.addEffect(0));
                }
            } else {
                if (callBack != null) {
                    tvEffect1.setSelected(!callBack.stopEffect(0));
                }
            }
        });

        tvEffect2.setOnClickListener(v -> {
            if (!tvEffect2.isSelected()) {
                if (callBack != null) {
                    tvEffect2.setSelected(callBack.addEffect(1));
                }
            } else {
                if (callBack != null) {
                    tvEffect2.setSelected(!callBack.stopEffect(1));
                }
            }
        });

        sbrEffectVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (callBack != null) {
                    int[] index = new int[2];
                    index[0] = tvEffect1.isSelected() ? 1 : 0;
                    index[1] = tvEffect2.isSelected() ? 1 : 0;
                    callBack.onEffectVolumeChange(progress, index);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initParams() {
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.audioControlBg)));

            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.BOTTOM;
            // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);

        }
        setCancelable(true);//设置点击外部是否消失
    }

    public void setTextViewSelected(String textView, boolean selected) {
        switch (textView) {
            case TV_MUSIC_1:
                tvMusic1.setSelected(selected);
                break;
            case TV_MUSIC_2:
                tvMusic2.setSelected(selected);
                break;
            case TV_EFFECT_1:
                tvEffect1.setSelected(selected);
                break;
            case TV_EFFECT_2:
                tvEffect2.setSelected(selected);
                break;
            default:
                break;
        }
    }


    public interface DialogActionsCallBack {
        void setMusicPlay(int index);

        void onMusicVolumeChange(int progress);

        boolean addEffect(int index);

        void onEffectVolumeChange(int progress, int[] index);

        boolean stopEffect(int index);
    }
}
