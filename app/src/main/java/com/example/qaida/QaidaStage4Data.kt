package com.example.qaida

/*
 * Noorani Qaida — Stage 4 for Urdu-speaking learners (IndoPak conventions):
 * how to stop (waqf), the IndoPak stop signs, qalqalah, then short surahs from Juz 'Amma.
 * Ayat are typed IndoPak-style without waqf marks; a unit test checks their letters against the
 * app's bundled Quran text. Waqf-sign meanings follow common IndoPak mushaf notes and need a teacher's review.
 */
object QaidaStage4 {

    private const val SUKUN = 'ْ'
    private val shortVowels = setOf('َ', 'ُ', 'ِ', 'ٌ', 'ٍ')

    /**
     * How a word is read when you stop on it:
     *  - a short vowel or do pesh / do zer on the last letter becomes jazm: اَحَدٌ → اَحَدْ
     *  - do zabar becomes a zabar with alif: عَلِيْمًا → عَلِيْمَا
     *  - gol ta (ة) is read as ہ with jazm: جَنَّةٌ → جَنَّهْ
     */
    fun stopForm(word: String): String {
        if (word.endsWith("ًا")) return word.dropLast(2) + "َا"
        val core = word.trimEnd { it in shortVowels }
        if (core.endsWith('ة')) return core.dropLast(1) + "ه" + SUKUN
        if (core.endsWith(SUKUN) || core.endsWith('\u0651')) return core  // already still, or a doubled letter
        return core + SUKUN
    }

    fun stopOnLastWord(ayah: String): String = stopForm(ayah.trim().substringAfterLast(' '))

    data class StopExample(val word: String, val urdu: String, val audioKey: String) {
        val stopped get() = stopForm(word)
    }

    val stopExamples = listOf(
        StopExample("اَحَدٌ", "دو پیش ← جزم", "s4_stop_ahad"),
        StopExample("الصَّمَدُ", "پیش ← جزم", "s4_stop_samad"),
        StopExample("الرَّحِيْمِ", "زیر ← جزم", "s4_stop_rahim"),
        StopExample("الْفَلَقِ", "زیر ← جزم", "s4_stop_falaq"),
        StopExample("عَلِيْمًا", "دو زبر ← زبر اور الف", "s4_stop_alima"),
        StopExample("خَبِيْرًا", "دو زبر ← زبر اور الف", "s4_stop_khabira"),
        StopExample("جَنَّةٌ", "گول ة ← ہ ساکن", "s4_stop_jannah"),
        StopExample("رَحْمَةً", "گول ة ← ہ ساکن", "s4_stop_rahmah"),
    )

    // ---- Waqf signs (رموزِ اوقاف) --------------------------------------------------------------
    data class WaqfSign(val id: String, val sign: String, val name: String, val rule: String, val stop: Stop)
    enum class Stop(val urdu: String) { MUST("ضرور رکیں"), BETTER_STOP("رکنا بہتر"), EITHER("رکیں یا ملائیں"), BETTER_JOIN("ملانا بہتر"), DONT("نہ رکیں") }

