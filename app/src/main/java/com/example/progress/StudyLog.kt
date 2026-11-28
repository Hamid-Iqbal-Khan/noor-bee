package com.example.progress

import android.content.Context
import android.os.SystemClock
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** What a child did with one lesson on one day. */
data class LessonDay(
    val course: String,      // "English", "Quran (Urdu)", "Quran (English)", "Arabic words"
    val key: String,         // lesson id inside the course
    val title: String,
    val percent: Int,        // how much of the lesson was covered, 100 = finished
    val heard: Int,          // items tapped and listened to
    val seconds: Int,        // time spent with the lesson open
    val score: Int?,         // practice score out of 10, if it was a practice
    val lastAt: Long,
)

/**
 * Day-by-day record of the lessons a child opened, how far they got and for how long.
 * Kept per profile on the phone (SharedPreferences, one JSON object per day, 90 days).
 */
class StudyLog(context: Context, profileId: Int) {
    private val prefs = context.applicationContext.getSharedPreferences("study_log_$profileId", Context.MODE_PRIVATE)

    fun day(date: String = today()): List<LessonDay> {
        val o = runCatching { JSONObject(prefs.getString("d_$date", "{}")!!) }.getOrDefault(JSONObject())
        return o.keys().asSequence().map { k ->
            val e = o.getJSONObject(k)
            LessonDay(e.getString("course"), e.getString("key"), e.getString("title"), e.getInt("percent"), e.optInt("heard"),
                e.optInt("seconds"), if (e.has("score")) e.getInt("score") else null, e.optLong("lastAt"))
        }.sortedByDescending { it.lastAt }.toList()
    }

    /** The last [n] days, newest first (days with nothing studied are included, as empty lists). */
    fun recent(n: Int = 7): List<Pair<String, List<LessonDay>>> {
        val cal = Calendar.getInstance()
        return (0 until n).map { i ->
            val d = fmt().format(cal.time); cal.add(Calendar.DAY_OF_YEAR, -1); d to day(d)
        }
    }

    /** Merge new progress into today's entry for this lesson (percent and score only ever go up). */
    fun update(course: String, key: String, title: String, percent: Int? = null, heard: Int? = null, addSeconds: Int = 0, score: Int? = null) {
        val date = today()
        val o = runCatching { JSONObject(prefs.getString("d_$date", "{}")!!) }.getOrDefault(JSONObject())
        val id = "$course|$key"
        val e = o.optJSONObject(id) ?: JSONObject().put("course", course).put("key", key).put("percent", 0).put("heard", 0).put("seconds", 0)
        e.put("title", title)
        if (percent != null) e.put("percent", maxOf(e.optInt("percent"), percent.coerceIn(0, 100)))
        if (heard != null) e.put("heard", maxOf(e.optInt("heard"), heard))
        if (addSeconds > 0) e.put("seconds", e.optInt("seconds") + addSeconds)
        if (score != null) e.put("score", maxOf(e.optInt("score", -1), score))
        e.put("lastAt", System.currentTimeMillis())
        o.put(id, e)
        prefs.edit().putString("d_$date", o.toString()).apply()
        prune()
    }

    private fun prune() {
        val cutoff = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -90) }.time.let { fmt().format(it) }
        val old = prefs.all.keys.filter { it.startsWith("d_") && it.removePrefix("d_") < cutoff }
        if (old.isNotEmpty()) prefs.edit().apply { old.forEach { remove(it) } }.apply()
    }

    companion object {
        private fun fmt() = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        fun today(): String = fmt().format(Date())
    }
}

/**
 * Follows one open lesson: how far down the child scrolled, how many items they listened to,
 * how long it was open, and whether they finished. Writes to [StudyLog] as it goes.
 */
class LessonTracker(private val log: StudyLog, val course: String, val key: String, val title: String) {
    private var reach = 0f
    private val heardItems = mutableSetOf<String>()
    private var expected = 0
    private var finished = false
    private var openedAt = 0L

    /** Lessons without a scrolling list say how many items they have (29 letters, 12 cards…). */
    fun expect(items: Int) { expected = items }

    fun reach(fraction: Float) {
        if (fraction > reach + 0.01f) { reach = fraction.coerceIn(0f, 1f); save() }
    }

    fun heard(item: String) { if (heardItems.add(item)) save() }

    /** For card decks and practice: [index] of [total] done. */
    fun position(index: Int, total: Int) { if (total > 0) reach(index / total.toFloat()) }

    fun done(score: Int? = null) { finished = true; log.update(course, key, title, percent = 100, heard = heardItems.size, score = score) }

    fun percent(): Int = if (finished) 100 else {
        // when the lesson says how many items it has, listening to them counts most (scrolling past is not studying);
        // seeing or hearing everything is still not finishing: 100 only with the Done button or a practice
        val covered = if (expected > 0) 0.3f * reach + 0.7f * (heardItems.size / expected.toFloat()).coerceAtMost(1f) else reach
        (covered * 100).toInt().coerceAtMost(95)
    }

    private fun save() = log.update(course, key, title, percent = percent(), heard = heardItems.size)

    fun open() { openedAt = SystemClock.elapsedRealtime(); log.update(course, key, title, percent = percent()) }

    fun close() {
        if (openedAt == 0L) return
        // a lesson left open on the table counts for at most 30 minutes
        val secs = ((SystemClock.elapsedRealtime() - openedAt) / 1000).toInt().coerceIn(0, 30 * 60)
        openedAt = 0L
        log.update(course, key, title, addSeconds = secs, heard = heardItems.size)
    }
}

val LocalLessonTracker = staticCompositionLocalOf<LessonTracker?> { null }

/** Wraps an open lesson so everything inside it can report progress. */
@Composable
fun TrackLesson(profileId: Int, course: String, key: String, title: String, content: @Composable () -> Unit) {
    val ctx = LocalContext.current
    val tracker = remember(profileId, course, key) { LessonTracker(StudyLog(ctx, profileId), course, key, title) }
    DisposableEffect(tracker) { tracker.open(); onDispose { tracker.close() } }
    CompositionLocalProvider(LocalLessonTracker provides tracker) { content() }
}

/** A list state that tells the open lesson how far down the child has scrolled. */
@Composable
fun trackedListState(): LazyListState {
    val st = rememberLazyListState()
    val tracker = LocalLessonTracker.current
    if (tracker != null) LaunchedEffect(st, tracker) {
        snapshotFlow {
            val info = st.layoutInfo
            val last = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            if (info.totalItemsCount == 0) 0f else (last + 1) / info.totalItemsCount.toFloat()
        }.collect { tracker.reach(it) }
    }
    return st
}
