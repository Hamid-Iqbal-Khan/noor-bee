package com.example.english

import androidx.compose.ui.graphics.Color

/*
 * English reading course — the English-medium partner of the Quran course, built the same way:
 * stages of short lessons, each stage closed by a 10-question practice (8 correct completes it).
 * Order follows common synthetic-phonics teaching: letter sounds -> blending short words ->
 * digraphs -> long vowels -> tricky (sight) words -> reading sentences.
 * Every keyword and practice word is an Islamic word or comes from a child's Muslim life
 * (A for Allah, B for Bismillah, D for Dua...). Where an Islamic word does not show the
 * letter's usual sound (Charity, Eid, Quran), the card says so.
 * Sound is the phone's English voice; keywords are given so a parent can model each sound.
 */

/** A letter (or letter group) with the sound it makes, a keyword for that sound and a picture. */
data class Sound(val grapheme: String, val keyword: String, val emoji: String, val note: String = "") {
    /** What the voice says when the tile is tapped. */
    val spoken get() = "$grapheme, as in $keyword"
}

/** A word to read, broken into its sound tiles: c + a + t = cat. */
data class ReadWord(val word: String, val parts: List<String>, val emoji: String)

/** A sentence to read; [key] is the word its picture shows (used for "fill the gap"). */
data class Sentence(val text: String, val emoji: String, val key: String = "")

/** A phrase to say, what it means or when we say it, and a picture. */
data class Phrase(val text: String, val meaning: String, val emoji: String)

/** One line of a conversation. */
data class Line(val speaker: String, val text: String)

enum class LessonKind { ALPHABET, SOUNDS, WORDS, PAIRS, TRICKY, CARDS, SENTENCES, STORY, PHRASES, DIALOGUE }

data class EnLesson(
    val id: String,           // progress key, e.g. "e2.3"
    val title: String,
    val subtitle: String,
    val kind: LessonKind,
    val intro: String,
    val sounds: List<Sound> = emptyList(),
    val words: List<ReadWord> = emptyList(),
    val pairs: List<Pair<ReadWord, ReadWord>> = emptyList(),   // magic e: cap -> cape
    val tricky: List<String> = emptyList(),
    val sentences: List<Sentence> = emptyList(),
    val phrases: List<Phrase> = emptyList(),
    val dialogue: List<Line> = emptyList(),
)

data class EnStage(
    val number: Int, val short: String, val title: String, val subtitle: String, val hero: String,
    val emoji: String, val bg: Color, val line: Color, val lessons: List<EnLesson>,
) {
    val quizKey get() = "e$number.q"
}

private fun w(word: String, emoji: String, vararg parts: String) =
    ReadWord(word, if (parts.isEmpty()) word.map { it.toString() } else parts.toList(), emoji)

object EnglishCourse {

    // ---- Stage 1: letters and their sounds, each with an Islamic keyword ---------------------------------
    val alphabet = listOf(
        Sound("a", "Allah", "☝️"),
        Sound("b", "Bismillah", "📖"),
        Sound("c", "Charity", "💝", "In Charity, c and h together say 'ch'. On its own c says 'k', as in camel."),
        Sound("d", "Dua", "🤲"),
        Sound("e", "Eid", "🎉", "In Eid, e says its long sound 'ee'. Its short sound is 'e' as in egg."),
        Sound("f", "Fajr", "🌅"),
        Sound("g", "Good deeds", "🤝"),
        Sound("h", "Hajj", "⛺"),
        Sound("i", "Islam", "💚"),
        Sound("j", "Jannah", "🌴"),
        Sound("k", "Kaaba", "⬛"),
        Sound("l", "Learn", "📚", "The first word of the Quran revealed was Iqra: Read!"),
        Sound("m", "Masjid", "🕌"),
        Sound("n", "Namaz", "🙇", "Namaz is the Urdu and Persian word for Salah, the prayer."),
        Sound("o", "Olive", "🌿", "Allah swears by the fig and the olive in Surah At-Tin."),
        Sound("p", "Prophet", "🌹", "Prophet Muhammad ﷺ is the last Prophet of Allah."),
        Sound("q", "Quran", "📗", "In English q nearly always comes with u (quiet). Quran is an Arabic word."),
        Sound("r", "Ramadan", "🌙"),
        Sound("s", "Salah", "🕋", "Salah is the prayer we offer five times a day."),
        Sound("t", "Tasbih", "📿"),
        Sound("u", "Ummah", "🌍", "The Ummah is all the Muslims of the world."),
        Sound("v", "Virtue", "🏅"),
        Sound("w", "Wudu", "💧"),
        Sound("x", "six", "6️⃣", "No Islamic word starts with x. x says 'ks' at the end of a word: we believe in six articles of faith."),
        Sound("y", "Yusuf", "🌟", "Prophet Yusuf, peace be upon him: his story is told in Surah Yusuf."),
        Sound("z", "Zakat", "💰"),
    )

    private fun sounds(letters: String) = letters.split(' ').map { g -> alphabet.firstOrNull { it.grapheme == g } ?: extra.getValue(g) }
    private val extra = mapOf(
        "ck" to Sound("ck", "black stone", "⬛", "c and k together make one sound"),
        "qu" to Sound("qu", "quiet", "🤫", "q and u together say 'kw'"),
    )

    /** Teaching groups — the first six letters already make words (Nas, sit, tap, pin). */
    val groups = listOf(
        "s a t p i n" to "With just these six you can read: sit, tap, pin, tip",
        "m d g o c k" to "Now: Adam, dad, Mom, mat (prayer mat), Mecca",
        "ck e u r" to "Now: Eid, Ummah, sun, rest",
        "h b f l" to "Now: Bilal, halal, help, Fajr",
        "j v w x y z qu" to "Now: Hajj, Wudu, Zakat, six, Yusuf, quiet",
    )

