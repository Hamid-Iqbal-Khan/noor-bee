package com.example.english

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qaida.Amber
import com.example.qaida.AmberLine
import com.example.qaida.Green
import com.example.qaida.GreenSoft
import com.example.qaida.Ink
import com.example.qaida.Paper
import com.example.qaida.Quiet
import kotlin.random.Random

private val Edge = Color(0xFFE2E5EA)
private val Tile = Color(0xFFFFF8E1)
private val TileInk = Color(0xFF5D4037)

@Composable
private fun T(text: String, modifier: Modifier = Modifier, size: TextUnit = 15.sp, color: Color = Ink,
              weight: FontWeight = FontWeight.Normal, align: TextAlign = TextAlign.Start) =
    Text(text, modifier, color = color, fontSize = size, fontWeight = weight, textAlign = align, lineHeight = size * 1.35f)

/** English reading course: stage tabs, lesson list, lessons and practice. Left to right. */
@Composable
fun EnglishScreen(
    profileId: Int,
    onBackPressed: () -> Unit,
    onSpeak: (String) -> Unit,                     // English voice
    onEarnRewards: (Int, Int, String) -> Unit,
    startStage: Int = 1,
) {
    // a real recording when there is one, else the phone's voice
    val onSpeakRaw = onSpeak
    val context = LocalContext.current
    @Suppress("NAME_SHADOWING") val onSpeak: (String) -> Unit = { t ->
        EnglishAudio.speak(context, t) { onSpeakRaw(EnglishCourse.pronounce(it)) }
    }
    val progress = remember(profileId) { EnglishProgress(context, profileId) }
    var stage by rememberSaveable { mutableIntStateOf(startStage.coerceIn(1, EnglishCourse.stages.size)) }
    var page by rememberSaveable { mutableStateOf("home") }
    var refresh by remember { mutableIntStateOf(0) }
    val st = EnglishCourse.stage(stage)
    val lesson = st.lessons.firstOrNull { it.id == page }
    BackHandler(enabled = page != "home") { page = "home"; refresh++ }

    Column(Modifier.fillMaxSize().background(Paper).testTag("english_root")) {
        Row(Modifier.fillMaxWidth().background(Green).padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (page == "home") onBackPressed() else { page = "home"; refresh++ } }, modifier = Modifier.testTag("english_back")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            T(when { page == "quiz" -> "Level $stage practice"; page == "credits" -> "Sound credits"; page == "sounds" -> "Grown-ups: letter sounds"
                lesson != null -> lesson.title; else -> "Learn English" },
                size = 18.sp, color = Color.White, weight = FontWeight.Bold)
        }
        Box(Modifier.weight(1f)) {
            when {
                page == "quiz" -> EnglishQuizScreen(stage, onSpeak, onFinished = { score ->
                    progress.record(stage, score)
                    if (score >= EnglishProgress.PASS_MARK) onEarnRewards(10, 50, "English: Level $stage practice passed")
                    refresh++
                }, onDone = { page = "home"; refresh++ })
                lesson != null -> LessonScreen(lesson, progress, onSpeak, nextTitle = st.lessons.getOrNull(st.lessons.indexOf(lesson) + 1)?.title) {
                    progress.markDone(lesson.id); onEarnRewards(5, 20, "English: ${lesson.title}")
                    page = "home"; refresh++
                }
                page == "credits" -> CreditsScreen()
                page == "sounds" -> LetterSoundsScreen()
                else -> EnglishHome(progress, refresh, stage, onStage = { stage = it }) { page = it }
            }
        }
    }
}

