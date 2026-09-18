package com.example.model

/**
 * Represents a Surah in the Holy Quran.
 */
data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val versesCount: Int,
    val revelationType: RevelationType, // Meccan or Medinan
    val pageNumber: Int,
    val juzNumber: Int
)

enum class RevelationType(val arabicName: String) {
    MECCAN("مكية"),
    MEDINAN("مدنية")
}

/**
 * Represents a single Ayah with its text and Al-Tafsir Al-Muyassar explanation.
 */
data class Ayah(
    val surahNumber: Int,
    val ayahNumber: Int,
    val textArabic: String,
    val tafsirMuyassar: String
)

/**
 * Represents a Reciter (Sheikh) from the 400 distinct reciters directory.
 */
data class Reciter(
    val id: Int,
    val name: String,
    val subName: String = "",
    val riwayah: String = "حفص عن عاصم",
    val serverUrl: String,
    val country: String = "العالم الإسلامي",
    val isFavorite: Boolean = false,
    val localImageRes: Int? = null,
    val photoUrl: String = ""
)

/**
 * Represents an Azkar chapter out of the 132 Hisn Al-Muslim chapters.
 */
data class AzkarChapter(
    val id: Int,
    val title: String,
    val items: List<AzkarItem>
)

data class AzkarItem(
    val id: Int,
    val text: String,
    val repeatCount: Int = 1,
    val virtue: String = "",
    val reference: String = ""
) {
    val textArabic: String get() = text
    val count: Int get() = repeatCount
    val fadl: String get() = virtue
}

/**
 * City coordinates for precise Qibla tuning.
 */
data class CityLocation(
    val nameArabic: String,
    val countryArabic: String,
    val latitude: Double,
    val longitude: Double
)

/**
 * Represents a printed page in the standard 604-page Holy Quran (Madinah Mushaf).
 */
data class MushafPage(
    val pageNumber: Int,
    val juzNumber: Int,
    val hizbNumber: Int,
    val surahNumber: Int,
    val surahName: String,
    val surahType: RevelationType = RevelationType.MECCAN,
    val versesRange: String,
    val isSurahStart: Boolean = false,
    val hasBismillah: Boolean = true,
    val verses: List<Ayah>
)

/**
 * State representing audio player playback in foreground service and UI.
 */
data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentSurah: Surah? = null,
    val currentReciter: Reciter? = null,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val playbackSpeed: Float = 1.0f,
    val errorMessage: String? = null
)