    // ---- Stage 2: blending short Islamic words (the first vowel is a short sound) ------------------------
    val shortA = listOf(w("Adam", "🌱"), w("Hajj", "🕋", "H", "a", "jj"), w("mat", "🙏"), w("cap", "🧢"), w("hands", "🤲"),
        w("lamb", "🐑"), w("dad", "👨"))
    val shortE = listOf(w("Mecca", "🕌", "M", "e", "cc", "a"), w("help", "🤝"), w("ten", "🔟"), w("bed", "🛏️"), w("pen", "🖊️"),
        w("Medina", "🌴"))
    val shortI = listOf(w("Islam", "💚"), w("Ismail", "🐏"), w("Ibrahim", "🔥"), w("gift", "🎁"), w("milk", "🥛"))
    val shortO = listOf(w("box", "📦"), w("Mom", "👩"), w("honest", "✅"), w("socks", "🧦", "s", "o", "ck", "s"))
    val shortU = listOf(w("Ummah", "🌍", "U", "mm", "a", "h"), w("Jummah", "📅", "J", "u", "mm", "a", "h"), w("sun", "☀️"),
        w("cup", "☕"), w("hug", "🤗"))

    /** The first vowel tile of a word (the sound the Stage 2 practice asks about). */
    fun firstVowel(word: ReadWord): String = word.parts.first { it.lowercase() in setOf("a", "e", "i", "o", "u") }.lowercase()

    // ---- Stage 3: two letters, one sound ------------------------------------------------------------
    val digraphsA = listOf(
        Sound("sh", "ship", "🚢", "put a finger on your lips: shh! Prophet Nuh built a ship"),
        Sound("ch", "Charity", "💝"),
        Sound("th", "thanks", "🙏", "tongue between the teeth — like the Arabic ث"),
    )
    val digraphWordsA = listOf(w("ship", "🚢", "sh", "i", "p"), w("fish", "🐟", "f", "i", "sh"), w("wash", "🧼", "w", "a", "sh"),
        w("charity", "💝", "ch", "a", "r", "i", "t", "y"), w("children", "🧒", "ch", "i", "l", "d", "r", "e", "n"),
        w("chapter", "📖", "ch", "a", "p", "t", "er"),
        w("thanks", "🙏", "th", "a", "n", "k", "s"), w("truth", "✔️", "t", "r", "u", "th"))
    val digraphsB = listOf(
        Sound("ck", "black stone", "⬛", "c and k together make one sound"),
        Sound("ng", "king", "👑", "the sound comes through the nose — like ghunnah! Al-Malik, the King, is a name of Allah"),
        Sound("ll", "Allah", "☝️", "two l's, one sound"),
        Sound("ss", "bless", "✨", "two s's, one sound"),
    )
    val digraphWordsB = listOf(w("black", "⬛", "b", "l", "a", "ck"), w("sick", "🤒", "s", "i", "ck"),
        w("fasting", "🍽️", "f", "a", "s", "t", "i", "ng"), w("king", "👑", "k", "i", "ng"),
        w("Allah", "☝️", "A", "ll", "a", "h"), w("well", "💧", "w", "e", "ll"),
        w("kindness", "💗", "k", "i", "n", "d", "n", "e", "ss"), w("bless", "✨", "b", "l", "e", "ss"))
    val digraphsC = listOf(
        Sound("wh", "white", "⚪", "pilgrims wear white for Hajj"),
        Sound("ph", "Prophet", "🌹", "p and h together say 'f'"),
        Sound("qu", "quiet", "🤫", "q and u together say 'kw'. Be quiet when the Quran is recited"),
    )
    val digraphWordsC = listOf(w("when", "⏰", "wh", "e", "n"), w("which", "🤔", "wh", "i", "ch"),
        w("Prophet", "🌹", "P", "r", "o", "ph", "e", "t"), w("dolphin", "🐬", "d", "o", "l", "ph", "i", "n"),
        w("quiet", "🤫", "qu", "i", "e", "t"), w("quick", "⚡", "qu", "i", "ck"))

    // ---- Stage 4: long vowels ---------------------------------------------------------------------------
    val magicE = listOf(
        w("hop", "🐇") to w("hope", "🤲", "h", "o_e", "p"),
        w("mad", "😠") to w("made", "🌸", "m", "a_e", "d"),
        w("cub", "🐻") to w("cube", "🕋", "c", "u_e", "b"),
        w("plan", "🗺️") to w("plane", "✈️", "p", "l", "a_e", "n"),
        w("rip", "👖") to w("ripe", "🌴", "r", "i_e", "p"),
        w("glob", "💧") to w("globe", "🌍", "g", "l", "o_e", "b"),
        w("pin", "📌") to w("pine", "🌲", "p", "i_e", "n"),
        w("cut", "✂️") to w("cute", "🥰", "c", "u_e", "t"),
    )
    val vowelTeams = listOf(
        Sound("ai", "rain", "🌧️", "ai and ay both say the name of a"), Sound("ay", "pray", "🙇"),
        Sound("ee", "Deen", "📗", "ee and ea both say the name of e"), Sound("ea", "peace", "🕊️"),
        Sound("oa", "boat", "🚢", "oa and ow (as in grow) say the name of o. Prophet Nuh built the ark"), Sound("ow", "grow", "🌱"),
        Sound("igh", "night", "🌃", "three letters, one sound: the name of i. Laylat al-Qadr is a blessed night"), Sound("oo", "moon", "🌙"),
    )
    val teamWords = listOf(w("rain", "🌧️", "r", "ai", "n"), w("pray", "🙇", "p", "r", "ay"), w("praise", "✨", "p", "r", "ai", "s", "e"),
        w("Deen", "📗", "D", "ee", "n"), w("sleep", "😴", "s", "l", "ee", "p"), w("tree", "🌳", "t", "r", "ee"),
        w("eat", "🍽️", "ea", "t"), w("peace", "🕊️", "p", "ea", "c", "e"),
        w("boat", "🚢", "b", "oa", "t"), w("grow", "🌱", "g", "r", "ow"),
        w("night", "🌃", "n", "igh", "t"), w("light", "💡", "l", "igh", "t"), w("right", "✋", "r", "igh", "t"),
        w("moon", "🌙", "m", "oo", "n"), w("food", "🍲", "f", "oo", "d"))

