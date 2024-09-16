package com.example.pdapp2022919.Database.Clock;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ClockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) //預設萬一執行出錯怎麼辦，REPLACE為覆蓋
    void addClock(Clock clock);

    @Update
    void updateClock(Clock clock);

    @Delete
    void deleteClock(Clock clock);

    @Query("SELECT * FROM clock WHERE uuid LIKE :uuid" +
            " ORDER BY hour ASC, minute ASC, clock_state ASC")
    List<Clock> getAllClock(String uuid);

    @Query("SELECT * FROM clock WHERE hour LIKE :hour AND" +
            " minute LIKE :min AND" +
            " clock_state LIKE :state")
    Clock getClock(int hour, int min, byte state);
}
