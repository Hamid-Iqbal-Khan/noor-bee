package com.example

import android.speech.tts.TextToSpeech
import java.util.Locale
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.data.ChildProfile
import com.example.ui.NoorBeeViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: NoorBeeViewModel by viewModels()
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
            }
        }
        
        // Edge-to-edge support is mandatory under specifications
        enableEdgeToEdge()

        setContent {
            // Read dynamic parent config
            val config by viewModel.parentConfig.collectAsState()
            
            // Mirror coordinates for RTL support if activated
            val layoutDirection = if (config.arabicRTLMirror) {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                MyApplicationTheme {
                    val navController = rememberNavController()

                    // Collect reactive flows from Room View Model
                    val activeUser by viewModel.activeUser.collectAsState()
                    val activeProfile by viewModel.currentProfile.collectAsState()
                    val allProfiles by viewModel.allProfiles.collectAsState()
                    val progressHistory by viewModel.currentProfileProgress.collectAsState()
                    val unlockedOutfits by viewModel.currentProfileOutfits.collectAsState()
                    val selectedWord by viewModel.selectedWord.collectAsState()
                    val quizQuestions by viewModel.quizQuestions.collectAsState()
                    val quizAnswers by viewModel.quizAnswers.collectAsState()
                    val currentQuestionIdx by viewModel.currentQuestionIndex.collectAsState()
                    val quizScore by viewModel.quizScore.collectAsState()
                    val quizAnswerChecked by viewModel.quizAnswerChecked.collectAsState()
                    val stickersOnPlayground by viewModel.stickersOnPlayground.collectAsState()
                    val mascotColor by viewModel.mascotColor.collectAsState()

                    val equippedOutfitId = unlockedOutfits.firstOrNull { it.isEquipped }?.outfitId ?: "classic_bee"

                    CompositionLocalProvider(LocalNoorBeeStyle provides NoorBeeStyle(equippedOutfitId = equippedOutfitId, mascotColor = mascotColor)) {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "splash",
                            modifier = Modifier.padding(innerPadding),
                            enterTransition = { fadeIn(animationSpec = tween(450)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(450)) },
                            exitTransition = { fadeOut(animationSpec = tween(450)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(450)) },
                            popEnterTransition = { fadeIn(animationSpec = tween(450)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(450)) },
                            popExitTransition = { fadeOut(animationSpec = tween(450)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(450)) }
                        ) {
                            // SPLASH SCREEN
                            composable("splash") {
                                SplashScreen(
                                    onStartClick = {
                                        if (allProfiles.isEmpty()) {
                                            navController.navigate("setup")
                                        } else {
                                            navController.navigate("dashboard")
                                        }
                                    },
                                    onParentSetupClick = {
                                        navController.navigate("parent")
                                    }
                                )
                            }

                            // PROFILE SETUP SCREEN
                            composable("setup") {
                                ChildSetupScreen(
                                    onBackPressed = {
                                        if (allProfiles.isNotEmpty()) navController.popBackStack()
                                        else navController.navigate("splash")
                                    },
                                    onSaveProfile = { name, gender, age, level, avatar ->
                                        viewModel.createChildProfile(name, gender, age, level, avatar)
                                        navController.navigate("dashboard") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // HOME DASHBOARD INTERACTIVE GAME SELECTOR
                            composable("dashboard") {
                                val current = activeProfile ?: ChildProfile(name = "Champion", gender = "Male", level = 3, age = 5)
                                HomeDashboardScreen(
                                    profile = current,
                                    allProfiles = allProfiles,
                                    onSelectProfile = { viewModel.selectProfileFlow(it) },
                                    onUpdateProfile = { viewModel.updateChildProfile(it) },
                                    onCreateProfile = { name, gender, age, level, avatar -> viewModel.createChildProfile(name, gender, age, level, avatar) },
                                    onDeleteProfile = { viewModel.deleteChildProfile(it) },
                                    onTopicSelected = { topic ->
                                        // Navigate to learning path pathway
                                        navController.navigate("path/$topic")
                                    },
                                    onNavigateToLanguagePicker = {
                                        navController.navigate("langpicker")
                                    },
                                    onNavigateToStickerRoom = {
                                        navController.navigate("rewards/stickers")
                                    },
                                    onNavigateToParentPanel = {
                                        navController.navigate("parent")
                                    },
                                    onNavigateToDua = {
                                        navController.navigate("dua")
                                    },
                                    onNavigateToQuran = {
                                        navController.navigate("quran")
                                    },
                                    onNavigateToQaida = { stage ->
                                        navController.navigate("qaida?stage=$stage")
                                    },
                                    onNavigateToQuranEnglish = { stage ->
                                        navController.navigate("qaida?stage=$stage&lang=en")
                                    },
                                    onNavigateToEnglishCourse = { stage ->
                                        navController.navigate("english?stage=$stage")
                                    },
                                    onOpenArabicTopic = { topic ->
                                        // Arabic content follows the profile's language; switch it, then open the path
                                        viewModel.updateLanguage("Arabic")
                                        navController.navigate("path/$topic")
                                    },
                                    onLevelChanged = { newLevel ->
                                        viewModel.updateLevel(newLevel)
                                    }
                                )
                            }

                            // QURAN LEARNING ADAPTIVE PATH
                            composable("quran") {
                                val current = activeProfile ?: ChildProfile(name = "Champion", gender = "Male", level = 4)
                                QuranScreen(
                                    profile = current,
                                    onBackPressed = { navController.popBackStack() },
                                    onEarnRewards = { coins, stars, desc ->
                                        viewModel.earnCoinsAndStars(coins, stars, desc)
                                    },
                                    onSoundTrigger = { target ->
                                        val isArabic = target.startsWith("text:arabic:")
                                        val wordToSpeak = if (isArabic) {
                                            target.removePrefix("text:arabic:")
                                        } else {
                                            target.removePrefix("text:english:")
                                        }
                                        tts?.let { t ->
                                            if (ttsReady) {
                                                val lang = if (isArabic) Locale("ar", "SA") else Locale("en", "US")
                                                val result = t.setLanguage(lang)
                                                if (isArabic && (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)) {
                                                    t.language = Locale("ar")
                                                } else {
                                                    t.language = lang
                                                }
                                                t.setSpeechRate(config.speechRate)
                                                t.speak(sanitizeSpeechText(wordToSpeak), TextToSpeech.QUEUE_FLUSH, null, null)
                                            }
                                        }
                                    }
                                )
                            }

                            // BASIC DAILY SUPPLICATIONS (DUA) FOR AGE 3+
                            composable("dua") {
                                val current = activeProfile ?: ChildProfile(name = "Champion", gender = "Male", level = 3)
                                DuaScreens(
                                    profile = current,
                                    onBackPressed = { navController.popBackStack() },
                                    onEarnRewards = { coins, stars, desc ->
                                        viewModel.earnCoinsAndStars(coins, stars, desc)
                                    },
                                    onSoundTrigger = { target ->
                                        val isArabic = target.startsWith("text:arabic:")
                                        val wordToSpeak = if (isArabic) {
                                            target.removePrefix("text:arabic:")
                                        } else {
                                            target.removePrefix("text:english:")
                                        }
                                        tts?.let { t ->
                                            if (ttsReady) {
                                                val lang = if (isArabic) Locale("ar", "SA") else Locale("en", "IN")
                                                val result = t.setLanguage(lang)
                                                if (isArabic && (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)) {
                                                    t.language = Locale("ar")
                                                } else {
                                                    t.language = lang
                                                }
                                                t.setSpeechRate(config.speechRate)
                                                t.speak(sanitizeSpeechText(wordToSpeak), TextToSpeech.QUEUE_FLUSH, null, null)
                                            }
                                        }
                                    }
                                )
                            }

                            // NOORANI QAIDA — Urdu-medium Arabic course (Stage 1)
                            composable(
                                "qaida?stage={stage}&lang={lang}",
                                arguments = listOf(
                                    androidx.navigation.navArgument("stage") { type = androidx.navigation.NavType.IntType; defaultValue = 1 },
                                    androidx.navigation.navArgument("lang") { type = androidx.navigation.NavType.StringType; defaultValue = "ur" },
                                )
                            ) { entry ->
                                com.example.qaida.QaidaScreen(
                                    profileId = activeProfile?.id ?: 0,
                                    startStage = entry.arguments?.getInt("stage") ?: 1,
                                    english = entry.arguments?.getString("lang") == "en",
                                    onBackPressed = { navController.popBackStack() },
                                    onSpeakArabic = { text ->
                                        // Temporary voice until the qari's recordings are bundled
                                        tts?.let { t ->
                                            if (ttsReady) {
                                                val result = t.setLanguage(Locale("ar", "SA"))
                                                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                                    t.language = Locale("ar")
                                                }
                                                t.setSpeechRate(config.speechRate * 0.8f)
                                                t.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                                            }
                                        }
                                    },
                                    onEarnRewards = { coins, stars, desc -> viewModel.earnCoinsAndStars(coins, stars, desc) }
                                )
                            }

                            // ENGLISH READING COURSE — phonics to sentences, the English partner of the Quran course
                            composable(
                                "english?stage={stage}",
                                arguments = listOf(androidx.navigation.navArgument("stage") { type = androidx.navigation.NavType.IntType; defaultValue = 1 })
                            ) { entry ->
                                com.example.english.EnglishScreen(
                                    profileId = activeProfile?.id ?: 0,
                                    startStage = entry.arguments?.getInt("stage") ?: 1,
                                    onBackPressed = { navController.popBackStack() },
                                    onSpeak = { text ->
                                        tts?.let { t ->
                                            if (ttsReady) {
                                                t.language = Locale("en", "US")
                                                t.setSpeechRate(config.speechRate * 0.85f)
                                                t.speak(sanitizeSpeechText(text), TextToSpeech.QUEUE_FLUSH, null, null)
                                            }
                                        }
                                    },
                                    onEarnRewards = { coins, stars, desc -> viewModel.earnCoinsAndStars(coins, stars, desc) }
                                )
                            }

                            // LANGUAGE EN/AR PICKER
                            composable("langpicker") {
                                val current = activeProfile ?: ChildProfile(name = "Champion", gender = "Male", level = 3)
                                LanguagePickerScreen(
                                    currentLang = current.currentLanguage,
                                    onBackPressed = { navController.popBackStack() },
                                    onLangChosen = { lang ->
                                        viewModel.updateLanguage(lang)
                                        navController.popBackStack()
                                    }
                                )
                            }

                            // ROADMAP CHRONOLOGICAL PATH
                            composable(
                                route = "path/{topic}",
                                arguments = listOf(navArgument("topic") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val topic = backStackEntry.arguments?.getString("topic") ?: "Vocabulary"
                                val current = activeProfile ?: ChildProfile(name = "Champion", gender = "Male", level = 3)
                                LearningPathScreens(
                                    profile = current,
                                    topicGroup = topic,
                                    onBackPressed = { navController.popBackStack() },
                                    onLevelSelected = { subtopicId ->
                                        // Initialize the session words for this category/subtopic
                                        viewModel.startSessionForSubtopic(subtopicId)

                                        // Route into active interactive game exercises
                                        val activityType = when {
                                            subtopicId.contains("intro") -> "intro"
                                            subtopicId.contains("matching") -> "match"
                                            subtopicId.contains("listening") -> "listening"
                                            subtopicId.contains("quiz") -> {
                                                viewModel.startNewQuiz()
                                                "quiz"
                                            }
                                            subtopicId.contains("practice") -> {
                                                // Always route the practice milestones directly to our beautiful 
                                                // flippable flashcard layout to learn the words card by card!
                                                "vocab"
                                            }
                                            else -> "vocab"
                                        }
                                        navController.navigate("activity/$activityType")
                                    }
                                )
                            }

                            // INTERACTIVE GUEST ACTIVITIES
                            composable(
                                route = "activity/{type}",
                                arguments = listOf(navArgument("type") { type = NavType.StringType })
                            ) { backStack ->
                                val type = backStack.arguments?.getString("type") ?: "intro"
                                val current = activeProfile ?: ChildProfile(name = "Champion", gender = "Male", level = 3)
                                ActivityScreens(
                                    profile = current,
                                    activityType = type,
                                    selectedWord = selectedWord,
                                    quizQuestions = quizQuestions,
                                    quizAnswers = quizAnswers,
                                    currentQuestionIndex = currentQuestionIdx,
                                    quizScore = quizScore,
                                    quizAnswerChecked = quizAnswerChecked,
                                    onBackPressed = { navController.popBackStack() },
                                    onNextWordOrBack = {
                                        val hasMore = viewModel.nextSessionWord()
                                        if (!hasMore) {
                                            navController.popBackStack()
                                        }
                                    },
                                    onSoundTrigger = { target ->
                                        // Speak word natively using Text to Speech
                                        val isArabic: Boolean
                                        val wordToSpeak: String
                                        if (target.startsWith("text:arabic:")) {
                                            isArabic = true
                                            wordToSpeak = target.removePrefix("text:arabic:")
                                        } else if (target.startsWith("text:english:")) {
                                            isArabic = false
                                            wordToSpeak = target.removePrefix("text:english:")
                                        } else {
                                            isArabic = current.currentLanguage == "Arabic"
                                            wordToSpeak = if (isArabic) selectedWord.arabic else selectedWord.english
                                        }
                                        tts?.let { t ->
                                            if (ttsReady) {
                                                val lang = if (isArabic) Locale("ar", "SA") else Locale("en", "IN")
                                                val result = t.setLanguage(lang)
                                                if (isArabic && (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)) {
                                                    t.language = Locale("ar")
                                                } else {
                                                    t.language = lang
                                                }
                                                t.setSpeechRate(config.speechRate)
                                                t.speak(sanitizeSpeechText(wordToSpeak), TextToSpeech.QUEUE_FLUSH, null, null)
                                            }
                                        }
                                    },
                                    onSubmitQuizAnswer = { ans ->
                                        viewModel.submitQuizAnswer(ans)
                                    },
                                    onNextQuestion = {
                                        viewModel.nextQuizQuestion()
                                    },
                                    onActionComplete = { coins, stars, desc ->
                                        viewModel.earnCoinsAndStars(coins, stars, desc)
                                    },
                                    onResetQuiz = {
                                        viewModel.startNewQuiz()
                                    }
                                )
                            }

                            // REWARDS SECTION: STICKER PLAYGROUND, WARDROBE OR TROPHIES
                            composable(
                                route = "rewards/{section}",
                                arguments = listOf(navArgument("section") { type = NavType.StringType })
                            ) { backStack ->
                                val section = backStack.arguments?.getString("section") ?: "wardrobe"
                                val current = activeProfile ?: ChildProfile(name = "Champion", gender = "Male", level = 3)
                                RewardsScreens(
                                    profile = current,
                                    section = section,
                                    unlockedOutfits = unlockedOutfits,
                                    stickersOnPlayground = stickersOnPlayground,
                                    onBackPressed = { navController.popBackStack() },
                                    onAddSticker = { sticker, px, py ->
                                        viewModel.addStickerToPlayground(sticker, px, py)
                                    },
                                    onClearStickers = {
                                        viewModel.clearStickers()
                                    },
                                    onBuyOutfit = { outfitId, cost ->
                                        viewModel.unlockAndEquipOutfit(outfitId, cost)
                                    },
                                    onEquipOutfit = { outfitId ->
                                        viewModel.equipOutfit(outfitId)
                                    },
                                    currentBeeColor = mascotColor,
                                    onUpdateBeeColor = { color ->
                                        viewModel.updateMascotColor(color)
                                    },
                                    onSoundTrigger = { target ->
                                        val isArabic = target.startsWith("text:arabic:")
                                        val wordToSpeak = if (isArabic) {
                                            target.removePrefix("text:arabic:")
                                        } else {
                                            target.removePrefix("text:english:")
                                        }
                                        tts?.let { t ->
                                            if (ttsReady) {
                                                val lang = if (isArabic) Locale("ar", "SA") else Locale("en", "IN")
                                                val result = t.setLanguage(lang)
                                                if (isArabic && (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)) {
                                                    t.language = Locale("ar")
                                                } else {
                                                    t.language = lang
                                                }
                                                t.setSpeechRate(config.speechRate)
                                                t.speak(sanitizeSpeechText(wordToSpeak), TextToSpeech.QUEUE_FLUSH, null, null)
                                            }
                                        }
                                    }
                                )
                            }

                            // PARENT PANEL
                            composable("parent") {
                                val activeUserVal by viewModel.activeUser.collectAsState()
                                val allUsersVal by viewModel.allUsers.collectAsState()
                                ParentPanelScreens(
                                    allUsers = allUsersVal,
                                    activeProfile = activeProfile,
                                    allProfiles = allProfiles,
                                    config = config,
                                    progressHistory = progressHistory,
                                    activeUser = activeUserVal,
                                    onBackPressed = { navController.popBackStack() },
                                    onAddChildClick = {
                                        navController.navigate("setup")
                                    },
                                    onSelectActiveProfile = { prof ->
                                        viewModel.selectProfileFlow(prof)
                                    },
                                    onSpeechRateChange = { rate ->
                                        viewModel.updateSpeechRate(rate)
                                    },
                                    onToggleRTL = { toggled ->
                                        viewModel.toggleRTLMirror(toggled)
                                    },
                                    onUpgradePremium = {
                                        viewModel.purchasePremiumTier()
                                    },
                                    onLogout = {
                                        viewModel.logout()
                                        navController.navigate("splash") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        tts?.shutdown()
        super.onDestroy()
    }
}

fun sanitizeSpeechText(text: String): String {
    var clean = text
    // Replace (PBUH) or (PUBH) with parentheses/brackets or without, fully case-insensitive
    clean = clean.replace(Regex("(?i)\\(?p[bu]h\\)?"), " Peace Be Upon Him ")
    // Replace sequences of underscores (possibly separated by spaces, like "_ _ _ _" or "____" or "_ _") with a single "dash"
    clean = clean.replace(Regex("(_\\s*)+"), " dash ")
    // Replace sequences of hyphens/dashes (2 or more, possibly separated by spaces, like "--" or "- -") with "dash"
    clean = clean.replace(Regex("(-\\s*){2,}"), " dash ")
    // Replace multiple spaces with a single space
    clean = clean.replace(Regex("\\s+"), " ")
    return clean.trim()
}
