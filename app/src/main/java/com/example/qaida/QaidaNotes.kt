package com.example.qaida

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * Short, well-known background for surahs, and notes for parents on ayat that need a gentle explanation.
 * Kept to facts that are widely agreed in tafsir and authentic narrations; a scholar should still review them.
 */
object QaidaNotes {
    val background = mapOf(
        1 to "قرآن کی پہلی سورت، اسے 'اُمُّ القرآن' بھی کہتے ہیں۔ ہر نماز کی ہر رکعت میں پڑھی جاتی ہے؛ اس میں اللہ کی تعریف اور سیدھے راستے کی دعا ہے۔",
        112 to "اس سورت میں اللہ کی توحید (ایک ہونا) بیان ہے۔ نبی ﷺ نے فرمایا کہ یہ سورت ایک تہائی قرآن کے برابر ہے۔",
        108 to "جب دشمنوں نے طعنہ دیا کہ نبی ﷺ کا نام آگے نہیں چلے گا تو اللہ نے آپ ﷺ کو تسلی دی کہ آپ کو بہت بھلائی دی گئی ہے۔",
        103 to "تین آیتوں میں کامیابی کے چار اصول: ایمان، نیک کام، حق کی نصیحت اور صبر کی نصیحت۔",
        113 to "یہ اور اگلی سورت 'مُعَوِّذَتَین' (پناہ کی دو سورتیں) کہلاتی ہیں۔ نبی ﷺ سوتے وقت انہیں پڑھ کر ہاتھوں پر دم کرتے اور جسم پر پھیرتے تھے۔",
        114 to "پناہ کی دوسری سورت: اندر کے وسوسوں سے اللہ کی پناہ۔ سونے سے پہلے اور صبح و شام پڑھنا سنت ہے۔",
        105 to "نبی ﷺ کی پیدائش کے سال یمن کا حاکم ابرہہ ہاتھیوں والا لشکر لے کر کعبہ گرانے آیا۔ اللہ نے پرندوں کے ذریعے کنکریاں برسا کر پورا لشکر تباہ کر دیا۔",
        106 to "قریش مکہ کے تاجر تھے؛ سردیوں میں یمن اور گرمیوں میں شام کا تجارتی سفر کرتے تھے۔ کعبہ کی برکت سے ان کے سفر محفوظ تھے، اس لیے انہیں کعبہ کے رب کی عبادت کا حکم ہے۔",
        107 to "دین صرف نماز کا نام نہیں: یتیم اور غریب کا خیال رکھنا، دکھاوے سے بچنا اور چھوٹی چھوٹی مدد کرنا بھی دین ہے۔",
        109 to "مکہ کے سرداروں نے پیشکش کی کہ کچھ دن آپ ﷺ ان کے بتوں کی عبادت کریں اور کچھ دن وہ اللہ کی۔ اس پر یہ سورت اتری: عبادت میں کوئی سودا نہیں۔",
        110 to "فتحِ مکہ اور لوگوں کے جوق در جوق اسلام قبول کرنے کی خوشخبری۔ اسے آخری مکمل نازل ہونے والی سورت کہا جاتا ہے۔",
        111 to "ابو لہب نبی ﷺ کا چچا تھا مگر آپ ﷺ کا سخت دشمن؛ اس کی بیوی آپ ﷺ کے راستے میں کانٹے بچھاتی تھی۔ رشتہ داری ایمان کے بغیر کام نہیں آتی۔",
        104 to "طعنہ دینے، غیبت کرنے اور مال پر گھمنڈ کرنے کا انجام۔",
        102 to "زیادہ سے زیادہ جمع کرنے کی دوڑ انسان کو اللہ سے غافل کر دیتی ہے؛ ہر نعمت کا حساب ہوگا۔",
        99 to "قیامت کے دن زمین سب کچھ بتا دے گی؛ ذرّہ برابر نیکی بھی ضائع نہیں ہوتی۔",
        97 to "قرآن شبِ قدر میں اترا۔ یہ رات رمضان کی آخری دس راتوں میں (خاص طور پر طاق راتوں میں) تلاش کی جاتی ہے۔",
        96 to "اس کی پہلی پانچ آیتیں سب سے پہلی وحی ہیں جو غارِ حرا میں نبی ﷺ پر اتریں۔ آخری آیت سجدے کی آیت ہے۔",
        94 to "مشکل وقت میں نبی ﷺ کو تسلی: ہر مشکل کے ساتھ آسانی ہے۔",
        93 to "کچھ دن وحی رکی رہی تو دشمنوں نے کہا کہ رب نے چھوڑ دیا۔ اس پر یہ سورت تسلی کے لیے اتری۔",
        91 to "قومِ ثمود کا واقعہ: انہوں نے اللہ کی نشانی اونٹنی کو مار ڈالا اور تباہ ہوئے۔ کامیابی دل کو پاک رکھنے میں ہے۔",
        89 to "عاد، ثمود اور فرعون جیسی طاقتور قوموں کا انجام، اور 'اطمینان والی جان' کی خوشخبری۔",
        87 to "نبی ﷺ جمعہ اور عید کی نماز میں اکثر یہ سورت (اور الغاشیہ) پڑھتے تھے۔",
        85 to "پچھلے زمانے کے کچھ ایمان والوں کو صرف ایمان کی وجہ سے آگ کی کھائیوں میں ڈالا گیا؛ اللہ نے ظالموں کے انجام کی خبر دی۔",
        81 to "قیامت کے مناظر۔ نبی ﷺ نے فرمایا: جو قیامت کو آنکھوں سے دیکھنا چاہے وہ التکویر، الانفطار اور الانشقاق پڑھے۔",
        80 to "نبی ﷺ مکہ کے سرداروں کو اسلام کی دعوت دے رہے تھے کہ نابینا صحابی حضرت عبداللہ بن ام مکتومؓ دین سیکھنے آئے۔ اللہ نے سکھایا کہ سچا طالبِ علم زیادہ توجہ کا حق دار ہے۔ بعد میں نبی ﷺ ان کی بہت عزت فرماتے تھے۔",
        78 to "اس سورت سے پارہ عمّ شروع ہوتا ہے: قیامت کی 'بڑی خبر'، اللہ کی نعمتیں، اور جنت و دوزخ۔",
    )

