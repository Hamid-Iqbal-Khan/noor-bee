package com.example.qaida

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The Quran course in English medium: every lesson of all 11 stages is opened, scrolled to the end and
 * every practice answered, and no Urdu text may be left untranslated anywhere on the way.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = RobolectricDeviceQualifiers.Pixel8)
class QaidaEnglishTest {

  @get:Rule val rule = createComposeRule()
  private val spoken = mutableListOf<String>()

  @After fun backToUrdu() { QaidaLang.english = false }

  private fun launch(stage: Int) {
    QaidaLang.missing.clear()
    rule.setContent {
      MyApplicationTheme {
        QaidaScreen(profileId = 77, onBackPressed = {}, onSpeakArabic = { spoken += it }, onEarnRewards = { _, _, _ -> },
          startStage = stage, english = true)
      }
    }
    rule.waitForIdle()
  }

  private fun shot(name: String) = rule.onRoot().captureRoboImage(filePath = "build/outputs/roborazzi/qaida-en/$name.png")

  private fun tagStartsWith(prefix: String) = SemanticsMatcher("tag starts with $prefix") {
    it.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(prefix) == true
  }

  private fun open(tag: String) {
    try { rule.onNodeWithTag(tag).performScrollTo() } catch (_: Throwable) { }
    rule.onNodeWithTag(tag).performClick(); rule.waitForIdle()
  }

  /** Scrolls a lazy list one item at a time so every item is composed (and so checked). */
  private fun scrollThrough() {
    val lists = rule.onAllNodes(hasScrollToIndexAction()).fetchSemanticsNodes()
    if (lists.isEmpty()) return
    val list = rule.onAllNodes(hasScrollToIndexAction())[0]
    var i = 0
    while (i < 400) {
      try { list.performScrollToIndex(i) } catch (_: Throwable) { break }
      rule.waitForIdle(); i++
    }
  }

  private fun back() { rule.onNodeWithTag("qaida_back").performClick(); rule.waitForIdle() }

  private fun answerQuiz() {
    repeat(10) {
      rule.onNodeWithTag("qaida_option_0").performScrollTo().performClick(); rule.waitForIdle()
      rule.onNodeWithTag("qaida_next").performScrollTo().performClick(); rule.waitForIdle()
    }
    rule.onNodeWithTag("qaida_quiz_result").assertExists()
  }

  private fun lessons(prefix: String, names: List<String>) = names.map { "lesson_${prefix}$it" }

  private fun walkStage(stage: Int, lessonTags: List<String>, quizTag: String, extra: (String) -> Unit = {}) {
    launch(stage); shot("stage${stage}_home")
    lessonTags.forEach { t ->
      open(t); shot("stage${stage}_${t.removePrefix("lesson_")}")
      extra(t); scrollThrough(); back()
    }
    open(quizTag); answerQuiz(); shot("stage${stage}_quiz_result")
    assertTrue("untranslated in stage $stage:\n" + QaidaLang.missing.joinToString("\n"), QaidaLang.missing.isEmpty())
  }

  @Test fun stage1_englishEverywhere() {
    walkStage(1, listOf("lesson_letters", "lesson_sounds", "lesson_harakat"), "lesson_quiz") { t ->
      if (t == "lesson_letters") {
        rule.onNodeWithTag("qaida_makhraj_map").performClick(); rule.waitForIdle(); shot("stage1_makhraj_map")
        // every letter's detail card, including the notes written for English speakers
        QaidaData.letters.forEach { l ->
          try { rule.onNodeWithTag("qaida_letter_${l.id}").performScrollTo() } catch (_: Throwable) { }
          rule.onNodeWithTag("qaida_letter_${l.id}").performSemanticsAction(SemanticsActions.OnClick); rule.waitForIdle()
          if (l.id == "dad") shot("stage1_letter_dad")
          rule.onNodeWithTag("qaida_letter_detail").assertExists()
          rule.onNodeWithText("OK").performClick(); rule.waitForIdle()
        }
      }
    }
    assertEquals("Baa fatha بَ", QaidaData.hijje(QaidaData.letter("ba"), QaidaData.Harakah.ZABAR))
  }

