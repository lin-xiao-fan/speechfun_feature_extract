package com.example.pdapp2022919;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.pdapp2022919.Database.Game.Game;
import com.example.pdapp2022919.Game.ChooseLevel;
import com.example.pdapp2022919.Game.Game1;
import com.example.pdapp2022919.Game.PretestCaption;
import com.example.pdapp2022919.KeepLong.KeepLongPage;
import com.example.pdapp2022919.PitchGame.PitchGameMain;
import com.example.pdapp2022919.ShortRecorder.ShortRecorederChoose;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;

public class ListPage extends ScreenSetting {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_page);
        hideSystemUI();
        Button backmainpageButton = findViewById(R.id.back_main_page_button);
        Button voicepButton= findViewById(R.id.voice_practice_button);
        Button PitchGameButton= findViewById(R.id.high_low_practice_button);
        Button shortrecorder = findViewById(R.id.shortrecorder_Button);
        Button keeplong = findViewById(R.id.keep_long_button);

        backmainpageButton.setOnClickListener(view -> openMain_page());
        voicepButton.setOnClickListener(view -> openChooseLevel(1));
        PitchGameButton.setOnClickListener(view -> openPitchGameMain());
        shortrecorder.setOnClickListener(view -> openrecorder());
        keeplong.setOnClickListener(view -> openkeeplong());
    }

    private void  openMain_page(){
        Intent intent = new Intent(this, MainPage.class);
        startActivity(intent);
    }

    private void openChooseLevel(int difficulty){
        Intent intent = new Intent(this,PretestCaption.class);
        Game data = new Game();
        data.Game_diffculty = difficulty;
        intent.putExtra(NameManager.RECORD_DATA, data);
        startActivity(intent);
    }
    private void openPitchGameMain(){
        Intent intent = new Intent(this, PitchGameMain.class);
        startActivity(intent);
    }

    private void debug(){
        Intent intent = new Intent(this, Game1.class);
        Game data = new Game();
        data.Game_diffculty = 1;
        data.Pretest_db = 45.0;
        data.Posttest_db = 60.0;
        data.Pass = true;
        intent.putExtra(NameManager.RECORD_DATA, data);
        startActivity(intent);
    }
    private void openrecorder(){
        Intent intent = new Intent(this, ShortRecorederChoose.class);
        startActivity(intent);
    }
    private void openkeeplong(){
        Intent intent = new Intent(this, KeepLongPage.class);
        startActivity(intent);
    }
    private void next(int difficulty) {
        Intent intent = new Intent(this, PretestCaption.class);
        Game data = new Game();
        data.Game_diffculty = difficulty;
        intent.putExtra(NameManager.RECORD_DATA, data);
        startActivity(intent);
    }

}
