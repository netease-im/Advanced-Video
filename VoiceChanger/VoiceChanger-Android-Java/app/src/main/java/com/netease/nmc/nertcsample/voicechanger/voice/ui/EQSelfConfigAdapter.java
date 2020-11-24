package com.netease.nmc.nertcsample.voicechanger.voice.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.netease.nmc.nertcsample.voicechanger.R;
import com.netease.nmc.nertcsample.voicechanger.voice.VoiceItemData;

import java.util.List;

/**
 * 自定义均衡器配置adapter
 */
class EQSelfConfigAdapter extends VoiceBaseAdapter<VoiceItemData> {
    private static final int LIMIT_MAX = 15;
    private static final int LIMIT_MIN = -15;
    private static final int LIMIT_STEP = 30;

    private Runnable adjustNotification;

    EQSelfConfigAdapter(Context context, List<VoiceItemData> dataSource) {
        super(context, dataSource);
    }

    void setAdjustNotification(Runnable adjustNotification) {
        this.adjustNotification = adjustNotification;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_eq_self_config, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        VoiceItemData itemData = getItem(position);
        if (itemData == null) {
            return;
        }

        TextView tvName = holder.getViewById(R.id.tv_param_name);
        tvName.setText(itemData.name);

        SeekBar sbValue = holder.getViewById(R.id.sb_param_value);
        sbValue.setMax(LIMIT_STEP);
        // 由于ui设置范围为[0,30]实际参数为[-15,15]需要通过计算获得偏移结果
        sbValue.setProgress(adjustToUiValue(itemData.value));
        sbValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                // 由于ui设置范围为[0,30]实际参数为[-15,15]需要通过计算获得真实sdk参数结果
                itemData.value = adjustToRealValue(progress);
                // 调整后需要通知外部整体设置 EQ 的自定义设置
                if (adjustNotification != null) {
                    adjustNotification.run();
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

    /**
     * UI SeekBar 值映射为 sdk 需要的真实值
     *
     * @param value ui SeekBar 当前值
     * @return 真实sdk所需值
     */
    private int adjustToRealValue(int value) {
        int result = value - LIMIT_MAX;

        if (result < LIMIT_MIN) {
            return LIMIT_MIN;
        }
        return Math.min(result, LIMIT_MAX);
    }

    /**
     * sdk 真实值映射为 UI SeekBar 值
     *
     * @param value 真实 sdk 参数值
     * @return UI SeekBar 值
     */
    private int adjustToUiValue(int value) {
        return value + LIMIT_MAX;
    }
}
