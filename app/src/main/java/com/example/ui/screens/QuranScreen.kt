package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import com.example.model.MushafPageTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.QuranDownloadManager
import com.example.data.MushafPagesDataProvider
import com.example.data.QuranBookmarkManager
import com.example.data.QuranDataProvider
import com.example.data.RecitersDataProvider
import com.example.model.Ayah
import com.example.model.MushafPage
import com.example.model.Surah
import com.example.ui.components.AyahItemCard
import com.example.ui.components.MushafPageView
import com.example.ui.components.toArabicDigits
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldBright
import kotlinx.coroutines.launch

enum class QuranDisplayMode {
    MUSHAF_PAGES, // صفحات المصحف الحقيقي الورقي مع التقليب
    SURAHS_LIST   // فهرس السور والآيات
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    onPlaySurah: (Surah) -> Unit,
    currentPlayingSurah: Surah? = null,
    isPlayingAudio: Boolean = false,
    currentPositionMs: Int = 0,
    durationMs: Int = 0,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var displayMode by remember { mutableStateOf(QuranDisplayMode.MUSHAF_PAGES) }
    val initialPage = remember { QuranBookmarkManager.getLastReadPage(context).coerceIn(1, 604) }

    val pagerState = rememberPagerState(
        initialPage = initialPage - 1,
        pageCount = { MushafPagesDataProvider.TOTAL_PAGES }
    )

    // Sync last read page
    LaunchedEffect(pagerState.currentPage) {
        QuranBookmarkManager.setLastReadPage(context, pagerState.currentPage + 1)
    }

    // Automatically navigate the Mushaf to the playing Surah's start page when playback begins
    LaunchedEffect(currentPlayingSurah) {
        if (currentPlayingSurah != null && isPlayingAudio) {
            val targetPage = currentPlayingSurah.pageNumber.coerceIn(1, MushafPagesDataProvider.TOTAL_PAGES)
            if (pagerState.currentPage != targetPage - 1) {
                pagerState.scrollToPage(targetPage - 1)
            }
        }
    }

    var bookmarkedPage by remember {
        mutableIntStateOf(QuranBookmarkManager.getBookmarkPage(context))
    }

    var fontSizeSp by remember { mutableFloatStateOf(22f) }
    var selectedTheme by remember { mutableStateOf(MushafPageTheme.ROYAL_MADINAH) }
    var showThemeDialog by remember { mutableStateOf(false) }

