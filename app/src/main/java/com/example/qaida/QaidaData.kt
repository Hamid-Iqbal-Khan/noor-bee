package com.example.qaida

/*
 * Noorani Qaida — Stage 1 content for Urdu-speaking learners (IndoPak tradition).
 *
 * All learner-facing explanations are in Urdu. Arabic is shown in a Qaida-style
 * Naskh face. Every string here must be reviewed by a qualified teacher/scholar
 * and a native Urdu speaker before release (see curriculum plan, section 7).
 *
 * Audio: each item has an `audioKey`. A recorded file at
 * assets/audio/qaida/<audioKey>.mp3 is played when present; otherwise the app
 * falls back to device text-to-speech and labels it as temporary.
 */

/** Broad articulation region, as taught to children (simplified from the 17 makharij). */
enum class MakhrajRegion(val urdu: String, val emoji: String) {
    THROAT("حلق", "🗣️"),
    TONGUE("زبان", "👅"),
    LIPS("ہونٹ", "👄"),
    MOUTH_SPACE("جوف (منہ اور حلق کا خالی حصہ)", "🌬️"),
}

data class QaidaLetter(
    val id: String,          // stable id, also used in audio keys
    val arabic: String,      // the isolated letter
    val qaidaName: String,   // name as recited in the Qaida, Urdu script
    val roman: String,       // romanised name for parents
    val region: MakhrajRegion,
    val makhraj: String,     // where it is pronounced, Urdu
    val urduNote: String? = null,   // what Urdu speakers must do differently, Urdu
    val urduShape: String? = null,  // how the same letter is usually written in Urdu, if different
    val heavy: Boolean = false,     // پُر (heavy) letter
) {
    val audioKey get() = "letter_$id"
}

object QaidaData {