@Composable
private fun EnglishHome(progress: EnglishProgress, refreshKey: Int, stage: Int, onStage: (Int) -> Unit, open: (String) -> Unit) {
    val done = { l: String -> refreshKey >= 0 && progress.isDone(l) }
    val st = EnglishCourse.stage(stage)
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(24.dp)).horizontalScroll(rememberScrollState()).padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            EnglishCourse.stages.forEach { s ->
                val on = s.number == stage
                val passed = done(s.quizKey)
                Box(Modifier.widthIn(min = 72.dp).heightIn(min = 44.dp).background(if (on) Green else Color.Transparent, RoundedCornerShape(20.dp))
                    .clickable { onStage(s.number) }.testTag("english_stage_tab_${s.number}"), contentAlignment = Alignment.Center) {
                    T((if (passed) "✓ " else "") + "${s.number}: ${s.short}", Modifier.padding(horizontal = 8.dp), size = 13.sp, color = if (on) Color.White else Green,
                        weight = FontWeight.Bold, align = TextAlign.Center)
                }
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = st.bg), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                T(st.hero, size = 38.sp, color = st.line, weight = FontWeight.Bold, align = TextAlign.Center)
                T(st.title, size = 18.sp, weight = FontWeight.Bold, align = TextAlign.Center)
                val doneCount = st.lessons.count { done(it.id) }
                T("$doneCount of ${st.lessons.size} lessons done · 10 minutes a day is plenty", size = 13.sp, color = Quiet, align = TextAlign.Center)
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(progress = { doneCount / st.lessons.size.toFloat() }, color = st.line, trackColor = Color.White,
                    modifier = Modifier.fillMaxWidth().height(8.dp).testTag("english_progress_$stage"))
            }
        }
        val next = st.lessons.firstOrNull { !done(it.id) }
        if (next != null) {
            Button(onClick = { open(next.id) }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("english_continue"),
                colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                T("▶  Continue: ${next.title}", size = 15.sp, color = Color.White, weight = FontWeight.Bold)
            }
        }
        val prev = EnglishCourse.stages.firstOrNull { it.number == stage - 1 }
        if (prev != null && !done(prev.quizKey)) {
            Card(colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine), modifier = Modifier.testTag("english_s${stage}_hint")) {
                T("Tip: finish the Level ${prev.number} practice first. You can still start now if you like.", Modifier.padding(12.dp), size = 13.sp)
            }
        }
        st.lessons.forEachIndexed { i, l -> LessonCard("english_lesson_${l.id}", i + 1, l.title, l.subtitle, done(l.id)) { open(l.id) } }
        val best = progress.best(stage)
        LessonCard("english_quiz_$stage", st.lessons.size + 1, "Level $stage practice",
            if (best > 0) "Best score: $best/10  ·  8 correct completes the level" else "10 questions  ·  8 correct completes the level",
            done(st.quizKey)) { open("quiz") }
        if (done(st.quizKey)) {
            val next = EnglishCourse.stages.firstOrNull { it.number == stage + 1 }
            Card(Modifier.clickable { next?.let { onStage(it.number) } }.testTag("english_go_next_$stage"),
                colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine)) {
                T(if (next != null) "Well done! Level $stage complete. Now start Level ${next.number}: ${next.short} →"
                    else "Well done! You can read English sentences and stories. Keep reading a little every day!",
                    Modifier.padding(16.dp), size = 15.sp, weight = FontWeight.Bold)
            }
        }
        T("🔈 Words and conversations are spoken by real people. Single letter sounds use the phone's voice until a grown-up records them.",
            size = 12.sp, color = Quiet)
        TextButton(onClick = { open("sounds") }, modifier = Modifier.testTag("english_grownups")) {
            T("👪 Grown-ups: record the letter sounds", size = 13.sp, color = Green, weight = FontWeight.Bold)
        }
        TextButton(onClick = { open("credits") }, modifier = Modifier.testTag("english_credits")) {
            T("Sound credits and sources", size = 13.sp, color = Green)
        }
    }
}

/** Who recorded each word, under which licence — required by CC BY / CC BY-SA. */
@Composable
private fun CreditsScreen() {
    val ctx = LocalContext.current
    val credits = remember { EnglishAudio.credits(ctx) }
    LazyColumn(Modifier.fillMaxSize().testTag("english_credits_list"), contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)) {
        item {
            T("Course order: \"Letters and Sounds\" (UK Department for Education and Skills, 2007). Sight words: Letters and " +
                "Sounds and the Dolch list. Stories retell public-domain folk tales.", size = 13.sp, color = Quiet)
        }
        item { T("Word recordings (${credits.size}) from Wikimedia Commons:", size = 15.sp, weight = FontWeight.Bold) }
        if (VoaData.lessons.isNotEmpty()) {
            item {
                T("Conversations and their audio (Levels 5–8): VOA Learning English, \"Let's Learn English\" — public domain, " +
                    "courtesy of learningenglish.voanews.com.", size = 13.sp, weight = FontWeight.Bold)
            }
            items(VoaData.lessons, key = { "voa_${it.number}" }) { v -> T("Lesson ${v.number}: ${v.title}\n${v.url}", size = 12.sp) }
        }
        item { T("Stories (CC BY 4.0, text as published):", size = 15.sp, weight = FontWeight.Bold) }
        items(Stories.books, key = { "story_" + it.title }) { b -> T(b.credit, size = 12.sp) }
        items(credits, key = { it.word }) { c ->
            T("${c.word} — ${c.author}, ${c.licence}\n${c.source}", size = 12.sp)
        }
    }
}

