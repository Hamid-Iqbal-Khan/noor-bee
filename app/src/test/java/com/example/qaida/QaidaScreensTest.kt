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

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8)
  fun phone_stage4_allLessons() {
    launch(stage = 4); shot("phone_s4_01_home")
    listOf("stop", "signs", "qalqalah", "kawthar", "falaq", "nas").forEachIndexed { i, b ->
      rule.onNodeWithTag("lesson_s4_$b").performScrollToSafe(); rule.onNodeWithTag("lesson_s4_$b").performClick(); rule.waitForIdle()
      shot("phone_s4_0${i + 2}_$b")
      rule.onNodeWithTag("qaida_s4_${b}_list").performScrollToNode(hasTestTag("qaida_s4_done"))
      rule.onNodeWithTag("qaida_s4_done").performClick(); rule.waitForIdle()
    }
    assertTrue("six stage-4 lessons rewarded", rewards == 6)
    rule.onNodeWithTag("lesson_s4_quiz").performScrollToSafe(); rule.onNodeWithTag("lesson_s4_quiz").performClick(); rule.waitForIdle()
    shot("phone_s4_08_quiz")
    rule.onNodeWithTag("qaida_back").performClick(); rule.waitForIdle()
    rule.onNodeWithTag("qaida_stage_tab_1").performClick(); rule.waitForIdle()
    rule.onNodeWithTag("lesson_letters").assertExists()
  }

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8)
  fun phone_stage5_allLessons() {
    launch(stage = 5); shot("phone_s5_01_home")
    listOf("words", "endings", "fatiha", "short", "falaq").forEachIndexed { i, b ->
      rule.onNodeWithTag("lesson_s5_$b").performScrollToSafe(); rule.onNodeWithTag("lesson_s5_$b").performClick(); rule.waitForIdle()
      shot("phone_s5_0${i + 2}_$b")
      rule.onNodeWithTag("qaida_s5_${b}_list").performScrollToNode(hasTestTag("qaida_s5_done"))
      rule.onNodeWithTag("qaida_s5_done").performClick(); rule.waitForIdle()
    }
    assertTrue("five stage-5 lessons rewarded", rewards == 5)
    rule.onNodeWithTag("lesson_s5_fatiha").performScrollToSafe(); rule.onNodeWithTag("lesson_s5_fatiha").performClick(); rule.waitForIdle()
    rule.onNodeWithTag("qaida_wbw_1_2_3").performClick(); rule.waitForIdle()
    assertTrue("word رَبِّ spoken", spoken.contains(QaidaStage5.fatiha.ayat[1].words[2]))
    rule.onNodeWithTag("qaida_back").performClick(); rule.waitForIdle()
    rule.onNodeWithTag("lesson_s5_quiz").performScrollToSafe(); rule.onNodeWithTag("lesson_s5_quiz").performClick(); rule.waitForIdle()
    shot("phone_s5_07_quiz")
  }

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8)
  fun phone_stage6_lessonsAndCards() {
    launch(stage = 6); shot("phone_s6_01_home")
    listOf("fil", "maun", "kafirun", "masad").forEachIndexed { i, b ->
      rule.onNodeWithTag("lesson_s6_$b").performScrollToSafe(); rule.onNodeWithTag("lesson_s6_$b").performClick(); rule.waitForIdle()
      shot("phone_s6_0${i + 2}_$b")
      rule.onNodeWithTag("qaida_s6_${b}_list").performScrollToNode(hasTestTag("qaida_s6_done"))
      rule.onNodeWithTag("qaida_s6_done").performClick(); rule.waitForIdle()
    }
    assertTrue("four stage-6 lessons rewarded", rewards == 4)
    rule.onNodeWithTag("lesson_s6_cards").performScrollToSafe(); rule.onNodeWithTag("lesson_s6_cards").performClick(); rule.waitForIdle()
    shot("phone_s6_06_card_front")
    rule.onNodeWithTag("qaida_card").performClick(); rule.waitForIdle()
    rule.onNodeWithTag("qaida_card_meaning", useUnmergedTree = true).assertExists(); shot("phone_s6_07_card_back")
    rule.onNodeWithTag("qaida_card_knew").performClick(); rule.waitForIdle()   // card 1 already flipped
    repeat(14) {
      rule.onNodeWithTag("qaida_card").performClick(); rule.waitForIdle()
      rule.onNodeWithTag(if (it % 2 == 0) "qaida_card_knew" else "qaida_card_notyet").performClick(); rule.waitForIdle()
    }
    rule.onNodeWithTag("qaida_cards_result").assertExists(); shot("phone_s6_08_cards_result")
  }

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8)
  fun phone_stage7_allLessons() {
    launch(stage = 7); shot("phone_s7_01_home")
    listOf("izhar", "idgham", "iqlab", "ikhfa", "meem", "spot").forEachIndexed { i, b ->
      rule.onNodeWithTag("lesson_s7_$b").performScrollToSafe(); rule.onNodeWithTag("lesson_s7_$b").performClick(); rule.waitForIdle()
      if (b == "spot") {
        rule.onNodeWithTag("qaida_s7_spot_list").performScrollToNode(hasTestTag("qaida_s7_spot_s7_izhar_anamta"))
        rule.onNodeWithTag("qaida_s7_spot_s7_izhar_anamta").performClick(); rule.waitForIdle()
      }
      shot("phone_s7_0${i + 2}_$b")
      rule.onNodeWithTag("qaida_s7_${b}_list").performScrollToNode(hasTestTag("qaida_s7_done"))
      rule.onNodeWithTag("qaida_s7_done").performClick(); rule.waitForIdle()
    }
    assertTrue("six stage-7 lessons rewarded", rewards == 6)
    rule.onNodeWithTag("lesson_s7_quiz").performScrollToSafe(); rule.onNodeWithTag("lesson_s7_quiz").performClick(); rule.waitForIdle()
    shot("phone_s7_08_quiz")
  }

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8)
  fun phone_parentProgress() {
    val ctx = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
    QaidaProgress(ctx, 7).apply {
      listOf("1.1", "1.2", "1.3", "2.1").forEach { markLessonDone(it) }
      recordQuizScore(9, stage = 1); recordQuizScore(6, stage = 2)
    }
    val kids = listOf(com.example.data.ChildProfile(id = 7, name = "Ammar", age = 3), com.example.data.ChildProfile(id = 8, name = "Aisha"))
    rule.setContent { MyApplicationTheme { QaidaParentProgress(kids) } }
    rule.waitForIdle()
    rule.onNodeWithTag("parent_qaida_child_7").assertExists()
    rule.onNodeWithTag("parent_qaida_next_7", useUnmergedTree = true).assertExists()
    val next = QaidaCourse.next(QaidaProgress(ctx, 7))!!
    org.junit.Assert.assertEquals(2, next.first.number); org.junit.Assert.assertEquals("Muqatta'at", next.second)
    org.junit.Assert.assertTrue(QaidaCourse.status(QaidaProgress(ctx, 7))[0].passed)
    shot("phone_parent_progress")
  }

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8)
  fun phone_juzAmmaStages() {
    launch(stage = 8); shot("phone_s8_01_home")
    rule.onNodeWithTag("lesson_juz_104").performClick(); rule.waitForIdle(); shot("phone_s8_02_humazah")
    rule.onNodeWithTag("qaida_wbw_104_1_1").performClick(); rule.waitForIdle()
    assertTrue("first word spoken", spoken.isNotEmpty())
    rule.onNodeWithTag("qaida_juz_104_list").performScrollToNode(hasTestTag("qaida_juz_done"))
    rule.onNodeWithTag("qaida_juz_done").performClick(); rule.waitForIdle()
    assertTrue("surah rewarded", rewards == 1)
    rule.onNodeWithTag("qaida_stage_tab_10").performScrollToSafe(); rule.onNodeWithTag("qaida_stage_tab_10").performClick(); rule.waitForIdle()
    shot("phone_s10_01_home")
    rule.onNodeWithTag("lesson_juz_78").performScrollToSafe(); rule.onNodeWithTag("lesson_juz_78").performClick(); rule.waitForIdle()
    shot("phone_s10_02_naba")
    rule.onNodeWithTag("qaida_back").performClick(); rule.waitForIdle()
    rule.onNodeWithTag("lesson_juz_quiz_10").performScrollToSafe(); rule.onNodeWithTag("lesson_juz_quiz_10").performClick(); rule.waitForIdle()
    shot("phone_s10_03_quiz")
  }

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8)
  fun phone_stage11_grammar() {
    launch(stage = 11); shot("phone_s11_01_home")
    listOf("roots", "patterns", "past", "present", "commands", "detective").forEachIndexed { i, b ->
      rule.onNodeWithTag("lesson_s11_$b").performScrollToSafe(); rule.onNodeWithTag("lesson_s11_$b").performClick(); rule.waitForIdle()
      shot("phone_s11_0${i + 2}_$b")
      rule.onNodeWithTag("qaida_s11_${b}_list").performScrollToNode(hasTestTag("qaida_s11_done"))
      rule.onNodeWithTag("qaida_s11_done").performClick(); rule.waitForIdle()
    }
    assertTrue("six grammar lessons rewarded", rewards == 6)
    rule.onNodeWithTag("lesson_s11_quiz").performScrollToSafe(); rule.onNodeWithTag("lesson_s11_quiz").performClick(); rule.waitForIdle()
    shot("phone_s11_08_quiz")
  }
}
