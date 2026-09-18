package com.example.audio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import com.example.model.AudioPlayerState
import com.example.model.Reciter
import com.example.model.Surah
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * High-level client wrapper that binds to QuranAudioService, ensuring audio playback
 * seamlessly operates in the background without interruptions.
 */
class QuranAudioPlayer(private val context: Context) {

    private var audioService: QuranAudioService? = null
    private var isBound = false

    private val _playerState = MutableStateFlow(AudioPlayerState())
    val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as QuranAudioService.LocalBinder
            val svc = binder.getService()
            audioService = svc
            isBound = true

            // Forward state updates to client
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                svc.playerState.collect { state ->
                    _playerState.value = state
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioService = null
            isBound = false
        }
    }

    init {
        startAndBindService()
    }

    private fun startAndBindService() {
        val intent = Intent(context, QuranAudioService::class.java)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } catch (_: Exception) {
            context.startService(intent)
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun playSurah(reciter: Reciter, surah: Surah) {
        if (audioService != null) {
            audioService?.playSurah(reciter, surah)
        } else {
            startAndBindService()
            // retry after short tick
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                audioService?.playSurah(reciter, surah)
            }, 300)
        }
    }

    fun togglePlayPause() {
        audioService?.togglePlayPause()
    }

    fun seekTo(positionMs: Int) {
        audioService?.seekTo(positionMs)
    }

    fun setPlaybackSpeed(speed: Float) {
        audioService?.setPlaybackSpeed(speed)
    }

    fun playNextSurah() {
        audioService?.playNextSurah()
    }

    fun playPreviousSurah() {
        audioService?.playPreviousSurah()
    }

    fun stop() {
        audioService?.stop()
    }

    fun release() {
        if (isBound) {
            try {
                context.unbindService(connection)
            } catch (_: Exception) {}
            isBound = false
        }
    }
}
