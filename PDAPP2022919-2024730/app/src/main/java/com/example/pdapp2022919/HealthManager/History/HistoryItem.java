package com.example.pdapp2022919.HealthManager.History;

import android.content.Context;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface HistoryItem {

    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");

    String getHistoryName();
    long getTime();
    void onItemClick(Context context);
    File[] getContentFiles();

    default String getTimeText() {
        return format.format(new Date(getTime()));
    }

}
