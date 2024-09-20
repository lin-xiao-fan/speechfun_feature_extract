package com.example.pdapp2022919.HealthManager.History;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pdapp2022919.Database.Game.Game;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.MediaManager;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;

import java.text.SimpleDateFormat;

public class GameHistory extends ScreenSetting {

    private Button back_history;
    private TextView diffculty_text_view,success_loss_text_view;
    private TextView posttestdb_text_view,pretestdb_text_view,play_how_long_text_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_history);
        back_history = findViewById(R.id.back_history_page);
        back_history.setOnClickListener(view -> {
            startActivity( new Intent(this, Calendar.class));
        });
        hideSystemUI();
        Game record_data = getIntent().getParcelableExtra(NameManager.HISTORY_DATA);
        gameHistoryListAdapter adapter = new gameHistoryListAdapter(record_data);

        SimpleDateFormat titleFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        TextView dateText = findViewById(R.id.dateText);
        dateText.setText(titleFormat.format(record_data.start_play_time));

        diffculty_text_view = findViewById(R.id.diffculty_text_view);
        pretestdb_text_view = findViewById(R.id.pretestdb_text_view);
        success_loss_text_view = findViewById(R.id.success_loss_text_view);
        posttestdb_text_view = findViewById(R.id.posttestdb_text_view);
        play_how_long_text_view = findViewById(R.id.play_how_long_text_view);

        diffculty_text_view.setText(getString(R.string.level_difficulty, getLevelDifficulty(record_data.Game_diffculty)));
        pretestdb_text_view.setText(getString(R.string.pretest_db, record_data.Pretest_db));
        success_loss_text_view.setText(getsuccess_loss(record_data.Pass));
        posttestdb_text_view.setText(getString(R.string.post_test_db, record_data.Posttest_db));
        play_how_long_text_view.setText(getString(R.string.play_how_long, covertTime(record_data.Play_how_long)));

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
            return"成功";
        }
        return "失敗";
    }

}