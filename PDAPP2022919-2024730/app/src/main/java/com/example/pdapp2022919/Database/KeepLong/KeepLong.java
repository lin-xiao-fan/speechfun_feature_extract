package com.example.pdapp2022919.Database.KeepLong;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.example.pdapp2022919.HealthManager.History.HistoryItem;
import com.example.pdapp2022919.HealthManager.History.KeepLongHistory;
import com.example.pdapp2022919.SystemManager.FileManager2;
import com.example.pdapp2022919.SystemManager.FileType;
import com.example.pdapp2022919.SystemManager.MediaManager;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.net.Client;

import java.io.File;

@Entity(primaryKeys = {"uuid", "Keep_long_time"})
public class KeepLong implements HistoryItem, Parcelable {

    @NonNull
    public String uuid;
    public long Keep_long_time;

    public KeepLong() {
        this.uuid = Client.getUuid().toString();
        this.Keep_long_time = System.currentTimeMillis();
    }

    protected KeepLong(Parcel in) {
        uuid = in.readString();
        Keep_long_time = in.readLong();
    }

    public static final Creator<KeepLong> CREATOR = new Creator<KeepLong>() {
        @Override
        public KeepLong createFromParcel(Parcel in) {
            return new KeepLong(in);
        }

        @Override
        public KeepLong[] newArray(int size) {
            return new KeepLong[size];
        }
    };

    @Override
    public String getHistoryName() {
        return "拉長音訓練";
    }

    @Override
    public long getTime() {
        return Keep_long_time;
    }

    @Override
    public void onItemClick(Context context) {
        Intent intent = new Intent(context, KeepLongHistory.class);
        intent.putExtra(NameManager.HISTORY_DATA,this);
        context.startActivity(intent);
//        File file = getContentFiles()[0];
//        MediaManager.playAudio(file.getAbsolutePath(), null);
    }

    @Override
    public File[] getContentFiles() {
        File folder = new File(FileManager2.getFolder(FileType.KEEP_LONG_a, this));
        return folder.listFiles();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeLong(Keep_long_time);
    }
}
