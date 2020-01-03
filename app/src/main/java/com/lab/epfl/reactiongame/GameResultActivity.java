package com.lab.epfl.reactiongame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GameResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);
        Intent intent = getIntent();
        Boolean win = intent.getBooleanExtra("win", false);
        Boolean highscore = intent.getBooleanExtra("highscore", false);
        ImageView winImage = findViewById(R.id.winImage);
        ImageView highscoreImage = findViewById(R.id.highscoreImage);
        if (win) {
            winImage.setImageResource(R.drawable.youwin);
        } else {
            winImage.setImageResource(R.drawable.youlose);
        }
        if(highscore) {
            highscoreImage.setImageResource(R.drawable.highscore);
        } else {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    10
            );
            highscoreImage.setLayoutParams(param);
        }
    }

    public void closeThis(View view) {
        finish();
    }
}
