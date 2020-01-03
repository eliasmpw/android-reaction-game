package com.lab.epfl.reactiongame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class GameFourActivity extends AppCompatActivity {

    private long startTime;
    private long yourTime;
    private long bestTime;
    private long oppoTime;
    private boolean result;
    private int ifbest = 0;

    private final String TAG = this.getClass().getSimpleName();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference dataGameRef;
    private boolean waitingForOponent;
    private int playerNumber;
    private String gameId;
    private int gameType;

    int imageNum = 0;
    int correctNum = 0;
    private TextView testhandle;
    private TextView game4guide;
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
            if(waitingForOponent == false) {
                super.handleMessage(msg);
                testhandle.setText("GO!");
                reactButton.setClickable(true);
                imageNum = new Random().nextInt(gamesize);
                hintImage.setImageDrawable(getDrawable(Integer.parseInt(imageNames.get(imageNum))));
                if (imageNum == correctNum)
                    startTime = System.currentTimeMillis(); // record the start time of the game
                sendEmptyMessageDelayed(1, 2000);
            }
            else if(waitingForOponent == true){
                testhandle.setText(yourTime+" ms");
                game4guide.setText("Please wait for your opponent...");
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game4);

        // TODO load player number from previous activity
        playerNumber = 1;
        // TODO load game type from previous activity
        gameType = 4;
        // TODO load Game Id from previous activity
        gameId = "TestGame4";

        setInitialVariables();

        startTime = System.currentTimeMillis(); // initialize the start time of the game

        ValueEventListener playerTimeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(waitingForOponent == true) {
                    // for player1
                    if (playerNumber == 1) {
                        if (dataSnapshot.child("time2").getValue() != null && (long) dataSnapshot.child("time2").getValue() != 0) {
                            oppoTime = (long) dataSnapshot.child("time2").getValue();
                            if (yourTime < oppoTime)
                                result = true;
                            else
                                result = false;

                            Intent intent = new Intent(GameFourActivity.this, WinLoseActivity.class);
                            intent.putExtra("result", result);
                            intent.putExtra("yourTime", yourTime);
                            intent.putExtra("ifbest",ifbest);
                            startActivity(intent);
                            finish();
                        }
                    }
                    // for player2
                    else {
                        if (dataSnapshot.child("time1").getValue() != null && (long) dataSnapshot.child("time1").getValue() != 0) {
                            oppoTime = (long) dataSnapshot.child("time1").getValue();
                            if (yourTime < oppoTime)
                                result = true;
                            else
                                result = false;

                            Intent intent = new Intent(GameFourActivity.this, WinLoseActivity.class);
                            intent.putExtra("result", result);
                            intent.putExtra("yourTime", yourTime);
                            intent.putExtra("ifbest",ifbest);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        dataGameRef.addValueEventListener(playerTimeListener);

        // start game
        handler.sendEmptyMessageDelayed(1,2000);
    }

    private void setInitialVariables() {
        dataGameRef = database.getReference("gameinprogress/" + gameId);

        testhandle = (TextView)findViewById(R.id.TestHandle);
        game4guide = (TextView)findViewById(R.id.game4guide);
        hintImage = (ImageView)findViewById(R.id.hintImage);
        reactButton = (ImageButton)findViewById(R.id.reactButton);


        correctNum = new Random().nextInt(gamesize);
        reactButton.setImageDrawable(getDrawable(Integer.parseInt(imageNames.get(correctNum))));
        reactButton.setClickable(false);

        waitingForOponent = false;
    }

    public void Click2React(View view){

        if(waitingForOponent == false) {
            if (imageNum == correctNum) {
                // player make correct choice
                long endTime = System.currentTimeMillis(); // record the end time of the game
                yourTime = (endTime - startTime);
            } else {
                yourTime = 9999;
            }
            // upload your time
            if (playerNumber == 1)
                dataGameRef.child("time1").setValue(yourTime);
            else
                dataGameRef.child("time2").setValue(yourTime);

            waitingForOponent = true;
        }
    }
}
