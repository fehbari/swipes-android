package com.swipesapp.android.sync.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.swipesapp.android.sync.service.SyncService;
import com.swipesapp.android.sync.service.TasksService;
import com.swipesapp.android.values.Actions;

/**
 * Helper for handling the snooze alarm. Automatically starts the alarm when
 * the device boots and provides a convenience call to start it at will.
 *
 * @author Felipe Bari
 */
public class SnoozeHelper extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Actions.BOOT_COMPLETED)) {
            // Start snooze alarm.
            createSnoozeAlarm(context);

            // Initialize services.
            TasksService.newInstance(context);
            SyncService.newInstance(context);
        }
    }

    public static void createSnoozeAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SnoozeReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Trigger the alarm after a minute.
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 60000, 60000, alarmIntent);
    }

}