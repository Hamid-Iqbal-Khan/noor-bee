package com.example.qaida

/*
 * Noorani Qaida — Stage 2 (Takhti 2–9) for Urdu-speaking learners, IndoPak conventions.
 * Joining, huroof-e-muqatta'at, tanween, khara zabar / khari zer / ulta pesh,
 * madd and leen letters, jazm. All learner text in Urdu; requires teacher review.
 */
object QaidaStage2 {

    // ---- 2.1 Murakkabat: joined shapes --------------------------------------------------

    /** Letters that never join to the letter after them (to their left). */
    val nonConnectorIds = setOf("alif", "dal", "dhal", "ra", "zay", "waw")

    data class JoinedForms(val isolated: String, val initial: String, val medial: String, val final: String)

    private const val TATWEEL = "ـ"

    fun forms(letter: QaidaLetter): JoinedForms {
        val c = letter.arabic
        return if (letter.id in nonConnectorIds || letter.id == "hamza") {
            // Joins only from the right: start and middle look like the isolated / end shapes
            JoinedForms(c, c, TATWEEL + c, TATWEEL + c)
        } else {
            JoinedForms(c, c + TATWEEL, TATWEEL + c + TATWEEL, TATWEEL + c)
        }
    }

    data class JoinedWord(val word: String, val letterIds: List<String>, val urduMeaning: String) {
        val audioKey get() = "s2_word_" + letterIds.joinToString("_")
    }

    /** Short Quranic words to split into letters (letters only, no harakat yet — the Takhti 2 way). */
    val joinedWords = listOf(
        JoinedWord("قل", listOf("qaf", "lam"), "کہو"),
        JoinedWord("من", listOf("mim", "nun"), "کون / سے"),
        JoinedWord("لم", listOf("lam", "mim"), "نہیں"),
        JoinedWord("كتب", listOf("kaf", "ta", "ba"), "لکھا"),
        JoinedWord("خلق", listOf("kha", "lam", "qaf"), "پیدا کیا"),
        JoinedWord("علم", listOf("ain", "lam", "mim"), "علم / جانا"),
        JoinedWord("نور", listOf("nun", "waw", "ra"), "روشنی"),
        JoinedWord("رب", listOf("ra", "ba"), "پالنے والا"),
        JoinedWord("دين", listOf("dal", "ya", "nun"), "دین / بدلہ"),
        JoinedWord("صبر", listOf("sad", "ba", "ra"), "صبر"),
    )

    // ---- 2.2 Huroof-e-muqatta'at ----------------------------------------------------------

    data class Muqattaat(val id: String, val text: String, val reading: String, val surahs: String) {
        val audioKey get() = "s2_muqattaat_$id"
    }

    /** The 14 distinct disjoined-letter openings, with how each is read letter by letter. */
    val muqattaat = listOf(
        Muqattaat("alm", "الٓمٓ", "اَلِف لَام مِیْم", "البقرہ، آلِ عمران، العنکبوت، الروم، لقمان، السجدہ"),
        Muqattaat("almas", "الٓمٓصٓ", "اَلِف لَام مِیْم صَاد", "الاعراف"),
        Muqattaat("alr", "الٓر", "اَلِف لَام رَا", "یونس، ہود، یوسف، ابراہیم، الحجر"),
        Muqattaat("almr", "الٓمٓر", "اَلِف لَام مِیْم رَا", "الرعد"),
        Muqattaat("khyas", "كٓهٰيٰعٓصٓ", "کَاف ہَا یَا عَیْن صَاد", "مریم"),
        Muqattaat("taha", "طٰهٰ", "طَا ہَا", "طٰہٰ"),
        Muqattaat("tsm", "طٰسٓمٓ", "طَا سِیْن مِیْم", "الشعراء، القصص"),
        Muqattaat("ts", "طٰسٓ", "طَا سِیْن", "النمل"),
        Muqattaat("yasin", "يٰسٓ", "یَا سِیْن", "یٰسٓ"),
        Muqattaat("sad", "صٓ", "صَاد", "صٓ"),
        Muqattaat("hm", "حٰمٓ", "حَا مِیْم", "غافر سے الاحقاف تک (سات سورتیں)"),
        Muqattaat("asq", "عٓسٓقٓ", "عَیْن سِیْن قَاف", "الشوریٰ (حٰمٓ کے بعد)"),
        Muqattaat("qaf", "قٓ", "قَاف", "قٓ"),
        Muqattaat("nun", "نٓ", "نُوْن", "القلم"),
    )

