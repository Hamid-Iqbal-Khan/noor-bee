package com.example.qaida

import androidx.activity.compose.BackHandler
import com.example.progress.LessonTracker
import com.example.progress.LocalLessonTracker
import com.example.progress.TrackLesson
import com.example.progress.trackedListState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlin.random.Random
import kotlinx.coroutines.flow.first

// ---------------------------------------------------------------------------------------
// Typography: Urdu in Nastaliq (needs generous line height), Arabic in a Qaida-style Naskh.
// Both fonts are SIL Open Font License (see assets/licenses).
// ---------------------------------------------------------------------------------------
val UrduFont = FontFamily(Font(R.font.noto_nastaliq_urdu))
val QaidaArabicFont = FontFamily(Font(R.font.noto_naskh_arabic))

internal val Ink = Color(0xFF1F2A44)
internal val Quiet = Color(0xFF5B6475)
internal val Green = Color(0xFF1B5E20)
internal val GreenSoft = Color(0xFFE8F5E9)
internal val Amber = Color(0xFFFFF4D6)
internal val AmberLine = Color(0xFFE0A800)
internal val Paper = Color(0xFFFFFCF5)
internal val Heavy = Color(0xFFB23B1E)

@Composable
fun UrduText(
    text: String, modifier: Modifier = Modifier, size: TextUnit = 16.sp, color: Color = Ink,
    weight: FontWeight = FontWeight.Normal, align: TextAlign = TextAlign.Start,
) = if (QaidaLang.english) Text(QaidaLang.tr(text), modifier, color = color, fontSize = size, fontWeight = weight,
        textAlign = align, lineHeight = size * 1.45f)
    else Text(text, modifier, color = color, fontSize = size, fontFamily = UrduFont, fontWeight = weight,
        textAlign = align, lineHeight = size * 2.1f)

@Composable
fun ArabicText(
    text: String, modifier: Modifier = Modifier, size: TextUnit = 32.sp, color: Color = Ink,
    align: TextAlign = TextAlign.Center,
) = Text(if (QaidaLang.looksUrdu(text)) QaidaLang.tr(text) else text, modifier, color = color, fontSize = size, fontFamily = QaidaArabicFont, fontWeight = FontWeight.Bold,
    textAlign = align, lineHeight = size * 1.7f)

/** Arabic always runs right to left, whatever the medium: rows of letters, harakat boards, word chips. */
@Composable
internal fun ArabicFlow(content: @Composable () -> Unit) =
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl, content = content)

/** The direction of the teaching language: right to left for Urdu, left to right for English. */
@Composable
internal fun MediumFlow(content: @Composable () -> Unit) =
    CompositionLocalProvider(LocalLayoutDirection provides (if (QaidaLang.english) LayoutDirection.Ltr else LayoutDirection.Rtl), content = content)

