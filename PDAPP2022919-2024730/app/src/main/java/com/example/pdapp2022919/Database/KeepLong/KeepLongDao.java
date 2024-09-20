package com.example.pdapp2022919.Database.KeepLong;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pdapp2022919.Database.ShortLine.ShortLine;

import java.util.List;

@Dao
public interface KeepLongDao {

    @Insert
    void addKeepLong(KeepLong keeplong);


    //    @Query("SELECT * FROM game WHERE uuid LIKE :uuid " +
//            " AND short_recorder_time LIKE :time")
//    Game getGame(String uuid, long time);
//
    @Query("SELECT * FROM keeplong WHERE uuid LIKE :uuid " +
            " AND Keep_long_time BETWEEN :from AND :to" +
            " ORDER BY Keep_long_time ASC ")
    List<KeepLong> getKeepLong(String uuid, long from, long to);
    //
    @Query("SELECT Keep_long_time FROM keeplong WHERE uuid LIKE :uuid ")
    List<Long> getAllTime(String uuid);

}

