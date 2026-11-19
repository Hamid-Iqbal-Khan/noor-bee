package com.example.qaida

/*
 * Noorani Qaida — Stage 3 for Urdu-speaking learners (IndoPak conventions):
 * tashdeed, ghunna, the madd sign, lam-e-shamsi / qamari, then first full ayat.
 * Quranic text here is typed in IndoPak style WITHOUT waqf marks; a unit test checks its
 * letters against the app's bundled Quran text. A teacher must still check it against a
 * printed IndoPak mushaf before release.
 */
object QaidaStage3 {

    data class Word(val written: String, val urdu: String, val audioKey: String)

    // ---- 3.1 Tashdeed board ----------------------------------------------------------------
    const val SHADDA = "ّ"

    enum class Vowel(val mark: String, val alif: String, val urduName: String, val key: String) {
        ZABAR("َ", "اَ", "زَبَر", "z"), ZER("ِ", "اِ", "زِیر", "zr"), PESH("ُ", "اُ", "پیش", "p"),
    }

    /** اَبَّ  اِبِّ  اُبُّ — tashdeed is taught after a vowelled alif, as in the Qaida. */
    fun tashdeed(letter: QaidaLetter, v: Vowel) = v.alif + letter.arabic + SHADDA + v.mark

    /** اَبَّ = اَبْ + بَ : the first half is the letter with jazm after the vowelled alif, the second half the letter with the vowel. */
    fun tashdeedParts(letter: QaidaLetter, v: Vowel) = Pair(v.alif + letter.arabic + "\u0652", letter.arabic + v.mark)

    fun tashdeedHijje(letter: QaidaLetter, v: Vowel) =
        tx("اَلِف ${v.urduName}، ${letter.qaidaName} ${v.urduName} تشدید: ${tashdeed(letter, v)}",
            "Alif ${QaidaLang.term(v.urduName)}, ${tr(letter.qaidaName)} ${QaidaLang.term(v.urduName)} shaddah: ${tashdeed(letter, v)}")

    fun tashdeedKey(letter: QaidaLetter, v: Vowel) = "s3_tashdeed_${letter.id}_${v.key}"

    /** Alif and hamza never carry tashdeed on the board. */
    fun tashdeedLetters() = QaidaData.letters.filter { it.id != "alif" && it.id != "hamza" }

    // ---- 3.2 Ghunna and tashdeed words --------------------------------------------------------
    val ghunnaWords = listOf(
        Word("اِنَّ", "بے شک", "s3_ghunna_inna"),
        Word("ثُمَّ", "پھر", "s3_ghunna_thumma"),
        Word("اَمَّا", "رہا / لیکن", "s3_ghunna_amma"),
        Word("اِنَّا", "بے شک ہم", "s3_ghunna_inna_we"),
        Word("جَنَّةٌ", "باغ / جنت", "s3_ghunna_jannah"),
        Word("مِمَّا", "اس میں سے جو", "s3_ghunna_mimma"),
    )
    val tashdeedWords = listOf(
        Word("رَبِّ", "پالنے والا", "s3_word_rabbi"),
        Word("كُلُّ", "ہر / سب", "s3_word_kullu"),
        Word("عَلَّمَ", "سکھایا", "s3_word_allama"),
        Word("مُحَمَّدٌ", "محمد ﷺ", "s3_word_muhammad"),
        Word("اَلْحَقُّ", "حق / سچ", "s3_word_alhaqqu"),
        Word("يُحِبُّ", "پسند کرتا ہے", "s3_word_yuhibbu"),
    )

    // ---- 3.3 Madd sign (~) ----------------------------------------------------------------------
    val maddSignWords = listOf(
        Word("جَآءَ", "آیا — مدِّ متصل: ۴ سے ۵ حرکت", "s3_madd_jaa"),
        Word("السَّمَآءُ", "آسمان — مدِّ متصل: ۴ سے ۵ حرکت", "s3_madd_samaa"),
        Word("بِمَآ", "اس کے ساتھ جو — مدِّ منفصل: ۴ سے ۵ حرکت", "s3_madd_bima"),
        Word("اُولٰٓئِكَ", "وہ لوگ — مدِّ متصل: ۴ سے ۵ حرکت", "s3_madd_ulaika"),
        Word("الضَّآلِّيْنَ", "گمراہ لوگ — مدِّ لازم: پورے ۶ حرکت", "s3_madd_daallin"),
    )

    // ---- 3.4 Lam-e-shamsi / qamari ---------------------------------------------------------------
    /** The 14 sun letters: after them the lam of اَل is silent and the letter gets tashdeed. */
    val shamsiIds = listOf("ta", "tha", "dal", "dhal", "ra", "zay", "sin", "shin", "sad", "dad", "ta_heavy", "dha_heavy", "lam", "nun")
    /** The 14 moon letters: the lam of اَل is read with jazm. */
    val qamariIds = listOf("alif", "ba", "jim", "ha_heavy", "kha", "ain", "ghain", "fa", "qaf", "kaf", "mim", "waw", "ha", "ya")