/** Entry point for the Urdu-medium Noorani Qaida (Stage 1). Whole section is right-to-left. */
@Composable
fun QaidaScreen(
    profileId: Int,
    onBackPressed: () -> Unit,
    onSpeakArabic: (String) -> Unit,              // TTS fallback until the qari's recordings are added
    onEarnRewards: (Int, Int, String) -> Unit,
    startStage: Int = 1,
    english: Boolean = false,
) {
    val context = LocalContext.current
    remember(english) { QaidaLang.load(context); QaidaLang.english = english }
    val progress = remember(profileId) { QaidaProgress(context, profileId) }
    val audio = remember { QaidaAudio(context) }
    DisposableEffect(Unit) { onDispose { audio.release() } }
    var page by rememberSaveable { mutableStateOf("home") }
    var stage by rememberSaveable { mutableIntStateOf(startStage.coerceIn(1, STAGE_COUNT)) }
    var refresh by remember { mutableIntStateOf(0) } // bump to re-read progress

    val recordingsPresent = remember { audio.hasRecording(QaidaData.letters.first().audioKey) }
    val juz = remember { QaidaJuzAmma.load(context) }
    remember { QaidaMafhoom.load(context) }
    val wordIndex = remember(juz) { QuranAudio.buildIndex(emptyList(), QuranAudio.courseAyat(juz)) }
    // the open lesson's tracker (set inside TrackLesson below), so every tap counts as "heard"
    val current = remember { arrayOfNulls<LessonTracker>(1) }
    val markDone: (String) -> Unit = { l -> markDone(l); current[0]?.done() }
    val play: (String, String) -> Unit = { key, text ->
        current[0]?.heard(key)
        audio.play(key, QuranAudio.urlForKey(key) ?: wordIndex.urlsFor(text), QuranAudio.speechFor(key, text), onSpeakArabic)
    }

    BackHandler(enabled = page != "home") { page = "home"; refresh++ }

    CompositionLocalProvider(LocalLayoutDirection provides (if (english) LayoutDirection.Ltr else LayoutDirection.Rtl)) {
        Column(Modifier.fillMaxSize().background(Paper).testTag("qaida_root")) {
            val title = if (page.startsWith("j_")) juz[page.removePrefix("j_").toInt()]?.urduName ?: ""
                else if (page.startsWith("jq_")) page.removePrefix("jq_").toInt().let { tx("مرحلہ ${urduDigit(it)} کی مشق", "Stage $it practice") }
                else when (page) {
                "letters" -> "سبق ۱: حروفِ مفردات"
                "sounds" -> "سبق ۲: ملتی جلتی آوازیں"
                "harakat" -> "سبق ۳: حرکات — زبر، زیر، پیش"
                "quiz" -> "مشق"
                "s2_joining" -> "سبق ۱: مرکبات — حروف ملانا"
                "s2_muqattaat" -> "سبق ۲: حروفِ مقطعات"
                "s2_tanween" -> "سبق ۳: تنوین"
                "s2_khara" -> "سبق ۴: کھڑا زبر، کھڑی زیر، اُلٹا پیش"
                "s2_madd" -> "سبق ۵: حروفِ مدّہ و لین"
                "s2_jazm" -> "سبق ۶: جزم"
                "s2_quiz" -> "مرحلہ ۲ کی مشق"
                "s3_tashdeed" -> "سبق ۱: تشدید"
                "s3_ghunna" -> "سبق ۲: غنّہ اور تشدید والے الفاظ"
                "s3_madd" -> "سبق ۳: مدّ کا نشان"
                "s3_shamsi" -> "سبق ۴: حروفِ شمسی و قمری"
                "s3_fatiha" -> "سبق ۵: سورۃ الفاتحہ"
                "s3_ikhlas" -> "سبق ۶: سورۃ الاخلاص"
                "s3_quiz" -> "مرحلہ ۳ کی مشق"
                "s4_stop" -> "سبق ۱: وقف کا طریقہ"
                "s4_signs" -> "سبق ۲: رموزِ اوقاف"
                "s4_qalqalah" -> "سبق ۳: قلقلہ"
                "s4_kawthar" -> "سبق ۴: الکوثر اور العصر"
                "s4_falaq" -> "سبق ۵: سورۃ الفلق"
                "s4_nas" -> "سبق ۶: سورۃ الناس"
                "s4_quiz" -> "مرحلہ ۴ کی مشق"
                "s5_words" -> "سبق ۱: قرآن کے عام الفاظ"
                "s5_endings" -> "سبق ۲: میرا، تیرا، اس کا"
                "s5_fatiha" -> "سبق ۳: الفاتحہ لفظ بہ لفظ"
                "s5_short" -> "سبق ۴: الاخلاص، الکوثر، العصر"
                "s5_falaq" -> "سبق ۵: الفلق اور الناس"
                "s5_quiz" -> "مرحلہ ۵ کی مشق"
                "s6_fil" -> "سبق ۱: الفیل اور قریش"
                "s6_maun" -> "سبق ۲: سورۃ الماعون"
                "s6_kafirun" -> "سبق ۳: الکافرون اور النصر"
                "s6_masad" -> "سبق ۴: سورۃ المسد"
                "s6_cards" -> "سبق ۵: مطلب بتائیں"
                "s6_quiz" -> "مرحلہ ۶ کی مشق"
                "s7_izhar" -> "سبق ۱: اظہار"
                "s7_idgham" -> "سبق ۲: ادغام"
                "s7_iqlab" -> "سبق ۳: اقلاب"
                "s7_ikhfa" -> "سبق ۴: اخفاء"
                "s7_meem" -> "سبق ۵: میم ساکن"
                "s7_spot" -> "سبق ۶: قاعدہ پہچانیں"
                "s7_quiz" -> "مرحلہ ۷ کی مشق"
                "s11_roots" -> "سبق ۱: مادہ (جڑ)"
                "s11_patterns" -> "سبق ۲: سانچے (اوزان)"
                "s11_past" -> "سبق ۳: ماضی — کس نے کیا"
                "s11_present" -> "سبق ۴: مضارع — کون کرتا ہے"
                "s11_commands" -> "سبق ۵: حکم اور 'نہیں'"
                "s11_detective" -> "سبق ۶: مادہ جاسوس"
                "s11_quiz" -> "مرحلہ ۱۱ کی مشق"
                else -> "نورانی قاعدہ"
            }
            QaidaHeader(title, onBack = { if (page == "home") onBackPressed() else { page = "home"; refresh++ } })
            if (!recordingsPresent) TemporaryAudioBanner()
            Box(Modifier.weight(1f)) {
              if (page == "home") current[0] = null
              val inLesson: @Composable (@Composable () -> Unit) -> Unit = { c ->
                  if (page == "home") c() else TrackLesson(profileId, if (english) "Quran (English)" else "Quran (Urdu)", page,
                      (if (page.startsWith("j_") || page.startsWith("jq_")) "" else "${page.substringBefore('_').removePrefix("s").toIntOrNull()?.let { tx("مرحلہ ${urduDigit(it)} · ", "Stage $it · ") } ?: tx("مرحلہ ۱ · ", "Stage 1 · ")}") + tr(title)) {
                      current[0] = LocalLessonTracker.current
                      if (page == "letters") current[0]?.expect(QaidaData.letters.size)
                      c()
                  }
              }
              inLesson {
                when (page) {
                    "letters" -> LettersLesson(progress, play) {
                        markDone(QaidaProgress.LESSON_LETTERS); onEarnRewards(5, 20, "Qaida: 29 letters")
                    }
                    "sounds" -> SoundsLesson(play) {
                        markDone(QaidaProgress.LESSON_SOUNDS); onEarnRewards(5, 20, "Qaida: sound pairs")
                        page = "home"; refresh++
                    }
                    "harakat" -> HarakatLesson(play) {
                        markDone(QaidaProgress.LESSON_HARAKAT); onEarnRewards(5, 20, "Qaida: harakat")
                        page = "home"; refresh++
                    }
                    "quiz" -> QaidaQuizScreen(play, stageNo = 1, generate = { QaidaQuiz.generate(it) }, onFinished = { score ->
                        current[0]?.done(score); progress.recordQuizScore(score)
                        if (score >= QaidaProgress.PASS_MARK) onEarnRewards(10, 50, "Qaida: Stage 1 quiz passed")
                        refresh++
                    }, onDone = { page = "home"; refresh++ })
                    "s2_quiz" -> QaidaQuizScreen(play, stageNo = 2, generate = { QaidaStage2Quiz.generate(it) }, onFinished = { score ->
                        current[0]?.done(score); progress.recordQuizScore(score, stage = 2)
                        if (score >= QaidaProgress.PASS_MARK) onEarnRewards(10, 50, "Qaida: Stage 2 quiz passed")
                        refresh++
                    }, onDone = { page = "home"; refresh++ })
                    "s3_quiz" -> QaidaQuizScreen(play, stageNo = 3, generate = { QaidaStage3Quiz.generate(it) }, onFinished = { score ->
                        current[0]?.done(score); progress.recordQuizScore(score, stage = 3)
                        if (score >= QaidaProgress.PASS_MARK) onEarnRewards(10, 50, "Qaida: Stage 3 quiz passed")
                        refresh++
                    }, onDone = { page = "home"; refresh++ })
                    "s4_quiz" -> QaidaQuizScreen(play, stageNo = 4, generate = { QaidaStage4Quiz.generate(it) }, onFinished = { score ->
                        current[0]?.done(score); progress.recordQuizScore(score, stage = 4)
                        if (score >= QaidaProgress.PASS_MARK) onEarnRewards(10, 50, "Qaida: Stage 4 quiz passed")
                        refresh++
                    }, onDone = { page = "home"; refresh++ })
                    "s5_quiz" -> QaidaQuizScreen(play, stageNo = 5, generate = { QaidaStage5Quiz.generate(it) }, onFinished = { score ->
                        current[0]?.done(score); progress.recordQuizScore(score, stage = 5)
                        if (score >= QaidaProgress.PASS_MARK) onEarnRewards(10, 50, "Qaida: Stage 5 quiz passed")
                        refresh++
                    }, onDone = { page = "home"; refresh++ })
                    "s6_quiz" -> QaidaQuizScreen(play, stageNo = 6, generate = { QaidaStage6Quiz.generate(it) }, onFinished = { score ->
                        current[0]?.done(score); progress.recordQuizScore(score, stage = 6)
                        if (score >= QaidaProgress.PASS_MARK) onEarnRewards(10, 50, "Qaida: Stage 6 quiz passed")
                        refresh++
                    }, onDone = { page = "home"; refresh++ })
                    "s7_quiz" -> QaidaQuizScreen(play, stageNo = 7, generate = { QaidaStage7Quiz.generate(it) }, onFinished = { score ->
                        current[0]?.done(score); progress.recordQuizScore(score, stage = 7)
                        if (score >= QaidaProgress.PASS_MARK) onEarnRewards(10, 50, "Qaida: Stage 7 quiz passed")
                        refresh++
                    }, onDone = { page = "home"; refresh++ })
                    "s11_quiz" -> QaidaQuizScreen(play, stageNo = 11, generate = { QaidaStage11Quiz.generate(it) }, onFinished = { score ->
                        current[0]?.done(score); progress.recordQuizScore(score, stage = 11)
                        if (score >= QaidaProgress.PASS_MARK) onEarnRewards(10, 50, "Qaida: Stage 11 quiz passed")
                        refresh++
                    }, onDone = { page = "home"; refresh++ })
                    "s11_roots", "s11_patterns", "s11_past", "s11_present", "s11_commands", "s11_detective" -> {
                        val lesson = mapOf("s11_roots" to QaidaProgress.S11_ROOTS, "s11_patterns" to QaidaProgress.S11_PATTERNS,
                            "s11_past" to QaidaProgress.S11_PAST, "s11_present" to QaidaProgress.S11_PRESENT,
                            "s11_commands" to QaidaProgress.S11_COMMANDS, "s11_detective" to QaidaProgress.S11_DETECTIVE).getValue(page)
                        val done11: () -> Unit = {
                            markDone(lesson); onEarnRewards(5, 20, "Qaida stage 11: $lesson")
                            page = "home"; refresh++
                        }
                        Stage11Lesson(page, play, done11)
                    }
                    "s7_izhar", "s7_idgham", "s7_iqlab", "s7_ikhfa", "s7_meem", "s7_spot" -> {
                        val lesson = mapOf("s7_izhar" to QaidaProgress.S7_IZHAR, "s7_idgham" to QaidaProgress.S7_IDGHAM,
                            "s7_iqlab" to QaidaProgress.S7_IQLAB, "s7_ikhfa" to QaidaProgress.S7_IKHFA,
                            "s7_meem" to QaidaProgress.S7_MEEM, "s7_spot" to QaidaProgress.S7_SPOT).getValue(page)
                        val done7: () -> Unit = {
                            markDone(lesson); onEarnRewards(5, 20, "Qaida stage 7: $lesson")
                            page = "home"; refresh++
                        }
                        Stage7Lesson(page, play, done7)
                    }
                    "s6_fil", "s6_maun", "s6_kafirun", "s6_masad", "s6_cards" -> {
                        val lesson = mapOf("s6_fil" to QaidaProgress.S6_FIL, "s6_maun" to QaidaProgress.S6_MAUN,
                            "s6_kafirun" to QaidaProgress.S6_KAFIRUN, "s6_masad" to QaidaProgress.S6_MASAD,
                            "s6_cards" to QaidaProgress.S6_CARDS).getValue(page)
                        val done6: () -> Unit = {
                            markDone(lesson); onEarnRewards(5, 20, "Qaida stage 6: $lesson")
                            page = "home"; refresh++
                        }
                        Stage6Lesson(page, play, done6)
                    }
                    "s5_words", "s5_endings", "s5_fatiha", "s5_short", "s5_falaq" -> {
                        val lesson = mapOf("s5_words" to QaidaProgress.S5_WORDS, "s5_endings" to QaidaProgress.S5_ENDINGS,
                            "s5_fatiha" to QaidaProgress.S5_FATIHA, "s5_short" to QaidaProgress.S5_SHORT,
                            "s5_falaq" to QaidaProgress.S5_FALAQ).getValue(page)
                        val done5: () -> Unit = {
                            markDone(lesson); onEarnRewards(5, 20, "Qaida stage 5: $lesson")
                            page = "home"; refresh++
                        }
                        Stage5Lesson(page, play, done5)
                    }
                    "s4_stop", "s4_signs", "s4_qalqalah", "s4_kawthar", "s4_falaq", "s4_nas" -> {
                        val lesson = mapOf("s4_stop" to QaidaProgress.S4_STOP, "s4_signs" to QaidaProgress.S4_SIGNS,
                            "s4_qalqalah" to QaidaProgress.S4_QALQALAH, "s4_kawthar" to QaidaProgress.S4_KAWTHAR,
                            "s4_falaq" to QaidaProgress.S4_FALAQ, "s4_nas" to QaidaProgress.S4_NAS).getValue(page)
                        val done4: () -> Unit = {
                            markDone(lesson); onEarnRewards(5, 20, "Qaida stage 4: $lesson")
                            page = "home"; refresh++
                        }
                        Stage4Lesson(page, play, done4)
                    }
                    "s3_tashdeed", "s3_ghunna", "s3_madd", "s3_shamsi", "s3_fatiha", "s3_ikhlas" -> {
                        val lesson = mapOf("s3_tashdeed" to QaidaProgress.S3_TASHDEED, "s3_ghunna" to QaidaProgress.S3_GHUNNA,
                            "s3_madd" to QaidaProgress.S3_MADD, "s3_shamsi" to QaidaProgress.S3_SHAMSI,
                            "s3_fatiha" to QaidaProgress.S3_FATIHA, "s3_ikhlas" to QaidaProgress.S3_IKHLAS).getValue(page)
                        val done3: () -> Unit = {
                            markDone(lesson); onEarnRewards(5, 20, "Qaida stage 3: $lesson")
                            page = "home"; refresh++
                        }
                        Stage3Lesson(page, play, done3)
                    }
                    "s2_joining", "s2_muqattaat", "s2_tanween", "s2_khara", "s2_madd", "s2_jazm" -> {
                        val lesson = mapOf("s2_joining" to QaidaProgress.S2_JOINING, "s2_muqattaat" to QaidaProgress.S2_MUQATTAAT,
                            "s2_tanween" to QaidaProgress.S2_TANWEEN, "s2_khara" to QaidaProgress.S2_KHARA,
                            "s2_madd" to QaidaProgress.S2_MADD, "s2_jazm" to QaidaProgress.S2_JAZM).getValue(page)
                        val done: () -> Unit = {
                            markDone(lesson); onEarnRewards(5, 20, "Qaida stage 2: $lesson")
                            page = "home"; refresh++
                        }
                        Stage2Lesson(page, play, done)
                    }
                    else -> when {
                        page.startsWith("j_") -> {
                            val n = page.removePrefix("j_").toInt()
                            val s = juz.getValue(n)
                            JuzAmmaLesson(s, play) {
                                markDone(QaidaJuzAmma.lessonKey(n)); onEarnRewards(5, 20, "Qaida: ${s.urduName}")
                                page = "home"; refresh++
                            }
                        }
                        page.startsWith("jq_") -> {
                            val st = page.removePrefix("jq_").toInt()
                            val surahs = QaidaJuzAmma.group(st).surahs.mapNotNull { juz[it] }
                            QaidaQuizScreen(play, stageNo = st, generate = { QaidaJuzAmmaQuiz.generate(it, surahs) }, onFinished = { score ->
                                current[0]?.done(score); progress.recordQuizScore(score, stage = st)
                                if (score >= QaidaProgress.PASS_MARK) onEarnRewards(10, 50, "Qaida: Stage $st quiz passed")
                                refresh++
                            }, onDone = { page = "home"; refresh++ })
                        }
                        else -> QaidaHome(progress, refresh, stage, juz, onStage = { stage = it }) { page = it }
                    }
                }
              }
            }
        }
    }
}

