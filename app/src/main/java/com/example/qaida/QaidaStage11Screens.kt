package com.example.qaida

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Teal11 = Color(0xFF00695C)
private val Teal11Soft = Color(0xFFE0F2F1)
private val Line11 = Color(0xFFE2E5EA)

/** Router for the Stage 11 (grammar) boards. */
@Composable
internal fun Stage11Lesson(page: String, play: (String, String) -> Unit, onDone: () -> Unit) {
    var revealed by remember { mutableStateOf(setOf<String>()) }
    LazyColumn(
        Modifier.fillMaxSize().testTag("qaida_${page}_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        when (page) {
            "s11_roots" -> roots(play)
            "s11_patterns" -> patterns(play)
            "s11_past" -> forms(play, QaidaStage11.past,
                "ماضی (گزرا ہوا کام): لفظ کا آخری حصہ بتاتا ہے کہ کس نے کیا — ـَ وہ، ـُوْا وہ سب، ـَتْ وہ (عورت)، ـْتَ تو، ـْتُ میں، ـْنَا ہم۔ نوٹ: خَلَقَ جیسے سادہ فعل کے علاوہ قَدَّمَ اور اَنْعَمَ جیسے دوسرے سانچے بھی ہیں؛ یہاں صرف آخری حصہ سیکھنا ہے۔")
            "s11_present" -> forms(play, QaidaStage11.present,
                "مضارع (ہو رہا یا ہونے والا کام): شروع کا حرف بتاتا ہے کہ کون کر رہا ہے — يَـ وہ، تَـ تو، اَ میں، نَـ ہم۔ آخر میں ـُوْنَ ہو تو کئی لوگ۔")
            "s11_commands" -> commands(play)
            "s11_detective" -> detective(play, revealed) { revealed = revealed + it }
        }
        item {
            UrduText("یہ گرامر بچوں کے لیے آسان کر کے لکھی گئی ہے؛ تفصیل استاد سے سیکھیں۔", size = 11.sp, color = Quiet)
        }
        item {
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("qaida_s11_done"),
                colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                UrduText("میں نے یہ سبق پڑھ لیا ✓", size = 15.sp, color = Color.White, weight = FontWeight.Bold)
            }
        }
    }
}

private fun LazyListScope.note11(text: String) = item { UrduText(text, size = 14.sp, color = Quiet) }

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WordChips(words: List<QaidaStage11.Word>, play: (String, String) -> Unit) = ArabicFlow {
    FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        words.forEach { w ->
            Column(Modifier.widthIn(min = 84.dp).background(Color.White, RoundedCornerShape(12.dp)).clickable { play("", w.arabic) }
                .padding(horizontal = 10.dp, vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                ArabicText(w.arabic, size = 24.sp, color = Teal11)
                UrduText(w.urdu, size = 12.sp, align = TextAlign.Center)
                if (w.note.isNotEmpty()) UrduText(w.note, size = 10.sp, color = Quiet, align = TextAlign.Center)
            }
        }
    }
}

// ---- 11.1 Roots --------------------------------------------------------------------------------------------
private fun LazyListScope.roots(play: (String, String) -> Unit) {
    note11("زیادہ تر عربی الفاظ تین حرفوں کے 'مادے' (جڑ) سے بنتے ہیں۔ مادہ ایک خیال بتاتا ہے، اور اس کے ارد گرد حروف بدلنے سے نئے الفاظ بنتے ہیں۔ اچھی خبر: اردو میں ان میں سے بہت سے الفاظ آپ پہلے سے جانتے ہیں!")
    items(QaidaStage11.roots, key = { it.id }) { r ->
        Column(Modifier.fillMaxWidth().background(Teal11Soft, RoundedCornerShape(14.dp)).padding(12.dp).testTag("qaida_s11_root_${r.id}")) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ArabicText(r.letters, size = 30.sp, color = Teal11)
                UrduText("  =  " + r.idea, size = 16.sp, weight = FontWeight.Bold)
            }
            WordChips(r.words, play)
        }
    }
}

