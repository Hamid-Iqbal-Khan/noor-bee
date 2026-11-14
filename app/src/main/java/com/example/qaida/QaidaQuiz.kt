package com.example.qaida

import kotlin.random.Random

/** One multiple-choice question. Exactly one option is correct. */
data class QaidaQuestion(
    val prompt: String,
    val promptIsArabic: Boolean,
    val instruction: String,          // Urdu
    val options: List<String>,
    val optionsAreArabic: Boolean,
    val correctIndex: Int,
    val audioKey: String? = null,
)

/** Builds the Stage 1 practice quiz from the lesson data (pure, testable). */
object QaidaQuiz {

    fun generate(random: Random, length: Int = QaidaProgress.QUIZ_LENGTH): List<QaidaQuestion> {
        val letters = QaidaData.letters
        return List(length) { i ->
            when (i % 3) {
                0 -> { // letter -> name
                    val answer = letters.random(random)
                    val wrong = letters.filter { it.id != answer.id && it.qaidaName != answer.qaidaName }.shuffled(random).take(3)
                    val options = (wrong + answer).shuffled(random)
                    QaidaQuestion(answer.arabic, true, "اس حرف کا نام کیا ہے؟",
                        options.map { it.qaidaName }, false, options.indexOf(answer), answer.audioKey)
                }
                1 -> { // name -> letter (uses a merged-sound group when possible: the hard part for Urdu speakers)
                    val group = QaidaData.soundGroups.random(random)
                    val answer = QaidaData.letter(group.letterIds.random(random))
                    val sameGroup = group.letterIds.filter { it != answer.id }.map { QaidaData.letter(it) }
                    val others = letters.filter { it.id != answer.id && it !in sameGroup }.shuffled(random)
                    val wrong = (sameGroup + others).take(3)
                    val options = (wrong + answer).shuffled(random)
                    QaidaQuestion(answer.qaidaName, false, "'${answer.qaidaName}' کون سا حرف ہے؟",
                        options.map { it.arabic }, true, options.indexOf(answer), answer.audioKey)
                }
                else -> { // letter with harakah -> harakah name
                    val letter = QaidaData.harakatLetters().random(random)
                    val h = QaidaData.Harakah.values().random(random)
                    val options = QaidaData.Harakah.values().toList()
                    QaidaQuestion(QaidaData.withHarakah(letter, h), true, "اس حرف پر کون سی حرکت ہے؟",
                        options.map { it.urduName }, false, options.indexOf(h), QaidaData.audioKey(letter, h))
                }
            }
        }
    }
}
