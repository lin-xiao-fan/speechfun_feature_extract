package com.example.pdapp2022919.Correction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.pdapp2022919.Database.Correction.Correction;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.Recode.WavRecorder;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;

import java.util.Objects;

public class EnvironmentTest extends ScreenSetting {

    private double db = 0;
    private Button nextstepButton;
    private TextView realTimeDBText;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    handler.sendEmptyMessageDelayed(1, 500);
//                    db = WavRecorder.getDB(WavRecorder.getMaxAmplitude());
                    WavRecorder.enableMark(false);
                    double avgdb = WavRecorder.getDB(WavRecorder.getMarkAverage());
                    WavRecorder.enableMark(true);
                    db = avgdb;
                    realTimeDBText.setText(String.format("%2.0f", avgdb));
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment_test);
        hideSystemUI();
        Correction correction = getIntent().getParcelableExtra(NameManager.CORRECTION_OBJ);
        nextstepButton = findViewById(R.id.nextStepButton);
        realTimeDBText = findViewById(R.id.realTimeDB);
        nextstepButton.setOnClickListener(view -> {
            correction.Environment_dB = db;
            Intent intent = new Intent(this,DistanceMeasure.class);
            intent.putExtra(NameManager.CORRECTION_OBJ, correction);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        WavRecorder.startRecording(null);
        handler.sendEmptyMessage(1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WavRecorder.stopRecording((String) null, null);
        handler.removeCallbacksAndMessages(null);
    }
}