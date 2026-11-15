package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream
import coil.compose.rememberAsyncImagePainter
import com.example.data.ChildProfile
import com.example.ui.theme.*

fun saveUriToInternalStorage(context: Context, uri: Uri, fileName: String): String? {
    return try {
        val destDir = File(context.filesDir, "profile_pics")
        if (!destDir.exists()) {
            destDir.mkdirs()
        }
        val file = File(destDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun ProfileAvatarView(
    avatarUrl: String,
    modifier: Modifier = Modifier
) {
    val isCustomPhoto = avatarUrl.startsWith("content://") || avatarUrl.contains("/")
    
    Box(
        modifier = modifier
            .background(Color.White, CircleShape)
            .border(1.dp, BorderGrey, CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isCustomPhoto) {
            val painter = rememberAsyncImagePainter(model = avatarUrl)
            androidx.compose.foundation.Image(
                painter = painter,
                contentDescription = "User Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        } else {
            // Preset avatar (bee_default, bee_painter, etc.)
            val outfitId = when (avatarUrl) {
                "bee_painter" -> "painter_hat"
                "bee_chef" -> "chef_hat"
                "bee_space" -> "space_helmet"
                "bee_detective" -> "classic_bee"
                "bee_royal" -> "royal_crown"
                else -> "classic_bee"
            }
            CompositionLocalProvider(LocalNoorBeeStyle provides NoorBeeStyle(equippedOutfitId = outfitId)) {
                NoorBeeMascot(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardScreen(
    profile: ChildProfile,
    allProfiles: List<ChildProfile> = emptyList(),
    onSelectProfile: (ChildProfile) -> Unit = {},
    onUpdateProfile: (ChildProfile) -> Unit = {},
    onCreateProfile: (String, String, Int, Int, String) -> Unit = { _, _, _, _, _ -> },
    onDeleteProfile: (ChildProfile) -> Unit = {},
    onTopicSelected: (String) -> Unit,
    onNavigateToLanguagePicker: () -> Unit,
    onNavigateToStickerRoom: () -> Unit,
    onNavigateToParentPanel: () -> Unit,
    onNavigateToDua: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToQaida: (Int) -> Unit = {},      // Urdu path: Qaida stage 1 or 2
    onOpenArabicTopic: (String) -> Unit = {},   // Arabic section: "Alphabet" or "Vocabulary"
    onLevelChanged: (Int) -> Unit = {}
) {
    var showParentGate by remember { mutableStateOf(false) }
    var showAgeSelectorDialog by remember { mutableStateOf(false) }
    var showProfileManager by remember { mutableStateOf(false) }
    var showTutorialDialog by remember { mutableStateOf(false) }
    var pinValue by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("NoorBeePrefs", android.content.Context.MODE_PRIVATE) }
    
    LaunchedEffect(Unit) {
        val hasSeenTutorial = prefs.getBoolean("has_seen_tutorial_v1", false)
        if (!hasSeenTutorial) {
            showTutorialDialog = true
            prefs.edit().putBoolean("has_seen_tutorial_v1", true).apply()
        }
    }

    // Random math task for simple child gate addition protection
    val gateTask = remember { Pair((3..8).random(), (2..7).random()) }
    val gateAns = gateTask.first + gateTask.second
    var gateInput by remember { mutableStateOf("") }
    var gateError by remember { mutableStateOf(false) }

    val isArabicPath = profile.currentLanguage == "Arabic"
    val level = profile.level

    val letterPathsTitle = when (level) {
        1 -> if (isArabicPath) "غرفة الحروف 🧸" else "Letter Playroom 🧸"
        2 -> if (isArabicPath) "مكتشف الحروف 🧭" else "Letter Explorer 🧭"
        3 -> if (isArabicPath) "مغامرة الصوتيات 🏰" else "Phonics Quest 🏰"
        4 -> if (isArabicPath) "بطل الصوتيات 🏆" else "Phonics Champ 🏆"
        5 -> if (isArabicPath) "حديقة الجمل 🌻" else "Sentence Garden 🌻"
        else -> if (isArabicPath) "عبقري الحروف 🎓" else "Word Genius 🎓"
    }

    val letterPathsSubtitle = when (level) {
        1 -> if (isArabicPath) "تعرف على الحروف الأولى والملونة" else "Meet basic phonics & letters"
        2 -> if (isArabicPath) "اكتشف أشكال الحروف وطرق كتابتها" else "Discover letters & tracing"
        3 -> if (isArabicPath) "أتقن أصوات الحروف وحركاتها بدقة" else "Master letter sounds & shapes"
        4 -> if (isArabicPath) "تعلم تراكيب الحروف المتقدمة" else "Learn phonetic combinations"
        5 -> if (isArabicPath) "تعلم تراكيب الجمل وصياغة العبارات" else "Explore sentence structures"
        else -> if (isArabicPath) "حل ألغاز وتحديات الحروف الفائقة" else "Solve advanced letter riddles"
    }

    val letterPathsEmoji = when (level) {
        1 -> "🧸"
        2 -> "🧭"
        3 -> "🏰"
        4 -> "🏆"
        5 -> "🌻"
        else -> "🎓"
    }

    val wordLabTitle = when (level) {
        1 -> if (isArabicPath) "كلماتي الأولى 🍼" else "First Words 🍼"
        2 -> if (isArabicPath) "سفاري الكلمات 🦁" else "Word Safari 🦁"
        3 -> if (isArabicPath) "باني الكلمات 🛠️" else "Word Builder 🛠️"
        4 -> if (isArabicPath) "مختبر التهجئة 🐝" else "Spelling Sandbox 🐝"
        5 -> if (isArabicPath) "رابط الكلمات 🔗" else "Sentence Connect 🔗"
        else -> if (isArabicPath) "مختبر الخبراء 🧠" else "Expert Word Lab 🧠"
    }

    val wordLabSubtitle = when (level) {
        1 -> if (isArabicPath) "تعلم الكلمات الأولى السهلة للغاية" else "Learn your very first words"
        2 -> if (isArabicPath) "طابق الكلمات والصور الممتعة" else "Match fun intermediate words"
        3 -> if (isArabicPath) "ركب الكلمات الملونة والممتعة" else "Build words & spelling blocks"
        4 -> if (isArabicPath) "تهجأ الكلمات المتقدمة حرفًا بحرف" else "Assemble spelling block puzzles"
        5 -> if (isArabicPath) "صل الكلمات المترابطة لبناء جمل" else "Connect words into phrases"
        else -> if (isArabicPath) "تعلم وأتقن الكلمات الصعبة والشيقة" else "Master advanced vocabulary"
    }

    val wordLabEmoji = when (level) {
        1 -> "🍼"
        2 -> "🦁"
        3 -> "🛠️"
        4 -> "🐝"
        5 -> "🔗"
        else -> "🧠"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    // Profile Banner Clickable
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { showProfileManager = true }
                            .testTag("avatar_profile_badge")
                    ) {
                        Box(
                            modifier = Modifier
                                .requiredSize(45.dp)
                        ) {
                            ProfileAvatarView(avatarUrl = profile.avatar, modifier = Modifier.fillMaxSize())
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Hello, ${profile.name}! 👋",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavy,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Age ${profile.age} • ${profile.gender}",
                                fontSize = 12.sp,
                                color = SlateGrey,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    TextButton(
                        onClick = { showTutorialDialog = true },
                        modifier = Modifier.testTag("app_tutorial_button")
                    ) {
                        Text(
                            text = "Guide",
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MinimalWhite)
            )
        },
        containerColor = MinimalWhite
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("home_dashboard_screen"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interactive Profile Stats Bar (🎂 Age, ⭐ Stars, 🍯 Honey)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Level select/change pill
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showAgeSelectorDialog = true }
                            .testTag("level_selector_badge"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LightBlueGrey),
                        border = BorderStroke(1.dp, MinimalBlue)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("🌟", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Level ${profile.level}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MinimalBlue,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Stars Card
                    Card(
                        modifier = Modifier
                            .weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BadgeGrey),
                        border = BorderStroke(1.dp, BorderGrey)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("⭐", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${profile.stars}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = DeepNavy,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Gold Honey/Coins
                    Card(
                        modifier = Modifier
                            .weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BadgeGrey),
                        border = BorderStroke(1.dp, BorderGrey)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("🍯", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${profile.coins}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = DeepNavy,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                // Active Pathway Banner Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pathway_banner"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = LightBlueGrey),
                    border = BorderStroke(1.dp, BorderGrey)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Learning Language Path 🌍",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = DeepNavy
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Choose your path! Switch anytime:",
                            fontSize = 11.sp,
                            color = SlateGrey
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val isEnglish = profile.currentLanguage == "English"
                            
                            // Left Option: English
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(55.dp)
                                    .clickable {
                                        if (!isEnglish) {
                                            onUpdateProfile(profile.copy(currentLanguage = "English"))
                                        }
                                    }
                                    .testTag("lang_en_button"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isEnglish) Color.White else Color(0x1AFFFFFF)
                                ),
                                border = BorderStroke(
                                    width = if (isEnglish) 2.dp else 1.dp,
                                    color = if (isEnglish) MinimalBlue else BorderGrey
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(text = "🇬🇧", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(horizontalAlignment = Alignment.Start) {
                                        Text(
                                            text = "English",
                                            fontWeight = if (isEnglish) FontWeight.ExtraBold else FontWeight.Normal,
                                            fontSize = 13.sp,
                                            color = if (isEnglish) DeepNavy else SlateGrey
                                        )
                                        if (isEnglish) {
                                            Text(
                                                text = "Active ✨",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                color = MinimalBlue
                                            )
                                        }
                                    }
                                }
                            }
                            
                            // Right Option: Urdu (Urdu-medium Noorani Qaida path)
                            val isArabic = profile.currentLanguage == "Urdu"
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(55.dp)
                                    .clickable {
                                        if (!isArabic) {
                                            onUpdateProfile(profile.copy(currentLanguage = "Urdu"))
                                        }
                                    }
                                    .testTag("lang_ur_button"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isArabic) Color.White else Color(0x1AFFFFFF)
                                ),
                                border = BorderStroke(
                                    width = if (isArabic) 2.dp else 1.dp,
                                    color = if (isArabic) MinimalBlue else BorderGrey
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(text = "🇵🇰", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(horizontalAlignment = Alignment.Start) {
                                        Text(
                                            text = "اردو",
                                            fontWeight = if (isArabic) FontWeight.ExtraBold else FontWeight.Normal,
                                            fontSize = 13.sp,
                                            color = if (isArabic) DeepNavy else SlateGrey
                                        )
                                        if (isArabic) {
                                            Text(
                                                text = "فعال ✨",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                color = MinimalBlue
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Title
                Text(
                    text = "Pick A Learning Game! 🐝",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Honeycomb list using standard Rows for perfect scrollability with outer scroll
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Row 1 — the active learning path
                    if (profile.currentLanguage == "Urdu") {
                        // Right-to-left like the Qaida: first card on the right
                        androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f).testTag("dashboard_qaida_stage1")) {
                                DashboardHoneycomb(
                                    title = "مرحلہ ۱: حروف 🔤",
                                    subtitle = "Qaida letters & harakat",
                                    color = Color(0xFFE8F5E9),
                                    borderStroke = Color(0xFF1B5E20),
                                    emoji = "📖",
                                    onClick = { onNavigateToQaida(1) }
                                )
                            }
                            Box(modifier = Modifier.weight(1f).testTag("dashboard_qaida_stage2")) {
                                DashboardHoneycomb(
                                    title = "مرحلہ ۲: جوڑ 🔗",
                                    subtitle = "Joining, tanween, madd",
                                    color = Color(0xFFFFF4D6),
                                    borderStroke = Color(0xFFE0A800),
                                    emoji = "📗",
                                    onClick = { onNavigateToQaida(2) }
                                )
                            }
                        }
                        }
                        androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f).testTag("dashboard_qaida_stage3")) {
                                DashboardHoneycomb(
                                    title = "مرحلہ ۳: تلاوت 📖",
                                    subtitle = "Tashdeed, Al-Fatiha",
                                    color = Color(0xFFE8EAF6),
                                    borderStroke = Color(0xFF283593),
                                    emoji = "🌙",
                                    onClick = { onNavigateToQaida(3) }
                                )
                            }
                            Box(modifier = Modifier.weight(1f).testTag("dashboard_qaida_stage4")) {
                                DashboardHoneycomb(
                                    title = "مرحلہ ۴: وقف ✋",
                                    subtitle = "Stopping, short surahs",
                                    color = Color(0xFFE0F2F1),
                                    borderStroke = Color(0xFF00695C),
                                    emoji = "🕋",
                                    onClick = { onNavigateToQaida(4) }
                                )
                            }
                        }
                        }
                    } else {
                    // Row 1
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                DashboardHoneycomb(
                                    title = letterPathsTitle,
                                    subtitle = letterPathsSubtitle,
                                    color = Color.White,
                                    borderStroke = BorderGrey,
                                    emoji = letterPathsEmoji,
                                    onClick = { onTopicSelected("Alphabet") }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                DashboardHoneycomb(
                                    title = wordLabTitle,
                                    subtitle = wordLabSubtitle,
                                    color = LightBlueGrey,
                                    borderStroke = BorderGrey,
                                    emoji = wordLabEmoji,
                                    onClick = { onTopicSelected("Vocabulary") }
                                )
                            }
                        }
    
                    }

                    // Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            DashboardHoneycomb(
                                title = "Phonics Whiteboard 🎨",
                                subtitle = "Trace letters & hear sounds",
                                color = Color.White,
                                borderStroke = BorderGrey,
                                emoji = "🎨",
                                onClick = onNavigateToStickerRoom
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DashboardHoneycomb(
                                title = "Daily Duas 🌙",
                                subtitle = if (profile.level == 1) "🧸 Level 1 Match!" else "Sweet supplications",
                                color = if (profile.level == 1) Color(0xFFFFFDEC) else Color.White,
                                borderStroke = if (profile.level == 1) Color(0xFFFFB300) else BorderGrey,
                                emoji = "🌙",
                                onClick = onNavigateToDua
                            )
                        }
                    }

                    // Arabic section: Arabic letters, phonics and words (moved down from the top switch)
                    Text(
                        text = "العربية · Arabic Words & Phonics 🇸🇦",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepNavy,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    // Right-to-left like the Qaida: first card on the right
                    androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
                    Row(
                        modifier = Modifier.fillMaxWidth().testTag("dashboard_arabic_section"),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f).testTag("dashboard_arabic_letters")) {
                            DashboardHoneycomb(
                                title = "الحروف والأصوات 🔤",
                                subtitle = "Arabic letters & phonics",
                                color = Color(0xFFE3F2FD),
                                borderStroke = Color(0xFF1565C0),
                                emoji = "🇸🇦",
                                onClick = { onOpenArabicTopic("Alphabet") }
                            )
                        }
                        Box(modifier = Modifier.weight(1f).testTag("dashboard_arabic_words")) {
                            DashboardHoneycomb(
                                title = "الكلمات 🍎",
                                subtitle = "Arabic word games",
                                color = Color(0xFFFCE4EC),
                                borderStroke = Color(0xFFAD1457),
                                emoji = "🍎",
                                onClick = { onOpenArabicTopic("Vocabulary") }
                            )
                        }
                    }
                    }

                    // Row 3 (Quran Explorer)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        DashboardHoneycomb(
                            title = "Quran Explorer 🕌",
                            subtitle = "Interactive API Text & Recit",
                            color = Color.White,
                            borderStroke = BorderGrey,
                            emoji = "🕌",
                            onClick = onNavigateToQuran
                        )
                    }
                }
            }

            if (showTutorialDialog) {
                AlertDialog(
                    onDismissRequest = { showTutorialDialog = false },
                    containerColor = Color.White,
                    title = {
                        Text(
                            text = "App Guide & Tutorials 📚",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723)
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                "Welcome to Noor Bee! Here's a quick guide to navigating the app:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            val tutorials = listOf(
                                "Profile 👤" to "Tap the avatar at the top left to switch kids or view progress.",
                                "Language 🌍" to "Use the Language card to toggle between English and Arabic mode.",
                                "Playroom 🧸" to "Dive into letter games, tracing, and phonics.",
                                "Word Lab 🧠" to "Expand vocabulary with interactive word builder challenges.",
                                "Daily Duas 🌙" to "Learn essential daily supplications with audio.",
                                "Quran 🕌" to "Explore the 15-line Quran interface.",
                                "Noor Bee 🐝" to "Tap the Bee character for interactive voice encouragement!",
                                "Settings ⚙️" to "Parents can access settings at the bottom to configure speech and premium features."
                            )
                            
                            tutorials.forEach { (title, desc) ->
                                Row(
                                    modifier = Modifier.padding(bottom = 12.dp)
                                ) {
                                    Text(
                                        text = "• ",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFB300)
                                    )
                                    Column {
                                        Text(
                                            text = title,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF3E2723)
                                        )
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showTutorialDialog = false }) {
                            Text("Got it!", color = DeepNavy, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            // Math Security Bypass Dialog / Gate (Minimal Grey Dialog Design)
            if (showParentGate) {
                AlertDialog(
                    onDismissRequest = { showParentGate = false },
                    title = {
                        Text(
                            text = "Parent Lock Verification 🔒",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp
                        )
                    },
                    text = {
                        Column {
                            Text(
                                "Safety verification question for profile managers and parents.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            // Math equation challenge (Minimalist grey card)
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Text(
                                    text = "Solve: ${gateTask.first} + ${gateTask.second} = ?",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = gateInput,
                                onValueChange = {
                                    gateInput = it
                                    gateError = false
                                },
                                label = { Text("Enter Answer") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("parent_gate_input"),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                isError = gateError
                            )

                            if (gateError) {
                                Text(
                                    text = "Oops, incorrect! Try again, parent.",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (gateInput.trim() == gateAns.toString()) {
                                    showParentGate = false
                                    gateInput = ""
                                    onNavigateToParentPanel()
                                } else {
                                    gateError = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MinimalBlue),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("Verify", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showParentGate = false }) {
                            Text("Cancel", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                        }
                    }
                )
            }

            if (showAgeSelectorDialog) {
                AlertDialog(
                    onDismissRequest = { showAgeSelectorDialog = false },
                    title = {
                        Text(
                            text = "Select Level 🌟",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp
                        )
                    },
                    text = {
                        Column {
                            Text(
                                "Choose a level to customize learning activities:",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                (1..3).forEach { selectableLevel ->
                                    val isSelected = profile.level == selectableLevel
                                    Box(
                                        modifier = Modifier
                                            .size(45.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) LightBlueGrey else BadgeGrey
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) MinimalBlue else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                onLevelChanged(selectableLevel)
                                                showAgeSelectorDialog = false
                                            }
                                            .weight(1f),
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

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                (4..6).forEach { selectableLevel ->
                                    val isSelected = profile.level == selectableLevel
                                    Box(
                                        modifier = Modifier
                                            .size(45.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) LightBlueGrey else BadgeGrey
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) MinimalBlue else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                onLevelChanged(selectableLevel)
                                                showAgeSelectorDialog = false
                                            }
                                            .weight(1f),
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
                    },
                    confirmButton = {
                        TextButton(onClick = { showAgeSelectorDialog = false }) {
                            Text("Cancel", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            if (showProfileManager) {
                var isEditingActive by remember { mutableStateOf(false) }
                var editName by remember { mutableStateOf(profile.name) }
                var editGender by remember { mutableStateOf(profile.gender) }
                var editAge by remember { mutableStateOf(profile.age) }
                var editLevel by remember { mutableStateOf(profile.level) }
                var editAvatar by remember { mutableStateOf(profile.avatar) }
                var showAddSiblingPanel by remember { mutableStateOf(false) }
                
                var newSiblingName by remember { mutableStateOf("") }
                var newSiblingGender by remember { mutableStateOf("Male") }
                var newSiblingAge by remember { mutableStateOf(5) }
                var newSiblingLevel by remember { mutableStateOf(1) }
                var newSiblingAvatar by remember { mutableStateOf("bee_default") }

                val context = LocalContext.current
                val photoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    uri?.let {
                        val profileIdToken = if (showAddSiblingPanel) "sibling_${System.currentTimeMillis()}" else "active_${profile.id}"
                        val persistentPath = saveUriToInternalStorage(context, it, "avatar_$profileIdToken.jpg")
                        persistentPath?.let { path ->
                            if (showAddSiblingPanel) {
                                newSiblingAvatar = path
                            } else {
                                editAvatar = path
                                isEditingActive = true
                            }
                        }
                    }
                }

                AlertDialog(
                    onDismissRequest = { showProfileManager = false },
                    modifier = Modifier.testTag("profile_manager_dialog"),
                    title = {
                        Text(
                            text = if (showAddSiblingPanel) "New Sibling Profile 🐝" else "Manage Profiles ✨",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    text = {
                        androidx.compose.foundation.lazy.LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                        ) {
                            if (showAddSiblingPanel) {
                                // Sub-module to ADD New Profile
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .requiredSize(100.dp)
                                                .clickable { photoPickerLauncher.launch("image/*") }
                                                .testTag("sibling_avatar_upload"),
                                            contentAlignment = Alignment.BottomEnd
                                        ) {
                                            ProfileAvatarView(avatarUrl = newSiblingAvatar, modifier = Modifier.fillMaxSize())
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .background(MinimalBlue, CircleShape)
                                                    .border(1.dp, Color.White, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("➕", fontSize = 12.sp)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Tap to upload a custom profile picture!",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))

                                        OutlinedTextField(
                                            value = newSiblingName,
                                            onValueChange = { newSiblingName = it },
                                            label = { Text("Sibling Name") },
                                            placeholder = { Text("e.g. Karim, Sarah") },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth().testTag("add_sibling_name_input"),
                                            shape = RoundedCornerShape(12.dp)
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Select Gender: $newSiblingGender",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.align(Alignment.Start)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            listOf("Male", "Female").forEach { genderOption ->
                                                val isSelect = newSiblingGender == genderOption
                                                Card(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(40.dp)
                                                        .clickable { newSiblingGender = genderOption },
                                                    shape = RoundedCornerShape(8.dp),
                                                    border = BorderStroke(
                                                        width = if (isSelect) 2.dp else 1.dp,
                                                        color = if (isSelect) MinimalBlue else BorderGrey
                                                    ),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = if (isSelect) LightBlueGrey else Color.White
                                                    )
                                                ) {
                                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                        Text(if (genderOption == "Male") "Male 👦" else "Female 👧", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Select Age: $newSiblingAge Years Old 🎂",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.align(Alignment.Start)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            (3..10).forEach { selectableAge ->
                                                val isSel = newSiblingAge == selectableAge
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .background(if (isSel) LightBlueGrey else BadgeGrey, CircleShape)
                                                        .border(1.dp, if (isSel) MinimalBlue else Color.Transparent, CircleShape)
                                                        .clickable { newSiblingAge = selectableAge },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "$selectableAge",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = if (isSel) DeepNavy else SlateGrey
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Select Level: Level $newSiblingLevel",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.align(Alignment.Start)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            (1..6).forEach { selectableLevel ->
                                                val isSel = newSiblingLevel == selectableLevel
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .background(if (isSel) LightBlueGrey else BadgeGrey, CircleShape)
                                                        .border(1.dp, if (isSel) MinimalBlue else Color.Transparent, CircleShape)
                                                        .clickable { newSiblingLevel = selectableLevel },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "$selectableLevel",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = if (isSel) DeepNavy else SlateGrey
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                // EDIT ACTIVE PROFILE & LIST OTHER PROFILES
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .requiredSize(100.dp)
                                                .clickable { photoPickerLauncher.launch("image/*") }
                                                .testTag("avatar_upload_trigger"),
                                            contentAlignment = Alignment.BottomEnd
                                        ) {
                                            ProfileAvatarView(avatarUrl = editAvatar, modifier = Modifier.fillMaxSize())
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .background(MinimalBlue, CircleShape)
                                                    .border(1.dp, Color.White, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("📸", fontSize = 12.sp)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Tap photo above to upload a custom profile picture!",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))

                                        OutlinedTextField(
                                            value = editName,
                                            onValueChange = {
                                                editName = it
                                                isEditingActive = true
                                            },
                                            label = { Text("Name") },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth().testTag("edit_profile_name"),
                                            shape = RoundedCornerShape(12.dp)
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Select Gender: $editGender",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.align(Alignment.Start)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            listOf("Male", "Female").forEach { genderOption ->
                                                val isSelect = editGender == genderOption
                                                Card(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(40.dp)
                                                        .clickable {
                                                            editGender = genderOption
                                                            isEditingActive = true
                                                        },
                                                    shape = RoundedCornerShape(8.dp),
                                                    border = BorderStroke(
                                                        width = if (isSelect) 2.dp else 1.dp,
                                                        color = if (isSelect) MinimalBlue else BorderGrey
                                                    ),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = if (isSelect) LightBlueGrey else Color.White
                                                    )
                                                ) {
                                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                        Text(if (genderOption == "Male") "Male 👦" else "Female 👧", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Select Age: $editAge Years Old 🎂",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.align(Alignment.Start)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            (3..10).forEach { selectableAge ->
                                                val isSel = editAge == selectableAge
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .background(if (isSel) LightBlueGrey else BadgeGrey, CircleShape)
                                                        .border(1.dp, if (isSel) MinimalBlue else Color.Transparent, CircleShape)
                                                        .clickable {
                                                            editAge = selectableAge
                                                            isEditingActive = true
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "$selectableAge",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = if (isSel) DeepNavy else SlateGrey
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Select Level: Level $editLevel",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.align(Alignment.Start)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            (1..6).forEach { selectableLevel ->
                                                val isSel = editLevel == selectableLevel
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .background(if (isSel) LightBlueGrey else BadgeGrey, CircleShape)
                                                        .border(1.dp, if (isSel) MinimalBlue else Color.Transparent, CircleShape)
                                                        .clickable {
                                                            editLevel = selectableLevel
                                                            isEditingActive = true
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "$selectableLevel",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = if (isSel) DeepNavy else SlateGrey
                                                    )
                                                }
                                            }
                                        }

                                        if (isEditingActive) {
                                            Spacer(modifier = Modifier.height(14.dp))
                                            Button(
                                                onClick = {
                                                    if (editName.isNotBlank()) {
                                                        onUpdateProfile(profile.copy(name = editName, gender = editGender, age = editAge, level = editLevel, avatar = editAvatar))
                                                        isEditingActive = false
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text("Save Active Profile Changes", color = Color(0xFF3E2723), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(24.dp))
                                    }
                                }

                                if (allProfiles.size > 1) {
                                    item {
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderGrey)
                                        Text(
                                            text = "Switch Sibling Profile 👥",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            allProfiles.forEach { otherProfile ->
                                                if (otherProfile.id != profile.id) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(BadgeGrey, RoundedCornerShape(12.dp))
                                                            .clickable {
                                                                onSelectProfile(otherProfile)
                                                                showProfileManager = false
                                                            }
                                                            .padding(10.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Box(modifier = Modifier.requiredSize(32.dp)) {
                                                                ProfileAvatarView(avatarUrl = otherProfile.avatar, modifier = Modifier.fillMaxSize())
                                                            }
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Column {
                                                                Text(otherProfile.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepNavy)
                                                                Text("Age: ${otherProfile.age} • ${otherProfile.gender}", fontSize = 10.sp, color = SlateGrey)
                                                            }
                                                        }
                                                        Text(
                                                            text = "Switch ➡️",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MinimalBlue
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (showAddSiblingPanel) {
                                Button(
                                    onClick = {
                                        if (newSiblingName.isNotBlank()) {
                                            onCreateProfile(newSiblingName, newSiblingGender, newSiblingAge, newSiblingLevel, newSiblingAvatar)
                                            showAddSiblingPanel = false
                                            newSiblingName = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MinimalBlue),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text("Add Sibling", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                TextButton(
                                    onClick = { showAddSiblingPanel = false },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Back", color = MaterialTheme.colorScheme.primary)
                                }
                            } else if (isEditingActive) {
                                Button(
                                    onClick = {
                                        if (editName.isNotBlank()) {
                                            onUpdateProfile(profile.copy(name = editName, gender = editGender, age = editAge, level = editLevel, avatar = editAvatar))
                                            isEditingActive = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                                    modifier = Modifier.weight(1.5f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text("Save Changes", color = Color(0xFF3E2723), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                TextButton(
                                    onClick = {
                                        editName = profile.name
                                        editGender = profile.gender
                                        editAge = profile.age
                                        editLevel = profile.level
                                        editAvatar = profile.avatar
                                        isEditingActive = false
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancel", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { showAddSiblingPanel = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MinimalBlue),
                                    modifier = Modifier.weight(1.5f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text("+ New Sibling", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                TextButton(
                                    onClick = { showProfileManager = false },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Close", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DashboardHoneycomb(
    title: String,
    subtitle: String,
    color: Color,
    borderStroke: Color,
    emoji: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .clickable { onClick() }
            .testTag("dashboard_honeycomb_${title.substringBefore(" ")}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        border = BorderStroke(1.dp, borderStroke)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, borderStroke, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 18.sp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DeepNavy,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    color = SlateGrey,
                    textAlign = TextAlign.Center,
                    lineHeight = 11.sp
                )
            }
        }
    }
}