@Composable
private fun LessonCard(tag: String, number: Int, title: String, subtitle: String, done: Boolean, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick).testTag(tag), colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (done) Green else Edge), shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).background(if (done) Green else GreenSoft, CircleShape), contentAlignment = Alignment.Center) {
                T(if (done) "✓" else number.toString(), size = 17.sp, color = if (done) Color.White else Green, weight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                T(title, size = 17.sp, weight = FontWeight.Bold)
                T(subtitle, size = 12.sp, color = Quiet)
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------------
// Lessons
// ---------------------------------------------------------------------------------------------------
@Composable
private fun LessonScreen(l: EnLesson, progress: EnglishProgress, speak: (String) -> Unit, nextTitle: String? = null, onDone: () -> Unit) {
    if (l.kind == LessonKind.CARDS) { Flashcards(l.tricky, speak, onDone); return }
    var seen by remember { mutableStateOf(progress.seen()) }
    LazyColumn(Modifier.fillMaxSize().testTag("english_${l.id}_list"), contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { T(l.intro, size = 14.sp, color = Quiet) }
        when (l.kind) {
            LessonKind.ALPHABET -> alphabet(l.sounds, seen, speak) { progress.markSeen(it); seen = progress.seen() }
            LessonKind.SOUNDS -> {
                items(l.sounds, key = { "s_" + it.grapheme }) { SoundRow(it, speak) }
                if (l.id == "e1.2" || l.id == "e1.3") {
                    item { T("Words you can read now:", size = 15.sp, weight = FontWeight.Bold) }
                    items(EnglishCourse.groups.filter { g -> g.first.split(' ').all { p -> l.sounds.any { it.grapheme == p } } },
                        key = { it.first }) { g -> Card(colors = CardDefaults.cardColors(containerColor = GreenSoft)) {
                            Column(Modifier.padding(10.dp)) { T(g.first, size = 20.sp, color = Green, weight = FontWeight.Bold); T(g.second, size = 13.sp) }
                        } }
                }
                if (l.words.isNotEmpty()) {
                    item { T("Read these words:", size = 15.sp, weight = FontWeight.Bold) }
                    items(l.words, key = { "w_" + it.word }) { WordRow(it, speak) }
                }
            }
            LessonKind.WORDS -> items(l.words, key = { "w_" + it.word }) { WordRow(it, speak) }
            LessonKind.PAIRS -> items(l.pairs, key = { "p_" + it.first.word }) { (a, b) -> PairRow(a, b, l.pairJoin, speak) }
            LessonKind.VOCAB -> vocabGrid(l.vocab, speak)
            LessonKind.GRAMMAR -> items(l.tips, key = { "t_" + it.title }) { TipCard(it, speak) }
            LessonKind.TRICKY -> trickyWords(l.tricky, speak)
            LessonKind.SENTENCES -> items(l.sentences, key = { it.text }) { SentenceCard(it, speak) }
            LessonKind.STORY -> item(key = "story_${l.id}") { StoryPager(l.id, l.sentences, speak) }
            LessonKind.CARDS -> Unit
            LessonKind.PHRASES -> items(l.phrases, key = { "ph_" + it.text }) { PhraseCard(it, speak) }
            LessonKind.DIALOGUE -> dialogues(l.id, listOf(l.dialogue), speak)
            LessonKind.REPEAT -> item(key = "repeat_${l.id}") { RepeatCard(l.id, l.dialogue, speak) }
        }
        item {
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("english_done"),
                colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                T(when (l.kind) {
                    LessonKind.DIALOGUE, LessonKind.REPEAT, LessonKind.PHRASES, LessonKind.VOCAB -> "I practised this ✓"
                    else -> "I have read this lesson ✓"
                } + (nextTitle?.let { "  ·  next: $it" } ?: ""), size = 15.sp, color = Color.White, weight = FontWeight.Bold,
                    align = TextAlign.Center)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
private fun LazyListScope.alphabet(sounds: List<Sound>, seen: Set<String>, speak: (String) -> Unit, onSeen: (String) -> Unit) {
    item {
        FlowRow(Modifier.fillMaxWidth().testTag("english_alphabet"), horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            sounds.forEach { s ->
                val on = s.grapheme in seen
                Column(Modifier.width(74.dp).background(if (on) GreenSoft else Color.White, RoundedCornerShape(14.dp))
                    .clickable { onSeen(s.grapheme); speak(s.spoken) }.padding(6.dp).testTag("english_letter_${s.grapheme}")
                    .semantics { contentDescription = s.spoken }, horizontalAlignment = Alignment.CenterHorizontally) {
                    T("${s.grapheme.uppercase()} ${s.grapheme}", size = 24.sp, color = Green, weight = FontWeight.Bold)
                    T(s.emoji, size = 20.sp)
                    T(s.keyword, size = 11.sp, color = Quiet, align = TextAlign.Center)
                }
            }
        }
    }
    item { T("Letters seen: ${seen.count { s -> sounds.any { it.grapheme == s } }} / ${sounds.size}", size = 13.sp, color = Quiet) }
}

@Composable
private fun SoundRow(s: Sound, speak: (String) -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { speak(s.spoken) }.testTag("english_sound_${s.grapheme}"),
        colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Edge)) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(60.dp).background(Tile, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                T(s.grapheme, size = 28.sp, color = TileInk, weight = FontWeight.Bold)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                T("${s.emoji}  as in ${s.keyword}", size = 16.sp, weight = FontWeight.Bold)
                if (s.note.isNotEmpty()) T(s.note, size = 12.sp, color = Quiet)
            }
        }
    }
}

/** c + a + t = cat 🐱 — tap a tile for its sound, the word to hear it whole. */
@Composable
private fun WordRow(wd: ReadWord, speak: (String) -> Unit) {
    Card(Modifier.fillMaxWidth().testTag("english_word_${wd.word}"), colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Edge)) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            // long words (charity, children) get smaller tiles so the word stays on one line
            val long = wd.parts.size > 5
            wd.parts.forEachIndexed { i, p ->
                if (i > 0) T("+", Modifier.padding(horizontal = if (long) 1.dp else 3.dp), size = if (long) 12.sp else 16.sp, color = Quiet)
                Box(Modifier.background(Tile, RoundedCornerShape(8.dp)).clickable { speak(EnglishAudio.SOUND + p) }
                    .padding(horizontal = if (long) 5.dp else 8.dp, vertical = 4.dp)) {
                    T(p, size = if (long) 16.sp else 20.sp, color = TileInk, weight = FontWeight.Bold)
                }
            }
            T("=", Modifier.padding(horizontal = if (long) 3.dp else 6.dp), size = 18.sp, color = Quiet)
            Box(Modifier.weight(1f).clickable { speak(wd.word) }.testTag("english_say_${wd.word}")) {
                Text("${wd.word} ${wd.emoji}", color = Green, fontSize = if (long) 18.sp else 22.sp, fontWeight = FontWeight.Bold,
                    maxLines = 1, softWrap = false)
            }
        }
    }
}

