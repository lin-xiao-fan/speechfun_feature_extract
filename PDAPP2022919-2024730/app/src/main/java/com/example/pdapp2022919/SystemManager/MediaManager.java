package com.example.pdapp2022919.SystemManager;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;

public class MediaManager {

    private static MediaMetadataRetriever retriever;
    private static MediaPlayer player;
    private static String playingFile = "";

    public static void releasePlayer() {
        if (player == null) return;
        player.release();
        player = null;
    }

    public static void releaseMetaRetriever() {
        if (retriever == null) return;
        try {
            retriever.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
        retriever = null;
    }

    public static void playAudio(String fileName, Runnable callback) {
        if (player == null) player = new MediaPlayer();
        player.setOnCompletionListener((mediaPlayer -> {
            playingFile = "";
            if (callback != null) callback.run();
        }));
        player.stop();
        player.reset();
        if (playingFile.equals(fileName)) {
            playingFile = "";
            return;
        }
        try {
            player.setDataSource(fileName);
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();
        playingFile = fileName;
    }

    public static void stopPlayer() {
        if (player == null) return;
        player.stop();
        player.reset();
        playingFile = "";
    }

    public static int getMediaDuration(File file) {
        if (retriever == null) retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getAbsolutePath());
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        System.out.println(file.getAbsolutePath() + " : " + duration);
        return Integer.parseInt(duration);
    }

    public static String milliTimeToText(long time) {
        time /= 1000;
        long hour = time / 3600;
        time %= 3600;
        long min = time / 60;
        time %= 60;
        long sec = time;
        return String.format("%02d:%02d:%02d", hour, min, sec);
    }

}
