package com.example.qaida

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

// ---------------------------------------------------------------------------------------
// Typography: Urdu in Nastaliq (needs generous line height), Arabic in a Qaida-style Naskh.
// Both fonts are SIL Open Font License (see assets/licenses).
// ---------------------------------------------------------------------------------------
val UrduFont = FontFamily(Font(R.font.noto_nastaliq_urdu))
val QaidaArabicFont = FontFamily(Font(R.font.noto_naskh_arabic))

private val Ink = Color(0xFF1F2A44)
private val Quiet = Color(0xFF5B6475)
private val Green = Color(0xFF1B5E20)
private val GreenSoft = Color(0xFFE8F5E9)
private val Amber = Color(0xFFFFF4D6)
private val AmberLine = Color(0xFFE0A800)
private val Paper = Color(0xFFFFFCF5)
private val Heavy = Color(0xFFB23B1E)

@Composable
fun UrduText(
    text: String, modifier: Modifier = Modifier, size: TextUnit = 16.sp, color: Color = Ink,
    weight: FontWeight = FontWeight.Normal, align: TextAlign = TextAlign.Start,
) = Text(text, modifier, color = color, fontSize = size, fontFamily = UrduFont, fontWeight = weight,
    textAlign = align, lineHeight = size * 2.1f)

@Composable
fun ArabicText(
    text: String, modifier: Modifier = Modifier, size: TextUnit = 32.sp, color: Color = Ink,
    align: TextAlign = TextAlign.Center,
) = Text(text, modifier, color = color, fontSize = size, fontFamily = QaidaArabicFont, fontWeight = FontWeight.Bold,
    textAlign = align, lineHeight = size * 1.7f)

/** Entry point for the Urdu-medium Noorani Qaida (Stage 1). Whole section is right-to-left. */
@Composable
fun QaidaScreen(
    profileId: Int,
    onBackPressed: () -> Unit,
    onSpeakArabic: (String) -> Unit,              // TTS fallback until the qari's recordings are added
    onEarnRewards: (Int, Int, String) -> Unit,
) {
    val context = LocalContext.current
    val progress = remember(profileId) { QaidaProgress(context, profileId) }
    val audio = remember { QaidaAudio(context) }
    DisposableEffect(Unit) { onDispose { audio.stop() } }
    var page by rememberSaveable { mutableStateOf("home") }
    var refresh by remember { mutableIntStateOf(0) } // bump to re-read progress

    val play: (String, String) -> Unit = { key, fallbackText -> if (!audio.play(key)) onSpeakArabic(fallbackText) }
    val recordingsPresent = remember { audio.hasRecording(QaidaData.letters.first().audioKey) }

    BackHandler(enabled = page != "home") { page = "home"; refresh++ }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(Modifier.fillMaxSize().background(Paper).testTag("qaida_root")) {
            val title = when (page) {
                "letters" -> "سبق ۱: حروفِ مفردات"
                "sounds" -> "سبق ۲: ملتی جلتی آوازیں"
                "harakat" -> "سبق ۳: حرکات — زبر، زیر، پیش"
                "quiz" -> "مشق"
                else -> "نورانی قاعدہ"
            }
            QaidaHeader(title, onBack = { if (page == "home") onBackPressed() else { page = "home"; refresh++ } })
            if (!recordingsPresent) TemporaryAudioBanner()
            Box(Modifier.weight(1f)) {
                when (page) {
                    "letters" -> LettersLesson(progress, play) {
                        progress.markLessonDone(QaidaProgress.LESSON_LETTERS); onEarnRewards(5, 20, "Qaida: 29 letters")
                    }
                    "sounds" -> SoundsLesson(play) {
                        progress.markLessonDone(QaidaProgress.LESSON_SOUNDS); onEarnRewards(5, 20, "Qaida: sound pairs")
                        page = "home"; refresh++
                    }
                    "harakat" -> HarakatLesson(play) {
                        progress.markLessonDone(QaidaProgress.LESSON_HARAKAT); onEarnRewards(5, 20, "Qaida: harakat")
                        page = "home"; refresh++
                    }
                    "quiz" -> QaidaQuizScreen(play, onFinished = { score ->
                        progress.recordQuizScore(score)
                        if (score >= QaidaProgress.PASS_MARK) onEarnRewards(10, 50, "Qaida: Stage 1 quiz passed")
                        refresh++
                    }, onDone = { page = "home"; refresh++ })
                    else -> QaidaHome(progress, refresh) { page = it }
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
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "واپس", tint = Color.White)
        }
        UrduText(title, size = 18.sp, color = Color.White, weight = FontWeight.Bold)
    }
}

