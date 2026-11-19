package com.example.arabic

import androidx.compose.ui.graphics.Color

/*
 * Arabic words, from the very first nouns to short sentences. Every word is written with full
 * harakat (and tanween where the word takes it, e.g. تُفَّاحَةٌ tuffāḥatun), with its English
 * meaning and its Urdu meaning, which is shown in brackets on both sides of the flip card.
 * Modern Standard (Fusha) Arabic, the Arabic of the Quran and of school textbooks.
 */
data class ArWord(
    val arabic: String,
    val translit: String,
    val english: String,
    val urdu: String,
    val emoji: String = "",
)

data class ArSet(val id: String, val title: String, val titleUrdu: String, val emoji: String, val words: List<ArWord>)

data class ArLevel(
    val number: Int, val title: String, val titleUrdu: String, val band: String, val bandUrdu: String,
    val bg: Color, val line: Color, val sets: List<ArSet>,
) {
    val quizKey get() = "a$number.q"
    val words get() = sets.flatMap { it.words }
}

private fun w(arabic: String, translit: String, english: String, urdu: String, emoji: String = "") =
    ArWord(arabic, translit, english, urdu, emoji)

object ArabicWords {

    val levels: List<ArLevel> = listOf(
        ArLevel(1, "First words", "پہلے الفاظ", "Beginner", "آسان", Color(0xFFFFEBEE), Color(0xFFC62828), listOf(
            ArSet("a1.1", "Fruits", "پھل", "🍎", listOf(
                w("تُفَّاحَةٌ", "tuffāḥatun", "an apple", "سیب", "🍎"),
                w("مَوْزَةٌ", "mawzatun", "a banana", "کیلا", "🍌"),
                w("بُرْتُقَالَةٌ", "burtuqālatun", "an orange", "سنگترہ", "🍊"),
                w("عِنَبٌ", "ʿinabun", "grapes", "انگور", "🍇"),
                w("تَمْرٌ", "tamrun", "dates", "کھجور", "🌴"),
                w("لَيْمُونٌ", "laymūnun", "a lemon", "لیموں", "🍋"),
                w("بِطِّيخٌ", "biṭṭīkhun", "a watermelon", "تربوز", "🍉"),
                w("كَرَزٌ", "karazun", "cherries", "چیری", "🍒"),
                w("فَرَاوِلَةٌ", "farāwilatun", "a strawberry", "اسٹرابیری", "🍓"),
                w("خَوْخٌ", "khawkhun", "a peach", "آڑو", "🍑"),
            )),
            ArSet("a1.2", "Food and drink", "کھانا پینا", "🍞", listOf(
                w("خُبْزٌ", "khubzun", "bread", "روٹی", "🍞"),
                w("مَاءٌ", "māʾun", "water", "پانی", "💧"),
                w("حَلِيبٌ", "ḥalībun", "milk", "دودھ", "🥛"),
                w("عَسَلٌ", "ʿasalun", "honey", "شہد", "🍯"),
                w("أَرُزٌّ", "aruzzun", "rice", "چاول", "🍚"),
                w("بَيْضَةٌ", "bayḍatun", "an egg", "انڈا", "🥚"),
                w("لَحْمٌ", "laḥmun", "meat", "گوشت", "🍖"),
                w("سَمَكٌ", "samakun", "fish (food)", "مچھلی", "🐟"),
                w("جُبْنٌ", "jubnun", "cheese", "پنیر", "🧀"),
                w("شَايٌ", "shāyun", "tea", "چائے", "🍵"),
            )),
            ArSet("a1.3", "Animals", "جانور", "🐪", listOf(
                w("قِطَّةٌ", "qiṭṭatun", "a cat", "بلی", "🐱"),
                w("كَلْبٌ", "kalbun", "a dog", "کتا", "🐶"),
                w("بَقَرَةٌ", "baqaratun", "a cow", "گائے", "🐄"),
                w("نَحْلَةٌ", "naḥlatun", "a bee", "شہد کی مکھی", "🐝"),
                w("أَسَدٌ", "asadun", "a lion", "شیر", "🦁"),
                w("جَمَلٌ", "jamalun", "a camel", "اونٹ", "🐪"),
                w("حِصَانٌ", "ḥiṣānun", "a horse", "گھوڑا", "🐴"),
                w("طَائِرٌ", "ṭāʾirun", "a bird", "پرندہ", "🐦"),
                w("فِيلٌ", "fīlun", "an elephant", "ہاتھی", "🐘"),
                w("خَرُوفٌ", "kharūfun", "a sheep", "بھیڑ", "🐑"),
            )),
        )),
        ArLevel(2, "Me and my family", "میں اور میرا گھرانہ", "Beginner", "آسان", Color(0xFFFFF4D6), Color(0xFFE0A800), listOf(
            ArSet("a2.1", "My family", "میرا گھرانہ", "👨‍👩‍👧", listOf(
                w("أَبٌ", "abun", "a father", "والد (ابو)", "👨"),
                w("أُمٌّ", "ummun", "a mother", "والدہ (امی)", "👩"),
                w("أَخٌ", "akhun", "a brother", "بھائی", "👦"),
                w("أُخْتٌ", "ukhtun", "a sister", "بہن", "👧"),
                w("جَدٌّ", "jaddun", "a grandfather", "دادا / نانا", "👴"),
                w("جَدَّةٌ", "jaddatun", "a grandmother", "دادی / نانی", "👵"),
                w("اِبْنٌ", "ibnun", "a son", "بیٹا", "👶"),
                w("بِنْتٌ", "bintun", "a daughter, a girl", "بیٹی", "👧"),
                w("طِفْلٌ", "ṭiflun", "a child", "بچہ", "🧒"),
                w("صَدِيقٌ", "ṣadīqun", "a friend", "دوست", "🤝"),
            )),
            ArSet("a2.2", "My body", "میرا جسم", "✋", listOf(
                w("رَأْسٌ", "raʾsun", "a head", "سر", "🙂"),
                w("عَيْنٌ", "ʿaynun", "an eye", "آنکھ", "👁️"),
                w("أُذُنٌ", "udhunun", "an ear", "کان", "👂"),
                w("أَنْفٌ", "anfun", "a nose", "ناک", "👃"),
                w("فَمٌ", "famun", "a mouth", "منہ", "👄"),
                w("يَدٌ", "yadun", "a hand", "ہاتھ", "✋"),
                w("رِجْلٌ", "rijlun", "a leg, a foot", "ٹانگ، پاؤں", "🦵"),
                w("قَلْبٌ", "qalbun", "a heart", "دل", "❤️"),
                w("سِنٌّ", "sinnun", "a tooth", "دانت", "🦷"),
                w("شَعْرٌ", "shaʿrun", "hair", "بال", "💇"),
            )),
            ArSet("a2.3", "Numbers 1 to 10", "گنتی ایک سے دس", "🔢", listOf(
                w("وَاحِدٌ", "wāḥidun", "one", "ایک", "1️⃣"),
                w("اِثْنَانِ", "ithnāni", "two", "دو", "2️⃣"),
                w("ثَلَاثَةٌ", "thalāthatun", "three", "تین", "3️⃣"),
                w("أَرْبَعَةٌ", "arbaʿatun", "four", "چار", "4️⃣"),
                w("خَمْسَةٌ", "khamsatun", "five", "پانچ", "5️⃣"),
                w("سِتَّةٌ", "sittatun", "six", "چھ", "6️⃣"),
                w("سَبْعَةٌ", "sabʿatun", "seven", "سات", "7️⃣"),
                w("ثَمَانِيَةٌ", "thamāniyatun", "eight", "آٹھ", "8️⃣"),
                w("تِسْعَةٌ", "tisʿatun", "nine", "نو", "9️⃣"),
                w("عَشَرَةٌ", "ʿasharatun", "ten", "دس", "🔟"),
            )),
        )),
        ArLevel(3, "Around me", "میرے آس پاس", "Easy–medium", "درمیانہ", Color(0xFFE3F2FD), Color(0xFF1565C0), listOf(
            ArSet("a3.1", "Colours", "رنگ", "🎨", listOf(
                w("أَحْمَرُ", "aḥmaru", "red", "لال (سرخ)", "🔴"),
                w("أَزْرَقُ", "azraqu", "blue", "نیلا", "🔵"),
                w("أَخْضَرُ", "akhḍaru", "green", "ہرا (سبز)", "🟢"),
                w("أَصْفَرُ", "aṣfaru", "yellow", "پیلا", "🟡"),
                w("أَبْيَضُ", "abyaḍu", "white", "سفید", "⚪"),
                w("أَسْوَدُ", "aswadu", "black", "کالا", "⚫"),
            )),
            ArSet("a3.2", "My home", "میرا گھر", "🏠", listOf(
                w("بَيْتٌ", "baytun", "a house", "گھر", "🏠"),
                w("بَابٌ", "bābun", "a door", "دروازہ", "🚪"),
                w("نَافِذَةٌ", "nāfidhatun", "a window", "کھڑکی", "🖼️"),
                w("سَرِيرٌ", "sarīrun", "a bed", "بستر", "🛏️"),
                w("كُرْسِيٌّ", "kursiyyun", "a chair", "کرسی", "🪑"),
                w("طَاوِلَةٌ", "ṭāwilatun", "a table", "میز", "🍽️"),
                w("مِفْتَاحٌ", "miftāḥun", "a key", "چابی", "🔑"),
                w("كُوبٌ", "kūbun", "a cup", "پیالی (کپ)", "☕"),
                w("مِلْعَقَةٌ", "milʿaqatun", "a spoon", "چمچ", "🥄"),
                w("سَاعَةٌ", "sāʿatun", "a clock, a watch", "گھڑی", "⏰"),
            )),
            ArSet("a3.3", "At school", "اسکول میں", "🏫", listOf(
                w("مَدْرَسَةٌ", "madrasatun", "a school", "اسکول (مدرسہ)", "🏫"),
                w("كِتَابٌ", "kitābun", "a book", "کتاب", "📖"),
                w("قَلَمٌ", "qalamun", "a pen", "قلم", "🖊️"),
                w("دَفْتَرٌ", "daftarun", "a notebook", "کاپی", "📒"),
                w("حَقِيبَةٌ", "ḥaqībatun", "a school bag", "بستہ", "🎒"),
                w("مُعَلِّمٌ", "muʿallimun", "a teacher", "استاد", "🧑‍🏫"),
                w("طَالِبٌ", "ṭālibun", "a student", "طالب علم", "🧑‍🎓"),
                w("سَبُّورَةٌ", "sabbūratun", "a blackboard", "تختۂ سیاہ", "⬛"),
                w("مِسْطَرَةٌ", "misṭaratun", "a ruler", "فٹا (اسکیل)", "📏"),
                w("وَرَقَةٌ", "waraqatun", "a sheet of paper", "کاغذ", "📄"),
            )),
            ArSet("a3.4", "Nature", "قدرت", "🌳", listOf(
                w("شَمْسٌ", "shamsun", "the sun", "سورج", "☀️"),
                w("قَمَرٌ", "qamarun", "the moon", "چاند", "🌙"),
                w("نَجْمٌ", "najmun", "a star", "ستارہ", "⭐"),
                w("سَمَاءٌ", "samāʾun", "the sky", "آسمان", "🌌"),
                w("أَرْضٌ", "arḍun", "the earth, land", "زمین", "🌍"),
                w("بَحْرٌ", "baḥrun", "a sea", "سمندر", "🌊"),
                w("جَبَلٌ", "jabalun", "a mountain", "پہاڑ", "⛰️"),
                w("شَجَرَةٌ", "shajaratun", "a tree", "درخت", "🌳"),
                w("وَرْدَةٌ", "wardatun", "a rose", "گلاب کا پھول", "🌹"),
                w("مَطَرٌ", "maṭarun", "rain", "بارش", "🌧️"),
            )),
        )),
        ArLevel(4, "My deen and my day", "میرا دین اور میرا دن", "Medium", "درمیانہ", Color(0xFFE8F5E9), Color(0xFF2E7D32), listOf(
            ArSet("a4.1", "Masjid and worship", "مسجد اور عبادت", "🕌", listOf(
                w("مَسْجِدٌ", "masjidun", "a mosque", "مسجد", "🕌"),
                w("صَلَاةٌ", "ṣalātun", "prayer (salah)", "نماز", "🤲"),
                w("وُضُوءٌ", "wuḍūʾun", "ablution (wudu)", "وضو", "💧"),
                w("قُرْآنٌ", "qurʾānun", "the Quran", "قرآن", "📗"),
                w("أَذَانٌ", "adhānun", "the call to prayer", "اذان", "📢"),
                w("إِمَامٌ", "imāmun", "an imam", "امام", "🧔"),
                w("صَوْمٌ", "ṣawmun", "fasting", "روزہ", "🌙"),
                w("زَكَاةٌ", "zakātun", "zakah (obligatory charity)", "زکوٰۃ", "💰"),
                w("حَجٌّ", "ḥajjun", "Hajj (pilgrimage)", "حج", "🕋"),
                w("دُعَاءٌ", "duʿāʾun", "a supplication (dua)", "دعا", "🙏"),
            )),
            ArSet("a4.2", "Doing words", "کام والے الفاظ", "🏃", listOf(
                w("ذَهَبَ", "dhahaba", "he went", "وہ گیا", "🚶"),
                w("جَلَسَ", "jalasa", "he sat", "وہ بیٹھا", "🪑"),
                w("أَكَلَ", "akala", "he ate", "اس نے کھایا", "🍽️"),
                w("شَرِبَ", "shariba", "he drank", "اس نے پیا", "🥤"),
                w("كَتَبَ", "kataba", "he wrote", "اس نے لکھا", "✍️"),
                w("قَرَأَ", "qaraʾa", "he read", "اس نے پڑھا", "📖"),
                w("نَامَ", "nāma", "he slept", "وہ سویا", "😴"),
                w("فَتَحَ", "fataḥa", "he opened", "اس نے کھولا", "📂"),
                w("دَخَلَ", "dakhala", "he entered", "وہ اندر آیا", "🚪"),
                w("خَرَجَ", "kharaja", "he went out", "وہ باہر نکلا", "🏃"),
            )),
            ArSet("a4.3", "Describing words", "صفت والے الفاظ", "📏", listOf(
                w("كَبِيرٌ", "kabīrun", "big", "بڑا", "🐘"),
                w("صَغِيرٌ", "ṣaghīrun", "small", "چھوٹا", "🐜"),
                w("جَمِيلٌ", "jamīlun", "beautiful", "خوبصورت", "🌸"),
                w("طَوِيلٌ", "ṭawīlun", "tall, long", "لمبا", "🦒"),
                w("قَصِيرٌ", "qaṣīrun", "short", "ٹھگنا (کم لمبا)", "🐧"),
                w("جَدِيدٌ", "jadīdun", "new", "نیا", "🆕"),
                w("قَدِيمٌ", "qadīmun", "old", "پرانا", "🏺"),
                w("حَارٌّ", "ḥārrun", "hot", "گرم", "🔥"),
                w("بَارِدٌ", "bāridun", "cold", "ٹھنڈا", "🧊"),
                w("نَظِيفٌ", "naẓīfun", "clean", "صاف", "✨"),
            )),
        )),
        ArLevel(5, "Time and good manners", "وقت اور آداب", "Hard", "مشکل", Color(0xFFF3E5F5), Color(0xFF6A1B9A), listOf(
            ArSet("a5.1", "Time", "وقت", "⏳", listOf(
                w("يَوْمٌ", "yawmun", "a day", "دن", "📆"),
                w("لَيْلٌ", "laylun", "night", "رات", "🌃"),
                w("صَبَاحٌ", "ṣabāḥun", "morning", "صبح", "🌅"),
                w("مَسَاءٌ", "masāʾun", "evening", "شام", "🌇"),
                w("أُسْبُوعٌ", "usbūʿun", "a week", "ہفتہ (سات دن)", "🗓️"),
                w("شَهْرٌ", "shahrun", "a month", "مہینہ", "📅"),
                w("سَنَةٌ", "sanatun", "a year", "سال", "🎆"),
                w("اَلْيَوْمَ", "al-yawma", "today", "آج", "👉"),
                w("غَدًا", "ghadan", "tomorrow", "آنے والا کل", "⏭️"),
                w("أَمْسِ", "amsi", "yesterday", "گزرا ہوا کل", "⏮️"),
            )),
            ArSet("a5.2", "Days of the week", "ہفتے کے دن", "🗓️", listOf(
                w("اَلسَّبْتُ", "as-sabtu", "Saturday", "ہفتہ (دن)", "6️⃣"),
                w("اَلْأَحَدُ", "al-aḥadu", "Sunday", "اتوار", "7️⃣"),
                w("اَلْاِثْنَيْنِ", "al-ithnayn", "Monday", "پیر", "1️⃣"),
                w("اَلثُّلَاثَاءُ", "ath-thulāthāʾu", "Tuesday", "منگل", "2️⃣"),
                w("اَلْأَرْبِعَاءُ", "al-arbiʿāʾu", "Wednesday", "بدھ", "3️⃣"),
                w("اَلْخَمِيسُ", "al-khamīsu", "Thursday", "جمعرات", "4️⃣"),
                w("اَلْجُمُعَةُ", "al-jumuʿatu", "Friday", "جمعہ", "🕌"),
            )),
            ArSet("a5.3", "Good manners", "اچھے آداب", "🤝", listOf(
                w("اَلسَّلَامُ عَلَيْكُمْ", "as-salāmu ʿalaykum", "peace be upon you", "السلام علیکم", "👋"),
                w("وَعَلَيْكُمُ السَّلَامُ", "wa ʿalaykumu s-salām", "and upon you be peace", "وعلیکم السلام", "😊"),
                w("شُكْرًا", "shukran", "thank you", "شکریہ", "🙏"),
                w("مِنْ فَضْلِكَ", "min faḍlika", "please", "مہربانی کر کے", "🙂"),
                w("نَعَمْ", "naʿam", "yes", "ہاں", "✅"),
                w("لَا", "lā", "no", "نہیں", "❌"),
                w("صَبَاحُ الْخَيْرِ", "ṣabāḥu l-khayr", "good morning", "صبح بخیر", "🌞"),
                w("مَعَ السَّلَامَةِ", "maʿa s-salāmah", "goodbye", "خدا حافظ", "👋"),
                w("كَيْفَ حَالُكَ؟", "kayfa ḥāluka?", "how are you?", "آپ کیسے ہیں؟", "❓"),
                w("جَزَاكَ اللّٰهُ خَيْرًا", "jazāka llāhu khayran", "may Allah reward you with good", "اللہ آپ کو بہترین بدلہ دے", "🌟"),
            )),
        )),
        ArLevel(6, "Quran words and sentences", "قرآنی الفاظ اور جملے", "Hard", "مشکل", Color(0xFFE0F2F1), Color(0xFF00695C), listOf(
            ArSet("a6.1", "Words of the Quran", "قرآن کے الفاظ", "📗", listOf(
                w("رَبٌّ", "rabbun", "Lord, Sustainer", "رب (پالنے والا)", "🌍"),
                w("إِلٰهٌ", "ilāhun", "god (one who is worshipped)", "معبود", "☝️"),
                w("رَسُولٌ", "rasūlun", "a messenger", "رسول (پیغمبر)", "📜"),
                w("نَبِيٌّ", "nabiyyun", "a prophet", "نبی", "✨"),
                w("عَبْدٌ", "ʿabdun", "a servant (of Allah)", "بندہ", "🤲"),
                w("جَنَّةٌ", "jannatun", "a garden, Paradise", "باغ، جنت", "🌴"),
                w("نَارٌ", "nārun", "fire", "آگ", "🔥"),
                w("عِلْمٌ", "ʿilmun", "knowledge", "علم", "📚"),
                w("رَحْمَةٌ", "raḥmatun", "mercy", "رحمت", "💗"),
                w("نُورٌ", "nūrun", "light", "نور (روشنی)", "💡"),
            )),
            ArSet("a6.2", "Short sentences", "چھوٹے جملے", "💬", listOf(
                w("هٰذَا كِتَابٌ", "hādhā kitābun", "This is a book.", "یہ ایک کتاب ہے۔", "📖"),
                w("هٰذِهِ تُفَّاحَةٌ", "hādhihi tuffāḥatun", "This is an apple.", "یہ ایک سیب ہے۔", "🍎"),
                w("أَنَا مُسْلِمٌ", "anā muslimun", "I am a Muslim.", "میں مسلمان ہوں۔", "🤲"),
                w("اِسْمِي أَحْمَدُ", "ismī aḥmadu", "My name is Ahmad.", "میرا نام احمد ہے۔", "🙋"),
                w("اَلْبَيْتُ كَبِيرٌ", "al-baytu kabīrun", "The house is big.", "گھر بڑا ہے۔", "🏠"),
                w("اَلْمَاءُ بَارِدٌ", "al-māʾu bāridun", "The water is cold.", "پانی ٹھنڈا ہے۔", "🧊"),
                w("أَيْنَ الْقَلَمُ؟", "ayna l-qalamu?", "Where is the pen?", "قلم کہاں ہے؟", "🖊️"),
                w("ذَهَبْتُ إِلَى الْمَسْجِدِ", "dhahabtu ilā l-masjidi", "I went to the mosque.", "میں مسجد گیا۔", "🕌"),
                w("أُحِبُّ أُمِّي", "uḥibbu ummī", "I love my mother.", "مجھے اپنی امی سے محبت ہے۔", "❤️"),
                w("اَلشَّمْسُ جَمِيلَةٌ", "ash-shamsu jamīlatun", "The sun is beautiful.", "سورج خوبصورت ہے۔", "☀️"),
            )),
        )),
    )

    fun level(n: Int) = levels.first { it.number == n }
    fun set(id: String) = levels.flatMap { it.sets }.first { it.id == id }
    val allWords get() = levels.flatMap { it.words }
}
