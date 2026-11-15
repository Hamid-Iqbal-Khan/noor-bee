package com.example.qaida

/*
 * Stage 5 — understanding what we read (Urdu medium).
 * Frequent Quranic words, pronoun endings, then word-by-word meanings of the surahs from Stages 3–4.
 * Meanings are short, simple Urdu written for children; a qualified teacher must review them before release.
 */
object QaidaStage5 {

    data class Word(val arabic: String, val urdu: String, val slug: String) {
        val audioKey get() = "s5_word_$slug"
    }

    data class Group(val id: String, val title: String, val words: List<Word>)

    private fun w(a: String, u: String, s: String) = Word(a, u, s)

    val groups = listOf(
        Group("names", "اللہ کا نام اور صفات", listOf(
            w("اللّٰهُ", "اللہ", "allah"),
            w("رَبٌّ", "رب، پالنے والا", "rabb"),
            w("رَحْمٰنٌ", "بہت مہربان", "rahman"),
            w("رَحِيْمٌ", "نہایت رحم والا", "rahim"),
            w("عَلِيْمٌ", "سب کچھ جاننے والا", "alim"),
            w("حَكِيْمٌ", "حکمت والا", "hakim"),
        )),
        Group("small", "چھوٹے الفاظ (حروف)", listOf(
            w("مِنْ", "سے", "min"),
            w("فِيْ", "میں", "fi"),
            w("عَلٰى", "پر", "ala"),
            w("اِلٰى", "کی طرف", "ila"),
            w("عَنْ", "سے، کے بارے میں", "an"),
            w("وَ", "اور", "wa"),
            w("لَا", "نہیں، نہ", "la"),
            w("اِنَّ", "بے شک", "inna"),
            w("اِلَّا", "مگر، سوائے", "illa"),
        )),
        Group("pronouns", "ضمیریں", listOf(
            w("هُوَ", "وہ (ایک)", "huwa"),
            w("هُمْ", "وہ سب", "hum"),
            w("اَنْتَ", "تُو، آپ", "anta"),
            w("اَنَا", "میں", "ana"),
            w("نَحْنُ", "ہم", "nahnu"),
            w("الَّذِيْ", "وہ جو", "alladhi"),
            w("الَّذِيْنَ", "وہ لوگ جو", "alladhina"),
        )),
        Group("verbs", "کام (افعال)", listOf(
            w("قَالَ", "اس نے کہا", "qala"),
            w("قُلْ", "کہو، کہہ دیجیے", "qul"),
            w("كَانَ", "تھا", "kana"),
            w("خَلَقَ", "اس نے پیدا کیا", "khalaqa"),
            w("يَعْلَمُ", "وہ جانتا ہے", "yalamu"),
            w("اٰمَنُوْا", "وہ ایمان لائے", "amanu"),
            w("عَمِلُوْا", "انہوں نے عمل کیے", "amilu"),
        )),
        Group("nouns", "چیزوں کے نام", listOf(
            w("يَوْمٌ", "دن", "yawm"),
            w("اَرْضٌ", "زمین", "ard"),
            w("سَمَآءٌ", "آسمان", "sama"),
            w("نَاسٌ", "لوگ", "nas"),
            w("كِتٰبٌ", "کتاب", "kitab"),
            w("حَقٌّ", "سچ، حق", "haqq"),
            w("قَلْبٌ", "دل", "qalb"),
            w("جَنَّةٌ", "باغ، جنت", "jannah"),
        )),
    )

    val allWords get() = groups.flatMap { it.words }

    // ---- 5.2 Pronoun endings on رَبّ --------------------------------------------------------------
    data class Ending(val ending: String, val urdu: String, val example: String, val exampleUrdu: String, val slug: String) {
        val audioKey get() = "s5_end_$slug"
    }

    val endings = listOf(
        Ending("ـكَ", "تیرا / آپ کا", "رَبُّكَ", "تیرا رب", "ka"),
        Ending("ـهٗ", "اس کا", "رَبُّهٗ", "اس کا رب", "hu"),
        Ending("ـهُمْ", "ان کا", "رَبُّهُمْ", "ان کا رب", "hum"),
        Ending("ـكُمْ", "تمہارا", "رَبُّكُمْ", "تمہارا رب", "kum"),
        Ending("ـنَا", "ہمارا", "رَبُّنَا", "ہمارا رب", "na"),
        Ending("ـِيْ", "میرا", "رَبِّيْ", "میرا رب", "i"),
    )

    // ---- 5.3 – 5.5 Word by word ----------------------------------------------------------------------
    data class WbwAyah(val ayah: QaidaStage3.Ayah, val meanings: List<String>) {
        val words get() = ayah.text.split(' ')
        fun wordKey(i: Int) = "s5_wbw_${ayah.surah}_${ayah.number}_${i + 1}"
    }
    data class WbwSurah(val number: Int, val urduName: String, val ayat: List<WbwAyah>)

    private fun wbw(src: List<QaidaStage3.Ayah>, vararg m: List<String>) = src.mapIndexed { i, a -> WbwAyah(a, m[i]) }

