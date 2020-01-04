package com.lab.epfl.reactiongame;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ScoresActivity extends AppCompatActivity implements ScoreGame1Fragment.OnFragmentInteractionListener {
    private final String TAG = this.getClass().getSimpleName();
    final List<String> nameList = new ArrayList<String>();
    final List<String> scoresList = new ArrayList<String>();
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference highScoresGetRef = database.getReference("highscores");
    private static DatabaseReference highScoresRef = highScoresGetRef.push();


    private ScoreGame1Fragment myScoreGame1Fragment;
    private ScoreGame2Fragment myScoreGame2Fragment;
    private ScoreGame3Fragment myScoreGame3Fragment;
    private ScoreGame4Fragment myScoreGame4Fragment;
    private SectionsStatePagerAdapter mSectionStatePagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        mSectionStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        // Do this in case of detaching of Fragments
        myScoreGame1Fragment = new ScoreGame1Fragment();
        myScoreGame2Fragment = new ScoreGame2Fragment();
        myScoreGame3Fragment = new ScoreGame3Fragment();
        myScoreGame4Fragment = new ScoreGame4Fragment();

        ViewPager mViewPager = findViewById(R.id.mainViewPager);
        setUpViewPager(mViewPager);

        // Set NewRecordingFragment as default tab once started the activity
        mViewPager.setCurrentItem(mSectionStatePagerAdapter.getPositionByTitle(getString(R.string
                .tab_title_score_game_1)));


//        getScores();

    }

//    private void getScores() {
//
//
//        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference highScoresRef = rootref.child("highscores");
//        DatabaseReference namesRef = highScoresRef.child("Game1");
//
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
//                        String tmpName = userSnapshot.child("username").getValue(String.class);
//                        nameList4.add(tmpName);
//                        String tmpScore = userSnapshot.child("score").getValue(String.class);
//                        scoresList4.add(tmpScore);
//                    }
//                Log.i(TAG, "THIS IS THE TAGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG" + nameList4.toString());
//                Log.i(TAG, "THIS IS THE TAGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG" + scoresList4.toString());
//                TextView first = findViewById(R.id.firstPlace);
//                first.setText(nameList4.get(0));
//                TextView firstScore = findViewById(R.id.firstPlaceScore);
//                firstScore.setText(scoresList4.get(0));
//
//
//                TextView second = findViewById(R.id.SecondPlace);
//                second.setText(nameList4.get(1));
//                TextView secondScore = findViewById(R.id.SecondPlaceScore);
//                secondScore.setText(scoresList4.get(1));
//
//                TextView third = findViewById(R.id.ThirdPlace);
//                third.setText(nameList4.get(2));
//                TextView thirdScore = findViewById(R.id.ThirdPlaceScore);
//                thirdScore.setText(scoresList4.get(2));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        namesRef.addListenerForSingleValueEvent(valueEventListener);
//
//    }

    private void setUpViewPager(ViewPager mViewPager) {
        mSectionStatePagerAdapter.addFragment(myScoreGame1Fragment, getString(R.string
                .tab_title_score_game_1));
        mSectionStatePagerAdapter.addFragment(myScoreGame2Fragment, getString(R.string
                .tab_title_score_game_2));
        mSectionStatePagerAdapter.addFragment(myScoreGame3Fragment, getString(R.string
                .tab_title_score_game_3));
        mSectionStatePagerAdapter.addFragment(myScoreGame4Fragment, getString(R.string
                .tab_title_score_game_4));
        mViewPager.setAdapter(mSectionStatePagerAdapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        Intent auxIntent = new Intent(this, WearService.class);
        auxIntent.setAction(WearService.ACTION_SEND.CLOSE_HIGHSCORES.name());
        startService(auxIntent);
        super.onBackPressed();
    }
}
