package com.netease.nmc.nertcsample.voicechanger.voice.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netease.nmc.nertcsample.voicechanger.R;
import com.netease.nmc.nertcsample.voicechanger.voice.VoiceChangerHelper;
import com.netease.nmc.nertcsample.voicechanger.voice.VoiceItemData;

import java.util.List;

/**
 * 变声配置主面板弹窗
 */
public class VoiceConfigDialog extends Dialog {
    protected Activity activity;
    protected View rootView;

    public VoiceConfigDialog(@NonNull Activity activity) {
        super(activity, R.style.BottomDialogTheme);
        this.activity = activity;
        rootView = LayoutInflater.from(getContext()).inflate(contentLayoutId(), null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window window = getWindow();
        if (window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(wlp);
        }

        setContentView(rootView);
        setCanceledOnTouchOutside(true);
    }

    protected @LayoutRes
    int contentLayoutId() {
        return R.layout.dialog_voice_config;
    }

    /**
     * 页面渲染
     */
    protected void renderRootView(View rootView) {
        if (rootView == null) {
            return;
        }
        renderChanger(rootView.findViewById(R.id.rv_voice_changer));
        renderReverberation(rootView.findViewById(R.id.rv_reverberation));
        renderEq(rootView.findViewById(R.id.rv_eq));
    }

    @Override
    public void show() {
        renderRootView(rootView);
        super.show();
    }

    /**
     * 退出房间需要清空声音设置参数，保持预设
     */
    public void release() {
        DataSourceCenter.restoreValues();
    }

    /**
     * 渲染 变声部分布局
     *
     * @param changerView 变声 RecyclerView
     */
    private void renderChanger(RecyclerView changerView) {
        if (changerView == null) {
            return;
        }
        changerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        // 从DataSourceCenter 获取变声预置数据源
        List<VoiceItemData> source = DataSourceCenter.VOICE_CHANGER_SOURCE;
        VoiceConfigAdapter adapter = new VoiceConfigAdapter(getContext(), source, DataSourceCenter.CURRENT_CHANGER_VALUE);
        VoiceChangerHelper.updateVoiceChanger(DataSourceCenter.CURRENT_CHANGER_VALUE);
        adapter.setOnItemClickListener(itemData -> {
                    DataSourceCenter.CURRENT_CHANGER_VALUE = itemData;
                    VoiceChangerHelper.updateVoiceChanger(itemData);
                }
        );
        changerView.setAdapter(adapter);
    }

    /**
     * 渲染 混响部分布局
     *
     * @param reverberationView 混响 RecyclerView
     */
    private void renderReverberation(RecyclerView reverberationView) {
        if (reverberationView == null) {
            return;
        }
        reverberationView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        // 从DataSourceCenter 获取混响预置数据源
        List<VoiceItemData> source = DataSourceCenter.REVERBERATION_SOURCE;
        VoiceConfigAdapter adapter = new VoiceConfigAdapter(getContext(), source, DataSourceCenter.CURRENT_REVERBERATION_VALUE);
        VoiceChangerHelper.updateVoiceReverberation(DataSourceCenter.CURRENT_REVERBERATION_VALUE);
        adapter.setOnItemClickListener(itemData -> {
            DataSourceCenter.CURRENT_REVERBERATION_VALUE = itemData;
            VoiceChangerHelper.updateVoiceReverberation(itemData);

        });
        reverberationView.setAdapter(adapter);
    }

    /**
     * 渲染 均衡器部分布局
     *
     * @param eqView 均衡器 RecyclerView
     */
    private void renderEq(RecyclerView eqView) {
        if (eqView == null) {
            return;
        }
        eqView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        // 从DataSourceCenter 获取均衡器预置数据源
        List<VoiceItemData> source = DataSourceCenter.EQ_SOURCE;
        VoiceConfigAdapter adapter = new VoiceConfigAdapter(getContext(), source, DataSourceCenter.CURRENT_EQ_VALUE);
        eqView.setAdapter(adapter);
        // 设置点击回调
        adapter.setOnItemClickListener(itemData -> {
            DataSourceCenter.CURRENT_EQ_VALUE = itemData;
            // 当前点击如果为 DataSourceCenter.EQ_TYPE_SELF 则需要展示均衡器自定义面板，并更新当前自定义均衡器参数
            if (itemData.value == DataSourceCenter.EQ_TYPE_SELF) {
                VoiceChangerHelper.updateVoiceSelfConfigEq(DataSourceCenter.getEqParamConfig());
                new EQSelfConfigDialog(activity).show();
                return;
            }
            // 如果当前点击项非 自定义据衡器配置项则更新对应均衡器预设值
            VoiceChangerHelper.updateVoiceEq(itemData);
        });
    }
}
