package com.lab.epfl.reactiongame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class WinLoseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_lose);
        ImageView resultImage = findViewById(R.id.resultImage);

        long yourtime;
        yourtime = (long)getIntent().getSerializableExtra("yourTime");
        Boolean result;
        result = (Boolean)getIntent().getSerializableExtra("result");
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
    }

    public void Return2Main(View view){
        Intent intent = new Intent(WinLoseActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
