package com.example.pdapp2022919.Game.GameObject;

import android.graphics.Canvas;

import com.example.pdapp2022919.Game.GameView;

public abstract class GameObject {

    protected final int viewWidth, viewHeight;
    public int x, y;

    public GameObject(GameView view) {
        viewWidth = view.getViewWidth();
        viewHeight = view.getViewHeight();
    }

    public abstract void onDraw(Canvas canvas);

}
