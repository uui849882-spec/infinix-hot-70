package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.QuranDownloadManager
import com.example.data.QuranDataProvider
import com.example.data.RecitersDataProvider
import com.example.model.Reciter
import com.example.model.Surah
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldBright

@Composable
fun RecitersScreen(
    onPlaySurah: (Reciter, Surah) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val downloadManager = remember { QuranDownloadManager(context) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("الكل") }
    var selectedReciterForSurahs by remember { mutableStateOf<Reciter?>(null) }
    var favoriteIds by remember { mutableStateOf(setOf(1, 2, 3, 4, 7, 8, 10, 11, 12, 13, 14, 16)) }

    val filterOptions = listOf("الكل", "المفضلة", "حفص عن عاصم", "ورش عن نافع", "قالون عن نافع", "الدوري عن أبي عمرو")

    val filteredReciters = remember(searchQuery, selectedFilter, favoriteIds) {
        RecitersDataProvider.reciters.filter { reciter ->
            val matchesSearch = reciter.name.contains(searchQuery.trim(), ignoreCase = true) ||
                    reciter.subName.contains(searchQuery.trim(), ignoreCase = true)

            val matchesCategory = when (selectedFilter) {
                "الكل" -> true
                "المفضلة" -> favoriteIds.contains(reciter.id)
                "حفص عن عاصم" -> reciter.riwayah.contains("حفص")
                "ورش عن نافع" -> reciter.riwayah.contains("ورش")
                "قالون عن نافع" -> reciter.riwayah.contains("قالون")
                "الدوري عن أبي عمرو" -> reciter.riwayah.contains("الدوري")
                else -> true
            }
            matchesSearch && matchesCategory
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header with badge counter (400 Shuyukh)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "القراء الشيوخ",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldBright
                    )
                    Text(
                        text = "٤٠٠ قارئ وشيخ مع الاستماع والتحميل للهاتف بدون نت",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF13362A))
                        .border(1.dp, IslamicGold, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${filteredReciters.size} قارئ",
                        color = IslamicGoldBright,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("ابحث عن أي شيخ من الـ 400 شيخ...", color = Color.White.copy(alpha = 0.5f))
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "بحث", tint = IslamicGold)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "مسح", tint = Color.White)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0x660F2B21),
                    unfocusedContainerColor = Color(0x400F2B21),
                    focusedIndicatorColor = IslamicGold,
                    unfocusedIndicatorColor = IslamicGold.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips Horizontal Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions) { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) IslamicGold else Color(0x550F2B21))
                            .border(
                                1.dp,
                                if (isSelected) IslamicGoldBright else IslamicGold.copy(alpha = 0.35f),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) IslamicEmeraldDark else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Reciters List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredReciters, key = { it.id }) { reciter ->
                    val isFav = favoriteIds.contains(reciter.id)
                    ReciterCardItem(
                        reciter = reciter,
                        isFavorite = isFav,
                        onToggleFavorite = {
                            favoriteIds = if (isFav) favoriteIds - reciter.id else favoriteIds + reciter.id
                        },
                        onOpenSurahPicker = { selectedReciterForSurahs = reciter },
                        onQuickPlayFirstSurah = {
                            val fatihah = QuranDataProvider.surahs.first()
                            onPlaySurah(reciter, fatihah)
                            Toast.makeText(context, "بدء تلاوة سورة الفاتحة للشيخ ${reciter.name}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }

        // Surah Selection & Download Dialog for Sheikh
        selectedReciterForSurahs?.let { sheikh ->
            SurahSelectorModal(
                reciter = sheikh,
                onDismiss = { selectedReciterForSurahs = null },
                onSelectSurahToPlay = { surah ->
                    onPlaySurah(sheikh, surah)
                    selectedReciterForSurahs = null
                },
                onDownloadSurah = { surah ->
                    Toast.makeText(
                        context,
                        "جاري تنزيل سورة ${surah.nameArabic} للشيخ ${sheikh.name}...",
                        Toast.LENGTH_SHORT
                    ).show()
                    downloadManager.downloadSurahDirect(sheikh, surah.number) { success, _ ->
                        if (success) {
                            Toast.makeText(
                                context,
                                "✅ تم تنزيل سورة ${surah.nameArabic} بنجاح وجاهزة للاستماع دون إنترنت!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "❌ تعذر تنزيل السورة، يرجى فحص الاتصال بالإنترنت",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                isDownloaded = { surahNumber ->
                    downloadManager.isSurahDownloaded(sheikh.id, surahNumber)
                }
            )
        }
    }
}

@Composable
private fun ReciterCardItem(
    reciter: Reciter,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onOpenSurahPicker: () -> Unit,
    onQuickPlayFirstSurah: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenSurahPicker() },
        color = Color(0x990F2B21),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sheikh Personal Portrait / Avatar
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(IslamicEmeraldDark)
                        .border(1.5.dp, IslamicGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = reciter.localImageRes ?: R.drawable.ic_reciter_avatar),
                        contentDescription = reciter.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reciter.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (reciter.subName.isNotEmpty()) "${reciter.subName} • ${reciter.riwayah}" else reciter.riwayah,
                        fontSize = 12.sp,
                        color = IslamicGoldBright.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Favorite Button
                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "المفضلة",
                        tint = if (isFavorite) Color(0xFFFF5252) else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Play Button
                IconButton(
                    onClick = onQuickPlayFirstSurah,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(IslamicGold)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "تشغيل السورة",
                        tint = IslamicEmeraldDark,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SurahSelectorModal(
    reciter: Reciter,
    onDismiss: () -> Unit,
    onSelectSurahToPlay: (Surah) -> Unit,
    onDownloadSurah: (Surah) -> Unit,
    isDownloaded: (Int) -> Boolean
) {
    var modalSearch by remember { mutableStateOf("") }
    val surahsList = remember(modalSearch) {
        if (modalSearch.isBlank()) QuranDataProvider.surahs
        else QuranDataProvider.surahs.filter {
            it.nameArabic.contains(modalSearch.trim()) || it.number.toString() == modalSearch.trim()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", color = IslamicGoldBright)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = reciter.localImageRes ?: R.drawable.ic_reciter_avatar),
                    contentDescription = reciter.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, IslamicGold, CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "المصحف بصوت القارئ",
                        fontSize = 12.sp,
                        color = IslamicGold
                    )
                    Text(
                        text = reciter.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = reciter.riwayah,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.height(420.dp)) {
                // Search inside dialog
                OutlinedTextField(
                    value = modalSearch,
                    onValueChange = { modalSearch = it },
                    placeholder = { Text("ابحث عن سورة للاستماع أو التحميل...", fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0x550F2B21),
                        unfocusedContainerColor = Color(0x330F2B21),
                        focusedIndicatorColor = IslamicGold,
                        unfocusedIndicatorColor = IslamicGold.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(surahsList, key = { it.number }) { surah ->
                        val downloaded = isDownloaded(surah.number)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            color = Color(0x66081F17),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, IslamicGold.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSelectSurahToPlay(surah) },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${surah.number}.",
                                        color = IslamicGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "سورة ${surah.nameArabic}",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Download Button
                                    IconButton(
                                        onClick = { onDownloadSurah(surah) },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (downloaded) Icons.Default.DownloadDone else Icons.Default.Download,
                                            contentDescription = if (downloaded) "تم التنزيل" else "تنزيل السورة",
                                            tint = if (downloaded) Color(0xFF66BB6A) else IslamicGoldBright,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Play Button
                                    IconButton(
                                        onClick = { onSelectSurahToPlay(surah) },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.PlayArrow,
                                            contentDescription = "استماع",
                                            tint = IslamicGoldBright,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = Color(0xF20B211A)
    )
}
