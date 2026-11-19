package com.example.english

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** English reading course: every lesson of every stage opens and scrolls, every practice can be finished. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = RobolectricDeviceQualifiers.Pixel8)
class EnglishScreensTest {

  @get:Rule val rule = createComposeRule()
  private val spoken = mutableListOf<String>()
  private var rewards = 0

  private fun launch(stage: Int) {
    rule.setContent {
      MyApplicationTheme {
        EnglishScreen(profileId = 91, onBackPressed = {}, onSpeak = { spoken += it }, onEarnRewards = { _, _, _ -> rewards++ }, startStage = stage)
      }
    }
    rule.waitForIdle()
  }

  private fun shot(name: String) = rule.onRoot().captureRoboImage(filePath = "build/outputs/roborazzi/english/$name.png")

  private fun open(tag: String) {
    try { rule.onNodeWithTag(tag).performScrollTo() } catch (_: Throwable) { }
    rule.onNodeWithTag(tag).performClick(); rule.waitForIdle()
  }

  private fun walk(stage: Int) {
    launch(stage); shot("stage${stage}_home")
    EnglishCourse.stage(stage).lessons.forEach { l ->
      open("english_lesson_${l.id}"); shot("stage${stage}_${l.id}")
      if (l.kind == LessonKind.CARDS) {
        repeat(15) {
          rule.onNodeWithTag("english_card").performClick(); rule.waitForIdle()
          rule.onNodeWithTag("english_card_knew").performClick(); rule.waitForIdle()
        }
        rule.onNodeWithTag("english_cards_result").assertExists()
      } else {
        val list = rule.onNodeWithTag("english_${l.id}_list")
        var i = 0
        while (i < 200) { try { list.performScrollToIndex(i) } catch (_: Throwable) { break }; rule.waitForIdle(); i++ }
      }
      rule.onNodeWithTag("english_done").performScrollTo().performClick(); rule.waitForIdle()
    }
    open("english_quiz_$stage")
    repeat(10) {
      rule.onNodeWithTag("english_option_0").performScrollTo().performClick(); rule.waitForIdle()
      rule.onNodeWithTag("english_next").performScrollTo().performClick(); rule.waitForIdle()
    }
    rule.onNodeWithTag("english_quiz_result").assertExists(); shot("stage${stage}_quiz_result")
    assertTrue("lessons rewarded", rewards >= EnglishCourse.stage(stage).lessons.size)
  }

  @Test fun stage1() { walk(1); rule.onNodeWithTag("english_back").performClick() }
  @Test fun stage2() = walk(2)
  @Test fun stage3() = walk(3)
  @Test fun stage4() = walk(4)
  @Test fun stage5() = walk(5)
  @Test fun stage6() = walk(6)

  @Test fun tileTap_saysJustThatLetter_andWordSaysTheWord() {
    launch(2); open("english_lesson_e2.1")
    assertTrue(soundOf("R") == "R" && soundOf("a") == "A" && soundOf("b") == "B" && soundOf("jj") == "J")
    rule.onNodeWithTag("english_say_Rab").performClick(); rule.waitForIdle()
    assertTrue(spoken.last() == "Rab")
  }

  @Test fun letterTap_speaksSoundAndKeyword() {
    launch(1); open("english_lesson_e1.1")
    rule.onNodeWithTag("english_letter_s").performScrollTo().performClick(); rule.waitForIdle()
    assertTrue(spoken.last() == "s, as in Salah")
  }
}
