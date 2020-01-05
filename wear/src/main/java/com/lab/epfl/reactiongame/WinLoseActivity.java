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

import static com.lab.epfl.reactiongame.MainActivity.BROADCAST_GAME4_RESULTCLOSE;
import static com.lab.epfl.reactiongame.MainActivity.BROADCAST_GAME4_RESULTSHOW;

public class WinLoseActivity extends WearableActivity {

    private TextView mTextView;
    private TextView gameresult;
    private TextView ifbest;
    private ImageView resultimage;

    private BroadcastReceiver ShowResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("result");
            switch (result){
                case "11":
                    gameresult.setText("You WIN!");
                    ifbest.setText("High Score!");
                    resultimage.setImageDrawable(getDrawable(R.drawable.winimage));
                    break;
                case "10":
                    gameresult.setText("You WIN!");
                    ifbest.setText("");
                    resultimage.setImageDrawable(getDrawable(R.drawable.winimage));
                    break;
                case "01":
                    gameresult.setText("You LOSE!");
                    ifbest.setText("High Score!");
                    resultimage.setImageDrawable(getDrawable(R.drawable.loseimage));
                    break;
                case "00":
                    gameresult.setText("You LOSE!");
                    ifbest.setText("");
                    resultimage.setImageDrawable(getDrawable(R.drawable.loseimage));
                    break;
                default:
                    break;
            }
        }
    };
    private BroadcastReceiver CloseResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_lose);
        gameresult = (TextView)findViewById(R.id.gameresult);
        ifbest = (TextView)findViewById(R.id.ifbest);
        resultimage = (ImageView)findViewById(R.id.resultimage);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(ShowResult, new IntentFilter(
                        BROADCAST_GAME4_RESULTSHOW));
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(CloseResult, new IntentFilter(
                        BROADCAST_GAME4_RESULTCLOSE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Un-register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(ShowResult);
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(CloseResult);

    }
}
