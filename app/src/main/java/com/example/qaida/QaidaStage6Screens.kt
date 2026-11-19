package com.example.qaida

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

/** Router for the Stage 6 boards: word-by-word surahs and flashcards. */
@Composable
internal fun Stage6Lesson(page: String, play: (String, String) -> Unit, onDone: () -> Unit) {
    if (page == "s6_cards") { Flashcards(play, onDone); return }
    LazyColumn(
        Modifier.fillMaxSize().testTag("qaida_${page}_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        when (page) {
            "s6_fil" -> { wordByWord(play, QaidaStage6.fil); wordByWord(play, QaidaStage6.quraysh) }
            "s6_maun" -> wordByWord(play, QaidaStage6.maun)
            "s6_kafirun" -> { wordByWord(play, QaidaStage6.kafirun); wordByWord(play, QaidaStage6.nasr) }
            "s6_masad" -> wordByWord(play, QaidaStage6.masad)
        }
        item {
            UrduText("یہ معانی بچوں کے لیے آسان اردو میں لکھے گئے ہیں؛ کسی مستند ترجمے اور استاد سے ملا کر پڑھیں۔", size = 11.sp, color = Quiet)
        }
        item {
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("qaida_s6_done"),
                colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                UrduText("میں نے یہ سبق پڑھ لیا ✓", size = 15.sp, color = Color.White, weight = FontWeight.Bold)
            }
        }
    }
}

private const val CARDS_PER_ROUND = 15

/** "Say the meaning": see the word, say its meaning aloud, flip, then mark yourself honestly. */
@Composable
private fun Flashcards(play: (String, String) -> Unit, onDone: () -> Unit) {
    var seed by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    val deck = remember(seed) { QaidaStage6.flashcards.shuffled(Random(seed)).take(CARDS_PER_ROUND) }
    var index by rememberSaveable(seed) { mutableIntStateOf(0) }
    var known by rememberSaveable(seed) { mutableIntStateOf(0) }
    var flipped by rememberSaveable(seed, index) { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        if (index >= deck.size) {
            Spacer(Modifier.height(24.dp))
            UrduText(if (known >= deck.size * 2 / 3) "ماشاء اللہ! 🌟" else "شاباش! روز تھوڑی مشق کریں 💪", size = 24.sp,
                weight = FontWeight.Bold, align = TextAlign.Center)
            UrduText(tx("$known / ${deck.size} معنی یاد تھے", "$known / ${deck.size} meanings remembered"), size = 20.sp, color = Green, align = TextAlign.Center,
                modifier = Modifier.testTag("qaida_cards_result"))
            Spacer(Modifier.height(16.dp))
            Button(onClick = { seed = System.currentTimeMillis() }, colors = ButtonDefaults.buttonColors(containerColor = Green),
                modifier = Modifier.testTag("qaida_cards_again")) { UrduText("نئے کارڈ", size = 15.sp, color = Color.White) }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onDone, colors = ButtonDefaults.buttonColors(containerColor = Green),
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("qaida_s6_done")) {
                UrduText("مشق مکمل ✓", size = 15.sp, color = Color.White, weight = FontWeight.Bold)
            }
            return@Column
        }
        val c = deck[index]
        UrduText(tx("کارڈ ${index + 1} / ${deck.size}   ·   یاد تھے: $known", "Card ${index + 1} / ${deck.size}   ·   remembered: $known"), size = 13.sp, color = Quiet)
        UrduText("لفظ دیکھ کر اس کا مطلب زبان سے بولیں، پھر کارڈ پلٹیں۔", size = 13.sp, color = Quiet, align = TextAlign.Center)
        Spacer(Modifier.height(12.dp))
        Card(
            Modifier.fillMaxWidth().heightIn(min = 220.dp).clickable { flipped = !flipped; if (flipped) play(c.audioKey, c.arabic) }
                .testTag("qaida_card"),
            colors = CardDefaults.cardColors(containerColor = if (flipped) GreenSoft else Color.White),
            border = BorderStroke(2.dp, Green), shape = RoundedCornerShape(24.dp),
        ) {
            Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                ArabicText(c.arabic, size = 52.sp)
                if (flipped) {
                    UrduText(c.urdu, size = 22.sp, color = Green, weight = FontWeight.Bold, align = TextAlign.Center,
                        modifier = Modifier.testTag("qaida_card_meaning"))
                } else {
                    UrduText("👆 پلٹنے کے لیے ٹیپ کریں", size = 13.sp, color = Quiet)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        if (flipped) {
            // Right to left: "I knew it" first (on the right), "not yet" second.
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { known++; index++ }, modifier = Modifier.weight(1f).heightIn(min = 52.dp).testTag("qaida_card_knew"),
                    colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                    UrduText("یاد تھا ✓", size = 15.sp, color = Color.White, weight = FontWeight.Bold)
                }
                OutlinedButton(onClick = { index++ }, modifier = Modifier.weight(1f).heightIn(min = 52.dp).testTag("qaida_card_notyet"),
                    border = BorderStroke(1.dp, Green)) {
                    UrduText("ابھی نہیں ↺", size = 15.sp, color = Green)
                }
            }
        } else {
            Box(Modifier.heightIn(min = 52.dp))
        }
    }
}
