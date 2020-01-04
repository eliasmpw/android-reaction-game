package com.lab.epfl.reactiongame;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WinLoseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_lose);
        ImageView resultImage = findViewById(R.id.resultImage);
        TextView ifBestResult = findViewById(R.id.ifbestresult);

        long yourtime;
        yourtime = (long)getIntent().getSerializableExtra("yourTime");
        Boolean result;
        result = (Boolean)getIntent().getSerializableExtra("result");
        int ifbest;
        ifbest = (int)getIntent().getSerializableExtra("ifbest");
        TextView textView = findViewById(R.id.game4result);
        if(result == false) {
            resultImage.setImageDrawable(getDrawable(R.drawable.loseimage));
            if (yourtime == -1)
                textView.setText("You make wrong choice...\nYou LOSE :(");
            else
                textView.setText("Your react time is " + (int) yourtime + " ms!\nYou LOSE :(");
        }
        else {
            resultImage.setImageDrawable(getDrawable(R.drawable.winimage));
            textView.setText("Your react time is " + (int) yourtime + " ms!\nYou WIN :)");
        }
        if (ifbest == 0) {
            ifBestResult.setText("");
        }
    }
}