/** What the voice says for a sound tile: the tile and its keyword word. */
internal fun soundOf(part: String): String {
    val t = part.lowercase()
    // one letter: just that letter (upper case, so the voice says "A", not the word "a")
    if (t.length == 1) return t.uppercase()
    // letter teams: just the one sound they make, nothing more
    return when (t) {
        "sh" -> "shh"; "ch" -> "chuh"; "th" -> "thuh"; "ng" -> "ing"; "ck" -> "K"; "ll" -> "L"; "ss" -> "S"
        "wh" -> "W"; "ph" -> "F"; "qu" -> "kw"; "jj" -> "J"; "er" -> "er"; "cc" -> "K"; "mm" -> "M"
        "ai", "ay", "a_e" -> "ay"; "ee", "ea" -> "ee"; "oa", "o_e" -> "oh"; "ow" -> "ow"; "igh", "i_e" -> "eye"
        "oo", "ue", "ew" -> "oo"; "u_e" -> "you"; "ie" -> "eye"
        "ar" -> "ar"; "or" -> "or"; "ur", "ir" -> "er"; "ou" -> "ow"; "oi", "oy" -> "oy"; "nk" -> "ink"
        "ff" -> "F"; "zz" -> "Z"; "gg" -> "G"; "dd" -> "D"
        else -> t
    }
}