    // ---- 2.3 – 2.6 Marks taught on every letter ------------------------------------------

    enum class Mark(
        val lesson: String,       // which lesson board it belongs to
        val urduName: String,
        val hint: String,         // Urdu
        val sample: String,       // written on ب
        val hijjeSuffix: String,  // what the child says after the letter name, e.g. "دو زبر بَن"
    ) {
        DO_ZABAR("tanween", "دو زَبَر", "دو ترچھی لکیریں اوپر — آخر میں 'ن' کی آواز: بَن", "بًا", "دو زَبَر بَن"),
        DO_ZER("tanween", "دو زِیر", "دو ترچھی لکیریں نیچے — آخر میں 'ن' کی آواز: بِن", "بٍ", "دو زِیر بِن"),
        DO_PESH("tanween", "دو پیش", "دو پیش اوپر — آخر میں 'ن' کی آواز: بُن", "بٌ", "دو پیش بُن"),
        KHARA_ZABAR("khara", "کھڑا زَبَر", "اوپر سیدھی کھڑی لکیر — زبر کو ایک الف کے برابر لمبا کریں: بَا", "بٰ", "کھڑا زَبَر بَا"),
        KHARI_ZER("khara", "کھڑی زِیر", "نیچے سیدھی کھڑی لکیر — زیر کو لمبا کریں: بِی", "بٖ", "کھڑی زِیر بِی"),
        ULTA_PESH("khara", "اُلٹا پیش", "اوپر اُلٹا پیش — پیش کو لمبا کریں: بُو", "بٗ", "اُلٹا پیش بُو"),
        JAZM("jazm", "جَزْم", "اوپر چھوٹا دائرہ (ساکن) — حرف کو پچھلے حرف سے ملا کر پڑھیں: اَبْ", "اَبْ", "جزم"),
    }

    /** Written form of a letter with a mark, Qaida style. Jazm is shown after a zabar-alif: اَبْ. */
    fun write(letter: QaidaLetter, m: Mark): String {
        val c = letter.arabic
        return when (m) {
            Mark.DO_ZABAR -> if (letter.id == "hamza") "ءً" else c + "ً" + "ا"   // بًا (alif carrier, as written)
            Mark.DO_ZER -> c + "ٍ"
            Mark.DO_PESH -> c + "ٌ"
            Mark.KHARA_ZABAR -> c + "ٰ"
            Mark.KHARI_ZER -> c + "ٖ"
            Mark.ULTA_PESH -> c + "ٗ"
            Mark.JAZM -> "اَ" + c + "ْ"
        }
    }

    fun hijje(letter: QaidaLetter, m: Mark): String =
        if (m == Mark.JAZM) "اَلِف زَبَر اَ، ${letter.qaidaName} جَزْم: ${write(letter, m)}"
        else "${letter.qaidaName} ${m.hijjeSuffix.substringBeforeLast(' ')} ${write(letter, m)}"

    fun audioKey(letter: QaidaLetter, m: Mark) = "s2_${m.name.lowercase()}_${letter.id}"

    /** Letters used on the mark boards (alif carries no mark; hamza is shown on its own). */
    fun boardLetters(m: Mark): List<QaidaLetter> = QaidaData.letters.filter {
        it.id != "alif" && !(m == Mark.JAZM && it.id == "hamza") && !(m == Mark.DO_ZABAR && it.id == "hamza")
    }

    // ---- 2.5 Huroof-e-madda and leen (Takhti 7–8) ------------------------------------------

    data class MaddExample(val written: String, val urdu: String, val audioKey: String)

