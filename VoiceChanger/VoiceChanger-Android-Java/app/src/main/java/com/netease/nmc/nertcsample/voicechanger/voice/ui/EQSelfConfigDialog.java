package com.netease.nmc.nertcsample.voicechanger.voice.ui;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netease.nmc.nertcsample.voicechanger.R;
import com.netease.nmc.nertcsample.voicechanger.voice.VoiceChangerHelper;

/**
 * 自定义据衡器配置 dialog
 */
class EQSelfConfigDialog extends VoiceConfigDialog {
    EQSelfConfigDialog(@NonNull Activity activity) {
        super(activity);
    }

    @Override
    protected int contentLayoutId() {
        return R.layout.dialog_eq_self_config;
    }

    @Override
    protected void renderRootView(View rootView) {
        if (rootView == null) {
            return;
        }
        RecyclerView rvSelfEqConfig = rootView.findViewById(R.id.rv_self_eq_config);

        rvSelfEqConfig.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        EQSelfConfigAdapter adapter = new EQSelfConfigAdapter(getContext(), DataSourceCenter.EQ_PARAM_CONFIG);
        // 当任何参数被拖动后会进行回调，更新自定义配置，由于更新自定义均衡器接口参数为数据类型，每次需要更新全部自定义参数，数据大小固定为10
        // 详细可参考 VoiceChangerHelper.updateVoiceSelfConfigEq 说明
        adapter.setAdjustNotification(() -> VoiceChangerHelper.updateVoiceSelfConfigEq(DataSourceCenter.getEqParamConfig()));
        rvSelfEqConfig.setAdapter(adapter);
    }
}
