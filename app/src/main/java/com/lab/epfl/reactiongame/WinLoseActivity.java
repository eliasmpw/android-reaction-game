package com.lab.epfl.reactiongame;

import android.content.Intent;
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


        // send result to wear
        String resultString = "00";
        if(result && ifbest == 1)
            resultString = "11";
        else if (result && ifbest == 0)
            resultString = "10";
        else if (!result == false && ifbest == 1)
            resultString = "01";
        else if (!result == false && ifbest == 0)
            resultString = "00";
        Intent auxIntent = new Intent(WinLoseActivity.this, WearService.class);
        auxIntent.putExtra("result",resultString);
        auxIntent.setAction(WearService.ACTION_SEND.GAME4_RESULTSHOW.name());
        startService(auxIntent);

        if(result == false) {
            resultImage.setImageDrawable(getDrawable(R.drawable.loseimage));
            if (yourtime == 9999)
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
        else{
            ifBestResult.setText("High Score!");
        }
    }
    @Override
    public void onBackPressed() {
        Intent auxIntent = new Intent(this, WearService.class);
        auxIntent.setAction(WearService.ACTION_SEND.CLOSE_GAME4RESULT.name());
        startService(auxIntent);
        super.onBackPressed();
    }
}
