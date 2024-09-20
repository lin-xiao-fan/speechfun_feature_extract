package com.example.pdapp2022919;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.pdapp2022919.Correction.EnvironmentTest;
import com.example.pdapp2022919.Database.Correction.Correction;
import com.example.pdapp2022919.HealthManager.HealthMangerList;
import com.example.pdapp2022919.Questionnaire.QCaption;
import com.example.pdapp2022919.Questionnaire.QList;
import com.example.pdapp2022919.Store.StorePage;
import com.example.pdapp2022919.SystemManager.AndroidFFMPEGLocator;
import com.example.pdapp2022919.SystemManager.FileManager2;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;
import com.example.pdapp2022919.net.Client;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;



public class MainPage extends ScreenSetting {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }


        //new AndroidFFMPEGLocator(this);
        AndroidFFMPEGLocator ffmpegLocator = new AndroidFFMPEGLocator(this);
        ffmpegLocator.check(this);




//        hideSystemUI();
        checkPermission();
        createNotificationChannel();
        FileManager2.setFileDir(getFilesDir().getAbsolutePath());
        FileManager2.createHintVoice(this);
        Client.loadUuid(this);

        MaterialButton singoutButton = findViewById(R.id.signOutButton);
        Button startgameButton = (Button) findViewById(R.id.ok_button);
        Button store_button = (Button) findViewById(R.id.cancel_button);
        Button questionnaire_button = (Button) findViewById(R.id.questionnaire_button);
        Button healthmanager_button = (Button) findViewById(R.id.healthmanager_button);

        if (Client.getUuid() == Client.GUEST_UUID) {
            singoutButton.setText(R.string.sign_in);
            singoutButton.setIcon(AppCompatResources.getDrawable(this, R.drawable.add_user));
        }

        LayoutInflater inflater = getLayoutInflater();
        singoutButton.setOnClickListener(view -> {
            View managerKeyView = inflater.inflate(R.layout.manager_key, null);
            new MaterialAlertDialogBuilder(this)
                    .setView(managerKeyView)
                    .setPositiveButton(R.string.confirm, (dialog, id) -> {
                        EditText key = managerKeyView.findViewById(R.id.key_password);
                        String input = key.getText().toString();
                        if (input.equals("804")) {
                            //回到初始值
                            Client.logout();
                            startActivity(new Intent(this, FirstPage.class));
                        }
                        else {
                            Toast.makeText(this, R.string.password_error, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) -> {
//                        dialog.cancel();
                    })
                    .show();
        });

        startgameButton.setOnClickListener(view -> {
            Correction correction = new Correction();
            Intent intent = new Intent(this, EnvironmentTest.class);
            intent.putExtra(NameManager.CORRECTION_OBJ, correction);
            startActivity(intent);
        });

        store_button.setOnClickListener(view -> {
            startActivity(new Intent(this, StorePage.class));
        });
        questionnaire_button.setOnClickListener(view -> {
            startActivity(new Intent(this, QCaption.class));
        });
        healthmanager_button.setOnClickListener(view -> {
            startActivity(new Intent(this, HealthMangerList.class));
        });
    }






    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Speech Fun";
            String desc = "Channel for Alarm Manager";
            int imp = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Speech Fun", name, imp);
            channel.setDescription(desc);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**確認是否有麥克風使用權限*/
    private void checkPermission(){
        ArrayList<String> permissions = new ArrayList<>();
        int requestCode = 0;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.RECORD_AUDIO);
            requestCode |= 1;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.CAMERA);
            requestCode |= 2;
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                permissions.add(Manifest.permission.POST_NOTIFICATIONS);
//                requestCode |= 4;
//            }
//        }
        if (permissions.isEmpty()) return;
        ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), requestCode);
    }
    /**取得權限回傳*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int count = 0;
        for (int i = 0; i < 3; i++) {
            if (((requestCode >> i) & 0x01) == 0x01 && grantResults[count++] == PackageManager.PERMISSION_DENIED) {
                finish();
            }
        }
    }
}