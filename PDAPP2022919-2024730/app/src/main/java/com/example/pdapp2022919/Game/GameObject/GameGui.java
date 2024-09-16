package com.example.pdapp2022919.Game.GameObject;

import static com.example.pdapp2022919.Game.GameUtils.createBitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.pdapp2022919.Game.GameView;
import com.example.pdapp2022919.R;

public class GameGui extends GameObject {

    private static final float LIGHT_SIZE = 250;

    private final Rect src;
    private final Bitmap redLight;
    private final Bitmap greenLight;
    private final Bitmap heart;
    private final RectF dest = new RectF(0, 0, LIGHT_SIZE, LIGHT_SIZE);
    private final Matrix matrix = new Matrix();

    private boolean inGap;
    private int heartAmount = 3;

    public GameGui(GameView view) {
        super(view);
        redLight = createBitmap(view, R.drawable.group127, 400, 400);
        greenLight = createBitmap(view, R.drawable.group129, 400, 400);
        heart = createBitmap(view, R.drawable.heart, 60, 55);
        src = new Rect(0, 0, redLight.getWidth() - 1, redLight.getHeight() - 1);
    }

    public void switchLight(boolean inGap) {
        this.inGap = inGap;
    }

    public void setHeartCount(int amount) {
        this.heartAmount = amount;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();
        // draw light
        matrix.setTranslate(
                (viewWidth - LIGHT_SIZE) * 0.5f,
                viewHeight * 0.13f
        );
        canvas.setMatrix(matrix);
        if (inGap) canvas.drawBitmap(greenLight, src, dest, null);
        else canvas.drawBitmap(redLight, src, dest, null);
        // draw heart
        matrix.setTranslate(viewWidth * 0.5f, viewHeight * 0.08f);
        canvas.setMatrix(matrix);
        for (int i = 0; i < heartAmount; i++) {
            canvas.drawBitmap(heart, -30 + 60 * (i - 1) + i * 10, 0, null);
        }
        canvas.restore();
    }
}
