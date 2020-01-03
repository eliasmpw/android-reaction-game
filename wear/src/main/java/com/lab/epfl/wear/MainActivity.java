package com.lab.epfl.wear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    public static final String ACTION_RECEIVE_PROFILE_INFO = "RECEIVE_PROFILE_INFO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text);

        // Register for updates
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                TextView textView = findViewById(R.id.showView);

                String username = intent.getStringExtra("user");
                textView.setText("Welcome "+username + "!");
            }
        }, new IntentFilter(ACTION_RECEIVE_PROFILE_INFO));

        // Enables Always-on
        setAmbientEnabled();
    }
}
