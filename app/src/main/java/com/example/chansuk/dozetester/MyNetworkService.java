package com.example.chansuk.dozetester;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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
    private static final int MAX_NETWORK_TRY = 30;

    private int network_try_counter = 0;
    private boolean isRunning = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            isRunning = true;

            network_try_counter = 0;
            new Thread() {
                @Override
                public void run() {
                    while (network_try_counter < MAX_NETWORK_TRY) {
                        try {
                            testNetworkConnection();
                        } catch (IOException e) {
                            Log.d(TAG, "Fail to connect:" + e);
                        } finally {
                            dumpProcessPriority();
                        }

                        SystemClock.sleep(3000);
                        ++network_try_counter;
                    }
                }
            }.start();

        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
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
        } else if (priority <= 300) {
            return "IMPORTANCE_SERVICE";
        }

        return "IMPORTANCE_ETC";
    }

}
