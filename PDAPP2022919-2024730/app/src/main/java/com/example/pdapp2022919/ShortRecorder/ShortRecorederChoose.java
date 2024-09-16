package com.example.pdapp2022919.ShortRecorder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.pdapp2022919.ListPage;
import com.example.pdapp2022919.MainPage;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;

public class ShortRecorederChoose extends ScreenSetting {
    private Button word_Button, sentance_Button, back_main_page_button8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_recoreder_choose);
        word_Button = findViewById(R.id.word_Button);
        sentance_Button = findViewById(R.id.santance_Button);
        back_main_page_button8 = findViewById(R.id.back_main_page_button8);

        back_main_page_button8.setOnClickListener(view -> openMainPage());
        sentance_Button.setOnClickListener(view -> {
            Intent intent = new Intent(this, ShortRecorderV2.class);
            intent.putExtra(NameManager.SHORT_RECORDER, getResources().getStringArray(R.array.short_sentence));
            startActivity(intent);
        });
        word_Button.setOnClickListener(view -> {
            Intent intent = new Intent(this, ShortRecorderV2.class);
            intent.putExtra(NameManager.SHORT_RECORDER, getResources().getStringArray(R.array.shortrecorder_word));
            startActivity(intent);

        });
    }

    private void openMainPage() {
        Intent intent = new Intent(this, ListPage.class);
        startActivity(intent);
    }
}