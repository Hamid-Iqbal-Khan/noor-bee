// src/data/curriculum.js

export const AGE_GROUPS = [
  { id: 'ag1', range: '1-2', label: 'Toddlers', emoji: '👶', color: '#FF6B9D', dailyMins: 5 },
  { id: 'ag2', range: '2-3', label: 'Early Talkers', emoji: '🐣', color: '#FF9800', dailyMins: 10 },
  { id: 'ag3', range: '3-4', label: 'Story Lovers', emoji: '🌱', color: '#4CAF50', dailyMins: 15 },
  { id: 'ag4', range: '4-5', label: 'Curious Learners', emoji: '🔍', color: '#2196F3', dailyMins: 20 },
  { id: 'ag5', range: '5-6', label: 'Readers & Writers', emoji: '📚', color: '#9C27B0', dailyMins: 25 },
  { id: 'ag6', range: '6-7', label: 'Fluent Communicators', emoji: '🚀', color: '#F44336', dailyMins: 30 },
];

// ─── ENGLISH ALPHABET — Islamic Words ────────────────────────────────────────
export const ENGLISH_ALPHABET = [
  { letter: 'A', lower: 'a', islamicWord: 'Allah', arabic: 'الله', pronunciation: 'Al-lah', meaning: 'The One God', emoji: '⭐', color: '#1B5E20', rhyme: 'A for Allah, kind and near,\nWe thank Allah every year.' },
  { letter: 'B', lower: 'b', islamicWord: 'Bismillah', arabic: 'بِسْمِ الله', pronunciation: 'Bis-mil-lah', meaning: 'In the name of Allah', emoji: '🍽️', color: '#E65100', rhyme: "B for Bismillah, before we eat,\nAllah's blessings make life sweet." },
  { letter: 'C', lower: 'c', islamicWord: 'Charity', arabic: 'صَدَقَة', pronunciation: 'Sa-da-qah', meaning: 'Helping the needy', emoji: '🤝', color: '#880E4F', rhyme: 'C for Charity, share and care,\nHelping others everywhere.' },
  { letter: 'D', lower: 'd', islamicWord: 'Dua', arabic: 'دُعَاء', pronunciation: 'Du-aa', meaning: 'Supplication to Allah', emoji: '🤲', color: '#4A148C', rhyme: "D for Dua, day and night,\nAsking Allah for what's right." },
  { letter: 'E', lower: 'e', islamicWord: 'Eid', arabic: 'عِيد', pronunciation: 'Eed', meaning: 'Islamic festival', emoji: '🎉', color: '#BF360C', rhyme: 'E for Eid, happy day,\nSmiles and joy come out to play.' },
  { letter: 'F', lower: 'f', islamicWord: 'Fajr', arabic: 'الفَجْر', pronunciation: 'Faj-r', meaning: 'Dawn prayer', emoji: '🌅', color: '#0D47A1', rhyme: "F for Fajr, morning bright,\nStart the day with Allah's light." },
  { letter: 'G', lower: 'g', islamicWord: 'Ghusl', arabic: 'غُسْل', pronunciation: 'Ghus-l', meaning: 'Ritual bath', emoji: '🚿', color: '#1A237E', rhyme: 'G for Ghusl, clean and neat,\nPurity makes faith complete.' },
  { letter: 'H', lower: 'h', islamicWord: 'Hajj', arabic: 'حَج', pronunciation: 'Haj-j', meaning: 'Pilgrimage to Makkah', emoji: '🕋', color: '#212121', rhyme: "H for Hajj, journey grand,\nVisiting Allah's holy land." },
  { letter: 'I', lower: 'i', islamicWord: 'Iman', arabic: 'إِيمَان', pronunciation: 'Ee-man', meaning: 'Faith in Allah', emoji: '💚', color: '#1B5E20', rhyme: 'I for Iman, faith so strong,\nHelping us do right, not wrong.' },
  { letter: 'J', lower: 'j', islamicWord: 'Jannah', arabic: 'الجَنَّة', pronunciation: 'Jan-nah', meaning: 'Paradise', emoji: '🌺', color: '#2E7D32', rhyme: "J for Jannah, bright and fair,\nAllah's rewards waiting there." },
  { letter: 'K', lower: 'k', islamicWord: 'Kaaba', arabic: 'الكَعْبَة', pronunciation: 'Kaa-bah', meaning: 'Sacred House in Makkah', emoji: '🕋', color: '#263238', rhyme: 'K for Kaaba, black and square,\nMuslims face it everywhere.' },
  { letter: 'L', lower: 'l', islamicWord: 'Laylatul Qadr', arabic: 'لَيْلَةُ القَدْر', pronunciation: 'Lay-la-tul Qadr', meaning: 'Night of Power', emoji: '🌙', color: '#311B92', rhyme: 'L for Laylatul Qadr, special night,\nFilled with blessings and heavenly light.' },
  { letter: 'M', lower: 'm', islamicWord: 'Masjid', arabic: 'مَسْجِد', pronunciation: 'Mas-jid', meaning: 'Mosque', emoji: '🕌', color: '#004D40', rhyme: "M for Masjid, calm and bright,\nA place to pray and do what's right." },
  { letter: 'N', lower: 'n', islamicWord: 'Nabi', arabic: 'نَبِي', pronunciation: 'Na-bee', meaning: 'Prophet of Allah', emoji: '📖', color: '#BF360C', rhyme: 'N for Nabi, truthful and wise,\nTeaching goodness under the skies.' },
  { letter: 'O', lower: 'o', islamicWord: 'Obedience', arabic: 'طَاعَة', pronunciation: 'Taa-ah', meaning: "Following Allah's commands", emoji: '🙏', color: '#4E342E', rhyme: 'O for Obedience, kind and true,\nAllah loves good things we do.' },
  { letter: 'P', lower: 'p', islamicWord: 'Prophet', arabic: 'رَسُول', pronunciation: 'Ra-sool', meaning: 'Messenger of Allah', emoji: '🌟', color: '#1565C0', rhyme: 'P for Prophet, guide and friend,\nFollowing him till the end.' },
  { letter: 'Q', lower: 'q', islamicWord: 'Quran', arabic: 'القُرآن', pronunciation: 'Qur-aan', meaning: 'Holy Book of Islam', emoji: '📗', color: '#1B5E20', rhyme: 'Q for Quran, words so bright,\nShowing us the path of light.' },
  { letter: 'R', lower: 'r', islamicWord: 'Ramadan', arabic: 'رَمَضَان', pronunciation: 'Ra-ma-dan', meaning: 'Month of fasting', emoji: '🌙', color: '#4A148C', rhyme: 'R for Ramadan, month so dear,\nBringing blessings every year.' },
  { letter: 'S', lower: 's', islamicWord: 'Salah', arabic: 'صَلَاة', pronunciation: 'Sa-lah', meaning: 'Prayer — 5 times a day', emoji: '🙇', color: '#006064', rhyme: 'S for Salah, five times a day,\nHelping Muslims learn and pray.' },
  { letter: 'T', lower: 't', islamicWord: 'Tawheed', arabic: 'التَّوحِيد', pronunciation: 'Taw-heed', meaning: 'Oneness of Allah', emoji: '☝️', color: '#E65100', rhyme: 'T for Tawheed, Allah is One,\nCreator of everyone.' },
  { letter: 'U', lower: 'u', islamicWord: 'Ummah', arabic: 'أُمَّة', pronunciation: 'Um-mah', meaning: 'Muslim community worldwide', emoji: '🤝', color: '#880E4F', rhyme: 'U for Ummah, big and wide,\nMuslims standing side by side.' },
  { letter: 'V', lower: 'v', islamicWord: 'Virtue', arabic: 'فَضِيلَة', pronunciation: 'Fa-dee-lah', meaning: 'Good character', emoji: '💝', color: '#37474F', rhyme: 'V for Virtue, good and kind,\nA caring heart and peaceful mind.' },
  { letter: 'W', lower: 'w', islamicWord: 'Wudu', arabic: 'وُضُوء', pronunciation: 'Wu-doo', meaning: 'Ablution before prayer', emoji: '💧', color: '#0277BD', rhyme: 'W for Wudu, wash and pray,\nClean for Allah every day.' },
  { letter: 'X', lower: 'x', islamicWord: 'eXcellence', arabic: 'الإِحسَان', pronunciation: 'Al-Ih-san', meaning: 'Doing everything with excellence', emoji: '🌠', color: '#6A1B9A', rhyme: 'X for eXcellence, do your best,\nAllah loves sincere success.' },
  { letter: 'Y', lower: 'y', islamicWord: 'Yaqeen', arabic: 'يَقِين', pronunciation: 'Ya-qeen', meaning: 'Certainty in Allah', emoji: '💛', color: '#F57F17', rhyme: 'Y for Yaqeen, faith so strong,\nTrusting Allah all day long.' },
  { letter: 'Z', lower: 'z', islamicWord: 'Zakat', arabic: 'زَكَاة', pronunciation: 'Za-kat', meaning: 'Obligatory charity', emoji: '💰', color: '#2E7D32', rhyme: "Z for Zakat, share what you can,\nHelping others is Allah's plan." },
];

