package com.lab.epfl.reactiongame;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference profileGetRef = database.getReference("profiles");
    private static DatabaseReference profileRef = profileGetRef.push();

    private static final int PICK_IMAGE = 1;
    private File imageFile;
    private Profile userProfile;
    private String userID;
    private Uri savedImageUri;
    private boolean tmp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            userID = intent.getExtras().getString(MyProfileFragment.USER_ID);
            fetchDataFromFirebase();
        }

        if (savedInstanceState != null) {
            savedImageUri = savedInstanceState.getParcelable("ImageUri");
            if (savedImageUri != null) {
                try {
                    InputStream imageStream = getContentResolver().openInputStream(savedImageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    ImageView imageView = findViewById(R.id.userImage);
                    imageView.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ImageUri", savedImageUri);
    }

    private void fetchDataFromFirebase() {
        final TextView usernameTextView = findViewById(R.id.editUsername);
        final TextView passwordTextView = findViewById(R.id.editPassword);

        profileGetRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user_db = dataSnapshot.child("username").getValue(String.class);
                String password_db = dataSnapshot.child("password").getValue(String.class);
                String photo = dataSnapshot.child("photo").getValue(String.class);

                usernameTextView.setText(user_db);
                passwordTextView.setText(password_db);

                //  Reference to an image file in Firebase Storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl
                        (photo);
                storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        final Bitmap selectedImage = BitmapFactory.decodeByteArray(bytes, 0,
                                bytes.length);
                        ImageView imageView = findViewById(R.id.userImage);
                        imageView.setImageBitmap(selectedImage);
                    }
                });


                profileRef = profileGetRef.child(userID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final TextView usernameTextView = findViewById(R.id.editUsername);
        switch (item.getItemId()) {
            case R.id.action_validate:
                editUser();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("profiles").orderByChild("username").equalTo(usernameTextView.getText().toString()).addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Log.i(TAG, "dataSnapshot value = " + dataSnapshot.getValue());

                                if (dataSnapshot.exists()) {

                                    // User Exists
                                    // Do your stuff here if user already exists
                                    Toast.makeText(getApplicationContext(), "Username already exists. Please try other username.", Toast.LENGTH_SHORT).show();

                                } else {

                                    // User Not Yet Exists
                                    // Do your stuff here if user not yet exists
                                    addProfileToFirebaseDB();

                                }
                            }
                            @Override
                            public void onCancelled (DatabaseError databaseError){

                            }
                        }

                );

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addProfileToFirebaseDB() {
        final TextView userName = findViewById(R.id.editUsername);
        final TextView userPassword = findViewById(R.id.editPassword);

        BitmapDrawable bitmapDrawable = (BitmapDrawable) ((ImageView) findViewById(R.id
                .userImage)).getDrawable();

        if (bitmapDrawable == null) {
            Toast.makeText(this, R.string.missing_picture, Toast.LENGTH_SHORT).show();
            return;
        }

        if (userName.getText().toString().equals("")) {
            Toast.makeText(this, R.string.missing_username, Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPassword.getText().toString().equals("")) {
            Toast.makeText(this, R.string.missing_password, Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference photoRef = storageRef.child("photos").child(profileRef.getKey() + ".jpg");
        UploadTask uploadTask = photoRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(EditProfileActivity.this, R.string.photo_upload_failed, Toast
                        .LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new PhotoUploadSuccessListener());
    }

    private void clearUser() {
        ImageView userImageView = findViewById(R.id.userImage);
        TextView usernameTextView = findViewById(R.id.editUsername);
        TextView passwordTextView = findViewById(R.id.editPassword);

        userImageView.setImageDrawable(null);
        usernameTextView.setText("");
        passwordTextView.setText("");
    }

    private void setProfileToEdit() {
        ImageView userImageView = findViewById(R.id.userImage);
        TextView usernameTextView = findViewById(R.id.editUsername);
        TextView passwordTextView = findViewById(R.id.editPassword);

        final InputStream imageStream;
        try {
            imageStream = new FileInputStream(userProfile.photoPath);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            userImageView.setImageBitmap(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        usernameTextView.setText(userProfile.username);
        passwordTextView.setText(userProfile.password);
    }

    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select " + "Picture"), PICK_IMAGE);
    }

    private void editUser() {
        TextView username = findViewById(R.id.editUsername);
        TextView password = findViewById(R.id.editPassword);
        userProfile = new Profile(username.getText().toString(), password.getText().toString());


        if (imageFile == null) {
            userProfile.photoPath = "";
        } else {
            userProfile.photoPath = imageFile.getPath();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            imageFile = new File(getExternalFilesDir(null), "profileImage");
            try {
                copyImage(imageUri, imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final InputStream imageStream;
            try {
                savedImageUri = Uri.fromFile(imageFile);
                imageStream = getContentResolver().openInputStream(Uri.fromFile(imageFile));
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ImageView imageView = findViewById(R.id.userImage);
                imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyImage(Uri uriInput, File fileOutput) throws IOException {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = getContentResolver().openInputStream(uriInput);
            out = new FileOutputStream(fileOutput);
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
            out.close();
        }
    }


    private class ProfileDataUploadHandler implements Transaction.Handler {
        @NonNull
        @Override
        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
            mutableData.child("username").setValue(userProfile.username);
            mutableData.child("password").setValue(userProfile.password);
            mutableData.child("photo").setValue(userProfile.photoPath);
            return Transaction.success(mutableData);
        }

        @Override
        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable
                DataSnapshot dataSnapshot) {
            if (b) {
                Toast.makeText(EditProfileActivity.this, R.string.registration_success, Toast
                        .LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra(MyProfileFragment.USER_PROFILE, userProfile);
                setResult(AppCompatActivity.RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(EditProfileActivity.this, R.string.registration_failed, Toast
                        .LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkForDuplicate(final String usernameToCheck) {
        // Function to verify if the user with the same name is registered already
        final List<String> userNamesRegistered = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query userQuery = ref.child("profiles").orderByChild("username").equalTo(userProfile.username);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Log.e(TAG, data.child("username").getValue() + " " + usernameToCheck);
                    userNamesRegistered.add((String) data.child("username").getValue());
                    Log.i(TAG, String.valueOf(userNamesRegistered));
                    if (data.child("username").getValue().equals(usernameToCheck)) {
                        Toast.makeText(EditProfileActivity.this, R.string.duplicate_username, Toast
                                .LENGTH_LONG).show();
                        Log.e(TAG, "SETTING THE TMP TO TRUE1!!!!");
                        tmp = true;

                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());

            }
        });
        Log.i(TAG, "THIS IS THE RETURNNNNNN " + tmp + userNamesRegistered);
        return tmp;
    }

    private class PhotoUploadSuccessListener implements
            OnSuccessListener<UploadTask.TaskSnapshot> {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            taskSnapshot.getMetadata()
                    .getReference()
                    .getDownloadUrl()
                    .addOnSuccessListener(
                            new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    userProfile.photoPath = uri.toString();
                                    profileRef.runTransaction(new ProfileDataUploadHandler());
                                }
                            });
        }
    }

}