    val letters: List<QaidaLetter> = listOf(
        QaidaLetter("alif", "ا", "اَلِف", "alif", MakhrajRegion.MOUTH_SPACE,
            "الف خود ساکن رہتا ہے؛ اپنے سے پہلے والے حرف کی آواز کو لمبا کرتا ہے۔ حرکت والا الف دراصل ہمزہ (ء) ہوتا ہے۔"),
        QaidaLetter("ba", "ب", "بَا", "baa", MakhrajRegion.LIPS, "دونوں ہونٹوں کے تر حصے کو ملا کر۔"),
        QaidaLetter("ta", "ت", "تَا", "taa", MakhrajRegion.TONGUE,
            "زبان کی نوک اوپر کے سامنے والے دانتوں کی جڑ سے لگا کر، باریک آواز۔",
            urduNote = "ط سے الگ رکھیں: ت باریک ہے، ط موٹی۔"),
        QaidaLetter("tha", "ث", "ثَا", "thaa", MakhrajRegion.TONGUE,
            "زبان کی نوک اوپر کے سامنے والے دانتوں کے کنارے سے لگا کر، نرم آواز۔",
            urduNote = "اردو میں اسے 'س' پڑھتے ہیں۔ قرآن میں 'س' نہ پڑھیں؛ زبان کی نوک دانتوں کے کنارے پر رکھیں۔"),
        QaidaLetter("jim", "ج", "جِیْم", "jeem", MakhrajRegion.TONGUE, "زبان کے بیچ کا حصہ اوپر کے تالو سے لگا کر۔"),
        QaidaLetter("ha_heavy", "ح", "حَا", "Haa", MakhrajRegion.THROAT,
            "حلق کے بیچ کے حصے سے، صاف سانس کی طرح، کھرکھراہٹ کے بغیر۔",
            urduNote = "اردو میں ح اور ہ ایک جیسے بولے جاتے ہیں۔ ح حلق کے بیچ سے ہے، ہ حلق کے آخری حصے سے۔"),
        QaidaLetter("kha", "خ", "خَا", "khaa", MakhrajRegion.THROAT, "حلق کے اوپر والے حصے سے (منہ کے قریب)۔", heavy = true),
        QaidaLetter("dal", "د", "دَال", "daal", MakhrajRegion.TONGUE,
            "زبان کی نوک اوپر کے سامنے والے دانتوں کی جڑ سے لگا کر۔",
            urduNote = "اردو کے 'ڈ' کی طرح زبان پیچھے نہ موڑیں۔"),
        QaidaLetter("dhal", "ذ", "ذَال", "dhaal", MakhrajRegion.TONGUE,
            "زبان کی نوک اوپر کے سامنے والے دانتوں کے کنارے سے لگا کر، نرم آواز۔",
            urduNote = "اردو میں اسے 'ز' پڑھتے ہیں۔ قرآن میں زبان کی نوک دانتوں کے کنارے پر رکھیں۔"),
        QaidaLetter("ra", "ر", "رَا", "raa", MakhrajRegion.TONGUE,
            "زبان کی نوک اور اس کے ساتھ کا حصہ اوپر کے تالو سے لگا کر۔",
            urduNote = "اردو کے 'ڑ' کی طرح زبان نہ موڑیں۔"),
        QaidaLetter("zay", "ز", "زَا", "zaa", MakhrajRegion.TONGUE,
            "زبان کی نوک نیچے کے دانتوں کے قریب، سیٹی جیسی آواز (صفیر)۔"),
        QaidaLetter("sin", "س", "سِیْن", "seen", MakhrajRegion.TONGUE,
            "زبان کی نوک نیچے کے دانتوں کے قریب، سیٹی جیسی باریک آواز (صفیر)۔"),
        QaidaLetter("shin", "ش", "شِیْن", "sheen", MakhrajRegion.TONGUE, "زبان کے بیچ کا حصہ اوپر کے تالو سے لگا کر، آواز پھیلا کر۔"),
        QaidaLetter("sad", "ص", "صَاد", "Saad", MakhrajRegion.TONGUE,
            "س کی جگہ سے، مگر منہ بھر کر موٹی آواز۔",
            urduNote = "اردو میں اسے 'س' پڑھتے ہیں۔ قرآن میں ص کی آواز موٹی (پُر) ہے۔", heavy = true),
        QaidaLetter("dad", "ض", "ضَاد", "Daad", MakhrajRegion.TONGUE,
            "زبان کی کروٹ (کنارہ) اوپر کی داڑھوں سے لگا کر، موٹی آواز۔",
            urduNote = "اردو میں اسے 'ز' پڑھتے ہیں۔ یہ سب سے مشکل حرف ہے؛ استاد سے سن کر مشق کریں۔", heavy = true),
        QaidaLetter("ta_heavy", "ط", "طَا", "Taa", MakhrajRegion.TONGUE,
            "ت کی جگہ سے، مگر منہ بھر کر موٹی آواز۔",
            urduNote = "اردو میں ت اور ط ایک جیسے بولے جاتے ہیں۔ ط موٹی (پُر) ہے۔", heavy = true),
        QaidaLetter("dha_heavy", "ظ", "ظَا", "Dhaa", MakhrajRegion.TONGUE,
            "ذ کی جگہ سے (زبان کی نوک دانتوں کے کنارے)، مگر موٹی آواز۔",
            urduNote = "اردو میں اسے 'ز' پڑھتے ہیں۔ ظ کی آواز ذ جیسی مگر موٹی ہے۔", heavy = true),
        QaidaLetter("ain", "ع", "عَیْن", "'ayn", MakhrajRegion.THROAT,
            "حلق کے بیچ کے حصے سے، آواز کو ہلکا سا دبا کر۔",
            urduNote = "اردو میں ع کو الف کی طرح پڑھ لیتے ہیں۔ قرآن میں ع کی آواز حلق کے بیچ سے صاف نکالیں۔"),
        QaidaLetter("ghain", "غ", "غَیْن", "ghayn", MakhrajRegion.THROAT, "حلق کے اوپر والے حصے سے (منہ کے قریب)۔", heavy = true),
        QaidaLetter("fa", "ف", "فَا", "faa", MakhrajRegion.LIPS, "نیچے کے ہونٹ کا اندرونی حصہ اوپر کے دانتوں کے کنارے سے لگا کر۔"),
        QaidaLetter("qaf", "ق", "قَاف", "qaaf", MakhrajRegion.TONGUE,
            "زبان کی جڑ کوّے کے قریب نرم تالو سے لگا کر، موٹی آواز۔",
            urduNote = "اردو میں ق کو اکثر 'ک' بولتے ہیں۔ ق کی جگہ ک سے پیچھے ہے اور آواز موٹی ہے۔", heavy = true),
        QaidaLetter("kaf", "ك", "کَاف", "kaaf", MakhrajRegion.TONGUE,
            "زبان کی جڑ سے ذرا آگے کا حصہ تالو سے لگا کر، باریک آواز۔",
            urduShape = "ک"),
        QaidaLetter("lam", "ل", "لَام", "laam", MakhrajRegion.TONGUE, "زبان کا کنارہ (نوک کے قریب) اوپر کے سامنے والے دانتوں کے مسوڑوں سے لگا کر۔"),
        QaidaLetter("mim", "م", "مِیْم", "meem", MakhrajRegion.LIPS, "دونوں ہونٹوں کے خشک حصے کو ملا کر۔"),
        QaidaLetter("nun", "ن", "نُوْن", "noon", MakhrajRegion.TONGUE, "زبان کی نوک اوپر کے مسوڑوں سے لگا کر۔"),
        QaidaLetter("waw", "و", "وَاو", "waaw", MakhrajRegion.LIPS,
            "دونوں ہونٹوں کو گول کر کے، پوری طرح ملائے بغیر۔",
            urduNote = "اردو کے 'و' کی طرح دانت ہونٹ پر نہ لگائیں؛ صرف ہونٹ گول کریں۔"),
        QaidaLetter("ha", "ه", "ہَا", "haa", MakhrajRegion.THROAT,
            "حلق کے سب سے آخری حصے سے (سینے کے قریب)، ہلکی آواز۔",
            urduNote = "قرآن میں اس کی شکل ه ہے، اردو کی ہ / ھ نہیں۔", urduShape = "ہ"),
        QaidaLetter("hamza", "ء", "ہَمْزَہ", "hamzah", MakhrajRegion.THROAT,
            "حلق کے سب سے آخری حصے سے، ہلکے جھٹکے کے ساتھ۔"),
        QaidaLetter("ya", "ي", "یَا", "yaa", MakhrajRegion.TONGUE,
            "زبان کے بیچ کا حصہ اوپر کے تالو سے لگا کر۔",
            urduNote = "عربی ي کے نیچے دو نقطے ہوتے ہیں؛ اردو میں آخر میں ی / ے لکھتے ہیں۔", urduShape = "ی"),
    )