// ─── ARABIC ALPHABET — Islamic Words (RTL order) ─────────────────────────────
export const ARABIC_ALPHABET = [
  { letter: 'ا', name: 'Alif', roman: 'Alif', word: 'الله', wordEn: 'Allah', sound: 'aa', emoji: '⭐', forms: { initial: 'ا', medial: 'ـا', final: 'ـا' }, color: '#e74c3c' },
  { letter: 'ب', name: 'Ba', roman: 'Ba', word: 'بِسْمِ اللَّهِ', wordEn: 'Bismillah', sound: 'b', emoji: '🤲', forms: { initial: 'بـ', medial: 'ـبـ', final: 'ـب' }, color: '#e67e22' },
  { letter: 'ت', name: 'Ta', roman: 'Ta', word: 'تَوبَة', wordEn: 'Tawbah (Repentance)', sound: 't', emoji: '💚', forms: { initial: 'تـ', medial: 'ـتـ', final: 'ـت' }, color: '#f1c40f' },
  { letter: 'ث', name: 'Tha', roman: 'Tha', word: 'ثَواب', wordEn: 'Thawab (Reward)', sound: 'th', emoji: '🌟', forms: { initial: 'ثـ', medial: 'ـثـ', final: 'ـث' }, color: '#2ecc71' },
  { letter: 'ج', name: 'Jim', roman: 'Jim', word: 'جَنَّة', wordEn: 'Jannah (Paradise)', sound: 'j', emoji: '🌺', forms: { initial: 'جـ', medial: 'ـجـ', final: 'ـج' }, color: '#1abc9c' },
  { letter: 'ح', name: 'Ha', roman: 'Ḥa', word: 'حَمد', wordEn: 'Hamd (Praise)', sound: 'ħ', emoji: '🙏', forms: { initial: 'حـ', medial: 'ـحـ', final: 'ـح' }, color: '#3498db' },
  { letter: 'خ', name: 'Kha', roman: 'Kha', word: 'خَليفة', wordEn: 'Khalifah (Successor)', sound: 'kh', emoji: '👑', forms: { initial: 'خـ', medial: 'ـخـ', final: 'ـخ' }, color: '#9b59b6' },
  { letter: 'د', name: 'Dal', roman: 'Dal', word: 'دُعاء', wordEn: "Du'a (Supplication)", sound: 'd', emoji: '🤲', forms: { initial: 'د', medial: 'ـد', final: 'ـد' }, color: '#e91e8c' },
  { letter: 'ذ', name: 'Dhal', roman: 'Dhal', word: 'ذِكر', wordEn: 'Dhikr (Remembrance)', sound: 'dh', emoji: '💫', forms: { initial: 'ذ', medial: 'ـذ', final: 'ـذ' }, color: '#e74c3c' },
  { letter: 'ر', name: 'Ra', roman: 'Ra', word: 'رَحمة', wordEn: 'Rahmah (Mercy)', sound: 'r', emoji: '💝', forms: { initial: 'ر', medial: 'ـر', final: 'ـر' }, color: '#e67e22' },
  { letter: 'ز', name: 'Zay', roman: 'Zay', word: 'زَكاة', wordEn: 'Zakah (Charity)', sound: 'z', emoji: '💰', forms: { initial: 'ز', medial: 'ـز', final: 'ـز' }, color: '#f1c40f' },
  { letter: 'س', name: 'Sin', roman: 'Sin', word: 'سَلام', wordEn: 'Salam (Peace)', sound: 's', emoji: '☮️', forms: { initial: 'سـ', medial: 'ـسـ', final: 'ـس' }, color: '#2ecc71' },
  { letter: 'ش', name: 'Shin', roman: 'Shin', word: 'شُكر', wordEn: 'Shukr (Gratitude)', sound: 'sh', emoji: '🌸', forms: { initial: 'شـ', medial: 'ـشـ', final: 'ـش' }, color: '#1abc9c' },
  { letter: 'ص', name: 'Sad', roman: 'Ṣad', word: 'صَلاة', wordEn: 'Salah (Prayer)', sound: 'ṣ', emoji: '🙇', forms: { initial: 'صـ', medial: 'ـصـ', final: 'ـص' }, color: '#3498db' },
  { letter: 'ض', name: 'Dad', roman: 'Ḍad', word: 'ضِياء', wordEn: "Diya' (Light)", sound: 'ḍ', emoji: '✨', forms: { initial: 'ضـ', medial: 'ـضـ', final: 'ـض' }, color: '#9b59b6' },
  { letter: 'ط', name: 'Ta', roman: 'Ṭa', word: 'طَهارة', wordEn: 'Taharah (Purity)', sound: 'ṭ', emoji: '💧', forms: { initial: 'طـ', medial: 'ـطـ', final: 'ـط' }, color: '#e91e8c' },
  { letter: 'ظ', name: 'Dha', roman: 'Ẓa', word: 'ظُهر', wordEn: 'Dhuhr (Noon Prayer)', sound: 'ẓ', emoji: '☀️', forms: { initial: 'ظـ', medial: 'ـظـ', final: 'ـظ' }, color: '#e74c3c' },
  { letter: 'ع', name: 'Ain', roman: "'Ain", word: 'عِلم', wordEn: "'Ilm (Knowledge)", sound: 'ʿ', emoji: '📚', forms: { initial: 'عـ', medial: 'ـعـ', final: 'ـع' }, color: '#e67e22' },
  { letter: 'غ', name: 'Ghain', roman: 'Ghain', word: 'غُفران', wordEn: 'Ghufran (Forgiveness)', sound: 'gh', emoji: '🌈', forms: { initial: 'غـ', medial: 'ـغـ', final: 'ـغ' }, color: '#f1c40f' },
  { letter: 'ف', name: 'Fa', roman: 'Fa', word: 'فَجر', wordEn: 'Fajr (Dawn Prayer)', sound: 'f', emoji: '🌅', forms: { initial: 'فـ', medial: 'ـفـ', final: 'ـف' }, color: '#2ecc71' },
  { letter: 'ق', name: 'Qaf', roman: 'Qaf', word: 'قُرآن', wordEn: 'Quran (Holy Book)', sound: 'q', emoji: '📗', forms: { initial: 'قـ', medial: 'ـقـ', final: 'ـق' }, color: '#1abc9c' },
  { letter: 'ك', name: 'Kaf', roman: 'Kaf', word: 'كَرَم', wordEn: 'Karam (Generosity)', sound: 'k', emoji: '💎', forms: { initial: 'كـ', medial: 'ـكـ', final: 'ـك' }, color: '#3498db' },
  { letter: 'ل', name: 'Lam', roman: 'Lam', word: 'لَيلة', wordEn: 'Laylah (Night)', sound: 'l', emoji: '🌙', forms: { initial: 'لـ', medial: 'ـلـ', final: 'ـل' }, color: '#9b59b6' },
  { letter: 'م', name: 'Mim', roman: 'Mim', word: 'مَسجِد', wordEn: 'Masjid (Mosque)', sound: 'm', emoji: '🕌', forms: { initial: 'مـ', medial: 'ـمـ', final: 'ـم' }, color: '#e91e8c' },
  { letter: 'ن', name: 'Nun', roman: 'Nun', word: 'نُور', wordEn: 'Nur (Light)', sound: 'n', emoji: '💡', forms: { initial: 'نـ', medial: 'ـنـ', final: 'ـن' }, color: '#e74c3c' },
  { letter: 'ه', name: 'Ha', roman: 'Ha', word: 'هِداية', wordEn: 'Hidayah (Guidance)', sound: 'h', emoji: '🌟', forms: { initial: 'هـ', medial: 'ـهـ', final: 'ـه' }, color: '#e67e22' },
  { letter: 'و', name: 'Waw', roman: 'Waw', word: 'وُضوء', wordEn: 'Wudu (Ablution)', sound: 'w', emoji: '💧', forms: { initial: 'و', medial: 'ـو', final: 'ـو' }, color: '#f1c40f' },
  { letter: 'ي', name: 'Ya', roman: 'Ya', word: 'يَقين', wordEn: 'Yaqin (Certainty)', sound: 'y', emoji: '✨', forms: { initial: 'يـ', medial: 'ـيـ', final: 'ـي' }, color: '#2ecc71' },
];

