package com.dmapps.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.stream.Stream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    AVLoadingIndicatorView avi;
    TextView name,email,mobile,logout;
    CircleImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();

        avi = findViewById(R.id.avi);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);
        logout = findViewById(R.id.logout);
        profile =  findViewById(R.id.profile_image);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v->{onBackPressed();});

        logout.setOnClickListener(v->logoutUser());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        getUserInformation(currentUser.getUid());
    }

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void getUserInformation(String uid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("database").child("user").child(uid);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                email.setText(user.getEmail());
                mobile.setText(user.getMobile());
                profile.setImageBitmap(decodeImage((user.getProfile())));
                avi.setVisibility(AVLoadingIndicatorView.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("---->", "Failed to read value.", error.toException());
                Toast.makeText(getApplicationContext()
                        , "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
                avi.setVisibility(AVLoadingIndicatorView.GONE);
            }
        });
    }

    private Bitmap decodeImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}