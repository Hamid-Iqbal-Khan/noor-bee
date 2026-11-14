package com.example.uat

import android.app.Application
import android.os.Looper
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.MainActivity
import com.example.data.NoorBeeDatabase
import com.example.ui.NoorBeeViewModel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.rules.ExternalResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** End-to-end journeys through the real MainActivity (Room DB, navigation, ViewModel). */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class UatFlowTest {

  // Fresh database for every test (Robolectric keeps statics between tests in one sandbox).
  @get:Rule(order = 0) val freshDb = FreshDatabaseRule()
  @get:Rule(order = 1) val rule = createAndroidComposeRule<MainActivity>()

  private fun step(ms: Long = 1_500) {
    repeat((ms / 100).toInt()) {
      rule.mainClock.advanceTimeBy(100)
      shadowOf(Looper.getMainLooper()).idle()
      Thread.sleep(15) // let Room's background executor finish
    }
  }

  private fun shot(name: String) = rule.onRoot().captureRoboImage(filePath = "build/outputs/roborazzi/flow/$name.png")

  private fun waitTag(tag: String, timeoutMs: Long = 10_000) {
    val end = System.currentTimeMillis() + timeoutMs
    while (System.currentTimeMillis() < end) {
      if (rule.onAllNodes(hasTestTag(tag), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()) return
      step(200)
    }
    throw AssertionError("Timed out waiting for testTag '$tag'")
  }

  private fun clickText(text: String) {
    rule.onAllNodes(hasText(text, substring = true), useUnmergedTree = false)[0].performScrollTo().performClick()
    step()
  }

  private fun goBack() { rule.runOnUiThread { rule.activity.onBackPressedDispatcher.onBackPressed() }; step() }

  @Test fun firstRun_createProfile_thenVisitEverySection() {
    rule.mainClock.autoAdvance = false
    waitTag("splash_screen_container"); shot("01_splash")

    rule.onNode(hasTestTag("submit_button")).performScrollTo().performClick(); step()
    waitTag("child_setup_screen"); shot("02_setup")

    rule.onNode(hasTestTag("username_input")).performTextInput("Aisha")
    rule.onNode(hasTestTag("save_profile_button")).performScrollTo().performClick(); step(3_000)
    waitTag("home_dashboard_screen"); shot("03_dashboard")

    clickText("Daily Duas"); waitTag("dua_section_screen"); shot("04_duas"); goBack()
    waitTag("home_dashboard_screen")
    clickText("Quran Explorer"); step(2_000); shot("05_quran"); goBack()
    waitTag("home_dashboard_screen")
    rule.onNode(hasTestTag("lang_ar_button")).performScrollTo().performClick(); step(); shot("06_after_arabic_toggle")
    clickText("Phonics Whiteboard"); step(2_000); shot("07_whiteboard"); goBack()
  }

  @Test fun saveProfile_doubleTap_createsOnlyOneProfile() {
    rule.mainClock.autoAdvance = false
    waitTag("splash_screen_container")
    rule.onNode(hasTestTag("submit_button")).performScrollTo().performClick(); step()
    waitTag("child_setup_screen")
    rule.onNode(hasTestTag("username_input")).performTextInput("Adam")
    val save = rule.onNode(hasTestTag("save_profile_button")).performScrollTo()
    save.performClick(); save.performClick() // impatient double tap
    step(3_000)
    val dao = NoorBeeDatabase.getDatabase(ApplicationProvider.getApplicationContext()).dao
    val count = runBlocking { dao.getAllProfilesFlow().first().size }
    assertEquals("profiles after a double tap on Save", 1, count)
  }
}

/** Settings a parent changes must survive an app restart. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SettingsPersistenceTest {
  @get:Rule val freshDb = FreshDatabaseRule()

  @Test fun parentSettings_surviveAppRestart() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val dao = NoorBeeDatabase.getDatabase(app).dao
    fun settle() = repeat(40) { shadowOf(Looper.getMainLooper()).idle(); Thread.sleep(25) }

    val first = NoorBeeViewModel(app); settle()
    first.updateSpeechRate(0.6f); first.toggleRTLMirror(true); settle()
    assertEquals(0.6f, runBlocking { dao.getParentConfig()!!.speechRate }, 0.001f)

    NoorBeeViewModel(app); settle() // simulates the next app launch
    val after = runBlocking { dao.getParentConfig()!! }
    assertEquals("speech rate after restart", 0.6f, after.speechRate, 0.001f)
    assertEquals("RTL mirror after restart", true, after.arabicRTLMirror)
  }
}

/** Closes and forgets the Room singleton and deletes the DB file before each test. */
class FreshDatabaseRule : ExternalResource() {
  override fun before() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val field = NoorBeeDatabase::class.java.getDeclaredField("INSTANCE").apply { isAccessible = true }
    (field.get(null) as? NoorBeeDatabase)?.close()
    field.set(null, null)
    app.deleteDatabase("noor_bee_database")
  }
}
