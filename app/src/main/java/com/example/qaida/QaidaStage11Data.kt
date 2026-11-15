package com.example.qaida

/*
 * Stage 11 — first steps in Quranic grammar for Urdu speakers.
 * Urdu already uses many Quranic words (علم، عالم، معلوم، کتاب، مسجد), so the stage starts from roots
 * and patterns the child half-knows, then verbs with the prefixes/endings met in the surahs read so far.
 * Teaching simplifications are deliberate; a teacher should review the wording.
 */
object QaidaStage11 {

    data class Word(val arabic: String, val urdu: String, val note: String = "")

    // ---- 11.1 Roots ------------------------------------------------------------------------------------
    data class Root(val id: String, val letters: String, val idea: String, val words: List<Word>)

    val roots = listOf(
        Root("alm", "ع ل م", "جاننا", listOf(
            Word("عِلْمٌ", "علم، جاننا", "اردو میں بھی: علم"), Word("عَالِمٌ", "جاننے والا", "اردو میں: عالم"),
            Word("مَعْلُوْمٌ", "جانا ہوا", "اردو میں: معلوم"), Word("عَلِيْمٌ", "سب جاننے والا"),
            Word("عَلَّمَ", "اس نے سکھایا", "العلق میں آیا"), Word("يَعْلَمُ", "وہ جانتا ہے", "الاعلیٰ میں آیا"))),
        Root("ktb", "ك ت ب", "لکھنا", listOf(
            Word("كَتَبَ", "اس نے لکھا"), Word("كِتٰبٌ", "کتاب، لکھی ہوئی چیز", "اردو میں: کتاب"),
            Word("كَاتِبٌ", "لکھنے والا", "اردو میں: کاتب"), Word("مَكْتُوْبٌ", "لکھا ہوا", "اردو میں: مکتوب"),
            Word("كُتُبٌ", "کتابیں", "البینہ میں آیا"))),
        Root("sjd", "س ج د", "سجدہ کرنا", listOf(
            Word("سَجَدَ", "اس نے سجدہ کیا"), Word("سُجُوْدٌ", "سجدے"), Word("سَاجِدٌ", "سجدہ کرنے والا"),
            Word("مَسْجِدٌ", "سجدے کی جگہ", "اردو میں: مسجد"), Word("اسْجُدْ", "سجدہ کر", "العلق میں آیا"))),
        Root("khlq", "خ ل ق", "پیدا کرنا", listOf(
            Word("خَلَقَ", "اس نے پیدا کیا", "العلق میں آیا"), Word("خَالِقٌ", "پیدا کرنے والا", "اردو میں: خالق"),
            Word("مَخْلُوْقٌ", "پیدا کیا ہوا", "اردو میں: مخلوق"), Word("خَلْقٌ", "پیدا کرنا، بناوٹ"))),
        Root("abd", "ع ب د", "عبادت کرنا", listOf(
            Word("عَبْدٌ", "بندہ", "اردو میں: عبد"), Word("عِبَادَةٌ", "عبادت"), Word("مَعْبُوْدٌ", "جس کی عبادت ہو"),
            Word("عَابِدٌ", "عبادت کرنے والا"), Word("نَعْبُدُ", "ہم عبادت کرتے ہیں", "الفاتحہ میں آیا"))),
        Root("rhm", "ر ح م", "رحم کرنا", listOf(
            Word("رَحْمَةٌ", "رحمت", "اردو میں: رحمت"), Word("رَحِيْمٌ", "بہت رحم والا"),
            Word("الرَّحْمٰنُ", "بے حد مہربان", "صرف اللہ کا نام"), Word("الْمَرْحَمَةُ", "رحم دلی", "البلد میں آیا"))),
        Root("dhkr", "ذ ك ر", "یاد کرنا", listOf(
            Word("ذِكْرٌ", "یاد، نصیحت", "اردو میں: ذکر"), Word("ذَاكِرٌ", "یاد کرنے والا"),
            Word("تَذْكِرَةٌ", "نصیحت کی بات", "عبس میں آیا"), Word("فَذَكِّرْ", "تو نصیحت کیجیے", "الغاشیہ میں آیا"))),
    )

