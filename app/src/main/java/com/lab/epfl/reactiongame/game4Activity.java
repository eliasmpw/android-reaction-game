package com.lab.epfl.reactiongame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class game4Activity extends AppCompatActivity {

    private long startTime;
    private long yourTime;
    private long yourbestTime;
    private long oppoTime = 1000;
    private boolean result;
    private boolean ifbest;

    int imageNum = 0;
    int correctNum = 0;
    private TextView testhandle;
    private ImageView hintImage;
    private ImageButton reactButton;
    private final int gamesize = 6; // Number of images

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


    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            testhandle.setText("GO!");
            reactButton.setClickable(true);
            /*imageNum++;
            if(imageNum > 5)
                imageNum = 0;*/
            imageNum = new Random().nextInt(gamesize);
            hintImage.setImageDrawable(getDrawable(Integer.parseInt(imageNames.get(imageNum))));
            if(imageNum == correctNum)
                startTime = System.currentTimeMillis(); // record the start time of the game
            sendEmptyMessageDelayed(1,2000);
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game4);

        testhandle = (TextView)findViewById(R.id.TestHandle);
        hintImage = (ImageView)findViewById(R.id.hintImage);
        reactButton = (ImageButton)findViewById(R.id.reactButton);

        correctNum = new Random().nextInt(gamesize);
        reactButton.setImageDrawable(getDrawable(Integer.parseInt(imageNames.get(correctNum))));
        reactButton.setClickable(false);

        startTime = System.currentTimeMillis(); // initialize the start time of the game

        handler.sendEmptyMessageDelayed(1,2000);

    }
    public void Click2React(View view){

        if(imageNum == correctNum) {
            // player make correct choice
            long endTime = System.currentTimeMillis(); // record the end time of the game
            yourTime = (endTime - startTime);
            if (yourTime > oppoTime)
                result = false;
            else
                result = true;
        }

        else{
            // player make wrong choice
            yourTime = -1;
            result = false;
        }

        Intent intent = new Intent(game4Activity.this,WinLoseActivity.class);
        intent.putExtra("result",result);
        intent.putExtra("yourTime",yourTime);
        startActivity(intent);
        finish();
    }

//    private void sendStartToWatch() {
//        Intent intentWear = new Intent(game4Activity.this, WearService.class);
//        /*intentWear.setAction(WearService.ACTION_SEND.STARTACTIVITY.name());
//        intentWear.putExtra(WearService.ACTIVITY_TO_START, "game4Activity");*/
//        intentWear.setAction(WearService.ACTION_SEND.MESSAGE.name());
//        intentWear.putExtra(WearService.MESSAGE, "Messaging other device!");
//        startService(intentWear);
//    }
//    private void sendProfileToWatch() {
//        int userProfile = 1;
//        Intent intentWear = new Intent(game4Activity.this,WearService.class);
//        intentWear.setAction(WearService.ACTION_SEND.PROFILE_SEND.name());
//        intentWear.putExtra("user",userProfile);
//        startService(intentWear);
//    }
}