    val madd = listOf(
        MaddExample("بَا", "زبر کے بعد الف — آواز ایک الف کے برابر لمبی: بَا", "s2_madd_ba_alif"),
        MaddExample("بُو", "پیش کے بعد واؤ — لمبی آواز: بُو", "s2_madd_bu_waw"),
        MaddExample("بِي", "زیر کے بعد یا — لمبی آواز: بِی", "s2_madd_bi_ya"),
        MaddExample("قَالَ", "مثال: قَالَ (اس نے کہا)", "s2_madd_qala"),
        MaddExample("يَقُولُ", "مثال: يَقُولُ (وہ کہتا ہے)", "s2_madd_yaqulu"),
        MaddExample("قِيلَ", "مثال: قِيلَ (کہا گیا)", "s2_madd_qila"),
    )
    val leen = listOf(
        MaddExample("بَوْ", "زبر کے بعد ساکن واؤ — نرم آواز، کھینچے بغیر: بَوْ", "s2_leen_baw"),
        MaddExample("بَيْ", "زبر کے بعد ساکن یا — نرم آواز: بَیْ", "s2_leen_bay"),
        MaddExample("خَوْفٌ", "مثال: خَوْفٌ (ڈر)", "s2_leen_khawf"),
        MaddExample("بَيْتٌ", "مثال: بَيْتٌ (گھر)", "s2_leen_bayt"),
    )

    /** Jazm practice words (Takhti 9). */
    val jazmWords = listOf(
        MaddExample("قُلْ", "کہو", "s2_jazmword_qul"),
        MaddExample("لَمْ", "نہیں", "s2_jazmword_lam"),
        MaddExample("مِنْ", "سے", "s2_jazmword_min"),
        MaddExample("كُنْ", "ہو جا", "s2_jazmword_kun"),
        MaddExample("اَنْتَ", "تُو", "s2_jazmword_anta"),
        MaddExample("اَلْحَمْدُ", "سب تعریف", "s2_jazmword_alhamdu"),
    )

    /** IndoPak words that use the long-vowel marks (Takhti 6). */
    val kharaWords = listOf(
        MaddExample("الرَّحْمٰنِ", "بڑا مہربان — کھڑا زبر", "s2_khara_rahman"),
        MaddExample("هٰذَا", "یہ — کھڑا زبر", "s2_khara_hadha"),
        MaddExample("ذٰلِكَ", "وہ — کھڑا زبر", "s2_khara_dhalika"),
        MaddExample("بِهٖ", "اس سے — کھڑی زیر", "s2_khara_bihi"),
        MaddExample("لَهٗ", "اس کے لیے — اُلٹا پیش", "s2_khara_lahu"),
    )
}

/** Stage 2 practice quiz: name the mark, or split a joined word. */
object QaidaStage2Quiz {
    fun generate(random: kotlin.random.Random, length: Int = QaidaProgress.QUIZ_LENGTH): List<QaidaQuestion> =
        List(length) { i ->
            if (i % 3 == 2) {
                val w = QaidaStage2.joinedWords.random(random)
                val correct = w.letterIds.joinToString(" ") { QaidaData.letter(it).arabic }
                val wrong = QaidaStage2.joinedWords.filter { it != w && it.letterIds.size == w.letterIds.size }
                    .map { o -> o.letterIds.joinToString(" ") { QaidaData.letter(it).arabic } }
                    .distinct().filter { it != correct }.shuffled(random).take(3)
                val options = (wrong + correct).shuffled(random)
                QaidaQuestion(w.word, true, "اس لفظ کے حروف کون سے ہیں؟", options, true, options.indexOf(correct), w.audioKey)
            } else {
                val marks = QaidaStage2.Mark.values()
                val m = marks.random(random)
                val l = QaidaStage2.boardLetters(m).random(random)
                val options = (marks.filter { it != m }.shuffled(random).take(3) + m).shuffled(random)
                QaidaQuestion(QaidaStage2.write(l, m), true, "اس پر کون سا نشان ہے؟",
                    options.map { it.urduName }, false, options.indexOf(m), QaidaStage2.audioKey(l, m))
            }
        }
}