@Composable
private fun QaidaHeader(title: String, onBack: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(Green).padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("qaida_back")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("واپس"), tint = Color.White)
        }
        UrduText(title, size = 18.sp, color = Color.White, weight = FontWeight.Bold)
    }
}

@Composable
private fun TemporaryAudioBanner() {
    Row(Modifier.fillMaxWidth().background(Amber).padding(horizontal = 16.dp, vertical = 2.dp).testTag("qaida_audio_banner"), verticalAlignment = Alignment.CenterVertically) {
        UrduText("🔈 آیات اور قرآنی الفاظ: قاری مشاری العفاسی کی تلاوت (پہلی بار انٹرنیٹ چاہیے، پھر فون میں محفوظ)۔ اکیلے حروف اور حرکات ابھی فون کی آواز میں ہیں؛ قاری صاحب کی ریکارڈنگ جلد۔", size = 12.sp, color = Quiet)
    }
}

// ---------------------------------------------------------------------------------------
// Lesson list
// ---------------------------------------------------------------------------------------
@Composable
private fun QaidaHome(progress: QaidaProgress, refreshKey: Int, stage: Int, juz: Map<Int, QaidaStage5.WbwSurah>, onStage: (Int) -> Unit, open: (String) -> Unit) {
    val seen = remember(refreshKey) { progress.seenLetters().size }
    // refreshKey changes whenever a lesson closes, so these reads are fresh on every return here
    val done = { l: String -> refreshKey >= 0 && progress.isLessonDone(l) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Keep the open stage's tab in view (later tabs sit off-screen on narrow phones).
        val tabScroll = rememberScrollState()
        androidx.compose.runtime.LaunchedEffect(stage) {
            // Only later stages need it; wait for the first layout, then jump once (no animation, no re-trigger).
            if (stage >= 4) {
                androidx.compose.runtime.snapshotFlow { tabScroll.maxValue }.first { it > 0 }
                tabScroll.scrollTo(tabScroll.maxValue * (stage - 1) / (STAGE_COUNT - 1))
            } else if (tabScroll.value != 0) tabScroll.scrollTo(0)
        }
        Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(24.dp))
            .horizontalScroll(tabScroll).padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            val tabs = listOf(1 to "۱: حروف", 2 to "۲: جوڑ", 3 to "۳: تلاوت", 4 to "۴: وقف", 5 to "۵: معنی", 6 to "۶: سورتیں", 7 to "۷: تجوید") +
                QaidaJuzAmma.groups.map { it.stage to it.tab } + listOf(11 to "۱۱: گرامر")
            tabs.forEach { (n, label) ->
                val on = stage == n
                Box(Modifier.widthIn(min = 72.dp).heightIn(min = 44.dp).background(if (on) Green else Color.Transparent, RoundedCornerShape(20.dp))
                    .clickable { onStage(n) }.testTag("qaida_stage_tab_$n"), contentAlignment = Alignment.Center) {
                    UrduText(label, Modifier.padding(horizontal = 8.dp), size = 13.sp, color = if (on) Color.White else Green, weight = FontWeight.Bold, align = TextAlign.Center)
                }
            }
        }
        if (stage == 1) {
            val best = progress.bestQuizScore(1)
            HeroCard("ا ب ت", "مرحلہ ۱: حروف اور پہلی آوازیں", "روزانہ ۵ سے ۱۰ منٹ۔ ہر سبق کے بعد مشق کریں۔")
            LessonCard("lesson_letters", "۱", "حروفِ مفردات", tx("قاعدے کے ۲۹ حروف، ان کے نام اور مخارج  ·  $seen/29 دیکھے", "The 29 letters of the Qaida, their names and makharij  ·  $seen/29 seen"),
                done(QaidaProgress.LESSON_LETTERS)) { open("letters") }
            LessonCard("lesson_sounds", "۲", "ملتی جلتی آوازیں", "وہ حروف جو اردو میں ایک جیسے بولے جاتے ہیں: س ث ص، ز ذ ظ ض، ت ط، ہ ح، ء ع، ک ق",
                done(QaidaProgress.LESSON_SOUNDS)) { open("sounds") }
            LessonCard("lesson_harakat", "۳", "حرکات: زبر، زیر، پیش", "تختی ۴ — ہر حرف تین حرکتوں کے ساتھ، ہجے سمیت",
                done(QaidaProgress.LESSON_HARAKAT)) { open("harakat") }
            LessonCard("lesson_quiz", "۴", "مشق", quizLine(best, 1),
                done(QaidaProgress.LESSON_QUIZ)) { open("quiz") }
            if (done(QaidaProgress.LESSON_QUIZ)) {
                Card(Modifier.clickable { onStage(2) }.testTag("qaida_go_stage2"), colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine)) {
                    UrduText("ماشاء اللہ! مرحلہ ۱ مکمل۔ اب مرحلہ ۲ (حروف ملا کر پڑھنا) شروع کریں ←", Modifier.padding(16.dp), size = 15.sp, weight = FontWeight.Bold)
                }
            }
        } else if (stage == 2) {
            val best = progress.bestQuizScore(2)
            HeroCard("بًا  بٰ  اَبْ", "مرحلہ ۲: حروف جوڑنا اور نشانات", "تختی ۲ تا ۹ — مرکبات، مقطعات، تنوین، کھڑی حرکات، مدّ و لین، جزم")
            if (!done(QaidaProgress.LESSON_QUIZ)) {
                Card(colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine), modifier = Modifier.testTag("qaida_s2_hint")) {
                    UrduText("مشورہ: پہلے مرحلہ ۱ کی مشق مکمل کر لیں، پھر یہاں آئیں۔ آپ چاہیں تو ابھی بھی شروع کر سکتے ہیں۔", Modifier.padding(12.dp), size = 13.sp)
                }
            }
            LessonCard("lesson_s2_joining", "۱", "مرکبات: حروف ملانا", "تختی ۲–۳ — حرف کی شروع، درمیان اور آخر کی شکل؛ ا د ذ ر ز و آگے نہیں جڑتے",
                done(QaidaProgress.S2_JOINING)) { open("s2_joining") }
            LessonCard("lesson_s2_muqattaat", "۲", "حروفِ مقطعات", "تختی ۳ — سورتوں کے شروع کے ۱۴ حروف، الگ الگ نام سے پڑھیں",
                done(QaidaProgress.S2_MUQATTAAT)) { open("s2_muqattaat") }
            LessonCard("lesson_s2_tanween", "۳", "تنوین: دو زبر، دو زیر، دو پیش", "تختی ۵ — آخر میں 'ن' کی آواز: بًا بٍ بٌ",
                done(QaidaProgress.S2_TANWEEN)) { open("s2_tanween") }
            LessonCard("lesson_s2_khara", "۴", "کھڑا زبر، کھڑی زیر، اُلٹا پیش", "تختی ۶ — حرکت کو لمبا پڑھنا: بٰ بٖ بٗ",
                done(QaidaProgress.S2_KHARA)) { open("s2_khara") }
            LessonCard("lesson_s2_madd", "۵", "حروفِ مدّہ و لین", "تختی ۷–۸ — ا و ی سے لمبی آواز، اور نرم آواز: بَا بُو بِی، بَوْ بَیْ",
                done(QaidaProgress.S2_MADD)) { open("s2_madd") }
            LessonCard("lesson_s2_jazm", "۶", "جزم (سکون)", "تختی ۹ — ساکن حرف کو پچھلے حرف سے ملانا: اَبْ، قُلْ",
                done(QaidaProgress.S2_JAZM)) { open("s2_jazm") }
            LessonCard("lesson_s2_quiz", "۷", "مرحلہ ۲ کی مشق", quizLine(best, 2),
                done(QaidaProgress.S2_QUIZ)) { open("s2_quiz") }
            if (done(QaidaProgress.S2_QUIZ)) {
                Card(Modifier.clickable { onStage(3) }.testTag("qaida_go_stage3"), colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine)) {
                    UrduText("ماشاء اللہ! مرحلہ ۲ مکمل۔ اب مرحلہ ۳ (تشدید اور تلاوت) شروع کریں ←", Modifier.padding(16.dp), size = 15.sp, weight = FontWeight.Bold)
                }
            }
        } else if (stage == 3) {
            val best = progress.bestQuizScore(3)
            HeroCard("اَبَّ  اَلشَّمْسُ", "مرحلہ ۳: تشدید اور پہلی تلاوت", "تشدید، غنّہ، مدّ کا نشان، لامِ شمسی و قمری — پھر سورۃ الفاتحہ اور سورۃ الاخلاص")
            AdabCard()
            if (!done(QaidaProgress.S2_QUIZ)) {
                Card(colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine), modifier = Modifier.testTag("qaida_s3_hint")) {
                    UrduText("مشورہ: پہلے مرحلہ ۲ کی مشق مکمل کر لیں۔ آپ چاہیں تو ابھی بھی شروع کر سکتے ہیں۔", Modifier.padding(12.dp), size = 13.sp)
                }
            }
            LessonCard("lesson_s3_tashdeed", "۱", "تشدید", "حرف دو بار — اَبَّ اِبِّ اُبُّ، ہر حرف پر ہجے کے ساتھ",
                done(QaidaProgress.S3_TASHDEED)) { open("s3_tashdeed") }
            LessonCard("lesson_s3_ghunna", "۲", "غنّہ اور تشدید والے الفاظ", "نّ مّ پر ناک کی آواز؛ رَبِّ، كُلُّ، عَلَّمَ",
                done(QaidaProgress.S3_GHUNNA)) { open("s3_ghunna") }
            LessonCard("lesson_s3_madd", "۳", "مدّ کا نشان (~)", "زیادہ لمبی آواز: جَآءَ، السَّمَآءُ",
                done(QaidaProgress.S3_MADD)) { open("s3_madd") }
            LessonCard("lesson_s3_shamsi", "۴", "حروفِ شمسی و قمری", "اَلشَّمْسُ میں لام خاموش، اَلْقَمَرُ میں لام پڑھا جاتا ہے",
                done(QaidaProgress.S3_SHAMSI)) { open("s3_shamsi") }
            LessonCard("lesson_s3_fatiha", "۵", "تلاوت: سورۃ الفاتحہ", "سات آیتیں، ہر آیت میں آنے والے اصول کے ساتھ",
                done(QaidaProgress.S3_FATIHA)) { open("s3_fatiha") }
            LessonCard("lesson_s3_ikhlas", "۶", "تلاوت: سورۃ الاخلاص", "چار آیتیں",
                done(QaidaProgress.S3_IKHLAS)) { open("s3_ikhlas") }
            LessonCard("lesson_s3_quiz", "۷", "مرحلہ ۳ کی مشق", quizLine(best, 3),
                done(QaidaProgress.S3_QUIZ)) { open("s3_quiz") }
            if (done(QaidaProgress.S3_QUIZ)) {
                Card(Modifier.clickable { onStage(4) }.testTag("qaida_go_stage4"), colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine)) {
                    UrduText("ماشاء اللہ! مرحلہ ۳ مکمل۔ اب مرحلہ ۴ (وقف اور مختصر سورتیں) شروع کریں ←", Modifier.padding(16.dp), size = 15.sp, weight = FontWeight.Bold)
                }
            }
        } else if (stage == 4) {
            val best = progress.bestQuizScore(4)
            HeroCard("۝  م  ط  ج", "مرحلہ ۴: وقف اور مختصر سورتیں", "رکنے کا طریقہ، رموزِ اوقاف، قلقلہ — پھر الکوثر، العصر، الفلق اور الناس")
            if (!done(QaidaProgress.S3_QUIZ)) {
                Card(colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine), modifier = Modifier.testTag("qaida_s4_hint")) {
                    UrduText("مشورہ: پہلے مرحلہ ۳ کی مشق مکمل کر لیں۔ آپ چاہیں تو ابھی بھی شروع کر سکتے ہیں۔", Modifier.padding(12.dp), size = 13.sp)
                }
            }
            LessonCard("lesson_s4_stop", "۱", "وقف کا طریقہ", "رکتے وقت آخری حرف ساکن: اَحَدٌ ← اَحَدْ، عَلِيْمًا ← عَلِيْمَا",
                done(QaidaProgress.S4_STOP)) { open("s4_stop") }
            LessonCard("lesson_s4_signs", "۲", "رموزِ اوقاف", "۝ م ط ج ز ص لا — کہاں رکنا ہے اور کہاں ملانا ہے",
                done(QaidaProgress.S4_SIGNS)) { open("s4_signs") }
            LessonCard("lesson_s4_qalqalah", "۳", "قلقلہ", "ق ط ب ج د پر جزم ہو تو اچھلتی آواز",
                done(QaidaProgress.S4_QALQALAH)) { open("s4_qalqalah") }
            LessonCard("lesson_s4_kawthar", "۴", "تلاوت: الکوثر اور العصر", "دو چھوٹی سورتیں، ہر آیت پر وقف کے ساتھ",
                done(QaidaProgress.S4_KAWTHAR)) { open("s4_kawthar") }
            LessonCard("lesson_s4_falaq", "۵", "تلاوت: سورۃ الفلق", "پانچ آیتیں — ہر آیت کے آخر میں قلقلہ",
                done(QaidaProgress.S4_FALAQ)) { open("s4_falaq") }
            LessonCard("lesson_s4_nas", "۶", "تلاوت: سورۃ الناس", "چھ آیتیں — غنّہ اور لامِ شمسی",
                done(QaidaProgress.S4_NAS)) { open("s4_nas") }
            LessonCard("lesson_s4_quiz", "۷", "مرحلہ ۴ کی مشق", quizLine(best, 4),
                done(QaidaProgress.S4_QUIZ)) { open("s4_quiz") }
            if (done(QaidaProgress.S4_QUIZ)) {
                Card(Modifier.clickable { onStage(5) }.testTag("qaida_go_stage5"), colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine)) {
                    UrduText("ماشاء اللہ! قاعدہ مکمل۔ اب مرحلہ ۵ میں جو پڑھا اس کا مطلب سیکھیں ←", Modifier.padding(16.dp), size = 15.sp, weight = FontWeight.Bold)
                }
            }
        } else if (stage == 5) {
            val best = progress.bestQuizScore(5)
            HeroCard("رَبُّنَا  ·  ہمارا رب", "مرحلہ ۵: قرآن سمجھیں", "عام قرآنی الفاظ، ضمیریں، پھر الفاتحہ اور مختصر سورتیں لفظ بہ لفظ")
            if (!done(QaidaProgress.S4_QUIZ)) {
                Card(colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine), modifier = Modifier.testTag("qaida_s5_hint")) {
                    UrduText("مشورہ: پہلے مرحلہ ۴ کی مشق مکمل کر لیں۔ آپ چاہیں تو ابھی بھی شروع کر سکتے ہیں۔", Modifier.padding(12.dp), size = 13.sp)
                }
            }
            LessonCard("lesson_s5_words", "۱", "قرآن کے عام الفاظ", tx("${QaidaStage5.allWords.size} الفاظ: اللہ کی صفات، حروف، ضمیریں، کام، چیزیں", "${QaidaStage5.allWords.size} words: Allah's attributes, particles, pronouns, actions, things"),
                done(QaidaProgress.S5_WORDS)) { open("s5_words") }
            LessonCard("lesson_s5_endings", "۲", "میرا، تیرا، اس کا", "رَبُّنَا ہمارا رب، رَبُّكَ تیرا رب، رَبِّيْ میرا رب",
                done(QaidaProgress.S5_ENDINGS)) { open("s5_endings") }
            LessonCard("lesson_s5_fatiha", "۳", "الفاتحہ لفظ بہ لفظ", "ہر لفظ کے نیچے اس کا اردو مطلب",
                done(QaidaProgress.S5_FATIHA)) { open("s5_fatiha") }
            LessonCard("lesson_s5_short", "۴", "الاخلاص، الکوثر، العصر", "تین چھوٹی سورتیں لفظ بہ لفظ",
                done(QaidaProgress.S5_SHORT)) { open("s5_short") }
            LessonCard("lesson_s5_falaq", "۵", "الفلق اور الناس", "پناہ مانگنے کی دو سورتیں لفظ بہ لفظ",
                done(QaidaProgress.S5_FALAQ)) { open("s5_falaq") }
            LessonCard("lesson_s5_quiz", "۶", "مرحلہ ۵ کی مشق", quizLine(best, 5),
                done(QaidaProgress.S5_QUIZ)) { open("s5_quiz") }
            if (done(QaidaProgress.S5_QUIZ)) {
                Card(Modifier.clickable { onStage(6) }.testTag("qaida_go_stage6"), colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine)) {
                    UrduText("ماشاء اللہ! اب مرحلہ ۶ میں باقی چھوٹی سورتیں سمجھیں ←", Modifier.padding(16.dp), size = 15.sp, weight = FontWeight.Bold)
                }
            }
        } else if (stage == 6) {
            val best = progress.bestQuizScore(6)
            HeroCard("الفیل ← المسد", "مرحلہ ۶: چھوٹی سورتیں سمجھیں", "پارہ عمّ کی باقی چھ سورتیں لفظ بہ لفظ، پھر مطلب بتانے کی مشق")
            if (!done(QaidaProgress.S5_QUIZ)) {
                Card(colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine), modifier = Modifier.testTag("qaida_s6_hint")) {
                    UrduText("مشورہ: پہلے مرحلہ ۵ کی مشق مکمل کر لیں۔ آپ چاہیں تو ابھی بھی شروع کر سکتے ہیں۔", Modifier.padding(12.dp), size = 13.sp)
                }
            }
            LessonCard("lesson_s6_fil", "۱", "الفیل اور قریش", "ہاتھی والوں کا واقعہ، اور قریش پر اللہ کی نعمت",
                done(QaidaProgress.S6_FIL)) { open("s6_fil") }
            LessonCard("lesson_s6_maun", "۲", "سورۃ الماعون", "سات آیتیں — یتیم، مسکین اور نماز",
                done(QaidaProgress.S6_MAUN)) { open("s6_maun") }
            LessonCard("lesson_s6_kafirun", "۳", "الکافرون اور النصر", "تمہارا دین تمہارے لیے؛ اللہ کی مدد اور فتح",
                done(QaidaProgress.S6_KAFIRUN)) { open("s6_kafirun") }
            LessonCard("lesson_s6_masad", "۴", "سورۃ المسد", "پانچ آیتیں لفظ بہ لفظ",
                done(QaidaProgress.S6_MASAD)) { open("s6_masad") }
            LessonCard("lesson_s6_cards", "۵", "مطلب بتائیں (کارڈ)", tx("${QaidaStage6.flashcards.size} الفاظ میں سے ۱۵ کارڈ — لفظ دیکھیں، مطلب بولیں، پلٹ کر دیکھیں", "15 cards from ${QaidaStage6.flashcards.size} words — see the word, say the meaning, flip to check"),
                done(QaidaProgress.S6_CARDS)) { open("s6_cards") }
            LessonCard("lesson_s6_quiz", "۶", "مرحلہ ۶ کی مشق", quizLine(best, 6),
                done(QaidaProgress.S6_QUIZ)) { open("s6_quiz") }
            if (done(QaidaProgress.S6_QUIZ)) {
                Card(Modifier.clickable { onStage(7) }.testTag("qaida_go_stage7"), colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine)) {
                    UrduText("ماشاء اللہ! اب مرحلہ ۷ میں نون اور میم ساکن کے قاعدے سیکھیں ←", Modifier.padding(16.dp), size = 15.sp, weight = FontWeight.Bold)
                }
            }
        } else if (stage == 7) {
            val best = progress.bestQuizScore(7)
            HeroCard("مِنْ شَرِّ  ·  مِنْ مَّسَدٍ", "مرحلہ ۷: نون اور میم ساکن", "اظہار، ادغام، اقلاب، اخفاء — اور میم ساکن کے تین قاعدے")
            if (!done(QaidaProgress.S6_QUIZ)) {
                Card(colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine), modifier = Modifier.testTag("qaida_s7_hint")) {
                    UrduText("مشورہ: پہلے مرحلہ ۶ کی مشق مکمل کر لیں۔ آپ چاہیں تو ابھی بھی شروع کر سکتے ہیں۔", Modifier.padding(12.dp), size = 13.sp)
                }
            }
            LessonCard("lesson_s7_izhar", "۱", "اظہار", "حلق کے ۶ حروف سے پہلے نون صاف: اَنْعَمْتَ، مِنْ خَوْفٍ",
                done(QaidaProgress.S7_IZHAR)) { open("s7_izhar") }
            LessonCard("lesson_s7_idgham", "۲", "ادغام", "ی ن م و میں غنّہ کے ساتھ، ل ر میں بغیر غنّہ: مِنْ مَّسَدٍ، يَكُنْ لَّهٗ",
                done(QaidaProgress.S7_IDGHAM)) { open("s7_idgham") }
            LessonCard("lesson_s7_iqlab", "۳", "اقلاب", "ب سے پہلے نون میم بن جاتا ہے: مِنْۢ بَعْدِ",
                done(QaidaProgress.S7_IQLAB)) { open("s7_iqlab") }
            LessonCard("lesson_s7_ikhfa", "۴", "اخفاء", "باقی ۱۵ حروف سے پہلے نون چھپا کر: مِنْ شَرِّ، اَنْتُمْ",
                done(QaidaProgress.S7_IKHFA)) { open("s7_ikhfa") }
            LessonCard("lesson_s7_meem", "۵", "میم ساکن", "اخفائے شفوی، ادغامِ شفوی، اظہارِ شفوی",
                done(QaidaProgress.S7_MEEM)) { open("s7_meem") }
            LessonCard("lesson_s7_spot", "۶", "قاعدہ پہچانیں", "پڑھی ہوئی سورتوں کی مثالیں — خود بتائیں، پھر جواب دیکھیں",
                done(QaidaProgress.S7_SPOT)) { open("s7_spot") }
            LessonCard("lesson_s7_quiz", "۷", "مرحلہ ۷ کی مشق", quizLine(best, 7),
                done(QaidaProgress.S7_QUIZ)) { open("s7_quiz") }
            if (done(QaidaProgress.S7_QUIZ)) {
                Card(Modifier.clickable { onStage(8) }.testTag("qaida_go_stage8"), colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine)) {
                    UrduText("ماشاء اللہ! قاعدہ کے سب قاعدے مکمل۔ اب مرحلہ ۸ میں پارہ عمّ کی باقی سورتیں سمجھیں ←", Modifier.padding(16.dp), size = 15.sp, weight = FontWeight.Bold)
                }
            }
        } else if (stage in 8..10) {
            val g = QaidaJuzAmma.group(stage)
            val best = progress.bestQuizScore(stage)
            val quizNo = urduDigit(g.surahs.size + 1)
            HeroCard(g.hero, g.title, tx("${urduDigit(g.surahs.size)} سورتیں، ${urduDigit(QaidaJuzAmma.ayahCount(juz, stage))} آیتیں — ہر لفظ کے نیچے اس کا اردو مطلب", "${g.surahs.size} surahs, ${QaidaJuzAmma.ayahCount(juz, stage)} ayat — the English meaning under every word"))
            val prevQuiz = if (stage == 8) QaidaProgress.S7_QUIZ else QaidaJuzAmma.quizKey(stage - 1)
            if (!done(prevQuiz)) {
                Card(colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine), modifier = Modifier.testTag("qaida_s${stage}_hint")) {
                    UrduText(tx("مشورہ: پہلے مرحلہ ${urduDigit(stage - 1)} کی مشق مکمل کر لیں۔ آپ چاہیں تو ابھی بھی شروع کر سکتے ہیں۔", "Tip: finish the Stage ${stage - 1} practice first. You can still start now if you like."), Modifier.padding(12.dp), size = 13.sp)
                }
            }
            g.surahs.forEachIndexed { i, n ->
                val s = juz[n]
                LessonCard("lesson_juz_$n", urduDigit(i + 1), s?.urduName ?: tx("سورۃ $n", "Surah $n"),
                    (s?.ayat?.size ?: 0).let { tx("${urduDigit(it)} آیتیں لفظ بہ لفظ", "$it ayat word by word") }, done(QaidaJuzAmma.lessonKey(n))) { open("j_$n") }
            }
            LessonCard("lesson_juz_quiz_$stage", quizNo, tx("مرحلہ ${urduDigit(stage)} کی مشق", "Stage $stage practice"),
                quizLine(best, null),
                done(QaidaJuzAmma.quizKey(stage))) { open("jq_$stage") }
            if (done(QaidaJuzAmma.quizKey(stage))) {
                val next = QaidaJuzAmma.groups.firstOrNull { it.stage == stage + 1 }
                Card(Modifier.clickable { onStage(next?.stage ?: 11) }.testTag("qaida_go_next_$stage"),
                    colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine)) {
                    UrduText(if (next != null) tx("ماشاء اللہ! اب مرحلہ ${urduDigit(next.stage)} شروع کریں ←", "Masha'Allah! Now start Stage ${next.stage} →")
                        else "ماشاء اللہ! پورا پارہ عمّ پڑھ اور سمجھ لیا۔ اب مرحلہ ۱۱ میں قرآنی گرامر سیکھیں ←",
                        Modifier.padding(16.dp), size = 15.sp, weight = FontWeight.Bold)
                }
            }
        } else {
            val best = progress.bestQuizScore(11)
            HeroCard("ع ل م  ←  عَالِمٌ", "مرحلہ ۱۱: قرآنی گرامر کی بنیادیں", "مادہ، سانچے، ماضی اور مضارع، حکم اور نفی — اردو کے جانے پہچانے الفاظ سے")
            if (!done(QaidaJuzAmma.quizKey(10))) {
                Card(colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine), modifier = Modifier.testTag("qaida_s11_hint")) {
                    UrduText("مشورہ: یہ مرحلہ کسی بھی وقت پڑھا جا سکتا ہے، مگر پہلے کچھ سورتیں سمجھ لینا مفید ہے۔", Modifier.padding(12.dp), size = 13.sp)
                }
            }
            LessonCard("lesson_s11_roots", "۱", "مادہ (جڑ)", "تین حرفی جڑ: ع ل م سے علم، عالم، معلوم",
                done(QaidaProgress.S11_ROOTS)) { open("s11_roots") }
            LessonCard("lesson_s11_patterns", "۲", "سانچے (اوزان)", "فَاعِل کرنے والا، مَفْعُوْل جس پر ہوا، فَعِيْل بہت زیادہ، مَفْعَل جگہ",
                done(QaidaProgress.S11_PATTERNS)) { open("s11_patterns") }
            LessonCard("lesson_s11_past", "۳", "ماضی — کس نے کیا", "خَلَقَ، اٰمَنُوْا، اَنْعَمْتَ، قَدَّمْتُ، خَلَقْنَا",
                done(QaidaProgress.S11_PAST)) { open("s11_past") }
            LessonCard("lesson_s11_present", "۴", "مضارع — کون کرتا ہے", "يَـ وہ، تَـ تو، اَ میں، نَـ ہم: نَعْبُدُ، اَعْبُدُ",
                done(QaidaProgress.S11_PRESENT)) { open("s11_present") }
            LessonCard("lesson_s11_commands", "۵", "حکم اور 'نہیں'", "قُلْ، اقْرَاْ — لَا، لَمْ، مَا، لَنْ",
                done(QaidaProgress.S11_COMMANDS)) { open("s11_commands") }
            LessonCard("lesson_s11_detective", "۶", "مادہ جاسوس 🔍", "لفظ میں سے تین اصل حروف ڈھونڈیں",
                done(QaidaProgress.S11_DETECTIVE)) { open("s11_detective") }
            LessonCard("lesson_s11_quiz", "۷", "مرحلہ ۱۱ کی مشق", quizLine(best, null),
                done(QaidaProgress.S11_QUIZ)) { open("s11_quiz") }
            if (done(QaidaProgress.S11_QUIZ)) {
                Card(colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine)) {
                    UrduText("ماشاء اللہ! پورا کورس مکمل — حروف سے لے کر پارہ عمّ سمجھنے اور قرآنی گرامر تک۔ اب استاد کے ساتھ ناظرہ اور ترجمہ جاری رکھیں۔", Modifier.padding(16.dp), size = 15.sp, weight = FontWeight.Bold)
                }
            }
        }
    }
}