    // ---- Stage 5: tricky words — learn them by sight -------------------------------------------------
    val tricky1 = listOf("the", "to", "I", "no", "go", "into", "he", "she", "we", "me", "be", "was")
    val tricky2 = listOf("you", "they", "all", "are", "my", "her", "said", "have", "like", "so", "do", "some")
    val tricky3 = listOf("come", "were", "there", "little", "one", "when", "out", "what", "here", "their", "people", "could")
    val islamicWords = listOf("Allah", "Muhammad", "Islam", "Muslim", "Quran", "Salah", "Masjid", "Ramadan", "Eid", "Sunnah", "Hadith", "Jannah")
    val allTricky = tricky1 + tricky2 + tricky3 + islamicWords

    // ---- Stage 6: reading sentences -------------------------------------------------------------------
    val sentences1 = listOf(
        Sentence("Allah made the sun and the moon.", "☀️", "sun"), Sentence("I read the Quran every day.", "📗", "Quran"),
        Sentence("We go to the masjid to pray.", "🕌", "masjid"), Sentence("I give charity to the poor.", "💝", "charity"),
        Sentence("We fast in the month of Ramadan.", "🌙", "Ramadan"), Sentence("I make wudu before I pray.", "💧", "wudu"),
    )
    val sentences2 = listOf(
        Sentence("I say Bismillah before I eat.", "🍽️", "eat"), Sentence("I say Assalamu alaikum to my friend.", "👋", "friend"),
        Sentence("I help my mum and dad at home.", "🏠", "home"), Sentence("I give a gift on Eid.", "🎁", "gift"),
        Sentence("I make dua with my hands.", "🤲", "hands"), Sentence("I share my toys with my sister.", "🧸", "toys"),
    )
    val story = listOf(
        Sentence("It is a very hot day.", "☀️"), Sentence("Ali sees a thirsty cat.", "🐱"),
        Sentence("He fills a cup with water.", "💧"), Sentence("The cat drinks and drinks.", "😺"),
        Sentence("Ali says, \"Alhamdulillah!\"", "🤲"), Sentence("Allah loves those who are kind.", "💗"),
    )

    // ---- Stages 5–10: speaking, in the life of a Muslim family ------------------------------------------
    private fun ph(text: String, meaning: String, emoji: String) = Phrase(text, meaning, emoji)
    private fun talk(vararg lines: Pair<String, String>) = lines.map { Line(it.first, it.second) }

    val islamicGreetings = listOf(
        ph("Assalamu alaikum", "Peace be upon you. We say it when we meet someone.", "👋"),
        ph("Wa alaikum assalam", "And upon you be peace. This is the reply to salam.", "😊"),
        ph("Assalamu alaikum wa rahmatullahi wa barakatuh", "Peace, mercy and blessings of Allah be upon you. The full salam brings more reward.", "🌟"),
        ph("Good morning!", "We say it in the morning, after the salam.", "🌅"),
        ph("Good night. Sleep well.", "We say it at bedtime, then read our dua.", "🌙"),
        ph("Fi amanillah", "In Allah's care. We say it when we say goodbye.", "🤲"),
    )
    val dailyWords = listOf(
        ph("Bismillah", "In the name of Allah. We say it before we start: eating, reading, getting dressed.", "🍽️"),
        ph("Alhamdulillah", "All praise is for Allah. We say it when we are happy, after eating, and after we sneeze.", "💚"),
        ph("InshaAllah", "If Allah wills. We say it when we talk about the future.", "🗓️"),
        ph("MashaAllah", "Allah has willed it. We say it when we see something good.", "🌸"),
        ph("SubhanAllah", "Glory be to Allah. We say it when we see something amazing.", "🌄"),
        ph("JazakAllahu khairan", "May Allah reward you with good. A beautiful way to say thank you.", "🎁"),
        ph("Astaghfirullah", "I ask Allah to forgive me. We say it when we make a mistake.", "🙏"),
        ph("Ameen", "O Allah, please accept. We say it after a dua.", "☝️"),
    )
    val politeWords = listOf(
        ph("Please", "We say it when we ask for something.", "🙂"),
        ph("Thank you", "We say it when someone helps us or gives us something.", "💐"),
        ph("You're welcome", "The reply when someone says thank you.", "🤗"),
        ph("Sorry", "We say it when we hurt someone or make a mistake.", "😔"),
        ph("Excuse me", "We say it to ask someone politely, or to pass by.", "🙋"),
        ph("May I come in?", "We ask before we enter a room or a house.", "🚪"),
    )
    val meetingFriend = talk(
        "Ali" to "Assalamu alaikum, Yusuf!", "Yusuf" to "Wa alaikum assalam, Ali!",
        "Ali" to "How are you?", "Yusuf" to "Alhamdulillah, I am fine. And you?",
        "Ali" to "Alhamdulillah, I am fine too.", "Yusuf" to "See you at the masjid, InshaAllah.",
        "Ali" to "InshaAllah. Fi amanillah!",
    )
    val sneezing = talk(
        "Maryam" to "Atchoo! Alhamdulillah!", "Aisha" to "Yarhamukillah! May Allah have mercy on you.",
        "Maryam" to "Yahdikumullah wa yuslihu balakum. May Allah guide you and make your life good.",
        "Aisha" to "Ameen!",
    )

