package com.example.english

import android.content.Context
import kotlin.random.Random

/** One practice question. [speak] is said aloud when the prompt is tapped (and for "listen" questions). */
data class EnQuestion(
    val prompt: String,
    val instruction: String,
    val options: List<String>,
    val correctIndex: Int,
    val speak: String? = null,
    val promptIsWord: Boolean = false,  // true: show the prompt as large letters (not a picture)
)

/** Progress for the English reading course, per child, kept apart from the Quran course. */
class EnglishProgress(context: Context, profileId: Int) {
    private val prefs = context.applicationContext.getSharedPreferences("english_course_$profileId", Context.MODE_PRIVATE)
    fun isDone(lesson: String) = prefs.getBoolean("done_$lesson", false)
    fun markDone(lesson: String) = prefs.edit().putBoolean("done_$lesson", true).apply()
    fun best(stage: Int) = prefs.getInt("best_$stage", 0)
    fun record(stage: Int, score: Int) {
        if (score > best(stage)) prefs.edit().putInt("best_$stage", score).apply()
        if (score >= PASS_MARK) markDone(EnglishCourse.stage(stage).quizKey)
    }
    fun seen(): Set<String> = prefs.getStringSet("seen", emptySet()) ?: emptySet()
    fun markSeen(letter: String) = prefs.edit().putStringSet("seen", seen() + letter).apply()

    companion object {
        const val QUIZ_LENGTH = 10
        const val PASS_MARK = 8
    }
}

object EnglishQuiz {
    /** The answer plus three different wrong options, shuffled. */
    private fun options(answer: String, pool: List<String>, random: Random): Pair<List<String>, Int> {
        val opts = (pool.filter { it != answer }.distinct().shuffled(random).take(3) + answer).shuffled(random)
        return opts to opts.indexOf(answer)
    }

    private fun q(prompt: String, instruction: String, answer: String, pool: List<String>, random: Random,
                  speak: String? = null, word: Boolean = false): EnQuestion {
        val (opts, i) = options(answer, pool, random)
        return EnQuestion(prompt, instruction, opts, i, speak, word)
    }

    fun generate(stage: Int, random: Random, length: Int = EnglishProgress.QUIZ_LENGTH): List<EnQuestion> = List(length) { n ->
        when (stage) {
            1 -> letters(n % 3, random)
            2 -> shortWords(n % 3, random)
            3 -> if (n % 2 == 0) letterTeams((n / 2) % 3, random) else longVowels((n / 2) % 3, random)
            4 -> if (n % 2 == 0) trickyWord(random) else firstSentences((n / 2) % 3, random)
            else -> speaking(stage, n % 3, random)
        }
    }

    private fun letters(kind: Int, random: Random): EnQuestion {
        // "starts with" needs a keyword that really starts with the letter (x is taught with six)
        val s = if (kind == 0) EnglishCourse.alphabet.filter { it.keyword.lowercase().startsWith(it.grapheme) }.random(random)
            else EnglishCourse.alphabet.random(random)
        val letters = EnglishCourse.alphabet.map { it.grapheme }
        return when (kind) {
            0 -> q(s.emoji, "Which letter does '${s.keyword}' start with?", s.grapheme, letters, random, s.keyword)
            1 -> q(s.grapheme.uppercase(), "Find the small letter for this big letter.", s.grapheme, letters, random, word = true)
            else -> q("🔊", "Listen. Which letter is it?", s.grapheme, letters, random, s.spoken)
        }
    }

    private fun shortWords(kind: Int, random: Random): EnQuestion {
        val all = EnglishCourse.shortA + EnglishCourse.shortE + EnglishCourse.shortI + EnglishCourse.shortO + EnglishCourse.shortU
        val wd = all.random(random)
        return when (kind) {
            0 -> q(wd.emoji, "Which word is this?", wd.word, all.map { it.word }, random, wd.word)
            1 -> q(wd.parts.joinToString(" · "), "Blend the sounds. Which word do they make?", wd.word, all.map { it.word }, random, word = true)
            else -> q(wd.word, "Which vowel sound do you hear first?", EnglishCourse.firstVowel(wd), listOf("a", "e", "i", "o", "u"), random, wd.word, word = true)
        }
    }

