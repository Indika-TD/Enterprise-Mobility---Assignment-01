package com.dmapps.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class LevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        String language = getIntent().getStringExtra("language");

        RelativeLayout levelOne = findViewById(R.id.level_one);
        levelOne.setOnClickListener(v -> {
            moveToQuiz(language,"levelOne");
        });
        RelativeLayout levelTwo = findViewById(R.id.level_two);
        levelTwo.setOnClickListener(v -> {
            moveToQuiz(language,"levelTwo");
        });

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v->{onBackPressed();});
    }

    private void moveToQuiz(String language, String level) {
        Intent intent = new Intent(this,QuizActivity.class);
        intent.putExtra("language",language);
        intent.putExtra("level",level);
        startActivity(intent);
        finish();
    }
}