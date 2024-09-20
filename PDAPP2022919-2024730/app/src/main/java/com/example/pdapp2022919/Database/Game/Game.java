package com.example.pdapp2022919.Database.Game;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.example.pdapp2022919.HealthManager.History.GameHistory;
import com.example.pdapp2022919.HealthManager.History.HistoryItem;
import com.example.pdapp2022919.SystemManager.FileManager2;
import com.example.pdapp2022919.SystemManager.FileType;
import com.example.pdapp2022919.SystemManager.MediaManager;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.net.Client;

import java.io.File;
import java.util.ArrayList;

@Entity(primaryKeys = {"uuid", "start_play_time"})
//Parcelable跳轉頁面可傳資料
public class Game implements HistoryItem, Parcelable {

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    @NonNull
    public String uuid;
    public long start_play_time;
    public long stop_play_time;
    public long Play_how_long;
    public int Game_diffculty;
    public double Pretest_db;
    public double Posttest_db;
    public boolean Pass;

    public Game() {
        this.uuid = Client.getUuid().toString();
        this.start_play_time = System.currentTimeMillis();
    }

    @Ignore
    protected Game(Parcel in) {
        uuid = in.readString();
        start_play_time = in.readLong();
        stop_play_time = in.readLong();
        Play_how_long = in.readLong();
        Game_diffculty = in.readInt();
        Pretest_db = in.readDouble();
        Posttest_db = in.readDouble();
        Pass = in.readByte() != 0;
    }

    public String getPlayTimeText() {
        return MediaManager.milliTimeToText(Play_how_long);
    }

    public String getDifficultText(){
        switch (Game_diffculty){
            case 1:
                return "簡單";
            case 2:
                return "中等";
            case 3:
                return "困難";
            default:
                return "";
        }
    }

    @Override
    public String getHistoryName() {
        return "音量練習";
    }

    @Override
    public long getTime() {
        return start_play_time;
    }

    @Override
    public void onItemClick(Context context) {
        Intent intent = new Intent(context, GameHistory.class);
        intent.putExtra(NameManager.HISTORY_DATA, this);
        context.startActivity(intent);
    }

    @Override
    public File[] getContentFiles() {
        File folder = new File(FileManager2.getFolder(FileType.PRETEST, this));
        return folder.listFiles();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeLong(start_play_time);
        parcel.writeLong(stop_play_time);
        parcel.writeLong(Play_how_long);
        parcel.writeInt(Game_diffculty);
        parcel.writeDouble(Pretest_db);
        parcel.writeDouble(Posttest_db);
        parcel.writeByte((byte) (Pass ? 1 : 0));
    }
}
