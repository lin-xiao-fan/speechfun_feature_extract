package com.example.pdapp2022919;

import android.util.Log;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.mfcc.MFCC;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;

public class MFCCExtractor {

    private static final String TAG = "MFCCExtractor"; // 用於識別日誌消息的標籤

    public static List<Float> extractMFCC(String filePath) {
        List<float[]> rawFeatures = new ArrayList<>();
        List<Float> processedFeatures = new ArrayList<>();

        // 檢查文件路徑是否有效
        File audioFile = new File(filePath);
        if (!audioFile.exists() || !audioFile.isFile()) {
            Log.e(TAG, "無效的文件路徑: " + filePath);
            return processedFeatures;
        }

        Log.d(TAG, "有效的文件路徑: " + filePath );
        Log.d(TAG, "文件大小: " + audioFile.length() );
        // 在線程中運行音頻處理，避免阻塞 UI 線程
        new Thread(() -> {
            try {
                File ffmpegFile = new File("/data/user/0/com.dtx804lab.pdapp2022919/cache/ffmpeg");
                Log.d(TAG, "ffmpeg位置 : "+ ffmpegFile.getAbsolutePath() );
                Log.d(TAG, "ffmpeg是否可執行? : "+ ffmpegFile.canExecute() );
                File cacheDir = new File("/data");
                Log.d(TAG, "data directory : " + cacheDir.getAbsolutePath());
                Log.d(TAG, "data directory exists: " + cacheDir.exists());
                Log.d(TAG, "data directory readable: " + cacheDir.canRead());
                Log.d(TAG, "data directory writable: " + cacheDir.canWrite());
                Log.d(TAG, "data directory executable: " + cacheDir.canExecute());



                // 創建 AudioDispatcher 實例
                AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(filePath, 44100, 1024, 512);

                Log.d(TAG, "AudioDispatcher 實例創建成功"+ dispatcher );

                MFCC mfcc = new MFCC(1024, 44100);
                Log.d(TAG, "MFCC 實例已創建: " + mfcc.toString());
                // 添加 MFCC 處理器
                dispatcher.addAudioProcessor(mfcc);
                AudioProcessor audioProcessor = new AudioProcessor() {

                    @Override
                    public boolean process(AudioEvent audioEvent) {
                        Log.d(TAG, "process被調用");
                        float[] mfccValues = mfcc.getMFCC();

                        // 檢查是否返回了有效的 MFCC 特徵
                        Log.d(TAG, "處理到的 MFCC 特徵: " + Arrays.toString(mfccValues));

                        // 複製 MFCC 特徵數組以避免覆蓋
                        float[] mfccCopy = Arrays.copyOf(mfccValues, mfccValues.length);
                        rawFeatures.add(mfccCopy);

                        return true;
                    }

                    @Override
                    public void processingFinished() {
                        Log.d(TAG, "音頻處理完成");
                    }
                }

                ;

                dispatcher.addAudioProcessor(audioProcessor);
                Log.d(TAG, "AudioProcessor 添加成功: " + audioProcessor);


                // 啟動音頻處理
                Log.d(TAG, "開始處理音頻文件: " + filePath);
                dispatcher.run();
                Log.d(TAG, "處理結束 ");
                // 處理特徵數據
                if (!rawFeatures.isEmpty()) {
                    Log.d(TAG, "開始處理原始特徵數據");
                    processFeatures(rawFeatures, processedFeatures);
                } else {
                    Log.d(TAG, "未檢測到特徵數據");
                }

            } catch (Exception e) {
                Log.e(TAG, "音頻處理出錯", e);
            }
        }).start(); // 在新線程中運行



        return processedFeatures;
    }

    private static void processFeatures(List<float[]> rawFeatures, List<Float> processedFeatures) {
        int mfccSize = 13;
        int numFeatures = rawFeatures.size();
        float[][] mfccArray = new float[numFeatures][mfccSize];

        // 將 rawFeatures 轉換為二維數組
        for (int i = 0; i < numFeatures; i++) {
            mfccArray[i] = rawFeatures.get(i);
        }

        for (int i = 0; i < mfccSize; i++) {
            float[] featureColumn = new float[numFeatures];
            for (int j = 0; j < numFeatures; j++) {
                featureColumn[j] = mfccArray[j][i];
            }

            // 計算平均值、標準差、偏度、峰值
            DescriptiveStatistics stats = new DescriptiveStatistics();
            for (float value : featureColumn) {
                stats.addValue(value);
            }

            // 打印每一列特徵的統計信息
            Log.d(TAG, "特徵列 " + i + ": 平均值=" + stats.getMean() +
                    ", 標準差=" + stats.getStandardDeviation() +
                    ", 偏度=" + stats.getSkewness() +
                    ", 峰值=" + stats.getKurtosis());

            processedFeatures.add((float) stats.getMean());
            processedFeatures.add((float) stats.getStandardDeviation());
            processedFeatures.add((float) stats.getSkewness());
            processedFeatures.add((float) stats.getKurtosis());
        }
    }
}
