package com.example.qaida

/**
 * Letters-only form of Quranic text, so IndoPak and Uthmani/simple spellings can be compared:
 * drops harakat, small signs, alif forms and every hamza form. Khari zer on an alif (اٖلٰفِهِمْ) stands for a written ya;
 * on a pronoun (بِهٖ) it is only a long vowel.
 */
// Hamza seats differ between IndoPak and simple spelling (لَتُسْئَلُنَّ / لتسألن), so every hamza form is dropped.
object QuranSkeleton {
  private val marks = Regex("[\\u064B-\\u065F\\u0670\\u06D6-\\u06ED\\u0640]")
  fun of(t: String) = t.replace("\uFEFF", "").replace("\u0627\u0656", "\u0627ي")
    .replace("\u0621\u0657", "\u0621و")                      // الْمَوْءٗدَةُ: the small waw is a written waw
    .replace(Regex("\u0670و(?=ة)"), "\u0670")                  // الصَّلٰوةَ / الزَّكٰوةَ / الْحَيٰوةَ are spelled with alif in simple text
    .replace(marks, "")
    .replace(Regex("ى(?=[\\u0622-\\u064A])"), "")   // Uthmani writes some long-a sounds as a mid-word ى (اَدْرٰىكَ)
    .replace(Regex("[اأإآٱءئؤ]"), "").replace('ى', 'ي').replace('ة', 'ه').replace(Regex("\\s"), "")
}
