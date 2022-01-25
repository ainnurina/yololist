package com.example.ToList;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent notificationIntent;

        String message = "Hello shoppers, this is a reminder for you to shop today \n" +
                "Do remember to add your expenses once you are done shopping!";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyToList")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Today is your Shopping Day")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationIntent = new Intent(context, YoloListActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra("ListID", "-MuBlDhvzZFxYSaOtjcm");

        PendingIntent pintent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        builder.setContentIntent(pintent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, builder.build());
    }
}