    // ---- 11.2 Patterns on ف ع ل -------------------------------------------------------------------------
    data class Pattern(val id: String, val pattern: String, val meaning: String, val examples: List<Word>)

    val patterns = listOf(
        Pattern("faail", "فَاعِلٌ", "کام کرنے والا", listOf(
            Word("عَالِمٌ", "جاننے والا"), Word("كَاتِبٌ", "لکھنے والا"), Word("سَاجِدٌ", "سجدہ کرنے والا"), Word("خَالِقٌ", "پیدا کرنے والا"))),
        Pattern("mafool", "مَفْعُوْلٌ", "جس پر کام ہوا", listOf(
            Word("مَعْلُوْمٌ", "جانا ہوا"), Word("مَكْتُوْبٌ", "لکھا ہوا"), Word("مَخْلُوْقٌ", "پیدا کیا ہوا"), Word("مَعْبُوْدٌ", "جس کی عبادت ہو"))),
        Pattern("faeel", "فَعِيْلٌ", "بہت زیادہ، ہمیشہ والی صفت", listOf(
            Word("عَلِيْمٌ", "سب جاننے والا"), Word("رَحِيْمٌ", "بہت رحم والا"), Word("سَمِيْعٌ", "سب سننے والا"), Word("بَصِيْرٌ", "سب دیکھنے والا"))),
        Pattern("mafal", "مَفْعَلٌ / مَفْعِلٌ", "کام کی جگہ یا وقت", listOf(
            Word("مَسْجِدٌ", "سجدے کی جگہ"), Word("مَكْتَبٌ", "لکھنے کی جگہ"), Word("مَطْلَعٌ", "نکلنے کا وقت", "القدر: مَطْلَعِ الْفَجْرِ"), Word("مَنْزِلٌ", "اترنے کی جگہ، گھر"))),
    )

    // ---- 11.3 Past tense: who did it ---------------------------------------------------------------------
    data class Form(val ending: String, val who: String, val example: String, val exampleUrdu: String, val from: String)

    val past = listOf(
        Form("فَعَلَ", "اس (ایک مرد) نے کیا", "خَلَقَ", "اس نے پیدا کیا", "العلق"),
        Form("فَعَلُوْا", "انہوں نے کیا", "اٰمَنُوْا", "وہ ایمان لائے", "العصر"),
        Form("فَعَلَتْ", "اس (عورت / قوم) نے کیا", "كَذَّبَتْ", "اس نے جھٹلایا", "الشمس"),
        Form("فَعَلْتَ", "تو نے کیا", "اَنْعَمْتَ", "تو نے انعام کیا", "الفاتحہ"),
        Form("فَعَلْتُ", "میں نے کیا", "قَدَّمْتُ", "میں نے آگے بھیجا", "الفجر"),
        Form("فَعَلْنَا", "ہم نے کیا", "خَلَقْنَا", "ہم نے پیدا کیا", "التین"),
    )

    // ---- 11.4 Present tense: the letter in front tells who --------------------------------------------------
    val present = listOf(
        Form("يَفْعَلُ", "وہ (ایک مرد) کرتا ہے", "يَعْلَمُ", "وہ جانتا ہے", "الاعلیٰ"),
        Form("تَفْعَلُ", "تو کرتا ہے / وہ (عورت) کرتی ہے", "تَعْلَمُ", "تو جانتا ہے", ""),
        Form("اَفْعَلُ", "میں کرتا ہوں", "اَعْبُدُ", "میں عبادت کرتا ہوں", "الکافرون"),
        Form("نَفْعَلُ", "ہم کرتے ہیں", "نَعْبُدُ", "ہم عبادت کرتے ہیں", "الفاتحہ"),
        Form("يَفْعَلُوْنَ", "وہ سب کرتے ہیں", "يَعْلَمُوْنَ", "وہ جانتے ہیں", "الانفطار"),
        Form("تَفْعَلُوْنَ", "تم سب کرتے ہو", "تَعْبُدُوْنَ", "تم عبادت کرتے ہو", "الکافرون"),
    )

