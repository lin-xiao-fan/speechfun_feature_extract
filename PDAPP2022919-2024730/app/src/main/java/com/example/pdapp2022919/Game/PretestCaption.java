package com.example.pdapp2022919.Game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.pdapp2022919.ListPage;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.ScreenSetting;

public class PretestCaption extends ScreenSetting {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pretest_caption);
        hideSystemUI();
        Button pretestCaption = (Button) findViewById(R.id.caption_ok);
        Button back_main_page_button9 = (Button) findViewById(R.id.back_main_page_button9);
        pretestCaption.setOnClickListener(view -> {
            Intent intent = new Intent(PretestCaption.this, RecorderTest.class);
            intent.putExtras(getIntent());
            startActivity(intent);
        });
        back_main_page_button9.setOnClickListener(view -> {
            startActivity(new Intent(this, ListPage.class));
        });
    }
}