    val waqfSigns = listOf(
        WaqfSign("ayah", "۝", "گول دائرہ — آیت", "آیت ختم۔ یہاں رکیں اور سانس لیں۔", Stop.BETTER_STOP),
        WaqfSign("lazim", "م", "وقفِ لازم", "یہاں ضرور رکیں، ورنہ معنی بگڑ سکتا ہے۔", Stop.MUST),
        WaqfSign("mutlaq", "ط", "وقفِ مطلق", "یہاں رکنا بہتر ہے۔", Stop.BETTER_STOP),
        WaqfSign("qif", "قف", "قِف", "رکنا بہتر ہے۔", Stop.BETTER_STOP),
        WaqfSign("jaiz", "ج", "وقفِ جائز", "رکنا بھی ٹھیک، ملا کر پڑھنا بھی؛ رکنا بہتر۔", Stop.EITHER),
        WaqfSign("mujawwaz", "ز", "وقفِ مجوّز", "رک سکتے ہیں، مگر ملا کر پڑھنا بہتر ہے۔", Stop.BETTER_JOIN),
        WaqfSign("murakhkhas", "ص", "وقفِ مرخّص", "سانس ٹوٹے تو رک سکتے ہیں، ورنہ ملائیں۔", Stop.BETTER_JOIN),
        WaqfSign("sala", "صلے", "صَلے", "ملا کر پڑھنا بہتر ہے۔", Stop.BETTER_JOIN),
        WaqfSign("la", "لا", "لا — نہ رکیں", "آیت کے بیچ میں ہو تو نہ رکیں؛ آیت کے نشان پر ہو تو رکنا جائز ہے۔", Stop.DONT),
        WaqfSign("muanaqa", "∴ ∴", "معانقہ", "دو جگہ تین نقطے ہوں تو ان میں سے صرف ایک جگہ رکیں۔", Stop.EITHER),
        WaqfSign("sakta", "س", "سکتہ", "سانس توڑے بغیر ذرا سی دیر رکیں، پھر آگے پڑھیں۔", Stop.EITHER),
    )

    // ---- Qalqalah ------------------------------------------------------------------------------------
    /** قُطْبُ جَدٍ — the five bouncing letters. */
    val qalqalahIds = listOf("qaf", "ta_heavy", "ba", "jim", "dal")

    data class Word(val written: String, val urdu: String, val audioKey: String)

    val qalqalahWords = listOf(
        Word("اَقْرَبُ", "زیادہ قریب — قْ", "s4_qal_aqrabu"),
        Word("اَطْعَمَهُمْ", "انہیں کھلایا — طْ", "s4_qal_atamahum"),
        Word("الْاَبْتَرُ", "جڑ کٹا — بْ", "s4_qal_abtar"),
        Word("يَجْعَلُوْنَ", "وہ بناتے ہیں — جْ", "s4_qal_yajaluna"),
        Word("يَدْخُلُوْنَ", "وہ داخل ہوتے ہیں — دْ", "s4_qal_yadkhuluna"),
        Word("الْفَلَقْ", "وقف پر — زیادہ واضح قلقلہ", "s4_qal_falaq"),
        Word("وَقَبْ", "وقف پر قلقلہ", "s4_qal_waqab"),
        Word("حَسَدْ", "وقف پر قلقلہ", "s4_qal_hasad"),
    )

    // ---- Short surahs ----------------------------------------------------------------------------------
    data class Surah(val number: Int, val urduName: String, val ayat: List<QaidaStage3.Ayah>)

    private fun a(s: Int, n: Int, t: String, r: String) = QaidaStage3.Ayah(s, n, t, r)

