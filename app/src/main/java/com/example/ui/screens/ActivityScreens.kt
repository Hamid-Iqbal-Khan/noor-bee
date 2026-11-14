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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChildProfile
import com.example.ui.WordItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreens(
    profile: ChildProfile,
    activityType: String, // "intro", "match", "vocab", "listening", "speaking", "quiz"
    selectedWord: WordItem,
    quizQuestions: List<WordItem>,
    quizAnswers: List<String>,
    currentQuestionIndex: Int,
    quizScore: Int,
    quizAnswerChecked: Boolean?,
    onBackPressed: () -> Unit,
    onNextWordOrBack: () -> Unit,
    onSoundTrigger: (String) -> Unit, // Play sound of word
    onSubmitQuizAnswer: (String) -> Unit,
    onNextQuestion: () -> Boolean, // returns false if quiz completed, true if more
    onActionComplete: (Int, Int, String) -> Unit, // (gold, stars, actionText)
    onResetQuiz: () -> Unit
) {
    val isArabicPath = profile.currentLanguage == "Arabic"
    var speakingState by remember(selectedWord.id) { mutableStateOf(false) } // Is speaking microphone recording?
    var showQuizCompleteDialog by remember { mutableStateOf(false) }
    var matchSelection by remember(selectedWord.id) { mutableStateOf<String?>(null) }
    var matchChecked by remember(selectedWord.id) { mutableStateOf<Boolean?>(null) } // null = unchecked, true = right, false = wrong
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (activityType) {
                            "intro" -> if (isArabicPath) "ركن الكلمات 📝" else "Vocabulary Intro Card"
                            "match" -> if (isArabicPath) "لعبة التوصيل 🧩" else "Match Translation"
                            "vocab" -> if (isArabicPath) "بطاقات المراجعة 🃏" else "Flashcards Lab"
                            "listening" -> if (isArabicPath) "تحدي الاستماع 🎧" else "Listening Ear"
                            "speaking" -> if (isArabicPath) "مختبر النطق 🎙️" else "Speech Laboratory"
                            "quiz" -> if (isArabicPath) "الاختبار السريع 🏆" else "Progress Assessment"
                            else -> "Game"
                        },
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723)
                    )
                },
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
                .testTag("activity_screen_container"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (activityType) {
                "intro" -> {
                    // Stage 1: Vocabulary Cards (Intro)
                    IntroActivityLayout(
                        word = selectedWord,
                        isArabic = isArabicPath,
                        onSoundClick = { onSoundTrigger(if (isArabicPath) "text:arabic:${selectedWord.arabic}" else "text:english:${selectedWord.english}") },
                        onNextClick = {
                            onActionComplete(3, 5, "Word Look")
                            onNextWordOrBack()
                        }
                    )
                }
                "match" -> {
                    // Stage 2: Match Game
                    MatchActivityLayout(
                        word = selectedWord,
                        isArabic = isArabicPath,
                        selectedOption = matchSelection,
                        checkedState = matchChecked,
                        onSelect = { matchSelection = it },
                        onSubmitCheck = {
                            val rightLabel = if (isArabicPath) selectedWord.english else selectedWord.arabic
                            val isCorrect = matchSelection.equals(rightLabel, ignoreCase = true)
                            matchChecked = isCorrect
                            if (isCorrect) {
                                onActionComplete(5, 12, "Translation Match")
                            }
                        },
                        onNextClick = {
                            matchSelection = null
                            matchChecked = null
                            onNextWordOrBack()
                        }
                    )
                }
                "vocab" -> {
                    // Stage 3: Flippable Flashcards
                    FlashcardActivityLayout(
                        profile = profile,
                        word = selectedWord,
                        onSoundTrigger = onSoundTrigger,
                        onPlaySound = { isArabic ->
                            if (isArabic) {
                                onSoundTrigger("text:arabic:${selectedWord.arabic}")
                            } else {
                                onSoundTrigger("text:english:${selectedWord.english}")
                            }
                        },
                        onNextClick = {
                            onActionComplete(4, 8, "Flashcard Flip")
                            onNextWordOrBack()
                        }
                    )
                }
                "listening" -> {
                    // Stage 4: Listening Challenge
                    ListeningActivityLayout(
                        word = selectedWord,
                        isArabic = isArabicPath,
                        onPlayAudio = { onSoundTrigger(if (isArabicPath) "text:arabic:${selectedWord.arabic}" else "text:english:${selectedWord.english}") },
                        onComplete = {
                            onActionComplete(5, 10, "Listening Practice")
                            onNextWordOrBack()
                        }
                    )
                }
                "speaking" -> {
                    // Stage 5: Speech Recorder Lab
                    SpeakingActivityLayout(
                        word = selectedWord,
                        isArabic = isArabicPath,
                        isRecording = speakingState,
                        onMicClick = {
                            speakingState = !speakingState
                            if (!speakingState) {
                                // simulated success callback after delay
                                coroutineScope.launch {
                                    delay(800)
                                    onActionComplete(8, 15, "Speech Lab")
                                }
                            }
                        },
                        onNextClick = {
                            onNextWordOrBack()
                        }
                    )
                }
                "quiz" -> {
                    // Stage 6: Scoring Quiz
                    QuizActivityLayout(
                        questions = quizQuestions,
                        answers = quizAnswers,
                        currentIndex = currentQuestionIndex,
                        score = quizScore,
                        checkedState = quizAnswerChecked,
                        isArabic = isArabicPath,
                        onSubmit = onSubmitQuizAnswer,
                        onNext = {
                            val hasMore = onNextQuestion()
                            if (!hasMore) {
                                showQuizCompleteDialog = true
                            }
                        }
                    )
                }
            }
        }

        // Celebrate Quiz Accomplished Modal with Animated Noor Bee Mascot Overlay
        NoorBeeCelebrationOverlay(
            visible = showQuizCompleteDialog,
            coinsEarned = 15,
            starsEarned = 50,
            lessonName = if (isArabicPath) "تم بنجاح: الاختبار السريع 🏆" else "Completed: Progress Assessment 🏆",
            currentLanguage = profile.currentLanguage,
            onDismiss = {
                showQuizCompleteDialog = false
                onActionComplete(15, 50, "Quiz Completed")
                onBackPressed()
            },
            onSoundTrigger = onSoundTrigger
        )
    }
}

