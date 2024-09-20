package com.example.pdapp2022919;

public class AudioFile {
    private final String name;
    private final String date;

    private final String AbsolutePath;
    public AudioFile(String name, String date, String AbsolutePath) {
        this.name = name;
        this.date = date;
        this.AbsolutePath = AbsolutePath;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getAbsolutePath() {
        return AbsolutePath;
    }
}
