package com.netease.nmc.nertcsample.beauty.module;

import com.netease.nertcbeautysample.R;
import com.netease.nmc.nertcsample.beauty.NEConstants;

import java.util.HashMap;

public enum NEFilterEnum {
    ORIGIN(R.id.rb_filter_origin, "origin", NEConstants.DEFAULT_FILTER_LEVEL),
    F1(R.id.rb_filter_f1, "filter_style_f1", NEConstants.DEFAULT_FILTER_LEVEL),
    F2(R.id.rb_filter_f2, "filter_style_f2", NEConstants.DEFAULT_FILTER_LEVEL),
    F3(R.id.rb_filter_f3, "filter_style_f3", NEConstants.DEFAULT_FILTER_LEVEL),
    F4(R.id.rb_filter_f4, "filter_style_f4", NEConstants.DEFAULT_FILTER_LEVEL),
    FN1(R.id.rb_filter_fn1, "filter_style_FN1", NEConstants.DEFAULT_FILTER_LEVEL),
    FN2(R.id.rb_filter_fn2, "filter_style_FN2", NEConstants.DEFAULT_FILTER_LEVEL),
    N2(R.id.rb_filter_n2, "filter_style_n2", NEConstants.DEFAULT_FILTER_LEVEL),
    P1(R.id.rb_filter_p1, "filter_style_个性1", NEConstants.DEFAULT_FILTER_LEVEL),
    P2(R.id.rb_filter_p2, "filter_style_个性2", NEConstants.DEFAULT_FILTER_LEVEL),
    P3(R.id.rb_filter_p3, "filter_style_个性3", NEConstants.DEFAULT_FILTER_LEVEL),
    P4(R.id.rb_filter_p4, "filter_style_个性4", NEConstants.DEFAULT_FILTER_LEVEL),
    P5(R.id.rb_filter_p5, "filter_style_个性5", NEConstants.DEFAULT_FILTER_LEVEL),
    P6(R.id.rb_filter_p6, "filter_style_个性6", NEConstants.DEFAULT_FILTER_LEVEL),
    SUMMER(R.id.rb_filter_summer, "filter_style_夏日", NEConstants.DEFAULT_FILTER_LEVEL),
    WINTER(R.id.rb_filter_winter, "filter_style_寒冬", NEConstants.DEFAULT_FILTER_LEVEL),
    SHIMMER(R.id.rb_filter_shimmer, "filter_style_微光", NEConstants.DEFAULT_FILTER_LEVEL),
    FEMININE(R.id.rb_filter_feminine, "filter_style_柔美", NEConstants.DEFAULT_FILTER_LEVEL),
    FOREST(R.id.rb_filter_forest, "filter_style_森林", NEConstants.DEFAULT_FILTER_LEVEL),
    GLITTER(R.id.rb_filter_waterl, "filter_style_水光", NEConstants.DEFAULT_FILTER_LEVEL),
    SODA(R.id.rb_filter_soda, "filter_style_汽水", NEConstants.DEFAULT_FILTER_LEVEL),
    REF1(R.id.rb_filter_refresh1, "filter_style_清新1", NEConstants.DEFAULT_FILTER_LEVEL),
    REF2(R.id.rb_filter_refresh2, "filter_style_清新2", NEConstants.DEFAULT_FILTER_LEVEL),
    REF3(R.id.rb_filter_refresh3, "filter_style_清新3", NEConstants.DEFAULT_FILTER_LEVEL),
    REF4(R.id.rb_filter_refresh4, "filter_style_清新4", NEConstants.DEFAULT_FILTER_LEVEL),
    REF5(R.id.rb_filter_refresh5, "filter_style_清新5", NEConstants.DEFAULT_FILTER_LEVEL),
    REF6(R.id.rb_filter_refresh6, "filter_style_清新6", NEConstants.DEFAULT_FILTER_LEVEL),
    FAIR(R.id.rb_filter_fair, "filter_style_白皙", NEConstants.DEFAULT_FILTER_LEVEL),
    FAIR1(R.id.rb_filter_fair1, "filter_style_白皙1", NEConstants.DEFAULT_FILTER_LEVEL),
    FAIR2(R.id.rb_filter_fair2, "filter_style_白皙2", NEConstants.DEFAULT_FILTER_LEVEL),
    FAIR3(R.id.rb_filter_fair3, "filter_style_白皙3", NEConstants.DEFAULT_FILTER_LEVEL),
    FAIR4(R.id.rb_filter_fair4, "filter_style_白皙4", NEConstants.DEFAULT_FILTER_LEVEL),
    FAIR5(R.id.rb_filter_fair5, "filter_style_白皙5", NEConstants.DEFAULT_FILTER_LEVEL),
    FAIR6(R.id.rb_filter_fair6, "filter_style_白皙6", NEConstants.DEFAULT_FILTER_LEVEL),
    AUTUMN(R.id.rb_filter_autumn, "filter_style_秋分", NEConstants.DEFAULT_FILTER_LEVEL),
    NA(R.id.rb_filter_nature, "filter_style_自然", NEConstants.DEFAULT_FILTER_LEVEL),
    NA1(R.id.rb_filter_nature1, "filter_style_自然1", NEConstants.DEFAULT_FILTER_LEVEL),
    NA2(R.id.rb_filter_nature2, "filter_style_自然2", NEConstants.DEFAULT_FILTER_LEVEL),
    NA3(R.id.rb_filter_nature3, "filter_style_自然3", NEConstants.DEFAULT_FILTER_LEVEL),
    NA4(R.id.rb_filter_nature4, "filter_style_自然4", NEConstants.DEFAULT_FILTER_LEVEL),
    NA5(R.id.rb_filter_nature5, "filter_style_自然5", NEConstants.DEFAULT_FILTER_LEVEL),
    NA6(R.id.rb_filter_nature6, "filter_style_自然6", NEConstants.DEFAULT_FILTER_LEVEL),
    STAMEN(R.id.rb_filter_stamen, "filter_style_花蕊", NEConstants.DEFAULT_FILTER_LEVEL),
    TEX1(R.id.rb_filter_texture1, "filter_style_质感1", NEConstants.DEFAULT_FILTER_LEVEL),
    TEX2(R.id.rb_filter_texture2, "filter_style_质感2", NEConstants.DEFAULT_FILTER_LEVEL),
    TEX3(R.id.rb_filter_texture3, "filter_style_质感3", NEConstants.DEFAULT_FILTER_LEVEL),
    TEX4(R.id.rb_filter_texture4, "filter_style_质感4", NEConstants.DEFAULT_FILTER_LEVEL),
    TEX5(R.id.rb_filter_texture5, "filter_style_质感5", NEConstants.DEFAULT_FILTER_LEVEL),
    TEX6(R.id.rb_filter_texture6, "filter_style_质感6", NEConstants.DEFAULT_FILTER_LEVEL);

    private int resId;
    private String name;
    private float level;

    NEFilterEnum(int resId, String name, float level) {
        this.resId = resId;
        this.name = name;
        this.level = level;
    }

    public int getResId() {
        return resId;
    }

    public String getName() {
        return name;
    }

    public float getLevel() {
        return level;
    }

    public static HashMap<Integer, NEFilter> getFilters() {
        NEFilterEnum[] filterEnums = NEFilterEnum.values();
        HashMap<Integer, NEFilter> filters = new HashMap<>();
        for (NEFilterEnum filter : filterEnums) {
            filters.put(filter.resId, new NEFilter(filter.resId, filter.name, filter.level));
        }

        return filters;
    }
}
