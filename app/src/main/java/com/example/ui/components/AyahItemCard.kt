package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Ayah
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldBright

@Composable
fun AyahItemCard(
    ayah: Ayah,
    fontSizeSp: Float = 22f,
    onPlayAyah: (() -> Unit)? = null,
    onDownloadAyah: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isTafsirVisible by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        color = Color(0xB30F2A20),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Ayah Number in decorative frame & Quick actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ayah badge ﴿١﴾
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(IslamicEmeraldDark)
                        .border(1.dp, IslamicGold, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "﴿${ayah.ayahNumber}﴾",
                        color = IslamicGoldBright,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Play Ayah in background
                    if (onPlayAyah != null) {
                        IconButton(
                            onClick = onPlayAyah,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "استماع للآية في الخلفية",
                                tint = IslamicGoldBright,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Download Ayah
                    if (onDownloadAyah != null) {
                        IconButton(
                            onClick = onDownloadAyah,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = "تنزيل الآية",
                                tint = IslamicGoldBright,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Copy Ayah
                    IconButton(
                        onClick = {
                            clipboardManager.setText(
                                AnnotatedString("${ayah.textArabic} ﴿${ayah.ayahNumber}﴾")
                            )
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "نسخ الآية",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Tafsir Toggle Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isTafsirVisible) IslamicGold else Color(0x33D4AF37))
                            .border(1.dp, IslamicGold, RoundedCornerShape(12.dp))
                            .clickable { isTafsirVisible = !isTafsirVisible }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (isTafsirVisible) Icons.Filled.AutoStories else Icons.Outlined.AutoStories,
                                contentDescription = null,
                                tint = if (isTafsirVisible) IslamicEmeraldDark else IslamicGoldBright,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "التفسير الميسر",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTafsirVisible) IslamicEmeraldDark else IslamicGoldBright
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quran Ayah Arabic Text
            Text(
                text = ayah.textArabic,
                fontSize = fontSizeSp.sp,
                lineHeight = (fontSizeSp * 1.7f).sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFF9F6EE),
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            // Al-Tafsir Al-Muyassar expanded section
            AnimatedVisibility(visible = isTafsirVisible) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF091913))
                        .border(1.dp, IslamicGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(IslamicGoldBright)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "التفسير الميسر:",
                            color = IslamicGoldBright,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = ayah.tafsirMuyassar,
                        color = Color(0xFFD3E0D8),
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Right
                    )
                }
            }
        }
    }
}
