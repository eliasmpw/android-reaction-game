package com.lab.epfl.reactiongame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadingActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference gameInProgressGetRef = database.getReference("gameinprogress");
    private static DatabaseReference gameInProgressRef = gameInProgressGetRef.push();
    private DatabaseReference lookingforgameGetRef ;
    private DatabaseReference lookingforgameRef;
    private String userID;
    private String name;
    private int gameType;
    private HashMap<DatabaseReference, ValueEventListener> listenerHashMap = new HashMap<>();
    private HashMap<Query, ValueEventListener> queryHashMap = new HashMap<>();
    private String lookingforgame = "lookingforgame";

    public static void removeValueEventListener(HashMap<DatabaseReference, ValueEventListener> hashMap, HashMap<Query, ValueEventListener> secondHashMap) {
        for (Map.Entry<DatabaseReference, ValueEventListener> entry : hashMap.entrySet()) {
            DatabaseReference databaseReference = entry.getKey();
            ValueEventListener valueEventListener = entry.getValue();
            databaseReference.removeEventListener(valueEventListener);
        }
        for (Map.Entry<Query, ValueEventListener> entry : secondHashMap.entrySet()) {
            Query databaseReference = entry.getKey();
            ValueEventListener valueEventListener = entry.getValue();
            databaseReference.removeEventListener(valueEventListener);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        name = intent.getStringExtra("name");
        gameType = intent.getIntExtra("gameType",0);
        switch (gameType){
            case 0:
                lookingforgameGetRef = database.getReference("lookingforgame0");
                lookingforgameRef = lookingforgameGetRef.push();
                break;
            case 1:
                lookingforgameGetRef = database.getReference("lookingforgame1");
                lookingforgameRef = lookingforgameGetRef.push();
                break;
            case 2:
                lookingforgameGetRef = database.getReference("lookingforgame2");
                lookingforgameRef = lookingforgameGetRef.push();
                break;
            case 3:
                lookingforgameGetRef = database.getReference("lookingforgame3");
                lookingforgameRef = lookingforgameGetRef.push();
                break;

        }
        Log.e(TAG, "GAME TYPEEEE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + gameType);
        MatchMaking(userID, gameType);


    }






    private void MatchMaking(final String userID, final int gameType) {
        final List<String> Mylist = new ArrayList<String>();

        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference playersRef = rootref.child(lookingforgame + String.valueOf(gameType));

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                Log.e(TAG, "count = " + count);
                if (count < 1) {
                    Log.e(TAG, "less than ");
                    addNameToLookingForGameDB(userID);


                    // now we will be listening for changes on the database with my userID
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    Query userQuery = ref.child(lookingforgame + gameType).orderByChild("username").equalTo(userID);

                    ValueEventListener listener2 = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                                userSnapshot.getRef().removeValue();
                                Log.e(TAG, "INSIDE OF DATACHANGE FOR lookingforgame WITH USERID !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
                                Log.e(TAG, "this is the id of the user" + dataSnapshot.getValue());


                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                Query test = ref.child("gameinprogress").orderByChild("username1").equalTo(userID);

                                ValueEventListener auxListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                            String gameID = "";
                                            Iterable<DataSnapshot> auxChildren = dataSnapshot.getChildren();
                                            for (DataSnapshot child : auxChildren) {
                                                gameID = child.getKey();
                                            }

                                            Log.e(TAG, "this is the id of the gameinprogress" + gameID);
                                            Log.e(TAG, "INSIDE OF gameinprogress FOR GAME IN PROGRESS WITH USERID !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
                                            Log.e(TAG, "this is the id of the gameinprogress" + dataSnapshot.getValue());


                                            Intent gameIntent;
                                            if (gameType < 3) {
                                                gameIntent = new Intent(LoadingActivity.this, GameChooseActivity.class);
                                                openGameChooseWear();
                                            } else {
                                                gameIntent = new Intent(LoadingActivity.this, GameFourActivity.class);
                                            }
                                            gameIntent.putExtra("NumPlayer", 1);
                                            gameIntent.putExtra("userID", userID) ;
                                            gameIntent.putExtra("name", name) ;
                                            gameIntent.putExtra("gameID", gameID);
                                            gameIntent.putExtra("gameType", gameType);
                                            LoadingActivity.this.startActivity(gameIntent);
                                            removeValueEventListener(listenerHashMap, queryHashMap);
                                            LoadingActivity.this.finish();

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e(TAG, "onCancelled", databaseError.toException());

                                    }
                                };

                                test.addValueEventListener(auxListener);
                                queryHashMap.put(test, auxListener);


                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled", databaseError.toException());

                        }
                    };
                    userQuery.addValueEventListener(listener2);
                    queryHashMap.put(userQuery, listener2);

                } else {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String tmp = userSnapshot.child("username").getValue(String.class);
                        Mylist.add(tmp);
                    }
                    Log.i(TAG, "MORE THAN ONE USER IN THE TABLE" + Mylist.toString());
                    // Adding the first user to the data base
                    addNameToGameInProgressDB(Mylist.get(0), userID);
                    removeNameToLookingForGameDB(userID, lookingforgame + gameType);
                    removeNameToLookingForGameDB(Mylist.get(0), lookingforgame + gameType);
                    Mylist.remove(0);
                    Log.e(TAG, Mylist.toString());
                    // now we will be listening for changes on the database with my userID
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    Query userQuery = ref.child("gameinprogress").orderByChild("username2").equalTo(userID);

                    ValueEventListener auxQuery = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String gameID = "";
                                Iterable<DataSnapshot> auxChildren = dataSnapshot.getChildren();
                                for (DataSnapshot child : auxChildren) {
                                    gameID = child.getKey();
                                }

                                Log.e(TAG, "this is the id of the gameinprogress" + gameID);
                                Log.e(TAG, "INSIDE OF gameinprogress FOR GAME IN PROGRESS WITH USERID !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
                                Log.e(TAG, "this is the id of the gameinprogress" + dataSnapshot.getValue());

                                Intent gameIntent;

                                if (gameType < 3) {
                                    gameIntent = new Intent(LoadingActivity.this, GameChooseActivity.class);
                                    openGameChooseWear();
                                } else {
                                    gameIntent = new Intent(LoadingActivity.this, GameFourActivity.class);
                                }
                                gameIntent.putExtra("NumPlayer", 2);
                                gameIntent.putExtra("userID", userID);
                                gameIntent.putExtra("name", name);
                                gameIntent.putExtra("gameID", gameID);
                                gameIntent.putExtra("gameType", gameType);
                                LoadingActivity.this.startActivity(gameIntent);
                                removeValueEventListener(listenerHashMap, queryHashMap);
                                LoadingActivity.this.finish();

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled", databaseError.toException());

                        }
                    };
                    userQuery.addValueEventListener(auxQuery);
                    queryHashMap.put(userQuery, auxQuery);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        playersRef.addListenerForSingleValueEvent(valueEventListener);
        listenerHashMap.put(playersRef, valueEventListener);


    }

    private void removeNameToLookingForGameDB(final String userID, final String gameID) {
//        HashMap<String, String> user = session.getUserDetails();
//        final String userID = user.get(SessionManager.KEY_NAME);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query userQuery = ref.child(lookingforgame + gameType).orderByChild("username").equalTo(userID);
        ValueEventListener auxListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    userSnapshot.getRef().removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());

            }
        };
        userQuery.addListenerForSingleValueEvent(auxListener);
        queryHashMap.put(userQuery, auxListener);

    }


    private void addNameToGameInProgressDB(final String userID_1, final String userID_2) {
        gameInProgressRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                mutableData.child("username1").setValue(userID_1);
                mutableData.child("username2").setValue(userID_2);
                mutableData.child("gameType").setValue(gameType);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });


    }

    private void addNameToLookingForGameDB(final String userID) {
//        private void addNameToLookingForGameDB() {
//        HashMap<String, String> user = session.getUserDetails();
//        final String userID = user.get(SessionManager.KEY_NAME);
        lookingforgameRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                mutableData.child("username").setValue(userID);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });

//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mDatabase.child("lookingforgame").child("username").setValue(userID);
    }

    private long backPressedTime = 0;    // used by onBackPressed()


    @Override
    public void onBackPressed() {        // to prevent irritating accidental logouts
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            Toast.makeText(this, "Press back again to exit queue",
                    Toast.LENGTH_SHORT).show();
        } else {    // this guy is serious
            // clean up
            removeNameToLookingForGameDB(userID, lookingforgame + gameType);
            removeValueEventListener(listenerHashMap, queryHashMap);
            closeLoadingWear();
            super.onBackPressed();       // bye
        }
    }

    public void openGameChooseWear() {
        Intent auxIntent = new Intent(this, WearService.class);
        auxIntent.setAction(WearService.ACTION_SEND.OPEN_GAMECHOOSE.name());
        startService(auxIntent);
    }

    public void closeLoadingWear() {
        Intent auxIntent = new Intent(this, WearService.class);
        auxIntent.setAction(WearService.ACTION_SEND.CLOSE_LOADING.name());
        startService(auxIntent);
    }
}
