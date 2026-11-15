package com.example.qaida

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Line5 = Color(0xFFE2E5EA)
private val Plum = Color(0xFF4A148C)
private val PlumSoft = Color(0xFFF3E5F5)

/** Router for the Stage 5 boards. */
@Composable
internal fun Stage5Lesson(page: String, play: (String, String) -> Unit, onDone: () -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize().testTag("qaida_${page}_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        when (page) {
            "s5_words" -> frequentWords(play)
            "s5_endings" -> endings(play)
            "s5_fatiha" -> wordByWord(play, QaidaStage5.fatiha)
            "s5_short" -> { wordByWord(play, QaidaStage5.ikhlas); wordByWord(play, QaidaStage5.kawthar); wordByWord(play, QaidaStage5.asr) }
            "s5_falaq" -> { wordByWord(play, QaidaStage5.falaq); wordByWord(play, QaidaStage5.nas) }
        }
        item {
            UrduText("یہ معانی بچوں کے لیے آسان اردو میں لکھے گئے ہیں؛ کسی مستند ترجمے اور استاد سے ملا کر پڑھیں۔", size = 11.sp, color = Quiet)
        }
        item {
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("qaida_s5_done"),
                colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                UrduText("میں نے یہ سبق پڑھ لیا ✓", size = 15.sp, color = Color.White, weight = FontWeight.Bold)
            }
        }
    }
}

private fun LazyListScope.note5(text: String) = item { UrduText(text, size = 14.sp, color = Quiet) }

// ---- 5.1 Frequent words ---------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
private fun LazyListScope.frequentWords(play: (String, String) -> Unit) {
    note5("یہ وہ الفاظ ہیں جو قرآن میں بار بار آتے ہیں۔ انہیں یاد کر لیں تو ہر صفحے پر کچھ نہ کچھ سمجھ آنے لگے گا۔ ہر لفظ پر ٹیپ کر کے سنیں۔")
    items(QaidaStage5.groups, key = { it.id }) { g ->
        Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(14.dp)).padding(10.dp).testTag("qaida_s5_group_${g.id}")) {
            UrduText(g.title, size = 15.sp, color = Plum, weight = FontWeight.Bold)
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                g.words.forEach { w -> WordChip(w.arabic, w.urdu, Modifier.testTag("qaida_s5_word_${w.slug}")) { play(w.audioKey, w.arabic) } }
            }
        }
    }
}

@Composable
private fun WordChip(arabic: String, urdu: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(modifier.widthIn(min = 72.dp).background(PlumSoft, RoundedCornerShape(12.dp)).clickable(onClick = onClick)
        .padding(horizontal = 10.dp, vertical = 4.dp).semantics { contentDescription = urdu },
        horizontalAlignment = Alignment.CenterHorizontally) {
        ArabicText(arabic, size = 26.sp, color = Plum)
        UrduText(urdu, size = 12.sp, align = TextAlign.Center)
    }
}

// ---- 5.2 Pronoun endings -----------------------------------------------------------------------------
private fun LazyListScope.endings(play: (String, String) -> Unit) {
    note5("عربی میں 'میرا، تیرا، اس کا' لفظ کے آخر میں جڑ جاتا ہے۔ رَبّ (رب) کے ساتھ دیکھیں — یہی آخری حصے ہر لفظ کے ساتھ آتے ہیں۔")
    items(QaidaStage5.endings, key = { it.slug }) { e ->
        Card(Modifier.fillMaxWidth().clickable { play(e.audioKey, e.example) }.testTag("qaida_s5_end_${e.slug}"),
            colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line5)) {
            // Right to left: رَبّ + ending = word, then its meaning.
            Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                ArabicText("رَبّ", size = 26.sp)
                UrduText("+", size = 18.sp, color = Quiet)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ArabicText(e.ending, size = 26.sp, color = Plum)
                    UrduText(e.urdu, size = 11.sp, color = Quiet)
                }
                UrduText("=", size = 18.sp, color = Quiet)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ArabicText(e.example, size = 30.sp, color = Green)
                    UrduText(e.exampleUrdu, size = 12.sp, weight = FontWeight.Bold)
                }
            }
        }
    }
}

// ---- 5.3 – 5.5 Word by word ---------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
internal fun LazyListScope.wordByWord(play: (String, String) -> Unit, s: QaidaStage5.WbwSurah) {
    item {
        Box(Modifier.fillMaxWidth().background(GreenSoft, RoundedCornerShape(14.dp)).padding(8.dp), contentAlignment = Alignment.Center) {
            UrduText(s.urduName + " — لفظ بہ لفظ", size = 17.sp, color = Green, weight = FontWeight.Bold)
        }
    }
    items(s.ayat, key = { "wbw_${it.ayah.surah}_${it.ayah.number}" }) { a ->
        Card(Modifier.fillMaxWidth().testTag("qaida_wbw_${a.ayah.surah}_${a.ayah.number}"),
            colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line5)) {
            Column(Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(28.dp).background(GreenSoft, CircleShape).clickable { play(a.ayah.audioKey, a.ayah.text) },
                        contentAlignment = Alignment.Center) {
                        UrduText(a.ayah.number.toString().map { "۰۱۲۳۴۵۶۷۸۹"[it - '0'] }.joinToString(""), size = 12.sp, color = Green, weight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(6.dp))
                    UrduText("لفظ پر ٹیپ کریں · نمبر پر ٹیپ کر کے پوری آیت سنیں", size = 11.sp, color = Quiet)
                }
                FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    a.words.forEachIndexed { i, word ->
                        WordChip(word, a.meanings[i], Modifier.testTag("qaida_wbw_${a.ayah.surah}_${a.ayah.number}_${i + 1}")) {
                            play(a.wordKey(i), word)
                        }
                    }
                }
                Spacer(Modifier.width(4.dp))
                UrduText("لفظی معنی: " + a.meanings.joinToString(" "), size = 13.sp, color = Ink)
            }
        }
    }
}
