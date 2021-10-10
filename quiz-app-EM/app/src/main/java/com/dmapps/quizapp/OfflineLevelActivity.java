package com.dmapps.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class OfflineLevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_level);

        RelativeLayout levelOne = findViewById(R.id.level_one);
        levelOne.setOnClickListener(v -> {
            moveToQuiz("levelOne");
        });
        RelativeLayout levelTwo = findViewById(R.id.level_two);
        levelTwo.setOnClickListener(v -> {
            moveToQuiz("levelTwo");
        });
    }

    private void moveToQuiz(String level) {
        Intent intent = new Intent(this,OfflineQuizActivity.class);
        intent.putExtra("level",level);
        startActivity(intent);
        finish();
    }
}