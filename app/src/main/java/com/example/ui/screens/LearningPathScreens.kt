package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.data.ChildProfile
import com.example.sanitizeSpeechText
import android.speech.tts.TextToSpeech
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.AnnotatedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningPathScreens(
    profile: ChildProfile,
    topicGroup: String, // "Alphabet" or "Vocabulary"
    onBackPressed: () -> Unit,
    onLevelSelected: (String) -> Unit // returns subtopic name
) {
    val scrollState = rememberScrollState()
    val isArabic = profile.currentLanguage == "Arabic"

    val titleText = if (isArabic) {
        if (topicGroup == "Alphabet") "مسار الحروف العربية 🇸🇦" else "مسار الكلمات العربية 🍎"
    } else {
        if (topicGroup == "Alphabet") "English Alphabet Path 🇬🇧" else "English Word Path 🍎"
    }

    // Define 4 progressive milestones
    val milestones = rememberByTopicAndLang(topicGroup, isArabic, profile.level)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titleText, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723)) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val lessonAge = profile.age.coerceIn(3, 8)
            val isAgeThreeAlphabet = lessonAge == 3 && topicGroup == "Alphabet"
            val isAgeFourAlphabet = lessonAge == 4 && topicGroup == "Alphabet"
            val isAgeFiveAlphabet = lessonAge == 5 && topicGroup == "Alphabet"
            val isAgeSixAlphabet = lessonAge == 6 && topicGroup == "Alphabet"
            val isAgeSevenAlphabet = lessonAge == 7 && topicGroup == "Alphabet"
            val isAgeEightAlphabet = lessonAge >= 8 && topicGroup == "Alphabet"
            if (isAgeThreeAlphabet) {
                AgeThreeAlphabetBoard(
                    isArabic = isArabic,
                    onStartAdventure = {
                        onLevelSelected("Alphabet_intro")
                    }
                )
            } else if (isAgeFourAlphabet) {
                AgeFourAlphabetBoard(
                    isArabic = isArabic,
                    onStartAdventure = {
                        onLevelSelected("Alphabet_intro")
                    }
                )
            } else if (isAgeFiveAlphabet) {
                AgeFiveAlphabetBoard(
                    isArabic = isArabic,
                    onStartAdventure = {
                        onLevelSelected("Alphabet_intro")
                    }
                )
            } else if (isAgeSixAlphabet) {
                AgeSixAlphabetBoard(
                    isArabic = isArabic,
                    onStartAdventure = {
                        onLevelSelected("Alphabet_intro")
                    }
                )
            } else if (isAgeSevenAlphabet) {
                AgeSevenAlphabetBoard(
                    isArabic = isArabic,
                    onStartAdventure = {
                        onLevelSelected("Alphabet_intro")
                    }
                )
            } else if (isAgeEightAlphabet) {
                AgeEightAlphabetBoard(
                    isArabic = isArabic,
                    onStartAdventure = {
                        onLevelSelected("Alphabet_intro")
                    }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(24.dp)
                        .testTag("learning_path_screen"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                // Intro Mascot Tip Block
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isArabic) {
                                "مستعد يا بطل؟ لنبدأ من المستوى الأول ونجمع العسل! 🍯"
                            } else {
                                "Ready, Champion? Let's start from level 1 and collect raw honey! 🍯"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5D4037),
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Scrollable roadmap nodes representing Duolingo visual flows
                milestones.forEachIndexed { index, milestone ->
                    val isUnlocked = true
                    
                    // Road connector line
                    if (index > 0) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            drawLine(
                                color = if (isUnlocked) Color(0xFFFFB300) else Color(0xFFE0E0E0),
                                start = androidx.compose.ui.geometry.Offset(size.width / 2, 0f),
                                end = androidx.compose.ui.geometry.Offset(size.width / 2, size.height),
                                strokeWidth = 8f,
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        }
                    }

                    // Milestone card visual representation representation
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .shadow(if (isUnlocked) (6.dp) else 1.dp, RoundedCornerShape(24.dp))
                            .clickable(enabled = isUnlocked) {
                                onLevelSelected(milestone.subtopicId)
                            }
                            .testTag("milestone_card_${milestone.subtopicId}"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnlocked) Color.White else Color(0xFFF5F5F5)
                        ),
                        border = BorderStroke(
                            width = if (isUnlocked) 2.dp else 1.dp,
                            color = if (isUnlocked) Color(0xFFFFB300) else Color(0xFFE0E0E0)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Stage Badge Number
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .shadow(2.dp, CircleShape)
                                    .background(
                                        if (isUnlocked) Color(0xFFFFEE58) else Color(0xFFBDBDBD),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isUnlocked) {
                                    Text("🐝", fontSize = 28.sp)
                                } else {
                                    Text("🔒", fontSize = 18.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isArabic) "المرحلة ${index + 1}" else "Stage ${index + 1}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF795548)
                                )
                                Text(
                                    text = milestone.title,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = if (isUnlocked) Color(0xFF3E2723) else Color(0xFF757575)
                                )
                                Text(
                                    text = milestone.description,
                                    fontSize = 11.sp,
                                    color = Color(0xFF8D6E63),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
            }
        }
    }
}

data class Milestone(
    val subtopicId: String,
    val title: String,
    val description: String
)

@Composable
fun rememberByTopicAndLang(topicGroup: String, isArabic: Boolean, level: Int): List<Milestone> {
    return remember(topicGroup, isArabic, level) {
        if (isArabic) {
            if (topicGroup == "Alphabet") {
                listOf(
                    Milestone("Alphabet_intro", "الألف والباء 📝", "تعلم نطق الحروف كـ أ، ب، ت مع تشكيل الفتحة"),
                    Milestone("Alphabet_matching", "مطابقة الحروف 🧩", "صل الحروف المتشابهة مع نطق Noor Bee المبهج"),
                    Milestone("Alphabet_listening", "نطق وتحليل 🎧", "استمع لنطق الحروف والرموز بحركات التشكيل"),
                    Milestone("Alphabet_quiz", "تحدي الأبطال 🎓", "اختبار مهارات الحروف لربح نجوم العسل الكبرى")
                )
            } else {
                when (level) {
                    2 -> listOf(
                        Milestone("Fruits_practice", "سفاري الفواكه 🍐", "تعلم أسماء كمثرى، كرز، ومانجو مع صور ممتعة"),
                        Milestone("Animals_practice", "الأصدقاء الحيوانات 🦁", "تعلم أصوات وأسماء أسد، أرنب، وثعلب"),
                        Milestone("Objects_practice", "مكتشف الأشياء 🌳", "تعلم أسماء شجرة، بيت، وحقيبة ونطقها الفصيح"),
                        Milestone("Fruits_quiz", "تحدي السافاري الشامل 🏆", "تحدي نطق ومطابقة كلمات السافاري لربح كؤوس الإنجاز")
                    )
                    3 -> listOf(
                        Milestone("Fruits_practice", "مصنع الفواكه 🍉", "تعلم تركيب كلمات بطيخ، فراولة، وأناناس لربح العسل"),
                        Milestone("Animals_practice", "باني الحيوانات 🐘", "تعلم تركيب أسماء فيل، زرافة، وقرد مع تهجئتها"),
                        Milestone("Objects_practice", "ورشة الأشياء ⏰", "تعلم كلمات ساعة، حاسوب، ومظلة لبناء حصيلة ممتازة"),
                        Milestone("Fruits_quiz", "تحدي البناء الأكبر 🏆", "اختبر مهارات تركيب الكلمات لتربح تاج العباقرة")
                    )
                    4 -> listOf(
                        Milestone("Fruits_practice", "صندوق الفواكه 🍇", "تعلم كلمات أفوكادو، أناناس، وفاكهة التنين"),
                        Milestone("Animals_practice", "صندوق الحيوانات 🦎", "تعلم أسماء حرباء، كانغر، وأخطبوط مع نطق مبهج"),
                        Milestone("Objects_practice", "صندوق الأشياء 🔭", "تعلم كلمات مرقاب، بوصلة، وحقيبة ظهر بدقة"),
                        Milestone("Fruits_quiz", "اختبار التهجئة الكبير 🏆", "تحدي تركيب وتهجئة الكلمات المعقدة لتربح نقاطاً عسلية")
                    )
                    5 -> listOf(
                        Milestone("Fruits_practice", "رابط الفواكه 🫒", "تعلم كلمات رمان، تين، وجوز الهند مع صور مبهجة"),
                        Milestone("Animals_practice", "رابط الحيوانات 🦅", "تعلم أسماء نسر، صقر، وحوت مع أصواتها الشيقة"),
                        Milestone("Objects_practice", "رابط الأشياء 🩺", "تعلم كلمات سماعة الطبيب، بوصلة، وميزان الحرارة"),
                        Milestone("Fruits_quiz", "تحدي الربط الشامل 🏆", "اختبر مهارات التعبير والربط بين الكلمات المعقدة")
                    )
                    6 -> listOf(
                        Milestone("Fruits_practice", "فواكه العباقرة 🌰", "تعلم كلمات منغوستين، كاكي، وأبو فروة بنطق سليم"),
                        Milestone("Animals_practice", "حيوانات العباقرة 🦑", "تعلم أسماء منقار البط، تنين كومودو، وحرباء بدقة"),
                        Milestone("Objects_practice", "أشياء العباقرة 📊", "تعلم كلمات مقياس الزلازل، جايروسكوب، ومقياس السرعة"),
                        Milestone("Fruits_quiz", "اختبار العباقرة الكلي 🏆", "تحدي الكلمات الفائقة والصعبة لتربح وسام التميز الكلي")
                    )
                    else -> listOf(
                        Milestone("Fruits_practice", "الفواكه اللذيذة 🍎", "تعلم أسماء تفاح وموز مع قراءتها وكتابتها"),
                        Milestone("Animals_practice", "الأصدقاء الحيوانات 🦁", "تعلم أصوات وأسماء أسد، نحلة، وقطة"),
                        Milestone("Objects_practice", "الأشياء حولي 🚗", "تعلم أسماء سيارة، قمر وشمس ونطقها الفصيح"),
                        Milestone("Fruits_quiz", "اختبار الكلمات الشامل 🏆", "تحدي نطق ومطابقة كلمات لربح كؤوس الإنجاز")
                    )
                }
            }
        } else {
            if (topicGroup == "Alphabet") {
                listOf(
                    Milestone("Alphabet_intro", "Letters A-B-C 📝", "Learn basic English letters phonics and pronunciation"),
                    Milestone("Alphabet_matching", "Letter Playroom 🧩", "Match words and letters creatively with Noor Bee assistant"),
                    Milestone("Alphabet_listening", "Listen & Discover 🎧", "Listen to correct phonetics and match them"),
                    Milestone("Alphabet_quiz", "Alphabet Master Quiz 🎓", "Challenge and lock in letters for maximum star points")
                )
            } else {
                when (level) {
                    2 -> listOf(
                        Milestone("Fruits_practice", "Fruit Safari 🍐", "Learn intermediate words like Pear, Cherry, Mango"),
                        Milestone("Animals_practice", "Kind Beasts 🦁", "Learn intermediate words like Lion, Fox, Rabbit, Bear"),
                        Milestone("Objects_practice", "Safari Objects 🌳", "Learn intermediate words like Tree, Window, Bag, Shoe"),
                        Milestone("Fruits_quiz", "Safari Word Quiz 🏆", "Comprehensive safari quiz to show off your intermediate vocabulary")
                    )
                    3 -> listOf(
                        Milestone("Fruits_practice", "Fruit Constructor 🍉", "Build advanced words like Watermelon, Strawberry, Pineapple"),
                        Milestone("Animals_practice", "Animal Constructor 🐘", "Assemble advanced animal words like Elephant, Giraffe, Monkey"),
                        Milestone("Objects_practice", "Object Workshop ⏰", "Construct everyday objects like Clock, Computer, Umbrella"),
                        Milestone("Fruits_quiz", "Master Builder Quiz 🏆", "Show off your building skills in this ultimate advanced word quiz")
                    )
                    4 -> listOf(
                        Milestone("Fruits_practice", "Sandbox Fruits 🍇", "Learn complex words like Grapefruit, Avocado, Dragonfruit"),
                        Milestone("Animals_practice", "Sandbox Animals 🦎", "Learn complex animals like Chameleon, Kangaroo, Octopus"),
                        Milestone("Objects_practice", "Sandbox Objects 🔭", "Learn complex objects like Telescope, Keyboard, Flashlight"),
                        Milestone("Fruits_quiz", "Spelling Sandbox Quiz 🏆", "Ultimate spelling puzzle challenge for expert level words")
                    )
                    5 -> listOf(
                        Milestone("Fruits_practice", "Fruit Connector 🫒", "Learn advanced vocabulary like Pomegranate, Fig, Coconut"),
                        Milestone("Animals_practice", "Animal Connector 🦅", "Learn advanced animals like Eagle, Koala, Whale, Falcon"),
                        Milestone("Objects_practice", "Object Connector 🩺", "Learn advanced objects like Stethoscope, Pendulum, Hourglass"),
                        Milestone("Fruits_quiz", "Sentence Connect Quiz 🏆", "Show off your phrasing and advanced vocabulary skills")
                    )
                    6 -> listOf(
                        Milestone("Fruits_practice", "Expert Fruits 🌰", "Learn elite words like Mangosteen, Rambutan, Chestnut"),
                        Milestone("Animals_practice", "Expert Animals 🦑", "Learn elite animals like Platypus, Komodo, Chameleon"),
                        Milestone("Objects_practice", "Expert Objects 📊", "Learn elite objects like Seismograph, Gyroscope, Speedometer"),
                        Milestone("Fruits_quiz", "Expert Word Lab Quiz 🏆", "The ultimate vocabulary test for the top of the leaderboards")
                    )
                    else -> listOf(
                        Milestone("Fruits_practice", "Delicious Fruits 🍎", "Learn word tags like Apple, Banana, Orange"),
                        Milestone("Animals_practice", "Kind Animals 🦁", "Learn word tags like Bee, Cat, Lion, Bird"),
                        Milestone("Objects_practice", "Common Objects 🚗", "Learn word tags like Car, Book, Sun, Moon"),
                        Milestone("Fruits_quiz", "Vocabulary Super Challenge 🏆", "Comprehensive quiz assessment representing your success")
                    )
                }
            }
        }
    }
}

data class PlayLetter(
    val char: String,
    val word: String,
    val icon: String
)