    val fatiha = WbwSurah(1, "سورۃ الفاتحہ", wbw(QaidaStage3.fatiha,
        listOf("نام سے", "اللہ کے", "جو بہت مہربان", "نہایت رحم والا"),
        listOf("سب تعریف", "اللہ کے لیے", "جو رب ہے", "تمام جہانوں کا"),
        listOf("بہت مہربان", "نہایت رحم والا"),
        listOf("مالک", "دن کا", "بدلے کے"),
        listOf("صرف تیری", "ہم عبادت کرتے ہیں", "اور صرف تجھ سے", "ہم مدد مانگتے ہیں"),
        listOf("ہمیں چلا", "راستے پر", "سیدھے"),
        listOf("راستہ", "ان لوگوں کا", "تو نے انعام کیا", "جن پر", "نہ", "جن پر غضب ہوا", "ان پر", "اور نہ", "گمراہوں کا"),
    ))
    val ikhlas = WbwSurah(112, "سورۃ الاخلاص", wbw(QaidaStage3.ikhlas,
        listOf("کہہ دیجیے", "وہ", "اللہ", "ایک ہے"),
        listOf("اللہ", "بے نیاز ہے"),
        listOf("نہ", "اس کی کوئی اولاد", "اور نہ", "وہ کسی کی اولاد"),
        listOf("اور نہیں", "ہے", "اس کا", "برابر", "کوئی"),
    ))
    val kawthar = WbwSurah(108, "سورۃ الکوثر", wbw(QaidaStage4.kawthar.ayat,
        listOf("بے شک ہم نے", "آپ کو عطا کی", "کوثر"),
        listOf("تو نماز پڑھیے", "اپنے رب کے لیے", "اور قربانی کیجیے"),
        listOf("بے شک", "آپ کا دشمن", "وہی", "بے نام و نشان ہے"),
    ))
    val asr = WbwSurah(103, "سورۃ العصر", wbw(QaidaStage4.asr.ayat,
        listOf("قسم ہے زمانے کی"),
        listOf("بے شک", "انسان", "یقیناً میں ہے", "نقصان"),
        listOf("سوائے", "ان لوگوں کے جو", "ایمان لائے", "اور عمل کیے", "نیک", "اور ایک دوسرے کو نصیحت کی", "حق کی",
            "اور ایک دوسرے کو نصیحت کی", "صبر کی"),
    ))
    val falaq = WbwSurah(113, "سورۃ الفلق", wbw(QaidaStage4.falaq.ayat,
        listOf("کہہ دیجیے", "میں پناہ مانگتا ہوں", "رب کی", "صبح کے"),
        listOf("سے", "برائی", "اس کی جو", "اس نے پیدا کیا"),
        listOf("اور سے", "برائی", "اندھیرے کی", "جب", "وہ چھا جائے"),
        listOf("اور سے", "برائی", "پھونکنے والیوں کی", "میں", "گرہوں"),
        listOf("اور سے", "برائی", "حسد کرنے والے کی", "جب", "وہ حسد کرے"),
    ))
    val nas = WbwSurah(114, "سورۃ الناس", wbw(QaidaStage4.nas.ayat,
        listOf("کہہ دیجیے", "میں پناہ مانگتا ہوں", "رب کی", "لوگوں کے"),
        listOf("بادشاہ", "لوگوں کے"),
        listOf("معبود", "لوگوں کے"),
        listOf("سے", "برائی", "وسوسہ ڈالنے والے کی", "جو پیچھے ہٹ جاتا ہے"),
        listOf("جو", "وسوسہ ڈالتا ہے", "میں", "سینوں", "لوگوں کے"),
        listOf("سے", "جنوں", "اور انسانوں"),
    ))

    val wbwSurahs = listOf(fatiha, ikhlas, kawthar, asr, falaq, nas)
}

/** Stage 5 quiz: word → meaning, meaning → word, and pronoun endings. */
object QaidaStage5Quiz {
    fun generate(random: kotlin.random.Random, length: Int = QaidaProgress.QUIZ_LENGTH): List<QaidaQuestion> {
        val words = QaidaStage5.allWords
        return List(length) { i ->
            when (i % 3) {
                0 -> {
                    val w = words.random(random)
                    val opts = (words.filter { it.urdu != w.urdu }.shuffled(random).take(3).map { it.urdu } + w.urdu).shuffled(random)
                    QaidaQuestion(w.arabic, true, "اس لفظ کا مطلب کیا ہے؟", opts, false, opts.indexOf(w.urdu), w.audioKey)
                }
                1 -> {
                    val w = words.random(random)
                    val opts = (words.filter { it.arabic != w.arabic }.shuffled(random).take(3).map { it.arabic } + w.arabic).shuffled(random)
                    QaidaQuestion(w.urdu, false, "عربی میں کیا کہتے ہیں؟", opts, true, opts.indexOf(w.arabic), null)
                }
                else -> {
                    val e = QaidaStage5.endings.random(random)
                    val opts = (QaidaStage5.endings.filter { it != e }.shuffled(random).take(3) + e).shuffled(random).map { it.exampleUrdu }
                    QaidaQuestion(e.example, true, "اس کا مطلب کیا ہے؟", opts, false, opts.indexOf(e.exampleUrdu), e.audioKey)
                }
            }
        }
    }
}
