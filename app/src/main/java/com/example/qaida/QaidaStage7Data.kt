package com.example.qaida

/*
 * Stage 7 — tajweed of noon sakin, tanween and meem sakin (the closing boards of the Noorani Qaida).
 * Examples are short phrases, most of them from surahs the child already read in Stages 3–6.
 * Rule names and letter groups follow the standard Hafs teaching used in IndoPak madaris; a qari must review.
 */
object QaidaStage7 {

    enum class Rule(val urdu: String, val short: String, val how: String) {
        IZHAR("اظہار", "صاف پڑھنا",
            "نون ساکن یا تنوین کے بعد حلق کا حرف ہو تو نون کو صاف، بغیر غنّہ کے پڑھیں۔"),
        IDGHAM_GHUNNA("ادغام مع الغنّہ", "ملا کر، غنّہ کے ساتھ",
            "بعد میں ی ن م و ہو تو نون اگلے حرف میں مل جاتا ہے اور دو حرکت غنّہ ہوتا ہے۔"),
        IDGHAM_NO_GHUNNA("ادغام بلا غنّہ", "ملا کر، غنّہ کے بغیر",
            "بعد میں ل یا ر ہو تو نون پوری طرح مل جاتا ہے، غنّہ نہیں ہوتا۔"),
        IQLAB("اقلاب", "نون کو میم سے بدلنا",
            "بعد میں ب ہو تو نون کو میم کی آواز سے بدل کر غنّہ کریں۔ انڈو پاک قرآن میں اوپر چھوٹا م لکھا ہوتا ہے۔"),
        IKHFA("اخفاء", "چھپا کر، غنّہ کے ساتھ",
            "باقی ۱۵ حروف سے پہلے نون کو ناک میں چھپا کر غنّہ کے ساتھ پڑھیں — نہ پورا صاف، نہ پورا ملا ہوا۔"),
    }

    /** Which rule a noon sakin / tanween gets, by the letter after it (alif never follows directly). */
    val izharIds = listOf("hamza", "ha", "ain", "ha_heavy", "ghain", "kha")
    val idghamGhunnaIds = listOf("ya", "nun", "mim", "waw")
    val idghamNoGhunnaIds = listOf("lam", "ra")
    val iqlabIds = listOf("ba")
    val ikhfaIds = listOf("ta", "tha", "jim", "dal", "dhal", "zay", "sin", "shin", "sad", "dad", "ta_heavy", "dha_heavy", "fa", "qaf", "kaf")

    fun ruleFor(letterId: String): Rule = when (letterId) {
        in izharIds -> Rule.IZHAR
        in idghamGhunnaIds -> Rule.IDGHAM_GHUNNA
        in idghamNoGhunnaIds -> Rule.IDGHAM_NO_GHUNNA
        in iqlabIds -> Rule.IQLAB
        in ikhfaIds -> Rule.IKHFA
        else -> error("no noon rule before $letterId")
    }

    data class Example(val phrase: String, val nextLetterId: String, val note: String, val audioKey: String, val from: String = "") {
        val rule get() = ruleFor(nextLetterId)
    }

    val examples = listOf(
        // Izhar
        Example("اَنْعَمْتَ", "ain", "نْ کے بعد ع", "s7_izhar_anamta", "الفاتحہ"),
        Example("مِنْ خَوْفٍ", "kha", "نْ کے بعد خ", "s7_izhar_khawf", "قریش"),
        Example("كُفُوًا اَحَدٌ", "hamza", "تنوین کے بعد ء (ا)", "s7_izhar_ahad", "الاخلاص"),
        Example("مَنْ اٰمَنَ", "hamza", "نْ کے بعد ء (ا)", "s7_izhar_amana"),
        Example("عَلِيْمٌ حَكِيْمٌ", "ha_heavy", "تنوین کے بعد ح", "s7_izhar_hakim"),
        // Idgham with ghunna
        Example("مِنْ مَّسَدٍ", "mim", "نْ کے بعد م", "s7_idg_masad", "المسد"),
        Example("لَهَبٍ وَّتَبَّ", "waw", "تنوین کے بعد و", "s7_idg_tabba", "المسد"),
        Example("خَيْرًا يَّرَهٗ", "ya", "تنوین کے بعد ی", "s7_idg_yarah", "الزلزال"),
        Example("مِنْ نَّارٍ", "nun", "نْ کے بعد ن", "s7_idg_nar"),
        // Idgham without ghunna
        Example("يَكُنْ لَّهٗ", "lam", "نْ کے بعد ل", "s7_idgl_lahu", "الاخلاص"),
        Example("غَفُوْرٌ رَّحِيْمٌ", "ra", "تنوین کے بعد ر", "s7_idgl_rahim"),
        Example("مِنْ رَّبِّهِمْ", "ra", "نْ کے بعد ر", "s7_idgl_rabbihim"),
        // Iqlab (small meem ۢ above)
        Example("مِنْۢ بَعْدِ", "ba", "نْ کے بعد ب", "s7_iqlab_bad"),
        Example("سَمِيْعٌۢ بَصِيْرٌ", "ba", "تنوین کے بعد ب", "s7_iqlab_basir"),
        Example("اَنْۢبِئْهُمْ", "ba", "ایک لفظ میں نْ کے بعد ب", "s7_iqlab_anbihum"),
        // Ikhfa
        Example("مِنْ شَرِّ", "shin", "نْ کے بعد ش", "s7_ikhfa_sharr", "الفلق"),
        Example("اَنْتُمْ", "ta", "نْ کے بعد ت", "s7_ikhfa_antum", "الکافرون"),
        Example("مِنْ جُوْعٍ", "jim", "نْ کے بعد ج", "s7_ikhfa_ju", "قریش"),
        Example("يُنْفِقُوْنَ", "fa", "نْ کے بعد ف", "s7_ikhfa_yunfiquna"),
        Example("كُنْتُمْ", "ta", "نْ کے بعد ت", "s7_ikhfa_kuntum"),
        Example("حَبْلٌ مِّنْ مَّسَدٍ", "mim", "تنوین کے بعد م", "s7_idg_habl", "المسد"),
    )

