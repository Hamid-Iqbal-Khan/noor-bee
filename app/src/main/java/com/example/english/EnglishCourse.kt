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

enum class LessonKind { ALPHABET, SOUNDS, WORDS, PAIRS, TRICKY, CARDS, SENTENCES, STORY }

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
        EnStage(2, "Blending", "Stage 2: Reading short Islamic words", "H + a + jj = Hajj", "H·a·jj",
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
        EnStage(3, "Digraphs", "Stage 3: Two letters, one sound", "ship, Charity, thanks…", "sh ch th",
            "👯", Color(0xFFF3E5F5), Color(0xFF6A1B9A), listOf(
                EnLesson("e3.1", "sh · ch · th", "ship, charity, thanks", LessonKind.SOUNDS,
                    "Some letters team up to make one new sound. Read the pair as one tile.",
                    sounds = digraphsA, words = digraphWordsA),
                EnLesson("e3.2", "ck · ng · ll · ss", "black stone, fasting, Allah, bless", LessonKind.SOUNDS,
                    "These teams often come at the end of a word.", sounds = digraphsB, words = digraphWordsB),
                EnLesson("e3.3", "wh · ph · qu", "white, Prophet, quiet", LessonKind.SOUNDS,
                    "ph sounds like f, and qu sounds like kw.", sounds = digraphsC, words = digraphWordsC),
            )),
        EnStage(4, "Long vowels", "Stage 4: Long vowel sounds", "pray, Deen, peace, night…", "a_e  ee",
            "🪄", Color(0xFFE0F2F1), Color(0xFF00695C), listOf(
                EnLesson("e4.1", "Magic e", "hop → hope, cub → cube", LessonKind.PAIRS,
                    "A silent e at the end jumps over one letter and makes the vowel say its name. The Kaaba is a cube!", pairs = magicE),
                EnLesson("e4.2", "Vowel teams", "pray, Deen, peace, night, moon", LessonKind.SOUNDS,
                    "Two (or three) vowels together often say the name of the first one.", sounds = vowelTeams, words = teamWords),
            )),
        EnStage(5, "Tricky words", "Stage 5: Tricky words", "the, said, was… Allah, Quran", "the  said",
            "⭐", Color(0xFFFFF8E1), Color(0xFFFF8F00), listOf(
                EnLesson("e5.1", "Tricky words 1", tricky1.joinToString(", "), LessonKind.TRICKY,
                    "Tricky words don't follow the sound rules. Look, say, and remember them by sight.", tricky = tricky1),
                EnLesson("e5.2", "Tricky words 2", tricky2.joinToString(", "), LessonKind.TRICKY,
                    "Tap each word to hear it, then say it yourself.", tricky = tricky2),
                EnLesson("e5.3", "Tricky words 3", tricky3.joinToString(", "), LessonKind.TRICKY,
                    "Tap each word to hear it, then say it yourself.", tricky = tricky3),
                EnLesson("e5.4", "Islamic words", islamicWords.joinToString(", "), LessonKind.TRICKY,
                    "These words come from Arabic. Learn to read them by sight — you will meet them in every Islamic book.",
                    tricky = islamicWords),
                EnLesson("e5.5", "Flash cards", "15 cards — read the word, then check", LessonKind.CARDS,
                    "Read the word out loud, then tap to hear if you were right.", tricky = allTricky),
            )),
        EnStage(6, "Reading", "Stage 6: Reading sentences", "sentences and a story about kindness", "📚",
            "📚", Color(0xFFE8F5E9), Color(0xFF2E7D32), listOf(
                EnLesson("e6.1", "My deen", "I read the Quran every day.", LessonKind.SENTENCES,
                    "Point to each word as you read. Tap a word to hear it, or the speaker to hear the sentence.",
                    sentences = sentences1),
                EnLesson("e6.2", "Good manners", "I say Bismillah before I eat.", LessonKind.SENTENCES,
                    "Read about good manners from our deen.", sentences = sentences2),
                EnLesson("e6.3", "Story: Ali and the thirsty cat", "Six sentences, then talk about it", LessonKind.STORY,
                    "Read the story. The Prophet ﷺ told us there is reward in being kind to every living thing. Then tell someone: what did Ali do?",
                    sentences = story),
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
