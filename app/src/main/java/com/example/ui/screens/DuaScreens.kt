package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChildProfile
import com.example.ui.theme.*

// Data model for a beautiful, simple supplication
data class SupplicationItem(
    val id: String,
    val title: String,
    val arabTitle: String,
    val emoji: String,
    val arabic: String,
    val transliteration: String,
    val english: String,
    val tip: String,
    val arabTip: String,
    val accentColor: Color,
    val cardColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuaScreens(
    profile: ChildProfile,
    onBackPressed: () -> Unit,
    onEarnRewards: (Int, Int, String) -> Unit,
    onSoundTrigger: (String) -> Unit
) {
    val isArabicLanguage = remember(profile.currentLanguage) { profile.currentLanguage == "Arabic" }
    val lessonAge = remember(profile.age) { profile.age.coerceIn(3, 8) }
    var selectedAgeFilter by remember(lessonAge) { mutableStateOf(lessonAge) }

    // Dynamic list of supplications based on selected age filter
    val supplications = remember(selectedAgeFilter) {
        when (selectedAgeFilter) {
            3 -> listOf(
                SupplicationItem(
                    id = "first_kalima",
                    title = "First Kalima: Tayyabah ☝️",
                    arabTitle = "الكلمة الأولى: الطيبة ☝️",
                    emoji = "☝️",
                    arabic = "لَا إِلٰهَ إِلَّا اللّٰهُ مُحَمَّدٌ رَسُولُ اللّٰهِ",
                    transliteration = "La ilaha illallahu Muhammadur Rasulullah",
                    english = "There is no god but Allah, and Muhammad is the Messenger of Allah",
                    tip = "Say this with your index finger pointed high! Key to Jannah! 🔑✨",
                    arabTip = "قل هذا مع رفع إصبعك السبابة عالياً! مفتاح الجنة! 🔑✨",
                    accentColor = Color(0xFF4CAF50), // Islamic Green
                    cardColor = Color(0xFFE8F5E9)
                ),
                SupplicationItem(
                    id = "second_kalima",
                    title = "Second Kalima: Shahadah 🤝",
                    arabTitle = "الكلمة الثانية: الشهادة 🤝",
                    emoji = "🤝",
                    arabic = "أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللّٰهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ",
                    transliteration = "Ash-hadu al-la ilaha illallahu wa ash-hadu anna Muhammadan 'abduhu wa Rasuluh",
                    english = "I bear witness that there is no god but Allah, and I bear witness that Muhammad is His servant and Messenger",
                    tip = "Our beautiful statement of faith! Repeat beautifully with Noor Bee! 🐝💖",
                    arabTip = "شهادتنا الرائعة للإيمان! كررها بجمال مع نورا النحلة! 🐝💖",
                    accentColor = Color(0xFF2196F3), // Bright Blue
                    cardColor = Color(0xFFE3F2FD)
                ),
                SupplicationItem(
                    id = "before_eating",
                    title = "Before Eating 🍽️",
                    arabTitle = "قبل الطعام 🍽️",
                    emoji = "🍯",
                    arabic = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
                    transliteration = "Bismillahir Rahmanir Rahim",
                    english = "In the name of Allah, the Most Gracious, the Most Merciful",
                    tip = "Say this before we eat our yummy honey and fruits! 🍎",
                    arabTip = "نقول هذا قبل أن نأكل العسل والفواكه اللذيذة! 🍎",
                    accentColor = Color(0xFFFFB300), // Amber Gold
                    cardColor = Color(0xFFFFFDF0)
                ),
                SupplicationItem(
                    id = "after_eating",
                    title = "After Eating 🍉",
                    arabTitle = "بعد الطعام 🍉",
                    emoji = "🍉",
                    arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا وَجَعَلَنَا مُسْلِمِينَ",
                    transliteration = "Alhamdu lillahil-ladhi at'amana wa saqana wa ja'alana muslimin",
                    english = "Praise be to Allah who has fed us and given us drink, and made us Muslims",
                    tip = "Thank Allah for the sweet juices and delicious food! 🤲",
                    arabTip = "نشكر الله على العصائر اللذيذة والطعام الرائع والجميل! 🤲",
                    accentColor = Color(0xFF4CAF50), // Lime Green
                    cardColor = Color(0xFFF1FDF2)
                ),
                SupplicationItem(
                    id = "before_sleeping",
                    title = "Before Sleeping 🛌",
                    arabTitle = "قبل النوم 🛌",
                    emoji = "🌙",
                    arabic = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
                    transliteration = "Bismika Allahumma amutu wa ahya",
                    english = "In Your name, O Allah, I die and I live",
                    tip = "Say this to have sweet and cozy dreams with Noor Bee! 🛌✨",
                    arabTip = "قل هذا لتحلم أحلاماً سعيدة ودافئة مع صديقتنا نورا النحلة! 🛌✨",
                    accentColor = Color(0xFF3F51B5), // Indigo Blue
                    cardColor = Color(0xFFF1F3FC)
                ),
                SupplicationItem(
                    id = "waking_up",
                    title = "Waking Up ☀️",
                    arabTitle = "الاستيقاظ من النوم ☀️",
                    emoji = "☀️",
                    arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
                    transliteration = "Alhamdu lillahil-ladhi ahyana ba'da ma amatana wa ilayhin-nushur",
                    english = "Praise be to Allah Who gave us life after death, and to Him is the return",
                    tip = "Stretch high and say this to start an exciting, happy day! ☀️",
                    arabTip = "تمدد لأعلى وقل هذا لبدء يوم جديد سعيد ومثير! ☀️",
                    accentColor = Color(0xFF03A9F4), // Sky Blue
                    cardColor = Color(0xFFF0FAFF)
                )
            )
            4 -> listOf(
                SupplicationItem(
                    id = "entering_home",
                    title = "Entering the Home 🏠",
                    arabTitle = "دعاء دخول المنزل 🏠",
                    emoji = "🏠",
                    arabic = "بِسْمِ اللَّهِ وَلَجْنَا وَبِسْمِ اللَّهِ خَرَجْنَا وَعَلَى اللَّهِ رَبِّنَا تَوَكَّلْنَا",
                    transliteration = "Bismillahi walajna wa bismillahi kharajna wa 'ala Allahi Rabbina tawakkalna",
                    english = "In the name of Allah we enter, in the name of Allah we leave, and upon our Lord we rely",
                    tip = "Say this when you step inside your sweet home to bring peace! 🏠🌸",
                    arabTip = "قل هذا عندما تدخل منزلك الجميل لنشر السلام والبركة! 🏠🌸",
                    accentColor = Color(0xFF9C27B0), // Purple Indigo
                    cardColor = Color(0xFFF3E5F5)
                ),
                SupplicationItem(
                    id = "leaving_home",
                    title = "Leaving the Home 🚪",
                    arabTitle = "دعاء الخروج من المنزل 🚪",
                    emoji = "🚪",
                    arabic = "بِسْمِ اللَّهِ تَوَكَّلْتُ عَلَى اللَّهِ لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
                    transliteration = "Bismillahi tawakkaltu 'alallah la hawla wa la quwwata illa billah",
                    english = "In the name of Allah, I place my trust in Allah; there is no power or might except with Allah",
                    tip = "Say this as we step outside! An angel will protect you all day! 🗺️✨",
                    arabTip = "قل هذا عندما تخطو للخارج! سيحميك ملاك طوال اليوم! 🗺️✨",
                    accentColor = Color(0xFFFF5722), // Vibrant Orange
                    cardColor = Color(0xFFFBE9E7)
                ),
                SupplicationItem(
                    id = "drinking_milk",
                    title = "Drinking Milk 🥛",
                    arabTitle = "دعاء شرب اللبن 🥛",
                    emoji = "🥛",
                    arabic = "اللَّهُمَّ بَارِكْ لَنَا فِيهِ وَزِدْنَا مِنْه",
                    transliteration = "Allahumma barik lana fihi wa zidna minhu",
                    english = "O Allah, bless it for us and give us more of it",
                    tip = "Milk makes us strong! Ask Allah to bless our tasty milk! 💪🥛",
                    arabTip = "الحليب يجعلنا أقوياء! اطلب من الله أن يبارك حليبنا اللذيذ! 💪🥛",
                    accentColor = Color(0xFF00BCD4), // Teal
                    cardColor = Color(0xFFE0F7FA)
                ),
                SupplicationItem(
                    id = "putting_clothes",
                    title = "Getting Dressed 👕",
                    arabTitle = "دعاء لبس الثوب 👕",
                    emoji = "👕",
                    arabic = "الْحَمْدُ لِلَّهِ الَّذِي كَسَانِي هَذَا الثَّوْبَ وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
                    transliteration = "Alhamdu lillahil-ladhi kasani hadha-thawba wa razaqanihi min ghayri hawlin minni wa la quwwatin",
                    english = "Praise be to Allah who clothed me with this and provided it without any effort or power on my part",
                    tip = "Thank Allah for our colorful, soft, and cozy outfits! 🎨👕",
                    arabTip = "نشكر الله على ملابسنا الملونة والناعمة والجميلة! 🎨👕",
                    accentColor = Color(0xFF795548), // Warm Brown
                    cardColor = Color(0xFFEFEBE9)
                ),
                SupplicationItem(
                    id = "parents_dua",
                    title = "Love for Our Parents 👪",
                    arabTitle = "بر الوالدين والدعاء لهما 👪",
                    emoji = "💖",
                    arabic = "رَبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
                    transliteration = "Rabbi irhamhuma kama rabbayani saghira",
                    english = "My Lord! Have mercy on them as they brought me up when I was small",
                    tip = "Say this to show love to your kind Mommy and Daddy! 🥰",
                    arabTip = "قل هذا لتعبر عن حبك الكبير لماما وبابا الطيبين! 🥰",
                    accentColor = Color(0xFFE91E63), // Pink Heart
                    cardColor = Color(0xFFFFF0F5)
                )
            )
            5 -> listOf(
                SupplicationItem(
                    id = "seeking_knowledge",
                    title = "Learning & Wisdom 🎓",
                    arabTitle = "طلب العلم والفهم 🎓",
                    emoji = "🎓",
                    arabic = "رَبِّ زِدْنِي عِلْمًا",
                    transliteration = "Rabbi zidni 'ilman",
                    english = "O my Lord! Increase me in knowledge",
                    tip = "Noor Bee loves this! It makes our brain super smart! 🧠💡",
                    arabTip = "نورا النحلة تحب هذا الدعاء! إنه يجعل عقولنا ذكية جداً! 🧠💡",
                    accentColor = Color(0xFF9C27B0), // Purple Power
                    cardColor = Color(0xFFFCF0FF)
                ),
                SupplicationItem(
                    id = "entering_mosque",
                    title = "Entering the Mosque 🕌",
                    arabTitle = "دعاء دخول المسجد 🕌",
                    emoji = "🕌",
                    arabic = "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
                    transliteration = "Allahumma-ftah li abwaba rahmatika",
                    english = "O Allah, open for me the gates of Your mercy",
                    tip = "Step in with your right foot first to see Allah's blessings! 🕌✨",
                    arabTip = "ادخل بقدمك اليمنى أولاً لتنال بركات الله الكثيرة! 🕌✨",
                    accentColor = Color(0xFF4CAF50), // Green Mosque
                    cardColor = Color(0xFFE8F5E9)
                ),
                SupplicationItem(
                    id = "leaving_mosque",
                    title = "Leaving the Mosque 🚶‍♂️",
                    arabTitle = "دعاء الخروج من المسجد 🚶‍♂️",
                    emoji = "🚶‍♂️",
                    arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ",
                    transliteration = "Allahumma inni as'aluka min fadlika",
                    english = "O Allah, indeed I ask You of Your bounty",
                    tip = "Step out with your left foot first, asking Allah for amazing goodness! 🌟🐾",
                    arabTip = "اخرج بقدمك اليسرى أولاً، واطلب من الله فضله وخيره الواسع! 🌟🐾",
                    accentColor = Color(0xFF009688), // Ocean Teal
                    cardColor = Color(0xFFE0F2F1)
                ),
                SupplicationItem(
                    id = "sneezing_manners",
                    title = "Sneezing Manners 🤧",
                    arabTitle = "آداب العطس 🤧",
                    emoji = "🤧",
                    arabic = "الْحَمْدُ لِلَّهِ",
                    transliteration = "Alhamdulillah",
                    english = "Praise be to Allah",
                    tip = "Praise Allah when you sneeze! Your friend should reply: Yarhamukallah! 🤧❤️",
                    arabTip = "احمد الله عندما تعطس! وينبغي لصديقك أن يقول لك: يرحمك الله! 🤧❤️",
                    accentColor = Color(0xFFFF9800), // Orange
                    cardColor = Color(0xFFFFF3E0)
                ),
                SupplicationItem(
                    id = "protection_harm",
                    title = "Shield of Protection 🛡️",
                    arabTitle = "دعاء الحفظ والتحصين 🛡️",
                    emoji = "🛡️",
                    arabic = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
                    transliteration = "Bismillahil-ladhi la yadurru ma'as-mihi shay'un fil-ardi wa la fis-sama'i wa Huwas-Sami'ul-'Alim",
                    english = "In the name of Allah, by whose name nothing can cause harm on Earth or in Heaven, and He is the All-Hearing, All-Knowing",
                    tip = "Your powerful morning & evening protective shield made of sweet light! 🌟🐝",
                    arabTip = "درع الحماية الصباحي والمسائي القوي المصنوع من النور اللطيف! 🌟🐝",
                    accentColor = Color(0xFF3F51B5), // Indigo
                    cardColor = Color(0xFFE8EAF6)
                )
            )
            6 -> listOf(
                SupplicationItem(
                    id = "master_dua",
                    title = "The Master Supplication 🌍",
                    arabTitle = "دعاء الخير في الدارين 🌍",
                    emoji = "🌍",
                    arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
                    transliteration = "Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan waqina 'adhaban-nar",
                    english = "Our Lord, give us in this world that which is good and in the Hereafter that which is good and protect us from the punishment of the Fire",
                    tip = "The most beautiful comprehensive dua! Helps us live happily now & in Jannah! 🌳🌸",
                    arabTip = "أجمل وأشمل دعاء! يساعدنا على العيش بسعادة الآن وفي الجنة لاحقاً! 🌳🌸",
                    accentColor = Color(0xFFE91E63), // Pink
                    cardColor = Color(0xFFFCE4EC)
                ),
                SupplicationItem(
                    id = "forgiveness_parents",
                    title = "Forgiveness & Mercy 🤲",
                    arabTitle = "الاستغفار والدعاء للمؤمنين 🤲",
                    emoji = "✨",
                    arabic = "رَبَّنَا اغْفِرْ لِي وَلِوَالِدَيَّ وَلِلْمُؤْمِنِينَ يَوْمَ يَقُومُ الْحِسَابُ",
                    transliteration = "Rabbana-ghfir li wali-walidayya wa lil-mu'minina yawma yaqumul-hisab",
                    english = "Our Lord, forgive me and my parents and the believers the Day the account is established",
                    tip = "A gorgeous, kind prayer for your family and every single friend in the world! 🤝❤️",
                    arabTip = "دعاء رقيق ولطيف لعائلتك ولكل صديق في هذا العالم! 🤝❤️",
                    accentColor = Color(0xFF1E88E5), // Blue Accent
                    cardColor = Color(0xFFE3F2FD)
                ),
                SupplicationItem(
                    id = "trust_and_courage",
                    title = "Trust & Bravery 🦁",
                    arabTitle = "التوكل واليقين 🦁",
                    emoji = "🦁",
                    arabic = "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
                    transliteration = "Hasbiyallahu la ilaha illa Huwa 'alayhi tawakkaltu wa Huwa Rabbul-'Arshil-'Adhim",
                    english = "Sufficient for me is Allah; there is no deity except Him. On Him I have relied, and He is the Lord of the Great Throne",
                    tip = "Feel brave and secure like a gentle lion, because Allah is guarding you! 🦁🛡️",
                    arabTip = "اشعر بالشجاعة والأمان مثل الأسد اللطيف، لأن الله يحميك ويرعاك! 🦁🛡️",
                    accentColor = Color(0xFFFF9800), // Amber Orange
                    cardColor = Color(0xFFFFF3E0)
                ),
                SupplicationItem(
                    id = "patience_ease",
                    title = "Ease & Sunshine 🌸",
                    arabTitle = "دعاء تيسير الأمور وشرح الصدر 🌸",
                    emoji = "🌸",
                    arabic = "رَبِّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي",
                    transliteration = "Rabbi-shrah li sadri wa yassir li amri",
                    english = "O my Lord! Open for me my chest, and make easy for me my task",
                    tip = "Feeling nervous or trying something new? Say this and see how beautiful it gets! 🌟💨",
                    arabTip = "هل تشعر بالقلق أو تجرب شيئاً جديداً؟ ردد هذا وشاهد كيف يصبح كل شيء سهلاً! 🌟💨",
                    accentColor = Color(0xFF00ACC1), // Cyan
                    cardColor = Color(0xFFE0F7FA)
                )
            )
            7 -> listOf(
                SupplicationItem(
                    id = "steadfast_heart",
                    title = "Steadfast Heart 💖",
                    arabTitle = "يا مقلب القلوب 💖",
                    emoji = "💖",
                    arabic = "يَا مُقَلِّبَ الْقُلُوبِ ثَبِّتْ قَلْبِي عَلَى دِينِكَ",
                    transliteration = "Ya Muqallibal-qulubi thabbit qalbi 'ala dinik",
                    english = "O Turner of the hearts, keep my heart steadfast upon Your religion",
                    tip = "A powerful, sweet dua to keep our hearts glowing with goodness and kindness! 🌟",
                    arabTip = "دعاء عظيم وجميل ليظل قلبنا مليئاً بالخير والإيمان والطاعة! 🌟",
                    accentColor = Color(0xFFE91E63), // Pink
                    cardColor = Color(0xFFFFF0F5)
                ),
                SupplicationItem(
                    id = "gratitude_thanks",
                    title = "Gratitude & Thanks 🌟",
                    arabTitle = "الشكر والامتنان للنعم 🌟",
                    emoji = "🌟",
                    arabic = "رَبِّ أَوْزِعْنِي أَنْ أَشْكُرَ نِعْمَتَكَ الَّتِي أَنْعَمْتَ عَلَيَّ",
                    transliteration = "Rabbi awzi'ni an ashkura ni'matakal-lati an'amta 'alayya",
                    english = "My Lord, enable me to be grateful for Your favor which You have bestowed upon me",
                    tip = "Ask Allah to help us thank Him for our happy home, toys, and loving family! 👪❤️",
                    arabTip = "اسأل الله أن يوفقنا لشكره على الصحة والمنزل والألعاب والوالدين! 👪❤️",
                    accentColor = Color(0xFFFFB300), // Amber Gold
                    cardColor = Color(0xFFFFFDF0)
                ),
                SupplicationItem(
                    id = "moses_speech",
                    title = "Confidence & Ease 🎤",
                    arabTitle = "دعاء تيسير الكلام والشجاعة 🎤",
                    emoji = "🎤",
                    arabic = "رَبِّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي وَاحْلُلْ عُقْدَةً مِنْ لِسَانِي يَفْقَهُوا قَوْلِي",
                    transliteration = "Rabbi-shrah li sadri wa yassir li amri wa-hlul 'uqdatan min lisani yafqahu qawli",
                    english = "My Lord, expand for me my chest, ease for me my task, and untie the knot from my tongue that they may understand my speech",
                    tip = "Say this to feel confident, brave, and speak beautifully when talking to people! 🎤🐝",
                    arabTip = "قل هذا لتشعر بالثقة والطلاقة والفصاحة والتوفيق عند التحدث للجميع! 🎤🐝",
                    accentColor = Color(0xFF03A9F4), // Sky Blue
                    cardColor = Color(0xFFF0FAFF)
                )
            )
            8 -> listOf(
                SupplicationItem(
                    id = "beneficial_knowledge",
                    title = "Beneficial Knowledge 🎓",
                    arabTitle = "طلب العلم النافع والرزق الطيب 🎓",
                    emoji = "🎓",
                    arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا وَرِزْقًا طَيِّبًا وَعَمَلًا مُتَقَبَّلًا",
                    transliteration = "Allahumma inni as'aluka 'ilman nafi'an, wa rizqan tayyiban, wa 'amalan mutaqabbalan",
                    english = "O Allah, I ask You for beneficial knowledge, goodly provision, and acceptable deeds",
                    tip = "Say this every morning to ask Allah to make your school, learning, and life filled with wisdom and blessings! ☀️📚",
                    arabTip = "ردد هذا الدعاء كل صباح لتطلب من الله التوفيق في دراستك وعملك ورزقك الطيب! ☀️📚",
                    accentColor = Color(0xFF4CAF50),
                    cardColor = Color(0xFFE8F5E9)
                ),
                SupplicationItem(
                    id = "protection_lazy",
                    title = "Protection & Energy 🛡️",
                    arabTitle = "الاستعاذة من العجز والكسل والهم 🛡️",
                    emoji = "🛡️",
                    arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْعَجْزِ وَالْكَسَلِ وَالْجُبْنِ وَالْهَرَمِ وَالْبُخْلِ",
                    transliteration = "Allahumma inni a'udhu bika minal-'ajzi wal-kasali wal-jubni wal-harami wal-bukhl",
                    english = "O Allah, I seek refuge in You from physical weakness, laziness, cowardice, old age, and stinginess",
                    tip = "A powerful prayer to keep your body energetic, helpful, and brave every day! 💪🐝",
                    arabTip = "دعاء رائع ليملأ الله جسدك بالنشاط والهمة، ويبعد عنك التعب والكسل! 💪🐝",
                    accentColor = Color(0xFF9C27B0),
                    cardColor = Color(0xFFF3E5F5)
                ),
                SupplicationItem(
                    id = "light_paths",
                    title = "Light in Every Way ☀️",
                    arabTitle = "دعاء النور والبركة في الحواس ☀️",
                    emoji = "☀️",
                    arabic = "اللَّهُمَّ اجْعَلْ فِي قَلْبِي نُورًا وَفِي بَصَرِي نُورًا وَفِي سَمْعِي نُورًا وَعَنْ يَمِينِي نُورًا",
                    transliteration = "Allahumma-j'al fi qalbi nuran, wa fi basari nuran, wa fi sam'i nuran, wa 'an yamini nuran",
                    english = "O Allah, place light in my heart, light in my sight, light in my hearing, and light on my right",
                    tip = "Ask Allah to light up your eyes, ears, and heart so you only see and do beautiful things! 💖🌟",
                    arabTip = "اسأل الله أن ينور قلبك وبصرك وسمعك لتفعل الخير وتتكلم بالكلمات الطيبة! 💖🌟",
                    accentColor = Color(0xFFFFB300),
                    cardColor = Color(0xFFFFFDE7)
                )
            )
            else -> emptyList() // ages are kept between 3 and 8, so this is never reached (it held a second copy of the age-7 entry)
        }
    }

    var selectedDua by remember { mutableStateOf<SupplicationItem?>(null) }
    var completedList by remember { mutableStateOf(setOf<String>()) }
    var showCelebrate by remember { mutableStateOf(false) }

    // Quiz Mode States
    var isQuizActive by remember { mutableStateOf(false) }
    var quizQuestionsList by remember { mutableStateOf(emptyList<DuaQuizQuestion>()) }
    var currentQuizQuestionIndex by remember { mutableStateOf(0) }
    var selectedQuizAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }
    var quizScoreState by remember { mutableStateOf(0) }
    var isLastAnswerCorrect by remember { mutableStateOf(false) }
    var showQuizSummary by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Duas 🌙", fontWeight = FontWeight.Bold, color = DeepNavy) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DeepNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MinimalWhite)
            )
        },
        containerColor = MinimalWhite
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isQuizActive && quizQuestionsList.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    DuaQuizArenaLayout(
                        questions = quizQuestionsList,
                        currentIndex = currentQuizQuestionIndex,
                        selectedIndex = selectedQuizAnswerIndex,
                        isSubmitted = isAnswerSubmitted,
                        onSelect = { selectedQuizAnswerIndex = it },
                        onSubmit = {
                            isAnswerSubmitted = true
                            val q = quizQuestionsList[currentQuizQuestionIndex]
                            val isCorrect = if (isArabicLanguage) {
                                q.optionsAr[selectedQuizAnswerIndex ?: 0] == q.correctAnswerAr
                            } else {
                                q.options[selectedQuizAnswerIndex ?: 0] == q.correctAnswer
                            }
                            isLastAnswerCorrect = isCorrect
                            if (isCorrect) {
                                quizScoreState += 1
                                onSoundTrigger("text:english:Correct! Splendid job!")
                            } else {
                                onSoundTrigger("text:english:Ooh, incorrect. Keep learning with Noor Bee!")
                            }
                        },
                        onNext = {
                            if (currentQuizQuestionIndex < quizQuestionsList.size - 1) {
                                currentQuizQuestionIndex += 1
                                selectedQuizAnswerIndex = null
                                isAnswerSubmitted = false
                            } else {
                                showQuizSummary = true
                            }
                        },
                        isCorrect = isLastAnswerCorrect,
                        isArabicLanguage = isArabicLanguage,
                        onExit = { isQuizActive = false },
                        showSummary = showQuizSummary,
                        score = quizScoreState,
                        onFinish = {
                            onEarnRewards(quizScoreState * 10, quizScoreState * 15, "Quiz: Completed Level $selectedAgeFilter Challenge")
                            isQuizActive = false
                        },
                        onSoundTrigger = onSoundTrigger
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .testTag("dua_section_screen")
                ) {
                // Friendly Intro Banner
                Card(
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(vertical = 12.dp)
                         .testTag("dua_intro_banner"),
                     shape = RoundedCornerShape(24.dp),
                     colors = CardDefaults.cardColors(containerColor = LightBlueGrey),
                     border = BorderStroke(1.dp, BorderGrey)
                ) {
                     Column(
                         modifier = Modifier.padding(16.dp),
                         horizontalAlignment = Alignment.CenterHorizontally
                     ) {
                         Text(
                             text = if (isArabicLanguage) "الأذكار والأدعية اليومية 🤲" else "Daily Supplications 🤲",
                             fontWeight = FontWeight.Bold,
                             fontSize = 15.sp,
                             color = DeepNavy,
                             textAlign = TextAlign.Center,
                             modifier = Modifier.fillMaxWidth()
                         )
                         Spacer(modifier = Modifier.height(4.dp))
                         Text(
                             text = if (isArabicLanguage) {
                                 "أذكار وأدعية جميلة لجميع المستويات! استمع بالآداب، ردد مع نورة النحلة، واجمع النجوم والعسل!"
                             } else {
                                 "Beautiful supplications for all levels! Listen, repeat after Noor Bee, and collect stars & honey!"
                             },
                             fontSize = 11.sp,
                             color = SlateGrey,
                             lineHeight = 14.sp,
                             textAlign = TextAlign.Center,
                             modifier = Modifier.fillMaxWidth()
                         )
                     }
                }

                // Quiz Challenge card and banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            val generated = generateQuizQuestionsForAge(selectedAgeFilter).shuffled().take(3)
                            quizQuestionsList = generated
                            currentQuizQuestionIndex = 0
                            selectedQuizAnswerIndex = null
                            isAnswerSubmitted = false
                            quizScoreState = 0
                            isQuizActive = true
                            showQuizSummary = false
                        }
                        .testTag("start_dua_quiz_button"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                    border = BorderStroke(1.5.dp, Color(0xFFFFB300))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎓", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabicLanguage) "تحدي مسابقة الأدعية اليومية! 🐝✨" else "Daily Dua Quiz Challenge! 🐝✨",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF3E2723)
                            )
                            Text(
                                text = if (isArabicLanguage) "اختبر معلوماتك لتربح الجوائز والعسل والمزيد!" else "Test your knowledge of supplications & win honey!",
                                fontSize = 10.sp,
                                color = Color(0xFF5D4037)
                            )
                        }
                        Button(
                            onClick = {
                                val generated = generateQuizQuestionsForAge(selectedAgeFilter).shuffled().take(3)
                                quizQuestionsList = generated
                                currentQuizQuestionIndex = 0
                                selectedQuizAnswerIndex = null
                                isAnswerSubmitted = false
                                quizScoreState = 0
                                isQuizActive = true
                                showQuizSummary = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isArabicLanguage) "ابدأ المسابقة ⚡️" else "Start ⚡️",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF3E2723)
                            )
                        }
                    }
                }

                // Interactive Age Level Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(3, 4, 5, 6, 7, 8).forEach { ageNum ->
                        val isSelected = selectedAgeFilter == ageNum
                        val text = if (isArabicLanguage) {
                            when (ageNum) {
                                3 -> "عمر ٣"
                                4 -> "عمر ٤"
                                5 -> "عمر ٥"
                                6 -> "عمر ٦"
                                7 -> "عمر ٧"
                                else -> "عمر ٨"
                            }
                        } else {
                            "Age $ageNum"
                        }
                        val icon = when (ageNum) {
                            3 -> "🧸"
                            4 -> "🏠"
                            5 -> "🎓"
                            6 -> "🌟"
                            7 -> "👑"
                            else -> "🚀"
                        }

                        Surface(
                            onClick = { selectedAgeFilter = ageNum },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("age_filter_$ageNum"),
                            shape = RoundedCornerShape(19.dp),
                            color = if (isSelected) DeepNavy else Color(0xFFF1F2F6),
                            border = if (isSelected) null else BorderStroke(1.dp, BorderGrey)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "$icon $text",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White else SlateGrey
                                )
                            }
                        }
                    }
                }

                Text(
                    text = if (isArabicLanguage) "اختر دعاءً أو كلمةً طيبة 🧸" else "Pick a Supplication 🧸",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DeepNavy,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Duas List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(supplications) { item ->
                        val isCompleted = completedList.contains(item.id)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDua = item }
                                .testTag("dua_card_${item.id}"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = item.cardColor),
                            border = BorderStroke(
                                width = if (selectedDua?.id == item.id) 2.dp else 1.dp,
                                color = if (selectedDua?.id == item.id) item.accentColor else BorderGrey
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(Color.White, CircleShape)
                                        .border(1.dp, BorderGrey, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = item.emoji, fontSize = 22.sp)
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = DeepNavy
                                    )
                                    Text(
                                        text = item.transliteration,
                                        fontSize = 11.sp,
                                        color = SlateGrey
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                // Interactive speaker button directly on the list card
                                if (item.id != "first_kalima") {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp) // Minimum touch target size
                                            .clickable {
                                                onSoundTrigger("text:arabic:${item.arabic}")
                                            }
                                            .testTag("speaker_list_button_${item.id}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(item.accentColor.copy(alpha = 0.12f), CircleShape)
                                                .border(1.2.dp, item.accentColor, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("🔊", fontSize = 16.sp, modifier = Modifier.testTag("speaker_emoji_${item.id}"))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                if (isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Completed",
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .border(1.dp, BorderGrey, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("?", fontSize = 12.sp, color = SlateGrey)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Beautiful interactive details overlay when a Dua is tapped
            selectedDua?.let { item ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f))
                        .clickable { selectedDua = null }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .clickable(enabled = false) {}
                            .testTag("dua_player_overlay"),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Drag handle
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(5.dp)
                                    .background(BorderGrey, CircleShape)
                                    .align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(item.cardColor, CircleShape)
                                        .border(1.dp, item.accentColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = item.emoji, fontSize = 24.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = DeepNavy
                                    )
                                    Text(
                                        text = item.arabTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = item.accentColor
                                    )
                                }
                                IconButton(onClick = { selectedDua = null }) {
                                    Text("✖", fontSize = 16.sp, color = SlateGrey)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // ARABIC TEXT CONTAINER (Beautiful Classroom style blackboard or premium slate)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (item.id != "first_kalima") {
                                            Modifier.clickable { onSoundTrigger("text:arabic:${item.arabic}") }
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .testTag("arabic_container_card"),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9FB)),
                                border = BorderStroke(1.2.dp, item.accentColor.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (item.id != "first_kalima") {
                                        Row(
                                            modifier = Modifier
                                                .background(item.accentColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                                .padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("🔊 ", fontSize = 16.sp)
                                            Text(
                                                text = if (isArabicLanguage) "اضغط للاستماع 🇸🇦" else "Tap to Play 🇸🇦",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = item.accentColor
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                    }

                                    Text(
                                        text = item.arabic,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepNavy,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 36.sp,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "“ ${item.transliteration} ”",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = SlateGrey,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // ENGLISH TRANSLATION CARD
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (item.id != "first_kalima") {
                                            Modifier.clickable { onSoundTrigger("text:english:${item.english}") }
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .testTag("english_translation_card"),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FA)),
                                border = BorderStroke(1.dp, BorderGrey)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    if (item.id != "first_kalima") {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(Color.White, CircleShape)
                                                .border(1.dp, BorderGrey, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("🔊", fontSize = 14.sp)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                    }
                                    Text(
                                        text = item.english,
                                        fontSize = 13.sp,
                                        color = DeepNavy,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // ADORABLE TIP FROM MASCOT
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(BadgeGrey, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🐝", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabicLanguage) item.arabTip else item.tip,
                                    fontSize = 11.sp,
                                    color = SlateGrey,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // AUDIO RECITATION CONTROLLERS (M3 Play Action button with high contrast)
                            if (item.id != "first_kalima") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Recite Arabic Audio Button
                                    Button(
                                        onClick = { onSoundTrigger("text:arabic:${item.arabic}") },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp)
                                            .testTag("play_arabic_audio"),
                                        colors = ButtonDefaults.buttonColors(containerColor = item.accentColor),
                                        shape = RoundedCornerShape(26.dp)
                                    ) {
                                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play Arabic")
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isArabicLanguage) "تلاوة عربية 🇸🇦" else "Arabic Recite 🇸🇦",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    // Recite English Audio Button
                                    OutlinedButton(
                                        onClick = { onSoundTrigger("text:english:${item.english}") },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp)
                                            .testTag("play_english_audio"),
                                        border = BorderStroke(1.5.dp, item.accentColor),
                                        shape = RoundedCornerShape(26.dp)
                                    ) {
                                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play English", tint = item.accentColor)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isArabicLanguage) "الترجمة 🇬🇧" else "English Translation 🇬🇧",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = item.accentColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Gamified Star Completion Reward button!
                            val isCompleted = completedList.contains(item.id)
                            Button(
                                onClick = {
                                    if (!isCompleted) {
                                        completedList = completedList + item.id
                                        onEarnRewards(5, 20, "Learned Dua: ${item.title}")
                                        // Play happy sounds and show celebratory feedback
                                        if (item.id != "first_kalima") {
                                            onSoundTrigger("text:arabic:ما شاء الله! أحسنت يا بطل لقد كسبت نجوماً وعسلاً!")
                                        }
                                        showCelebrate = true
                                    } else {
                                        if (item.id != "first_kalima") {
                                            onSoundTrigger("text:english:You already mastered this dua! Let's listen once more!")
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("dua_complete_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFFFD54F)
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isCompleted) Color(0xFF4CAF50) else Color(0xFFFFA000)
                                ),
                                shape = RoundedCornerShape(27.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Star",
                                    tint = if (isCompleted) Color(0xFF4CAF50) else Color(0xFFE65100),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isCompleted) {
                                        if (isArabicLanguage) "تم الحفظ! 🌟 (+٢٠ نجمة)" else "Already Mastered! 🌟 (+20 Stars)"
                                    } else {
                                        if (isArabicLanguage) "لقد حفظتها! 🌟 (+٢٠ نجمة و ٥🍯)" else "I Learned It! 🌟 (+20 Stars & 5🍯)"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isCompleted) Color(0xFF2E7D32) else Color(0xFF5D4037)
                                )
                            }
                        }
                    }
                }
            } // End of Column

            } // End of else block

            // Premium Noor Bee Mascot Celebration overlay
            NoorBeeCelebrationOverlay(
                visible = showCelebrate,
                coinsEarned = 5,
                starsEarned = 20,
                lessonName = if (isArabicLanguage) "حفظ الدعاء بنجاح! 🤲✨" else "Mastered Supplication! 🤲✨",
                currentLanguage = profile.currentLanguage,
                onDismiss = { showCelebrate = false },
                onSoundTrigger = { target ->
                    if (selectedDua?.id != "first_kalima") {
                        onSoundTrigger(target)
                    }
                }
            )
        }
    }
}

// --- Dua & Kalima Interactive Quiz Data & Layout Classes ---

data class DuaQuizQuestion(
    val id: String,
    val questionText: String,
    val questionTextAr: String,
    val options: List<String>,
    val correctAnswer: String,
    val optionsAr: List<String>,
    val correctAnswerAr: String,
    val infoTip: String,
    val infoTipAr: String,
    val emoji: String
)

fun generateQuizQuestionsForAge(age: Int): List<DuaQuizQuestion> {
    return when (age) {
        3 -> listOf(
            DuaQuizQuestion(
                id = "eating_dua_q",
                questionText = "What do we say before we eat yummy honey? 🍯",
                questionTextAr = "ماذا نقول قبل تناول الفواكه اللذيذة والعسل؟ 🍯",
                options = listOf("Bismillahir Rahmanir Rahim", "Alhamdulillah", "Subhanallah"),
                correctAnswer = "Bismillahir Rahmanir Rahim",
                optionsAr = listOf("بسم الله الرحمن الرحيم", "الحمد لله", "سبحان الله"),
                correctAnswerAr = "بسم الله الرحمن الرحيم",
                infoTip = "Start eating food by saying Bismillah to bless it!",
                infoTipAr = "ابدأ طعامك بالبسملة ليبارك الله فيه!",
                emoji = "🍯"
            ),
            DuaQuizQuestion(
                id = "first_kalima_q",
                questionText = "What is the beautiful First Kalima of faith? ☝️",
                questionTextAr = "ما هي الكلمة الطبية الأولى لشهادة الإيمان؟ ☝️",
                options = listOf("La ilaha illallahu Muhammadur Rasulullah", "Bismillah", "Alhamdulillah"),
                correctAnswer = "La ilaha illallahu Muhammadur Rasulullah",
                optionsAr = listOf("لا إله إلا الله محمد رسول الله", "بسم الله", "الحمد لله"),
                correctAnswerAr = "لا إله إلا الله محمد رسول الله",
                infoTip = "It means: There is no god but Allah, and Muhammad is His messenger.",
                infoTipAr = "ومعناها: لا إله إلا الله محمد رسول الله.",
                emoji = "☝️"
            ),
            DuaQuizQuestion(
                id = "sleeping_dua_q",
                questionText = "What is the cozy Dua you repeat before sleeping? 🛌",
                questionTextAr = "ما هو الدعاء الدافئ الذي تردده رغبة في نوم هادئ؟ 🛌",
                options = listOf("Bismika Allahumma amutu wa ahya", "Alhamdu lillahil-ladhi at'amana...", "Bismillah"),
                correctAnswer = "Bismika Allahumma amutu wa ahya",
                optionsAr = listOf("باسمك اللهم أموت وأحيا", "الحمد لله الذي أطعمنا...", "بسم الله"),
                correctAnswerAr = "باسمك اللهم أموت وأحيا",
                infoTip = "It means: In Your name, O Allah, I die and I live.",
                infoTipAr = "تعني: باسمك اللهم أموت وأحيا لرؤية أحلام سعيدة.",
                emoji = "🛌"
            ),
            DuaQuizQuestion(
                id = "sleeping_emoji_q",
                questionText = "Which cute emoji matches the 'Waking Up' supplication? ☀️",
                questionTextAr = "أي رمز لطيف يناسب دعاء الاستيقاظ من النوم؟ ☀️",
                options = listOf("☀️ Sun", "🛌 Bed", "🍉 Watermelon"),
                correctAnswer = "☀️ Sun",
                optionsAr = listOf("☀️ الشمس", "🛌 السرير", "🍉 البطيخ"),
                correctAnswerAr = "☀️ الشمس",
                infoTip = "Praise Allah who wakes us up to enjoy the glowing sun!",
                infoTipAr = "الحمد لله الذي أحيانا للاستمتاع بالشمس المشرقة والصباح السعيد!",
                emoji = "☀️"
            )
        )
        4 -> listOf(
            DuaQuizQuestion(
                id = "entering_home_q",
                questionText = "What do we recite when stepping inside our peaceful home? 🏠",
                questionTextAr = "ماذا نقرأ عند الدخول إلى منزلنا الهادئ والآمن؟ 🏠",
                options = listOf("Bismillahi walajna wa bismillahi kharajna...", "Bismillahi tawakkaltu...", "Alhamdu lillah"),
                correctAnswer = "Bismillahi walajna wa bismillahi kharajna...",
                optionsAr = listOf("بسم الله ولجنا وبسم الله خرجنا...", "بسم الله توكلت...", "الحمد لله"),
                correctAnswerAr = "بسم الله ولجنا وبسم الله خرجنا...",
                infoTip = "We ask Allah to bless our coming in and going out!",
                infoTipAr = "نسأل الله البركة والخير في دخولنا وخروجنا من المنزل!",
                emoji = "🏠"
            ),
            DuaQuizQuestion(
                id = "leaving_home_q",
                questionText = "What do we say when leaving the home to be protected by angels? 🚪",
                questionTextAr = "ماذا نقول عند الخروج من المنزل لحمايتنا؟ 🚪",
                options = listOf("Bismillahi tawakkaltu 'alallah, la hawla...", "Bismillahir Rahmanir Rahim", "Rabbi irhamhuma..."),
                correctAnswer = "Bismillahi tawakkaltu 'alallah, la hawla...",
                optionsAr = listOf("بسم الله توكلت على الله، لا حول...", "بسم الله الرحمن الرحيم", "رب ارحمهما..."),
                correctAnswerAr = "بسم الله توكلت على الله، لا حول...",
                infoTip = "Placing trust in Allah protects you all day long!",
                infoTipAr = "التوكل على الله يحفظك من كل مكروه طوال اليوم!",
                emoji = "🚪"
            ),
            DuaQuizQuestion(
                id = "clothes_dua_q",
                questionText = "What do we say to thank Allah for our cozy outfits? 👕",
                questionTextAr = "ماذا نقول لنشكر الله على ملابسنا الجميلة والدافئة؟ 👕",
                options = listOf("Alhamdu lillahil-ladhi kasani hadha...", "Bismika Allahumma amutu...", "Subhanallah"),
                correctAnswer = "Alhamdu lillahil-ladhi kasani hadha...",
                optionsAr = listOf("الحمد لله الذي كساني هذا الثوب...", "باسمك اللهم أموت...", "سبحان الله"),
                correctAnswerAr = "الحمد لله الذي كساني هذا الثوب...",
                infoTip = "Praise Allah who clothed me with clothes without power from me!",
                infoTipAr = "الحمد لله الذي كساني هذا الثوب الجميل ورزقنيه من غير حول مني ولا قوة!",
                emoji = "👕"
            ),
            DuaQuizQuestion(
                id = "parents_dua_q",
                questionText = "How do we ask Allah to show love and mercy to our Parents? 👪",
                questionTextAr = "كيف ندعو الله أن يرحم والدينا الكرام؟ 👪",
                options = listOf("Rabbi irhamhuma kama rabbayani saghira", "Rabbi zidni 'ilman", "Alhamdulillah"),
                correctAnswer = "Rabbi irhamhuma kama rabbayani saghira",
                optionsAr = listOf("رب ارحمهما كما ربياني صغيراً", "رب زدني علماً", "الحمد لله"),
                correctAnswerAr = "رب ارحمهما كما ربياني صغيراً",
                infoTip = "A beautiful prayer of love for Mommy and Daddy!",
                infoTipAr = "دعاء رائع مليء بالحب والامتنان لماما وبابا الطيبين!",
                emoji = "💖"
            )
        )
        5 -> listOf(
            DuaQuizQuestion(
                id = "seeking_knowledge_q",
                questionText = "How do we ask Allah to increase our knowledge? 🎓",
                questionTextAr = "كيف نسأل الله زيادة العلم والفهم الذكي؟ 🎓",
                options = listOf("Rabbi zidni 'ilman", "Subhanallah", "Alhamdulillah"),
                correctAnswer = "Rabbi zidni 'ilman",
                optionsAr = listOf("رب زدني علماً", "سبحان الله", "الحمد لله"),
                correctAnswerAr = "رب زدني علماً",
                infoTip = "It means: O my Lord! Increase me in knowledge.",
                infoTipAr = "ومعناه: رب زدني علماً وفهماً نافعاً.",
                emoji = "🎓"
            ),
            DuaQuizQuestion(
                id = "mosque_dua_q",
                questionText = "What do we say when entering the Mosque? 🕌",
                questionTextAr = "ماذا نقول عند دخول المسجد بقدمنا اليمنى؟ 🕌",
                options = listOf("Allahumma-ftah li abwaba rahmatika", "Bismika Allahumma...", "Alhamdulillah"),
                correctAnswer = "Allahumma-ftah li abwaba rahmatika",
                optionsAr = listOf("اللهم افتح لي أبواب رحمتك", "باسمك اللهم...", "الحمد لله"),
                correctAnswerAr = "اللهم افتح لي أبواب رحمتك",
                infoTip = "Asking Allah to open the gates of His infinite mercy!",
                infoTipAr = "نسأل الله ذو الجلال والإكرام أن يفتح لنا أبواب رحمته الواسعة!",
                emoji = "🕌"
            ),
            DuaQuizQuestion(
                id = "greeting_q",
                questionText = "What words of peace do we say when we meet a friend? 🤝",
                questionTextAr = "ما هي الكلمات الجميلة للسلام عند لقاء الأصدقاء؟ 🤝",
                options = listOf("As-salamu 'alaykum wa rahmatullah", "Alhamdu lillah", "Subhanallah"),
                correctAnswer = "As-salamu 'alaykum wa rahmatullah",
                optionsAr = listOf("السلام عليكم ورحمة الله", "الحمد لله", "سبحان الله"),
                correctAnswerAr = "السلام عليكم ورحمة الله",
                infoTip = "Greeting spreads love, warmth, and brotherhood!",
                infoTipAr = "إفشاء السلام ينشر المحبة والألفة بين الجميع!",
                emoji = "🤝"
            )
        )
        6 -> listOf(
            DuaQuizQuestion(
                id = "bravery_dua_q",
                questionText = "What do we say to feel brave like a lion, trusting Allah fully? 🦁",
                questionTextAr = "ماذا نقول للشعور بالقوة والشجاعة العظيمة متوكلين على الله؟ 🦁",
                options = listOf("Hasbiyallahu la ilaha illa Huwa...", "Subhanallah", "Bismillahi..."),
                correctAnswer = "Hasbiyallahu la ilaha illa Huwa...",
                optionsAr = listOf("حسبي الله لا إله إلا هو...", "سبحان الله", "بسم الله..."),
                correctAnswerAr = "حسبي الله لا إله إلا هو...",
                infoTip = "A mighty Dua: Sufficient for me is Allah; there is no deity except Him.",
                infoTipAr = "دعاء جليل: حسبي الله لا إله إلا هو عليه توكلت وهو رب العرش العظيم.",
                emoji = "🦁"
            ),
            DuaQuizQuestion(
                id = "patience_ease_q",
                questionText = "What is the Dua we say for patience and chest opening? 🌸",
                questionTextAr = "ما هو الدعاء لتيسير الأمور الصعبة وشرح الصدور بالسكينة؟ 🌸",
                options = listOf("Rabbi-shrah li sadri wa yassir li amri", "Alhamdu lillah", "Bismillah"),
                correctAnswer = "Rabbi-shrah li sadri wa yassir li amri",
                optionsAr = listOf("رب اشرح لي صدري ويسر لي أمري", "الحمد لله", "بسم الله"),
                correctAnswerAr = "رب اشرح لي صدري ويسر لي أمري",
                infoTip = "Ask Allah to make your speech clear and chest light!",
                infoTipAr = "نسأل الله أن يشرح لنا صدورنا وييسر لنا كل عسير!",
                emoji = "🌸"
            )
        )
        7 -> listOf(
            DuaQuizQuestion(
                id = "steadfast_heart_q",
                questionText = "Which Dua do we say to keep our heart steadfast and shining on Islam? 💖",
                questionTextAr = "ما الدعاء الذي نردده ليثبت الله قلوبنا الطاهرة على طاعته وإسلامنا الحبيب؟ 💖",
                options = listOf("Ya Muqallibal-qulubi thabbit qalbi 'ala dinik", "Subhanallah", "Alhamdulillah"),
                correctAnswer = "Ya Muqallibal-qulubi thabbit qalbi 'ala dinik",
                optionsAr = listOf("يا مقلب القلوب ثبت قلبي على دينك", "سبحان الله", "الحمد لله"),
                correctAnswerAr = "يا مقلب القلوب ثبت قلبي على دينك",
                infoTip = "Ask Allah to guide your heart to do kind and helpful deeds every day!",
                infoTipAr = "نسأل الله الهداية والتثبيت الدائم لقلوبنا لتفعل الخير وتتجنب الأذى!",
                emoji = "💖"
            ),
            DuaQuizQuestion(
                id = "gratitude_thanks_q",
                questionText = "What is the beautiful Quranic Dua we say to thank Allah for all favors? 🌟",
                questionTextAr = "ما هو الدعاء القرآني الرائع الذي نسأل الله فيه التوفيق لشكره وتقدير نعمه؟ 🌟",
                options = listOf("Rabbi awzi'ni an ashkura ni'matak...", "Bismillah", "Astaghfirullah"),
                correctAnswer = "Rabbi awzi'ni an ashkura ni'matak...",
                optionsAr = listOf("رب أوزعني أن أشكر نعمتك...", "بسم الله", "أستغفر الله"),
                correctAnswerAr = "رب أوزعني أن أشكر نعمتك...",
                infoTip = "Being thankful to Allah makes His blessings grow bigger and happier!",
                infoTipAr = "الشكر والامتنان يزيد النعم ويبسط الرزق والسعادة في المنزل والمدرسة!",
                emoji = "🌟"
            ),
            DuaQuizQuestion(
                id = "moses_speech_q",
                questionText = "Which majestic Moses Dua do we recite to talk confidently and clearly? 🎤",
                questionTextAr = "أي دعاء مأثور عن نبي الله موسى نقرأه لتيسير أمورنا وطلاقة لساننا للتعبير؟ 🎤",
                options = listOf("Rabbi-shrah li sadri...", "Bismillahi walajna...", "Alhamdu lillahil-ladhi kasani..."),
                correctAnswer = "Rabbi-shrah li sadri...",
                optionsAr = listOf("رب اشرح لي صدري...", "بسم الله ولجنا...", "الحمد لله الذي كساني..."),
                correctAnswerAr = "رب اشرح لي صدري...",
                infoTip = "Say this prayer of Moses (Musa) to feel calm, talk easily, and make friends happy!",
                infoTipAr = "ردد دعاء نبي الله موسى عليه السلام لتحدث طلاقة وفصاحة ويحب الناس الاستماع لكلامك الجميل!",
                emoji = "🎤"
            )
        )
        8 -> listOf(
            DuaQuizQuestion(
                id = "beneficial_knowledge_q",
                questionText = "Which Dua do we say in the morning to ask Allah for beneficial knowledge? 🎓",
                questionTextAr = "ما هو الدعاء المأثور الذي نسأل الله فيه في الصباح علماً نافعاً وعملاً متقبلاً؟ 🎓",
                options = listOf("Allahumma inni as'aluka 'ilman nafi'an...", "Alhamdulillah", "Subhanallah"),
                correctAnswer = "Allahumma inni as'aluka 'ilman nafi'an...",
                optionsAr = listOf("اللهم إني أسألك علماً نافعاً...", "الحمد لله", "سبحان الله"),
                correctAnswerAr = "اللهم إني أسألك علماً نافعاً...",
                infoTip = "Asking for beneficial knowledge helps us learn and help others!",
                infoTipAr = "سؤال الله العلم النافع يزيدنا حكمة وقدرة على نفع مجتمعنا وعائلتنا!",
                emoji = "🎓"
            ),
            DuaQuizQuestion(
                id = "protection_lazy_q",
                questionText = "Which Dua protects us from weakness and laziness? 🛡️",
                questionTextAr = "أي دعاء يحفظنا من العجز والكسل ويملأ يومنا بالنشاط والهمة؟ 🛡️",
                options = listOf("Allahumma inni a'udhu bika minal-'ajzi wal-kasal...", "Bismillah", "Astaghfirullah"),
                correctAnswer = "Allahumma inni a'udhu bika minal-'ajzi wal-kasal...",
                optionsAr = listOf("اللهم إني أعوذ بك من العجز والكسل...", "بسم الله", "أستغفر الله"),
                correctAnswerAr = "اللهم إني أعوذ بك من العجز والكسل...",
                infoTip = "This powerful prayer helps keep our bodies energetic and strong!",
                infoTipAr = "هذا الدعاء الرائع يساعدنا على طرد الكسل والقيام بواجباتنا بنشاط وهمة عالية!",
                emoji = "🛡️"
            ),
            DuaQuizQuestion(
                id = "light_paths_q",
                questionText = "What do we say to ask Allah to place light in our heart, hearing, and sight? ☀️",
                questionTextAr = "ماذا نقول لنسأل الله أن يجعل في قلوبنا وأبصارنا وأسماعنا نوراً ساطعاً؟ ☀️",
                options = listOf("Allahumma-j'al fi qalbi nuran...", "La ilaha illallah", "Alhamdulillah"),
                correctAnswer = "Allahumma-j'al fi qalbi nuran...",
                optionsAr = listOf("اللهم اجعل في قلبي نوراً...", "لا إله إلا الله", "الحمد لله"),
                correctAnswerAr = "اللهم اجعل في قلبي نوراً...",
                infoTip = "Asking for light in our heart helps us see and feel goodness everywhere!",
                infoTipAr = "نسأل الله النور لنرى الخير دائماً ونبتعد عن الشر والسوء في حياتنا!",
                emoji = "☀️"
            )
        )
        else -> emptyList() // ages are kept between 3 and 8, so this is never reached (it held a second copy of the age-7 entry)
    }
}

@Composable
fun DuaQuizArenaLayout(
    questions: List<DuaQuizQuestion>,
    currentIndex: Int,
    selectedIndex: Int?,
    isSubmitted: Boolean,
    onSelect: (Int) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    isCorrect: Boolean,
    isArabicLanguage: Boolean,
    onExit: () -> Unit,
    showSummary: Boolean,
    score: Int,
    onFinish: () -> Unit,
    onSoundTrigger: (String) -> Unit
) {
    val currentQuestion = questions[currentIndex]

    CompositionLocalProvider(LocalLayoutDirection provides if (isArabicLanguage) LayoutDirection.Rtl else LayoutDirection.Ltr) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("dua_quiz_arena_container")
        ) {
            // Quiz Header Row with Exit button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onExit,
                    modifier = Modifier.testTag("exit_quiz_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit Quiz",
                        tint = DeepNavy
                    )
                }

                Text(
                    text = if (isArabicLanguage) "تحدي نورا للأذكار 🏆" else "Noor Bee's Dua Challenge 🏆",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = DeepNavy
                )

                Text(
                    text = "${currentIndex + 1}/${questions.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = SlateGrey
                )
            }

            // Progress indicators
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / questions.size.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFFFFB300),
                trackColor = Color(0xFFF1F2F6),
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (showSummary) {
                // Success summary result board!
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .testTag("quiz_summary_card"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderGrey)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.size(110.dp)) {
                            AnimatedNoorBeeMascot(modifier = Modifier.fillMaxSize(), isCelebrating = true)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isArabicLanguage) "ما شاء الله! اكتمل التحدي! 🎉" else "Ma Sha Allah! Quiz Finished! 🎉",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color(0xFFE65100),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isArabicLanguage) "لقد أجبت بشكل رائع يا بطل!" else "You did a marvelous job learning supplications!",
                            fontSize = 12.sp,
                            color = SlateGrey,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Score result row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFFDE7), RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("⭐ Score", fontSize = 11.sp, color = SlateGrey)
                                Text("$score / ${questions.size}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = DeepNavy)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🍯 Earned", fontSize = 11.sp, color = SlateGrey)
                                Text("+${score * 10} Jars", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFFE65100))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("✨ Stars", fontSize = 11.sp, color = SlateGrey)
                                Text("+${score * 15}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF2196F3))
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onFinish,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("collect_rewards_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                            shape = RoundedCornerShape(27.dp)
                        ) {
                            Text(
                                text = if (isArabicLanguage) "جمع المكافآت والعسل! 🍯🌟" else "Collect My Honey Jars! 🍯🌟",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF3E2723)
                            )
                        }
                    }
                }
            } else {
                // Main Active Question screen
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("active_question_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
                    border = BorderStroke(1.dp, Color(0xFFFFE082))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(currentQuestion.emoji, fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabicLanguage) currentQuestion.questionTextAr else currentQuestion.questionText,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = Color(0xFF3E2723),
                                lineHeight = 18.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                val textToSpeak = if (isArabicLanguage) currentQuestion.questionTextAr else currentQuestion.questionText
                                onSoundTrigger("text:${if (isArabicLanguage) "arabic" else "english"}:$textToSpeak")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Speak Question",
                                tint = Color(0xFFE65100)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options List
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val currentOptions = if (isArabicLanguage) currentQuestion.optionsAr else currentQuestion.options
                    val currentCorrect = if (isArabicLanguage) currentQuestion.correctAnswerAr else currentQuestion.correctAnswer

                    currentOptions.forEachIndexed { idx, optionText ->
                        val isSelected = selectedIndex == idx
                        val borderCol = when {
                            isSubmitted && optionText == currentCorrect -> Color(0xFF4CAF50)
                            isSubmitted && isSelected && !isCorrect -> Color(0xFFE53935)
                            isSelected -> Color(0xFFFFB300)
                            else -> BorderGrey
                        }
                        val bgCol = when {
                            isSubmitted && optionText == currentCorrect -> Color(0xFFE8F5E9)
                            isSubmitted && isSelected && !isCorrect -> Color(0xFFFFEBEE)
                            isSelected -> Color(0xFFFFFDF0)
                            else -> Color.White
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isSubmitted) { onSelect(idx) }
                                .testTag("quiz_option_$idx"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = bgCol),
                            border = BorderStroke(if (isSelected || isSubmitted) 2.dp else 1.dp, borderCol)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            when {
                                                isSubmitted && optionText == currentCorrect -> Color(0xFF4CAF50)
                                                isSubmitted && isSelected && !isCorrect -> Color(0xFFE53935)
                                                else -> Color(0xFFF1F2F6)
                                            },
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ('A'.code + idx).toChar().toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isSubmitted && (optionText == currentCorrect || (isSelected && !isCorrect))) Color.White else SlateGrey
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Text(
                                    text = optionText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DeepNavy,
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(
                                    onClick = { onSoundTrigger("text:${if (isArabicLanguage) "arabic" else "english"}:$optionText") }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Speak option",
                                        tint = SlateGrey,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom action controller
                if (isSubmitted) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ),
                        border = BorderStroke(1.dp, if (isCorrect) Color(0xFF4CAF50) else Color(0xFFE53935))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isCorrect) {
                                    if (isArabicLanguage) "إجابة صحيحة! أحسنت! 🥳🐝" else "Excellent! That's Correct! 🥳🐝"
                                } else {
                                    if (isArabicLanguage) "تلميح مفيد من نورا: ✨" else "Noor Bee's Tip: ✨"
                                },
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                            Text(
                                text = if (isArabicLanguage) currentQuestion.infoTipAr else currentQuestion.infoTip,
                                fontSize = 11.sp,
                                color = SlateGrey,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Button(
                        onClick = onNext,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("quiz_next_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        shape = RoundedCornerShape(27.dp)
                    ) {
                        Text(
                            text = if (currentIndex == questions.size - 1) {
                                if (isArabicLanguage) "رؤية النتيجة 🏆" else "See Results 🏆"
                            } else {
                                if (isArabicLanguage) "السؤال التالي ➡️" else "Next Question ➡️"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF3E2723)
                        )
                    }
                } else {
                    Button(
                        onClick = onSubmit,
                        enabled = selectedIndex != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("quiz_submit_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedIndex != null) Color(0xFFFFB300) else Color(0xFFF1F2F6)
                        ),
                        shape = RoundedCornerShape(27.dp)
                    ) {
                        Text(
                            text = if (isArabicLanguage) "تحقق من الإجابة ☑️" else "Check Answer ☑️",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (selectedIndex != null) Color(0xFF3E2723) else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

