package com.example.pdapp2022919.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pdapp2022919.Database.User.User;
import com.example.pdapp2022919.Database.User.UserDao;
import com.example.pdapp2022919.FirstPage;
import com.example.pdapp2022919.MainPage;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.DatabaseManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;
import com.example.pdapp2022919.net.Client;

public class LoginPage extends ScreenSetting {
    private EditText editTextPatientID3, editTextTextPersonName3;
    private TextView hint;
    private Button backFirst, user2Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        hideSystemUI();
        hint = findViewById(R.id.hint);
        editTextPatientID3 = findViewById(R.id.editTextPatientID3);
        editTextTextPersonName3 = findViewById(R.id.editTextTextPersonName3);
        backFirst = findViewById(R.id.back_first_page_button3);
        backFirst.setOnClickListener(view -> {
            startActivity(new Intent(this, FirstPage.class));
        });

//        Button user1Button = (Button) findViewById(R.id.user1_Button);
        user2Button = (Button) findViewById(R.id.user1_Button);
        user2Button.setOnClickListener(view -> {
            int id = Integer.parseInt(editTextPatientID3.getText().toString());
            String name = editTextTextPersonName3.getText().toString();
            System.out.println(id + ": " + name);
            new Thread(() -> {
                Client.login(this, id, name, isSucceed -> {
                    if (isSucceed) {
                        runOnUiThread(() -> hint.setVisibility(View.GONE));
                        // TODO 從雲端下載profile
                        UserDao dao = DatabaseManager.getInstance(this).userDao();
                        if (dao.findByUuid(Client.getUuid().toString()) == null) {
                            // TODO 從雲端下載profile 填入User data
                            dao.addUser(new User(
                                    Client.getUuid().toString(),
                                    Integer.toString(id),
                                    name,
                                    0,
                                    0
                            ));
                        }
                        else {
                            dao.updateUser(new User(
                                    Client.getUuid().toString(),
                                    Integer.toString(id),
                                    name,
                                    0,
                                    0
                            ));
                        }
                        startActivity(new Intent(LoginPage.this, MainPage.class));
                    } else runOnUiThread(() -> hint.setVisibility(View.VISIBLE));
                });
            }).start();
        });
    }

}