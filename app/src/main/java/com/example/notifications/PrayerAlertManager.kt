package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.audio.IslamicVoiceSpeaker
import com.example.data.CityLocationsProvider
import com.example.data.PrayerTimesCalculator
import com.example.model.DayPrayerTimes
import com.example.model.PrayerType
import java.util.Calendar

object PrayerAlertManager {

    const val CHANNEL_ID = "prayer_alert_channel"
    private const val CHANNEL_NAME = "تنبيهات مواقيت الصلاة والأذان"
    private const val PREFS_NAME = "prayer_alert_prefs"

    private const val KEY_ALERTS_ENABLED = "prayer_alerts_enabled"
    private const val KEY_ADVANCE_MINUTES = "prayer_advance_minutes" // Remind before prayer
    private const val KEY_VOICE_ENABLED = "prayer_voice_enabled"
    private const val KEY_SELECTED_CITY_INDEX = "prayer_city_index"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isAlertsEnabled(context: Context): Boolean = getPrefs(context).getBoolean(KEY_ALERTS_ENABLED, true)
    fun setAlertsEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_ALERTS_ENABLED, enabled).apply()
        if (enabled) scheduleNextPrayerAlert(context)
        else cancelPrayerAlert(context)
    }

    fun getAdvanceMinutes(context: Context): Int = getPrefs(context).getInt(KEY_ADVANCE_MINUTES, 10) // default 10 min before
    fun setAdvanceMinutes(context: Context, minutes: Int) {
        getPrefs(context).edit().putInt(KEY_ADVANCE_MINUTES, minutes).apply()
        if (isAlertsEnabled(context)) scheduleNextPrayerAlert(context)
    }

    fun isVoiceEnabled(context: Context): Boolean = getPrefs(context).getBoolean(KEY_VOICE_ENABLED, true)
    fun setVoiceEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_VOICE_ENABLED, enabled).apply()
    }

    fun getSelectedCityIndex(context: Context): Int = getPrefs(context).getInt(KEY_SELECTED_CITY_INDEX, 4) // Default Cairo
    fun setSelectedCityIndex(context: Context, index: Int) {
        getPrefs(context).edit().putInt(KEY_SELECTED_CITY_INDEX, index).apply()
        if (isAlertsEnabled(context)) scheduleNextPrayerAlert(context)
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات مواقيت الصلاة بصوت الأذان والتنبيه الصوتي"
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Sends an immediate or scheduled prayer alert notification and triggers voice announcement.
     */
    fun sendPrayerAlertNotification(context: Context, prayerName: String, isAdvanceReminder: Boolean = false, isTest: Boolean = false) {
        createNotificationChannel(context)

        val title = if (isAdvanceReminder) {
            "⏳ اقترب موعد صلاة $prayerName"
        } else {
            "🕌 حان الآن موعد أذان صلاة $prayerName"
        }

        val body = if (isAdvanceReminder) {
            val advanceMins = getAdvanceMinutes(context)
            "تذكير: باقي $advanceMins دقائق على موعد أذان صلاة $prayerName. استعد للوضوء والصلاة."
        } else {
            "حي على الصلاة، حي على الفلاح.. حان وقت صلاة $prayerName المباركة. تقبل الله طاعتكم."
        }

        // Trigger spoken announcement if voice enabled
        if (isVoiceEnabled(context)) {
            val spokenMessage = if (isAdvanceReminder) {
                "تذكير، اقترب موعد صلاة $prayerName، استعد للوضوء والصلاة"
            } else {
                "حان الآن موعد صلاة $prayerName، الله أكبر الله أكبر"
            }
            IslamicVoiceSpeaker.getInstance(context).speak(spokenMessage)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            201,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 7777 + (prayerName.hashCode() % 100)
        notificationManager.notify(notificationId, builder.build())
    }

    /**
     * Schedules the next prayer alert accurately using Android AlarmManager.
     */
    fun scheduleNextPrayerAlert(context: Context) {
        createNotificationChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val cityIdx = getSelectedCityIndex(context).coerceIn(0, CityLocationsProvider.cities.lastIndex)
        val city = CityLocationsProvider.cities[cityIdx]

        val dayPrayers = PrayerTimesCalculator.calculatePrayerTimes(
            latitude = city.latitude,
            longitude = city.longitude,
            cityName = city.nameArabic
        )

        val next = dayPrayers.nextPrayer ?: return
        val advanceMinutes = getAdvanceMinutes(context)
        val advanceAlertTime = next.timeMillis - (advanceMinutes * 60 * 1000L)
        val now = System.currentTimeMillis()

        // Choose either advance alert or exact prayer time depending on what's next
        val (targetTime, isAdvance) = if (advanceAlertTime > now) {
            Pair(advanceAlertTime, true)
        } else {
            Pair(next.timeMillis, false)
        }

        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra("prayer_name", next.type.nameArabic)
            putExtra("is_advance", isAdvance)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            202,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetTime, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetTime, pendingIntent)
            }
        } catch (_: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetTime, pendingIntent)
        }
    }

    fun cancelPrayerAlert(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, PrayerAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            202,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Test function to immediately speak the prayer announcement out loud.
     */
    fun testVoiceAnnouncement(context: Context, prayerName: String) {
        IslamicVoiceSpeaker.getInstance(context).speak("حان الآن موعد صلاة $prayerName، الله أكبر الله أكبر")
    }
}
