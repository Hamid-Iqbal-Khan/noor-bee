package com.example.qaida

import android.content.Context
import android.media.MediaPlayer

/** Stage 1 and 2 progress, kept per child profile in SharedPreferences (survives restarts). */
class QaidaProgress(context: Context, profileId: Int) {
    private val prefs = context.applicationContext.getSharedPreferences("qaida_progress_$profileId", Context.MODE_PRIVATE)

    fun seenLetters(): Set<String> = prefs.getStringSet(KEY_SEEN, emptySet()) ?: emptySet()

    fun markLetterSeen(id: String) {
        val now = seenLetters() + id
        prefs.edit().putStringSet(KEY_SEEN, now).apply()
    }

    fun isLessonDone(lesson: String): Boolean = prefs.getBoolean("done_$lesson", false)
    fun markLessonDone(lesson: String) = prefs.edit().putBoolean("done_$lesson", true).apply()

    fun bestQuizScore(stage: Int = 1): Int = prefs.getInt(if (stage == 1) KEY_BEST else "${KEY_BEST}_s$stage", 0)
    fun recordQuizScore(score: Int, stage: Int = 1) {
        if (score > bestQuizScore(stage)) prefs.edit().putInt(if (stage == 1) KEY_BEST else "${KEY_BEST}_s$stage", score).apply()
        if (score >= PASS_MARK) markLessonDone(when (stage) { 1 -> LESSON_QUIZ; 2 -> S2_QUIZ; else -> S3_QUIZ })
    }

    companion object {
        private const val KEY_SEEN = "seen_letters"
        private const val KEY_BEST = "best_quiz"
        const val QUIZ_LENGTH = 10
        const val PASS_MARK = 8 // 80% unlocks Stage 2
        const val LESSON_LETTERS = "1.1"
        const val LESSON_SOUNDS = "1.2"
        const val LESSON_HARAKAT = "1.3"
        const val LESSON_QUIZ = "1.4"
        const val S2_JOINING = "2.1"
        const val S2_MUQATTAAT = "2.2"
        const val S2_TANWEEN = "2.3"
        const val S2_KHARA = "2.4"
        const val S2_MADD = "2.5"
        const val S2_JAZM = "2.6"
        const val S2_QUIZ = "2.7"
        const val S3_TASHDEED = "3.1"
        const val S3_GHUNNA = "3.2"
        const val S3_MADD = "3.3"
        const val S3_SHAMSI = "3.4"
        const val S3_FATIHA = "3.5"
        const val S3_IKHLAS = "3.6"
        const val S3_QUIZ = "3.7"
    }
}

/**
 * Plays the qari's recording from assets/audio/qaida/<key>.mp3 when it exists.
 * Returns false when there is no recording, so the caller can fall back to TTS.
 */
class QaidaAudio(private val context: Context) {
    private var player: MediaPlayer? = null

    fun hasRecording(key: String): Boolean = try {
        context.assets.openFd("audio/qaida/$key.mp3").close(); true
    } catch (_: Exception) { false }

    fun play(key: String): Boolean {
        if (!hasRecording(key)) return false
        return try {
            stop()
            val afd = context.assets.openFd("audio/qaida/$key.mp3")
            player = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                setOnCompletionListener { it.release(); if (player === it) player = null }
                prepare(); start()
            }
            true
        } catch (_: Exception) { false }
    }

    fun stop() { player?.let { runCatching { it.stop(); it.release() } }; player = null }
}