    // ---- Lesson 1.2: sounds Urdu speakers merge -----------------------------------

    data class ExampleWord(val arabic: String, val urduMeaning: String) {
        val audioKey get() = "word_" + arabic.filter { it.isLetter() }
    }

    data class SoundGroup(
        val id: String,
        val urduSays: String,          // what Urdu speakers say for all of them
        val letterIds: List<String>,
        val tip: String,               // Urdu explanation
        val examples: Map<String, ExampleWord>, // letterId -> example word
    )

    val soundGroups: List<SoundGroup> = listOf(
        SoundGroup("s", "س", listOf("sin", "tha", "sad"),
            "اردو میں تینوں کو 'س' بولتے ہیں۔ ث میں زبان کی نوک اوپر کے دانتوں کے کنارے پر رکھیں (نرم آواز)، ص کو منہ بھر کر موٹی آواز سے پڑھیں۔",
            mapOf("sin" to ExampleWord("سَمِيْعٌ", "سننے والا"), "tha" to ExampleWord("ثُمَّ", "پھر"), "sad" to ExampleWord("صَبْرٌ", "صبر"))),
        SoundGroup("z", "ز", listOf("zay", "dhal", "dha_heavy", "dad"),
            "اردو میں چاروں کو 'ز' بولتے ہیں۔ ذ: زبان کی نوک دانتوں کے کنارے پر، نرم آواز۔ ظ: یہی جگہ مگر موٹی آواز۔ ض: زبان کی کروٹ اوپر کی داڑھوں سے، موٹی آواز۔",
            mapOf("zay" to ExampleWord("زَيْتُوْنٌ", "زیتون"), "dhal" to ExampleWord("ذِكْرٌ", "یاد"),
                "dha_heavy" to ExampleWord("ظِلٌّ", "سایہ"), "dad" to ExampleWord("ضُحًى", "چاشت کا وقت"))),
        SoundGroup("t", "ت", listOf("ta", "ta_heavy"),
            "ت باریک آواز ہے؛ ط اسی جگہ سے مگر منہ بھر کر موٹی آواز ہے۔",
            mapOf("ta" to ExampleWord("تِيْنٌ", "انجیر"), "ta_heavy" to ExampleWord("طَيِّبٌ", "پاکیزہ"))),
        SoundGroup("h", "ہ", listOf("ha", "ha_heavy"),
            "ہ حلق کے آخری حصے سے ہلکی آواز ہے؛ ح حلق کے بیچ سے، صاف سانس کی طرح۔",
            mapOf("ha" to ExampleWord("هُدًى", "ہدایت"), "ha_heavy" to ExampleWord("حَمْدٌ", "تعریف"))),
        SoundGroup("a", "ا", listOf("hamza", "ain"),
            "ء (حرکت والا الف) حلق کے آخری حصے سے ہلکے جھٹکے کے ساتھ؛ ع حلق کے بیچ سے دبا کر۔",
            mapOf("hamza" to ExampleWord("أَحَدٌ", "ایک"), "ain" to ExampleWord("عِلْمٌ", "علم"))),
        SoundGroup("k", "ک", listOf("kaf", "qaf"),
            "ک باریک آواز ہے؛ ق زبان کی جڑ سے، ک سے پیچھے، موٹی آواز۔",
            mapOf("kaf" to ExampleWord("كِتَابٌ", "کتاب"), "qaf" to ExampleWord("قَلَمٌ", "قلم"))),
    )

