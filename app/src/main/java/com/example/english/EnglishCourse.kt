package com.example.english

import androidx.compose.ui.graphics.Color

/*
 * English course, from the alphabet to speaking. General English for any child.
 * Order of teaching follows the UK government programme "Letters and Sounds" (DfES 2007):
 * s a t p -> i n m d -> g o c k -> ck e u r -> h b f l -> j v w x -> y z qu, then blending,
 * letter teams and long vowels. Sight words follow Letters and Sounds and the Dolch list.
 * Words are played from real human recordings (Wikimedia Commons) where one exists; see
 * EnglishAudio and assets/audio/en/CREDITS.csv.
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

    // ---- Level 1: the 26 letters, each with a picture word that starts with its usual sound -------------
    val alphabet = listOf(
        Sound("a", "apple", "🍎"), Sound("b", "ball", "⚽"), Sound("c", "cat", "🐱"), Sound("d", "dog", "🐶"),
        Sound("e", "egg", "🥚"), Sound("f", "fish", "🐟"), Sound("g", "goat", "🐐"), Sound("h", "hat", "🎩"),
        Sound("i", "insect", "🐞"), Sound("j", "jam", "🍓"), Sound("k", "kite", "🪁"), Sound("l", "lion", "🦁"),
        Sound("m", "moon", "🌙"), Sound("n", "nest", "🪺"), Sound("o", "octopus", "🐙"), Sound("p", "pig", "🐷"),
        Sound("q", "queen", "👸", "q almost always comes with u. Together they say 'kw'."),
        Sound("r", "rabbit", "🐰"), Sound("s", "sun", "☀️"), Sound("t", "tent", "⛺"),
        Sound("u", "umbrella", "☂️"), Sound("v", "van", "🚐"), Sound("w", "web", "🕸️"),
        Sound("x", "box", "📦", "Few words start with x. It usually comes at the end and says 'ks', as in box."),
        Sound("y", "yo-yo", "🪀"), Sound("z", "zebra", "🦓"),
    )

    private fun sounds(letters: String) = letters.split(' ').map { g -> alphabet.firstOrNull { it.grapheme == g } ?: extra.getValue(g) }
    private val extra = mapOf(
        "ck" to Sound("ck", "duck", "🦆", "c and k together make one sound, at the end of a word"),
        "ff" to Sound("ff", "puff", "💨", "two f's, one sound"),
        "ll" to Sound("ll", "bell", "🔔", "two l's, one sound"),
        "ss" to Sound("ss", "kiss", "💋", "two s's, one sound"),
        "zz" to Sound("zz", "buzz", "🐝", "two z's, one sound"),
        "qu" to Sound("qu", "queen", "👸", "q and u together say 'kw'"),
    )

    /** Letters and Sounds teaching sets — each set makes new words with the letters before it. */
    val groups = listOf(
        "s a t p" to "With just these four you can read: sat, pat, tap, at",
        "i n m d" to "Now: pin, tin, man, map, sit, dad",
        "g o c k" to "Now: dog, cat, pot, kit, top, can",
        "ck e u r" to "Now: duck, red, sun, run, peck",
        "h b f l" to "Now: hat, bed, fan, leg, bell",
        "j v w x" to "Now: jam, van, web, box",
        "y z qu" to "Now: yes, zip, quiz, buzz",
    )

    private val set1 = listOf(w("sat", "🪑"), w("pat", "🤚"), w("tap", "🚰"))
    private val set2 = listOf(w("pin", "📌"), w("man", "👨"), w("map", "🗺️"), w("dad", "👨‍👧"))
    private val set3 = listOf(w("dog", "🐶"), w("cat", "🐱"), w("pot", "🍲"), w("top", "🔝"))
    private val set4 = listOf(w("duck", "🦆", "d", "u", "ck"), w("red", "🔴"), w("sun", "☀️"), w("run", "🏃"))
    private val set5 = listOf(w("hat", "🎩"), w("bed", "🛏️"), w("fan", "🪭"), w("bell", "🔔", "b", "e", "ll"))
    private val set6 = listOf(w("jam", "🍓"), w("van", "🚐"), w("web", "🕸️"), w("box", "📦"))
    private val set7 = listOf(w("yes", "👍"), w("zip", "🤐"), w("quiz", "❓", "qu", "i", "z"), w("buzz", "🐝", "b", "u", "zz"))

    // ---- Level 2: blending short words ---------------------------------------------------------------
    val shortA = listOf(w("cat", "🐱"), w("hat", "🎩"), w("bag", "👜"), w("map", "🗺️"), w("van", "🚐"), w("jam", "🍓"), w("fan", "🪭"))
    val shortE = listOf(w("bed", "🛏️"), w("pen", "🖊️"), w("hen", "🐔"), w("net", "🥅"), w("leg", "🦵"), w("ten", "🔟"), w("web", "🕸️"))
    val shortI = listOf(w("pig", "🐷"), w("bin", "🗑️"), w("lip", "👄"), w("six", "6️⃣"), w("zip", "🤐"), w("pin", "📌"), w("kid", "🧒"))
    val shortO = listOf(w("dog", "🐶"), w("box", "📦"), w("fox", "🦊"), w("pot", "🍲"), w("mop", "🧹"), w("log", "🪵"),
        w("sock", "🧦", "s", "o", "ck"))
    val shortU = listOf(w("sun", "☀️"), w("bus", "🚌"), w("cup", "☕"), w("bug", "🐛"), w("nut", "🥜"), w("hug", "🤗"),
        w("duck", "🦆", "d", "u", "ck"))
    val doubles = listOf(w("bell", "🔔", "b", "e", "ll"), w("doll", "🪆", "d", "o", "ll"), w("kiss", "💋", "k", "i", "ss"),
        w("egg", "🥚", "e", "gg"), w("hill", "⛰️", "h", "i", "ll"), w("buzz", "🐝", "b", "u", "zz"))
    val blendsStart = listOf(w("frog", "🐸"), w("crab", "🦀"), w("flag", "🚩"), w("drum", "🥁"), w("swim", "🏊"), w("stop", "🛑"), w("clap", "👏"))
    val blendsEnd = listOf(w("hand", "✋"), w("milk", "🥛"), w("tent", "⛺"), w("lamp", "🪔"), w("nest", "🪺"), w("jump", "🦘"), w("gift", "🎁"))
    val captions = listOf(
        Sentence("a cat on a mat", "🐱", "cat"), Sentence("a big red bus", "🚌", "bus"), Sentence("a dog in a box", "📦", "box"),
        Sentence("a hen in a pen", "🐔", "hen"), Sentence("a frog on a log", "🐸", "frog"), Sentence("the sun is hot", "☀️", "sun"),
    )

    /** The first vowel tile of a word (the sound the Level 2 practice asks about). */
    fun firstVowel(word: ReadWord): String = word.parts.first { it.lowercase() in setOf("a", "e", "i", "o", "u") }.lowercase()

    // ---- Level 3: letter teams and long vowels -------------------------------------------------------
    val digraphsA = listOf(
        Sound("sh", "ship", "🚢", "put a finger on your lips: shh!"),
        Sound("ch", "chick", "🐤"),
        Sound("th", "thumb", "👍", "put your tongue between your teeth and blow gently"),
    )
    val digraphWordsA = listOf(w("ship", "🚢", "sh", "i", "p"), w("fish", "🐟", "f", "i", "sh"), w("shop", "🏪", "sh", "o", "p"),
        w("chick", "🐤", "ch", "i", "ck"), w("lunch", "🍱", "l", "u", "n", "ch"), w("bath", "🛁", "b", "a", "th"),
        w("moth", "🦋", "m", "o", "th"))
    val digraphsB = listOf(
        Sound("ng", "ring", "💍", "the sound comes through your nose"),
        Sound("nk", "sink", "🚰", "n and k together: 'ngk'"),
        Sound("ck", "rock", "🪨", "c and k together make one sound"),
    )
    val digraphWordsB = listOf(w("ring", "💍", "r", "i", "ng"), w("king", "👑", "k", "i", "ng"), w("wing", "🪽", "w", "i", "ng"),
        w("bank", "🏦", "b", "a", "nk"), w("sink", "🚰", "s", "i", "nk"), w("skunk", "🦨", "s", "k", "u", "nk"),
        w("rock", "🪨", "r", "o", "ck"), w("clock", "⏰", "c", "l", "o", "ck"))
    val digraphsC = listOf(
        Sound("wh", "wheel", "🛞", "w and h together say 'w'"),
        Sound("ph", "phone", "📱", "p and h together say 'f'"),
        Sound("qu", "queen", "👸", "q and u together say 'kw'"),
    )
    val digraphWordsC = listOf(w("wheel", "🛞", "wh", "ee", "l"), w("whale", "🐋", "wh", "a_e", "l"),
        w("phone", "📱", "ph", "o_e", "n"), w("photo", "📷", "ph", "o", "t", "o"), w("dolphin", "🐬", "d", "o", "l", "ph", "i", "n"),
        w("queen", "👸", "qu", "ee", "n"), w("quick", "⚡", "qu", "i", "ck"))

    val magicE = listOf(
        w("cap", "🧢") to w("cape", "🦸", "c", "a_e", "p"),
        w("kit", "🧰") to w("kite", "🪁", "k", "i_e", "t"),
        w("hop", "🐇") to w("hope", "🤞", "h", "o_e", "p"),
        w("cub", "🐻") to w("cube", "🧊", "c", "u_e", "b"),
        w("pin", "📌") to w("pine", "🌲", "p", "i_e", "n"),
        w("tap", "🚰") to w("tape", "📼", "t", "a_e", "p"),
        w("not", "❌") to w("note", "📝", "n", "o_e", "t"),
        w("cut", "✂️") to w("cute", "🥰", "c", "u_e", "t"),
    )

    val vowelTeams1 = listOf(
        Sound("ai", "rain", "🌧️", "ai says the name of a"), Sound("ee", "tree", "🌳", "ee says the name of e"),
        Sound("igh", "night", "🌃", "three letters, one sound: the name of i"), Sound("oa", "boat", "⛵", "oa says the name of o"),
        Sound("oo", "moon", "🌙", "long oo, as in moon, and short oo, as in book"),
    )
    val teamWords1 = listOf(w("rain", "🌧️", "r", "ai", "n"), w("snail", "🐌", "s", "n", "ai", "l"), w("tree", "🌳", "t", "r", "ee"),
        w("bee", "🐝", "b", "ee"), w("night", "🌃", "n", "igh", "t"), w("light", "💡", "l", "igh", "t"),
        w("boat", "⛵", "b", "oa", "t"), w("goat", "🐐", "g", "oa", "t"), w("moon", "🌙", "m", "oo", "n"), w("book", "📖", "b", "oo", "k"))
    val vowelTeams2 = listOf(
        Sound("ar", "car", "🚗"), Sound("or", "fork", "🍴"), Sound("ur", "nurse", "🧑‍⚕️", "ur, ir and er often make the same sound"),
        Sound("ir", "bird", "🐦"), Sound("er", "ladder", "🪜"),
    )
    val teamWords2 = listOf(w("car", "🚗", "c", "ar"), w("star", "⭐", "s", "t", "ar"), w("fork", "🍴", "f", "or", "k"),
        w("corn", "🌽", "c", "or", "n"), w("nurse", "🧑‍⚕️", "n", "ur", "s", "e"), w("purse", "👛", "p", "ur", "s", "e"),
        w("bird", "🐦", "b", "ir", "d"), w("girl", "👧", "g", "ir", "l"), w("ladder", "🪜", "l", "a", "dd", "er"))
    val vowelTeams3 = listOf(
        Sound("ow", "cow", "🐄", "ow can also say the name of o, as in snow"), Sound("ou", "house", "🏠", "ou and ow often make the same sound"),
        Sound("oi", "coin", "🪙"), Sound("oy", "boy", "👦", "oi and oy make the same sound"),
    )
    val teamWords3 = listOf(w("cow", "🐄", "c", "ow"), w("owl", "🦉", "ow", "l"), w("house", "🏠", "h", "ou", "s", "e"),
        w("mouse", "🐭", "m", "ou", "s", "e"), w("coin", "🪙", "c", "oi", "n"), w("boy", "👦", "b", "oy"), w("toy", "🧸", "t", "oy"))
    val vowelTeams4 = listOf(
        Sound("ay", "day", "🌞", "ay says the name of a, at the end of a word"), Sound("ea", "leaf", "🍃", "ea often says the name of e"),
        Sound("ie", "pie", "🥧"), Sound("ue", "blue", "🔵", "ue and ew both say 'oo'"), Sound("ew", "new", "🆕"),
    )
    val teamWords4 = listOf(w("day", "🌞", "d", "ay"), w("play", "🛝", "p", "l", "ay"), w("tea", "🍵", "t", "ea"),
        w("leaf", "🍃", "l", "ea", "f"), w("pie", "🥧", "p", "ie"), w("tie", "👔", "t", "ie"), w("blue", "🔵", "b", "l", "ue"),
        w("glue", "🧴", "g", "l", "ue"), w("new", "🆕", "n", "ew"), w("screw", "🔩", "s", "c", "r", "ew"))
    val vowelTeams = vowelTeams1 + vowelTeams2 + vowelTeams3 + vowelTeams4
    val teamWords = teamWords1 + teamWords2 + teamWords3 + teamWords4

    // ---- Level 4: sight words (Letters and Sounds tricky words, then Dolch), sentences and stories ------------
    val tricky1 = listOf("the", "to", "I", "no", "go", "into", "he", "she", "we", "me", "be", "was")
    val tricky2 = listOf("you", "they", "all", "are", "my", "her", "said", "have", "like", "so", "do", "some")
    val tricky3 = listOf("come", "were", "there", "little", "one", "when", "out", "what", "here", "their", "people", "could")
    val tricky4 = listOf("look", "play", "saw", "who", "went", "want", "again", "because", "any", "many", "friend", "where")
    val colours = listOf("red", "blue", "green", "yellow", "orange", "pink", "purple", "brown", "black", "white")
    val numbers = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten")
    val allTricky = (tricky1 + tricky2 + tricky3 + tricky4).distinct()

    val sentences1 = listOf(
        Sentence("This is my mum.", "👩", "mum"), Sentence("My dad is in the garden.", "👨‍🌾", "dad"),
        Sentence("The cat is on the bed.", "🐈", "cat"), Sentence("I can see a red ball.", "⚽", "ball"),
        Sentence("We eat lunch at one.", "🍱", "lunch"), Sentence("My bag is green.", "🎒", "bag"),
    )
    val sentences2 = listOf(
        Sentence("I can run fast.", "🏃", "run"), Sentence("The duck is in the pond.", "🦆", "duck"),
        Sentence("We play on the swing.", "🛝", "swing"), Sentence("A bird sits in the tree.", "🐦", "bird"),
        Sentence("The kite is up high.", "🪁", "kite"), Sentence("Look at the big dog!", "🐶", "dog"),
    )
    /** A retelling of the folk tale "The Little Red Hen" (public domain). */
    val story = listOf(
        Sentence("A little red hen found some wheat.", "🐔"), Sentence("\"Who will help me plant it?\" she said.", "🌱"),
        Sentence("\"Not I,\" said the cat. \"Not I,\" said the dog.", "🐱"), Sentence("So she planted it herself.", "🌾"),
        Sentence("She made the wheat into bread.", "🍞"), Sentence("\"Who will help me eat it?\" \"I will!\" said all of them.", "🐶"),
        Sentence("\"No,\" said the hen. \"I will eat it myself!\"", "😋"),
    )
    /** A retelling of the folk tale "The Enormous Turnip" (public domain). */
    val story2 = listOf(
        Sentence("Grandpa planted a turnip seed.", "👴"), Sentence("The turnip grew and grew. It was very big!", "🥔"),
        Sentence("Grandpa pulled, but it did not come out.", "💪"), Sentence("Grandma came to help. They pulled and pulled.", "👵"),
        Sentence("A girl came, then a dog, then a cat.", "👧"), Sentence("Then a little mouse came to help.", "🐭"),
        Sentence("They all pulled. Pop! Out came the turnip!", "🎉"),
    )

    // ---- Speaking levels (5–8) are added in the next build ---------------------------------------------
    /** Everything a speaking level's practice can ask about. */
    fun speakingPool(stage: Int): Triple<List<Phrase>, List<List<Line>>, List<Sentence>> {
        val st = stage(stage)
        return Triple(st.lessons.flatMap { it.phrases }, st.lessons.map { it.dialogue }.filter { it.isNotEmpty() },
            st.lessons.flatMap { it.sentences })
    }

    val stages: List<EnStage> = listOf(
        EnStage(1, "Sounds", "Level 1: Letters and sounds", "a for apple, b for ball", "a b c",
            "🔤", Color(0xFFE3F2FD), Color(0xFF1565C0), listOf(
                EnLesson("e1.1", "The alphabet", "a for apple, b for ball, c for cat…", LessonKind.ALPHABET,
                    "Tap a letter to hear its name, then its picture word.", sounds = alphabet),
                EnLesson("e1.2", "s a t p", "sat, pat, tap", LessonKind.SOUNDS,
                    "Learn four letters. Already you can read real words!", sounds = sounds("s a t p"), words = set1),
                EnLesson("e1.3", "i n m d", "pin, man, map, dad", LessonKind.SOUNDS,
                    "Four more letters. Tap the picture word to hear it.", sounds = sounds("i n m d"), words = set2),
                EnLesson("e1.4", "g o c k", "dog, cat, pot, top", LessonKind.SOUNDS,
                    "c and k make the same sound.", sounds = sounds("g o c k"), words = set3),
                EnLesson("e1.5", "ck e u r", "duck, red, sun, run", LessonKind.SOUNDS,
                    "ck is two letters with one sound.", sounds = sounds("ck e u r"), words = set4),
                EnLesson("e1.6", "h b f l", "hat, bed, fan, bell", LessonKind.SOUNDS,
                    "At the end of a short word, f, l and s are often doubled: puff, bell, kiss.",
                    sounds = sounds("h b f l ff ll ss"), words = set5),
                EnLesson("e1.7", "j v w x", "jam, van, web, box", LessonKind.SOUNDS,
                    "x says 'ks' at the end of a word.", sounds = sounds("j v w x"), words = set6),
                EnLesson("e1.8", "y z qu", "yes, zip, quiz, buzz", LessonKind.SOUNDS,
                    "Now you know every letter!", sounds = sounds("y z zz qu"), words = set7),
            )),
        EnStage(2, "Blending", "Level 2: Reading short words", "c + a + t = cat", "c·a·t",
            "🧩", Color(0xFFFFF4D6), Color(0xFFE0A800), listOf(
                EnLesson("e2.1", "Short a", "cat, hat, bag, map…", LessonKind.WORDS,
                    "Tap each letter, then tap the word. Push the sounds together faster and faster: c… a… t… cat!", words = shortA),
                EnLesson("e2.2", "Short e", "bed, pen, hen, net…", LessonKind.WORDS, "e as in egg. Blend each word.", words = shortE),
                EnLesson("e2.3", "Short i", "pig, bin, lip, six…", LessonKind.WORDS, "i as in insect. Blend each word.", words = shortI),
                EnLesson("e2.4", "Short o", "dog, box, fox, pot…", LessonKind.WORDS, "o as in octopus. Blend each word.", words = shortO),
                EnLesson("e2.5", "Short u", "sun, bus, cup, bug…", LessonKind.WORDS, "u as in umbrella. Blend each word.", words = shortU),
                EnLesson("e2.6", "Double letters", "bell, doll, kiss, egg…", LessonKind.WORDS,
                    "Two of the same letter make one sound.", words = doubles),
                EnLesson("e2.7", "Blends at the start", "frog, crab, flag, drum…", LessonKind.WORDS,
                    "Two consonants at the start: say both sounds, one after the other — f-r-o-g.", words = blendsStart),
                EnLesson("e2.8", "Blends at the end", "hand, milk, tent, lamp…", LessonKind.WORDS,
                    "Two consonants at the end: h-a-n-d.", words = blendsEnd),
                EnLesson("e2.9", "Read a caption", "a cat on a mat", LessonKind.SENTENCES,
                    "Read each little caption. Tap a word to hear it.", sentences = captions),
            )),
        EnStage(3, "Letter teams", "Level 3: Letter teams and long vowels", "ship, ring, rain, cake…", "sh  ee",
            "👯", Color(0xFFF3E5F5), Color(0xFF6A1B9A), listOf(
                EnLesson("e3.1", "sh · ch · th", "ship, chick, thumb", LessonKind.SOUNDS,
                    "Some letters team up to make one new sound. Read the pair as one tile.",
                    sounds = digraphsA, words = digraphWordsA),
                EnLesson("e3.2", "ng · nk · ck", "ring, sink, rock", LessonKind.SOUNDS,
                    "These teams come at the end of a word.", sounds = digraphsB, words = digraphWordsB),
                EnLesson("e3.3", "wh · ph · qu", "wheel, phone, queen", LessonKind.SOUNDS,
                    "ph sounds like f, and qu sounds like kw.", sounds = digraphsC, words = digraphWordsC),
                EnLesson("e3.4", "ai · ee · igh · oa · oo", "rain, tree, night, boat, moon", LessonKind.SOUNDS,
                    "Two or three letters together often say a long vowel.", sounds = vowelTeams1, words = teamWords1),
                EnLesson("e3.5", "ar · or · ur · ir · er", "car, fork, nurse, bird", LessonKind.SOUNDS,
                    "A vowel followed by r makes a new sound.", sounds = vowelTeams2, words = teamWords2),
                EnLesson("e3.6", "ow · ou · oi · oy", "cow, house, coin, boy", LessonKind.SOUNDS,
                    "These teams slide from one sound to another.", sounds = vowelTeams3, words = teamWords3),
                EnLesson("e3.7", "Magic e", "cap → cape, kit → kite", LessonKind.PAIRS,
                    "A silent e at the end jumps over one letter and makes the vowel say its name.", pairs = magicE),
                EnLesson("e3.8", "ay · ea · ie · ue · ew", "day, leaf, pie, blue, new", LessonKind.SOUNDS,
                    "More ways to write long vowel sounds.", sounds = vowelTeams4, words = teamWords4),
            )),
        EnStage(4, "First reading", "Level 4: Sight words, sentences and stories", "the, said, was… I can read!", "the  said",
            "📚", Color(0xFFFFF8E1), Color(0xFFFF8F00), listOf(
                EnLesson("e4.1", "Tricky words 1", tricky1.joinToString(", "), LessonKind.TRICKY,
                    "Tricky words don't follow the sound rules. Look, say, and remember them by sight.", tricky = tricky1),
                EnLesson("e4.2", "Tricky words 2", tricky2.joinToString(", "), LessonKind.TRICKY,
                    "Tap each word to hear it, then say it yourself.", tricky = tricky2),
                EnLesson("e4.3", "Tricky words 3", tricky3.joinToString(", "), LessonKind.TRICKY,
                    "Tap each word to hear it, then say it yourself.", tricky = tricky3),
                EnLesson("e4.4", "Sight words 4", tricky4.joinToString(", "), LessonKind.TRICKY,
                    "More words you will see in every book.", tricky = tricky4),
                EnLesson("e4.5", "Colours and numbers", "red, blue… one, two, three…", LessonKind.TRICKY,
                    "Read the colours and the numbers from one to ten.", tricky = colours + numbers.filter { it !in colours }),
                EnLesson("e4.6", "Flash cards", "15 cards — read the word, then check", LessonKind.CARDS,
                    "Read the word out loud, then tap to hear if you were right.", tricky = allTricky),
                EnLesson("e4.7", "At home", "This is my mum.", LessonKind.SENTENCES,
                    "Point to each word as you read. Tap a word to hear it, or the speaker to hear the sentence.", sentences = sentences1),
                EnLesson("e4.8", "At the park", "I can run fast.", LessonKind.SENTENCES,
                    "Read about a day at the park.", sentences = sentences2),
                EnLesson("e4.9", "Story: The Little Red Hen", "An old folk tale in seven sentences", LessonKind.STORY,
                    "Read the story. Then tell someone: why did the hen eat the bread herself?", sentences = story),
                EnLesson("e4.10", "Story: The Enormous Turnip", "An old folk tale in seven sentences", LessonKind.STORY,
                    "Read the story. Who was the smallest helper?", sentences = story2),
            )),
    )

    fun stage(n: Int) = stages.first { it.number == n }

    /** Respellings for the phone voice, used only when no recording exists. Single letters are never changed. */
    val pronunciation = mapOf("yo-yo" to "yo yo")

    /** Text as the voice should say it. */
    fun pronounce(text: String): String {
        if (text.length <= 2) return text
        return pronunciation.entries.fold(text) { t, (k, v) -> t.replace(k, v, ignoreCase = true) }
    }

    /** Every word the course reads, for quizzes and checks. */
    val allWords: List<ReadWord> get() = (set1 + set2 + set3 + set4 + set5 + set6 + set7 + shortA + shortE + shortI + shortO + shortU +
        doubles + blendsStart + blendsEnd + digraphWordsA + digraphWordsB + digraphWordsC +
        magicE.flatMap { listOf(it.first, it.second) } + teamWords).distinctBy { it.word }
}
