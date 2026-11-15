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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

private val Line = Color(0xFFE2E5EA)

/** Router for the six Stage 2 boards (Takhti 2–9). */
@Composable
internal fun Stage2Lesson(page: String, play: (String, String) -> Unit, onDone: () -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize().testTag("qaida_${page}_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        when (page) {
            "s2_joining" -> joining(play)
            "s2_muqattaat" -> muqattaat(play)
            "s2_tanween" -> markBoard(play, QaidaStage2.Mark.values().filter { it.lesson == "tanween" },
                "تنوین کا مطلب ہے آخر میں چھپی ہوئی 'ن' کی آواز۔ دو زبر کو 'اَن'، دو زیر کو 'اِن' اور دو پیش کو 'اُن' پڑھیں۔ دو زبر عموماً الف کے ساتھ لکھا جاتا ہے، الف نہیں پڑھا جاتا۔", null)
            "s2_khara" -> markBoard(play, QaidaStage2.Mark.values().filter { it.lesson == "khara" },
                "انڈو پاک قرآن میں یہ نشان بہت آتے ہیں۔ کھڑا زبر = زبر + الف کی لمبائی، کھڑی زیر = زیر + یا کی لمبائی، اُلٹا پیش = پیش + واؤ کی لمبائی۔ ایک الف کے برابر (تقریباً دو حرکت) کھینچیں۔",
                QaidaStage2.kharaWords)
            "s2_madd" -> madd(play)
            "s2_jazm" -> markBoard(play, listOf(QaidaStage2.Mark.JAZM),
                "جزم والا حرف ساکن ہوتا ہے — اس پر کوئی حرکت نہیں۔ اسے اکیلا نہ پڑھیں، پچھلے حرف کے ساتھ ملا کر پڑھیں۔ ہجے: الف زبر اَ، با جزم — اَبْ۔",
                QaidaStage2.jazmWords)
        }
        item {
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("qaida_s2_done"),
                colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                UrduText("میں نے یہ تختی پڑھ لی ✓", size = 15.sp, color = Color.White, weight = FontWeight.Bold)
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.intro(text: String) =
    item { UrduText(text, size = 14.sp, color = Quiet) }

// ---- 2.1 Joining ----------------------------------------------------------------------------
private fun androidx.compose.foundation.lazy.LazyListScope.joining(play: (String, String) -> Unit) {
    intro("عربی حروف آپس میں جڑ کر لکھے جاتے ہیں، جیسے اردو میں۔ ہر حرف کی چار شکلیں ہو سکتی ہیں: اکیلا، شروع، درمیان، آخر۔ ا د ذ ر ز و اپنے بعد والے حرف سے نہیں جڑتے۔")
    item {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            listOf("اکیلا", "شروع", "درمیان", "آخر").forEach {
                UrduText(it, Modifier.weight(1f), size = 12.sp, color = Quiet, align = TextAlign.Center)
            }
        }
    }
    items(QaidaData.letters, key = { "f_" + it.id }) { l ->
        val f = QaidaStage2.forms(l)
        val non = l.id in QaidaStage2.nonConnectorIds
        Row(Modifier.fillMaxWidth().background(if (non) Amber else Color.White, RoundedCornerShape(12.dp))
            .clickable { play(l.audioKey, l.arabic) }.padding(4.dp).testTag("qaida_forms_${l.id}")
            .semantics { contentDescription = l.qaidaName + if (non) "، آگے نہیں جڑتا" else "" },
            verticalAlignment = Alignment.CenterVertically) {
            listOf(f.isolated, f.initial, f.medial, f.final).forEach {
                ArabicText(it, Modifier.weight(1f), size = 30.sp, color = if (l.heavy) Heavy else Ink)
            }
        }
    }
    item { UrduText("اب لفظ کو حروف میں توڑیں — ٹیپ کر کے سنیں:", size = 15.sp, weight = FontWeight.Bold) }
    items(QaidaStage2.joinedWords, key = { it.audioKey }) { w ->
        Card(Modifier.fillMaxWidth().clickable { play(w.audioKey, w.word) }.testTag("qaida_joined_${w.audioKey}"),
            colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line)) {
            Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                ArabicText(w.word, Modifier.width(90.dp), size = 34.sp, color = Green)
                UrduText("=", size = 18.sp, color = Quiet)
                Spacer(Modifier.width(8.dp))
                ArabicText(w.letterIds.joinToString(" + ") { QaidaData.letter(it).arabic }, Modifier.weight(1f), size = 26.sp)
                UrduText(w.urduMeaning, size = 12.sp, color = Quiet)
            }
        }
    }
}

