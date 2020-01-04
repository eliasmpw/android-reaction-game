package com.lab.epfl.reactiongame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private static final int REGISTER_PROFILE = 1;

    private Profile userProfile = null;
    private String userID;
    SessionManager session;

    private HashMap<DatabaseReference, ValueEventListener> listenerHashMap = new HashMap<>();
    public static void removeValueEventListener(HashMap<DatabaseReference, ValueEventListener> hashMap) {
        for (Map.Entry<DatabaseReference, ValueEventListener> entry : hashMap.entrySet()) {
            DatabaseReference databaseReference = entry.getKey();
            ValueEventListener valueEventListener = entry.getValue();
            databaseReference.removeEventListener(valueEventListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        Log.v(TAG, "Hello world!");

        Button rButton = findViewById(R.id.RegisterButton);
        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEditProfile = new Intent(LoginActivity.this, EditProfileActivity
                        .class);
                if (userProfile != null) {
                    intentEditProfile.putExtra(MyProfileFragment.USER_PROFILE, userProfile);
                }
                startActivityForResult(intentEditProfile, REGISTER_PROFILE);
            }
        });

        session = new SessionManager(getApplicationContext());
        checkLogin(session);
    }

    public void clickedLoginButtonXmlCallback(View view) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference profileRef = database.getReference("profiles");

        final TextView mTextView = findViewById(R.id.LoginMessage);
        final String usernameInput = ((EditText) findViewById(R.id.username))
                .getText().toString();
        final String passwordInput = ((EditText) findViewById(R.id.password))
                .getText().toString();

        session = new SessionManager(getApplicationContext());
        final ValueEventListener auxListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean notMember = true;
                for (final DataSnapshot user : dataSnapshot.getChildren()) {
                    String usernameDatabase = user.child("username")
                            .getValue(String.class);
                    String passwordDatabase = user.child("password")
                            .getValue(String.class);
                    if (usernameInput.equals(usernameDatabase)
                            && passwordInput.equals(passwordDatabase)) {
                        userID = user.getKey();
                        notMember = false;
                        break;
                    }
                }
                if (notMember) {
                    mTextView.setText(R.string.not_registered_yet);
                    mTextView.setTextColor(Color.RED);
                    removeValueEventListener(listenerHashMap);
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(MyProfileFragment.USER_ID, userID);
                    session.createLoginSession(userID, usernameInput);
                    startActivity(intent);
                    removeValueEventListener(listenerHashMap);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        profileRef.addValueEventListener(auxListener);
        listenerHashMap.put(profileRef, auxListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REGISTER_PROFILE && resultCode == RESULT_OK && data != null) {
            userProfile = (Profile) data.getSerializableExtra(MyProfileFragment.USER_PROFILE);
            if (userProfile != null) {
                TextView username = findViewById(R.id.username);
                username.setText(userProfile.username);
                TextView password = findViewById(R.id.password);
                password.setText(userProfile.password);
            }
        }
    }


    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(SessionManager session){
        Log.e(TAG, "checking");
        // Check login status
        if(session.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            removeValueEventListener(listenerHashMap);
            finish();
        }

    }
}
