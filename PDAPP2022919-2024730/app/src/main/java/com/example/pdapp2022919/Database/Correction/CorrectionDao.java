package com.example.pdapp2022919.Database.Correction;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pdapp2022919.Database.Game.Game;


@Dao
public interface CorrectionDao{
    @Insert
    void addCorrection(Correction correction);
//不需要取出來用在APP裡所以不用@Query

}