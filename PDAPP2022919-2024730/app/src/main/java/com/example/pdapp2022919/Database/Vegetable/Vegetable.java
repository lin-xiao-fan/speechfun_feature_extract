package com.example.pdapp2022919.Database.Vegetable;

import com.example.pdapp2022919.R;

public enum Vegetable {

    CABBAGE(R.string.cabbage, R.drawable.cabbage),
    WATERMELON(R.string.watermelon,R.drawable.image_99),
    BANANA(R.string.banana,R.drawable.image_100),
    LEMON(R.string.lemon,R.drawable.image_102),
    GUAVA(R.string.guava,R.drawable.image_105),
    PASSION_FRUIT(R.string.passion_fruit,R.drawable.image_83),
    LONGAN(R.string.longan,R.drawable.image_94),
    GREEN_APPLE(R.string.green_apple,R.drawable.image_98),
    PURPLE_CABBAGE(R.string.purple_cabbage,R.drawable.image_96__1_),
    ORANGE(R.string.orange,R.drawable.image_107),
    GREEN_GRAPE(R.string.green_grape,R.drawable.image_76),
    TOMATO(R.string.tomato,R.drawable.image_106);

    public final int nameID;
    public final int drawableID;

    Vegetable(int nameID, int drawableID) {
        this.nameID = nameID;
        this.drawableID = drawableID;
    }

    public static Vegetable getVegetable(int number) {
        if (number >= values().length) return null;
        return values()[number];
    }

}
