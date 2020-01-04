package com.lab.epfl.reactiongame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference lookingforgameGetRef = database.getReference("lookingforgame");
    private static DatabaseReference lookingforgameRef = lookingforgameGetRef.push();

    Profile userProfile;
//    private DatabaseReference mDatabase;


    private static final DatabaseReference gameInProgressGetRef = database.getReference("gameinprogress");
    private static DatabaseReference gameInProgressRef = gameInProgressGetRef.push();

    private static final DatabaseReference highScoresGetRef = database.getReference("highscores");
    private static DatabaseReference highScoresRef = highScoresGetRef.push();

    SessionManager session;
//    private Profile userProfile;

    public static final String EXAMPLE_INTENT_STRING_NAME_ACTIVITY_TO_SERVICE =
            "EXAMPLE_INTENT_STRING_NAME_ACTIVITY_TO_SERVICE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = getIntent();
//        userProfile = (Profile) intent.getSerializableExtra("userProfileWelcome");
//        sendProfileToWatch();
        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        final String userID = user.get(SessionManager.KEY_NAME);
        // user
        final String name = user.get(SessionManager.KEY_USER);
        Log.v(TAG, userID);
        Button playButton1 = findViewById(R.id.PlayButton1);
        playButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CLICKED PLAY BUTTON!!!!");
                Log.v(TAG, userID);
                Intent loadingIntent = new Intent(MainActivity.this, LoadingActivity.class);
                loadingIntent.putExtra("userID", userID);
                loadingIntent.putExtra("name", name);
                loadingIntent.putExtra("gameType", 0);
                MainActivity.this.startActivity(loadingIntent);
                openLoadingWear();
//                MatchMaking(userID);
//                MainActivity.this.finish();
            }
        });
        Button playButton2 = findViewById(R.id.PlayButton2);
        playButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CLICKED PLAY BUTTON!!!!");
                Log.v(TAG, userID);
//                addNameToLookingForGameDB(userID);
                Intent loadingIntent = new Intent(MainActivity.this, LoadingActivity.class);
                loadingIntent.putExtra("userID", userID);
                loadingIntent.putExtra("name", name);
                loadingIntent.putExtra("gameType", 1);
                MainActivity.this.startActivity(loadingIntent);
                openLoadingWear();
//                MatchMaking(userID);
//                MainActivity.this.finish();
            }
        });
        Button playButton3 = findViewById(R.id.PlayButton3);
        playButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CLICKED PLAY BUTTON!!!!");
                Log.v(TAG, userID);
//                addNameToLookingForGameDB(userID);
                Intent loadingIntent = new Intent(MainActivity.this, LoadingActivity.class);
                loadingIntent.putExtra("userID", userID);
                loadingIntent.putExtra("name", name);
                loadingIntent.putExtra("gameType", 2);
                MainActivity.this.startActivity(loadingIntent);
                openLoadingWear();
//                MatchMaking(userID);
//                MainActivity.this.finish();
            }
        });
        Button playButton4 = findViewById(R.id.PlayButton4);
        playButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CLICKED PLAY BUTTON!!!!");
                Log.v(TAG, userID);
//                addNameToLookingForGameDB(userID);
                Intent loadingIntent = new Intent(MainActivity.this, LoadingActivity.class);
                loadingIntent.putExtra("userID", userID);
                loadingIntent.putExtra("name", name);
                loadingIntent.putExtra("gameType", 3);
                MainActivity.this.startActivity(loadingIntent);
                openLoadingWear();
//                MatchMaking(userID);
//                MainActivity.this.finish();
            }
        });

        Button scoresButton = findViewById(R.id.scoresButton);
        scoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, userID);
                Intent scoresIntent = new Intent(MainActivity.this, ScoresActivity.class);
                MainActivity.this.startActivity(scoresIntent);
                openHighscoresWear();
//                MatchMaking(userID);
//                MainActivity.this.finish();
            }
        });


        Button logoutButton = findViewById(R.id.LogoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CLICKED logoutButton BUTTON!!!!");
                session.logoutUser();
                finish();
            }
        });

        Button exitButton = findViewById(R.id.ExitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CLICKED exitButton BUTTON!!!!");
                Log.v(TAG, userID);
//                removeNameToLookingForGameDB(userID);
//                CreateHighScoreDB(userID);
                finish();
                System.exit(0);

            }
        });
    }

    public void openLoadingWear() {
        Intent auxIntent = new Intent(this, WearService.class);
        auxIntent.setAction(WearService.ACTION_SEND.OPEN_LOADING.name());
        startService(auxIntent);
    }

    public void openHighscoresWear() {
        Intent auxIntent = new Intent(this, WearService.class);
        auxIntent.setAction(WearService.ACTION_SEND.OPEN_HIGHSCORES.name());
        startService(auxIntent);
    }
}
