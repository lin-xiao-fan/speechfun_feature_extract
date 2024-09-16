package com.example.pdapp2022919.Database.Questionnaire;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.example.pdapp2022919.net.Client;

@Entity(primaryKeys = {"uuid", "time"})
public class Questionnaire  {
    @NonNull
    public String uuid;
    public long time;
    public String q_type;
    public int q_score;
    public String q_answer;

    public Questionnaire(long time, String q_type, int q_score, String q_answer) {
        this.uuid = Client.getUuid().toString();
        this.time = time;
        this.q_type = q_type;
        this.q_score = q_score;
        this.q_answer = q_answer;
    }
}