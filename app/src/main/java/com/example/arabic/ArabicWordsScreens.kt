package com.example.arabic

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qaida.Amber
import com.example.qaida.AmberLine
import com.example.qaida.Green
import com.example.qaida.GreenSoft
import com.example.qaida.Ink
import com.example.qaida.Paper
import com.example.qaida.QaidaArabicFont
import com.example.qaida.Quiet
import com.example.qaida.UrduFont
import kotlin.random.Random

private val Line = Color(0xFFE2E5EA)

/** Progress for the Arabic words course, per child. */
class ArabicWordsProgress(context: Context, profileId: Int) {
    private val prefs = context.applicationContext.getSharedPreferences("arabic_words_$profileId", Context.MODE_PRIVATE)
    fun isDone(key: String) = prefs.getBoolean("done_$key", false)
    fun markDone(key: String) = prefs.edit().putBoolean("done_$key", true).apply()
    fun best(level: Int) = prefs.getInt("best_$level", 0)
    fun record(level: Int, score: Int) {
        if (score > best(level)) prefs.edit().putInt("best_$level", score).apply()
        if (score >= PASS_MARK) markDone(ArabicWords.level(level).quizKey)
    }
    companion object { const val QUIZ_LENGTH = 10; const val PASS_MARK = 8 }
}

data class ArQuestion(val prompt: String, val promptKind: Kind, val instruction: String, val instructionUrdu: String,
                      val options: List<ArWord>, val optionsKind: Kind, val correctIndex: Int) {
    enum class Kind { ARABIC, MEANING, LISTEN }
}

object ArabicWordsQuiz {
    fun generate(level: Int, random: Random, length: Int = ArabicWordsProgress.QUIZ_LENGTH): List<ArQuestion> {
        val pool = ArabicWords.level(level).words
        return List(length) { n ->
            val answer = pool.random(random)
            // wrong options come from the same set first (closest words), never with the same meaning
            val near = ArabicWords.levels.flatMap { it.sets }.first { answer in it.words }.words
            val wrong = (near.shuffled(random) + pool.shuffled(random))
                .filter { it.english != answer.english && it.urdu != answer.urdu && it.arabic != answer.arabic }
                .distinctBy { it.english }.take(3)
            val opts = (wrong + answer).shuffled(random)
            when (n % 3) {
                0 -> ArQuestion(answer.arabic, ArQuestion.Kind.ARABIC, "What does this word mean?", "اس لفظ کا مطلب کیا ہے؟",
                    opts, ArQuestion.Kind.MEANING, opts.indexOf(answer))
                1 -> ArQuestion("${answer.emoji}  ${answer.english}", ArQuestion.Kind.MEANING, "Which is the Arabic for this?",
                    "اس کی عربی کون سی ہے؟", opts, ArQuestion.Kind.ARABIC, opts.indexOf(answer))
                else -> ArQuestion(answer.arabic, ArQuestion.Kind.LISTEN, "Listen, then tap the word you hear.",
                    "سنیں، پھر وہ لفظ چنیں جو آپ نے سنا", opts, ArQuestion.Kind.ARABIC, opts.indexOf(answer))
            }
        }
    }
}

@Composable
private fun T(text: String, modifier: Modifier = Modifier, size: TextUnit = 15.sp, color: Color = Ink,
              weight: FontWeight = FontWeight.Normal, align: TextAlign = TextAlign.Start, urdu: Boolean = false) =
    if (urdu) Text(text, modifier, color = color, fontSize = size, fontFamily = UrduFont, fontWeight = weight,
        textAlign = align, lineHeight = size * 2.0f)
    else Text(text, modifier, color = color, fontSize = size, fontWeight = weight, textAlign = align, lineHeight = size * 1.35f)

@Composable
private fun AR(text: String, modifier: Modifier = Modifier, size: TextUnit = 34.sp, color: Color = Ink) =
    Text(text, modifier, color = color, fontSize = size, fontFamily = QaidaArabicFont, fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center, lineHeight = size * 1.6f)

/**
 * Arabic words course: levels from first nouns to short sentences. Flip cards show the Arabic on one
 * side and the English on the other, with the Urdu meaning in brackets on both sides.
 * [urdu] = teach in Urdu (right to left) or in English (left to right), following the dashboard.
 */
