package com.example.pdapp2022919.SystemManager;

public enum FileType {

    PRETEST("game", "pretest"),
    POSTTEST("game", "posttest"),
    GAME_ONE("game", "game1"),
    GAME_TWO("game", "game2"),
    GAME_THREE("game", "game3"),
    SHORT_LINE("short_line", "short_line"),
    KEEP_LONG_a("keep_long","keep_long_a"),
    KEEP_LONG_i("keep_long","keep_long_i"),
    KEEP_LONG_u("keep_long","keep_long_u");



    public final String fileType;
    public final String fileName;

    FileType(String type, String name) {
        this.fileType = type;
        this.fileName = name;
    }

}
