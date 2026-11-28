package com.example.progress

import androidx.test.core.app.ApplicationProvider
import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StudyLogTest {
  private val ctx: Context = ApplicationProvider.getApplicationContext()

  @Test fun lessonsOfTheDay_keepHowMuchWasCovered() {
    val log = StudyLog(ctx, 9001)
    val a = LessonTracker(log, "English", "e2.1", "Level 2 · Short a")
    a.open(); a.reach(0.2f); a.heard("cat"); a.heard("hat"); a.reach(0.45f); a.close()
    val b = LessonTracker(log, "Quran (Urdu)", "s2_joining", "Stage 2 · Joining")
    b.open(); b.reach(0.3f); b.done(); b.close()
    val q = LessonTracker(log, "English", "quiz2", "Level 2 practice")
    q.open(); q.position(5, 10); q.done(score = 8); q.close()

    val today = log.day()
    assertEquals(3, today.size)
    val short = today.first { it.key == "e2.1" }
    assertEquals(45, short.percent); assertEquals(2, short.heard)
    assertEquals(100, today.first { it.key == "s2_joining" }.percent)
    assertEquals(8, today.first { it.key == "quiz2" }.score)
    // opening the same lesson again later never lowers what was covered
    LessonTracker(log, "English", "e2.1", "Level 2 · Short a").apply { open(); reach(0.1f); close() }
    assertEquals(45, log.day().first { it.key == "e2.1" }.percent)
    assertEquals(7, log.recent(7).size)
    assertTrue(log.recent(7).first().second.isNotEmpty())
  }

  @Test fun seeingEverything_isNotFinishing() {
    val log = StudyLog(ctx, 9002)
    val t = LessonTracker(log, "Quran (Urdu)", "letters", "Stage 1 · Letters")
    t.expect(29); t.open()
    repeat(29) { t.heard("l$it") }
    assertEquals(95, log.day().first().percent)   // 100 only when the lesson is finished
    t.done(); assertEquals(100, log.day().first().percent)
  }
}
