package com.example.pdapp2022919.Game.GameObject;

import static com.example.pdapp2022919.Game.GameUtils.createBitmap;

import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;

import com.example.pdapp2022919.Game.GameView;
import com.example.pdapp2022919.R;

public class Block extends GameObject {

    private static final int ROUND_VALUE = 80;

    private final BitmapShader grassShader;
    private final BitmapShader groundShader;

    private final Paint p = new Paint();
    private final Matrix matrix = new Matrix();
    public final int length;

    public Block(GameView view, int px, int py, int length) {
        super(view);
        this.x = px;
        this.y = py;
        this.length = length;

        grassShader = new BitmapShader(
                createBitmap(view, R.drawable.grass, 498, 100),
                Shader.TileMode.MIRROR, Shader.TileMode.MIRROR
        );

        groundShader = new BitmapShader(
                createBitmap(view, R.drawable.ground, 992, 452),
                Shader.TileMode.MIRROR, Shader.TileMode.MIRROR
        );
    }

    @Override
    public void onDraw(Canvas canvas) {
        matrix.setTranslate(x, y + 100);
        groundShader.setLocalMatrix(matrix);
        p.setShader(groundShader);
        canvas.drawRect(x, y + 50, x + length, viewHeight + 100, p);
        matrix.setTranslate(x, y - 1);
        grassShader.setLocalMatrix(matrix);
        p.setShader(grassShader);
        canvas.drawRoundRect(x, y, x + length, y + 100, ROUND_VALUE, ROUND_VALUE, p);
    }

}
