package com.example.qaida

import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.net.HttpURLConnection
import java.net.URL

/**
 * Checks every ayah typed into the course (Stages 3–6) letter by letter against the Tanzil
 * "quran-simple-clean" text from api.alquran.cloud. Needs the network, so it runs on CI and is
 * skipped (not failed) when offline.
 */
class QuranReferenceTest {

  private fun fetch(n: Int): List<String>? = try {
    val c = URL("https://api.alquran.cloud/v1/surah/$n/quran-simple-clean").openConnection() as HttpURLConnection
    c.connectTimeout = 10_000; c.readTimeout = 15_000
    val body = c.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    Regex("\"text\":\"((?:[^\"\\\\]|\\\\.)*)\"").findAll(body).map { m ->
      m.groupValues[1].replace(Regex("\\\\u([0-9a-fA-F]{4})")) { it.groupValues[1].toInt(16).toChar().toString() }
    }.toList()
  } catch (_: Exception) { null }

  @Test fun courseAyatMatchReferenceText() {
    val course: Map<Int, List<QaidaStage3.Ayah>> =
      (listOf(1 to QaidaStage3.fatiha, 112 to QaidaStage3.ikhlas) +
        QaidaStage4.surahs.map { it.number to it.ayat } +
        QaidaStage6.surahs.map { s -> s.number to s.ayat.map { it.ayah } } +
        QaidaJuzAmma.parse(java.io.File("src/main/assets/${QaidaJuzAmma.ASSET}").readLines(Charsets.UTF_8).asSequence())
          .map { (n, s) -> n to s.ayat.map { it.ayah } }).toMap()
    val bismillah = QuranSkeleton.of("بسم الله الرحمن الرحيم")
    var checked = 0
    for ((n, ayat) in course) {
      val ref = fetch(n)
      assumeTrue("reference text reachable", ref != null)
      assertEquals("ayah count of surah $n", ayat.size, ref!!.size)
      ayat.forEachIndexed { i, a ->
        var r = QuranSkeleton.of(ref[i])
        if (n != 1 && i == 0 && r.startsWith(bismillah) && r != bismillah) r = r.removePrefix(bismillah)
        assertEquals("surah $n:${i + 1}", r, QuranSkeleton.of(a.text)); checked++
      }
    }
    println("QuranReferenceTest: $checked ayat matched the reference text")
  }
}
