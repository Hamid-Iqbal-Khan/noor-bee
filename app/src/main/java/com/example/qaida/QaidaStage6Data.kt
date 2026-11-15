package com.example.qaida

/*
 * Stage 6 — the remaining short surahs of Juz 'Amma (Al-Fil to Al-Masad), word by word in Urdu,
 * plus a flashcard practice of every word met in Stages 5–6.
 * Text is IndoPak style without waqf marks. These surahs are not in the app's offline Quran, so a CI
 * test compares their letters with the Tanzil "simple-clean" text served by api.alquran.cloud.
 * Meanings are simple Urdu for children and need a teacher's review.
 */
object QaidaStage6 {

    private fun ayah(s: Int, n: Int, t: String, r: String = "") = QaidaStage3.Ayah(s, n, t, r)
    private fun surah(n: Int, name: String, vararg a: Pair<String, List<String>>) =
        QaidaStage5.WbwSurah(n, name, a.mapIndexed { i, (t, m) -> QaidaStage5.WbwAyah(ayah(n, i + 1, t), m) })

    val fil = surah(105, "سورۃ الفیل",
        "اَلَمْ تَرَ كَيْفَ فَعَلَ رَبُّكَ بِاَصْحٰبِ الْفِيْلِ" to listOf("کیا نہیں", "آپ نے دیکھا", "کیسا", "سلوک کیا", "آپ کے رب نے", "والوں کے ساتھ", "ہاتھی"),
        "اَلَمْ يَجْعَلْ كَيْدَهُمْ فِيْ تَضْلِيْلٍ" to listOf("کیا نہیں", "کر دیا", "ان کی چال کو", "میں", "ناکامی"),
        "وَّاَرْسَلَ عَلَيْهِمْ طَيْرًا اَبَابِيْلَ" to listOf("اور بھیجے", "ان پر", "پرندے", "جھنڈ کے جھنڈ"),
        "تَرْمِيْهِمْ بِحِجَارَةٍ مِّنْ سِجِّيْلٍ" to listOf("جو انہیں مارتے تھے", "پتھروں سے", "کے", "پکی مٹی"),
        "فَجَعَلَهُمْ كَعَصْفٍ مَّاْكُوْلٍ" to listOf("پھر انہیں کر دیا", "بھوسے کی طرح", "کھایا ہوا"),
    )
    val quraysh = surah(106, "سورۃ قریش",
        "لِاِيْلٰفِ قُرَيْشٍ" to listOf("مانوس کرنے کی وجہ سے", "قریش کو"),
        "اٖلٰفِهِمْ رِحْلَةَ الشِّتَآءِ وَالصَّيْفِ" to listOf("انہیں مانوس کرنا", "سفر سے", "سردی کے", "اور گرمی کے"),
        "فَلْيَعْبُدُوْا رَبَّ هٰذَا الْبَيْتِ" to listOf("تو وہ عبادت کریں", "رب کی", "اس", "گھر کے"),
        "الَّذِيْٓ اَطْعَمَهُمْ مِّنْ جُوْعٍ وَّاٰمَنَهُمْ مِّنْ خَوْفٍ" to listOf("جس نے", "انہیں کھانا دیا", "میں", "بھوک", "اور انہیں امن دیا", "سے", "ڈر"),
    )
    val maun = surah(107, "سورۃ الماعون",
        "اَرَءَيْتَ الَّذِيْ يُكَذِّبُ بِالدِّيْنِ" to listOf("کیا آپ نے دیکھا", "اسے جو", "جھٹلاتا ہے", "روزِ جزا کو"),
        "فَذٰلِكَ الَّذِيْ يَدُعُّ الْيَتِيْمَ" to listOf("تو یہی ہے", "جو", "دھکے دیتا ہے", "یتیم کو"),
        "وَلَا يَحُضُّ عَلٰى طَعَامِ الْمِسْكِيْنِ" to listOf("اور نہیں", "ترغیب دیتا", "پر", "کھانا کھلانے", "مسکین کو"),
        "فَوَيْلٌ لِّلْمُصَلِّيْنَ" to listOf("تو خرابی ہے", "ان نمازیوں کے لیے"),
        "الَّذِيْنَ هُمْ عَنْ صَلَاتِهِمْ سَاهُوْنَ" to listOf("جو", "وہ", "سے", "اپنی نماز", "غافل ہیں"),
        "الَّذِيْنَ هُمْ يُرَآءُوْنَ" to listOf("جو", "وہ", "دکھاوا کرتے ہیں"),
        "وَيَمْنَعُوْنَ الْمَاعُوْنَ" to listOf("اور روکتے ہیں", "برتنے کی چھوٹی چیزیں"),
    )
    val kafirun = surah(109, "سورۃ الکافرون",
        "قُلْ يٰٓاَيُّهَا الْكٰفِرُوْنَ" to listOf("کہہ دیجیے", "اے", "کافرو"),
        "لَآ اَعْبُدُ مَا تَعْبُدُوْنَ" to listOf("نہیں", "میں عبادت کرتا", "جس کی", "تم عبادت کرتے ہو"),
        "وَلَآ اَنْتُمْ عٰبِدُوْنَ مَآ اَعْبُدُ" to listOf("اور نہ", "تم", "عبادت کرنے والے ہو", "جس کی", "میں عبادت کرتا ہوں"),
        "وَلَآ اَنَا عَابِدٌ مَّا عَبَدْتُّمْ" to listOf("اور نہ", "میں", "عبادت کرنے والا ہوں", "جس کی", "تم نے عبادت کی"),
        "وَلَآ اَنْتُمْ عٰبِدُوْنَ مَآ اَعْبُدُ" to listOf("اور نہ", "تم", "عبادت کرنے والے ہو", "جس کی", "میں عبادت کرتا ہوں"),
        "لَكُمْ دِيْنُكُمْ وَلِيَ دِيْنِ" to listOf("تمہارے لیے", "تمہارا دین", "اور میرے لیے", "میرا دین"),
    )
    val nasr = surah(110, "سورۃ النصر",
        "اِذَا جَآءَ نَصْرُ اللّٰهِ وَالْفَتْحُ" to listOf("جب", "آ جائے", "مدد", "اللہ کی", "اور فتح"),
        "وَرَاَيْتَ النَّاسَ يَدْخُلُوْنَ فِيْ دِيْنِ اللّٰهِ اَفْوَاجًا" to listOf("اور آپ دیکھ لیں", "لوگوں کو", "داخل ہوتے", "میں", "دین", "اللہ کے", "فوج در فوج"),
        "فَسَبِّحْ بِحَمْدِ رَبِّكَ وَاسْتَغْفِرْهُ اِنَّهٗ كَانَ تَوَّابًا" to listOf("تو تسبیح کیجیے", "تعریف کے ساتھ", "اپنے رب کی", "اور اس سے بخشش مانگیے", "بے شک وہ", "ہے", "بہت توبہ قبول کرنے والا"),
    )
    val masad = surah(111, "سورۃ المسد",
        "تَبَّتْ يَدَآ اَبِيْ لَهَبٍ وَّتَبَّ" to listOf("ٹوٹ گئے", "دونوں ہاتھ", "ابو", "لہب کے", "اور وہ برباد ہوا"),
        "مَآ اَغْنٰى عَنْهُ مَالُهٗ وَمَا كَسَبَ" to listOf("نہ", "کام آیا", "اس کے", "اس کا مال", "اور جو", "اس نے کمایا"),
        "سَيَصْلٰى نَارًا ذَاتَ لَهَبٍ" to listOf("وہ جلد داخل ہوگا", "آگ میں", "والی", "شعلوں"),
        "وَّامْرَاَتُهٗ حَمَّالَةَ الْحَطَبِ" to listOf("اور اس کی بیوی بھی", "اٹھانے والی", "لکڑیاں"),
        "فِيْ جِيْدِهَا حَبْلٌ مِّنْ مَّسَدٍ" to listOf("میں", "اس کی گردن", "رسی ہے", "کی", "بٹی ہوئی مونج"),
    )