    val parentNotes = mapOf(
        (80 to 1) to "نبی ﷺ کا ادب ہر حال میں لازم ہے۔ بچوں کو بتائیں کہ یہاں اللہ نے ہمیں سکھایا کہ ہر طالبِ علم کی قدر کریں، چاہے وہ غریب ہو یا نابینا۔",
        (81 to 8) to "جاہلیت کے زمانے میں کچھ لوگ بیٹیوں کو زندہ دفن کر دیتے تھے۔ اسلام نے اسے بڑا گناہ کہا اور بیٹیوں کو عزت دی۔ چھوٹے بچوں کو نرمی سے مختصر بتائیں۔",
        (78 to 33) to "جنت کی نعمتوں کا ذکر ہے۔ چھوٹے بچوں کو بس اتنا بتانا کافی ہے کہ جنت میں ہر خوشی اور ہر اچھی چیز ہوگی۔",
        (89 to 22) to "اللہ کو مخلوق جیسا نہ سمجھیں؛ وہ ویسے آئے گا جیسے اس کی شان کے لائق ہے۔",
        (111 to 1) to "یہ سورت کسی کو برا کہنا نہیں سکھاتی؛ یہ بتاتی ہے کہ اللہ کے رسول ﷺ سے دشمنی کا انجام برا ہے۔",
    )
}

@Composable
internal fun SurahBackground(surah: Int) {
    val text = QaidaNotes.background[surah] ?: return
    Column(Modifier.fillMaxWidth().padding(top = 6.dp).background(Color(0xFFFFF8E1), RoundedCornerShape(10.dp)).padding(8.dp)
        .testTag("qaida_background_$surah")) {
        UrduText("📜 پس منظر", size = 12.sp, color = Color(0xFF8D6E63), weight = FontWeight.Bold)
        UrduText(text, size = 12.sp, color = Ink)
    }
}

