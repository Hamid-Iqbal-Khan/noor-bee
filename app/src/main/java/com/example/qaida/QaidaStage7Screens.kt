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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Line7 = Color(0xFFE2E5EA)

internal fun ruleColor(r: QaidaStage7.Rule) = when (r) {
    QaidaStage7.Rule.IZHAR -> Color(0xFF1565C0)
    QaidaStage7.Rule.IDGHAM_GHUNNA -> Color(0xFF2E7D32)
    QaidaStage7.Rule.IDGHAM_NO_GHUNNA -> Color(0xFF6D4C41)
    QaidaStage7.Rule.IQLAB -> Color(0xFFAD1457)
    QaidaStage7.Rule.IKHFA -> Color(0xFFE65100)
}

/** Router for the Stage 7 boards. */
@Composable
internal fun Stage7Lesson(page: String, play: (String, String) -> Unit, onDone: () -> Unit) {
    // Practice board: which cards have been turned over.
    var revealed by remember { mutableStateOf(setOf<String>()) }
    LazyColumn(
        Modifier.fillMaxSize().testTag("qaida_${page}_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        when (page) {
            "s7_izhar" -> rule(play, QaidaStage7.Rule.IZHAR, QaidaStage7.izharIds)
            "s7_idgham" -> {
                rule(play, QaidaStage7.Rule.IDGHAM_GHUNNA, QaidaStage7.idghamGhunnaIds)
                rule(play, QaidaStage7.Rule.IDGHAM_NO_GHUNNA, QaidaStage7.idghamNoGhunnaIds)
                item {
                    UrduText("یاد رکھیں: ایک ہی لفظ کے اندر نْ کے بعد ی یا و ہو تو ادغام نہیں ہوتا، جیسے دُنْيَا اور صِنْوَانٌ — نون صاف پڑھیں۔",
                        size = 12.sp, color = Quiet)
                }
            }
            "s7_iqlab" -> rule(play, QaidaStage7.Rule.IQLAB, QaidaStage7.iqlabIds)
            "s7_ikhfa" -> rule(play, QaidaStage7.Rule.IKHFA, QaidaStage7.ikhfaIds)
            "s7_meem" -> meem(play)
            "s7_spot" -> spot(play, revealed) { revealed = revealed + it }
        }
        item {
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("qaida_s7_done"),
                colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                UrduText("میں نے یہ سبق پڑھ لیا ✓", size = 15.sp, color = Color.White, weight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
private fun LazyListScope.rule(play: (String, String) -> Unit, r: QaidaStage7.Rule, ids: List<String>) {
    val c = ruleColor(r)
    item {
        Column(Modifier.fillMaxWidth().background(c.copy(alpha = 0.08f), RoundedCornerShape(14.dp)).padding(12.dp)
            .testTag("qaida_s7_rule_${r.name.lowercase()}")) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UrduText(r.urdu, size = 18.sp, color = c, weight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                UrduText(r.short, Modifier.background(c, RoundedCornerShape(8.dp)).padding(horizontal = 6.dp), size = 11.sp, color = Color.White)
            }
            UrduText(r.how, size = 13.sp)
            UrduText("حروف (${ids.size}):", size = 12.sp, color = Quiet)
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ids.forEach { id ->
                    val l = QaidaData.letter(id)
                    Box(Modifier.size(44.dp).background(Color.White, CircleShape).clickable { play(l.audioKey, l.arabic) }
                        .semantics { contentDescription = l.qaidaName }, contentAlignment = Alignment.Center) {
                        ArabicText(l.arabic, size = 24.sp, color = c)
                    }
                }
            }
        }
    }
    items(QaidaStage7.examplesFor(r), key = { it.audioKey }) { e -> ExampleCard(e.phrase, e.note, e.from, e.audioKey, c, play) }
}

@Composable
private fun ExampleCard(phrase: String, note: String, from: String, key: String, c: Color, play: (String, String) -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { play(key, phrase) }.testTag("qaida_s7_ex_$key"),
        colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line7)) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            ArabicText(phrase, Modifier.width(170.dp), size = 30.sp, color = c)
            Column(Modifier.weight(1f)) {
                UrduText(note, size = 13.sp, weight = FontWeight.Bold)
                if (from.isNotEmpty()) UrduText("📖 $from میں آیا", size = 11.sp, color = Quiet)
            }
        }
    }
}

// ---- Meem sakin -------------------------------------------------------------------------------------------
private fun LazyListScope.meem(play: (String, String) -> Unit) {
    item { UrduText("میم ساکن (مْ) کے تین قاعدے ہیں — اگلا حرف دیکھیں:", size = 14.sp, color = Quiet) }
    QaidaStage7.MeemRule.values().forEach { m ->
        val c = when (m) {
            QaidaStage7.MeemRule.IKHFA_SHAFAWI -> Color(0xFFE65100)
            QaidaStage7.MeemRule.IDGHAM_SHAFAWI -> Color(0xFF2E7D32)
            QaidaStage7.MeemRule.IZHAR_SHAFAWI -> Color(0xFF1565C0)
        }
        item {
            Column(Modifier.fillMaxWidth().background(c.copy(alpha = 0.08f), RoundedCornerShape(14.dp)).padding(12.dp)
                .testTag("qaida_s7_meem_${m.name.lowercase()}")) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UrduText(m.urdu, size = 17.sp, color = c, weight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    UrduText("مْ + ${m.letters}", Modifier.background(c, RoundedCornerShape(8.dp)).padding(horizontal = 6.dp), size = 11.sp, color = Color.White)
                }
                UrduText(m.how, size = 13.sp)
            }
        }
        items(QaidaStage7.meemExamples.filter { it.rule == m }, key = { it.audioKey }) { e -> ExampleCard(e.phrase, e.note, e.from, e.audioKey, c, play) }
    }
}

// ---- Spot the rule ----------------------------------------------------------------------------------------
private fun LazyListScope.spot(play: (String, String) -> Unit, revealed: Set<String>, reveal: (String) -> Unit) {
    item { UrduText("ہر کارڈ پر نون ساکن یا تنوین ڈھونڈیں، اگلا حرف دیکھیں اور قاعدہ خود بولیں۔ پھر کارڈ پر ٹیپ کر کے جواب دیکھیں۔", size = 14.sp, color = Quiet) }
    items(QaidaStage7.examples.sortedBy { it.audioKey.hashCode() }, key = { "spot_" + it.audioKey }) { e ->
        val open = e.audioKey in revealed
        val c = ruleColor(e.rule)
        Card(Modifier.fillMaxWidth().clickable { reveal(e.audioKey); play(e.audioKey, e.phrase) }.testTag("qaida_s7_spot_${e.audioKey}"),
            colors = CardDefaults.cardColors(containerColor = if (open) c.copy(alpha = 0.08f) else Color.White),
            border = BorderStroke(1.dp, if (open) c else Line7)) {
            Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                ArabicText(e.phrase, Modifier.width(170.dp), size = 30.sp)
                if (open) {
                    Column(Modifier.weight(1f)) {
                        UrduText(e.rule.urdu, size = 15.sp, color = c, weight = FontWeight.Bold)
                        UrduText(e.note, size = 12.sp, color = Quiet)
                    }
                } else {
                    UrduText("❓ قاعدہ؟", Modifier.weight(1f), size = 14.sp, color = Quiet)
                }
            }
        }
    }
}
