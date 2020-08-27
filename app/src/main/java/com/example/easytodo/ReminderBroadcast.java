package com.example.easytodo;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int i = intent.getIntExtra("i", 0);

        Intent notifyIntent = new Intent(context, SnoozeOrRemoveActivity.class);
        notifyIntent.putExtra("notification_title", intent.getStringExtra("title"));
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify")
                .setContentIntent(notifyPendingIntent)
                .setSmallIcon(R.drawable.ic_baseline_check_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (intent.hasExtra("updatedTitle")) {
            builder.setContentText(intent.getStringExtra("updatedTitle"));
        }
        else {
            builder.setContentText(intent.getStringExtra("title"));
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(i, builder.build());

    }
 }


