package com.example.pdapp2022919.HealthManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pdapp2022919.Database.User.User;
import com.example.pdapp2022919.Database.User.UserDao;
import com.example.pdapp2022919.Profile.ProfileData;
import com.example.pdapp2022919.MainPage;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.DatabaseManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;
import com.example.pdapp2022919.net.Client;

import java.io.File;
import java.util.Calendar;

// TODO profile?
public class PersonalData extends ScreenSetting {

//    private boolean ismodification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        hideSystemUI();
//        Button changeButton = findViewById(R.id.changeStore);
        Button backHome = findViewById(R.id.backhome);
        TextView NameText = findViewById(R.id.NameText);
        TextView MediaNumberText = findViewById(R.id.MediaNumberText);
//        TextView BirthdayText = findViewById(R.id.BirthdayText);
//        EditText phoneNumber = findViewById(R.id.phoneText);
//        RadioButton boy_radioButton = findViewById(R.id.boy_radioButton);
//        RadioButton girl_radioButton = findViewById(R.id.girl_radioButton);
        backHome.setOnClickListener(view -> {
            startActivity(new Intent(this, HealthMangerList.class));
        });
        new Thread(() -> {
            UserDao dao = DatabaseManager.getInstance(this).userDao();
            User user = dao.getUser();
            runOnUiThread(() -> {
                NameText.setText(user.password);
                MediaNumberText.setText(user.account);
            });
        }).start();
//        FileManager.setTimestamp(FileManager.FileType.PROFILE);
//        //帶入以存取之資料
//        ProfileData profile = FileManager.readProfile();
//        NameText.setText(profile.patient_name);
//        MediaNumberText.setText(profile.patient_number);
//        phoneNumber.setText(profile.phone_number);
//        if (profile.birthday_year != 0) {
//            BirthdayText.setText(setDateFormat(
//                    profile.birthday_year, profile.birthday_month, profile.birthday_day));
//        }
//        if (profile.sex == 1) {
//            boy_radioButton.setChecked(true);
//        } else if (profile.sex == 2) {
//            girl_radioButton.setChecked(true);
//        }
        //寫入
//        changeButton.setOnClickListener(view -> {
//            //按下後改狀態
//            ismodification = !ismodification;
//            //1.修改按鈕的字
//            if (ismodification) {
//                changeButton.setText("儲存");
//            } else {
//                changeButton.setText("修改");
//                FileManager.writeProfile(profile);
//                File[] files = new File[] { FileManager.getProfile() };
//                Client.upload(this, files, isSucceed -> {
//                    new Handler(Looper.getMainLooper()).post(() -> {
//                        if (isSucceed) {
//                            Toast.makeText(this, R.string.upload_success, Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            Toast.makeText(this, R.string.upload_failed, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                });
//            }
//            //2.輸入狀態
//            BirthdayText.setEnabled(ismodification);
//            boy_radioButton.setEnabled(ismodification);
//            girl_radioButton.setEnabled(ismodification);
//            phoneNumber.setEnabled(ismodification);
//        });
//        BirthdayText.setOnClickListener(view -> {
//            int mYear, mMonth, mDay;

//            if (profile.birthday_year == 0) {
//                final Calendar c = Calendar.getInstance();
//                mYear = c.get(Calendar.YEAR);
//                mMonth = c.get(Calendar.MONTH);
//                mDay = c.get(Calendar.DAY_OF_MONTH);
//            } else {
//                mYear = profile.birthday_year;
//                mMonth = profile.birthday_month;
//                mDay = profile.birthday_day;
//            }
//
//            new DatePickerDialog(this, (v, year, month, day) -> {
//                String format = setDateFormat(year, month, day);
//                BirthdayText.setText(format);
//                profile.birthday_year = year;
//                profile.birthday_month = month;
//                profile.birthday_day = day;
//            }, mYear, mMonth, mDay).show();
//        });
//        boy_radioButton.setOnClickListener(view -> profile.sex = 1);
//        girl_radioButton.setOnClickListener(view -> profile.sex = 2);
//        phoneNumber.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
////                profile.phone_number = editable.toString();
//            }
//        });
    }

//    private String setDateFormat(int year, int monthOfYear, int dayOfMonth) {
//        return year + "年"
//                + (monthOfYear + 1) + "月"
//                + dayOfMonth + "日";
//    }
}