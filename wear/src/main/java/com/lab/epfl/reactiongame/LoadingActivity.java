package com.lab.epfl.reactiongame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LoadingActivity extends WearableActivity {
    public static final String
            BROADCAST_CHOOSE =
            "BROADCAST_CHOOSE";
    public static final String
            BROADCAST_CLOSE_LOADING =
            "BROADCAST_CLOSE_LOADING";
    public static final String
            BROADCAST_GAME4 =
            "BROADCAST_GAME4";

    private BroadcastReceiver chooseBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent auxIntent = new Intent(LoadingActivity.this, GameChooseActivity.class);
            startActivity(auxIntent);
            finish();
        }
    };
    private BroadcastReceiver Game4BroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent auxIntent = new Intent(LoadingActivity.this, GameFourActivity.class);
            startActivity(auxIntent);
            finish();
        }
    };

    private BroadcastReceiver closeBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(chooseBroadcastReceiver, new IntentFilter(
                        BROADCAST_CHOOSE));
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(closeBroadcastReceiver, new IntentFilter(
                        BROADCAST_CLOSE_LOADING));
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(Game4BroadcastReceiver, new IntentFilter(
                        BROADCAST_GAME4));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Un-register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(chooseBroadcastReceiver);
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(closeBroadcastReceiver);
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(Game4BroadcastReceiver);
    }
}