@Composable
internal fun ParentNote(surah: Int, ayah: Int) {
    val text = QaidaNotes.parentNotes[surah to ayah] ?: return
    Row(Modifier.fillMaxWidth().padding(top = 4.dp).background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp)).padding(8.dp)
        .testTag("qaida_parent_note_${surah}_$ayah")) {
        UrduText("👪 والدین کے لیے: $text", size = 11.sp, color = Ink)
    }
}

// ---- Makhraj map ----------------------------------------------------------------------------------------
private data class Area(val name: String, val note: String, val letters: String, val color: Color)

private val areas = listOf(   // in the order sound leaves the body: from the throat to the lips (right to left)
    Area("حلق", "گلا: آخر، بیچ، شروع", "ء ہ · ع ح · غ خ", Color(0xFFE57373)),
    Area("زبان", "زبان کی جڑ سے نوک تک", "ق ک ج ش ی ض ل ن ر ط د ت ص ز س ظ ذ ث", Color(0xFFFFB74D)),
    Area("ہونٹ", "دونوں ہونٹ", "ف ب م و", Color(0xFF81C784)),
)
private val jawf = Area("جوف", "منہ اور حلق کا خالی حصہ", "مدّ والے: ا و ی", Color(0xFF64B5F6))
private val khayshum = Area("خیشوم", "ناک کا بانسہ", "غنّہ: نّ مّ", Color(0xFFBA68C8))

/** The five makhraj areas as a simple diagram: sound travels from the throat (right) to the lips (left). */
@Composable
internal fun MakhrajMap() {
    var open by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(14.dp)).clickable { open = !open }.padding(10.dp)
        .testTag("qaida_makhraj_map")) {
        UrduText(if (open) "📍 مخارج کا نقشہ ▲" else "📍 مخارج کا نقشہ — حرف کہاں سے نکلتا ہے؟ (دیکھنے کے لیے ٹیپ کریں) ▼",
            size = 14.sp, color = Green, weight = FontWeight.Bold)
        if (!open) return@Column
        val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
        Canvas(Modifier.fillMaxWidth().height(90.dp).padding(vertical = 6.dp)) {
            val w = size.width; val h = size.height
            // nose (khayshum) above, mouth tube below; throat on the right in RTL
            val nose = khayshum.color; val tube = h * 0.45f; val top = h * 0.45f
            drawRoundRect(jawf.color.copy(alpha = 0.25f), Offset(0f, top), Size(w, tube), CornerRadius(24f, 24f))
            val seg = w / 3f
            areas.forEachIndexed { i, a ->
                val x = if (rtl) w - seg * (i + 1) else seg * i
                drawRoundRect(a.color, Offset(x + 4f, top + 6f), Size(seg - 8f, tube - 12f), CornerRadius(18f, 18f))
            }
            drawArc(nose, startAngle = 180f, sweepAngle = 180f, useCenter = false,
                topLeft = Offset(w * 0.25f, 4f), size = Size(w * 0.5f, top * 1.6f), style = Stroke(width = 8f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            areas.forEach { a ->
                Column(Modifier.weight(1f).padding(2.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(10.dp).background(a.color, CircleShape))
                    UrduText(a.name, size = 13.sp, weight = FontWeight.Bold, align = TextAlign.Center)
                    UrduText(a.note, size = 10.sp, color = Quiet, align = TextAlign.Center)
                    ArabicText(a.letters, size = 16.sp, color = Ink)
                }
            }
        }
        listOf(jawf, khayshum).forEach { a ->
            Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(10.dp).background(a.color, CircleShape))
                UrduText("  ${a.name} (${a.note}): ", size = 12.sp, weight = FontWeight.Bold)
                ArabicText(a.letters, size = 16.sp, color = Ink, align = TextAlign.Start)
            }
        }
        UrduText("آواز حلق سے شروع ہو کر ہونٹوں تک آتی ہے — اسی ترتیب سے مخارج ہیں۔ ہر حرف پر ٹیپ کر کے اس کا اپنا مخرج پڑھیں۔",
            size = 11.sp, color = Quiet)
    }
}
