package com.example.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.QuranDataProvider
import com.example.data.RecitersDataProvider
import com.example.model.AudioPlayerState
import com.example.model.Reciter
import com.example.model.Surah
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Foreground Service for robust, non-stop Holy Quran background recitation.
 * Keeps playing smoothly when switching apps, locking the phone, or turning off the screen.
 */
class QuranAudioService : Service() {

    inner class LocalBinder : Binder() {
        fun getService(): QuranAudioService = this@QuranAudioService
    }

    private val binder = LocalBinder()
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var downloadManager: QuranDownloadManager

    private val _playerState = MutableStateFlow(AudioPlayerState())
    val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressTrackerJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        const val CHANNEL_ID = "quran_audio_playback_channel"
        const val NOTIFICATION_ID = 9991

        const val ACTION_PLAY_PAUSE = "com.example.audio.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.audio.ACTION_NEXT"
        const val ACTION_PREV = "com.example.audio.ACTION_PREV"
        const val ACTION_STOP = "com.example.audio.ACTION_STOP"
    }

    override fun onCreate() {
        super.onCreate()
        downloadManager = QuranDownloadManager(applicationContext)

        val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
        wakeLock = powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "QuranAudio::RecitationWakeLock")

        createNotificationChannel()

        // Defaults
        val defaultReciter = RecitersDataProvider.reciters.firstOrNull()
        val defaultSurah = QuranDataProvider.surahs.firstOrNull()
        _playerState.value = _playerState.value.copy(
            currentReciter = defaultReciter,
            currentSurah = defaultSurah
        )
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> togglePlayPause()
            ACTION_NEXT -> playNextSurah()
            ACTION_PREV -> playPreviousSurah()
            ACTION_STOP -> {
                stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "تشغيل القرآن الكريم في الخلفية",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "مشغل صوتيات القرآن الكريم في الخلفية بدون انقطاع"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val surah = _playerState.value.currentSurah
        val reciter = _playerState.value.currentReciter
        val isPlaying = _playerState.value.isPlaying

        val title = if (surah != null) "سورة ${surah.nameArabic} - تلاوة مباركة" else "القرآن الكريم"
        val subtitle = reciter?.name ?: "القارئ"

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action Intents
        val playPauseIntent = Intent(this, QuranAudioService::class.java).apply { action = ACTION_PLAY_PAUSE }
        val playPausePending = PendingIntent.getService(
            this, 1, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = Intent(this, QuranAudioService::class.java).apply { action = ACTION_NEXT }
        val nextPending = PendingIntent.getService(
            this, 2, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = Intent(this, QuranAudioService::class.java).apply { action = ACTION_PREV }
        val prevPending = PendingIntent.getService(
            this, 3, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, QuranAudioService::class.java).apply { action = ACTION_STOP }
        val stopPending = PendingIntent.getService(
            this, 4, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIcon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        val playPauseText = if (isPlaying) "إيقاف مؤقت" else "تشغيل"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(openPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(isPlaying)
            .addAction(android.R.drawable.ic_media_previous, "السابق", prevPending)
            .addAction(playPauseIcon, playPauseText, playPausePending)
            .addAction(android.R.drawable.ic_media_next, "التالي", nextPending)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "إغلاق", stopPending)
            .build()
    }

    private fun updateNotification() {
        if (_playerState.value.isPlaying) {
            startForeground(NOTIFICATION_ID, buildNotification())
        } else {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(NOTIFICATION_ID, buildNotification())
        }
    }

    fun playSurah(reciter: Reciter, surah: Surah) {
        stop()

        _playerState.value = _playerState.value.copy(
            currentReciter = reciter,
            currentSurah = surah,
            isBuffering = true,
            isPlaying = false,
            errorMessage = null,
            currentPositionMs = 0,
            durationMs = 0
        )

        wakeLock?.acquire(3 * 60 * 60 * 1000L) // 3 hours max wake lock
        updateNotification()

        if (downloadManager.isSurahDownloaded(reciter.id, surah.number)) {
            val localFile = downloadManager.getDownloadedFile(reciter.id, surah.number)
            playFromUri(Uri.fromFile(localFile), null)
        } else {
            val urls = RecitersDataProvider.getSurahAudioUrls(reciter, surah.number)
            playFromUrlList(urls, 0)
        }
    }

    private fun playFromUrlList(urls: List<String>, index: Int) {
        if (index >= urls.size) {
            _playerState.value = _playerState.value.copy(
                isBuffering = false,
                isPlaying = false,
                errorMessage = "تعذر تشغيل تلاوة السورة بعد محاولة جميع الخوادم. يرجى التحقق من اتصال الإنترنت."
            )
            updateNotification()
            return
        }

        val streamUrl = urls[index]
        playFromUri(Uri.parse(streamUrl)) {
            // On failure, attempt next mirror
            playFromUrlList(urls, index + 1)
        }
    }

    private fun playFromUri(uri: Uri, onFallback: (() -> Unit)? = null) {
        try {
            mediaPlayer?.let {
                try {
                    it.reset()
                    it.release()
                } catch (ignored: Exception) {}
            }
            mediaPlayer = null

            val headers = mapOf(
                "User-Agent" to "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36",
                "Accept" to "*/*"
            )

            val player = MediaPlayer().apply {
                setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                if (uri.scheme == "file") {
                    setDataSource(applicationContext, uri)
                } else {
                    setDataSource(applicationContext, uri, headers)
                }

                setOnPreparedListener { mp ->
                    _playerState.value = _playerState.value.copy(
                        isBuffering = false,
                        isPlaying = true,
                        durationMs = mp.duration,
                        errorMessage = null
                    )
                    applyPlaybackSpeed(_playerState.value.playbackSpeed)
                    mp.start()
                    startProgressTracker()
                    updateNotification()
                }

                setOnCompletionListener {
                    _playerState.value = _playerState.value.copy(
                        isPlaying = false,
                        currentPositionMs = _playerState.value.durationMs
                    )
                    playNextSurah()
                }

                setOnErrorListener { _, what, extra ->
                    if (onFallback != null) {
                        onFallback.invoke()
                    } else {
                        _playerState.value = _playerState.value.copy(
                            isBuffering = false,
                            isPlaying = false,
                            errorMessage = "تعذر تشغيل الصوت (رمز: $what)"
                        )
                        updateNotification()
                    }
                    true
                }

                setOnBufferingUpdateListener { _, percent ->
                    if (percent < 100 && !_playerState.value.isPlaying) {
                        _playerState.value = _playerState.value.copy(isBuffering = true)
                    } else {
                        _playerState.value = _playerState.value.copy(isBuffering = false)
                    }
                }

                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            if (onFallback != null) {
                onFallback.invoke()
            } else {
                _playerState.value = _playerState.value.copy(
                    isBuffering = false,
                    isPlaying = false,
                    errorMessage = "خطأ في تشغيل السورة: ${e.localizedMessage}"
                )
                updateNotification()
            }
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer
        if (mp != null) {
            if (mp.isPlaying) {
                mp.pause()
                _playerState.value = _playerState.value.copy(isPlaying = false)
            } else {
                mp.start()
                _playerState.value = _playerState.value.copy(isPlaying = true)
                startProgressTracker()
            }
            updateNotification()
        } else {
            val reciter = _playerState.value.currentReciter ?: RecitersDataProvider.reciters.first()
            val surah = _playerState.value.currentSurah ?: QuranDataProvider.surahs.first()
            playSurah(reciter, surah)
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.let { mp ->
            mp.seekTo(positionMs)
            _playerState.value = _playerState.value.copy(currentPositionMs = positionMs)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _playerState.value = _playerState.value.copy(playbackSpeed = speed)
        applyPlaybackSpeed(speed)
    }

    private fun applyPlaybackSpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mediaPlayer?.let { mp ->
                try {
                    val params = mp.playbackParams
                    params.speed = speed
                    mp.playbackParams = params
                } catch (_: Exception) {}
            }
        }
    }

    fun playNextSurah() {
        val current = _playerState.value.currentSurah ?: return
        val nextNumber = if (current.number < 114) current.number + 1 else 1
        val nextSurah = QuranDataProvider.getSurahByNumber(nextNumber) ?: return
        val reciter = _playerState.value.currentReciter ?: RecitersDataProvider.reciters.first()
        playSurah(reciter, nextSurah)
    }

    fun playPreviousSurah() {
        val current = _playerState.value.currentSurah ?: return
        val prevNumber = if (current.number > 1) current.number - 1 else 114
        val prevSurah = QuranDataProvider.getSurahByNumber(prevNumber) ?: return
        val reciter = _playerState.value.currentReciter ?: RecitersDataProvider.reciters.first()
        playSurah(reciter, prevSurah)
    }

    private fun startProgressTracker() {
        progressTrackerJob?.cancel()
        progressTrackerJob = scope.launch {
            while (isActive) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        _playerState.value = _playerState.value.copy(
                            currentPositionMs = mp.currentPosition,
                            durationMs = if (mp.duration > 0) mp.duration else _playerState.value.durationMs
                        )
                    }
                }
                delay(400)
            }
        }
    }

    fun stop() {
        progressTrackerJob?.cancel()
        mediaPlayer?.let { mp ->
            try {
                if (mp.isPlaying) mp.stop()
                mp.release()
            } catch (_: Exception) {}
        }
        mediaPlayer = null
        _playerState.value = _playerState.value.copy(isPlaying = false, isBuffering = false)
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }
}
