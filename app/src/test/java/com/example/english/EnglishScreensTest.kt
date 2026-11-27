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
    EnglishAudio.enabled = false   // every tap reaches the voice callback below
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
  // speaking levels exist when CI has fetched the VOA conversations
  @Test fun stage5() { org.junit.Assume.assumeTrue(EnglishCourse.stages.size >= 5); walk(5) }
  @Test fun stage6() { org.junit.Assume.assumeTrue(EnglishCourse.stages.size >= 6); walk(6) }
  @Test fun stage7() { org.junit.Assume.assumeTrue(EnglishCourse.stages.size >= 7); walk(7) }
  @Test fun stage8() { org.junit.Assume.assumeTrue(EnglishCourse.stages.size >= 8); walk(8) }

  @Test fun listenAndRepeat_andRolePlay() {
    org.junit.Assume.assumeTrue(EnglishCourse.stages.size >= 5)
    launch(5)
    val talk = EnglishCourse.stage(5).lessons.first { it.kind == LessonKind.DIALOGUE }
    open("english_lesson_${talk.id}")
    rule.onNodeWithTag("english_talk_all_${talk.id}_0").performScrollTo().performClick(); rule.waitForIdle()
    val who = talk.dialogue[1].speaker
    rule.onNodeWithTag("english_be_${talk.id}_0_$who").performScrollTo().performClick(); rule.waitForIdle()
    shot("voa_roleplay")
    rule.onNodeWithTag("english_back").performClick(); rule.waitForIdle()
    val rep = EnglishCourse.stage(5).lessons.first { it.kind == LessonKind.REPEAT }
    open("english_lesson_${rep.id}")
    rule.onNodeWithTag("english_repeat_play").performScrollTo().performClick(); rule.waitForIdle()
    assertTrue(spoken.last() == rep.dialogue[0].text)
    rule.onNodeWithTag("english_repeat_next").performScrollTo().performClick(); rule.waitForIdle()
    assertTrue(spoken.last() == rep.dialogue[1].text)
    shot("voa_repeat")
  }
  @Test fun grownUps_canRecordLetterSounds() {
    launch(1); open("english_grownups")
    rule.onNodeWithTag("english_sounds_list").assertExists()
    rule.onNodeWithTag("english_sound_a_record").assertExists(); shot("grownup_sounds")
  }

  @Test fun continueButton_opensNextLesson() {
    launch(2); open("english_continue")
    rule.onNodeWithTag("english_e2.1_list").assertExists()
  }

  @Test fun speakingLevel_vocabAndGrammar() {
    org.junit.Assume.assumeTrue(EnglishCourse.stages.size >= 5)
    launch(5)
    val vocab = EnglishCourse.stage(5).lessons.first { it.kind == LessonKind.VOCAB }
    open("english_lesson_${vocab.id}")
    rule.onNodeWithTag("english_vocab_${vocab.vocab.first().word}").performScrollTo().performClick(); rule.waitForIdle()
    assertTrue(spoken.last() == vocab.vocab.first().word); shot("vocab")
    rule.onNodeWithTag("english_back").performClick(); rule.waitForIdle()
    val grammar = EnglishCourse.stage(5).lessons.first { it.kind == LessonKind.GRAMMAR }
    open("english_lesson_${grammar.id}"); shot("grammar")
  }

  @Test fun credits_listsSources() {
    launch(1); open("english_credits")
    rule.onNodeWithTag("english_credits_list").assertExists(); shot("credits")
  }

  @Test fun tileTap_saysJustThatLetter_andWordSaysTheWord() {
    launch(2); open("english_lesson_e2.1")
    assertTrue(soundOf("c") == "C" && soundOf("a") == "A" && soundOf("sh") == "shh" && soundOf("ck") == "K")
    rule.onNodeWithTag("english_say_cat").performClick(); rule.waitForIdle()
    assertTrue(spoken.last() == "cat")
  }

  @Test fun letterTap_speaksSoundAndKeyword() {
    launch(1); open("english_lesson_e1.1")
    rule.onNodeWithTag("english_letter_s").performScrollTo().performClick(); rule.waitForIdle()
    assertTrue(spoken.last() == "s, as in sun")
  }
}
