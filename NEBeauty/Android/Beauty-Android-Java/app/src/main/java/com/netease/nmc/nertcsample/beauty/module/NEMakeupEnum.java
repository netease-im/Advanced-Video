package com.netease.nmc.nertcsample.beauty.module;

import com.netease.nertcbeautysample.R;

import java.util.HashMap;

public enum NEMakeupEnum {
    EYESHADOW(R.id.rb_makeup_eyeshadow, "makeup");
    private int resId;
    private String name;

    NEMakeupEnum(int resId, String name) {
        this.resId = resId;
        this.name = name;
    }

   public static HashMap<Integer, NEMakeup> getMakeups() {
        NEMakeupEnum[] makeupEnums = NEMakeupEnum.values();
        HashMap<Integer, NEMakeup> makeups = new HashMap<>();
        for (NEMakeupEnum makeup : makeupEnums) {
            makeups.put(makeup.resId, new NEMakeup(makeup.resId, makeup.name));
        }

        return makeups;
   }
}
