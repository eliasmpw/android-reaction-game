package com.lab.epfl.reactiongame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void ClickedStartGame4(View view){
        Intent intent = new Intent(MainActivity.this,game4Activity.class);
        startActivity(intent);
        finish();
    }
}
