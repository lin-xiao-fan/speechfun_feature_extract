package com.example.pdapp2022919.Game.GameFeedback;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.example.pdapp2022919.R;

public class CoinPopUp extends PopupWindow {

    public CoinPopUp(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_coin_pop_up, null);
        setContentView(view);
        setOutsideTouchable(true);

        view.setOnTouchListener((view1, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                dismiss();
            }
            view1.performClick();
            return true;
        });

        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    }

}