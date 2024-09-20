package com.example.pdapp2022919.Database.Questionnaire;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.pdapp2022919.Database.Clock.Clock;

import java.util.List;

@Dao
public interface QuestionnaireDao {

    @Insert
    void addQuestionnaire(Questionnaire questionnaire);

    @Query("SELECT * FROM questionnaire WHERE uuid LIKE :uuid" +
            " ORDER BY time DESC")
    List<Questionnaire> getAllQuestionnaire(String uuid);

    @Query("SELECT * FROM questionnaire WHERE uuid LIKE :uuid" +
            " AND q_type LIKE :type" +
            " ORDER BY time DESC")
    List<Questionnaire> getTypeQuestionnaire(String uuid, String type);

}
