package com.example.qaida

/**
 * Letters-only form of Quranic text, so IndoPak and Uthmani/simple spellings can be compared:
 * drops harakat, small signs, alif forms and every hamza form; khari zer counts as ya.
 */
object QuranSkeleton {
  private val marks = Regex("[\\u064B-\\u0655\\u0657-\\u065F\\u0670\\u06D6-\\u06ED\\u0640]")
  fun of(t: String) = t.replace("\uFEFF", "").replace('\u0656', 'ي').replace(marks, "")
    .replace(Regex("ى(?=[\\u0621-\\u064A])"), "")   // Uthmani writes some long-a sounds as a mid-word ى (اَدْرٰىكَ)
    .replace(Regex("[اأإآٱءئؤ]"), "")   // hamza seats differ between IndoPak and simple spelling (لَتُسْئَلُنَّ / لتسألن).replace('ى', 'ي').replace('ة', 'ه').replace(Regex("\\s"), "")
}