@Composable
fun AgeThreeAlphabetBoard(
    isArabic: Boolean,
    onStartAdventure: () -> Unit
) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }

    DisposableEffect(context, isArabic) {
        val instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
            }
        }
        tts = instance
        onDispose {
            instance.stop()
            instance.shutdown()
        }
    }

    val letters = remember(isArabic) {
        if (isArabic) {
            listOf(
                PlayLetter("أ", "الله", "☝️"),
                PlayLetter("ب", "بسم الله", "🍽️"),
                PlayLetter("ت", "تقوى", "💖"),
                PlayLetter("ث", "ثواب", "🏆"),
                PlayLetter("ج", "جنّة", "🌺"),
                PlayLetter("ح", "حجّ", "🕋"),
                PlayLetter("خ", "خير", "✨"),
                PlayLetter("د", "دعاء", "🤲"),
                PlayLetter("ذ", "ذكر", "📿"),
                PlayLetter("ر", "رمضان", "🌙"),
                PlayLetter("ز", "زكاة", "💰"),
                PlayLetter("س", "سجود", "🛐"),
                PlayLetter("ش", "شكر", "🙌"),
                PlayLetter("ص", "صلاة", "🕌"),
                PlayLetter("ض", "ضحى", "☀️"),
                PlayLetter("ط", "طهارة", "💧"),
                PlayLetter("ظ", "ظهر", "☀️"),
                PlayLetter("ع", "عدل", "⚖️"),
                PlayLetter("غ", "غفران", "🤲"),
                PlayLetter("ف", "فجر", "🌅"),
                PlayLetter("ق", "قرآن", "📗"),
                PlayLetter("ك", "كعبة", "🕋"),
                PlayLetter("ل", "ليلة القدر", "🌌"),
                PlayLetter("م", "مسجد", "🕌"),
                PlayLetter("ن", "نبي", "📖"),
                PlayLetter("هـ", "هداية", "🌟"),
                PlayLetter("و", "وضوء", "💧"),
                PlayLetter("ي", "يقين", "💛")
            )
        } else {
            listOf(
                PlayLetter("A", "Allah", "⭐"),
                PlayLetter("B", "Bismillah", "🍽️"),
                PlayLetter("C", "Charity", "🤝"),
                PlayLetter("D", "Dua", "🤲"),
                PlayLetter("E", "Eid", "🎉"),
                PlayLetter("F", "Fajr", "🌅"),
                PlayLetter("G", "Ghusl", "🚿"),
                PlayLetter("H", "Hajj", "🕋"),
                PlayLetter("I", "Iman", "💚"),
                PlayLetter("J", "Jannah", "🌺"),
                PlayLetter("K", "Kaaba", "🕋"),
                PlayLetter("L", "Laylatul Qadr", "🌙"),
                PlayLetter("M", "Masjid", "🕌"),
                PlayLetter("N", "Nabi", "📖"),
                PlayLetter("O", "Obedience", "🙏"),
                PlayLetter("P", "Prophet", "🌟"),
                PlayLetter("Q", "Quran", "📗"),
                PlayLetter("R", "Ramadan", "🌙"),
                PlayLetter("S", "Salah", "🛐"),
                PlayLetter("T", "Tawheed", "☝️"),
                PlayLetter("U", "Ummah", "🤝"),
                PlayLetter("V", "Virtue", "💖"),
                PlayLetter("W", "Wudu", "💧"),
                PlayLetter("X", "eXcellence", "✨"),
                PlayLetter("Y", "Yaqeen", "💛"),
                PlayLetter("Z", "Zakat", "💰")
            )
        }
    }

    var selectedLetter by remember(isArabic) { mutableStateOf(letters.first()) }

    fun speakLetter(letter: PlayLetter) {
        tts?.let { t ->
            val lang = if (isArabic) Locale("ar", "SA") else Locale("en", "IN")
            val result = t.setLanguage(lang)
            if (isArabic && (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)) {
                t.language = Locale("ar")
            } else {
                t.language = lang
            }
            val textToSpeak = if (isArabic) {
                "${letter.char} ... ${letter.word}"
            } else {
                "${letter.char} ... for ... ${letter.word}"
            }
            t.speak(sanitizeSpeechText(textToSpeak), TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    LaunchedEffect(selectedLetter, ttsReady) {
        if (ttsReady) {
            speakLetter(selectedLetter)
        }
    }

    val scrollState = rememberScrollState()

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FIXED ON TOP: Rectangular compact preview card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, Color(0xFF2E7D32))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = selectedLetter.icon,
                            fontSize = 32.sp
                        )
                        Column {
                            Text(
                                text = if (isArabic) selectedLetter.char else "${selectedLetter.char.uppercase()} ${selectedLetter.char.lowercase()}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                text = selectedLetter.word,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFB300)
                            )
                        }
                    }
                    Button(
                        onClick = { speakLetter(selectedLetter) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEE58)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("🗣️ ", fontSize = 14.sp)
                        Text(
                            text = if (isArabic) "استمع" else "Listen Phonics",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // SCROLLABLE CONTAINER:
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Elegant top-aligned header banner cards (Fixed)
                if (!isArabic) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)), // Forest Green
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("🌙 ", fontSize = 18.sp)
                                Text(
                                    text = "Islamic A to Z",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "A for Allah · B for Bismillah · C for Charity...",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("🌙 ", fontSize = 18.sp)
                                Text(
                                    text = "حروف الهجاء الإسلامية",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "أ لله · ب بسم الله · ت تقوى...",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Text(
                    text = if (isArabic) "اضغط على أي حرف لتسمع صوته! 🐝" else "Tap any letter to hear its sound! 🐝",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF795548),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

            // Scrollable Grid and challenge button
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Grid card color palette
                val solidPremiumColors = listOf(
                    Color(0xFF2E7D32), // Forest Green
                    Color(0xFFE65100), // Vibrant Orange
                    Color(0xFF880E4F), // Deep Plum/Magenta
                    Color(0xFF4A148C), // Deep Indigo/Purple
                    Color(0xFFBF360C), // Deep Rust
                    Color(0xFF0D47A1), // Royal Blue
                    Color(0xFF1A237E), // Dark Slate Blue
                    Color(0xFF263238), // Dark Charcoal
                    Color(0xFF006064)  // Deep Teal
                )

                val chunks = remember(letters) { letters.chunked(4) }
                chunks.forEach { rowLetters ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowLetters.forEach { letter ->
                            val index = letters.indexOf(letter)
                            val cardBg = solidPremiumColors[index % solidPremiumColors.size]
                            val isSelected = selectedLetter == letter

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(115.dp)
                                    .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(16.dp))
                                    .clickable {
                                        selectedLetter = letter
                                        speakLetter(letter)
                                    }
                                    .testTag("age3_letter_${letter.char}"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) cardBg.copy(alpha = 0.85f) else cardBg
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 4.dp else 0.dp,
                                    color = if (isSelected) Color(0xFFFFD54F) else Color.Transparent
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = letter.char,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = letter.icon,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = letter.word,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.9f),
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        if (rowLetters.size < 4) {
                            repeat(4 - rowLetters.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onStartAdventure,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(56.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                ) {
                    Text("🚀 ", fontSize = 18.sp)
                    Text(
                        text = if (isArabic) "ابدأ مغامرة التحدي! 🍯" else "Start Practice Playroom! 🍯",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
            }
        }
    }
}

data class AdvPlayLetter(
    val char: String,
    val word: String,
    val phrase: String,
    val icon: String,
    val quizBlanked: String,
    val correctChoice: String,
    val o1: String,
    val o2: String,
    val o3: String
)

@Composable
fun AgeFourAlphabetBoard(
    isArabic: Boolean,
    onStartAdventure: () -> Unit
) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }

    DisposableEffect(context, isArabic) {
        val instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
            }
        }
        tts = instance
        onDispose {
            instance.stop()
            instance.shutdown()
        }
    }

    val advLetters = remember(isArabic) {
        if (isArabic) {
            listOf(
                AdvPlayLetter("أ", "الله", "اللَّهُ رَبِّي وَخَالِقِي وَخَالِقُ كُلِّ شَيْءٍ ☝️", "☝️", "ـلله", "أ", "أ", "ب", "ت"),
                AdvPlayLetter("ب", "بسم الله", "أَقُولُ بِسْمِ اللَّهِ فِي بِدَايَةِ كُلِّ عَمَلٍ 🍽️", "🍽️", "ـسْمِ الله", "ب", "ب", "ت", "أ"),
                AdvPlayLetter("ت", "تقوى", "التَّقْوَى هِيَ طَاعَةُ اللَّهِ وَحُبُّهُ وَرِعَايَةُ كَلَامِهِ 💖", "💖", "ـقْوَى", "ت", "ت", "ث", "ب"),
                AdvPlayLetter("ث", "ثواب", "نَفْعَلُ الْخَيْرَ لِنَنَالَ الثَّوَابَ الْعَظِيمَ مِنَ اللَّهِ 🏆", "🏆", "ـوَاب", "ث", "ث", "ج", "ت"),
                AdvPlayLetter("ج", "جنة", "الْجَنَّةُ هِيَ بَيْتُ الْأَبْرَارِ وَالأَخْلَاقِ الطَّيِّبَةِ 🌺", "🌺", "ـنَّة", "ج", "ج", "ح", "خ"),
                AdvPlayLetter("ح", "حج", "الْحَجُّ هِيَ زِيَارَةُ بِتِ اللَّهِ الْحَرَامِ فِي مَكَّةَ 🕋", "🕋", "ـجّ", "ح", "ح", "ج", "خ"),
                AdvPlayLetter("خ", "خير", "أُحِبُّ الْخَيْرَ لِكُلِّ النَّاسِ كَمَا أُحِبُّهُ لِنَفْسِي ✨", "✨", "ـيْر", "خ", "خ", "ح", "ج"),
                AdvPlayLetter("د", "دعاء", "الدُّعَاءُ هِيَ سُؤَالُ اللَّهِ وَطَلَبُ الْعَوْنِ مِنْهُ دَائِمَاً 🤲", "🤲", "ـعَاء", "د", "د", "ذ", "ر"),
                AdvPlayLetter("ذ", "ذكر", "الذِّكْرُ هِيَ قَوْلُ سُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ 📿", "📿", "ـكْر", "ذ", "ذ", "د", "ز"),
                AdvPlayLetter("ر", "رمضان", "رَمَضَانُ هِيَ شَهْرُ الصِّيَامِ وَالْقُرْآنِ وَالْخَيْرِ 🌙", "🌙", "ـمَضَان", "ر", "ر", "ز", "د"),
                AdvPlayLetter("ز", "زكاة", "الزَّكَاةُ هِيَ مَالٌ نُبَارِكُ بِهِ وَنُسَاعِدُ الْفُقَرَاءَ 💰", "💰", "ـكَاة", "ز", "ز", "ر", "س"),
                AdvPlayLetter("س", "سجود", "السُّجُودُ هِيَ أَقْرَبُ مَا يَكُونُ الْعَبْدُ مِنْ رَبِّهِ 🛐", "🛐", "ـجُود", "س", "س", "ش", "ص"),
                AdvPlayLetter("ش", "شكر", "الشُّكْرُ هِيَ الْحَمْدُ لِلَّهِ عَلَى كُلِّ النِّعَمِ 🙌", "🙌", "ـكْر", "ش", "ش", "س", "ص"),
                AdvPlayLetter("ص", "صلاة", "الصَّلَاةُ هِيَ عِمَادُ الدِّينِ وَنُورُ الْقُلُوبِ 🕌", "🕌", "ـلَاة", "ص", "ص", "ض", "ط"),
                AdvPlayLetter("ض", "ضحى", "الضُّحَى هِيَ وَقْتُ شُرُوقِ الشَّمْسِ وَرِزْقِ الْعِبَادِ ☀️", "☀️", "ـحَى", "ض", "ض", "ص", "ظ"),
                AdvPlayLetter("ط", "طهارة", "الطَّهَارَةُ هِيَ نَظَافَةُ الْجِسْمِ وَالثَّوْبِ وَالْمَكَانِ 💧", "💧", "ـهَارَة", "ط", "ط", "ظ", "ت"),
                AdvPlayLetter("ظ", "ظهر", "الظُّهْرُ هِيَ وَقْتُ صَلَاةِ الظُّهْرِ فِي مُنْتَصَفِ النَّهَارِ ☀️", "☀️", "ـهْر", "ظ", "ظ", "ط", "ض"),
                AdvPlayLetter("ع", "عدل", "الْعَدْلُ هِيَ إِعْطَاءُ كُلِّ ذِي حَقٍّ حَقَّهُ 🙌", "⚖️", "ـدْل", "ع", "ع", "غ", "ف"),
                AdvPlayLetter("غ", "غفران", "الْغُفْرَانُ هِيَ مَسْحُ الذُّنُوبِ بِرَحْمَةِ اللَّهِ 🤲", "🤲", "ـفْرَان", "غ", "غ", "ع", "ق"),
                AdvPlayLetter("ف", "فجر", "الْفَجْرُ هِيَ بُدَايَةُ الصَّبَاحِ الْجَمِيلِ وَأَوَّلُ صَلَاةٍ 🌅", "🌅", "ـجْر", "ف", "ف", "ق", "غ"),
                AdvPlayLetter("ق", "قرآن", "الْقُرْآنُ هِيَ كَلَامُ اللَّهِ الْمُنَزَّلُ عَلَى النَّبِيِّ 📗", "📗", "ـرْآن", "ق", "ق", "ف", "ك"),
                AdvPlayLetter("ك", "كعبة", "الْكَعْبَةُ هِيَ قِبْلَةُ الْمُسْلِمِينَ فِي الصَّلَاةِ 🕋", "🕋", "ـعْبَة", "ك", "ك", "ق", "ل"),
                AdvPlayLetter("ل", "ليلة القدر", "لَيْلَةُ الْقَدْرِ هِيَ خَيْرٌ مِنْ أَلْفِ شَهْرٍ 🌌", "🌌", "ـيْلَة القدر", "ل", "ل", "ك", "م"),
                AdvPlayLetter("م", "مسجد", "الْمَسْجِدُ هِيَ بَيْتُ اللَّهِ حَيْثُ نَجْتَمِعُ لِلصَّلَاةِ 🕌", "🕌", "ـسْجِد", "م", "م", "ن", "هـ"),
                AdvPlayLetter("ن", "نبي", "النَّبِيُّ هِيَ رَسُولٌ أَرْسَلَهُ اللَّهُ لِهِدَايَتِنَا 📖", "📖", "ـبِي", "ن", "ن", "م", "ي"),
                AdvPlayLetter("هـ", "هداية", "الْهِدَايَةُ هِيَ طَرِيقُ الْحَقِّ وَالْإِيمَانِ بِاللَّهِ 🌟", "🌟", "ـدَايَة", "هـ", "هـ", "و", "ي"),
                AdvPlayLetter("و", "وضوء", "الْوُضُوءُ هِيَ طَهَارَةٌ نَفْعَلُهَا لِلِاسْتِعْدَادِ لِلصَّلَاةِ 💧", "💧", "ـضُوء", "و", "و", "ي", "هـ"),
                AdvPlayLetter("ي", "يقين", "الْيَقِينُ هِيَ صِدْقُ الْإِيمَانِ وَالثِّقَةُ بِاللَّهِ 💛", "💛", "ـقِين", "ي", "ي", "و", "أ")
            )
        } else {
            listOf(
                AdvPlayLetter("A", "Allah", "Allah is the Only Creator who made everything!", "⭐", "_llah", "A", "A", "B", "C"),
                AdvPlayLetter("B", "Bismillah", "We say Bismillah before eating our meals!", "🍽️", "_ismillah", "B", "B", "S", "H"),
                AdvPlayLetter("C", "Charity", "Charity is sharing what we have with people!", "🤝", "_harity", "C", "C", "J", "G"),
                AdvPlayLetter("D", "Dua", "Dua is talking to Allah and asking for help!", "🤲", "_ua", "D", "D", "E", "F"),
                AdvPlayLetter("E", "Eid", "Eid is a beautiful and happy celebration holiday!", "🎉", "_id", "E", "E", "I", "N"),
                AdvPlayLetter("F", "Fajr", "Fajr is our beautiful prayer in the early morning!", "🌅", "_ajr", "F", "F", "T", "Z"),
                AdvPlayLetter("G", "Ghusl", "Ghusl is bathing to keep our body purer!", "🚿", "_husl", "G", "G", "Q", "M"),
                AdvPlayLetter("H", "Hajj", "Hajj is the amazing pilgrimage to the Kaaba!", "🕋", "_ajj", "H", "H", "Y", "K"),
                AdvPlayLetter("I", "Iman", "Iman is believing in Allah with all our heart!", "💚", "_man", "I", "I", "O", "P"),
                AdvPlayLetter("J", "Jannah", "Jannah is the beautiful paradise for kind people!", "🌺", "_annah", "J", "J", "L", "R"),
                AdvPlayLetter("K", "Kaaba", "Kaaba is the sacred house located in Makkah!", "🕋", "_aaba", "K", "K", "C", "W"),
                AdvPlayLetter("L", "Laylatul Qadr", "Laylatul Qadr is a very blessed night!", "🌙", "_aylatul Qadr", "L", "L", "X", "V"),
                AdvPlayLetter("M", "Masjid", "Masjid is a clean mosque where we pray!", "🕌", "_asjid", "M", "M", "N", "W"),
                AdvPlayLetter("N", "Nabi", "Nabi is a chosen messenger of guidance!", "📖", "_abi", "N", "N", "P", "M"),
                AdvPlayLetter("O", "Obedience", "Obeying our kind parents makes Allah glad!", "🙏", "_bedience", "O", "O", "U", "V"),
                AdvPlayLetter("P", "Prophet", "Our beloved Prophet always spoke kindly!", "🌟", "_rophet", "P", "P", "B", "T"),
                AdvPlayLetter("Q", "Quran", "The Holy Quran is the word of Allah!", "📗", "_uran", "Q", "Q", "K", "C"),
                AdvPlayLetter("R", "Ramadan", "Ramadan is the month of fasting and smiles!", "🌙", "_amadan", "R", "R", "D", "L"),
                AdvPlayLetter("S", "Salah", "Salah is the daily prayer that gives peace!", "🛐", "_alah", "S", "S", "C", "Z"),
                AdvPlayLetter("T", "Tawheed", "Tawheed means believing Allah is One!", "☝️", "_awheed", "T", "T", "D", "B"),
                AdvPlayLetter("U", "Ummah", "The Ummah is our unified Muslim family!", "🤝", "_mmah", "U", "U", "Y", "W"),
                AdvPlayLetter("V", "Virtue", "Virtue is doing good deeds for reward!", "💖", "_irtue", "V", "V", "F", "W"),
                AdvPlayLetter("W", "Wudu", "Wudu is washing up to prepare for prayer!", "💧", "_udu", "W", "W", "Q", "V"),
                AdvPlayLetter("X", "eXcellence", "Excellent actions make our hearts shine bright!", "✨", "e_cellence", "X", "X", "S", "Z"),
                AdvPlayLetter("Y", "Yaqeen", "Yaqeen is trusting Allah completely!", "💛", "_aqeen", "Y", "Y", "J", "G"),
                AdvPlayLetter("Z", "Zakat", "Zakat helps poor children buy food!", "💰", "_akat", "Z", "Z", "S", "R")
            )
        }
    }

    var selectedLetter by remember(isArabic) { mutableStateOf(advLetters.first()) }
    var selectedAnswer by remember(selectedLetter) { mutableStateOf<String?>(null) }
    var pointsEarned by remember(selectedLetter) { mutableStateOf(0) }

    fun speakPhrase(letter: AdvPlayLetter) {
        tts?.let { t ->
            val lang = if (isArabic) Locale("ar", "SA") else Locale("en", "IN")
            val result = t.setLanguage(lang)
            if (isArabic && (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)) {
                t.language = Locale("ar")
            } else {
                t.language = lang
            }
            val textToSpeak = if (isArabic) {
                "${letter.char} ... ${letter.word} ... ${letter.phrase}"
            } else {
                "${letter.char} ... is for ... ${letter.word} ... ${letter.phrase}"
            }
            t.speak(sanitizeSpeechText(textToSpeak), TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    LaunchedEffect(selectedLetter, ttsReady) {
        if (ttsReady) {
            speakPhrase(selectedLetter)
        }
    }

    val scrollState = rememberScrollState()

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FIXED ON TOP: Rectangular compact preview card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, Color(0xFF1565C0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = selectedLetter.icon,
                                fontSize = 32.sp
                            )
                            Column {
                                Text(
                                    text = if (isArabic) selectedLetter.char else "${selectedLetter.char.uppercase()} ${selectedLetter.char.lowercase()}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0D47A1)
                                )
                                Text(
                                    text = selectedLetter.word,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFFB300)
                                )
                            }
                        }
                        Button(
                            onClick = { speakPhrase(selectedLetter) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90CAF9)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("🗣️ ", fontSize = 14.sp)
                            Text(
                                text = if (isArabic) "العبارة" else "Sentence",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1),
                                fontSize = 11.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedLetter.phrase,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF37474F),
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            // SCROLLABLE CONTAINER:
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interactive Header Banner Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)), // Indigo Blue
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("🌟 ", fontSize = 18.sp)
                            Text(
                                text = if (isArabic) "المستوى ٤: مستكشف صوتيات المبادئ" else "Level 4: Phonics & Spelling Explorer",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isArabic) "تعلم مع نور بي نطق الجمل والعبارات وحل الألغاز التفاعلية!" else "Learn whole sentences and play the Spelling Quiz with Noor Bee!",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

            // Noor Bee's interactive Spelling puzzle quiz card!
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)), // Warm yellow background
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, Color(0xFFFFD54F))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isArabic) "تحدي الحرف المفقود من نور بي! 🐝" else "Noor Bee's Spellbee Challenge! 🐝",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF5D4037),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Puzzle word
                    Text(
                        text = if (selectedAnswer == selectedLetter.correctChoice) selectedLetter.word else selectedLetter.quizBlanked,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF2E7D32),
                        letterSpacing = 4.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(12.dp))
                            .padding(horizontal = 24.dp, vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Options list buttons (o1, o2, o3)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(selectedLetter.o1, selectedLetter.o2, selectedLetter.o3).forEach { option ->
                            val isCorrect = option == selectedLetter.correctChoice
                            val isChosen = selectedAnswer == option
                            val btnBg = when {
                                isChosen && isCorrect -> Color(0xFF43A047) // Green
                                isChosen && !isCorrect -> Color(0xFFE53935) // Red
                                else -> Color(0xFFFFF9C4) // Default Yellow-Cream
                            }
                            val btnTextCol = if (isChosen) Color.White else Color(0xFF5D4037)

                            Card(
                                modifier = Modifier
                                    .padding(horizontal = 6.dp)
                                    .size(54.dp)
                                    .shadow(2.dp, CircleShape)
                                    .clickable {
                                        selectedAnswer = option
                                        if (isCorrect) {
                                            pointsEarned = 10
                                            val target = if (isArabic) "text:arabic:إجابة رائعة أحسنت يا بطل" else "text:english:Great job, that is correct!"
                                            tts?.let { t ->
                                                val l = if (isArabic) Locale("ar") else Locale("en", "IN")
                                                t.language = l
                                                t.speak(if (isArabic) "ممتاز! إجابة صحيحة!" else "Superb! Correct!", TextToSpeech.QUEUE_FLUSH, null, null)
                                            }
                                        } else {
                                            pointsEarned = 0
                                            tts?.let { t ->
                                                val l = if (isArabic) Locale("ar") else Locale("en", "IN")
                                                t.language = l
                                                t.speak(if (isArabic) "حاول ثانية، يا بطل!" else "Try again, you can do it!", TextToSpeech.QUEUE_FLUSH, null, null)
                                            }
                                        }
                                    },
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(containerColor = btnBg),
                                border = BorderStroke(2.dp, Color(0xFFFFB300))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = option,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = btnTextCol
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Feedback description
                    if (selectedAnswer != null) {
                        val isCorrect = selectedAnswer == selectedLetter.correctChoice
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                text = if (isCorrect) "🎉 " else "✏️ ",
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (isCorrect) {
                                    if (isArabic) "رائع جداً! مبروك حصلت على +10 عسل! 🍯" else "Wohoo! You solved it! Got +10 Honey Stars! 🍯"
                                } else {
                                    if (isArabic) "حاول مجدداً، استكشف شكل الكلمة!" else "Almost! Try another letter bubble, champion!"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                    } else {
                        Text(
                            text = if (isArabic) "اضغط على فقاعة الحرف الصحيح لإكمال الكلمة!" else "Tap the correct letter bubble to spell the word!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Horizontally Scrollable Letters list for navigation
            Text(
                text = if (isArabic) "اختر حرفاً للاستكشاف والتعلم: 🐝" else "Choose any letter to trace and play: 🐝",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF795548),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(advLetters) { letter ->
                    val isSelected = selectedLetter == letter
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFFBBDEFB) else Color(0xFFECEFF1))
                            .border(2.dp, if (isSelected) Color(0xFF1565C0) else Color.Transparent, CircleShape)
                            .clickable {
                                selectedLetter = letter
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.char,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) Color(0xFF0D47A1) else Color(0xFF455A64)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onStartAdventure,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp))
            ) {
                Text("🍯 ", fontSize = 18.sp)
                Text(
                    text = if (isArabic) "اكتمل الدرس! احصل على الجوائز" else "Finish Lesson & Save Progress",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
        }
    }
}

data class AgeSixPlayLetter(
    val char: String,
    val word: String,
    val icon: String,
    val title: String,
    val lessonText: String,
    val question: String,
    val correctWord: String,
    val o1: String,
    val o2: String,
    val o3: String
)

@Composable
fun AgeSixAlphabetBoard(
    isArabic: Boolean,
    onStartAdventure: () -> Unit
) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }

    DisposableEffect(context, isArabic) {
        val instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
            }
        }
        tts = instance
        onDispose {
            instance.stop()
            instance.shutdown()
        }
    }

    val ageSixLetters = remember(isArabic) {
        if (isArabic) {
            listOf(
                AgeSixPlayLetter("أ", "أَدَب", "☝️", "الأدب والأخلاق", "الأدب زين المسلم ونبينا هو القائل: إنما بعثت لأتمم مكارم الأخلاق.", "ما هي الحركة الصحيحة لحرف الألف في بداية كلمة أَدَب؟", "الفتحة (◌َ)", "الفتحة (◌َ)", "الضمة (◌ُ)", "الكسرة (◌ِ)"),
                AgeSixPlayLetter("ب", "بِرّ الوالدين", "💖", "بر الوالدين", "بر الوالدين من أعظم الأعمال التي تقربنا إلى الجنة ورضا الله.", "ما هي الحركة الصحيحة لحرف الباء في كلمة بِرّ؟", "الكسرة (◌ِ)", "الكسرة (◌ِ)", "الفتحة (◌َ)", "الضمة (◌ُ)"),
                AgeSixPlayLetter("ت", "تَرْتِيل", "📖", "ترتيل القرآن", "ترتيل القرآن الكريم يملأ البيت بركة ويبهج قلوب السامعين.", "أي حرف مد (طويل) موجود في كلمة تَرْتِيل؟", "الياء", "الياء", "الألف", "الواو"),
                AgeSixPlayLetter("ث", "ثَبَات", "🧱", "الثبات على الحق", "نسأل الله الثبات على الحق والعمل الصالح حتى ننال رضاه.", "ما هي الحركة الصحيحة لحرف الثاء في كلمة ثَبَات؟", "الفتحة (◌َ)", "الفتحة (◌َ)", "الكسرة (◌ِ)", "السكون (◌ْ)"),
                AgeSixPlayLetter("ج", "جَمَاعَة", "👥", "صلاة الجماعة", "صلاة الجماعة في المسجد تضاعف الأجر وتعلمنا التعاون والوحدة.", "أي نوع من المد تجده في كلمة جَمَاعَة؟", "مد الألف", "مد الألف", "مد الياء", "مد الواو"),
                AgeSixPlayLetter("ح", "حِلْم", "🕊️", "الحلم والرفق", "الحِلم والرفق بالآخرين من صفات المؤمنين الصالحين المحبوبين.", "ما هي الحركة الصحيحة لحرف الحاء في كَلِمة حِلْم؟", "الكسرة (◌ِ)", "الكسرة (◌ِ)", "الفتحة (◌َ)", "السكون (◌ْ)"),
                AgeSixPlayLetter("خ", "خُشُوع", "🤲", "الخشوع في الصلاة", "الخشوع في الصلاة يجعلنا نشعر بالطمأنينة والقرب من الله.", "ما هو المد الصوتي الموجود في كَلِمة خُشُوع؟", "مد الواو", "مد الواو", "مد الألف", "مد الياء"),
                AgeSixPlayLetter("د", "دِين", "🕌", "دين السلام والمحبة", "دين الإسلام هو دين السلام والمحبة والصدق والمساعدة.", "ما هي حركة حرف الدال في كَلِمة دِين؟", "الكسرة (◌ِ)", "الكسرة (◌ِ)", "الفتحة (◌َ)", "السكون (◌ْ)"),
                AgeSixPlayLetter("ذ", "ذَهَب", "🪙", "أخلاقنا ذهب", "الأفعال الطيبة والخلق الحسن أثمن من الذهب في ميزان الحسنات.", "حرف الذال هو من الحروف:", "اللثوية", "اللثوية", "الشفوية", "الحلقية"),
                AgeSixPlayLetter("ر", "رِفْق", "🐈", "الرفق بالجميع", "الرفق بالحيوان والإنسان والرفق بالجميع يجلب محبة ورحمة الله.", "ما هي الحركة التي فوق حرف الفاء في كَلِمة رِفْق؟", "السكون (◌ْ)", "السكون (◌ْ)", "الفتحة (◌َ)", "الضمة (◌ُ)"),
                AgeSixPlayLetter("ز", "زُهْد", "🥣", "الزهد والقناعة", "القناعة والزهد والرضا بما قسمه الله لنا تجعل قلوبنا سعيدة.", "ما هي حركة حرف الزاي في كَلِمة زُهْد؟", "الضمة (◌ُ)", "الضمة (◌ُ)", "الفتحة (◌َ)", "الكسرة (◌ِ)"),
                AgeSixPlayLetter("س", "سَخَاء", "🎁", "السخاء والكرم", "السخاء والكرم يمسح العيوب ويبني المحبة بين الزملاء.", "ما هو المد الصوتي في كَلِمة سَخَاء؟", "مد الألف", "مد الألف", "مد الياء", "مد الواو"),
                AgeSixPlayLetter("ش", "شَهَادَة", "💬", "شهادة الحق", "قول أشهد أن لا إله إلا الله هو أول أركان الإسلام الخمسة.", "ما هو الحرف المفتوح في بداية كلمة شَهَادَة؟", "الشين", "الشين", "الهاء", "الدال"),
                AgeSixPlayLetter("ص", "صِدْق", "✅", "الصدق والأمانة", "الصدق يهدي إلى البر، والمؤمن دائماً يصدق في كلامه.", "ما هي الحركة التي على حرف الدال في كَلِمة صِدْق؟", "السكون (◌ْ)", "السكون (◌ْ)", "الفتحة (◌َ)", "الضمة (◌ُ)"),
                AgeSixPlayLetter("ض", "ضِيَاء", "☀️", "القرآن ضياء", "القرآن الكريم والعمل الصالح هما ضياء للمؤمن في الدنيا والآخرة.", "ما هو المد الصوتي المسموع في كَلِمة ضِيَاء؟", "مد الألف", "مد الألف", "مد الياء", "مد الواو"),
                AgeSixPlayLetter("ط", "طُمَأْنِينَة", "🌸", "طمأنينة القلب", "طمأنينة القلب تأتي دائماً بذكر الله وقراءة آياته العذبة.", "ما هي حركة حرف الميم في كلمة طُمَأْنِينَة؟", "الفتحة (◌َ)", "الفتحة (◌َ)", "الضمة (◌ُ)", "السكون (◌ْ)"),
                AgeSixPlayLetter("ظ", "ظِلّ", "🌳", "ظل الرحمن", "يظل الله بظله يوم القيامة العباد الصالحين الذين يحبون الآخرين.", "ما هي حركة حرف الظاء في كلمة ظِلّ؟", "الكسرة (◌ِ)", "الكسرة (◌ِ)", "الفتحة (◌َ)", "الضمة (◌ُ)"),
                AgeSixPlayLetter("ع", "عِلْم", "🎓", "طلب العلم فريضة", "العلم ينير العقول، وطلب العلم فريضة تفتح أبواب الجنة.", "ما هي حركة العين في كلمة عِلْم؟", "الكسرة (◌ِ)", "الكسرة (◌ِ)", "الفتحة (◌َ)", "السكون (◌ْ)"),
                AgeSixPlayLetter("غ", "غِنَى", "💎", "غنى النفس والقناعة", "الغنى الحقيقي هو غنى النفس والقلب الشاكر الراضي بفضل الله.", "ما نوع الألف المكتوبة في نهاية كَلِمة غِنَى؟", "مقصورة (ى)", "مقصورة (ى)", "ممدودة (ا)", "واو"),
                AgeSixPlayLetter("ف", "فَرَح", "🎈", "الفرح بالطاعة", "نفرح فرحاً كبيراً بالطاعات وعيد المسلمين وهدايا الخير.", "ما هي حركة الفاء في كلمة فَرَح؟", "الفتحة (◌َ)", "الفتحة (◌َ)", "الضمة (◌ُ)", "الكسرة (◌ِ)"),
                AgeSixPlayLetter("ق", "قَنَاعَة", "🍯", "القناعة والرضا", "القناعة كنز لا يفنى، وهي تجعلنا راضين بما أعطانا الله.", "أي نوع مد نكتشفه في كلمة قَنَاعَة؟", "مد الألف", "مد الألف", "مد الياء", "مد الواو"),
                AgeSixPlayLetter("ك", "كَرَم", "🍲", "الكرم والجود", "الكرم هو صفة الأنبياء، وإكرام الضيف ومساعدة الجار من الإيمان.", "كم حرف يحمل حركة الفتحة في كَلِمة كَرَم؟", "حرفان", "حرفان", "ثلاثة حروف", "حرف واحد"),
                AgeSixPlayLetter("ل", "لِين", "🍦", "اللين والرفق", "اللين والرفق وحلاوة اللسان تكسب القلوب وترضي الرحمن.", "ما هو نوع الياء في كلمة لِين؟", "ياء لينة", "ياء لينة", "مد الياء", "ياء مشددة"),
                AgeSixPlayLetter("م", "مُرُوءَة", "🦁", "المروءة والشهامة", "المروءة هي مكارم الأخلاق وحب النجدة ومساعدة الضعيف.", "أي حرف مد موجود في كلمة مُرُوءَة؟", "الواو", "الواو", "الألف", "الياء"),
                AgeSixPlayLetter("ن", "نَصِيحَة", "📢", "النصيحة باللطف", "الدين النصيحة، ننصح بالخير ونرشد زملائنا باللطف دائماً.", "ما هو المد الموجود في كلمة نَصِيحَة؟", "مد الياء", "مد الياء", "مد الألف", "مد الواو"),
                AgeSixPlayLetter("هـ", "هِمَّة", "🚀", "الهمة العالية", "الهمة العالية تجعل الطفل المسلم ينجز واجباته بامتياز وشغف.", "ما هي العلامة المميزة على حرف الميم في كَلِمة هِمَّة؟", "الشدة مع الفتحة (◌َّ)", "الشدة مع الفتحة (◌َّ)", "السكون (◌ْ)", "الكسرة (◌ِ)"),
                AgeSixPlayLetter("و", "وَفَاء", "🤝", "الوفاء بالوعد", "الوفاء بالعهود والوعد هو خلق عظيم يعلمنا إياه ديننا الحنيف.", "ما هو المد الصوتي الذي يتوسط كَلِمة وَفَاء؟", "مد الألف", "مد الألف", "مد الياء", "مد الواو"),
                AgeSixPlayLetter("ي", "يَقِظ", "⏰", "اليقظة والانتباه", "اليقظة والانتباه للخير تجعل المؤمن دائماً يصلي في وقته.", "ما هي حركة الياء في كَلِمة يَقِظ؟", "الفتحة (◌َ)", "الفتحة (◌َ)", "الضمة (◌ُ)", "الكسرة (◌ِ)")
            )
        } else {
            listOf(
                AgeSixPlayLetter("A", "Adab", "☝️", "Good Manners", "Our Prophet Muhammad (PBUH) had the absolute best manners. Adab makes everyone respect you.", "Which consonant blend completes the word: 'res_ _ _ _'?", "pect", "pect", "ting", "sion"),
                AgeSixPlayLetter("B", "Birr", "💖", "Kindness to Parents", "Birr means absolute love and respecting parents. Doing good to them opens Jannah.", "Which blend completes: 'Our parents are _ _essings'?", "Bl", "Bl", "Br", "Pl"),
                AgeSixPlayLetter("C", "Charity", "🎁", "Sadaqah", "Giving charity removes bad deeds and spreads happiness in our community.", "Which blend makes the starting sound of 'Charity'?", "Ch", "Ch", "Sh", "Th"),
                AgeSixPlayLetter("D", "Deen", "🕌", "Way of Life", "Islam is our beautiful Deen! It teaches us honesty, kindness, and love.", "Which vowel digraph creates the long /ee/ sound in 'Deen'?", "ee", "ee", "ea", "ei"),
                AgeSixPlayLetter("E", "Excellence", "✨", "Ihsan", "Doing your best in everything you do (writing, praying, helping) is Islamic excellence.", "What suffix can make 'Excellence' an action adjective?", "Excellent", "Excellent", "Excellency", "Excelling"),
                AgeSixPlayLetter("F", "Fasting", "🌙", "Sawm", "Fasting teaches us patience, gratitude, and empathy for hungry people.", "Which blend completes the spelling of: 'fa_ _ing'?", "st", "st", "sh", "th"),
                AgeSixPlayLetter("G", "Gratitude", "🙌", "Shukr", "Gratitude is saying Al-hamdulillah when we receive blessings, and thanking everyone who helps us.", "Which blend completes the starting sound of: '_ _atitude'?", "Gr", "Gr", "Gl", "Pr"),
                AgeSixPlayLetter("H", "Hajj", "🕋", "Pilgrimage", "Hajj is a journey to Makkah. It brings all Muslims together in unity and peace.", "Which consonant makes the starting sound of 'Hajj'?", "H", "H", "J", "G"),
                AgeSixPlayLetter("I", "Iman", "💚", "Pure Faith", "Iman is having full faith in Allah, loving His creation, and trusting Him in everything.", "Which letter makes the starting sound of 'Iman'?", "I", "I", "E", "A"),
                AgeSixPlayLetter("J", "Jannah", "🌺", "Paradise Garden", "Jannah is our eternal reward full of honey rivers and beautiful gardens to enjoy.", "Which consonant makes the starting sound of 'Jannah'?", "J", "J", "G", "Y"),
                AgeSixPlayLetter("K", "Kindness", "🤝", "Al-Khayr", "Prophet Muhammad (PBUH) said that kindness beautifies everything, and without it, things are dull.", "Which suffix completes the word: 'kind_ _ _ _'?", "ness", "ness", "ment", "tion"),
                AgeSixPlayLetter("L", "Loyalty", "📦", "Trustworthiness", "Loyalty means standing firmly by our promises and keeping secrets safe.", "Which letter makes the starting sound of 'Loyalty'?", "L", "L", "R", "Y"),
                AgeSixPlayLetter("M", "Manners", "👑", "Good Akhlaq", "Manners mean being gentle in our words and treating people with love and care.", "Which suffix completes the plural form: 'manner_ _'?", "s", "s", "es", "ed"),
                AgeSixPlayLetter("N", "Niyyah", "🎯", "Intention", "We start every single good deed with a pure Niyyah (intention) in our heart to please Allah.", "Which letter makes the starting sound of 'Niyyah'?", "N", "N", "M", "L"),
                AgeSixPlayLetter("O", "Obedience", "🙏", "Taa'ah", "Obedience to Allah and our parents brings peace, success, and blessings.", "Which letters complete the word: 'obedi_ _ _ _'?", "ence", "ence", "ance", "ense"),
                AgeSixPlayLetter("P", "Prayer", "🛐", "Salah", "Salah is our beautiful direct connection to Allah five times a day, bringing light to our hearts.", "Which suffix completes the word: 'pray_ _'?", "er", "er", "ing", "ed"),
                AgeSixPlayLetter("Q", "Quran", "📗", "Holy Book", "The Quran contains pure guidelines of peace and wisdom that lead all human souls to success.", "Which vowel blend completes the spelling in 'Qu_ _ _n'?", "ra", "ra", "re", "ro"),
                AgeSixPlayLetter("R", "Respect", "🎖️", "Al-Ihtiram", "Respect is key in Islam. We show regard for our parents, our surroundings, and our teachers.", "Which prefix completes the negative antonym: '_ _ _respect'?", "dis", "dis", "un", "mis"),
                AgeSixPlayLetter("S", "Sincerity", "💎", "Ikhlas", "Sincerity (Ikhlas) means helping others purely for Allah, with no showing off or boasting.", "Which suffix completes the word 'sincer_ _ _ _'?", "ity", "ity", "ness", "ly"),
                AgeSixPlayLetter("T", "Taqwa", "🛡️", "God Consciousness", "Taqwa is loving Allah fully and living carefully to obey His commands and spread goodness.", "Which letter makes the starting sound of 'Taqwa'?", "T", "T", "D", "P"),
                AgeSixPlayLetter("U", "Unity", "🌍", "Ummah Cooperation", "The Ummah is like one body. Unity makes us strong and helpful to each other.", "Which blend completes the spelling of: 'uni_ _'?", "ty", "ty", "ce", "te"),
                AgeSixPlayLetter("V", "Virtue", "⭐", "Moral Goodness", "A person of virtue speaks truth and keeps agreements with pure dedication to doing good.", "Which suffix completes: 'virtu_ _ _'?", "ous", "ous", "ful", "ness"),
                AgeSixPlayLetter("W", "Wisdom", "🦉", "Hikmah", "Wisdom is listening carefully, knowing proper actions, and choosing kind decisions.", "Which suffix is at the end of the word 'Wis_ _ _'?", "dom", "dom", "ness", "tion"),
                AgeSixPlayLetter("X", "eXcellence", "🏆", "Beauty in Actions", "Striving for excellence (Ihsan) is highly loved by Allah in every single field of our lives.", "Which letters complete the spelling of: 'ex_ _ _ _ence'?", "cell", "cell", "sel", "sill"),
                AgeSixPlayLetter("Y", "Yaqeen", "💛", "Certainty", "Yaqeen is absolute certainty that Allah listens to your quietest prayers.", "Which vowel digraph creates the long /ee/ sound in 'Yaqeen'?", "ee", "ee", "ea", "ei"),
                AgeSixPlayLetter("Z", "Zuhd", "🥣", "Simplistic Living", "Zuhd means loving simple things, having high gratitude, and avoiding showy luxury.", "Which letter makes the starting sound of 'Zuhd'?", "Z", "Z", "S", "X")
            )
        }
    }

    var selectedLetter by remember(isArabic) { mutableStateOf(ageSixLetters.first()) }
    var selectedAnswer by remember(selectedLetter) { mutableStateOf<String?>(null) }
    var pointsEarned by remember(selectedLetter) { mutableStateOf(0) }

    val isCorrectChosen = selectedAnswer == selectedLetter.correctWord
    val questionToDisplay = remember(selectedLetter, selectedAnswer, isArabic) {
        val q = selectedLetter.question
        if (isCorrectChosen && !isArabic) {
            val regex = Regex("_[_\\s]+")
            val matchResult = regex.find(q)
            if (matchResult != null) {
                val range = matchResult.range
                buildAnnotatedString {
                    append(q.substring(0, range.first))
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))) {
                        append(selectedLetter.correctWord)
                    }
                    append(q.substring(range.last + 1))
                }
            } else {
                AnnotatedString(q)
            }
        } else {
            AnnotatedString(q)
        }
    }

    fun speakFullLesson(letter: AgeSixPlayLetter) {
        tts?.let { t ->
            val lang = if (isArabic) Locale("ar", "SA") else Locale("en", "IN")
            val result = t.setLanguage(lang)
            if (isArabic && (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)) {
                t.language = Locale("ar")
            } else {
                t.language = lang
            }
            val speechText = "${letter.word}. ${letter.lessonText}. ${letter.question}"
            t.speak(sanitizeSpeechText(speechText), TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    LaunchedEffect(selectedLetter, ttsReady) {
        if (ttsReady) {
            speakFullLesson(selectedLetter)
        }
    }

    val scrollState = rememberScrollState()

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FIXED ON TOP: Compact preview card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, Color(0xFF1565C0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = selectedLetter.icon,
                                fontSize = 32.sp
                            )
                            Column {
                                Text(
                                    text = if (isArabic) selectedLetter.char else "${selectedLetter.char.uppercase()} ${selectedLetter.char.lowercase()}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0D47A1)
                                )
                                Text(
                                    text = selectedLetter.word,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                        Button(
                            onClick = { speakFullLesson(selectedLetter) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3F2FD)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("🗣️ ", fontSize = 14.sp)
                            Text(
                                text = if (isArabic) "الحكمة" else "Wisdom",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1),
                                fontSize = 11.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedLetter.lessonText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723),
                        lineHeight = 15.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF3E0), RoundedCornerShape(6.dp))
                            .padding(6.dp)
                    )
                }
            }

            // SCROLLABLE CONTAINER
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("👑 ", fontSize = 18.sp)
                            Text(
                                text = if (isArabic) "المستوى ٤: حكمة الحروف وأصوات الكلمات!" else "Level 4: Letters Wisdom & Phonetic Sounds!",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isArabic) "اقرأ الحكمة وحل لغز أصوات الحروف لجمع عسل النور بي! 🐝" else "Read the moral lesson & solve phonetic blends to collect Noor Bee Honey! 🐝",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(2.dp, Color(0xFF90CAF9))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isArabic) "🧠 تحدي القواعد والمخارج والحركات 🧠" else "🧠 Quick Language & Sound Quiz 🧠",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0D47A1),
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = questionToDisplay,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1565C0),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(selectedLetter.o1, selectedLetter.o2, selectedLetter.o3).forEach { option ->
                                val isCorrect = option == selectedLetter.correctWord
                                val isChosen = selectedAnswer == option
                                val btnBg = when {
                                    isChosen && isCorrect -> Color(0xFF2E7D32)
                                    isChosen && !isCorrect -> Color(0xFFC62828)
                                    else -> Color.White
                                }
                                val btnTextCol = if (isChosen) Color.White else Color(0xFF0D47A1)

                                Card(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .height(50.dp)
                                        .weight(1f)
                                        .shadow(2.2.dp, RoundedCornerShape(12.dp))
                                        .clickable {
                                            selectedAnswer = option
                                            if (isCorrect) {
                                                pointsEarned = 10
                                                tts?.let { t ->
                                                    val l = if (isArabic) Locale("ar") else Locale("en", "IN")
                                                    t.language = l
                                                    t.speak(if (isArabic) "رائع جداً يا حكيم! إجابة صحيحة متميزة!" else "Brilliant puzzle solver! That is correct!", TextToSpeech.QUEUE_FLUSH, null, null)
                                                }
            } else {
                                                pointsEarned = 0
                                                tts?.let { t ->
                                                    val l = if (isArabic) Locale("ar") else Locale("en", "IN")
                                                    t.language = l
                                                    t.speak(if (isArabic) "حاول ثانية، يا بطل!" else "Try again, you can do it!", TextToSpeech.QUEUE_FLUSH, null, null)
                                                }
                                            }
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = btnBg),
                                    border = BorderStroke(1.5.dp, Color(0xFF90CAF9))
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = option,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = btnTextCol,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (selectedAnswer != null) {
                            val isCorrect = selectedAnswer == selectedLetter.correctWord
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = if (isCorrect) "👑 🍯 " else "✏️ ",
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = if (isCorrect) {
                                        if (isArabic) "حل ممتاز! لقد حصلت على +15 عسل للتقدم في المسار! 🐝" else "Splendid resolution! Claimed +15 Honey Stars! 🐝"
                                    } else {
                                        if (isArabic) "أعد التفكير في الصوت أو الحركة!" else "Almost got it! Keep checking letters!"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        } else {
                            Text(
                                text = if (isArabic) "اختر الخيار اللغوي الصحيح ليرضى عنك الرحمن وتجمع العسل!" else "Understand the rules and select the right blend option!",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Text(
                    text = if (isArabic) "اختر حرفاً لتبحر في حكمته اللطيفة: 📖" else "Explore any letters for beautiful wisdoms: 📖",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ageSixLetters) { letter ->
                        val isSelected = selectedLetter == letter
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF90CAF9) else Color(0xFFECEFF1))
                                .border(2.dp, if (isSelected) Color(0xFF0D47A1) else Color.Transparent, CircleShape)
                                .clickable {
                                    selectedLetter = letter
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter.char,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.White else Color(0xFF37474F)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onStartAdventure,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(50.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                ) {
                    Text("🍯 ", fontSize = 18.sp)
                    Text(
                        text = if (isArabic) "اكتمل درس الحكمة! صب عسل الفخر 🍯" else "Finish Quest & Collect Wisdom Honey Stars! 🍯",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

data class AgeSevenPlayLetter(
    val char: String,
    val word: String,
    val icon: String,
    val title: String,
    val lessonText: String,
    val question: String,
    val correctWord: String,
    val o1: String,
    val o2: String,
    val o3: String
)

@Composable
fun AgeSevenAlphabetBoard(
    isArabic: Boolean,
    onStartAdventure: () -> Unit
) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }

    DisposableEffect(context, isArabic) {
        val instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
            }
        }
        tts = instance
        onDispose {
            instance.stop()
            instance.shutdown()
        }
    }

    val ageSevenLetters = remember(isArabic) {
        if (isArabic) {
            listOf(
                AgeSevenPlayLetter("أ", "أَخْلَاق", "👑", "الأخلاق الكريمة", "الأخلاق الكريمة ترفع شأن المؤمن وهي أثقل ما يوضع في ميزان الحسنات يوم القيامة.", "ما هو الموقع الإعرابي لكلمة (أخلاقٌ) في جملة 'أخلاقُ المسلمِ كريمةٌ'؟", "مبتدأ مرفوع", "مبتدأ مرفوع", "خبر مرفوع", "فاعل مرفوع"),
                AgeSevenPlayLetter("ب", "بَرَكَة", "🌾", "البركة والنماء", "البركة هي نماء الخير وزيادته بكثرة ذكر الله والصدق والعمل الصالح والنية الطيبة.", "ما الحركة الإعرابية لحرف الراء في كلمة (بَرَكَة)؟", "الفتحة (◌َ)", "الفتحة (◌َ)", "الضمة (◌ُ)", "الكسرة (◌ِ)"),
                AgeSevenPlayLetter("ت", "تَوَاضُع", "🌱", "التواضع لله وللناس", "التواضع لله وللناس يرفع صاحبه منزلة ومحبة عند الخالق والمخلوقين ويذهب الكبر.", "ما هو الوزن الصرفي الصحيح لمصدر الكلمة (تَوَاضُع)؟", "تَفَاعُل", "تَفَاعُل", "تَفْعِيل", "فَعِيل"),
                AgeSevenPlayLetter("ث", "ثَوَاب", "💎", "الثواب العظيم من الله", "الثواب العظيم يناله المؤمن المخلص في أعماله الطيبة كلها سرًا وعلنًا لوجه الله.", "ما هو ضد أو عكس كلمة (ثَوَاب) في المعنى اللغوي؟", "عِقَاب", "عِقَاب", "جَزَاء", "كَرَم"),
                AgeSevenPlayLetter("ج", "جَمَال", "🌸", "جمال الروح والخلق", "الله جميل يحب الجمال؛ جمال الروح والأخلاق والتبسم في وجوه الناس من الإيمان.", "ما هو المد الصوتي المسموع في وسط كلمة (جَمَال)؟", "مد الألف", "مد الألف", "مد الياء", "مد الواو"),
                AgeSevenPlayLetter("ح", "حَيَاء", "🕊️", "الحياء شعبة الإيمان", "الحياء شعبة من شعب الإيمان، وهو يزين المؤمن ويمنعه من فعل السوء وقول الباطل.", "عند الوقف الصوتي على كلمة (حَيَاء)، ما نوع الهمزة المتطرفة؟", "همزة بعد ألف مد", "همزة بعد ألف مد", "همزة على نبرة", "همزة متوسطة"),
                AgeSevenPlayLetter("خ", "خَيْر", "🤝", "فعل الخير والمعروف", "افعلوا الخير لعلكم تفلحون؛ فكل معروف صدقة تسعد القلوب وتفرج الهموم والكروب.", "ما نوع الياء في وسط كلمة (خَيْر)؟", "ياء لينة ساكنة", "ياء لينة ساكنة", "ياء مد طبيعي", "ياء مشددة"),
                AgeSevenPlayLetter("د", "دُعَاء", "🤲", "الدعاء والعبادة", "الدعاء هو العبادة، وهو صلة مباشرة وجميلة بين المؤمن بربه في كل الأوقات والأحوال.", "ما نوع الهمزة المتطرفة في نهاية الكلمة (دُعَاء)؟", "همزة على السطر", "همزة على السطر", "همزة على الواو", "همزة على الياء"),
                AgeSevenPlayLetter("ذ", "ذِكْر", "📿", "الذكر الدائم في القلوب", "ألا بذكر الله تطمئن القلوب الشاكرة، فذكر الله ينير الصدر واللسان ويطرد الشيطان.", "ما هي الحركة النطقية لحرف الكاف الساكن في كلمة (ذِكْر)؟", "السكون (◌ْ)", "السكون (◌ْ)", "الفتحة (◌َ)", "الكسرة (◌ِ)"),
                AgeSevenPlayLetter("ض", "ضَمِير", "🧘", "الضمير الحي الموجه", "الضمير الحي يوجه المسلم لقول الصدق والعمل الصالح ومراقبة ربه دائمًا بوعي ويقين.", "ما هو المد الصوتي المتوسط في كلمة (ضَمِير)؟", "مد الياء", "مد الياء", "مد الألف", "مد الواو"),
                AgeSevenPlayLetter("ط", "طَاعَة", "🙏", "طاعة الرحمن وبر الوالدين", "طاعة الله ورسوله وبر الوالدين تجلب البركة والسعادة والرضا وتفتح أبواب الجنة العالية.", "عند وقفنا الصوتي على كلمة (طَاعَة)، كيف ننطق الحرف الأخير؟", "ينطق هاءً ساكنة", "ينطق هاءً ساكنة", "ينطق تاءً صريحة", "ينطق ألفاً ممدودة"),
                AgeSevenPlayLetter("ظ", "ظَفَر", "🏆", "الظفر بالخير والنجاح", "الظفر بالخير والنجاح يأتي بالصبر والعمل الدؤوب والتوكل التام على الله عز وجل في كل مسعى.", "ما هي حركة حرف الفاء في كلمة (ظَفَر)؟", "الفتحة (◌َ)", "الفتحة (◌َ)", "الضمة (◌ُ)", "الكسرة (◌ِ)"),
                AgeSevenPlayLetter("ع", "عَدْل", "⚖️", "العدل والإنصاف", "العدل أساس الصلاح والخير، والمسلم يعامل الجميع بالعدل والمساواة والإنصاف والرفق.", "ما هي علامة الإعراب لاسم (عَدْلٌ) في جملة \"هذا عَدْلٌ\"؟", "الضمة الظاهرة", "الضمة الظاهرة", "الفتحة المقدرة", "السكون الأخير"),
                AgeSevenPlayLetter("غ", "غُفْرَان", "🤲", "غفران الله الواسع للذنوب", "نرجو غفران الله ورحمته في كل حين، فالله هو الغفور التواب الرحيم بعباده الصادقين.", "ما الوزن الصرفي لكلمة (غُفْرَان)؟", "فُعْلَان", "فُعْلَان", "فِعْلَان", "فَعَلَان"),
                AgeSevenPlayLetter("ف", "فَلَاح", "🌟", "الفلاح والنجاح الحقيقي", "الفلاح والنجاح الحقيقي هو الفوز برضا الله وجنته بالعمل الصالح ومكارم الأخلاق الطيبة.", "ما هو الحرف الممدود بمد الألف في كلمة (فَلَاح)؟", "اللام", "اللام", "الفاء", "الحاء"),
                AgeSevenPlayLetter("ق", "قَلْب", "💖", "القلب النقي السليم", "القلب النقي الخالي من الغل والحسد هو موضع نظر الله والإيمان الصادق والمحبة الخالصة.", "ما هي حركة حرف اللام في كلمة (قَلْب)؟", "السكون (◌ْ)", "السكون (◌ْ)", "الفتحة (◌َ)", "الضمة (◌ُ)"),
                AgeSevenPlayLetter("ك", "كِتَاب", "📚", "الكتاب والتعلم", "الكتاب صديق طيب ومعلم مفيد ينير عقولنا بالمعرفة النافعة والآداب الراقية والقصص المعبرة.", "ما نوع التنوين في نهاية كلمة (كِتَابٌ)؟", "تنوين ضم", "تنوين ضم", "تنوين فتح", "تنوين كسر"),
                AgeSevenPlayLetter("ل", "لُطْف", "🍦", "اللطف والرفق مع الجميع", "اللطف والرفق في التعامل يكسب القلوب وينشر المحبة والسلام والوئام في كل مكان.", "ما هي حركة حرف الطاء في كلمة (لُطْف)؟", "السكون (◌ْ)", "السكون (◌ْ)", "الفتحة (◌َ)", "الضمة (◌ُ)"),
                AgeSevenPlayLetter("م", "مَحَبَّة", "💛", "المحبة والوئام", "المحبة في الله تجمع القلوب الصافية وتصنع مجتمعًا مترابطًا ومتعاونًا بالخير والرحمة.", "ما هو ضبط حرف الحاء في كلمة (مَحَبَّة)؟", "الفتحة (◌َ)", "الفتحة (◌َ)", "الكسرة (◌ِ)", "الضمة (◌ُ)"),
                AgeSevenPlayLetter("ن", "نِعْمَة", "🎁", "نعم الله الكثيرة", "نعم الله علينا لا تعد ولا تحصى، وشكر النعمة بلساننا وعملنا يزيدها ويبقيها بفضل الله ورحمته.", "ما نوع اللام عند إدخالها على (نِعْمَة) لتصبح (النِّعْمَة)؟", "لام شمسية تكتب ولا تلفظ", "لام شمسية تكتب ولا تلفظ", "لام قمرية", "لام أصلية"),
                AgeSevenPlayLetter("هـ", "هِدَايَة", "🚀", "الهداية وصراط المستقيم", "الهداية صراط مستقيم نسأله من الله سبحانه في كل ركعة من صلواتنا اليومية وعباداتنا.", "ما هو الحرف الممدود بمد الألف في كلمة (هِدَايَة)؟", "الدال", "الدال", "الياء", "الهاء"),
                AgeSevenPlayLetter("و", "وُدّ", "🤝", "الود والتراحم", "الود والتراحم بين الناس يبني جسورًا قوية ومتينة من التعاطف والمحبة الخالصة الطيبة والترابط.", "ما هي العلامة المميزة فوق حرف الدال في كلمة (وُدٌّ)؟", "الشدة مع الضم والتنوين", "الشدة مع الضم والتنوين", "الفتحة", "الكسرة"),
                AgeSevenPlayLetter("ي", "يُسْر", "⚡", "اليسر والفرج", "إن مع العسر يسراً، فكل ضيق يعقبه فرج عظيم ويسر من الله الكريم الحكيم واللطيف بعباده.", "ما هي حركة حرف الياء في كلمة (يُسْر)؟", "الضمة (◌ُ)", "الضمة (◌ُ)", "الفتحة (◌َ)", "الكسرة (◌ِ)")
            )
        } else {
            listOf(
                AgeSevenPlayLetter("A", "Akhlaq", "👑", "Noble Character", "Akhlaq refers to having noble character and pleasant behaviors with everyone. Good Akhlaq is highly prized in Islam.", "Which word is a synonym of Akhlaq describing polite behavior?", "Politeness", "Politeness", "Anger", "Rudeness"),
                AgeSevenPlayLetter("B", "Barakah", "🌾", "Divine Blessings", "Barakah is the spiritual blessing from Allah that increases goodness, happiness, and value in our time, wealth, and home.", "Identify the suffix in the adjective form 'Blessings'?", "s", "s", "ing", "ness"),
                AgeSevenPlayLetter("C", "Compassion", "🕊️", "Merciful Care", "Compassion is showing mercy, softness, and kind support to all creatures, including birds, plants, and other humans.", "In 'She acted with compassion', what part of speech is 'compassion'?", "Noun", "Noun", "Verb", "Adjective"),
                AgeSevenPlayLetter("D", "Dhikr", "📿", "Remembrance", "Dhikr means keeping our hearts and tongues busy with the beautiful remembrance of Allah, bringing peace to our souls.", "Dhikr is a noun of action. Which active verb means 'to remember' in English?", "Remember", "Remember", "Remembrance", "Memory"),
                AgeSevenPlayLetter("E", "Empathy", "🤝", "Understanding Hearts", "Empathy is putting ourselves in others' situations, understanding their feelings, and helping them with sweet concern.", "What is the adjective form of 'Empathy' describing an understanding person?", "Empathetic", "Empathetic", "Empathy", "Empathetically"),
                AgeSevenPlayLetter("F", "Forgiveness", "🤲", "Maghfirah", "Forgiveness is pardon and mercy. Allah is most forgiving, and He loves when we forgive others' small mistakes.", "In the noun 'Forgiveness', which suffix means 'the state of being'?", "ness", "ness", "ive", "ful"),
                AgeSevenPlayLetter("G", "Generosity", "🎁", "Sakhawah", "Generosity is giving happily from our resources, sharing food, toys, and help with those who need it.", "Which of these is the correct adjective form of 'Generosity'?", "Generous", "Generous", "Generously", "Generosity"),
                AgeSevenPlayLetter("H", "Humility", "🌱", "Tawadu'", "Humility is being humble and avoiding pride. A humble person respects everyone and knows that all blessings come from Allah.", "What is the opposite antonym of the moral quality 'Humility'?", "Pride", "Pride", "Kindness", "Modesty"),
                AgeSevenPlayLetter("I", "Ihsan", "✨", "Perfect Actions", "Ihsan is doing every action beautifully and perfectly, knowing that Allah is watching over all of our efforts.", "Which English word best translates the high standard of 'Ihsan'?", "Excellence", "Excellence", "Speed", "Doubt"),
                AgeSevenPlayLetter("J", "Justice", "⚖️", "Adl and Fairness", "Justice means being fair, honest, and giving everyone their rightful due. It builds a beautiful, balanced community.", "What prefix turns the positive word 'Justice' into its opposite?", "In", "In", "Dis", "Un"),
                AgeSevenPlayLetter("K", "Knowledge", "🎓", "Seeking Ilm", "Seeking knowledge is a mandatory duty. Useful knowledge illuminates our path and guides us to do good deeds.", "Which root verb is 'Knowledge' derived from in English?", "Know", "Know", "Known", "Knowing"),
                AgeSevenPlayLetter("L", "Love", "💖", "Mahabbah", "Love for the sake of Allah means wishing goodness, peace, and Jannah for all brothers, sisters, and friends.", "In the sentence 'We love good deeds', what grammatical role does 'love' play?", "Verb", "Verb", "Noun", "Adjective"),
                AgeSevenPlayLetter("M", "Mercy", "🐈", "Rahmah", "Prophet Muhammad (PBUH) was sent as a mercy to the worlds. Showing mercy to animals and children brings Allah's mercy.", "What is the correct adjective form of the beautiful quality 'Mercy'?", "Merciful", "Merciful", "Mercifully", "Merciless"),
                AgeSevenPlayLetter("N", "Neighbor", "🏡", "Jiwar Ties", "Islam teaches us to be incredibly kind, helpful, and generous to our neighbors, keeping them safe from any harm.", "What suffix can make 'Neighbor' a noun for the whole neighborhood community?", "hood", "hood", "ness", "ship"),
                AgeSevenPlayLetter("O", "Optimism", "☀️", "Husn al-Zann", "Optimism means thinking good thoughts about Allah's plans and having high hopes for the future.", "What adjective describes a child full of high optimism?", "Optimistic", "Optimistic", "Optimism", "Optimistically"),
                AgeSevenPlayLetter("P", "Patience", "⏳", "Sabr", "Patience is keeping calm and trusting Allah during difficult times. Allah is always with those who are patient.", "What is the adjective form of 'Patience' describing a calm person?", "Patient", "Patient", "Patience", "Patiently"),
                AgeSevenPlayLetter("Q", "Qalb", "🌸", "Pure Heart", "Qalb means the heart. A clean, pure heart full of belief and free from anger is loved by Allah.", "In the phrase 'a pure qalb', what part of speech is 'pure'?", "Adjective", "Adjective", "Noun", "Verb"),
                AgeSevenPlayLetter("R", "Righteousness", "🎖️", "Al-Birr", "Righteousness means believing in Allah and doing good deeds, helping relatives, and keeping promises.", "In 'Righteousness', which suffix denotes an abstract noun state of being?", "ness", "ness", "ous", "ly"),
                AgeSevenPlayLetter("S", "Smiling", "😊", "Sadaqah Smile", "Smiling at your brother or friend is a beautiful act of charity (Sadaqah) that spreads peace and joy.", "In 'Smiling brings joy', what grammatical role does the gerund 'Smiling' play?", "Subject", "Subject", "Object", "Adjective"),
                AgeSevenPlayLetter("T", "Trust", "⚓", "Tawakkul", "Trusting Allah (Tawakkul) means doing our absolute best and then leaving the results to Allah's wisdom.", "What suffix can make 'Trust' an adjective meaning reliable?", "worthy", "worthy", "ness", "fully"),
                AgeSevenPlayLetter("U", "Ummah", "🤝", "One Global Family", "The Ummah is the global community of all Muslims, united in love, helping each other like one body.", "What is the primary meaning of the Arabic word 'Ummah'?", "Community", "Community", "Nation", "City"),
                AgeSevenPlayLetter("V", "Valor", "🛡️", "Shaja'ah", "Valor means having the courage to speak the truth, protect the weak, and stand up for fairness.", "Which of these is a synonym for 'Valor' in English?", "Courage", "Courage", "Fear", "Sadness"),
                AgeSevenPlayLetter("W", "Worship", "🛐", "Pure Ibadah", "Worship is the primary purpose of our lives. It includes praying, fasting, and helping people to please Allah.", "Which suffix completes the noun: 'worship_ _ _ _' describing a person?", "per", "per", "ing", "ness"),
                AgeSevenPlayLetter("X", "eXexemplar", "🌟", "Uswah Hasanah", "An exemplar is a perfect model. Prophet Muhammad (PBUH) is our supreme exemplar in manners and life.", "What does the word 'exemplar' mean in simple English?", "Role model", "Role model", "Helper", "Friend"),
                AgeSevenPlayLetter("Y", "Yearning", "💛", "Shawq for Good", "Yearning means having a deep, loving desire in our hearts to please Allah and meet Him in Jannah.", "What suffix turns the type 'Yearn' into the continuous action noun 'Yearning'?", "ing", "ing", "ness", "ly"),
                AgeSevenPlayLetter("Z", "Zakat", "💰", "Wealth Purifying", "Zakat is giving a small portion of our savings to the poor, purifying our wealth and spreading equity.", "Which word has a matching meaning with the holy obligation 'Zakat'?", "Almsgiving", "Almsgiving", "Buying", "Selling")
            )
        }
    }

    var selectedLetter by remember(isArabic) { mutableStateOf(ageSevenLetters.first()) }
    var selectedAnswer by remember(selectedLetter) { mutableStateOf<String?>(null) }
    var pointsEarned by remember(selectedLetter) { mutableStateOf(0) }

    fun speakFullLesson(letter: AgeSevenPlayLetter) {
        tts?.let { t ->
            val lang = if (isArabic) Locale("ar", "SA") else Locale("en", "IN")
            val result = t.setLanguage(lang)
            if (isArabic && (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)) {
                t.language = Locale("ar")
            } else {
                t.language = lang
            }
            val speechText = "${letter.word}. ${letter.lessonText}. ${letter.question}"
            t.speak(sanitizeSpeechText(speechText), TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    LaunchedEffect(selectedLetter, ttsReady) {
        if (ttsReady) {
            speakFullLesson(selectedLetter)
        }
    }

    val scrollState = rememberScrollState()

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FIXED ON TOP: Rectangular compact preview card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, Color(0xFF1565C0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = selectedLetter.icon,
                                fontSize = 32.sp
                            )
                            Column {
                                Text(
                                    text = if (isArabic) selectedLetter.char else "${selectedLetter.char.uppercase()} ${selectedLetter.char.lowercase()}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0D47A1)
                                )
                                Text(
                                    text = "${selectedLetter.word} • ${selectedLetter.title}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                        Button(
                            onClick = { speakFullLesson(selectedLetter) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3F2FD)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("🗣️ ", fontSize = 14.sp)
                            Text(
                                text = if (isArabic) "الحكمة" else "Wisdom",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1),
                                fontSize = 11.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedLetter.lessonText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723),
                        lineHeight = 15.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF3E0), RoundedCornerShape(6.dp))
                            .padding(6.dp)
                    )
                }
            }

            // SCROLLABLE CONTAINER:
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("👑 ", fontSize = 18.sp)
                            Text(
                                text = if (isArabic) "المستوى ٥: سياق الجمل وقواعد الكلمات الشريفة!" else "Level 5: Sentence Context & Grammar Mastery!",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isArabic) "اقرأ العبر وحل أسئلة قواعد النحو والصرف لجمع عسل النور بي! 🐝" else "Examine the moral lessons & solve standard language structures to earn Honey Stars! 🐝",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(2.dp, Color(0xFF90CAF9))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isArabic) "🧠 تحدي القواعد والمفردات والترتيب 🧠" else "🧠 Sentence Grammar & Word Challenge 🧠",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0D47A1),
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = selectedLetter.question,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1565C0),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(selectedLetter.o1, selectedLetter.o2, selectedLetter.o3).forEach { option ->
                                val isCorrect = option == selectedLetter.correctWord
                                val isChosen = selectedAnswer == option
                                val btnBg = when {
                                    isChosen && isCorrect -> Color(0xFF2E7D32)
                                    isChosen && !isCorrect -> Color(0xFFC62828)
                                    else -> Color.White
                                }
                                val btnTextCol = if (isChosen) Color.White else Color(0xFF0D47A1)

                                Card(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .height(50.dp)
                                        .weight(1f)
                                        .shadow(2.dp, RoundedCornerShape(12.dp))
                                        .clickable {
                                            selectedAnswer = option
                                            if (isCorrect) {
                                                pointsEarned = 10
                                                tts?.let { t ->
                                                    val l = if (isArabic) Locale("ar") else Locale("en", "IN")
                                                    t.language = l
                                                    t.speak(if (isArabic) "ما شاء الله! إجابة متميزة تدل على ذكائك!" else "Excellent reflection! That is absolutely right!", TextToSpeech.QUEUE_FLUSH, null, null)
                                                }
                                            } else {
                                                pointsEarned = 0
                                                tts?.let { t ->
                                                    val l = if (isArabic) Locale("ar") else Locale("en", "IN")
                                                    t.language = l
                                                    t.speak(if (isArabic) "فكر جيداً بتركيب الجملة وحاول يا صغاري!" else "Read carefully, let's try again, champion!", TextToSpeech.QUEUE_FLUSH, null, null)
                                                }
                                            }
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = btnBg),
                                    border = BorderStroke(1.5.dp, Color(0xFF90CAF9))
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = option,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = btnTextCol,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (selectedAnswer != null) {
                            val isCorrect = selectedAnswer == selectedLetter.correctWord
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = if (isCorrect) "👑 🍯 " else "✏️ ",
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = if (isCorrect) {
                                        if (isArabic) "رائع! لقد أحرزت تقدمًا وحصلت على +15 عسل للنجاح! 🐝" else "Brilliant grammar resolution! Earned +15 Honey Stars! 🐝"
                                    } else {
                                        if (isArabic) "راجع الحركات والضبط الصرفي للكلمة!" else "Check your word endings, you can do it!"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        } else {
                            Text(
                                text = if (isArabic) "حل اللغز النحوي لترضي ربك وتجمع عسلاً لذيذاً!" else "Examine the word options and tap to place the correct answer!",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Text(
                    text = if (isArabic) "اختر الحرف لتبحر بسياقه وقواعده: 📖" else "Tap any letters to view their sentence context: 📖",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ageSevenLetters) { letter ->
                        val isSelected = selectedLetter == letter
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF90CAF9) else Color(0xFFECEFF1))
                                .border(2.dp, if (isSelected) Color(0xFF0D47A1) else Color.Transparent, CircleShape)
                                .clickable {
                                    selectedLetter = letter
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter.char,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.White else Color(0xFF37474F)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onStartAdventure,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(50.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                ) {
                    Text("🍿 ", fontSize = 18.sp)
                    Text(
                        text = if (isArabic) "أكملت الدرس بنجاح! صب العسل 🍯" else "Finish Lesson & Pour Sweet Honey! 🍯",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
            }
        }
    }



data class AgeFivePlayLetter(
    val char: String,
    val word: String,
    val phraseWithBlank: String,
    val fullPhrase: String,
    val correctWord: String,
    val icon: String,
    val o1: String,
    val o2: String,
    val o3: String
)

@Composable
fun AgeFiveAlphabetBoard(
    isArabic: Boolean,
    onStartAdventure: () -> Unit
) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }

    DisposableEffect(context, isArabic) {
        val instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
            }
        }
        tts = instance
        onDispose {
            instance.stop()
            instance.shutdown()
        }
    }

    val ageFiveLetters = remember(isArabic) {
        if (isArabic) {
            listOf(
                AgeFivePlayLetter("أ", "الله", "اللَّهُ رَبِّي وَهُوَ ____ كُلِّ شَيْءٍ 🌍", "اللَّهُ رَبِّي وَهُوَ خَالِقُ كُلِّ شَيْءٍ 🌍", "خَالِقُ", "☝️", "خَالِقُ", "صَاحِبُ", "قَرِيبُ"),
                AgeFivePlayLetter("ب", "بسم الله", "نَقُولُ ____ فِي بِدَايَةِ كُلِّ عَمَلٍ 🍽️", "نَقُولُ بِسْمِ اللَّهِ فِي بِدَايَةِ كُلِّ عَمَلٍ 🍽️", "بِسْمِ اللَّهِ", "🍽️", "بِسْمِ اللَّهِ", "الْحَمْدُ لله", "سُبْحَانَ الله"),
                AgeFivePlayLetter("ت", "تقوى", "التَّقْوَى تَعْنِي أَنْ نَعْمَلَ بِـ ____ اللَّهِ دَائِمَاً 💖", "التَّقْوَى تَعْنِي أَنْ نَعْمَلَ بِـ طَاعَةِ اللَّهِ دَائِمَاً 💖", "طَاعَةِ", "💖", "طَاعَةِ", "مَعْصِيَةِ", "نِسْيَانِ"),
                AgeFivePlayLetter("ث", "ثواب", "الْمُسْلِمُ الْمُتَعَاوِنُ يَنَالُ ____ مِنَ اللَّهِ 🏆", "الْمُسْلِمُ الْمُتَعَاوِنُ يَنَالُ ثَوَاباً مِنَ اللَّهِ 🏆", "ثَوَاباً", "🏆", "ثَوَاباً", "عِقَاباً", "خَسَارَةً"),
                AgeFivePlayLetter("ج", "جنة", "نَسْأَلُ اللَّهَ أَنْ يُدْخِلَنَا ____ النَّعِيمِ 🌺", "نَسْأَلُ اللَّهَ أَنْ يُدْخِلَنَا جَنَّةَ النَّعِيمِ 🌺", "جَنَّةَ", "🌺", "جَنَّةَ", "نَارَ", "بُسْتَانَ"),
                AgeFivePlayLetter("ح", "حج", "نَطُوفُ حَوْلَ الْكَعْبَةِ فِي مَوْسِمِ الْـ ____ 🕋", "نَطُوفُ حَوْلَ الْكَعْبَةِ فِي مَوْسِمِ الْـ حَجِّ 🕋", "حَجِّ", "🕋", "حَجِّ", "صَوْمِ", "عِيدِ"),
                AgeFivePlayLetter("خ", "خير", "أَنْتَ طِفْلٌ طَيِّبٌ تُحِبُّ الْـ ____ لِإِخْوَانِكَ ✨", "أَنْتَ طِفْلٌ طَيِّبٌ تُحِبُّ الْـ خَيْرَ لِإِخْوَانِكَ ✨", "خَيْرَ", "✨", "خَيْرَ", "شَرَّ", "غَضَبَ"),
                AgeFivePlayLetter("د", "دعاء", "نَرْفَعُ أَيْدِينَا إِلَى السَّمَاءِ لِلـ ____ 🤲", "نَرْفَعُ أَيْدِينَا إِلَى السَّمَاءِ لِلـ دُعَاءِ 🤲", "دُعَاءِ", "🤲", "دُعَاءِ", "لَّعِبِ", "صُّرَاخِ"),
                AgeFivePlayLetter("ذ", "ذكر", "قَلْبُ الْمُؤْمِنِ مَلِيءٌ بِـ ____ اللَّهِ الْعَزِيزِ 📿", "قَلْبُ الْمُؤْمِنِ مَلِيءٌ بِـ ذِكْرِ اللَّهِ الْعَزِيزِ 📿", "ذِكْرِ", "📿", "ذِكْرِ", "نَوْمِ", "كَذِبِ"),
                AgeFivePlayLetter("ر", "رمضان", "شَهْرُ الصِّيَامِ وَالْقُرْآنِ هُوَ شَهْرُ ____ 🌙", "شَهْرُ الصِّيَامِ وَالْقُرْآنِ هُوَ شَهْرُ رَمَضَانَ 🌙", "رَمَضَانَ", "🌙", "رَمَضَانَ", "شَعْبَانَ", "شَوَّالَ"),
                AgeFivePlayLetter("ز", "زكاة", "نُعْطِي ____ لِلْفُقَرَاءِ لِيَكُونَ مَالُنَا مُبَارَكاً 💰", "نُعْطِي زَكَاةً لِلْفُقَرَاءِ لِيَكُونَ مَالُنَا مُبَارَكاً 💰", "زَكَاةً", "💰", "زَكَاةً", "أَلْعَاباً", "أَحْجَاراً"),
                AgeFivePlayLetter("س", "سجود", "نَضَعُ جَبْهَتَنَا عَلَى الْأَرْضِ أَثْنَاءِ الْـ ____ 🛐", "نَضَعُ جَبْهَتَنَا عَلَى الْأَرْضِ أَثْنَاءِ الْـ سُجُودِ 🛐", "سُجُودِ", "🛐", "سُجُودِ", "وُقُوفِ", "جُلُوسِ"),
                AgeFivePlayLetter("ش", "شكر", "نَقُولُ الْحَمْدُ لِلَّهِ لِكَيْ نُعَبِّرَ عَنِ الْـ ____ 🙌", "نَقُولُ الْحَمْدُ لِلَّهِ لِكَيْ نُعَبِّرَ عَنِ الْـ شُكْرِ 🙌", "شُكْرِ", "🙌", "شُكْرِ", "إِهْمَالِ", "غَضَبِ"),
                AgeFivePlayLetter("ص", "صلاة", "نَتَوَجَّهُ نَحْوَ الْكَعْبَةِ عِنْدَ أَدَاءِ الْـ ____ 🕌", "نَتَوَجَّهُ نَحْوَ الْكَعْبَةِ عِنْدَ أَدَاءِ الْـ صَّلَاةِ 🕌", "صَّلَاةِ", "🕌", "صَّلَاةِ", "قِرَاءةِ", "لَّعِبِ"),
                AgeFivePlayLetter("ض", "ضحى", "صَلَاةُ رَكْعَتَيْنِ فِي الصَّبَاحِ هِيَ صَلَاةُ الْـ ____ ☀️", "صَلَاةُ رَكْعَتَيْنِ فِي الصَّبَاحِ هِيَ صَلَاةُ الْـ ضُحَى ☀️", "ضُحَى", "☀️", "ضُحَى", "مَغْرِبِ", "عِشَاءِ"),
                AgeFivePlayLetter("ط", "طهارة", "الْمُسْلِمُ يُحَافِظُ عَلَى نَظَافَتِهِ وَ ____ بَدَنِهِ 💧", "الْمُسْلِمُ يُحَافِظُ عَلَى نَظَافَتِهِ وَ طَهَارَةِ بَدَنِهِ 💧", "طَهَارَةِ", "💧", "طَهَارَةِ", "تَوْسِيخِ", "كَذِبِ"),
                AgeFivePlayLetter("ظ", "ظهر", "نُصَلِّي أَرْبَعَ رَكَعَاتٍ فِي مُنْتَصَفِ النَّهَارِ فِي صَلَاةِ الْـ ____ ☀️", "نُصَلِّي أَرْبَعَ رَكَعَاتٍ فِي مُنْتَصَفِ النَّهَارِ فِي صَلَاةِ الْـ ظُهْرِ ☀️", "ظُهْرِ", "☀️", "ظُهْرِ", "فَجْرِ", "عَصْرِ"),
                AgeFivePlayLetter("ع", "عدل", "يَحْكُمُ الْقَاضِي الصَّالِحُ بَيْنَ النَّاسِ بِالْـ ____ ⚖️", "يَحْكُمُ الْقَاضِي الصَّالِحُ بَيْنَ النَّاسِ بِالْـ عَدْلِ ⚖️", "عَدْلِ", "⚖️", "عَدْلِ", "ظُّلْمِ", "غَضَبِ"),
                AgeFivePlayLetter("غ", "غفران", "يَمْحُو اللَّهُ ذُنُوبَنَا بِـ ____ وَرَحْمَتِهِ الْوَاسِعَةِ 🤲", "يَمْحُو اللَّهُ ذُنُوبَنَا بِـ غُفْرَانِهِ وَرَحْمَتِهِ الْوَاسِعَةِ 🤲", "غُفْرَانِهِ", "🤲", "غُفْرَانِهِ", "غَضَبِهِ", "تَعْذِيبِهِ"),
                AgeFivePlayLetter("ف", "فجر", "أَوَّلُ صَلَاةٍ نَبْدَأُ بِهَا يَوْمَنَا الرَّائِعَ هِيَ الْـ ____ 🌅", "أَوَّلُ صَلَاةٍ نَبْدَأُ بِهَا يَوْمَنَا الرَّائِعَ هِيَ الْـ فَجْرُ 🌅", "فَجْرُ", "🌅", "فَجْرُ", "ظُّهْرُ", "مَغْرِبُ"),
                AgeFivePlayLetter("ق", "قرآن", "كَلَامُ اللَّهِ الْكَرِيمُ الَّذِي نَقْرَأُهُ هُوَ الْـ ____ 📗", "كَلَامُ اللَّهِ الْكَرِيمُ الَّذِي نَقْرَأُهُ هُوَ الْـ قُرْآنُ 📗", "قُرْآنُ", "📗", "قُرْآنُ", "شِعْرُ", "قِصَّةُ"),
                AgeFivePlayLetter("ك", "كعبة", "الْمَسْجِدُ الْحَرَامُ فِيهِ ____ الْمُشَرَّفَةُ 🕋", "الْمَسْجِدُ الْحَرَامُ فِيهِ كَعْبَةُ الْمُشَرَّفَةُ 🕋", "كَعْبَةُ", "🕋", "كَعْبَةُ", "قُبَّةُ", "مَنَارَةُ"),
                AgeFivePlayLetter("ل", "ليلة القدر", "أَفْضَلُ لَيْلَةٍ فِي السَّنَةِ هِيَ ____ 🌌", "أَفْضَلُ لَيْلَةٍ فِي السَّنَةِ هِيَ لَيْلَةُ الْقَدْرِ 🌌", "لَيْلَةُ الْقَدْرِ", "🌌", "لَيْلَةُ الْقَدْرِ", "الْعِيدِ", "الْجُمُعَةِ"),
                AgeFivePlayLetter("م", "مسجد", "نَجْتَمِعُ مَعَ الْمُسْلِمِينَ لِلْجَمَاعَةِ فِي الْـ ____ 🕌", "نَجْتَمِعُ مَعَ الْمُسْلِمِينَ لِلْجَمَاعَةِ فِي الْـ مَسْجِدِ 🕌", "مَسْجِدِ", "🕌", "مَسْجِدِ", "سُّوقِ", "بَيْتِ"),
                AgeFivePlayLetter("ن", "نبي", "مُحَمَّدٌ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ هُوَ نَبِيُّ ____ 📖", "مُحَمَّدٌ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ هُوَ نَبِيُّ الرَّحْمَةِ 📖", "الرَّحْمَةِ", "📖", "الرَّحْمَةِ", "الْقُوَّةِ", "الْغَضَبِ"),
                AgeFivePlayLetter("هـ", "هداية", "نَسْأَلُ اللَّهَ أَنْ يُنِيرَ طَرِيقَنَا بِالْـ ____ دَائِمَاً 🌟", "نَسْأَلُ اللَّهَ أَنْ يُنِيرَ طَرِيقَنَا بِالْـ هِدَايَةِ دَائِمَاً 🌟", "هِدَايَةِ", "🌟", "هِدَايَةِ", "تَّيْهِ", "ظَّلَامِ"),
                AgeFivePlayLetter("و", "وضوء", "نَتَطَهَّرُ بِالْمَاءِ وَالْـ ____ لِنَقِفَ بَيْنَ يَدَيِ اللَّهِ 💧", "نَتَطَهَّرُ بِالْمَاءِ وَالْـ وُضُوءِ لِنَقِفَ بَيْنَ يَدَيِ اللَّهِ 💧", "وُضُوءِ", "💧", "وُضُوءِ", "اللَّعِبِ", "تَّلَوُّثِ"),
                AgeFivePlayLetter("ي", "يقين", "الْإِيمَانُ الصَّادِقُ يَجْعَلُ فِي قَلْبِي ____ بِاللَّهِ 💛", "الْإِيمَانُ الصَّادِقُ يَجْعَلُ فِي قَلْبِي يَقِينًا بِاللَّهِ 💛", "يَقِينًا", "💛", "يَقِينًا", "شَكًّا", "خَوْفًا")
            )
        } else {
            listOf(
                AgeFivePlayLetter("A", "Allah", "Allah is the Only ____ who made the beautiful heavy skies and oceans! 🌍", "Allah is the Only Creator who made the beautiful heavy skies and oceans! 🌍", "Creator", "⭐", "Creator", "Prophet", "Angel"),
                AgeFivePlayLetter("B", "Bismillah", "We should start our meal saying ____ to seek blessings! 🍽️", "We should start our meal saying Bismillah to seek blessings! 🍽️", "Bismillah", "🍽️", "Bismillah", "Subhanallah", "Alhamdulillah"),
                AgeFivePlayLetter("C", "Charity", "Helping poor families by giving money is called ____! 🤝", "Helping poor families by giving money is called Charity! 🤝", "Charity", "🤝", "Charity", "Trading", "Buying"),
                AgeFivePlayLetter("D", "Dua", "Whenever we are sad or happy, we make ____ to talk to Allah! 🤲", "Whenever we are sad or happy, we make Dua to talk to Allah! 🤲", "Dua", "🤲", "Dua", "Speeches", "Plannings"),
                AgeFivePlayLetter("E", "Eid", "Eid is a wonderful day of ____ and visiting relatives! 🎉", "Eid is a wonderful day of Celebration and visiting relatives! 🎉", "Celebration", "🎉", "Celebration", "Studying", "Sleeping"),
                AgeFivePlayLetter("F", "Fajr", "The beautiful morning prayer before the sun rises is ____! 🌅", "The beautiful morning prayer before the sun rises is Fajr! 🌅", "Fajr", "🌅", "Fajr", "Dhuhr", "Maghrib"),
                AgeFivePlayLetter("G", "Ghusl", "We take a special pure bath called ____ to stay clean! 🚿", "We take a special pure bath called Ghusl to stay clean! 🚿", "Ghusl", "🚿", "Ghusl", "Wudu", "Sajdah"),
                AgeFivePlayLetter("H", "Hajj", "Muslims travel to the holy city of Makkah for ____! 🕋", "Muslims travel to the holy city of Makkah for Hajj! 🕋", "Hajj", "🕋", "Hajj", "Eid", "Ramadan"),
                AgeFivePlayLetter("I", "Iman", "Iman means having complete ____ in Allah and His messages! 💚", "Iman means having complete Faith in Allah and His messages! 💚", "Faith", "💚", "Faith", "Doubts", "Fears"),
                AgeFivePlayLetter("J", "Jannah", "Allah made the gorgeous garden of ____ for kind and helpful souls! 🌺", "Allah made the gorgeous garden of Jannah for kind and helpful souls! 🌺", "Jannah", "🌺", "Jannah", "Earth", "Space"),
                AgeFivePlayLetter("K", "Kaaba", "We direct our daily prayers facing the sacred ____! 🕋", "We direct our daily prayers facing the sacred Kaaba! 🕋", "Kaaba", "🕋", "Kaaba", "Masjid", "Castle"),
                AgeFivePlayLetter("L", "Laylatul Qadr", "The search for the blessed night of ____ happens in Ramadan! 🌙", "The search for the blessed night of Laylatul Qadr happens in Ramadan! 🌙", "Laylatul Qadr", "🌙", "Laylatul Qadr", "Mubarak", "Jummah"),
                AgeFivePlayLetter("M", "Masjid", "The mosque where Muslims join together in prayer is a ____! 🕌", "The mosque where Muslims join together in prayer is a Masjid! 🕌", "Masjid", "🕌", "Masjid", "School", "Library"),
                AgeFivePlayLetter("N", "Nabi", "A holy Prophet chosen by Allah to guide us is a ____! 📖", "A holy Prophet chosen by Allah to guide us is a Nabi! 📖", "Nabi", "📖", "Nabi", "King", "Scholar"),
                AgeFivePlayLetter("O", "Obedience", "Being kind and showing ____ to our parents is highly rewarded! 🙏", "Being kind and showing Obedience to our parents is highly rewarded! 🙏", "Obedience", "🙏", "Obedience", "Disrespect", "Laziness"),
                AgeFivePlayLetter("P", "Prophet", "Our beloved ____ Muhammad (PBUH) taught us mercy and smiles! 🌟", "Our beloved Prophet Muhammad (PBUH) taught us mercy and smiles! 🌟", "Prophet", "🌟", "Prophet", "Caliph", "Companion"),
                AgeFivePlayLetter("Q", "Quran", "The Holy ____ is the miraculous guide revealed to mankind! 📗", "The Holy Quran is the miraculous guide revealed to mankind! 📗", "Quran", "📗", "Quran", "Hadith", "History"),
                AgeFivePlayLetter("R", "Ramadan", "We practice patience by fasting during the month of ____! 🌙", "We practice patience by fasting during the month of Ramadan! 🌙", "Ramadan", "🌙", "Ramadan", "Shawwal", "Safar"),
                AgeFivePlayLetter("S", "Salah", "Connecting with Allah five times daily is called ____! 🛐", "Connecting with Allah five times daily is called Salah! 🛐", "Salah", "🛐", "Salah", "Dua", "Zakat"),
                AgeFivePlayLetter("T", "Tawheed", "The pure belief in the oneness of Allah is called ____! ☝️", "The pure belief in the oneness of Allah is called Tawheed! ☝️", "Tawheed", "☝️", "Tawheed", "Charity", "Sincerity"),
                AgeFivePlayLetter("U", "Ummah", "The beautiful world community of all Muslims is the ____! 🤝", "The beautiful world community of all Muslims is the Ummah! 🤝", "Ummah", "🤝", "Ummah", "Tribe", "Continent"),
                AgeFivePlayLetter("V", "Virtue", "Always speaking truth and helping friends shows strong ____! 💖", "Always speaking truth and helping friends shows strong Virtue! 💖", "Virtue", "💖", "Virtue", "Anger", "Pride"),
                AgeFivePlayLetter("W", "Wudu", "We wash our hands and face doing ____ before praying! 💧", "We wash our hands and face doing Wudu before praying! 💧", "Wudu", "💧", "Wudu", "Ghusl", "Tayammum"),
                AgeFivePlayLetter("X", "eXcellence", "Doing our absolute best in all deeds is Islamic ____! ✨", "Doing our absolute best in all deeds is Islamic Excellence! ✨", "Excellence", "✨", "Excellence", "Playtime", "Hurrying"),
                AgeFivePlayLetter("Y", "Yaqeen", "Yaqeen means holding complete ____ that Allah always saves us! 💛", "Yaqeen means holding complete certainty that Allah always saves us! 💛", "certainty", "💛", "certainty", "doubts", "worries"),
                AgeFivePlayLetter("Z", "Zakat", "Sharing a part of our savings with the needy is ____! 💰", "Sharing a part of our savings with the needy is Zakat! 💰", "Zakat", "💰", "Zakat", "Tax", "Bargain")
            )
        }
    }

    var selectedLetter by remember(isArabic) { mutableStateOf(ageFiveLetters.first()) }
    var selectedAnswer by remember(selectedLetter) { mutableStateOf<String?>(null) }
    var pointsEarned by remember(selectedLetter) { mutableStateOf(0) }

    fun speakFullSentence(letter: AgeFivePlayLetter) {
        tts?.let { t ->
            val lang = if (isArabic) Locale("ar", "SA") else Locale("en", "IN")
            val result = t.setLanguage(lang)
            if (isArabic && (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)) {
                t.language = Locale("ar")
            } else {
                t.language = lang
            }
            t.speak(sanitizeSpeechText(letter.fullPhrase), TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    LaunchedEffect(selectedLetter, ttsReady) {
        if (ttsReady) {
            speakFullSentence(selectedLetter)
        }
    }

    val scrollState = rememberScrollState()

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        val isCorrectChosen = selectedAnswer == selectedLetter.correctWord
        val textToDisplay = if (isCorrectChosen) selectedLetter.fullPhrase else selectedLetter.phraseWithBlank

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FIXED ON TOP: Rectangular compact preview card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, Color(0xFF00695C))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = selectedLetter.icon,
                                fontSize = 32.sp
                            )
                            Column {
                                Text(
                                    text = if (isArabic) selectedLetter.char else "${selectedLetter.char.uppercase()} ${selectedLetter.char.lowercase()}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF004D40)
                                )
                                Text(
                                    text = selectedLetter.word,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFFB300)
                                )
                            }
                        }
                        Button(
                            onClick = { speakFullSentence(selectedLetter) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB2DFDB)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("🗣️ ", fontSize = 14.sp)
                            Text(
                                text = if (isArabic) "الجملة" else "Sentence",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF004D40),
                                fontSize = 11.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = textToDisplay,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrectChosen) Color(0xFF2E7D32) else Color(0xFF37474F),
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            // SCROLLABLE CONTAINER:
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interactive Header Banner Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00695C)), // Deep Teal
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("🧩 ", fontSize = 18.sp)
                            Text(
                                text = if (isArabic) "المستوى ٥: صانع الجمل وبطل المفردات" else "Level 5: Sentence Builder & Vocab Master",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isArabic) "املأ الفراغات بالكلمة الإسلامية الصحيحة لتبني جملة جميلة!" else "Fill the blank with the correct Islamic word to build complete sentences!",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

            // Interactive Sentence Matching Challenge
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)), // Matching Teal-Light
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, Color(0xFF80CBC4))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isArabic) "تحدي الجملة المفقودة من نور بي! 🐝" else "Noor Bee's Sentence Builder! 🐝",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF004D40),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Options list buttons (o1, o2, o3)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(selectedLetter.o1, selectedLetter.o2, selectedLetter.o3).forEach { option ->
                            val isCorrect = option == selectedLetter.correctWord
                            val isChosen = selectedAnswer == option
                            val btnBg = when {
                                isChosen && isCorrect -> Color(0xFF2E7D32) // Green
                                isChosen && !isCorrect -> Color(0xFFC62828) // Red
                                else -> Color.White // Elegant white choice
                            }
                            val btnTextCol = if (isChosen) Color.White else Color(0xFF004D40)

                            Card(
                                modifier = Modifier
                                    .padding(horizontal = 6.dp)
                                    .height(48.dp)
                                    .weight(1f)
                                    .shadow(2.dp, RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedAnswer = option
                                        if (isCorrect) {
                                            pointsEarned = 10
                                            tts?.let { t ->
                                                val l = if (isArabic) Locale("ar") else Locale("en", "IN")
                                                t.language = l
                                                t.speak(if (isArabic) "ممتاز! بنيت الجملة بنجاح!" else "Outstanding! You built the sentence!", TextToSpeech.QUEUE_FLUSH, null, null)
                                            }
                                        } else {
                                            pointsEarned = 0
                                            tts?.let { t ->
                                                val l = if (isArabic) Locale("ar") else Locale("en", "IN")
                                                t.language = l
                                                t.speak(if (isArabic) "حاول ثانية، يا بطل!" else "Try again, you can do it!", TextToSpeech.QUEUE_FLUSH, null, null)
                                            }
                                        }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = btnBg),
                                border = BorderStroke(1.5.dp, Color(0xFF80CBC4))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = option,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = btnTextCol,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Feedback description
                    if (selectedAnswer != null) {
                        val isCorrect = selectedAnswer == selectedLetter.correctWord
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                text = if (isCorrect) "🎉 " else "✏️ ",
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (isCorrect) {
                                    if (isArabic) "رائع جداً! بنيت الجملة وحصلت على +10 عسل! 🍯" else "Wohoo! Sentence built! Got +10 Honey Stars! 🍯"
                                } else {
                                    if (isArabic) "حاول مجدداً، اقرأ الكلمات جيداً!" else "Almost! Try another word bubble, champion!"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                    } else {
                        Text(
                            text = if (isArabic) "اختر كلمة صحيحة لإكمال الجملة وبنائها بنجاح!" else "Choose the correct word to complete the sentence!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Horizontally Scrollable Letters list for navigation
            Text(
                text = if (isArabic) "اختر حرفاً لتبني جملته: 🐝" else "Choose any letter to construct sentences: 🐝",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF004D40),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ageFiveLetters) { letter ->
                    val isSelected = selectedLetter == letter
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFF80CBC4) else Color(0xFFECEFF1))
                            .border(2.dp, if (isSelected) Color(0xFF004D40) else Color.Transparent, CircleShape)
                            .clickable {
                                selectedLetter = letter
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.char,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) Color.White else Color(0xFF37474F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onStartAdventure,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp))
            ) {
                Text("🍯 ", fontSize = 18.sp)
                Text(
                    text = if (isArabic) "اكتمل درس الجمل! صب العسل" else "Finish Lesson & Claim honey!",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
        }
    }
}

