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

    public static void startPushStream(Context context, String roomId, long userId) {
        Intent intent = new Intent(context, PushStreamActivity.class);
        intent.putExtra(Extras.ROOM_ID, roomId);
        intent.putExtra(Extras.USER_ID, userId);
        context.startActivity(intent);
    }
}
