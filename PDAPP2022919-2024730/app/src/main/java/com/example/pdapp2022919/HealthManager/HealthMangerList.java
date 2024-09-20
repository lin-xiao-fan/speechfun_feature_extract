package com.example.pdapp2022919.HealthManager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.example.pdapp2022919.PredictPage ;
import com.example.pdapp2022919.Correction.EnvironmentTest;
import com.example.pdapp2022919.HealthManager.AlarmClock.ClockList;
import com.example.pdapp2022919.HealthManager.History.Calendar;
import com.example.pdapp2022919.MainPage;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.ScreenSetting;


public class HealthMangerList extends ScreenSetting {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_manger_list);
        Button back_main_page_button4= (Button) findViewById(R.id.back_main_page_button4);
        Button personal_button= (Button) findViewById(R.id.personal_button);
//        Button alarmclock_button= (Button) findViewById(R.id.alarmclock_button);
        Button QRecord_button= (Button) findViewById(R.id.QRecord_button);
        Button history_Button= (Button) findViewById(R.id.history_Button);
        Button predict_Button= (Button) findViewById(R.id.predict_button);


        back_main_page_button4.setOnClickListener(view -> {
            startActivity(new Intent(this, MainPage.class));
        });
        personal_button.setOnClickListener(view -> {
            startActivity(new Intent(this,PersonalData.class));
        });
//        alarmclock_button.setOnClickListener(view -> {
//            startActivity(new Intent(this,ClockList.class));
//        });
        QRecord_button.setOnClickListener(view -> {
            startActivity(new Intent(this,QRecoder.class));
        });
        history_Button.setOnClickListener(view -> {
            startActivity(new Intent(this, Calendar.class));
        });
        predict_Button.setOnClickListener(view -> {
            startActivity(new Intent(this, PredictPage.class));
        });


    }
}