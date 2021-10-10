package com.dmapps.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class OfflineScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_score);

        String score = getIntent().getStringExtra("score");
        TextView scoreText = findViewById(R.id.score);
        String conText = "Congrats!\n"+score+" score";
        scoreText.setText(conText);

        Button playAgain = findViewById(R.id.paly);
        playAgain.setOnClickListener(v -> playAgain());
    }

    private void playAgain() {
        Intent intent = new Intent(this, OfflineLevelActivity.class);
        startActivity(intent);
        finish();
    }
}