// --- Layout 1: Vocabulary Card Intro --
@Composable
fun ColumnScope.IntroActivityLayout(
    word: WordItem,
    isArabic: Boolean,
    onSoundClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Emojis
            Text(
                text = word.emoji,
                fontSize = 100.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )

            // Dynamic bilingual display display
            Text(
                text = word.english,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF3E2723)
            )
            Text(
                text = "/ ${word.englishPhonetic} /",
                fontSize = 15.sp,
                color = Color(0xFF795548),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Divider(color = Color(0xFFFFF9C4), modifier = Modifier.padding(vertical = 12.dp))

            Text(
                text = word.arabic,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20) // Deep leafy green
            )
            Text(
                text = "[ ${word.arabicPhonetic} ]",
                fontSize = 15.sp,
                color = Color(0xFF558B2F),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Audio button click
            IconButton(
                onClick = onSoundClick,
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFFFF9C4), CircleShape)
                    .border(1.dp, Color(0xFFFFD54F), CircleShape)
            ) {
                Text(
                    text = "🔊",
                    fontSize = 28.sp
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onNextClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(6.dp, RoundedCornerShape(28.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E342E)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Text("Done • Keep Going! 🐝", fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
    }
}

// --- Layout 2: Match Translation --
@Composable
fun ColumnScope.MatchActivityLayout(
    word: WordItem,
    isArabic: Boolean,
    selectedOption: String?,
    checkedState: Boolean?,
    onSelect: (String) -> Unit,
    onSubmitCheck: () -> Unit,
    onNextClick: () -> Unit
) {
    Text(
        text = if (isArabic) "ما معنى هذه الكلمة؟" else "What does this word mean?",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF5D4037),
        modifier = Modifier.padding(bottom = 16.dp)
    )

    // Challenge Card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = word.emoji, fontSize = 64.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isArabic) word.arabic else word.english,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF3E2723)
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Match choices: Display target definitions
    val targetLangLabel = if (isArabic) word.english else word.arabic
    val dummyOptions = remember(word) {
        if (isArabic) listOf("Apple", "Car", "Sun").filter { it != word.english }.take(2)
        else listOf("موز", "نحلة", "شمس").filter { it != word.arabic }.take(2)
    }
    val allShuffledOptions = remember(word) {
        (dummyOptions + targetLangLabel).shuffled()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        allShuffledOptions.forEach { opt ->
            val isSelected = selectedOption == opt
            val itemBackground = when {
                checkedState != null && isSelected && checkedState -> Color(0xFFC8E6C9) // Green success
                checkedState != null && isSelected && !checkedState -> Color(0xFFFFCDD2) // Red err
                isSelected -> Color(0xFFFFF9C4)
                else -> Color.White
            }
            val borderStrokeColor = when {
                checkedState != null && isSelected && checkedState -> Color(0xFF4CAF50)
                checkedState != null && isSelected && !checkedState -> Color(0xFFF44336)
                isSelected -> Color(0xFFFFC107)
                else -> Color(0xFFE0E0E0)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clickable(enabled = checkedState == null) { onSelect(opt) }
                    .testTag("match_option_$opt"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = itemBackground),
                border = BorderStroke(2.dp, borderStrokeColor)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = opt,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723)
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    if (checkedState == null) {
        Button(
            onClick = onSubmitCheck,
            enabled = selectedOption != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E342E)),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Verify Answer", fontWeight = FontWeight.Bold, color = Color.White)
        }
    } else {
        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (checkedState) Color(0xFF4CAF50) else Color(0xFFE53935)
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = if (checkedState) "Awesome! Next 🌟" else "Try Again!",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// --- Layout 3: Flippable Flashcard --
private val wordLabSentencesEng = mapOf(
    "apple" to Pair("We say Bismillah before eating a sweet red ____! 🍎", "apple"),
    "banana" to Pair("Allah blessed us with a yellow and healthy ____! 🍌", "banana"),
    "orange" to Pair("I drink fresh juice made from a delicious ____! 🍊", "orange"),
    "grapes" to Pair("We share a sweet plate of purple ____ with friends! 🍇", "grapes"),
    "watermelon" to Pair("In the warm summer, we enjoy a big slice of ____! 🍉", "watermelon"),
    "strawberry" to Pair("We thank Allah for the tiny seeds on a red ____! 🍓", "strawberry"),
    "peach" to Pair("A fuzzy and soft fruit we love is the ____! 🍑", "peach"),
    "pineapple" to Pair("A tropical and juicy fruit with a crown is the ____! 🍍", "pineapple"),
    "mango" to Pair("The sweet king of all tropical fruits is the ____! 🥭", "mango"),
    "lemon" to Pair("We squeeze a sour yellow ____ to make lemonade! 🍋", "lemon"),
    "cherry" to Pair("A tiny, bright red fruit on a green stem is a ____! 🍒", "cherry"),
    "pear" to Pair("A delicious, sweet fruit shaped like a teardrop is a ____! 🍐", "pear"),
    "kiwi" to Pair("A brown and fuzzy skin outside, but green inside is the ____! 🥝", "kiwi"),
    "plum" to Pair("A small, juicy fruit with a smooth purple skin is a ____! 🍑", "plum"),
    "apricot" to Pair("A small, sweet orange-colored fruit we love is the ____! 🍑", "apricot"),
    "coconut" to Pair("The hard, brown shell has healthy white milk inside the ____! 🥥", "coconut"),
    "dates" to Pair("The Prophet loved to break his fasting with sweet ____! 🌴", "dates"),
    "fig" to Pair("Allah swears by this blessed, sweet fruit called the ____! 🍇", "fig"),
    "bee" to Pair("The tiny and hardworking ____ makes sweet honey for us! 🐝", "bee"),
    "cat" to Pair("Our beloved Prophet had a cute and friendly pet ____! 🐱", "cat"),
    "lion" to Pair("The powerful and brave ____ lives in the wide grasslands! 🦁", "lion"),
    "bird" to Pair("A beautiful flying ____ praises Allah with its sweet morning song! 🐦", "bird"),
    "dog" to Pair("A loyal, friendly, and helpful animal is the ____! 🐶", "dog"),
    "rabbit" to Pair("A cute, fluffy ____ loves to hop around eating fresh carrots! 🐰", "rabbit"),
    "elephant" to Pair("The majestic, large ____ uses its long trunk to drink water! 🐘", "elephant"),
    "monkey" to Pair("A playful and smart ____ swings happily between tree branches! 🐒", "monkey"),
    "giraffe" to Pair("With its long neck, the tall ____ easily reaches tree leaves! 🦒", "giraffe"),
    "bear" to Pair("The big, cozy ____ loves sweet honey from Noor Bee! 🐻", "bear"),
    "fox" to Pair("A clever and nimble ____ runs swiftly through the woods! 🦊", "fox"),
    "cow" to Pair("A gentle, generous ____ gives us healthy fresh milk to drink! 🐮", "cow"),
    "sheep" to Pair("We learn kindness by caring for a soft, woolly ____! 🐑", "sheep"),
    "horse" to Pair("A fast, strong, and beautiful ____ is wonderful for riding! 🐴", "horse"),
    "camel" to Pair("The patient ____ can travel long distances in the warm desert! 🐫", "camel"),
    "duck" to Pair("The happy ____ splashes in the pond saying quack-quack! 🦆", "duck"),
    "fish" to Pair("A colorful swimming ____ breathes gracefully deep in the sea! 🐟", "fish"),
    "turtle" to Pair("The wise, slow-moving ____ carries its safe shell house! 🐢", "turtle"),
    "frog" to Pair("A cheerful green ____ leaps high on large water lilies! 🐸", "frog"),
    "butterfly" to Pair("A colorful, flying ____ lands softly on pretty flowers! 🦋", "butterfly"),
    "squirrel" to Pair("A busy and fluffy ____ gathers healthy nuts from the ground! 🐿️", "squirrel"),
    "car" to Pair("We make a safety dua before traveling in our family ____! 🚗", "car"),
    "sun" to Pair("Allah made the bright, warm ____ to light up our wonderful day! ☀️", "sun"),
    "moon" to Pair("At night, we see the glowing, silvery ____ in the dark sky! 🌙", "moon"),
    "book" to Pair("Reading a good ____ helps us learn wisdom and become smarter! 📖", "book"),
    "tree" to Pair("A tall green ____ gives us refreshing shade and clean cool air! 🌳", "tree"),
    "key" to Pair("We say Bismillah when we type or turn a door ____! 🔑", "key"),
    "pen" to Pair("We use a clean ____ to draw beautiful pictures and write letters! 🖊️", "pen"),
    "chair" to Pair("We sit politely on our comfy ____ during study time! 🪑", "chair"),
    "table" to Pair("We place our books and tasty food neatly on the ____! 🪵", "table"),
    "clock" to Pair("A ticking ____ helps us pray our five daily prayers on time! ⏰", "clock"),
    "phone" to Pair("We speak kindly and politely when talking on the ____! 📱", "phone"),
    "computer" to Pair("We learn useful and beneficial science using our modern ____! 💻", "computer"),
    "house" to Pair("We thank Allah for our warm, safe, and happy family ____! 🏠", "house"),
    "window" to Pair("We look out of our kitchen ____ to see Allah's beautiful clouds! 🪟", "window"),
    "door" to Pair("We knock gently and smile before entering through any ____! 🚪", "door"),
    "bag" to Pair("We pack our clean books inside our helpful school ____! 💼", "bag"),
    "shoe" to Pair("We wear our clean right ____ first before walking outside! 👟", "shoe"),
    "spoon" to Pair("We use our right hand and a shiny ____ to eat rice and helper soup! 🥄", "spoon"),
    "cup" to Pair("We drink clean, cool water from our favorite glass ____! 🥛", "cup"),
    "umbrella" to Pair("A wide, colorful ____ keeps us safe and dry from the rain! ☂️", "umbrella"),
    "lightbulb" to Pair("A glowing ____ brightens our workspace so we can study nicely! 💡", "lightbulb"),
    "mangosteen" to Pair("The dark purple fruit often called the queen of fruits is the ____! 🥭", "mangosteen"),
    "rambutan" to Pair("A sweet tropical fruit with a hairy red outer skin is the ____! 🍇", "rambutan"),
    "durian" to Pair("Known as the king of tropical fruits with a strong smell and spiky shell is the ____! 🍈", "durian"),
    "persimmon" to Pair("A delicious, sweet orange fruit resembling a tomato is the ____! 🍑", "persimmon"),
    "papaya" to Pair("A vibrant orange tropical fruit filled with tiny black seeds is the ____! 🥭", "papaya"),
    "passionfruit" to Pair("A highly fragrant round fruit with a golden jelly seed center is the ____! 🍇", "passionfruit"),
    "dragonfruit" to Pair("A spectacular hot pink cactus fruit with white speckled pulp is the ____! 🐉", "dragonfruit"),
    "boysenberry" to Pair("A large, deep purple berry cross between a raspberry and blackberry is the ____! 🫐", "boysenberry"),
    "chestnut" to Pair("A delicious brown nut roasted over an open fire during cold days is the ____! 🌰", "chestnut"),
    "mulberry" to Pair("A sweet, elongated berry growing on ancient trees that silkworms love is the ____! 🫐", "mulberry"),
    "platypus" to Pair("The unique egg-laying mammal with a duck-like bill is the ____! 🦆", "platypus"),
    "chameleon" to Pair("The clever lizard that shifts its skin color to match its surroundings is the ____! 🦎", "chameleon"),
    "komodo" to Pair("The largest living species of lizard on Earth is the giant ____! 🦎", "komodo dragon"),
    "hippopotamus" to Pair("A large river-dwelling mammal that loves to splash in cool muddy waters is the ____! 🦛", "hippopotamus"),
    "cheetah" to Pair("The fastest land animal on Earth with a beautiful spotted coat is the ____! 🐆", "cheetah"),
    "gorilla" to Pair("The powerful, gentle giant ape living in peaceful forest families is the ____! 🦍", "gorilla"),
    "chimpanzee" to Pair("A highly intelligent ape that can learn sign language and use tools is the ____! 🐒", "chimpanzee"),
    "jellyfish" to Pair("The glowing, umbrella-shaped sea creature with soft flowing tentacles is the ____! 🪼", "jellyfish"),
    "starfish" to Pair("The five-armed ocean creature that can regenerate its lost limbs is the ____! ⭐", "starfish"),
    "kangaroo" to Pair("The famous bouncy mammal that carries its tiny baby in a warm pouch is the ____! 🦘", "kangaroo"),
    "seismograph" to Pair("An advanced scientific instrument used to detect and measure earthquakes is the ____! 📊", "seismograph"),
    "gyroscope" to Pair("A spinning wheel device used to maintain balance and direction in spaceships is the ____! ⚙️", "gyroscope"),
    "calorimeter" to Pair("An instrument used by scientists to measure the heat of chemical reactions is the ____! 🧪", "calorimeter"),
    "speedometer" to Pair("A dial in cars that tells us exactly how fast we are traveling is the ____! ⏱️", "speedometer"),
    "anemometer" to Pair("An instrument used to measure the speed and force of the wind is the ____! 💨", "anemometer"),
    "syringe" to Pair("A medical tool used by doctors to give life-saving vaccines to children is the ____! 💉", "syringe"),
    "calculator" to Pair("An electronic tool used to solve complex mathematical equations instantly is the ____! 🧮", "calculator"),
    "telescope" to Pair("A beautiful optical tube used to observe distant planets, stars, and galaxies is the ____! 🔭", "telescope"),
    "microscope" to Pair("A scientific tool that magnifies microscopic bacteria and cells is the ____! 🔬", "microscope"),
    "globe" to Pair("A spherical representation of our wonderful planet Earth is the ____! 🌐", "globe")
)

private val wordLabSentencesAr = mapOf(
    "apple" to Pair("نَقُولُ بِسْمِ اللهِ قَبْلَ أَنْ نَأْكُلَ ____ اللَّذِيذَةَ! 🍎", "تفاحة"),
    "banana" to Pair("رَزَقَنَا اللهُ بِـ ____ حُلْوَةٍ وَمُفِيدَةٍ لِلْقُوَّةِ! 🍌", "موز"),
    "orange" to Pair("نَعْصِرُ ____ لِنَشْرَبَ عَصِيرًا لَذِيذًا وَمُنْعِشًا! 🍊", "برتقال"),
    "grapes" to Pair("نَتَشَارَكُ طَبَقًا مِنَ ____ اللَّذِيذِ مَعَ أَصْدِقَائِنَا! 🍇", "عنب"),
    "watermelon" to Pair("نَسْتَمْتِعُ بِتَنَاوُلِ شَرِيحَةٍ بَارِدَةٍ مِنَ ____ فِي الصَّيْفِ! 🍉", "بطيخ"),
    "strawberry" to Pair("نَشْكُرُ اللهَ عَلَى بذور ____ الْحَمْرَاءِ الصَّغِيرَةِ! 🍓", "فراولة"),
    "peach" to Pair("الْـ ____ هِيَ فَاكِهَةٌ نَاعِمَةٌ وَطَيِّبَةٌ نُحِبُّهَا جَمِيعًا! 🍑", "دراق"),
    "pineapple" to Pair("فَاكِهَةٌ شَهِيَّةٌ لَهَا تَاجٌ أَخْضَرُ جَمِيلٌ هِيَ الْـ ____! 🍍", "أناناس"),
    "mango" to Pair("مَلِكَةُ الْفَوَاكِهِ الاسْتِوَائِيَّةِ الْحُلْوَةِ هِيَ الْـ ____! 🥭", "مانجو"),
    "lemon" to Pair("نَعْصِرُ الْـ ____ الْحَامِضَ لِنَصْنَعَ شَرَابًا مُفِيدًا لِلْجِسْمِ! 🍋", "ليمون"),
    "cherry" to Pair("فَاكِهَةٌ حَمْرَاءُ صَغِيرَةٌ ذَاتُ غُصْنٍ أَخْضَرَ جَمِيلٍ هِيَ الْـ ____! 🍒", "كرز"),
    "pear" to Pair("فَاكِهَةٌ حُلْوَةٌ تُشْبِهُ الدَّمْعَةَ فِي شَكْلِهَا هِيَ الْـ ____! 🍐", "إجاص"),
    "kiwi" to Pair("حُلْوَةٌ مِنَ الدَّاخِلِ وَبُنِّيَّةٌ مِنَ الْخَارِجِ هِيَ ثَمَرَةُ الْـ ____! 🥝", "كيوي"),
    "plum" to Pair("الْـ ____ فَاكِهَةٌ صَغِيرَةٌ عَصِيرِيَّةٌ ذَاتُ قِشْرَةٍ بَنَفْسَجِيَّةٍ! 🍑", "برقوق"),
    "apricot" to Pair("ثَمَرَةُ الْـ ____ حُلْوَةٌ وَمُفِيدَةٌ نَأْكُلُهَا بِفَرَحٍ! 🍑", "مشمش"),
    "coconut" to Pair("فَاكِهَةُ ____ تُعْطِينَا حَلِيبًا بَارِدًًا وَمَاءً شَهِيًّا! 🥥", "جوز الهند"),
    "dates" to Pair("كَانَ الرَّسُولُ يُحِبُّ أَنْ يُفْطِرَ عَلَى حَبَّاتِ ____ لَذِيذَةٍ! 🌴", "تمر"),
    "fig" to Pair("أَقْسَمَ اللهُ تَعَالَى بِهَذِهِ الْفَاكِهَةِ الْمُبَارَكَةِ الْـ ____! 🍇", "تين"),
    "bee" to Pair("الْـ ____ النَّشِيطَةُ تَطِيرُ بَيْنَ الْأَزْهَارِ لِتَصْنَعَ عَسَلًا جَمِيلًا! 🐝", "نحلة"),
    "cat" to Pair("كَانَ لِرَسُولِنَا الْكَرِيمِ ____ لَطِيفَةٌ وَأَلِيفَةٌ يُحِبُّهَا! 🐱", "قطة"),
    "lion" to Pair("الْـ ____ الْقَوِيُّ وَالشُّجَاعُ يَعِيشُ فِي الْغَابَةِ الْوَاسِعَةِ! 🦁", "أسد"),
    "bird" to Pair("يُغَرِّدُ الْـ ____ الْجَمِيلُ لِيُسَبِّحَ اللهَ فِي كُلِّ صَبَاحٍ! 🐦", "طائر"),
    "dog" to Pair("الْـ ____ حَيَوَانٌ وَفِيٌّ وَمُفِيدٌ يَحْمِي الْمَزَارِعَ دَائِمًَا! 🐶", "كلب"),
    "rabbit" to Pair("يَقْفِزُ الْـ ____ الْأَلِيفُ بِفَرَحٍ لِيَأْكُلَ الْجَزَرَ الطَّازَجَ! 🐰", "أرنب"),
    "elephant" to Pair("الْـ ____ الْكَبِيرُ يَسْتَخْدِمُ خُرْطُومَهُ الطَّوِيلَ لِيَشْرَبَ الْمَاءَ! 🐘", "فيل"),
    "monkey" to Pair("يَقْفِزُ الْـ ____ النَّشِيطُ بَيْنَ أَغْصَانِ الْأَشْجَارِ بِمَرَحٍ! 🐒", "قرد"),
    "giraffe" to Pair("تَسْتَطِيعُ الْـ ____ الطَّوِيلَةُ أَنْ تَأْكُلَ أَوْرَاقَ الشَّجَرِ الْعَالِيَةِ! 🦒", "زرافة"),
    "bear" to Pair("الْـ ____ الْكَبِيرُ يُحِبُّ تَنَاوُلَ الْعَسَلِ الشَّهِيِّ مِنَ النَّحْلَةِ! 🐻", "دب"),
    "fox" to Pair("يَجْرِي الْـ ____ الذَّكِيُّ وَالسَّرِيعُ بَيْنَ الْأَشْجَارِ فِي الْغَابَةِ! 🦊", "ثعلب"),
    "cow" to Pair("الْـ ____ اللَّطِيفَةُ تُعْطِينَا حَلِيبًا طَازَجًا وَمُفِيدًا لِنَكْبُرَ كُلَّ يَوْمٍ! 🐮", "بقرة"),
    "sheep" to Pair("نَتَعَلَّمُ الرِّفْقَ وَالْأَمَانَةَ عِنْدَ رِعَايَةِ الْـ ____ الْوَدِيعِ! 🐑", "خروف"),
    "horse" to Pair("الْـ ____ هُوَ حَيَوَانٌ سَرِيعٌ وَقَوِيٌّ وَرُكُوبُهُ مُفِيدٌ لِلْجِسْمِ! 🐴", "حصان"),
    "camel" to Pair("الْـ ____ الصَّبُورُ يَسْتَطِيعُ السَّفَرَ طَوِيلًا فِي الصَّحْرَاءِ الْوَاسِعَةِ! 🐫", "جمل"),
    "duck" to Pair("تَسْبَحُ الْـ ____ النَّشِيطَةُ فِي الْبُحَيْرَةِ وَتَقُولُ كُوَاك كُوَاك! 🦆", "بطة"),
    "fish" to Pair("تَسْبَحُ الْـ ____ الْمُلَوَّنَةُ بِجَمَالٍ فِي أَعْمَاقِ الْبَحْرِ! 🐟", "سمكة"),
    "turtle" to Pair("الْـ ____ الْهَادِئَةُ تَحْمِلُ بَيْتَهَا الدَّافِئَ عَلَى ظَهْرِهَا! 🐢", "سلحفاة"),
    "frog" to Pair("يَقْفِزُ الْـ ____ الْأَخْضَرُ عَالِيًا عَلَى أَوْرَاقِ النَّبَاتَاتِ الْمَائِيَّةِ! 🐸", "ضفدع"),
    "butterfly" to Pair("تَطِيرُ الْـ ____ الْمُلَوَّنَةُ بِرِقَّةٍ وَتَقِفُ عَلَى الزُّهُورِ! 🦋", "فراشة"),
    "squirrel" to Pair("الْـ ____ النَّشِيطُ يَجْمَعُ الْجَوْزَ اللَّذِيذَ مِنَ الْأَرْضِ بِسُرْعَةٍ! 🐿️", "سنجاب"),
    "car" to Pair("نَقُولُ دُعَاءَ السَّفَرِ قَبْلَ أَنْ نَرْكَبَ فِي ____ الْعَائِلَةِ! 🚗", "سيارة"),
    "sun" to Pair("خَلَقَ اللهُ الْـ ____ الْمُضِيئَةَ لِتُنِيرَ لَنَا يَوْمَنَا الرَّائِعَ! ☀️", "شمس"),
    "moon" to Pair("نَرَى الْـ ____ الْفِضِّيَّ الْجَمِيلَ يُزَيِّنُ السَّمَاءِ لَيْلًا! 🌙", "قمر"),
    "book" to Pair("قِرَاءةُ ____ نَافِعٍ تُسَاعِدُنَا عَلَى فَهْمِ الْعُلُومِ وَالْحِكْمَةِ! 📖", "كتاب"),
    "tree" to Pair("الْـ ____ الطَّوِيلَةُ وَالْخَضْرَاءُ تُعْطِينَا ظِلًّا بَارِدًا ومُنْعِشًا! 🌳", "شجرة"),
    "key" to Pair("نَقُولُ بِسْمِ اللهِ قَبْلَ أَنْ نَفْتَحَ الْبَابَ بِالْـ ____! 🔑", "مفتاح"),
    "pen" to Pair("نَسْتَخْدِمُ ____ جَمِيلًا لِنَكْتُبَ الْأَحْرُفَ وَنَرْسُمَ الرَّسْمَاتِ! 🖊️", "قلم"),
    "chair" to Pair("نَجْلِسُ بِأَدَبٍ وَهُدُوءٍ عَلَى الـ ____ أَثْنَاءِ الْقِرَاءَةِ! 🪑", "كرسي"),
    "table" to Pair("نَضَعُ الْكُتُبَ وَالطَّعَامَ اللَّذِيذَ عَلَى الـ ____ بِتَرْتِيبٍ! 🪵", "طاولة"),
    "clock" to Pair("تُسَاعِدُنَا الـ ____ الذَّهَبِيَّةُ عَلَى مَعْرِفَةِ أَوْقَاتِ الصَّلَوَاتِ! ⏰", "ساعة"),
    "phone" to Pair("نَتَحَدَّثُ بِأَدَبٍ وَصَوْتٍ لَطِيفٍ عِنْدَمَا نَسْتَخْدِمُ الـ ____! 📱", "هاتف"),
    "computer" to Pair("نَتَعَلَّمُ عُلُومًا مُفِيدَةً جِدًّا بِاسْتِخْدَامِ الـ ____ الْحَدِيثِ! 💻", "حاسوب"),
    "house" to Pair("نَشْكُرُ اللهَ عَلَى بَيْتِنَا الْـ ____ الدَّافِئِ الَّذِي يَجْمَعُنَا! 🏠", "بيت"),
    "window" to Pair("نَنْظُرُ مِنَ الـ ____ لِنَرَى غُيُومَ اللهِ الْبَيْضَاءَ اللَّطِيفَةَ! 🪟", "نافذة"),
    "door" to Pair("نَدُقُّ الْـ ____ بِأَدَبٍ وَنَبْتَسِمُ قَبْلَ أَنْ نَدْخُلَ أَيَّ مَكَانٍ! 🚪", "باب"),
    "bag" to Pair("نَضَعُ كُتُبَنَا الطَّيِّبَةَ دَاخِلَ الـ ____ الْمَدْرَسِيَّةِ النَّظِيفَةِ! 💼", "حقيبة"),
    "shoe" to Pair("نَلْبَسُ الْـ ____ الْأَيْمَنَ أَوَّلًا قَبْلَ أَنْ نَخْرُجَ لِلَّعِبِ! 👟", "حذاء"),
    "spoon" to Pair("نَأْكُلُ طَعَامَنَا اللَّذِيذَ بِالْـ ____ الْمُرِيحَةِ بِالْيَدِ الْيُمْنَى! 🥄", "ملعقة"),
    "cup" to Pair("نَشْرَبُ الْمَاءَ الْبَارِدَ وَالنَّظِيفَ مِنْ ____ زُجَاجِيٍّ نَظِيفٍ! 🥛", "كوب"),
    "umbrella" to Pair("الْـ ____ الزَّاهِيَةُ تَحْمِينَا مِنَ الْمَطَرِ وَتُبْقِينَا جَافِّينَ! ☂️", "مظلة"),
    "lightbulb" to Pair("الْـ ____ الْمُضِيءُ يُنِيرُ غُرْفَتَنَا لِنَقْرَأَ كِتَابَنَا بِوُضُوحٍ! 💡", "مصباح"),
    "mangosteen" to Pair("تُسَمَّى فَاكِهَةُ الْـ ____ بِمَلِكَةِ الْفَوَاكِهِ الاسْتِوَائِيَّةِ الْحُلْوَةِ! 🥭", "منغوستين"),
    "rambutan" to Pair("فَاكِهَةٌ اسْتِوَائِيَّةٌ لَذِيذَةٌ ذَاتُ شَعْرٍ أَحْمَرَ جَمِيلٍ هِيَ الْـ ____! 🍇", "رامبوتان"),
    "durian" to Pair("فَاكِهَةٌ اسْتِوَائِيَّةٌ شَهِيرَةٌ بِرَائِحَتِهَا الْقَوِيَّةِ وَقِشْرَتِهَا الشَّاوْكِيَّةِ هِيَ الْـ ____! 🍈", "دوريان"),
    "persimmon" to Pair("فَاكِهَةٌ لَذِيذَةٌ وَحُلْوَةٌ تُشْبِهُ الطَّمَاطِمَ فِي شَكْلِهَا الْخَارِجِيِّ هِيَ الْـ ____! 🍑", "كاكي"),
    "papaya" to Pair("فَاكِهَةٌ اسْتِوَائِيَّةٌ بُرْتُقَالِيَّةٌ مَلِيئَةٌ بِالْبُذُورِ السَّوْدَاءِ الصَّغِيرَةِ هِيَ الْـ ____! 🥭", "بابايا"),
    "passionfruit" to Pair("فَاكِهَةٌ عِطْرِيَّةٌ دَائِرِيَّةٌ ذَاتُ لُبٍّ هُلَامِيٍّ ذَهَبِيٍّ لَذِيذٍ هِيَ الْـ ____! 🍇", "فاكهة الشغف"),
    "dragonfruit" to Pair("فَاكِهَةُ الصَّبَّارِ الْوَرْدِيَّةِ الْجَمِيلَةِ ذَاتِ اللَّحْمِ الْأَبْيَضِ الْمُنَقَّطِ هِيَ الْـ ____! 🐉", "فاكهة التنين"),
    "boysenberry" to Pair("تُوتٌ كَبِيرٌ دَاكِنٌ يَمْتَزِجُ فِيهِ طَعْمُ الْعُلَّيْقِ مَعَ التُّوتِ الْأَسْوَدِ هُوَ الْـ ____! 🫐", "توت بويزن"),
    "chestnut" to Pair("نُسَمِّي الْكَسْتَنَاءَ اللَّذِيذَةَ الَّتِي نَشْوِيهَا فِي الشِّتَاءِ بِـ ____! 🌰", "أبو فروة"),
    "mulberry" to Pair("تُوتٌ طَوِيلٌ حُلْوٌ يَنْمُو عَلَى أَشْجَارٍ عَمِيقَةٍ تُحِبُّهَا دُودَةُ الْقَزِّ هُوَ الْـ ____! 🫐", "توت أسود"),
    "platypus" to Pair("الْـ ____ هُوَ حَيَوَانٌ ثَدْيِيٌّ بَرْمَائِيٌّ فَرِيدٌ يَبِيضُ وَلَهُ مِنْقَارٌ كَالْبَطِّ! 🦆", "منقار البط"),
    "chameleon" to Pair("تَسْتَطِيعُ الْـ ____ الذَّكِيَّةُ تَغْيِيرَ لَوْنِ جِلْدِهَا لِتَخْتَفِيَ عَنِ الْأَعْدَاءِ! 🦎", "حرباء متلونة"),
    "komodo" to Pair("يُعْتَبَرُ عِمْلَاقُ زَوَاحِفِ الْأَرْضِ الْـ جَدِيدَ بِاسْمِ ____! 🦎", "تنين كومودو"),
    "hippopotamus" to Pair("الْـ ____ حَيَوَانٌ عِمْلَاقٌ يَقْضِي مُعْظَمَ وَقْتِهِ فِي مِيَاهِ الْأَنْهَارِ الدَّافِئَةِ! 🦛", "فرس النهر"),
    "cheetah" to Pair("الْـ ____ هُوَ أَسْرَعُ الْحَيَوَانَاتِ الْبَرِّيَّةِ عَلَى الْأَرْضِ ذُو جِلْدٍ مُنَقَّطٍ جَمِيلٍ! 👑", "فهد سريع"),
    "gorilla" to Pair("تَعِيشُ الْـ ____ الْقَوِيَّةُ وَاللَّطِيفَةُ فِي عَائِلَاتٍ هَادِئَةٍ دَاخِلَ الْغَابَاتِ! 🦍", "غوريلا"),
    "chimpanzee" to Pair("يُعْتَبَرُ الْـ ____ مِنَ الْقِرَدَةِ الْأَذْكِيَاءِ جِدًّا الَّتِي تَسْتَخْدِمُ الْأَدَوَاتِ! 🐒", "شيمبانزي"),
    "jellyfish" to Pair("يَسْبَحُ الْـ ____ الشَّفَّافُ وَالْمُضِيءُ بِرِقَّةٍ فِي أَعْمَاقِ الْبِحَارِ! 🪼", "قنديل البحر"),
    "starfish" to Pair("حَيَوَانٌ بَحْرِيٌّ جَمِيلٌ لَهُ خَمْسَةُ أَذْرُعٍ يُشْبِهُ النَّجْمَ السَّاطِعَ هُوَ ____! ⭐", "نجم البحر"),
    "kangaroo" to Pair("يَقْفِزُ الْـ ____ بِسُرْعَةٍ وَيَحْمِلُ طِفْلَهُ الصَّغِيرَ فِي جَيْبٍ دَافِئٍ! 🦘", "كانغر"),
    "seismograph" to Pair("الْـ ____ هُوَ جِهَازٌ عِلْمِيٌّ حَدِيثٌ يُسْتَخْدَمُ لِرَصْدِ وَقِيَاسِ الزَّلَازِلِ! 📊", "مقياس الزلازل"),
    "gyroscope" to Pair("الْـ ____ هُوَ عِجْلَةٌ دَوَّارَةٌ تُسَاعِدُ الطَّائِرَاتِ وَالْمَرْكَبَاتِ عَلَى التَّوَازُنِ! ⚙️", "جايروسكوب"),
    "calorimeter" to Pair("الْـ ____ هُوَ وِعَاءٌ عِلْمِيٌّ مُقْفَلٌ يُسْتَخْدَمُ لِقِيَاسِ كَمِّيَّةِ الْحَرَارَةِ! 🧪", "مقياس الحرارة"),
    "speedometer" to Pair("يُسَاعِدُنَا مُؤَشِّرُ ____ فِي السَّيَّارَةِ عَلَى مَعْرِفَةِ سُرْعَتِنَا بِدِقَّةٍ! ⏱️", "مقياس السرعة"),
    "anemometer" to Pair("نَسْتَخْدِمُ جِهَازَ ____ لِقِيَاسِ سُرْعَةِ وَقُوَّةِ الرِّيَاحِ فِي الْأَرْصَادِ! 💨", "مقياس الرياح"),
    "syringe" to Pair("يَسْتَخْدِمُ الطَّبِيبُ ____ طِبِّيَّةً لِإِعْطَاءِ اللِّقَاحَاتِ الْحَامِيَةِ لِلْأَطْفَالِ! 💉", "حقنة طبية"),
    "calculator" to Pair("الْـ ____ الإِلْكِتْرُونِيَّةُ تُسَاعِدُنَا عَلَى حساب الْأَرْقَامِ الْصَعْبَةِ بِسُرْعَةٍ! 🧮", "حاسبة"),
    "telescope" to Pair("نَنْظُرُ عَبْرَ الْـ ____ الْفَلَكِيِّ لِنَرَى النُّجُومَ وَالْكَوَاكِبَ الْبَعِيدَةَ جِدًّا! 🔭", "تليسكوب"),
    "microscope" to Pair("الْـ ____ الْعِلْمِيُّ الدَّقِيقُ يُكَبِّرُ الْخَلَايَا الصَّغِيرَةَ الَّتِي لَا نَرَاهَا! 🔬", "مجهر"),
    "globe" to Pair("الْـ ____ تُمَثِّلُ كُرَتَنَا الْأَرْضِيَّةَ الرَّائِعَةَ وَتُرِينَا مَوَاقِعَ الدُّوَلِ! 🌐", "كرة أرضية")
)

@Composable
fun ColumnScope.FlashcardActivityLayout(
    profile: ChildProfile,
    word: WordItem,
    onSoundTrigger: (String) -> Unit,
    onPlaySound: (isArabic: Boolean) -> Unit,
    onNextClick: () -> Unit
) {
    val level = profile.level
    var flipped by remember(word.id) { mutableStateOf(false) }
    val isArabicPath = profile.currentLanguage == "Arabic"
    val density = androidx.compose.ui.platform.LocalDensity.current.density
    
    // Spelling progress state for Age 4 & 5 children
    val targetString = remember(word.id, isArabicPath) {
        if (isArabicPath) word.arabic.trim() else word.english.uppercase().trim()
    }
    var spellingProgress by remember(word.id, isArabicPath) { mutableStateOf("") }
    
    // Level 5 Sentence Puzzle States
    var sentenceSolved by remember(word.id, isArabicPath) { mutableStateOf(false) }
    var sentenceErrorChoice by remember(word.id, isArabicPath) { mutableStateOf<String?>(null) }

    // Automatically speak words when looking at either side of the card!
    LaunchedEffect(word.id, flipped) {
        delay(400)
        onPlaySound(flipped)
    }

    Text(
        text = "Tap the Card to Flip! 🎴",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF5D4037),
        modifier = Modifier.padding(bottom = 16.dp)
    )

    // Flipping Animation rotation value
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(600, easing = EaseInOutSine),
        label = "flashcard_rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .shadow(6.dp, RoundedCornerShape(28.dp))
            .clickable { flipped = !flipped }
            .testTag("flashcard_container"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (flipped) Color(0xFFFFF3E0) else Color.White
        ),
        border = BorderStroke(2.dp, Color(0xFFFFB300))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation < 90f) {
                // Front: English definition
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(word.emoji, fontSize = 72.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        word.english,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF3E2723)
                    )
                    Text("English", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    IconButton(
                        onClick = { onPlaySound(false) }, // Speak English
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .shadow(1.dp, CircleShape)
                    ) {
                        Text("🔊", fontSize = 24.sp)
                    }
                }
            } else {
                // Back: Arabic representation
                Column(
                    modifier = Modifier.graphicsLayer {
                        rotationY = 180f // undo horizontal mirroring from 3D flipping
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🇸🇦", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        word.arabic,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text("[ ${word.arabicPhonetic} ]", fontSize = 14.sp, color = Color(0xFF558B2F))
                    Spacer(modifier = Modifier.height(16.dp))

                    IconButton(
                        onClick = { onPlaySound(true) }, // Speak Arabic
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .shadow(1.dp, CircleShape)
                    ) {
                        Text("🔊", fontSize = 24.sp)
                    }
                }
            }
        }
    }

    // Advanced Level 4 Word Lab spelling sandbox below the flashcard!
    if (level == 4) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .shadow(3.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, Color(0xFF81C784))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (flipped) "🐝 تحدي تركيب حروف الكلمة المتقدم! 🧪" else "🐝 Advanced Word Lab Spelling Block! 🧪",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1B5E20)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Words blank blocks representation
                CompositionLocalProvider(
                    LocalLayoutDirection provides (if (isArabicPath) LayoutDirection.Rtl else LayoutDirection.Ltr)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        targetString.forEachIndexed { idx, char ->
                            val isSpelled = idx < spellingProgress.length
                            val charToShow = if (isSpelled) spellingProgress[idx].toString() else ""

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .border(
                                        width = 2.dp,
                                        color = if (isSpelled) Color(0xFF2E7D32) else Color(0xFFC8E6C9),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = charToShow,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val isSpellingComplete = spellingProgress.equals(targetString, ignoreCase = true)

                if (isSpellingComplete) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text("🎉 ", fontSize = 16.sp)
                        Text(
                            text = if (flipped) "أحسنت يا بطل! ركبت حروف الكلمة بنجاح! +10 عسل 🍯" else "Superb spelling! Word complete! +10 Honey Stars! 🍯",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                    }
                } else {
                    // Shuffled letter choice bubbles
                    val remainingShuffled = remember(word.id, flipped) {
                        targetString.map { it.toString() }.shuffled()
                    }

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        remainingShuffled.forEachIndexed { _, charToken ->
                            val isCorrectNext = targetString.getOrNull(spellingProgress.length)?.toString().equals(charToken, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFA5D6A7))
                                    .clickable {
                                        if (isCorrectNext) {
                                            spellingProgress += targetString[spellingProgress.length].toString()
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = charToken,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (flipped) "إعادة المحاولة 🔄" else "Reset progress 🔄",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037),
                        modifier = Modifier.clickable { spellingProgress = "" }
                    )
                }
            }
        }
    }

    // Advanced Level 5 Word Lab Sentence Connector and Spelling Sandbox
    if (level == 5) {
        val correctWordOption = remember(word.id, isArabicPath) {
            if (isArabicPath) {
                wordLabSentencesAr[word.id.lowercase()]?.second ?: word.arabic
            } else {
                wordLabSentencesEng[word.id.lowercase()]?.second ?: word.english
            }
        }

        val sentenceOptions = remember(word.id, isArabicPath, correctWordOption) {
            val alt1 = if (isArabicPath) {
                if (word.arabic == "نحلة" || word.arabic == "قطة") "كلب" else "نحلة"
            } else {
                if (word.english.uppercase() == "BEE" || word.english.uppercase() == "CAT") "DOG" else "BEE"
            }
            val alt2 = if (isArabicPath) {
                if (word.arabic == "كتاب" || word.arabic == "سيارة") "بيت" else "كتاب"
            } else {
                if (word.english.uppercase() == "BOOK" || word.english.uppercase() == "CAR") "HOUSE" else "BOOK"
            }
            listOf(correctWordOption, alt1, alt2).distinct().shuffled()
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .shadow(4.dp, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)), // Royal Blue background for level 5
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(2.dp, Color(0xFF90CAF9))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("🧩 ", fontSize = 16.sp)
                    Text(
                        text = if (isArabicPath) "تركيب الجمل المتقدم - المستوى ٥! 👑" else "Advanced Sentence Builder - Level 5! 👑",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0D47A1)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sentence blank or solved preview
                val sentencePair = if (isArabicPath) {
                    wordLabSentencesAr[word.id.lowercase()]
                } else {
                    wordLabSentencesEng[word.id.lowercase()]
                } ?: Pair(if (isArabicPath) "نحن نحب ____ اللطيف! 💖" else "We love the ____! 💖", correctWordOption)

                val displayPhrase = if (sentenceSolved) {
                    sentencePair.first.replace("____", correctWordOption)
                } else {
                    sentencePair.first
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFBBDEFB))
                ) {
                    Text(
                        text = displayPhrase,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (sentenceSolved) Color(0xFF2E7D32) else Color(0xFF37474F),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!sentenceSolved) {
                    Text(
                        text = if (isArabicPath) "اختر الكلمة الصحيحة لملء الفراغ:" else "Find the correct word to fill the blank:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        sentenceOptions.forEach { option ->
                            val isError = sentenceErrorChoice == option
                            val chipBg = when {
                                isError -> Color(0xFFFFEBEE)
                                else -> Color.White
                            }
                            val chipBorder = when {
                                isError -> Color(0xFFEF5350)
                                else -> Color(0xFF90CAF9)
                            }

                            Card(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .weight(1f)
                                    .height(44.dp)
                                    .clickable {
                                        if (option.equals(correctWordOption, ignoreCase = true)) {
                                            sentenceSolved = true
                                            sentenceErrorChoice = null
                                            val completePhraseText = sentencePair.first.replace("____", correctWordOption)
                                            if (isArabicPath) {
                                                onSoundTrigger("text:arabic:$completePhraseText")
                                            } else {
                                                onSoundTrigger("text:english:$completePhraseText")
                                            }
                                        } else {
                                            sentenceErrorChoice = option
                                            onSoundTrigger(if (isArabicPath) "text:arabic:حاول مرة أخرى يا بطل" else "text:english:Try again, champion!")
                                        }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = chipBg),
                                border = BorderStroke(1.5.dp, chipBorder)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = option,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isError) Color(0xFFC62828) else Color(0xFF0D47A1)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(10.dp))
                                .padding(8.dp)
                        ) {
                            Text("✨ ", fontSize = 16.sp)
                            Text(
                                text = if (isArabicPath) "رائع! الآن تهجى الكلمة لتكتمل الخلية!" else "Splendid! Now spell the word to complete the lock!",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Spelling Blocks
                        CompositionLocalProvider(
                            LocalLayoutDirection provides (if (isArabicPath) LayoutDirection.Rtl else LayoutDirection.Ltr)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                targetString.forEachIndexed { idx, char ->
                                    val isSpelled = idx < spellingProgress.length
                                    val charToShow = if (isSpelled) spellingProgress[idx].toString() else ""

                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(Color.White, RoundedCornerShape(8.dp))
                                            .border(
                                                width = 2.dp,
                                                color = if (isSpelled) Color(0xFF2E7D32) else Color(0xFFE0E0E0),
                                                shape = RoundedCornerShape(8.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = charToShow,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF0D47A1)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val isSpellingComplete = spellingProgress.equals(targetString, ignoreCase = true)

                        if (isSpellingComplete) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text("🏆 ", fontSize = 16.sp)
                                Text(
                                    text = if (isArabicPath) "مذهل! بنيت الجملة وتهجيت الكلمة بنجاح! +15 عسل 🍯" else "Stellar! Sentence and spelling complete! +15 Honey Stars! 🍯",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        } else {
                            val remainingShuffled = remember(word.id, isArabicPath) {
                                targetString.map { it.toString() }.shuffled()
                            }

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                remainingShuffled.forEachIndexed { _, charToken ->
                                    val isCorrectNext = targetString.getOrNull(spellingProgress.length)?.toString().equals(charToken, ignoreCase = true)
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFBBDEFB))
                                            .clickable {
                                                if (isCorrectNext) {
                                                    spellingProgress += targetString[spellingProgress.length].toString()
                                                    if (spellingProgress.length == targetString.length) {
                                                        onSoundTrigger(if (isArabicPath) "text:arabic:عمل رائع ومتميز يا بطل" else "text:english:Incredible spelling job helper!")
                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = charToken,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF0D47A1)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isArabicPath) "إعادة محاولة التهجئة 🔄" else "Reset spelling progress 🔄",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5D4037),
                                modifier = Modifier.clickable { spellingProgress = "" }
                            )
                        }
                    }
                }
            }
        }
    }

    // Advanced Level 6 Word Lab Fill-in-the-letter activity
    if (level >= 6) {
        var grammarSolved by remember(word.id, isArabicPath) { mutableStateOf(false) }
        var grammarErrorChoice by remember(word.id, isArabicPath) { mutableStateOf<String?>(null) }

        val grammarQuestion = remember(word.id, isArabicPath) {
            val wid = word.id.lowercase()
            if (isArabicPath) {
                when (wid) {
                    "calculator" -> Triple("ما نوع كلمة (حاسبة)؟", listOf("اسم آلة", "اسم فاعل", "اسم مفعول"), "اسم آلة")
                    "telescope" -> Triple("كلمة (تليسكوب) تستخدم لرؤية الأشياء الـ...", listOf("البعيدة جداً", "القريبة جداً", "الدقيقة مجهرياً"), "البعيدة جداً")
                    "microscope" -> Triple("ما وزن كلمة (مِجْهَر)؟", listOf("مِفْعَل", "مِفْعَال", "مَفْعَل"), "مِفْعَل")
                    "globe" -> Triple("ما الحركة الإعرابية لـ (كرةُ) في (كرةٌ أرضيةٌ)؟", listOf("الضمة", "الفتحة", "الكسرة"), "الضمة")
                    "cheetah" -> Triple("ما إعراب كلمة (سريعٌ) في (هذا فهدٌ سريعٌ)؟", listOf("نعت/صفة", "فاعل مرفوع", "مفعول به منصوب"), "نعت/صفة")
                    "gorilla" -> Triple("كلمة (غوريلا) تصنف علمياً من...", listOf("الثدييات", "الزواحف", "الطيور"), "الثدييات")
                    "syringe" -> Triple("ما هي وظيفة (الحقنة الطبية)؟", listOf("إعطاء اللقاحات والأدوية", "قياس درجة الحرارة", "فحص نبضات القلب"), "إعطاء اللقاحات والأدوية")
                    "anemometer" -> Triple("كلمة (الرياح) في (مقياسُ الرياحِ) تعرب...", listOf("مضاف إليه مجرور", "فاعل مرفوع", "صفة مرفوعة"), "مضاف إليه مجرور")
                    "seismograph" -> Triple("الزلازل التي يقيسها (السيسموغراف) هي هزات أرضية...", listOf("مفاجئة وقوية", "بطيئة جداً ودائمة", "تحدث في الفضاء فقط"), "مفاجئة وقوية")
                    "hippopotamus" -> Triple("كلمة (فرس) في (فرس النهر) هي اسم...", listOf("مذكر", "مؤنث", "فعل مضارع"), "مذكر")
                    else -> Triple("ما الصفة المناسبة لـ (${word.arabic}) في جملة مفيدة؟", listOf("مفيدٌ ونافعٌ", "صعبٌ جداً", "مخيفٌ للغاية"), "مفيدٌ ونافعٌ")
                }
            } else {
                when (wid) {
                    "calculator" -> Triple("What type of noun is 'calculator'?", listOf("Concrete Noun", "Abstract Noun", "Proper Noun"), "Concrete Noun")
                    "telescope" -> Triple("What does the Greek prefix 'tele-' mean in 'telescope'?", listOf("Distant / Far", "Small / Micro", "Sound / Audio"), "Distant / Far")
                    "microscope" -> Triple("What does the Greek prefix 'micro-' mean in 'microscope'?", listOf("Small / Minute", "Large / Macro", "Heavy / Weight"), "Small / Minute")
                    "globe" -> Triple("What geometric shape represents a 'globe'?", listOf("Spherical", "Flat Plane", "Cubic Box"), "Spherical")
                    "cheetah" -> Triple("Identify the adverb in: 'The cheetah runs extremely fast.'", listOf("extremely", "runs", "fast"), "extremely")
                    "gorilla" -> Triple("What is the correct plural form of the word 'gorilla'?", listOf("gorillas", "gorillae", "gorillaes"), "gorillas")
                    "chimpanzee" -> Triple("Which pronoun best replaces 'The chimpanzee' in a sentence?", listOf("It", "He", "They"), "It")
                    "gyroscope" -> Triple("What does the root word 'gyro' mean in 'gyroscope'?", listOf("Spin or Circle", "Static or Fixed", "Heavy or Metal"), "Spin or Circle")
                    "speedometer" -> Triple("What does the Greek suffix '-meter' mean?", listOf("To measure", "To drive", "To spin"), "To measure")
                    "syringe" -> Triple("In 'The doctor used a syringe', what part of speech is 'used'?", listOf("Verb", "Noun", "Adjective"), "Verb")
                    "anemometer" -> Triple("What does an anemometer measure?", listOf("Wind speed & force", "Earthquake magnitude", "Water temperature"), "Wind speed & force")
                    "seismograph" -> Triple("What does the prefix 'seismo-' relate to?", listOf("Earthquakes & shaking", "Deep ocean water", "Stars & planets"), "Earthquakes & shaking")
                    "calorimeter" -> Triple("What does a calorimeter measure in science?", listOf("Heat of chemical reactions", "Light intensity", "Sound frequency"), "Heat of chemical reactions")
                    else -> Triple("What is the correct category of (${word.english})?", listOf("Nouns & Objects", "Action Verbs", "Descriptive Adjectives"), "Nouns & Objects")
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .shadow(4.dp, RoundedCornerShape(24.dp))
                .testTag("grammar_booster_card"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)), // Deep purple / lavender theme
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(2.dp, Color(0xFFE1BEE7))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("🧠 ", fontSize = 18.sp)
                    Text(
                        text = if (isArabicPath) "مُستكشف القواعد واللغة - المستوى ٦! 🚀" else "Grammar & Meaning Sandbox - Level 6! 🚀",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4A148C)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = grammarQuestion.first,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF311B92),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (!grammarSolved) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        grammarQuestion.second.forEach { option ->
                            val isError = grammarErrorChoice == option
                            val choiceBg = if (isError) Color(0xFFFFEBEE) else Color.White
                            val choiceBorder = if (isError) Color(0xFFEF5350) else Color(0xFFD1C4E9)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clickable {
                                        if (option == grammarQuestion.third) {
                                            grammarSolved = true
                                            grammarErrorChoice = null
                                            if (isArabicPath) {
                                                onSoundTrigger("text:arabic:رائع ومتميز! إجابتك صحيحة!")
                                            } else {
                                                onSoundTrigger("text:english:Stellar job, champion! That is correct!")
                                            }
                                        } else {
                                            grammarErrorChoice = option
                                            if (isArabicPath) {
                                                onSoundTrigger("text:arabic:حاول مرة أخرى يا بطل!")
                                            } else {
                                                onSoundTrigger("text:english:Close attempt! Try another option!")
                                            }
                                        }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = choiceBg),
                                border = BorderStroke(1.5.dp, choiceBorder)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = option,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isError) Color(0xFFC62828) else Color(0xFF4A148C),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Text("🎉 🎓 ", fontSize = 18.sp)
                        Text(
                            text = if (isArabicPath) "إجابة صحيحة ومذهلة! لقد كسبت نجمة النور بي! 🍯" else "Correct! You successfully solved the Grammar Booster! 🍯",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val missingCharIdx = remember(word.id, isArabicPath) {
            val s = targetString.trim()
            if (s.length > 2) s.length / 2 else 0
        }
        val correctChar = remember(word.id, isArabicPath, missingCharIdx) {
            targetString.trim().getOrNull(missingCharIdx)?.toString() ?: ""
        }
        
        var selectedFillChar by remember(word.id, isArabicPath) { mutableStateOf<String?>(null) }
        var isFillCorrect by remember(word.id, isArabicPath) { mutableStateOf<Boolean?>(null) }
        
        val fillOptions = remember(word.id, isArabicPath, correctChar) {
            val fallbackAlphabet = if (isArabicPath) {
                listOf("أ", "ب", "ت", "ث", "ج", "ح", "خ", "د", "ذ", "ر", "ز", "س", "ش", "ص", "ض", "ط", "ظ", "ع", "غ", "ف", "ق", "ك", "ل", "م", "ن", "هـ", "و", "ي")
            } else {
                listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
            }
            val filtered = fallbackAlphabet.filter { !it.equals(correctChar, ignoreCase = true) }.shuffled().take(2)
            (filtered + correctChar).distinct().shuffled()
        }
        
        val letterLayoutDirection = if (isArabicPath) androidx.compose.ui.unit.LayoutDirection.Rtl else androidx.compose.ui.unit.LayoutDirection.Ltr
        androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides letterLayoutDirection) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .shadow(4.dp, RoundedCornerShape(24.dp))
                    .testTag("fill_in_the_letter_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)), // Sunny yellow background for level 6
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(2.dp, Color(0xFFFBC02D))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("💡 ", fontSize = 18.sp)
                        Text(
                            text = if (isArabicPath) "تحدي ملء الحرف الناقص - المستوى ٦! 👑" else "Fill-in-the-Letter Challenge - Level 6! 👑",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFE65100)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    // Display the word with "_"
                    CompositionLocalProvider(
                        LocalLayoutDirection provides (if (isArabicPath) LayoutDirection.Rtl else LayoutDirection.Ltr)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                        ) {
                        targetString.forEachIndexed { idx, char ->
                            val isMissing = idx == missingCharIdx
                            val isSolved = isFillCorrect == true
                            val textToShow = when {
                                isMissing && isSolved -> correctChar
                                isMissing -> "_"
                                else -> char.toString()
                            }
                            val boxBg = if (isMissing) {
                                if (isSolved) Color(0xFFC8E6C9) else Color(0xFFFFCC80)
                            } else {
                                Color.White
                            }
                            val boxBorderColor = if (isMissing) {
                                if (isSolved) Color(0xFF2E7D32) else Color(0xFFE65100)
                            } else {
                                Color(0xFFE0E0E0)
                            }
                            
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(38.dp)
                                    .background(boxBg, RoundedCornerShape(8.dp))
                                    .border(2.dp, boxBorderColor, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = textToShow,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isMissing) Color(0xFFE65100) else Color(0xFF37474F)
                                )
                            }
                        }
                    }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (isFillCorrect == null || isFillCorrect == false) {
                        Text(
                            text = if (isArabicPath) "اختر الحرف الصحيح لإكمال الكلمة:" else "Choose the correct letter to complete the word:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            fillOptions.forEach { option ->
                                val isChosen = selectedFillChar == option
                                val isCorrect = option.equals(correctChar, ignoreCase = true)
                                val buttonBg = when {
                                    isChosen && isCorrect -> Color(0xFFC8E6C9)
                                    isChosen && !isCorrect -> Color(0xFFFFCDD2)
                                    else -> Color.White
                                }
                                val borderCol = when {
                                    isChosen && isCorrect -> Color(0xFF4CAF50)
                                    isChosen && !isCorrect -> Color(0xFFF44336)
                                    else -> Color(0xFFFBC02D)
                                }
                                val textCol = when {
                                    isChosen && isCorrect -> Color(0xFF2E7D32)
                                    isChosen && !isCorrect -> Color(0xFFC62828)
                                    else -> Color(0xFFE65100)
                                }
                                
                                Card(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .size(52.dp)
                                        .clickable {
                                            selectedFillChar = option
                                            if (isCorrect) {
                                                isFillCorrect = true
                                                if (isArabicPath) {
                                                    onSoundTrigger("text:arabic:أحسنت يا بطل! الإجابة صحيحة، حرف $correctChar يكمل الكلمة!")
                                                } else {
                                                    onSoundTrigger("text:english:Excellent job! The letter $correctChar is correct!")
                                                }
                                            } else {
                                                isFillCorrect = false
                                                if (isArabicPath) {
                                                    onSoundTrigger("text:arabic:حاول مجدداً يا بطل!")
                                                } else {
                                                    onSoundTrigger("text:english:That is close! Try again!")
                                                }
                                            }
                                        },
                                    shape = CircleShape,
                                    colors = CardDefaults.cardColors(containerColor = buttonBg),
                                    border = BorderStroke(2.dp, borderCol)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = option,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Black,
                                            color = textCol
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Text("✨ 🍯 ", fontSize = 20.sp)
                            Text(
                                text = if (isArabicPath) "عبقري! لقد أكملت الكلمة بنجاح وحصلت على +20 من النجوم العسل! 🐝" else "Incredible! Balanced fill-in complete! Claimed +20 honey stars! 🐝",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onNextClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E342E)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Text("Done • Collect Coins 🍯", fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
    }
}

// --- Layout 4: Listening Activity --
@Composable
fun ColumnScope.ListeningActivityLayout(
    word: WordItem,
    isArabic: Boolean,
    onPlayAudio: () -> Unit,
    onComplete: () -> Unit
) {
    var hasPlayed by remember { mutableStateOf(false) }
    var selectedVal by remember { mutableStateOf<String?>(null) }
    var resultChecked by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(word) {
        delay(400)
        onPlayAudio()
        hasPlayed = true
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Listen closely and tap the right card! 🎧",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Play button
            Button(
                onClick = {
                    onPlayAudio()
                    hasPlayed = true
                },
                modifier = Modifier
                    .size(90.dp)
                    .shadow(4.dp, CircleShape),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                shape = CircleShape
            ) {
                Text("🔊", fontSize = 32.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (hasPlayed) "Sound playing..." else "Tap to Play Sound",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }

    Spacer(modifier = Modifier.height(30.dp))

    // Simple option list of 3 matching emojis
    val allOptions = remember(word) {
        listOf(
            Pair("apple", "🍎"),
            Pair("bee", "🐝"),
            Pair("sun", "☀️"),
            Pair("cat", "🐱")
        ).shuffled().take(3).let { choices ->
            // ensure target is included
            if (choices.any { it.first == word.id }) choices
            else (choices.take(2) + Pair(word.id, word.emoji)).shuffled()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(allOptions) { item ->
            val isSelected = selectedVal == item.first
            val borderCol = when {
                resultChecked != null && isSelected && resultChecked == true -> Color.Green
                resultChecked != null && isSelected && resultChecked == false -> Color.Red
                isSelected -> Color(0xFFFFB300)
                else -> Color.Transparent
            }

            Card(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable(enabled = hasPlayed && resultChecked == null) {
                        selectedVal = item.first
                    },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(3.dp, borderCol),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(item.second, fontSize = 42.sp)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    if (resultChecked == null) {
        Button(
            onClick = {
                val isCorrect = selectedVal == word.id
                resultChecked = isCorrect
            },
            enabled = selectedVal != null,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E342E)),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Verify Match", color = Color.White, fontWeight = FontWeight.Bold)
        }
    } else {
        Button(
            onClick = {
                if (resultChecked == true) {
                    onComplete()
                } else {
                    resultChecked = null
                    selectedVal = null
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (resultChecked == true) Color(0xFF4CAF50) else Color(0xFFD32F2F)
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(if (resultChecked == true) "Correct! Collect Honey 🌟" else "Try again", color = Color.White)
        }
    }
}

// --- Layout 5: Speaking Practice --
@Composable
fun ColumnScope.SpeakingActivityLayout(
    word: WordItem,
    isArabic: Boolean,
    isRecording: Boolean,
    onMicClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Say this word loudly! 🎙️",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E2723),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(word.emoji, fontSize = 80.sp)
            Text(
                text = if (isArabic) word.arabic else word.english,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF3E2723)
            )
            Text(
                text = if (isArabic) "[ ${word.arabicPhonetic} ]" else "/ ${word.englishPhonetic} /",
                fontSize = 15.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sound waves animation preview
            if (isRecording) {
                Text("Analyzing your voice... 🗣️", color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.height(30.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Animated sonic waves representation
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    listOf(12.dp, 28.dp, 20.dp, 35.dp, 16.dp).forEachIndexed { i, dHeight ->
                        val scaleFactor by infiniteTransition.animateFloat(
                            initialValue = 1.0f,
                            targetValue = 0.33f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(300 + (i * 80), easing = EaseInOutSine),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "waveBar"
                        )
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .height(dHeight * scaleFactor)
                                .background(Color(0xFFE53935), RoundedCornerShape(4.dp))
                        )
                    }
                }
            } else {
                Text("Tap the Mic and speak into your phone!", fontSize = 11.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Large Recording Trigger
            Button(
                onClick = onMicClick,
                modifier = Modifier
                    .size(80.dp)
                    .shadow(if (isRecording) 8.dp else 2.dp, CircleShape),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) Color(0xFFE53935) else Color(0xFF4D2C1E)
                ),
                shape = CircleShape
            ) {
                Text("🎙️", fontSize = 28.sp)
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onNextClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E342E)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Text("Done • Head Back 🐝", color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
    }
}

// --- Layout 6: Quiz Scoring Arena --
@Composable
fun ColumnScope.QuizActivityLayout(
    questions: List<WordItem>,
    answers: List<String>,
    currentIndex: Int,
    score: Int,
    checkedState: Boolean?,
    isArabic: Boolean,
    onSubmit: (String) -> Unit,
    onNext: () -> Unit
) {
    if (currentIndex >= questions.size) return
    val word = questions[currentIndex]

    // Score Board
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Question: ${currentIndex + 1} / ${questions.size}",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3E2723)
        )
        Box(
            modifier = Modifier
                .background(Color(0xFFFFECB3), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text("Score: $score ⭐️", fontWeight = FontWeight.ExtraBold, color = Color(0xFFE65100))
        }
    }

    // Linear Progress Meter
    LinearProgressIndicator(
        progress = { (currentIndex + 1).toFloat() / questions.size },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .padding(bottom = 16.dp),
        color = Color(0xFFFFB300),
        trackColor = Color(0xFFE0E0E0)
    )

    // Main Question Card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(word.emoji, fontSize = 72.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Identify translation for:",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = if (isArabic) word.arabic else word.english,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF3E2723)
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Multiple Choices answers
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        answers.forEach { choice ->
            val correctAnsVal = if (isArabic) word.english else word.arabic
            val isOptionRight = choice == correctAnsVal

            val btnColor = when {
                checkedState != null && isOptionRight -> Color(0xFFC8E6C9) // Green success highlight
                checkedState != null -> Color(0xFFF5F5F5)
                else -> Color.White
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable(enabled = checkedState == null) {
                        onSubmit(choice)
                    }
                    .testTag("quiz_choice_$choice"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = btnColor),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
                    Text(choice, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    if (checkedState != null) {
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4E342E)
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Next Question 🎉", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
