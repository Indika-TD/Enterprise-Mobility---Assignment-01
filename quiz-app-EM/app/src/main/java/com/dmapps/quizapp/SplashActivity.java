package com.dmapps.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    AppCompatButton offline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        offline = findViewById(R.id.offline);

        if(isNetworkAvailable(this)) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent mainIntent;
                if (currentUser==null) {
                    mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                else {
                    mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                }
                startActivity(mainIntent);
                finish();

            }, 2000);
        }
        else {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                offline.setVisibility(AppCompatButton.VISIBLE);
            }, 2000);
        }

        offline.setOnClickListener(v->moveToOffline());
    }

    private void moveToOffline() {
        Intent mainIntent = new Intent(SplashActivity.this, OfflineLevelActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}