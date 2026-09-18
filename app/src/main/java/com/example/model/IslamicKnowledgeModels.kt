package com.example.model

data class PrayerStep(
    val stepNumber: Int,
    val titleArabic: String,
    val description: String,
    val dhikr: String,
    val rukuOrSujoodDetails: String? = null
)

data class PrayerLesson(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String, // شروط الصلاة، أركان الوضوء، كيفية الصلاة، سنن الصلاة، مبطلات الصلاة
    val contentHtmlOrText: String,
    val steps: List<PrayerStep> = emptyList()
)

data class Hadith(
    val id: Int,
    val chapter: String,
    val textArabic: String,
    val narrator: String,
    val source: String,
    val explanation: String
)

data class ProphetStory(
    val id: Int,
    val nameArabic: String,
    val title: String,
    val quranMentions: String,
    val summary: String,
    val fullStory: String,
    val lessonsLearned: List<String>
)

data class RamadanNasheed(
    val id: Int,
    val title: String,
    val singer: String,
    val durationText: String,
    val audioUrl: String,
    val lyrics: String
)
