package com.example.qaida

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

private val Line4 = Color(0xFFE2E5EA)
private val StopRed = Color(0xFFC62828)
private val Teal = Color(0xFF00695C)
private val TealSoft = Color(0xFFE0F2F1)

private fun stopColor(s: QaidaStage4.Stop) = when (s) {
    QaidaStage4.Stop.MUST -> StopRed
    QaidaStage4.Stop.BETTER_STOP -> Color(0xFFE65100)
    QaidaStage4.Stop.EITHER -> Color(0xFF6D4C41)
    QaidaStage4.Stop.BETTER_JOIN -> Teal
    QaidaStage4.Stop.DONT -> Color(0xFF1565C0)
}

/** Router for the Stage 4 boards. */
@Composable
internal fun Stage4Lesson(page: String, play: (String, String) -> Unit, onDone: () -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize().testTag("qaida_${page}_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        when (page) {
            "s4_stop" -> stopping(play)
            "s4_signs" -> signs()
            "s4_qalqalah" -> qalqalah(play)
            "s4_kawthar" -> { surah(play, QaidaStage4.kawthar); surah(play, QaidaStage4.asr) }
            "s4_falaq" -> surah(play, QaidaStage4.falaq)
            "s4_nas" -> surah(play, QaidaStage4.nas)
        }
        item {
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("qaida_s4_done"),
                colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                UrduText("میں نے یہ سبق پڑھ لیا ✓", size = 15.sp, color = Color.White, weight = FontWeight.Bold)
            }
        }
    }
}

private fun LazyListScope.note4(text: String) = item { UrduText(text, size = 14.sp, color = Quiet) }

// ---- 4.1 How to stop ---------------------------------------------------------------------------------
private fun LazyListScope.stopping(play: (String, String) -> Unit) {
    note4("آیت کے آخر میں یا سانس ٹوٹنے پر لفظ پر رکیں تو آخری حرف کی حرکت ہٹا کر اسے ساکن پڑھیں۔ دو زبر ہو تو زبر اور الف پڑھیں، اور گول ة ہو تو 'ہ' ساکن پڑھیں۔")
    items(QaidaStage4.stopExamples, key = { it.audioKey }) { e ->
        Card(Modifier.fillMaxWidth().testTag("qaida_s4_stop_${e.audioKey}"),
            colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line4)) {
            Column(Modifier.padding(10.dp)) {
                // Right to left: the written word, an arrow, then how it is read when stopping.
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                    ArabicText(e.word, Modifier.clickable { play(e.audioKey, e.word) }, size = 32.sp)
                    UrduText("←", size = 22.sp, color = Quiet)
                    ArabicText(e.stopped, Modifier.clickable { play(e.audioKey + "_stop", e.stopped) }, size = 32.sp, color = StopRed)
                }
                UrduText(e.urdu, Modifier.fillMaxWidth(), size = 12.sp, color = Quiet, align = TextAlign.Center)
            }
        }
    }
}

// ---- 4.2 Waqf signs -----------------------------------------------------------------------------------
private fun LazyListScope.signs() {
    note4("انڈو پاک قرآن میں آیت کے بیچ چھوٹے نشان بتاتے ہیں کہ کہاں رکنا ہے اور کہاں ملا کر پڑھنا ہے۔ رکیں تو اوپر والا وقف کا طریقہ استعمال کریں۔")
    items(QaidaStage4.waqfSigns, key = { it.id }) { w ->
        Card(Modifier.fillMaxWidth().testTag("qaida_s4_sign_${w.id}"),
            colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line4)) {
            Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(64.dp).background(stopColor(w.stop).copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center) {
                    ArabicText(w.sign, size = 28.sp, color = stopColor(w.stop))
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UrduText(w.name, size = 15.sp, weight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        UrduText(w.stop.urdu, Modifier.background(stopColor(w.stop), RoundedCornerShape(8.dp)).padding(horizontal = 6.dp),
                            size = 11.sp, color = Color.White)
                    }
                    UrduText(w.rule, size = 12.sp, color = Quiet)
                }
            }
        }
    }
    item { UrduText("یہ وضاحتیں عام انڈو پاک مصاحف کے مطابق ہیں؛ استاد سے تصدیق کر لیں۔", size = 11.sp, color = Quiet) }
}

// ---- 4.3 Qalqalah --------------------------------------------------------------------------------------
private fun LazyListScope.qalqalah(play: (String, String) -> Unit) {
    note4("قلقلہ کے پانچ حروف ہیں: ق ط ب ج د — یاد رکھنے کو 'قُطْبُ جَدٍ'۔ جب ان پر جزم ہو تو آواز ہلکی سی اچھال کے ساتھ ادا کریں۔ لفظ کے آخر میں رکنے پر قلقلہ زیادہ واضح ہوتا ہے۔")
    item {
        Row(Modifier.fillMaxWidth().background(TealSoft, RoundedCornerShape(14.dp)).padding(10.dp).testTag("qaida_s4_qal_letters"),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            QaidaStage4.qalqalahIds.forEach { id ->
                val l = QaidaData.letter(id)
                Box(Modifier.size(52.dp).background(Color.White, CircleShape).clickable { play(l.audioKey, l.arabic) }
                    .semantics { contentDescription = l.qaidaName }, contentAlignment = Alignment.Center) {
                    ArabicText(l.arabic, size = 28.sp, color = Teal)
                }
            }
        }
    }
    items(QaidaStage4.qalqalahWords, key = { it.audioKey }) { w ->
        Card(Modifier.fillMaxWidth().clickable { play(w.audioKey, w.written) }.testTag("qaida_s4_word_${w.audioKey}"),
            colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line4)) {
            Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                ArabicText(w.written, Modifier.width(150.dp), size = 30.sp, color = Teal)
                UrduText(w.urdu, Modifier.weight(1f), size = 13.sp)
            }
        }
    }
}

// ---- 4.4 – 4.6 Short surahs -------------------------------------------------------------------------------
private fun LazyListScope.surah(play: (String, String) -> Unit, s: QaidaStage4.Surah) {
    item {
        Box(Modifier.fillMaxWidth().background(GreenSoft, RoundedCornerShape(14.dp)).padding(8.dp), contentAlignment = Alignment.Center) {
            UrduText(s.urduName, size = 18.sp, color = Green, weight = FontWeight.Bold)
        }
    }
    items(s.ayat, key = { it.audioKey }) { a ->
        Card(Modifier.fillMaxWidth().clickable { play(a.audioKey, a.text) }.testTag("qaida_ayah_${a.surah}_${a.number}"),
            colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line4)) {
            Column(Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(32.dp).background(GreenSoft, CircleShape), contentAlignment = Alignment.Center) {
                        UrduText(a.number.toString().map { "۰۱۲۳۴۵۶۷۸۹"[it - '0'] }.joinToString(""), size = 13.sp, color = Green, weight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(8.dp))
                    ArabicText(a.text, Modifier.weight(1f), size = 30.sp, align = TextAlign.Start)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UrduText("✋ وقف:", size = 12.sp, color = StopRed, weight = FontWeight.Bold)
                    Spacer(Modifier.width(6.dp))
                    ArabicText(QaidaStage4.stopOnLastWord(a.text), size = 22.sp, color = StopRed)
                }
                UrduText("💡 " + a.rules, size = 12.sp, color = Quiet)
            }
        }
    }
}