@Composable
fun ArabicWordsScreen(
    profileId: Int,
    urdu: Boolean,
    onBackPressed: () -> Unit,
    onSpeakArabic: (String) -> Unit,
    onEarnRewards: (Int, Int, String) -> Unit,
    startLevel: Int = 1,
) {
    val context = LocalContext.current
    val progress = remember(profileId) { ArabicWordsProgress(context, profileId) }
    var level by rememberSaveable { mutableIntStateOf(startLevel.coerceIn(1, ArabicWords.levels.size)) }
    var page by rememberSaveable { mutableStateOf("home") }
    var refresh by remember { mutableIntStateOf(0) }
    val lv = ArabicWords.level(level)
    val set = lv.sets.firstOrNull { it.id == page }
    val L = { en: String, ur: String -> if (urdu) ur else en }
    BackHandler(enabled = page != "home") { page = "home"; refresh++ }

    CompositionLocalProvider(LocalLayoutDirection provides (if (urdu) LayoutDirection.Rtl else LayoutDirection.Ltr)) {
        Column(Modifier.fillMaxSize().background(Paper).testTag("arabic_words_root")) {
            Row(Modifier.fillMaxWidth().background(Green).padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (page == "home") onBackPressed() else { page = "home"; refresh++ } }, modifier = Modifier.testTag("arabic_words_back")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = L("Back", "واپس"), tint = Color.White)
                }
                T(when {
                    page == "quiz" -> L("Level $level practice", "درجہ $level کی مشق")
                    set != null -> L(set.title, set.titleUrdu)
                    else -> L("Arabic words", "عربی الفاظ")
                }, size = 18.sp, color = Color.White, weight = FontWeight.Bold, urdu = urdu)
            }
            Box(Modifier.weight(1f)) {
                when {
                    page == "quiz" -> QuizScreen(level, urdu, onSpeakArabic, onFinished = { score ->
                        progress.record(level, score)
                        if (score >= ArabicWordsProgress.PASS_MARK) onEarnRewards(10, 50, "Arabic words: level $level practice passed")
                        refresh++
                    }, onDone = { page = "home"; refresh++ })
                    set != null -> CardsScreen(set, urdu, onSpeakArabic) {
                        progress.markDone(set.id); onEarnRewards(5, 20, "Arabic words: ${set.title}")
                        page = "home"; refresh++
                    }
                    else -> Home(progress, refresh, level, urdu, onLevel = { level = it }) { page = it }
                }
            }
        }
    }
}

@Composable
private fun Home(progress: ArabicWordsProgress, refreshKey: Int, level: Int, urdu: Boolean, onLevel: (Int) -> Unit, open: (String) -> Unit) {
    val done = { k: String -> refreshKey >= 0 && progress.isDone(k) }
    val L = { en: String, ur: String -> if (urdu) ur else en }
    val lv = ArabicWords.level(level)
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(24.dp)).horizontalScroll(rememberScrollState()).padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            ArabicWords.levels.forEach { l ->
                val on = l.number == level
                Box(Modifier.widthIn(min = 64.dp).heightIn(min = 44.dp).background(if (on) Green else Color.Transparent, RoundedCornerShape(20.dp))
                    .clickable { onLevel(l.number) }.testTag("arabic_level_tab_${l.number}"), contentAlignment = Alignment.Center) {
                    T(L("Level ${l.number}", "درجہ ${l.number}"), Modifier.padding(horizontal = 8.dp), size = 13.sp,
                        color = if (on) Color.White else Green, weight = FontWeight.Bold, align = TextAlign.Center, urdu = urdu)
                }
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = lv.bg), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                AR(lv.sets.first().words.first().arabic, size = 38.sp, color = lv.line)
                T(L("Level $level: ${lv.title}", "درجہ $level: ${lv.titleUrdu}"), size = 18.sp, weight = FontWeight.Bold, align = TextAlign.Center, urdu = urdu)
                T(L("${lv.band} · ${lv.words.size} words · tap a card to turn it", "${lv.bandUrdu} · ${lv.words.size} الفاظ · کارڈ پلٹنے کے لیے دبائیں"),
                    size = 13.sp, color = Quiet, align = TextAlign.Center, urdu = urdu)
            }
        }
        val prev = ArabicWords.levels.firstOrNull { it.number == level - 1 }
        if (prev != null && !done(prev.quizKey)) {
            Card(colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine), modifier = Modifier.testTag("arabic_level_hint")) {
                T(L("Tip: finish the Level ${prev.number} practice first. You can still start now if you like.",
                    "مشورہ: پہلے درجہ ${prev.number} کی مشق مکمل کر لیں۔ آپ چاہیں تو ابھی بھی شروع کر سکتے ہیں۔"),
                    Modifier.padding(12.dp), size = 13.sp, urdu = urdu)
            }
        }
        lv.sets.forEachIndexed { i, s ->
            Row_(tag = "arabic_set_${s.id}", number = i + 1, title = L(s.title, s.titleUrdu) + "  " + s.emoji,
                subtitle = s.words.take(4).joinToString("  ·  ") { it.arabic } + " …", done = done(s.id), urdu = urdu) { open(s.id) }
        }
        val best = progress.best(level)
        Row_(tag = "arabic_quiz_$level", number = lv.sets.size + 1, title = L("Level $level practice", "درجہ $level کی مشق"),
            subtitle = if (best > 0) L("Best score: $best/10 · 8 correct completes the level", "بہترین نتیجہ: $best/10 · 8 درست پر درجہ مکمل")
            else L("10 questions · 8 correct completes the level", "10 سوال · 8 درست پر درجہ مکمل"), done = done(lv.quizKey), urdu = urdu) { open("quiz") }
        if (done(lv.quizKey)) {
            val next = ArabicWords.levels.firstOrNull { it.number == level + 1 }
            Card(Modifier.clickable { next?.let { onLevel(it.number) } }.testTag("arabic_go_next_$level"),
                colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine)) {
                T(if (next != null) L("Masha'Allah! Level $level complete. Now start Level ${next.number}: ${next.title} →",
                    "ماشاء اللہ! درجہ $level مکمل۔ اب درجہ ${next.number} شروع کریں: ${next.titleUrdu} ←")
                else L("Masha'Allah! You have learned all the Arabic words. Keep revising a few cards every day!",
                    "ماشاء اللہ! آپ نے سارے عربی الفاظ سیکھ لیے۔ روزانہ چند کارڈ دہراتے رہیں!"),
                    Modifier.padding(16.dp), size = 15.sp, weight = FontWeight.Bold, urdu = urdu)
            }
        }
    }
}

