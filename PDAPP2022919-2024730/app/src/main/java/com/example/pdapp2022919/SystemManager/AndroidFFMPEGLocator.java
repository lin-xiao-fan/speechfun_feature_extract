package com.example.pdapp2022919.SystemManager;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

// 處理 TarsosDsp 所需的 ffmpeg 檔案，檔案放在 assets
public class AndroidFFMPEGLocator {

    private static final String TAG = "AndroidFFMPEGLocator";

    public AndroidFFMPEGLocator(Context context) {
        Log.d(TAG, "AndroidFFMPEGLocator initialized.");

        CPUArchitecture architecture = getCPUArchitecture();

        if (architecture != null) {
            Log.i(TAG, "Detected Native CPU Architecture: " + architecture.name());
        } else {
            Log.e(TAG, "Could not detect CPU architecture.");
        }

        if (!ffmpegIsCorrectlyInstalled(context)) {
            String ffmpegFileName = getFFMPEGFileName(architecture);
            Log.d(TAG, "FFmpeg file to be extracted: " + ffmpegFileName);

            AssetManager assetManager = context.getAssets();
            unpackFFmpeg(assetManager, ffmpegFileName, context);
        }

        File ffmpegTargetLocation = AndroidFFMPEGLocator.ffmpegTargetLocation(context);
        ffmpegTargetLocation.setExecutable(true);
        Log.d(TAG, "ffmpeg權限設置完成");
        Log.i(TAG, "FFmpeg binary location: " + ffmpegTargetLocation.getAbsolutePath() +
                " is executable? " + ffmpegTargetLocation.canExecute() +
                " size: " + ffmpegTargetLocation.length() + " bytes");
    }

    private String getFFMPEGFileName(CPUArchitecture architecture) {
        final String ffmpegFileName;
        switch (architecture) {
            case X86:
            case X86_64:
                ffmpegFileName = "x86_ffmpeg";
                break;
            case ARMEABI_V7A:
                ffmpegFileName = "armeabi-v7a_ffmpeg";
                break;
            case ARMEABI_V7A_NEON:
                ffmpegFileName = "armeabi-v7a-neon_ffmpeg";
                break;
            default:
                ffmpegFileName = null;
                String message = "Could not determine your processor architecture correctly, no ffmpeg binary available.";
                Log.e(TAG, message);
                throw new Error(message);
        }
        return ffmpegFileName;
    }

    private boolean ffmpegIsCorrectlyInstalled(Context context) {
        File ffmpegTargetLocation = AndroidFFMPEGLocator.ffmpegTargetLocation(context);
        boolean isInstalled = ffmpegTargetLocation.exists() &&
                ffmpegTargetLocation.canExecute() &&
                ffmpegTargetLocation.length() > 1000000;
        Log.d(TAG, "FFmpeg installed: " + isInstalled);
        return isInstalled;
    }

    private void unpackFFmpeg(AssetManager assetManager, String ffmpegAssetFileName, Context context) {
        Log.d(TAG, "Starting to unpack FFmpeg binary...");
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            File ffmpegTargetLocation = AndroidFFMPEGLocator.ffmpegTargetLocation(context);
            inputStream = assetManager.open(ffmpegAssetFileName);
            outputStream = new FileOutputStream(ffmpegTargetLocation);
            byte buffer[] = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            ffmpegTargetLocation.setExecutable(true);
            Log.d(TAG, "ffmpeg權限設置完成");
            Log.i(TAG, "Unpacked FFmpeg binary " + ffmpegAssetFileName +
                    ", extracted " + ffmpegTargetLocation.length() +
                    " bytes. Extracted to: " + ffmpegTargetLocation.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Error while unpacking FFmpeg", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error closing input stream", e);
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error closing output stream", e);
            }
        }
    }

    private static final File ffmpegTargetLocation(Context context) {
        File filesDir = context.getFilesDir();
        File ffmpegTargetLocation = new File(filesDir, "ffmpeg");
        Log.d(TAG, "FFmpeg target location: " + ffmpegTargetLocation.getAbsolutePath());
        return ffmpegTargetLocation;
    }

    private enum CPUArchitecture {
        X86, ARMEABI_V7A, ARMEABI_V7A_NEON, X86_64;
    }

    private boolean isCPUArchitectureSupported(String alias) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (String supportedAlias : Build.SUPPORTED_ABIS) {
                if (supportedAlias.equals(alias) || (alias.equals("x86") && supportedAlias.equals("x86_64"))) {
                    Log.d(TAG, alias + " architecture supported.");
                    return true;
                }
            }
            Log.d(TAG, alias + " architecture not supported.");
            return false;
        } else {
            boolean isSupported = Build.CPU_ABI.equals(alias) || (alias.equals("x86") && Build.CPU_ABI.equals("x86_64"));
            Log.d(TAG, "Build version < Lollipop. Architecture " + alias + " supported: " + isSupported);
            return isSupported;
        }
    }

    private CPUArchitecture getCPUArchitecture() {
        Log.d(TAG, "Detecting CPU architecture...");
        if (isCPUArchitectureSupported("x86")) {
            return CPUArchitecture.X86;
        } else if (isCPUArchitectureSupported("armeabi-v7a")) {
            if (isNeonSupported()) {
                return CPUArchitecture.ARMEABI_V7A_NEON;
            } else {
                return CPUArchitecture.ARMEABI_V7A;
            }
        }
        Log.e(TAG, "Unsupported CPU architecture.");
        return null;
    }

    private boolean isNeonSupported() {
        Log.d(TAG, "Checking if NEON is supported...");
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/proc/cpuinfo"))));
            String line;
            while ((line = input.readLine()) != null) {
                Log.d(TAG, "CPUINFO line: " + line);
                if (line.toLowerCase().contains("neon")) {
                    Log.d(TAG, "NEON support detected.");
                    input.close();
                    return true;
                }
            }
            input.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading /proc/cpuinfo", e);
        }
        Log.d(TAG, "NEON support not detected.");
        return false;
    }

    public void check(Context context) {
        File ffmpegTargetLocation = AndroidFFMPEGLocator.ffmpegTargetLocation(context);
        Log.d(TAG, "FFmpeg 執行權限設置 : " + ffmpegTargetLocation.canExecute());
        Log.d(TAG, "FFmpeg 寫入權限設置 : " + ffmpegTargetLocation.canWrite());
        Log.d(TAG, "FFmpeg 讀取權限設置 : " + ffmpegTargetLocation.canRead());
        if (!ffmpegTargetLocation.canExecute() || !ffmpegTargetLocation.canWrite() || !ffmpegTargetLocation.canRead()) {
            ffmpegTargetLocation.setExecutable(true);
            ffmpegTargetLocation.setReadable(true);
            ffmpegTargetLocation.setWritable(true);
            Log.d(TAG, "FFmpeg 權限修改");
        }
        Log.d(TAG, "FFmpeg 執行權限設置 : " + ffmpegTargetLocation.canExecute());
        Log.d(TAG, "FFmpeg 寫入權限設置 : " + ffmpegTargetLocation.canWrite());
        Log.d(TAG, "FFmpeg 讀取權限設置 : " + ffmpegTargetLocation.canRead());
    }
}
