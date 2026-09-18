package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AzkarDataProvider
import com.example.model.AzkarChapter
import com.example.model.AzkarItem
import com.example.notifications.AzkarNotificationManager
import com.example.ui.components.InteractiveDhikrCounter
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldBright

@Composable
fun AzkarScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedChapter by remember { mutableStateOf<AzkarChapter?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedQuickFilter by remember { mutableStateOf("الكل") }

    // Notifications state
    var notificationsEnabled by remember { mutableStateOf(AzkarNotificationManager.isEnabled(context)) }
    var intervalMinutes by remember { mutableIntStateOf(AzkarNotificationManager.getIntervalMinutes(context)) }
    var voiceSpeechEnabled by remember { mutableStateOf(AzkarNotificationManager.isVoiceSpeechEnabled(context)) }
    var notificationMode by remember { mutableStateOf(AzkarNotificationManager.getNotificationMode(context)) }

    // State map to track user's current counts for each AzkarItem in the opened chapter
    val itemCounts = remember { mutableStateMapOf<Int, Int>() }

    val quickFilters = listOf("الكل", "أذكار الصباح", "أذكار المساء", "أذكار النوم", "أذكار الصلاة", "الاستغفار")

    val filteredChapters = remember(searchQuery, selectedQuickFilter) {
        AzkarDataProvider.chapters.filter { chapter ->
            val matchesSearch = chapter.title.contains(searchQuery.trim(), ignoreCase = true) ||
                    chapter.id.toString() == searchQuery.trim()

            val matchesCategory = when (selectedQuickFilter) {
                "الكل" -> true
                "أذكار الصباح" -> chapter.title.contains("الصباح")
                "أذكار المساء" -> chapter.title.contains("المساء")
                "أذكار النوم" -> chapter.title.contains("النوم")
                "أذكار الصلاة" -> chapter.title.contains("الصلاة") || chapter.title.contains("الوضوء") || chapter.title.contains("الأذان")
                "الاستغفار" -> chapter.title.contains("الاستغفار") || chapter.title.contains("التسبيح")
                else -> true
            }

            matchesSearch && matchesCategory
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (selectedChapter == null) {
            // All 132 Chapters List
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
                Spacer(modifier = Modifier.height(16.dp))

                // Title Banner with 132 indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "حصن المسلم والأذكار",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGoldBright
                        )
                        Text(
                            text = "١٣٢ باباً كاملاً من الأدعية المأثورة والسنن النبوية",
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
                            text = "١٣٢ باب",
                            color = IslamicGoldBright,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notifications Banner Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0x990A221A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (notificationsEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = IslamicGoldBright,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "إشعارات الأذكار والصلاة على النبي ﷺ",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (notificationsEnabled) "تصلك تلقائياً كل $intervalMinutes دقيقة" else "التنبيهات متوقفة حالياً",
                                        color = IslamicGoldBright.copy(alpha = 0.8f),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { isChecked ->
                                    notificationsEnabled = isChecked
                                    AzkarNotificationManager.setEnabled(context, isChecked)
                                    val msg = if (isChecked) "تم تفعيل تنبيهات الأذكار بنجاح 🔔" else "تم إيقاف تنبيهات الأذكار"
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = IslamicGoldBright,
                                    checkedTrackColor = Color(0xFF1E5B44),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color(0xFF0D251D)
                                )
                            )
                        }

                        if (notificationsEnabled) {
                            Spacer(modifier = Modifier.height(10.dp))

                            // Test notification button (instant trigger)
                            Button(
                                onClick = {
                                    AzkarNotificationManager.sendAzkarNotification(context, isTest = true)
                                    Toast.makeText(context, "اللهم صلِّ وسلم وبارك على سيدنا محمد ﷺ (تم إرسال الإشعار)", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth().height(38.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4E3C))
                            ) {
                                Text(
                                    text = "🔔 تجربة إشعار فوري: اللهم صلِّ وسلم وبارك على سيدنا محمد",
                                    color = IslamicGoldBright,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Voice Speech Toggle (نطق الذكر والصلاة صوتياً)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF13382B))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.RecordVoiceOver,
                                        contentDescription = null,
                                        tint = if (voiceSpeechEnabled) IslamicGoldBright else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "صوت بشري خاشع عند وصول الإشعار (بصوت الشيخ مشاري العفاسي)",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Switch(
                                    checked = voiceSpeechEnabled,
                                    onCheckedChange = { isChecked ->
                                        voiceSpeechEnabled = isChecked
                                        AzkarNotificationManager.setVoiceSpeechEnabled(context, isChecked)
                                        val msg = if (isChecked) "تم تفعيل الصوت البشري الخاشع للشيخ مشاري العفاسي 🔊" else "تم إيقاف الصوت للإشعارات"
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = IslamicGoldBright,
                                        checkedTrackColor = Color(0xFF1E5B44),
                                        uncheckedThumbColor = Color.Gray,
                                        uncheckedTrackColor = Color(0xFF0D251D)
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Interval selector chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "التكرار:",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )

                                listOf(15 to "١٥ دقيقة", 30 to "٣٠ دقيقة", 60 to "ساعة", 120 to "ساعتين").forEach { (mins, label) ->
                                    val isSelected = intervalMinutes == mins
                                    Surface(
                                        modifier = Modifier.clickable {
                                            intervalMinutes = mins
                                            AzkarNotificationManager.setIntervalMinutes(context, mins)
                                            Toast.makeText(context, "تم ضبط التنبيه كل $label", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) IslamicGold else Color(0xFF143B2D),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) IslamicGoldBright else Color.Transparent)
                                    ) {
                                        Text(
                                            text = label,
                                            color = if (isSelected) IslamicEmeraldDark else Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("ابحث بين ١٣٢ باباً من الأذكار...", color = Color.White.copy(alpha = 0.5f))
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

                // Quick Filters
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickFilters) { filter ->
                        val isSelected = selectedQuickFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) IslamicGold else Color(0x550F2B21))
                                .border(
                                    1.dp,
                                    if (isSelected) IslamicGoldBright else IslamicGold.copy(alpha = 0.35f),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedQuickFilter = filter }
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

                // Chapters LazyColumn
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredChapters, key = { it.id }) { chapter ->
                        ChapterListItem(
                            chapter = chapter,
                            onClick = {
                                selectedChapter = chapter
                                itemCounts.clear()
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(90.dp))
                    }
                }
            }
        } else {
            // Single Chapter Items View
            val chapter = selectedChapter!!
            Column(modifier = Modifier.fillMaxSize()) {
                // Chapter Top Header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xF2091C15),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { selectedChapter = null }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = IslamicGoldBright
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "الباب رقم (${chapter.id})",
                                fontSize = 11.sp,
                                color = IslamicGold
                            )
                            Text(
                                text = chapter.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Azkar Items List with Counters
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    items(chapter.items, key = { it.id }) { item ->
                        val currentCount = itemCounts[item.id] ?: 0
                        AzkarItemView(
                            item = item,
                            currentCount = currentCount,
                            onIncrement = {
                                val current = itemCounts[item.id] ?: 0
                                if (current < item.count) {
                                    itemCounts[item.id] = current + 1
                                }
                            },
                            onReset = {
                                itemCounts[item.id] = 0
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(90.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ChapterListItem(
    chapter: AzkarChapter,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color(0x990F2B21),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chapter Number Badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(IslamicEmeraldDark)
                        .border(1.5.dp, IslamicGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${chapter.id}",
                        color = IslamicGoldBright,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chapter.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${chapter.items.size} أدعية وأذكار",
                        fontSize = 11.sp,
                        color = IslamicGoldBright.copy(alpha = 0.8f)
                    )
                }
            }

            Icon(
                Icons.Default.Mosque,
                contentDescription = null,
                tint = IslamicGold.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun AzkarItemView(
    item: AzkarItem,
    currentCount: Int,
    onIncrement: () -> Unit,
    onReset: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xB30E2A20),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            // Header Row: Count & Copy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(IslamicEmeraldDark)
                        .border(1.dp, IslamicGold, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "التكرار: ${item.count} مرات",
                        color = IslamicGoldBright,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { clipboardManager.setText(AnnotatedString(item.textArabic)) },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "نسخ الذكر",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Azkar Arabic Text
            Text(
                text = item.textArabic,
                fontSize = 19.sp,
                lineHeight = 32.sp,
                color = Color(0xFFF9F6EE),
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            // Virtue or Hadith info if present
            if (item.fadl.isNotEmpty() || item.reference.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0A1C15))
                        .padding(8.dp)
                ) {
                    if (item.fadl.isNotEmpty()) {
                        Text(
                            text = "الفضل: ${item.fadl}",
                            fontSize = 12.sp,
                            color = IslamicGoldBright,
                            textAlign = TextAlign.Right
                        )
                    }
                    if (item.reference.isNotEmpty()) {
                        Text(
                            text = "المصدر: ${item.reference}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Right
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Dhikr Counter
            InteractiveDhikrCounter(
                currentCount = currentCount,
                targetCount = item.count,
                onIncrement = onIncrement,
                onReset = onReset
            )
        }
    }
}