    // ---- 11.5 Commands and "not" ------------------------------------------------------------------------------
    val commands = listOf(
        Word("قُلْ", "کہو", "الاخلاص، الفلق، الناس"), Word("اقْرَاْ", "پڑھو", "العلق"), Word("اسْجُدْ", "سجدہ کرو", "العلق"),
        Word("فَسَبِّحْ", "تو تسبیح کرو", "النصر"), Word("ادْخُلِيْ", "داخل ہو جا", "الفجر"),
    )
    val negatives = listOf(
        Word("لَا اَعْبُدُ", "میں عبادت نہیں کرتا", "لَا + حال = نہیں کرتا"),
        Word("لَمْ يَلِدْ", "اس نے نہیں جنا", "لَمْ + مضارع = ماضی میں نہیں کیا"),
        Word("مَا وَدَّعَكَ", "اس نے آپ کو نہیں چھوڑا", "مَا + ماضی = نہیں کیا"),
        Word("لَنْ يَقْدِرَ", "وہ ہرگز قابو نہ پائے گا", "لَنْ + مضارع = ہرگز نہیں کرے گا"),
    )

    // ---- 11.6 Root detective --------------------------------------------------------------------------------
    data class Detective(val word: String, val rootId: String, val urdu: String)

    val detective = listOf(
        Detective("مَسْجِدٌ", "sjd", "مسجد"), Detective("يَعْلَمُوْنَ", "alm", "وہ جانتے ہیں"),
        Detective("الْكِتٰبِ", "ktb", "کتاب"), Detective("مَخْلُوْقٌ", "khlq", "پیدا کیا ہوا"),
        Detective("عِبَادِيْ", "abd", "میرے بندے"), Detective("الرَّحْمٰنِ", "rhm", "بے حد مہربان (اللہ)"),
        Detective("الذِّكْرٰى", "dhkr", "نصیحت"), Detective("عَلَّمَ", "alm", "اس نے سکھایا"),
        Detective("سَاجِدِيْنَ", "sjd", "سجدہ کرنے والے"), Detective("خَلَقْنَا", "khlq", "ہم نے پیدا کیا"),
    )

    fun root(id: String) = roots.first { it.id == id }
}

/** Stage 11 quiz: find the root, name the pattern, say who does the verb. */
object QaidaStage11Quiz {
    fun generate(random: kotlin.random.Random, length: Int = QaidaProgress.QUIZ_LENGTH): List<QaidaQuestion> =
        List(length) { i ->
            when (i % 3) {
                0 -> {
                    val d = QaidaStage11.detective.random(random)
                    val right = QaidaStage11.root(d.rootId).letters
                    val opts = (QaidaStage11.roots.map { it.letters }.filter { it != right }.shuffled(random).take(3) + right).shuffled(random)
                    QaidaQuestion(d.word, true, "اس لفظ کا مادہ (جڑ) کیا ہے؟", opts, true, opts.indexOf(right), null)
                }
                1 -> {
                    val p = QaidaStage11.patterns.random(random)
                    val w = p.examples.random(random)
                    val opts = (QaidaStage11.patterns.filter { it != p }.shuffled(random).take(3).map { it.meaning } + p.meaning).shuffled(random)
                    QaidaQuestion(w.arabic, true, "یہ لفظ کس قسم کا ہے؟", opts, false, opts.indexOf(p.meaning), null)
                }
                else -> {
                    val all = QaidaStage11.past + QaidaStage11.present
                    val f = all.random(random)
                    val opts = (all.filter { it.who != f.who }.map { it.who }.distinct().shuffled(random).take(3) + f.who).shuffled(random)
                    QaidaQuestion(f.example, true, "یہ کام کون کر رہا ہے؟", opts, false, opts.indexOf(f.who), null)
                }
            }
        }
}