/** Manners of reciting, shown where real Quran recitation begins (Stage 3). */
@Composable
private fun AdabCard() {
    Card(Modifier.testTag("qaida_adab"), colors = CardDefaults.cardColors(containerColor = GreenSoft), border = BorderStroke(1.dp, Green)) {
        Column(Modifier.padding(14.dp)) {
            UrduText("🤲 تلاوت کے آداب", size = 15.sp, color = Green, weight = FontWeight.Bold)
            UrduText("• ہو سکے تو وضو کر کے، پاک جگہ پر، توجہ سے پڑھیں (فون پر پڑھنے کے لیے وضو ضروری نہیں، مگر بہتر ہے)۔\n" +
                "• شروع میں اَعُوْذُ بِاللّٰهِ اور سورت کے شروع میں بِسْمِ اللّٰهِ پڑھیں۔\n" +
                "• آہستہ اور صاف پڑھیں؛ غلطی ہو تو دوبارہ ٹھیک پڑھیں۔\n" +
                "• ۩ کا نشان سجدۂ تلاوت ہے: یہ آیت پڑھنے یا سننے پر بالغ پر سجدہ واجب ہے (حنفی)۔", size = 13.sp)
        }
    }
}

@Composable
private fun HeroCard(arabic: String, title: String, sub: String) {
    Card(colors = CardDefaults.cardColors(containerColor = GreenSoft), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            ArabicText(arabic, size = 40.sp, color = Green)
            UrduText(title, size = 18.sp, weight = FontWeight.Bold, align = TextAlign.Center)
            UrduText(sub, size = 13.sp, color = Quiet, align = TextAlign.Center)
        }
    }
}

