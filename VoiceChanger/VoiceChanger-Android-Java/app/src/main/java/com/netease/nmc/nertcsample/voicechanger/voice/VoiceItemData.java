package com.netease.nmc.nertcsample.voicechanger.voice;

/**
 * UI 层数据模型，用于展示使用
 */
public class VoiceItemData {
    /**
     * 参数名称
     */
    public String name;
    /**
     * 参数具体值
     */
    public int value;

    public VoiceItemData(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * ui 层数据通过 value 映射到真实sdk参数获取预设类型
     *
     * @param mapper 映射关系
     * @param <T>    待转换类型
     * @return 具体 参数值
     */
    public <T> T getMapValue(ReMapper<Integer, T> mapper) {
        return mapper.map(value);
    }

    @Override
    public String toString() {
        return "VoiceItemData{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
