package com.example.qaida

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import kotlin.random.Random

class QaidaDataTest {

  @Test fun qaidaHas29UniqueLettersInQaidaOrder() {
    val l = QaidaData.letters
    assertEquals(29, l.size)
    assertEquals("ids unique", 29, l.map { it.id }.toSet().size)
    assertEquals("glyphs unique", 29, l.map { it.arabic }.toSet().size)
    assertEquals("ابتثجحخدذرزسشصضطظعغفقكلمنوهءي", l.joinToString("") { it.arabic })
  }

  @Test fun heavyLettersAreTheSeven_khussa_daghtin_qiz() {
    // خُصَّ ضَغْطٍ قِظْ
    assertEquals(setOf("خ", "ص", "ض", "غ", "ط", "ق", "ظ"), QaidaData.letters.filter { it.heavy }.map { it.arabic }.toSet())
  }

  @Test fun everyLetterHasUrduNameAndMakhraj() {
    QaidaData.letters.forEach {
      assertTrue("${it.id} name", it.qaidaName.isNotBlank())
      assertTrue("${it.id} makhraj", it.makhraj.length > 10)
    }
  }

  @Test fun soundGroupsCoverTheMergedLettersWithExamples() {
    val groups = QaidaData.soundGroups
    assertEquals(6, groups.size)
    groups.forEach { g ->
      assertTrue(g.letterIds.size >= 2)
      g.letterIds.forEach { id ->
        QaidaData.letter(id) // throws if unknown
        val ex = g.examples[id]
        assertTrue("example for $id", ex != null && ex.arabic.isNotBlank() && ex.urduMeaning.isNotBlank())
        // the example word must contain the letter it illustrates (hamza may sit on alif)
        val glyph = QaidaData.letter(id).arabic
        assertTrue("example '${ex!!.arabic}' contains $glyph", ex.arabic.contains(glyph) || (id == "hamza" && ex.arabic.contains("أ")))
      }
    }
  }

  @Test fun harakatFormsAreCorrect() {
    val ba = QaidaData.letter("ba")
    assertEquals("بَ", QaidaData.withHarakah(ba, QaidaData.Harakah.ZABAR))
    assertEquals("بِ", QaidaData.withHarakah(ba, QaidaData.Harakah.ZER))
    assertEquals("بُ", QaidaData.withHarakah(ba, QaidaData.Harakah.PESH))
    assertEquals("إِ", QaidaData.withHarakah(QaidaData.letter("hamza"), QaidaData.Harakah.ZER))
    assertEquals(28, QaidaData.harakatLetters().size)
    assertEquals("بَا زَبَر بَ", QaidaData.hijje(ba, QaidaData.Harakah.ZABAR))
  }

  @Test fun quizQuestionsAreWellFormedForManySeeds() {
    repeat(200) { seed ->
      val qs = QaidaQuiz.generate(Random(seed))
      assertEquals(10, qs.size)
      qs.forEach { q ->
        assertTrue("index in range", q.correctIndex in q.options.indices)
        assertEquals("options distinct: ${q.options}", q.options.size, q.options.toSet().size)
        assertTrue(q.options.size >= 3)
      }
      // name -> letter questions: the correct glyph's name must be the prompt
      qs.filter { !it.promptIsArabic }.forEach { q ->
        assertEquals(q.prompt, QaidaData.letters.first { it.arabic == q.options[q.correctIndex] }.qaidaName)
      }
    }
  }

  /** Writes the qari's recording script (one row per audio clip) next to the CI screenshots. */
  @Test fun writeRecordingScript() {
    val rows = mutableListOf("audio_key,arabic,what_to_record_urdu")
    QaidaData.letters.forEach { rows += "${it.audioKey},${it.arabic},حرف کا نام: ${it.qaidaName}" }
    QaidaData.soundGroups.forEach { g -> g.examples.forEach { (id, ex) -> rows += "${QaidaData.exampleAudioKey(id)},${ex.arabic},لفظ: ${ex.urduMeaning}" } }
    QaidaData.harakatLetters().forEach { l ->
      QaidaData.Harakah.values().forEach { h -> rows += "${QaidaData.audioKey(l, h)},${QaidaData.withHarakah(l, h)},ہجے: ${QaidaData.hijje(l, h)}" }
    }
    val s1 = rows.size
    // Stage 2
    QaidaStage2.joinedWords.forEach { rows += "${it.audioKey},${it.word},لفظ: ${it.urduMeaning}" }
    QaidaStage2.muqattaat.forEach { rows += "${it.audioKey},${it.text},مقطعات: ${it.reading}" }
    QaidaStage2.Mark.values().forEach { m ->
      QaidaStage2.boardLetters(m).forEach { l -> rows += "${QaidaStage2.audioKey(l, m)},${QaidaStage2.write(l, m)},ہجے: ${QaidaStage2.hijje(l, m)}" }
    }
    (QaidaStage2.kharaWords + QaidaStage2.madd + QaidaStage2.leen + QaidaStage2.jazmWords)
      .forEach { rows += "${it.audioKey},${it.written},${it.urdu.replace(',', '،')}" }
    // Stage 3
    QaidaStage3.tashdeedLetters().forEach { l -> QaidaStage3.Vowel.values().forEach { v ->
      rows += "${QaidaStage3.tashdeedKey(l, v)},${QaidaStage3.tashdeed(l, v)},ہجے: ${QaidaStage3.tashdeedHijje(l, v).replace(',', '،')}" } }
    (QaidaStage3.ghunnaWords + QaidaStage3.tashdeedWords + QaidaStage3.maddSignWords + QaidaStage3.shamsiWords + QaidaStage3.qamariWords)
      .forEach { rows += "${it.audioKey},${it.written},لفظ: ${it.urdu.replace(',', '،')}" }
    (QaidaStage3.fatiha + QaidaStage3.ikhlas).forEach { rows += "${it.audioKey},${it.text},آیت ${it.surah}:${it.number} — ترتیل سے" }
    val keys = rows.drop(1).map { it.substringBefore(',') }
    assertEquals("audio keys unique", keys.size, keys.toSet().size)
    val out = File("build/outputs/roborazzi/qaida-recording-script.csv")
    out.parentFile.mkdirs(); out.writeText(rows.joinToString("\n") + "\n")
    assertEquals(1 + 29 + 15 + 84, s1)
    println("Qaida recording script: ${rows.size - 1} clips")
  }