// ---- 2.2 Muqatta'at -----------------------------------------------------------------------
private fun androidx.compose.foundation.lazy.LazyListScope.muqattaat(play: (String, String) -> Unit) {
    intro("قرآن کی ۲۹ سورتیں ایسے حروف سے شروع ہوتی ہیں جو ملا کر نہیں بلکہ ہر حرف کا نام لے کر پڑھے جاتے ہیں۔ ان کا اصل مطلب اللہ ہی بہتر جانتا ہے۔")
    items(QaidaStage2.muqattaat, key = { it.id }) { m ->
        Card(Modifier.fillMaxWidth().clickable { play(m.audioKey, m.text) }.testTag("qaida_muqattaat_${m.id}"),
            colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line)) {
            Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                ArabicText(m.text, Modifier.width(120.dp), size = 34.sp, color = Green)
                Column(Modifier.weight(1f)) {
                    UrduText(m.reading, size = 16.sp, weight = FontWeight.Bold)
                    UrduText(m.surahs, size = 11.sp, color = Quiet)
                }
            }
        }
    }
}

// ---- 2.3 / 2.4 / 2.6 Mark boards ------------------------------------------------------------
private fun androidx.compose.foundation.lazy.LazyListScope.markBoard(
    play: (String, String) -> Unit, marks: List<QaidaStage2.Mark>, introText: String,
    words: List<QaidaStage2.MaddExample>?,
) {
    intro(introText)
    item {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            marks.forEach { m ->
                Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = GreenSoft)) {
                    Column(Modifier.fillMaxWidth().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        ArabicText(m.sample, size = 40.sp, color = Green)
                        UrduText(m.urduName, size = 15.sp, weight = FontWeight.Bold, align = TextAlign.Center)
                        UrduText(m.hint, size = 11.sp, color = Quiet, align = TextAlign.Center)
                    }
                }
            }
        }
    }
    if (words != null) {
        item { UrduText("قرآنی مثالیں:", size = 15.sp, weight = FontWeight.Bold) }
        items(words, key = { it.audioKey }) { w -> ExampleRow(w, play) }
        item { UrduText("اب ہر حرف پر مشق کریں:", size = 15.sp, weight = FontWeight.Bold) }
    }
    val letters = QaidaStage2.boardLetters(marks.first())
    items(letters, key = { "m_" + marks.first().name + it.id }) { l ->
        Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(14.dp)).padding(8.dp).testTag("qaida_s2_row_${l.id}"),
            verticalAlignment = Alignment.CenterVertically) {
            UrduText(l.qaidaName, Modifier.width(64.dp), size = 13.sp, color = Quiet)
            marks.forEach { m ->
                val form = QaidaStage2.write(l, m)
                Box(Modifier.weight(1f).padding(horizontal = 4.dp).heightIn(min = 56.dp)
                    .background(if (l.heavy) Color(0xFFFBE9E7) else GreenSoft, RoundedCornerShape(10.dp))
                    .clickable { play(QaidaStage2.audioKey(l, m), form) }
                    .semantics { contentDescription = QaidaStage2.hijje(l, m) },
                    contentAlignment = Alignment.Center) {
                    ArabicText(form, size = 30.sp, color = if (l.heavy) Heavy else Ink)
                }
            }
        }
    }
}

// ---- 2.5 Madd and leen ----------------------------------------------------------------------
private fun androidx.compose.foundation.lazy.LazyListScope.madd(play: (String, String) -> Unit) {
    intro("حروفِ مدّہ تین ہیں: ا و ی۔ زبر کے بعد الف، پیش کے بعد واؤ، زیر کے بعد یا ہو تو آواز ایک الف کے برابر لمبی کریں۔")
    items(QaidaStage2.madd, key = { it.audioKey }) { ExampleRow(it, play) }
    item { UrduText("حروفِ لین دو ہیں: ساکن و اور ساکن ی جن سے پہلے زبر ہو۔ انہیں نرمی سے، بغیر جھٹکے اور بغیر کھینچے پڑھیں۔", size = 14.sp, color = Quiet) }
    items(QaidaStage2.leen, key = { it.audioKey }) { ExampleRow(it, play) }
}

@Composable
private fun ExampleRow(w: QaidaStage2.MaddExample, play: (String, String) -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { play(w.audioKey, w.written) }.testTag("qaida_ex_${w.audioKey}"),
        colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line)) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            ArabicText(w.written, Modifier.width(120.dp), size = 32.sp, color = Green)
            UrduText(w.urdu, Modifier.weight(1f), size = 13.sp)
        }
    }
}