@Composable
private fun LessonCard(tag: String, number: String, title: String, subtitle: String, done: Boolean, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick).testTag(tag),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (done) Green else Color(0xFFE2E5EA)),
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).background(if (done) Green else GreenSoft, CircleShape), contentAlignment = Alignment.Center) {
                if (done) Icon(Icons.Filled.Check, contentDescription = "مکمل", tint = Color.White)
                else UrduText(number, size = 16.sp, color = Green, weight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                UrduText(title, size = 17.sp, weight = FontWeight.Bold)
                UrduText(subtitle, size = 12.sp, color = Quiet)
            }
        }
    }
}

// ---------------------------------------------------------------------------------------
// 1.1 Letters
// ---------------------------------------------------------------------------------------
@Composable
private fun LettersLesson(progress: QaidaProgress, play: (String, String) -> Unit, onAllSeen: () -> Unit) {
    var selected by remember { mutableStateOf<QaidaLetter?>(null) }
    var seen by remember { mutableStateOf(progress.seenLetters()) }
    Column(Modifier.fillMaxSize()) {
        UrduText("حرف پر ٹیپ کریں، نام سنیں اور مخرج پڑھیں۔ موٹے (پُر) حروف سرخ ہیں — یاد رکھنے کو: خُصَّ ضَغْطٍ قِظْ (خ ص ض غ ط ق ظ)۔ ض کو کبھی ز یا د کی طرح نہ پڑھیں۔", Modifier.padding(horizontal = 16.dp, vertical = 8.dp), size = 13.sp, color = Quiet)
        ArabicFlow { LazyVerticalGrid(
            columns = GridCells.Adaptive(72.dp),
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp).testTag("qaida_letters_grid"),
            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) { MediumFlow { MakhrajMap() } }
            items(QaidaData.letters, key = { it.id }) { l ->
                val isSeen = l.id in seen
                Card(
                    Modifier.heightIn(min = 84.dp).clickable {
                        selected = l
                        progress.markLetterSeen(l.id); seen = progress.seenLetters()
                        play(l.audioKey, l.arabic)
                        if (seen.size == QaidaData.letters.size && !progress.isLessonDone(QaidaProgress.LESSON_LETTERS)) onAllSeen()
                    }.testTag("qaida_letter_${l.id}").semantics { contentDescription = "${l.qaidaName} ${l.roman}" },
                    colors = CardDefaults.cardColors(containerColor = if (isSeen) GreenSoft else Color.White),
                    border = BorderStroke(1.dp, if (isSeen) Green else Color(0xFFE2E5EA)),
                ) {
                    Column(Modifier.fillMaxWidth().padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        ArabicText(l.arabic, size = 34.sp, color = if (l.heavy) Heavy else Ink)
                        UrduText(l.qaidaName, size = 12.sp, color = Quiet, align = TextAlign.Center)
                    }
                }
            }
        } }
    }
    selected?.let { l -> LetterDetailDialog(l, play) { selected = null } }
}

