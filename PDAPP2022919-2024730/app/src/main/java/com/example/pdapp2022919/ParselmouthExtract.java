package com.example.pdapp2022919;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;

public class ParselmouthExtract {

    private static List<Double> amplitudes = new ArrayList<>();
    private static int pitchCount = 0; // 有效音高檢測數量

    public static List<Float> extractFeatures(String filePath) {
        List<Float> features = new ArrayList<>();
        File audioFile = new File(filePath);
        if (!audioFile.exists() || !audioFile.isFile()) {
            System.err.println("無效的文件路徑: " + filePath);
            return features; // 如果文件路徑無效，返回空列表
        }

        final CountDownLatch latch = new CountDownLatch(1); // 同步機制

        try {
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(filePath, 44100, 1024, 512);

            dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 44100, 1024, new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                    if (pitchDetectionResult.getPitch() != -1) {
                        double amplitude = calculateAmplitude(audioEvent);
                        amplitudes.add(amplitude);
                        pitchCount++;
                    }
                }
            }));

            dispatcher.addAudioProcessor(new AudioProcessor() {
                @Override
                public boolean process(AudioEvent audioEvent) {
                    return true;
                }

                @Override
                public void processingFinished() {
                    features.add((float) calculateLocalShimmer());
                    features.add((float) calculateAPQ3Shimmer());
                    latch.countDown(); // 處理完成後釋放鎖
                }
            });

            new Thread(() -> dispatcher.run()).start(); // 在新線程中運行 dispatcher

            latch.await(); // 等待處理完成

        } catch (Exception e) {
            e.printStackTrace();
        }

        return features;
    }

    private static double calculateAmplitude(AudioEvent audioEvent) {
        // 計算振幅的佔位符
        float[] buffer = audioEvent.getFloatBuffer();
        double sum = 0;
        for (float sample : buffer) {
            sum += sample * sample;
        }
        return Math.sqrt(sum / buffer.length);
    }

    private static double calculateLocalShimmer() {
        if (amplitudes.size() < 2) {
            return 0.0; // 數據不足以計算 shimmer
        }

        List<Double> shimmerDifferences = new ArrayList<>();
        for (int i = 1; i < amplitudes.size(); i++) {
            double shimmer = Math.abs(amplitudes.get(i) - amplitudes.get(i - 1));
            shimmerDifferences.add(shimmer);
        }

        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (double diff : shimmerDifferences) {
            stats.addValue(diff);
        }

        return stats.getMean(); // 返回平均 shimmer
    }

    private static double calculateAPQ3Shimmer() {
        if (amplitudes.size() < 3) {
            return 0.0; // 數據不足以計算 APQ3 Shimmer
        }

        List<Double> apq3ShimmerDifferences = new ArrayList<>();
        for (int i = 3; i < amplitudes.size(); i++) {
            double shimmer = Math.abs(amplitudes.get(i) - amplitudes.get(i - 3));
            apq3ShimmerDifferences.add(shimmer);
        }

        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (double diff : apq3ShimmerDifferences) {
            stats.addValue(diff);
        }

        return stats.getMean(); // 返回平均 shimmer
    }
}
