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
}
