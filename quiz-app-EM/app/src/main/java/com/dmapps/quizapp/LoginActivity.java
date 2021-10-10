package com.dmapps.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static final int CAM_REQUEST_CODE = 41;
    private FirebaseAuth mAuth;
    EditText email,password;

    AppCompatButton login;
    AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        avi = findViewById(R.id.avi);
        login = findViewById(R.id.login);

        login.setOnClickListener(v -> {
            if(email.getText().toString().length() == 0) {
                email.setError("Invalid email");
            }
            else if(!isEmailValid(email.getText().toString())) {
                email.setError("Invalid email");
            }
            else if(password.getText().toString().length() == 0) {
                password.setError("Invalid password");
            }
            else {
                login.setVisibility(AppCompatButton.GONE);
                avi.setVisibility(AVLoadingIndicatorView.VISIBLE);
                checkUser();
            }
        });

        TextView register = findViewById(R.id.register);
        register.setOnClickListener(v -> clickRegister());

        if(!checkPermissionForCam()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAM_REQUEST_CODE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void checkUser() {
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("-->", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d("-->", user.getUid());
                            getUserInformation(user.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("-->", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext()
                                    , "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            login.setVisibility(AppCompatButton.VISIBLE);
                            avi.setVisibility(AVLoadingIndicatorView.GONE);
                        }
                    }
                });
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
                Log.d("--", "Value is: " + user.getName());
                login.setVisibility(AppCompatButton.VISIBLE);
                avi.setVisibility(AVLoadingIndicatorView.GONE);
                clickLogin();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("---->", "Failed to read value.", error.toException());
                Toast.makeText(getApplicationContext()
                        , "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
                login.setVisibility(AppCompatButton.VISIBLE);
                avi.setVisibility(AVLoadingIndicatorView.GONE);
            }
        });
    }

    private void clickLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void clickRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean checkPermissionForCam() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.CAMERA);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
}