@Composable
private fun PairRow(a: ReadWord, b: ReadWord, join: String, speak: (String) -> Unit) {
    Card(Modifier.fillMaxWidth().testTag("english_pair_${a.word}"), colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Edge)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(Modifier.clickable { speak(a.word) }, horizontalAlignment = Alignment.CenterHorizontally) {
                T(a.word, size = 24.sp, weight = FontWeight.Bold); T(a.emoji, size = 18.sp)
            }
            T(join, size = 18.sp, color = Quiet)
            Column(Modifier.clickable { speak(b.word) }, horizontalAlignment = Alignment.CenterHorizontally) {
                T(b.word, size = 24.sp, color = Green, weight = FontWeight.Bold); T(b.emoji, size = 18.sp)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
private fun LazyListScope.trickyWords(words: List<String>, speak: (String) -> Unit) {
    item {
        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            words.forEach { wd ->
                Box(Modifier.widthIn(min = 90.dp).background(Color(0xFFFFF8E1), RoundedCornerShape(14.dp)).clickable { speak(wd) }
                    .padding(horizontal = 14.dp, vertical = 10.dp).testTag("english_tricky_$wd"), contentAlignment = Alignment.Center) {
                    T(wd, size = 24.sp, color = Color(0xFFE65100), weight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SentenceCard(s: Sentence, speak: (String) -> Unit) {
    Card(Modifier.fillMaxWidth().testTag("english_sentence_${s.text.hashCode()}"), colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Edge)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            T(s.emoji, Modifier.clickable { speak(s.text) }.padding(end = 8.dp), size = 30.sp)
            FlowRow(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                s.text.split(' ').forEach { wd ->
                    T(wd, Modifier.clickable { speak(wd.trim('.', ',', '!', '"', '?')) }, size = 22.sp, weight = FontWeight.SemiBold)
                }
            }
            T("🔊", Modifier.clickable { speak(s.text) }.padding(start = 6.dp).semantics { contentDescription = "Listen" }, size = 22.sp)
        }
    }
}

private const val CARDS_PER_ROUND = 15

@Composable
private fun Flashcards(words: List<String>, speak: (String) -> Unit, onDone: () -> Unit) {
    var seed by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    val deck = remember(seed) { words.shuffled(Random(seed)).take(CARDS_PER_ROUND) }
    var index by rememberSaveable(seed) { mutableIntStateOf(0) }
    var known by rememberSaveable(seed) { mutableIntStateOf(0) }
    var heard by rememberSaveable(seed, index) { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (index >= deck.size) {
            T(if (known >= deck.size * 2 / 3) "Excellent! 🌟" else "Well done! Practise a little every day 💪", size = 24.sp,
                weight = FontWeight.Bold, align = TextAlign.Center)
            T("$known / ${deck.size} read correctly", size = 20.sp, color = Green, align = TextAlign.Center, modifier = Modifier.testTag("english_cards_result"))
            Spacer(Modifier.height(16.dp))
            Button(onClick = { seed = System.currentTimeMillis() }, colors = ButtonDefaults.buttonColors(containerColor = Green)) { T("New cards", color = Color.White) }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onDone, colors = ButtonDefaults.buttonColors(containerColor = Green),
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("english_done")) {
                T("Practice done ✓", color = Color.White, weight = FontWeight.Bold)
            }
            return@Column
        }
        val wd = deck[index]
        T("Card ${index + 1} / ${deck.size}   ·   read correctly: $known", size = 13.sp, color = Quiet)
        T("Read the word out loud, then tap the card to hear it.", size = 13.sp, color = Quiet, align = TextAlign.Center)
        Spacer(Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth().heightIn(min = 200.dp).clickable { heard = true; speak(wd) }.testTag("english_card"),
            colors = CardDefaults.cardColors(containerColor = if (heard) GreenSoft else Color.White), border = BorderStroke(2.dp, Green),
            shape = RoundedCornerShape(24.dp)) {
            Box(Modifier.fillMaxWidth().heightIn(min = 200.dp), contentAlignment = Alignment.Center) {
                T(wd, size = 56.sp, weight = FontWeight.Bold, color = Color(0xFFE65100))
            }
        }
        Spacer(Modifier.height(16.dp))
        if (heard) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { known++; index++ }, modifier = Modifier.weight(1f).heightIn(min = 52.dp).testTag("english_card_knew"),
                    colors = ButtonDefaults.buttonColors(containerColor = Green)) { T("I read it ✓", color = Color.White, weight = FontWeight.Bold) }
                OutlinedButton(onClick = { index++ }, modifier = Modifier.weight(1f).heightIn(min = 52.dp).testTag("english_card_notyet"),
                    border = BorderStroke(1.dp, Green)) { T("Not yet ↺", color = Green) }
            }
        } else Box(Modifier.heightIn(min = 52.dp))
    }
}

// ---------------------------------------------------------------------------------------------------
// Practice
// ---------------------------------------------------------------------------------------------------
@Composable
private fun EnglishQuizScreen(stage: Int, speak: (String) -> Unit, onFinished: (Int) -> Unit, onDone: () -> Unit) {
    var seed by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    val questions = remember(seed) { EnglishQuiz.generate(stage, Random(seed)) }
    var index by rememberSaveable(seed) { mutableIntStateOf(0) }
    var score by rememberSaveable(seed) { mutableIntStateOf(0) }
    var chosen by rememberSaveable(seed, index) { mutableStateOf<Int?>(null) }
    var finished by rememberSaveable(seed) { mutableStateOf(false) }
    var missed by rememberSaveable(seed) { mutableStateOf(listOf<String>()) }

    if (finished) {
        val passed = score >= EnglishProgress.PASS_MARK
        Column(Modifier.fillMaxSize().padding(24.dp).testTag("english_quiz_result"), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            T(if (passed) "Excellent! 🌟" else "Good try — practise again 💪", size = 24.sp, weight = FontWeight.Bold, align = TextAlign.Center)
            T("$score / ${questions.size} correct", size = 20.sp, color = Green, align = TextAlign.Center)
            if (missed.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                T("Practise these:", size = 14.sp, weight = FontWeight.Bold)
                missed.distinct().forEach { m ->
                    T("🔊 $m", Modifier.clickable { speak(m) }.padding(vertical = 3.dp).testTag("english_missed"), size = 15.sp,
                        color = Green, align = TextAlign.Center)
                }
            }
            T(if (passed) "Level $stage is complete." else "8 correct answers complete Level $stage.", size = 14.sp, color = Quiet, align = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { seed = System.currentTimeMillis() }, colors = ButtonDefaults.buttonColors(containerColor = Green),
                modifier = Modifier.testTag("english_quiz_retry")) { T("Practise again", color = Color.White) }
            TextButton(onClick = onDone) { T("List of lessons", color = Green) }
        }
        return
    }
    val q = questions[index]
    // listening questions play straight away (tap the card to hear again)
    LaunchedEffect(seed, index) { if (q.prompt == "🔊") { delay(400); q.speak?.let(speak) } }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        T("Question ${index + 1} / ${questions.size}   ·   correct: $score", size = 13.sp, color = Quiet)
        Spacer(Modifier.height(8.dp))
        T(q.instruction, size = 17.sp, weight = FontWeight.Bold, align = TextAlign.Center)
        Card(Modifier.padding(vertical = 12.dp).widthIn(min = 160.dp).clickable { q.speak?.let(speak) }.testTag("english_quiz_prompt"),
            colors = CardDefaults.cardColors(containerColor = GreenSoft)) {
            Box(Modifier.padding(horizontal = 28.dp, vertical = 12.dp), contentAlignment = Alignment.Center) {
                T(q.prompt, size = if (q.promptIsWord) (if (q.prompt.length > 40) 18.sp else if (q.prompt.length > 14) 22.sp else 40.sp) else 56.sp,
                    weight = FontWeight.Bold, align = TextAlign.Center)
            }
        }
        q.options.forEachIndexed { i, opt ->
            val bg = when { chosen == null -> Color.White; i == q.correctIndex -> Color(0xFFC8E6C9); i == chosen -> Color(0xFFFFCDD2); else -> Color.White }
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp).heightIn(min = 56.dp)
                .clickable(enabled = chosen == null) {
                    chosen = i
                    if (i == q.correctIndex) score++ else missed = missed + q.options[q.correctIndex]
                }.testTag("english_option_$i"),
                colors = CardDefaults.cardColors(containerColor = bg), border = BorderStroke(1.dp, Edge)) {
                Box(Modifier.fillMaxWidth().padding(10.dp), contentAlignment = Alignment.Center) { T(opt, size = if (opt.length > 30) 15.sp else if (opt.length > 14) 18.sp else 24.sp, align = TextAlign.Center) }
            }
        }
        if (chosen != null) {
            T(if (chosen == q.correctIndex) "Excellent! ✓" else "The correct answer is in green.", size = 15.sp,
                color = if (chosen == q.correctIndex) Green else Color(0xFFB23B1E), align = TextAlign.Center)
            Button(onClick = { if (index + 1 >= questions.size) { finished = true; onFinished(score) } else index++ },
                colors = ButtonDefaults.buttonColors(containerColor = Green), modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("english_next")) {
                T(if (index + 1 >= questions.size) "See the result" else "Next question", color = Color.White, weight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PhraseCard(p: Phrase, speak: (String) -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { speak(p.text) }.testTag("english_phrase_${p.text.hashCode()}"),
        colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Edge)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            T(p.emoji, Modifier.padding(end = 10.dp), size = 30.sp)
            Column(Modifier.weight(1f)) {
                T(p.text, size = 20.sp, weight = FontWeight.Bold, color = Green)
                T(p.meaning, size = 13.sp, color = Quiet)
            }
            T("🔊", Modifier.padding(start = 6.dp), size = 22.sp)
        }
    }
}

