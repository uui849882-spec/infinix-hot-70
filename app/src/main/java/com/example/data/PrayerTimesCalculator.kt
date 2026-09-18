package com.example.data

import com.example.model.CalculationMethod
import com.example.model.DayPrayerTimes
import com.example.model.JuristicMethod
import com.example.model.PrayerTimeItem
import com.example.model.PrayerType
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

/**
 * High-precision astronomical calculation engine for Islamic prayer times
 * based on solar positioning, equation of time, solar declination, and juristic shadows.
 */
object PrayerTimesCalculator {

    fun calculatePrayerTimes(
        latitude: Double,
        longitude: Double,
        date: Calendar = Calendar.getInstance(),
        timeZoneOffsetHours: Double = (TimeZone.getDefault().getOffset(date.timeInMillis) / 3600000.0),
        method: CalculationMethod = CalculationMethod.EGYPTIAN,
        juristic: JuristicMethod = JuristicMethod.SHAFI_HANBALI_MALIKI,
        cityName: String = "موقعي الحالي"
    ): DayPrayerTimes {
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH) + 1
        val day = date.get(Calendar.DAY_OF_MONTH)

        // Julian Date calculation
        val julianDate = julianDate(year, month, day) - (longitude / (15.0 * 24.0))

        // Solar parameters
        val solarCoords = calculateSolarCoordinates(julianDate)
        val solarDeclination = solarCoords.first // declination in degrees
        val equationOfTime = solarCoords.second  // equation of time in minutes

        // Solar Noon (Zawal / Dhuhr base)
        val dhuhrLocal = 12.0 + timeZoneOffsetHours - (longitude / 15.0) - (equationOfTime / 60.0)

        // Sunrise & Sunset (center of sun 50 arcminutes below horizon due to refraction)
        val sunAltAngle = -0.8333
        val sunHalfDayHours = calculateSunHourAngle(latitude, solarDeclination, sunAltAngle) / 15.0

        val sunriseLocal = dhuhrLocal - sunHalfDayHours
        val sunsetLocal = dhuhrLocal + sunHalfDayHours

        // Fajr
        val fajrHalfDayHours = calculateSunHourAngle(latitude, solarDeclination, -method.fajrAngle) / 15.0
        val fajrLocal = dhuhrLocal - fajrHalfDayHours

        // Asr calculation (Shafi/Hanbali/Maliki: shadow = object + noon; Hanafi: shadow = 2*object + noon)
        val latRad = Math.toRadians(latitude)
        val decRad = Math.toRadians(solarDeclination)
        val noonAngle = abs(latRad - decRad)
        val asrAltitudeRad = atan(1.0 / (juristic.shadowMultiplier + tan(noonAngle)))
        val asrAltitudeDeg = Math.toDegrees(asrAltitudeRad)
        val asrHalfDayHours = calculateSunHourAngle(latitude, solarDeclination, asrAltitudeDeg) / 15.0
        val asrLocal = dhuhrLocal + asrHalfDayHours

        // Maghrib (identical or slight offset to Sunset)
        val maghribLocal = sunsetLocal + (1.0 / 60.0) // 1 minute buffer for safety

        // Isha
        val ishaLocal = if (method.ishaFixedMinutes > 0) {
            maghribLocal + (method.ishaFixedMinutes / 60.0)
        } else {
            val ishaHalfDayHours = calculateSunHourAngle(latitude, solarDeclination, -method.ishaAngle) / 15.0
            dhuhrLocal + ishaHalfDayHours
        }

        // Convert double hours to calendar time millis & formatted strings
        val fajrCal = calendarFromHours(date, fajrLocal)
        val sunriseCal = calendarFromHours(date, sunriseLocal)
        val dhuhrCal = calendarFromHours(date, dhuhrLocal)
        val asrCal = calendarFromHours(date, asrLocal)
        val maghribCal = calendarFromHours(date, maghribLocal)
        val ishaCal = calendarFromHours(date, ishaLocal)

        val nowMillis = System.currentTimeMillis()

