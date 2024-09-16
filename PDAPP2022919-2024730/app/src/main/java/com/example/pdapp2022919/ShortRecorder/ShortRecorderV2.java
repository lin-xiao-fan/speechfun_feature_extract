package com.example.pdapp2022919.ShortRecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pdapp2022919.Database.ShortLine.ShortLine;
import com.example.pdapp2022919.Database.ShortLine.ShortLineDao;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.Recode.WavRecorder;
import com.example.pdapp2022919.SystemManager.DatabaseManager;
import com.example.pdapp2022919.SystemManager.FileManager2;
import com.example.pdapp2022919.SystemManager.FileType;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;

public class ShortRecorderV2 extends ScreenSetting {
    private ImageView recorder_state2;
    private ShortRecorderViewModel viewModel;
    private Button playsound, next, finish;
    private TextView Numofshort, textView11;
    private ProgressBar progressBar3;
    private void startRecording() {
        WavRecorder.startRecording(() -> {
            runOnUiThread(() -> {
                if (!WavRecorder.isRecording()) recorder_state2.setVisibility(View.GONE);
                else recorder_state2.setVisibility(View.VISIBLE);
            });
        });
    }

    private void stopRecording() {
        // TODO 紀錄檔案位置
        ShortLine shortLine = new ShortLine();
        String path = FileManager2.getWavPath(FileType.SHORT_LINE, shortLine);
        WavRecorder.stopRecording(path);
        new Thread(() -> {
            ShortLineDao dao = DatabaseManager.getInstance(this).shortLineDao();
            dao.addShortLine(shortLine);
        }).start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortrecorderv2);
        viewModel = ViewModelProviders.of(this).get(ShortRecorderViewModel.class);

        String[] words = getIntent().getStringArrayExtra(NameManager.SHORT_RECORDER);
        viewModel.init(this.getApplication(), words);

        playsound = findViewById(R.id.playsound_Button);
        playsound.setOnClickListener(view -> viewModel.playVoice());
        next = findViewById(R.id.next_Button);
        finish = findViewById(R.id.Finish_Button1);
        next.setOnClickListener(view -> viewModel.next());
//        finish.setOnClickListener(view -> );
        Numofshort = findViewById(R.id.Numofshort);
        textView11 = findViewById(R.id.textView11);
        progressBar3 = findViewById(R.id.progressBar3);
        recorder_state2 = findViewById(R.id.recorder_state2);
        startRecording();
        viewModel.wordNumber.observe(this, number -> {
            Numofshort.setText(number + "/10");
            progressBar3.setProgress(number * 10);
            if (number == 10) {
                next.setBackground(AppCompatResources.getDrawable(this, R.drawable.group1491));
                next.setOnClickListener(view -> {
                    Intent intent = new Intent();
                    intent.setClass(this, ShortRecorederChoose.class);
                    startActivity(intent);
                });
            }
        });

        viewModel.word.observe(this, text -> {
            textView11.setText(text);
        });

        viewModel.isPlaySound.observe(this, enable -> {
            next.setEnabled(!enable);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRecording();
    }
}