    fun examplesFor(r: Rule) = examples.filter { it.rule == r }

    // ---- Meem sakin ------------------------------------------------------------------------------------
    enum class MeemRule(val urdu: String, val how: String, val letters: String) {
        IKHFA_SHAFAWI("اخفائے شفوی", "مْ کے بعد ب ہو تو میم کو ہونٹوں میں ہلکا چھپا کر غنّہ کریں۔", "ب"),
        IDGHAM_SHAFAWI("ادغامِ شفوی", "مْ کے بعد م ہو تو دونوں میم ملا کر غنّہ کے ساتھ پڑھیں۔", "م"),
        IZHAR_SHAFAWI("اظہارِ شفوی", "باقی سب حروف سے پہلے میم کو صاف پڑھیں — خاص طور پر و اور ف سے پہلے۔", "باقی حروف"),
    }

    data class MeemExample(val phrase: String, val rule: MeemRule, val note: String, val audioKey: String, val from: String = "")

    val meemExamples = listOf(
        MeemExample("تَرْمِيْهِمْ بِحِجَارَةٍ", MeemRule.IKHFA_SHAFAWI, "مْ کے بعد ب", "s7_meem_hijarah", "الفیل"),
        MeemExample("وَهُمْ بِالْاٰخِرَةِ", MeemRule.IKHFA_SHAFAWI, "مْ کے بعد ب", "s7_meem_akhirah"),
        MeemExample("لَهُمْ مَّا", MeemRule.IDGHAM_SHAFAWI, "مْ کے بعد م", "s7_meem_lahum"),
        MeemExample("كَمْ مِّنْ", MeemRule.IDGHAM_SHAFAWI, "مْ کے بعد م", "s7_meem_kam"),
        MeemExample("اَلَمْ تَرَ", MeemRule.IZHAR_SHAFAWI, "مْ کے بعد ت", "s7_meem_alam", "الفیل"),
        MeemExample("لَكُمْ دِيْنُكُمْ", MeemRule.IZHAR_SHAFAWI, "مْ کے بعد د", "s7_meem_lakum", "الکافرون"),
        MeemExample("عَلَيْهِمْ وَلَا", MeemRule.IZHAR_SHAFAWI, "مْ کے بعد و — یہاں خاص خیال", "s7_meem_alayhim", "الفاتحہ"),
        MeemExample("هُمْ فِيْهَا", MeemRule.IZHAR_SHAFAWI, "مْ کے بعد ف — یہاں خاص خیال", "s7_meem_fiha"),
    )
}

/** Stage 7 quiz: name the noon rule of a phrase, of a following letter, or the meem rule. */
object QaidaStage7Quiz {
    private val noonRules = QaidaStage7.Rule.values().map { it.urdu }

    fun generate(random: kotlin.random.Random, length: Int = QaidaProgress.QUIZ_LENGTH): List<QaidaQuestion> =
        List(length) { i ->
            when (i % 3) {
                0 -> {
                    val e = QaidaStage7.examples.random(random)
                    val opts = (noonRules.filter { it != e.rule.urdu }.shuffled(random).take(3) + e.rule.urdu).shuffled(random)
                    QaidaQuestion(e.phrase, true, "یہاں نون ساکن / تنوین کا کون سا قاعدہ ہے؟", opts, false, opts.indexOf(e.rule.urdu), e.audioKey)
                }
                1 -> {
                    val l = QaidaData.letters.filter { it.id != "alif" }.random(random)
                    val r = QaidaStage7.ruleFor(l.id)
                    val opts = (noonRules.filter { it != r.urdu }.shuffled(random).take(3) + r.urdu).shuffled(random)
                    QaidaQuestion("نْ + " + l.arabic, true, "نون ساکن کے بعد یہ حرف ہو تو کیا پڑھیں گے؟", opts, false, opts.indexOf(r.urdu), null)
                }
                else -> {
                    val m = QaidaStage7.meemExamples.random(random)
                    val opts = QaidaStage7.MeemRule.values().map { it.urdu }
                    QaidaQuestion(m.phrase, true, "یہاں میم ساکن کا کون سا قاعدہ ہے؟", opts, false, m.rule.ordinal, m.audioKey)
                }
            }
        }
}
