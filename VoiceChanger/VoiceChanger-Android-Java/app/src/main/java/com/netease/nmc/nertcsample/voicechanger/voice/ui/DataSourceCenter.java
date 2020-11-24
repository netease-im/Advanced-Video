package com.netease.nmc.nertcsample.voicechanger.voice.ui;

import androidx.annotation.StringRes;

import com.netease.nmc.nertcsample.voicechanger.R;
import com.netease.nmc.nertcsample.voicechanger.VoiceChangerApplication;
import com.netease.nmc.nertcsample.voicechanger.voice.VoiceItemData;

import java.util.Arrays;
import java.util.List;

/**
 * UI 层数据预设
 */
final class DataSourceCenter {
    /**
     * 均衡器自定义类型，UI 层数据值
     */
    static final int EQ_TYPE_SELF = 3;

    /**
     * 通用默认类型
     */
    static final VoiceItemData DEFAULT_ITEM = new VoiceItemData(getString(R.string.type_default), -1);

    /**
     * 变声数据配置
     */
    static final List<VoiceItemData> VOICE_CHANGER_SOURCE = Arrays.asList(
            DEFAULT_ITEM,
            new VoiceItemData(getString(R.string.changer_type_robot), 0),
            new VoiceItemData(getString(R.string.changer_type_giant), 1),
            new VoiceItemData(getString(R.string.changer_type_church_echo), 2),
            new VoiceItemData(getString(R.string.changer_type_horror), 3),
            new VoiceItemData(getString(R.string.changer_type_muffled), 4),
            new VoiceItemData(getString(R.string.changer_type_man_to_woman), 5),
            new VoiceItemData(getString(R.string.changer_type_woman_to_man), 6),
            new VoiceItemData(getString(R.string.changer_type_man_to_lolita), 7),
            new VoiceItemData(getString(R.string.changer_type_woman_to_lolita), 8)
    );

    /**
     * 混响数据
     */
    static final List<VoiceItemData> REVERBERATION_SOURCE = Arrays.asList(
            DEFAULT_ITEM,
            new VoiceItemData(getString(R.string.reverberation_type_ktv), 0),
            new VoiceItemData(getString(R.string.reverberation_type_recite), 1)
    );

    /**
     * 均衡器数据配置
     */
    static final List<VoiceItemData> EQ_SOURCE = Arrays.asList(
            DEFAULT_ITEM,
            new VoiceItemData(getString(R.string.eq_type_deep), 0),
            new VoiceItemData(getString(R.string.eq_type_mellow), 1),
            new VoiceItemData(getString(R.string.eq_type_clear), 2),
            new VoiceItemData(getString(R.string.eq_type_self), EQ_TYPE_SELF)
    );

    static final List<VoiceItemData> EQ_PARAM_CONFIG = Arrays.asList(
            new VoiceItemData("31", 0),
            new VoiceItemData("63", 0),
            new VoiceItemData("125", 0),
            new VoiceItemData("250", 0),
            new VoiceItemData("500", 0),
            new VoiceItemData("1k", 0),
            new VoiceItemData("2k", 0),
            new VoiceItemData("4k", 0),
            new VoiceItemData("8k", 0),
            new VoiceItemData("16k", 0)
    );

    /**
     * 通过 {@link #EQ_PARAM_CONFIG} 列表获取其中 value 部分返回对应数据
     *
     * @return 对应 自定义 eq 数据，大小为10
     */
    static int[] getEqParamConfig() {
        int[] result = new int[10];
        for (int index = 0; index < EQ_PARAM_CONFIG.size(); index++) {
            result[index] = EQ_PARAM_CONFIG.get(index).value;
        }
        return result;
    }

    /**
     * 缓存当前用户声音设置参数
     */
    static VoiceItemData CURRENT_CHANGER_VALUE = DEFAULT_ITEM;
    static VoiceItemData CURRENT_REVERBERATION_VALUE = DEFAULT_ITEM;
    static VoiceItemData CURRENT_EQ_VALUE = DEFAULT_ITEM;

    /**
     * 恢复至预设默认值
     */
    static void restoreValues() {
        CURRENT_CHANGER_VALUE = DEFAULT_ITEM;
        CURRENT_REVERBERATION_VALUE = DEFAULT_ITEM;
        CURRENT_EQ_VALUE = DEFAULT_ITEM;

        for (VoiceItemData itemData : EQ_PARAM_CONFIG) {
            itemData.value = 0;
        }
    }

    /**
     * 获取字符串值
     *
     * @param resId 字符串资源id
     * @return 资源id 对应的字符串value
     */
    private static String getString(@StringRes int resId) {
        return VoiceChangerApplication.getContext().getString(resId);
    }
}
