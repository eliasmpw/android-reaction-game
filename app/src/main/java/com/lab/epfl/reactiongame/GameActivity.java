package com.lab.epfl.reactiongame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String userID = intent.getStringExtra("userID");
        final int gameType = intent.getIntExtra("gameType",0);

//        TextView text1 = findViewById(R.id.textView1);
//        text1.setText("gameID" + gameID);
//        TextView text2 = findViewById(R.id.textView2);
//        text2.setText("userID" +userID);
//        TextView text3 = findViewById(R.id.textView3);
//        text3.setText("numPlayer" +numPlayer);
//        TextView text4 = findViewById(R.id.textView4);
//        text4.setText("gameType" + gameType);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final long score = 1;

        final DatabaseReference highscoreRef;
        highscoreRef = database.getReference("highscores/Game" + (gameType + 1));

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GameHighscores highScores = dataSnapshot.getValue(GameHighscores.class);
                if (score < Long.parseLong(highScores.user1.score)) {
                    highScores.user5 = highScores.user4;
                    highScores.user4 = highScores.user3;
                    highScores.user3 = highScores.user2;
                    highScores.user2 = highScores.user1;
                    highScores.user1 = new SingleScore(Long.toString(score), userID, name);
                    highscoreRef.setValue(highScores);
                } else if (score < Long.parseLong(highScores.user2.score)) {
                    highScores.user5 = highScores.user4;
                    highScores.user4 = highScores.user3;
                    highScores.user3 = highScores.user2;
                    highScores.user2 = new SingleScore(Long.toString(score), userID, name);
                    highscoreRef.setValue(highScores);
                } else if (score < Long.parseLong(highScores.user3.score)) {
                    highScores.user5 = highScores.user4;
                    highScores.user4 = highScores.user3;
                    highScores.user3 = new SingleScore(Long.toString(score), userID, name);
                    highscoreRef.setValue(highScores);
                } else if (score < Long.parseLong(highScores.user4.score)) {
                    highScores.user5 = highScores.user4;
                    highScores.user4 = new SingleScore(Long.toString(score), userID, name);
                    highscoreRef.setValue(highScores);
                } else if (score < Long.parseLong(highScores.user5.score)) {
                    highScores.user5 = new SingleScore(Long.toString(score), userID, name);
                    highscoreRef.setValue(highScores);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        highscoreRef.addListenerForSingleValueEvent(valueEventListener);

    }
    private long backPressedTime = 0;    // used by onBackPressed()
    @Override
    public void onBackPressed() {        // to prevent irritating accidental logouts
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            Toast.makeText(this, "Press back again to exit",
                    Toast.LENGTH_SHORT).show();
        } else {    // this guy is serious
            // clean up
            GameActivity.this.finish();
            super.onBackPressed();       // bye
        }
    }
}
