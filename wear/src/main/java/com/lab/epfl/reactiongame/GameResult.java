package com.lab.epfl.reactiongame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class GameResult extends WearableActivity {
    public static final String
            BROADCAST_CLOSE_GAMERESULT =
            "BROADCAST_CLOSE_GAMERESULT";

    private BroadcastReceiver closeBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);
        Intent intent = getIntent();
        boolean isWinner = intent.getBooleanExtra("winner", false);
        ImageView imageResult = findViewById(R.id.imageResult);
        if (isWinner) {
            imageResult.setImageResource(R.drawable.youwin);
        } else {
            imageResult.setImageResource(R.drawable.youlose);
        }

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(closeBroadcastReceiver, new IntentFilter(
                        BROADCAST_CLOSE_GAMERESULT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Un-register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(closeBroadcastReceiver);
    }
}
