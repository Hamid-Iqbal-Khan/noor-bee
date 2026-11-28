package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChildProfile
import com.example.data.UnlockedOutfit
import com.example.ui.StickerNode
import kotlinx.coroutines.launch

// Custom drawn path data class for the interactive Whiteboard brush strokes
data class DrawPath(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreens(
    profile: ChildProfile,
    section: String, // "wardrobe", "stickers", "achievements"
    unlockedOutfits: List<UnlockedOutfit>,
    stickersOnPlayground: List<StickerNode>,
    onBackPressed: () -> Unit,
    onAddSticker: (String, Float, Float) -> Unit,
    onClearStickers: () -> Unit,
    onBuyOutfit: (String, Int) -> Unit, // cost
    onEquipOutfit: (String) -> Unit,
    onSoundTrigger: (String) -> Unit = {},
    currentBeeColor: String = "default",
    onUpdateBeeColor: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (section) {
                            "wardrobe" -> "Bee Customizer Wardrobe 👑"
                            "stickers" -> "Phonics Whiteboard 🎨"
                            "achievements" -> "My Achievement Trophies 🏆"
                            else -> "My Rewards Shelf"
                        },
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF3E2723)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFFDE7))
            )
        },
        containerColor = Color(0xFFFFFAEC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .testTag("rewards_screen_container")
        ) {
            when (section) {
                "wardrobe" -> {
                    WardrobeLayout(
                        profile = profile,
                        unlockedOutfits = unlockedOutfits,
                        onBuy = onBuyOutfit,
                        onEquip = onEquipOutfit,
                        currentBeeColor = currentBeeColor,
                        onUpdateBeeColor = onUpdateBeeColor
                    )
                }
                "stickers" -> {
                    PhonicsWhiteboardLayout(
                        profile = profile,
                        onSoundTrigger = onSoundTrigger
                    )
                }
                "achievements" -> {
                    AchievementsGrid(profile = profile)
                }
            }
        }
    }
}