@Composable
private fun TemporaryAudioBanner() {
    Row(Modifier.fillMaxWidth().background(Amber).padding(horizontal = 16.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        UrduText("🔈 عارضی آواز: قاری صاحب کی ریکارڈنگ جلد شامل ہوگی۔ ابھی فون کی آواز ہے جو درست مخرج نہیں بتاتی۔", size = 12.sp, color = Quiet)
    }
}

// ---------------------------------------------------------------------------------------
// Lesson list
// ---------------------------------------------------------------------------------------
@Composable
private fun QaidaHome(progress: QaidaProgress, refreshKey: Int, open: (String) -> Unit) {
    val seen = remember(refreshKey) { progress.seenLetters().size }
    val best = remember(refreshKey) { progress.bestQuizScore() }
    // refreshKey changes whenever a lesson closes, so these reads are fresh on every return here
    val done = { l: String -> refreshKey >= 0 && progress.isLessonDone(l) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(colors = CardDefaults.cardColors(containerColor = GreenSoft), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                ArabicText("ا ب ت", size = 40.sp, color = Green)
                UrduText("مرحلہ ۱: حروف اور پہلی آوازیں", size = 18.sp, weight = FontWeight.Bold, align = TextAlign.Center)
                UrduText("روزانہ ۵ سے ۱۰ منٹ۔ ہر سبق کے بعد مشق کریں۔", size = 13.sp, color = Quiet, align = TextAlign.Center)
            }
        }
        LessonCard("lesson_letters", "۱", "حروفِ مفردات", "قاعدے کے ۲۹ حروف، ان کے نام اور مخارج  ·  $seen/29 دیکھے",
            done(QaidaProgress.LESSON_LETTERS)) { open("letters") }
        LessonCard("lesson_sounds", "۲", "ملتی جلتی آوازیں", "وہ حروف جو اردو میں ایک جیسے بولے جاتے ہیں: س ث ص، ز ذ ظ ض، ت ط، ہ ح، ء ع، ک ق",
            done(QaidaProgress.LESSON_SOUNDS)) { open("sounds") }
        LessonCard("lesson_harakat", "۳", "حرکات: زبر، زیر، پیش", "تختی ۴ — ہر حرف تین حرکتوں کے ساتھ، ہجے سمیت",
            done(QaidaProgress.LESSON_HARAKAT)) { open("harakat") }
        LessonCard("lesson_quiz", "۴", "مشق", if (best > 0) "بہترین نتیجہ: $best/10  ·  ۸ درست پر مرحلہ ۱ مکمل" else "۱۰ سوال  ·  ۸ درست پر مرحلہ ۱ مکمل",
            done(QaidaProgress.LESSON_QUIZ)) { open("quiz") }
        if (done(QaidaProgress.LESSON_QUIZ)) {
            Card(colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine)) {
                UrduText("ماشاء اللہ! مرحلہ ۱ مکمل۔ مرحلہ ۲ (حروف ملا کر پڑھنا) جلد آ رہا ہے۔", Modifier.padding(16.dp), size = 15.sp, weight = FontWeight.Bold)
            }
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
        UrduText("حرف پر ٹیپ کریں، نام سنیں اور مخرج پڑھیں۔ موٹے (پُر) حروف سرخ ہیں۔", Modifier.padding(horizontal = 16.dp, vertical = 8.dp), size = 13.sp, color = Quiet)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(72.dp),
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp).testTag("qaida_letters_grid"),
            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
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
        }
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
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Column(Modifier.verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                    ArabicText(l.arabic, size = 88.sp, color = if (l.heavy) Heavy else Ink)
                    UrduText("${l.qaidaName}  ·  ${l.roman}", size = 18.sp, weight = FontWeight.Bold, align = TextAlign.Center)
                    if (l.heavy) UrduText("پُر (موٹا) حرف", size = 12.sp, color = Heavy, align = TextAlign.Center)
                    Spacer(Modifier.height(4.dp))
                    PlayButton("نام سنیں") { play(l.audioKey, l.arabic) }
                    Spacer(Modifier.height(8.dp))
                    InfoBox("${l.region.emoji} مخرج: ${l.region.urdu}", l.makhraj, GreenSoft, Green)
                    l.urduNote?.let { Spacer(Modifier.height(8.dp)); InfoBox("⚠️ اردو بولنے والوں کے لیے", it, Amber, AmberLine) }
                    l.urduShape?.let {
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
private fun InfoBox(title: String, body: String, bg: Color, line: Color) {
    Column(Modifier.fillMaxWidth().background(bg, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
        UrduText(title, size = 13.sp, color = line, weight = FontWeight.Bold)
        UrduText(body, size = 14.sp)
    }
}

@Composable
private fun PlayButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
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
    LazyColumn(Modifier.fillMaxSize().testTag("qaida_sounds_list"), contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            UrduText("اردو میں کئی عربی حروف ایک جیسے بولے جاتے ہیں۔ قرآن پڑھتے وقت ہر حرف کو اس کی اپنی جگہ سے ادا کرنا ضروری ہے، ورنہ لفظ کا معنی بدل سکتا ہے۔ ہر حرف اور مثال کو سنیں اور فرق پہچانیں۔",
                size = 14.sp, color = Quiet)
        }
        items(QaidaData.soundGroups, key = { it.id }) { g ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E5EA)),
                modifier = Modifier.testTag("qaida_group_${g.id}")) {
                Column(Modifier.padding(14.dp)) {
                    UrduText("اردو میں سب '${g.urduSays}'", size = 16.sp, weight = FontWeight.Bold, color = Green)
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
                                UrduText(l.qaidaName + if (l.heavy) "  (پُر)" else "", size = 13.sp, weight = FontWeight.Bold)
                                UrduText("مثال: ${ex.urduMeaning}", size = 12.sp, color = Quiet)
                            }
                            Box(Modifier.clickable { play(ex.audioKey, ex.arabic) }.padding(horizontal = 8.dp)
                                .semantics { contentDescription = "مثال سنیں: ${ex.urduMeaning}" }) {
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
    LazyColumn(Modifier.fillMaxSize().testTag("qaida_harakat_list"), contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
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
        }
        item { UrduText("ہر خانے پر ٹیپ کر کے ہجے سنیں، پھر پوری سطر خود پڑھیں: بَا زَبَر بَ، بَا زِیر بِ، بَا پیش بُ", size = 13.sp, color = Quiet) }
        items(letters, key = { it.id }) { l ->
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
        }
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
private fun QaidaQuizScreen(play: (String, String) -> Unit, onFinished: (Int) -> Unit, onDone: () -> Unit) {
    var seed by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    val questions = remember(seed) { QaidaQuiz.generate(Random(seed)) }
    var index by rememberSaveable(seed) { mutableIntStateOf(0) }
    var score by rememberSaveable(seed) { mutableIntStateOf(0) }
    var chosen by rememberSaveable(seed, index) { mutableStateOf<Int?>(null) }
    var finished by rememberSaveable(seed) { mutableStateOf(false) }

    if (finished) {
        val passed = score >= QaidaProgress.PASS_MARK
        Column(Modifier.fillMaxSize().padding(24.dp).testTag("qaida_quiz_result"), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            UrduText(if (passed) "ماشاء اللہ! 🌟" else "شاباش، دوبارہ کوشش کریں 💪", size = 24.sp, weight = FontWeight.Bold, align = TextAlign.Center)
            UrduText("$score / ${questions.size} درست", size = 20.sp, color = Green, align = TextAlign.Center)
            UrduText(if (passed) "مرحلہ ۱ مکمل ہو گیا۔" else "۸ درست جواب پر مرحلہ ۱ مکمل ہوگا۔", size = 14.sp, color = Quiet, align = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { seed = System.currentTimeMillis() }, colors = ButtonDefaults.buttonColors(containerColor = Green),
                modifier = Modifier.testTag("qaida_quiz_retry")) { UrduText("دوبارہ مشق", size = 15.sp, color = Color.White) }
            TextButton(onClick = onDone) { UrduText("اسباق کی فہرست", size = 15.sp, color = Green) }
        }
        return
    }

    val q = questions[index]
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        UrduText("سوال ${index + 1} / ${questions.size}   ·   درست: $score", size = 13.sp, color = Quiet)
        Spacer(Modifier.height(8.dp))
        UrduText(q.instruction, size = 17.sp, weight = FontWeight.Bold, align = TextAlign.Center)
        Card(Modifier.padding(vertical = 12.dp).widthIn(min = 160.dp).clickable { q.audioKey?.let { play(it, q.prompt) } }
            .testTag("qaida_quiz_prompt"), colors = CardDefaults.cardColors(containerColor = GreenSoft)) {
            Box(Modifier.padding(horizontal = 32.dp, vertical = 12.dp), contentAlignment = Alignment.Center) {
                if (q.promptIsArabic) ArabicText(q.prompt, size = 64.sp) else UrduText(q.prompt, size = 30.sp, weight = FontWeight.Bold)
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
