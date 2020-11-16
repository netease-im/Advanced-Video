package com.netease.nmc.nertcsample.voicechanger.voice;

import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.audio.NERtcVoiceChangerType;
import com.netease.lava.nertc.sdk.audio.NERtcVoiceEqualizationType;
import com.netease.lava.nertc.sdk.audio.NERtcVoiceReverbType;

/**
 * 变声&美声对 NERtc sdk 使用逻辑封装
 */
public final class VoiceChangerHelper {

    /**
     * 更新当前用户变声设置,枚举了十种预设类型
     * <p>
     * 详细见{@link NERtcVoiceChangerType}，默认为 {@link NERtcVoiceChangerType#kNERtcVoiceChangerOff}
     *
     * @param itemData ui层数据需通过 {@link ReMapper}接口实现真实值映射
     */
    public static void updateVoiceChanger(VoiceItemData itemData) {
        NERtcVoiceChangerType changerType = itemData.getMapValue(MAPPER_CHANGER);
        NERtcEx.getInstance().setLocalVoiceChangerPreset(changerType);
    }

    /**
     * 更新当前用户混响设置，枚举三种预设类型
     * <p>
     * 详细见 {@link NERtcVoiceReverbType}，默认为 {@link NERtcVoiceReverbType#kNERtcVoiceReverbOff}
     *
     * @param itemData ui层数据模型，需通过 {@link ReMapper}接口映射到真正的 {@link NERtcVoiceReverbType} 类型
     */
    public static void updateVoiceReverberation(VoiceItemData itemData) {
        NERtcVoiceReverbType reverberationType = itemData.getMapValue(MAPPER_REVERBERATION);
        NERtcEx.getInstance().setLocalVoiceReverbPreset(reverberationType);
    }

    /**
     * 更新当前用户均衡器设置，枚举四种预设类型
     * <p>
     * 详细见 {@link NERtcVoiceEqualizationType}，默认为 {@link NERtcVoiceEqualizationType#kNERtcVoiceEqualizationOff}
     *
     * @param itemData ui层数据模型，需通过 {@link ReMapper}接口映射到真正的 {@link NERtcVoiceEqualizationType}类型
     */
    public static void updateVoiceEq(VoiceItemData itemData) {
        NERtcVoiceEqualizationType eqType = itemData.getMapValue(MAPPER_EQ);
        NERtcEx.getInstance().setLocalVoiceEqualizationPreset(eqType);
    }

    /**
     * 更新当前用户自定义均衡器设置
     *
     * @param selfConfig int 数组类型，且数组大小固定为 10 ，单位为 db；
     *                   每个元素取值范围为 [-15,15] ，默认值为 0；
     *                   分别设置对应参数 中心频率 [31，63，125，250，500，1k，2k，4k，8k，16k] Hz；
     */
    public static void updateVoiceSelfConfigEq(int[] selfConfig) {
        NERtcEx.getInstance().setLocalVoiceEqualizations(selfConfig);
    }
//------------------------------------------------------------- 业务模型和实际 sdk 参数映射关系
    /**
     * Integer 映射 {@link NERtcVoiceChangerType} 对应关系
     */
    private static final ReMapper<Integer, NERtcVoiceChangerType> MAPPER_CHANGER = value -> {
        if (value == null) {
            return NERtcVoiceChangerType.kNERtcVoiceChangerOff;
        }
        NERtcVoiceChangerType result;
        switch (value) {
            case 0:
                result = NERtcVoiceChangerType.kNERtcVoiceChangerRobot;// 机器人
                break;
            case 1:
                result = NERtcVoiceChangerType.kNERtcVoiceChangerGaint;//巨人
                break;
            case 2:
                result = NERtcVoiceChangerType.kNERtcVoiceChangerChurchecho;//教堂回声
                break;
            case 3:
                result = NERtcVoiceChangerType.kNERtcVoiceChangerHorror;//恐怖
                break;
            case 4:
                result = NERtcVoiceChangerType.kNERtcVoiceChangerMuffled;//低沉
                break;
            case 5:
                result = NERtcVoiceChangerType.kNERtcVoiceChangerManToWoman;//男变女
                break;
            case 6:
                result = NERtcVoiceChangerType.kNERtcVoiceChangerWomanToMan;//女变男
                break;
            case 7:
                result = NERtcVoiceChangerType.kNERtcVoiceChangerManToLoli;//男变萝莉
                break;
            case 8:
                result = NERtcVoiceChangerType.kNERtcVoiceChangerWomanToLoli;//女变萝莉
                break;
            case -1:
            default:
                result = NERtcVoiceChangerType.kNERtcVoiceChangerOff;//关闭
                break;
        }
        return result;
    };

    /**
     * Integer 映射 {@link NERtcVoiceReverbType} 对应关系
     */
    private static final ReMapper<Integer, NERtcVoiceReverbType> MAPPER_REVERBERATION = value -> {
        if (value == null) {
            return NERtcVoiceReverbType.kNERtcVoiceReverbOff;
        }
        NERtcVoiceReverbType result;
        switch (value) {
            case 0:
                result = NERtcVoiceReverbType.kNERtcVoiceReverbKTV;//KTV
                break;
            case 1:
                result = NERtcVoiceReverbType.kNERtcVoiceReverbRecite;//朗诵
                break;
            case -1:
            default:
                result = NERtcVoiceReverbType.kNERtcVoiceReverbOff;//关闭
                break;
        }
        return result;
    };

    /**
     * Integer 映射 {@link NERtcVoiceEqualizationType} 对应关系
     */
    private static final ReMapper<Integer, NERtcVoiceEqualizationType> MAPPER_EQ = value -> {
        if (value == null) {
            return NERtcVoiceEqualizationType.kNERtcVoiceEqualizationOff;
        }
        NERtcVoiceEqualizationType result;
        switch (value) {
            case 0:
                result = NERtcVoiceEqualizationType.kNERtcVoiceEqualizationDeep;//低沉
                break;
            case 1:
                result = NERtcVoiceEqualizationType.kNERtcVoiceEqualizationMellow;//圆润
                break;
            case 2:
                result = NERtcVoiceEqualizationType.kNERtcVoiceEqualizationClear;//清晰
                break;
            case -1:
            default:
                result = NERtcVoiceEqualizationType.kNERtcVoiceEqualizationOff;//关闭
                break;
        }
        return result;
    };
}
