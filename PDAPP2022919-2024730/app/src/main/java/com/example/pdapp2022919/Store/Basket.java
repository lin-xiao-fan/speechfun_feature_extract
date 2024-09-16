package com.example.pdapp2022919.Store;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.constraintlayout.helper.widget.Carousel;

import com.example.pdapp2022919.Database.User.User;
import com.example.pdapp2022919.Database.User.UserDao;
import com.example.pdapp2022919.MainPage;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.DatabaseManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;
import com.example.pdapp2022919.net.Client;

public class Basket extends ScreenSetting {
    private Button back_button,back_button2;
    private RadioButton[] vegetable = new RadioButton[12];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        back_button = findViewById(R.id.back_button);
        back_button2 = findViewById(R.id.back_button2);

        back_button.setOnClickListener(view -> {
            go_back();
        });
        back_button2.setOnClickListener(view -> {
            go_back();
        });

        vegetable[0] = findViewById(R.id.f1);
        vegetable[1] = findViewById(R.id.f2);
        vegetable[2] = findViewById(R.id.f3);
        vegetable[3] = findViewById(R.id.f4);
        vegetable[4] = findViewById(R.id.f5);
        vegetable[5] = findViewById(R.id.f6);
        vegetable[6] = findViewById(R.id.f7);
        vegetable[7] = findViewById(R.id.f8);
        vegetable[8] = findViewById(R.id.f9);
        vegetable[9] = findViewById(R.id.f10);
        vegetable[10] = findViewById(R.id.f11);
        vegetable[11] = findViewById(R.id.f12);
        new Thread(() -> {
            UserDao dao = DatabaseManager.getInstance(this).userDao();
            User user = dao.getUser();
            int checked = user.checked_vegetable;
            boolean[] unlock = user.getUnlocked_vegetable();
            runOnUiThread(() -> {
                vegetable[checked].setChecked(true);
                for (int i = 0; i < unlock.length; i++) {
                    int finalI = i;
                    vegetable[i].setEnabled(unlock[i]);
                    vegetable[i].setOnCheckedChangeListener((compoundButton, b) -> {
                        if (b) user.checked_vegetable = finalI;
                        new Thread(() -> dao.updateUser(user)).start();
                    });
                }
            });
        }).start();
    }
    private void go_back(){
        Intent intent = new Intent(this,StorePage.class);
        startActivity(intent);
    }
}
