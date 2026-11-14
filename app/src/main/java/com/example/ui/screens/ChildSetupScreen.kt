package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildSetupScreen(
    currentName: String = "",
    currentGender: String = "Male",
    currentAge: Int = 5,
    currentLevel: Int = 1,
    currentAvatar: String = "bee_painter",
    onBackPressed: () -> Unit,
    onSaveProfile: (String, String, Int, Int, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var saving by remember { mutableStateOf(false) }
    var gender by remember { mutableStateOf(currentGender) }
    var age by remember { mutableStateOf(currentAge) }
    var level by remember { mutableStateOf(currentLevel) }
    var selectedAvatar by remember { mutableStateOf(currentAvatar) }

    val avatars = listOf(
        Pair("bee_default", "Classic 🐝"),
        Pair("bee_painter", "Painter 🎨"),
        Pair("bee_chef", "Chef 🍳"),
        Pair("bee_space", "Astronaut 🚀"),
        Pair("bee_detective", "Detective 🔍"),
        Pair("bee_royal", "Queen Bee 👑")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Up Profile", fontWeight = FontWeight.Bold, color = DeepNavy) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DeepNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MinimalWhite
                )
            )
        },
        containerColor = MinimalWhite
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(scrollState)
                .testTag("child_setup_screen"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Interactive Hero mascot preview (Minimal style circular badge)
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, BorderGrey, CircleShape)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Change expression/accessory styling based on selected avatar
                NoorBeeMascot(modifier = Modifier.fillMaxSize())
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Profile Input Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, BorderGrey)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Introduce Yourself!",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it.take(20) },
                        label = { Text("Child's Name") },
                        placeholder = { Text("e.g. Aly, Adam, Leyla") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepNavy,
                            unfocusedTextColor = DeepNavy,
                            focusedBorderColor = MinimalBlue,
                            unfocusedBorderColor = BorderGrey,
                            focusedLabelColor = MinimalBlue,
                            unfocusedLabelColor = SlateGrey,
                            cursorColor = MinimalBlue
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Gender Selection
                    Text(
                        text = "Select Gender: $gender",
                        fontWeight = FontWeight.Bold,
                        color = DeepNavy,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        listOf("Male", "Female").forEach { genderOption ->
                            val isSelected = gender == genderOption
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clickable { gender = genderOption }
                                    .testTag("gender_option_$genderOption"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MinimalBlue else BorderGrey
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) LightBlueGrey else Color.White
                                )
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (genderOption == "Male") "Male 👦" else "Female 👧",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) DeepNavy else SlateGrey,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Age selection
                    Text(
                        text = "Select Age: $age Years Old 🎂",
                        fontWeight = FontWeight.Bold,
                        color = DeepNavy,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        (3..10).forEach { selectableAge ->
                            val isSelected = age == selectableAge
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) LightBlueGrey else BadgeGrey
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) MinimalBlue else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { age = selectableAge }
                                    .testTag("age_option_$selectableAge"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$selectableAge",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) DeepNavy else SlateGrey,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Level selection
                    Text(
                        text = "Select Level: Level $level",
                        fontWeight = FontWeight.Bold,
                        color = DeepNavy,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        (1..6).forEach { selectableLevel ->
                            val isSelected = level == selectableLevel
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) LightBlueGrey else BadgeGrey
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) MinimalBlue else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { level = selectableLevel }
                                    .testTag("level_option_$selectableLevel"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$selectableLevel",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) DeepNavy else SlateGrey,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }



            Spacer(modifier = Modifier.height(24.dp))

            // CTA Submit button
            Button(
                onClick = {
                    if (saving) return@Button // ignore double taps: one profile per Save
                    saving = true
                    val finalName = name.trim().ifBlank { "Smart Kid" }
                    onSaveProfile(finalName, gender, age, level, selectedAvatar)
                },
                enabled = !saving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("save_profile_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MinimalBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Let's Go! 🎬",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
            }
        }
    }
}
