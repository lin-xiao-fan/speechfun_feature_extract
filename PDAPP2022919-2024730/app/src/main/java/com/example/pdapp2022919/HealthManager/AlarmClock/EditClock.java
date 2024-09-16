package com.example.pdapp2022919.HealthManager.AlarmClock;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.pdapp2022919.Database.Clock.Clock;
import com.example.pdapp2022919.Database.Clock.ClockDao;
import com.example.pdapp2022919.R;
import com.example.pdapp2022919.SystemManager.DatabaseManager;
import com.example.pdapp2022919.SystemManager.NameManager;
import com.example.pdapp2022919.SystemManager.ScreenSetting;

import java.util.Calendar;

public class EditClock extends ScreenSetting {

    private CheckBox[] Day = new CheckBox[7] ;
    private Button store_button, delet_clock_button,cancel_button;
    private TimePicker timePicker;
    private AlarmManager alarmManager = null;

    private boolean[] selectedDay = new boolean[7];
    private int hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_clock);

        Intent intent = getIntent();
        boolean isEditMode = intent.getBooleanExtra(NameManager.IS_EDIT_MODE, false);
        Clock clock = intent.getParcelableExtra(NameManager.CLOCK_DATA);

        Day[0]= findViewById(R.id.monday);
        Day[1]= findViewById(R.id.Tuesday);
        Day[2]= findViewById(R.id.Wensday);
        Day[3]= findViewById(R.id.Thursday);
        Day[4]= findViewById(R.id.Friday);
        Day[5]= findViewById(R.id.Saturday);
        Day[6]= findViewById(R.id.Sunday);

        timePicker = findViewById(R.id.timePicker);
        store_button = findViewById(R.id.store_button);
        cancel_button = findViewById(R.id.cancel_button);
        delet_clock_button = findViewById(R.id.back_history_page);

        for (int i = 0; i < Day.length; i++) {
            int finalI = i;
            Day[i].setOnCheckedChangeListener((view, enable) -> {
                selectedDay[finalI] = enable;
            });
            if ((clock.clock_state & (0X40 >> i)) > 0) {
                selectedDay[i] = true;
                Day[i].setChecked(true);
            }
        }

        timePicker.setHour(clock.hour);
        timePicker.setMinute(clock.minute);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        store_button.setOnClickListener(view -> {
//            Intent intent = new Intent(EditClock.this, ClockList.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(EditClock.this, 0X102, intent, 0);
//            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
            // TODO 儲存 星期 時間
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
            Toast.makeText(EditClock.this, "鬧鐘設置成功" + hour + ":" + minute, Toast.LENGTH_SHORT).show();
            new Thread(() -> {
                ClockDao dao = DatabaseManager.getInstance(this).clockDao();
                if (isEditMode && (hour != clock.hour || minute != clock.minute)) {
                    dao.deleteClock(clock);
                    cancelAlarm(clock);
                }
                clock.hour = hour;
                clock.minute = minute;
                clock.clock_state = getState();
                dao.addClock(clock);
                addAlarm(clock);
            }).start();
            finish();
        });
        cancel_button.setOnClickListener((view -> {
            finish();
        }));
        if (isEditMode) delet_clock_button.setVisibility(View.VISIBLE);
        delet_clock_button.setOnClickListener((view -> {
            new Thread(() -> {
                ClockDao dao = DatabaseManager.getInstance(this).clockDao();
                dao.deleteClock(clock);
                cancelAlarm(clock);
            }).start();
            finish();
        }));
    }

    private byte getState() {
        byte result = (byte) 0x80;
        for (int i = 0; i < selectedDay.length; i++) {
            if(selectedDay[i]) {
                result |= 0x40 >> i;
            }
        }
        return result;
    }

    private PendingIntent getPendingIntent(Clock clock) {
        int id = clock.hour * 100 + clock.minute;
        Intent intent = new Intent(this, AlarmReceiver.class);
        return PendingIntent.getBroadcast(
                this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private void addAlarm(Clock clock) {
        PendingIntent intent = getPendingIntent(clock);

        for (int i = 0; i < Day.length; i++) {
            if ((clock.clock_state & (0X40 >> i)) > 0) {
                System.out.println("set alarm: " + i);
                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.DAY_OF_WEEK, i + 2);
                calendar.set(Calendar.HOUR_OF_DAY, clock.hour);
                calendar.set(Calendar.MINUTE, clock.minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY * 7,
                        intent
                );
            }
        }
    }

    private void cancelAlarm(Clock clock) {
        PendingIntent intent = getPendingIntent(clock);
        alarmManager.cancel(intent);
    }

}