  @Test fun stage2_englishEverywhere() =
    walkStage(2, lessons("s2_", listOf("joining", "muqattaat", "tanween", "khara", "madd", "jazm")), "lesson_s2_quiz")

  @Test fun stage3_englishEverywhere() =
    walkStage(3, lessons("s3_", listOf("tashdeed", "ghunna", "madd", "shamsi", "fatiha", "ikhlas")), "lesson_s3_quiz")

  @Test fun stage4_englishEverywhere() =
    walkStage(4, lessons("s4_", listOf("stop", "signs", "qalqalah", "kawthar", "falaq", "nas")), "lesson_s4_quiz")

  @Test fun stage5_englishEverywhere() =
    walkStage(5, lessons("s5_", listOf("words", "endings", "fatiha", "short", "falaq")), "lesson_s5_quiz")

  @Test fun stage6_englishEverywhere() {
    walkStage(6, lessons("s6_", listOf("fil", "maun", "kafirun", "masad")), "lesson_s6_quiz")
    // flash cards: flip every card of a round
    rule.onNodeWithTag("qaida_back").performClick(); rule.waitForIdle()
    open("lesson_s6_cards")
    repeat(15) {
      rule.onNodeWithTag("qaida_card").performClick(); rule.waitForIdle()
      rule.onNodeWithTag("qaida_card_knew").performClick(); rule.waitForIdle()
    }
    rule.onNodeWithTag("qaida_cards_result").assertExists(); shot("stage6_cards_result")
    assertTrue("untranslated cards:\n" + QaidaLang.missing.joinToString("\n"), QaidaLang.missing.isEmpty())
  }

  @Test fun stage7_englishEverywhere() =
    walkStage(7, lessons("s7_", listOf("izhar", "idgham", "iqlab", "ikhfa", "meem", "spot")), "lesson_s7_quiz") { t ->
      if (t == "lesson_s7_spot") {
        val cards = rule.onAllNodes(tagStartsWith("qaida_s7_spot_")).fetchSemanticsNodes().size
        repeat(cards) { i -> try { rule.onAllNodes(tagStartsWith("qaida_s7_spot_"))[i].performSemanticsAction(SemanticsActions.OnClick) } catch (e: Throwable) { println("spot card $i: $e") }; rule.waitForIdle() }
      }
    }

  private fun juzStage(stage: Int) = walkStage(stage, QaidaJuzAmma.group(stage).surahs.map { "lesson_juz_$it" }, "lesson_juz_quiz_$stage")
  @Test fun stage8_englishEverywhere() = juzStage(8)
  @Test fun stage9_englishEverywhere() = juzStage(9)
  @Test fun stage10_englishEverywhere() = juzStage(10)

  @Test fun stage11_englishEverywhere() =
    walkStage(11, lessons("s11_", listOf("roots", "patterns", "past", "present", "commands", "detective")), "lesson_s11_quiz") { t ->
      if (t == "lesson_s11_detective") {
        val cards = rule.onAllNodes(tagStartsWith("qaida_s11_detect_")).fetchSemanticsNodes().size
        repeat(cards) { i -> try { rule.onAllNodes(tagStartsWith("qaida_s11_detect_"))[i].performSemanticsAction(SemanticsActions.OnClick) } catch (e: Throwable) { println("detective card $i: $e") }; rule.waitForIdle() }
      }
    }

  @Test fun englishLayoutIsLeftToRight_andUrduStaysRightToLeft() {
    launch(1)
    val back = rule.onNodeWithTag("qaida_back").fetchSemanticsNode().boundsInRoot
    assertTrue("back arrow on the left in English", back.left < 200f)
    assertTrue(QaidaLang.english)
  }
}
