package com.lab.epfl.reactiongame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Locale;

public class GameFourActivity extends WearableActivity {
    int imageNum = 0;
    private final int gamesize = 6; // Number of images
    ImageButton reactButton;
    TextView hintText;

    public static final String
            BROADCAST_GAME4_CHANGEIMAGE =
            "BROADCAST_GAME4_CHANGEIMAGE";
    public static final String
            BROADCAST_GAME4_CLOSE =
            "BROADCAST_GAME4_CLOSE";
    public static final String
            BROADCAST_GAME4_RESULT =
            "BROADCAST_GAME4_RESULT";

    private ArrayList<String> imageNames = new ArrayList() {
        {
            add(String.format(Locale.getDefault(), "%d", R.drawable.image1));
            add(String.format(Locale.getDefault(), "%d", R.drawable.image2));
            add(String.format(Locale.getDefault(), "%d", R.drawable.image3));
            add(String.format(Locale.getDefault(), "%d", R.drawable.image4));
            add(String.format(Locale.getDefault(), "%d", R.drawable.image5));
            add(String.format(Locale.getDefault(), "%d", R.drawable.image6));
        }
    };

    private BroadcastReceiver ChangeImage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String str = intent.getStringExtra("correctNum");
            int correctNum = Integer.parseInt(str);
            hintText.setText("GO!");
            reactButton.setImageDrawable(getDrawable(Integer.parseInt(imageNames.get(correctNum))));
        }
    };

    private BroadcastReceiver Result = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent resultIntent = new Intent(GameFourActivity.this,WinLoseActivity.class);
            startActivity(resultIntent);
            finish();
        }
    };

    private BroadcastReceiver Close = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game4);
        reactButton = (ImageButton)findViewById(R.id.reactButton);
        hintText = (TextView)findViewById(R.id.hintText);
        reactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent auxIntent = new Intent(GameFourActivity.this, WearService.class);
                auxIntent.setAction(WearService.ACTION_SEND.GAME4_REACT.name());
                startService(auxIntent);

                Intent resultIntent = new Intent(GameFourActivity.this,WinLoseActivity.class);
                startActivity(resultIntent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(ChangeImage, new IntentFilter(
                        BROADCAST_GAME4_CHANGEIMAGE));
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(Close, new IntentFilter(
                        BROADCAST_GAME4_CLOSE));
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(Result, new IntentFilter(
                        BROADCAST_GAME4_RESULT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Un-register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(ChangeImage);
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(Close);
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(Result);

    }
}
