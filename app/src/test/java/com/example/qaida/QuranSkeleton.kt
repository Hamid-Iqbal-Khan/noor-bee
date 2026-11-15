package com.example.qaida

/**
 * Letters-only form of Quranic text, so IndoPak and Uthmani/simple spellings can be compared:
 * drops harakat, small signs, alif forms and hamza; khari zer counts as ya.
 */
object QuranSkeleton {
  private val marks = Regex("[\\u064B-\\u0655\\u0657-\\u065F\\u0670\\u06D6-\\u06ED\\u0640]")
  fun of(t: String) = t.replace('\u0656', 'ي').replace(marks, "")
    .replace(Regex("[اأإآٱء]"), "").replace('ى', 'ي').replace('ة', 'ه').replace(Regex("\\s"), "")
}
