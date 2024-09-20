package com.example.pdapp2022919.Recode;

import android.media.MediaRecorder;
import android.os.Build;

import java.io.File;
import java.io.IOException;

public class RecorderManager {

    private static boolean isRecoding = false;
    private static MediaRecorder mediaRecorder;

    /**開啟檢測*/
    public static void startMeasure(File file){
        if (isRecoding) return;
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mediaRecorder.setOutputFile(file);
        }
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecoding = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**關閉檢測*/
    public static void stopMeasure(){
        if (!isRecoding)return;
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
        isRecoding = false;
    }

    public static int getMaxRaw() {
        return mediaRecorder.getMaxAmplitude();
    }

    public static double dbTransfer(int raw) {
        double db = 20 * (Math.log10(Math.abs(raw)));
        if (Math.round(db) == -9223372036854775808.0) return 0;
        return db;
    }

}
