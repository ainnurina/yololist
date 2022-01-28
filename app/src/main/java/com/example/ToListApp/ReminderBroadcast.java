package com.example.ToListApp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {

    String ListID;

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent notificationIntent;
        Bundle extras = intent.getExtras();
        ListID = extras.getString("ListID");



        String message = "Hello shoppers, this is a reminder for you to shop today \n" +
                "Do remember to add your expenses once you are done shopping!";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyToList")
                .setSmallIcon(R.drawable.ic_baseline_add_alert_24)
                .setContentTitle("Today is your Shopping Day")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationIntent = new Intent(context, viewBudgetActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra("ListID", ListID);
        PendingIntent pintent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pintent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(1, builder.build());
    }
}


