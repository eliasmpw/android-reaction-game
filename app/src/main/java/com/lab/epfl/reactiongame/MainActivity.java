package com.lab.epfl.reactiongame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private String newGameId;
    SessionManager session;
//    private Profile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent intent = getIntent();
//        userProfile = (Profile) intent.getSerializableExtra("userProfileWelcome");
//        sendProfileToWatch();
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        final String userID = user.get(SessionManager.KEY_NAME);
        Log.v(TAG, userID);
        Button playButton1 = findViewById(R.id.PlayButton1);
        playButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CLICKED PLAY BUTTON!!!!");
                Log.v(TAG, userID);
                Intent loadingIntent = new Intent(MainActivity.this, GameChooseActivity.class);
                MainActivity.this.startActivity(loadingIntent);
            }
        });
        Button playButton2 = findViewById(R.id.PlayButton2);
        playButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CLICKED PLAY BUTTON!!!!");
                Log.v(TAG, userID);
                addNameToLookingForGameDB(userID);
                Intent loadingIntent = new Intent(MainActivity.this, LoadingActivity.class);
                MainActivity.this.startActivity(loadingIntent);
                MatchMaking();
                MainActivity.this.finish();
            }
        });
        Button playButton3 = findViewById(R.id.PlayButton3);
        playButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CLICKED PLAY BUTTON!!!!");
                Log.v(TAG, userID);
                addNameToLookingForGameDB(userID);
                Intent loadingIntent = new Intent(MainActivity.this, LoadingActivity.class);
                MainActivity.this.startActivity(loadingIntent);
                MatchMaking();
                MainActivity.this.finish();
            }
        });
        Button playButton4 = findViewById(R.id.PlayButton4);
        playButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CLICKED PLAY BUTTON!!!!");
                Log.v(TAG, userID);
                //addNameToLookingForGameDB(userID);
                Intent loadingIntent = new Intent(MainActivity.this, GameFourActivity.class);
                MainActivity.this.startActivity(loadingIntent);
                //MatchMaking();
                //MainActivity.this.finish();
            }
        });

        Button scoresButton = findViewById(R.id.scoresButton);
        scoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, userID);
                Intent scoresIntent = new Intent(MainActivity.this, ScoresActivity.class);
                MainActivity.this.startActivity(scoresIntent);
                MatchMaking();
                MainActivity.this.finish();
            }
        });


        Button logoutButton = findViewById(R.id.LogoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CLICKED logoutButton BUTTON!!!!");
                session.logoutUser();
            }
        });

        Button exitButton = findViewById(R.id.ExitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CLICKED exitButton BUTTON!!!!");
                Log.v(TAG, userID);
//                removeNameToLookingForGameDB(userID);
                CreateHighScoreDB(userID);
//                finish();
//                System.exit(0);

            }
        });
    }

    private void addNameToLookingForGameDB(final String userID) {
//        private void addNameToLookingForGameDB() {
//        HashMap<String, String> user = session.getUserDetails();
//        final String userID = user.get(SessionManager.KEY_NAME);
        lookingforgameRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                mutableData.child("username").setValue(userID);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });

//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mDatabase.child("lookingforgame").child("username").setValue(userID);
    }

    private void removeNameToLookingForGameDB(final String userID) {
//        HashMap<String, String> user = session.getUserDetails();
//        final String userID = user.get(SessionManager.KEY_NAME);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query userQuery = ref.child("lookingforgame").orderByChild("username").equalTo(userID);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    userSnapshot.getRef().removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,"onCancelled", databaseError.toException());

            }
        });
    }


    private void addNameToGameInProgressDB(final String userID_1,final String userID_2) {
        gameInProgressRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                mutableData.child("username1").setValue(userID_1);
                mutableData.child("username2").setValue(userID_2);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                Log.e(TAG, dataSnapshot.toString());
                newGameId = dataSnapshot.getKey();
            }
        });


    }

    private void MatchMaking() {
        final List<String> Mylist = new ArrayList<String>();

        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference playersRef = rootref.child("lookingforgame");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                Log.e(TAG, "count = " + count);
                if (count >= 2){
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                        String tmp = userSnapshot.child("username").getValue(String.class);
                        Mylist.add(tmp);
                    }
                    Log.e(TAG, Mylist.toString());
                    // Adding the first user to the data base
                    addNameToGameInProgressDB(Mylist.get(0),Mylist.get(1));
                    removeNameToLookingForGameDB(Mylist.get(0));
                    removeNameToLookingForGameDB(Mylist.get(1));
                    Mylist.remove(0);
                    Mylist.remove(0);
                    Log.e(TAG, Mylist.toString());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        playersRef.addListenerForSingleValueEvent(valueEventListener);

    }
    private void sendProfileToWatch() {
        Intent intentWear = new Intent(MainActivity.this,WearService.class);
        intentWear.setAction(WearService.ACTION_SEND.PROFILE_SEND.name());
        intentWear.putExtra(WearService.PROFILE, userProfile);
        startService(intentWear);
    }


    private void CreateHighScoreDB(final String userID_1) {
        highScoresRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                mutableData.child("userID").setValue(userID_1);
                mutableData.child("username").setValue("Jhon");
                mutableData.child("score").setValue("123");
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }
}
