package com.example.qaida

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/*
 * Medium of instruction for the Quran course: Urdu (right-to-left, the original) or English
 * (left-to-right). All teaching text is written in Urdu in the stage files; in English mode it is
 * swapped at display time from assets/qaida/ui_en.tsv (built by tools/qaida/en/build_ui_catalog.py).
 * Text assembled at run time uses tx(urdu, english). Quran text, letters and marks never change.
 */
object QaidaLang {
    var english by mutableStateOf(false)

    private var catalog: Map<String, String> = emptyMap()
    private val known = HashSet<String>()

    /** Urdu text that was shown in English mode without a translation (test hook; should stay empty). */
    val missing = LinkedHashSet<String>()

    fun load(context: Context) {
        if (catalog.isEmpty()) catalog = parse(context.assets.open("qaida/ui_en.tsv").bufferedReader().readText())
    }

    fun parse(tsv: String): Map<String, String> = tsv.lineSequence().filter { '\t' in it }
        .associate { l -> l.substringBefore('\t') to l.substringAfter('\t') }

    fun setCatalogForTest(map: Map<String, String>) { catalog = map }
    fun catalogSize() = catalog.size

    private const val URDU_ONLY = "یہکےںٹڈڑگپچژھۂۃۓۀ۰۱۲۳۴۵۶۷۸۹"
    fun looksUrdu(s: String) = s.any { it in URDU_ONLY }

    /** The text to show for an Urdu UI string in the current medium. */
    fun tr(s: String): String {
        if (!english || s.isEmpty()) return s
        catalog[s]?.let { return it }
        if (s in known || !looksUrdu(s)) return s
        // a decoration before the text ("💡 ", "  =  ") — translate what follows it
        val start = s.indexOfFirst { it in '\u0621'..'\u06FF' }
        if (start > 0) catalog[s.substring(start)]?.let { return s.substring(0, start) + it }
        if ('\n' in s) return s.split('\n').joinToString("\n") { if (it.isBlank()) it else tr(it) }
        missing += s
        return s
    }

    fun tx(urdu: String, english: String): String = if (this.english) english.also { known += it } else urdu

    private val shortTerm = mapOf(
        "زَبَر" to "fatha", "زِیر" to "kasra", "پیش" to "damma", "جَزْم" to "sukoon", "جزم" to "sukoon",
    )

    /** A vowel-mark name as said inside a spelling (hijje): "fatha", not "Fatha (zabar)". */
    fun term(urdu: String): String = if (english) shortTerm[urdu] ?: tr(urdu) else urdu

    /** Urdu digits in Urdu mode, Western digits in English mode. */
    fun digits(n: Int): String = if (english) n.toString() else n.toString().map { "۰۱۲۳۴۵۶۷۸۹"[it - '0'] }.joinToString("")
}

/** Picks the Urdu or the English form of run-time text. */
fun tx(urdu: String, english: String) = QaidaLang.tx(urdu, english)

/** Translates a single catalog string (for places that are not drawn with UrduText, e.g. content descriptions). */
fun tr(urdu: String) = QaidaLang.tr(urdu)
