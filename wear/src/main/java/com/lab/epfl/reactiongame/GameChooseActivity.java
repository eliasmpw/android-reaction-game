package com.lab.epfl.reactiongame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.HashMap;

public class GameChooseActivity extends WearableActivity {
    private final String TAG = this.getClass().getSimpleName();
    public static final String
            BROADCAST_GAMERESULT =
            "BROADCAST_GAMERESULT";
    public static final String
            BROADCAST_CLOSEGAMECHOOSE =
            "BROADCAST_CLOSEGAMECHOOSE";
    public static final String
            BROADCAST_UPDATEDATA =
            "BROADCAST_UPDATEDATA";


    private ArrayList<Integer> fieldIDs = new ArrayList<Integer>(){
        {
            add(R.id.Field0);
            add(R.id.Field1);
            add(R.id.Field2);
            add(R.id.Field3);
            add(R.id.Field4);
            add(R.id.Field5);
            add(R.id.Field6);
            add(R.id.Field7);
            add(R.id.Field8);
        }
    };
    private ArrayList<Button> allButtons = new ArrayList<>();
    private HashMap<String, Integer> mapAnimals = new HashMap<>();
    private HashMap<String, Integer> mapColors = new HashMap<>();
    private int gameType;


    private BroadcastReceiver gameResultBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean winner = intent.getBooleanExtra("isWinner", false);
            Intent auxIntent = new Intent(GameChooseActivity.this, GameResult.class);
            auxIntent.putExtra("winner", winner);
            startActivity(auxIntent);
            finish();
        }
    };

    private BroadcastReceiver closeBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private BroadcastReceiver updateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String color, text, image;
            Button auxButton;
            if (gameType == -1) {
                color = intent.getStringExtra("color" + 0);
                text = intent.getStringExtra("text" + 0);
                image = intent.getStringExtra("image" + 0);
//                Log.e("OOOH++", "-"+color + "-"+text + "-" + image+"-");
                if (!image.equals("")) {
                    gameType = 2;
                } else if(!color.equals("")) {
                    gameType = 1;
                } else {
                    gameType = 0;
                }
            }
            for (int i = 0; i < 9; i++) {
                color = intent.getStringExtra("color" + i);
                text = intent.getStringExtra("text" + i);
                image = intent.getStringExtra("image" + i);
                auxButton = allButtons.get(i);
//                Log.e("OOOH++", color + "-"+text + "-" + image);
                switch (gameType) {
                    case 0:
                        auxButton.setText(text);
                        auxButton.setClickable(true);
                        break;
                    case 1:
                        auxButton.setText(text);
                        auxButton.setTextColor(mapColors.get(color));
                        auxButton.setClickable(true);
                        break;
                    case 2:
                        auxButton.setText("");
                        auxButton.setBackgroundResource(mapAnimals.get(image));
                        auxButton.setClickable(true);
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_choose);
        initializeVariables();

        // Enables Always-on
        setAmbientEnabled();
    }

    private void initializeVariables() {
        gameType = -1;
        mapAnimals.put("Antelope", getResources().getIdentifier("animalsantelope", "drawable", getPackageName()));
        mapAnimals.put("Bat", getResources().getIdentifier("animalsbat", "drawable", getPackageName()));
        mapAnimals.put("Bear", getResources().getIdentifier("animalsbear", "drawable", getPackageName()));
        mapAnimals.put("Camel", getResources().getIdentifier("animalscamel", "drawable", getPackageName()));
        mapAnimals.put("Chameleon", getResources().getIdentifier("animalschameleon", "drawable", getPackageName()));
        mapAnimals.put("Cheetah", getResources().getIdentifier("animalscheetah", "drawable", getPackageName()));
        mapAnimals.put("Crocodile", getResources().getIdentifier("animalscrocodile", "drawable", getPackageName()));
        mapAnimals.put("Deer", getResources().getIdentifier("animalsdeer", "drawable", getPackageName()));
        mapAnimals.put("Dolphin", getResources().getIdentifier("animalsdolphin", "drawable", getPackageName()));
        mapAnimals.put("Elephant", getResources().getIdentifier("animalselephant", "drawable", getPackageName()));
        mapAnimals.put("Fox", getResources().getIdentifier("animalsfox", "drawable", getPackageName()));
        mapAnimals.put("Giraffe", getResources().getIdentifier("animalsgiraffe", "drawable", getPackageName()));
        mapAnimals.put("Gorilla", getResources().getIdentifier("animalsgorilla", "drawable", getPackageName()));
        mapAnimals.put("Hippo", getResources().getIdentifier("animalshippo", "drawable", getPackageName()));
        mapAnimals.put("Kangaroo", getResources().getIdentifier("animalskangaroo", "drawable", getPackageName()));
        mapAnimals.put("Koala", getResources().getIdentifier("animalskoala", "drawable", getPackageName()));
        mapAnimals.put("Lion", getResources().getIdentifier("animalslion", "drawable", getPackageName()));
        mapAnimals.put("Lizard", getResources().getIdentifier("animalslizard", "drawable", getPackageName()));
        mapAnimals.put("Llama", getResources().getIdentifier("animalsllama", "drawable", getPackageName()));
        mapAnimals.put("Monkey", getResources().getIdentifier("animalsmonkey", "drawable", getPackageName()));
        mapAnimals.put("Mouse", getResources().getIdentifier("animalsmouse", "drawable", getPackageName()));
        mapAnimals.put("Panda", getResources().getIdentifier("animalspanda", "drawable", getPackageName()));
        mapAnimals.put("Panther", getResources().getIdentifier("animalspanther", "drawable", getPackageName()));
        mapAnimals.put("Polar bear", getResources().getIdentifier("animalspolarbear", "drawable", getPackageName()));
        mapAnimals.put("Rhino", getResources().getIdentifier("animalsrhino", "drawable", getPackageName()));
        mapAnimals.put("Snake", getResources().getIdentifier("animalssnake", "drawable", getPackageName()));
        mapAnimals.put("Tiger", getResources().getIdentifier("animalstiger", "drawable", getPackageName()));
        mapAnimals.put("Wolf", getResources().getIdentifier("animalswolf", "drawable", getPackageName()));
        mapAnimals.put("Zebra", getResources().getIdentifier("animalszebra", "drawable", getPackageName()));
        mapColors.put("Red", Color.RED);
        mapColors.put("Light Green", Color.rgb(0, 255, 0));
        mapColors.put("Dark Green", Color.rgb(0, 120, 0));
        mapColors.put("Yellow", Color.YELLOW);
        mapColors.put("Light Blue", Color.rgb(153, 255, 255));
        mapColors.put("Dark Blue", Color.rgb(0, 0, 255));
        mapColors.put("Black", Color.BLACK);
        mapColors.put("Orange", Color.rgb(255, 153, 51));
        mapColors.put("Brown", Color.rgb(153, 76, 0));
        mapColors.put("Pink", Color.rgb(255, 153, 204));
        mapColors.put("Purple", Color.rgb(73, 0, 153));
        mapColors.put("Gray", Color.rgb(160, 160, 160));
        allButtons.add((Button) findViewById(R.id.Field0));
        allButtons.add((Button) findViewById(R.id.Field1));
        allButtons.add((Button) findViewById(R.id.Field2));
        allButtons.add((Button) findViewById(R.id.Field3));
        allButtons.add((Button) findViewById(R.id.Field4));
        allButtons.add((Button) findViewById(R.id.Field5));
        allButtons.add((Button) findViewById(R.id.Field6));
        allButtons.add((Button) findViewById(R.id.Field7));
        allButtons.add((Button) findViewById(R.id.Field8));
    }

    public void selectField(View view) {
        Button field = (Button) view;
        int id = field.getId();
        int position = fieldIDs.indexOf(id);
        Log.e(TAG, "selected: " + position);
        Intent auxIntent = new Intent(this, WearService.class);
        auxIntent.setAction(WearService.ACTION_SEND.SELECT_OPTION.name());
        auxIntent.putExtra("indexOption", Integer.toString(position));
        startService(auxIntent);

        for (Button auxButton: allButtons) {
            auxButton.setBackgroundResource(android.R.color.white);
            auxButton.setText("?");
            auxButton.setTextColor(Color.BLACK);
            auxButton.setClickable(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(gameResultBroadcastReceiver, new IntentFilter(
                        BROADCAST_GAMERESULT));
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(closeBroadcastReceiver, new IntentFilter(
                        BROADCAST_CLOSEGAMECHOOSE));
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(updateBroadcastReceiver, new IntentFilter(
                        BROADCAST_UPDATEDATA));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Un-register broadcasts from WearService
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(gameResultBroadcastReceiver);
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(closeBroadcastReceiver);
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(updateBroadcastReceiver);
    }
}
