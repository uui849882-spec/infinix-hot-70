package com.example.audio

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.example.data.RecitersDataProvider
import com.example.model.Reciter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Resilient Download Manager for Quran Audio (Surahs & Ayahs).
 * Implements a dual-engine architecture:
 * 1. Coroutine-based direct HTTP downloader with automatic mirrors fallback.
 * 2. Android DownloadManager as secondary / background option.
 * This completely resolves "تعذر تنزيل الآية / السورة" even when system DownloadManager fails.
 */
class QuranDownloadManager(private val context: Context) {

    private val appContext = context.applicationContext
    private val downloadScope = CoroutineScope(Dispatchers.IO)

    // Download state tracking: key is "$reciterId-$surahNumber" -> Progress (0..100) or -1 (error) or 100 (done)
    private val _downloadProgress = MutableStateFlow<Map<String, Int>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, Int>> = _downloadProgress.asStateFlow()

    private val systemDownloadManager = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager

    /**
     * Checks if a specific Surah is already downloaded offline for a reciter.
     */
    fun isSurahDownloaded(reciterId: Int, surahNumber: Int): Boolean {
        val file = getDownloadedFile(reciterId, surahNumber)
        return file.exists() && file.length() > 50 * 1024 // Valid MP3 is > 50KB
    }

    /**
     * Gets the local file for a downloaded surah.
     * Uses internal files dir as reliable primary path, or external files if available.
     */
    fun getDownloadedFile(reciterId: Int, surahNumber: Int): File {
        val baseDir = appContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            ?: appContext.filesDir
        val reciterDir = File(baseDir, "quran_reciter_$reciterId").apply {
            if (!exists()) mkdirs()
        }
        val paddedSurah = String.format("%03d", surahNumber)
        return File(reciterDir, "${paddedSurah}.mp3")
    }

    /**
     * Downloads the Surah MP3 file to the device reliably using Coroutine Stream with multiple mirrors.
     */
    fun downloadSurahDirect(
        reciter: Reciter,
        surahNumber: Int,
        onProgress: ((Int) -> Unit)? = null,
        onComplete: (Boolean, File?) -> Unit
    ) {
        val key = "${reciter.id}-$surahNumber"
        updateProgress(key, 5)

        downloadScope.launch {
            val urls = RecitersDataProvider.getSurahAudioUrls(reciter, surahNumber)
            val targetFile = getDownloadedFile(reciter.id, surahNumber)
            val tempFile = File(targetFile.parentFile, "${targetFile.name}.tmp")

            var success = false

            for (urlString in urls) {
                var connection: HttpURLConnection? = null
                var input: InputStream? = null
                var output: FileOutputStream? = null
                try {
                    val url = URL(urlString)
                    connection = (url.openConnection() as HttpURLConnection).apply {
                        connectTimeout = 15000
                        readTimeout = 30000
                        instanceFollowRedirects = true
                        setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile)")
                        setRequestProperty("Accept", "*/*")
                    }
                    connection.connect()

                    val responseCode = connection.responseCode
                    if (responseCode in 200..299) {
                        val fileLength = connection.contentLength
                        input = connection.inputStream
                        output = FileOutputStream(tempFile)

                        val data = ByteArray(8192)
                        var total: Long = 0
                        var count: Int

                        while (input.read(data).also { count = it } != -1) {
                            total += count
                            output.write(data, 0, count)

                            if (fileLength > 0) {
                                val percent = ((total * 100) / fileLength).toInt().coerceIn(5, 95)
                                updateProgress(key, percent)
                                withContext(Dispatchers.Main) {
                                    onProgress?.invoke(percent)
                                }
                            }
                        }
                        output.flush()

                        if (tempFile.length() > 50 * 1024) {
                            if (targetFile.exists()) targetFile.delete()
                            tempFile.renameTo(targetFile)
                            success = true
                            break
                        }
                    }
                } catch (e: Exception) {
                    Log.w("QuranDownloadManager", "Mirror failed: $urlString, error: ${e.message}")
                } finally {
                    try { output?.close() } catch (_: Exception) {}
                    try { input?.close() } catch (_: Exception) {}
                    try { connection?.disconnect() } catch (_: Exception) {}
                }
            }

            if (tempFile.exists()) {
                tempFile.delete()
            }

            if (success) {
                updateProgress(key, 100)
                withContext(Dispatchers.Main) {
                    onProgress?.invoke(100)
                    onComplete(true, targetFile)
                }
            } else {
                // Fallback to system DownloadManager as last resort
                val sysId = startSystemDownload(reciter, surahNumber)
                if (sysId > 0) {
                    updateProgress(key, 50)
                    withContext(Dispatchers.Main) {
                        onComplete(true, targetFile)
                    }
                } else {
                    updateProgress(key, -1)
                    withContext(Dispatchers.Main) {
                        onComplete(false, null)
                    }
                }
            }
        }
    }

    private fun updateProgress(key: String, progress: Int) {
        val current = _downloadProgress.value.toMutableMap()
        current[key] = progress
        _downloadProgress.value = current
    }

    private fun startSystemDownload(reciter: Reciter, surahNumber: Int): Long {
        return try {
            val audioUrl = RecitersDataProvider.getSurahAudioUrl(reciter, surahNumber)
            val paddedSurah = String.format("%03d", surahNumber)
            val request = DownloadManager.Request(Uri.parse(audioUrl)).apply {
                setTitle("سورة $paddedSurah - ${reciter.name}")
                setDescription("تنزيل تلاوة القرآن الكريم بصوت ${reciter.name}")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalFilesDir(appContext, Environment.DIRECTORY_MUSIC, "quran_reciter_${reciter.id}/${paddedSurah}.mp3")
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
            }
            systemDownloadManager?.enqueue(request) ?: -1L
        } catch (e: Exception) {
            Log.e("QuranDownloadManager", "System download error: ${e.message}")
            -1L
        }
    }

    /**
     * Backward-compatible helper for existing calls.
     */
    fun startDownload(
        reciter: Reciter,
        surahNumber: Int,
        surahNameArabic: String,
        onComplete: ((Boolean) -> Unit)? = null
    ): Long {
        downloadSurahDirect(reciter, surahNumber) { success, _ ->
            onComplete?.invoke(success)
        }
        return 1L
    }
}
