package com.lab.epfl.reactiongame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class GameFourActivity extends AppCompatActivity {

    private long startTime;
    private long yourTime;
    private long bestTime;
    private long oppoTime;
    private boolean result;

    private final String TAG = this.getClass().getSimpleName();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference dataGameRef;
    private boolean waitingForOponent;
    private boolean WearImagceChanged;
    private String name;
    private String userID;
    private int playerNumber;
    private String gameId;
    private int gameType;

    int imageNum = 0;
    public int correctNum = 0;
    private TextView testhandle;
    private TextView game4guide;
    private ImageView hintImage;
    private ImageButton reactButton;
    private final int gamesize = 6; // Number of images

    public static final String
            BROADCAST_GAME4_REACT =
            "BROADCAST_GAME4_REACT";


    private HashMap<DatabaseReference, ValueEventListener> listenerHashMap = new HashMap<>();
    public static void removeValueEventListener(HashMap<DatabaseReference, ValueEventListener> hashMap) {
        for (Map.Entry<DatabaseReference, ValueEventListener> entry : hashMap.entrySet()) {
            DatabaseReference databaseReference = entry.getKey();
            ValueEventListener valueEventListener = entry.getValue();
            databaseReference.removeEventListener(valueEventListener);
        }
    }

    private BroadcastReceiver WearReact = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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
    };

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
            if(WearImagceChanged == false) {
                // change image on wear
                Intent changeimageIntent = new Intent(GameFourActivity.this, WearService.class);
                changeimageIntent.putExtra("correctNum", String.valueOf(correctNum));
                changeimageIntent.setAction(WearService.ACTION_SEND.GAME4_CHANGEIMAGE.name());
                startService(changeimageIntent);
                WearImagceChanged = true;
            }

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
                if(yourTime != 9999)
                    testhandle.setText(yourTime+" ms");
                else
                    testhandle.setText("Wrong :(");
                game4guide.setText("Please wait for your opponent...");
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game4);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        name = intent.getStringExtra("name");
        playerNumber = intent.getIntExtra("NumPlayer",1);
        gameType = intent.getIntExtra("gameType",0);
        gameId = intent.getStringExtra("gameID");
        Log.v(TAG, Integer.toString(playerNumber));

        setInitialVariables();

        ValueEventListener playerTimeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If other player left the game and removed game in progress, finish activity
                if (dataSnapshot.getValue() == null) {
                    removeValueEventListener(listenerHashMap);
                    finish();
                    return;
                }

                if(waitingForOponent == true) {
                    // for player1
                    if (playerNumber == 1) {
                        if (dataSnapshot.child("time2").getValue() != null && (long) dataSnapshot.child("time2").getValue() != 0) {
                            oppoTime = (long) dataSnapshot.child("time2").getValue();
                            if (yourTime < oppoTime)
                                result = true;
                            else
                                result = false;

                            final long score = yourTime;
                            final DatabaseReference highscoreRef;
                            highscoreRef = database.getReference("highscores/Game" + (gameType + 1));

                            ValueEventListener valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Boolean hasHighscore = false;
                                    GameHighscores highScores = dataSnapshot.getValue(GameHighscores.class);
                                    if (score < Long.parseLong(highScores.user1.score)) {
                                        highScores.user5 = highScores.user4;
                                        highScores.user4 = highScores.user3;
                                        highScores.user3 = highScores.user2;
                                        highScores.user2 = highScores.user1;
                                        highScores.user1 = new SingleScore(Long.toString(score), userID, name);
                                        highscoreRef.setValue(highScores);
                                        hasHighscore = true;
                                    } else if (score < Long.parseLong(highScores.user2.score)) {
                                        highScores.user5 = highScores.user4;
                                        highScores.user4 = highScores.user3;
                                        highScores.user3 = highScores.user2;
                                        highScores.user2 = new SingleScore(Long.toString(score), userID, name);
                                        highscoreRef.setValue(highScores);
                                        hasHighscore = true;
                                    } else if (score < Long.parseLong(highScores.user3.score)) {
                                        highScores.user5 = highScores.user4;
                                        highScores.user4 = highScores.user3;
                                        highScores.user3 = new SingleScore(Long.toString(score), userID, name);
                                        highscoreRef.setValue(highScores);
                                        hasHighscore = true;
                                    } else if (score < Long.parseLong(highScores.user4.score)) {
                                        highScores.user5 = highScores.user4;
                                        highScores.user4 = new SingleScore(Long.toString(score), userID, name);
                                        highscoreRef.setValue(highScores);
                                        hasHighscore = true;
                                    } else if (score < Long.parseLong(highScores.user5.score)) {
                                        highScores.user5 = new SingleScore(Long.toString(score), userID, name);
                                        highscoreRef.setValue(highScores);
                                        hasHighscore = true;
                                    }

                                    Intent intent = new Intent(GameFourActivity.this, WinLoseActivity.class);
                                    intent.putExtra("result", result);
                                    intent.putExtra("yourTime", yourTime);
                                    intent.putExtra("ifbest", hasHighscore ? 1 : 0 );
                                    startActivity(intent);
                                    removeValueEventListener(listenerHashMap);
                                    removeGameInProgress();
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            };
                            highscoreRef.addListenerForSingleValueEvent(valueEventListener);
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

                            final long score = yourTime;
                            final DatabaseReference highscoreRef;
                            highscoreRef = database.getReference("highscores/Game" + (gameType + 1));

                            ValueEventListener valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Boolean hasHighscore = false;
                                    GameHighscores highScores = dataSnapshot.getValue(GameHighscores.class);
                                    if (score < Long.parseLong(highScores.user1.score)) {
                                        highScores.user5 = highScores.user4;
                                        highScores.user4 = highScores.user3;
                                        highScores.user3 = highScores.user2;
                                        highScores.user2 = highScores.user1;
                                        highScores.user1 = new SingleScore(Long.toString(score), userID, name);
                                        highscoreRef.setValue(highScores);
                                        hasHighscore = true;
                                    } else if (score < Long.parseLong(highScores.user2.score)) {
                                        highScores.user5 = highScores.user4;
                                        highScores.user4 = highScores.user3;
                                        highScores.user3 = highScores.user2;
                                        highScores.user2 = new SingleScore(Long.toString(score), userID, name);
                                        highscoreRef.setValue(highScores);
                                        hasHighscore = true;
                                    } else if (score < Long.parseLong(highScores.user3.score)) {
                                        highScores.user5 = highScores.user4;
                                        highScores.user4 = highScores.user3;
                                        highScores.user3 = new SingleScore(Long.toString(score), userID, name);
                                        highscoreRef.setValue(highScores);
                                        hasHighscore = true;
                                    } else if (score < Long.parseLong(highScores.user4.score)) {
                                        highScores.user5 = highScores.user4;
                                        highScores.user4 = new SingleScore(Long.toString(score), userID, name);
                                        highscoreRef.setValue(highScores);
                                        hasHighscore = true;
                                    } else if (score < Long.parseLong(highScores.user5.score)) {
                                        highScores.user5 = new SingleScore(Long.toString(score), userID, name);
                                        highscoreRef.setValue(highScores);
                                        hasHighscore = true;
                                    }

                                    Intent intent = new Intent(GameFourActivity.this, WinLoseActivity.class);
                                    intent.putExtra("result", result);
                                    intent.putExtra("yourTime", yourTime);
                                    intent.putExtra("ifbest", hasHighscore ? 1 : 0 );
                                    startActivity(intent);
                                    removeValueEventListener(listenerHashMap);
                                    removeGameInProgress();
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            };
                            highscoreRef.addListenerForSingleValueEvent(valueEventListener);
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
        listenerHashMap.put(dataGameRef, playerTimeListener);

        startTime = System.currentTimeMillis(); // initialize the start time of the game

        //start game on wear
        Intent auxIntent = new Intent(this, WearService.class);
        auxIntent.setAction(WearService.ACTION_SEND.OPEN_GAME4.name());
        startService(auxIntent);


        // start game
        handler.sendEmptyMessageDelayed(1,3000);


    }

    private void removeGameInProgress() {
        dataGameRef.removeValue();
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
        WearImagceChanged = false;
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

            //
            Intent go2resultIntent = new Intent(GameFourActivity.this, WearService.class);
            go2resultIntent.setAction(WearService.ACTION_SEND.GAME4_RESULT.name());
            startService(go2resultIntent);

            // upload your time
            if (playerNumber == 1)
                dataGameRef.child("time1").setValue(yourTime);
            else
                dataGameRef.child("time2").setValue(yourTime);

            waitingForOponent = true;
        }
    }


    private long backPressedTime = 0;    // used by onBackPressed()

    @Override
    public void onBackPressed() {        // to prevent irritating accidental logouts
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            Toast.makeText(this, "Press back again to exit game",
                    Toast.LENGTH_SHORT).show();
        } else {    // this guy is serious
            // clean up
            removeValueEventListener(listenerHashMap);
            removeGameInProgress();
            closeGameFourWear();
            super.onBackPressed();       // bye
        }
    }

    public void closeGameFourWear() {
        Intent auxIntent = new Intent(this, WearService.class);
        auxIntent.setAction(WearService.ACTION_SEND.CLOSE_GAME4.name());
        startService(auxIntent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(WearReact, new IntentFilter(
                        BROADCAST_GAME4_REACT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Un-register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(WearReact);

    }
}