data class AgeEightPlayLetter(
    val char: String,
    val word: String,
    val icon: String,
    val title: String,
    val lessonText: String,
    val question: String,
    val correctWord: String,
    val o1: String,
    val o2: String,
    val o3: String
)

@Composable
fun AgeEightAlphabetBoard(
    isArabic: Boolean,
    onStartAdventure: () -> Unit
) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }

    DisposableEffect(context, isArabic) {
        val instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
            }
        }
        tts = instance
        onDispose {
            instance.stop()
            instance.shutdown()
        }
    }

    val ageEightLetters = remember(isArabic) {
        if (isArabic) {
            listOf(
                AgeEightPlayLetter("أ", "إِخْلَاص", "💎", "الإخلاص والصدق مع الله", "الإخلاص هو تصفية العمل من كل شائبة وابتغاء رضا الله وحده دون رياء أو سمعة.", "ما الحركة الإعرابية لحرف الخاء الساكن في كلمة (إِخْلَاص)؟", "السكون (◌ْ)", "السكون (◌ْ)", "الفتحة (◌َ)", "الضمة (◌ُ)"),
                AgeEightPlayLetter("ب", "بَصِيرَة", "👁️", "البصيرة ونور العقل", "البصيرة هي نور يقذفه الله في قلب المؤمن ليميز به بين الحق والباطل والهدى والضلال.", "ما نوع التاء المربوطة في نهاية كلمة (بَصِيرَة) عند الوقف عليها؟", "تنطق هاء ساكنة", "تنطق هاء ساكنة", "تنطق تاء مفتوحة", "لا تنطق أبداً"),
                AgeEightPlayLetter("ت", "تَقْوَى", "🛡️", "التقوى ومخافة الله", "التقوى هي امتثال الأوامر واجتناب النواهي وهي خير زاد للمؤمن في الدنيا والآخرة.", "ما نوع الألف اللينة المرسومة في نهاية الكلمة الإيمانية (تَقْوَى)؟", "ألف مقصورة على صورة الياء", "ألف مقصورة على صورة الياء", "ألف ممدودة قائمة", "واو مدية"),
                AgeEightPlayLetter("ث", "ثِقَة", "⚓", "الثقة بالله والتوكل عليه", "الثقة بالله هي عين اليقين وبها تتحقق المعجزات وتنكشف الهموم والكروب بإذن الله.", "ما هو الحرف المحذوف في كلمة (ثِقَة) من أصلها اللغوي (وثق)؟", "الواو", "الواو", "الثاء", "القاف"),
                AgeEightPlayLetter("ج", "جُود", "🎁", "الجود والسخاء والندى", "الجود هو العطاء بغير سؤال وهو من أجل صفات الكرام الأخيار وأولياء الله الصالحين.", "ما نوع المد الصوتي المسموع في وسط كلمة (جُود)؟", "مد الواو", "مد الواو", "مد الألف", "مد الياء"),
                AgeEightPlayLetter("ح", "حِكْمَة", "🦉", "الحكمة وفصل الخطاب", "الحكمة هي وضع الشيء في موضعه اللائق به وهي ضالة المؤمن وعنوان رجاحة عقله.", "ما هي الحركة النطقية لحرف الكاف الساكن في كلمة (حِكْمَة)؟", "السكون (◌ْ)", "السكون (◌ْ)", "الكسرة (◌ِ)", "الفتحة (◌َ)"),
                AgeEightPlayLetter("خ", "خِدْمَة", "🤝", "خدمة الناس ونفع المجتمع", "خدمة الناس والضعفاء والمساكين صدقة مباركة تنشر المودة والسلام والأمان والوئام.", "ما نوع اللام في لفظة (الناس) عند نطقها متصلة؟", "لام شمسية تكتب ولا تلفظ", "لام شمسية تكتب ولا تلفظ", "لام قمرية", "لام أصلية"),
                AgeEightPlayLetter("د", "دَأَب", "🏋️", "الدأب والجد في العمل", "الدأب هو الاستمرار والجد والنشاط في طلب العلم وفعل الخيرات وابتغاء سبل الرشاد.", "ما الحركة الإعرابية لحرف الهمزة في وسط الكلمة (دَأَب)؟", "الفتحة (◌َ)", "الفتحة (◌َ)", "الضمة (◌ُ)", "الكسرة (◌ِ)"),
                AgeEightPlayLetter("ذ", "ذَوْق", "🌸", "الذوق الرفيع والآداب", "الذوق خلق إسلامي رفيع ينظم التعامل بين الناس بأعلى درجات اللطف والجمال والوقار.", "ما نوع الواو في وسط كلمة (ذَوْق) الساكنة المفتوح ما قبلها؟", "واو لينة", "واو لينة", "واو مدية", "واو مشددة"),
                AgeEightPlayLetter("ر", "رَحْمَة", "🕊️", "الرحمة بالخلق أجمعين", "الرحمة صفة الرحمن، وارحموا من في الأرض يرحمكم من في السماء ويدخلكم جنته.", "ما هو ضد أو عكس صفة (الرحمة) في القاموس والمعنى اللغوي؟", "القَسْوَة", "القَسْوَة", "الشدة", "العنف"),
                AgeEightPlayLetter("ز", "زَكَاة", "💰", "الزكاة وطهارة المال", "الزكاة ركن الإسلام الثالث تطهر نفوسنا من الشح وتغني الفقراء والمساكين والمحتاجين.", "ما هو الحرف الممدود بمد الألف في وسط كلمة (زَكَاة)؟", "الكاف", "الكاف", "الزاي", "التاء"),
                AgeEightPlayLetter("س", "سَلَام", "🕊️", "السلام والوئام والأمان", "السلام اسم من أسماء الله الحسنى، وإفشاء السلام ينشر الأمان والمحبة بين البرية.", "ما هو المد الصوتي المسموع في وسط كلمة (سَلَام)؟", "مد الألف", "مد الألف", "مد الياء", "مد الواو"),
                AgeEightPlayLetter("ش", "شُكْر", "🙌", "الشكر والحمد الدائم", "شكر الله على نعمه الواسعة يزيدها ويبقيها ويرفع منزلة العبد الصابر الشكور.", "ما هي حركة حرف الكاف الساكن في كلمة (شُكْر)؟", "السكون (◌ْ)", "السكون (◌ْ)", "الفتحة (◌َ)", "الضمة (◌ُ)"),
                AgeEightPlayLetter("ص", "صَبْر", "⏳", "الصبر الجميل والثبات", "الصبر ضياء وعزيمة، وبشر الصابرين الذين إذا أصابتهم مصيبة قالوا إنا لله وإنا إليه راجعون.", "ما هي حركة حرف الباء الساكن في كلمة (صَبْر)؟", "السكون (◌ْ)", "السكون (◌ْ)", "الفتحة (◌َ)", "الكسرة (◌ِ)"),
                AgeEightPlayLetter("ض", "ضِيَافَة", "☕", "ضيافة وإكرام الزوار", "الضيافة وإكرام الضيف من إيمان المسلم بالله واليوم الآخر وشيم الصالحين الأحرار.", "ما هو حرف المد الموجود في وسط الكلمة (ضِيَافَة)؟", "مد الألف", "مد الألف", "مد الياء", "مد الواو"),
                AgeEightPlayLetter("ط", "طَهَارَة", "💧", "الطهور والمحافظة على الوضوء", "الطهور شطر الإيمان، والمحافظة على طهارة البدن والقلب تجلب السعادة الدائمة للمؤمن.", "ما نوع اللام النطقية في مستهل كلمة (الـطَّهَارَة)؟", "لام شمسية تكتب ولا تلفظ", "لام شمسية تكتب ولا تلفظ", "لام قمرية", "لام أصلية ورئيسية"),
                AgeEightPlayLetter("ظ", "ظَرِيف", "✨", "الظرافة واللطف في القول", "الظريف هو اللطيف الحسن المعاملة الذي يدخل السرور على قلوب إخوانه بكلماته الطيبة.", "ما هو المد الصوتي المتوسط المسموع في كلمة (ظَرِيف)؟", "مد الياء", "مد الياء", "مد الألف", "مد الواو"),
                AgeEightPlayLetter("ع", "عَهْد", "🤝", "الوفاء بالعهد والوعد", "الوفاء بالعهود ميزة المؤمنين، والعهد كان مسؤولاً يرفع شأن صاحبه في الدنيا والآخرة.", "ما هي حركة حرف الهاء الساكن في كلمة (عَهْد)؟", "السكون (◌ْ)", "السكون (◌ْ)", "الفتحة (◌َ)", "الكسرة (◌ِ)"),
                AgeEightPlayLetter("غ", "غَيْرَة", "🛡️", "الغيرة المحمودة على الدين", "الغيرة المحمودة هي غيرة المؤمن على دين الله ومحارمه وحماية أهله وبيته بوعي ورفق.", "ما نوع الياء في وسط كلمة (غَيْرَة) الساكنة المفتوح ما قبلها؟", "ياء لينة", "ياء لينة", "ياء مدية", "ياء مشددة"),
                AgeEightPlayLetter("ف", "فِرْدَوْس", "🌸", "الفردوس الأعلى من الجنة", "الفردوس هي أعلى مراتب الجنان، نسأل الله أن يجمعنا فيها بصحبة الأنبياء والصالحين.", "ما حالة الراء الساكنة المسبوقة بكسر أصيل في كلمة (فِرْدَوْس)؟", "ترقيق الراء", "ترقيق الراء", "تفخيم الراء", "جواز الوجهين"),
                AgeEightPlayLetter("ق", "قُدْوَة", "🌟", "القدوة الحسنة والصالحة", "الرسول محمد هو قدوتنا الحسنة في الأخلاق والتعامل والوفاء والصدق والرحمة بالناس.", "ما هي حركة حرف الدال الساكن في كلمة (قُدْوَة)؟", "السكون (◌ْ)", "السكون (◌ْ)", "الفتحة (◌َ)", "الضمة (◌ُ)"),
                AgeEightPlayLetter("ك", "كِفَايَة", "🍯", "الكفاية والقناعة بالرزق", "الكفاية تعني الرضا بما قسم الله لنا والشعور بالغنى والحمد في كل النعم والظروف.", "ما نوع التاء المربوطة في نهاية كلمة (كِفَايَة)؟", "تنطق هاء عند الوقف وتاء عند الوصل", "تنطق هاء عند الوقف وتاء عند الوصل", "تنطق ألفاً ممدودة", "تكتب ولا تنطق"),
                AgeEightPlayLetter("ل", "لَهْفَة", "🔥", "اللهفة والشغف للخير", "السرعة والشغف واللهفة في أداء الصلاة وفعل المعروف من سمات المسلم الحريص على مرضاة ربه.", "ما هي حركة حرف الهاء الساكن في كلمة (لَهْفَة)؟", "السكون (◌ْ)", "السكون (◌ْ)", "الفتحة (◌َ)", "الضمة (◌ُ)"),
                AgeEightPlayLetter("م", "مُوَاسَاة", "💚", "مواساة الضعفاء ومساعدة المحتاجين", "المواساة هي تخفيف الآلام عن القلوب الحزينة والوقوف بجانب الضعفاء برحمة ومحبة.", "ما نوع المد المتكرر في وسط كلمة (مُوَاسَاة)؟", "مد الألف", "مد الألف", "مد الياء", "مد الواو"),
                AgeEightPlayLetter("ن", "نَزَاهَة", "🎖️", "النزاهة والصدق المالي", "النزاهة هي الترفع عن الحرام وحفظ النفس عن الشبهات والصدق والأمانة المطلقة في كل تعامل.", "ما هو الحرف الممدود بمد الألف في وسط كلمة (نَزَاهَة)؟", "الهاء", "الهاء", "الزاي", "النون"),
                AgeEightPlayLetter("هـ", "هُدَى", "🚀", "الهدى والنور الرباني", "القرآن هدى للناس ونور يضيء دروبهم ويوجه قلوبهم إلى صراط الله المستقيم الجميل.", "ما نوع الألف اللينة المرسومة في نهاية كلمة (هُدَى)؟", "ألف مقصورة", "ألف مقصورة", "ألف ممدودة", "ياء صريحة"),
                AgeEightPlayLetter("و", "وِئَام", "🤝", "الوئام والتآخي بين القلوب", "الوئام والانسجام بين أفراد المجتمع الإسلامي يبني قوة متماسكة متعاونة على البر والتقوى.", "ما هي الهمزة المتوسطة المرسومة على نبرة في كلمة (وِئَام)؟", "همزة مكسور ما قبلها", "همزة مكسور ما قبلها", "همزة مفتوحة بعد فتح", "همزة ساكنة"),
                AgeEightPlayLetter("ي", "يَقِين", "💛", "اليقين التام بفضل الله", "اليقين هو الاعتقاد الجازم والصادق بنصر الله وعطائه وحكمته البالغة في كل أمورنا.", "ما هو المد الصوتي المتوسط المسموع في نهاية كلمة (يَقِين)؟", "مد الياء", "مد الياء", "مد الألف", "مد الواو")
            )
        } else {
            listOf(
                AgeEightPlayLetter("A", "Altruism", "🤝", "Selfless Caring", "Altruism is preferring others over ourselves, sharing our beloved toys and meals with a pure heart.", "Which suffix makes the abstract noun 'Altruism' an active adjective?", "Altruistic", "Altruistic", "Altruism", "Altruisticly"),
                AgeEightPlayLetter("B", "Benevolence", "💖", "Active Goodwill", "Benevolence is the loving desire to do good things for our family, classmates, and all of Allah's creatures.", "Identify the abstract noun suffix in the word 'Benevolence'?", "ence", "ence", "ent", "ing"),
                AgeEightPlayLetter("C", "Courage", "🦁", "Shaja'ah Spirit", "Courage means standing firm for what is true, guarding weak friends, and defending Islamic principles.", "What is the synonym of the moral word 'Courage' under noble qualities?", "Bravery", "Bravery", "Fear", "Sadness"),
                AgeEightPlayLetter("D", "Diligence", "🏋️", "Determined Effort", "Diligence is working hard, learning our lessons, and practicing our Deen with consistent high resolve.", "In 'She works with diligence', what part of speech is 'diligence'?", "Noun", "Noun", "Verb", "Adjective"),
                AgeEightPlayLetter("E", "Endurance", "⏳", "Patience in Struggle", "Endurance is staying firm and smiling calmly during hard times, fully trusting Allah's beautiful destiny.", "Which verb is the source of the abstract noun 'Endurance'?", "Endure", "Endure", "Enduring", "Endured"),
                AgeEightPlayLetter("F", "Fortitude", "🛡️", "Strength of Soul", "Fortitude is quiet mental strength that enables us to carry out good actions even under pressure.", "In 'He showed high fortitude', what is the grammatical role of 'high'?", "Adjective", "Adjective", "Adverb", "Noun"),
                AgeEightPlayLetter("G", "Grace", "🕊️", "Elegant Kindness", "Grace is treating people with elegant manners, speaking softly, and letting go of anger with absolute beauty.", "What is the correct adjective form of 'Grace' describing a kind person?", "Graceful", "Graceful", "Gracelessly", "Gracing"),
                AgeEightPlayLetter("H", "Harmony", "🌍", "Peaceful Concord", "Harmony means living in beautiful peace and cooperating with everyone in our global family.", "What suffix converts the noun 'Harmony' into its adjective form?", "ous (Harmonious)", "ous (Harmonious)", "ly", "ness"),
                AgeEightPlayLetter("I", "Integrity", "💎", "Amanah Standard", "Integrity means being honest, reliable, and doing what is right even when nobody is watching you.", "Which prefix creates the moral opposite (antonym) of 'Integrity'?", "Dis", "Dis", "In", "Un"),
                AgeEightPlayLetter("J", "Jubilation", "🎉", "Spiritual Delight", "Jubilation is the clean joy and happiness we feel in our hearts after completing a blessed good deed.", "How many syllables are inside the joyful word 'Jubilation'?", "Four", "Four", "Three", "Five"),
                AgeEightPlayLetter("K", "Kinship", "🏡", "Silah bonds", "Kinship is maintaining loving relations with our parents, siblings, and extended family members.", "What does the suffix '-ship' mean in 'Kinship'?", "State of being related", "State of being related", "A water boat", "A fast plane"),
                AgeEightPlayLetter("L", "Liberality", "🎁", "Pure Munificence", "Liberality is the habit of giving generously from our savings, sharing what we love with others.", "What is the primary root word of the quality 'Liberality'?", "Liberal", "Liberal", "Liberally", "Liberty"),
                AgeEightPlayLetter("M", "Modesty", "🌸", "Haya' Character", "Modesty is having quiet behavior and dressing neatly to show high respect for ourselves and our Deen.", "Which of these words is a synonym of 'Modesty' in character?", "Humble", "Humble", "Pride", "Arrogance"),
                AgeEightPlayLetter("N", "Nobility", "👑", "Noble Conduct", "Nobility means rising above petty arguments, being kind to everyone, and behaving with high character.", "Identify the adjective form of the character noun 'Nobility'?", "Noble", "Noble", "Nobly", "Nobleness"),
                AgeEightPlayLetter("O", "Obligation", "📝", "Sacred Duty", "An obligation is a duty we perform to please Allah, like praying our daily prayers on time.", "What active verb means 'to bind someone by a duty'?", "Oblige", "Oblige", "Obligated", "Obliging"),
                AgeEightPlayLetter("P", "Piety", "🕌", "Taqwa devotion", "Piety is the sweet love of Allah that inspires us to obey His beautiful rules and help people.", "What is the adjective form of 'Piety' describing a devout believer?", "Pious", "Pious", "Piety", "Piously"),
                AgeEightPlayLetter("Q", "Quietude", "🧘", "Sakinah Calming", "Quietude is the peace and stillness we find in our hearts when we sit and read the Quran calmly.", "In 'The masjid has deep quietude', what is the subject of the sentence?", "masjid", "masjid", "quietude", "deep"),
                AgeEightPlayLetter("R", "Rectitude", "📈", "Istiqamah Path", "Rectitude is staying on the straight, correct path of truth and doing good deeds with pure intentions.", "What is the synonym of 'Rectitude' describing moral correctness?", "Righteousness", "Righteousness", "Weakness", "Doubt"),
                AgeEightPlayLetter("S", "Serenity", "🕊️", "Sakinah Spirit", "Serenity is a state of calm mind that feels happy with Allah's decree and spreads peace.", "What adjective is derived from the peaceful noun 'Serenity'?", "Serene", "Serene", "Serenely", "Serenest"),
                AgeEightPlayLetter("T", "Tolerance", "🤝", "Tasamuh and Respect", "Tolerance is respecting other people's views, being patient, and treating everyone with kindness.", "What suffix transforms the verb 'Tolerate' into 'Tolerance'?", "ance", "ance", "ent", "ing"),
                AgeEightPlayLetter("U", "Uprightness", "🎖️", "Salah integrity", "Uprightness is living honestly, keeping all our promises, and treating friends with fairness.", "In the compound noun 'Uprightness', what is the root word?", "Upright", "Upright", "Right", "Righteous"),
                AgeEightPlayLetter("V", "Vigilance", "🛡️", "Muraqabah Mind", "Vigilance is being careful with our words and actions, knowing that Allah always watches over us.", "What is the adjective form of 'Vigilance' describing an alert mind?", "Vigilant", "Vigilant", "Vigilantly", "Vigilance"),
                AgeEightPlayLetter("W", "Wholeheartedness", "❤️", "Ikhlas devotion", "Wholeheartedness means doing every good action with complete, sincere effort to please Allah.", "How many syllables are in the compound word 'Wholehearted'?", "Three", "Three", "Four", "Two"),
                AgeEightPlayLetter("X", "eXultation", "🌟", "Spiritual Delight", "Exultation is the highest level of spiritual joy when we succeed in doing great actions of service.", "Which suffix completes the noun: 'exultat_ _ _' under action nouns?", "ion", "ion", "ive", "ly"),
                AgeEightPlayLetter("Y", "Youthfulness", "⚡", "Energy in Good", "Youthfulness is using our high energy, health, and young years to serve our parents and community.", "What is the suffix in the word 'Youthfulness' showing a state of being?", "ness", "ness", "ful", "Youth"),
                AgeEightPlayLetter("Z", "Zeal", "🔥", "Himmah Resolve", "Zeal is having incredible enthusiasm and high energy to do good deeds and help those in need.", "Which adjective describes a child full of high zeal and devotion?", "Zealous", "Zealous", "Zeal", "Zealously")
            )
        }
    }
