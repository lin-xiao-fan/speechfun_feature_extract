package com.example.pdapp2022919.Database.Game;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GameDao {
//加入新資料 @Insert
    @Insert
    void addGame(Game game);

//拿出來(有條件
    @Query("SELECT * FROM game WHERE uuid LIKE :uuid " +
            " AND start_play_time LIKE :time")
    Game getGame(String uuid, long time);

    @Query("SELECT * FROM game WHERE uuid LIKE :uuid " +
            " AND start_play_time BETWEEN :from AND :to" +
            " ORDER BY start_play_time ASC ")
    List<Game> getGames(String uuid, long from, long to);

    @Query("SELECT start_play_time FROM game WHERE uuid LIKE :uuid ")
    List<Long> getAllTime(String uuid);

    @Delete
    void deleteGame(Game game);

}