// --- Section 1: Custom Character Outfit Dresser --
@Composable
fun ColumnScope.WardrobeLayout(
    profile: ChildProfile,
    unlockedOutfits: List<UnlockedOutfit>,
    onBuy: (String, Int) -> Unit,
    onEquip: (String) -> Unit,
    currentBeeColor: String = "default",
    onUpdateBeeColor: (String) -> Unit = {}
) {
    val itemsToBuy = listOf(
        OutfitProduct("classic_bee", "Classic 🐝", 0, "Default styling"),
        OutfitProduct("super_bee", "Superhero Cloak 🦸‍♂️", 15, "Fly with style!"),
        OutfitProduct("chef_hat", "Chef Hat 🍳", 20, "Fresh baked cookies!"),
        OutfitProduct("painter_hat", "Painter Beret 🎨", 25, "Paint the sky yellow"),
        OutfitProduct("space_helmet", "Space Helmet 🚀", 35, "Reach the outer orbit"),
        OutfitProduct("royal_crown", "Royal Crown 👑", 50, "King/Queen of the hive")
    )

    // Current equipped outfit ID
    val currentEquippedId = unlockedOutfits.find { it.isEquipped }?.outfitId ?: "classic_bee"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(20.dp))
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Large live dressed preview
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color(0xFFFFF9C4), CircleShape)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            NoorBeeMascot(modifier = Modifier.fillMaxSize())
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Styling Station",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = Color(0xFF3E2723)
            )
            Text(
                text = "My Outfits: ${unlockedOutfits.size} Options",
                fontSize = 12.sp,
                color = Color.Gray
            )

            // Honey Comb coins balance
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🍯 Balance:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${profile.coins} Honey Jars",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = Color(0xFFE65100)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Interactive Mascot Color customizer station
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Noor Bee's Profile Colors 🌟",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF3E2723)
            )
            Text(
                text = "Tap a magic color bubble to customize your mascot profile!",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val colorOptions = listOf(
                Triple("default", "Gold Honey", Color(0xFFFFB300)),
                Triple("royal_purple", "Royal Purple", Color(0xFF9C27B0)),
                Triple("emerald_green", "Emerald Green", Color(0xFF4CAF50)),
                Triple("cosmic_pink", "Neon Pink", Color(0xFFE91E63)),
                Triple("ocean_blue", "Azure Blue", Color(0xFF00ACC1))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                colorOptions.forEach { option ->
                    val isSelected = currentBeeColor == option.first
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onUpdateBeeColor(option.first) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(option.third, CircleShape)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF3E2723) else Color(0x22111111),
                                    shape = CircleShape
                                )
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Active",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = option.second,
                            fontSize = 8.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFFE65100) else Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Dress Up Noor Bee! 🐝",
        fontSize = 16.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF3E2723),
        modifier = Modifier.padding(bottom = 8.dp)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(itemsToBuy) { item ->
            val isUnlocked = unlockedOutfits.any { it.outfitId == item.id }
            val isCurrentlyEquipped = currentEquippedId == item.id

            val borderCol = when {
                isCurrentlyEquipped -> Color(0xFFFFB300)
                isUnlocked -> Color(0xFF4CAF50)
                else -> Color(0xFFE0E0E0)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clickable {
                        if (isUnlocked) onEquip(item.id)
                        else if (profile.coins >= item.cost) onBuy(item.id, item.cost)
                    },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, borderCol),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrentlyEquipped) Color(0xFFFFFDE7) else Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        item.emojiSymbol(),
                        fontSize = 32.sp
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            item.title,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = Color(0xFF3E2723),
                            textAlign = TextAlign.Center
                        )
                        if (isCurrentlyEquipped) {
                            Text(
                                "Equipped Check",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFB300)
                            )
                        } else if (isUnlocked) {
                            Text(
                                "Equip Outfit • Free",
                                fontSize = 10.sp,
                                color = Color(0xFF4CAF50)
                            )
                        } else {
                            Text(
                                "Unlock: 🍯 ${item.cost}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (profile.coins >= item.cost) Color(0xFFE65100) else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper Outfits item products model
data class OutfitProduct(
    val id: String,
    val title: String,
    val cost: Int,
    val desc: String
) {
    fun emojiSymbol(): String {
        return when (id) {
            "classic_bee" -> "🐝"
            "super_bee" -> "🛡️"
            "chef_hat" -> "🍳"
            "painter_hat" -> "🎨"
            "space_helmet" -> "🚀"
            "royal_crown" -> "👑"
            else -> "🧢"
        }
    }
}

// --- Section 2: Phonics Interactive Handwriting Whiteboard --
@Composable
fun ColumnScope.PhonicsWhiteboardLayout(
    profile: ChildProfile,
    onSoundTrigger: (String) -> Unit
) {
    val profile = profile.copy(age = profile.age.coerceIn(3, 8))
    var isArabicLanguage by remember { mutableStateOf(profile.currentLanguage == "Arabic") }
    
    val englishLetters = remember { ('A'..'Z').map { it.toString() } }
    val arabicLetters = remember {
        listOf(
            "أ", "ب", "ت", "ث", "ج", "ح", "خ", "د", "ذ", "ر", "ز", "س", "ش", "ص", "ض", "ط", "ظ", "ع", "غ", "ف", "ق", "ك", "ل", "م", "ن", "هـ", "و", "ي"
        )
    }

    val englishLettersAgeSix = remember {
        listOf("CH", "SH", "TH", "PH", "WH", "QU", "AI", "EE", "OO", "AR")
    }

    val englishLettersAgeSeven = remember {
        listOf("IGH", "ING", "ANK", "ALL", "OUT", "AY", "OY", "AW", "OW", "IGHT")
    }

    val englishLettersAgeEight = remember {
        listOf("TION", "SION", "OUGH", "AIGH", "EIGH", "OUSE", "AL", "EL", "IL", "OL")
    }

    val arabicLettersAgeSix = remember {
        listOf("ال", "أو", "إي", "تش", "لا", "شّ", "خّ", "غّ", "ضّ", "ظّ")
    }

    val arabicLettersAgeSeven = remember {
        listOf("ـاً", "ـٍ", "ـٌ", "بّ", "رّ", "قّ", "تَين", "يَا", "ذّ", "مّ")
    }

    val arabicLettersAgeEight = remember {
        listOf("ـٰ", "ـّاً", "ـَّ", "ـِّ", "ـُّ", "هُم", "كُم", "نَا", "هَذَا", "ذَلِكَ")
    }

    val wordPhonicsMapEng = remember {
        mapOf(
            "A" to Pair("Aa • Allah ⭐", "A is for Allah. Allah is our Creator."),
            "B" to Pair("Bb • Bee 🐝", "B is for Bee. Bee flies in the garden."),
            "C" to Pair("Cc • Charity 🤝", "C is for Charity. We share with love."),
            "D" to Pair("Dd • Dua 🤲", "D is for Dua. We call upon Allah."),
            "E" to Pair("Ee • Eid 🎉", "E is for Eid. A very happy celebration!"),
            "F" to Pair("Ff • Fajr 🌅", "F is for Fajr. Beautiful early morning prayer."),
            "G" to Pair("Gg • Ghusl 🚿", "G is for Ghusl. Clean and pure body."),
            "H" to Pair("Hh • Hajj 🕋", "H is for Hajj. Tracing to the Kaaba."),
            "I" to Pair("Ii • Iman 💚", "I is for Iman. Believing with all your heart."),
            "J" to Pair("Jj • Jannah 🌺", "J is for Jannah. Beautiful paradise for kind people."),
            "K" to Pair("Kk • Kaaba 🕋", "K is for Kaaba. House of peace in Makkah."),
            "L" to Pair("Ll • Love 💖", "L is for Love. Love Allah and your family."),
            "M" to Pair("Mm • Masjid 🕌", "M is for Masjid. We pray in the mosque."),
            "N" to Pair("Nn • Nabi 📖", "N is for Nabi. Chosen messenger of guidance."),
            "O" to Pair("Oo • Owl 🦉", "O is for Owl. Wise bird of the night."),
            "P" to Pair("Pp • Pray 🛐", "P is for Pray. Praying makes our heart pure."),
            "Q" to Pair("Qq • Quran 📗", "Q is for Quran. The Holy Book of guidance."),
            "R" to Pair("Rr • Ramadan 🌙", "R is for Ramadan. The month of fasting and smiles."),
            "S" to Pair("Ss • Salah 🛐", "S is for Salah. The key to Jannah."),
            "T" to Pair("Tt • Truth ✨", "T is for Truth. Always speak the truth."),
            "U" to Pair("Uu • Ummah 🤝", "U is for Ummah. One friendly family."),
            "V" to Pair("Vv • Virtue 💖", "V is for Virtue. Good deeds grow in hearts."),
            "W" to Pair("Ww • Wudu 💧", "W is for Wudu. Washing up for prayer."),
            "X" to Pair("Xx • eXcellent 🏆", "X is for eXcellent. Always do your best!"),
            "Y" to Pair("Yy • Yaqeen 💛", "Y is for Yaqeen. Trusting Allah completely."),
            "Z" to Pair("Zz • Zakat 💰", "Z is for Zakat. Share with the poor.")
        )
    }

    val wordPhonicsMapAr = remember {
        mapOf(
            "أ" to Pair("أَ • الله ☝️", "ألف ... الله ربي وإلهي"),
            "ب" to Pair("بَ • بسم الله 🍽️", "باء ... بسم الله في بداية العمل"),
            "ت" to Pair("تَ • تقوى 💖", "تاء ... تقوى تملأ قلبي طاعة"),
            "ث" to Pair("ثَ • ثواب 🏆", "ثاء ... ثواب لفاعل الخير دائماً"),
            "ج" to Pair("جَ • جنة 🌺", "جيم ... جنة بيت للصالحين"),
            "ح" to Pair("حَ • حج 🕋", "حاء ... حج لبيت الله الحرام"),
            "خ" to Pair("خَ • خير ✨", "خاء ... خير نحبه لكل الناس"),
            "د" to Pair("دَ • دعاء 🤲", "دال ... دعاء فيه الخير والبركة"),
            "ذ" to Pair("ذَ • ذكر 📿", "ذال ... ذكر الله يشرح الصدور"),
            "ر" to Pair("رَ • رمضان 🌙", "راء ... رمضان شهر الصوم والقرآن"),
            "ز" to Pair("زَ • زكاة 💰", "زاي ... زكاة تطهر نفوسنا وأموالنا"),
            "س" to Pair("سَ • سجود 🛐", "سين ... سجود نقترب به من خالقنا"),
            "ش" to Pair("شَ • شكر 🙌", "شين ... شكر وحمد لله على نعمه"),
            "ص" to Pair("صَ • صلاة 🕌", "صاد ... صلاة عماد الدين ونور الصدر"),
            "ض" to Pair("ضَ • ضحى ☀️", "ضاد ... ضحى وقت شروق الشمس والنشاط"),
            "ط" to Pair("طَ • طهارة 💧", "طاء ... طهارة البدن والثوب والمكان"),
            "ظ" to Pair("ظَ • ظهر ☀️", "ظاء ... ظهر وقت الصلاة والعمل الصالح"),
            "ع" to Pair("عَ • عدل ⚖️", "عين ... عدل وإعطاء كل ذي حق حقه"),
            "غ" to Pair("غَ • غفران 🤲", "غين ... غفران ورحمة من الله الخالق"),
            "ف" to Pair("فَ • فجر 🌅", "فاء ... فجر بداية الصباح الجميل"),
            "ق" to Pair("قَ • قرآن 📗", "قاف ... قرآن كلام الله المُنزل الهادي"),
            "ك" to Pair("كَ • كعبة 🕋", "كاف ... كعبة قبلتنا المباركة"),
            "ل" to Pair("لَ • ليلة القدر 🌌", "لام ... ليلة القدر خير من ألف شهر"),
            "م" to Pair("مَ • مسجد 🕌", "ميم ... مسجد نطهر فيه القلوب"),
            "ن" to Pair("نَ • نبي 📖", "نون ... نبي أرسله الله هداية لنا"),
            "هـ" to Pair("هَـ • هداية 🌟", "هاء ... هداية تمهد طريق الخير"),
            "و" to Pair("وَ • وضوء 💧", "واو ... وضوء يطهر الأعضاء بالماء"),
            "ي" to Pair("يَ • يقين 💛", "ياء ... يقين وثقة تامة برضا ربي")
        )
    }

    val wordPhonicsMapEngAgeSix = remember {
        mapOf(
            "CH" to Pair("CH • Charity 🤝", "Ch sound for Charity! We share with love."),
            "SH" to Pair("SH • Shield 🛡️", "Sh sound for Shield! Faith is our shield."),
            "TH" to Pair("TH • Thankful 💖", "Th sound for Thankful! Be thankful to Allah."),
            "PH" to Pair("PH • Phone 📱", "Ph sound for Phone! Call your grandparents."),
            "WH" to Pair("WH • Whale 🐳", "Wh sound for Whale! Allah created the massive whale."),
            "QU" to Pair("QU • Queen 👑", "Qu sound for Queen! Respect your mother and queen."),
            "AI" to Pair("AI • Rain 🌧️", "Ai sound for Rain! Rain is a blessing from Allah."),
            "EE" to Pair("EE • Deep 🌊", "Ee sound for Deep! Deeply loving everyone for peace."),
            "OO" to Pair("OO • Book 📖", "Oo sound for Book! Read and learn clean knowledge."),
            "AR" to Pair("AR • Star ⭐", "Ar sound for Star! Look at the shining star.")
        )
    }

    val wordPhonicsMapArAgeSix = remember {
        mapOf(
            "ال" to Pair("الـ • التعريف 🌟", "الـ ... ال للتعريف والكمال كالعلم والنور في حياتنا"),
            "أو" to Pair("أو • الاختيار 🌿", "أو ... للاختيار والتدبر والبحث عن طرق الخير والبر"),
            "إي" to Pair("إي • إيمان 💚", "إي ... كلمة الإيمان واليقين الصادق بوجود خالقنا وحده"),
            "تش" to Pair("تش • تشجيع 🏆", "تش ... تشجيع وإعجاب بعملك الصالح ومساندة أصدقائك الصغار"),
            "لا" to Pair("لا • لا إله إلا الله ☝️", "لا ... كلمة التوحيد الخالدة لا إله إلا الله الملك الحق المبين"),
            "شّ" to Pair("شّ • شَدَّة 🐝", "شين مع الشدة لتقوية اللفظ ونطق كلام متميز وجميل"),
            "خّ" to Pair("خّ • فخر ✨", "خاء مع الشدة لنطق فخر واعتزاز بعملنا وأخلاقنا الفاضلة"),
            "غّ" to Pair("غّ • غسل 🚿", "غين مع الشدة للتأكيد على النظافة والطهور الدائم"),
            "ضّ" to Pair("ضّ • ضياء ☀️", "ضاد مع الشدة للضياء والنور الساطع الذي يملأ الأرجاء"),
            "ظّ" to Pair("ظّ • حظ 🏆", "ظاء مع الشدة للحفظ والتوفيق العالي من الله ربي")
        )
    }

    val wordPhonicsMapEngAgeSeven = remember {
        mapOf(
            "IGH" to Pair("IGH • Light ✨", "Igh sound for Light! Allah is the Light of the heavens and earth."),
            "ING" to Pair("ING • King 👑", "Ing sound for King! Allah is Al-Malik, the absolute King."),
            "ANK" to Pair("ANK • Thankful 🤲", "Ank sound for Thankful! Always thank Allah with 'Alhamdulillah'."),
            "ALL" to Pair("ALL • Allah ☝️", "All sound for Allah! Allah is the One, the Only, Al-Khaliq."),
            "OUT" to Pair("OUT • Outing 🚗", "Out sound for Outing! We step out with a beautiful travel dua."),
            "AY" to Pair("AY • Pray 🛐", "Ay sound for Pray! Praying everyday brings peaceful smiles."),
            "OY" to Pair("OY • Joy 🎉", "Oy sound for Joy! Doing good deeds fills our heart with joy."),
            "AW" to Pair("AW • Dawn 🌅", "Aw sound for Dawn! Dawn is when we pray beautiful Fajr."),
            "OW" to Pair("OW • Bow 🛐", "Ow sound for Bow! We bow in Ruku to show respect to Allah."),
            "IGHT" to Pair("IGHT • Night 🌌", "Ight sound for Night! Sleep in peace during the quiet night.")
        )
    }

    val wordPhonicsMapArAgeSeven = remember {
        mapOf(
            "ـاً" to Pair("ـاً • تنوين فتح 🌟", "التنوين بالفتح كـ شكراً وعفواً لله دائماً"),
            "ـٍ" to Pair("ـٍ • تنوين كسر 💎", "التنوين بالكسر كـ بسلامٍ وإحسانٍ مبارك"),
            "ـٌ" to Pair("ـٌ • تنوين ضم 🏆", "التنوين بالضم كـ نورٌ هادٍ لقلوبنا الطاهرة"),
            "بّ" to Pair("بّ • حبّ 💖", "باء مع الشدة لنطق حبّ ومودة وتراحم بين القلوب"),
            "رّ" to Pair("رّ • برّ 🌿", "راء مع الشدة لبرّ الوالدين وطاعة أوامرهما بالحب والخير"),
            "قّ" to Pair("قّ • حقّ ⚖️", "قاف مع الشدة لنطق حقّ وعدل ينير حياتنا دائماً"),
            "تَين" to Pair("تَين • صلاتين 🕌", "لتثنية الصلوات كـ صلاتين مباركتين في اليوم"),
            "يَا" to Pair("يَا • نداء 🤲", "يا للنداء والابتهال كـ يا الله ويا رحمن ارحمنا"),
            "ذّ" to Pair("ذّ • لذّة 🍯", "ذال مع الشدة للذّة وحلاوة الإيمان وطعم العسل"),
            "مّ" to Pair("مّ • أمّ 💖", "ميم مع الشدة لنطق أمّ رحيمة هي كنز ونور بيتنا")
        )
    }

    val wordPhonicsMapEngAgeEight = remember {
        mapOf(
            "TION" to Pair("TION • Creation 🌍", "Tion sound for Creation! Allah is Al-Khaliq, Creator of all."),
            "SION" to Pair("SION • Compassion 💖", "Sion sound for Compassion! Be kind and show mercy to animals."),
            "OUGH" to Pair("OUGH • Enough 🥛", "Ough sound for Enough! Allah's blessings are always enough for us."),
            "AIGH" to Pair("AIGH • Straight 🛣️", "Aigh sound for Straight! We ask Allah for the Straight Path."),
            "EIGH" to Pair("EIGH • Neighbor 🏠", "Eigh sound for Neighbor! Prophet Muhammad (PBUH) taught us to respect neighbors."),
            "OUSE" to Pair("OUSE • House 🕌", "Ouse sound for House! Praying at home brings safety and glowing light."),
            "AL" to Pair("AL • Eternal ♾️", "Al sound for Eternal! Allah is Al-Baqi, the Everlasting and Eternal."),
            "EL" to Pair("EL • Angel 👼", "El sound for Angel! Angels are made of light and obey Allah."),
            "IL" to Pair("IL • Pupil 👁️", "Il sound for Pupil! Our eyes are miraculous blessings to appreciate."),
            "OL" to Pair("OL • Control 🎮", "Ol sound for Control! Self-control (Sabr) is a beautiful virtue.")
        )
    }

    val wordPhonicsMapArAgeEight = remember {
        mapOf(
            "ـٰ" to Pair("ـٰ • ألف خنجرية 📖", "الألف الخنجرية في كلمات مثل (الرَّحْمَٰن) و(إِلَٰه)"),
            "ـّاً" to Pair("ـّاً • شدة وتنوين 🏆", "شدة مع تنوين فتح كـ (حبّاً جمّاً)"),
            "ـَّ" to Pair("ـَّ • شدة وفتحة 🌿", "شدة مع فتحة كـ (علَّمَ) و(سبَّحَ)"),
            "ـِّ" to Pair("ـِّ • شدة وكسرة 💎", "شدة مع كسرة كـ (علِّم) و(ربِّ)"),
            "ـُّ" to Pair("ـُّ • شدة وضمة 🌟", "شدة مع ضمة كـ (يحبُّ) و(يدعُّ)"),
            "هُم" to Pair("هُم • ضمير الغائبين 👥", "ضمير الجماعة كـ (عَلَيْهِمْ) و(فِيهِمْ)"),
            "كُم" to Pair("كُم • ضمير المخاطبين 🤝", "ضمير للمخاطبين كـ (عَلَيْكُمْ) و(دِينُكُمْ)"),
            "نَا" to Pair("نَا • ضمير المتكلمين 🌍", "ضمير المتكلمين كـ (إِنَّا) و(خَلَقْنَا)"),
            "هَذَا" to Pair("هَذَا • اسم إشارة ✨", "هذا اسم إشارة للمذكر القريب كـ (هَذَا خَلْقُ اللَّهِ)"),
            "ذَلِكَ" to Pair("ذَلِكَ • اسم إشارة للبعيد 🚀", "ذلك اسم إشارة للبعيد كـ (ذَلِكَ الْكِتَابُ لَا رَيْبَ فِيهِ)")
        )
    }

    val currentList = if (profile.age >= 8) {
        if (isArabicLanguage) arabicLettersAgeEight else englishLettersAgeEight
    } else if (profile.age == 7) {
        if (isArabicLanguage) arabicLettersAgeSeven else englishLettersAgeSeven
    } else if (profile.age == 6) {
        if (isArabicLanguage) arabicLettersAgeSix else englishLettersAgeSix
    } else {
        if (isArabicLanguage) arabicLetters else englishLetters
    }

    var selectedLetter by remember {
        mutableStateOf(
            if (profile.age >= 8) {
                if (isArabicLanguage) "ـٰ" else "TION"
            } else if (profile.age == 7) {
                if (isArabicLanguage) "ـاً" else "IGH"
            } else if (profile.age == 6) {
                if (isArabicLanguage) "ال" else "CH"
            } else {
                if (isArabicLanguage) "أ" else "A"
            }
        )
    }

    // State for drawn paths
    val completedPaths = remember { mutableStateListOf<DrawPath>() }
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    
    // Brush State variables
    var currentColor by remember { mutableStateOf(Color(0xFFE53935)) } // Red Default
    var currentStrokeWidth by remember { mutableStateOf(16f) } // Medium default

    val colorsPalette = listOf(
        Color(0xFFE53935), // Red
        Color(0xFF1E88E5), // Blue
        Color(0xFF43A047), // Green
        Color(0xFFFFB300), // Yellow Gold
        Color(0xFF8E24AA), // Purple
        Color(0xFF212121)  // Dark Charcoal
    )



    var boardWidth by remember { mutableStateOf(500f) }
    var boardHeight by remember { mutableStateOf(500f) }

    fun getLetterWaypointsSingle(letter: String, isArabic: Boolean): List<Offset> {
        if (isArabic) {
            return when (letter) {
                "أ", "ا", "إ" -> listOf(Offset(0.5f, 0.2f), Offset(0.5f, 0.45f), Offset(0.5f, 0.7f))
                "ب" -> listOf(Offset(0.75f, 0.5f), Offset(0.5f, 0.65f), Offset(0.25f, 0.5f), Offset(0.5f, 0.85f))
                "ت" -> listOf(Offset(0.75f, 0.5f), Offset(0.5f, 0.65f), Offset(0.25f, 0.5f), Offset(0.45f, 0.35f), Offset(0.55f, 0.35f))
                "ث" -> listOf(Offset(0.75f, 0.5f), Offset(0.5f, 0.65f), Offset(0.25f, 0.5f), Offset(0.5f, 0.3f), Offset(0.42f, 0.4f), Offset(0.58f, 0.4f))
                "ج" -> listOf(Offset(0.3f, 0.3f), Offset(0.6f, 0.3f), Offset(0.7f, 0.55f), Offset(0.5f, 0.8f), Offset(0.3f, 0.65f), Offset(0.5f, 0.55f))
                "ح" -> listOf(Offset(0.3f, 0.3f), Offset(0.6f, 0.3f), Offset(0.7f, 0.55f), Offset(0.5f, 0.8f), Offset(0.3f, 0.65f))
                "خ" -> listOf(Offset(0.3f, 0.3f), Offset(0.6f, 0.3f), Offset(0.7f, 0.55f), Offset(0.5f, 0.8f), Offset(0.3f, 0.65f), Offset(0.5f, 0.15f))
                "د" -> listOf(Offset(0.65f, 0.4f), Offset(0.4f, 0.6f), Offset(0.65f, 0.7f))
                "ذ" -> listOf(Offset(0.65f, 0.4f), Offset(0.4f, 0.6f), Offset(0.65f, 0.7f), Offset(0.5f, 0.25f))
                "ر" -> listOf(Offset(0.6f, 0.45f), Offset(0.5f, 0.65f), Offset(0.35f, 0.85f))
                "ز" -> listOf(Offset(0.6f, 0.45f), Offset(0.5f, 0.65f), Offset(0.35f, 0.85f), Offset(0.6f, 0.3f))
                "س" -> listOf(Offset(0.75f, 0.4f), Offset(0.6f, 0.4f), Offset(0.45f, 0.4f), Offset(0.3f, 0.6f), Offset(0.45f, 0.8f), Offset(0.6f, 0.7f))
                "ش" -> listOf(Offset(0.75f, 0.4f), Offset(0.6f, 0.4f), Offset(0.45f, 0.4f), Offset(0.3f, 0.6f), Offset(0.45f, 0.8f), Offset(0.6f, 0.25f), Offset(0.5f, 0.25f))
                "ص" -> listOf(Offset(0.5f, 0.5f), Offset(0.75f, 0.45f), Offset(0.5f, 0.45f), Offset(0.3f, 0.6f), Offset(0.45f, 0.8f))
                "ض" -> listOf(Offset(0.5f, 0.5f), Offset(0.75f, 0.45f), Offset(0.5f, 0.45f), Offset(0.3f, 0.6f), Offset(0.45f, 0.8f), Offset(0.65f, 0.3f))
                "ط" -> listOf(Offset(0.4f, 0.5f), Offset(0.65f, 0.45f), Offset(0.4f, 0.45f), Offset(0.55f, 0.25f), Offset(0.55f, 0.5f))
                "ظ" -> listOf(Offset(0.4f, 0.5f), Offset(0.65f, 0.45f), Offset(0.4f, 0.45f), Offset(0.55f, 0.25f), Offset(0.55f, 0.5f), Offset(0.7f, 0.3f))
                "ع" -> listOf(Offset(0.65f, 0.35f), Offset(0.5f, 0.25f), Offset(0.4f, 0.45f), Offset(0.65f, 0.65f), Offset(0.45f, 0.8f))
                "غ" -> listOf(Offset(0.65f, 0.35f), Offset(0.5f, 0.25f), Offset(0.4f, 0.45f), Offset(0.65f, 0.65f), Offset(0.45f, 0.8f), Offset(0.55f, 0.15f))
                "ف" -> listOf(Offset(0.35f, 0.45f), Offset(0.45f, 0.45f), Offset(0.65f, 0.5f), Offset(0.45f, 0.65f), Offset(0.25f, 0.5f), Offset(0.4f, 0.25f))
                "ق" -> listOf(Offset(0.35f, 0.45f), Offset(0.45f, 0.45f), Offset(0.55f, 0.65f), Offset(0.35f, 0.75f), Offset(0.25f, 0.55f), Offset(0.35f, 0.25f), Offset(0.45f, 0.25f))
                "ك" -> listOf(Offset(0.65f, 0.25f), Offset(0.65f, 0.7f), Offset(0.35f, 0.7f), Offset(0.45f, 0.5f))
                "ل" -> listOf(Offset(0.65f, 0.25f), Offset(0.65f, 0.7f), Offset(0.45f, 0.85f), Offset(0.35f, 0.75f))
                "م" -> listOf(Offset(0.6f, 0.55f), Offset(0.45f, 0.55f), Offset(0.45f, 0.75f), Offset(0.45f, 0.95f))
                "ن" -> listOf(Offset(0.7f, 0.45f), Offset(0.5f, 0.65f), Offset(0.3f, 0.45f), Offset(0.5f, 0.35f))
                "هـ" -> listOf(Offset(0.5f, 0.35f), Offset(0.35f, 0.55f), Offset(0.5f, 0.75f), Offset(0.65f, 0.55f), Offset(0.5f, 0.55f))
                "و" -> listOf(Offset(0.6f, 0.45f), Offset(0.45f, 0.55f), Offset(0.4f, 0.75f), Offset(0.3f, 0.85f))
                "ي" -> listOf(Offset(0.65f, 0.45f), Offset(0.5f, 0.55f), Offset(0.35f, 0.75f), Offset(0.45f, 0.85f), Offset(0.55f, 0.85f))
                else -> listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.5f), Offset(0.5f, 0.7f))
            }
        } else {
            return when (letter.uppercase()) {
                "A" -> listOf(Offset(0.5f, 0.25f), Offset(0.35f, 0.55f), Offset(0.65f, 0.55f), Offset(0.25f, 0.8f), Offset(0.75f, 0.8f))
                "B" -> listOf(Offset(0.35f, 0.25f), Offset(0.35f, 0.5f), Offset(0.35f, 0.8f), Offset(0.55f, 0.35f), Offset(0.55f, 0.65f))
                "C" -> listOf(Offset(0.65f, 0.35f), Offset(0.45f, 0.25f), Offset(0.35f, 0.5f), Offset(0.45f, 0.75f), Offset(0.65f, 0.65f))
                "D" -> listOf(Offset(0.35f, 0.25f), Offset(0.35f, 0.8f), Offset(0.55f, 0.3f), Offset(0.6f, 0.5f), Offset(0.55f, 0.7f))
                "E" -> listOf(Offset(0.35f, 0.25f), Offset(0.35f, 0.5f), Offset(0.35f, 0.8f), Offset(0.6f, 0.25f), Offset(0.55f, 0.5f), Offset(0.6f, 0.8f))
                "F" -> listOf(Offset(0.35f, 0.25f), Offset(0.35f, 0.5f), Offset(0.35f, 0.8f), Offset(0.6f, 0.25f), Offset(0.55f, 0.5f))
                "G" -> listOf(Offset(0.6f, 0.35f), Offset(0.45f, 0.25f), Offset(0.35f, 0.5f), Offset(0.45f, 0.75f), Offset(0.6f, 0.75f), Offset(0.6f, 0.55f))
                "H" -> listOf(Offset(0.35f, 0.25f), Offset(0.35f, 0.8f), Offset(0.65f, 0.25f), Offset(0.65f, 0.8f), Offset(0.5f, 0.5f))
                "I" -> listOf(Offset(0.5f, 0.25f), Offset(0.5f, 0.5f), Offset(0.5f, 0.8f), Offset(0.35f, 0.25f), Offset(0.65f, 0.25f), Offset(0.35f, 0.8f), Offset(0.65f, 0.8f))
                "J" -> listOf(Offset(0.55f, 0.25f), Offset(0.55f, 0.7f), Offset(0.4f, 0.8f), Offset(0.3f, 0.7f))
                "K" -> listOf(Offset(0.35f, 0.25f), Offset(0.35f, 0.8f), Offset(0.5f, 0.5f), Offset(0.65f, 0.3f), Offset(0.65f, 0.7f))
                "L" -> listOf(Offset(0.35f, 0.25f), Offset(0.35f, 0.8f), Offset(0.6f, 0.8f))
                "M" -> listOf(Offset(0.25f, 0.8f), Offset(0.25f, 0.3f), Offset(0.45f, 0.55f), Offset(0.65f, 0.3f), Offset(0.65f, 0.8f))
                "N" -> listOf(Offset(0.3f, 0.8f), Offset(0.3f, 0.3f), Offset(0.5f, 0.55f), Offset(0.7f, 0.8f), Offset(0.7f, 0.3f))
                "O" -> listOf(Offset(0.5f, 0.25f), Offset(0.35f, 0.5f), Offset(0.5f, 0.75f), Offset(0.65f, 0.5f))
                "P" -> listOf(Offset(0.35f, 0.8f), Offset(0.35f, 0.25f), Offset(0.55f, 0.35f), Offset(0.55f, 0.55f), Offset(0.35f, 0.6f))
                "Q" -> listOf(Offset(0.5f, 0.25f), Offset(0.35f, 0.5f), Offset(0.5f, 0.75f), Offset(0.65f, 0.5f), Offset(0.58f, 0.68f), Offset(0.7f, 0.8f))
                "R" -> listOf(Offset(0.35f, 0.8f), Offset(0.35f, 0.25f), Offset(0.55f, 0.35f), Offset(0.35f, 0.55f), Offset(0.6f, 0.8f))
                "S" -> listOf(Offset(0.6f, 0.35f), Offset(0.48f, 0.25f), Offset(0.35f, 0.4f), Offset(0.48f, 0.5f), Offset(0.6f, 0.6f), Offset(0.48f, 0.75f), Offset(0.35f, 0.65f))
                "T" -> listOf(Offset(0.35f, 0.25f), Offset(0.65f, 0.25f), Offset(0.5f, 0.25f), Offset(0.5f, 0.8f))
                "U" -> listOf(Offset(0.35f, 0.25f), Offset(0.35f, 0.65f), Offset(0.5f, 0.8f), Offset(0.65f, 0.65f), Offset(0.65f, 0.25f))
                "V" -> listOf(Offset(0.25f, 0.25f), Offset(0.5f, 0.8f), Offset(0.75f, 0.25f))
                "W" -> listOf(Offset(0.25f, 0.25f), Offset(0.35f, 0.8f), Offset(0.5f, 0.55f), Offset(0.65f, 0.8f), Offset(0.75f, 0.25f))
                "X" -> listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.7f), Offset(0.7f, 0.3f), Offset(0.3f, 0.7f))
                "Y" -> listOf(Offset(0.3f, 0.3f), Offset(0.5f, 0.55f), Offset(0.7f, 0.3f), Offset(0.5f, 0.8f))
                "Z" -> listOf(Offset(0.3f, 0.3f), Offset(0.7f, 0.3f), Offset(0.5f, 0.55f), Offset(0.3f, 0.8f), Offset(0.7f, 0.8f))
                else -> listOf(Offset(0.5f, 0.3f), Offset(0.5f, 0.5f), Offset(0.5f, 0.7f))
            }
        }
    }

    fun getLetterWaypoints(letter: String, isArabic: Boolean): List<Offset> {
        val normalized = letter.replace("ّ", "")
        if (normalized.length > 1) {
            val char1 = normalized.substring(0, 1)
            val char2 = normalized.substring(1, 2)
            
            val wp1 = getLetterWaypointsSingle(char1, isArabic)
            val wp2 = getLetterWaypointsSingle(char2, isArabic)
            
            val scaleX = 0.40f
            val (shiftX1, shiftX2) = if (isArabic) {
                Pair(0.55f, 0.05f)
            } else {
                Pair(0.05f, 0.55f)
            }
            
            val res = mutableListOf<Offset>()
            wp1.forEach { ot ->
                res.add(Offset(ot.x * scaleX + shiftX1, ot.y))
            }
            wp2.forEach { ot ->
                res.add(Offset(ot.x * scaleX + shiftX2, ot.y))
            }
            return res
        } else {
            return getLetterWaypointsSingle(normalized, isArabic)
        }
    }

    val layoutDirection = if (isArabicLanguage) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Text(
                text = if (isArabicLanguage) {
                    "🎨 سبورة الصوتيات التفاعلية! اضغط على أي حرف لسماع نطق الصوت، ثم تتبع الحرف أو ارسمه بيدك!"
                } else {
                    "🎨 Phonics Interactive Traceboard! Tap a letter below to hear its sound, then trace or draw on the chalkboard!"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF5D4037),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Language switch tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        isArabicLanguage = false
                        selectedLetter = if (profile.age >= 7) "IGH" else if (profile.age == 6) "CH" else "A"
                        completedPaths.clear()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isArabicLanguage) Color(0xFFFFB300) else Color(0xFFEEEEEE),
                        contentColor = if (!isArabicLanguage) Color(0xFF3E2723) else Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                ) {
                    Text("🇺🇸 English Phonics", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        isArabicLanguage = true
                        selectedLetter = if (profile.age >= 7) "ـاً" else if (profile.age == 6) "ال" else "أ"
                        completedPaths.clear()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isArabicLanguage) Color(0xFFFFB300) else Color(0xFFEEEEEE),
                        contentColor = if (isArabicLanguage) Color(0xFF3E2723) else Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                ) {
                    Text("🇸🇦 لغة عربية", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // Letter options bubble selector with horizontal scrolling list
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentList) { letter ->
                    val isSelected = (selectedLetter == letter)
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color(0xFFFFE082) else Color(0xFFF1F8E9))
                            .border(2.dp, if (isSelected) Color(0xFFFFB300) else Color.Transparent, CircleShape)
                            .clickable {
                                selectedLetter = letter
                                completedPaths.clear() // Clear screen on click so kids can trace the new letter!
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Brush Settings Control Bar (brush colors, eraser/clear, speech rate, thickness)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF3E0), RoundedCornerShape(16.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Colors palette
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    colorsPalette.forEach { col ->
                        val isColorSelected = (currentColor == col)
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(col)
                                .border(
                                    2.dp,
                                    if (isColorSelected) Color.White else Color.Transparent,
                                    CircleShape
                                )
                                .clickable { currentColor = col }
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hear Sound Button
                    IconButton(
                        onClick = {
                            val target = if (profile.age >= 8) {
                                val sentence = if (isArabicLanguage) {
                                    wordPhonicsMapArAgeEight[selectedLetter]?.second ?: selectedLetter
                                } else {
                                    wordPhonicsMapEngAgeEight[selectedLetter]?.second ?: selectedLetter
                                }
                                if (isArabicLanguage) "text:arabic:$sentence" else "text:english:$sentence"
                            } else if (profile.age == 7) {
                                val sentence = if (isArabicLanguage) {
                                    wordPhonicsMapArAgeSeven[selectedLetter]?.second ?: selectedLetter
                                } else {
                                    wordPhonicsMapEngAgeSeven[selectedLetter]?.second ?: selectedLetter
                                }
                                if (isArabicLanguage) "text:arabic:$sentence" else "text:english:$sentence"
                            } else if (profile.age == 6) {
                                val sentence = if (isArabicLanguage) {
                                    wordPhonicsMapArAgeSix[selectedLetter]?.second ?: selectedLetter
                                } else {
                                    wordPhonicsMapEngAgeSix[selectedLetter]?.second ?: selectedLetter
                                }
                                if (isArabicLanguage) "text:arabic:$sentence" else "text:english:$sentence"
                            } else if (profile.age == 5) {
                                val sentence = if (isArabicLanguage) {
                                    wordPhonicsMapAr[selectedLetter]?.second ?: selectedLetter
                                } else {
                                    wordPhonicsMapEng[selectedLetter]?.second ?: selectedLetter
                                }
                                if (isArabicLanguage) "text:arabic:$sentence" else "text:english:$sentence"
                            } else if (profile.age == 4) {
                                val wordVal = if (isArabicLanguage) {
                                    wordPhonicsMapAr[selectedLetter]?.first ?: selectedLetter
                                } else {
                                    wordPhonicsMapEng[selectedLetter]?.first ?: selectedLetter
                                }
                                if (isArabicLanguage) "text:arabic:$wordVal" else "text:english:$wordVal"
                            } else {
                                if (isArabicLanguage) "text:arabic:$selectedLetter" else "text:english:$selectedLetter"
                            }
                            onSoundTrigger(target)
                        },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .size(36.dp)
                    ) {
                        Text("🔊", fontSize = 18.sp)
                    }

                    // Eraser / Clear Sandbox Button
                    IconButton(
                        onClick = { completedPaths.clear() },
                        modifier = Modifier
                            .background(Color(0xFFFFCDD2), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Clear board",
                            tint = Color.Red
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // The amazing Canvas Tracer Board! High-contrast Classroom Blackboard styling
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(3.dp, Color(0xFFFFB300), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3022)), // Elegant Chalkboard Green
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { size ->
                            boardWidth = size.width.toFloat()
                            boardHeight = size.height.toFloat()
                        }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPoints = listOf(offset)
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    currentPoints = currentPoints + change.position
                                },
                                onDragEnd = {
                                    if (currentPoints.isNotEmpty()) {
                                        completedPaths.add(
                                            DrawPath(
                                                points = currentPoints,
                                                color = currentColor,
                                                strokeWidth = currentStrokeWidth
                                            )
                                        )
                                        currentPoints = emptyList()
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // 1. Trace Reference Letter, Word, or Sentence in Center
                    val bgText = if (profile.age >= 8) {
                        if (isArabicLanguage) {
                            wordPhonicsMapArAgeEight[selectedLetter]?.second ?: selectedLetter
                        } else {
                            wordPhonicsMapEngAgeEight[selectedLetter]?.second ?: selectedLetter
                        }
                    } else if (profile.age == 7) {
                        if (isArabicLanguage) {
                            wordPhonicsMapArAgeSeven[selectedLetter]?.second ?: selectedLetter
                        } else {
                            wordPhonicsMapEngAgeSeven[selectedLetter]?.second ?: selectedLetter
                        }
                    } else if (profile.age == 6) {
                        if (isArabicLanguage) {
                            wordPhonicsMapArAgeSix[selectedLetter]?.second ?: selectedLetter
                        } else {
                            wordPhonicsMapEngAgeSix[selectedLetter]?.second ?: selectedLetter
                        }
                    } else if (profile.age == 5) {
                        if (isArabicLanguage) {
                            wordPhonicsMapAr[selectedLetter]?.second ?: selectedLetter
                        } else {
                            wordPhonicsMapEng[selectedLetter]?.second ?: selectedLetter
                        }
                    } else if (profile.age == 4) {
                        if (isArabicLanguage) {
                            wordPhonicsMapAr[selectedLetter]?.first ?: selectedLetter
                        } else {
                            wordPhonicsMapEng[selectedLetter]?.first ?: selectedLetter
                        }
                    } else {
                        selectedLetter
                    }
                    val bgFontSize = if (profile.age >= 8) 22.sp else if (profile.age == 7) 24.sp else if (profile.age == 6) 24.sp else if (profile.age == 5) 24.sp else if (profile.age == 4) 42.sp else 180.sp
                    Text(
                        text = bgText,
                        fontSize = bgFontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0x3BFFFFFF), // Faint transparent overlay trace dashed helper
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )

                    // Dynamic tracing helper label
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = if (profile.age >= 7) {
                                if (isArabicLanguage) "💎 مستوى العباقرة الكبار! تتبع تركيب الكلمة! ✍️" else "💎 Super Genius Level! Trace complex level-5 phonics! ✍️"
                            } else if (profile.age == 6) {
                                if (isArabicLanguage) "👑 فك تركيب وجمال الأصوات المعقدة وتتبعها! ✍️" else "👑 Trace & master complex phonological combinations! ✍️"
                            } else if (profile.age == 5) {
                                if (isArabicLanguage) "🌟 اقرأ الجملة كاملة وتتبع حركات كتابتها! ✍️" else "🌟 Trace the full phonics sentence & speak! ✍️"
                            } else if (profile.age == 4) {
                                if (isArabicLanguage) "🌟 تتبع الكلمة ونطق حركاتها اللطيفة! ✍️" else "🌟 Trace the phonic word & speak with Noor Bee! ✍️"
                            } else {
                                if (isArabicLanguage) "تتبع وحرّك قلمك لرسم الحرف! ✍️" else "Draw or trace with hand or pen! ✍️"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xB3FFFFFF),
                            fontWeight = if (profile.age >= 4) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    // 2. Draw user hand tracing paths & current active lines!
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Completed strokes
                        completedPaths.forEach { drawPath ->
                            if (drawPath.points.size > 1) {
                                val path = Path().apply {
                                    val start = drawPath.points.first()
                                    moveTo(start.x, start.y)
                                    for (i in 1 until drawPath.points.size) {
                                        val pt = drawPath.points[i]
                                        lineTo(pt.x, pt.y)
                                    }
                                }
                                drawPath(
                                    path = path,
                                    color = drawPath.color,
                                    style = Stroke(
                                        width = drawPath.strokeWidth,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            } else if (drawPath.points.isNotEmpty()) {
                                drawCircle(
                                    color = drawPath.color,
                                    radius = drawPath.strokeWidth / 2f,
                                    center = drawPath.points.first()
                                )
                            }
                        }

                        // Currently active drawing line
                        if (currentPoints.size > 1) {
                            val activePath = Path().apply {
                                        val start = currentPoints.first()
                                        moveTo(start.x, start.y)
                                        for (i in 1 until currentPoints.size) {
                                            val pt = currentPoints[i]
                                            lineTo(pt.x, pt.y)
                                        }
                                    }
                            drawPath(
                                path = activePath,
                                color = currentColor,
                                style = Stroke(
                                    width = currentStrokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        } else if (currentPoints.isNotEmpty()) {
                            drawCircle(
                                color = currentColor,
                                radius = currentStrokeWidth / 2f,
                                center = currentPoints.first()
                            )
                        }


                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Try Next button (replaces Check My Drawings validation check)
            Button(
                onClick = {
                    val randomLetter = currentList.filter { it != selectedLetter }.randomOrNull() ?: currentList.randomOrNull()
                    if (randomLetter != null) {
                        selectedLetter = randomLetter
                        completedPaths.clear()
                        currentPoints = emptyList()
                        
                        // Automatically play phonetic audio for the newly selected item
                        val target = if (profile.age >= 8) {
                            val sentence = if (isArabicLanguage) {
                                wordPhonicsMapArAgeEight[randomLetter]?.second ?: randomLetter
                            } else {
                                wordPhonicsMapEngAgeEight[randomLetter]?.second ?: randomLetter
                            }
                            if (isArabicLanguage) "text:arabic:$sentence" else "text:english:$sentence"
                        } else if (profile.age == 7) {
                            val sentence = if (isArabicLanguage) {
                                wordPhonicsMapArAgeSeven[randomLetter]?.second ?: randomLetter
                            } else {
                                wordPhonicsMapEngAgeSeven[randomLetter]?.second ?: randomLetter
                            }
                            if (isArabicLanguage) "text:arabic:$sentence" else "text:english:$sentence"
                        } else if (profile.age == 6) {
                            val sentence = if (isArabicLanguage) {
                                wordPhonicsMapArAgeSix[randomLetter]?.second ?: randomLetter
                            } else {
                                wordPhonicsMapEngAgeSix[randomLetter]?.second ?: randomLetter
                            }
                            if (isArabicLanguage) "text:arabic:$sentence" else "text:english:$sentence"
                        } else if (profile.age == 5) {
                            val sentence = if (isArabicLanguage) {
                                wordPhonicsMapAr[randomLetter]?.second ?: randomLetter
                            } else {
                                wordPhonicsMapEng[randomLetter]?.second ?: randomLetter
                            }
                            if (isArabicLanguage) "text:arabic:$sentence" else "text:english:$sentence"
                        } else if (profile.age == 4) {
                            val wordVal = if (isArabicLanguage) {
                                wordPhonicsMapAr[randomLetter]?.first ?: randomLetter
                            } else {
                                wordPhonicsMapEng[randomLetter]?.first ?: randomLetter
                            }
                            if (isArabicLanguage) "text:arabic:$wordVal" else "text:english:$wordVal"
                        } else {
                            if (isArabicLanguage) "text:arabic:$randomLetter" else "text:english:$randomLetter"
                        }
                        onSoundTrigger(target)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32), // High accessibility forest green
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("verify_handwriting_button"),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("➡️", fontSize = 18.sp)
                    Text(
                        text = if (isArabicLanguage) "جرب التالي ➡️" else "Try Next ➡️",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// --- Section 3: Achievement trophies milestone tracker --
@Composable
fun AchievementsGrid(profile: ChildProfile) {
    val trophies = listOf(
        Pair("Star Collector ⭐", profile.stars >= 100),
        Pair("Sticker Designer 🎪", profile.completedActivities > 0),
        Pair("Honey Farmer 🍯", profile.coins >= 40),
        Pair("High Flyer 🐝", profile.stars >= 200),
        Pair("Super Dresser 👑", profile.coins >= 15),
        Pair("Bilingual Master 🇸🇦", profile.completedActivities >= 2)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(trophies) { trophy ->
            val isEarned = trophy.second
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .shadow(if (isEarned) 4.dp else 0.dp, RoundedCornerShape(24.dp))
                    .testTag("trophy_card_${trophy.first.substringBefore(" ")}"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEarned) Color.White else Color(0x60EEEEEE)
                ),
                border = BorderStroke(1.dp, if (isEarned) Color(0xFFFFB300) else Color(0xFFEEEEEE))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                if (isEarned) Color(0xFFFFECB3) else Color(0xFFF5F5F5),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isEarned) "🏆" else "🔒",
                            fontSize = 32.sp
                        )
                    }

                    Text(
                        trophy.first,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = if (isEarned) Color(0xFF3E2723) else Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (isEarned) "Earned Completed!" else "Incomplete Locked",
                        fontSize = 10.sp,
                        color = if (isEarned) Color(0xFF4CAF50) else Color.Gray
                    )
                }
            }
        }
    }
}