// ─── ISLAMIC A–Z (same as English alphabet now) ───────────────────────────────
export const ISLAMIC_ALPHABET = ENGLISH_ALPHABET;

// ─── VOCABULARY CATEGORIES ────────────────────────────────────────────────────
export const VOCAB_CATEGORIES = {
  english: [
    {
      id: 'animals', title: 'Animals', emoji: '🐾', color: '#4CAF50',
      words: [
        { word: 'Cat', emoji: '🐱', pronunciation: 'kæt', sentence: 'The cat says meow.', arabic: 'قطة', fun: 'Cats sleep 16 hours a day!' },
        { word: 'Dog', emoji: '🐶', pronunciation: 'dɒg', sentence: 'My dog loves to play.', arabic: 'كلب', fun: 'Dogs can smell 1000x better than humans!' },
        { word: 'Bird', emoji: '🐦', pronunciation: 'bɜːrd', sentence: 'The bird can fly.', arabic: 'طائر', fun: 'Some birds can learn to talk!' },
        { word: 'Fish', emoji: '🐠', pronunciation: 'fɪʃ', sentence: 'Fish swim in water.', arabic: 'سمكة', fun: 'Fish never close their eyes!' },
        { word: 'Lion', emoji: '🦁', pronunciation: 'laɪən', sentence: 'The lion roars loudly.', arabic: 'أسد', fun: 'Lions are the only cats that live in groups!' },
        { word: 'Elephant', emoji: '🐘', pronunciation: 'ˈɛlɪfənt', sentence: 'The elephant has a long trunk.', arabic: 'فيل', fun: 'Elephants have amazing memories!' },
        { word: 'Monkey', emoji: '🐵', pronunciation: 'mʌŋki', sentence: 'The monkey climbs trees.', arabic: 'قرد', fun: 'Monkeys love bananas!' },
        { word: 'Rabbit', emoji: '🐰', pronunciation: 'ˈræbɪt', sentence: 'The rabbit has big ears.', arabic: 'أرنب', fun: 'Rabbits can jump very high!' },
        { word: 'Duck', emoji: '🦆', pronunciation: 'dʌk', sentence: 'The duck swims in the pond.', arabic: 'بطة', fun: 'Baby ducks are called ducklings!' },
        { word: 'Frog', emoji: '🐸', pronunciation: 'frɒg', sentence: 'The frog jumps on lily pads.', arabic: 'ضفدع', fun: 'Frogs drink water through their skin!' },
      ],
    },
    {
      id: 'colors', title: 'Colors', emoji: '🎨', color: '#E91E63',
      words: [
        { word: 'Red', emoji: '🔴', pronunciation: 'rɛd', sentence: 'The apple is red.', arabic: 'أحمر', fun: 'Red means stop!' },
        { word: 'Blue', emoji: '🔵', pronunciation: 'bluː', sentence: 'The sky is blue.', arabic: 'أزرق', fun: 'Blue is the color of the ocean!' },
        { word: 'Yellow', emoji: '🟡', pronunciation: 'ˈjɛloʊ', sentence: 'The sun is yellow.', arabic: 'أصفر', fun: 'Yellow is the brightest color!' },
        { word: 'Green', emoji: '🟢', pronunciation: 'ɡriːn', sentence: 'Grass is green.', arabic: 'أخضر', fun: 'Green means go!' },
        { word: 'Orange', emoji: '🟠', pronunciation: 'ˈɒrɪndʒ', sentence: 'Oranges are orange!', arabic: 'برتقالي', fun: 'Orange is a fruit AND a color!' },
        { word: 'Purple', emoji: '🟣', pronunciation: 'ˈpɜːrpəl', sentence: 'The grapes are purple.', arabic: 'بنفسجي', fun: 'Purple was once only for kings!' },
        { word: 'Pink', emoji: '🩷', pronunciation: 'pɪŋk', sentence: 'Flamingos are pink.', arabic: 'وردي', fun: 'Pink is red mixed with white!' },
        { word: 'Black', emoji: '⬛', pronunciation: 'blæk', sentence: 'The night sky is black.', arabic: 'أسود', fun: 'The Kaaba is covered in black cloth!' },
        { word: 'White', emoji: '⬜', pronunciation: 'waɪt', sentence: 'Snow is white.', arabic: 'أبيض', fun: 'White light contains all colors!' },
        { word: 'Brown', emoji: '🟫', pronunciation: 'braʊn', sentence: 'Chocolate is brown.', arabic: 'بني', fun: 'Brown is made by mixing red, yellow, and blue!' },
      ],
    },
    {
      id: 'family', title: 'Family', emoji: '👨‍👩‍👧', color: '#FF9800',
      words: [
        { word: 'Mother', emoji: '👩', pronunciation: 'ˈmʌðər', sentence: 'My mother loves me.', arabic: 'أم', fun: 'Another word for mother is Mama!' },
        { word: 'Father', emoji: '👨', pronunciation: 'ˈfɑːðər', sentence: 'My father is kind.', arabic: 'أب', fun: 'Another word for father is Baba!' },
        { word: 'Sister', emoji: '👧', pronunciation: 'ˈsɪstər', sentence: 'My sister is funny.', arabic: 'أخت', fun: 'Sisters are your first best friends!' },
        { word: 'Brother', emoji: '👦', pronunciation: 'ˈbrʌðər', sentence: 'My brother plays football.', arabic: 'أخ', fun: 'Brothers and sisters are called siblings!' },
        { word: 'Baby', emoji: '👶', pronunciation: 'ˈbeɪbi', sentence: 'The baby is sleeping.', arabic: 'طفل', fun: 'You were a baby once too!' },
        { word: 'Grandmother', emoji: '👵', pronunciation: 'ˈɡrænmʌðər', sentence: 'Grandmother tells great stories.', arabic: 'جدة', fun: 'We also call her Grandma or Nana!' },
        { word: 'Grandfather', emoji: '👴', pronunciation: 'ˈɡrænfɑːðər', sentence: 'Grandfather is very wise.', arabic: 'جد', fun: 'We also call him Grandpa!' },
      ],
    },
    {
      id: 'fruits', title: 'Fruits', emoji: '🍎', color: '#F44336',
      words: [
        { word: 'Apple', emoji: '🍎', pronunciation: 'ˈæpəl', sentence: 'I eat an apple every day.', arabic: 'تفاحة', fun: 'There are over 7,500 kinds of apples!' },
        { word: 'Banana', emoji: '🍌', pronunciation: 'bəˈnɑːnə', sentence: 'Monkeys love bananas.', arabic: 'موزة', fun: 'Bananas are actually berries!' },
        { word: 'Mango', emoji: '🥭', pronunciation: 'ˈmæŋɡoʊ', sentence: 'The mango is sweet.', arabic: 'مانجو', fun: 'Mango is the king of fruits!' },
        { word: 'Orange', emoji: '🍊', pronunciation: 'ˈɒrɪndʒ', sentence: 'Oranges give us Vitamin C.', arabic: 'برتقالة', fun: 'Oranges grow in warm places!' },
        { word: 'Grapes', emoji: '🍇', pronunciation: 'ɡreɪps', sentence: 'Grapes grow in bunches.', arabic: 'عنب', fun: 'Raisins are dried grapes!' },
        { word: 'Strawberry', emoji: '🍓', pronunciation: 'ˈstrɔːbɛri', sentence: 'Strawberries are red and sweet.', arabic: 'فراولة', fun: 'Strawberries are not really berries!' },
        { word: 'Watermelon', emoji: '🍉', pronunciation: 'ˈwɔːtərˌmɛlən', sentence: 'Watermelon is great in summer.', arabic: 'بطيخ', fun: 'Watermelon is 92% water!' },
        { word: 'Pineapple', emoji: '🍍', pronunciation: 'ˈpaɪnˌæpəl', sentence: 'Pineapple is yellow inside.', arabic: 'أناناس', fun: 'Pineapple takes 2 years to grow!' },
      ],
    },
    {
      id: 'numbers', title: 'Numbers', emoji: '🔢', color: '#3F51B5',
      words: [
        { word: 'One', emoji: '1️⃣', pronunciation: 'wʌn', sentence: 'I have one nose.', arabic: 'واحد', fun: 'Allah is One!' },
        { word: 'Two', emoji: '2️⃣', pronunciation: 'tuː', sentence: 'I have two eyes.', arabic: 'اثنان', fun: 'You have two hands and two feet!' },
        { word: 'Three', emoji: '3️⃣', pronunciation: 'θriː', sentence: 'A triangle has three sides.', arabic: 'ثلاثة', fun: 'Traffic lights have three colors!' },
        { word: 'Four', emoji: '4️⃣', pronunciation: 'fɔːr', sentence: 'A dog has four legs.', arabic: 'أربعة', fun: 'A table has four legs!' },
        { word: 'Five', emoji: '5️⃣', pronunciation: 'faɪv', sentence: 'I have five fingers.', arabic: 'خمسة', fun: 'Muslims pray 5 times a day!' },
        { word: 'Six', emoji: '6️⃣', pronunciation: 'sɪks', sentence: 'A bee has six legs.', arabic: 'ستة', fun: 'A snowflake has six sides!' },
        { word: 'Seven', emoji: '7️⃣', pronunciation: 'ˈsɛvən', sentence: 'There are seven days in a week.', arabic: 'سبعة', fun: 'A rainbow has seven colors!' },
        { word: 'Eight', emoji: '8️⃣', pronunciation: 'eɪt', sentence: 'A spider has eight legs.', arabic: 'ثمانية', fun: 'An octopus has eight arms!' },
        { word: 'Nine', emoji: '9️⃣', pronunciation: 'naɪn', sentence: 'I can see nine stars.', arabic: 'تسعة', fun: 'Nine is the biggest one-digit number!' },
        { word: 'Ten', emoji: '🔟', pronunciation: 'tɛn', sentence: 'I can count to ten!', arabic: 'عشرة', fun: 'We have 10 fingers and 10 toes!' },
      ],
    },
    {
      id: 'islamic', title: 'Islamic Words', emoji: '🌙', color: '#1B5E20',
      words: [
        { word: 'Bismillah', emoji: '🤲', pronunciation: 'Bis-mil-lah', sentence: 'Say Bismillah before eating.', arabic: 'بِسْمِ الله', fun: 'We say Bismillah before everything good!' },
        { word: 'Alhamdulillah', emoji: '🙏', pronunciation: 'Al-ham-du-lil-lah', sentence: 'Say Alhamdulillah after eating.', arabic: 'الحَمدُ لله', fun: 'Alhamdulillah means Praise be to Allah!' },
        { word: 'Inshallah', emoji: '🌟', pronunciation: 'In-sha-allah', sentence: 'We will go tomorrow, Inshallah.', arabic: 'إِن شاءَ الله', fun: 'We say it when planning future things!' },
        { word: 'Mashallah', emoji: '✨', pronunciation: 'Ma-sha-allah', sentence: 'Mashallah, you did great!', arabic: 'مَا شَاءَ الله', fun: 'We say it to praise something beautiful!' },
        { word: 'Allahu Akbar', emoji: '⭐', pronunciation: 'Al-la-hu Ak-bar', sentence: 'We say Allahu Akbar in prayer.', arabic: 'اللّٰه أَكبَر', fun: 'It means Allah is the Greatest!' },
        { word: 'Jazakallah', emoji: '💝', pronunciation: 'Ja-za-kal-lah', sentence: 'Jazakallah for your help!', arabic: 'جَزاكَ الله', fun: 'It means May Allah reward you!' },
      ],
    },
  ],
  arabic: [
    {
      id: 'ar_animals', title: 'الحيوانات', titleEn: 'Animals', emoji: '🐾', color: '#4CAF50',
      words: [
        { word: 'قِطَّة', transliteration: 'Qittah', english: 'Cat', emoji: '🐱', sentence: 'القِطَّةُ تَقولُ مياو', sentenceEn: 'The cat says meow', fun: 'القطط تنام 16 ساعة!' },
        { word: 'كَلب', transliteration: 'Kalb', english: 'Dog', emoji: '🐶', sentence: 'الكَلبُ وَفِيٌّ جِداً', sentenceEn: 'The dog is very loyal', fun: 'الكلاب تشم 1000 مرة أفضل!' },
        { word: 'أَسَد', transliteration: 'Asad', english: 'Lion', emoji: '🦁', sentence: 'الأَسَدُ مَلِكُ الغابَة', sentenceEn: 'The lion is king of the jungle', fun: 'زئير الأسد يُسمع من بعيد جداً!' },
        { word: 'فِيل', transliteration: 'Feel', english: 'Elephant', emoji: '🐘', sentence: 'الفِيلُ لَهُ خُرطوم طَويل', sentenceEn: 'The elephant has a long trunk', fun: 'ذُكر الفيل في سورة الفيل!' },
        { word: 'سَمَكة', transliteration: 'Samakah', english: 'Fish', emoji: '🐠', sentence: 'السَمَكَةُ تَسبَحُ في الماء', sentenceEn: 'The fish swims in water', fun: 'الأسماك لا تغمض عيونها!' },
        { word: 'طَائِر', transliteration: 'Tair', english: 'Bird', emoji: '🐦', sentence: 'الطَّائِرُ يَطيرُ في السَّماء', sentenceEn: 'The bird flies in the sky', fun: 'الطيور تذكر الله!' },
        { word: 'أَرنَب', transliteration: 'Arnab', english: 'Rabbit', emoji: '🐰', sentence: 'الأَرنَبُ لَهُ آذانٌ طَويلة', sentenceEn: 'The rabbit has long ears', fun: 'الأرانب تقفز عالياً جداً!' },
        { word: 'فَرَس', transliteration: 'Faras', english: 'Horse', emoji: '🐴', sentence: 'الفَرَسُ يَجري بِسُرعة', sentenceEn: 'The horse runs fast', fun: 'الخيول مذكورة في القرآن الكريم!' },
      ],
    },
    {
      id: 'ar_colors', title: 'الألوان', titleEn: 'Colors', emoji: '🎨', color: '#E91E63',
      words: [
        { word: 'أَحمَر', transliteration: 'Ahmar', english: 'Red', emoji: '🔴', sentence: 'التُّفّاحَةُ حَمراء', sentenceEn: 'The apple is red', fun: 'الأحمر يعني توقف!' },
        { word: 'أَزرَق', transliteration: 'Azraq', english: 'Blue', emoji: '🔵', sentence: 'السَّماءُ زَرقاء', sentenceEn: 'The sky is blue', fun: 'الأزرق لون المحيط!' },
        { word: 'أَصفَر', transliteration: 'Asfar', english: 'Yellow', emoji: '🟡', sentence: 'الشَّمسُ صَفراء', sentenceEn: 'The sun is yellow', fun: 'الأصفر أكثر الألوان لمعاناً!' },
        { word: 'أَخضَر', transliteration: 'Akhdar', english: 'Green', emoji: '🟢', sentence: 'العُشبُ أَخضَر', sentenceEn: 'The grass is green', fun: 'أهل الجنة يلبسون ثياباً خُضراً! (الكهف: ٣١)' },
        { word: 'أَبيَض', transliteration: 'Abyad', english: 'White', emoji: '⬜', sentence: 'الثَّلجُ أَبيَض', sentenceEn: 'The snow is white', fun: 'الأبيض لون الطهارة!' },
        { word: 'أَسوَد', transliteration: 'Aswad', english: 'Black', emoji: '⬛', sentence: 'اللَّيلُ أَسوَد', sentenceEn: 'The night is black', fun: 'الكعبة مغطاة بالقماش الأسود!' },
      ],
    },
    {
      id: 'ar_family', title: 'العائلة', titleEn: 'Family', emoji: '👨‍👩‍👧', color: '#FF9800',
      words: [
        { word: 'أُمّ', transliteration: 'Umm', english: 'Mother', emoji: '👩', sentence: 'أُمّي تُحِبُّني كَثيراً', sentenceEn: 'My mother loves me so much', fun: 'قال النبي ﷺ عن الأم: «فالزمها فإن الجنة تحت رجليها» (رواه النسائي)' },
        { word: 'أَبٌ', transliteration: 'Ab', english: 'Father', emoji: '👨', sentence: 'أَبي يَعمَلُ كُلَّ يَومٍ', sentenceEn: 'My father works every day', fun: 'برّ الوالدين من أعظم الأعمال!' },
        { word: 'أُخت', transliteration: 'Ukht', english: 'Sister', emoji: '👧', sentence: 'أُختي تُشارِكُني الألعاب', sentenceEn: 'My sister shares toys with me', fun: 'الأخت هي صديقتك الأولى!' },
        { word: 'أَخ', transliteration: 'Akh', english: 'Brother', emoji: '👦', sentence: 'أَخي يَلعَبُ مَعي', sentenceEn: 'My brother plays with me', fun: 'المسلمون كلهم إخوة!' },
        { word: 'جَدَّة', transliteration: 'Jaddah', english: 'Grandmother', emoji: '👵', sentence: 'جَدَّتي تَحكي قِصَصاً جَميلة', sentenceEn: 'Grandma tells beautiful stories', fun: 'الجدة دائماً تحب أحفادها!' },
        { word: 'جَدّ', transliteration: 'Jadd', english: 'Grandfather', emoji: '👴', sentence: 'جَدّي حَكيمٌ وَطَيِّب', sentenceEn: 'Grandpa is wise and kind', fun: 'الجد يحكي قصصاً قديمة!' },
      ],
    },
    {
      id: 'ar_islamic', title: 'الكلمات الإسلامية', titleEn: 'Islamic Words', emoji: '🌙', color: '#1B5E20',
      words: [
        { word: 'بِسمِ اللّٰه', transliteration: 'Bismillah', english: 'In the name of Allah', emoji: '🤲', sentence: 'قُل بِسمِ اللّٰه قَبلَ الأَكل', sentenceEn: 'Say Bismillah before eating', fun: 'نقول بسم الله قبل كل عمل خير!' },
        { word: 'الحَمدُ لله', transliteration: 'Alhamdulillah', english: 'Praise be to Allah', emoji: '🙏', sentence: 'قُل الحَمدُ لله بَعدَ الأَكل', sentenceEn: 'Say Alhamdulillah after eating', fun: 'الحمدلله نقولها شكراً لله!' },
        { word: 'السَّلامُ عَلَيكُم', transliteration: 'Assalamu Alaikum', english: 'Peace be upon you', emoji: '👋', sentence: 'قُل السَّلامُ عَلَيكُم لِلجَميع', sentenceEn: 'Say Salam to everyone', fun: 'السلام تحية المسلمين!' },
        { word: 'اللّٰه أَكبَر', transliteration: 'Allahu Akbar', english: 'Allah is the Greatest', emoji: '⭐', sentence: 'نَقولُ اللّٰه أَكبَر في الصَّلاة', sentenceEn: 'We say Allahu Akbar in prayer', fun: 'نقولها في كل صلاة!' },
        { word: 'إِن شاءَ اللّٰه', transliteration: 'Inshallah', english: 'If Allah wills', emoji: '🌟', sentence: 'سَنَذهَبُ غَداً إِن شاءَ اللّٰه', sentenceEn: 'We will go tomorrow Inshallah', fun: 'نقولها عند التخطيط للمستقبل!' },
        { word: 'مَا شَاءَ اللّٰه', transliteration: 'Mashallah', english: 'What Allah has willed', emoji: '✨', sentence: 'مَا شَاءَ اللّٰه، رَسَمتَ جَيِّداً', sentenceEn: 'Mashallah, you drew well', fun: 'نقولها لما نرى شيئاً جميلاً!' },
        { word: 'جَزاكَ اللّٰه خَيراً', transliteration: 'Jazak Allah Khayran', english: 'May Allah reward you', emoji: '💝', sentence: 'جَزاكَ اللّٰه خَيراً لِمُساعَدَتِك', sentenceEn: 'May Allah reward you for helping', fun: 'شكر أفضل من كلمة شكراً فقط!' },
      ],
    },
    {
      id: 'ar_numbers', title: 'الأرقام', titleEn: 'Numbers', emoji: '🔢', color: '#3F51B5',
      words: [
        { word: 'وَاحِد', transliteration: 'Wahid', english: 'One', emoji: '1️⃣', sentence: 'اللهُ وَاحِد', sentenceEn: 'Allah is One', fun: 'واحد هو العدد الأول!' },
        { word: 'اِثنَان', transliteration: 'Ithnan', english: 'Two', emoji: '2️⃣', sentence: 'لِي عَينان اثنتان', sentenceEn: 'I have two eyes', fun: 'لنا يدان اثنتان!' },
        { word: 'ثَلاثَة', transliteration: 'Thalathah', english: 'Three', emoji: '3️⃣', sentence: 'المُثَلَّثُ لَهُ ثَلاثَةُ أَضلاع', sentenceEn: 'A triangle has three sides', fun: 'إشارة المرور لها ثلاثة ألوان!' },
        { word: 'أَربَعَة', transliteration: 'Arbaah', english: 'Four', emoji: '4️⃣', sentence: 'الكُرسيُّ لَهُ أَربَعُ أَرجُل', sentenceEn: 'The chair has four legs', fun: 'المربع له أربعة أضلاع!' },
        { word: 'خَمسَة', transliteration: 'Khamsah', english: 'Five', emoji: '5️⃣', sentence: 'الصَّلَواتُ خَمسٌ في اليَوم', sentenceEn: 'There are five prayers in a day', fun: 'نصلي خمس مرات كل يوم!' },
        { word: 'سِتَّة', transliteration: 'Sittah', english: 'Six', emoji: '6️⃣', sentence: 'النَّحلَةُ لَها سِتُّ أَرجُل', sentenceEn: 'A bee has six legs', fun: 'ندفة الثلج لها ستة جوانب!' },
        { word: 'سَبعَة', transliteration: 'Sabah', english: 'Seven', emoji: '7️⃣', sentence: 'الأُسبوعُ سَبعَةُ أَيَّام', sentenceEn: 'The week has seven days', fun: 'قوس قزح له سبعة ألوان!' },
        { word: 'ثَمانِيَة', transliteration: 'Thamaniyah', english: 'Eight', emoji: '8️⃣', sentence: 'الأَخطَبوطُ لَهُ ثَمانِي أَذرُع', sentenceEn: 'The octopus has eight arms', fun: 'العنكبوت له ثمانية أرجل!' },
        { word: 'تِسعَة', transliteration: 'Tisah', english: 'Nine', emoji: '9️⃣', sentence: 'لله تِسعَةٌ وَتِسعونَ اسماً', sentenceEn: 'Allah has 99 beautiful names', fun: 'الله له 99 اسماً!' },
        { word: 'عَشَرَة', transliteration: 'Asharah', english: 'Ten', emoji: '🔟', sentence: 'عَدَدتُ حَتَّى عَشَرَة', sentenceEn: 'I counted to ten', fun: 'عندنا 10 أصابع في اليدين!' },
      ],
    },
  ],
};

