package com.example.pdapp2022919.Database.ShortLine;

import android.content.Context;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.example.pdapp2022919.HealthManager.History.HistoryItem;
import com.example.pdapp2022919.SystemManager.FileManager2;
import com.example.pdapp2022919.SystemManager.FileType;
import com.example.pdapp2022919.SystemManager.MediaManager;
import com.example.pdapp2022919.net.Client;

import java.io.File;

@Entity(primaryKeys = {"uuid", "Short_recorder_time"})
public class ShortLine implements HistoryItem {

    @NonNull
    public String uuid;
    public long Short_recorder_time;

    public ShortLine() {
        this.uuid = Client.getUuid().toString();
        this.Short_recorder_time = System.currentTimeMillis();
    }

    @Override
    public String getHistoryName() {
        return "短句練習";
    }

    @Override
    public long getTime() {
        return Short_recorder_time;
    }

    @Override
    public void onItemClick(Context context) {
        File file = getContentFiles()[0];
        MediaManager.playAudio(file.getAbsolutePath(), null);
    }

    @Override
    public File[] getContentFiles() {
        return new File[] {
                new File(FileManager2.getWavPath(FileType.SHORT_LINE, this))
        };
    }
}