    val kawthar = Surah(108, "سورۃ الکوثر", listOf(
        a(108, 1, "اِنَّآ اَعْطَيْنٰكَ الْكَوْثَرَ", "اِنَّآ میں غنّہ اور مدّ؛ اَعْطَيْنٰكَ میں کھڑا زبر"),
        a(108, 2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "دو تشدید؛ آخری لفظ پہلے سے ساکن ہے"),
        a(108, 3, "اِنَّ شَانِئَكَ هُوَ الْاَبْتَرُ", "الْاَبْتَرُ میں بْ پر قلقلہ"),
    ))
    val asr = Surah(103, "سورۃ العصر", listOf(
        a(103, 1, "وَالْعَصْرِ", "لامِ قمری؛ وقف پر رْ"),
        a(103, 2, "اِنَّ الْاِنْسَانَ لَفِيْ خُسْرٍ", "دو زیر والے لفظ پر وقف: خُسْرْ"),
        a(103, 3, "اِلَّا الَّذِيْنَ اٰمَنُوْا وَعَمِلُوا الصّٰلِحٰتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ",
            "لمبی آیت: سانس ٹوٹے تو بِالْحَقِّ پر رک کر دوبارہ ملا کر پڑھیں"),
    ))
    val falaq = Surah(113, "سورۃ الفلق", listOf(
        a(113, 1, "قُلْ اَعُوْذُ بِرَبِّ الْفَلَقِ", "وقف پر قْ — قلقلہ"),
        a(113, 2, "مِنْ شَرِّ مَا خَلَقَ", "وقف پر قْ — قلقلہ"),
        a(113, 3, "وَمِنْ شَرِّ غَاسِقٍ اِذَا وَقَبَ", "وقف پر بْ — قلقلہ"),
        a(113, 4, "وَمِنْ شَرِّ النَّفّٰثٰتِ فِي الْعُقَدِ", "النَّفّٰثٰتِ میں تشدید اور دو کھڑے زبر؛ وقف پر دْ — قلقلہ"),
        a(113, 5, "وَمِنْ شَرِّ حَاسِدٍ اِذَا حَسَدَ", "وقف پر دْ — قلقلہ"),
    ))
    val nas = Surah(114, "سورۃ الناس", listOf(
        a(114, 1, "قُلْ اَعُوْذُ بِرَبِّ النَّاسِ", "النَّاسِ میں لامِ شمسی اور غنّہ"),
        a(114, 2, "مَلِكِ النَّاسِ", "ہر آیت النَّاسْ پر رکتی ہے"),
        a(114, 3, "اِلٰهِ النَّاسِ", "اِلٰهِ میں کھڑا زبر"),
        a(114, 4, "مِنْ شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "الْخَنَّاسِ میں تشدید اور غنّہ"),
        a(114, 5, "الَّذِيْ يُوَسْوِسُ فِيْ صُدُوْرِ النَّاسِ", "حروفِ مدّہ: الَّذِيْ، فِيْ، صُدُوْرِ"),
        a(114, 6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "الْجِنَّةِ میں غنّہ"),
    ))

    val surahs = listOf(kawthar, asr, falaq, nas)
}

/** Stage 4 quiz: how to stop, what a sign means, which letter bounces. */
object QaidaStage4Quiz {
    fun generate(random: kotlin.random.Random, length: Int = QaidaProgress.QUIZ_LENGTH): List<QaidaQuestion> =
        List(length) { i ->
            when (i % 3) {
                0 -> {
                    val pool = QaidaStage4.stopExamples.map { it.word to it.audioKey } +
                        QaidaStage4.surahs.flatMap { s -> s.ayat.map { it.text.substringAfterLast(' ') to it.audioKey } }
                    val (w, key) = pool.filter { QaidaStage4.stopForm(it.first) != it.first }.random(random)
                    val correct = QaidaStage4.stopForm(w)
                    val wrong = listOf(w, w.trimEnd { it in "ًٌٍَُِ" } + "ِ")
                        .filter { it != correct }.distinct()
                    val opts = (wrong + correct).shuffled(random)
                    QaidaQuestion(w, true, "اس لفظ پر رکیں تو کیسے پڑھیں گے؟", opts, true, opts.indexOf(correct), key)
                }
                1 -> {
                    val s = QaidaStage4.waqfSigns.filter { it.id != "muanaqa" }.random(random)
                    val opts = QaidaStage4.Stop.values().map { it.urdu }.shuffled(random).take(4).let {
                        if (s.stop.urdu in it) it else it.dropLast(1) + s.stop.urdu
                    }.shuffled(random)
                    QaidaQuestion(s.sign, true, "یہ وقف کا نشان کیا کہتا ہے؟", opts, false, opts.indexOf(s.stop.urdu), null)
                }
                else -> {
                    val q = QaidaData.letter(QaidaStage4.qalqalahIds.random(random))
                    val others = QaidaData.letters.filter { it.id !in QaidaStage4.qalqalahIds && it.id != "hamza" }.shuffled(random).take(3)
                    val opts = (others + q).shuffled(random)
                    QaidaQuestion("قلقلہ", false, "ان میں سے قلقلہ کا حرف کون سا ہے؟", opts.map { it.arabic }, true, opts.indexOf(q), null)
                }
            }
        }
}
