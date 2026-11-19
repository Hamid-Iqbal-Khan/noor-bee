package com.example.qaida

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * Stages 8–10 — the rest of Juz 'Amma (Al-Humazah back to An-Naba), word by word in Urdu.
 * Data: assets/qaida/juz_amma.tsv, built by tools/qaida/build_juz_amma.py from the standard (Tanzil-based)
 * text in semarketir/quranjson (MIT), converted to IndoPak-style marks with the letters unchanged.
 * CI checks every ayah against api.alquran.cloud. Urdu meanings are written for children; a teacher must review them.
 */
object QaidaJuzAmma {
    const val ASSET = "qaida/juz_amma.tsv"

    data class Group(val stage: Int, val surahs: List<Int>, val tab: String, val title: String, val hero: String)

    val groups = listOf(
        Group(8, listOf(104, 102, 101, 100, 99, 98, 97, 96, 95, 94, 93), "۸: عمّ ۱", "مرحلہ ۸: الہمزہ سے الضحیٰ تک", "الھمزہ ← الضحیٰ"),
        Group(9, listOf(92, 91, 90, 89, 88, 87, 86, 85), "۹: عمّ ۲", "مرحلہ ۹: اللیل سے البروج تک", "اللیل ← البروج"),
        Group(10, listOf(84, 83, 82, 81, 80, 79, 78), "۱۰: عمّ ۳", "مرحلہ ۱۰: الانشقاق سے النبأ تک", "الانشقاق ← النبأ"),
    )

    val urduNames = mapOf(
        78 to "سورۃ النبأ", 79 to "سورۃ النازعات", 80 to "سورۃ عبس", 81 to "سورۃ التکویر", 82 to "سورۃ الانفطار",
        83 to "سورۃ المطففین", 84 to "سورۃ الانشقاق", 85 to "سورۃ البروج", 86 to "سورۃ الطارق", 87 to "سورۃ الاعلیٰ",
        88 to "سورۃ الغاشیہ", 89 to "سورۃ الفجر", 90 to "سورۃ البلد", 91 to "سورۃ الشمس", 92 to "سورۃ اللیل",
        93 to "سورۃ الضحیٰ", 94 to "سورۃ الشرح", 95 to "سورۃ التین", 96 to "سورۃ العلق", 97 to "سورۃ القدر",
        98 to "سورۃ البینہ", 99 to "سورۃ الزلزال", 100 to "سورۃ العادیات", 101 to "سورۃ القارعہ", 102 to "سورۃ التکاثر",
        104 to "سورۃ الہمزہ",
    )

    fun group(stage: Int) = groups.first { it.stage == stage }
    fun lessonKey(surah: Int) = "${groupOf(surah).stage}.$surah"
    fun quizKey(stage: Int) = "$stage.q"
    fun groupOf(surah: Int) = groups.first { surah in it.surahs }

    /** Parses the TSV: surah, ayah, text, meanings separated by |. */
    fun parse(lines: Sequence<String>): Map<Int, QaidaStage5.WbwSurah> {
        val ayat = lines.filter { it.isNotBlank() && !it.startsWith("#") }.map { line ->
            val cols = line.split('\t')
            val (s, n, text, means) = cols
            QaidaStage5.WbwAyah(QaidaStage3.Ayah(s.toInt(), n.toInt(), text, ""), means.split('|'), sajdah = cols.getOrNull(4) == "1",
                meaningsEn = cols.getOrNull(5)?.split('|') ?: emptyList())
        }.toList()
        return ayat.groupBy { it.ayah.surah }.mapValues { (s, list) ->
            QaidaStage5.WbwSurah(s, urduNames[s] ?: "سورۃ $s", list.sortedBy { it.ayah.number })
        }
    }

    @Volatile private var cache: Map<Int, QaidaStage5.WbwSurah>? = null

    fun load(context: Context): Map<Int, QaidaStage5.WbwSurah> =
        cache ?: context.applicationContext.assets.open(ASSET).bufferedReader(Charsets.UTF_8).useLines { parse(it) }.also { cache = it }

    fun ayahCount(surahs: Map<Int, QaidaStage5.WbwSurah>, stage: Int) = group(stage).surahs.sumOf { surahs[it]?.ayat?.size ?: 0 }
}

