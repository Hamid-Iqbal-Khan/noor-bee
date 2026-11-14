package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChildProfile
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import org.json.JSONObject
import org.json.JSONArray
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info


// Data Classes for Quran Path
data class QuranLetterItem(
    val letter: String,
    val name: String,
    val nameAr: String,
    val phonetic: String,
    val example: String,
    val exampleAr: String,
    val translation: String,
    val accentColor: Color,
    val cardColor: Color
)

data class QuranSurahItem(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val meaning: String,
    val totalVerses: Int,
    val verses: List<SurahVerse>,
    val color: Color,
    val accentColor: Color
)

data class SurahVerse(
    val number: Int,
    val textAr: String,
    val transliteration: String,
    val translation: String
)

data class TajweedRuleItem(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val exampleAr: String,
    val exampleEn: String,
    val pronunciationGuidance: String,
    val color: Color,
    val accentColor: Color,
    val soundText: String
)

data class QuranWordItem(
    val wordAr: String,
    val phonetic: String,
    val translation: String,
    val cardColor: Color,
    val accentColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    profile: ChildProfile,
    onBackPressed: () -> Unit,
    onEarnRewards: (Int, Int, String) -> Unit,
    onSoundTrigger: (String) -> Unit
) {
    CompleteQuranReaderLayout(
        onBackPressed = onBackPressed,
        onSoundTrigger = onSoundTrigger,
        onEarnRewards = onEarnRewards
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreenDeprecated_OldLanding_DeadCode_Ignore(
    profile: ChildProfile,
    onBackPressed: () -> Unit,
    onEarnRewards: (Int, Int, String) -> Unit,
    onSoundTrigger: (String) -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("NoorBeePrefs", Context.MODE_PRIVATE) }

    // Complete Quran active states (modes: null, "16line", "english")
    var activeQuranMode by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quran Explorer 📖",
                        color = DeepNavy,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                },
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
        if (activeQuranMode != null) {
            CompleteQuranReaderLayout(
                is16LineMode = (activeQuranMode == "16line"),
                onBackPressed = { activeQuranMode = null },
                onSoundTrigger = onSoundTrigger,
                onEarnRewards = onEarnRewards
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Banner
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = LightBlueGrey),
                        border = BorderStroke(1.dp, BorderGrey)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📖", fontSize = 36.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Welcome to Al-Quran Al-Kareem! ✨",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = DeepNavy
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Perfect your Arabic recitation, explore stunning verses, and obtain shiny Honey coins! 🐝",
                                    fontSize = 11.sp,
                                    color = SlateGrey
                                )
                            }
                        }
                    }

                    // Dynamic bookmarks
                    val b16SurahNum = sharedPrefs.getInt("bookmarked_16line_surah", -1).let { if (it == -1) null else it }
                    val b16VerseNum = sharedPrefs.getInt("bookmarked_16line_verse", -1).let { if (it == -1) null else it }
                    val bEngSurahNum = sharedPrefs.getInt("bookmarked_english_surah", -1).let { if (it == -1) null else it }
                    val bEngVerseNum = sharedPrefs.getInt("bookmarked_english_verse", -1).let { if (it == -1) null else it }
                    
                    val surahsCache = remember { getQuran114Surahs() }
                    val b16SurahName = if (b16SurahNum != null) surahsCache.find { it.number == b16SurahNum }?.nameEn else null
                    val bEngSurahName = if (bEngSurahNum != null) surahsCache.find { it.number == bEngSurahNum }?.nameEn else null

                    // ADD TWO OPTIONS: 15 Line Quran & English Translation Quran
                    Text(
                        text = "Arabic Quran Recitation & English Translation 👇",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DeepNavy,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp).align(Alignment.Start)
                    )

                    // Option 1: Hafizi 15-Line Quran (Arabic Only)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                activeQuranMode = "16line"
                            }
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBE7)), // Creamy green feel
                        border = BorderStroke(1.5.dp, Color(0xFF558B2F)) // Beautiful Leafy Green
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFDCEDC8), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🕌", fontSize = 22.sp)
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Quran (Arabic Only) • المصحف الشريف",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF33691E)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Read purely in Arabic. Styled in the beautiful, classic standard 15-line print structure for easy recitation & memorization.",
                                        fontSize = 10.sp,
                                        color = Color(0xFF558B2F),
                                        lineHeight = 13.sp
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Open Quran (Arabic Only)",
                                    tint = Color(0xFF33691E),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            if (b16SurahNum != null && b16SurahName != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFFDCEDC8).copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("📍", fontSize = 12.sp)
                                        Text(
                                            text = "Saved Location: Surah $b16SurahName, Verse $b16VerseNum",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF33691E)
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            text = "Tap cover to resume",
                                            fontSize = 8.sp,
                                            color = Color(0xFF33691E).copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Option 2: Quran with International English Translation
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                activeQuranMode = "english"
                            }
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)), // Calming sky blue/navy feel
                        border = BorderStroke(1.5.dp, Color(0xFF1565C0)) // Strong premium blue
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFBBDEFB), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🌍", fontSize = 22.sp)
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Quran (with English Translation) • بالترجمة",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0D47A1)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Explore all 114 Surahs with complete sentence-by-sentence International translations, meanings, and audio recitation.",
                                        fontSize = 10.sp,
                                        color = Color(0xFF1565C0),
                                        lineHeight = 13.sp
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Open Translated Quran",
                                    tint = Color(0xFF0D47A1),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            if (bEngSurahNum != null && bEngSurahName != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFFBBDEFB).copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("📍", fontSize = 12.sp)
                                        Text(
                                            text = "Saved Location: Surah $bEngSurahName, Verse $bEngVerseNum",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0D47A1)
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            text = "Tap cover to resume",
                                            fontSize = 8.sp,
                                            color = Color(0xFF0D47A1).copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreenDeprecated(
    profile: ChildProfile,
    onBackPressed: () -> Unit,
    onEarnRewards: (Int, Int, String) -> Unit,
    onSoundTrigger: (String) -> Unit
) {
    // Dynamically set starting age filter relative to current kid profile level
    val lessonAge = (profile.level + 2).coerceIn(3, 8)
    val startingAge = if (lessonAge in listOf(6, 7, 8)) lessonAge else 6
    var selectedAgeFilter by remember { mutableStateOf(startingAge) }
    
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("NoorBeePrefs", Context.MODE_PRIVATE) }

    // Complete Quran active states (modes: null, "16line", "english")
    var activeQuranMode by remember { mutableStateOf<String?>(null) }
    
    // States for Age 6 (Letters)
    var selectedLetter by remember { mutableStateOf<QuranLetterItem?>(null) }
    var masteredLetters by remember { mutableStateOf(setOf<String>()) }
    
    // States for Age 7 (Surahs)
    var expandedSurahId by remember { mutableStateOf<String?>(null) }
    var completedVerses by remember { mutableStateOf(setOf<String>()) } // Format: "surahId_verseNumber"
    var completedSurahs by remember { mutableStateOf(setOf<String>()) }
    
    // States for Age 8 (Tajweed & Words)
    var selectedTajweedRule by remember { mutableStateOf<TajweedRuleItem?>(null) }
    var completedTajweedRules by remember { mutableStateOf(setOf<String>()) }
    var completedVocabularyWords by remember { mutableStateOf(setOf<String>()) }
    
    // Core data lists
    val quranLettersList = remember {
        listOf(
            QuranLetterItem("أ", "Alif", "أَلِف", "a / i / u", "Allah (الله)", "الله", "Allah/God", Color(0xFF4CAF50), Color(0xFFE8F5E9)),
            QuranLetterItem("ب", "Baa", "بَاء", "b", "Bayt (House)", "بَيْت", "House", Color(0xFF2196F3), Color(0xFFE3F2FD)),
            QuranLetterItem("ت", "Taa", "تَاء", "t", "Tufaha (Apple)", "تُفَّاحَة", "Apple", Color(0xFFFF9800), Color(0xFFFFF3E0)),
            QuranLetterItem("ث", "Thaa", "ثَاء", "th", "Thamaar (Fruit)", "ثَمَار", "Fruit", Color(0xFFE91E63), Color(0xFFFCE4EC)),
            QuranLetterItem("ج", "Jeem", "جِيم", "j", "Jannah (Garden/Paradise)", "جَنَّة", "Paradise", Color(0xFF9C27B0), Color(0xFFF3E5F5)),
            QuranLetterItem("ح", "Haa", "حَاء", "h", "Haleeb (Milk)", "حَلِيب", "Milk", Color(0xFF00BCD4), Color(0xFFE0F7FA)),
            QuranLetterItem("خ", "Khaa", "خَاء", "kh", "Khubz (Bread)", "خُبْز", "Bread", Color(0xFF795548), Color(0xFFEFEBE9)),
            QuranLetterItem("د", "Daal", "دَال", "d", "Dua (Supplication)", "دُعَاء", "Prayer/Dua", Color(0xFF607D8B), Color(0xFFECEFF1)),
            QuranLetterItem("ذ", "Thaal", "ذَال", "dh", "Thahab (Gold)", "ذَهَب", "Gold", Color(0xFFFFC107), Color(0xFFFFF8E1)),
            QuranLetterItem("ر", "Raa", "رَاء", "r", "Rahmah (Mercy)", "رَحْمَة", "Mercy", Color(0xFF8BC34A), Color(0xFFF1F8E9)),
            QuranLetterItem("ز", "Zay", "زَاي", "z", "Zakaah (Charity)", "زَكَاة", "Charity", Color(0xFF3F51B5), Color(0xFFE8EAF6)),
            QuranLetterItem("س", "Seen", "سِين", "s", "Sajdah (Prostration)", "سَجْدَة", "Prostration", Color(0xFF009688), Color(0xFFE0F2F1)),
            QuranLetterItem("ش", "Sheen", "شِين", "sh", "Shams (Sun)", "شَمْس", "Sun", Color(0xFFFF5722), Color(0xFFFBE9E7)),
            QuranLetterItem("ص", "Saad", "صَاد", "s", "Salaah (Prayer)", "صَلَاة", "Prayer", Color(0xFF4CAF50), Color(0xFFE8F5E9)),
            QuranLetterItem("ض", "Daad", "ضَاد", "d", "Duhaa (Forenoon)", "ضُحَى", "Morning Light", Color(0xFF2196F3), Color(0xFFE3F2FD)),
            QuranLetterItem("ط", "Taa", "طَاء", "t", "Tayr (Bird)", "طَيْر", "Bird", Color(0xFFFF9800), Color(0xFFFFF3E0)),
            QuranLetterItem("ظ", "Zaa", "ظَاء", "z", "Zill (Shadow)", "ظِلّ", "Shadow", Color(0xFFE91E63), Color(0xFFFCE4EC)),
            QuranLetterItem("ع", "Ayn", "عَيْن", "a", "Ilm (Knowledge)", "عِلْم", "Knowledge", Color(0xFF9C27B0), Color(0xFFF3E5F5)),
            QuranLetterItem("غ", "Ghayn", "غَيْن", "gh", "Ghafoor (Forgiving)", "غَفُور", "All-Forgiving", Color(0xFF00BCD4), Color(0xFFE0F7FA)),
            QuranLetterItem("ف", "Faa", "فَاء", "f", "Fajr (Dawn)", "فَجْر", "Dawn", Color(0xFF795548), Color(0xFFEFEBE9)),
            QuranLetterItem("ق", "Qaaf", "قَاف", "q", "Qalam (Pen)", "قَلَم", "Pen", Color(0xFFFFC107), Color(0xFFFFF8E1)),
            QuranLetterItem("ك", "Kaaf", "كَاف", "k", "Kitaab (Book)", "كِتَاب", "Book", Color(0xFFFF5722), Color(0xFFFBE9E7)),
            QuranLetterItem("ل", "Laam", "لاَم", "l", "Layl (Night)", "لَيْل", "Night", Color(0xFF009688), Color(0xFFE0F2F1)),
            QuranLetterItem("م", "Meem", "مِيم", "m", "Masjid (Mosque)", "مَسْجِد", "Mosque", Color(0xFF3F51B5), Color(0xFFE8EAF6)),
            QuranLetterItem("ن", "Noon", "نُون", "n", "Noor (Light)", "نُور", "Light", Color(0xFF4CAF50), Color(0xFFE8F5E9)),
            QuranLetterItem("و", "Waw", "وَاو", "w", "Wudu (Ablution)", "وُضُوء", "Ablution", Color(0xFF2196F3), Color(0xFFE3F2FD)),
            QuranLetterItem("هـ", "Haa", "هَاء", "h", "Huda (Guidance)", "هُدَى", "Guidance", Color(0xFFFF9800), Color(0xFFFFF3E0)),
            QuranLetterItem("ء", "Hamzah", "هَمْزَة", "'", "Eemaan (Faith)", "إِيمَان", "Faith", Color(0xFFE91E63), Color(0xFFFCE4EC)),
            QuranLetterItem("ي", "Yaa", "يَاء", "y", "Yaqeen (Certainty)", "يَقِين", "Certainty", Color(0xFF9C27B0), Color(0xFFF3E5F5))
        )
    }

    val surahsList = remember {
        listOf(
            QuranSurahItem(
                id = "ikhlas",
                nameEn = "Al-Ikhlas",
                nameAr = "الإخلاص",
                meaning = "Purity of Faith",
                totalVerses = 4,
                verses = listOf(
                    SurahVerse(1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Qul huwal-laahu ahad", "Say, 'He is Allah, [who is] One,"),
                    SurahVerse(2, "اللَّهُ الصَّمَدُ", "Allahus-samad", "Allah, the Eternal Refuge."),
                    SurahVerse(3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "Lam yalid wa lam yoolad", "He neither begets nor is born,"),
                    SurahVerse(4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Wa lam yakul-lahoo kufuwan ahad", "Nor is there to Him any equivalent.'")
                ),
                color = Color(0xFFFFF3E0),
                accentColor = Color(0xFFFF9800)
            ),
            QuranSurahItem(
                id = "kawthar",
                nameEn = "Al-Kawthar",
                nameAr = "الكوثر",
                meaning = "Abundant Blessings",
                totalVerses = 3,
                verses = listOf(
                    SurahVerse(1, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "Innaaa a'tainaa kal kawthar", "Indeed, We have granted you, [O Muhammad], al-Kawthar."),
                    SurahVerse(2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "Fa salli li rabbika wanhar", "So pray to your Lord and sacrifice [to Him alone]."),
                    SurahVerse(3, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "Inna shaani'aka huwal abtar", "Indeed, your enemy is the one cut off.")
                ),
                color = Color(0xFFFCE4EC),
                accentColor = Color(0xFFE91E63)
            ),
            QuranSurahItem(
                id = "fatiha",
                nameEn = "Al-Fatihah",
                nameAr = "الفاتِحة",
                meaning = "The Opening",
                totalVerses = 7,
                verses = listOf(
                    SurahVerse(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "Bismillaahir-Rahmaanir-Raheem", "In the name of Allah, the Entirely Merciful, the Especially Merciful."),
                    SurahVerse(2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Al-hamdu lillaahi Rabbil- 'aalameen", "[All] praise is [due] to Allah, Lord of the worlds -"),
                    SurahVerse(3, "الرَّحْمَٰنِ الرَّحِيمِ", "Ar-Rahmaanir-Raheem", "The Entirely Merciful, the Especially Merciful,"),
                    SurahVerse(4, "مَالِكِ يَوْمِ الدِّينِ", "Maaliki Yawmid-Deen", "Sovereign of the Day of Recompense."),
                    SurahVerse(5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "Iyyaaka na'budu wa iyyaaka nasta'een", "It is You we worship and You we ask for help."),
                    SurahVerse(6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Ihdinas-Siraatal-Mustaqeem", "Guide us to the straight path -"),
                    SurahVerse(7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "Siraatal-lazeena an'amta 'alaihim ghayril-maghdoobi 'alaihim wa lad-daalleen", "The path of those upon whom You have bestowed favor, not of those who have evoked anger, nor of those who are astray.")
                ),
                color = Color(0xFFE3F2FD),
                accentColor = Color(0xFF2196F3)
            )
        )
    }

    val tajweedList = remember {
        listOf(
            TajweedRuleItem(
                id = "ghunnah",
                titleEn = "Ghunnah (Nasal Hum ✨)",
                titleAr = "الغُنَّة",
                descriptionEn = "Pronounce a humming sound from deep in your nose for 2 counts! Keep it long when you see a double Shaddah (ّ) on Noon or Meem.",
                descriptionAr = "صوت يخرج من الأنف بمقدار حركتين عند التشديد على الميم أو النون",
                exampleAr = "مِنْ مَّاءٍ",
                exampleEn = "Min Maaa'",
                pronunciationGuidance = "Gently pause and make a sweet ringing buzz kind of like a small bee buzzing in the garden!",
                color = Color(0xFFE8F5E9),
                accentColor = Color(0xFF2E7D32),
                soundText = "text:arabic:مِنْ مَّاءٍ"
            ),
            TajweedRuleItem(
                id = "qalqalah",
                titleEn = "Qalqalah (Echo Bounce ⚽)",
                titleAr = "القَلْقَلَة",
                descriptionEn = "Bounce or echo your tongue quickly when you pronounce (ق, ط, ب, ج, د) with a Sukoon (ْ) or stop!",
                descriptionAr = "نبرة واهتزاز في الصوت عند نطق هذه الحروف ساكنة: قطب جد",
                exampleAr = "فَلَقْ",
                exampleEn = "Falaq",
                pronunciationGuidance = "Hit the letter cleanly and let your voice snap open instantly, like bouncing a soccer ball!",
                color = Color(0xFFE3F2FD),
                accentColor = Color(0xFF1565C0),
                soundText = "text:arabic:فَلَقْ"
            ),
            TajweedRuleItem(
                id = "madd",
                titleEn = "Madd (Vowel Stretch ↕️)",
                titleAr = "المَدّ",
                descriptionEn = "Stretch and prolong your vowel sounds elegantly whenever you spot a wave symbol (~) on top of Alif, Waw or Yaa!",
                descriptionAr = "إطالة الصوت بمدة مناسبة لحروف المد الثلاثة: الألف، الواو، والياء",
                exampleAr = "جَآءَ",
                exampleEn = "Jaaa'a",
                pronunciationGuidance = "Pull your voice smoothly out like drawing a colorful bow or chewing sweet bubble gum!",
                color = Color(0xFFFFF3E0),
                accentColor = Color(0xFFEF6C00),
                soundText = "text:arabic:جَآءَ"
            )
        )
    }

    val quranWordsList = remember {
        listOf(
            QuranWordItem("كِتَابٌ", "Kitaab", "Book", Color(0xFFE8EAF6), Color(0xFF3F51B5)),
            QuranWordItem("نُورٌ", "Noor", "Light (Noor)", Color(0xFFE8F5E9), Color(0xFF4CAF50)),
            QuranWordItem("قَمَرٌ", "Qamar", "Moon", Color(0xFFECEFF1), Color(0xFF607D8B)),
            QuranWordItem("شَمْسٌ", "Shams", "Sun", Color(0xFFFBE9E7), Color(0xFFFF5722)),
            QuranWordItem("جَنَّةٌ", "Jannah", "Paradise Garden", Color(0xFFF3E5F5), Color(0xFF9C27B0)),
            QuranWordItem("رَحْمَةٌ", "Rahmah", "Mercy & Care", Color(0xFFFCE4EC), Color(0xFFE91E63))
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quran Explorer 📖",
                        color = DeepNavy,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                },
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
        if (activeQuranMode != null) {
            CompleteQuranReaderLayout(
                is16LineMode = (activeQuranMode == "16line"),
                onBackPressed = { activeQuranMode = null },
                onSoundTrigger = onSoundTrigger,
                onEarnRewards = onEarnRewards
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = LightBlueGrey),
                    border = BorderStroke(1.dp, BorderGrey)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📖", fontSize = 36.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Welcome to Al-Quran Al-Kareem! ✨",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = DeepNavy
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Perfect your Arabic recitation, explore stunning verses, and obtain shiny Honey coins! 🐝",
                                fontSize = 11.sp,
                                color = SlateGrey
                            )
                        }
                    }
                }

                // Dynamic bookmarks
                val b16SurahNum = sharedPrefs.getInt("bookmarked_16line_surah", -1).let { if (it == -1) null else it }
                val b16VerseNum = sharedPrefs.getInt("bookmarked_16line_verse", -1).let { if (it == -1) null else it }
                val bEngSurahNum = sharedPrefs.getInt("bookmarked_english_surah", -1).let { if (it == -1) null else it }
                val bEngVerseNum = sharedPrefs.getInt("bookmarked_english_verse", -1).let { if (it == -1) null else it }
                
                val surahsCache = remember { getQuran114Surahs() }
                val b16SurahName = if (b16SurahNum != null) surahsCache.find { it.number == b16SurahNum }?.nameEn else null
                val bEngSurahName = if (bEngSurahNum != null) surahsCache.find { it.number == bEngSurahNum }?.nameEn else null

                // ADD TWO OPTIONS: 16 Line Quran & English Translation Quran
                Text(
                    text = "Arabic Quran Recitation & English Translation 👇",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DeepNavy,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp).align(Alignment.Start)
                )

                // Option 1: 16 Line Quran (Arabic Only)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            activeQuranMode = "16line"
                            onSoundTrigger("text:english:Opening 16-line Arabic Quran.")
                        }
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBE7)), // Creamy green feel
                    border = BorderStroke(1.5.dp, Color(0xFF558B2F)) // Beautiful Leafy Green
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFDCEDC8), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📖", fontSize = 22.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "16 Line Quran (Arabic Only) • المصحف الشريف",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF33691E)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Read purely in Arabic with no translations. Styled in beautiful classic page structure for easy reading & memorization.",
                                    fontSize = 10.sp,
                                    color = Color(0xFF558B2F),
                                    lineHeight = 13.sp
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Open 16 Line Quran",
                                tint = Color(0xFF33691E),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        if (b16SurahNum != null && b16SurahName != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color(0xFFDCEDC8).copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("📍", fontSize = 12.sp)
                                    Text(
                                        text = "Saved Location: Surah $b16SurahName, Verse $b16VerseNum",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF33691E)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "Tap cover to resume",
                                        fontSize = 8.sp,
                                        color = Color(0xFF33691E).copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Option 2: Quran with International English Translation
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            activeQuranMode = "english"
                            onSoundTrigger("text:english:Opening Quran with English Translation.")
                        }
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)), // Calming sky blue/navy feel
                    border = BorderStroke(1.5.dp, Color(0xFF1565C0)) // Strong premium blue
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFBBDEFB), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🌍", fontSize = 22.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Quran (with English Translation) • بالترجمة",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF0D47A1)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Explore all 114 Surahs with complete sentence-by-sentence International translations, meanings, and audio recitation.",
                                    fontSize = 10.sp,
                                    color = Color(0xFF1565C0),
                                    lineHeight = 13.sp
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Open Translated Quran",
                                tint = Color(0xFF0D47A1),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        if (bEngSurahNum != null && bEngSurahName != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color(0xFFBBDEFB).copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("📍", fontSize = 12.sp)
                                    Text(
                                        text = "Saved Location: Surah $bEngSurahName, Verse $bEngVerseNum",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0D47A1)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "Tap cover to resume",
                                        fontSize = 8.sp,
                                        color = Color(0xFF0D47A1).copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Age Roadmaps Tab Selection Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Age 6 Tab - Active
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedAgeFilter = 6 }
                            .background(
                                color = if (selectedAgeFilter == 6) Color(0xFFE8F5E9) else Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = if (selectedAgeFilter == 6) 2.dp else 1.dp,
                                color = if (selectedAgeFilter == 6) Color(0xFF4CAF50) else BorderGrey,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("👶 Age 6", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepNavy)
                        Text("Letters", fontSize = 9.sp, color = if (selectedAgeFilter == 6) Color(0xFF2E7D32) else SlateGrey)
                    }

                    // Age 7 Tab - Surahs
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedAgeFilter = 7 }
                            .background(
                                color = if (selectedAgeFilter == 7) Color(0xFFE3F2FD) else Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = if (selectedAgeFilter == 7) 2.dp else 1.dp,
                                color = if (selectedAgeFilter == 7) Color(0xFF2196F3) else BorderGrey,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("👦 Age 7", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepNavy)
                        Text("Short Surahs", fontSize = 9.sp, color = if (selectedAgeFilter == 7) Color(0xFF1565C0) else SlateGrey)
                    }

                    // Age 8 Tab - Advanced Tajweed
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedAgeFilter = 8 }
                            .background(
                                color = if (selectedAgeFilter == 8) Color(0xFFFFF3E0) else Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = if (selectedAgeFilter == 8) 2.dp else 1.dp,
                                color = if (selectedAgeFilter == 8) Color(0xFFFF9800) else BorderGrey,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🧒 Age 8+", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepNavy)
                        Text("Tajweed / Words", fontSize = 9.sp, color = if (selectedAgeFilter == 8) Color(0xFFE65100) else SlateGrey)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // DYNAMIC AGE CONTENT ROUTER
                when (selectedAgeFilter) {
                    6 -> {
                        // Age 6 Core Content: Pronunciation Letters Grid
                        Text(
                            text = "Tap on a letter to hear the sound & practice!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Text(
                            text = "Mastered: ${masteredLetters.size}/${quranLettersList.size} Letters ✨",
                            fontSize = 11.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .heightIn(max = 700.dp)
                                .testTag("quran_letters_grid"),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(quranLettersList) { item ->
                                val isMastered = masteredLetters.contains(item.letter)
                                Card(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clickable {
                                            selectedLetter = item
                                            onSoundTrigger("text:arabic:${item.letter}")
                                        }
                                        .testTag("quran_letter_${item.name.lowercase()}"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isMastered) Color(0xFFE8F5E9) else item.cardColor
                                    ),
                                    border = BorderStroke(
                                        width = if (isMastered) 2.dp else 1.dp,
                                        color = if (isMastered) Color(0xFF4CAF50) else item.accentColor.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isMastered) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Mastered",
                                                tint = Color(0xFF4CAF50),
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(4.dp)
                                                    .size(14.dp)
                                            )
                                        }

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = item.letter,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (isMastered) Color(0xFF1B5E20) else DeepNavy
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = item.name,
                                                fontSize = 10.sp,
                                                color = SlateGrey,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    7 -> {
                        // Age 7 Content: Short Surahs Interactive Recitation
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Beautiful Surahs Explorer 🕌",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = DeepNavy
                            )
                            Text(
                                text = "Read and listen to verses. Master the complete surah for Honey bonuses!",
                                fontSize = 11.sp,
                                color = SlateGrey
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // List out Surahs
                            surahsList.forEach { surah ->
                                val isCompleted = completedSurahs.contains(surah.id)
                                val isExpanded = expandedSurahId == surah.id

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .shadow(2.dp, RoundedCornerShape(16.dp)),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isCompleted) Color(0xFFE8F5E9) else Color.White
                                    ),
                                    border = BorderStroke(
                                        width = if (isExpanded) 2.dp else 1.dp,
                                        color = if (isExpanded) surah.accentColor else BorderGrey
                                    )
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        // Header Row of Surah Card
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    expandedSurahId = if (isExpanded) null else surah.id
                                                    onSoundTrigger("text:english:Exploring Surah ${surah.nameEn}")
                                                }
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .background(surah.color, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (isCompleted) {
                                                        Icon(Icons.Default.CheckCircle, contentDescription = "Finished", tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                                                    } else {
                                                        Text("⭐", fontSize = 16.sp)
                                                    }
                                                }

                                                Column {
                                                    Text(
                                                        text = "${surah.nameEn} (${surah.meaning})",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp,
                                                        color = DeepNavy
                                                    )
                                                    Text(
                                                        text = "${surah.totalVerses} Verses • Completed: ${surah.verses.count { completedVerses.contains("${surah.id}_${it.number}") }}/${surah.totalVerses}",
                                                        fontSize = 10.sp,
                                                        color = SlateGrey
                                                    )
                                                }
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = surah.nameAr,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = surah.accentColor
                                                )
                                                Icon(
                                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                    contentDescription = "Expand",
                                                    tint = SlateGrey
                                                )
                                            }
                                        }

                                        // Expanded verses list
                                        AnimatedVisibility(visible = isExpanded) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(LightBlueGrey.copy(alpha = 0.5f))
                                                    .padding(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                surah.verses.forEach { verse ->
                                                    val isVerseDone = completedVerses.contains("${surah.id}_${verse.number}")
                                                    
                                                    Card(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = RoundedCornerShape(12.dp),
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = if (isVerseDone) Color(0xFFE8F5E9) else Color.White
                                                        ),
                                                        border = BorderStroke(1.dp, if (isVerseDone) Color(0xFF81C784) else BorderGrey)
                                                    ) {
                                                        Column(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(12.dp)
                                                        ) {
                                                            // Verse Arabic text
                                                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                                                Row(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    // Verse number badge
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .size(24.dp)
                                                                            .background(surah.color, CircleShape)
                                                                            .border(1.dp, surah.accentColor, CircleShape),
                                                                        contentAlignment = Alignment.Center
                                                                    ) {
                                                                        Text(
                                                                            text = verse.number.toString(),
                                                                            fontSize = 10.sp,
                                                                            fontWeight = FontWeight.Bold,
                                                                            color = DeepNavy
                                                                        )
                                                                    }

                                                                    Text(
                                                                        text = verse.textAr,
                                                                        fontSize = 20.sp,
                                                                        fontWeight = FontWeight.Black,
                                                                        color = DeepNavy,
                                                                        modifier = Modifier.weight(1f)
                                                                    )
                                                                }
                                                            }

                                                            Spacer(modifier = Modifier.height(6.dp))

                                                            // Transliteration
                                                            Text(
                                                                text = verse.transliteration,
                                                                fontSize = 12.sp,
                                                                color = DeepNavy,
                                                                fontWeight = FontWeight.Bold
                                                            )

                                                            // Translation
                                                            Text(
                                                                text = verse.translation,
                                                                fontSize = 11.sp,
                                                                color = SlateGrey
                                                            )

                                                            Spacer(modifier = Modifier.height(10.dp))

                                                            // Action Buttons
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                            ) {
                                                                // Hear Recitation
                                                                Button(
                                                                    onClick = {
                                                                        onSoundTrigger("text:arabic:${verse.textAr}")
                                                                    },
                                                                    colors = ButtonDefaults.buttonColors(containerColor = surah.color),
                                                                    shape = RoundedCornerShape(12.dp),
                                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                                    modifier = Modifier.height(32.dp)
                                                                ) {
                                                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(14.dp), tint = surah.accentColor)
                                                                    Spacer(modifier = Modifier.width(4.dp))
                                                                    Text("Listen 🔊", color = surah.accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                                }

                                                                // Complete Check
                                                                if (!isVerseDone) {
                                                                    Button(
                                                                        onClick = {
                                                                            val vId = "${surah.id}_${verse.number}"
                                                                            completedVerses = completedVerses + vId
                                                                            onEarnRewards(2, 5, "Factual verse completion!")
                                                                            onSoundTrigger("text:english:Mashallah! Beautiful recitation of verse ${verse.number}")
                                                                            
                                                                            // Check if entire Surah is completed
                                                                            val allVersesMatched = surah.verses.all { 
                                                                                val checkId = "${surah.id}_${it.number}"
                                                                                checkId == vId || completedVerses.contains(checkId) 
                                                                            }
                                                                            if (allVersesMatched) {
                                                                                completedSurahs = completedSurahs + surah.id
                                                                                onEarnRewards(15, 20, "Completed entire Surah ${surah.nameEn}! 🎉")
                                                                                onSoundTrigger("text:english:Allahu Akbar! Fantastic! You completed the whole surah! Take your massive Honey stars!")
                                                                            }
                                                                        },
                                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9)),
                                                                        shape = RoundedCornerShape(12.dp),
                                                                        border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                                                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                                        modifier = Modifier.height(32.dp)
                                                                    ) {
                                                                        Icon(Icons.Default.Check, contentDescription = "Done", modifier = Modifier.size(14.dp), tint = Color(0xFF2E7D32))
                                                                        Spacer(modifier = Modifier.width(4.dp))
                                                                        Text("Recited ✅", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                                    }
                                                                } else {
                                                                    Row(
                                                                        verticalAlignment = Alignment.CenterVertically,
                                                                        modifier = Modifier.padding(horizontal = 8.dp)
                                                                    ) {
                                                                        Icon(Icons.Default.CheckCircle, contentDescription = "Certified", tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                                                                        Spacer(modifier = Modifier.width(4.dp))
                                                                        Text("Spelled & Completed", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    8 -> {
                        // Age 8 Content: Interactive Tajweed and Word Quest
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Quran Tajweed Guidance 💎",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = DeepNavy
                            )
                            Text(
                                text = "Learn to sound like professional reciters with decorative guidelines!",
                                fontSize = 11.sp,
                                color = SlateGrey
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Tajweed Lessons Carousel/Column
                            tajweedList.forEach { rule ->
                                val isDone = completedTajweedRules.contains(rule.id)
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clickable {
                                            selectedTajweedRule = rule
                                            onSoundTrigger("text:english:Reading about ${rule.titleEn}")
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = rule.color),
                                    border = BorderStroke(1.dp, rule.accentColor.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = rule.titleEn,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = DeepNavy
                                                )
                                                if (isDone) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Icon(Icons.Default.CheckCircle, contentDescription = "Mastered", tint = rule.accentColor, modifier = Modifier.size(14.dp))
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = rule.descriptionEn,
                                                fontSize = 11.sp,
                                                color = SlateGrey
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(Color.White, RoundedCornerShape(12.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(rule.exampleAr, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = rule.accentColor)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Interactive Section 2: Vocabulary Words Quest
                            Text(
                                text = "Interactive Vocabulary Quest 🎮",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = DeepNavy
                            )
                            Text(
                                text = "Learn core Quranic terms. Hear the correct pronunciation and master them!",
                                fontSize = 11.sp,
                                color = SlateGrey
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier
                                    .heightIn(max = 350.dp)
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(quranWordsList) { wordItem ->
                                    val isLearned = completedVocabularyWords.contains(wordItem.wordAr)
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isLearned) Color(0xFFE8F5E9) else wordItem.cardColor
                                        ),
                                        border = BorderStroke(
                                            width = if (isLearned) 2.dp else 1.dp,
                                            color = if (isLearned) Color(0xFF4CAF50) else wordItem.accentColor.copy(alpha = 0.3f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = wordItem.wordAr,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Black,
                                                color = DeepNavy
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "/ ${wordItem.phonetic} /",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DeepNavy
                                            )
                                            Text(
                                                text = wordItem.translation,
                                                fontSize = 10.sp,
                                                color = SlateGrey
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Hear word
                                                IconButton(
                                                    onClick = { onSoundTrigger("text:arabic:${wordItem.wordAr}") },
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .background(Color.White, CircleShape)
                                                ) {
                                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(14.dp), tint = wordItem.accentColor)
                                                }

                                                // Mark read
                                                if (!isLearned) {
                                                    IconButton(
                                                        onClick = {
                                                            completedVocabularyWords = completedVocabularyWords + wordItem.wordAr
                                                            onEarnRewards(3, 5, "Mastered vocabulary term!")
                                                            onSoundTrigger("text:english:Perfect pronunciation! Sweet honey award delivered!")
                                                        },
                                                        modifier = Modifier
                                                            .size(28.dp)
                                                            .background(Color.White, CircleShape)
                                                    ) {
                                                        Icon(Icons.Default.Check, contentDescription = "Complete", modifier = Modifier.size(14.dp), tint = Color(0xFF2E7D32))
                                                    }
                                                } else {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = "Complete", tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }

            // Interactive Dialog for Selected Letter Details (Age 6)
            selectedLetter?.let { letterItem ->
                AlertDialog(
                    onDismissRequest = { selectedLetter = null },
                    icon = { Text("✨ Sparkly Letter Guide ✨", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300)) },
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = letterItem.letter,
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1B5E20)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = letterItem.nameAr,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy
                                )
                                Text(
                                    text = "(${letterItem.name})",
                                    fontSize = 14.sp,
                                    color = SlateGrey
                                )
                            }
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Phonetic sound: / ${letterItem.phonetic} /",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DeepNavy,
                                modifier = Modifier
                                    .background(LightBlueGrey, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))

                            // Quranic Example Word section
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = letterItem.cardColor),
                                border = BorderStroke(1.dp, letterItem.accentColor.copy(alpha = 0.3f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Example Quranic Word:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateGrey
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = letterItem.exampleAr,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black,
                                        color = DeepNavy
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = letterItem.example,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepNavy
                                    )
                                    Text(
                                        text = "Meaning: \"${letterItem.translation}\"",
                                        fontSize = 12.sp,
                                        color = SlateGrey
                                    )
                                    
                                    Spacer(modifier = Modifier.height(10.dp))
                                    
                                    Button(
                                        onClick = { onSoundTrigger("text:arabic:${letterItem.exampleAr}") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(1.dp, letterItem.accentColor),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text(text = "🔊 Say Word", color = letterItem.accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Master/Review buttons
                            val isMastered = masteredLetters.contains(letterItem.letter)
                            if (!isMastered) {
                                Button(
                                    onClick = {
                                        masteredLetters = masteredLetters + letterItem.letter
                                        onEarnRewards(5, 10, "Mastered Quranic letter ${letterItem.letter}!")
                                        onSoundTrigger("text:english:Mascot says great job! You mastered the letter!")
                                        selectedLetter = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("master_letter_button")
                                ) {
                                    Text("Mark Completed! 🎉 (+5 Hex Coins)", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Spelled & Completed! 🌟", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { selectedLetter = null }) {
                            Text("Done", color = DeepNavy, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            // Interactive Dialog for Selected Tajweed Rule (Age 8)
            selectedTajweedRule?.let { rule ->
                AlertDialog(
                    onDismissRequest = { selectedTajweedRule = null },
                    icon = { Text("💎 Tajweed Detective Guide 💎", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = rule.accentColor) },
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(text = rule.titleEn, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
                            Text(text = rule.titleAr, fontSize = 24.sp, fontWeight = FontWeight.Black, color = rule.accentColor)
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = rule.descriptionEn,
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp,
                                color = DeepNavy
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, BorderGrey),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("PRACTICE KEYWORD:", fontSize = 11.sp, color = SlateGrey, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(rule.exampleAr, fontSize = 36.sp, fontWeight = FontWeight.Black, color = DeepNavy)
                                    Text("Pronounced: \"${rule.exampleEn}\"", fontSize = 13.sp, color = SlateGrey)
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    Button(
                                        onClick = { onSoundTrigger(rule.soundText) },
                                        colors = ButtonDefaults.buttonColors(containerColor = rule.color),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("🔊 Audify Pronunciation", color = rule.accentColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Pro Parent & Child Tip:\n${rule.pronunciationGuidance}",
                                textAlign = TextAlign.Center,
                                fontSize = 11.sp,
                                color = SlateGrey
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            val hasRuleDone = completedTajweedRules.contains(rule.id)
                            if (!hasRuleDone) {
                                Button(
                                    onClick = {
                                        completedTajweedRules = completedTajweedRules + rule.id
                                        onEarnRewards(8, 15, "Learned Tajweed rule ${rule.titleEn}!")
                                        onSoundTrigger("text:english:SubhanAllah! Brilliant effort! You studied the tajweed guideline.")
                                        selectedTajweedRule = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("I Said It Nicely! 🗣️ (+8 Hex Coins)", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Certified", tint = Color(0xFF4CAF50), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Lesson Completed! 🌟", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { selectedTajweedRule = null }) {
                            Text("Done", color = DeepNavy, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }
        }
    }
}

// --- Complete Quran Reader Mode ---

data class SurahHeader(
    val number: Int,
    val nameEn: String,
    val nameAr: String,
    val meaning: String,
    val versesCount: Int,
    val type: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteQuranReaderLayout(
    is16LineMode: Boolean = false,
    onBackPressed: () -> Unit,
    onSoundTrigger: (String) -> Unit,
    onEarnRewards: (Int, Int, String) -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("NoorBeePrefs", Context.MODE_PRIVATE) }
    
    val is16LineMode = false
    
    val initialSurahKey = if (is16LineMode) "bookmarked_16line_surah" else "bookmarked_english_surah"
    val initialVerseKey = if (is16LineMode) "bookmarked_16line_verse" else "bookmarked_english_verse"

    val surahs = remember { getQuran114Surahs() }
    var searchQuery by remember { mutableStateOf("") }
    var activeSurah by remember { mutableStateOf<SurahHeader?>(null) }
    var loadedVerses by remember { mutableStateOf<List<SurahVerse>?>(null) }
    var errorLoading by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Custom controls
    var showTranslation by remember(is16LineMode) { mutableStateOf(!is16LineMode) }
    var arabicFontSize by remember(is16LineMode) { mutableStateOf(if (is16LineMode) 32f else 26f) }
    
    // Playback state and MediaPlayer setup
    var playingVerseKey by remember { mutableStateOf<String?>(null) } // Format "surahNum:verseNum"
    val mediaPlayer = remember { MediaPlayer().apply {
        setOnCompletionListener {
            playingVerseKey = null
        }
        setOnErrorListener { mp, what, extra ->
            playingVerseKey = null
            false
        }
    }}

    DisposableEffect(Unit) {
        onDispose {
            try {
                mediaPlayer.stop()
                mediaPlayer.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playAudioForVerse(surahNum: Int, verseNum: Int) {
        val key = "$surahNum:$verseNum"
        if (playingVerseKey == key) {
            try {
                mediaPlayer.stop()
                mediaPlayer.reset()
            } catch (e: Exception) {}
            playingVerseKey = null
            return
        }
        
        playingVerseKey = key
        try {
            mediaPlayer.reset()
            val formattedSurah = java.lang.String.format(java.util.Locale.US, "%03d", surahNum)
            val formattedVerse = java.lang.String.format(java.util.Locale.US, "%03d", verseNum)
            val audioUrl = "https://everyayah.com/data/Alafasy_128kbps/$formattedSurah$formattedVerse.mp3"
            mediaPlayer.setDataSource(audioUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener { mp ->
                mp.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            playingVerseKey = null
        }
    }

    // Reactive downloaded surahs listing
    var downloadedSurahs by remember {
        mutableStateOf(
            context.fileList()
                .filter { it.startsWith("surah_") && it.endsWith(".json") }
                .mapNotNull { it.substringAfter("surah_").substringBefore(".json").toIntOrNull() }
                .toSet()
        )
    }

    var downloadingSurahId by remember { mutableStateOf<Int?>(null) }
    
    // Persistent state markers loaded from SharedPreferences
    var bookmarkedSurahNumber by remember(is16LineMode) {
        val stored = sharedPrefs.getInt(initialSurahKey, -1)
        mutableStateOf<Int?>(if (stored == -1) null else stored)
    }
    var bookmarkedVerseNumber by remember(is16LineMode) {
        val stored = sharedPrefs.getInt(initialVerseKey, -1)
        mutableStateOf<Int?>(if (stored == -1) null else stored)
    }

    var selectedVerseNum by remember { mutableStateOf<Int?>(null) }

    val pages = remember(activeSurah, loadedVerses) {
        val currentSurah = activeSurah
        val currentVerses = loadedVerses
        if (currentSurah == null || currentVerses == null) {
            emptyList<List<SurahVerse>>()
        } else {
            val surahNum = currentSurah.number
            if (surahNum == 1) {
                listOf(currentVerses)
            } else if (surahNum == 2) {
                val p = mutableListOf<List<SurahVerse>>()
                val p1 = currentVerses.filter { it.number in 1..5 }
                if (p1.isNotEmpty()) p.add(p1)
                
                val p2 = currentVerses.filter { it.number in 6..16 }
                if (p2.isNotEmpty()) p.add(p2)
                
                val p3 = currentVerses.filter { it.number in 17..24 }
                if (p3.isNotEmpty()) p.add(p3)
                
                val remaining = currentVerses.filter { it.number > 24 }
                remaining.chunked(12).forEach { p.add(it) }
                p
            } else {
                if (currentVerses.size <= 12) {
                    listOf(currentVerses)
                } else {
                    currentVerses.chunked(12)
                }
            }
        }
    }

    val listState = rememberLazyListState()
    var shouldScrollToBookmark by remember { mutableStateOf(false) }

    LaunchedEffect(pages, shouldScrollToBookmark, bookmarkedVerseNumber) {
        if (shouldScrollToBookmark && pages.isNotEmpty()) {
            val targetIdx = pages.indexOfFirst { pageVerses ->
                pageVerses.any { it.number == bookmarkedVerseNumber }
            }
            if (targetIdx >= 0) {
                try {
                    // Give Jetpack Compose layout and composition passes some time to complete
                    kotlinx.coroutines.delay(300)
                    listState.scrollToItem(targetIdx)
                    // Optional second scroll call to guarantee scrolling even with dynamic heights
                    kotlinx.coroutines.delay(100)
                    listState.scrollToItem(targetIdx)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            shouldScrollToBookmark = false
        }
    }

    LaunchedEffect(activeSurah) {
        if (activeSurah != null) {
            if (activeSurah?.number == bookmarkedSurahNumber) {
                selectedVerseNum = bookmarkedVerseNumber
            } else {
                selectedVerseNum = null
            }
        } else {
            selectedVerseNum = null
        }
    }

    // Favorites persist database
    var favoriteSurahNumbers by remember {
        val stored = sharedPrefs.getStringSet("fav_quran_surahs", emptySet()) ?: emptySet()
        mutableStateOf(stored.mapNotNull { it.toIntOrNull() }.toSet())
    }

    // Toggle Favorite Action
    val toggleFavorite: (Int) -> Unit = { num ->
        val updated = favoriteSurahNumbers.toMutableSet()
        if (updated.contains(num)) {
            updated.remove(num)
        } else {
            updated.add(num)
            onEarnRewards(2, 5, "Favourited a Surah! 💖")
        }
        favoriteSurahNumbers = updated
        sharedPrefs.edit()
            .putStringSet("fav_quran_surahs", updated.map { it.toString() }.toSet())
            .apply()
    }

    // Modal popup state
    var activeDialogType by remember { mutableStateOf<String?>(null) }
    
    // Quick search toggle state
    var searchBarExpanded by remember { mutableStateOf(false) }
    
    // Tab selector (0 = BY SURAH, 1 = BY PARAH, 2 = FAVOURITE)
    var activeTabIndex by remember { mutableStateOf(0) }
    
    // Parah Index model data
    val parahList = remember {
        listOf(
            ParahItem(1, "Alif Lam Meem", "آلم", 2, 1),
            ParahItem(2, "Sayaqool", "سيقول", 2, 142),
            ParahItem(3, "Tilkal Rusul", "تلك الرسل", 2, 253),
            ParahItem(4, "Lan Tanaloo", "لن تنالوا", 3, 93),
            ParahItem(5, "Wal Mohsanat", "والمحصنات", 4, 24),
            ParahItem(6, "La Yuhibbullah", "لا يحب الله", 4, 148),
            ParahItem(7, "Wa Iza Samiu", "وإذا سمعوا", 5, 82),
            ParahItem(8, "Wa Lau Annana", "ولو أننا", 6, 111),
            ParahItem(9, "Qal Al-Mala'u", "قال الملأ", 7, 88),
            ParahItem(10, "Walamu", "واعلموا", 8, 41),
            ParahItem(11, "Yatadhiruon", "يعتذرون", 9, 93),
            ParahItem(12, "Wa Ma Min Daabbahin", "وما من دابة", 11, 6),
            ParahItem(13, "Wa Ma Ubarri'u", "وما أبرئ", 12, 53),
            ParahItem(14, "Alif Lam Ra", "ربما", 15, 1),
            ParahItem(15, "Subhanallazi", "سبحان الذي", 17, 1),
            ParahItem(16, "Qal Alam", "قال ألم", 18, 75),
            ParahItem(17, "Iqtaraba Lin-Nasi", "اقترب للناس", 21, 1),
            ParahItem(18, "Qad Aflaha", "قد أفلح", 23, 1),
            ParahItem(19, "Wa Qalallazina", "وقال الذين", 25, 21),
            ParahItem(20, "Aman Khalaqa", "أمن خلق", 27, 56),
            ParahItem(21, "Utlu Ma Oohiya", "اتل ما أوحي", 29, 46),
            ParahItem(22, "Wa Man Yaqnut", "ومن يقنت", 33, 31),
            ParahItem(23, "Wa Maliya", "ومالي", 36, 28),
            ParahItem(24, "Faman Azlamu", "فمن أظلم", 39, 32),
            ParahItem(25, "Ilayhi Yuraddu", "إليه يرد", 41, 47),
            ParahItem(26, "Ha Meem", "حم", 46, 1),
            ParahItem(27, "Qala Fama Khatbukum", "قال فما خطبكم", 51, 31),
            ParahItem(28, "Qad Sami'allahu", "قد سمع الله", 58, 1),
            ParahItem(29, "Tabarakallazi", "تبارك الذي", 67, 1),
            ParahItem(30, "Amma Yatasa'aloon", "عم يتساءلون", 78, 1)
        )
    }
    
    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            surahs
        } else {
            surahs.filter {
                it.nameEn.contains(searchQuery, ignoreCase = true) ||
                it.meaning.contains(searchQuery, ignoreCase = true) ||
                it.number.toString() == searchQuery ||
                it.nameAr.contains(searchQuery)
            }
        }
    }
    
    val scope = rememberCoroutineScope()
    
    // Trigger loader on Active Surah select
    LaunchedEffect(activeSurah) {
        val current = activeSurah
        if (current == null) {
            loadedVerses = null
            errorLoading = null
            return@LaunchedEffect
        }
        
        isLoading = true
        errorLoading = null
        loadedVerses = null
        playingVerseKey = null
        
        // Try offline first
        val offline = getOfflineSurah(context, current.number)
        if (offline != null) {
            loadedVerses = offline
            isLoading = false
            return@LaunchedEffect
        }
        
        // Fetch dynamically from internet and cache it automatically
        fetchSurahOnline(current.number) { verses, err ->
            scope.launch {
                isLoading = false
                if (err != null) {
                    errorLoading = err
                } else if (verses != null) {
                    loadedVerses = verses
                    // Silently save in localized storage to allow seamless subsequent offline readings
                    saveSurahOffline(context, current.number, verses)
                }
            }
        }
    }

    // Standard Theme Colors setup for 15-line Mode (Screen 1 & 2 layout aesthetics)
    val primaryDarkBg = Color(0xFF1E222B)
    val cardDarkBg = Color(0xFF242B35)
    val accentGreenGold = Color(0xFF4CAF50)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (is16LineMode) primaryDarkBg else MinimalWhite)
    ) {
        if (activeSurah == null) {
            // Surah Explorer List / Dashboard View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Header Bar (With customized Dark style for 15-line)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (is16LineMode) Color.White else DeepNavy
                        )
                    }
                    
                    Text(
                        text = if (is16LineMode) "Quran (Arabic Only)" else "Quran Translation • English",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = if (is16LineMode) Color.White else DeepNavy
                    )
                    
                    IconButton(onClick = { searchBarExpanded = !searchBarExpanded }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (is16LineMode) accentGreenGold else DeepNavy
                        )
                    }
                }


                
                // Toggleable search bar
                if (searchBarExpanded || searchQuery.isNotEmpty()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        placeholder = { 
                            Text(
                                "Search chapters here...", 
                                fontSize = 12.sp,
                                color = if (is16LineMode) Color.LightGray else SlateGrey
                            ) 
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = if (is16LineMode) accentGreenGold else SlateGrey) },
                        trailingIcon = {
                            IconButton(onClick = { 
                                searchQuery = ""
                                searchBarExpanded = false
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = if (is16LineMode) Color.White else SlateGrey)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = if (is16LineMode) cardDarkBg else Color.White,
                            unfocusedContainerColor = if (is16LineMode) cardDarkBg else Color.White,
                            focusedBorderColor = if (is16LineMode) accentGreenGold else DeepNavy,
                            unfocusedBorderColor = if (is16LineMode) Color.DarkGray else BorderGrey,
                            focusedTextColor = if (is16LineMode) Color.White else Color.Black,
                            unfocusedTextColor = if (is16LineMode) Color.White else Color.Black
                        ),
                        singleLine = true
                    )
                }

                // Elevated Authentic Header Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (is16LineMode) Color(0xFF1B2C24) else Color(0xFFE8F5E9)
                    ),
                    border = BorderStroke(1.2.dp, if (is16LineMode) Color(0xFFD4AF37) else Color(0xFF2E7D32))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "المصحف الشريف",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (is16LineMode) Color(0xFFFFD700) else Color(0xFF2E7D32),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "AL-QURAN AL-KAREEM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (is16LineMode) Color.White else DeepNavy,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Standard Resume Reading Block for BOTH modes
                if (bookmarkedSurahNumber != null) {
                    val bSurah = surahs.find { it.number == bookmarkedSurahNumber }
                    if (bSurah != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    shouldScrollToBookmark = true
                                    activeSurah = bSurah
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (is16LineMode) Color(0xFF2A362D) else Color(0xFFFFFDE7)
                            ),
                            border = BorderStroke(1.dp, if (is16LineMode) Color(0xFFD4AF37) else Color(0xFFFFD54F))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("📍", fontSize = 18.sp)
                                    Column {
                                        Text(
                                            text = "Resume Reading Progress",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (is16LineMode) Color.White else DeepNavy
                                        )
                                        Text(
                                            text = "Surah ${bSurah.nameEn} • Verse ${bookmarkedVerseNumber ?: 1}",
                                            fontSize = 11.sp,
                                            color = if (is16LineMode) Color.LightGray else SlateGrey
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Resume Progress",
                                    tint = if (is16LineMode) Color(0xFFFFD700) else DeepNavy,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Unified Tab list container for both light & dark mode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val tabs = listOf("BY SURAH", "BY PARAH", "FAVOURITE")
                    tabs.forEachIndexed { idx, label ->
                        val isActive = activeTabIndex == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { activeTabIndex = idx }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isActive) {
                                        if (is16LineMode) Color(0xFFFFD700) else Color(0xFF1B5E20)
                                    } else {
                                        Color.LightGray
                                    }
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(50.dp)
                                        .height(2.dp)
                                        .background(
                                            if (isActive) {
                                                if (is16LineMode) Color(0xFFFFD700) else Color(0xFF1B5E20)
                                            } else {
                                                Color.Transparent
                                            }
                                        )
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Index List based on active tab
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (activeTabIndex == 0) {
                        // Display Sura index list
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredSurahs) { surah ->
                                val isFav = favoriteSurahNumbers.contains(surah.number)
                                val isOfflineSpec = listOf(1, 103, 108, 112, 113, 114).contains(surah.number)
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { activeSurah = surah }
                                        .testTag("surah_card_${surah.number}"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (is16LineMode) cardDarkBg else Color.White
                                    ),
                                    border = BorderStroke(1.dp, if (is16LineMode) Color.DarkGray else BorderGrey)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            // Left portion: Calligraphy & Number matching screenshot Style 1
                                            Text(
                                                text = "${surah.number}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (is16LineMode) Color.Gray else DeepNavy,
                                                modifier = Modifier.width(18.dp)
                                            )
                                            
                                            // Big calligraphy Arabic name in vibrant green on left side
                                            Text(
                                                text = surah.nameAr,
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (is16LineMode) Color(0xFF00FF87) else DeepNavy
                                            )
                                        }
                                        
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            // Center info portion
                                            Column(horizontalAlignment = Alignment.End) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Text(
                                                        text = if (surah.nameEn == "Al-Fatihah") "Surat-ul-Fateha" else "Surat ${surah.nameEn}",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = if (is16LineMode) Color.White else DeepNavy
                                                    )
                                                }
                                                Text(
                                                    text = "Verses ${surah.versesCount}",
                                                    fontSize = 10.sp,
                                                    color = if (is16LineMode) Color.LightGray.copy(alpha = 0.8f) else SlateGrey
                                                )
                                            }
                                            
                                            // Heart toggle
                                            IconButton(
                                                onClick = { toggleFavorite(surah.number) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                    contentDescription = "Favorite",
                                                    tint = if (isFav) Color.Red else Color.LightGray,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (activeTabIndex == 1) {
                        // Display PARAH (Juzs) List
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(parahList) { parah ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val header = surahs.find { it.number == parah.startSurah }
                                            if (header != null) {
                                                activeSurah = header
                                            }
                                        },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (is16LineMode) cardDarkBg else Color.White
                                    ),
                                    border = BorderStroke(1.dp, if (is16LineMode) Color.DarkGray else BorderGrey)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Text(
                                                text = "${parah.number}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (is16LineMode) Color.Gray else DeepNavy,
                                                modifier = Modifier.width(18.dp)
                                            )
                                            
                                            Text(
                                                text = parah.nameAr,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (is16LineMode) Color(0xFF00FF87) else DeepNavy
                                            )
                                        }
                                        
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = parah.nameEn,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (is16LineMode) Color.White else DeepNavy
                                            )
                                            Text(
                                                text = "Juz ${parah.number} • Jump to Surah ${parah.startSurah}",
                                                fontSize = 10.sp,
                                                color = if (is16LineMode) Color.LightGray.copy(alpha = 0.8f) else SlateGrey
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else if (activeTabIndex == 2) {
                        // Display FAVOURITE List
                        val favSurahs = filteredSurahs.filter { favoriteSurahNumbers.contains(it.number) }
                        if (favSurahs.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("💖", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Your Favourites list is empty!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (is16LineMode) Color.White else DeepNavy
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap the heart outline icon beside any Surah to save it here for fast reading.",
                                    fontSize = 11.sp,
                                    color = if (is16LineMode) Color.LightGray else SlateGrey,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(favSurahs) { surah ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { activeSurah = surah },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (is16LineMode) cardDarkBg else Color.White
                                        ),
                                        border = BorderStroke(1.dp, if (is16LineMode) Color.DarkGray else BorderGrey)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Text(
                                                    text = "${surah.number}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (is16LineMode) Color.Gray else DeepNavy,
                                                    modifier = Modifier.width(18.dp)
                                                )
                                                
                                                Text(
                                                    text = surah.nameAr,
                                                    fontSize = 17.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = if (is16LineMode) Color(0xFF00FF87) else DeepNavy
                                                )
                                            }
                                            
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text(
                                                        text = "Surat ${surah.nameEn}",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = if (is16LineMode) Color.White else DeepNavy
                                                    )
                                                    Text(
                                                        text = "Verses ${surah.versesCount}",
                                                        fontSize = 10.sp,
                                                        color = if (is16LineMode) Color.LightGray.copy(alpha = 0.8f) else SlateGrey
                                                    )
                                                }
                                                IconButton(
                                                    onClick = { toggleFavorite(surah.number) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Favorite,
                                                        contentDescription = "Unfavorite",
                                                        tint = Color.Red,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Surah Detail Reader Mode
            val s = activeSurah!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (is16LineMode) Color(0xFF282B30) else Color.White)
            ) {
                // Toolbar (Beautifully themed matching Screen 2)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 4.dp,
                    color = if (is16LineMode) Color(0xFF1E2125) else MinimalWhite,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = { activeSurah = null }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Close",
                                    tint = if (is16LineMode) Color.White else DeepNavy
                                )
                            }
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${s.number}. ${s.nameEn}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = if (is16LineMode) Color.White else DeepNavy
                                )
                                Text(
                                    text = "${s.meaning} • ${s.versesCount} Verses",
                                    fontSize = 9.sp,
                                    color = if (is16LineMode) Color.LightGray else SlateGrey
                                )
                            }
                            
                            Text(
                                text = s.nameAr,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (is16LineMode) Color(0xFF00E676) else DeepNavy
                            )
                        }
                        
                        Divider(
                            modifier = Modifier.padding(vertical = 6.dp), 
                            color = if (is16LineMode) Color.DarkGray else BorderGrey
                        )
                        
                        // Controls Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "A-", 
                                    fontSize = 10.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    color = if (is16LineMode) Color.LightGray else SlateGrey
                                )
                                Slider(
                                    value = arabicFontSize,
                                    onValueChange = { arabicFontSize = it },
                                    valueRange = 18f..44f,
                                    modifier = Modifier.width(100.dp),
                                    colors = SliderDefaults.colors(
                                        activeTrackColor = if (is16LineMode) Color(0xFF00E676) else DeepNavy,
                                        thumbColor = if (is16LineMode) Color(0xFF00E676) else DeepNavy,
                                        inactiveTrackColor = Color.Gray
                                    )
                                )
                                Text(
                                    text = "A+", 
                                    fontSize = 10.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    color = if (is16LineMode) Color.LightGray else SlateGrey
                                )
                            }
                            
                            if (!is16LineMode) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { showTranslation = !showTranslation }
                                ) {
                                    Checkbox(
                                        checked = showTranslation,
                                        onCheckedChange = { showTranslation = it },
                                        colors = CheckboxDefaults.colors(checkedColor = DeepNavy)
                                    )
                                    Text("Show Meaning", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF1E3324), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Quran (Arabic Only) 🕌", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                                }
                            }
                        }
                    }
                }
                
                // Verses layout list
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (isLoading) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = if (is16LineMode) Color(0xFF00E676) else DeepNavy)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Seeking Surah pages from Quran Library... 🕌", 
                                fontSize = 12.sp, 
                                color = if (is16LineMode) Color.LightGray else SlateGrey
                            )
                        }
                    } else if (errorLoading != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🌐 Offline Notice", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Could not fetch Surah text from the network.",
                                fontWeight = FontWeight.Bold,
                                color = if (is16LineMode) Color.White else DeepNavy,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = errorLoading ?: "Network connection failed",
                                fontSize = 10.sp,
                                color = Color.Red,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (is16LineMode) cardDarkBg else LightBlueGrey
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "⚡ Instant Offline Surahs Available",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (is16LineMode) Color(0xFF00E676) else DeepNavy
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "You can instantly read any of these cached offline Suras on this child-secure app:",
                                        fontSize = 10.sp,
                                        color = if (is16LineMode) Color.LightGray else SlateGrey
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    
                                    val fallbackOfflineSuras = listOf(
                                        1 to "Al-Fatihah",
                                        103 to "Al-Asr",
                                        108 to "Al-Kawthar",
                                        112 to "Al-Ikhlas",
                                        113 to "Al-Falaq",
                                        114 to "Al-Nas"
                                    )
                                    fallbackOfflineSuras.forEach { pair ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    val header = surahs.find { it.number == pair.first }
                                                    if (header != null) {
                                                        activeSurah = header
                                                    }
                                                }
                                                .padding(vertical = 5.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "📖 ${pair.second}", 
                                                fontSize = 11.sp, 
                                                fontWeight = FontWeight.Bold, 
                                                color = if (is16LineMode) Color.White else DeepNavy
                                            )
                                            Text(
                                                text = "Read Offline 📲", 
                                                fontSize = 10.sp, 
                                                fontWeight = FontWeight.SemiBold, 
                                                color = if (is16LineMode) Color(0xFF00E676) else Color(0xFF2E7D32)
                                            )
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { activeSurah = s },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (is16LineMode) Color(0xFF00E676) else DeepNavy,
                                    contentColor = if (is16LineMode) Color.Black else Color.White
                                )
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Retry Connection")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retry Connection")
                            }
                        }
                    } else if (loadedVerses != null) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF1F1F1F))
                                    .padding(horizontal = 8.dp)
                            ) {
                                itemsIndexed(pages) { pageIndex, pageVerses ->
                                    val s = activeSurah!!
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp, horizontal = 4.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF2C2C2C)
                                        ),
                                        border = BorderStroke(1.2.dp, Color(0xFFC5A059))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .drawBehind {
                                                    val gold = Color(0xFFC5A059)
                                                    val strokeW = 1.2.dp.toPx()
                                                    val margin = 8.dp.toPx()
                                                    val size = size
                                                    
                                                    drawRect(
                                                        color = gold,
                                                        topLeft = Offset(margin, margin),
                                                        size = Size(size.width - margin * 2, size.height - margin * 2),
                                                        style = Stroke(width = strokeW)
                                                    )
                                                    
                                                    val cornerSize = 12.dp.toPx()
                                                    
                                                    // Top-Left Corner Ornate Arc
                                                    drawArc(
                                                        color = gold,
                                                        startAngle = 0f,
                                                        sweepAngle = 90f,
                                                        useCenter = false,
                                                        topLeft = Offset(margin, margin),
                                                        size = Size(cornerSize, cornerSize),
                                                        style = Stroke(width = strokeW * 1.5f)
                                                    )
                                                    // Top-Right Corner Ornate Arc
                                                    drawArc(
                                                        color = gold,
                                                        startAngle = 90f,
                                                        sweepAngle = 90f,
                                                        useCenter = false,
                                                        topLeft = Offset(size.width - margin - cornerSize, margin),
                                                        size = Size(cornerSize, cornerSize),
                                                        style = Stroke(width = strokeW * 1.5f)
                                                    )
                                                    // Bottom-Left Corner Ornate Arc
                                                    drawArc(
                                                        color = gold,
                                                        startAngle = 270f,
                                                        sweepAngle = 90f,
                                                        useCenter = false,
                                                        topLeft = Offset(margin, size.height - margin - cornerSize),
                                                        size = Size(cornerSize, cornerSize),
                                                        style = Stroke(width = strokeW * 1.5f)
                                                    )
                                                    // Bottom-Right Corner Ornate Arc
                                                    drawArc(
                                                        color = gold,
                                                        startAngle = 180f,
                                                        sweepAngle = 90f,
                                                        useCenter = false,
                                                        topLeft = Offset(size.width - margin - cornerSize, size.height - margin - cornerSize),
                                                        size = Size(cornerSize, cornerSize),
                                                        style = Stroke(width = strokeW * 1.5f)
                                                    )
                                                }
                                                .padding(horizontal = 24.dp, vertical = 20.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(bottom = 8.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "سورة ${s.nameAr}",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF00FF87)
                                                    )
                                                    
                                                    val displayPageNum = when (s.number) {
                                                        1 -> 2
                                                        2 -> pageIndex + 3
                                                        else -> pageIndex + (s.number * 4) + 1
                                                    }
                                                    Text(
                                                        text = displayPageNum.toString(),
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFC5A059)
                                                    )
                                                    
                                                    val subText = if (s.type.contains("meccan", ignoreCase = true)) "مكيّة" else "مدنيّة"
                                                    Text(
                                                        text = if (s.number == 2 && pageIndex == 0) "الم" else subText,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF00FF87)
                                                    )
                                                }
                                                
                                                Divider(
                                                    color = Color(0xFFC5A059).copy(alpha = 0.5f),
                                                    thickness = 1.dp,
                                                    modifier = Modifier.padding(bottom = 12.dp)
                                                )
                                                
                                                if (pageIndex == 0) {
                                                    Card(
                                                        modifier = Modifier
                                                            .padding(bottom = 12.dp)
                                                            .widthIn(max = 240.dp),
                                                        shape = RoundedCornerShape(8.dp),
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = Color(0xFF1B4D22)
                                                        ),
                                                        border = BorderStroke(1.dp, Color(0xFFC5A059))
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(vertical = 6.dp, horizontal = 16.dp),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "سُورَةُ ${s.nameAr}",
                                                                fontSize = 16.sp,
                                                                fontWeight = FontWeight.ExtraBold,
                                                                color = Color(0xFFFFD700)
                                                            )
                                                        }
                                                    }
                                                    
                                                    if (s.number != 9) {
                                                        Text(
                                                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                                            fontSize = 20.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF00FF87),
                                                            textAlign = TextAlign.Center,
                                                            modifier = Modifier.padding(bottom = 16.dp)
                                                        )
                                                    }
                                                }
                                                
                                                val annotatedString = buildAnnotatedString {
                                                    pageVerses.forEach { verse ->
                                                        val startOffset = length
                                                        append(verse.textAr)
                                                        
                                                        val markerText = " ﴿${verse.number}﴾ "
                                                        val markerStart = length
                                                        append(markerText)
                                                        val markerEnd = length
                                                        
                                                        addStyle(
                                                            style = SpanStyle(
                                                                color = Color(0xFFC5A059),
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = (arabicFontSize * 0.8).sp
                                                            ),
                                                            start = markerStart,
                                                            end = markerEnd
                                                        )
                                                        
                                                        addStringAnnotation(
                                                            tag = "verse",
                                                            annotation = verse.number.toString(),
                                                            start = startOffset,
                                                            end = length
                                                        )
                                                        
                                                        val isSelected = selectedVerseNum == verse.number
                                                        val isPlaying = playingVerseKey == "${s.number}:${verse.number}"
                                                        val isLastSeen = bookmarkedSurahNumber == s.number && bookmarkedVerseNumber == verse.number
                                                        
                                                        if (isPlaying || isSelected) {
                                                            addStyle(
                                                                style = SpanStyle(
                                                                    background = Color(0x3300FF87),
                                                                    color = Color(0xFFFFD700)
                                                                ),
                                                                start = startOffset,
                                                                end = length
                                                            )
                                                        } else if (isLastSeen) {
                                                            addStyle(
                                                                style = SpanStyle(
                                                                    background = Color(0x66FF9800), // Distinct amber highlight for Last Seen
                                                                    color = Color(0xFFFFD700)
                                                                ),
                                                                start = startOffset,
                                                                end = length
                                                            )
                                                        }
                                                    }
                                                }
                                                
                                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                                    ClickableText(
                                                        text = annotatedString,
                                                        style = TextStyle(
                                                            textAlign = TextAlign.Center,
                                                            fontSize = arabicFontSize.sp,
                                                            color = Color.White,
                                                            lineHeight = (arabicFontSize * 1.7).sp,
                                                            textDirection = TextDirection.Rtl
                                                        ),
                                                        modifier = Modifier.fillMaxWidth(),
                                                        onClick = { offset ->
                                                            annotatedString.getStringAnnotations(tag = "verse", start = offset, end = offset)
                                                                .firstOrNull()?.let { annotation ->
                                                                    val clickedVerseNum = annotation.item.toIntOrNull()
                                                                    if (clickedVerseNum != null) {
                                                                        selectedVerseNum = clickedVerseNum
                                                                    }
                                                                }
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                item {
                                    Spacer(modifier = Modifier.height(130.dp))
                                }
                            }

                            // FLOATING TRANSLATION PANEL AT THE BOTTOM OF THE SCREEN
                            val currentSelectedVerse = if (selectedVerseNum != null) loadedVerses?.find { it.number == selectedVerseNum } else null
                                
                            if (currentSelectedVerse != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .align(Alignment.BottomCenter)
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(12.dp, RoundedCornerShape(20.dp)),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xE61E2125)
                                        ),
                                        border = BorderStroke(1.dp, Color(0xFFC5A059).copy(alpha = 0.5f))
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Verse ${currentSelectedVerse.number} of ${s.versesCount} 📍",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFFD700)
                                                )
                                                
                                                IconButton(
                                                    onClick = {
                                                        selectedVerseNum = null
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Close Panel",
                                                        tint = Color.LightGray,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                            
                                            Spacer(modifier = Modifier.height(4.dp))
                                            
                                            if (currentSelectedVerse.translation.isNotBlank()) {
                                                Text(
                                                    text = currentSelectedVerse.translation,
                                                    fontSize = 12.sp,
                                                    color = Color.White,
                                                    lineHeight = 16.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                            
                                            if (currentSelectedVerse.transliteration.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = currentSelectedVerse.transliteration,
                                                    fontSize = 10.sp,
                                                    color = Color.LightGray,
                                                    fontStyle = FontStyle.Italic
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(14.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                val isThisLastSeen = bookmarkedSurahNumber == s.number && bookmarkedVerseNumber == currentSelectedVerse.number
                                                
                                                // Option 1: Mark as Last Seen
                                                Button(
                                                    onClick = {
                                                        bookmarkedSurahNumber = s.number
                                                        bookmarkedVerseNumber = currentSelectedVerse.number
                                                        sharedPrefs.edit()
                                                            .putInt(initialSurahKey, s.number)
                                                            .putInt(initialVerseKey, currentSelectedVerse.number)
                                                            .apply()
                                                        Toast.makeText(context, "Verse ${currentSelectedVerse.number} marked as Last Seen! 📍", Toast.LENGTH_SHORT).show()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (isThisLastSeen) Color(0xFF2E7D32) else Color.White,
                                                        contentColor = if (isThisLastSeen) Color.White else Color.Black
                                                    ),
                                                    shape = RoundedCornerShape(12.dp),
                                                    border = if (isThisLastSeen) null else BorderStroke(1.dp, Color.LightGray),
                                                    modifier = Modifier.weight(1f).height(46.dp),
                                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isThisLastSeen) Icons.Default.Check else Icons.Default.Star,
                                                        contentDescription = null,
                                                        tint = if (isThisLastSeen) Color.White else Color.Black,
                                                        modifier = Modifier.size(16.dp).padding(end = 4.dp)
                                                    )
                                                    Text(
                                                        text = if (isThisLastSeen) "Last Seen Marked" else "Mark as Last Seen",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                
                                                // Option 2: Play audio
                                                val isPlayingThis = playingVerseKey == "${s.number}:${currentSelectedVerse.number}"
                                                Button(
                                                    onClick = {
                                                        playAudioForVerse(s.number, currentSelectedVerse.number)
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (isPlayingThis) Color(0xFFC62828) else Color.White,
                                                        contentColor = if (isPlayingThis) Color.White else Color.Black
                                                    ),
                                                    shape = RoundedCornerShape(12.dp),
                                                    border = if (isPlayingThis) null else BorderStroke(1.dp, Color.LightGray),
                                                    modifier = Modifier.weight(1f).height(46.dp),
                                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isPlayingThis) Icons.Default.Close else Icons.Default.PlayArrow,
                                                        contentDescription = null,
                                                        tint = if (isPlayingThis) Color.White else Color.Black,
                                                        modifier = Modifier.size(16.dp).padding(end = 4.dp)
                                                    )
                                                    Text(
                                                        text = if (isPlayingThis) "Stop audio" else "Play audio",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Screen 2 Translucent Glassmorphic Floating Bottom Bar for 15-line Mode (inside BoxScope)
                    if (is16LineMode && loadedVerses != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .align(Alignment.BottomEnd),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .shadow(8.dp, RoundedCornerShape(26.dp)),
                            shape = RoundedCornerShape(26.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xDF1A3A5F) // Translucent deep navy blue
                            ),
                            border = BorderStroke(1.dp, Color(0xFF0288D1).copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Arabic Quran",
                                    color = Color(0xFF00E676),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 12.dp)
                                )

                                // Interactive Text scale buttons (- size +)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IconButton(
                                        onClick = { if (arabicFontSize > 18f) arabicFontSize -= 2f },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Text("-", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    }
                                    
                                    Text(
                                        text = "${arabicFontSize.toInt()} pt", 
                                        color = Color.White, 
                                        fontSize = 12.sp, 
                                        fontWeight = FontWeight.Bold
                                    )

                                    IconButton(
                                        onClick = { if (arabicFontSize < 44f) arabicFontSize += 2f },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Text("+", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Red Circular Dismiss Button
                                Box(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(32.dp)
                                        .background(Color.Red, CircleShape)
                                        .clickable { activeSurah = null }, // Action closes reader view
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Close",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                } // Closes outer Box for verses
            }
        }

        // Action Modal Dialogs (Beautifully customized with NO dead-ends)
        if (activeDialogType != null) {
            when (activeDialogType) {
                "do_dont" -> {
                    AlertDialog(
                        onDismissRequest = { activeDialogType = null },
                        confirmButton = {
                            Button(
                                onClick = { activeDialogType = null },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Text("Acknowledge", color = Color.White)
                            }
                        },
                        title = {
                            Text("Quran Recitation Etiquette • آداب التلاوة", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
                        },
                        text = {
                            Column(
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                              ) {
                                Text("Do's (Recommended Guidelines):", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 13.sp)
                                Text("✅ Perform Wudu (purification) before touching the Holy Book.", fontSize = 11.sp, color = SlateGrey)
                                Text("✅ Listen silently and with absolute respect when recitation is active.", fontSize = 11.sp, color = SlateGrey)
                                Text("✅ Pronounce letters slowly adhering to beautiful Tajweed guidelines.", fontSize = 11.sp, color = SlateGrey)
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Don'ts (Things to Avoid):", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 13.sp)
                                Text("❌ Avoid laughing, chatting, or playing games during active recitation.", fontSize = 11.sp, color = SlateGrey)
                                Text("❌ Do not rush the pronunciation of these sacred lines.", fontSize = 11.sp, color = SlateGrey)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = Color.White
                    )
                }
                "bookmarks" -> {
                    AlertDialog(
                        onDismissRequest = { activeDialogType = null },
                        confirmButton = {
                            TextButton(onClick = { activeDialogType = null }) {
                                Text("Close", color = DeepNavy)
                            }
                        },
                        title = {
                            Text("Your Saved Locations 📍", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
                        },
                        text = {
                            val bSNum = sharedPrefs.getInt(initialSurahKey, -1)
                            val bVNum = sharedPrefs.getInt(initialVerseKey, -1)
                            
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                if (bSNum != -1) {
                                    val bName = surahs.find { it.number == bSNum }?.nameEn
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val target = surahs.find { it.number == bSNum }
                                                if (target != null) {
                                                    activeSurah = target
                                                    activeDialogType = null
                                                }
                                            },
                                        colors = CardDefaults.cardColors(containerColor = LightBlueGrey)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("Quran Session Progress", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepNavy)
                                                Text("Surah: $bName • Verse: $bVNum", fontSize = 11.sp, color = SlateGrey)
                                            }
                                            Icon(Icons.Default.PlayArrow, contentDescription = "", tint = DeepNavy)
                                        }
                                    }
                                } else {
                                    Text(
                                        "No saved reading progress found yet. Bookmark your progress using ‘Mark Progress’ inside any Surah!",
                                        fontSize = 11.sp,
                                        color = SlateGrey
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = Color.White
                    )
                }
                "notes" -> {
                    var writtenNote by remember {
                        mutableStateOf(sharedPrefs.getString("saved_quran_note", "") ?: "")
                    }
                    AlertDialog(
                        onDismissRequest = { activeDialogType = null },
                        confirmButton = {
                            Button(
                                onClick = {
                                    sharedPrefs.edit().putString("saved_quran_note", writtenNote).apply()
                                    activeDialogType = null
                                    onEarnRewards(3, 8, "Saved a personal Quran note reflection!")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy)
                            ) {
                                Text("Save Note 💾", color = Color.White)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { activeDialogType = null }) {
                                Text("Cancel", color = SlateGrey)
                            }
                        },
                        title = {
                            Text("My Reflection Notes 📝", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Jot down any thoughts, memorization goals, or beautiful feelings experienced during your studies:", fontSize = 11.sp, color = SlateGrey)
                                OutlinedTextField(
                                    value = writtenNote,
                                    onValueChange = { writtenNote = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(115.dp),
                                    placeholder = { Text("Write something beautiful...", fontSize = 12.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = DeepNavy,
                                        unfocusedBorderColor = BorderGrey
                                    )
                                )
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = Color.White
                    )
                }
                "faq" -> {
                    AlertDialog(
                        onDismissRequest = { activeDialogType = null },
                        confirmButton = {
                            Button(
                                onClick = { activeDialogType = null },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy)
                            ) {
                                Text("Perfect", color = Color.White)
                            }
                        },
                        title = {
                            Text("15 Line Quran FAQ 🧠", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
                        },
                        text = {
                            Column(
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("Q: What is a 15 Line Quran?", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepNavy)
                                Text("A: It is the traditional layout used by Huffaz (memorizers) worldwide. Each printed page contains exactly 15 horizontal line segments making memorization landmarks easy to recognize.", fontSize = 11.sp, color = SlateGrey)
                                
                                Text("Q: How can I earn Honey Stars?", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepNavy)
                                Text("A: Tapping the bookmark progress flag or saving notes records your genuine effort. Our app automatically rewards you with Honey stars to motivate your recitation consistency!", fontSize = 11.sp, color = SlateGrey)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = Color.White
                    )
                }
            }
        }
    }
}

// Data holder classes for Parah categories
data class ParahItem(
    val number: Int,
    val nameEn: String,
    val nameAr: String,
    val startSurah: Int,
    val startVerse: Int
)

// Full 114 Surah Metadata Catalog Parser
fun getQuran114Surahs(): List<SurahHeader> {
    val list = listOf(
        "1|Al-Fatihah|الفاتحة|The Opening|7|Meccan",
        "2|Al-Baqarah|البقرة|The Cow|286|Medinan",
        "3|Ali 'Imran|آل عمران|Family of Imran|200|Medinan",
        "4|An-Nisa|النساء|The Women|176|Medinan",
        "5|Al-Ma'idah|المائدة|The Table Spread|120|Medinan",
        "6|Al-An'am|الأنعام|The Cattle|165|Meccan",
        "7|Al-A'raf|الأعراف|The Heights|206|Meccan",
        "8|Al-Anfal|الأنفال|The Spoils of War|75|Medinan",
        "9|At-Tawbah|التوبة|The Repentance|129|Medinan",
        "10|Yunus|يونس|Jonah|109|Meccan",
        "11|Hud|هود|Hud|123|Meccan",
        "12|Yusuf|يوسف|Joseph|111|Meccan",
        "13|Ar-Ra'd|الرعد|The Thunder|43|Medinan",
        "14|Ibrahim|ابراهيم|Abraham|52|Meccan",
        "15|Al-Hijr|الحجر|The Rocky Tract|99|Meccan",
        "16|An-Nahl|النحل|The Bee|128|Meccan",
        "17|Al-Isra|الإسراء|The Night Journey|111|Meccan",
        "18|Al-Kahf|الكهف|The Cave|110|Meccan",
        "19|Maryam|مريم|Mary|98|Meccan",
        "20|Taha|طه|Ta-Ha|135|Meccan",
        "21|Al-Anbya|الأنبياء|The Prophets|112|Meccan",
        "22|Al-Hajj|الحج|The Pilgrimage|78|Medinan",
        "23|Al-Mu'minun|المؤمنون|The Believers|118|Meccan",
        "24|An-Nur|النور|The Light|64|Medinan",
        "25|Al-Furqan|الفرقان|The Criterion|77|Meccan",
        "26|Ash-Shu'ara|الشعراء|The Poets|227|Meccan",
        "27|An-Naml|النمل|The Ant|93|Meccan",
        "28|Al-Qasas|القصص|The Stories|88|Meccan",
        "29|Al-Ankabut|العنكبوت|The Spider|69|Meccan",
        "30|Ar-Rum|الروم|The Romans|60|Meccan",
        "31|Luqman|لقمان|Luqman|34|Meccan",
        "32|As-Sajdah|السجدة|The Prostration|30|Meccan",
        "33|Al-Ahzab|الأحزاب|The Combined Forces|73|Medinan",
        "34|Saba|سبأ|Sheba|54|Meccan",
        "35|Fatir|فاطر|The Originator|45|Meccan",
        "36|Yasin|يس|Ya-Sin|83|Meccan",
        "37|As-Saffat|الصافات|Those Who Set the Ranks|182|Meccan",
        "38|Sad|ص|Sad|88|Meccan",
        "39|Az-Zumar|الزمر|The Troops|75|Meccan",
        "40|Ghafir|غافر|The Forgiver|85|Meccan",
        "41|Fussilat|فصلت|Explained in Detail|54|Meccan",
        "42|Ash-Shura|الشورى|The Consultation|53|Meccan",
        "43|Az-Zukhruf|الزخرف|The Gold Adornments|89|Meccan",
        "44|Ad-Dukhan|الدخان|The Smoke|59|Meccan",
        "45|Al-Jathiyah|الجاثية|The Crouching|37|Meccan",
        "46|Al-Ahqaf|الأحقاف|The Wind-Curved Sandhills|35|Meccan",
        "47|Muhammad|محمد|Muhammad|38|Medinan",
        "48|Al-Fath|الفتح|The Victory|29|Medinan",
        "49|Al-Hujurat|الحجرات|The Dwellings|18|Medinan",
        "50|Qaf|ق|Qaf|45|Meccan",
        "51|Adh-Dhariyat|الذاريات|The Winnowing Winds|60|Meccan",
        "52|At-Tur|الطور|The Mount|49|Meccan",
        "53|An-Najm|النجم|The Star|62|Meccan",
        "54|Al-Qamar|القمر|The Moon|55|Meccan",
        "55|Ar-Rahman|الرحمن|The Beneficent|78|Medinan",
        "56|Al-Waqi'ah|الواقعة|The Inevitable|96|Meccan",
        "57|Al-Hadid|الحديد|The Iron|29|Medinan",
        "58|Al-Mujadila|المجادلة|The Pleading Woman|22|Medinan",
        "59|Al-Hashr|الحشر|The Exile|24|Medinan",
        "60|Al-Mumtahanah|الممتحنة|She That Is to Be Examined|13|Medinan",
        "61|As-Saff|الصف|The Ranks|14|Medinan",
        "62|Al-Jumu'ah|الجمعة|The Congregation, Friday|11|Medinan",
        "63|Al-Munafiqun|المنافقون|The Hypocrites|11|Medinan",
        "64|At-Taghabun|التغابن|The Mutual Disillusion|18|Medinan",
        "65|At-Talaq|الطلاق|The Divorce|12|Medinan",
        "66|At-Tahrim|التحريم|The Prohibition|12|Medinan",
        "67|Al-Mulk|الملك|The Sovereignty|30|Meccan",
        "68|Al-Qalam|القلم|The Pen|52|Meccan",
        "69|Al-Haqqah|الحاقة|The Reality|52|Meccan",
        "70|Al-Ma'arij|المعارج|The Ascending Stairways|44|Meccan",
        "71|Nuh|نوح|Noah|28|Meccan",
        "72|Al-Jinn|الجن|The Jinn|28|Meccan",
        "73|Al-Muzzammil|المزمل|The Enshrouded One|20|Meccan",
        "74|Al-Muddaththir|المدثر|The Cloaked One|56|Meccan",
        "75|Al-Qiyamah|القيامة|The Resurrection|40|Meccan",
        "76|Al-Insan|الانسان|The Man|31|Medinan",
        "77|Al-Mursalat|المرسلات|The Emissaries|50|Meccan",
        "78|An-Naba|النبأ|The Tidings|40|Meccan",
        "79|An-Nazi'at|النازعات|Those Who Drag Forth|46|Meccan",
        "80|Abasa|عبس|He Frowned|42|Meccan",
        "81|At-Takwir|التكوير|The Overthrowing|29|Meccan",
        "82|Al-Infitar|الانفطار|The Cleaving|19|Meccan",
        "83|Al-Mutaffifin|المطففين|The Defrauding|36|Meccan",
        "84|Al-Inshiqaq|الانشقاق|The Sundering|25|Meccan",
        "85|Al-Buruj|البروج|The Mansions of the Stars|22|Meccan",
        "86|At-Tariq|الطارق|The Night-Comer|17|Meccan",
        "87|Al-A'la|الأعلى|The Most High|19|Meccan",
        "88|Al-Ghashiyah|الغاشية|The Overwhelming|26|Meccan",
        "89|Al-Fajr|الفجر|The Dawn|30|Meccan",
        "90|Al-Balad|البلد|The City|20|Meccan",
        "91|Ash-Shams|الشمس|The Sun|15|Meccan",
        "92|Al-Layl|الليل|The Night|21|Meccan",
        "93|Ad-Duha|الضحى|The Morning Hours|11|Meccan",
        "94|Ash-Sharh|الشرح|The Relief|8|Meccan",
        "95|At-Tin|التين|The Fig|8|Meccan",
        "96|Al-Alaq|العلق|The Clot|19|Meccan",
        "97|Al-Qadr|القدر|The Power, Fate|5|Meccan",
        "98|Al-Bayyinah|البينة|The Clear Proof|8|Medinan",
        "99|Az-Zalzalah|الزلزلة|The Earthquake|8|Medinan",
        "100|Al-Adiyat|العاديات|The Courser|11|Meccan",
        "101|Al-Qari'ah|القارعة|The Calamity|11|Meccan",
        "102|At-Takathur|التكاثر|The Rivalry in World Increase|8|Meccan",
        "103|Al-Asr|العصر|The Declining Day, Epoch|3|Meccan",
        "104|Al-Humazah|الهمزة|The Traducer|9|Meccan",
        "105|Al-Fil|الفيل|The Elephant|5|Meccan",
        "106|Quraysh|قريش|Quraysh|4|Meccan",
        "107|Al-Ma'un|الماعون|The Small Kindness|7|Meccan",
        "108|Al-Kawthar|الكوثر|The Abundant Plenty|3|Meccan",
        "109|Al-Kafirun|الكافرون|The Disbelievers|6|Meccan",
        "110|An-Nasr|النصر|The Divine Support|3|Medinan",
        "111|Al-Masad|المسد|The Palm Fiber|5|Meccan",
        "112|Al-Ikhlas|الإخلاص|The Sincerity|4|Meccan",
        "113|Al-Falaq|الفلق|The Daybreak|5|Meccan",
        "114|An-Nas|الناس|Mankind|6|Meccan"
    )
    return list.map { line ->
        val parts = line.split("|")
        SurahHeader(
            number = parts[0].toInt(),
            nameEn = parts[1],
            nameAr = parts[2],
            meaning = parts[3],
            versesCount = parts[4].toInt(),
            type = parts[5]
        )
    }
}

// 6 local pre-cached high frequency Surahs to support instant offline reading
fun getOfflineSurah(surahNumber: Int): List<SurahVerse>? {
    return when (surahNumber) {
        1 -> listOf(
            SurahVerse(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "", "In the name of Allah, the Entirely Merciful, the Especially Merciful."),
            SurahVerse(2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "", "[All] praise is [due] to Allah, Lord of the worlds -"),
            SurahVerse(3, "الرَّحْمَٰنِ الرَّحِيمِ", "", "The Entirely Merciful, the Especially Merciful,"),
            SurahVerse(4, "مَالِكِ يَوْمِ الدِّينِ", "", "Sovereign of the Day of Recompense."),
            SurahVerse(5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "", "It is You we worship and You we ask for help."),
            SurahVerse(6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "", "Guide us to the straight path -"),
            SurahVerse(7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "", "The path of those upon whom You have bestowed favor, not of those who have evoked anger, nor of those who are astray.")
        )
        103 -> listOf(
            SurahVerse(1, "وَالْعَصْرِ", "", "By time,"),
            SurahVerse(2, "إِنَّ الْإِنْسَانَ لَفِي خُسْرٍ", "", "Indeed, mankind is in loss,"),
            SurahVerse(3, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ", "", "Except for those who have believed and done righteous deeds and advised each other to truth and advised each other to patience.")
        )
        108 -> listOf(
            SurahVerse(1, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "", "Indeed, We have granted you, [O Muhammad], al-Kawthar."),
            SurahVerse(2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "", "So pray to your Lord and sacrifice [to Him alone]."),
            SurahVerse(3, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "", "Indeed, your enemy is the one cut off.")
        )
        112 -> listOf(
            SurahVerse(1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "", "Say, \"He is Allah, [who is] One,"),
            SurahVerse(2, "اللَّهُ الصَّمَدُ", "", "Allah, the Eternal Refuge."),
            SurahVerse(3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "", "He neither begets nor is born,"),
            SurahVerse(4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "", "Nor is there to Him any equivalent.\"")
        )
        113 -> listOf(
            SurahVerse(1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "", "Say, \"I seek refuge in the Lord of daybreak"),
            SurahVerse(2, "مِن شَرِّ مَا خَلَقَ", "", "From the evil of that which He created"),
            SurahVerse(3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "", "And from the evil of darkness when it settles"),
            SurahVerse(4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "", "And from the evil of the blowers in knots"),
            SurahVerse(5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "", "And from the evil of an envier when he envies.\"")
        )
        114 -> listOf(
            SurahVerse(1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "", "Say, \"I seek refuge in the Lord of mankind,"),
            SurahVerse(2, "مَلِكِ النَّاسِ", "", "The Sovereign of mankind,"),
            SurahVerse(3, "إِلَهِ النَّاسِ", "", "The God of mankind,"),
            SurahVerse(4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "", "From the evil of the retreating whisperer -"),
            SurahVerse(5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "", "Who whispers [evil] into the breasts of mankind -"),
            SurahVerse(6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "", "From among the jinn and mankind.\"")
        )
        else -> null
    }
}

fun saveSurahOffline(context: Context, surahNumber: Int, verses: List<SurahVerse>) {
    try {
        val array = JSONArray()
        for (v in verses) {
            val obj = JSONObject()
            obj.put("number", v.number)
            obj.put("textAr", v.textAr)
            obj.put("transliteration", v.transliteration)
            obj.put("translation", v.translation)
            array.put(obj)
        }
        context.openFileOutput("surah_${surahNumber}.json", Context.MODE_PRIVATE).use {
            it.write(array.toString().toByteArray())
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun deleteSurahOffline(context: Context, surahNumber: Int) {
    try {
        context.deleteFile("surah_${surahNumber}.json")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun getOfflineSurah(context: Context, surahNumber: Int): List<SurahVerse>? {
    val file = context.getFileStreamPath("surah_${surahNumber}.json")
    if (file != null && file.exists()) {
        try {
            context.openFileInput("surah_${surahNumber}.json").use { stream ->
                val size = stream.available()
                val buffer = ByteArray(size)
                stream.read(buffer)
                val jsonString = String(buffer)
                val array = JSONArray(jsonString)
                val list = mutableListOf<SurahVerse>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        SurahVerse(
                            number = obj.optInt("number", i + 1),
                            textAr = obj.optString("textAr", ""),
                            transliteration = obj.optString("transliteration", ""),
                            translation = obj.optString("translation", "")
                        )
                    )
                }
                return list
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return getOfflineSurah(surahNumber)
}

// Fetch complete Surah network helper from api.alquran.cloud for robust offline-ready layout
fun fetchSurahOnline(surahNumber: Int, onResult: (List<SurahVerse>?, String?) -> Unit) {
    val client = OkHttpClient()
    val url = "https://api.alquran.com/api/v4/verses/by_chapter/$surahNumber?language=en&words=false&translations=131&fields=text_uthmani"
    val fallbackUrl = "https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,en.sahih"
    val request = Request.Builder().url(fallbackUrl).build()
    
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            // If primary fails, notify with failure message
            onResult(null, e.localizedMessage ?: "Connection timed out. Check your local internet connectivity.")
        }
        
        override fun onResponse(call: Call, response: Response) {
            response.use { res ->
                if (!res.isSuccessful) {
                    onResult(null, "The Quran API returned an error: Code ${res.code}")
                    return
                }
                
                try {
                    val body = res.body?.string() ?: ""
                    val json = JSONObject(body)
                    val dataArray = json.getJSONArray("data")
                    
                    val arabicEdition = dataArray.getJSONObject(0)
                    val englishEdition = dataArray.getJSONObject(1)
                    
                    val arabicAyahs = arabicEdition.getJSONArray("ayahs")
                    val englishAyahs = englishEdition.getJSONArray("ayahs")
                    
                    val list = mutableListOf<SurahVerse>()
                    val total = arabicAyahs.length()
                    
                    for (i in 0 until total) {
                        val arObj = arabicAyahs.getJSONObject(i)
                        val enObj = englishAyahs.getJSONObject(i)
                        
                        val verseNumber = arObj.getInt("numberInSurah")
                        val textAr = arObj.getString("text")
                        val textEn = enObj.getString("text")
                            .replace("<sup.*?>.*?</sup>".toRegex(), "") // strip translation footnotes
                            .replace("<br.*?>".toRegex(), "")
                        
                        list.add(
                            SurahVerse(
                                number = verseNumber,
                                textAr = textAr,
                                transliteration = "",
                                translation = textEn
                            )
                        )
                    }
                    onResult(list, null)
                } catch (e: Exception) {
                    onResult(null, "Error parsing scriptures: ${e.localizedMessage}")
                }
            }
        }
    })
}