// ---- 11.2 Patterns -----------------------------------------------------------------------------------------
private fun LazyListScope.patterns(play: (String, String) -> Unit) {
    note11("عربی میں الفاظ سانچوں (اوزان) میں ڈھلتے ہیں۔ سانچہ ف ع ل سے دکھایا جاتا ہے۔ ف ع ل کی جگہ مادے کے تین حروف رکھیں: فَاعِل میں ع ل م رکھیں تو عَالِم بنتا ہے۔")
    items(QaidaStage11.patterns, key = { it.id }) { p ->
        Column(Modifier.fillMaxWidth().background(Teal11Soft, RoundedCornerShape(14.dp)).padding(12.dp).testTag("qaida_s11_pattern_${p.id}")) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ArabicText(p.pattern, size = 30.sp, color = Teal11)
                UrduText("  =  " + p.meaning, size = 15.sp, weight = FontWeight.Bold)
            }
            WordChips(p.examples, play)
        }
    }
}

// ---- 11.3 / 11.4 Verb forms -----------------------------------------------------------------------------------
private fun LazyListScope.forms(play: (String, String) -> Unit, list: List<QaidaStage11.Form>, intro: String) {
    note11(intro)
    items(list, key = { it.ending }) { f ->
        Card(Modifier.fillMaxWidth().clickable { play("", f.example) }.testTag("qaida_s11_form_${f.ending}"),
            colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line11)) {
            Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.width(110.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    ArabicText(f.ending, size = 26.sp, color = Teal11)
                    UrduText(f.who, size = 11.sp, color = Quiet, align = TextAlign.Center)
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    ArabicText(f.example, size = 30.sp, color = Green)
                    UrduText(f.exampleUrdu, size = 13.sp, weight = FontWeight.Bold, align = TextAlign.Center)
                    if (f.from.isNotEmpty()) UrduText("📖 ${f.from}", size = 10.sp, color = Quiet)
                }
            }
        }
    }
}

// ---- 11.5 Commands and negatives --------------------------------------------------------------------------
private fun LazyListScope.commands(play: (String, String) -> Unit) {
    note11("حکم (امر): مضارع کا شروع والا حرف ہٹا کر آخر ساکن کر دیں۔ تَقْرَاُ (تو پڑھتا ہے) سے اِقْرَاْ (پڑھو) — شروع میں جزم آ جائے تو اس سے پہلے الف لگتا ہے۔ تَقُوْلُ (تو کہتا ہے) سے قُلْ (کہو) — بیچ کا و بھی گر جاتا ہے۔")
    item { Column(Modifier.fillMaxWidth().background(Teal11Soft, RoundedCornerShape(14.dp)).padding(12.dp)) { WordChips(QaidaStage11.commands, play) } }
    note11("'نہیں' کے چار طریقے:")
    items(QaidaStage11.negatives, key = { it.arabic }) { w ->
        Card(Modifier.fillMaxWidth().clickable { play("", w.arabic) }, colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Line11)) {
            Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                ArabicText(w.arabic, Modifier.width(140.dp), size = 28.sp, color = Teal11)
                Column(Modifier.weight(1f)) {
                    UrduText(w.urdu, size = 14.sp, weight = FontWeight.Bold)
                    UrduText(w.note, size = 11.sp, color = Quiet)
                }
            }
        }
    }
}

// ---- 11.6 Root detective ------------------------------------------------------------------------------------
private fun LazyListScope.detective(play: (String, String) -> Unit, revealed: Set<String>, reveal: (String) -> Unit) {
    note11("مادہ جاسوس 🔍: لفظ میں سے اضافی حروف (اَل، مَـ، يَـ، ـُوْنَ، ـِيْنَ، الف وغیرہ) ہٹائیں اور تین اصل حروف ڈھونڈیں۔ پھر کارڈ پر ٹیپ کر کے دیکھیں۔")
    items(QaidaStage11.detective, key = { it.word }) { d ->
        val open = d.word in revealed
        val root = QaidaStage11.root(d.rootId)
        Card(Modifier.fillMaxWidth().clickable { reveal(d.word); play("", d.word) }.testTag("qaida_s11_detect_${d.rootId}_${d.word.length}"),
            colors = CardDefaults.cardColors(containerColor = if (open) Teal11Soft else Color.White),
            border = BorderStroke(1.dp, if (open) Teal11 else Line11)) {
            Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                ArabicText(d.word, Modifier.width(150.dp), size = 30.sp)
                if (open) {
                    Column(Modifier.weight(1f)) {
                        ArabicText(root.letters, size = 22.sp, color = Teal11, align = TextAlign.Start)
                        UrduText(tx("${root.idea} · ${d.urdu}", "${tr(root.idea)} · ${tr(d.urdu)}"), size = 12.sp, color = Quiet)
                    }
                } else UrduText("❓ مادہ؟", Modifier.weight(1f), size = 14.sp, color = Quiet)
            }
        }
    }
}
