package com.dmapps.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class OfflineQuizActivity extends AppCompatActivity {

    String level;
    TextView que,a1,a2,a3,a4;
    AppCompatButton next;
    JSONArray array;
    int qNumber = 1,validAnswer=0;
    String qAnswer = "";
    RelativeLayout roundOne,roundTwo,roundThree,roundFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_quiz);

        level = getIntent().getStringExtra("level");

        //render offline data
        try {
            JSONObject object =  new JSONObject(loadJSONFile());
            if (level.equals("levelOne")) {
                array = object.getJSONArray("levelOne");
            }
            else {
                array = object.getJSONArray("levelTwo");
            }
        } catch (JSONException e) {
            array = new JSONArray();
            e.printStackTrace();
        }

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v->{onBackPressed();});

        que = findViewById(R.id.que);
        a1 = findViewById(R.id.a1);
        a2=findViewById(R.id.a2);
        a3 = findViewById(R.id.a3);
        a4 = findViewById(R.id.a4);

        next = findViewById(R.id.next);
        next.setOnClickListener(v -> {
            checkValidOrNot();
        });

        answerList();
        updateQuestion();
    }

    public String loadJSONFile() {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("file.json");
            int size = inputStream.available();
            byte[] byteArray = new byte[size];
            inputStream.read(byteArray);
            inputStream.close();
            json = new String(byteArray, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    private void checkValidOrNot() {

        if(array.length() != 0) {
            JSONObject singleSnapshot = null;
            try {
                singleSnapshot = array.getJSONObject(qNumber-1);
                if(singleSnapshot.getString("answer").equals(qAnswer)) {
                    validAnswer++;
                }
                if(array.length() == qNumber) {
                    int percent = (int)(validAnswer * 100.0f) / qNumber;
                    String score = percent+"%";
                    Intent intent = new Intent(this,OfflineScoreActivity.class);
                    intent.putExtra("score",score);
                    startActivity(intent);
                    finish();
                }
                else {
                    qNumber++;
                    refreshAnswer();
                    updateQuestion();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            onBackPressed();
        }
    }

    private void refreshAnswer() {
        roundOne.setBackgroundResource(R.drawable.round);
        roundTwo.setBackgroundResource(R.drawable.round);
        roundThree.setBackgroundResource(R.drawable.round);
        roundFour.setBackgroundResource(R.drawable.round);
        qAnswer = "";
    }

    private void updateQuestion() {
        JSONObject singleSnapshot = null;
        try {
            singleSnapshot = array.getJSONObject(qNumber-1);
            que.setText(singleSnapshot.getString("question"));
            a1.setText(singleSnapshot.getString("answer_a"));
            a2.setText(singleSnapshot.getString("answer_b"));
            a3.setText(singleSnapshot.getString("answer_c"));
            a4.setText(singleSnapshot.getString("answer_d"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void answerList() {
        RelativeLayout one = findViewById(R.id.one);
        RelativeLayout two = findViewById(R.id.two);
        RelativeLayout three = findViewById(R.id.three);
        RelativeLayout four = findViewById(R.id.four);

        roundOne = findViewById(R.id.clickOne);
        roundTwo = findViewById(R.id.clickTwo);
        roundThree = findViewById(R.id.clickThree);
        roundFour = findViewById(R.id.clickFour);
        one.setOnClickListener(v -> {
            roundOne.setBackgroundResource(R.drawable.click_round);
            roundTwo.setBackgroundResource(R.drawable.round);
            roundThree.setBackgroundResource(R.drawable.round);
            roundFour.setBackgroundResource(R.drawable.round);
            qAnswer = "a";
        });

        two.setOnClickListener(v -> {
            roundOne.setBackgroundResource(R.drawable.round);
            roundTwo.setBackgroundResource(R.drawable.click_round);
            roundThree.setBackgroundResource(R.drawable.round);
            roundFour.setBackgroundResource(R.drawable.round);
            qAnswer = "b";
        });

        three.setOnClickListener(v -> {
            roundOne.setBackgroundResource(R.drawable.round);
            roundTwo.setBackgroundResource(R.drawable.round);
            roundThree.setBackgroundResource(R.drawable.click_round);
            roundFour.setBackgroundResource(R.drawable.round);
            qAnswer = "c";
        });

        four.setOnClickListener(v -> {
            roundOne.setBackgroundResource(R.drawable.round);
            roundTwo.setBackgroundResource(R.drawable.round);
            roundThree.setBackgroundResource(R.drawable.round);
            roundFour.setBackgroundResource(R.drawable.click_round);
            qAnswer = "d";
        });
    }
}