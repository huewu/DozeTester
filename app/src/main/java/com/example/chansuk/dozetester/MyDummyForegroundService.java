package com.example.chansuk.dozetester;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

/**
 * Created by chansuk on 2016. 4. 4..
 */
public class MyDummyForegroundService extends Service {

    private static final String TAG = "DummyForegroundService";
    private static final int NOTI_ID = 1002;
    private static final int MAX_NETWORK_TRY = 30;

    private boolean isRunning = false;

    private BroadcastReceiver receiver = new IdleReceiver();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            isRunning = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                registerReceiver(receiver, IdleReceiver.Filter);
            }

            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("Doze Test Service is running");
            startForeground(NOTI_ID, builder.getNotification());
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            unregisterReceiver(receiver);
        }
    }

    public static class IdleReceiver extends IdleChangeReceiver {
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {

            PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
            if (pm.isDeviceIdleMode()){
                launchMaskActivtiy(context);
            }
        }
    }
}