    val familyWords = listOf(
        ph("father", "Abu, Baba, Dad", "👨"), ph("mother", "Ummi, Mama, Mum", "👩"), ph("brother", "a boy in my family", "👦"),
        ph("sister", "a girl in my family", "👧"), ph("grandfather", "my father's or my mother's father", "👴"),
        ph("grandmother", "my father's or my mother's mother", "👵"), ph("uncle", "my father's or mother's brother", "🧔"),
        ph("aunt", "my father's or mother's sister", "🧕"), ph("baby", "a very young child", "👶"),
        ph("family", "all of us together", "👨‍👩‍👧‍👦"),
    )
    val familySentences = listOf(
        Sentence("This is my father. He is kind.", "👨", "father"), Sentence("This is my mother. She cooks for us.", "👩", "mother"),
        Sentence("I have one brother and two sisters.", "👦", "brother"), Sentence("My grandfather reads the Quran every morning.", "👴", "grandfather"),
        Sentence("My baby sister is sleeping.", "👶", "baby"), Sentence("I love my family for the sake of Allah.", "💗", "family"),
    )
    val homeWords = listOf(
        ph("kitchen", "where we cook food", "🍳"), ph("bedroom", "where we sleep", "🛏️"),
        ph("bathroom", "we go in with the left foot and say the dua", "🚿"), ph("living room", "where the family sits together", "🛋️"),
        ph("prayer corner", "a clean place where we pray at home", "🙇"), ph("garden", "where flowers and trees grow", "🌳"),
    )
    val helpingAtHome = talk(
        "Mother" to "Ahmad, can you help me, please?", "Ahmad" to "Yes, Ummi. What can I do?",
        "Mother" to "Please set the table for dinner.", "Ahmad" to "Okay, InshaAllah.",
        "Mother" to "JazakAllahu khairan, my son.", "Ahmad" to "Wa iyyaki, Ummi.",
    )

    val fivePrayers = listOf(
        ph("Fajr", "the prayer at dawn, before the sun rises", "🌅"), ph("Zuhr", "the prayer after midday", "☀️"),
        ph("Asr", "the prayer in the afternoon", "🌤️"), ph("Maghrib", "the prayer just after sunset", "🌇"),
        ph("Isha", "the prayer at night", "🌙"),
    )
    val morning = listOf(
        Sentence("I wake up and say the dua.", "⏰", "dua"), Sentence("I brush my teeth and make wudu.", "💧", "wudu"),
        Sentence("I pray Fajr with my father.", "🌅", "Fajr"), Sentence("I eat breakfast with my right hand.", "🥣", "breakfast"),
        Sentence("I say Bismillah before I eat.", "🍽️", "Bismillah"), Sentence("Then I go to school.", "🏫", "school"),
    )
    val evening = listOf(
        Sentence("I come home and pray Asr.", "🏠", "Asr"), Sentence("I read the Quran after Maghrib.", "📗", "Quran"),
        Sentence("We eat dinner together.", "🍲", "dinner"), Sentence("I do my homework.", "📝", "homework"),
        Sentence("I pray Isha and go to bed.", "🌙", "Isha"), Sentence("I say the dua before I sleep.", "😴", "sleep"),
    )
    val weekDays = listOf(
        ph("Monday", "The Prophet ﷺ liked to fast on Mondays.", "1️⃣"), ph("Tuesday", "the day after Monday", "2️⃣"),
        ph("Wednesday", "the middle of the week", "3️⃣"), ph("Thursday", "The Prophet ﷺ liked to fast on Thursdays too.", "4️⃣"),
        ph("Friday", "Jumu'ah! We go to the masjid for the Friday prayer.", "🕌"), ph("Saturday", "a day to visit family", "6️⃣"),
        ph("Sunday", "a day to help at home", "7️⃣"),
    )
    val whatTime = talk(
        "Bilal" to "Excuse me, what time is it?", "Hamza" to "It is one o'clock.",
        "Bilal" to "Is it time for Zuhr?", "Hamza" to "Yes, the adhan is soon.",
        "Bilal" to "Let's make wudu and go to the masjid.", "Hamza" to "Good idea!",
    )

    val masjidPhrases = listOf(
        ph("Let's go to the masjid.", "We invite a friend to pray with us.", "🕌"),
        ph("Take off your shoes.", "We keep the masjid clean.", "👟"),
        ph("Enter with your right foot.", "This is the sunnah way to enter the masjid.", "🦶"),
        ph("Please be quiet.", "People are praying and reading the Quran.", "🤫"),
        ph("Pray two rak'ahs.", "We greet the masjid with two rak'ahs before we sit.", "🙇"),
        ph("Stand in a straight line.", "We stand shoulder to shoulder for the prayer.", "📏"),
    )
    val questionWords = listOf(
        ph("What?", "What is your name?", "❓"), ph("Where?", "Where is the masjid?", "📍"),
        ph("When?", "When is the Asr prayer?", "⏰"), ph("Who?", "Who is the imam?", "🧔"),
        ph("Why?", "Why do we fast in Ramadan?", "🤔"), ph("How?", "How do you make wudu?", "💧"),
    )
    val atTheMasjid = talk(
        "Imam" to "Assalamu alaikum. Welcome to the masjid.", "Zaid" to "Wa alaikum assalam. Where can I make wudu?",
        "Imam" to "The wudu area is on the left.", "Zaid" to "JazakAllahu khairan. When is the Asr prayer?",
        "Imam" to "At four o'clock, InshaAllah.", "Zaid" to "Thank you. I will be here.",
    )
    val classroom = talk(
        "Teacher" to "Good morning, class!", "Sara" to "Assalamu alaikum, Teacher. Good morning!",
        "Sara" to "May I ask a question, please?", "Teacher" to "Yes, of course.",
        "Sara" to "How do you spell 'masjid'?", "Teacher" to "M, A, S, J, I, D.",
        "Sara" to "Thank you, Teacher.",
    )

