package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PrayerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("prayer_name") ?: "الصلاة"
        val isAdvance = intent.getBooleanExtra("is_advance", false)

        if (PrayerAlertManager.isAlertsEnabled(context)) {
            PrayerAlertManager.sendPrayerAlertNotification(
                context = context,
                prayerName = prayerName,
                isAdvanceReminder = isAdvance
            )
            // Schedule next prayer
            PrayerAlertManager.scheduleNextPrayerAlert(context)
        }
    }
}
