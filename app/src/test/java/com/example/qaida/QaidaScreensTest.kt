package com.example.qaida

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
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

  private fun launch() {
    rule.setContent {
      MyApplicationTheme {
        QaidaScreen(profileId = 42, onBackPressed = {}, onSpeakArabic = { spoken += it }, onEarnRewards = { _, _, _ -> rewards++ })
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
}