export const SIGHT_WORDS = {
  preK: ['a', 'and', 'the', 'in', 'is', 'it', 'to', 'I', 'you', 'can', 'big', 'little', 'go', 'come', 'up', 'see', 'look', 'my', 'like', 'me'],
  kinder: ['all', 'am', 'are', 'at', 'be', 'but', 'came', 'do', 'eat', 'for', 'get', 'have', 'he', 'help', 'here', 'jump', 'know', 'make', 'not', 'one'],
  grade1: ['after', 'again', 'any', 'ask', 'before', 'been', 'call', 'could', 'did', 'does', 'each', 'find', 'first', 'fly', 'from', 'give', 'going', 'had', 'has', 'her'],
};

export const STORIES = [
  {
    id: 's1', title: 'Sam and the Cat', emoji: '🐱', level: 1, ageGroup: '4-5', language: 'english',
    pages: [
      { text: 'Sam has a cat.', image: '🐱', bg: '#FFF3E0' },
      { text: 'The cat is fat.', image: '😸', bg: '#E8F5E9' },
      { text: 'The cat sat on the mat.', image: '🛏️', bg: '#E3F2FD' },
      { text: 'Sam sat on the mat too.', image: '🧒🐱', bg: '#FCE4EC' },
      { text: 'The cat and Sam are happy!', image: '😊', bg: '#F3E5F5' },
    ],
    comprehension: [
      { question: 'What does Sam have?', options: ['A dog', 'A cat', 'A fish', 'A bird'], answer: 1 },
      { question: 'Where did the cat sit?', options: ['On a chair', 'On a bed', 'On the mat', 'On the floor'], answer: 2 },
      { question: 'How does Sam feel?', options: ['Sad', 'Angry', 'Happy', 'Scared'], answer: 2 },
    ],
  },
  {
    id: 's2', title: 'The Big Red Bus', emoji: '🚌', level: 2, ageGroup: '5-6', language: 'english',
    pages: [
      { text: 'Look at the big red bus!', image: '🚌', bg: '#FFEBEE' },
      { text: 'The bus goes up the hill.', image: '⛰️', bg: '#E8F5E9' },
      { text: 'Children get on the bus.', image: '👦👧', bg: '#E3F2FD' },
      { text: 'The bus takes them to school.', image: '🏫', bg: '#FFF9C4' },
      { text: 'Everyone is ready to learn!', image: '📚', bg: '#F3E5F5' },
    ],
    comprehension: [
      { question: 'What color is the bus?', options: ['Blue', 'Yellow', 'Red', 'Green'], answer: 2 },
      { question: 'Where does the bus go?', options: ['To the park', 'To school', 'To the beach', 'To the store'], answer: 1 },
      { question: 'Who gets on the bus?', options: ['Teachers', 'Adults', 'Children', 'Animals'], answer: 2 },
    ],
  },
  {
    id: 's3', title: 'القطة الصغيرة', emoji: '🐱', level: 1, ageGroup: '4-5', language: 'arabic',
    pages: [
      { text: 'هذه قِطَّةٌ صَغيرة', textEn: 'This is a small cat', image: '🐱', bg: '#FFF3E0' },
      { text: 'القِطَّةُ بَيضاء', textEn: 'The cat is white', image: '🤍', bg: '#E8F5E9' },
      { text: 'القِطَّةُ تَلعَبُ بِالكُرة', textEn: 'The cat plays with the ball', image: '⚽', bg: '#E3F2FD' },
      { text: 'القِطَّةُ جائِعَة', textEn: 'The cat is hungry', image: '😿', bg: '#FCE4EC' },
      { text: 'أَطعَمتُ القِطَّة، فَسَعِدَت', textEn: 'I fed the cat and it was happy', image: '😸', bg: '#F3E5F5' },
    ],
    comprehension: [
      { question: 'ما لون القطة؟', questionEn: 'What color is the cat?', options: ['سوداء', 'بيضاء', 'حمراء', 'صفراء'], answer: 1 },
      { question: 'بماذا تلعب القطة؟', questionEn: 'What does the cat play with?', options: ['لعبة', 'كرة', 'قلم', 'كتاب'], answer: 1 },
    ],
  },
];

