package com.example.pdapp2022919;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FFmpegHelper {

    public static void copyFFmpegBinary(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        File ffmpegFile = new File(context.getFilesDir(), "ffmpeg");
        try (InputStream in = assetManager.open("ffmpeg/ffmpeg");
             FileOutputStream out = new FileOutputStream(ffmpegFile)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
        // 设置为可执行
        ffmpegFile.setExecutable(true);
    }
}
