package com.example.uat

import com.example.ui.screens.getOfflineSurah
import com.example.ui.screens.getQuran114Surahs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Islamic-content integrity checks (pure JVM). These guard the bundled Quran
 * data against accidental edits such as markdown characters in the text.
 */
class ContentIntegrityTest {

  // Standard Hafs ayah counts, surah 1..114 (total 6236).
  private val hafsCounts = intArrayOf(
    7, 286, 200, 176, 120, 165, 206, 75, 129, 109, 123, 111, 43, 52, 99, 128, 111, 110, 98, 135,
    112, 78, 118, 64, 77, 227, 93, 88, 69, 60, 34, 30, 73, 54, 45, 83, 182, 88, 75, 85,
    54, 53, 89, 59, 37, 35, 38, 29, 18, 45, 60, 49, 62, 55, 78, 96, 29, 22, 24, 13,
    14, 11, 11, 18, 12, 12, 30, 52, 52, 44, 28, 28, 20, 56, 40, 31, 50, 40, 46, 42,
    29, 19, 36, 25, 22, 17, 19, 26, 30, 20, 15, 21, 11, 8, 8, 19, 5, 8, 8, 11,
    11, 8, 3, 9, 5, 4, 7, 3, 6, 3, 5, 4, 5, 6,
  )

  @Test fun surahIndex_has114SurahsWithHafsAyahCounts() {
    val surahs = getQuran114Surahs()
    assertEquals(114, surahs.size)
    surahs.forEachIndexed { i, s ->
      assertEquals("surah number at index $i", i + 1, s.number)
      assertEquals("ayah count of ${s.nameEn}", hafsCounts[i], s.versesCount)
    }
    assertEquals(6236, surahs.sumOf { it.versesCount })
  }

  @Test fun offlineSurahs_haveCompleteVerseListsMatchingIndex() {
    val index = getQuran114Surahs().associateBy { it.number }
    for (n in 1..114) {
      val verses = getOfflineSurah(n) ?: continue
      assertEquals("verse count for offline surah $n", index.getValue(n).versesCount, verses.size)
      verses.forEachIndexed { i, v -> assertEquals("numbering in surah $n", i + 1, v.number) }
    }
  }

  @Test fun offlineQuranText_containsOnlyArabicScript() {
    // Any ASCII letter, digit or markup char inside Quranic text is corruption.
    val forbidden = Regex("[A-Za-z0-9*#_~`<>\\[\\]{}|\\\\]")
    for (n in 1..114) {
      val verses = getOfflineSurah(n) ?: continue
      verses.forEach { v ->
        assertTrue("Surah $n:${v.number} contains non-Arabic characters: ${v.textAr}", !forbidden.containsMatchIn(v.textAr))
        assertTrue("Surah $n:${v.number} is empty", v.textAr.isNotBlank())
        assertTrue("Surah $n:${v.number} has no translation", v.translation.isNotBlank())
      }
    }
  }
}
