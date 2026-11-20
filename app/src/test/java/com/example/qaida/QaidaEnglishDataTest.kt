package com.example.qaida

import com.example.english.EnglishCourse
import com.example.english.EnglishQuiz
import com.example.english.LessonKind
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.random.Random

/** English medium of the Quran course, and the English reading course: content checks. */
class QaidaEnglishDataTest {

  private val catalog = QaidaLang.parse(File("src/main/assets/qaida/ui_en.tsv").readText(Charsets.UTF_8))

  @Before fun english() { QaidaLang.setCatalogForTest(catalog); QaidaLang.english = true; QaidaLang.missing.clear() }
  @After fun urdu() { QaidaLang.english = false }

  private fun assertTranslated(where: String, urdu: String) {
    val en = QaidaLang.tr(urdu)
    assertFalse("$where: no English for \"$urdu\"", QaidaLang.looksUrdu(en))
  }

  @Test fun catalog_isCompleteAndEnglish() {
    assertTrue("catalog has ${catalog.size} entries", catalog.size > 1000)
    catalog.forEach { (ur, en) ->
      assertTrue("empty English for $ur", en.isNotBlank())
      assertFalse("English value still has Urdu letters: $ur -> $en", QaidaLang.looksUrdu(en))
    }
  }

  @Test fun everyLetter_hasEnglishNameMakhrajAndNote() {
    QaidaData.letters.forEach { l ->
      assertTranslated("name of ${l.id}", l.qaidaName)
      assertTranslated("makhraj of ${l.id}", l.makhraj)
      assertTranslated("region of ${l.id}", l.region.urdu)
      l.urduNote?.let { assertTranslated("note for ${l.id}", it) }
    }
    // names must stay distinct so the "name of this letter" quiz has one right answer
    val names = QaidaData.letters.map { QaidaLang.tr(it.qaidaName) }
    assertEquals(names.size, names.distinct().size)
    QaidaData.soundGroups.forEach { g -> assertTranslated("tip ${g.id}", g.tip); g.examples.values.forEach { assertTranslated("example", it.urduMeaning) } }
  }

  @Test fun everySurahName_andNote_isEnglish() {
    QaidaJuzAmma.urduNames.values.forEach { assertTranslated("surah name", it) }
    QaidaNotes.background.forEach { (s, t) -> assertTranslated("background $s", t) }
    QaidaNotes.parentNotes.forEach { (k, t) -> assertTranslated("parent note $k", t) }
  }

  @Test fun spellings_inEnglish() {
    assertEquals("Baa fatha بَ", QaidaData.hijje(QaidaData.letter("ba"), QaidaData.Harakah.ZABAR))
    assertEquals("Alif fatha a, Baa sukoon: اَبْ", QaidaStage2.hijje(QaidaData.letter("ba"), QaidaStage2.Mark.JAZM))
    assertEquals("Baa double fatha بًا", QaidaStage2.hijje(QaidaData.letter("ba"), QaidaStage2.Mark.DO_ZABAR))
    assertEquals("Alif fatha, Baa fatha shaddah: " + QaidaStage3.tashdeed(QaidaData.letter("ba"), QaidaStage3.Vowel.ZABAR), QaidaStage3.tashdeedHijje(QaidaData.letter("ba"), QaidaStage3.Vowel.ZABAR))
    QaidaLang.english = false
    assertEquals("بَا زَبَر بَ", QaidaData.hijje(QaidaData.letter("ba"), QaidaData.Harakah.ZABAR))
  }

  @Test fun everyQuiz_hasDistinctEnglishOptions() {
    val juz = QaidaJuzAmma.parse(File("src/main/assets/${QaidaJuzAmma.ASSET}").readLines(Charsets.UTF_8).asSequence())
    val gens: Map<String, (Random) -> List<QaidaQuestion>> = mapOf(
      "1" to { r: Random -> QaidaQuiz.generate(r) }, "2" to { r: Random -> QaidaStage2Quiz.generate(r) }, "3" to { r: Random -> QaidaStage3Quiz.generate(r) },
      "4" to { r: Random -> QaidaStage4Quiz.generate(r) }, "5" to { r: Random -> QaidaStage5Quiz.generate(r) }, "6" to { r: Random -> QaidaStage6Quiz.generate(r) },
      "7" to { r: Random -> QaidaStage7Quiz.generate(r) }, "11" to { r: Random -> QaidaStage11Quiz.generate(r) },
    ) + (8..10).associate { st -> "$st" to { r: Random -> QaidaJuzAmmaQuiz.generate(r, QaidaJuzAmma.group(st).surahs.mapNotNull { juz[it] }) } }
    val problems = linkedSetOf<String>()
    gens.forEach { (stage, gen) ->
      repeat(150) { seed ->
        gen(Random(seed)).forEach { q ->
          val shown = q.options.map { if (q.optionsAreArabic) it else QaidaLang.tr(it) }
          if (shown.size != shown.distinct().size) problems += "stage $stage: two options look the same in English: $shown (${q.options})"
          assertTranslated("stage $stage instruction", q.instruction)
          if (!q.optionsAreArabic) shown.forEach { assertFalse("stage $stage option not translated: $it", QaidaLang.looksUrdu(it)) }
          if (!q.promptIsArabic) assertTranslated("stage $stage prompt", q.prompt)
        }
      }
    }
    problems.forEach { println(it) }
    assertTrue(problems.joinToString("\n"), problems.isEmpty())
  }

