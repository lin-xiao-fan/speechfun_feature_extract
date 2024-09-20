package com.example.pdapp2022919.SystemManager;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.FileObserver;

import com.example.pdapp2022919.HealthManager.History.HistoryItem;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

public class FileManager2 {

    private static String fileDir;
    private static final String hintVoiceDir = "/hint_voice";

    public static void setFileDir(String path) {
        fileDir = path;
    }

    public static String getFolder(FileType type, HistoryItem item) {
        String folder = fileDir + "/" + type.fileType.toLowerCase(Locale.ROOT) + "/"  + item.getTimeText();
        createFolder(folder);
        return folder;
    }

    public static String getWavPath(FileType type, HistoryItem item) {
        return getFolder(type, item) + "/" + type.fileName + ".wav";
    }

    public static String getWavPath(FileType type, HistoryItem item, int index) {
        if (index <= 0) {
            return getWavPath(type, item);
        }
        return getFolder(type, item) + "/" + type.fileName + "-" + index + ".wav";
    }

    public static String getTempRaw() {
        String folder = fileDir + "/temp";
        createFolder(folder);
        return folder + "/temp_record.raw";
    }

    public static boolean deleteFile(File[] files) {
        if (files != null) {
            for (File file : files) {
                if (!file.delete()) return false;
            }
        }
        return true;
    }

    public static void createHintVoice(Context context) {
        try {
            String folderPath = fileDir + hintVoiceDir;
            createFolder(folderPath);
            String[] voicePaths = context.getAssets().list("hint_voice");
            assert voicePaths != null;
            for (String path : voicePaths) {
                String newPath = folderPath + path;
                InputStream is = context.getAssets().open("hint_voice/" + path);
                FileOutputStream fos = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int byteCount = is.read(buffer);
                while (byteCount != -1) {
                    fos.write(buffer, 0, byteCount);
                    byteCount = is.read(buffer);
                }
                fos.flush();
                is.close();
                fos.close();
            }
            System.out.println("finish");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getHintVoicePath() {
        return fileDir + hintVoiceDir + "start_hint_voice.mp3";
    }

    private static void createFolder(String path) {
        File dir = new File(path);
        if (dir.exists()) return;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.createDirectories(Paths.get(path));
            }
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    public static File[] getAllAudioFiles(String folderPath) {
        File folder = new File(folderPath);
        return folder.listFiles((dir, name) -> name.endsWith(".wav")); // 過濾和獲取.wav
    }

    public static List<File> getAllWavFilesInAllFolders() {
        List<File> wavFiles = new ArrayList<>();
        File rootFolder = new File(fileDir);

        if (rootFolder.exists() && rootFolder.isDirectory()) {
            collectWavFiles(rootFolder, wavFiles);
        }

        return wavFiles;
    }

    private static void collectWavFiles(File folder, List<File> wavFiles) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {

                    collectWavFiles(file, wavFiles);
                } else if (file.isFile() && file.getName().endsWith(".wav")) {
                    wavFiles.add(file);
                }
            }
        }
    }



}
