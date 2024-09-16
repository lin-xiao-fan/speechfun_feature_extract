package com.example.pdapp2022919.Store;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pdapp2022919.Database.User.User;
import com.example.pdapp2022919.Database.User.UserDao;
import com.example.pdapp2022919.Database.Vegetable.Vegetable;
import com.example.pdapp2022919.MainPage;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.DatabaseManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;
import com.example.pdapp2022919.net.Client;

import java.util.Arrays;

public class StorePage extends ScreenSetting {

    private TextView coin_count_text,coin_not_enough;
    private Button buy_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_page);

        coin_count_text = findViewById(R.id.coin_count_text);
        coin_not_enough = findViewById(R.id.coin_not_enough);
        coin_not_enough.setVisibility(View.INVISIBLE);
        Button back_main_page_button3 = findViewById(R.id.back_main_page_button3);
        Button basket = findViewById(R.id.basket);
        back_main_page_button3.setOnClickListener(view -> {
            startActivity(new Intent(this, MainPage.class));
        });
        basket.setOnClickListener(view -> {
        startActivity(new Intent(this, Basket.class));
                });
        buy_button = findViewById(R.id.buy_button);
        buy_button.setOnClickListener(view -> {
            buyVegetable();
        });
        new Thread(() -> {
            UserDao dao = DatabaseManager.getInstance(this).userDao();
            User user = dao.getUser();
            new Handler(Looper.getMainLooper()).post(() -> {
                coin_count_text.setText(Integer.toString(user.coin_count));
            });
        }).start();
    }


    public class popupWindow extends PopupWindow implements View.OnClickListener {

        View view;

        public popupWindow(Context mContext) {
            this.view = LayoutInflater.from(mContext).inflate(R.layout.activity_vegetable_pop_up, null);
            this.setOutsideTouchable(true);
            this.view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        dismiss();
                    }
                    return true;
                }
            });
            this.setContentView(this.view);
            this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
            this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        }

        @Override
        public void onClick(View view) {
        }

    }
    private void buyVegetable(){
        new Thread(() -> {
            UserDao dao = DatabaseManager.getInstance(this).userDao();
            User user = dao.getUser();
            // 扣錢
            if(user.coin_count<2) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    coin_not_enough.setText(R.string.coin_not_enough);
                    coin_not_enough.setVisibility(View.VISIBLE);
                });
                return;
            }
            int unlock = user.unlockVegetable();
            if (unlock == -1) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    // 沒有解鎖 提示
                    coin_not_enough.setText(R.string.vegetable_sold_out);
                    coin_not_enough.setVisibility(View.VISIBLE);
                });
                return;
            }
            else {
                user.coin_count -= 2;
                dao.updateUser(user);
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                Vegetable vegetable = Vegetable.getVegetable(unlock);
                if (vegetable == null) return;
                popupWindow popupWindow = new popupWindow(this);
                TextView vegetable_name_text = popupWindow.view.findViewById(R.id.vegetable_name_text);
                ImageView vegetable_image = popupWindow.view.findViewById(R.id.vegetable_image);
                vegetable_name_text.setText(vegetable.nameID);
                vegetable_image.setImageResource(vegetable.drawableID);
                popupWindow.showAtLocation(buy_button, Gravity.CENTER, 0, 0);
                coin_count_text.setText(Integer.toString(user.coin_count));
            });
        }).start();
    }
}
