package com.netease.nmc.nertcsample.voicechanger.voice.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import com.netease.nmc.nertcsample.voicechanger.R;
import com.netease.nmc.nertcsample.voicechanger.voice.VoiceItemData;

import java.util.List;

/**
 * 变声配置数据 adapter
 */
class VoiceConfigAdapter extends VoiceBaseAdapter<VoiceItemData> {

    VoiceConfigAdapter(Context context, List<VoiceItemData> dataSource, VoiceItemData focusedData) {
        super(context, dataSource, focusedData);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_voice_config, parent, false));
    }

    @Override
    protected View clickableView(Holder holder) {
        return holder.getViewById(R.id.cb_item);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        super.onBindViewHolder(holder, position);
        VoiceItemData itemData = getItem(position);
        if (itemData == null) {
            return;
        }
        CheckBox checkBox = holder.getViewById(R.id.cb_item);
        checkBox.setText(itemData.name);
    }

    @Override
    protected void onFocus(Holder holder, VoiceItemData itemData) {
        CheckBox checkBox = holder.getViewById(R.id.cb_item);
        checkBox.setChecked(true);
    }

    @Override
    protected void onUnFocus(Holder holder, VoiceItemData itemData) {
        CheckBox checkBox = holder.getViewById(R.id.cb_item);
        checkBox.setChecked(false);
    }
}
