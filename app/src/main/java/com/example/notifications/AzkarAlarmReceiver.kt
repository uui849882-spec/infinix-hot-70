package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AzkarAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Send the notification
        if (AzkarNotificationManager.isEnabled(context)) {
            AzkarNotificationManager.sendAzkarNotification(context)
            // Reschedule next occurrence
            AzkarNotificationManager.schedulePeriodicReminder(context)
        }
    }
}
