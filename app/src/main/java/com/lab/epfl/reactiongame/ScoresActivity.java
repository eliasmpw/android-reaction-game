package com.lab.epfl.reactiongame;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ScoresActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    final List<String> nameList = new ArrayList<String>();
    final List<String> scoresList = new ArrayList<String>();
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference highScoresGetRef = database.getReference("highscores");
    private static DatabaseReference highScoresRef = highScoresGetRef.push();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        getScores();

    }

    private void getScores() {


        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference highScoresRef = rootref.child("highscores");
        DatabaseReference namesRef = highScoresRef.child("Game1");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                        String tmpName = userSnapshot.child("username").getValue(String.class);
                        nameList.add(tmpName);
                        String tmpScore = userSnapshot.child("score").getValue(String.class);
                        scoresList.add(tmpScore);
                    }
                Log.i(TAG, "THIS IS THE TAGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG" + nameList.toString());
                Log.i(TAG, "THIS IS THE TAGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG" + scoresList.toString());
                TextView first = findViewById(R.id.firstPlace);
                first.setText(nameList.get(0));
                TextView firstScore = findViewById(R.id.firstPlaceScore);
                firstScore.setText(scoresList.get(0));


                TextView second = findViewById(R.id.SecondPlace);
                second.setText(nameList.get(1));
                TextView secondScore = findViewById(R.id.SecondPlaceScore);
                secondScore.setText(scoresList.get(1));

                TextView third = findViewById(R.id.ThirdPlace);
                third.setText(nameList.get(2));
                TextView thirdScore = findViewById(R.id.ThirdPlaceScore);
                thirdScore.setText(scoresList.get(2));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        namesRef.addListenerForSingleValueEvent(valueEventListener);

    }
}
