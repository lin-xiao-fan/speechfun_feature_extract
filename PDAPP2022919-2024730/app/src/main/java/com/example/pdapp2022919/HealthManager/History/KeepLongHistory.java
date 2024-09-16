package com.example.pdapp2022919.HealthManager.History;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pdapp2022919.Database.KeepLong.KeepLong;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.MediaManager;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;

import java.text.SimpleDateFormat;

public class KeepLongHistory extends ScreenSetting {
    private RecyclerView keeplongList;
    private TextView time_word;
    private Button back_history_page_k;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_long_history);

        SimpleDateFormat titleFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        KeepLong record_data = getIntent().getParcelableExtra(NameManager.HISTORY_DATA);
        KeepLongAdapter adapter = new KeepLongAdapter(record_data);

        time_word =findViewById(R.id.time_keep_long_text);
        time_word.setText(titleFormat.format(record_data.Keep_long_time));
        back_history_page_k = findViewById(R.id.back_history_page_k);
        back_history_page_k.setOnClickListener(view->{
            finish();
        });

        keeplongList = findViewById(R.id.keeplongList);
        keeplongList.setLayoutManager(new LinearLayoutManager(this));
        keeplongList.setNestedScrollingEnabled(false);
        keeplongList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        keeplongList.setAdapter(adapter);
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
}