package com.example.pdapp2022919.Questionnaire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.pdapp2022919.MainPage;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;

public class QCaption extends ScreenSetting {
private Button caption_ok,back_main_page_button9;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qcaption);
        caption_ok = findViewById(R.id.caption_ok);

        back_main_page_button9 = findViewById(R.id.back_main_page_button9);
        back_main_page_button9.setOnClickListener(view -> {
            startActivity(new Intent(this, MainPage.class));
        });


        caption_ok.setOnClickListener(view -> {
            int VOS_questionID = R.array.vos_questionnaire;
            int[] VOS_answerID = new int[]{
                    R.array.vos_answer1,
                    R.array.vos_answer2,
                    R.array.vos_answer3_5,
                    R.array.vos_answer4,
                    R.array.vos_answer3_5
            };
            qkind(NameManager.VOS, VOS_questionID, VOS_answerID);
        });
    }
    private void qkind (String kind, int questionID, int[] answerID) {
        Intent intent = new Intent(this, QContent.class);
        intent.putExtra(NameManager.Q_KIND, kind);
        intent.putExtra(NameManager.QUESTION, questionID);
        intent.putExtra(NameManager.ANSWER, answerID);
        startActivity(intent);
    }
}