    val surahs = listOf(fil, quraysh, maun, kafirun, nasr, masad)

    // ---- Flashcards: every word met in Stages 5 and 6 ------------------------------------------------
    data class Card(val arabic: String, val urdu: String, val audioKey: String)

    /** Distinct (word, meaning) pairs from the frequent-words list and all word-by-word surahs. */
    val flashcards: List<Card> by lazy {
        val fromWords = QaidaStage5.allWords.map { Card(it.arabic, it.urdu, it.audioKey) }
        val fromSurahs = (QaidaStage5.wbwSurahs + surahs).flatMap { s ->
            s.ayat.flatMap { a -> a.words.mapIndexed { i, w -> Card(w, a.meanings[i], a.wordKey(i)) } }
        }
        (fromWords + fromSurahs).distinctBy { it.arabic }
    }
}

/** Stage 6 quiz: meaning of a word from the six new surahs, and which surah an ayah opens. */
object QaidaStage6Quiz {
    fun generate(random: kotlin.random.Random, length: Int = QaidaProgress.QUIZ_LENGTH): List<QaidaQuestion> {
        val cards = QaidaStage6.surahs.flatMap { s -> s.ayat.flatMap { a -> a.words.mapIndexed { i, w -> Triple(w, a.meanings[i], a.wordKey(i)) } } }
            .distinctBy { it.second }
        return List(length) { i ->
            if (i % 3 == 2) {
                val s = QaidaStage6.surahs.random(random)
                val names = (QaidaStage6.surahs.filter { it != s }.shuffled(random).take(3) + s).shuffled(random).map { it.urduName }
                val first = s.ayat.first().ayah
                QaidaQuestion(first.text, true, "یہ کس سورت کی پہلی آیت ہے؟", names, false, names.indexOf(s.urduName), first.audioKey)
            } else {
                val (w, m, key) = cards.random(random)
                val opts = (cards.filter { it.second != m }.shuffled(random).take(3).map { it.second } + m).shuffled(random)
                QaidaQuestion(w, true, "اس لفظ کا مطلب کیا ہے؟", opts, false, opts.indexOf(m), key)
            }
        }
    }
}
