package com.example.pdapp2022919.Game;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.pdapp2022919.Game.GameObject.Block;
import com.example.pdapp2022919.Game.GameObject.GameGui;
import com.example.pdapp2022919.Game.GameObject.Player;
import com.example.pdapp2022919.Game.GameObject.Spike;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.FileManager2;
import com.example.pdapp2022919.SystemManager.MediaManager;

import java.util.Random;

public class GameView extends View {

    private static final Random random = new Random();
    private static final int xSpeed = 10;

    private int level_difficulty;
    private int viewWidth, viewHeight;
    private int ground;
    private int blockCount = 0, failCount = 0;
    private int xInit;
    private double stander;
    private boolean isGameOver = false;

    private final Block[] blocks = new Block[4];
    private Player player;
    private GameGui gui;
    private Spike spike;
    private boolean isPlayVoice = false;

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void update(double volume) {
        if (isGameOver) return;
        volume = 20 * (Math.log10(Math.abs(volume)));
        Block nowBlock = blocks[blockCount];
        // move ball
        player.move(volume, getLimit(volume), nowBlock);
        if (viewWidth <= 0) return;
        // play hint
        if (!isPlayVoice && nowBlock.x + nowBlock.length < xSpeed * 60 * 2) {
            isPlayVoice = true;
            MediaManager.playAudio(FileManager2.getHintVoicePath(), null);
        }
        // jump over the gap
        if (nowBlock.x + nowBlock.length < 0) {
            blockCount++;
            isPlayVoice = false;
        }
        else if (blockCount == blocks.length - 1 && nowBlock.x < 0) {
            // game finished
            blockCount++;
        }
        // move block
        for (Block block : blocks) {
            block.x -= xSpeed;
        }
        spike.x -= 10;
        if (spike.x < -viewWidth * 2) spike.x = 0;
        if (player.x + Player.BALL_RADIUS < 0 || player.y > viewHeight) {
            isGameOver = true;
            failCount++;
        }
        // switch light
        gui.switchLight(player.x < nowBlock.x);
    }

    public void startGame() {
        player = new Player(this, xInit, (int) getLimit(stander + 3) - 10, stander);
        blockCount = 0;
        isGameOver = false;
        int x = 0;
        int y = (int) getLimit(stander + level_difficulty);
        // generate block
        for (int i = 0; i < blocks.length; i++) {
            int blockLength = i == 0 ? xSpeed * 60 * 3 : xSpeed * 60 * 6;
            int gapLength = xSpeed * 60 * 5;
            blocks[i] = new Block(this, x, y, blockLength);
            x += gapLength + blockLength;
        }
    }

    public void setLevelDifficulty(int level_difficulty) {
        this.level_difficulty = level_difficulty;
    }

    public void setStander(double avg) {
        stander = avg;
    }

    public int getGap() {
        return blockCount - 1;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public int failCount() {
        return failCount;
    }

    private double getLimit(double volume) {
        return ground - (volume - stander) / (90 - stander) * ground;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw ball
        if (player != null) player.onDraw(canvas);
        // draw spike
        spike.onDraw(canvas);
        // draw block
        for (Block block : blocks) {
            if (block == null) continue;
            block.onDraw(canvas);
        }
        // draw hintLight and heart
        gui.setHeartCount(3 - failCount);
        gui.onDraw(canvas);
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!changed) return;
        viewWidth = right - left;
        viewHeight = bottom - top;
        xInit = viewWidth / 8;
        ground = viewHeight / 3 * 2;
        gui = new GameGui(this);
        spike = new Spike(this);
    }

}