export const QUIZ_BANK = {
  english_animals: [
    { q: 'Which animal says "Meow"?', options: ['Dog', 'Cat', 'Bird', 'Fish'], answer: 1, emoji: '🐱' },
    { q: 'Which animal can fly?', options: ['Cat', 'Dog', 'Bird', 'Fish'], answer: 2, emoji: '🐦' },
    { q: 'Which animal lives in water?', options: ['Lion', 'Rabbit', 'Fish', 'Cat'], answer: 2, emoji: '🐠' },
    { q: 'Which is the biggest animal?', options: ['Cat', 'Rabbit', 'Elephant', 'Dog'], answer: 2, emoji: '🐘' },
    { q: 'What sound does a dog make?', options: ['Meow', 'Woof', 'Moo', 'Roar'], answer: 1, emoji: '🐶' },
  ],
  english_colors: [
    { q: 'What color is the sky?', options: ['Red', 'Green', 'Blue', 'Yellow'], answer: 2, emoji: '🌤️' },
    { q: 'What color is an apple?', options: ['Blue', 'Red', 'Purple', 'Orange'], answer: 1, emoji: '🍎' },
    { q: 'What color is grass?', options: ['Red', 'Blue', 'Yellow', 'Green'], answer: 3, emoji: '🌿' },
    { q: 'What color is a banana?', options: ['Yellow', 'Red', 'Blue', 'Green'], answer: 0, emoji: '🍌' },
    { q: 'What color is snow?', options: ['Black', 'Yellow', 'White', 'Brown'], answer: 2, emoji: '❄️' },
  ],
  arabic_animals: [
    { q: 'ما هو الحيوان الذي يقول مياو؟', options: ['كلب', 'قطة', 'طائر', 'سمكة'], answer: 1, emoji: '🐱' },
    { q: 'أي حيوان يطير؟', options: ['قطة', 'كلب', 'طائر', 'سمكة'], answer: 2, emoji: '🐦' },
    { q: 'أي حيوان يعيش في الماء؟', options: ['أسد', 'أرنب', 'سمكة', 'قطة'], answer: 2, emoji: '🐠' },
  ],
};

