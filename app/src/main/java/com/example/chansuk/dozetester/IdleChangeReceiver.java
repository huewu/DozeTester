package com.example.chansuk.dozetester;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.PowerManager;

/**
 * Created by chansuk on 2016. 4. 5..
 */
public abstract class IdleChangeReceiver extends BroadcastReceiver {

    public static IntentFilter Filter
            = new IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED);

    protected void launchMaskActivtiy(Context context) {
        Intent startMain = new Intent(context, MaskActivity.class);
        startMain.addCategory(Intent.CATEGORY_DEFAULT);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
    }

    protected void bringToHome(Context context) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
    }
}
