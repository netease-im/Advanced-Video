package com.netease.nmc.nertcsample;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.netease.lava.api.Trace;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.video.NERtcScreenConfig;


/**
 * 为了SDK的接入灵活性，3.9.0 将ScreenShareService从SDK中删除，用户需要自行实现，这里是个示例代码
 */
@TargetApi(21)
public class SimpleScreenShareService extends Service {
    private static final String TAG = "SimpleScreenShareService";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "SimpleScreenShareService";

    private ScreenShareBinder mScreenShareBinder;
    private ScreenShareNotification screenShareNotification;

    public SimpleScreenShareService() {
        mScreenShareBinder = new ScreenShareBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Trace.i(TAG, "onBind ");
        startForeground();
        return mScreenShareBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Trace.i(TAG, "onUnbind");
        stopScreenCapture();
        stopForeground(true);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Trace.i(TAG, "onDestroy");
        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_ID, importance);
            channel.setDescription(CHANNEL_ID);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotification() {
        screenShareNotification = () -> {
            Intent notificationIntent = new Intent(getApplicationContext(), getApplicationContext().getClass());
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

            Notification.Builder builder = new Notification.Builder(getApplicationContext())
                    .setContentTitle(CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setContentText(CHANNEL_ID);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID);
            }

            return builder.build();
        };
    }

    public class ScreenShareBinder extends Binder {
        public SimpleScreenShareService getService() {
            return SimpleScreenShareService.this;
        }
    }

    private void startForeground() {

        createNotificationChannel();
        createNotification();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Trace.i(TAG, "sdkVer:" + Build.VERSION.SDK_INT + " using FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION");
            try {
                startForeground(NOTIFICATION_ID, screenShareNotification.getNotification(),
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                stopForeground(true);
                startForeground(NOTIFICATION_ID, screenShareNotification.getNotification());
            }

        } else {
            startForeground(NOTIFICATION_ID, screenShareNotification.getNotification());
        }
    }


    public int startScreenCapture(NERtcScreenConfig videoConfig,
                                  Intent mediaProjectionPermissionResultData,
                                  MediaProjection.Callback mediaProjectionCallback) {
        return NERtcEx.getInstance().startScreenCapture(videoConfig, mediaProjectionPermissionResultData, mediaProjectionCallback);

    }

    public void stopScreenCapture() {
        NERtcEx.getInstance().stopScreenCapture();
    }


}
