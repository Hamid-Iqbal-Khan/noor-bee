package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalContext
import android.content.ClipboardManager
import android.content.ClipData
import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChildProfile
import com.example.data.LearningProgress
import com.example.data.ParentConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentPanelScreens(
    activeProfile: ChildProfile?,
    allProfiles: List<ChildProfile>,
    config: ParentConfig,
    progressHistory: List<LearningProgress>,
    activeUser: com.example.data.User? = null,
    allUsers: List<com.example.data.User> = emptyList(),
    onBackPressed: () -> Unit,
    onAddChildClick: () -> Unit,
    onSelectActiveProfile: (ChildProfile) -> Unit,
    onSpeechRateChange: (Float) -> Unit,
    onToggleRTL: (Boolean) -> Unit,
    onUpgradePremium: () -> Unit,
    onLogout: () -> Unit = {}
) {
    var activeSubTab by remember { mutableStateOf("dashboard") } // "dashboard", "reports", "settings", "billing", "admin"
 
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parent Control Center 🔐", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723)) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF3E2723)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFFDE7))
            )
        },
        containerColor = Color(0xFFFFFAEC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("parent_panel_root")
        ) {
            // Nested Tabs navigation navigation
            ScrollableTabRow(
                selectedTabIndex = when (activeSubTab) {
                    "dashboard" -> 0
                    "reports" -> 1
                    "settings" -> 2
                    "tutorials" -> 3
                    "billing" -> 4
                    "admin" -> 5
                    else -> 0
                },
                containerColor = Color(0xFFFFFDE7),
                contentColor = Color(0xFF3E2723),
                edgePadding = 16.dp
            ) {
                Tab(
                    selected = activeSubTab == "dashboard",
                    onClick = { activeSubTab = "dashboard" },
                    text = { Text("Dashboard", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeSubTab == "reports",
                    onClick = { activeSubTab = "reports" },
                    text = { Text("Analytics Report", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeSubTab == "settings",
                    onClick = { activeSubTab = "settings" },
                    text = { Text("App Preferences", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeSubTab == "tutorials",
                    onClick = { activeSubTab = "tutorials" },
                    text = { Text("App Tutorials", fontWeight = FontWeight.Bold) }
                )
                if (com.example.FeatureFlags.PREMIUM_ENABLED) {
                    Tab(
                        selected = activeSubTab == "billing",
                        onClick = { activeSubTab = "billing" },
                        text = { Text("Premium Tier", fontWeight = FontWeight.Bold) }
                    )
                }
                if (com.example.FeatureFlags.ADMIN_CONSOLE_ENABLED) {
                    Tab(
                        selected = activeSubTab == "admin",
                        onClick = { activeSubTab = "admin" },
                        text = { Text("Admin Console ⚙️", fontWeight = FontWeight.Bold) }
                    )
                }
            }
 
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp)
            ) {
                when (activeSubTab) {
                    "dashboard" -> DashboardSubTab(
                        activeProfile = activeProfile,
                        allProfiles = allProfiles,
                        isPremium = config.isPremium,
                        activeUser = activeUser,
                        onAddChild = onAddChildClick,
                        onSelect = onSelectActiveProfile,
                        onLogout = onLogout,
                        onGotoUpgrade = { activeSubTab = "billing" }
                    )
                    "reports" -> ReportsSubTab(
                        profile = activeProfile,
                        progressHistory = progressHistory
                    )
                    "settings" -> SettingsSubTab(
                        config = config,
                        onSpeechRateChange = onSpeechRateChange,
                        onToggleRTL = onToggleRTL
                    )
                    "tutorials" -> TutorialsSubTab()
                    "billing" -> BillingSubTab(
                        isPremium = config.isPremium,
                        onUpgrade = onUpgradePremium
                    )
                    "admin" -> AdminSubTab(
                        allUsers = allUsers
                    )
                }
            }
        }
    }
}
 
// --- Subtab 1: Main Management Dashboard --
@Composable
fun DashboardSubTab(
    activeProfile: ChildProfile?,
    allProfiles: List<ChildProfile>,
    isPremium: Boolean,
    activeUser: com.example.data.User?,
    onAddChild: () -> Unit,
    onSelect: (ChildProfile) -> Unit,
    onLogout: () -> Unit,
    onGotoUpgrade: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (activeUser != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFE0F2F1), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👤", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = activeUser.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF3E2723)
                                )
                                Text(
                                    text = activeUser.email,
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        
                        // Offline Mode Indicator (No Sign Out option needed for simplified offline mode)
                        Text(
                            text = "Offline Mode",
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
        }
        // Upgrade Notice notice (hidden until real Play Billing exists)
        if (com.example.FeatureFlags.PREMIUM_ENABLED) item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPremium) Color(0xFFC8E6C9) else Color(0xFFFFECB3)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .padding(8.dp)
                    ) {
                        Text(if (isPremium) "👑" else "🍯", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isPremium) "Membership Unlocked!" else "Get Noor Bee Premium",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color(0xFF3E2723)
                        )
                        Text(
                            text = if (isPremium) "Enjoy limitless spelling challenges and speech labs!" else "Unlock dual English/Arabic paths & priority rewards.",
                            fontSize = 11.sp,
                            color = Color(0xFF5D4037)
                        )
                    }
                    if (!isPremium) {
                        Button(
                            onClick = onGotoUpgrade,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E342E)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Upgrade", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Active profiles listing
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Manage Student Profiles",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = Color(0xFF3E2723)
                )
                Button(
                    onClick = { onAddChild() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("+ Add Kid", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        items(allProfiles) { prof ->
            val isActive = activeProfile?.id == prof.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(prof) }
                    .testTag("parent_profile_card_${prof.name}"),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(if (isActive) 3.dp else 1.dp, if (isActive) Color(0xFFFFB300) else Color(0xFFE0E0E0)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFFFFF9C4), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👶", fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = prof.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF3E2723)
                        )
                        Text(
                            text = "Age: ${prof.age} • ${prof.gender} • Stars: ${prof.stars} • Active Path: ${prof.currentLanguage}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    if (isActive) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE8F5E9), CircleShape)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Active", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    } else {
                        Text("➡️", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// --- Subtab 2: Comprehensive Learning Reports --
@Composable
fun ReportsSubTab(
    profile: ChildProfile?,
    progressHistory: List<LearningProgress>
) {
    if (profile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Please select or add an active profile setup.")
        }
        return
    }

    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
    ) {
        Text(
            text = "Analytics Report: ${profile.name}",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 17.sp,
            color = Color(0xFF3E2723)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Vector Canvas Bar Chart
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Weekly Activity Tracker",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val barsCount = 7
                        val barWidth = 40f
                        val maxBarVal = 100f
                        val spacing = (w - (barWidth * barsCount)) / (barsCount + 1)

                        // Draw Grid lines
                        drawLine(Color(0xFFEEEEEE), androidx.compose.ui.geometry.Offset(0f, h), androidx.compose.ui.geometry.Offset(w, h), strokeWidth = 3f)
                        drawLine(Color(0xFFEEEEEE), androidx.compose.ui.geometry.Offset(0f, h/2), androidx.compose.ui.geometry.Offset(w, h/2), strokeWidth = 2f)

                        // Render mock activities stars progress
                        val mockWeeklyStars = listOf(40f, 65f, 20f, 85f, 50f, 90f, 60f)
                        val daysLabels = listOf("M", "T", "W", "T", "F", "S", "S")

                        for (i in 0 until barsCount) {
                            val v = mockWeeklyStars[i]
                            val left = spacing + i * (barWidth + spacing)
                            val barHeight = (v / maxBarVal) * h
                            val top = h - barHeight

                            // Draw nice orange/amber bars representation
                            drawRoundRect(
                                color = Color(0xFFFFB300),
                                topLeft = androidx.compose.ui.geometry.Offset(left, top),
                                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                            )
                        }
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach {
                        Text(it, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // History logs listing
        Text(
            text = "Activity Log Record",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = Color(0xFF3E2723),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (progressHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No study log records yet. Completed matches or spelling quizzes will populate logs automatically!",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            progressHistory.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFF9C4), CircleShape)
                                .size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🍯", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                item.topic,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF3E2723)
                            )
                            Text(
                                "${item.activityType} • Completed on ${item.dateString}",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                        Text(
                            "+${item.score}⭐",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }
            }
        }
    }
}

// --- Subtab 3: Global preferences & Speed modifiers --
@Composable
fun SettingsSubTab(
    config: ParentConfig,
    onSpeechRateChange: (Float) -> Unit,
    onToggleRTL: (Boolean) -> Unit
) {
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Global Preferences",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = Color(0xFF3E2723)
        )

        // Preference 1: Phonetics Sound speed rate
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🗣️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Mascot Pronunciation Speed", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Speed scale: ${String.format("%.1fx", config.speechRate)} (Excellent for slow syllabic spelling training)",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Slider(
                    value = config.speechRate,
                    onValueChange = onSpeechRateChange,
                    valueRange = 0.5f..2.0f,
                    steps = 5,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFFFB300),
                        activeTrackColor = Color(0xFFFFB300)
                    ),
                    modifier = Modifier.testTag("speech_rate_slider")
                )
            }
        }

        // Preference 2: RTL manual mirror
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌐", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Arabic Layout Mirror (RTL)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Flipped alignments for Arabic-styled pathways", fontSize = 11.sp, color = Color.Gray)
                    }
                }

                Switch(
                    checked = config.arabicRTLMirror,
                    onCheckedChange = onToggleRTL,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFB300)),
                    modifier = Modifier.testTag("rtl_toggle")
                )
            }
        }

        Text(
            text = "Bilingual child systems require localized touch targets keeping interactive feedback responsive.",
            fontSize = 11.sp,
            color = Color.Gray,
            lineHeight = 15.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

// --- Subtab 4: Membership Plans Comparison --
@Composable
fun BillingSubTab(
    isPremium: Boolean,
    onUpgrade: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Noor Bee Premium Pass 🌟", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF3E2723))
        Spacer(modifier = Modifier.height(4.dp))
        Text("Get access to the full bilingual curriculum", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .shadow(4.dp, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(2.dp, Color(0xFFFFB300))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Golden Monthly Plan", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$2.99", fontWeight = FontWeight.Black, fontSize = 36.sp, color = Color(0xFFE65100))
                    Text(" / month", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 6.dp))
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                listOf(
                    "Dual paths (English & Arabic) complete chapters",
                    "Unlimited speech evaluation metrics",
                    "Custom exclusive sticker playground rewards",
                    "No marketing logs or popups"
                ).forEach { perk ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = "perk", tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(perk, fontSize = 11.sp, color = Color(0xFF424242))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (isPremium) {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFFC8E6C9))
                    ) {
                        Text("Active Premium Active", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onUpgrade,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("upgrade_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E342E))
                    ) {
                        Text("Join Golden Hub! 👑", color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TutorialsSubTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "How to Use Noor Bee 📚",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3E2723),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        TutorialCard(
            title = "1. Setup Your Child's Profile",
            icon = "👶",
            description = "Start by creating a profile for your child in the Dashboard tab. Enter their name, age, and select their gender. Each child gets a dedicated learning journey and progress tracking."
        )
        
        TutorialCard(
            title = "2. Select Language & Content",
            icon = "🌍",
            description = "On the Home screen, tap the language card to switch between English and Arabic. The app will dynamically adapt all learning games, letters, and phonics based on the chosen language."
        )
        
        TutorialCard(
            title = "3. Letter Playroom & Phonics",
            icon = "🧸",
            description = "Tap the Letter Games on the Home screen to explore alphabets, shapes, and basic sounds. As your child levels up, they'll advance to complex phonics, tracing, and sentence building."
        )
        
        TutorialCard(
            title = "4. Word Lab & Vocabulary",
            icon = "🧠",
            description = "The Word Lab section focuses on building vocabulary. From simple 'First Words' to advanced 'Spelling Sandbox', kids can match images to words and assemble spelling blocks."
        )
        
        TutorialCard(
            title = "5. Islamic Learning & Quran",
            icon = "🕌",
            description = "Use the daily Dua section for supplications and the Quran section for 15-line reading mode. The Quran feature provides an authentic reading experience with customizable fonts and themes."
        )
        
        TutorialCard(
            title = "6. Noor Bee Encouragement",
            icon = "🐝",
            description = "Tap the cheerful Noor Bee character for interactive, personalized voice encouragement. The bee uses safe AI to generate motivating, child-friendly praise related to their learning progress."
        )
        
        TutorialCard(
            title = "7. Sticker Room & Rewards",
            icon = "🍯",
            description = "Children earn Honey Jars as they complete tasks. They can visit the Sticker Room to unlock fun, colorful stickers using their collected honey, rewarding their continuous effort!"
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TutorialCard(title: String, icon: String, description: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = icon,
                fontSize = 28.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF5D4037),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// --- Subtab 6: Admin Dashboard Console ---
@Composable
fun AdminSubTab(
    allUsers: List<com.example.data.User>
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var showCsvDialog by remember { mutableStateOf(false) }
    var csvContentString by remember { mutableStateOf("") }

    val filteredUsers = remember(allUsers, searchQuery) {
        allUsers.filter { user ->
            user.fullName.contains(searchQuery, ignoreCase = true) ||
            user.email.contains(searchQuery, ignoreCase = true) ||
            user.phoneNumber.contains(searchQuery, ignoreCase = true)
        }
    }

    // Statistics metrics
    val totalRegistered = allUsers.size
    val totalDownloads = totalRegistered + 12 // Simulated downloads margin
    val conversionPercent = if (totalDownloads > 0) (totalRegistered.toFloat() / totalDownloads * 100).toInt() else 100

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stats Title
        item {
            Text(
                text = "Registration Statistics 📊",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723)
            )
        }

        // Stats Visual Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("Total Downloads", fontSize = 11.sp, color = Color(0xFF2E7D32))
                        Text("$totalDownloads", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Google Play Console", fontSize = 9.sp, color = Color(0xFF4CAF50))
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("Registered Users", fontSize = 11.sp, color = Color(0xFF1565C0))
                        Text("$totalRegistered", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Conversion: $conversionPercent%", fontSize = 9.sp, color = Color(0xFF1976D2))
                    }
                }
            }
        }

        // Search and Actions card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "User Directory & Actions",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by name, email or phone...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3E2723),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            if (allUsers.isEmpty()) {
                                Toast.makeText(context, "No user data available to export.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            // Compile real CSV string
                            val csv = StringBuilder()
                            csv.append("Full Name,Email Address,Phone Number,Registration Date,Device Info,App Version,Last Login\n")
                            allUsers.forEach { u ->
                                val formattedLogin = if (u.lastLoginTimestamp > 0L) {
                                    java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(u.lastLoginTimestamp))
                                } else "Never"
                                csv.append("\"${u.fullName}\",\"${u.email}\",\"${u.phoneNumber}\",\"${u.registrationDateTime}\",\"${u.deviceInfo}\",\"${u.appVersion}\",\"$formattedLogin\"\n")
                            }
                            csvContentString = csv.toString()

                            // Set Clipboard
                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("NoorBee Users Export", csvContentString)
                            clipboard.setPrimaryClip(clip)

                            Toast.makeText(context, "✅ CSV Copied to Clipboard!", Toast.LENGTH_LONG).show()
                            showCsvDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0061A4)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Export icon", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Directory Data (CSV)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section Title: Registered Users list
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Registered Parents Directory (${filteredUsers.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
            }
        }

        if (filteredUsers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No parents found matching query.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(filteredUsers) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFF1F1F1))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF9C4)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFF57F17))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = user.fullName,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3E2723),
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Last Login: ${
                                        if (user.lastLoginTimestamp > 0L) {
                                            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(user.lastLoginTimestamp))
                                        } else "Never"
                                    }",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Divider(color = Color(0xFFF5F5F5), modifier = Modifier.padding(bottom = 10.dp))

                        // User meta details
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text("Email: ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF5D4037))
                                Text(user.email, fontSize = 12.sp, color = Color.DarkGray)
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text("Phone: ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF5D4037))
                                Text(user.phoneNumber, fontSize = 12.sp, color = Color.DarkGray)
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text("Registered: ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF5D4037))
                                Text(user.registrationDateTime, fontSize = 12.sp, color = Color.DarkGray)
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text("Device: ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF5D4037))
                                Text(user.deviceInfo, fontSize = 12.sp, color = Color.DarkGray)
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text("App Version: ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF5D4037))
                                Text(user.appVersion, fontSize = 12.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
            }
        }
    }

    // --- CSV VISUAL DISPLAY DIALOG ---
    if (showCsvDialog) {
        AlertDialog(
            onDismissRequest = { showCsvDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    text = "CSV Export Success! 📄",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
            },
            text = {
                Column {
                    Text(
                        text = "The raw CSV data has been compiled and copied to your Android system clipboard. You can paste it directly into Excel, Google Sheets, or email.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = csvContentString,
                                fontSize = 10.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCsvDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0061A4))
                ) {
                    Text("Got it", color = Color.White)
                }
            }
        )
    }
}
