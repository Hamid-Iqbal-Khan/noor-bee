package com.example.english

import androidx.compose.ui.graphics.Color

/*
 * English reading course — the English-medium partner of the Quran course, built the same way:
 * stages of short lessons, each stage closed by a 10-question practice (8 correct completes it).
 * Order follows common synthetic-phonics teaching: letter sounds -> blending CVC words ->
 * digraphs -> long vowels -> tricky (sight) words -> reading sentences.
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

    // ---- Stage 1: letters and their sounds ---------------------------------------------------------
    val alphabet = listOf(
        Sound("a", "apple", "🍎"), Sound("b", "ball", "⚽"), Sound("c", "cat", "🐱"), Sound("d", "dog", "🐶"),
        Sound("e", "egg", "🥚"), Sound("f", "fish", "🐟"), Sound("g", "goat", "🐐"), Sound("h", "hat", "🎩"),
        Sound("i", "insect", "🐞"), Sound("j", "jam", "🍯"), Sound("k", "kite", "🪁"), Sound("l", "lion", "🦁"),
        Sound("m", "moon", "🌙"), Sound("n", "nose", "👃"), Sound("o", "octopus", "🐙"), Sound("p", "pen", "🖊️"),
        Sound("q", "queen", "👑", "q nearly always comes with u: qu"), Sound("r", "rabbit", "🐰"), Sound("s", "sun", "☀️"),
        Sound("t", "tent", "⛺"), Sound("u", "umbrella", "☂️"), Sound("v", "van", "🚐"), Sound("w", "web", "🕸️"),
        Sound("x", "box", "📦", "x says 'ks', usually at the end of a word"), Sound("y", "yo-yo", "🪀"), Sound("z", "zebra", "🦓"),
    )

    private fun sounds(letters: String) = letters.split(' ').map { g -> alphabet.firstOrNull { it.grapheme == g } ?: extra.getValue(g) }
    private val extra = mapOf(
        "ck" to Sound("ck", "duck", "🦆", "c and k together make one sound"),
        "qu" to Sound("qu", "queen", "👑", "q and u together say 'kw'"),
    )

    /** Teaching groups — the first six letters already make many words (sat, pin, tap, nap). */
    val groups = listOf(
        "s a t p i n" to "With just these six you can read: sat, pat, tin, pin, nap, tap",
        "m d g o c k" to "Now: mat, dog, cot, kid, map, dig",
        "ck e u r" to "Now: duck, sock, red, bed, sun, run",
        "h b f l" to "Now: hat, bat, fan, leg, lip, fun",
        "j v w x y z qu" to "Now: jam, van, web, box, yes, zip, quiz",
    )

    // ---- Stage 2: blending three-sound words --------------------------------------------------------
    val shortA = listOf(w("cat", "🐱"), w("bat", "🦇"), w("hat", "🎩"), w("map", "🗺️"), w("cap", "🧢"),
        w("bag", "👜"), w("van", "🚐"), w("jam", "🍯"), w("man", "👨"), w("pan", "🍳"))
    val shortE = listOf(w("bed", "🛏️"), w("hen", "🐔"), w("pen", "🖊️"), w("net", "🥅"), w("ten", "🔟"),
        w("web", "🕸️"), w("leg", "🦵"), w("jet", "✈️"), w("red", "🟥"), w("wet", "💧"))
    val shortI = listOf(w("pig", "🐷"), w("bin", "🗑️"), w("pin", "📌"), w("lip", "👄"), w("six", "6️⃣"),
        w("zip", "🤐"), w("kid", "🧒"), w("dig", "⛏️"), w("hit", "🏏"), w("tin", "🥫"))
    val shortO = listOf(w("dog", "🐶"), w("log", "🪵"), w("pot", "🍲"), w("box", "📦"), w("fox", "🦊"),
        w("mop", "🧹"), w("top", "🔝"), w("hop", "🐇"), w("dot", "🔵"), w("cot", "🛌"))
    val shortU = listOf(w("sun", "☀️"), w("bus", "🚌"), w("cup", "☕"), w("bug", "🐛"), w("hug", "🤗"),
        w("hut", "🛖"), w("nut", "🥜"), w("rug", "🧶"), w("gum", "🍬"), w("run", "🏃"))

    // ---- Stage 3: two letters, one sound ------------------------------------------------------------
    val digraphsA = listOf(
        Sound("sh", "ship", "🚢", "put a finger on your lips: shh!"),
        Sound("ch", "chick", "🐥"),
        Sound("th", "thumb", "👍", "tongue between the teeth — like the Arabic ث"),
    )
    val digraphWordsA = listOf(w("ship", "🚢", "sh", "i", "p"), w("fish", "🐟", "f", "i", "sh"), w("shop", "🏪", "sh", "o", "p"),
        w("chip", "🍟", "ch", "i", "p"), w("chin", "🧔", "ch", "i", "n"), w("lunch", "🥪", "l", "u", "n", "ch"),
        w("bath", "🛁", "b", "a", "th"), w("moth", "🦋", "m", "o", "th"), w("thin", "📏", "th", "i", "n"))
    val digraphsB = listOf(
        Sound("ck", "duck", "🦆", "c and k together make one sound"),
        Sound("ng", "ring", "💍", "the sound comes through the nose — like ghunnah!"),
        Sound("ll", "bell", "🔔", "two l's, one sound"),
        Sound("ss", "kiss", "💋", "two s's, one sound"),
    )
    val digraphWordsB = listOf(w("duck", "🦆", "d", "u", "ck"), w("sock", "🧦", "s", "o", "ck"), w("rock", "🪨", "r", "o", "ck"),
        w("ring", "💍", "r", "i", "ng"), w("king", "🤴", "k", "i", "ng"), w("sing", "🎤", "s", "i", "ng"),
        w("bell", "🔔", "b", "e", "ll"), w("doll", "🪆", "d", "o", "ll"), w("kiss", "💋", "k", "i", "ss"))
    val digraphsC = listOf(
        Sound("wh", "whale", "🐳"),
        Sound("ph", "dolphin", "🐬", "p and h together say 'f'"),
        Sound("qu", "queen", "👑", "q and u together say 'kw'"),
    )
    val digraphWordsC = listOf(w("when", "⏰", "wh", "e", "n"), w("which", "🤔", "wh", "i", "ch"), w("dolphin", "🐬", "d", "o", "l", "ph", "i", "n"),
        w("graph", "📈", "g", "r", "a", "ph"), w("quiz", "❓", "qu", "i", "z"), w("queen", "👑", "qu", "ee", "n"))

    // ---- Stage 4: long vowels ---------------------------------------------------------------------------
    val magicE = listOf(
        w("cap", "🧢") to w("cape", "🦸", "c", "a_e", "p"),
        w("tap", "🚰") to w("tape", "📼", "t", "a_e", "p"),
        w("kit", "🧰") to w("kite", "🪁", "k", "i_e", "t"),
        w("pin", "📌") to w("pine", "🌲", "p", "i_e", "n"),
        w("hop", "🐇") to w("hope", "🤞", "h", "o_e", "p"),
        w("not", "🚫") to w("note", "📝", "n", "o_e", "t"),
        w("cub", "🐻") to w("cube", "🧊", "c", "u_e", "b"),
        w("cut", "✂️") to w("cute", "🥰", "c", "u_e", "t"),
    )
    val vowelTeams = listOf(
        Sound("ai", "rain", "🌧️", "ai and ay both say the name of a"), Sound("ay", "day", "🌞"),
        Sound("ee", "tree", "🌳", "ee and ea both say the name of e"), Sound("ea", "sea", "🌊"),
        Sound("oa", "boat", "⛵", "oa and ow (as in snow) say the name of o"), Sound("ow", "snow", "❄️"),
        Sound("igh", "night", "🌃", "three letters, one sound: the name of i"), Sound("oo", "moon", "🌙"),
    )
    val teamWords = listOf(w("rain", "🌧️", "r", "ai", "n"), w("train", "🚆", "t", "r", "ai", "n"), w("play", "🛝", "p", "l", "ay"),
        w("tree", "🌳", "t", "r", "ee"), w("bee", "🐝", "b", "ee"), w("leaf", "🍃", "l", "ea", "f"),
        w("boat", "⛵", "b", "oa", "t"), w("goat", "🐐", "g", "oa", "t"), w("snow", "❄️", "s", "n", "ow"),
        w("light", "💡", "l", "igh", "t"), w("night", "🌃", "n", "igh", "t"), w("moon", "🌙", "m", "oo", "n"),
        w("food", "🍲", "f", "oo", "d"), w("zoo", "🦒", "z", "oo"))

    // ---- Stage 5: tricky words (they don't follow the sound rules — learn them by sight) --------------
    val tricky1 = listOf("the", "to", "I", "no", "go", "into", "he", "she", "we", "me", "be", "was")
    val tricky2 = listOf("you", "they", "all", "are", "my", "her", "said", "have", "like", "so", "do", "some")
    val tricky3 = listOf("come", "were", "there", "little", "one", "when", "out", "what", "here", "their", "people", "could")
    val allTricky = tricky1 + tricky2 + tricky3

    // ---- Stage 6: reading sentences -------------------------------------------------------------------
    val sentences1 = listOf(
        Sentence("The cat sat on the mat.", "🐱", "cat"), Sentence("I can see a red bus.", "🚌", "bus"),
        Sentence("The dog is in the box.", "🐶", "dog"), Sentence("A big fish is in the pond.", "🐟", "fish"),
        Sentence("The sun is hot.", "☀️", "sun"), Sentence("The rabbit can run and hop.", "🐇", "rabbit"),
    )
    val sentences2 = listOf(
        Sentence("I say Bismillah before I eat.", "🍽️", "eat"), Sentence("I say Assalamu alaikum to my friend.", "👋", "friend"),
        Sentence("I help my mum and dad at home.", "🏠", "home"), Sentence("We go to the masjid to pray.", "🕌", "masjid"),
        Sentence("I make dua with my hands.", "🤲", "hands"), Sentence("I share my toys with my sister.", "🧸", "toys"),
    )
    val story = listOf(
        Sentence("Sam has a red kite.", "🪁"), Sentence("The wind is strong.", "🌬️"),
        Sentence("The kite goes up and up.", "⬆️"), Sentence("Oh no! It is stuck in a tree.", "🌳"),
        Sentence("Dad helps Sam get it down.", "👨‍👦"), Sentence("Sam says, \"Thank you, Dad!\"", "😊"),
    )

    val stages: List<EnStage> = listOf(
        EnStage(1, "Sounds", "Stage 1: Letters and their sounds", "26 letters & first sounds", "a b c",
            "🔤", Color(0xFFE3F2FD), Color(0xFF1565C0), listOf(
                EnLesson("e1.1", "The alphabet", "26 letters, big and small, with a sound picture", LessonKind.ALPHABET,
                    "Tap a letter to hear it and its keyword. Say the sound, not just the name: 'a' as in apple.",
                    sounds = alphabet),
                EnLesson("e1.2", "First sounds", "s a t p i n — then m d g o c k", LessonKind.SOUNDS,
                    "Learn the sounds in small groups. After the first six you can already read real words!",
                    sounds = sounds("s a t p i n") + sounds("m d g o c k")),
                EnLesson("e1.3", "More sounds", "ck e u r · h b f l · j v w x y z qu", LessonKind.SOUNDS,
                    "Tap each picture and copy the sound. Watch out: ck is one sound, and q always brings u.",
                    sounds = sounds("ck e u r") + sounds("h b f l") + sounds("j v w x y z qu")),
            )),
        EnStage(2, "Blending", "Stage 2: Blending three sounds", "c + a + t = cat", "c·a·t",
            "🧩", Color(0xFFFFF4D6), Color(0xFFE0A800), listOf(
                EnLesson("e2.1", "Short a", "cat, map, bag, jam…", LessonKind.WORDS,
                    "Say each sound, then push them together faster and faster: c… a… t… cat!", words = shortA),
                EnLesson("e2.2", "Short e", "bed, hen, pen, web…", LessonKind.WORDS, "e as in egg. Blend each word.", words = shortE),
                EnLesson("e2.3", "Short i", "pig, pin, six, zip…", LessonKind.WORDS, "i as in insect. Blend each word.", words = shortI),
                EnLesson("e2.4", "Short o", "dog, box, fox, mop…", LessonKind.WORDS, "o as in octopus. Blend each word.", words = shortO),
                EnLesson("e2.5", "Short u", "sun, bus, cup, bug…", LessonKind.WORDS, "u as in umbrella. Blend each word.", words = shortU),
            )),
        EnStage(3, "Digraphs", "Stage 3: Two letters, one sound", "sh, ch, th, ck, ng…", "sh ch th",
            "👯", Color(0xFFF3E5F5), Color(0xFF6A1B9A), listOf(
                EnLesson("e3.1", "sh · ch · th", "ship, chip, bath", LessonKind.SOUNDS,
                    "Some letters team up to make one new sound. Read the pair as one tile.",
                    sounds = digraphsA, words = digraphWordsA),
                EnLesson("e3.2", "ck · ng · ll · ss", "duck, ring, bell, kiss", LessonKind.SOUNDS,
                    "These teams often come at the end of a word.", sounds = digraphsB, words = digraphWordsB),
                EnLesson("e3.3", "wh · ph · qu", "when, dolphin, quiz, queen", LessonKind.SOUNDS,
                    "ph sounds like f, and qu sounds like kw.", sounds = digraphsC, words = digraphWordsC),
            )),
        EnStage(4, "Long vowels", "Stage 4: Long vowel sounds", "magic e, ai, ee, oa, igh…", "a_e  ee",
            "🪄", Color(0xFFE0F2F1), Color(0xFF00695C), listOf(
                EnLesson("e4.1", "Magic e", "cap → cape, kit → kite", LessonKind.PAIRS,
                    "A silent e at the end jumps over one letter and makes the vowel say its name.", pairs = magicE),
                EnLesson("e4.2", "Vowel teams", "rain, tree, boat, night, moon", LessonKind.SOUNDS,
                    "Two (or three) vowels together often say the name of the first one.", sounds = vowelTeams, words = teamWords),
            )),
        EnStage(5, "Tricky words", "Stage 5: Tricky words", "the, said, was, you…", "the  said",
            "⭐", Color(0xFFFFF8E1), Color(0xFFFF8F00), listOf(
                EnLesson("e5.1", "Tricky words 1", tricky1.joinToString(", "), LessonKind.TRICKY,
                    "Tricky words don't follow the sound rules. Look, say, and remember them by sight.", tricky = tricky1),
                EnLesson("e5.2", "Tricky words 2", tricky2.joinToString(", "), LessonKind.TRICKY,
                    "Tap each word to hear it, then say it yourself.", tricky = tricky2),
                EnLesson("e5.3", "Tricky words 3", tricky3.joinToString(", "), LessonKind.TRICKY,
                    "Tap each word to hear it, then say it yourself.", tricky = tricky3),
                EnLesson("e5.4", "Flash cards", "15 cards — read the word, then check", LessonKind.CARDS,
                    "Read the word out loud, then tap to hear if you were right.", tricky = allTricky),
            )),
        EnStage(6, "Reading", "Stage 6: Reading sentences", "sentences and a little story", "📚",
            "📚", Color(0xFFE8F5E9), Color(0xFF2E7D32), listOf(
                EnLesson("e6.1", "Little sentences", "The cat sat on the mat.", LessonKind.SENTENCES,
                    "Point to each word as you read. Tap a word to hear it, or the speaker to hear the sentence.",
                    sentences = sentences1),
                EnLesson("e6.2", "Good manners", "I say Bismillah before I eat.", LessonKind.SENTENCES,
                    "Read about good manners from our deen.", sentences = sentences2),
                EnLesson("e6.3", "Story: Sam's kite", "Six sentences, then talk about it", LessonKind.STORY,
                    "Read the story. Then tell someone: what happened to the kite? Who helped?", sentences = story),
            )),
    )

    fun stage(n: Int) = stages.first { it.number == n }

    /** Every word the course reads, for quizzes and checks. */
    val allWords: List<ReadWord> get() = (shortA + shortE + shortI + shortO + shortU + digraphWordsA + digraphWordsB +
        digraphWordsC + magicE.flatMap { listOf(it.first, it.second) } + teamWords).distinctBy { it.word }
}