    val ramadanWords = listOf(
        ph("Ramadan", "the month of fasting", "🌙"), ph("suhoor", "the meal before dawn", "🥣"),
        ph("iftar", "the meal when we break the fast at sunset", "🍉"), ph("fasting", "no food or drink from dawn to sunset", "🚫"),
        ph("Taraweeh", "the night prayers of Ramadan", "🕌"), ph("Laylat al-Qadr", "the Night of Power, better than a thousand months", "✨"),
        ph("Zakat al-Fitr", "the charity we give before the Eid prayer", "💰"), ph("Eid Mubarak", "Blessed Eid! The Eid greeting.", "🎉"),
    )
    val yesterday = listOf(
        Sentence("Yesterday I woke up for suhoor.", "🥣", "suhoor"), Sentence("I fasted all day.", "☀️", "fasted"),
        Sentence("At sunset we broke our fast with dates.", "🌴", "dates"), Sentence("We prayed Maghrib together.", "🌇", "Maghrib"),
        Sentence("At night I went to Taraweeh with my father.", "🕌", "Taraweeh"), Sentence("I read one juz of the Quran.", "📗", "juz"),
    )
    val pastTense = listOf(
        ph("I pray → I prayed", "Yesterday I prayed all five prayers.", "🙇"), ph("I go → I went", "I went to the masjid.", "🚶"),
        ph("I eat → I ate", "I ate dates at iftar.", "🌴"), ph("I give → I gave", "I gave charity to a poor man.", "💝"),
        ph("I see → I saw", "I saw the new moon.", "🌙"), ph("I say → I said", "I said Bismillah.", "💬"),
        ph("I help → I helped", "I helped my mother.", "🤝"), ph("I read → I read", "I read Surah Al-Fatihah.", "📖"),
    )
    val eidMorning = talk(
        "Hamza" to "Eid Mubarak, Ali!", "Ali" to "Eid Mubarak! Taqabbal Allahu minna wa minkum.",
        "Hamza" to "Ameen. Did you go to the Eid prayer?", "Ali" to "Yes, I went with my family.",
        "Hamza" to "What did you do after the prayer?", "Ali" to "We visited my grandparents, and I gave a gift to my cousin.",
        "Hamza" to "MashaAllah! That is lovely.",
    )

    val newFriend = talk(
        "Musa" to "Assalamu alaikum! My name is Musa. What is your name?", "Idris" to "Wa alaikum assalam. My name is Idris.",
        "Musa" to "Nice to meet you, Idris. How old are you?", "Idris" to "I am eight years old. And you?",
        "Musa" to "I am nine. Which surah are you learning now?", "Idris" to "I am learning Surah Al-Mulk.",
        "Musa" to "MashaAllah! May Allah make it easy for you.", "Idris" to "Ameen. Would you like to play football after Asr?",
        "Musa" to "Yes, InshaAllah!",
    )
    val grandparents = talk(
        "Fatima" to "Assalamu alaikum, Grandma! May we come in?", "Grandma" to "Wa alaikum assalam! Come in, my dear.",
        "Fatima" to "How are you feeling today?", "Grandma" to "Alhamdulillah, much better.",
        "Fatima" to "We brought you some dates and fruit.", "Grandma" to "JazakAllahu khairan. May Allah bless you.",
        "Fatima" to "Wa iyyaki, Grandma. Can I make you some tea?", "Grandma" to "Yes please, that would be lovely.",
    )
    val atTheShop = talk(
        "Ahmad" to "Assalamu alaikum. Is this chicken halal?", "Shopkeeper" to "Wa alaikum assalam. Yes, it is halal.",
        "Ahmad" to "How much is it?", "Shopkeeper" to "It is five dirhams.",
        "Ahmad" to "I would like one, please.", "Shopkeeper" to "Here you are.",
        "Ahmad" to "Thank you. JazakAllahu khairan.",
    )
    val yunusStory = listOf(
        Sentence("Prophet Yunus left his town and got on a ship.", "🚢", "ship"),
        Sentence("A big storm came, and the sea was very rough.", "🌊", "storm"),
        Sentence("Yunus went into the sea, and a big whale swallowed him.", "🐋", "whale"),
        Sentence("Inside the whale, it was very dark.", "🌑", "dark"),
        Sentence("Yunus made dua: La ilaha illa anta, subhanaka, inni kuntu minaz-zalimin.", "🤲", "dua"),
        Sentence("Allah heard his dua and saved him from the whale.", "💚", "saved"),
    )
    val feelings = listOf(
        Sentence("I feel happy because I helped my friend.", "😊", "happy"),
        Sentence("I feel sad when someone is hurt, so I make dua for them.", "😢", "sad"),
        Sentence("When I am angry, I sit down and say A'udhu billah.", "😠", "angry"),
        Sentence("I feel thankful, so I say Alhamdulillah.", "🙏", "thankful"),
        Sentence("When I feel scared at night, I read Ayat al-Kursi.", "😨", "scared"),
        Sentence("I feel proud, but I remember that everything is from Allah.", "🏅", "proud"),
    )

    /** Everything a speaking stage's practice can ask about. */
    fun speakingPool(stage: Int): Triple<List<Phrase>, List<List<Line>>, List<Sentence>> {
        val st = stage(stage)
        return Triple(st.lessons.flatMap { it.phrases }, st.lessons.map { it.dialogue }.filter { it.isNotEmpty() },
            st.lessons.flatMap { it.sentences })
    }

