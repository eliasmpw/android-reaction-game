package com.lab.epfl.reactiongame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        final String gameID = intent.getStringExtra("gameID");
        final String userID = intent.getStringExtra("userID");
        final String numPlayer = intent.getStringExtra("NumPlayer");
        final int gameType = intent.getIntExtra("gameType",0);

        TextView text1 = findViewById(R.id.textView1);
        text1.setText("gameID" + gameID);
        TextView text2 = findViewById(R.id.textView2);
        text2.setText("userID" +userID);
        TextView text3 = findViewById(R.id.textView3);
        text3.setText("numPlayer" +numPlayer);
        TextView text4 = findViewById(R.id.textView4);
        text4.setText("gameType" + gameType);
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
