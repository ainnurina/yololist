package com.example.ToList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyToList")
                .setSmallIcon(R.drawable.ic_baseline_add_alert_24)
                .setContentTitle("Reminder ToList")
                .setContentText("Hello shoppers, this is a reminder for you to shop today! \n" +
                                "Do use our cute app as your shopping assistant!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, builder.build());
    }
}
