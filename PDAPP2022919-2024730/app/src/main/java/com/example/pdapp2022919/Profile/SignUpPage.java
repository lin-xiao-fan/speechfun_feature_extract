package com.example.pdapp2022919.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.pdapp2022919.Database.User.User;
import com.example.pdapp2022919.Database.User.UserDao;
import com.example.pdapp2022919.SystemManager.DatabaseManager;
import com.example.pdapp2022919.FirstPage;
import com.example.pdapp2022919.MainPage;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.ScreenSetting;
import com.example.pdapp2022919.net.Client;

public class SignUpPage extends ScreenSetting {
    private TextView hintText1;
    private EditText editTextTextPersonName, editTextPatientID;
    private Button enter_Button, backFirst;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);
        hideSystemUI();
        editTextPatientID = findViewById(R.id.editTextPatientID);
        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        hintText1 = findViewById(R.id.hintText1);
        enter_Button = findViewById(R.id.enter_Button);

        backFirst = findViewById(R.id.back_first_page_button4);
        backFirst.setOnClickListener(view -> {
            startActivity(new Intent(this, FirstPage.class));
        });

        // TODO 判斷輸入格式
        enter_Button.setOnClickListener(view -> {
            int id = Integer.parseInt(editTextPatientID.getText().toString());
            String name = editTextTextPersonName.getText().toString();
            System.out.println(id + ": " + name);
            new Thread(() -> {
                Client.register(this, id, name, isSucceed -> {
                    if (isSucceed) {
                        runOnUiThread(() -> hintText1.setVisibility(View.GONE));
//                        FileManager.setTimestamp(FileManager.FileType.PROFILE);
//                        ProfileData profile = new ProfileData();
//                        profile.patient_name = name;
//                        profile.patient_number = editTextPatientID.getText().toString();
//
//                        FileManager.writeProfile(profile);

                        UserDao dao = DatabaseManager.getInstance(this).userDao();
                        dao.addUser(new User(
                                Client.getUuid().toString(),
                                Integer.toString(id),
                                name,
                                0,
                                2
                        ));

                        startActivity(new Intent(SignUpPage.this, MainPage.class));
                    } else runOnUiThread(() -> hintText1.setVisibility(View.VISIBLE));
                });
            }).start();
        });
    }
}