    val stages: List<EnStage> = listOf(
        EnStage(1, "Sounds", "Stage 1: Letters and their sounds", "A for Allah, B for Bismillah", "a b c",
            "🔤", Color(0xFFE3F2FD), Color(0xFF1565C0), listOf(
                EnLesson("e1.1", "The alphabet", "A for Allah, B for Bismillah, D for Dua…", LessonKind.ALPHABET,
                    "Tap a letter to hear it and its Islamic keyword. Say the sound, not just the name: 'a' as in Allah.",
                    sounds = alphabet),
                EnLesson("e1.2", "First sounds", "s a t p i n — then m d g o c k", LessonKind.SOUNDS,
                    "Learn the sounds in small groups. After the first six you can already read real words!",
                    sounds = sounds("s a t p i n") + sounds("m d g o c k")),
                EnLesson("e1.3", "More sounds", "ck e u r · h b f l · j v w x y z qu", LessonKind.SOUNDS,
                    "Tap each picture and copy the sound. Watch out: ck is one sound, and in English q always brings u.",
                    sounds = sounds("ck e u r") + sounds("h b f l") + sounds("j v w x y z qu")),
            )),
        EnStage(2, "Blending", "Stage 2: Reading short words", "H + a + jj = Hajj", "H·a·jj",
            "🧩", Color(0xFFFFF4D6), Color(0xFFE0A800), listOf(
                EnLesson("e2.1", "Short a", "Adam, Hajj, hands, lamb…", LessonKind.WORDS,
                    "Tap each letter, then tap the word. Push the sounds together faster and faster: H… a… jj… Hajj!", words = shortA),
                EnLesson("e2.2", "Short e", "Mecca, help, ten, Medina…", LessonKind.WORDS, "e as in egg. Blend each word.", words = shortE),
                EnLesson("e2.3", "Short i", "Islam, Ismail, Ibrahim, gift…", LessonKind.WORDS,
                    "i as in Islam. Ibrahim and Ismail, peace be upon them, built the Kaaba. Blend each word.", words = shortI),
                EnLesson("e2.4", "Short o", "box, Mom, honest, socks…", LessonKind.WORDS, "o as in olive. Blend each word.", words = shortO),
                EnLesson("e2.5", "Short u", "Ummah, Jummah, sun, cup…", LessonKind.WORDS,
                    "u as in Ummah. Jummah is our special Friday prayer.", words = shortU),
            )),
        EnStage(3, "Letter teams", "Stage 3: Letter teams and long vowels", "ship, charity, pray, Deen…", "sh  ee",
            "👯", Color(0xFFF3E5F5), Color(0xFF6A1B9A), listOf(
                EnLesson("e3.1", "sh · ch · th", "ship, charity, thanks", LessonKind.SOUNDS,
                    "Some letters team up to make one new sound. Read the pair as one tile.",
                    sounds = digraphsA, words = digraphWordsA),
                EnLesson("e3.2", "ck · ng · ll · ss", "black stone, fasting, Allah, bless", LessonKind.SOUNDS,
                    "These teams often come at the end of a word.", sounds = digraphsB, words = digraphWordsB),
                EnLesson("e3.3", "wh · ph · qu", "white, Prophet, quiet", LessonKind.SOUNDS,
                    "ph sounds like f, and qu sounds like kw.", sounds = digraphsC, words = digraphWordsC),
                EnLesson("e3.4", "Magic e", "hop → hope, cub → cube", LessonKind.PAIRS,
                    "A silent e at the end jumps over one letter and makes the vowel say its name. The Kaaba is a cube!", pairs = magicE),
                EnLesson("e3.5", "Vowel teams", "pray, Deen, peace, night, moon", LessonKind.SOUNDS,
                    "Two (or three) vowels together often say the name of the first one.", sounds = vowelTeams, words = teamWords),
            )),
        EnStage(4, "First reading", "Stage 4: Sight words and first sentences", "the, said… Allah, Quran", "the  said",
            "📚", Color(0xFFFFF8E1), Color(0xFFFF8F00), listOf(
                EnLesson("e4.1", "Tricky words 1", tricky1.joinToString(", "), LessonKind.TRICKY,
                    "Tricky words don't follow the sound rules. Look, say, and remember them by sight.", tricky = tricky1),
                EnLesson("e4.2", "Tricky words 2", tricky2.joinToString(", "), LessonKind.TRICKY,
                    "Tap each word to hear it, then say it yourself.", tricky = tricky2),
                EnLesson("e4.3", "Tricky words 3", tricky3.joinToString(", "), LessonKind.TRICKY,
                    "Tap each word to hear it, then say it yourself.", tricky = tricky3),
                EnLesson("e4.4", "Islamic words", islamicWords.joinToString(", "), LessonKind.TRICKY,
                    "These words come from Arabic. Learn to read them by sight — you will meet them in every Islamic book.",
                    tricky = islamicWords),
                EnLesson("e4.5", "Flash cards", "15 cards — read the word, then check", LessonKind.CARDS,
                    "Read the word out loud, then tap to hear if you were right.", tricky = allTricky),
                EnLesson("e4.6", "My deen", "I read the Quran every day.", LessonKind.SENTENCES,
                    "Point to each word as you read. Tap a word to hear it, or the speaker to hear the sentence.", sentences = sentences1),
                EnLesson("e4.7", "Good manners", "I say Bismillah before I eat.", LessonKind.SENTENCES,
                    "Read about good manners from our deen.", sentences = sentences2),
                EnLesson("e4.8", "Story: Ali and the thirsty cat", "Six sentences, then talk about it", LessonKind.STORY,
                    "Read the story. The Prophet ﷺ told us there is reward in being kind to every living thing. Then tell someone: what did Ali do?",
                    sentences = story),
            )),
        EnStage(5, "Greetings", "Stage 5: Salam and good manners", "Assalamu alaikum! How are you?", "👋",
            "🤝", Color(0xFFE8F5E9), Color(0xFF2E7D32), listOf(
                EnLesson("e5.1", "Islamic greetings", "Assalamu alaikum, wa alaikum assalam…", LessonKind.PHRASES,
                    "A Muslim greets with salam. Tap each greeting, listen, and say it out loud.", phrases = islamicGreetings),
                EnLesson("e5.2", "Words we say every day", "Bismillah, InshaAllah, MashaAllah…", LessonKind.PHRASES,
                    "These words keep Allah in our talk all day. Learn what each one means and when we say it.", phrases = dailyWords),
                EnLesson("e5.3", "Polite English", "please, thank you, sorry…", LessonKind.PHRASES,
                    "Good manners are part of our deen. Use these words every day.", phrases = politeWords),
                EnLesson("e5.4", "Talk: meeting a friend", "Ali meets Yusuf", LessonKind.DIALOGUE,
                    "Listen to the talk. Then tap 'Your turn' and say Yusuf's words yourself.", dialogue = meetingFriend),
                EnLesson("e5.5", "Talk: when someone sneezes", "Alhamdulillah — Yarhamukillah", LessonKind.DIALOGUE,
                    "The Prophet ﷺ taught us what to say when we sneeze, and how to reply.", dialogue = sneezing),
            )),
        EnStage(6, "Family", "Stage 6: My family and my home", "This is my father. He is kind.", "👨‍👩‍👧",
            "🏠", Color(0xFFFFEBEE), Color(0xFFC62828), listOf(
                EnLesson("e6.1", "My family", "father, mother, brother, sister…", LessonKind.PHRASES,
                    "Tap each word and say it. Then point to someone in your family and say who they are.", phrases = familyWords),
                EnLesson("e6.2", "This is my family", "This is my father. He is kind.", LessonKind.SENTENCES,
                    "Read each sentence. Then talk about your own family: This is my…", sentences = familySentences),
                EnLesson("e6.3", "My home", "kitchen, bedroom, prayer corner…", LessonKind.PHRASES,
                    "Learn the rooms of a home.", phrases = homeWords),
                EnLesson("e6.4", "Talk: helping at home", "Ahmad helps his mother", LessonKind.DIALOGUE,
                    "Helping our parents is a good deed. Listen, then take Ahmad's part.", dialogue = helpingAtHome),
            )),
        EnStage(7, "My day", "Stage 7: My day as a Muslim", "I pray Fajr with my father.", "⏰",
            "⏰", Color(0xFFE0F7FA), Color(0xFF00838F), listOf(
                EnLesson("e7.1", "The five prayers", "Fajr, Zuhr, Asr, Maghrib, Isha", LessonKind.PHRASES,
                    "Our day is built around five prayers. Learn their names and their times.", phrases = fivePrayers),
                EnLesson("e7.2", "My morning", "I wake up and say the dua.", LessonKind.SENTENCES,
                    "Read about a Muslim morning. Then tell someone about your morning.", sentences = morning),
                EnLesson("e7.3", "My evening", "I read the Quran after Maghrib.", LessonKind.SENTENCES,
                    "Read about a Muslim evening.", sentences = evening),
                EnLesson("e7.4", "Days of the week", "Monday… Friday is Jumu'ah", LessonKind.PHRASES,
                    "Learn the seven days. Which day do we go to the masjid for Jumu'ah?", phrases = weekDays),
                EnLesson("e7.5", "Talk: what time is it?", "Is it time for Zuhr?", LessonKind.DIALOGUE,
                    "Listen, then take Hamza's part.", dialogue = whatTime),
            )),
        EnStage(8, "Masjid & school", "Stage 8: At the masjid and at school", "Where can I make wudu?", "🕌",
            "🕌", Color(0xFFEDE7F6), Color(0xFF4527A0), listOf(
                EnLesson("e8.1", "At the masjid", "Take off your shoes. Please be quiet.", LessonKind.PHRASES,
                    "The manners of the masjid, in English.", phrases = masjidPhrases),
                EnLesson("e8.2", "Question words", "What? Where? When? Who? Why? How?", LessonKind.PHRASES,
                    "Questions help us learn. Each word comes with a question you can ask.", phrases = questionWords),
                EnLesson("e8.3", "Talk: at the masjid door", "Zaid meets the imam", LessonKind.DIALOGUE,
                    "Listen, then take Zaid's part.", dialogue = atTheMasjid),
                EnLesson("e8.4", "Talk: in the classroom", "May I ask a question, please?", LessonKind.DIALOGUE,
                    "Listen, then take Sara's part.", dialogue = classroom),
            )),
        EnStage(9, "Ramadan & Eid", "Stage 9: Ramadan and Eid", "Yesterday I fasted all day.", "🌙",
            "🌙", Color(0xFFFFF3E0), Color(0xFFE65100), listOf(
                EnLesson("e9.1", "Ramadan words", "suhoor, iftar, Taraweeh…", LessonKind.PHRASES,
                    "Learn the words of the blessed month.", phrases = ramadanWords),
                EnLesson("e9.2", "Now and yesterday", "I pray → I prayed, I go → I went", LessonKind.PHRASES,
                    "To talk about yesterday, the action word changes. Many add -ed; some change completely.", phrases = pastTense),
                EnLesson("e9.3", "Yesterday in Ramadan", "I fasted all day.", LessonKind.SENTENCES,
                    "Read about a day in Ramadan. Every action happened yesterday.", sentences = yesterday),
                EnLesson("e9.4", "Talk: Eid morning", "Eid Mubarak! Taqabbal Allahu minna wa minkum", LessonKind.DIALOGUE,
                    "Listen, then take Ali's part.", dialogue = eidMorning),
            )),
        EnStage(10, "Let's talk", "Stage 10: Let's talk", "Nice to meet you! Which surah are you learning?", "💬",
            "💬", Color(0xFFE8EAF6), Color(0xFF283593), listOf(
                EnLesson("e10.1", "Talk: a new friend", "My name is Musa. What is your name?", LessonKind.DIALOGUE,
                    "A longer talk. Listen, then take Idris's part — and then talk about yourself.", dialogue = newFriend),
                EnLesson("e10.2", "Talk: visiting Grandma", "May we come in?", LessonKind.DIALOGUE,
                    "Visiting family is a good deed. Listen, then take Fatima's part.", dialogue = grandparents),
                EnLesson("e10.3", "Talk: at the shop", "Is this chicken halal?", LessonKind.DIALOGUE,
                    "Listen, then take Ahmad's part.", dialogue = atTheShop),
                EnLesson("e10.4", "Story: Prophet Yunus", "the ship, the whale and the dua", LessonKind.STORY,
                    "Read the story of Prophet Yunus, peace be upon him (Surah Al-Anbiya 21:87–88). Then tell it in your own words.",
                    sentences = yunusStory),
                EnLesson("e10.5", "How I feel", "I feel happy because…", LessonKind.SENTENCES,
                    "Talk about your feelings, and what a Muslim does with each one.", sentences = feelings),
            )),
    )

