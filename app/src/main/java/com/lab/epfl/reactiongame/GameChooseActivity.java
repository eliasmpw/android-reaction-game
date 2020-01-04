package com.lab.epfl.reactiongame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.abs;

public class GameChooseActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference dataGameRef;
    private final int gamesize = 9;
    private boolean startedGame;
    private boolean waitingForOponent;
    private boolean myResponseIsReady;
    private int playerNumber;
    private int gameType;
    private String gameId;
    private ArrayList<Integer> fieldIDs = new ArrayList<Integer>(){
        {
            add(R.id.Field0);
            add(R.id.Field1);
            add(R.id.Field2);
            add(R.id.Field3);
            add(R.id.Field4);
            add(R.id.Field5);
            add(R.id.Field6);
            add(R.id.Field7);
            add(R.id.Field8);
        }
    };
    private HashMap<String, Integer> mapStringToResource;
    private GameChoose game;
    private String name;
    private String userID;
    private Chronometer myTime;
    private int testInt = 0;
    public static final String
            BROADCAST_SELECTOPTION =
            "BROADCAST_SELECTOPTION";

    private HashMap<DatabaseReference, ValueEventListener> listenerHashMap = new HashMap<>();
    public static void removeValueEventListener(HashMap<DatabaseReference, ValueEventListener> hashMap) {
        for (Map.Entry<DatabaseReference, ValueEventListener> entry : hashMap.entrySet()) {
            DatabaseReference databaseReference = entry.getKey();
            ValueEventListener valueEventListener = entry.getValue();
            databaseReference.removeEventListener(valueEventListener);
        }
    }

    private BroadcastReceiver selectOptionBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String indexOption = intent.getStringExtra("indexOption");
            handleOptionSelected(Integer.parseInt(indexOption));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        name = intent.getStringExtra("name");
        playerNumber = intent.getIntExtra("NumPlayer",1);
        gameType = intent.getIntExtra("gameType",0);
        gameId = intent.getStringExtra("gameID");

        setContentView(R.layout.activity_game_choose);

        setInitialVariables();

        ValueEventListener createdOnListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null && (long) dataSnapshot.getValue() != 0) {
                    if (startedGame) {
                        waitingForOponent = false;
                    }
                    startedGame = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        DatabaseReference auxRef1 = dataGameRef.child("questionCreatedOn");
        auxRef1.addValueEventListener(createdOnListener);
        listenerHashMap.put(auxRef1, createdOnListener);

        ValueEventListener playerTimeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null && (long) dataSnapshot.getValue() != 0) {
                    waitingForOponent = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        DatabaseReference auxRef2 = dataGameRef.child(playerNumber == 1 ? "questionTimePlayer2" : "questionTimePlayer1");
        auxRef2.addValueEventListener(playerTimeListener);
        listenerHashMap.put(auxRef2, playerTimeListener);

        ValueEventListener gameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If other player left the game and removed game in progress, finish activity
                if (dataSnapshot.getValue() == null) {
                    removeValueEventListener(listenerHashMap);
                    Toast.makeText(getApplicationContext(),
                            "Other player left the match",
                            Toast.LENGTH_LONG).show();
                    closeGameChooseWear();
                    finish();
                    return;
                }
                
                Log.v(TAG, "DATA RECEIVED: " + dataSnapshot.toString());
                // Get Post object and use the values to update the UI
                game = dataSnapshot.getValue(GameChoose.class);
                Log.v(TAG, "DATA CHANGE FOR PLAYER: " + playerNumber + " -> " + gameToString(game));
                Log.v(TAG, "ENMIDATA CHANGE: " + Boolean.toString(startedGame) + " " + Boolean.toString(waitingForOponent) + Boolean.toString(myResponseIsReady));
                if (!startedGame) {
                    if (playerNumber == 2) {
                        if (game.answers.size() == 0) {
                            ArrayList<GameOption> auxAnswers = createAnswers();
                            setAnswers(auxAnswers);
                            Log.v(TAG, "SIZE ANSWERS: " + game.answers.size());
                            dataGameRef.setValue(game);
                        } else {
                            return;
                        }
                    } else {
                        if (game.answers.size() == 0) {
                            return;
                        } else {
                            game.questionCreatedOn = System.currentTimeMillis();
                            startedGame = true;
                            dataGameRef.setValue(game);
                        }
                    }
                } else {
                    if (waitingForOponent) {
                        return;
                    } else {
                        if (myResponseIsReady) {
                            if (game.points1 >= 5) {
                                Log.v(TAG, "Player 1 WINS");
                                removeValueEventListener(listenerHashMap);

                                final long score = playerNumber == 1 ? game.time1 : game.time2;
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

                                        Intent intent = new Intent(GameChooseActivity.this, GameResultActivity.class);
                                        intent.putExtra("win", playerNumber == 1);
                                        intent.putExtra("highscore", hasHighscore);
                                        intent.putExtra("time", score);
                                        startActivity(intent);
                                        openGameResultWear(playerNumber == 1);
                                        removeGameInProgress();
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                };
                                highscoreRef.addListenerForSingleValueEvent(valueEventListener);

                            } else if (game.points2 >= 5) {
                                Log.v(TAG, "Player 2 WINS");
                                removeValueEventListener(listenerHashMap);

                                final long score = playerNumber == 1 ? game.time1 : game.time2;
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

                                        Intent intent = new Intent(GameChooseActivity.this, GameResultActivity.class);
                                        intent.putExtra("win", playerNumber == 2);
                                        intent.putExtra("highscore", hasHighscore);
                                        intent.putExtra("time", score);
                                        startActivity(intent);
                                        openGameResultWear(playerNumber == 2);
                                        removeGameInProgress();
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                };
                                highscoreRef.addListenerForSingleValueEvent(valueEventListener);

                            } else {
                                waitingForOponent = true;
                                myResponseIsReady = false;
                                countDownAndShow();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        dataGameRef.addValueEventListener(gameListener);
        listenerHashMap.put(dataGameRef, gameListener);
    }

    private void countDownAndShow() {
        TextView myScore = findViewById(R.id.MyScore);
        TextView opponentScore = findViewById(R.id.OpponentScore);
        TextView question = findViewById(R.id.Question);
        TextView subtitle = findViewById(R.id.Subtitle);
        TextView totalTime = findViewById(R.id.TotalTime);

        subtitle.setTextColor(Color.WHITE);
        myScore.setText(playerNumber == 1 ? Integer.toString(game.points1) : Integer.toString(game.points2));
        opponentScore.setText(playerNumber == 1 ? Integer.toString(game.points2) : Integer.toString(game.points1));

        totalTime.setText(formatTimeMilliseconds(playerNumber == 1 ? game.time1 : game.time2));

        switch (game.lastWinner) {
            case -1: question.setText(R.string.NoWinner);
                question.setTextColor(Color.WHITE);
                break;
            case 0: question.setText(R.string.GetReady);
                question.setTextColor(Color.WHITE);
                break;
            case 1: question.setText(playerNumber == 1 ? R.string.PointYou : R.string.PointOpponent);
                question.setTextColor(playerNumber == 1 ? Color.GREEN : Color.RED);
                break;
            case 2: question.setText(playerNumber == 1 ? R.string.PointOpponent : R.string.PointYou);
                question.setTextColor(playerNumber == 1 ? Color.RED : Color.GREEN);
                break;
        }

        for (int buttonId: fieldIDs) {
            Button auxButton = findViewById(buttonId);
            auxButton.setBackgroundResource(android.R.color.white);
            auxButton.setText("?");
            auxButton.setTextColor(Color.BLACK);
            auxButton.setClickable(false);
        }

        subtitle.setText(Integer.toString(4));

        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                TextView subtitle = findViewById(R.id.Subtitle);
                subtitle.setText("Next question in: " + Long.toString(millisUntilFinished / 1000));
            }

            public void onFinish() {
                updateAnswersWear();
                TextView question = findViewById(R.id.Question);
                question.setText(game.question);
                question.setTextColor(Color.WHITE);
                TextView subtitle = findViewById(R.id.Subtitle);
                subtitle.setText("");
                switch (gameType) {
                    case 0:
                        for (int i = 0; i < 9; i++) {
                            Button auxButton = findViewById(fieldIDs.get(i));
                            auxButton.setText(game.answers.get("+" + i).text);
                            auxButton.setClickable(true);
                        }
                        break;
                    case 1:
                        for (int i = 0; i < 9; i++) {
                            Button auxButton = findViewById(fieldIDs.get(i));
                            auxButton.setText(game.answers.get("+" + i).text);
                            auxButton.setTextColor(mapStringToResource.get(game.answers.get("+" + i).color));
                            auxButton.setClickable(true);
                        }
                        break;
                    case 2:
                        for (int i = 0; i < 9; i++) {
                            Button auxButton = findViewById(fieldIDs.get(i));
                            auxButton.setText("");
                            auxButton.setBackgroundResource(mapStringToResource.get(game.answers.get("+" + i).image));
                            auxButton.setClickable(true);
                        }
                        break;
                }
                myTime.start();
            }
        }.start();
    }

    private ArrayList<GameOption> createAnswers() {
        ArrayList<GameOption> answers = new ArrayList<>();
        switch (gameType) {
            case 0:
                Random r = new Random();
                for (int i = 0; i < 9; i++) {
                    answers.add(new GameOption(null, Integer.toString(r.nextInt(90) + 10), null));
                }
                break;
            case 1:
                String[] auxArray = new String[mapStringToResource.keySet().size()];
                mapStringToResource.keySet().toArray(auxArray);
                String[] secondAuxArray = Arrays.copyOf(auxArray, auxArray.length);
                List<String> auxList = Arrays.asList(auxArray);
                Collections.shuffle(auxList);
                List<String> secondAuxList = Arrays.asList(secondAuxArray);
                Collections.shuffle(secondAuxList);
                for (int i = 0; i < 9; i++) {
                    answers.add(new GameOption(auxList.get(i), secondAuxList.get(i), null));
                }
                break;
            case 2:
                String[] auxImageArray = new String[mapStringToResource.keySet().size()];
                mapStringToResource.keySet().toArray(auxImageArray);
                List<String> auxImageList = Arrays.asList(auxImageArray);
                Collections.shuffle(auxImageList);
                for (int i = 0; i < 9; i++) {
                    answers.add(new GameOption(null, null, auxImageList.get(i)));
                }
                break;
        }
        return answers;
    }

    private void setAnswers(ArrayList<GameOption> auxAnswers) {
        Random r = new Random();
        game.correctAnswer = r.nextInt(9);
        for (int i = 0; i < 9; i++) {
            game.answers.put("+" + i, auxAnswers.get(i));
        }
        switch (gameType) {
            case 0:
                int auxCorrectAnswer = Integer.parseInt(auxAnswers.get(game.correctAnswer).text);
                int var1 = auxCorrectAnswer;
                int var2 = 1;
                int var3 = 0;
                while((var1 * var2) == auxCorrectAnswer) {
                    var1 = r.nextInt(10) + 1;
                    var2 = r.nextInt(10) + 1;
                }
                var3 = auxCorrectAnswer - (var1 * var2);
                game.question = "" +  var1 + " x " + var2 + (var3 >= 0 ? " + " : " - ") + abs(var3);
                break;
            case 1:
                game.question = auxAnswers.get(game.correctAnswer).color;
                break;
            case 2:
                game.question = auxAnswers.get(game.correctAnswer).image;
        }
    }

    public void selectField(View view) {
        myTime.stop();
        Button field = (Button) view;
        int id = field.getId();
        int position = fieldIDs.indexOf(id);
        handleOptionSelected(position);
    }

    public void handleOptionSelected(int position) {
        myResponseIsReady = true;
        for (int buttonId: fieldIDs) {
            findViewById(buttonId).setClickable(false);
        }
        TextView question = findViewById(R.id.Question);
        TextView subtitle = findViewById(R.id.Subtitle);
        question.setText(R.string.WaitingOpponentText);
        if (position == game.correctAnswer) {
            subtitle.setTextColor(Color.GREEN);
            subtitle.setText(R.string.Correct);
            if (playerNumber == 1) {
                game.questionTimePlayer1 = myTime.getTimeElapsed();
            } else {
                game.questionTimePlayer2 = myTime.getTimeElapsed();
            }
        } else {
            subtitle.setTextColor(Color.RED);
            subtitle.setText(R.string.Wrong);
            if (playerNumber == 1) {
                game.questionTimePlayer1 = -1;
            } else {
                game.questionTimePlayer2 = -1;
            }
        }
        if(game.questionTimePlayer1 != 0 && game.questionTimePlayer2 != 0) {
            if(game.questionTimePlayer1 == -1 && game.questionTimePlayer2 == -1) {
                game.questionTimePlayer1 = 10000;
                game.questionTimePlayer2 = 10000;
                game.lastWinner = -1;
            } else {
                if (game.questionTimePlayer1 == -1) {
                    game.questionTimePlayer1 = game.questionTimePlayer2 + 1000;
                }
                if (game.questionTimePlayer2 == -1) {
                    game.questionTimePlayer2 = game.questionTimePlayer1 + 1000;
                }
                if (game.questionTimePlayer1 < game.questionTimePlayer2) {
                    game.lastWinner = 1;
                    game.points1++;
                } else {
                    game.lastWinner = 2;
                    game.points2++;
                }
            }
            game.questionCreatedOn = System.currentTimeMillis();
            game.time1 += game.questionTimePlayer1;
            game.time2 += game.questionTimePlayer2;
            game.questionTimePlayer1 = 0;
            game.questionTimePlayer2 = 0;
            ArrayList<GameOption> auxAnswers = createAnswers();
            setAnswers(auxAnswers);
        }

        dataGameRef.setValue(game);
    }

    public String formatTimeMilliseconds(long time) {
        DecimalFormat df = new DecimalFormat("0");
        DecimalFormat two = new DecimalFormat("00");
        DecimalFormat mf = new DecimalFormat("000");

        int hours = (int)(time / (3600 * 1000));
        int remaining = (int)(time % (3600 * 1000));

        int minutes = (int)(remaining / (60 * 1000));
        remaining = (int)(remaining % (60 * 1000));

        int seconds = (int)(remaining / 1000);
        remaining = (int)(remaining % (1000));

        int milliseconds = (int)((int)time % 1000);

        String text = "";

        if (hours > 0) {
            text += df.format(hours) + ":";
        }
        if (minutes > 0) {
            text += df.format(minutes) + ":";
            text += two.format(seconds) + ".";
        } else {
            text += df.format(seconds) + ".";
        }
        text += mf.format(milliseconds);
        return text;
    }

    public void testButtonElias(View view) {
        Log.v(TAG, "TEST BUTTONCITO");
        Log.v(TAG, Boolean.toString(startedGame));
        Log.v(TAG, Boolean.toString(waitingForOponent));
        Log.v(TAG, Boolean.toString(myResponseIsReady));
        Log.v(TAG, gameToString(game));
    }

    private String gameToString(GameChoose game) {
        return "PUNTOS: " + game.points1 + " " + game.points2 + " CREATEDON: " + game.questionCreatedOn + " CORRECTANS: " + game.correctAnswer + " SIZEANS: " + game.answers.size();
    }

    private void setInitialVariables() {
        dataGameRef= database.getReference("gameinprogress/" + gameId);
        myTime = findViewById(R.id.GameTime);

        startedGame = false;
        waitingForOponent = false;
        myResponseIsReady = true;

        switch (gameType) {
            case 2:
                mapStringToResource = new HashMap<>();
                mapStringToResource.put("Antelope", getResources().getIdentifier("animalsantelope", "drawable", getPackageName()));
                mapStringToResource.put("Bat", getResources().getIdentifier("animalsbat", "drawable", getPackageName()));
                mapStringToResource.put("Bear", getResources().getIdentifier("animalsbear", "drawable", getPackageName()));
                mapStringToResource.put("Camel", getResources().getIdentifier("animalscamel", "drawable", getPackageName()));
                mapStringToResource.put("Chameleon", getResources().getIdentifier("animalschameleon", "drawable", getPackageName()));
                mapStringToResource.put("Cheetah", getResources().getIdentifier("animalscheetah", "drawable", getPackageName()));
                mapStringToResource.put("Crocodile", getResources().getIdentifier("animalscrocodile", "drawable", getPackageName()));
                mapStringToResource.put("Deer", getResources().getIdentifier("animalsdeer", "drawable", getPackageName()));
                mapStringToResource.put("Dolphin", getResources().getIdentifier("animalsdolphin", "drawable", getPackageName()));
                mapStringToResource.put("Elephant", getResources().getIdentifier("animalselephant", "drawable", getPackageName()));
                mapStringToResource.put("Fox", getResources().getIdentifier("animalsfox", "drawable", getPackageName()));
                mapStringToResource.put("Giraffe", getResources().getIdentifier("animalsgiraffe", "drawable", getPackageName()));
                mapStringToResource.put("Gorilla", getResources().getIdentifier("animalsgorilla", "drawable", getPackageName()));
                mapStringToResource.put("Hippo", getResources().getIdentifier("animalshippo", "drawable", getPackageName()));
                mapStringToResource.put("Kangaroo", getResources().getIdentifier("animalskangaroo", "drawable", getPackageName()));
                mapStringToResource.put("Koala", getResources().getIdentifier("animalskoala", "drawable", getPackageName()));
                mapStringToResource.put("Lion", getResources().getIdentifier("animalslion", "drawable", getPackageName()));
                mapStringToResource.put("Lizard", getResources().getIdentifier("animalslizard", "drawable", getPackageName()));
                mapStringToResource.put("Llama", getResources().getIdentifier("animalsllama", "drawable", getPackageName()));
                mapStringToResource.put("Monkey", getResources().getIdentifier("animalsmonkey", "drawable", getPackageName()));
                mapStringToResource.put("Mouse", getResources().getIdentifier("animalsmouse", "drawable", getPackageName()));
                mapStringToResource.put("Panda", getResources().getIdentifier("animalspanda", "drawable", getPackageName()));
                mapStringToResource.put("Panther", getResources().getIdentifier("animalspanther", "drawable", getPackageName()));
                mapStringToResource.put("Polar bear", getResources().getIdentifier("animalspolarbear", "drawable", getPackageName()));
                mapStringToResource.put("Rhino", getResources().getIdentifier("animalsrhino", "drawable", getPackageName()));
                mapStringToResource.put("Snake", getResources().getIdentifier("animalssnake", "drawable", getPackageName()));
                mapStringToResource.put("Tiger", getResources().getIdentifier("animalstiger", "drawable", getPackageName()));
                mapStringToResource.put("Wolf", getResources().getIdentifier("animalswolf", "drawable", getPackageName()));
                mapStringToResource.put("Zebra", getResources().getIdentifier("animalszebra", "drawable", getPackageName()));
                break;
            case 1:
                mapStringToResource = new HashMap<>();
                mapStringToResource.put("Red", Color.RED);
                mapStringToResource.put("Light Green", Color.rgb(0, 255, 0));
                mapStringToResource.put("Dark Green", Color.rgb(0, 120, 0));
                mapStringToResource.put("Yellow", Color.YELLOW);
                mapStringToResource.put("Light Blue", Color.rgb(153, 255, 255));
                mapStringToResource.put("Dark Blue", Color.rgb(0, 0, 255));
                mapStringToResource.put("Black", Color.BLACK);
                mapStringToResource.put("Orange", Color.rgb(255, 153, 51));
                mapStringToResource.put("Brown", Color.rgb(153, 76, 0));
                mapStringToResource.put("Pink", Color.rgb(255, 153, 204));
                mapStringToResource.put("Purple", Color.rgb(73, 0, 153));
                mapStringToResource.put("Gray", Color.rgb(160, 160, 160));
                break;
            default:
                break;
        }
    }

    private long backPressedTime = 0;    // used by onBackPressed()

    private void removeGameInProgress() {
        dataGameRef.removeValue();
    }
    
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
            closeGameChooseWear();
            removeGameInProgress();
            super.onBackPressed();       // bye
        }
    }

    public void openGameResultWear(Boolean isWinner) {
        Intent auxIntent = new Intent(this, WearService.class);
        auxIntent.setAction(WearService.ACTION_SEND.OPEN_GAMERESULT.name());
        auxIntent.putExtra("isWinner", Boolean.toString(isWinner));
        startService(auxIntent);
    }

    public void closeGameChooseWear() {
        Intent auxIntent = new Intent(this, WearService.class);
        auxIntent.setAction(WearService.ACTION_SEND.CLOSE_GAMECHOOSE.name());
        startService(auxIntent);
    }

    public void updateAnswersWear() {
        Intent auxIntent = new Intent(this, WearService.class);
        auxIntent.setAction(WearService.ACTION_SEND.UPDATE_GAMECHOOSE.name());
        for (int i = 0; i<9; i++) {
            GameOption auxAnswer = game.answers.get("+" + i);
            auxIntent.putExtra("color" + i, auxAnswer.color == null ? "" : auxAnswer.color);
            auxIntent.putExtra("text" + i, auxAnswer.text == null ? "" : auxAnswer.text);
            auxIntent.putExtra("image" + i, auxAnswer.image == null ? "" : auxAnswer.image);
        }
        startService(auxIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(selectOptionBroadcastReceiver, new IntentFilter(
                        BROADCAST_SELECTOPTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Un-register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(selectOptionBroadcastReceiver);
    }
}
