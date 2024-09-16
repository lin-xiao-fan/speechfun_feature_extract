package com.example.pdapp2022919.Questionnaire;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pdapp2022919.Database.Questionnaire.Questionnaire;
import com.example.pdapp2022919.Database.Questionnaire.QuestionnaireDao;
import com.example.pdapp2022919.MainPage;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.DatabaseManager;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;
import com.example.pdapp2022919.net.Client;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class QResult extends ScreenSetting {
    private String qkind;
    private TextView EvaluationResult, Grade, TestTimeText;
    private Long Qsuestion_time;
    private Button information_button;
    private PopupWindow questionnaire_popup;
    private final QRecoderAdapter adapter = new QRecoderAdapter();
    private RecyclerView QrecycleView;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qresult);
        Qsuestion_time = System.currentTimeMillis();
        EvaluationResult = findViewById(R.id.EvaluationResult);
        Button homeOrVHI = findViewById(R.id.homeOrVHI);
        Grade = findViewById(R.id.Grade);
        information_button = findViewById(R.id.information_button);
        information_button.setVisibility(View.GONE);
        TestTimeText = findViewById(R.id.TestTimeText);
        QrecycleView = findViewById(R.id.lineChart);
        QrecycleView.setLayoutManager(new LinearLayoutManager(this));
        QrecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        QrecycleView.setAdapter(adapter);
        String qsuestion_time = DATE_FORMAT.format(Qsuestion_time);
        Intent intent = getIntent();
        qkind = intent.getStringExtra(NameManager.Q_KIND);
        int[] reply = intent.getIntArrayExtra(NameManager.REPLY_ANSWER);

        if (Objects.equals(qkind, NameManager.VOS)) {
            homeOrVHI.setText("繼續填寫");
            homeOrVHI.setOnClickListener(view -> {
                int VHI_10_questionID = R.array.VHI_10_question;
                int[] VHI_10_answerID = new int[]{
                        R.array.VHI_10_answer,
                        R.array.VHI_10_answer,
                        R.array.VHI_10_answer,
                        R.array.VHI_10_answer,
                        R.array.VHI_10_answer,
                        R.array.VHI_10_answer,
                        R.array.VHI_10_answer,
                        R.array.VHI_10_answer,
                        R.array.VHI_10_answer,
                        R.array.VHI_10_answer
                };
                qkind(NameManager.VHI, VHI_10_questionID, VHI_10_answerID);
            });
        } else if (Objects.equals(qkind, NameManager.VHI)) {
            homeOrVHI.setText("繼續填寫");
            homeOrVHI.setOnClickListener(view -> {
                int PHQ_9_questionID = R.array.phq_9_question;
                int[] PHQ_9_answerID = new int[]{
                        R.array.phq_9_answer,
                        R.array.phq_9_answer,
                        R.array.phq_9_answer,
                        R.array.phq_9_answer,
                        R.array.phq_9_answer,
                        R.array.phq_9_answer,
                        R.array.phq_9_answer,
                        R.array.phq_9_answer,
                        R.array.phq_9_answer
                };
                qkind(NameManager.PHQ, PHQ_9_questionID, PHQ_9_answerID);
            });
        } else {
            homeOrVHI.setText("回到首頁");
            homeOrVHI.setOnClickListener(view -> {
                startActivity(new Intent(this, MainPage.class));
            });
        }

        EvaluationResult.setText(qkind + " 評估結果");
        Grade.setText(calculate(qkind, reply) + "");
        TestTimeText.setText(qsuestion_time);
        if (Objects.equals(qkind, NameManager.VHI)) {
            information_button.setVisibility(View.VISIBLE);
        } else information_button.setVisibility(View.GONE);

        information_button.setOnClickListener(view -> {
            new Handler(Looper.getMainLooper()).post(() -> {
                PopupWindow popupWindow = new popupwindow(this);
                popupWindow.showAtLocation(
                        LayoutInflater.from(this).inflate(R.layout.questionnaire_popup, null),
                        Gravity.CENTER, 0, 0
                );
            });
        });
        new Thread(() -> {
            QuestionnaireDao dao = DatabaseManager.getInstance(this).questionnaireDao();
            dao.addQuestionnaire(new Questionnaire(Qsuestion_time, qkind, calculate(qkind, reply), Arrays.toString(reply)));
            List<Questionnaire> list = dao.getTypeQuestionnaire(Client.getUuid().toString(), qkind);
            runOnUiThread(() -> adapter.setList(list));
        }).start();
    }

    private int calculate(String kind, int[] reply) {
        int result = 0;
        switch (kind) {
            case NameManager.VOS:
                result += (4 - reply[0]) * 25;
                result += (reply[1]) * 50;
                result += (4 - reply[2]) * 25;
                result += (reply[3]) * 25;
                result += (4 - reply[4]) * 25;
                result /= 5;
                break;
            case NameManager.VHI:
                for (int i : reply) {
                    result += i;
                }
                break;
            case NameManager.SUS:
                result += (reply[0]);
                result += (4 - reply[1]);
                result += (reply[2]);
                result += (4 - reply[3]);
                result += (reply[4]);
                result += (4 - reply[5]);
                result += (reply[6]);
                result += (4 - reply[7]);
                result += (reply[8]);
                result += (4 - reply[9]);
                result *= 2.5;
                break;

            case NameManager.PHQ:
                result += (reply[0]);
                result += (reply[1]);
                result += (reply[2]);
                result += (reply[3]);
                result += (reply[4]);
                result += (reply[5]);
                result += (reply[6]);
                result += (reply[7]);
                result += (reply[8]);
                break;
        }
        return result;
    }

    public class popupwindow extends PopupWindow implements View.OnClickListener {
        View view;

        public popupwindow(Context mContext) {
            this.view = LayoutInflater.from(mContext).inflate(R.layout.questionnaire_popup, null);
            this.setOutsideTouchable(true);
            this.view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        dismiss();
                    }
                    return true;
                }
            });
            this.setContentView(this.view);
            this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
            this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        }

        @Override
        public void onClick(View view) {
        }
    }

    private void qkind(String kind, int questionID, int[] answerID) {
        Intent intent = new Intent(this, QContent.class);
        intent.putExtra(NameManager.Q_KIND, kind);
        intent.putExtra(NameManager.QUESTION, questionID);
        intent.putExtra(NameManager.ANSWER, answerID);
        startActivity(intent);
    }
}

//陣列轉字串
//    private String reply_result (int[] reply) {
//        String result = "";
//        for (int i : reply) {
//            result += reply[i] + "";
//        }
//        return result;