@Composable
private fun Row_(tag: String, number: Int, title: String, subtitle: String, done: Boolean, urdu: Boolean, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick).testTag(tag), colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (done) Green else Line), shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).background(if (done) Green else GreenSoft, CircleShape), contentAlignment = Alignment.Center) {
                T(if (done) "✓" else number.toString(), size = 17.sp, color = if (done) Color.White else Green, weight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                T(title, size = 17.sp, weight = FontWeight.Bold, urdu = urdu)
                Text(subtitle, color = Quiet, fontSize = 15.sp, fontFamily = QaidaArabicFont, maxLines = 1)
            }
        }
    }
}

/** One card at a time. Front: Arabic (Urdu). Tap: English (Urdu). The voice reads the Arabic. */
@Composable
private fun CardsScreen(set: ArSet, urdu: Boolean, speak: (String) -> Unit, onDone: () -> Unit) {
    val L = { en: String, ur: String -> if (urdu) ur else en }
    var index by rememberSaveable(set.id) { mutableIntStateOf(0) }
    var known by rememberSaveable(set.id) { mutableIntStateOf(0) }
    var showEnglish by rememberSaveable(set.id, index) { mutableStateOf(false) }
    var englishFirst by rememberSaveable { mutableStateOf(false) }
    val turn by animateFloatAsState(if (showEnglish) 180f else 0f, tween(350), label = "flip")

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (index >= set.words.size) {
            T(L("Masha'Allah! 🌟", "ماشاء اللہ! 🌟"), size = 24.sp, weight = FontWeight.Bold, urdu = urdu)
            T(L("$known / ${set.words.size} known", "$known / ${set.words.size} یاد تھے"), size = 20.sp, color = Green, urdu = urdu,
                modifier = Modifier.testTag("arabic_cards_result"))
            Spacer(Modifier.height(12.dp))
            set.words.forEach { w ->
                Row(Modifier.fillMaxWidth().padding(vertical = 3.dp).background(Color.White, RoundedCornerShape(12.dp)).clickable { speak(w.arabic) }
                    .padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    T("${w.emoji}  ${w.english}", Modifier.weight(1f), size = 14.sp)
                    T("(${w.urdu})", size = 13.sp, color = Quiet, urdu = true)
                    Spacer(Modifier.width(8.dp))
                    AR(w.arabic, size = 22.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { index = 0; known = 0 }, border = BorderStroke(1.dp, Green)) { T(L("Go through again ↺", "دوبارہ دہرائیں ↺"), color = Green, urdu = urdu) }
            Button(onClick = onDone, colors = ButtonDefaults.buttonColors(containerColor = Green),
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("arabic_cards_done")) {
                T(L("I have learned these words ✓", "میں نے یہ الفاظ سیکھ لیے ✓"), color = Color.White, weight = FontWeight.Bold, urdu = urdu)
            }
            return@Column
        }
        val w = set.words[index]
        T(L("Card ${index + 1} / ${set.words.size}   ·   known: $known", "کارڈ ${index + 1} / ${set.words.size}   ·   یاد تھے: $known"),
            size = 13.sp, color = Quiet, urdu = urdu)
        T(L("Tap the card to turn it. 🔊 reads the Arabic.", "کارڈ پلٹنے کے لیے دبائیں۔ 🔊 عربی پڑھ کر سناتا ہے۔"), size = 13.sp, color = Quiet, urdu = urdu,
            align = TextAlign.Center)
        Spacer(Modifier.height(10.dp))
        val arabicSide = showEnglish == englishFirst
        Card(Modifier.fillMaxWidth().heightIn(min = 260.dp).graphicsLayer { rotationY = turn; cameraDistance = 12 * density }
            .clickable { showEnglish = !showEnglish; if (showEnglish == englishFirst) speak(w.arabic) }   // read the Arabic when its side comes up
            .testTag("arabic_card").semantics { contentDescription = "${w.arabic} — ${w.english} (${w.urdu})" },
            colors = CardDefaults.cardColors(containerColor = if (arabicSide) Color.White else GreenSoft),
            border = BorderStroke(2.dp, Green), shape = RoundedCornerShape(24.dp)) {
            // mirror the back face so its text is not reversed after the turn
            Column(Modifier.fillMaxWidth().heightIn(min = 260.dp).graphicsLayer { rotationY = if (turn > 90f) 180f else 0f }.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                if (arabicSide) {
                    AR(w.arabic, size = if (w.arabic.length > 14) 34.sp else 48.sp, modifier = Modifier.testTag("arabic_card_arabic"))
                    T(w.translit, size = 16.sp, color = Quiet, align = TextAlign.Center)
                    T("(${w.urdu})", size = 20.sp, color = Color(0xFF6A1B9A), align = TextAlign.Center, urdu = true,
                        modifier = Modifier.testTag("arabic_card_urdu"))
                } else {
                    T(w.emoji, size = 52.sp)
                    T(w.english, size = 28.sp, weight = FontWeight.Bold, color = Green, align = TextAlign.Center,
                        modifier = Modifier.testTag("arabic_card_english"))
                    T("(${w.urdu})", size = 20.sp, color = Color(0xFF6A1B9A), align = TextAlign.Center, urdu = true,
                        modifier = Modifier.testTag("arabic_card_urdu"))
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { speak(w.arabic) }, modifier = Modifier.testTag("arabic_card_speak")) { T("🔊 " + L("Hear it", "سنیں"), color = Green, urdu = urdu) }
            TextButton(onClick = { englishFirst = !englishFirst; showEnglish = false }, modifier = Modifier.testTag("arabic_card_side")) {
                T(if (englishFirst) L("Start with Arabic", "پہلے عربی دکھائیں") else L("Start with English", "پہلے انگریزی دکھائیں"), color = Green, size = 13.sp, urdu = urdu)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { known++; index++ }, modifier = Modifier.weight(1f).heightIn(min = 52.dp).testTag("arabic_card_knew"),
                colors = ButtonDefaults.buttonColors(containerColor = Green)) { T(L("I knew it ✓", "یاد تھا ✓"), color = Color.White, weight = FontWeight.Bold, urdu = urdu) }
            OutlinedButton(onClick = { index++ }, modifier = Modifier.weight(1f).heightIn(min = 52.dp).testTag("arabic_card_notyet"),
                border = BorderStroke(1.dp, Green)) { T(L("Not yet ↺", "ابھی نہیں ↺"), color = Green, urdu = urdu) }
        }
    }
}

@Composable
private fun QuizScreen(level: Int, urdu: Boolean, speak: (String) -> Unit, onFinished: (Int) -> Unit, onDone: () -> Unit) {
    val L = { en: String, ur: String -> if (urdu) ur else en }
    var seed by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    val questions = remember(seed) { ArabicWordsQuiz.generate(level, Random(seed)) }
    var index by rememberSaveable(seed) { mutableIntStateOf(0) }
    var score by rememberSaveable(seed) { mutableIntStateOf(0) }
    var chosen by rememberSaveable(seed, index) { mutableStateOf<Int?>(null) }
    var finished by rememberSaveable(seed) { mutableStateOf(false) }

    if (finished) {
        val passed = score >= ArabicWordsProgress.PASS_MARK
        Column(Modifier.fillMaxSize().padding(24.dp).testTag("arabic_quiz_result"), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            T(if (passed) L("Masha'Allah! 🌟", "ماشاء اللہ! 🌟") else L("Well done — try again 💪", "شاباش — دوبارہ کوشش کریں 💪"), size = 24.sp, weight = FontWeight.Bold, urdu = urdu)
            T(L("$score / ${questions.size} correct", "$score / ${questions.size} درست"), size = 20.sp, color = Green, urdu = urdu)
            T(if (passed) L("Level $level is complete.", "درجہ $level مکمل ہو گیا۔") else L("8 correct answers complete Level $level.", "8 درست جواب پر درجہ $level مکمل ہوگا۔"),
                size = 14.sp, color = Quiet, urdu = urdu)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { seed = System.currentTimeMillis() }, colors = ButtonDefaults.buttonColors(containerColor = Green),
                modifier = Modifier.testTag("arabic_quiz_retry")) { T(L("Practise again", "دوبارہ مشق کریں"), color = Color.White, urdu = urdu) }
            TextButton(onClick = onDone) { T(L("List of lessons", "اسباق کی فہرست"), color = Green, urdu = urdu) }
        }
        return
    }
    val q = questions[index]
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        T(L("Question ${index + 1} / ${questions.size}   ·   correct: $score", "سوال ${index + 1} / ${questions.size}   ·   درست: $score"),
            size = 13.sp, color = Quiet, urdu = urdu)
        T(L(q.instruction, q.instructionUrdu), size = 17.sp, weight = FontWeight.Bold, align = TextAlign.Center, urdu = urdu)
        Card(Modifier.padding(vertical = 12.dp).widthIn(min = 180.dp).clickable { speak(q.options[q.correctIndex].arabic) }.testTag("arabic_quiz_prompt"),
            colors = CardDefaults.cardColors(containerColor = GreenSoft)) {
            Column(Modifier.padding(horizontal = 24.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                when (q.promptKind) {
                    ArQuestion.Kind.ARABIC -> AR(q.prompt, size = 40.sp)
                    ArQuestion.Kind.LISTEN -> T("🔊", size = 52.sp)
                    ArQuestion.Kind.MEANING -> {
                        T(q.prompt, size = 26.sp, weight = FontWeight.Bold, align = TextAlign.Center)
                        T("(${q.options[q.correctIndex].urdu})", size = 18.sp, color = Color(0xFF6A1B9A), urdu = true)
                    }
                }
            }
        }
        q.options.forEachIndexed { i, o ->
            val bg = when { chosen == null -> Color.White; i == q.correctIndex -> Color(0xFFC8E6C9); i == chosen -> Color(0xFFFFCDD2); else -> Color.White }
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp).heightIn(min = 56.dp)
                .clickable(enabled = chosen == null) { chosen = i; if (i == q.correctIndex) score++ }.testTag("arabic_option_$i"),
                colors = CardDefaults.cardColors(containerColor = bg), border = BorderStroke(1.dp, Line)) {
                Column(Modifier.fillMaxWidth().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (q.optionsKind == ArQuestion.Kind.ARABIC) {
                        AR(o.arabic, size = 28.sp)
                        if (chosen != null) T("${o.english}  (${o.urdu})", size = 13.sp, color = Quiet)
                    } else {
                        T(o.english, size = 20.sp, align = TextAlign.Center)
                        T("(${o.urdu})", size = 15.sp, color = Color(0xFF6A1B9A), urdu = true)
                    }
                }
            }
        }
        if (chosen != null) {
            T(if (chosen == q.correctIndex) L("Excellent! ✓", "بہت خوب! ✓") else L("The correct answer is in green.", "درست جواب سبز رنگ میں ہے۔"),
                size = 15.sp, color = if (chosen == q.correctIndex) Green else Color(0xFFB23B1E), urdu = urdu)
            Button(onClick = { if (index + 1 >= questions.size) { finished = true; onFinished(score) } else index++ },
                colors = ButtonDefaults.buttonColors(containerColor = Green), modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("arabic_next")) {
                T(if (index + 1 >= questions.size) L("See the result", "نتیجہ دیکھیں") else L("Next question", "اگلا سوال"), color = Color.White, weight = FontWeight.Bold, urdu = urdu)
            }
        }
    }
}
