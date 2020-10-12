package com.netease.nmc.nertcsample;

import android.content.Context;
import android.content.Intent;

public class SampleLauncher {
    public static void startBasic(Context context, String roomId, long userId) {
        Intent intent = new Intent(context, BasicActivity.class);
        intent.putExtra(Extras.ROOM_ID, roomId);
        intent.putExtra(Extras.USER_ID, userId);
        context.startActivity(intent);
    }

    public static void startAVManage(Context context, String roomId, long userId) {
        Intent intent = new Intent(context, AVManageActivity.class);
        intent.putExtra(Extras.ROOM_ID, roomId);
        intent.putExtra(Extras.USER_ID, userId);
        context.startActivity(intent);
    }
}