export const PHRASES = {
  english: [
    { category: 'Greetings', phrases: [
      { text: 'Assalamu Alaikum!', emoji: '👋', response: 'Wa Alaikum Assalam!' },
      { text: 'Good morning!', emoji: '🌅', response: 'Good morning!' },
      { text: 'Good night!', emoji: '🌙', response: 'Sweet dreams!' },
      { text: 'How are you?', emoji: '😊', response: 'Alhamdulillah, I am fine!' },
      { text: 'Goodbye!', emoji: '👋', response: 'Ma\'as-salamah!' },
    ]},
    { category: 'Classroom', phrases: [
      { text: 'Can I have water please?', emoji: '💧', response: 'Here you go!' },
      { text: 'I don\'t understand.', emoji: '🤔', response: 'Let me explain again!' },
      { text: 'I finished!', emoji: '✅', response: 'Great job! Mashallah!' },
      { text: 'Can you repeat please?', emoji: '🔁', response: 'Of course!' },
    ]},
    { category: 'Feelings', phrases: [
      { text: 'I am happy! Alhamdulillah!', emoji: '😊', response: 'That\'s wonderful!' },
      { text: 'I am sad.', emoji: '😢', response: 'May Allah make it easy!' },
      { text: 'I am hungry.', emoji: '🍽️', response: 'Let\'s eat, say Bismillah!' },
      { text: 'I love you!', emoji: '❤️', response: 'I love you too!' },
    ]},
  ],
  arabic: [
    { category: 'التحيات', categoryEn: 'Greetings', phrases: [
      { text: 'السَّلامُ عَلَيكُم', transliteration: 'Assalamu Alaikum', meaning: 'Peace be upon you', emoji: '👋', response: 'وَعَلَيكُمُ السَّلام', responseEn: 'And upon you be peace' },
      { text: 'صَباحُ الخَير', transliteration: 'Sabah al-khair', meaning: 'Good morning', emoji: '🌅', response: 'صَباحُ النُّور', responseEn: 'Morning of light (reply)' },
      { text: 'مَساءُ الخَير', transliteration: 'Masa al-khair', meaning: 'Good evening', emoji: '🌙', response: 'مَساءُ النُّور', responseEn: 'Evening of light (reply)' },
      { text: 'كَيفَ حالُك؟', transliteration: 'Kayfa haluk?', meaning: 'How are you?', emoji: '😊', response: 'الحَمدُ لله، بِخَير', responseEn: 'Praise be to Allah, I am well' },
      { text: 'مَع السَّلامَة', transliteration: "Ma'as-salamah", meaning: 'Goodbye (go with peace)', emoji: '👋', response: 'إِلى اللِّقاء', responseEn: 'See you again' },
    ]},
    { category: 'عبارات يومية', categoryEn: 'Daily Phrases', phrases: [
      { text: 'بِسمِ اللّٰه', transliteration: 'Bismillah', meaning: 'In the name of Allah', emoji: '🤲', response: 'قَبلَ كُلِّ شَيء', responseEn: '(We say it) before everything' },
      { text: 'الحَمدُ لله', transliteration: 'Alhamdulillah', meaning: 'All praise is for Allah', emoji: '🙏', response: 'شُكرٌ لله', responseEn: 'Thanks to Allah' },
      { text: 'شُكراً جَزيلاً', transliteration: 'Shukran jazilan', meaning: 'Thank you very much', emoji: '💝', response: 'عَفواً', responseEn: "You're welcome" },
      { text: 'أُحِبُّكَ', transliteration: 'Uhibbuk', meaning: 'I love you', emoji: '❤️', response: 'أُحِبُّكَ أَيضاً', responseEn: 'I love you too' },
      { text: 'جَزاكَ اللّٰه خَيراً', transliteration: 'Jazak Allah Khayran', meaning: 'May Allah reward you with good', emoji: '⭐', response: 'وَإِيَّاكَ', responseEn: 'And you too' },
    ]},
  ],
};

export default {
  AGE_GROUPS, ENGLISH_ALPHABET, ARABIC_ALPHABET, ISLAMIC_ALPHABET,
  VOCAB_CATEGORIES, SIGHT_WORDS, STORIES, QUIZ_BANK, PHRASES,
};
