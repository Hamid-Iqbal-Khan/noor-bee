package com.example.qaida

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Line3 = Color(0xFFE2E5EA)
private val SunBg = Color(0xFFFFF3E0)
private val SunInk = Color(0xFFE65100)
private val MoonBg = Color(0xFFE8EAF6)
private val MoonInk = Color(0xFF283593)

/** Router for the Stage 3 boards. */
@Composable
internal fun Stage3Lesson(page: String, play: (String, String) -> Unit, onDone: () -> Unit) {
    // Tashdeed board: which box is open. The breakdown appears under that letter's row.
    var selected by remember { mutableStateOf("ba" to QaidaStage3.Vowel.ZABAR) }
    LazyColumn(
        Modifier.fillMaxSize().testTag("qaida_${page}_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        when (page) {
            "s3_tashdeed" -> tashdeedBoard(play, selected) { selected = it }
            "s3_ghunna" -> ghunna(play)
            "s3_madd" -> maddSign(play)
            "s3_shamsi" -> shamsiQamari(play)
            "s3_fatiha" -> recitation(play, QaidaStage3.fatiha,
                "سورۃ الفاتحہ آیت بہ آیت پڑھیں۔ ہر آیت پر ٹیپ کر کے سنیں، پھر خود پڑھیں۔ نیچے لکھا ہے کہ اس آیت میں کون سے اصول آئے ہیں۔ آیت کے آخر میں رُکیں۔")
            "s3_ikhlas" -> recitation(play, QaidaStage3.ikhlas,
                "سورۃ الاخلاص — چار آیتیں۔ پہلے ہر لفظ آہستہ پڑھیں، پھر پوری آیت روانی سے۔")
        }
        item {
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("qaida_s3_done"),
                colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                UrduText("میں نے یہ سبق پڑھ لیا ✓", size = 15.sp, color = Color.White, weight = FontWeight.Bold)
            }
        }
    }
}

private fun LazyListScope.note(text: String) = item { UrduText(text, size = 14.sp, color = Quiet) }
private fun LazyListScope.heading(text: String) = item { UrduText(text, size = 15.sp, weight = FontWeight.Bold) }

@Composable
private fun WordCard(w: QaidaStage3.Word, play: (String, String) -> Unit, bg: Color = Color.White, ink: Color = Green) {
    Card(Modifier.fillMaxWidth().clickable { play(w.audioKey, w.written) }.testTag("qaida_s3_word_${w.audioKey}"),
        colors = CardDefaults.cardColors(containerColor = bg), border = BorderStroke(1.dp, Line3)) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            ArabicText(w.written, Modifier.width(150.dp), size = 32.sp, color = ink)
            UrduText(w.urdu, Modifier.weight(1f), size = 13.sp)
        }
    }
}

// ---- 3.1 Tashdeed -----------------------------------------------------------------------------
private fun LazyListScope.tashdeedBoard(
    play: (String, String) -> Unit,
    selected: Pair<String, QaidaStage3.Vowel>,
    onSelect: (Pair<String, QaidaStage3.Vowel>) -> Unit,
) {
    note("تشدید (ّ) کا مطلب ہے حرف دو بار: پہلی بار ساکن (پچھلے حرف کے ساتھ) اور دوسری بار حرکت کے ساتھ۔ کسی بھی خانے پر ٹیپ کریں — اس کے نیچے لفظ کا جوڑ دکھائی دے گا۔")
    items(QaidaStage3.tashdeedLetters(), key = { "t_" + it.id }) { l ->
        Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(14.dp)).padding(8.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                UrduText(l.qaidaName, Modifier.width(64.dp), size = 13.sp, color = Quiet)
                QaidaStage3.Vowel.values().forEach { v ->
                    val form = QaidaStage3.tashdeed(l, v)
                    val isSel = selected.first == l.id && selected.second == v
                    Box(Modifier.weight(1f).padding(horizontal = 4.dp).heightIn(min = 56.dp)
                        .background(
                            when { isSel -> Green; l.heavy -> Color(0xFFFBE9E7); else -> GreenSoft },
                            RoundedCornerShape(10.dp))
                        .clickable { onSelect(l.id to v); play(QaidaStage3.tashdeedKey(l, v), form) }
                        .testTag("qaida_s3_cell_${l.id}_${v.key}")
                        .semantics { contentDescription = QaidaStage3.tashdeedHijje(l, v) },
                        contentAlignment = Alignment.Center) {
                        ArabicText(form, size = 28.sp, color = when { isSel -> Color.White; l.heavy -> Heavy; else -> Ink })
                    }
                }
            }
            AnimatedVisibility(visible = selected.first == l.id) {
                val v = selected.second
                TashdeedBreakdown(l, v, play)
            }
        }
    }
}

/** Right-to-left sum, as it is read: اَبْ + بَ = اَبَّ (first part on the right, answer on the left). */
@Composable
private fun TashdeedBreakdown(l: QaidaLetter, v: QaidaStage3.Vowel, play: (String, String) -> Unit) {
    val (first, second) = QaidaStage3.tashdeedParts(l, v)
    val whole = QaidaStage3.tashdeed(l, v)
    Card(Modifier.fillMaxWidth().padding(top = 8.dp).testTag("qaida_s3_breakdown_${l.id}"),
        colors = CardDefaults.cardColors(containerColor = GreenSoft), border = BorderStroke(1.dp, Green)) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            // The whole Qaida section is laid out right-to-left, so the first child sits on the right.
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                ArabicText(first, Modifier.clickable { play(if (v == QaidaStage3.Vowel.ZABAR) QaidaStage2.audioKey(l, QaidaStage2.Mark.JAZM) else "", first) }.testTag("qaida_s3_part1"), size = 34.sp)
                UrduText("+", size = 22.sp, color = Quiet)
                ArabicText(second, Modifier.clickable { play(QaidaData.audioKey(l, QaidaData.Harakah.values()[v.ordinal]), second) }.testTag("qaida_s3_part2"), size = 34.sp)
                UrduText("=", size = 22.sp, color = Quiet)
                ArabicText(whole, Modifier.clickable { play(QaidaStage3.tashdeedKey(l, v), whole) }, size = 40.sp,
                    color = if (l.heavy) Heavy else Green)
            }
            UrduText(QaidaStage3.tashdeedHijje(l, v), size = 12.sp, color = Quiet, align = TextAlign.Center,
                modifier = Modifier.fillMaxWidth())
        }
    }
}

