package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Represents decorative visual themes for Mushaf pages (أشكال وتصاميم المصحف الشريف).
 */
enum class MushafPageTheme(
    val id: String,
    val titleArabic: String,
    val descriptionArabic: String,
    val backgroundColor: Color,
    val pageBorderColor: Color,
    val innerFrameColor: Color,
    val textColor: Color,
    val ayahMedallionColor: Color,
    val surahBannerBg: Color,
    val surahBannerText: Color,
    val bismillahColor: Color,
    val highlightColor: Color
) {
    // 1. الرق الملكي الكلاسيكي (مصحف المدينة المنورة التقليدي المذهب)
    ROYAL_MADINAH(
        id = "royal_madinah",
        titleArabic = "مصحف المدينة الملكي",
        descriptionArabic = "الورق المصحفي الكلاسيكي بلون الرق مع الإطارات الذهبية المزخرفة",
        backgroundColor = Color(0xFFFAF6EB),
        pageBorderColor = Color(0xFFB88E3E),
        innerFrameColor = Color(0xFFD4AF37),
        textColor = Color(0xFF1B1B1B),
        ayahMedallionColor = Color(0xFFB88E3E),
        surahBannerBg = Color(0xFFF3EAD3),
        surahBannerText = Color(0xFF8B1A1A),
        bismillahColor = Color(0xFF8B1A1A),
        highlightColor = Color(0x66FFD54F)
    ),

    // 2. الرق العتيق الأندلسي / الشامي
    ANTIQUE_PARCHMENT(
        id = "antique_parchment",
        titleArabic = "الرق الأندلسي العتيق",
        descriptionArabic = "مظهر المخطوطات الأندلسية القديمة المريحة لعين القارئ",
        backgroundColor = Color(0xFFF4EBD7),
        pageBorderColor = Color(0xFF9E783D),
        innerFrameColor = Color(0xFFBCA062),
        textColor = Color(0xFF2A2118),
        ayahMedallionColor = Color(0xFF8D6E3F),
        surahBannerBg = Color(0xFFE8DCC2),
        surahBannerText = Color(0xFF6B2B20),
        bismillahColor = Color(0xFF6B2B20),
        highlightColor = Color(0x73FFE082)
    ),

    // 3. القراءة الليلية الزمردية (داكن ملكي مريح للعين في الظلام)
    NIGHT_EMERALD(
        id = "night_emerald",
        titleArabic = "المصحف الليلي الزمردي",
        descriptionArabic = "تصميم داكن فاخر مريح للعين في الإضاءة الخافتة بذهب ساطع",
        backgroundColor = Color(0xFF071B14),
        pageBorderColor = Color(0xFFD4AF37),
        innerFrameColor = Color(0xFF8F7425),
        textColor = Color(0xFFECEFF1),
        ayahMedallionColor = Color(0xFFFFDF73),
        surahBannerBg = Color(0xFF0F3226),
        surahBannerText = Color(0xFFFFDF73),
        bismillahColor = Color(0xFFFFDF73),
        highlightColor = Color(0x4DFFD700)
    ),

    // 4. المصحف الكحلي النوراني (الأزرق النيلي مع الخطوط الذهبية)
    MIDNIGHT_NAVY(
        id = "midnight_navy",
        titleArabic = "المصحف النيلي الذهبي",
        descriptionArabic = "خلفية كحلية داكنة ملكية مع براويز ذهبية ساطعة لقراءة هادئة",
        backgroundColor = Color(0xFF0A1526),
        pageBorderColor = Color(0xFFE5C158),
        innerFrameColor = Color(0xFF7E97B8),
        textColor = Color(0xFFF0F4F8),
        ayahMedallionColor = Color(0xFFFFE082),
        surahBannerBg = Color(0xFF13243F),
        surahBannerText = Color(0xFFFFD54F),
        bismillahColor = Color(0xFFFFE082),
        highlightColor = Color(0x594FC3F7)
    ),

    // 5. المصحف الأبيض الناصع فائق الوضوح
    PURE_WHITE(
        id = "pure_white",
        titleArabic = "المصحف الأبيض النقي",
        descriptionArabic = "بياض ناصع عالي التباين للقراءة النهارية المشرقة بوضوح تام",
        backgroundColor = Color(0xFFFFFFFF),
        pageBorderColor = Color(0xFF2E7D32),
        innerFrameColor = Color(0xFF81C784),
        textColor = Color(0xFF0A0A0A),
        ayahMedallionColor = Color(0xFF2E7D32),
        surahBannerBg = Color(0xFFE8F5E9),
        surahBannerText = Color(0xFF1B5E20),
        bismillahColor = Color(0xFF1B5E20),
        highlightColor = Color(0x66A5D6A7)
    );

    companion object {
        fun fromId(id: String?): MushafPageTheme {
            return entries.firstOrNull { it.id == id } ?: ROYAL_MADINAH
        }
    }
}
