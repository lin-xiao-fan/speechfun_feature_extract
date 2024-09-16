package com.example.pdapp2022919.Game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

public class GameUtils {

    private GameUtils() {}

    public static Bitmap createBitmap(View view, int drawableId, int newWidth, int newHeight) {
        Bitmap myBitmap = BitmapFactory.decodeResource(view.getResources(), drawableId);
        myBitmap = Bitmap.createScaledBitmap(myBitmap, newWidth, newHeight, false);
        return myBitmap;
    }

}
