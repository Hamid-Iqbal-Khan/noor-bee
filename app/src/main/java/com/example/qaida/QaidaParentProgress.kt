package com.example.qaida

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChildProfile

private val Brown = Color(0xFF3E2723)
private val Muted = Color(0xFF6D6D6D)

/**
 * Parent Panel → "Qaida Progress": every child's place in the Urdu Qaida course.
 * Reads the same per-profile progress the lessons write; nothing is sent anywhere.
 */
@Composable
fun QaidaParentProgress(profiles: List<ChildProfile>) {
    val context = LocalContext.current
    if (profiles.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Add a child profile to see Qaida progress.") }
        return
    }
    LazyColumn(Modifier.fillMaxSize().testTag("parent_qaida_progress"), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Urdu Qaida progress", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = Brown)
            Text("Lessons done and best quiz score (out of 10, pass at 8) for each stage. Stages aren't locked; " +
                "children are advised to pass each quiz before moving on.", fontSize = 12.sp, color = Muted)
        }
        items(profiles, key = { it.id }) { child ->
            val progress = remember(child.id) { QaidaProgress(context, child.id) }
            val status = remember(child.id) { QaidaCourse.status(progress) }
            val next = remember(child.id) { QaidaCourse.next(progress) }
            val totalLessons = QaidaCourse.stages.sumOf { it.lessons.size + 1 }
            val done = status.sumOf { it.lessonsDone + if (it.passed) 1 else 0 }
            Card(Modifier.fillMaxWidth().testTag("parent_qaida_child_${child.id}"),
                colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE6DCC8))) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(child.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Brown, modifier = Modifier.weight(1f))
                        Text("$done / $totalLessons", fontWeight = FontWeight.Bold, color = Green)
                    }
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(progress = { done / totalLessons.toFloat() }, modifier = Modifier.fillMaxWidth(),
                        color = Green, trackColor = GreenSoft)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        if (next == null) "🎉 Whole course complete" else "Next: Stage ${next.first.number} — ${next.second}",
                        fontSize = 13.sp, color = Brown, modifier = Modifier.testTag("parent_qaida_next_${child.id}"))
                    Text("Letters seen in Stage 1: ${progress.seenLetters().size} / 29", fontSize = 12.sp, color = Muted)
                    Spacer(Modifier.height(10.dp))
                    status.forEach { s -> StageRow(s) }
                }
            }
        }
        item {
            Text("Progress is kept on this device only. Reinstalling the app clears it.", fontSize = 11.sp, color = Muted)
        }
    }
}

@Composable
private fun StageRow(s: QaidaCourse.StageStatus) {
    val color = when { s.passed -> Green; s.started -> AmberLine; else -> Color(0xFFBDBDBD) }
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("parent_qaida_stage_${s.stage.number}"),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(28.dp).background(color, CircleShape), contentAlignment = Alignment.Center) {
            Text(if (s.passed) "✓" else s.stage.number.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("Stage ${s.stage.number}: ${s.stage.english}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Brown)
            Text("Lessons ${s.lessonsDone}/${s.stage.lessons.size}  ·  " +
                (if (s.bestQuiz > 0) "best quiz ${s.bestQuiz}/10" else "quiz not tried") + if (s.passed) "  ·  passed" else "",
                fontSize = 11.sp, color = Muted)
        }
        UrduText(s.stage.urdu, size = 12.sp, color = color)
    }
}
