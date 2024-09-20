package com.example.pdapp2022919.Database.ShortLine;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ShortLineDao {

    @Insert
    void addShortLine(ShortLine shortline);


//    @Query("SELECT * FROM game WHERE uuid LIKE :uuid " +
//            " AND short_recorder_time LIKE :time")
//    Game getGame(String uuid, long time);
//
    @Query("SELECT * FROM shortline WHERE uuid LIKE :uuid " +
            " AND Short_recorder_time BETWEEN :from AND :to" +
            " ORDER BY Short_recorder_time ASC ")
    List<ShortLine> getShortLines(String uuid, long from, long to);
//
    @Query("SELECT Short_recorder_time FROM shortline WHERE uuid LIKE :uuid ")
    List<Long> getAllTime(String uuid);

}