    private fun letterTeams(kind: Int, random: Random): EnQuestion {
        val all = EnglishCourse.digraphWordsA + EnglishCourse.digraphWordsB + EnglishCourse.digraphWordsC
        val teams = (EnglishCourse.digraphsA + EnglishCourse.digraphsB + EnglishCourse.digraphsC).map { it.grapheme }.distinct()
        val wd = all.random(random)
        return when (kind) {
            0 -> q(wd.emoji, "Which word is this?", wd.word, all.map { it.word }, random, wd.word)
            1 -> {
                val team = wd.parts.map { it.lowercase() }.first { it in teams }
                // wrong options must not also be in the word (quick has both qu and ck)
                q(wd.word, "Which sound team is in this word?", team, teams.filter { t -> wd.parts.none { it.equals(t, true) } }, random, wd.word, word = true)
            }
            else -> q("🔊", "Listen. Which word is it?", wd.word, all.map { it.word }, random, wd.word)
        }
    }

    private fun longVowels(kind: Int, random: Random): EnQuestion {
        val (short, long) = EnglishCourse.magicE.random(random)
        return when (kind) {
            0 -> q(short.word, "Add a magic e. What does it become?", long.word,
                EnglishCourse.magicE.flatMap { listOf(it.first.word, it.second.word) }.filter { it != short.word }, random, long.word, word = true)
            1 -> {
                val wd = EnglishCourse.teamWords.random(random)
                q(wd.emoji, "Which word is this?", wd.word, EnglishCourse.teamWords.map { it.word }, random, wd.word)
            }
            else -> {
                val wd = EnglishCourse.teamWords.random(random)
                val team = wd.parts.first { p -> EnglishCourse.vowelTeams.any { it.grapheme == p } }
                q(wd.word, "Which vowel team is in this word?", team,
                    EnglishCourse.vowelTeams.map { it.grapheme }.filter { it !in wd.parts }, random, wd.word, word = true)
            }
        }
    }

    private fun trickyWord(random: Random): EnQuestion {
        val wd = EnglishCourse.allTricky.random(random)
        return q("🔊", "Listen. Tap the word you hear.", wd, EnglishCourse.allTricky, random, wd)
    }

    private fun firstSentences(kind: Int, random: Random): EnQuestion {
        val pool = EnglishCourse.sentences1 + EnglishCourse.sentences2
        val s = pool.random(random)
        return if (kind == 2) q(s.text, "Which picture goes with this sentence?", s.emoji, pool.map { it.emoji }, random, s.text, word = true)
        else gap(s, pool, random)
    }

    // the gap is the word the picture shows, so only one option fits
    private fun gap(s: Sentence, pool: List<Sentence>, random: Random): EnQuestion {
        val shown = s.emoji + "  " + s.text.replaceFirst(Regex("\\b${Regex.escape(s.key)}\\b"), "____")
        return q(shown, "Look at the picture. Which word fills the gap?", s.key, pool.map { it.key }, random, s.text, word = true)
    }

    /** Levels 5–8: hear a line and find it, pick the right reply, fill a missing word. */
    private fun speaking(stage: Int, kind: Int, random: Random): EnQuestion {
        val talks = EnglishCourse.speakingPool(stage).second
        val lines = talks.flatten().map { it.text }.distinct()
        val otherLevels = EnglishCourse.stages.filter { it.number >= 5 }.flatMap { st -> st.lessons.map { it.dialogue } }.flatten()
            .map { it.text }.distinct()
        return when (kind) {
            0 -> {
                val ln = lines.random(random)
                q("🔊", "Listen. Which sentence do you hear?", ln, lines, random, ln, word = true)
            }
            1 -> {
                val talk = talks.filter { it.size >= 2 }.random(random)
                val i = random.nextInt(talk.size - 1)
                val answer = talk[i + 1].text
                // wrong replies come from other conversations, so they do not fit this one
                val wrong = otherLevels.filter { o -> talk.none { it.text == o } }
                q("${talk[i].speaker}: ${talk[i].text}", "What comes next in the conversation?", answer, wrong, random, talk[i].text, word = true)
            }
            else -> {
                val words = { t: String -> t.split(' ').map { it.trim('.', ',', '!', '?', '"', '\'', '…') }.filter { it.length >= 4 && it.all(Char::isLetter) } }
                val ln = lines.filter { words(it).isNotEmpty() }.random(random)
                val gap = words(ln).maxBy { it.length }
                val shown = ln.replaceFirst(Regex("\\b${Regex.escape(gap)}\\b"), "____")
                val pool = lines.flatMap(words).distinct().filter { w -> !Regex("\\b${Regex.escape(w)}\\b", RegexOption.IGNORE_CASE).containsMatchIn(ln) }
                q(shown, "Which word fills the gap?", gap, pool, random, ln, word = true)
            }
        }
    }
}
