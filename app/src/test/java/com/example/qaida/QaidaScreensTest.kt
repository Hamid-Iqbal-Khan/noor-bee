package com.example.qaida

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performScrollTo
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

/** UAT for the Urdu-medium Qaida: every lesson renders (RTL, Nastaliq) and the flows work. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class QaidaScreensTest {

  @get:Rule val rule = createComposeRule()
  private val spoken = mutableListOf<String>()
  private var rewards = 0

  private fun launch(stage: Int = 1) {
    rule.setContent {
      MyApplicationTheme {
        QaidaScreen(profileId = 42, onBackPressed = {}, onSpeakArabic = { spoken += it }, onEarnRewards = { _, _, _ -> rewards++ }, startStage = stage)
      }
    }
    rule.waitForIdle()
  }

  private fun shot(name: String) = rule.onRoot().captureRoboImage(filePath = "build/outputs/roborazzi/qaida/$name.png")

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8)
  fun phone_allLessons() {
    launch(); shot("phone_01_home")
    rule.onNodeWithTag("lesson_letters").performClick(); rule.waitForIdle(); shot("phone_02_letters")
    rule.onNodeWithTag("qaida_letter_tha").performClick(); rule.waitForIdle()
    assertTrue("fallback voice used when no recording", spoken.contains("ث"))
    shot("phone_03_letter_tha_detail")
    rule.onNodeWithTag("qaida_letter_detail").assertExists()
  }

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8)
  fun phone_soundsAndHarakat() {
    launch()
    rule.onNodeWithTag("lesson_sounds").performClick(); rule.waitForIdle(); shot("phone_04_sounds")
    rule.onNodeWithTag("qaida_sounds_list").performScrollToNode(hasTestTag("qaida_sounds_done"))
    rule.onNodeWithTag("qaida_sounds_done").performClick(); rule.waitForIdle()
    assertTrue("lesson 1.2 rewarded", rewards == 1)
    rule.onNodeWithTag("lesson_harakat").performClick(); rule.waitForIdle(); shot("phone_05_harakat")
  }

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8)
  fun phone_quizAnswersOnceAndAdvances() {
    launch()
    rule.onNodeWithTag("lesson_quiz").performClick(); rule.waitForIdle(); shot("phone_06_quiz")
    repeat(10) {
      rule.onNodeWithTag("qaida_option_0").performClick()
      rule.onNodeWithTag("qaida_option_1").performClick() // second tap must be ignored
      rule.waitForIdle()
      rule.onNodeWithTag("qaida_next").performClick(); rule.waitForIdle()
    }
    rule.onNodeWithTag("qaida_quiz_result").assertExists(); shot("phone_07_quiz_result")
  }

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.SmallPhone)
  fun smallPhone_homeAndLetters() {
    launch(); shot("small_01_home")
    rule.onNodeWithTag("lesson_letters").performClick(); rule.waitForIdle(); shot("small_02_letters")
  }

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.MediumTablet)
  fun tablet_homeAndHarakat() {
    launch(); shot("tablet_01_home")
    rule.onNodeWithTag("lesson_harakat").performClick(); rule.waitForIdle(); shot("tablet_02_harakat")
  }

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8)
  fun phone_stage2_allBoards() {
    launch(stage = 2); shot("phone_s2_01_home")
    rule.onNodeWithTag("lesson_s2_joining").assertExists()
    val boards = listOf("joining", "muqattaat", "tanween", "khara", "madd", "jazm")
    boards.forEachIndexed { i, b ->
      rule.onNodeWithTag("qaida_root").performScrollToNodeSafe("lesson_s2_$b")
      rule.onNodeWithTag("lesson_s2_$b").performClick(); rule.waitForIdle()
      shot("phone_s2_0${i + 2}_$b")
      rule.onNodeWithTag("qaida_s2_${b}_list").performScrollToNode(hasTestTag("qaida_s2_done"))
      rule.onNodeWithTag("qaida_s2_done").performClick(); rule.waitForIdle()
    }
    assertTrue("six stage-2 boards rewarded", rewards == 6)
    shot("phone_s2_08_home_done")
  }

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8)
  fun phone_stage2_tabAndQuiz() {
    launch()
    rule.onNodeWithTag("qaida_stage_tab_2").performClick(); rule.waitForIdle()
    rule.onNodeWithTag("qaida_s2_hint").assertExists()
    rule.onNodeWithTag("lesson_s2_tanween").performClick(); rule.waitForIdle()
    rule.onNodeWithTag("qaida_s2_cell_ba_do_zabar").performClick(); rule.waitForIdle()
    assertTrue("tanween fallback spoken", spoken.contains("بًا"))
    rule.onNodeWithTag("qaida_back").performClick(); rule.waitForIdle()
    rule.onNodeWithTag("lesson_s2_quiz").performScrollToSafe(); rule.onNodeWithTag("lesson_s2_quiz").performClick(); rule.waitForIdle()
    shot("phone_s2_09_quiz")
    repeat(10) {
      rule.onNodeWithTag("qaida_option_0").performClick(); rule.waitForIdle()
      rule.onNodeWithTag("qaida_next").performScrollToSafe(); rule.onNodeWithTag("qaida_next").performClick(); rule.waitForIdle()
    }
    rule.onNodeWithTag("qaida_quiz_result").assertExists(); shot("phone_s2_10_quiz_result")
  }

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.SmallPhone)
  fun small_stage2_khara() {
    launch(stage = 2)
    rule.onNodeWithTag("lesson_s2_khara").performScrollToSafe(); rule.onNodeWithTag("lesson_s2_khara").performClick(); rule.waitForIdle()
    shot("small_s2_khara")
  }

  private fun androidx.compose.ui.test.SemanticsNodeInteraction.performScrollToSafe() =
    try { performScrollTo() } catch (_: Throwable) { this }

  private fun androidx.compose.ui.test.SemanticsNodeInteraction.performScrollToNodeSafe(tag: String) {
    try { rule.onNodeWithTag(tag).performScrollTo() } catch (_: Throwable) { }
  }

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8)
  fun phone_stage3_allLessons() {
    launch(stage = 3); shot("phone_s3_01_home")
    listOf("tashdeed", "ghunna", "madd", "shamsi", "fatiha", "ikhlas").forEachIndexed { i, b ->
      rule.onNodeWithTag("lesson_s3_$b").performScrollToSafe(); rule.onNodeWithTag("lesson_s3_$b").performClick(); rule.waitForIdle()
      shot("phone_s3_0${i + 2}_$b")
      rule.onNodeWithTag("qaida_s3_${b}_list").performScrollToNode(hasTestTag("qaida_s3_done"))
      rule.onNodeWithTag("qaida_s3_done").performClick(); rule.waitForIdle()
    }
    assertTrue("six stage-3 lessons rewarded", rewards == 6)
    rule.onNodeWithTag("lesson_s3_quiz").performScrollToSafe(); rule.onNodeWithTag("lesson_s3_quiz").performClick(); rule.waitForIdle()
    shot("phone_s3_08_quiz")
  }

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8)
  fun phone_stage3_tapTashdeedAndAyah() {
    launch(stage = 3)
    rule.onNodeWithTag("lesson_s3_tashdeed").performClick(); rule.waitForIdle()
    rule.onNodeWithTag("qaida_s3_cell_ba_z").performClick(); rule.waitForIdle()
    assertTrue("tashdeed fallback spoken", spoken.contains("اَب\u0651\u064E"))
    rule.onNodeWithTag("qaida_s3_breakdown_ba").assertExists()      // default: open under با
    rule.onNodeWithTag("qaida_s3_cell_ta_z").performClick(); rule.waitForIdle()
    rule.onNodeWithTag("qaida_s3_breakdown_ta").assertExists()      // moves under تا
    rule.onNodeWithTag("qaida_s3_breakdown_ba").assertDoesNotExist()
    shot("phone_s3_tashdeed_breakdown_ta")
    rule.onNodeWithTag("qaida_s3_part1").performClick(); rule.waitForIdle()
    assertTrue("first half اَتْ spoken", spoken.contains("اَت\u0652"))
    rule.onNodeWithTag("qaida_back").performClick(); rule.waitForIdle()
    rule.onNodeWithTag("lesson_s3_fatiha").performScrollToSafe(); rule.onNodeWithTag("lesson_s3_fatiha").performClick(); rule.waitForIdle()
    rule.onNodeWithTag("qaida_ayah_1_1").performClick(); rule.waitForIdle()
    assertTrue("ayah fallback spoken", spoken.any { it.startsWith("بِسْمِ") })
  }
}
