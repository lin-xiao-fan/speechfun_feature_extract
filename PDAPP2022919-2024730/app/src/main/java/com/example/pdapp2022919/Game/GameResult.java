package com.example.pdapp2022919.Game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pdapp2022919.Database.Game.Game;
import com.example.pdapp2022919.Database.Game.GameDao;
import com.example.pdapp2022919.MainPage;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.Recode.WavRecorder;
import com.example.pdapp2022919.SystemManager.DatabaseManager;
import com.example.pdapp2022919.SystemManager.FileManager2;
import com.example.pdapp2022919.SystemManager.FileType;
import com.example.pdapp2022919.SystemManager.MediaManager;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;
import com.example.pdapp2022919.HealthManager.History.gameHistoryListAdapter;

import java.text.SimpleDateFormat;

public class GameResult extends ScreenSetting {

    private static final SimpleDateFormat DATE = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    private TextView diffculty_text_view, success_loss_text_view,hint_text;
    private TextView posttestdb_text_view, pretestdb_text_view, play_how_long_text_view;
    private Button PlayAgain, nextround_back_Button;
    private Game record_data;
    private Boolean countend = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);
        hideSystemUI();
        PlayAgain = findViewById(R.id.back_history_page);
        nextround_back_Button = findViewById(R.id.backHome);
        record_data = getIntent().getParcelableExtra(NameManager.RECORD_DATA);
        record_data.stop_play_time = System.currentTimeMillis();
        diffculty_text_view = findViewById(R.id.diffculty_text_view);
        pretestdb_text_view = findViewById(R.id.pretestdb_text_view);
        success_loss_text_view = findViewById(R.id.success_loss_text_view);
        posttestdb_text_view = findViewById(R.id.posttestdb_text_view);
        play_how_long_text_view = findViewById(R.id.play_how_long_text_view);
        hint_text = findViewById(R.id.hint2_text);
        diffculty_text_view.setText(getString(R.string.level_difficulty, getLevelDifficulty(record_data.Game_diffculty)));
        pretestdb_text_view.setText(getString(R.string.pretest_db, record_data.Pretest_db));
        success_loss_text_view.setText(getsuccess_loss(record_data.Pass));
        posttestdb_text_view.setText(getString(R.string.post_test_db, record_data.Posttest_db));
        play_how_long_text_view.setText(getString(R.string.play_how_long, covertTime(record_data.Play_how_long)));
        handlerMeasure.sendEmptyMessage(60);

        gameHistoryListAdapter adapter = new gameHistoryListAdapter(record_data);

        PlayAgain.setOnClickListener(view -> {
            if (!countend) {
                hint_text.setVisibility(View.VISIBLE);

                return;
            }
            switch (this.record_data.Game_diffculty) {
                case 1:
                    next(1);
                    break;
                case 2:
                    next(2);
                    break;
                case 3:
                    next(3);
                    break;
            }

        });
        nextround_back_Button.setOnClickListener(view -> {
            if (!countend) {
                hint_text.setVisibility(View.VISIBLE);
                return;
            }
            switch (this.record_data.Game_diffculty) {
                case 1:
                    next(2);
                    break;
                case 2:
                    next(3);
                    break;
                case 3:
                    setBackHome();
                    break;
            }
        });
        new Thread(() -> {
            // TODO update
            GameDao dao = DatabaseManager.getInstance(this).gameDao();
            dao.addGame(record_data);
        }).start();
        RecyclerView audioListView = findViewById(R.id.game_audio);
        audioListView.setLayoutManager(new LinearLayoutManager(this));
        audioListView.setNestedScrollingEnabled(false);
        audioListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        audioListView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.releasePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.stopPlayer();
    }

    private String covertTime(Long time) {
        time /= 1000;
        long hour = time / 3600;
        time %= 3600;
        long min = time / 60;
        time %= 60;
        long sec = time;
        return String.format("%02d:%02d:%02d", hour, min, sec);
    }

    private String getLevelDifficulty(int difficulty) {
        switch (difficulty) {
            case 1:
                return "簡單";
            case 2:
                return "中等";
            case 3:
                return "困難";
            default:
                return "";
        }
    }

    private String getsuccess_loss(boolean success) {
        if (success) {
            return "成功";
        }
        return "失敗";
    }

    private void next(int difficulty) {
        Intent intent = new Intent(this, PretestCaption.class);
        intent.putExtra(NameManager.POST, false);
        Game data = new Game();
        data.Game_diffculty = difficulty;
        intent.putExtra(NameManager.RECORD_DATA, data);
        startActivity(intent);
    }

    private void setBackHome() {
        Intent intent = new Intent(this, MainPage.class);
        startActivity(intent);
    }

    private Handler handlerMeasure = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what >= 0) {
                hint_text.setText("剩餘休息時間:\n"+msg.what+"秒");
                handlerMeasure.sendEmptyMessageDelayed(msg.what - 1, 1000);

            } else {
                countend = true;
                PlayAgain.setBackgroundResource(R.drawable.logging_page_button_f);
                nextround_back_Button.setBackgroundResource(R.drawable.logging_page_button_f);
            }

            super.handleMessage(msg);
        }
    };

}