@Composable
private fun LetterDetailDialog(l: QaidaLetter, play: (String, String) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("qaida_letter_detail"),
        confirmButton = { TextButton(onClick = onDismiss) { UrduText("ٹھیک ہے", size = 15.sp, color = Green, weight = FontWeight.Bold) } },
        title = null,
        text = {
            MediumFlow {
                Column(Modifier.verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                    ArabicText(l.arabic, size = 88.sp, color = if (l.heavy) Heavy else Ink)
                    UrduText(tx("${l.qaidaName}  ·  ${l.roman}", tr(l.qaidaName)), size = 18.sp, weight = FontWeight.Bold, align = TextAlign.Center)
                    if (l.heavy) UrduText("پُر (موٹا) حرف", size = 12.sp, color = Heavy, align = TextAlign.Center)
                    Spacer(Modifier.height(4.dp))
                    PlayButton("نام سنیں") { play(l.audioKey, l.arabic) }
                    Spacer(Modifier.height(8.dp))
                    InfoBox(tx("${l.region.emoji} مخرج: ${l.region.urdu}", "${l.region.emoji} Makhraj: ${tr(l.region.urdu)}"), l.makhraj, GreenSoft, Green)
                    l.urduNote?.let { Spacer(Modifier.height(8.dp)); InfoBox("⚠️ اردو بولنے والوں کے لیے", it, Amber, AmberLine) }
                    l.urduShape?.takeIf { !QaidaLang.english }?.let {
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            UrduText("قرآن میں:", size = 13.sp, color = Quiet); Spacer(Modifier.width(6.dp)); ArabicText(l.arabic, size = 28.sp)
                            Spacer(Modifier.width(18.dp))
                            UrduText("اردو میں:", size = 13.sp, color = Quiet); Spacer(Modifier.width(6.dp)); UrduText(it, size = 22.sp)
                        }
                    }
                }
            }
        },
    )
}

