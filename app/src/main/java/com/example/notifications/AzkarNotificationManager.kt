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
import com.example.data.AzkarDataProvider

object AzkarNotificationManager {

    const val CHANNEL_ID = "azkar_salawat_channel"
    private const val CHANNEL_NAME = "أذكار وتنبيهات الصلاة على النبي ﷺ"
    private const val PREFS_NAME = "azkar_notifications_prefs"

    private const val KEY_ENABLED = "notifications_enabled"
    private const val KEY_INTERVAL_MIN = "interval_minutes"
    private const val KEY_VOICE_ENABLED = "voice_speech_enabled"
    private const val KEY_MODE = "notification_mode" // "SALAWAT", "ALL_132_AZKAR", "MIXED"

    const val MODE_SALAWAT = "SALAWAT"
    const val MODE_ALL_AZKAR = "ALL_132_AZKAR"
    const val MODE_MIXED = "MIXED"

    val SALAWAT_LIST = listOf(
        Pair("ﷺ الصلاة على النبي ﷺ", "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى سَيِّدِنَا وَنَبِيِّنَا مُحَمَّدٍ وَعَلَى آلِهِ وَصَحْبِهِ أَجْمَعِينَ"),
        Pair("ﷺ الصلاة الإبراهيمية", "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ"),
        Pair("ﷺ الصلاة على الحبيب المصطفى", "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ صَلَاةً تُفَرِّجُ بِهَا الْكُرُوبَ وَتَشْرَحُ بِهَا الصُّدُورَ"),
        Pair("ﷺ مسك الختام", "الصَّلَاةُ وَالسَّلَامُ عَلَيْكَ يَا رَسُولَ اللَّهِ، يَا خَيْرَ خَلْقِ اللَّهِ وَخَاتَمَ النَّبِيِّينَ")
    )

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_ENABLED, true)
    }

    fun setEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
        if (enabled) {
            schedulePeriodicReminder(context)
        } else {
            cancelReminder(context)
        }
    }

    fun getIntervalMinutes(context: Context): Int {
        return getPrefs(context).getInt(KEY_INTERVAL_MIN, 30)
    }

    fun setIntervalMinutes(context: Context, minutes: Int) {
        getPrefs(context).edit().putInt(KEY_INTERVAL_MIN, minutes).apply()
        if (isEnabled(context)) {
            schedulePeriodicReminder(context)
        }
    }

    fun isVoiceSpeechEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_VOICE_ENABLED, true)
    }

    fun setVoiceSpeechEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_VOICE_ENABLED, enabled).apply()
    }

    fun getNotificationMode(context: Context): String {
        return getPrefs(context).getString(KEY_MODE, MODE_MIXED) ?: MODE_MIXED
    }

    fun setNotificationMode(context: Context, mode: String) {
        getPrefs(context).edit().putString(KEY_MODE, mode).apply()
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات دورية بأذكار المسلم من ١٣٢ باباً والصلاة على النبي ﷺ صوتياً وبصرياً"
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Sends the notification and sounds spoken Salawat or Azkar.
     */
    fun sendAzkarNotification(context: Context, isTest: Boolean = false, forceSalawat: Boolean = false) {
        createNotificationChannel(context)

        val mode = getNotificationMode(context)
        val shouldDoSalawat = forceSalawat || isTest || mode == MODE_SALAWAT || (mode == MODE_MIXED && (0..1).random() == 0)

        val title: String
        val body: String
        val spokenText: String

        if (shouldDoSalawat) {
            val item = SALAWAT_LIST.random()
            title = item.first
            body = item.second
            spokenText = "اللهم صل وسلم وبارك على سيدنا محمد وعلى آله وصحبه أجمعين"
        } else {
            // Pick from the 132 Hisn Al-Muslim Azkar Chapters
            val randomChapter = AzkarDataProvider.chapters.random()
            val randomItem = randomChapter.items.randomOrNull()
            title = "📿 ${randomChapter.title}"
            body = randomItem?.text ?: "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ"
            spokenText = body
        }

        // Voice pronunciation if enabled
        if (isVoiceSpeechEnabled(context)) {
            IslamicVoiceSpeaker.getInstance(context).speak(spokenText)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            100,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.star_on)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = (System.currentTimeMillis() % 10000).toInt()
        notificationManager.notify(notificationId, builder.build())
    }

    fun schedulePeriodicReminder(context: Context) {
        createNotificationChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intervalMinutes = getIntervalMinutes(context)

        val intent = Intent(context, AzkarAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = System.currentTimeMillis() + (intervalMinutes * 60 * 1000L)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, AzkarAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
