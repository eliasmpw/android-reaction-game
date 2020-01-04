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
        mImageView = findViewById(R.id.image);

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(mBroadcastReveiverString, new IntentFilter(
                        EXAMPLE_BROADCAST_NAME_FOR_NOTIFICATION_MESSAGE_STRING_RECEIVED));
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(mBroadcastReveiverImage, new IntentFilter(
                        EXAMPLE_BROADCAST_NAME_FOR_NOTIFICATION_IMAGE_DATAMAP_RECEIVED));
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Un-register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(mBroadcastReveiverString);
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(mBroadcastReveiverImage);
    }
}
