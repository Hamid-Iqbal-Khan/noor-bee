package com.example.arabic

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.random.Random

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = RobolectricDeviceQualifiers.Pixel8)
class ArabicWordsTest {

  @get:Rule val rule = createComposeRule()
  private val spoken = mutableListOf<String>()

  // ---- content ---------------------------------------------------------------------------------------
  @Test fun everyWord_isComplete_vowelled_andNotRepeated() {
    val all = ArabicWords.allWords
    assertTrue("${all.size} words", all.size >= 150)
    assertEquals("Arabic repeated", all.size, all.map { it.arabic }.distinct().size)
    assertEquals("English repeated", all.size, all.map { it.english.lowercase() }.distinct().size)
    assertEquals("Urdu repeated", all.size, all.map { it.urdu }.distinct().size)
    val harakat = Regex("[ً-ْ]")
    all.forEach { w ->
      assertTrue("no harakat: ${w.arabic}", harakat.containsMatchIn(w.arabic))
      assertTrue("Urdu meaning in Urdu script: ${w.urdu}", w.urdu.any { it in '؀'..'ۿ' })
      assertTrue(w.english.isNotBlank() && w.translit.isNotBlank() && w.emoji.isNotBlank())
    }
    assertEquals(listOf(1, 2, 3, 4, 5, 6), ArabicWords.levels.map { it.number })
    assertEquals("tuffāḥatun", ArabicWords.allWords.first { it.english == "an apple" }.translit)
  }

  @Test fun quizzes_haveOneRightAnswer() {
    for (level in 1..6) repeat(300) { seed ->
      ArabicWordsQuiz.generate(level, Random(seed)).forEach { q ->
        assertEquals(4, q.options.size)
        assertEquals(4, q.options.map { it.english }.distinct().size)
        assertEquals(4, q.options.map { it.urdu }.distinct().size)
        assertTrue(q.correctIndex in 0..3)
      }
    }
  }

  // ---- screens -------------------------------------------------------------------------------------
  private fun launch(urdu: Boolean, level: Int = 1) {
    rule.setContent { MyApplicationTheme { ArabicWordsScreen(55, urdu, {}, { spoken += it }, { _, _, _ -> }, level) } }
    rule.waitForIdle()
  }
  private fun shot(name: String) = rule.onRoot().captureRoboImage(filePath = "build/outputs/roborazzi/arabic/$name.png")
  private fun open(tag: String) { try { rule.onNodeWithTag(tag).performScrollTo() } catch (_: Throwable) { }; rule.onNodeWithTag(tag).performClick(); rule.waitForIdle() }

  @Test fun card_turnsFromArabicToEnglish_withUrduOnBothSides() {
    launch(urdu = false); shot("level1_home_en")
    open("arabic_set_a1.1")
    rule.onNodeWithTag("arabic_card_arabic").assertTextContains("تُفَّاحَةٌ")
    rule.onNodeWithTag("arabic_card_urdu").assertTextContains("(سیب)"); shot("card_front_arabic")
    rule.onNodeWithTag("arabic_card").performClick(); rule.waitForIdle()
    rule.onNodeWithTag("arabic_card_english").assertTextContains("an apple")
    rule.onNodeWithTag("arabic_card_urdu").assertTextContains("(سیب)"); shot("card_back_english")
    rule.onNodeWithTag("arabic_card_speak").performClick()
    assertEquals("تُفَّاحَةٌ", spoken.last())
  }

  private fun walk(urdu: Boolean, level: Int) {
    launch(urdu, level)
    ArabicWords.level(level).sets.forEach { s ->
      open("arabic_set_${s.id}")
      repeat(s.words.size) {
        rule.onNodeWithTag("arabic_card").performClick(); rule.waitForIdle()
        rule.onNodeWithTag("arabic_card_knew").performClick(); rule.waitForIdle()
      }
      rule.onNodeWithTag("arabic_cards_result").assertExists()
      rule.onNodeWithTag("arabic_cards_done").performScrollTo().performClick(); rule.waitForIdle()
    }
    open("arabic_quiz_$level")
    repeat(10) {
      rule.onNodeWithTag("arabic_option_0").performScrollTo().performClick(); rule.waitForIdle()
      rule.onNodeWithTag("arabic_next").performScrollTo().performClick(); rule.waitForIdle()
    }
    rule.onNodeWithTag("arabic_quiz_result").assertExists(); shot("level${level}_quiz_${if (urdu) "ur" else "en"}")
  }

  @Test fun level1_english() = walk(false, 1)
  @Test fun level2_urdu() = walk(true, 2)
  @Test fun level3_english() = walk(false, 3)
  @Test fun level4_urdu() = walk(true, 4)
  @Test fun level5_english() = walk(false, 5)
  @Test fun level6_urdu() = walk(true, 6)
}
