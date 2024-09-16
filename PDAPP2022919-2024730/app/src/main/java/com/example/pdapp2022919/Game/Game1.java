package com.example.pdapp2022919.Game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pdapp2022919.Database.Game.Game;
import com.example.pdapp2022919.Game.GameFeedback.AwardPopUP;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.Recode.WavRecorder;
import com.example.pdapp2022919.SystemManager.FileManager2;
import com.example.pdapp2022919.SystemManager.FileType;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;

public class Game1 extends ScreenSetting {

    private enum GameStatus {
        PLAYING, GAME_OVER, WAITING
    }

    private GameView gameView;
    private Button startGameButton, back, see_result_button, continue_game_button;
    private TextView real_time_db, hint_text, level_text,game_illustrate, db_avg_tv;
    private GameStatus status = GameStatus.WAITING;
    private boolean first_time = true;
    private Game recordData;
    private long startTime;
    private int times = 0;
    private AwardPopUP awardPopUP;
    private ImageView overdB_hint;
    private Boolean isTimerRunning= false ;
    private Long startoverdBTime;
    public static Boolean isoverdB =false;


    private void startMeasure() {
        startTime = System.currentTimeMillis();
        // TODO 錄音狀態
        WavRecorder.startRecording(null);
        handlerMeasure.sendEmptyMessage(1);
    }

    private void resumeRecording() {
        startTime = System.currentTimeMillis();
        // TODO 錄音狀態
        WavRecorder.resumeRecording();
        handlerMeasure.sendEmptyMessage(1);
    }

    private void stopMeasure() {
        recordData.Play_how_long += System.currentTimeMillis() - startTime;
        handlerMeasure.removeCallbacksAndMessages(null);
        String path;
        times++;
        switch (times) {
            case 1:
                path = FileManager2.getWavPath(FileType.GAME_ONE, recordData);
                break;
            case 2:
                path = FileManager2.getWavPath(FileType.GAME_TWO, recordData);
                break;
            case 3:
                path = FileManager2.getWavPath(FileType.GAME_THREE, recordData);
                break;
            default:
                path = "";
        }
        WavRecorder.stopRecording(path);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        recordData = intent.getParcelableExtra(NameManager.RECORD_DATA);

        gameView = findViewById(R.id.gameView);
        gameView.setStander(recordData.Pretest_db);
        gameView.setLevelDifficulty(recordData.Game_diffculty);

        level_text = findViewById(R.id.level_text);
        level_text.setText(recordData.getDifficultText());

        db_avg_tv = findViewById(R.id.db_avg_tv);
        db_avg_tv.setText("前測分貝："+getString(R.string.pretest_db, recordData.Pretest_db));
        overdB_hint = findViewById(R.id.overdB_hint);

        see_result_button = findViewById(R.id.see_result_button);
        game_illustrate = findViewById(R.id.gameIllustrate);
        real_time_db = findViewById(R.id.realtimedb);
        real_time_db.setVisibility(View.GONE);
        continue_game_button = findViewById(R.id.continue_game_button);
        continue_game_button.setOnClickListener(view -> {
            view.setVisibility(View.GONE);
            status = GameStatus.PLAYING;
            resumeRecording();
        });
        startGameButton = findViewById(R.id.startGameButton);
        startGameButton.setOnClickListener(view -> {
            if (first_time) {
                first_time = false;
            }
            view.setVisibility(View.GONE);
            hint_text.setVisibility(View.GONE);
            game_illustrate.setVisibility(View.GONE);
            gameView.startGame();
            startMeasure();
            status = GameStatus.PLAYING;
        });
        //回上一頁 finish 關閉當前頁面
        back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            finish();
        });
        hint_text = findViewById(R.id.hint_text);
        hint_text.setVisibility(View.GONE);

        awardPopUP = new AwardPopUP(this, hint_text);
    }

    private int frame = 0;

    private Handler handlerMeasure = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                if (gameView.isGameOver()) {
                    if (gameView.failCount() >= 3) {
                        status = GameStatus.GAME_OVER;
                        recordData.Pass = false;
                        hint_text.setVisibility(View.VISIBLE);
                        hint_text.setText("訓練結束");
                        awardPopUP.setStarCount(1);
                        awardPopUP.showAtLocation(hint_text, Gravity.CENTER, 0, 0);
                        see_result_button.setVisibility(View.VISIBLE);
                        see_result_button.setOnClickListener(view -> openRecorder_test());
                        stopMeasure();
                        return;
                    }
                    status = GameStatus.WAITING;
                    hint_text.setText("失敗");
                    hint_text.setVisibility(View.VISIBLE);
                    startGameButton.setText(" 重新開始 ");
                    startGameButton.setVisibility(View.VISIBLE);
                    stopMeasure();
                    return;
                }
                if (gameView.getGap() >= 3) {
                    status = GameStatus.GAME_OVER;
                    hint_text.setVisibility(View.VISIBLE);
                    hint_text.setText("訓練結束");
                    recordData.Pass = true;
                    if (gameView.failCount() == 0) awardPopUP.setStarCount(3);
                    else awardPopUP.setStarCount(2);
                    awardPopUP.showAtLocation(hint_text, Gravity.CENTER, 0, 0);
                    see_result_button.setVisibility(View.VISIBLE);
                    see_result_button.setOnClickListener(view -> openRecorder_test());
                    stopMeasure();
                    return;
                }
                WavRecorder.enableMark(false);
                double amp = WavRecorder.getMarkAverage();
                gameView.update(amp);
                WavRecorder.enableMark(true);
                handlerMeasure.sendEmptyMessageDelayed(1, 15);
                //刷新率計算到15次時settext，最後歸零重新計算
                if (frame > 15) {
                    double db = WavRecorder.getDB(amp);
                    real_time_db.setText("即時分貝："+getString(R.string.now_db, db));
                    real_time_db.setVisibility(View.VISIBLE);
                    if (db >= recordData.Pretest_db + 3) {
                        if (!isTimerRunning) {
                            // 開始計時
                            startoverdBTime = System.currentTimeMillis();
                            isTimerRunning = true;
                        } else {
                            long currentTime = System.currentTimeMillis();
                            if (currentTime - startoverdBTime >= 500) { // 0.5秒
                                if (currentTime - startoverdBTime >= 2500) {
                                    overdB_hint.setVisibility(View.INVISIBLE);
                                    isoverdB = false;
                                } else {
                                    overdB_hint.setVisibility(View.VISIBLE);
                                    isoverdB = true;
                                }
                            }
                        }
                    }else {
                            isTimerRunning =false;
                            startTime = 0;
                            overdB_hint.setVisibility(View.INVISIBLE);
                            isoverdB = false;
                    }
                    frame = 0;
                }
                frame++;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (status == GameStatus.PLAYING) {
            continue_game_button.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMeasure();
    }

    //禁止返回上一頁
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void openRecorder_test() {
        Intent intent = new Intent(this, RecorderTest.class);
        intent.putExtra(NameManager.POST, true);
        intent.putExtra(NameManager.RECORD_DATA, recordData);
        startActivity(intent);
    }

}
