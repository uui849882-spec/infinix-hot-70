package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Ayah
import com.example.model.MushafPage
import com.example.model.MushafPageTheme
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldBright

@Composable
fun MushafPageView(
    page: MushafPage,
    isBookmarked: Boolean,
    fontSizeSp: Float = 22f,
    theme: MushafPageTheme = MushafPageTheme.ROYAL_MADINAH,
    activeHighlightedAyahNumber: Int? = null,
    onToggleBookmark: () -> Unit,
    onOpenTafsir: () -> Unit,
    onAyahClick: (Ayah) -> Unit,
    onThemeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = theme.backgroundColor,
        border = androidx.compose.foundation.BorderStroke(2.5.dp, theme.pageBorderColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Bookmark Ribbon Visual on Top Left if bookmarked
            if (isBookmarked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 18.dp)
                        .width(22.dp)
                        .height(38.dp)
                        .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                        .background(Color(0xFF8B1A1A))
                        .shadow(4.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Outer Inner Frame with fine ornate border
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, theme.innerFrameColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. TOP HEADER (ترويسة الصفحة)
                        MushafPageHeader(
                            surahName = page.surahName,
                            juzNumber = page.juzNumber,
                            hizbNumber = page.hizbNumber,
                            theme = theme,
                            isBookmarked = isBookmarked,
                            onToggleBookmark = onToggleBookmark,
                            onThemeClick = onThemeClick
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        GoldenDivider(theme.innerFrameColor)
                        Spacer(modifier = Modifier.height(4.dp))

                        // 2. PAGE CONTENT (Scrollable with verse highlighting as Sheikh recites)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (page.isSurahStart) {
                                SurahHeaderBanner(
                                    surahName = page.surahName,
                                    revelationArabic = page.surahType.arabicName,
                                    versesCount = page.verses.size,
                                    theme = theme
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                if (page.hasBismillah) {
                                    Text(
                                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                        fontSize = (fontSizeSp + 2).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = theme.bismillahColor,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            }

                            // Verses continuous flow with real-time Sheikh recitation highlighting
                            MushafVersesFlow(
                                verses = page.verses,
                                fontSizeSp = fontSizeSp,
                                theme = theme,
                                highlightedAyahNumber = activeHighlightedAyahNumber,
                                onAyahClick = onAyahClick
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        GoldenDivider(theme.innerFrameColor)
                        Spacer(modifier = Modifier.height(4.dp))

                        // 3. BOTTOM FOOTER (رقم الصفحة وزر التفسير وثيم المصحف)
                        MushafPageFooter(
                            pageNumber = page.pageNumber,
                            theme = theme,
                            onOpenTafsir = onOpenTafsir,
                            onThemeClick = onThemeClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MushafPageHeader(
    surahName: String,
    juzNumber: Int,
    hizbNumber: Int,
    theme: MushafPageTheme,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onThemeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Right badge: Surah name
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = theme.innerFrameColor.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, theme.pageBorderColor.copy(alpha = 0.5f))
        ) {
            Text(
                text = "سُورَةُ $surahName",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = theme.pageBorderColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }

        // Action Buttons: Theme selector & Bookmark
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onThemeClick,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "تغيير شكل المصحف",
                    tint = theme.pageBorderColor,
                    modifier = Modifier.size(17.dp)
                )
            }

            IconButton(
                onClick = onToggleBookmark,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "حفظ الفاصلة",
                    tint = if (isBookmarked) Color(0xFF8B1A1A) else theme.pageBorderColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Left badge: Juz & Hizb
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = theme.innerFrameColor.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, theme.pageBorderColor.copy(alpha = 0.5f))
        ) {
            Text(
                text = "الجزء $juzNumber • الحزب $hizbNumber",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = theme.pageBorderColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun SurahHeaderBanner(
    surahName: String,
    revelationArabic: String,
    versesCount: Int,
    theme: MushafPageTheme
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        color = theme.surahBannerBg,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.pageBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "آياتها $versesCount",
                fontSize = 11.sp,
                color = theme.surahBannerText.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "۞ سُورَةُ $surahName ۞",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = theme.surahBannerText
            )

            Text(
                text = revelationArabic,
                fontSize = 11.sp,
                color = theme.surahBannerText.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MushafVersesFlow(
    verses: List<Ayah>,
    fontSizeSp: Float,
    theme: MushafPageTheme,
    highlightedAyahNumber: Int?,
    onAyahClick: (Ayah) -> Unit
) {
    val annotatedText = buildAnnotatedString {
        verses.forEach { ayah ->
            val isHighlighted = (highlightedAyahNumber != null && ayah.ayahNumber == highlightedAyahNumber)

            val textColor = if (isHighlighted) theme.surahBannerText else theme.textColor
            val bgColor = if (isHighlighted) theme.highlightColor else Color.Transparent

            withStyle(
                style = SpanStyle(
                    color = textColor,
                    background = bgColor,
                    fontSize = fontSizeSp.sp,
                    fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
            ) {
                append(ayah.textArabic)
            }

            // Ornate verse separator medallion
            withStyle(
                style = SpanStyle(
                    color = if (isHighlighted) theme.surahBannerText else theme.ayahMedallionColor,
                    background = if (isHighlighted) theme.highlightColor else Color.Transparent,
                    fontSize = (fontSizeSp * 0.9f).sp,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(" ﴿${ayah.ayahNumber.toArabicDigits()}﴾ ")
            }
        }
    }

    Text(
        text = annotatedText,
        fontSize = fontSizeSp.sp,
        lineHeight = (fontSizeSp * 1.85f).sp,
        textAlign = TextAlign.Justify,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .clickable {
                if (verses.isNotEmpty()) {
                    onAyahClick(verses.first())
                }
            }
    )
}

@Composable
private fun MushafPageFooter(
    pageNumber: Int,
    theme: MushafPageTheme,
    onOpenTafsir: () -> Unit,
    onThemeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Quick Tafsir Button
        Surface(
            modifier = Modifier.clickable { onOpenTafsir() },
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF1B4E3C),
            border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.7f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = IslamicGoldBright,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "التفسير الميسر",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Center Medallion with Page Number
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = theme.innerFrameColor.copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(1.dp, theme.pageBorderColor)
        ) {
            Text(
                text = "—  ${pageNumber.toArabicDigits()}  —",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = theme.pageBorderColor,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
            )
        }

        // Theme Style Chip Button
        Surface(
            modifier = Modifier.clickable { onThemeClick() },
            shape = RoundedCornerShape(10.dp),
            color = theme.innerFrameColor.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, theme.pageBorderColor.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = theme.pageBorderColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "شكل المصحف",
                    fontSize = 10.sp,
                    color = theme.pageBorderColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun GoldenDivider(color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        color.copy(alpha = 0.8f),
                        color,
                        color.copy(alpha = 0.8f),
                        Color.Transparent
                    )
                )
            )
    )
}

fun Int.toArabicDigits(): String {
    val easternDigits = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    val str = this.toString()
    val sb = StringBuilder()
    for (ch in str) {
        if (ch in '0'..'9') {
            sb.append(easternDigits[ch - '0'])
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}
