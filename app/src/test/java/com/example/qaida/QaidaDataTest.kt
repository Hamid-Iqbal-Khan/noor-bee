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
    QaidaData.soundGroups.forEach { g -> g.examples.values.forEach { rows += "${it.audioKey},${it.arabic},لفظ: ${it.urduMeaning}" } }
    QaidaData.harakatLetters().forEach { l ->
      QaidaData.Harakah.values().forEach { h -> rows += "${QaidaData.audioKey(l, h)},${QaidaData.withHarakah(l, h)},ہجے: ${QaidaData.hijje(l, h)}" }
    }
    val out = File("build/outputs/roborazzi/qaida-recording-script.csv")
    out.parentFile.mkdirs(); out.writeText(rows.joinToString("\n") + "\n")
    assertEquals(1 + 29 + 15 + 84, rows.size)
  }
}