/** Quiz for a Juz 'Amma stage: word meanings from its surahs, and "which surah does this ayah open". */
object QaidaJuzAmmaQuiz {
    fun generate(random: kotlin.random.Random, surahs: List<QaidaStage5.WbwSurah>, length: Int = QaidaProgress.QUIZ_LENGTH): List<QaidaQuestion> {
        val cards = surahs.flatMap { s -> s.ayat.flatMap { a -> a.words.mapIndexed { i, w -> Triple(w, a.shown[i], a.wordKey(i)) } } }
            .filter { it.first.length >= 3 }.distinctBy { it.second }
        return List(length) { i ->
            if (i % 3 == 2 && surahs.size >= 4) {
                val s = surahs.random(random)
                val names = (surahs.filter { it != s }.shuffled(random).take(3) + s).shuffled(random).map { it.urduName }
                val first = s.ayat.first().ayah
                QaidaQuestion(first.text, true, "یہ کس سورت کی پہلی آیت ہے؟", names, false, names.indexOf(s.urduName), first.audioKey)
            } else {
                val (w, m, key) = cards.random(random)
                val opts = (cards.filter { it.second != m }.shuffled(random).take(3).map { it.second } + m).shuffled(random)
                QaidaQuestion(w, true, "اس لفظ کا مطلب کیا ہے؟", opts, false, opts.indexOf(m), key)
            }
        }
    }
}

/** One surah, word by word, with a "done" button. */
@Composable
internal fun JuzAmmaLesson(surah: QaidaStage5.WbwSurah, play: (String, String) -> Unit, onDone: () -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize().testTag("qaida_juz_${surah.number}_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        wordByWord(play, surah)
        item {
            UrduText("قرآن کا متن: معیاری (تنزیل) متن، انڈو پاک طرز کے نشانات کے ساتھ۔ معانی بچوں کے لیے آسان اردو میں ہیں؛ استاد اور کسی مستند ترجمے سے ملا کر پڑھیں۔",
                size = 11.sp, color = Quiet)
        }
        item {
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("qaida_juz_done"),
                colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                UrduText("میں نے یہ سورت پڑھ لی ✓", size = 15.sp, color = Color.White, weight = FontWeight.Bold)
            }
        }
    }
}

/** Easy whole-ayah Urdu meaning for every ayah in the course (assets/qaida/mafhoom.tsv). */
object QaidaMafhoom {
    const val ASSET = "qaida/mafhoom.tsv"
    const val ASSET_EN = "qaida/mafhoom_en.tsv"
    @Volatile private var map: Map<Pair<Int, Int>, String> = emptyMap()
    @Volatile private var mapEn: Map<Pair<Int, Int>, String> = emptyMap()

    fun parse(lines: Sequence<String>): Map<Pair<Int, Int>, String> =
        lines.filter { it.isNotBlank() && !it.startsWith("#") }.associate { l ->
            val (s, n, t) = l.split('\t'); (s.toInt() to n.toInt()) to t
        }

    fun load(context: Context): Map<Pair<Int, Int>, String> {
        if (map.isEmpty()) map = context.applicationContext.assets.open(ASSET).bufferedReader(Charsets.UTF_8).useLines { parse(it) }
        if (mapEn.isEmpty()) mapEn = context.applicationContext.assets.open(ASSET_EN).bufferedReader(Charsets.UTF_8).useLines { parse(it) }
        return map
    }

    fun of(surah: Int, ayah: Int): String? = (if (QaidaLang.english) mapEn else map)[surah to ayah]
}

/** "آسان مفہوم" line shown under an ayah. */
@Composable
internal fun MafhoomLine(surah: Int, ayah: Int) {
    val m = QaidaMafhoom.of(surah, ayah) ?: return
    androidx.compose.foundation.layout.Column(Modifier.fillMaxWidth().padding(top = 4.dp)
        .background(Color(0xFFF1F8E9), androidx.compose.foundation.shape.RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
        .testTag("qaida_mafhoom_${surah}_$ayah")) {
        UrduText(tx("آسان مفہوم: $m", "Simple meaning: $m"), size = 14.sp, color = Ink, weight = FontWeight.Bold)
    }
    ParentNote(surah, ayah)
}
