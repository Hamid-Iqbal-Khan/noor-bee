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
    // Stage 4
    QaidaStage4.stopExamples.forEach { rows += "${it.audioKey},${it.word},لفظ (پورا)"; rows += "${it.audioKey}_stop,${it.stopped},وقف کے ساتھ" }
    QaidaStage4.qalqalahWords.forEach { rows += "${it.audioKey},${it.written},قلقلہ: ${it.urdu.replace(',', '،')}" }
    QaidaStage4.surahs.forEach { s -> s.ayat.forEach { rows += "${it.audioKey},${it.text},آیت ${it.surah}:${it.number} — آخر میں وقف" } }
    // Stage 5
    QaidaStage5.allWords.forEach { rows += "${it.audioKey},${it.arabic},لفظ: ${it.urdu.replace(',', '،')}" }
    QaidaStage5.endings.forEach { rows += "${it.audioKey},${it.example},${it.exampleUrdu}" }
    QaidaStage5.wbwSurahs.forEach { s -> s.ayat.forEach { a -> a.words.forEachIndexed { i, w -> rows += "${a.wordKey(i)},$w,اکیلا لفظ (${a.meanings[i].replace(',', '،')})" } } }
    // Stage 6
    QaidaStage6.surahs.forEach { s -> s.ayat.forEach { a ->
      rows += "${a.ayah.audioKey},${a.ayah.text},آیت ${a.ayah.surah}:${a.ayah.number} — ترتیل سے"
      a.words.forEachIndexed { i, w -> rows += "${a.wordKey(i)},$w,اکیلا لفظ (${a.meanings[i].replace(',', '،')})" }
    } }
    // Stage 7
    QaidaStage7.examples.forEach { rows += "${it.audioKey},${it.phrase},${it.rule.urdu} — ${it.note}" }
    QaidaStage7.meemExamples.forEach { rows += "${it.audioKey},${it.phrase},${it.rule.urdu} — ${it.note}" }
    // Stages 8–10 (Juz 'Amma) and 11
    juzAmma().values.forEach { s -> s.ayat.forEach { a ->
      rows += "${a.ayah.audioKey},${a.ayah.text},آیت ${a.ayah.surah}:${a.ayah.number} — ترتیل سے"
      a.words.forEachIndexed { i, w -> rows += "${a.wordKey(i)},$w,اکیلا لفظ (${a.meanings[i].replace(',', '،')})" }
    } }
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
    assertEquals("اَب\u0651\u064E", QaidaStage3.tashdeed(QaidaData.letter("ba"), QaidaStage3.Vowel.ZABAR))
    // every sun-letter example really has tashdeed on the letter after the lam, moon examples have jazm on the lam
    QaidaStage3.shamsiWords.forEach { w ->
      val after = w.written.removePrefix("اَل").first().toString()
      assertTrue(w.written, QaidaStage3.shamsiIds.any { QaidaData.letter(it).arabic == after })
      assertTrue(w.written, w.written.removePrefix("اَل").drop(1).takeWhile { it in '\u064B'..'\u0652' }.contains('\u0651'))
    }
    QaidaStage3.qamariWords.forEach { w ->
      assertTrue(w.written, w.written.startsWith("اَلْ"))
      val after = w.written.removePrefix("اَلْ").first().toString()
      assertTrue(w.written, QaidaStage3.qamariIds.any { QaidaData.letter(it).arabic == after })
    }
    val (p1, p2) = QaidaStage3.tashdeedParts(QaidaData.letter("ta"), QaidaStage3.Vowel.ZABAR)
    assertEquals("اَت\u0652", p1); assertEquals("ت\u064E", p2)
    assertEquals(7, QaidaStage3.fatiha.size); assertEquals(4, QaidaStage3.ikhlas.size)
  }

  /** Letters of the IndoPak ayat must match the app's bundled Quran text (diacritics, alif spelling and spaces ignored). */
  @Test fun stage3_ayatMatchBundledQuran() {
    fun skeleton(t: String) = QuranSkeleton.of(t)
    var checked = 0
    for ((n, ayat) in listOf(1 to QaidaStage3.fatiha, 112 to QaidaStage3.ikhlas) + QaidaStage4.surahs.map { it.number to it.ayat }) {
      val bundled = com.example.ui.screens.getOfflineSurah(n) ?: continue
      ayat.forEach { a ->
        assertEquals("$n:${a.number}", skeleton(bundled[a.number - 1].textAr), skeleton(a.text)); checked++
      }
    }
    assertTrue("Al-Fatiha must be checked", checked >= 7)
    // Ayah counts of the Stage 4 surahs
    assertEquals(listOf(3, 3, 5, 6), QaidaStage4.surahs.map { it.ayat.size })
  }

  @Test fun stage3_quiz() {
    repeat(50) { seed ->
      val qs = QaidaStage3Quiz.generate(Random(seed))
      assertEquals(10, qs.size)
      qs.forEach { q -> assertTrue(q.correctIndex in q.options.indices); assertEquals(q.options.size, q.options.toSet().size) }
    }
  }

  @Test fun stage4_stopForms() {
    val expected = mapOf(
      "اَحَدٌ" to "اَحَدْ", "الصَّمَدُ" to "الصَّمَدْ", "الرَّحِيْمِ" to "الرَّحِيْمْ",
      "عَلِيْمًا" to "عَلِيْمَا", "جَنَّةٌ" to "جَنَّهْ", "رَحْمَةً" to "رَحْمَهْ", "وَانْحَرْ" to "وَانْحَرْ",
      "لَهٗ" to "لَهْ", "بِهٖ" to "بِهْ",
    )
    expected.forEach { (w, s) -> assertEquals(w, s, QaidaStage4.stopForm(w)) }
    assertEquals("الْفَلَقْ", QaidaStage4.stopOnLastWord(QaidaStage4.falaq.ayat[0].text))
    assertEquals(5, QaidaStage4.qalqalahIds.size)
    QaidaStage4.qalqalahIds.forEach { QaidaData.letter(it) }
  }

  @Test fun stage4_quiz() {
    repeat(50) { seed ->
      val qs = QaidaStage4Quiz.generate(Random(seed))
      assertEquals(10, qs.size)
      qs.forEach { q -> assertTrue(q.correctIndex in q.options.indices); assertEquals(q.options.size, q.options.toSet().size); assertTrue(q.options.size >= 2) }
    }
  }

  @Test fun stage5_content() {
    QaidaStage5.wbwSurahs.forEach { s -> s.ayat.forEach { a ->
      assertEquals("meanings for ${a.ayah.surah}:${a.ayah.number}", a.words.size, a.meanings.size)
      a.meanings.forEach { assertTrue(it.isNotBlank()) }
    } }
    assertEquals(listOf(7, 4, 3, 3, 5, 6), QaidaStage5.wbwSurahs.map { it.ayat.size })
    val arabic = QaidaStage5.allWords.map { it.arabic }
    assertEquals("frequent words unique", arabic.size, arabic.toSet().size)
    assertTrue("word count", QaidaStage5.allWords.size >= 35)
    QaidaStage5.endings.forEach { assertTrue(it.example, it.example.filter { c -> c !in '\u064B'..'\u0652' }.startsWith("رب")) }
  }

  @Test fun stage5_quiz() {
    repeat(50) { seed ->
      val qs = QaidaStage5Quiz.generate(Random(seed))
      assertEquals(10, qs.size)
      qs.forEach { q -> assertTrue(q.correctIndex in q.options.indices); assertEquals(4, q.options.toSet().size) }
    }
  }

  @Test fun stage6_content() {
    assertEquals(listOf(105, 106, 107, 109, 110, 111), QaidaStage6.surahs.map { it.number })
    assertEquals(listOf(5, 4, 7, 6, 3, 5), QaidaStage6.surahs.map { it.ayat.size })
    QaidaStage6.surahs.forEach { s -> s.ayat.forEach { a ->
      assertEquals("meanings for ${a.ayah.surah}:${a.ayah.number}", a.words.size, a.meanings.size)
    } }
    assertTrue("flashcards", QaidaStage6.flashcards.size > 100)
    assertEquals(QaidaStage6.flashcards.size, QaidaStage6.flashcards.map { it.arabic }.toSet().size)
  }

  @Test fun stage6_quiz() {
    repeat(50) { seed ->
      val qs = QaidaStage6Quiz.generate(Random(seed))
      assertEquals(10, qs.size)
      qs.forEach { q -> assertTrue(q.correctIndex in q.options.indices); assertEquals(4, q.options.toSet().size) }
    }
  }

  @Test fun stage7_letterGroups() {
    val groups = listOf(QaidaStage7.izharIds, QaidaStage7.idghamGhunnaIds, QaidaStage7.idghamNoGhunnaIds, QaidaStage7.iqlabIds, QaidaStage7.ikhfaIds)
    assertEquals(listOf(6, 4, 2, 1, 15), groups.map { it.size })
    val all = groups.flatten()
    assertEquals("groups do not overlap", all.size, all.toSet().size)
    assertEquals("every letter except alif has a rule", QaidaData.letters.map { it.id }.filter { it != "alif" }.toSet(), all.toSet())
  }

  @Test fun stage7_examples() {
    QaidaStage7.Rule.values().forEach { r -> assertTrue("examples for $r", QaidaStage7.examplesFor(r).isNotEmpty()) }
    QaidaStage7.examples.forEach { e ->
      val bare = e.phrase.filter { it !in '\u064B'..'\u065F' && it != '\u06E2' && it != '\u0670' }
      // the phrase really has noon sakin or tanween followed (across a space or not) by the named letter
      val next = QaidaData.letter(e.nextLetterId).arabic.let { if (it == "ء") "ا" else it }
      val hasTanween = e.phrase.any { it in '\u064B'..'\u064D' }
      assertTrue(e.phrase, bare.contains("ن" + next) || bare.contains("ن " + next) || (hasTanween && bare.contains(" " + next)))
    }
    assertEquals(3, QaidaStage7.MeemRule.values().size)
    QaidaStage7.meemExamples.forEach { assertTrue(it.phrase, it.phrase.contains("م\u0652")) }
  }

  @Test fun stage7_quiz() {
    repeat(50) { seed ->
      val qs = QaidaStage7Quiz.generate(Random(seed))
      assertEquals(10, qs.size)
      qs.forEach { q -> assertTrue(q.correctIndex in q.options.indices); assertEquals(q.options.size, q.options.toSet().size) }
    }
  }

  private fun juzAmma() = QaidaJuzAmma.parse(File("src/main/assets/${QaidaJuzAmma.ASSET}").readLines(Charsets.UTF_8).asSequence())

  @Test fun juzAmma_dataComplete() {
    val juz = juzAmma()
    val expected = mapOf(78 to 40, 79 to 46, 80 to 42, 81 to 29, 82 to 19, 83 to 36, 84 to 25, 85 to 22, 86 to 17, 87 to 19,
      88 to 26, 89 to 30, 90 to 20, 91 to 15, 92 to 21, 93 to 11, 94 to 8, 95 to 8, 96 to 19, 97 to 5, 98 to 8, 99 to 8,
      100 to 11, 101 to 11, 102 to 8, 104 to 9)
    assertEquals(expected, juz.mapValues { it.value.ayat.size })
    assertEquals(513, juz.values.sumOf { it.ayat.size })
    juz.values.forEach { s -> s.ayat.forEachIndexed { i, a ->
      assertEquals("numbering ${s.number}", i + 1, a.ayah.number)
      assertEquals("meanings for ${s.number}:${a.ayah.number}", a.words.size, a.meanings.size)
      a.meanings.forEach { assertTrue("${s.number}:${a.ayah.number}", it.isNotBlank()) }
      assertTrue("Arabic only in ${s.number}:${a.ayah.number}", a.ayah.text.none { it in 'A'..'z' || it in '0'..'9' })
    } }
    assertEquals(juz.keys, QaidaJuzAmma.groups.flatMap { it.surahs }.toSet())
    juz.keys.forEach { assertTrue(QaidaJuzAmma.urduNames.containsKey(it)) }
  }

  @Test fun juzAmma_quiz() {
    val juz = juzAmma()
    QaidaJuzAmma.groups.forEach { g ->
      repeat(30) { seed ->
        val qs = QaidaJuzAmmaQuiz.generate(Random(seed), g.surahs.map { juz.getValue(it) })
        assertEquals(10, qs.size)
        qs.forEach { q -> assertTrue(q.correctIndex in q.options.indices); assertEquals(4, q.options.toSet().size) }
      }
    }
  }

  @Test fun stage11_content() {
    assertTrue(QaidaStage11.roots.size >= 7)
    // every word under a root really contains that root's three letters, in order
    fun bare(t: String) = t.filter { it in '\u0621'..'\u064A' && it !in "اأإآى" }
    QaidaStage11.roots.forEach { r ->
      val letters = r.letters.split(' ')
      (r.words.map { it.arabic } + QaidaStage11.detective.filter { it.rootId == r.id }.map { it.word }).forEach { w ->
        val b = bare(w)
        var at = -1
        letters.forEach { l -> at = b.indexOf(l, at + 1); assertTrue("$w should contain ${r.letters}", at >= 0) }
      }
    }
    assertEquals(6, QaidaStage11.past.size); assertEquals(6, QaidaStage11.present.size)
    repeat(50) { seed ->
      val qs = QaidaStage11Quiz.generate(Random(seed))
      assertEquals(10, qs.size)
      qs.forEach { q -> assertTrue(q.correctIndex in q.options.indices); assertEquals(q.options.size, q.options.toSet().size) }
    }
  }

  @Test fun review_fixes() {
    val juz = juzAmma()
    // sajdah ayat flagged
    assertEquals(setOf(84 to 21, 96 to 19), juz.values.flatMap { s -> s.ayat.filter { it.sajdah }.map { s.number to it.ayah.number } }.toSet())
    // silent waw of اُولٰٓئِكَ carries no jazm anywhere
    juz.values.forEach { s -> s.ayat.forEach { a -> assertTrue("${s.number}:${a.ayah.number}", !a.ayah.text.contains("اُوْلٰٓئِكَ")) } }
    // الرَّحْمٰنُ is never taught as an ordinary indefinite word
    assertTrue(QaidaStage5.allWords.none { it.arabic == "رَحْمٰنٌ" })
    assertTrue(QaidaStage11.roots.flatMap { it.words }.none { it.arabic == "رَحْمٰنٌ" })
    // madd lazim taught as 6
    assertTrue(QaidaStage3.maddSignWords.first { it.audioKey == "s3_madd_daallin" }.urdu.contains("۶"))
    // لَآ اُقْسِمُ not glossed as "نہیں"
    listOf(90 to 1, 81 to 15, 84 to 16).forEach { (s, n) -> assertTrue(!juz.getValue(s).ayat[n - 1].meanings[0].contains("نہیں")) }
    // Allah's planning is never "چال"
    assertTrue(!juz.getValue(86).ayat[15].meanings.joinToString().contains("چال"))
  }

  @Test fun audio_sources() {
    assertEquals(listOf("https://everyayah.com/data/Alafasy_128kbps/001001.mp3"), QuranAudio.urlForKey(QuranAudio.KEY_BASMALAH))
    assertEquals(listOf("https://everyayah.com/data/Alafasy_128kbps/096019.mp3"), QuranAudio.urlForKey("s3_ayah_96_19"))
    assertEquals(listOf("https://audio.qurancdn.com/wbw/112_001_003.mp3"), QuranAudio.urlForKey("s5_wbw_112_1_3"))
    assertEquals(listOf("https://audio.qurancdn.com/wbw/036_001_001.mp3"), QuranAudio.urlForKey("s2_muqattaat_yasin"))
    assertEquals("بَاءْ", QuranAudio.speechFor("letter_ba", "ب"))
    val index = QuranAudio.buildIndex(emptyList(), QuranAudio.courseAyat(juzAmma()))
    // words typed in other lessons find their recited form inside the course surahs
    assertEquals(listOf("https://audio.qurancdn.com/wbw/112_001_001.mp3"), index.urlsFor("قُلْ"))
    assertEquals(listOf("https://audio.qurancdn.com/wbw/001_007_008.mp3", "https://audio.qurancdn.com/wbw/001_007_009.mp3"),
      index.urlsFor("وَلَا الضَّآلِّيْنَ"))
    assertTrue("مِنْ شَرِّ found", index.urlsFor("مِنْ شَرِّ") != null)
    // how many example words across the course now get real recitation
    val examples = QaidaStage3.tashdeedWords + QaidaStage3.ghunnaWords + QaidaStage3.maddSignWords + QaidaStage3.shamsiWords + QaidaStage3.qamariWords
    val found = examples.count { index.urlsFor(it.written) != null }
    println("Stage 3 example words with recitation audio: $found / ${examples.size}")
  }

  @Test fun easyMeanings_everyAyah() {
    val m = QaidaMafhoom.parse(File("src/main/assets/${QaidaMafhoom.ASSET}").readLines(Charsets.UTF_8).asSequence())
    val course = QuranAudio.courseAyat(juzAmma())
    assertEquals(571, course.size)
    course.forEach { a -> assertTrue("easy meaning for ${a.surah}:${a.number}", !m[a.surah to a.number].isNullOrBlank()) }
    assertEquals(course.size, m.size)
    // the corrected renderings are used in the easy meanings too
    assertTrue(m.getValue(90 to 1).startsWith("سنو!")); assertTrue(m.getValue(86 to 16).contains("تدبیر"))
    assertTrue(m.values.none { it.contains("نامۂ اعمال") || it.contains("پرہیزگاری") })
  }

  @Test fun waqfSigns_inside_ayat() {
    val juz = juzAmma()
    val signs = juz.values.flatMap { s -> s.ayat.map { it.ayah.text } }.joinToString("").count { it in '\u06D6'..'\u06DB' }
    assertEquals(17, signs)
    assertTrue(QaidaStage6.nasr.ayat[2].ayah.text.contains("\u06DA"))
    // a stop sign never becomes a word of its own
    juz.values.forEach { s -> s.ayat.forEach { a -> a.words.forEach { w -> assertTrue(w.any { it in '\u0621'..'\u064A' }) } } }
  }

  @Test fun notes_point_at_real_ayat() {
    val course = QuranAudio.courseAyat(juzAmma()).map { it.surah to it.number }.toSet()
    QaidaNotes.parentNotes.keys.forEach { assertTrue("$it", it in course) }
    QaidaNotes.background.keys.forEach { s -> assertTrue("$s", course.any { it.first == s }) }
  }
}