    fun stage(n: Int) = stages.first { it.number == n }

    /**
     * How the phone's English voice should say the Arabic words, written the way an English
     * reader would say them (the voice otherwise reads "Dua" as "dew-a" and "Eid" as "eyed").
     * Single letters are never changed, so a tile still says just its letter.
     */
    val pronunciation = mapOf(
        "Dua" to "doo-ah", "Eid" to "eed", "Fajr" to "fudger", "Jannah" to "jannah", "Kaaba" to "kaa-bah",
        "Masjid" to "mus-jid", "Namaz" to "na-maaz", "Quran" to "Kur-aan", "Salah" to "sa-laah", "Tasbih" to "tas-beeh",
        "Ummah" to "oom-mah", "Jummah" to "joom-mah", "Wudu" to "woo-doo", "Yusuf" to "yoo-suf", "Zakat" to "za-kaat",
        "Ismail" to "is-ma-eel", "Ibrahim" to "ib-ra-heem", "Hajj" to "haj", "Bismillah" to "bis-mil-lah",
        "Allah" to "al-lah", "Ramadan" to "ra-ma-daan", "Sunnah" to "soon-nah", "Hadith" to "ha-deeth",
        "Muhammad" to "mu-ham-mad", "Bilal" to "bi-laal", "halal" to "ha-laal", "Medina" to "ma-dee-nah",
        "Mecca" to "mek-kah", "Alhamdulillah" to "al-ham-du-lil-lah", "Assalamu" to "as-sa-laa-mu", "alaikum" to "a-lai-kum",
        "assalam" to "as-sa-laam", "rahmatullahi" to "rah-ma-tul-laa-hi", "barakatuh" to "ba-ra-kaa-tuh", "amanillah" to "a-maa-nil-lah",
        "InshaAllah" to "in-shaa-al-lah", "MashaAllah" to "maa-shaa-al-lah", "SubhanAllah" to "sub-haa-nal-lah",
        "JazakAllahu" to "ja-zaa-kal-laa-hu", "khairan" to "khai-ran", "Astaghfirullah" to "as-tagh-fi-rul-lah", "Ameen" to "aa-meen",
        "Yarhamukillah" to "yar-ha-mu-kil-lah", "Yahdikumullah" to "yah-dee-ku-mul-lah", "yuslihu" to "yus-li-hu", "balakum" to "baa-la-kum",
        "Ummi" to "oom-mee", "iyyaki" to "iy-yaa-ki", "Zuhr" to "zuhr", "Asr" to "asr", "Maghrib" to "mugh-rib", "Isha" to "ee-shaa",
        "suhoor" to "su-hoor", "iftar" to "if-taar", "Taraweeh" to "ta-raa-weeh", "Laylat" to "lai-lat", "Qadr" to "qadr",
        "Fitr" to "fitr", "Mubarak" to "mu-baa-rak", "Taqabbal" to "ta-qab-bal", "Allahu" to "al-laa-hu", "minna" to "min-naa",
        "minkum" to "min-kum", "juz" to "juz", "Mulk" to "mulk", "Yunus" to "yoo-nus", "subhanaka" to "sub-haa-na-ka",
        "illa" to "il-laa", "anta" to "an-ta", "inni" to "in-nee", "kuntu" to "kun-tu", "minaz" to "mi-naz", "zalimin" to "zaa-li-meen",
        "Kursi" to "kur-see", "Ayat" to "aa-yat", "udhu" to "oo-dhu", "billah" to "bil-lah", "rak'ahs" to "rak-ahs", "Jumu'ah" to "ju-mu-ah",
    )

    /** Text as the voice should say it: known Arabic words respelled, everything else as written. */
    fun pronounce(text: String): String {
        if (text.length <= 2) return text
        return Regex("[A-Za-z]+").replace(text) { m -> pronunciation.entries.firstOrNull { it.key.equals(m.value, true) }?.value ?: m.value }
    }

    /** Every word the course reads, for quizzes and checks. */
    val allWords: List<ReadWord> get() = (shortA + shortE + shortI + shortO + shortU + digraphWordsA + digraphWordsB +
        digraphWordsC + magicE.flatMap { listOf(it.first, it.second) } + teamWords).distinctBy { it.word }
}
