package com.example.uat

import android.app.Application
import android.os.Looper
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
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

  // Watchdog: if a flow test stalls, print every thread's stack once so CI shows where.
  private val watchdog = Thread {
    try { Thread.sleep(180_000) } catch (_: InterruptedException) { return@Thread }
    System.err.println("=== UatFlowTest watchdog: stalled for 3 minutes, thread dump ===")
    Thread.getAllStackTraces().forEach { (t, st) ->
      System.err.println("--- ${t.name}"); st.take(40).forEach { System.err.println("    at $it") }
    }
  }.apply { isDaemon = true; start() }

  @org.junit.After fun stopWatchdog() = watchdog.interrupt()

  private fun log(msg: String) = System.err.println("[flow ${System.currentTimeMillis() % 100000}] $msg")

  private fun step(ms: Long = 1_500) {
    repeat((ms / 100).toInt()) {
      rule.mainClock.advanceTimeBy(100)
      shadowOf(Looper.getMainLooper()).idle()
      Thread.sleep(15) // let Room's background executor finish
    }
  }

  private fun shot(name: String) = rule.onRoot().captureRoboImage(filePath = "build/outputs/roborazzi/flow/$name.png")

  private val knownScreens = listOf("splash_screen_container", "child_setup_screen", "home_dashboard_screen",
    "dua_section_screen", "learning_path_screen", "parent_panel_root")

  private fun waitTag(tag: String, timeoutMs: Long = 15_000) {
    val end = System.currentTimeMillis() + timeoutMs
    while (System.currentTimeMillis() < end) {
      if (rule.onAllNodes(hasTestTag(tag), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()) return
      step(200)
    }
    val onScreen = knownScreens.filter { rule.onAllNodes(hasTestTag(it), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() }
    try { shot("FAILED_waiting_for_$tag") } catch (_: Throwable) {}
    throw AssertionError("Timed out waiting for testTag '$tag'; screens present: $onScreen")
  }

  // Click through the semantics action: no scrolling needed (scrolling the long dashboard under a paused clock stalled).
  private fun SemanticsNodeInteraction.tap(): SemanticsNodeInteraction {
    val clickable = fetchSemanticsNode().config.contains(androidx.compose.ui.semantics.SemanticsActions.OnClick)
    if (clickable) performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.OnClick) else performClick()
    return this
  }

  private fun clickTag(tag: String) { rule.onNode(hasTestTag(tag)).tap(); step() }

  private fun clickText(text: String) {
    rule.onAllNodes(hasText(text, substring = true), useUnmergedTree = false)[0].tap()
    step()
  }

  /** The dashboard opens an "App Guide" dialog on first visit; close it if shown. */
  private fun dismissGuide() {
    if (rule.onAllNodes(hasText("Got it!")).fetchSemanticsNodes().isNotEmpty()) clickText("Got it!")
  }

  private fun goBack() { rule.runOnUiThread { rule.activity.onBackPressedDispatcher.onBackPressed() }; step() }

  @Test fun firstRun_createProfile_thenVisitEverySection() {
    rule.mainClock.autoAdvance = false
    waitTag("splash_screen_container"); shot("01_splash")

    clickTag("submit_button"); step(2_000)
    // New installs go to profile setup; if a profile already exists the app goes straight to the dashboard.
    if (rule.onAllNodes(hasTestTag("child_setup_screen"), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()) {
      shot("02_setup")
      rule.onNode(hasTestTag("username_input")).performTextInput("Aisha")
      clickTag("save_profile_button"); step(3_000)
    }
    waitTag("home_dashboard_screen"); dismissGuide(); shot("03_dashboard")

    clickText("Daily Duas"); waitTag("dua_section_screen"); shot("04_duas"); goBack()
    waitTag("home_dashboard_screen"); dismissGuide()
    clickText("Quran Explorer"); step(2_000); shot("05_quran"); goBack()
    waitTag("home_dashboard_screen")
    waitTag("home_dashboard_screen"); dismissGuide()
    clickTag("lang_ur_button"); shot("06_after_urdu_toggle")
    log("tap stage 2 card"); clickTag("dashboard_qaida_stage2"); log("tapped"); step(1_500); log("stepped")
    shot("06b_qaida_stage2"); log("shot"); goBack(); log("back")
    waitTag("home_dashboard_screen"); dismissGuide()
    // Urdu mode adds the Qaida stage cards and pushes the English games further down: switch back first.
    clickText("English"); step(1_000); dismissGuide()
    // English mode: the Quran course taught in English, then the English reading course (they replaced the old games).
    clickTag("dashboard_quran_en_stage1"); step(1_500); waitTag("qaida_root"); shot("07_quran_english_stage1"); goBack()
    waitTag("home_dashboard_screen"); dismissGuide()
    clickTag("dashboard_english_stage1"); step(1_500); waitTag("english_root"); shot("07b_english_reading_stage1"); goBack()
    waitTag("home_dashboard_screen"); dismissGuide()
    clickText("My Stickers"); step(2_000); shot("07c_stickers"); goBack()
  }

  @Test fun saveProfile_doubleTap_createsOnlyOneProfile() {
    rule.mainClock.autoAdvance = false
    waitTag("splash_screen_container")
    clickTag("submit_button")
    waitTag("child_setup_screen")
    rule.onNode(hasTestTag("username_input")).performTextInput("Adam")
    val save = rule.onNode(hasTestTag("save_profile_button"))
    try { save.performScrollTo() } catch (_: AssertionError) {}
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
