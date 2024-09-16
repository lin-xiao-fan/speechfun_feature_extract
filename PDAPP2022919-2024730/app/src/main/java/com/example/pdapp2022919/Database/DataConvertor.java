package com.example.pdapp2022919.Database;

import androidx.room.TypeConverter;

import java.util.Arrays;

public class DataConvertor {

    @TypeConverter
    public static String booleanArray2String(boolean[] array) {
        return Arrays.toString(array);
    }

    @TypeConverter
    public static boolean[] String2booleanArray(String str) {
        String[] array = str
                .replace("[", "")
                .replace("]", "")
                .replaceAll(" ", "")
                .split(",");
        boolean[] result = new boolean[array.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = array[i].equals("true");
        }
        return result;
    }

}
