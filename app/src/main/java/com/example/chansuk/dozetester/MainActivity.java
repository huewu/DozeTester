package com.example.chansuk.dozetester;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopNetworkService();
    }

    public void onStartServicClick(View view) {
        startNetworkService();
    }

    private void startNetworkService() {
         startService(new Intent(this, MyNetworkService.class));
    }

    private void stopNetworkService() {
        stopService(new Intent(this, MyNetworkService.class));
    }
}
