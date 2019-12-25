package com.lab.epfl.reactiongame;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference lookingforgameGetRef = database.getReference("lookingforgame");
    private static DatabaseReference lookingforgameRef = lookingforgameGetRef.push();
    SessionManager session;
    private Profile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        final String userID = user.get(SessionManager.KEY_NAME);
        Log.v(TAG, userID);

        Button playButton = findViewById(R.id.PlayButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CLICKED PLAY BUTTON!!!!");
                Log.v(TAG, userID);
                addNameToLookingForGameDB();
            }
        });


        Button logoutButton = findViewById(R.id.LogoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CLICKED logoutButton BUTTON!!!!");
                session.logoutUser();
            }
        });

        Button exitButton = findViewById(R.id.ExitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "CLICKED exitButton BUTTON!!!!");
                Log.v(TAG, userID);
                removeNameToLookingForGameDB();

            }
        });
    }

    private void addNameToLookingForGameDB() {
        HashMap<String, String> user = session.getUserDetails();
        final String userID = user.get(SessionManager.KEY_NAME);
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
    }

    private void removeNameToLookingForGameDB() {
        HashMap<String, String> user = session.getUserDetails();
        final String userID = user.get(SessionManager.KEY_NAME);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query userQuery = ref.child("lookingforgame").orderByChild("username").equalTo(userID);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    userSnapshot.getRef().removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,"onCancelled", databaseError.toException());

            }
        });
    }

}
