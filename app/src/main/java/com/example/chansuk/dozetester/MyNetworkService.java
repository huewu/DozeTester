package com.example.chansuk.dozetester;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by chansuk on 2016. 4. 4..
 */
public class MyNetworkService extends Service {
    private static final String TAG = "MyNetworkService";
    private static final int NOTI_ID = 1001;
    private static final int MAX_NETWORK_TRY = 30;

    private int network_try_counter = 0;
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

            network_try_counter = 0;
            new Thread() {
                @Override
                public void run() {
                    while (network_try_counter < MAX_NETWORK_TRY) {
                        try {
                            testNetworkConnection();
                            dumpProcessPriority();
                        } catch (IOException e) {
                            Log.d(TAG, "Fail to connect:" + e);
                            break;
                        }
                        SystemClock.sleep(3000);
                        ++network_try_counter;
                    }
                }
            }.start();

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

        network_try_counter = MAX_NETWORK_TRY;
    }

    private void testNetworkConnection() throws IOException {

        URL url = new URL("http://www.google.com");
        Log.d(TAG,"Trying to connect...");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.connect();
        Log.d(TAG,"ResponseCode:" + conn.getResponseCode());
        conn.disconnect();
    }

    private void dumpProcessPriority() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processList = am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info : processList) {
            Log.d(TAG, "Process Name:" + info.processName + "["
                    + getPriorityDesc(info.importance) + "]");
        }
    }

    private String getPriorityDesc(int priority) {

        if(priority <= 100) {
            return "IMPORTANCE_FOREGROUND";
        } else if (priority <= 125) {
            return "IMPORTANCE_FOREGROUND_SERVICE";
        } else if (priority <=150) {
            return "IMPORTANCE_TOP_SLEEPING";
        } else if (priority <= 200) {
            return "IMPORTANCE_VISIBLE";
        }

        return "IMPORTANCE_ETC";
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
