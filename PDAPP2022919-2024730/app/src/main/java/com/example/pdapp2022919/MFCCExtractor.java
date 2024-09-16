package com.example.pdapp2022919;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.mfcc.MFCC;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;

public class MFCCExtractor {

    public static List<Float> extractMFCC(String filePath) {
        List<float[]> rawFeatures = new ArrayList<>();
        List<Float> processedFeatures = new ArrayList<>();

        // 檢查文件路徑是否有效
        File audioFile = new File(filePath);
        if (!audioFile.exists() || !audioFile.isFile()) {
            System.err.println("無效的文件路徑: " + filePath);
            return processedFeatures;
        }

        // 在線程中運行音頻處理，避免阻塞 UI 線程
        new Thread(() -> {
            try {
                // 創建 AudioDispatcher 實例
                AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(filePath, 44100, 1024, 512);

                MFCC mfcc = new MFCC(1024, 44100);

                // 添加 MFCC 處理器
                dispatcher.addAudioProcessor(mfcc);
                dispatcher.addAudioProcessor(new AudioProcessor() {
                    @Override
                    public boolean process(AudioEvent audioEvent) {
                        float[] mfccValues = mfcc.getMFCC();

                        // 檢查是否返回了有效的 MFCC 特徵

                        // 複製 MFCC 特徵數組以避免覆蓋
                        float[] mfccCopy = Arrays.copyOf(mfccValues, mfccValues.length);
                        rawFeatures.add(mfccCopy);

                        return true;
                    }

                    @Override
                    public void processingFinished() {
                    }
                });

                // 啟動音頻處理
                dispatcher.run();

                // 處理特徵數據
                if (!rawFeatures.isEmpty()) {
                    processFeatures(rawFeatures, processedFeatures);
                }

            } catch (Exception e) {
                e.printStackTrace();
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

            processedFeatures.add((float) stats.getMean());
            processedFeatures.add((float) stats.getStandardDeviation());
            processedFeatures.add((float) stats.getSkewness());
            processedFeatures.add((float) stats.getKurtosis());
        }
    }
}
