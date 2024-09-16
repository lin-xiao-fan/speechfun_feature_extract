package com.example.pdapp2022919.Game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.pdapp2022919.Database.Game.Game;
import com.example.pdapp2022919.ListPage;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;

public class ChooseLevel extends ScreenSetting {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_level);
        hideSystemUI();
        Button easy = findViewById(R.id.word_Button);
        Button medium = findViewById(R.id.levl2_Button);
        Button hard = findViewById(R.id.santance_Button);
        Button back_main_page_button8 =findViewById(R.id.back_main_page_button8);
        easy.setOnClickListener(view -> next(1));
        medium.setOnClickListener(view -> next(2));
        hard.setOnClickListener(view -> next(3));
        back_main_page_button8.setOnClickListener(view -> {
            startActivity(new Intent(this,ListPage.class));
        });
    }

    private void next(int difficulty) {
        Intent intent = new Intent(this,PretestCaption.class);
        Game data = new Game();
        data.Game_diffculty = difficulty;
        intent.putExtra(NameManager.RECORD_DATA, data);
        startActivity(intent);
    }

}