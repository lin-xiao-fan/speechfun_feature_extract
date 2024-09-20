package com.example.pdapp2022919.KeepLong;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pdapp2022919.Database.KeepLong.KeepLong;
import com.example.pdapp2022919.Database.KeepLong.KeepLongDao;
import com.example.pdapp2022919.ListPage;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.Recode.WavRecorder;
import com.example.pdapp2022919.SystemManager.DatabaseManager;
import com.example.pdapp2022919.SystemManager.FileManager2;
import com.example.pdapp2022919.SystemManager.FileType;
import com.example.pdapp2022919.SystemManager.ScreenSetting;

import java.util.Timer;
import java.util.TimerTask;

public class KeepLongPage extends ScreenSetting {

    private static final String[] words = {"啊", "衣", "屋"};

    private Button BT1, next_word_BT;
    private ImageView recorder_state, green_light_image;
    private TextView aiu_word, content1_text, content2_text, timer_text;
    private KeepLong keepLong = new KeepLong();
    private Boolean timeStarted = false;
    boolean mStartRecording = false;
    private int timer_count = 0;
    private Timer timer;
    private TimerTask timerTask;
    private Double time = 0.0;

    private enum State {
        START, RECORD, END
    }

    private int x = 0, T = 1;
    private State state = State.START;

    private void startRecording() {
        WavRecorder.startRecording(() -> {
            runOnUiThread(() -> {
                if (!WavRecorder.isRecording()) recorder_state.setVisibility(View.GONE);
                else recorder_state.setVisibility(View.VISIBLE);
            });
        });
    }

    private void stopRecording() {
        // TODO 紀錄檔案位置
        String path;
        switch (x) {
            case 0:
                path = FileManager2.getWavPath(FileType.KEEP_LONG_a, keepLong, T);
                break;
            case 1:
                path = FileManager2.getWavPath(FileType.KEEP_LONG_i, keepLong, T);
                break;
            case 2:
                path = FileManager2.getWavPath(FileType.KEEP_LONG_u, keepLong, T);
                break;
            default:
                path = "";
        }
        WavRecorder.stopRecording(path);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_long);
        BT1 = findViewById(R.id.long_start_Button);
        recorder_state = findViewById(R.id.recorder_state3);
        next_word_BT = findViewById(R.id.next_word_Button);
        green_light_image = findViewById(R.id.green_light_image);
        aiu_word = findViewById(R.id.aiu_word);
        timer_text = findViewById(R.id.timer_text);
        content1_text = findViewById(R.id.content1_text);
        content2_text = findViewById(R.id.content2_text);
        init();
        timer = new Timer();

        BT1.setOnClickListener(view -> setState());
        next_word_BT.setOnClickListener(view -> change_word());
    }

    private void init() {
        green_light_image.setVisibility(View.INVISIBLE);
        next_word_BT.setVisibility(View.INVISIBLE);
        BT1.setText("開始");
        aiu_word.setText(words[x]);
        content1_text.setText(getString(R.string.keep_long, words[x]));
    }

    private void textVisible(int visible) {
        content1_text.setVisibility(visible);
        content2_text.setVisibility(visible);
    }

    private void setState() {
        switch (state) {
            case START:
                startRecording();
                BT1.setText("停止");
                BT1.setBackgroundResource(R.drawable.red_button);
                next_word_BT.setVisibility(INVISIBLE);
                if (timeStarted == false) {
                    timeStarted = true;
                    startTimer();
                } else {
                    timeStarted = false;
                    timerTask.cancel();
                }
                green_light_image.setVisibility(VISIBLE);
                timer_text.setVisibility(VISIBLE);
                textVisible(INVISIBLE);
                state = State.RECORD;
                break;
            case RECORD:
                BT1.setText("重來");
                BT1.setBackgroundResource(R.drawable.logging_page_button_f);
                green_light_image.setVisibility(INVISIBLE);
                timer_text.setVisibility(INVISIBLE);
                timeStarted = false;
                if (timerTask != null) {
                    timerTask.cancel();
                    time = 0.0;
                    timeStarted = false;
                    timer_text.setText(formatTime(0));
                }
                T++;
                state = State.START;
                // TODO BT color red
                textVisible(VISIBLE);
                next_word_BT.setVisibility(VISIBLE);
                stopRecording();
                if (x >= words.length - 1) {
                    // TODO finish
                    next_word_BT.setText("完成");
                    next_word_BT.setOnClickListener(v -> {
                        new Thread(() -> {
                            KeepLongDao dao = DatabaseManager.getInstance(this).keepLongDao();
                            dao.addKeepLong(keepLong);
                        }).start();
                        Intent intent = new Intent(this, ListPage.class);
                        startActivity(intent);
                    });
                }

                break;
            case END:
                BT1.setText("開始");
                BT1.setBackgroundResource(R.drawable.logging_page_button_f);
                green_light_image.setVisibility(View.INVISIBLE);
                timer_text.setVisibility(INVISIBLE);
                next_word_BT.setVisibility(View.INVISIBLE);
                state = State.START;
                break;
        }
    }

    private void change_word() {
        x = x + 1;
        state = State.END;
        setState();
        T = 1;
        aiu_word.setText(words[x]);
        content1_text.setText(getString(R.string.keep_long, words[x]));
    }


    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        timer_text.setText(getTimerText());
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    private String getTimerText() {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        return formatTime(seconds);
    }

    private String formatTime(int seconds) {
        return String.format(String.format("%d", seconds));
    }
}