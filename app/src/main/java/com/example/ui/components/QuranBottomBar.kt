package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CompassCalibration
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SupervisorAccount
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldBright

enum class MainAppTab(
    val titleArabic: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    QURAN("المصحف", Icons.Filled.AutoStories, Icons.Outlined.AutoStories),
    RECITERS("القراء", Icons.Filled.Headphones, Icons.Outlined.Headphones),
    LEARN_PRAYER("تعلّم الصلاة", Icons.Filled.School, Icons.Outlined.School),
    HADITH("الأحاديث", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
    PROPHETS("قصص الأنبياء", Icons.Filled.SupervisorAccount, Icons.Outlined.SupervisorAccount),
    NASHEED("أناشيد رمضان", Icons.Filled.MusicNote, Icons.Outlined.MusicNote),
    AZKAR("الأذكار", Icons.Filled.Mosque, Icons.Outlined.Mosque),
    QIBLA("القبلة", Icons.Filled.CompassCalibration, Icons.Outlined.CompassCalibration),
    SETTINGS("الإعدادات", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun QuranBottomBar(
    selectedTab: MainAppTab,
    onTabSelected: (MainAppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color(0xF2091C15),
        shadowElevation = 16.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    IslamicGold.copy(alpha = 0.4f),
                    Color.Transparent
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainAppTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                val iconScale by animateFloatAsState(targetValue = if (isSelected) 1.15f else 1f, label = "tabScale")
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) IslamicGoldBright else Color.White.copy(alpha = 0.65f),
                    label = "tabTextColor"
                )

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(tab) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .scale(iconScale)
                            .then(
                                if (isSelected) Modifier
                                    .clip(CircleShape)
                                    .background(IslamicGold.copy(alpha = 0.22f))
                                    .border(1.dp, IslamicGold.copy(alpha = 0.6f), CircleShape)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.titleArabic,
                            tint = if (isSelected) IslamicGoldBright else Color.White.copy(alpha = 0.65f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = tab.titleArabic,
                        color = textColor,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