    val shamsiWords = listOf(
        Word("اَلشَّمْسُ", "سورج", "s3_shamsi_shams"),
        Word("اَلنَّاسُ", "لوگ", "s3_shamsi_nas"),
        Word("اَلرَّحْمٰنُ", "بڑا مہربان", "s3_shamsi_rahman"),
        Word("اَلدِّيْنُ", "دین / بدلہ", "s3_shamsi_din"),
        Word("اَلصِّرَاطُ", "راستہ", "s3_shamsi_sirat"),
    )
    val qamariWords = listOf(
        Word("اَلْقَمَرُ", "چاند", "s3_qamari_qamar"),
        Word("اَلْحَمْدُ", "سب تعریف", "s3_qamari_hamd"),
        Word("اَلْكِتٰبُ", "کتاب", "s3_qamari_kitab"),
        Word("اَلْعِلْمُ", "علم", "s3_qamari_ilm"),
        Word("اَلْمُسْتَقِيْمُ", "سیدھا", "s3_qamari_mustaqim"),
    )

    // ---- 3.5 / 3.6 First recitation ----------------------------------------------------------------
    data class Ayah(val surah: Int, val number: Int, val text: String, val rules: String) {
        val audioKey get() = "s3_ayah_${surah}_$number"
    }

    val fatiha = listOf(
        Ayah(1, 1, "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ", "اللّٰهِ اور الرَّحْمٰنِ میں کھڑا زبر؛ الرَّ میں لامِ شمسی اور تشدید"),
        Ayah(1, 2, "اَلْحَمْدُ لِلّٰهِ رَبِّ الْعٰلَمِيْنَ", "اَلْحَمْدُ میں لامِ قمری؛ رَبِّ میں تشدید؛ الْعٰلَمِيْنَ میں کھڑا زبر اور مدّ"),
        Ayah(1, 3, "الرَّحْمٰنِ الرَّحِيْمِ", "دونوں لفظوں میں لامِ شمسی اور تشدید"),
        Ayah(1, 4, "مٰلِكِ يَوْمِ الدِّيْنِ", "مٰلِكِ میں کھڑا زبر؛ يَوْمِ میں حرفِ لین؛ الدِّيْنِ میں لامِ شمسی"),
        Ayah(1, 5, "اِيَّاكَ نَعْبُدُ وَاِيَّاكَ نَسْتَعِيْنُ", "اِيَّاكَ میں تشدید؛ نَعْبُدُ اور نَسْتَعِيْنُ میں جزم"),
        Ayah(1, 6, "اِهْدِنَا الصِّرَاطَ الْمُسْتَقِيْمَ", "الصِّرَاطَ میں لامِ شمسی؛ الْمُسْتَقِيْمَ میں لامِ قمری"),
        Ayah(1, 7, "صِرَاطَ الَّذِيْنَ اَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوْبِ عَلَيْهِمْ وَلَا الضَّآلِّيْنَ",
            "الَّذِيْنَ میں لامِ شمسی؛ الضَّآلِّيْنَ میں مدِّ لازم — پورے ۶ حرکت کھینچیں"),
    )

    val ikhlas = listOf(
        Ayah(112, 1, "قُلْ هُوَ اللّٰهُ اَحَدٌ", "قُلْ میں جزم؛ اَحَدٌ میں دو پیش"),
        Ayah(112, 2, "اَللّٰهُ الصَّمَدُ", "الصَّمَدُ میں لامِ شمسی اور تشدید"),
        Ayah(112, 3, "لَمْ يَلِدْ وَلَمْ يُوْلَدْ", "ہر لفظ میں جزم؛ يُوْلَدْ میں حرفِ مدّہ"),
        Ayah(112, 4, "وَلَمْ يَكُنْ لَّهٗ كُفُوًا اَحَدٌ", "لَّهٗ میں تشدید (غنّہ کے بغیر ادغام) اور اُلٹا پیش؛ كُفُوًا میں دو زبر"),
    )
}

/** Stage 3 quiz: tashdeed vowel, sun or moon lam, and naming the mark. */
object QaidaStage3Quiz {
    private const val SUN = "حرفِ شمسی — لام نہیں پڑھا جاتا"
    private const val MOON = "حرفِ قمری — لام جزم سے پڑھا جاتا ہے"

    fun generate(random: kotlin.random.Random, length: Int = QaidaProgress.QUIZ_LENGTH): List<QaidaQuestion> =
        List(length) { i ->
            when (i % 3) {
                0 -> { // which vowel is on the doubled letter?
                    val l = QaidaStage3.tashdeedLetters().random(random)
                    val v = QaidaStage3.Vowel.values().random(random)
                    val opts = QaidaStage3.Vowel.values().map { tx("تشدید کے ساتھ ${it.urduName}", "shaddah with ${QaidaLang.term(it.urduName)}") }
                    QaidaQuestion(QaidaStage3.tashdeed(l, v), true, "دوسرے حرف پر کیا ہے؟", opts, false,
                        v.ordinal, QaidaStage3.tashdeedKey(l, v))
                }
                1 -> { // sun or moon?
                    val sun = random.nextBoolean()
                    val w = (if (sun) QaidaStage3.shamsiWords else QaidaStage3.qamariWords).random(random)
                    QaidaQuestion(w.written, true, "اس لفظ میں اَل کا لام کیسا ہے؟", listOf(SUN, MOON), false,
                        if (sun) 0 else 1, w.audioKey)
                }
                else -> { // classify a letter
                    val sun = random.nextBoolean()
                    val l = QaidaData.letter((if (sun) QaidaStage3.shamsiIds else QaidaStage3.qamariIds).random(random))
                    QaidaQuestion(l.arabic, true, "یہ حرف شمسی ہے یا قمری؟", listOf("شمسی ☀️", "قمری 🌙"), false,
                        if (sun) 0 else 1, l.audioKey)
                }
            }
        }
}
