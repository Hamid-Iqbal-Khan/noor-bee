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
        if (score >= PASS_MARK) markLessonDone(when (stage) { 1 -> LESSON_QUIZ; 2 -> S2_QUIZ; 3 -> S3_QUIZ; 4 -> S4_QUIZ; 5 -> S5_QUIZ; 6 -> S6_QUIZ; 7 -> S7_QUIZ; 11 -> S11_QUIZ; else -> "$stage.q" })
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
        const val S4_STOP = "4.1"
        const val S4_SIGNS = "4.2"
        const val S4_QALQALAH = "4.3"
        const val S4_KAWTHAR = "4.4"
        const val S4_FALAQ = "4.5"
        const val S4_NAS = "4.6"
        const val S4_QUIZ = "4.7"
        const val S5_WORDS = "5.1"
        const val S5_ENDINGS = "5.2"
        const val S5_FATIHA = "5.3"
        const val S5_SHORT = "5.4"
        const val S5_FALAQ = "5.5"
        const val S5_QUIZ = "5.6"
        const val S6_FIL = "6.1"
        const val S6_MAUN = "6.2"
        const val S6_KAFIRUN = "6.3"
        const val S6_MASAD = "6.4"
        const val S6_CARDS = "6.5"
        const val S6_QUIZ = "6.6"
        const val S7_IZHAR = "7.1"
        const val S7_IDGHAM = "7.2"
        const val S7_IQLAB = "7.3"
        const val S7_IKHFA = "7.4"
        const val S7_MEEM = "7.5"
        const val S7_SPOT = "7.6"
        const val S7_QUIZ = "7.7"
        const val S11_ROOTS = "11.1"
        const val S11_PATTERNS = "11.2"
        const val S11_PAST = "11.3"
        const val S11_PRESENT = "11.4"
        const val S11_COMMANDS = "11.5"
        const val S11_DETECTIVE = "11.6"
        const val S11_QUIZ = "11.7"
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

/** The whole Urdu Qaida course at a glance, used by the parent progress view. */
object QaidaCourse {
    data class Stage(val number: Int, val english: String, val urdu: String, val lessons: List<Pair<String, String>>, val quiz: String)

    private val ENGLISH = mapOf(78 to "An-Naba", 79 to "An-Nazi'at", 80 to "'Abasa", 81 to "At-Takwir", 82 to "Al-Infitar",
        83 to "Al-Mutaffifin", 84 to "Al-Inshiqaq", 85 to "Al-Buruj", 86 to "At-Tariq", 87 to "Al-A'la", 88 to "Al-Ghashiyah",
        89 to "Al-Fajr", 90 to "Al-Balad", 91 to "Ash-Shams", 92 to "Al-Layl", 93 to "Ad-Duha", 94 to "Ash-Sharh", 95 to "At-Tin",
        96 to "Al-'Alaq", 97 to "Al-Qadr", 98 to "Al-Bayyinah", 99 to "Az-Zalzalah", 100 to "Al-'Adiyat", 101 to "Al-Qari'ah",
        102 to "At-Takathur", 104 to "Al-Humazah")

    val stages = listOf(
        Stage(1, "Letters & harakat", "حروف", listOf("1.1" to "29 letters", "1.2" to "Similar sounds", "1.3" to "Zabar, zer, pesh"), "1.4"),
        Stage(2, "Joining & marks", "جوڑ", listOf("2.1" to "Joining letters", "2.2" to "Muqatta'at", "2.3" to "Tanween",
            "2.4" to "Khara zabar / zer, ulta pesh", "2.5" to "Madd & leen", "2.6" to "Jazm"), "2.7"),
        Stage(3, "Tashdeed & first recitation", "تلاوت", listOf("3.1" to "Tashdeed", "3.2" to "Ghunna", "3.3" to "Madd sign",
            "3.4" to "Sun & moon letters", "3.5" to "Al-Fatiha", "3.6" to "Al-Ikhlas"), "3.7"),
        Stage(4, "Stopping & short surahs", "وقف", listOf("4.1" to "How to stop", "4.2" to "Stop signs", "4.3" to "Qalqalah",
            "4.4" to "Al-Kawthar, Al-Asr", "4.5" to "Al-Falaq", "4.6" to "An-Nas"), "4.7"),
        Stage(5, "Meanings", "معنی", listOf("5.1" to "Common words", "5.2" to "My / your / his", "5.3" to "Al-Fatiha word by word",
            "5.4" to "Ikhlas, Kawthar, Asr", "5.5" to "Falaq, Nas"), "5.6"),
        Stage(6, "Short surahs understood", "سورتیں", listOf("6.1" to "Al-Fil, Quraysh", "6.2" to "Al-Ma'un", "6.3" to "Al-Kafirun, An-Nasr",
            "6.4" to "Al-Masad", "6.5" to "Flashcards"), "6.6"),
        Stage(7, "Noon & meem sakin", "تجوید", listOf("7.1" to "Izhar", "7.2" to "Idgham", "7.3" to "Iqlab", "7.4" to "Ikhfa",
            "7.5" to "Meem sakin", "7.6" to "Spot the rule"), "7.7"),
    ) + QaidaJuzAmma.groups.map { g ->
        Stage(g.stage, "Juz 'Amma: ${ENGLISH[g.surahs.first()]} to ${ENGLISH[g.surahs.last()]}", g.tab.substringAfter(": "),
            g.surahs.map { QaidaJuzAmma.lessonKey(it) to (ENGLISH[it] ?: "Surah $it") }, QaidaJuzAmma.quizKey(g.stage))
    } + Stage(11, "Quranic grammar basics", "گرامر", listOf("11.1" to "Roots", "11.2" to "Patterns", "11.3" to "Past tense",
        "11.4" to "Present tense", "11.5" to "Commands & 'not'", "11.6" to "Root detective"), "11.7")



    data class StageStatus(val stage: Stage, val lessonsDone: Int, val bestQuiz: Int, val passed: Boolean) {
        val started get() = lessonsDone > 0 || bestQuiz > 0
    }

    fun status(p: QaidaProgress): List<StageStatus> = stages.map { s ->
        StageStatus(s, s.lessons.count { p.isLessonDone(it.first) }, p.bestQuizScore(s.number), p.isLessonDone(s.quiz))
    }

    /** The first lesson (or quiz) not yet done, for "what's next". */
    fun next(p: QaidaProgress): Pair<Stage, String>? {
        for (s in stages) {
            s.lessons.firstOrNull { !p.isLessonDone(it.first) }?.let { return s to it.second }
            if (!p.isLessonDone(s.quiz)) return s to "Stage ${s.number} quiz"
        }
        return null
    }
}