var selectedLetter by remember(isArabic) { mutableStateOf(ageEightLetters.first()) }
    var selectedAnswer by remember(selectedLetter) { mutableStateOf<String?>(null) }
    var pointsEarned by remember(selectedLetter) { mutableStateOf(0) }

    fun speakFullLesson(letter: AgeEightPlayLetter) {
        tts?.let { t ->
            val lang = if (isArabic) Locale("ar", "SA") else Locale("en", "IN")
            val result = t.setLanguage(lang)
            if (isArabic && (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)) {
                t.language = Locale("ar")
            } else {
                t.language = lang
            }
            val speechText = "${letter.word}. ${letter.lessonText}. ${letter.question}"
            t.speak(sanitizeSpeechText(speechText), TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    LaunchedEffect(selectedLetter, ttsReady) {
        if (ttsReady) {
            speakFullLesson(selectedLetter)
        }
    }

    val scrollState = rememberScrollState()

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FIXED ON TOP: Rectangular compact preview card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(2.dp, Color(0xFF004D40))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = selectedLetter.icon,
                                fontSize = 32.sp
                            )
                            Column {
                                Text(
                                    text = if (isArabic) selectedLetter.char else "${selectedLetter.char.uppercase()} ${selectedLetter.char.lowercase()}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF004D40)
                                )
                                Text(
                                    text = "${selectedLetter.word} • ${selectedLetter.title}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                        Button(
                            onClick = { speakFullLesson(selectedLetter) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0F2F1)),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("🗣️ ", fontSize = 14.sp)
                            Text(
                                text = if (isArabic) "الحكمة" else "Wisdom",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF004D40),
                                fontSize = 11.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedLetter.lessonText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723),
                        lineHeight = 15.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF3E0), RoundedCornerShape(6.dp))
                            .padding(6.dp)
                    )
                }
            }

            // SCROLLABLE CONTAINER:
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF004D40)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("👑 ", fontSize = 18.sp)
                            Text(
                                text = if (isArabic) "المستوى ٦: أصول الكلمات، التجويد وقواعد النحو!" else "Level 6: Roots, Tajweed & Syntax Mastery!",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isArabic) "اقرأ العبر وحل أسئلة التجويد والأصول اللغوية لجمع عسل النور بي! 🐝" else "Read wisdom rules & solve advanced Tajweed/Root questions to claim Honey Stars! 🐝",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(2.dp, Color(0xFF80CBC4))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isArabic) "🧠 تحدي النحو وأصول الكشف والتجويد 🧠" else "🧠 Advanced Root, Syntax & Tajweed Quiz 🧠",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF004D40),
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = selectedLetter.question,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF00796B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(selectedLetter.o1, selectedLetter.o2, selectedLetter.o3).forEach { option ->
                                val isCorrect = option == selectedLetter.correctWord
                                val isChosen = selectedAnswer == option
                                val btnBg = when {
                                    isChosen && isCorrect -> Color(0xFF2E7D32)
                                    isChosen && !isCorrect -> Color(0xFFC62828)
                                    else -> Color.White
                                }
                                val btnTextCol = if (isChosen) Color.White else Color(0xFF004D40)

                                Card(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .height(50.dp)
                                        .weight(1f)
                                        .shadow(2.dp, RoundedCornerShape(12.dp))
                                        .clickable {
                                            selectedAnswer = option
                                            if (isCorrect) {
                                                pointsEarned = 10
                                                tts?.let { t ->
                                                    val l = if (isArabic) Locale("ar") else Locale("en", "IN")
                                                    t.language = l
                                                    t.speak(if (isArabic) "تبارك الله! حل صحيح يدل على فصاحتك ونباهتك!" else "Superb resolution! That is perfectly correct!", TextToSpeech.QUEUE_FLUSH, null, null)
                                                }
                                            } else {
                                                pointsEarned = 0
                                                tts?.let { t ->
                                                    val l = if (isArabic) Locale("ar") else Locale("en", "IN")
                                                    t.language = l
                                                    t.speak(if (isArabic) "فكر بأصول اللفظ والتجويد وحاول ثانية!" else "Examine rules carefully, let's try again!", TextToSpeech.QUEUE_FLUSH, null, null)
                                                }
                                            }
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = btnBg),
                                    border = BorderStroke(1.5.dp, Color(0xFF80CBC4))
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = option,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = btnTextCol,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (selectedAnswer != null) {
                            val isCorrect = selectedAnswer == selectedLetter.correctWord
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = if (isCorrect) "👑 🍯 " else "✏️ ",
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = if (isCorrect) {
                                        if (isArabic) "أحسنت جداً! كسبت +15 عسل للتفوق القرآني! 🐝" else "Excellent analysis! Unlocked +15 Honey Stars! 🐝"
                                    } else {
                                        if (isArabic) "تأمل في الحروف وأحكام المخارج والتجويد!" else "Reflect on root letters and rules, you can resolve it!"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        } else {
                            Text(
                                text = if (isArabic) "أجب على اللغز بطريق التجويد والأصول لتفيض ينابيع المعرفة العسلية!" else "Understand the roots and tap to choose the correct masterpiece answer!",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Text(
                    text = if (isArabic) "اختر الحرف لتبحر بأصوله وتجويده: 📖" else "Tap any letters to view advanced roots: 📖",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF004D40),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ageEightLetters) { letter ->
                        val isSelected = selectedLetter == letter
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF80CBC4) else Color(0xFFECEFF1))
                                .border(2.dp, if (isSelected) Color(0xFF004D40) else Color.Transparent, CircleShape)
                                .clickable {
                                    selectedLetter = letter
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter.char,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.White else Color(0xFF37474F)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onStartAdventure,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D40)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(50.dp)
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                ) {
                    Text("🍯 ", fontSize = 18.sp)
                    Text(
                        text = if (isArabic) "اكتمل المسار العالي بنجاح! فرغ العسل 🍯" else "Complete High Road & Absorb Honey Stars! 🍯",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}


