package com.example.ui.screens

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.QuranDownloadManager
import com.example.data.RamadanNasheedDataProvider
import com.example.data.RecitersDataProvider
import com.example.model.RamadanNasheed
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldBright

@Composable
fun RamadanNasheedScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val nasheeds = remember { RamadanNasheedDataProvider.nasheeds }
    var currentPlayingId by remember { mutableIntStateOf(-1) }
    var isPlaying by remember { mutableStateOf(false) }
    val downloadManager = remember { QuranDownloadManager(context) }
    val defaultReciter = remember { RecitersDataProvider.reciters.first() }

    val mediaPlayerHolder = remember {
        object {
            var player: MediaPlayer? = null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                mediaPlayerHolder.player?.stop()
                mediaPlayerHolder.player?.release()
                mediaPlayerHolder.player = null
            } catch (_: Exception) {}
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
            color = Color(0xD90E2D20),
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
                    Icon(Icons.Default.WbSunny, contentDescription = null, tint = IslamicGoldBright, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "أناشيد رمضانية وروحانية بدون تكرار",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldBright
                    )
                    Text(
                        text = "أناشيد عذبة نقية مع الاستماع والتنزيل على الهاتف للاستماع أوفلاين",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // List of Nasheeds
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(nasheeds, key = { it.id }) { item ->
                val isCurrent = (item.id == currentPlayingId && isPlaying)
                NasheedCard(
                    nasheed = item,
                    isPlaying = isCurrent,
                    onPlayToggle = {
                        if (isCurrent) {
                            try {
                                mediaPlayerHolder.player?.pause()
                                isPlaying = false
                            } catch (_: Exception) {}
                        } else {
                            try {
                                mediaPlayerHolder.player?.stop()
                                mediaPlayerHolder.player?.release()
                            } catch (_: Exception) {}

                            try {
                                val mp = MediaPlayer().apply {
                                    setWakeMode(context.applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
                                    setAudioAttributes(
                                        AudioAttributes.Builder()
                                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                            .setUsage(AudioAttributes.USAGE_MEDIA)
                                            .build()
                                    )
                                    setDataSource(context.applicationContext, Uri.parse(item.audioUrl))
                                    setOnPreparedListener {
                                        start()
                                        currentPlayingId = item.id
                                        isPlaying = true
                                        Toast.makeText(context, "بدأ تشغيل: ${item.title}", Toast.LENGTH_SHORT).show()
                                    }
                                    setOnCompletionListener {
                                        isPlaying = false
                                    }
                                    setOnErrorListener { _, _, _ ->
                                        isPlaying = false
                                        Toast.makeText(context, "تعذر تشغيل الصوت، تحقق من الإنترنت", Toast.LENGTH_SHORT).show()
                                        true
                                    }
                                    prepareAsync()
                                }
                                mediaPlayerHolder.player = mp
                            } catch (e: Exception) {
                                Toast.makeText(context, "تعذر التشغيل: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onDownload = {
                        Toast.makeText(context, "جاري تحميل نشيد «${item.title}» على الهاتف...", Toast.LENGTH_SHORT).show()
                        downloadManager.downloadSurahDirect(defaultReciter, item.id) { success, _ ->
                            if (success) {
                                Toast.makeText(context, "✅ تم تنزيل «${item.title}» بنجاح على الهاتف!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "❌ تعذر التنزيل، يرجى فحص الاتصال بالإنترنت", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun NasheedCard(
    nasheed: RamadanNasheed,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    onDownload: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isPlaying) Color(0xCC154231) else Color(0xB3102C21),
        border = androidx.compose.foundation.BorderStroke(
            if (isPlaying) 1.8.dp else 1.dp,
            if (isPlaying) IslamicGoldBright else IslamicGold.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isPlaying) IslamicGold else Color(0xFF1B533E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = if (isPlaying) IslamicEmeraldDark else IslamicGoldBright,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = nasheed.title,
                            color = IslamicGoldBright,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${nasheed.singer} • ${nasheed.durationText}",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onPlayToggle,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isPlaying) IslamicGoldBright else Color(0x44D4AF37))
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "إيقاف مؤقت" else "تشغيل",
                            tint = if (isPlaying) IslamicEmeraldDark else IslamicGoldBright,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onDownload,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "تحميل النشيد",
                            tint = IslamicGoldBright,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0x55091D15),
                border = androidx.compose.foundation.BorderStroke(0.6.dp, IslamicGold.copy(alpha = 0.2f))
            ) {
                Text(
                    text = nasheed.lyrics,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}
