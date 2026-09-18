package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HadithDataProvider
import com.example.model.Hadith
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldBright

@Composable
fun HadithScreen(
    modifier: Modifier = Modifier
) {
    val allHadiths = remember { HadithDataProvider.hadiths }
    var selectedChapter by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val filteredHadiths = remember(selectedChapter, searchQuery) {
        allHadiths.filter { hadith ->
            val matchesChapter = (selectedChapter == "الكل" || hadith.chapter == selectedChapter)
            val matchesQuery = searchQuery.isBlank() ||
                    hadith.textArabic.contains(searchQuery, ignoreCase = true) ||
                    hadith.narrator.contains(searchQuery, ignoreCase = true) ||
                    hadith.explanation.contains(searchQuery, ignoreCase = true)
            matchesChapter && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xD90E2C20),
            border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B533E))
                        .border(1.5.dp, IslamicGoldBright, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = IslamicGoldBright, modifier = Modifier.size(26.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "موسوعة الأحاديث النبوية الشريفة",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldBright
                    )
                    Text(
                        text = "صحيح الأحاديث مع الراوي والتخريج والشرح الميسر",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("بحث في نصوص الأحاديث، الرواة، والشرح...", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = IslamicGold) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0x66113628),
                unfocusedContainerColor = Color(0x440C241B),
                focusedIndicatorColor = IslamicGold,
                unfocusedIndicatorColor = IslamicGold.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Chapter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(HadithDataProvider.chapters) { chapter ->
                val isSelected = (chapter == selectedChapter)
                Surface(
                    modifier = Modifier.clickable { selectedChapter = chapter },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) IslamicGold else Color(0x331B4E3B),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) IslamicGoldBright else IslamicGold.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = chapter,
                        color = if (isSelected) IslamicEmeraldDark else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hadiths List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredHadiths, key = { it.id }) { hadith ->
                HadithCard(
                    hadith = hadith,
                    onCopy = {
                        clipboardManager.setText(
                            AnnotatedString("قال رسول الله ﷺ: «${hadith.textArabic}»\n[الراوي: ${hadith.narrator} - المصدر: ${hadith.source}]")
                        )
                    },
                    onShare = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "حديث شريف:\n«${hadith.textArabic}»\n\nالراوي: ${hadith.narrator}\nالمصدر: ${hadith.source}\nالشرح: ${hadith.explanation}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "مشاركة الحديث الشريف"))
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun HadithCard(
    hadith: Hadith,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xB30F2F23),
        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Card Header: Chapter badge + Copy & Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E5641)
                ) {
                    Text(
                        text = hadith.chapter,
                        color = IslamicGoldBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row {
                    IconButton(onClick = onCopy, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "نسخ الحديث", tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "مشاركة", tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Narrator
            Text(
                text = "عن ${hadith.narrator}:",
                color = IslamicGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Hadith Text
            Text(
                text = "« ${hadith.textArabic} »",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Source
            Text(
                text = "المصدر: ${hadith.source}",
                color = IslamicGoldBright.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Explanation
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0x66081A14),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, IslamicGold.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "الشرح المستفاد:", color = IslamicGoldBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = hadith.explanation, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp, lineHeight = 18.sp)
                }
            }
        }
    }
}
