package com.example.pdapp2022919;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.example.pdapp2022919.HealthManager.HealthMangerList;
import com.example.pdapp2022919.SystemManager.AndroidFFMPEGLocator;
import com.example.pdapp2022919.SystemManager.FileManager2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PredictPage extends AppCompatActivity {

    private List<AudioFile> audioFiles = new ArrayList<>();
    private AudioListAdapter audioListAdapter;
    private TextView predictionResultTextView;
    private AudioFile selectedAudioFile;

    private static final String TAG = "PredictPage"; // 用於識別日誌消息的標籤

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
        String file_path_test = "" ;
        try {
            String fileName = "H01_0.wav"; // 你在 assets 中的 WAV 文件名
            file_path_test = copyAssetToCache(fileName); // 将文件从 assets 复制到缓存目录

        } catch (IOException e) {
            e.printStackTrace();
        }

        //獲取python實例
        Python py = Python.getInstance();
        PyObject myModule = py.getModule("test"); // "my_module" 是Python檔案名
        Double[] results = myModule.callAttr("feature_extract", file_path_test).toJava(Double[].class) ;
        //result[0]是特徵 re

        //PyObject features = results.get(0) ;   // 特征值列表
        //int length = results.get(1).toJava(int.class) ; // 特征长度
        /*
        for (int i = 0; i < length; i++) {
            // 获取特征值并转换为 Java double 类型
            //Log.d("Features ", "Feature " + i + ": " + features[i] );

        }

        */


        for (int i = 0; i < results.length ; i++) {
            Log.d("Features ", "Feature " + i + ": " + results[i] );
        }



        //String result = "features size: " + pyresult.size() ;
        //String result = String.valueOf(pyresult.size());
        //String result = MFCC_extract(audioFile); // 示例结果

        predictionResultTextView.setText(String.valueOf(  results.length ) );
    }

    private String MFCC_extract(AudioFile audioFile) {
        String file_path = audioFile.getAbsolutePath() ;
        String file_path_test ;
        try {
            String fileName = "H01_0.wav"; // 你在 assets 中的 WAV 文件名
            file_path_test = copyAssetToCache(fileName); // 将文件从 assets 复制到缓存目录

        } catch (IOException e) {
            e.printStackTrace();
            return "Error: Could not load WAV file from assets.";
        }


        List<Float> MFCC_features = MFCCExtractor.extractMFCC(file_path_test);
        List<Float> PM_features = ParselmouthExtract.extractFeatures(file_path);

        String result = "features size: " + MFCC_features.size(); // 示例结果
        return result ;
    }

    private String copyAssetToCache(String fileName) throws IOException {
        File outFile = new File(getFilesDir(), fileName);
        if (!outFile.exists()) {
            try (InputStream is = getAssets().open(fileName);
                 FileOutputStream fos = new FileOutputStream(outFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            }
        }
        return outFile.getAbsolutePath();
    }


}
