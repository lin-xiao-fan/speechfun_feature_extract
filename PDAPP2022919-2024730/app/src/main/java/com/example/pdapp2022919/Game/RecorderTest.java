package com.example.pdapp2022919.Game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.pdapp2022919.Database.Game.Game;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.Recode.WavRecorder;
import com.example.pdapp2022919.SystemManager.FileManager2;
import com.example.pdapp2022919.SystemManager.FileType;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;

public class RecorderTest extends ScreenSetting {

    private static final int restTime = 1000;
    private static final int recordTime = 1000;

    private TextView hint_word, tvResult;
    private ImageView Green_light, Red_light;
    private int recordCount = 0;
    private double[] standard = new double[3];
    private double avg;
    private boolean post_test;
    private TextView countDown;
    private Game recode_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        recode_data = getIntent().getParcelableExtra(NameManager.RECORD_DATA);
        post_test = getIntent().getBooleanExtra(NameManager.POST, false);
        setContentView(R.layout.activity_recorder_test);
        TextView Post_Retest = findViewById(R.id.Post_Retest);
        if (post_test) Post_Retest.setText("後測");
        hint_word = findViewById(R.id.hint_word);
        Green_light = findViewById(R.id.Green_light);
        Red_light = findViewById(R.id.Red_light);
        tvResult = findViewById(R.id.tvResult);
        countDown = findViewById(R.id.countDown);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recordCount = 0;
        // TODO 錄音狀態
        WavRecorder.startRecording(null);
        for (int i = 0; i < standard.length; i++) {
            handlerMeasure.sendEmptyMessageDelayed(0, i * (recordTime + restTime));
        }
    }

    /**
     * 透過HandlerTask 取得檢測結果
     */
    @SuppressLint("HandlerLeak")
    private Handler handlerMeasure = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    hint_word.setText("綠燈亮時請「啊」\n持續5秒");
                    handlerMeasure.sendEmptyMessageDelayed(1, restTime);
                    Message count = new Message();
                    count.what = 4;
                    count.arg1 = restTime / 1000;
                    handlerMeasure.sendMessage(count);
                    break;
                case 1:
                    Green_light.setVisibility(View.VISIBLE);
                    Red_light.setVisibility(View.INVISIBLE);
                    WavRecorder.enableMark(true);
                    handlerMeasure.sendEmptyMessageDelayed(2, recordTime);
                    Message count1 = new Message();
                    count1.what = 4;
                    count1.arg1 = recordTime / 1000;
                    handlerMeasure.sendMessage(count1);
                    break;
                case 2:
                    Green_light.setVisibility(View.INVISIBLE);
                    Red_light.setVisibility(View.VISIBLE);
                    WavRecorder.enableMark(false);
                    double amp = WavRecorder.getMarkAverage();
                    standard[recordCount++] = amp;
                    tvResult.setText(String.format("%2.0f", WavRecorder.getDB(amp)));
                    if (recordCount == standard.length) {
                        double sum = 0;
                        for (double i : standard) {
                            sum += i;
                        }
                        avg = WavRecorder.getDB(sum / standard.length);
                        Intent intent;
                        String path;
                        // TODO 檔案儲存回饋
                        if (post_test) {
                            intent = new Intent(RecorderTest.this, GameResult.class);
                            path = FileManager2.getWavPath(FileType.POSTTEST, recode_data);
                            recode_data.Posttest_db = avg;
                        } else {
                            intent = new Intent(RecorderTest.this, Game1.class);
                            path = FileManager2.getWavPath(FileType.PRETEST, recode_data);
                            recode_data.Pretest_db = avg;
                        }
                        WavRecorder.stopRecording(path, null);
                        intent.putExtra(NameManager.RECORD_DATA, recode_data);
                        startActivity(intent);
                    }
                    break;
                case 4:
                    countDown.setText(Integer.toString(msg.arg1));
//                    countDown.setVisibility(View.VISIBLE);
                    if (msg.arg1 == 1) {
//                        countDown.setVisibility(View.GONE);
                        return;
                    }
                    count = new Message();
                    count.what = 4;
                    count.arg1 = msg.arg1 - 1;
                    handlerMeasure.sendMessageDelayed(count, 1000);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        // TODO 檔案儲存回饋
        WavRecorder.stopRecording(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handlerMeasure.removeCallbacksAndMessages(null);
    }
}