  @Test fun stage2_content() {
    assertEquals(14, QaidaStage2.muqattaat.size)
    assertEquals(setOf("alif", "dal", "dhal", "ra", "zay", "waw"), QaidaStage2.nonConnectorIds)
    QaidaStage2.nonConnectorIds.forEach { assertTrue(QaidaData.letters.any { l -> l.id == it }) }
    QaidaStage2.joinedWords.forEach { w ->
      assertEquals(w.word, w.letterIds.joinToString("") { QaidaData.letter(it).arabic })
    }
    val b = QaidaData.letter("ba")
    assertEquals("بًا", QaidaStage2.write(b, QaidaStage2.Mark.DO_ZABAR))
    assertEquals("ب\u0670", QaidaStage2.write(b, QaidaStage2.Mark.KHARA_ZABAR))
    assertEquals("ب\u0656", QaidaStage2.write(b, QaidaStage2.Mark.KHARI_ZER))
    assertEquals("ب\u0657", QaidaStage2.write(b, QaidaStage2.Mark.ULTA_PESH))
    assertEquals("اَبْ", QaidaStage2.write(b, QaidaStage2.Mark.JAZM))
    assertEquals(28, QaidaStage2.boardLetters(QaidaStage2.Mark.DO_ZER).size)
    assertEquals(27, QaidaStage2.boardLetters(QaidaStage2.Mark.JAZM).size)
  }

  @Test fun stage2_quiz() {
    repeat(50) { seed ->
      val qs = QaidaStage2Quiz.generate(kotlin.random.Random(seed))
      assertEquals(10, qs.size)
      qs.forEach { q ->
        assertTrue(q.correctIndex in q.options.indices)
        assertEquals(q.options.size, q.options.toSet().size)
        assertTrue(q.options.size >= 2)
      }
    }
  }

  @Test fun stage3_content() {
    assertEquals(14, QaidaStage3.shamsiIds.size); assertEquals(14, QaidaStage3.qamariIds.size)
    assertEquals(28, (QaidaStage3.shamsiIds + QaidaStage3.qamariIds).toSet().size)
    (QaidaStage3.shamsiIds + QaidaStage3.qamariIds).forEach { QaidaData.letter(it) }
    assertEquals("اَبَّ", QaidaStage3.tashdeed(QaidaData.letter("ba"), QaidaStage3.Vowel.ZABAR))
    // every sun-letter example really has tashdeed on the letter after the lam, moon examples have jazm on the lam
    QaidaStage3.shamsiWords.forEach { w ->
      val after = w.written.removePrefix("اَل").first().toString()
      assertTrue(w.written, QaidaStage3.shamsiIds.any { QaidaData.letter(it).arabic == after })
      assertTrue(w.written, w.written.contains(after + "\u0651"))
    }
    QaidaStage3.qamariWords.forEach { w ->
      assertTrue(w.written, w.written.startsWith("اَلْ"))
      val after = w.written.removePrefix("اَلْ").first().toString()
      assertTrue(w.written, QaidaStage3.qamariIds.any { QaidaData.letter(it).arabic == after })
    }
    assertEquals(7, QaidaStage3.fatiha.size); assertEquals(4, QaidaStage3.ikhlas.size)
  }

  /** Letters of the IndoPak ayat must match the app's bundled Quran text (diacritics, alif spelling and spaces ignored). */
  @Test fun stage3_ayatMatchBundledQuran() {
    fun skeleton(t: String) = t
      .replace(Regex("[\\u064B-\\u065F\\u0670\\u06D6-\\u06ED\\u0640]"), "")
      .replace(Regex("[اأإآٱ]"), "").replace('ى', 'ي').replace('ة', 'ه').replace(Regex("\\s"), "")
    var checked = 0
    for ((n, ayat) in listOf(1 to QaidaStage3.fatiha, 112 to QaidaStage3.ikhlas)) {
      val bundled = com.example.ui.screens.getOfflineSurah(n) ?: continue
      ayat.forEach { a ->
        assertEquals("$n:${a.number}", skeleton(bundled[a.number - 1].textAr), skeleton(a.text)); checked++
      }
    }
    assertTrue("Al-Fatiha must be checked", checked >= 7)
  }

  @Test fun stage3_quiz() {
    repeat(50) { seed ->
      val qs = QaidaStage3Quiz.generate(Random(seed))
      assertEquals(10, qs.size)
      qs.forEach { q -> assertTrue(q.correctIndex in q.options.indices); assertEquals(q.options.size, q.options.toSet().size) }
    }
  }
}
