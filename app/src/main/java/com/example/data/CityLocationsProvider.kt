package com.example.data

import com.example.model.CityLocation

object CityLocationsProvider {

    val cities: List<CityLocation> = listOf(
        CityLocation("مكة المكرمة", "المملكة العربية السعودية", 21.4225, 39.8262),
        CityLocation("المدينة المنورة", "المملكة العربية السعودية", 24.4672, 39.6111),
        CityLocation("الرياض", "المملكة العربية السعودية", 24.7136, 46.6753),
        CityLocation("جدة", "المملكة العربية السعودية", 21.5433, 39.1728),
        CityLocation("القاهرة", "جمهورية مصر العربية", 30.0444, 31.2357),
        CityLocation("الإسكندرية", "جمهورية مصر العربية", 31.2001, 29.9187),
        CityLocation("طنطا", "جمهورية مصر العربية", 30.7865, 31.0004),
        CityLocation("المنصورة", "جمهورية مصر العربية", 31.0409, 31.3785),
        CityLocation("أسيوط", "جمهورية مصر العربية", 27.1809, 31.1837),
        CityLocation("القدس الشريف", "فلسطين", 31.7683, 35.2137),
        CityLocation("غزة", "فلسطين", 31.5017, 34.4668),
        CityLocation("عمان", "المملكة الأردنية الهاشمية", 31.9539, 35.9106),
        CityLocation("دمشق", "الجمهورية العربية السورية", 33.5138, 36.2765),
        CityLocation("بيروت", "الجمهورية اللبنانية", 33.8938, 35.5018),
        CityLocation("بغداد", "جمهورية العراق", 33.3152, 44.3661),
        CityLocation("الكويت", "دولة الكويت", 29.3759, 47.9774),
        CityLocation("الدوحة", "دولة قطر", 25.2854, 51.5310),
        CityLocation("أبوظبي", "الإمارات العربية المتحدة", 24.4539, 54.3773),
        CityLocation("دبي", "الإمارات العربية المتحدة", 25.2048, 55.2708),
        CityLocation("المنامة", "مملكة البحرين", 26.2285, 50.5860),
        CityLocation("مسقط", "سلطنة عمان", 23.5880, 58.3829),
        CityLocation("صنعاء", "الجمهورية اليمنية", 15.3694, 44.1910),
        CityLocation("الخرطوم", "جمهورية السودان", 15.5007, 32.5599),
        CityLocation("طرابلس", "دولة ليبيا", 32.8872, 13.1913),
        CityLocation("تونس", "الجمهورية التونسية", 36.8065, 10.1815),
        CityLocation("الجزائر", "الجمهورية الجزائرية", 36.7538, 3.0588),
        CityLocation("الرباط", "المملكة المغربية", 34.0209, -6.8416),
        CityLocation("الدار البيضاء", "المملكة المغربية", 33.5731, -7.5898),
        CityLocation("نواكشوط", "الجمهورية الإسلامية الموريتانية", 18.0735, -15.9582),
        CityLocation("إسطنبول", "الجمهورية التركية", 41.0082, 28.9784),
        CityLocation("أنقرة", "الجمهورية التركية", 39.9334, 32.8597),
        CityLocation("جاكرتا", "جمهورية إندونيسيا", -6.2088, 106.8456),
        CityLocation("كوالالمبور", "مملكة ماليزيا", 3.1390, 101.6869),
        CityLocation("إسلام آباد", "جمهورية باكستان الإسلامية", 33.6844, 73.0479),
        CityLocation("لندن", "المملكة المتحدة", 51.5074, -0.1278),
        CityLocation("باريس", "الجمهورية الفرنسية", 48.8566, 2.3522),
        CityLocation("برلين", "جمهورية ألمانيا الاتحادية", 52.5200, 13.4050),
        CityLocation("نيويورك", "الولايات المتحدة الأمريكية", 40.7128, -74.0060)
    )

    /**
     * Calculates the exact Qibla direction (bearing in degrees clockwise from True North)
     * using the Great Circle spherical trigonometry formula towards the Holy Kaaba.
     * Kaaba: Lat 21.422487° N, Lon 39.826206° E
     */
    fun calculateQiblaAngle(latitude: Double, longitude: Double): Double {
        val kaabaLat = Math.toRadians(21.422487)
        val kaabaLon = Math.toRadians(39.826206)

        val userLat = Math.toRadians(latitude)
        val userLon = Math.toRadians(longitude)

        val deltaLon = kaabaLon - userLon

        val y = Math.sin(deltaLon)
        val x = Math.cos(userLat) * Math.tan(kaabaLat) - Math.sin(userLat) * Math.cos(deltaLon)

        var qiblaDegrees = Math.toDegrees(Math.atan2(y, x))
        qiblaDegrees = (qiblaDegrees + 360) % 360

        return qiblaDegrees
    }
}
