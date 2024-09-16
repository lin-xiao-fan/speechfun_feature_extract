package com.example.pdapp2022919.HealthManager.AlarmClock;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pdapp2022919.MainPage;
import com.example.pdapp2022919.R;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
//        Vibrator myVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
//        myVibrator.vibrate(5000);
//        System.out.println("FUck you");
//        context.startActivity(intent);

        Intent nextActivity = new Intent(context, MainPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nextActivity, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Speech Fun")
                .setSmallIcon(R.drawable.ic_garbage)
                .setContentTitle("Good Morning!")
                .setContentText("It's time to wake up")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());
    }

}