// ---- 3.2 Ghunna + tashdeed words -----------------------------------------------------------------
private fun LazyListScope.ghunna(play: (String, String) -> Unit) {
    note("نّ اور مّ پر تشدید ہو تو غنّہ کریں: آواز ناک میں تقریباً دو حرکت کے برابر روکیں۔")
    items(QaidaStage3.ghunnaWords, key = { it.audioKey }) { WordCard(it, play, bg = Color(0xFFF3E5F5), ink = Color(0xFF6A1B9A)) }
    heading("تشدید والے قرآنی الفاظ:")
    items(QaidaStage3.tashdeedWords, key = { it.audioKey }) { WordCard(it, play) }
}

// ---- 3.3 Madd sign ---------------------------------------------------------------------------------
private fun LazyListScope.maddSign(play: (String, String) -> Unit) {
    note("جہاں حرفِ مدّہ (ا و ی) پر مدّ کا نشان (~) ہو، وہاں آواز عام مدّ سے زیادہ لمبی کریں — تقریباً چار سے پانچ حرکت۔ قاری صاحب کی آواز سن کر لمبائی برابر رکھیں۔")
    item {
        Card(colors = CardDefaults.cardColors(containerColor = Amber), border = BorderStroke(1.dp, AmberLine)) {
            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ArabicText("بَا", size = 36.sp); UrduText("عام مدّ", size = 12.sp, color = Quiet)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ArabicText("بَآ", size = 36.sp, color = Heavy); UrduText("مدّ کا نشان — زیادہ لمبا", size = 12.sp, color = Quiet)
                }
            }
        }
    }
    items(QaidaStage3.maddSignWords, key = { it.audioKey }) { WordCard(it, play) }
}

// ---- 3.4 Sun and moon letters -----------------------------------------------------------------------
private fun LazyListScope.shamsiQamari(play: (String, String) -> Unit) {
    note("اَل (ال) کے بعد والا حرف دیکھیں۔ حرفِ شمسی ہو تو لام نہیں پڑھا جاتا اور اگلے حرف پر تشدید ہوتی ہے۔ حرفِ قمری ہو تو لام جزم سے صاف پڑھا جاتا ہے۔")
    item { LetterChips("☀️ حروفِ شمسی (۱۴)", QaidaStage3.shamsiIds, SunBg, SunInk, play, "qaida_s3_sun_letters") }
    items(QaidaStage3.shamsiWords, key = { it.audioKey }) { WordCard(it, play, bg = SunBg, ink = SunInk) }
    item { LetterChips("🌙 حروفِ قمری (۱۴)", QaidaStage3.qamariIds, MoonBg, MoonInk, play, "qaida_s3_moon_letters") }
    items(QaidaStage3.qamariWords, key = { it.audioKey }) { WordCard(it, play, bg = MoonBg, ink = MoonInk) }
}

@Composable
private fun LetterChips(title: String, ids: List<String>, bg: Color, ink: Color, play: (String, String) -> Unit, tag: String) {
    Card(Modifier.fillMaxWidth().testTag(tag), colors = CardDefaults.cardColors(containerColor = bg)) {
        Column(Modifier.padding(10.dp)) {
            UrduText(title, size = 15.sp, color = ink, weight = FontWeight.Bold)
            ids.chunked(7).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    row.forEach { id ->
                        val l = QaidaData.letter(id)
                        Box(Modifier.size(40.dp).background(Color.White, CircleShape).clickable { play(l.audioKey, l.arabic) }
                            .semantics { contentDescription = l.qaidaName }, contentAlignment = Alignment.Center) {
                            ArabicText(l.arabic, size = 22.sp, color = ink)
                        }
                    }
                }
                Spacer(Modifier.heightIn(min = 6.dp))
            }
        }
    }
}

// ---- 3.5 / 3.6 Recitation --------------------------------------------------------------------------
private fun LazyListScope.recitation(play: (String, String) -> Unit, ayat: List<QaidaStage3.Ayah>, intro: String) {
    note(intro)
    items(ayat, key = { it.audioKey }) { a ->
        Card(Modifier.fillMaxWidth().clickable { play(a.audioKey, a.text) }.testTag("qaida_ayah_${a.surah}_${a.number}"),
            colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Line3)) {
            Column(Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(32.dp).background(GreenSoft, CircleShape), contentAlignment = Alignment.Center) {
                        UrduText(a.number.toString().map { "۰۱۲۳۴۵۶۷۸۹"[it - '0'] }.joinToString(""), size = 13.sp, color = Green, weight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(8.dp))
                    ArabicText(a.text, Modifier.weight(1f), size = 30.sp, align = TextAlign.Start)
                }
                UrduText("💡 " + a.rules, size = 12.sp, color = Quiet)
            }
        }
    }
    item {
        UrduText("یہ متن انڈو پاک طرز پر ہے؛ وقف کے نشانات ابھی شامل نہیں۔ استاد چھپے ہوئے مصحف سے ملا کر تصدیق کریں۔", size = 11.sp, color = Quiet)
    }
}
