package com.example.qaida

/*
 * Where a Qaida sound comes from, best first:
 *  1. a qari recording bundled in assets/audio/qaida/<key>.mp3 (when the recordings are added)
 *  2. real recitation — whole ayat by Mishary Alafasy (everyayah.com, already used by Quran Explorer) and single
 *     Quran words from Quran.com's word-by-word audio — streamed once, then kept in the app's cache for offline use
 *  3. the phone's Arabic voice, as a last resort (isolated letters and harakat drills, until a qari records them)
 */
object QuranAudio {
    const val TAAWWUDH = "اَعُوْذُ بِاللّٰهِ مِنَ الشَّيْطٰنِ الرَّجِيْمِ"
    const val BASMALAH = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ"
    const val KEY_TAAWWUDH = "q_taawwudh"
    const val KEY_BASMALAH = "q_basmalah"

    private const val AYAH_BASE = "https://everyayah.com/data/Alafasy_128kbps/"
    private const val WORD_BASE = "https://audio.qurancdn.com/wbw/"

    private fun p3(n: Int) = n.toString().padStart(3, '0')
    fun ayahUrl(surah: Int, ayah: Int) = "$AYAH_BASE${p3(surah)}${p3(ayah)}.mp3"
    fun wordUrl(surah: Int, ayah: Int, word: Int) = "$WORD_BASE${p3(surah)}_${p3(ayah)}_${p3(word)}.mp3"

    /** First word of each muqatta'at opening, as recited (surah, ayah). */
    private val muqattaat = mapOf(
        "alm" to (2 to 1), "almas" to (7 to 1), "alr" to (10 to 1), "almr" to (13 to 1), "khyas" to (19 to 1),
        "taha" to (20 to 1), "tsm" to (26 to 1), "ts" to (27 to 1), "yasin" to (36 to 1), "sad" to (38 to 1),
        "hm" to (40 to 1), "asq" to (42 to 2), "qaf" to (50 to 1), "nun" to (68 to 1),
    )

    /** Fixed URL for keys that name a Quran location; null when the key is not one. */
    fun urlForKey(key: String): List<String>? {
        fun nums(prefix: String) = key.removePrefix(prefix).split('_').mapNotNull { it.toIntOrNull() }
        return when {
            key == KEY_BASMALAH -> listOf(ayahUrl(1, 1))
            key == KEY_TAAWWUDH -> listOf("${AYAH_BASE}audhubillah.mp3")
            key.startsWith("s3_ayah_") -> nums("s3_ayah_").takeIf { it.size == 2 }?.let { (s, n) -> listOf(ayahUrl(s, n)) }
            key.startsWith("s5_wbw_") -> nums("s5_wbw_").takeIf { it.size == 3 }?.let { (s, n, w) -> listOf(wordUrl(s, n, w)) }
            key.startsWith("s2_muqattaat_") -> muqattaat[key.removePrefix("s2_muqattaat_")]?.let { (s, n) -> listOf(wordUrl(s, n, 1)) }
            else -> null
        }
    }

    // ---- Finding example words inside the surahs the course already contains ----------------------------

    data class Loc(val surah: Int, val ayah: Int, val word: Int)

    /** Normalises spelling so the same word typed in two places matches: marks sorted per letter, jazm/madda/small meem dropped, vowel on a bare alif dropped. */
    fun normalize(word: String): String {
        val sb = StringBuilder()
        var marks = mutableListOf<Char>()
        var lastLetter: Char? = null
        fun flush() {
            lastLetter?.let { l ->
                sb.append(if (l == 'ى') 'ي' else l)
                if (l != 'ا') marks.sorted().forEach { sb.append(it) }
            }
            marks = mutableListOf()
        }
        for (c in word) {
            when {
                c in 'ً'..'ٗ' || c == 'ٰ' -> if (c != 'ْ' && c != 'ٓ' && c != 'ٔ') marks.add(c)
                c == 'ـ' || c in 'ۖ'..'ۭ' -> Unit
                else -> { flush(); lastLetter = c }
            }
        }
        flush()
        return sb.toString()
    }

    class Index(private val map: Map<String, List<Loc>>) {
        val size get() = map.size

        /** Recited audio for a word or short phrase of course words, or null when any word is not found. */
        fun urlsFor(text: String): List<String>? {
            val words = text.trim().split(' ').filter { it.isNotBlank() }
            if (words.isEmpty()) return null
            val firsts = map[normalize(words[0])] ?: return null
            // Prefer one place where the whole phrase occurs in order (same ayah).
            for (f in firsts) {
                val locs = words.indices.map { i -> Loc(f.surah, f.ayah, f.word + i) }
                if (words.indices.all { i -> map[normalize(words[i])]?.contains(locs[i]) == true })
                    return locs.map { wordUrl(it.surah, it.ayah, it.word) }
            }
            val each = words.map { w -> map[normalize(w)]?.firstOrNull() ?: return null }
            return each.map { wordUrl(it.surah, it.ayah, it.word) }
        }
    }

    fun buildIndex(surahs: Collection<QaidaStage5.WbwSurah>, ayat: Collection<QaidaStage3.Ayah>): Index {
        val map = HashMap<String, MutableList<Loc>>()
        fun add(a: QaidaStage3.Ayah) = a.text.split(' ').forEachIndexed { i, w ->
            map.getOrPut(normalize(w)) { mutableListOf() }.add(Loc(a.surah, a.number, i + 1))
        }
        surahs.forEach { s -> s.ayat.forEach { add(it.ayah) } }
        ayat.forEach { add(it) }
        return Index(map)
    }

    /** Every surah text the course contains (Stages 3–10). */
    fun courseAyat(juz: Map<Int, QaidaStage5.WbwSurah>): List<QaidaStage3.Ayah> =
        (QaidaStage3.fatiha + QaidaStage3.ikhlas + QaidaStage4.surahs.flatMap { it.ayat } +
            QaidaStage6.surahs.flatMap { s -> s.ayat.map { it.ayah } } + juz.values.flatMap { s -> s.ayat.map { it.ayah } })
            .distinctBy { it.surah to it.number }

    // ---- Phone voice: say letters by their Arabic names --------------------------------------------------
    private val letterNames = mapOf(
        "alif" to "أَلِفْ", "ba" to "بَاءْ", "ta" to "تَاءْ", "tha" to "ثَاءْ", "jim" to "جِيمْ", "ha_heavy" to "حَاءْ",
        "kha" to "خَاءْ", "dal" to "دَالْ", "dhal" to "ذَالْ", "ra" to "رَاءْ", "zay" to "زَايْ", "sin" to "سِينْ",
        "shin" to "شِينْ", "sad" to "صَادْ", "dad" to "ضَادْ", "ta_heavy" to "طَاءْ", "dha_heavy" to "ظَاءْ",
        "ain" to "عَيْنْ", "ghain" to "غَيْنْ", "fa" to "فَاءْ", "qaf" to "قَافْ", "kaf" to "كَافْ", "lam" to "لَامْ",
        "mim" to "مِيمْ", "nun" to "نُونْ", "waw" to "وَاوْ", "ha" to "هَاءْ", "hamza" to "هَمْزَةْ", "ya" to "يَاءْ",
    )

    /** What the phone voice should read for a key: a letter is read by its Arabic name, not as a bare glyph. */
    fun speechFor(key: String, text: String): String =
        if (key.startsWith("letter_")) letterNames[key.removePrefix("letter_")] ?: text else text
}
