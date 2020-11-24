package com.netease.nmc.nertcsample.voicechanger.voice;

public interface ReMapper<V, R> {
    /**
     * 用于映射 从 类型 V 到 R 的转换
     *
     * @param value 实际参数 V 类型
     * @return R 类型具体结果
     */
    R map(V value);
}
