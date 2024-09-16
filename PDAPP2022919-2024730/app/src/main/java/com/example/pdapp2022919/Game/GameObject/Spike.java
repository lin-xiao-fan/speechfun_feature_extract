package com.example.pdapp2022919.Game.GameObject;

import static com.example.pdapp2022919.Game.GameUtils.createBitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.pdapp2022919.Game.GameView;
import com.example.pdapp2022919.R;

public class Spike extends GameObject {

    private final Bitmap spike;
    private final Rect src;
    private final RectF dest;
    private final Matrix matrix = new Matrix();

    public Spike(GameView view) {
        super(view);
        spike = createBitmap(view, R.drawable.spike, 1082, 113);
        src = new Rect(0, 0, spike.getWidth() - 1, spike.getHeight() - 1);
        dest = new RectF(0, viewHeight * 0.9f, viewWidth * 3, viewHeight);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();
        matrix.setTranslate(x, 0);
        canvas.setMatrix(matrix);
        canvas.drawBitmap(spike, src, dest, null);
        canvas.restore();
    }
}
