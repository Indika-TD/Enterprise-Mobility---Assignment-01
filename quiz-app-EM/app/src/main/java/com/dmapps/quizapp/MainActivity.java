package com.dmapps.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CircleImageView profile = findViewById(R.id.profile_image);
        profile.setOnClickListener(v->{moveToProfile();});

        RelativeLayout java = findViewById(R.id.java);
        java.setOnClickListener(v->{moveToLevel("java");});

        RelativeLayout cLang = findViewById(R.id.c_lan);
        cLang.setOnClickListener(v->{moveToLevel("cLang");});

        RelativeLayout pay = findViewById(R.id.pay);
        pay.setOnClickListener(v->{moveToLevel("pay");});

        RelativeLayout php = findViewById(R.id.php);
        php.setOnClickListener(v->{moveToLevel("php");});
    }

    private void moveToLevel(String lang) {
        Intent intent = new Intent(this,LevelActivity.class);
        intent.putExtra("language",lang);
        startActivity(intent);
    }

    private void moveToProfile() {
        Intent intent = new Intent(this,ProfileActivity.class);
        startActivity(intent);
    }
}