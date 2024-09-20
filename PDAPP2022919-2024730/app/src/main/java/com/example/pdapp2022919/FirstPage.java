package com.example.pdapp2022919;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.pdapp2022919.Profile.LoginPage;
import com.example.pdapp2022919.Profile.SignUpPage;
import com.example.pdapp2022919.SystemManager.FileManager2;
import com.example.pdapp2022919.SystemManager.ScreenSetting;

public class FirstPage extends ScreenSetting {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN |  View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);


        Button signupButton = findViewById(R.id.sign_up_button);
        Button signinButton = findViewById(R.id.sign_in_button);
        Button DTxlablogo = findViewById(R.id.back_button3);
        signupButton.setOnClickListener(view -> openSingUpPage());
        signinButton.setOnClickListener(view -> openLoginPage());
        DTxlablogo.setOnClickListener(view -> openMainPage());
    }
    private void openMainPage(){
        Intent intent=new Intent(this, MainPage.class);
        startActivity(intent);
    }

    private void openLoginPage(){
        Intent intent=new Intent(this, LoginPage.class);
        startActivity(intent);
    }

    private void openSingUpPage(){
        Intent intent=new Intent(this, SignUpPage.class);
        startActivity(intent);
    }

}