    // ---- Lesson 1.3: harakat (Takhti 4) ------------------------------------------------

    enum class Harakah(val mark: String, val urduName: String, val hint: String, val example: String) {
        ZABAR("َ", "زَبَر", "حرف کے اوپر ترچھی لکیر — 'اَ' کی چھوٹی آواز", "بَ"),
        ZER("ِ", "زِیر", "حرف کے نیچے ترچھی لکیر — 'اِ' کی چھوٹی آواز", "بِ"),
        PESH("ُ", "پیش", "حرف کے اوپر چھوٹا واؤ — 'اُ' کی چھوٹی آواز", "بُ"),
    }

    /** Letters that take harakat in Takhti 4: alif is replaced by hamza-on-alif (أ). */
    fun harakatLetters(): List<QaidaLetter> = letters.filter { it.id != "alif" }

    /** The written form of a letter with a harakah, e.g. "بَ"; hamza is written on alif as in the Qaida. */
    fun withHarakah(letter: QaidaLetter, h: Harakah): String {
        if (letter.id == "hamza") return when (h) { Harakah.ZABAR -> "أَ"; Harakah.ZER -> "إِ"; Harakah.PESH -> "أُ" }
        return letter.arabic + h.mark
    }

    /** Hijje — spelling aloud the Qaida way, e.g. "بَا زَبَر بَ". */
    fun hijje(letter: QaidaLetter, h: Harakah): String = "${letter.qaidaName} ${h.urduName} ${withHarakah(letter, h)}"

    fun audioKey(letter: QaidaLetter, h: Harakah) = "harakah_${letter.id}_${h.name.lowercase()}"

    fun letter(id: String): QaidaLetter = letters.first { it.id == id }
}