  @Test fun juzAmma_hasEnglishMeaningForEveryWord_andEveryAyah() {
    val juz = QaidaJuzAmma.parse(File("src/main/assets/${QaidaJuzAmma.ASSET}").readLines(Charsets.UTF_8).asSequence())
    juz.values.flatMap { it.ayat }.forEach { a ->
      assertEquals("${a.ayah.surah}:${a.ayah.number}", a.words.size, a.meaningsEn.size)
      assertTrue(a.meaningsEn.all { it.isNotBlank() && !QaidaLang.looksUrdu(it) })
      assertEquals(a.meaningsEn, a.shown)
    }
    val ur = QaidaMafhoom.parse(File("src/main/assets/qaida/mafhoom.tsv").readLines().asSequence())
    val en = QaidaMafhoom.parse(File("src/main/assets/qaida/mafhoom_en.tsv").readLines().asSequence())
    assertEquals(ur.keys, en.keys)
    assertEquals(571, en.size)
    en.values.forEach { assertFalse(QaidaLang.looksUrdu(it)) }
    // the typed surahs (Stages 5–6) are translated through the catalogue
    (QaidaStage5.wbwSurahs + QaidaStage6.surahs).flatMap { it.ayat }.forEach { a ->
      a.meanings.forEach { assertTranslated("${a.ayah.surah}:${a.ayah.number}", it) }
    }
  }

  // ---- English reading course ------------------------------------------------------------------------------
  @Test fun englishCourse_structure() {
    // 4 reading levels, plus 4 speaking levels when CI has fetched the VOA conversations
    assertTrue(EnglishCourse.stages.size == 4 || EnglishCourse.stages.size == 8)
    com.example.english.VoaData.lessons.forEach { v ->
      assertTrue("VOA ${v.number} has a conversation", v.lines.size >= 2)
      v.lines.forEach { assertTrue("clip for '${it.text}'", com.example.english.VoaData.clips.containsKey(com.example.english.EnglishAudio.key(it.text))) }
    }
    val ids = EnglishCourse.stages.flatMap { s -> s.lessons.map { it.id } }
    assertEquals(ids.size, ids.distinct().size)
    EnglishCourse.allWords.forEach { w ->
      val joined = w.parts.joinToString("").replace("_e", "") + if (w.parts.any { it.endsWith("_e") }) "e" else ""
      assertEquals("tiles of ${w.word}", w.word.toCharArray().sorted(), joined.toCharArray().sorted())
    }
    assertEquals(26, EnglishCourse.alphabet.size)
    // general English keywords: a for apple, b for ball, c for cat, d for dog
    assertEquals(listOf("apple", "ball", "cat", "dog"), EnglishCourse.alphabet.take(4).map { it.keyword })
    // no religious content in the English course (that lives in the Quran sections)
    val text = (EnglishCourse.alphabet.map { it.keyword } + EnglishCourse.allWords.map { it.word } + EnglishCourse.allTricky +
      EnglishCourse.stages.flatMap { s -> s.lessons.flatMap { l -> l.sentences.map { it.text } + l.title + l.intro } }).joinToString(" ")
    listOf("Allah", "Islam", "Quran", "masjid", "dua", "Bismillah", "Salah", "Ramadan", "Eid").forEach {
      assertTrue("found $it", !Regex("\\b$it\\b", RegexOption.IGNORE_CASE).containsMatchIn(text))
    }
    EnglishCourse.stages.flatMap { it.lessons }.flatMap { it.sentences }.filter { it.key.isNotEmpty() }.forEach { s ->
      assertTrue("gap '${s.key}' in '${s.text}'", Regex("\\b${Regex.escape(s.key)}\\b").containsMatchIn(s.text))
    }
    assertEquals("A", EnglishCourse.pronounce("A"))
  }

  @Test fun englishCourse_quizzesHaveOneRightAnswer() {
    val emojiOf = EnglishCourse.allWords.associate { it.word to it.emoji }
    for (stage in EnglishCourse.stages.map { it.number }) repeat(300) { seed ->
      EnglishQuiz.generate(stage, Random(seed)).forEach { q ->
        assertEquals("stage $stage: 4 options ${q.options}", 4, q.options.size)
        assertEquals("stage $stage: distinct ${q.options}", 4, q.options.distinct().size)
        assertTrue(q.correctIndex in 0..3)
        if (q.instruction == "Which word is this?") {
          val others = q.options.filterIndexed { i, _ -> i != q.correctIndex }
          assertTrue("picture ${q.prompt} fits two words: ${q.options}", others.none { emojiOf[it] == q.prompt })
        }
        if (q.instruction.startsWith("Which letter does")) {
          val kw = q.instruction.substringAfter('\'').substringBefore('\'')
          assertTrue(kw.lowercase().startsWith(q.options[q.correctIndex]))
        }
      }
    }
  }
}
