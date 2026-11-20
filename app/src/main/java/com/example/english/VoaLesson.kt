package com.example.english

/** One "Let's Learn English" conversation from VOA Learning English (public domain), with its audio clips. */
data class VoaLesson(
    val level: Int,          // our Level 5..8
    val number: Int,         // VOA lesson number
    val title: String,
    val url: String,
    val audio: String,       // whole conversation, under assets/
    val lines: List<Line>,
)