@Composable
internal fun InfoBox(title: String, body: String, bg: Color, line: Color) {
    Column(Modifier.fillMaxWidth().background(bg, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
        UrduText(title, size = 13.sp, color = line, weight = FontWeight.Bold)
        UrduText(body, size = 14.sp)
    }
}

@Composable
internal fun PlayButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = modifier.heightIn(min = 48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Green), shape = RoundedCornerShape(24.dp),
    ) {
        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White)
        Spacer(Modifier.width(6.dp))
        UrduText(label, size = 14.sp, color = Color.White, weight = FontWeight.Bold)
    }
}

// ---------------------------------------------------------------------------------------
// 1.2 Sounds Urdu speakers merge
// ---------------------------------------------------------------------------------------
@Composable
private fun SoundsLesson(play: (String, String) -> Unit, onDone: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().testTag("qaida_sounds_list"), state = trackedListState(), contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            UrduText("اردو میں کئی عربی حروف ایک جیسے بولے جاتے ہیں۔ قرآن پڑھتے وقت ہر حرف کو اس کی اپنی جگہ سے ادا کرنا ضروری ہے، ورنہ لفظ کا معنی بدل سکتا ہے۔ ہر حرف اور مثال کو سنیں اور فرق پہچانیں۔",
                size = 14.sp, color = Quiet)
        }
        items(QaidaData.soundGroups, key = { it.id }) { g ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E5EA)),
                modifier = Modifier.testTag("qaida_group_${g.id}")) {
                Column(Modifier.padding(14.dp)) {
                    UrduText(tx("اردو میں سب '${g.urduSays}'", "Easy to mix up: " + g.letterIds.joinToString("  ") { QaidaData.letter(it).arabic }), size = 16.sp, weight = FontWeight.Bold, color = Green)
                    UrduText(g.tip, size = 13.sp)
                    Spacer(Modifier.height(6.dp))
                    g.letterIds.forEach { id ->
                        val l = QaidaData.letter(id); val ex = g.examples.getValue(id)
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(56.dp).clickable { play(l.audioKey, l.arabic) }
                                .background(if (l.heavy) Color(0xFFFBE9E7) else GreenSoft, RoundedCornerShape(12.dp))
                                .semantics { contentDescription = l.qaidaName }, contentAlignment = Alignment.Center) {
                                ArabicText(l.arabic, size = 30.sp, color = if (l.heavy) Heavy else Ink)
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                UrduText(tr(l.qaidaName) + if (l.heavy) tr("  (پُر)") else "", size = 13.sp, weight = FontWeight.Bold)
                                UrduText(tx("مثال: ${ex.urduMeaning}", "Example: ${tr(ex.urduMeaning)}"), size = 12.sp, color = Quiet)
                            }
                            Box(Modifier.clickable { play(QaidaData.exampleAudioKey(id), ex.arabic) }.padding(horizontal = 8.dp)
                                .semantics { contentDescription = tx("مثال سنیں: ${ex.urduMeaning}", "Hear the example: ${tr(ex.urduMeaning)}") }) {
                                ArabicText(ex.arabic, size = 26.sp, color = Green)
                            }
                        }
                    }
                }
            }
        }
        item {
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("qaida_sounds_done"),
                colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                UrduText("میں نے سب سن لیا ✓", size = 15.sp, color = Color.White, weight = FontWeight.Bold)
            }
        }
    }
}

