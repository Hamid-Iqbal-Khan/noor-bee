package com.example.uat

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.ChildProfile
import com.example.data.ParentConfig
import com.example.ui.screens.ChildSetupScreen
import com.example.ui.screens.DuaScreens
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.LanguagePickerScreen
import com.example.ui.screens.LearningPathScreens
import com.example.ui.screens.ParentPanelScreens
import com.example.ui.screens.QuranScreen
import com.example.ui.screens.RewardsScreens
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * UAT visual sweep: renders every top-level screen at small-phone, phone,
 * landscape-phone and tablet sizes and records a screenshot of each
 * (build/outputs/roborazzi -> attached to the CI release). A screen that throws
 * while composing fails its test, so this also acts as a crash check.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class UatScreensTest {

  @get:Rule val rule = createComposeRule()

  private val child = ChildProfile(id = 1, name = "Aisha", gender = "Female", level = 3, age = 5, stars = 120, coins = 45)

  private fun shoot(name: String, content: @Composable () -> Unit) {
    rule.mainClock.autoAdvance = false
    rule.setContent { MyApplicationTheme { content() } }
    rule.mainClock.advanceTimeBy(2_000)
    rule.onRoot().captureRoboImage(filePath = "build/outputs/roborazzi/uat/$name.png")
  }

  private fun allScreens(size: String) = listOf<Pair<String, @Composable () -> Unit>>(
    "01_splash" to { SplashScreen(onStartClick = {}, onParentSetupClick = {}) },
    "02_setup" to { ChildSetupScreen(onBackPressed = {}, onSaveProfile = { _, _, _, _, _ -> }) },
    "03_dashboard" to {
      HomeDashboardScreen(profile = child, onTopicSelected = {}, onNavigateToLanguagePicker = {},
        onNavigateToStickerRoom = {}, onNavigateToParentPanel = {}, onNavigateToDua = {}, onNavigateToQuran = {})
    },
    "04_path_alphabet" to { LearningPathScreens(profile = child, topicGroup = "Alphabet", onBackPressed = {}, onLevelSelected = {}) },
    "05_path_vocabulary" to { LearningPathScreens(profile = child, topicGroup = "Vocabulary", onBackPressed = {}, onLevelSelected = {}) },
    "06_duas" to { DuaScreens(profile = child, onBackPressed = {}, onEarnRewards = { _, _, _ -> }, onSoundTrigger = {}) },
    "07_quran" to { QuranScreen(profile = child, onBackPressed = {}, onEarnRewards = { _, _, _ -> }, onSoundTrigger = {}) },
    "08_language" to { LanguagePickerScreen(currentLang = "English", onBackPressed = {}, onLangChosen = {}) },
    "09_rewards_stickers" to {
      RewardsScreens(profile = child, section = "stickers", unlockedOutfits = emptyList(), stickersOnPlayground = emptyList(),
        onBackPressed = {}, onAddSticker = { _, _, _ -> }, onClearStickers = {}, onBuyOutfit = { _, _ -> }, onEquipOutfit = {})
    },
    "10_rewards_wardrobe" to {
      RewardsScreens(profile = child, section = "wardrobe", unlockedOutfits = emptyList(), stickersOnPlayground = emptyList(),
        onBackPressed = {}, onAddSticker = { _, _, _ -> }, onClearStickers = {}, onBuyOutfit = { _, _ -> }, onEquipOutfit = {})
    },
    "11_parent_panel" to {
      ParentPanelScreens(activeProfile = child, allProfiles = listOf(child), config = ParentConfig(), progressHistory = emptyList(),
        onBackPressed = {}, onAddChildClick = {}, onSelectActiveProfile = {}, onSpeechRateChange = {}, onToggleRTL = {}, onUpgradePremium = {})
    },
  ).map { (n, c) -> "${size}_$n" to c }

  // One test per (device, screen) keeps failures isolated and screenshots named.
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.SmallPhone) fun smallPhone_splash() = run("small", 0)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.SmallPhone) fun smallPhone_setup() = run("small", 1)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.SmallPhone) fun smallPhone_dashboard() = run("small", 2)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.SmallPhone) fun smallPhone_pathAlphabet() = run("small", 3)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.SmallPhone) fun smallPhone_pathVocab() = run("small", 4)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.SmallPhone) fun smallPhone_duas() = run("small", 5)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.SmallPhone) fun smallPhone_quran() = run("small", 6)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.SmallPhone) fun smallPhone_language() = run("small", 7)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.SmallPhone) fun smallPhone_stickers() = run("small", 8)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.SmallPhone) fun smallPhone_wardrobe() = run("small", 9)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.SmallPhone) fun smallPhone_parent() = run("small", 10)

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8) fun phone_splash() = run("phone", 0)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8) fun phone_setup() = run("phone", 1)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8) fun phone_dashboard() = run("phone", 2)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8) fun phone_pathAlphabet() = run("phone", 3)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8) fun phone_pathVocab() = run("phone", 4)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8) fun phone_duas() = run("phone", 5)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8) fun phone_quran() = run("phone", 6)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8) fun phone_language() = run("phone", 7)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8) fun phone_stickers() = run("phone", 8)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8) fun phone_wardrobe() = run("phone", 9)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.Pixel8) fun phone_parent() = run("phone", 10)

  @Test @Config(qualifiers = "w891dp-h411dp-land-420dpi") fun land_dashboard() = run("land", 2)
  @Test @Config(qualifiers = "w891dp-h411dp-land-420dpi") fun land_duas() = run("land", 5)
  @Test @Config(qualifiers = "w891dp-h411dp-land-420dpi") fun land_quran() = run("land", 6)

  @Test @Config(qualifiers = RobolectricDeviceQualifiers.MediumTablet) fun tablet_splash() = run("tablet", 0)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.MediumTablet) fun tablet_dashboard() = run("tablet", 2)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.MediumTablet) fun tablet_pathAlphabet() = run("tablet", 3)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.MediumTablet) fun tablet_duas() = run("tablet", 5)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.MediumTablet) fun tablet_quran() = run("tablet", 6)
  @Test @Config(qualifiers = RobolectricDeviceQualifiers.MediumTablet) fun tablet_parent() = run("tablet", 10)

  private fun run(size: String, index: Int) {
    val (name, content) = allScreens(size)[index]
    shoot(name, content)
  }
}
