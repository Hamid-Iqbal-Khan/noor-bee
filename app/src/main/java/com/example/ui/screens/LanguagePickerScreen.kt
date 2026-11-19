package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerScreen(
    currentLang: String,
    onBackPressed: () -> Unit,
    onLangChosen: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Path", fontWeight = FontWeight.Bold, color = DeepNavy) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DeepNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MinimalWhite)
            )
        },
        containerColor = MinimalWhite
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .testTag("language_picker_screen"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Interactive help bubble from Noor Bee mascot
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(28.dp))
                    .border(BorderStroke(1.dp, BorderGrey), RoundedCornerShape(28.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(60.dp)) {
                    NoorBeeMascot(modifier = Modifier.fillMaxSize())
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Choose a path to explore, friend!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DeepNavy
                    )
                    Text(
                        text = "English pathway or Arabic pathway? Let's try both!",
                        fontSize = 11.sp,
                        color = SlateGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Two main options
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Option 1: English
                LanguageOptionCard(
                    title = "Learn English 🇬🇧",
                    subtitle = "Alphabet, Phonetics, and Pronunciation practice",
                    sampleText = "Hello! Let's learn A, B, C...",
                    isSelected = currentLang == "English",
                    color = if (currentLang == "English") LightBlueGrey else Color.White,
                    borderColor = MinimalBlue,
                    onClick = { onLangChosen("English") }
                )

                // Option 2: Arabic
                LanguageOptionCard(
                    title = "تعلم العربية 🇸🇦",
                    subtitle = "الأبجدية، التشكيل، وبناء المفردات",
                    sampleText = "أهلاً بك! لنتعلم أ، ب، ت...",
                    isSelected = currentLang == "Arabic",
                    color = if (currentLang == "Arabic") LightBlueGrey else Color.White,
                    borderColor = MinimalBlue,
                    onClick = { onLangChosen("Arabic") }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Each language includes custom illustrated cards, speech scoring practice, active hearing quizzes, and achievements!",
                color = SlateGrey,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun LanguageOptionCard(
    title: String,
    subtitle: String,
    sampleText: String,
    isSelected: Boolean,
    color: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("lang_card_${title.take(6).trim()}"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) borderColor else BorderGrey
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepNavy
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(DeepNavy, CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Active", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = SlateGrey
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = sampleText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateGrey,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