        val list = listOf(
            PrayerTimeItem(PrayerType.FAJR, formatTime(fajrCal), fajrCal.timeInMillis),
            PrayerTimeItem(PrayerType.SUNRISE, formatTime(sunriseCal), sunriseCal.timeInMillis),
            PrayerTimeItem(PrayerType.DHUHR, formatTime(dhuhrCal), dhuhrCal.timeInMillis),
            PrayerTimeItem(PrayerType.ASR, formatTime(asrCal), asrCal.timeInMillis),
            PrayerTimeItem(PrayerType.MAGHRIB, formatTime(maghribCal), maghribCal.timeInMillis),
            PrayerTimeItem(PrayerType.ISHA, formatTime(ishaCal), ishaCal.timeInMillis)
        )

        // Find next prayer (excluding sunrise for main obligation prayer or keeping for reference)
        val nextItem = list.firstOrNull { it.timeMillis > nowMillis } ?: list.first().let {
            // Next is tomorrow's Fajr
            val tomorrowFajr = Calendar.getInstance().apply {
                timeInMillis = it.timeMillis
                add(Calendar.DAY_OF_YEAR, 1)
            }
            it.copy(timeMillis = tomorrowFajr.timeInMillis)
        }

        val updatedList = list.map { it.copy(isNext = it.type == nextItem.type) }

        val diffMs = nextItem.timeMillis - nowMillis
        val hoursRemaining = diffMs / 3600000L
        val minutesRemaining = (diffMs % 3600000L) / 60000L
        val timeUntilFormatted = if (hoursRemaining > 0) {
            "باقٍ $hoursRemaining ساعة و $minutesRemaining دقيقة"
        } else {
            "باقٍ $minutesRemaining دقيقة فقط"
        }

        val dateFormatted = "${date.get(Calendar.DAY_OF_MONTH)}/${date.get(Calendar.MONTH) + 1}/${date.get(Calendar.YEAR)}"

        return DayPrayerTimes(
            dateFormatted = dateFormatted,
            cityName = cityName,
            fajr = formatTime(fajrCal),
            sunrise = formatTime(sunriseCal),
            dhuhr = formatTime(dhuhrCal),
            asr = formatTime(asrCal),
            maghrib = formatTime(maghribCal),
            isha = formatTime(ishaCal),
            times = updatedList,
            nextPrayer = nextItem,
            timeUntilNextFormatted = timeUntilFormatted
        )
    }

    private fun calendarFromHours(baseDate: Calendar, hourFraction: Double): Calendar {
        val cal = baseDate.clone() as Calendar
        var normalizedHours = hourFraction
        while (normalizedHours < 0) normalizedHours += 24.0
        while (normalizedHours >= 24) normalizedHours -= 24.0

        val hours = floor(normalizedHours).toInt()
        val minutes = floor((normalizedHours - hours) * 60.0).toInt()
        val seconds = floor((((normalizedHours - hours) * 60.0) - minutes) * 60.0).toInt()

        cal.set(Calendar.HOUR_OF_DAY, hours)
        cal.set(Calendar.MINUTE, minutes)
        cal.set(Calendar.SECOND, seconds)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }

    private fun formatTime(cal: Calendar): String {
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val isPm = hour >= 12
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val period = if (isPm) "م" else "ص"
        val minStr = if (minute < 10) "0$minute" else "$minute"
        return "$displayHour:$minStr $period"
    }

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2.0 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun calculateSolarCoordinates(jd: Double): Pair<Double, Double> {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))

        val e = 23.439 - 0.00000036 * d
        val ra = fixAngle(Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l))))) / 15.0
        val declination = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(l))))
        val equationOfTime = (q / 15.0) - ra
        return Pair(declination, equationOfTime * 60.0)
    }

    private fun calculateSunHourAngle(latitude: Double, declination: Double, altitude: Double): Double {
        val latRad = Math.toRadians(latitude)
        val decRad = Math.toRadians(declination)
        val altRad = Math.toRadians(altitude)

        val cosHourAngle = (sin(altRad) - (sin(latRad) * sin(decRad))) / (cos(latRad) * cos(decRad))
        val clamped = cosHourAngle.coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(clamped))
    }

    private fun fixAngle(angle: Double): Double {
        var a = angle - (360.0 * floor(angle / 360.0))
        if (a < 0) a += 360.0
        return a
    }
}
