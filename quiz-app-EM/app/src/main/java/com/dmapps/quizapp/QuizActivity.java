package com.dmapps.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class QuizActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String language,level;
    boolean empty = false;
    int qNumber = 1,validAnswer=0;
    String qAnswer = "";
    TextView que,a1,a2,a3,a4;
    DataSnapshot ds;
    AppCompatButton next;
    RelativeLayout avi;
    LinearLayoutCompat layout;
    RelativeLayout roundOne,roundTwo,roundThree,roundFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        mAuth = FirebaseAuth.getInstance();

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v->{onBackPressed();});

        que = findViewById(R.id.que);
        a1 = findViewById(R.id.a1);
        a2=findViewById(R.id.a2);
        a3 = findViewById(R.id.a3);
        a4 = findViewById(R.id.a4);

        //intent data
        language = getIntent().getStringExtra("language");
        level = getIntent().getStringExtra("level");

        avi = findViewById(R.id.avi);
        layout = findViewById(R.id.layout);

        next = findViewById(R.id.next);
        next.setOnClickListener(v -> {
            checkValidOrNot();
        });

        //get data from firebase
        getQuizList();

        //click answers
        answerList();
    }

    private void checkValidOrNot() {
        if(!empty) {
            String s = qNumber+"";
            DataSnapshot singleSnapshot = ds.child(s);
            Quiz quiz = singleSnapshot.getValue(Quiz.class);
            assert quiz != null;
            if(quiz.getAnswer().equals(qAnswer)) {
                validAnswer++;
            }
            if(ds.getChildrenCount() == qNumber) {
                int percent = (int)(validAnswer * 100.0f) / qNumber;
                String score = percent+"%";
                Intent intent = new Intent(this,ScoreActivity.class);
                intent.putExtra("score",score);
                startActivity(intent);
                finish();
            }
            else {
                qNumber++;
                refreshAnswer();
                updateQuestion(qNumber);
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

    private void getQuizList() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("database").child("quiz").child(language).child(level);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    ds = dataSnapshot;
                    updateQuestion(qNumber);
                    avi.setVisibility(RelativeLayout.GONE);
                    layout.setVisibility(LinearLayoutCompat.VISIBLE);
                }
                else {
                    avi.setVisibility(RelativeLayout.GONE);
                    layout.setVisibility(LinearLayoutCompat.VISIBLE);
                    Toast.makeText(getApplicationContext()
                            , "Empty List",
                            Toast.LENGTH_SHORT).show();
                    empty = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("---->", "Failed to read value.", error.toException());
                Toast.makeText(getApplicationContext()
                        , "Failed to read value",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateQuestion(int number) {
        String s = number+"";
        DataSnapshot singleSnapshot = ds.child(s);
        Quiz quiz = singleSnapshot.getValue(Quiz.class);
        assert quiz != null;
        que.setText(quiz.getQuestion());
        a1.setText(quiz.getAnswer_a());
        a2.setText(quiz.getAnswer_b());
        a3.setText(quiz.getAnswer_c());
        a4.setText(quiz.getAnswer_d());
    }
}