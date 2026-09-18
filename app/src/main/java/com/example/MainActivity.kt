package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.notifications.AzkarNotificationManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.audio.QuranAudioPlayer
import com.example.audio.QuranDownloadManager
import com.example.data.RecitersDataProvider
import com.example.model.AudioPlayerState
import com.example.model.Reciter
import com.example.model.Surah
import com.example.ui.components.AnimatedIslamicBackground
import com.example.ui.components.MainAppTab
import com.example.ui.components.MiniAudioPlayerBar
import com.example.ui.components.QuranBottomBar
import com.example.ui.screens.AzkarScreen
import com.example.ui.screens.HadithScreen
import com.example.ui.screens.LearnPrayerScreen
import com.example.ui.screens.ProphetsStoriesScreen
import com.example.ui.screens.QiblaScreen
import com.example.ui.screens.QuranScreen
import com.example.ui.screens.RamadanNasheedScreen
import com.example.ui.screens.RecitersScreen
import com.example.ui.screens.SettingsAndDeveloperScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var audioPlayer: QuranAudioPlayer

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            AzkarNotificationManager.schedulePeriodicReminder(applicationContext)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        audioPlayer = QuranAudioPlayer(applicationContext)

        // Initialize and schedule Azkar & Salawat reminders
        AzkarNotificationManager.createNotificationChannel(applicationContext)
        if (AzkarNotificationManager.isEnabled(applicationContext)) {
            AzkarNotificationManager.schedulePeriodicReminder(applicationContext)
        }

        // Request POST_NOTIFICATIONS on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    MainAppContent(audioPlayer = audioPlayer)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.stop()
    }
}

@Composable
fun MainAppContent(audioPlayer: QuranAudioPlayer) {
    val context = LocalContext.current
    val downloadManager = remember { QuranDownloadManager(context) }
    var currentTab by remember { mutableStateOf(MainAppTab.QURAN) }

    val playerState by audioPlayer.playerState.collectAsState()

    val isCurrentDownloaded = remember(playerState.currentReciter, playerState.currentSurah) {
        val reciter = playerState.currentReciter
        val surah = playerState.currentSurah
        if (reciter != null && surah != null) {
            downloadManager.isSurahDownloaded(reciter.id, surah.number)
        } else false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Column {
                // Mini Player visible when a surah is loaded or playing
                if (playerState.currentSurah != null && playerState.currentReciter != null) {
                    MiniAudioPlayerBar(
                        state = playerState,
                        isDownloaded = isCurrentDownloaded,
                        onTogglePlay = { audioPlayer.togglePlayPause() },
                        onNext = { audioPlayer.playNextSurah() },
                        onPrev = { audioPlayer.playPreviousSurah() },
                        onSeek = { targetMs -> audioPlayer.seekTo(targetMs) },
                        onChangeSpeed = { speed -> audioPlayer.setPlaybackSpeed(speed) },
                        onDownload = {
                            val reciter = playerState.currentReciter
                            val surah = playerState.currentSurah
                            if (reciter != null && surah != null) {
                                downloadManager.startDownload(reciter, surah.number, surah.nameArabic)
                                Toast.makeText(
                                    context,
                                    "جاري تنزيل سورة ${surah.nameArabic} بصوت ${reciter.name} على الهاتف!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                }

                // Bottom Tab Navigation Bar
                QuranBottomBar(
                    selectedTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            }
        }
    ) { innerPadding ->
        AnimatedIslamicBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    (fadeIn() + slideInHorizontally { width -> if (targetState.ordinal > initialState.ordinal) width else -width })
                        .togetherWith(fadeOut() + slideOutHorizontally { width -> if (targetState.ordinal > initialState.ordinal) -width else width })
                },
                label = "tabContentTransition"
            ) { tab ->
                when (tab) {
                    MainAppTab.QURAN -> {
                        QuranScreen(
                            onPlaySurah = { surah ->
                                val activeReciter = playerState.currentReciter ?: RecitersDataProvider.reciters.first()
                                audioPlayer.playSurah(activeReciter, surah)
                                Toast.makeText(
                                    context,
                                    "بدء تشغيل سورة ${surah.nameArabic} بصوت ${activeReciter.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            currentPlayingSurah = playerState.currentSurah,
                            isPlayingAudio = playerState.isPlaying,
                            currentPositionMs = playerState.currentPositionMs,
                            durationMs = playerState.durationMs
                        )
                    }
                    MainAppTab.RECITERS -> {
                        RecitersScreen(
                            onPlaySurah = { reciter, surah ->
                                audioPlayer.playSurah(reciter, surah)
                            }
                        )
                    }
                    MainAppTab.LEARN_PRAYER -> {
                        LearnPrayerScreen()
                    }
                    MainAppTab.HADITH -> {
                        HadithScreen()
                    }
                    MainAppTab.PROPHETS -> {
                        ProphetsStoriesScreen()
                    }
                    MainAppTab.NASHEED -> {
                        RamadanNasheedScreen()
                    }
                    MainAppTab.AZKAR -> {
                        AzkarScreen()
                    }
                    MainAppTab.QIBLA -> {
                        QiblaScreen()
                    }
                    MainAppTab.SETTINGS -> {
                        SettingsAndDeveloperScreen()
                    }
                }
            }
        }
    }
}
