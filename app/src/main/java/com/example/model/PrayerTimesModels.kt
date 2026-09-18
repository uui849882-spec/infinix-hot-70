package com.example.model

/**
 * Supported astronomical calculation methods for accurate prayer times worldwide.
 */
enum class CalculationMethod(val titleArabic: String, val fajrAngle: Double, val ishaAngle: Double, val ishaFixedMinutes: Int = 0) {
    EGYPTIAN("الهيئة المصرية العامة للمساحة", 19.5, 17.5),
    UMM_AL_QURA("جامعة أم القرى - مكة المكرمة", 18.5, 0.0, ishaFixedMinutes = 90),
    MUSLIM_WORLD_LEAGUE("رابطة العالم الإسلامي", 18.0, 17.0),
    ISNA("الجمعية الإسلامية لأمريكا الشمالية (ISNA)", 15.0, 15.0),
    KARACHI("جامعة العلوم الإسلامية بكراتشي", 18.0, 18.0),
    KUWAIT("دولة الكويت", 18.0, 17.5)
}

enum class JuristicMethod(val titleArabic: String, val shadowMultiplier: Double) {
    SHAFI_HANBALI_MALIKI("الجمهور (شافعي، مالكي، حنبلي)", 1.0),
    HANAFI("المذهب الحنفي", 2.0)
}

enum class PrayerType(val nameArabic: String, val iconRes: String) {
    FAJR("الفجر", "🌅"),
    SUNRISE("الشروق", "☀️"),
    DHUHR("الظهر", "🌤️"),
    ASR("العصر", "🌇"),
    MAGHRIB("المغرب", "🌄"),
    ISHA("العشاء", "🌙")
}

data class PrayerTimeItem(
    val type: PrayerType,
    val formattedTime: String,
    val timeMillis: Long,
    val isNext: Boolean = false
)

data class DayPrayerTimes(
    val dateFormatted: String,
    val cityName: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val times: List<PrayerTimeItem>,
    val nextPrayer: PrayerTimeItem?,
    val timeUntilNextFormatted: String
)
