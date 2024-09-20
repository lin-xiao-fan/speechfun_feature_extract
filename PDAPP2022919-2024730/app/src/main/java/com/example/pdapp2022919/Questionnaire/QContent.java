package com.example.pdapp2022919.Questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;

import java.util.Arrays;

public class QContent extends ScreenSetting {
    private ProgressBar progressBar2;
    private TextView question, NumofQ;
    private Button Finish_Button, lastQelesButton, nextQelesButton;
    private String[] questions;
    private int[] answers;
    private RadioGroup answerButtonGroup;
    private RadioButton[] answerButtons = new RadioButton[5];
    private int[] reply;
    private int sentence = 0;
    private String qkind;
    private boolean canNextPage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qcontent);
        Intent intent = getIntent();
        qkind = intent.getStringExtra(NameManager.Q_KIND);

        lastQelesButton = findViewById(R.id.lastQelesButton);
        NumofQ = findViewById(R.id.NumofQ);
        Finish_Button = findViewById(R.id.Finish_Button1);
        question = findViewById(R.id.questionnaire_caption);
        nextQelesButton = findViewById(R.id.nextQelesButton);
        progressBar2 = findViewById(R.id.progressBar2);
        answerButtonGroup = findViewById(R.id.answer_button_group);
        int questionID = intent.getIntExtra(NameManager.QUESTION, -1);
        if (questionID == -1) return;
        questions = getResources().getStringArray(questionID);
        reply = new int[questions.length];
        Arrays.fill(reply, -1);
        answers = intent.getIntArrayExtra(NameManager.ANSWER);

        answerButtons[0] = findViewById(R.id.AnswerButton1);
        answerButtons[1] = findViewById(R.id.AnswerButton2);
        answerButtons[2] = findViewById(R.id.AnswerButton3);
        answerButtons[3] = findViewById(R.id.AnswerButton4);
        answerButtons[4] = findViewById(R.id.AnswerButton5);

        Handler handler = new Handler(Looper.getMainLooper());
        for (int i = 0; i < 5; i++) {
            int selected = i;
            answerButtons[i].setOnClickListener((view) -> {
                reply[sentence] = selected;
                if (!canNextPage) return;
                canNextPage = false;
                handler.postDelayed(this::nextQuestion, 300);
            });
        }
        setSentence();
        setReply();
        progressBar2.setMax(questions.length);

        Finish_Button.setOnClickListener(view -> {
            Intent resultIntent = new Intent(this, QResult.class);
            resultIntent.putExtra(NameManager.REPLY_ANSWER, reply);
            resultIntent.putExtra(NameManager.Q_KIND, qkind);
            startActivity(resultIntent);
        });

        nextQelesButton.setOnClickListener(view -> {
            nextQuestion();
        });
        lastQelesButton.setOnClickListener(view -> {
            switch (sentence) {
                case 0:
                    break;
                case 1:
                    lastQelesButton.setVisibility(View.INVISIBLE);
                default:
                    Finish_Button.setVisibility(View.GONE);
                    nextQelesButton.setVisibility(View.VISIBLE);
                    this.sentence -= 1;
                    setSentence();
                    setReply();
            }
        });
    }

    private void setSentence() {
        NumofQ.setText(
                getString(R.string.num_of_q, (this.sentence) + 1, questions.length)
        );
        question.setText(questions[this.sentence]);
        progressBar2.setProgress(this.sentence + 1);
    }

    private void setReply() {
        String[] answer = getResources().getStringArray(answers[this.sentence]);
        // r =第幾個回復的數值
        int r = reply[sentence];
        answerButtonGroup.clearCheck();
        for (int i = 0; i < answerButtons.length; i++) {
            if (i >= answer.length){
                answerButtons[i].setVisibility(View.GONE);
                continue;
            }
            answerButtons[i].setVisibility(View.VISIBLE);
            // 設定答案內容
            answerButtons[i].setText(answer[i]);
            // 如果跟r數值相同的按鈕則checked
            if (r == i) answerButtons[i].setChecked(true);
//            answerButtons[i].setChecked( r == i );
        }

    }

    private void nextQuestion(){
        canNextPage = true;
        if (sentence== questions.length-1){
            Finish_Button.setVisibility(View.VISIBLE);
            return;
        }
        if (sentence== questions.length-2){
            if (reply[sentence + 1] != -1) {
                Finish_Button.setVisibility(View.VISIBLE);
            }
            nextQelesButton.setVisibility(View.INVISIBLE);
        }
        lastQelesButton.setVisibility(View.VISIBLE);
        this.sentence += 1;
        if (reply[sentence] == -1) {
            nextQelesButton.setVisibility(View.INVISIBLE);
        }
        setSentence();
        setReply();
    }

}