// ---------------------------------------------------------------------------------------
// 1.3 Harakat (Takhti 4)
// ---------------------------------------------------------------------------------------
@Composable
private fun HarakatLesson(play: (String, String) -> Unit, onDone: () -> Unit) {
    val letters = remember { QaidaData.harakatLetters() }
    LazyColumn(Modifier.fillMaxSize().testTag("qaida_harakat_list"), state = trackedListState(), contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { ArabicFlow {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QaidaData.Harakah.values().forEach { h ->
                    Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = GreenSoft)) {
                        Column(Modifier.fillMaxWidth().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            ArabicText(h.example, size = 40.sp, color = Green)
                            UrduText(h.urduName, size = 16.sp, weight = FontWeight.Bold, align = TextAlign.Center)
                            UrduText(h.hint, size = 11.sp, color = Quiet, align = TextAlign.Center)
                        }
                    }
                }
            }
        } }
        item { UrduText("ہر خانے پر ٹیپ کر کے ہجے سنیں، پھر پوری سطر خود پڑھیں: بَا زَبَر بَ، بَا زِیر بِ، بَا پیش بُ", size = 13.sp, color = Quiet) }
        items(letters, key = { it.id }) { l -> ArabicFlow {
            Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(14.dp)).padding(8.dp).testTag("qaida_row_${l.id}"),
                verticalAlignment = Alignment.CenterVertically) {
                UrduText(l.qaidaName, Modifier.width(64.dp), size = 13.sp, color = Quiet)
                QaidaData.Harakah.values().forEach { h ->
                    val form = QaidaData.withHarakah(l, h)
                    Box(Modifier.weight(1f).padding(horizontal = 4.dp).heightIn(min = 56.dp)
                        .background(if (l.heavy) Color(0xFFFBE9E7) else GreenSoft, RoundedCornerShape(10.dp))
                        .clickable { play(QaidaData.audioKey(l, h), form) }
                        .semantics { contentDescription = QaidaData.hijje(l, h) },
                        contentAlignment = Alignment.Center) {
                        ArabicText(form, size = 30.sp, color = if (l.heavy) Heavy else Ink)
                    }
                }
            }
        } }
        item {
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("qaida_harakat_done"),
                colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                UrduText("میں نے پوری تختی پڑھ لی ✓", size = 15.sp, color = Color.White, weight = FontWeight.Bold)
            }
        }
    }
}

// ---------------------------------------------------------------------------------------
// 1.4 Practice quiz
// ---------------------------------------------------------------------------------------
@Composable
private fun QaidaQuizScreen(
    play: (String, String) -> Unit, stageNo: Int, generate: (Random) -> List<QaidaQuestion>,
    onFinished: (Int) -> Unit, onDone: () -> Unit,
) {
    var seed by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    val questions = remember(seed) { generate(Random(seed)) }
    var index by rememberSaveable(seed) { mutableIntStateOf(0) }
    var score by rememberSaveable(seed) { mutableIntStateOf(0) }
    var chosen by rememberSaveable(seed, index) { mutableStateOf<Int?>(null) }
    var finished by rememberSaveable(seed) { mutableStateOf(false) }

    if (finished) {
        val passed = score >= QaidaProgress.PASS_MARK
        Column(Modifier.fillMaxSize().padding(24.dp).testTag("qaida_quiz_result"), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            UrduText(if (passed) "ماشاء اللہ! 🌟" else "شاباش، دوبارہ کوشش کریں 💪", size = 24.sp, weight = FontWeight.Bold, align = TextAlign.Center)
            UrduText(tx("$score / ${questions.size} درست", "$score / ${questions.size} correct"), size = 20.sp, color = Green, align = TextAlign.Center)
            UrduText(if (passed) tx("مرحلہ ${urduDigit(stageNo)} مکمل ہو گیا۔", "Stage $stageNo is complete.")
                else tx("۸ درست جواب پر مرحلہ ${urduDigit(stageNo)} مکمل ہوگا۔", "8 correct answers complete Stage $stageNo."), size = 14.sp, color = Quiet, align = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { seed = System.currentTimeMillis() }, colors = ButtonDefaults.buttonColors(containerColor = Green),
                modifier = Modifier.testTag("qaida_quiz_retry")) { UrduText("دوبارہ مشق", size = 15.sp, color = Color.White) }
            TextButton(onClick = onDone) { UrduText("اسباق کی فہرست", size = 15.sp, color = Green) }
        }
        return
    }

    val q = questions[index]
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        UrduText(tx("سوال ${index + 1} / ${questions.size}   ·   درست: $score", "Question ${index + 1} / ${questions.size}   ·   correct: $score"), size = 13.sp, color = Quiet)
        Spacer(Modifier.height(8.dp))
        UrduText(q.instruction, size = 17.sp, weight = FontWeight.Bold, align = TextAlign.Center)
        Card(Modifier.padding(vertical = 12.dp).widthIn(min = 160.dp).clickable { q.audioKey?.let { play(it, q.prompt) } }
            .testTag("qaida_quiz_prompt"), colors = CardDefaults.cardColors(containerColor = GreenSoft)) {
            Box(Modifier.padding(horizontal = 32.dp, vertical = 12.dp), contentAlignment = Alignment.Center) {
                if (q.promptIsArabic) ArabicText(q.prompt, size = if (q.prompt.length > 12) 30.sp else 64.sp) else UrduText(q.prompt, size = 30.sp, weight = FontWeight.Bold)
            }
        }
        q.options.forEachIndexed { i, opt ->
            val bg = when {
                chosen == null -> Color.White
                i == q.correctIndex -> Color(0xFFC8E6C9)
                i == chosen -> Color(0xFFFFCDD2)
                else -> Color.White
            }
            Card(
                Modifier.fillMaxWidth().padding(vertical = 4.dp).heightIn(min = 56.dp)
                    .clickable(enabled = chosen == null) { // one answer per question; repeat taps ignored
                        chosen = i; if (i == q.correctIndex) score++
                    }.testTag("qaida_option_$i"),
                colors = CardDefaults.cardColors(containerColor = bg), border = BorderStroke(1.dp, Color(0xFFE2E5EA)),
            ) {
                Box(Modifier.fillMaxWidth().padding(10.dp), contentAlignment = Alignment.Center) {
                    if (q.optionsAreArabic) ArabicText(opt, size = 34.sp) else UrduText(opt, size = 18.sp, align = TextAlign.Center)
                }
            }
        }
        if (chosen != null) {
            UrduText(if (chosen == q.correctIndex) "بہت خوب! ✓" else "درست جواب سبز رنگ میں ہے۔", size = 15.sp,
                color = if (chosen == q.correctIndex) Green else Heavy, align = TextAlign.Center)
            Button(onClick = {
                if (index + 1 >= questions.size) { finished = true; onFinished(score) } else index++
            }, colors = ButtonDefaults.buttonColors(containerColor = Green), modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("qaida_next")) {
                UrduText(if (index + 1 >= questions.size) "نتیجہ دیکھیں" else "اگلا سوال", size = 15.sp, color = Color.White, weight = FontWeight.Bold)
            }
        }
    }
}

internal const val STAGE_COUNT = 11

internal fun urduDigit(n: Int) = QaidaLang.digits(n)

/** "Best score" / "10 questions" line under a stage quiz card. */
internal fun quizLine(best: Int, stage: Int?): String {
    val st = stage?.let { " ${urduDigit(it)}" } ?: ""
    return if (best > 0) tx("بہترین نتیجہ: $best/10  ·  ۸ درست پر مرحلہ$st مکمل", "Best score: $best/10  ·  8 correct completes the stage")
    else tx("۱۰ سوال  ·  ۸ درست پر مرحلہ$st مکمل", "10 questions  ·  8 correct completes the stage")
}
