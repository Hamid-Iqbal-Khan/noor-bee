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
 * Plays a Qaida sound: bundled qari recording → cached or streamed recitation (QuranAudio) → phone voice.
 * Recitation streamed once is saved to the app cache, so it also works offline afterwards.
 */
class QaidaAudio(private val context: Context) {
    private var player: MediaPlayer? = null
    private var queue: List<String> = emptyList()
    private val cacheDir = java.io.File(context.cacheDir, "qaida_audio").apply { mkdirs() }
    private var tts: android.speech.tts.TextToSpeech? = null
    @Volatile private var ttsReady = false

    init {
        // Prefer Google's voice engine when it is installed: its Arabic voice is clearer than most defaults.
        val listener = android.speech.tts.TextToSpeech.OnInitListener { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                val t = tts ?: return@OnInitListener
                val r = t.setLanguage(java.util.Locale("ar"))
                ttsReady = r != android.speech.tts.TextToSpeech.LANG_MISSING_DATA && r != android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED
                t.setSpeechRate(0.75f)
            }
        }
        tts = try {
            val google = "com.google.android.tts"
            val hasGoogle = runCatching { context.packageManager.getPackageInfo(google, 0) }.isSuccess
            if (hasGoogle) android.speech.tts.TextToSpeech(context, listener, google) else android.speech.tts.TextToSpeech(context, listener)
        } catch (_: Exception) { null }
    }

    fun hasRecording(key: String): Boolean = try {
        context.assets.openFd("audio/qaida/$key.mp3").close(); true
    } catch (_: Exception) { false }

    /**
     * Plays [key]. [urls] are recitation sources (from QuranAudio); [speak] is the fallback text for the phone voice.
     * Returns true when real audio (recording or recitation) was started, false when it fell back to the voice.
     */
    fun play(key: String, urls: List<String>?, speak: String, fallback: (String) -> Unit): Boolean {
        stop()
        lastRequest = Request(key, urls, speak)
        if (hasRecording(key)) return playAsset(key)
        if (!urls.isNullOrEmpty()) {
            queue = urls
            playNext { say(speak, fallback) }
            return true
        }
        say(speak, fallback)
        return false
    }

    private fun say(text: String, fallback: (String) -> Unit) {
        val t = tts
        if (t != null && ttsReady) t.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, "qaida") else fallback(text)
    }

    private fun cacheFile(url: String) = java.io.File(cacheDir, url.substringAfter("://").replace('/', '_'))

    private fun playNext(onFail: () -> Unit) {
        val url = queue.firstOrNull() ?: return
        queue = queue.drop(1)
        val cached = cacheFile(url)
        try {
            player = MediaPlayer().apply {
                if (cached.exists() && cached.length() > 0) setDataSource(cached.absolutePath)
                else { setDataSource(url); download(url, cached) }
                setOnPreparedListener { it.start() }
                setOnCompletionListener { mp -> mp.release(); if (player === mp) player = null; playNext(onFail) }
                setOnErrorListener { mp, _, _ -> mp.release(); if (player === mp) player = null; queue = emptyList(); onFail(); true }
                prepareAsync()
            }
        } catch (_: Exception) { queue = emptyList(); onFail() }
    }

    /** Saves the recitation file in the background for offline use next time. */
    private fun download(url: String, to: java.io.File) {
        Thread {
            runCatching {
                val tmp = java.io.File(to.path + ".part")
                java.net.URL(url).openStream().use { input -> tmp.outputStream().use { input.copyTo(it) } }
                if (tmp.length() > 0) tmp.renameTo(to)
            }
        }.apply { isDaemon = true }.start()
    }

    private fun playAsset(key: String): Boolean = try {
        val afd = context.assets.openFd("audio/qaida/$key.mp3")
        player = MediaPlayer().apply {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            setOnCompletionListener { it.release(); if (player === it) player = null }
            prepare(); start()
        }
        true
    } catch (_: Exception) { false }

    fun stop() {
        queue = emptyList()
        player?.let { runCatching { it.stop(); it.release() } }; player = null
        tts?.stop()
    }

    fun release() { stop(); tts?.shutdown(); tts = null }

    data class Request(val key: String, val urls: List<String>?, val speak: String)
    companion object {
        /** The last sound asked for — lets tests check which source a tap would use without playing it. */
        @Volatile var lastRequest: Request? = null
    }
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
