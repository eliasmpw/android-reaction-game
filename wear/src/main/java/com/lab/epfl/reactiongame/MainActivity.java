package com.lab.epfl.reactiongame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends WearableActivity {

    public static final String
            EXAMPLE_BROADCAST_NAME_FOR_NOTIFICATION_MESSAGE_STRING_RECEIVED =
            "EXAMPLE_BROADCAST_NAME_FOR_NOTIFICATION_MESSAGE_STRING_RECEIVED";
    public static final String
            EXAMPLE_INTENT_STRING_NAME_WHEN_BROADCAST =
            "EXAMPLE_INTENT_STRING_NAME_WHEN_BROADCAST";
    public static final String
            EXAMPLE_BROADCAST_NAME_FOR_NOTIFICATION_IMAGE_DATAMAP_RECEIVED =
            "EXAMPLE_BROADCAST_NAME_FOR_NOTIFICATION_IMAGE_DATAMAP_RECEIVED";
    public static final String
            EXAMPLE_INTENT_IMAGE_NAME_WHEN_BROADCAST =
            "EXAMPLE_INTENT_IMAGE_NAME_WHEN_BROADCAST";
    public static final String
            BROADCAST_LOADING =
            "BROADCAST_LOADING";
    public static final String
            BROADCAST_HIGHSCORES =
            "BROADCAST_HIGHSCORES";
    public static final String
            BROADCAST_CHOOSEFROMMAIN =
            "BROADCAST_CHOOSEFROMMAIN";
    public static final String
            BROADCAST_GAME4 =
            "BROADCAST_GAME4";
    public static final String
            BROADCAST_GAME4_CHANGEIMAGE =
            "BROADCAST_GAME4_CHANGEIMAGE";
    public static final String
            BROADCAST_GAME4_CLOSE =
            "BROADCAST_GAME4_CLOSE";
    public static final String
            BROADCAST_GAME4_RESULT =
            "BROADCAST_GAME4_RESULT";
    public static final String
            BROADCAST_GAME4_RESULTSHOW =
            "BROADCAST_GAME4_RESULTHOW";
    public static final String
            BROADCAST_GAME4_RESULTCLOSE =
            "BROADCAST_GAME4_RESULTCLOSE";

    private BroadcastReceiver loadingBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent auxIntent = new Intent(MainActivity.this, LoadingActivity.class);
            startActivity(auxIntent);
        }
    };

    private BroadcastReceiver highscoresBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent auxIntent = new Intent(MainActivity.this, HighscoresActivity.class);
            startActivity(auxIntent);
        }
    };
    private BroadcastReceiver chooseFromMainBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent auxIntent = new Intent(MainActivity.this, GameChooseActivity.class);
            startActivity(auxIntent);
        }
    };
    private BroadcastReceiver Game4BroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent auxIntent = new Intent(MainActivity.this, GameFourActivity.class);
            startActivity(auxIntent);
        }
    };

    private BroadcastReceiver mBroadcastReveiverString = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mTextView.setText(
                    intent.getStringExtra(EXAMPLE_INTENT_STRING_NAME_WHEN_BROADCAST));
        }
    };

    private BroadcastReceiver mBroadcastReveiverImage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Retrieve the PNG-compressed image
            byte[] bytes = intent.getByteArrayExtra(
                    EXAMPLE_INTENT_IMAGE_NAME_WHEN_BROADCAST);
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            mImageView.setImageBitmap(image);
        }
    };

    private TextView mTextView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.text);
        mImageView = findViewById(R.id.imageMain);

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(loadingBroadcastReceiver, new IntentFilter(
                        BROADCAST_LOADING));
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(highscoresBroadcastReceiver, new IntentFilter(
                        BROADCAST_HIGHSCORES));
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(chooseFromMainBroadcastReceiver, new IntentFilter(
                        BROADCAST_CHOOSEFROMMAIN));
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
                .unregisterReceiver(loadingBroadcastReceiver);
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(highscoresBroadcastReceiver);
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(chooseFromMainBroadcastReceiver);
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(Game4BroadcastReceiver);
    }
}
