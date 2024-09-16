package com.example.pdapp2022919;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.example.pdapp2022919.HealthManager.HealthMangerList;
import com.example.pdapp2022919.SystemManager.FileManager2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PredictPage extends AppCompatActivity {

    private List<AudioFile> audioFiles = new ArrayList<>();
    private AudioListAdapter audioListAdapter;
    private TextView predictionResultTextView;
    private AudioFile selectedAudioFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict_test);
        Button backHome = findViewById(R.id.backhome);
        RecyclerView audioListRecyclerView = findViewById(R.id.audio_list);
        predictionResultTextView = findViewById(R.id.prediction_result);

        audioListAdapter = new AudioListAdapter(this, audioFiles, audioFile -> {
            selectedAudioFile = audioFile;
            // 选中音频文件时的处理逻辑
        });
        audioListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        audioListRecyclerView.setAdapter(audioListAdapter);

        Button startPredictionButton = findViewById(R.id.start_prediction_button);
        startPredictionButton.setOnClickListener(view -> {
            if (selectedAudioFile != null) {
                // 执行预测操作
                startPrediction(selectedAudioFile);
            }
        });
        backHome.setOnClickListener(view -> {
            startActivity(new Intent(this, HealthMangerList.class));
        });
        // 示例数据加载
        loadAudioFiles();
    }

    private void loadAudioFiles() {
        // 从 FileManager2 获取所有的 .wav 文件
        List<File> wavFiles = FileManager2.getAllWavFilesInAllFolders();

        // 清空现有的 audioFiles 列表
        audioFiles.clear();

        // 将所有的 .wav 文件添加到 audioFiles 列表中
        for (File wavFile : wavFiles) {
            String absolutePath = wavFile.getAbsolutePath();
            String[] pathParts = absolutePath.split("/");
            String date = pathParts[pathParts.length - 2];
            String fileName = pathParts[pathParts.length - 1];

            // 创建 AudioFile 对象，假设你有一个合适的构造函数
            // 这里假设 AudioFile 的构造函数接受文件名和文件路径
            audioFiles.add(new AudioFile( fileName , date , absolutePath ));
        }

        // 更新适配器
        audioListAdapter.notifyDataSetChanged();
    }


    private void startPrediction(AudioFile audioFile) {
        //獲取python實例
        Python py = Python.getInstance();
        PyObject myModule = py.getModule("test"); // "my_module" 是Python檔案名
        PyObject pyresult = myModule.callAttr("test", audioFile.getAbsolutePath() );

        String resultString = pyresult.toString();



        String result = MFCC_extract(audioFile); // 示例结果

        predictionResultTextView.setText(result);
    }

    private String MFCC_extract(AudioFile audioFile) {
        String file_path = audioFile.getAbsolutePath() ;
        List<Float> MFCC_features = MFCCExtractor.extractMFCC(file_path);
        List<Float> PM_features = ParselmouthExtract.extractFeatures(file_path);

        String result = "features size: " + PM_features.size(); // 示例结果
        return result ;
    }
}