/** A talk between two people: tap a line to hear it, hear it all, or practise one side. */
private fun LazyListScope.dialogues(id: String, talks: List<List<Line>>, speak: (String) -> Unit) {
    talks.forEachIndexed { n, talk ->
        item(key = "talk_${id}_$n") { TalkCard(id, n, talk, speak) }
    }
}

private val SpeakerColours = listOf(Color(0xFFE8F5E9), Color(0xFFE3F2FD), Color(0xFFFFF3E0), Color(0xFFF3E5F5), Color(0xFFFCE4EC))

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TalkCard(id: String, n: Int, talk: List<Line>, speak: (String) -> Unit) {
    val speakers = talk.map { it.speaker }.distinct()
    var practise by remember(id, n) { mutableStateOf<String?>(null) }
    var shown by remember(id, n) { mutableStateOf(setOf<Int>()) }
    Card(Modifier.fillMaxWidth().testTag("english_talk_${id}_$n"), colors = CardDefaults.cardColors(containerColor = Paper),
        border = BorderStroke(1.dp, Edge)) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Button(onClick = { speak(talk.joinToString(" ") { it.text }) }, modifier = Modifier.fillMaxWidth().testTag("english_talk_all_${id}_$n"),
                colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                T("▶  Listen to the whole conversation", size = 14.sp, color = Color.White, weight = FontWeight.Bold)
            }
            talk.forEachIndexed { i, ln ->
                val who = speakers.indexOf(ln.speaker)
                val hidden = practise == ln.speaker && i !in shown
                Row(Modifier.fillMaxWidth(), horizontalArrangement = if (who % 2 == 0) Arrangement.Start else Arrangement.End) {
                    Card(Modifier.widthIn(max = 300.dp).clickable {
                            if (hidden) shown = shown + i
                            speak(ln.text)
                        }.testTag("english_line_${id}_${n}_$i"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SpeakerColours[who % SpeakerColours.size])) {
                        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            T(ln.speaker, size = 11.sp, color = Quiet, weight = FontWeight.Bold)
                            T(if (hidden) "🗣️ Your turn — say it, then tap to check" else ln.text, size = 17.sp,
                                color = if (hidden) Quiet else Ink, weight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            if (speakers.size > 1) {
                T("Practise one part: choose a person, say their lines, then tap to check.", size = 12.sp, color = Quiet)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    speakers.forEach { sp ->
                        val on = practise == sp
                        OutlinedButton(onClick = { practise = if (on) null else sp; shown = emptySet() },
                            border = BorderStroke(1.dp, if (on) Green else Edge),
                            modifier = Modifier.testTag("english_be_${id}_${n}_$sp")) {
                            T("Be $sp", size = 13.sp, color = if (on) Green else Ink, weight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/** Asks for the microphone once; returns (granted, ask). */
@Composable
private fun rememberMic(): Pair<Boolean, () -> Unit> {
    val ctx = LocalContext.current
    var granted by remember { mutableStateOf(ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted = it }
    return granted to { launcher.launch(Manifest.permission.RECORD_AUDIO) }
}

/** ● Record / ■ Stop and ▶ play back, for the child's voice or a grown-up's letter sound. */
@Composable
private fun RecordButtons(tag: String, hasRecording: () -> Boolean, start: () -> Boolean, play: () -> Unit, onSaved: () -> Unit = {}) {
    val (granted, ask) = rememberMic()
    var recording by remember { mutableStateOf(false) }
    var saved by remember(tag) { mutableStateOf(hasRecording()) }
    LaunchedEffect(recording) { if (recording) { delay(15_000); if (recording) { EnglishAudio.stopRecording(); recording = false; saved = hasRecording(); onSaved() } } }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        OutlinedButton(onClick = {
            when {
                !granted -> ask()
                recording -> { EnglishAudio.stopRecording(); recording = false; saved = hasRecording(); onSaved() }
                else -> recording = start()
            }
        }, border = BorderStroke(1.dp, if (recording) Color(0xFFC62828) else Green), modifier = Modifier.testTag("${tag}_record")) {
            T(if (recording) "■ Stop" else "● Record", size = 14.sp, color = if (recording) Color(0xFFC62828) else Green, weight = FontWeight.Bold)
        }
        OutlinedButton(onClick = play, enabled = saved && !recording, modifier = Modifier.testTag("${tag}_play")) {
            T("▶ Play", size = 14.sp)
        }
    }
}

/** Listen and repeat: one line at a time — hear it, say it, record yourself, compare. */
@Composable
private fun RepeatCard(id: String, lines: List<Line>, speak: (String) -> Unit) {
    val ctx = LocalContext.current
    var i by remember(id) { mutableIntStateOf(0) }
    val ln = lines[i.coerceIn(0, lines.size - 1)]
    LaunchedEffect(id, i) { EnglishAudio.clearMyVoice(ctx) }
    Card(Modifier.fillMaxWidth().testTag("english_repeat_$id"), colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Edge), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            T("${i + 1} / ${lines.size}", size = 13.sp, color = Quiet)
            LinearProgressIndicator(progress = { (i + 1) / lines.size.toFloat() }, color = Green, modifier = Modifier.fillMaxWidth())
            T(ln.speaker, size = 14.sp, color = Green, weight = FontWeight.Bold)
            T(ln.text, size = 22.sp, weight = FontWeight.SemiBold, align = TextAlign.Center)
            Button(onClick = { speak(ln.text) }, modifier = Modifier.heightIn(min = 52.dp).testTag("english_repeat_play"),
                colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                T("▶  Listen", size = 16.sp, color = Color.White, weight = FontWeight.Bold)
            }
            T("🗣️ Now say it! Then record yourself and compare.", size = 14.sp, color = Quiet, align = TextAlign.Center)
            key(id, i) {
                RecordButtons("english_me", { EnglishAudio.hasMyVoice(ctx) }, { EnglishAudio.startMyVoice(ctx) }, { EnglishAudio.playMyVoice(ctx) })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { if (i > 0) i-- }, enabled = i > 0, modifier = Modifier.testTag("english_repeat_back")) {
                    T("‹ Back", size = 14.sp)
                }
                OutlinedButton(onClick = { if (i < lines.size - 1) { i++; speak(lines[i].text) } }, enabled = i < lines.size - 1,
                    modifier = Modifier.testTag("english_repeat_next")) {
                    T("Next ›", size = 14.sp)
                }
            }
        }
    }
}

/** Picture words: tap a card to hear the word. */
@OptIn(ExperimentalLayoutApi::class)
private fun LazyListScope.vocabGrid(words: List<Vocab>, speak: (String) -> Unit) {
    item {
        FlowRow(Modifier.fillMaxWidth().testTag("english_vocab"), horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            words.forEach { v ->
                Column(Modifier.width(100.dp).background(Color.White, RoundedCornerShape(16.dp)).clickable { speak(v.word) }
                    .padding(8.dp).testTag("english_vocab_${v.word}").semantics { contentDescription = v.word },
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    T(v.picture, size = if (v.picture.length <= 2 || v.picture.codePointCount(0, v.picture.length) <= 2) 34.sp else 24.sp,
                        color = Green, weight = FontWeight.Bold)
                    T(v.word, size = 16.sp, weight = FontWeight.Bold, align = TextAlign.Center)
                }
            }
        }
    }
}

/** One grammar idea: the rule, a pattern, and real example sentences to hear. */
@Composable
private fun TipCard(t: Tip, speak: (String) -> Unit) {
    Card(Modifier.fillMaxWidth().testTag("english_tip_${t.title.hashCode()}"), colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Edge), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            T(t.title, size = 18.sp, color = Green, weight = FontWeight.Bold)
            T(t.rule, size = 14.sp)
            Box(Modifier.fillMaxWidth().background(Amber, RoundedCornerShape(10.dp)).padding(10.dp)) {
                T(t.pattern, size = 16.sp, weight = FontWeight.SemiBold)
            }
            T("Hear it in the conversations:", size = 12.sp, color = Quiet)
            t.examples.forEach { ex ->
                Row(Modifier.fillMaxWidth().clickable { speak(ex) }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    T("🔊", Modifier.padding(end = 8.dp).semantics { contentDescription = "Listen" }, size = 18.sp)
                    T(ex, size = 15.sp)
                }
            }
        }
    }
}

/** For a parent or teacher: record each letter sound once; the tiles then play that voice. */
@Composable
private fun LetterSoundsScreen() {
    val ctx = LocalContext.current
    val sounds = remember { EnglishCourse.recordableSounds }
    var refresh by remember { mutableIntStateOf(0) }
    LazyColumn(Modifier.fillMaxSize().testTag("english_sounds_list"), contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            T("Free recordings of single letter sounds don't exist, so the tiles use the phone's voice, which says letter names. " +
                "Record each sound once here (for example s = \"sss\", not \"ess\") and the tiles will play your voice. " +
                "Recordings stay on this phone.", size = 13.sp, color = Quiet)
        }
        item {
            val n = refresh.let { sounds.count { EnglishAudio.hasSound(ctx, it.grapheme) } }
            T("$n of ${sounds.size} sounds recorded", size = 15.sp, weight = FontWeight.Bold)
        }
        items(sounds, key = { "rec_" + it.grapheme }) { s ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Edge)) {
                Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(52.dp).background(Tile, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                        T(s.grapheme.replace("_", ""), size = 22.sp, color = TileInk, weight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        T("as in ${s.keyword} ${s.emoji}", size = 14.sp, weight = FontWeight.Bold)
                        RecordButtons("english_sound_${s.grapheme}", { EnglishAudio.hasSound(ctx, s.grapheme) },
                            { EnglishAudio.startSoundRecording(ctx, s.grapheme) }, { EnglishAudio.playSound(ctx, s.grapheme) }) { refresh++ }
                    }
                }
            }
        }
    }
}

/** A picture story, one page at a time. */
@Composable
private fun StoryPager(id: String, pages: List<Sentence>, speak: (String) -> Unit) {
    var i by remember(id) { mutableIntStateOf(0) }
    val p = pages[i.coerceIn(0, pages.size - 1)]
    Column(Modifier.fillMaxWidth().testTag("english_story_$id"), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Edge),
            shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                T(p.emoji, Modifier.clickable { speak(p.text) }, size = 72.sp)
                Spacer(Modifier.height(8.dp))
                SentenceCard(p, speak)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { if (i > 0) i-- }, enabled = i > 0, modifier = Modifier.testTag("english_story_prev")) { T("‹ Back", size = 14.sp) }
            T("Page ${i + 1} of ${pages.size}", size = 13.sp, color = Quiet)
            OutlinedButton(onClick = { if (i < pages.size - 1) i++ }, enabled = i < pages.size - 1,
                modifier = Modifier.testTag("english_story_next")) { T("Next ›", size = 14.sp) }
        }
    }
}