    // Dialog States
    var showJumpPageDialog by remember { mutableStateOf(false) }
    var showSurahJumpDialog by remember { mutableStateOf(false) }
    var showJuzJumpDialog by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }
    var showTafsirDialog by remember { mutableStateOf(false) }
    var currentTafsirPage by remember { mutableStateOf<MushafPage?>(null) }
    var selectedAyahForTafsir by remember { mutableStateOf<Ayah?>(null) }

    // Surah list view states
    var selectedSurahInList by remember { mutableStateOf<Surah?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val currentPageNumber = pagerState.currentPage + 1

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top Navigation Bar: Mode Selector & Title
            Surface(
                color = Color(0xDD0B241B),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "المصحف الشريف",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGoldBright
                        )

                        // Display Mode Toggle
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF1B4E3C))
                                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        ) {
                            Surface(
                                color = if (displayMode == QuranDisplayMode.MUSHAF_PAGES) IslamicGold else Color.Transparent,
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.clickable { displayMode = QuranDisplayMode.MUSHAF_PAGES }
                            ) {
                                Text(
                                    text = "📖 صفحات المصحف",
                                    color = if (displayMode == QuranDisplayMode.MUSHAF_PAGES) IslamicEmeraldDark else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }

                            Surface(
                                color = if (displayMode == QuranDisplayMode.SURAHS_LIST) IslamicGold else Color.Transparent,
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.clickable { displayMode = QuranDisplayMode.SURAHS_LIST }
                            ) {
                                Text(
                                    text = "📜 فهرس السور",
                                    color = if (displayMode == QuranDisplayMode.SURAHS_LIST) IslamicEmeraldDark else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // MAIN CONTENT BASED ON SELECTED MODE
            when (displayMode) {
                QuranDisplayMode.MUSHAF_PAGES -> {
                    // 1. PAGE-BY-PAGE MUSHAF WITH SMOOTH HORIZONTAL PAGER (قلب الصفحات)
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { pageIdx ->
                            val pageData = remember(pageIdx) {
                                MushafPagesDataProvider.getPage(pageIdx + 1)
                            }
                            val isCurrentBookmarked = (pageIdx + 1) == bookmarkedPage

                            // Real-time Audio-Text Synchronization (تظليل الآية مع صوت الشيخ)
                            val highlightedAyahNumber = remember(currentPlayingSurah, currentPositionMs, durationMs, pageData) {
                                if (isPlayingAudio && currentPlayingSurah != null && currentPlayingSurah.number == pageData.surahNumber && durationMs > 0) {
                                    val totalAyahs = currentPlayingSurah.versesCount.coerceAtLeast(1)
                                    val estimatedAyah = (((currentPositionMs.toDouble() / durationMs.toDouble()) * totalAyahs).toInt() + 1).coerceIn(1, totalAyahs)
                                    // Check if this ayah is on this page
                                    if (pageData.verses.any { it.ayahNumber == estimatedAyah }) estimatedAyah else null
                                } else null
                            }

                            MushafPageView(
                                page = pageData,
                                isBookmarked = isCurrentBookmarked,
                                fontSizeSp = fontSizeSp,
                                theme = selectedTheme,
                                activeHighlightedAyahNumber = highlightedAyahNumber,
                                onToggleBookmark = {
                                    if (isCurrentBookmarked) {
                                        QuranBookmarkManager.setBookmarkPage(context, 0)
                                        bookmarkedPage = 0
                                        Toast.makeText(context, "تم إزالة الفاصلة المرجعية", Toast.LENGTH_SHORT).show()
                                    } else {
                                        QuranBookmarkManager.setBookmarkPage(context, pageIdx + 1)
                                        bookmarkedPage = pageIdx + 1
                                        Toast.makeText(context, "تم وضع الفاصلة عند صفحة ${pageIdx + 1} 🔖", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onOpenTafsir = {
                                    currentTafsirPage = pageData
                                    selectedAyahForTafsir = null
                                    showTafsirDialog = true
                                },
                                onAyahClick = { clickedAyah ->
                                    currentTafsirPage = pageData
                                    selectedAyahForTafsir = clickedAyah
                                    showTafsirDialog = true
                                },
                                onThemeClick = {
                                    showThemeDialog = true
                                }
                            )
                        }
                    }

                    // Bottom Floating Quick Toolbar for Mushaf Page Navigation
                    Surface(
                        color = Color(0xF00A1F17),
                        modifier = Modifier.fillMaxWidth(),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Turn Page Next/Prev Buttons
                            IconButton(
                                onClick = {
                                    if (pagerState.currentPage > 0) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                        }
                                    }
                                },
                                enabled = pagerState.currentPage > 0,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "الصفحة السابقة",
                                    tint = if (pagerState.currentPage > 0) IslamicGoldBright else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Jump Page Action Button
                            Surface(
                                modifier = Modifier.clickable { showJumpPageDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1B4E3C),
                                border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = "صفحة $currentPageNumber من ٦٠٤",
                                    color = IslamicGoldBright,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }

                            // Surah Jump
                            IconButton(
                                onClick = { showSurahJumpDialog = true },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "انتقال لسورة",
                                    tint = IslamicGoldBright,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Juz Jump
                            IconButton(
                                onClick = { showJuzJumpDialog = true },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = "انتقال لجزء",
                                    tint = IslamicGoldBright,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Return to Bookmark Button
                            IconButton(
                                onClick = {
                                    val saved = QuranBookmarkManager.getBookmarkPage(context)
                                    if (saved in 1..604) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(saved - 1)
                                        }
                                        Toast.makeText(context, "تم الانتقال إلى الفاصلة (صفحة $saved)", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "لم تقم بحفظ فاصلة بعد", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "الانتقال للفاصلة",
                                    tint = if (bookmarkedPage > 0) Color(0xFFE57373) else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Theme Style Selector
                            IconButton(
                                onClick = { showThemeDialog = true },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "شكل وتصميم المصحف",
                                    tint = IslamicGoldBright,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Font size
                            IconButton(
                                onClick = { showFontSizeDialog = true },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatSize,
                                    contentDescription = "حجم الخط",
                                    tint = IslamicGoldBright,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Next Page Button
                            IconButton(
                                onClick = {
                                    if (pagerState.currentPage < MushafPagesDataProvider.TOTAL_PAGES - 1) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        }
                                    }
                                },
                                enabled = pagerState.currentPage < MushafPagesDataProvider.TOTAL_PAGES - 1,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "الصفحة التالية",
                                    tint = if (pagerState.currentPage < MushafPagesDataProvider.TOTAL_PAGES - 1) IslamicGoldBright else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                QuranDisplayMode.SURAHS_LIST -> {
                    // 2. SURAHS LIST VIEW WITH AUDIO & DIRECT JUMP
                    if (selectedSurahInList == null) {
                        SurahsListView(
                            searchQuery = searchQuery,
                            onSearchChange = { searchQuery = it },
                            onSelectSurah = { surah -> selectedSurahInList = surah },
                            onPlaySurah = onPlaySurah,
                            onOpenInMushaf = { surah ->
                                coroutineScope.launch {
                                    pagerState.scrollToPage(surah.pageNumber - 1)
                                    displayMode = QuranDisplayMode.MUSHAF_PAGES
                                }
                            }
                        )
                    } else {
                        // Verses list view for the selected surah
                        SurahVersesListView(
                            surah = selectedSurahInList!!,
                            fontSize = fontSizeSp,
                            onBack = { selectedSurahInList = null },
                            onPlay = { onPlaySurah(selectedSurahInList!!) },
                            onOpenInMushaf = {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(selectedSurahInList!!.pageNumber - 1)
                                    displayMode = QuranDisplayMode.MUSHAF_PAGES
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // --- DIALOGS ---

    // 1. Jump to Page Dialog
    if (showJumpPageDialog) {
        var targetPageInput by remember { mutableFloatStateOf(currentPageNumber.toFloat()) }
        AlertDialog(
            onDismissRequest = { showJumpPageDialog = false },
            containerColor = Color(0xFF0F3024),
            title = {
                Text(
                    text = "الانتقال إلى صفحة في المصحف",
                    color = IslamicGoldBright,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "صفحة رقم: ${targetPageInput.toInt().toArabicDigits()}",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Slider(
                        value = targetPageInput,
                        onValueChange = { targetPageInput = it },
                        valueRange = 1f..604f,
                        steps = 603,
                        colors = SliderDefaults.colors(
                            thumbColor = IslamicGoldBright,
                            activeTrackColor = IslamicGold,
                            inactiveTrackColor = Color.DarkGray
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "أو اختر أرقام سريعة:",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(1, 42, 293, 440, 562, 582).forEach { pageNum ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1B4E3C),
                                modifier = Modifier.clickable { targetPageInput = pageNum.toFloat() }
                            ) {
                                Text(
                                    text = "$pageNum",
                                    color = IslamicGoldBright,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pageToGo = targetPageInput.toInt().coerceIn(1, 604)
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pageToGo - 1)
                        }
                        showJumpPageDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGold)
                ) {
                    Text("انتقال للصفحة", color = IslamicEmeraldDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showJumpPageDialog = false }) {
                    Text("إلغاء", color = Color.White)
                }
            }
        )
    }

    // 2. Surah Jump Dialog (114 Surahs)
    if (showSurahJumpDialog) {
        AlertDialog(
            onDismissRequest = { showSurahJumpDialog = false },
            containerColor = Color(0xFF0F3024),
            title = {
                Text(
                    text = "اختر سورة للانتقال لصفحتها",
                    color = IslamicGoldBright,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                LazyColumn(modifier = Modifier.height(350.dp)) {
                    items(QuranDataProvider.surahs) { surah ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(surah.pageNumber - 1)
                                    }
                                    showSurahJumpDialog = false
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF163E30)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${surah.number}. سورة ${surah.nameArabic}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "صفحة ${surah.pageNumber}",
                                    color = IslamicGoldBright,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSurahJumpDialog = false }) {
                    Text("إغلاق", color = Color.White)
                }
            }
        )
    }

    // 3. Juz Jump Dialog (30 Juz)
    if (showJuzJumpDialog) {
        AlertDialog(
            onDismissRequest = { showJuzJumpDialog = false },
            containerColor = Color(0xFF0F3024),
            title = {
                Text(
                    text = "اختر جزءاً للانتقال لصفحته",
                    color = IslamicGoldBright,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                LazyColumn(modifier = Modifier.height(350.dp)) {
                    items(MushafPagesDataProvider.JUZ_START_PAGES) { juzPair ->
                        val juzNum = juzPair.first
                        val startPage = juzPair.second
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(startPage - 1)
                                    }
                                    showJuzJumpDialog = false
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF163E30)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "الجزء ${juzNum.toArabicDigits()}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "يبدأ بصفحة $startPage",
                                    color = IslamicGoldBright,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showJuzJumpDialog = false }) {
                    Text("إغلاق", color = Color.White)
                }
            }
        )
    }

    // 4. Tafsir Al-Muyassar Dialog for Current Page Verses
    if (showTafsirDialog && currentTafsirPage != null) {
        val page = currentTafsirPage!!
        AlertDialog(
            onDismissRequest = { showTafsirDialog = false },
            containerColor = Color(0xFF0C261D),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "التفسير الميسر (صفحة ${page.pageNumber})",
                            color = IslamicGoldBright,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "سورة ${page.surahName} • الآيات ${page.versesRange}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = { showTafsirDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }
            },
            text = {
                LazyColumn(modifier = Modifier.height(400.dp)) {
                    items(page.verses) { ayah ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF163E30),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedAyahForTafsir?.ayahNumber == ayah.ayahNumber) IslamicGold else IslamicGold.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "﴿${ayah.ayahNumber}﴾ ${ayah.textArabic}",
                                    color = IslamicGoldBright,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = ayah.tafsirMuyassar,
                                    color = Color.White.copy(alpha = 0.95f),
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showTafsirDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGold)
                ) {
                    Text("تم", color = IslamicEmeraldDark, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 5. Font Size Dialog
    if (showFontSizeDialog) {
        AlertDialog(
            onDismissRequest = { showFontSizeDialog = false },
            containerColor = Color(0xFF0F3024),
            title = {
                Text(
                    text = "تعديل حجم خط المصحف",
                    color = IslamicGoldBright,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        fontSize = fontSizeSp.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Slider(
                        value = fontSizeSp,
                        onValueChange = { fontSizeSp = it },
                        valueRange = 16f..34f,
                        colors = SliderDefaults.colors(
                            thumbColor = IslamicGoldBright,
                            activeTrackColor = IslamicGold
                        )
                    )
                    Text(
                        text = "${fontSizeSp.toInt()} نقطة",
                        color = IslamicGoldBright,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showFontSizeDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGold)
                ) {
                    Text("حفظ", color = IslamicEmeraldDark, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 6. Mushaf Theme / Design Selector Dialog (أشكال وتصاميم المصحف الشريف)
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            containerColor = Color(0xFF0C261D),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = IslamicGoldBright, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "أشكال وتصاميم المصحف الشريف",
                        color = IslamicGoldBright,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                LazyColumn(modifier = Modifier.height(360.dp)) {
                    items(MushafPageTheme.values()) { themeOption ->
                        val isSelected = (themeOption == selectedTheme)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedTheme = themeOption
                                    showThemeDialog = false
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = themeOption.backgroundColor,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 2.5.dp else 1.dp,
                                if (isSelected) themeOption.pageBorderColor else themeOption.innerFrameColor.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Visual color circle
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(themeOption.pageBorderColor)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = themeOption.titleArabic,
                                            color = themeOption.textColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                            color = themeOption.bismillahColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "محدد",
                                        tint = themeOption.pageBorderColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("إغلاق", color = Color.White)
                }
            }
        )
    }
}

// ----------------------------------------------------
// SURAHS LIST VIEW SUB-COMPONENTS
// ----------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SurahsListView(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSelectSurah: (Surah) -> Unit,
    onPlaySurah: (Surah) -> Unit,
    onOpenInMushaf: (Surah) -> Unit
) {
    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) QuranDataProvider.surahs
        else QuranDataProvider.surahs.filter {
            it.nameArabic.contains(searchQuery.trim()) ||
            it.nameEnglish.contains(searchQuery.trim(), ignoreCase = true) ||
            it.number.toString() == searchQuery.trim()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("ابحث عن سورة بالاسم أو الرقم...", color = Color.White.copy(alpha = 0.5f)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = IslamicGold) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0x770A2119),
                unfocusedContainerColor = Color(0x550A2119),
                focusedIndicatorColor = IslamicGold,
                unfocusedIndicatorColor = IslamicGold.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredSurahs, key = { it.number }) { surah ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSelectSurah(surah) },
                    color = Color(0x990F2E23),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Surah Number Medallion
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF194D3B),
                                border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGoldBright),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = surah.number.toString(),
                                        fontWeight = FontWeight.Bold,
                                        color = IslamicGoldBright,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "سورة ${surah.nameArabic}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${surah.revelationType.arabicName} • ${surah.versesCount} آية • صفحة ${surah.pageNumber}",
                                    color = IslamicGoldBright.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Open in Mushaf Pages Button
                            IconButton(
                                onClick = { onOpenInMushaf(surah) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1B4E3C))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = "فتح في المصحف الورقي",
                                    tint = IslamicGoldBright,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Play Audio Button
                            IconButton(
                                onClick = { onPlaySurah(surah) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(IslamicGold)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Headphones,
                                    contentDescription = "استماع للتلاوة",
                                    tint = IslamicEmeraldDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SurahVersesListView(
    surah: Surah,
    fontSize: Float,
    onBack: () -> Unit,
    onPlay: () -> Unit,
    onOpenInMushaf: () -> Unit
) {
    val context = LocalContext.current
    val downloadManager = remember { QuranDownloadManager(context) }
    val defaultReciter = remember { RecitersDataProvider.reciters.first() }

    val ayahs = remember(surah.number) {
        QuranDataProvider.getAyahsForSurah(surah.number)
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
        Spacer(modifier = Modifier.height(8.dp))

        // Top Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = IslamicGoldBright
                    )
                }
                Column {
                    Text(
                        text = "سورة ${surah.nameArabic}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldBright
                    )
                    Text(
                        text = "${surah.revelationType.arabicName} • صفحة ${surah.pageNumber} • الجزء ${surah.juzNumber}",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }

            Row {
                IconButton(onClick = onOpenInMushaf) {
                    Icon(Icons.Default.MenuBook, contentDescription = "عرض الصفحة الورقية", tint = IslamicGoldBright)
                }
                IconButton(onClick = onPlay) {
                    Icon(Icons.Default.Headphones, contentDescription = "تشغيل التلاوة في الخلفية", tint = IslamicGoldBright)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Verses List with Tafsir Muyassar and direct Play & Download
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(ayahs, key = { "${it.surahNumber}_${it.ayahNumber}" }) { ayah ->
                AyahItemCard(
                    ayah = ayah,
                    fontSizeSp = fontSize,
                    onPlayAyah = {
                        onPlay()
                        Toast.makeText(
                            context,
                            "بدء تلاوة الآية ﴿${ayah.ayahNumber}﴾ في الخلفية",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onDownloadAyah = {
                        Toast.makeText(
                            context,
                            "جاري تحميل تلاوة سورة ${surah.nameArabic} للاستماع دون إنترنت...",
                            Toast.LENGTH_SHORT
                        ).show()
                        downloadManager.downloadSurahDirect(defaultReciter, surah.number) { success, _ ->
                            if (success) {
                                Toast.makeText(
                                    context,
                                    "✅ تم تحميل سورة ${surah.nameArabic} بنجاح!",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "❌ تعذر تنزيل الآية، يرجى فحص الاتصال بالإنترنت",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )
            }
        }
    }
}
