package com.lab.epfl.reactiongame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class GameResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);
        Intent intent = getIntent();
        Boolean win = intent.getBooleanExtra("win", false);
        Boolean highscore = intent.getBooleanExtra("highscore", false);
        Long myTime = intent.getLongExtra("time", 0);
        ImageView winImage = findViewById(R.id.winImage);
        ImageView highscoreImage = findViewById(R.id.highscoreImage);
        TextView scoreGot = findViewById(R.id.ScoreGot);
        String totalTimeText = "Your total time is: " + formatTimeMilliseconds(myTime) + "seconds";
        scoreGot.setText(totalTimeText);
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
                    1
            );
            winImage.setLayoutParams(param);
        }
    }

    public void closeThis(View view) {
        finish();
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

    @Override
    public void onBackPressed() {
        Intent auxIntent = new Intent(this, WearService.class);
        auxIntent.setAction(WearService.ACTION_SEND.CLOSE_GAMERESULT.name());
        startService(auxIntent);
        super.onBackPressed();
    }
}
