package com.example.english

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log

/**
 * Plays real human recordings for English words (Wikimedia Commons / Lingua Libre, fetched by CI into
 * assets/audio/en, listed in index.txt with their credits in CREDITS.csv). Anything without a recording
 * falls back to the phone's voice. Single letters always use the voice, so a tile says just its letter.
 */
object EnglishAudio {
    private const val DIR = "audio/en"
    /** Tests turn this off so every tap reaches the voice callback they listen to. */
    @Volatile var enabled = true
    private var index: Set<String>? = null
    private var player: MediaPlayer? = null
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private val asIn = Regex("^(\\S+), as in (.+)$")

    fun key(text: String) = text.trim().trim('.', ',', '!', '?', '"', '\'', ' ').lowercase().replace(' ', '_')

    private fun index(ctx: Context): Set<String> = index ?: runCatching {
        ctx.assets.open("$DIR/index.txt").bufferedReader().readLines().map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }.getOrDefault(emptySet()).also { index = it }

    /** Asset path of the recording for [text]: a VOA conversation line, or a single-word recording. */
    private fun clip(ctx: Context, text: String): String? {
        if (text.trim().length <= 1) return null
        val k = key(text)
        return VoaData.clips[k] ?: if (k in index(ctx)) "$DIR/$k.ogg" else null
    }

    fun has(ctx: Context, text: String): Boolean = clip(ctx, text) != null

    /** Say [text]: a recording if there is one, else [voice]. "s, as in sun" = the letter, then the word. */
    fun speak(ctx: Context, text: String, voice: (String) -> Unit) {
        if (!enabled) { voice(text); return }
        handler.removeCallbacksAndMessages(null)
        val m = asIn.find(text)
        if (m != null) {
            val (letter, word) = m.destructured
            voice(letter)
            handler.postDelayed({ if (!play(ctx, word)) voice(word) }, 900)
            return
        }
        if (!play(ctx, text)) voice(text)
    }

    private fun play(ctx: Context, text: String): Boolean {
        val path = clip(ctx, text) ?: return false
        return try {
            player?.release()
            val afd = ctx.assets.openFd(path)
            player = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                setOnCompletionListener { it.release(); if (player === it) player = null }
                prepare(); start()
            }
            true
        } catch (e: Exception) {
            Log.w("EnglishAudio", "could not play ${key(text)}", e); false
        }
    }

    /** One credit line per recording: word, author, licence, source page. */
    data class Credit(val word: String, val author: String, val licence: String, val source: String)

    fun credits(ctx: Context): List<Credit> = runCatching {
        ctx.assets.open("$DIR/CREDITS.csv").bufferedReader().readLines().drop(1).mapNotNull { line ->
            val c = parseCsv(line); if (c.size >= 4) Credit(c[0], c[1], c[2], c[3]) else null
        }
    }.getOrDefault(emptyList())

    private fun parseCsv(line: String): List<String> {
        val out = mutableListOf<String>(); val sb = StringBuilder(); var q = false; var i = 0
        while (i < line.length) {
            val ch = line[i]
            when {
                q && ch == '"' && i + 1 < line.length && line[i + 1] == '"' -> { sb.append('"'); i++ }
                ch == '"' -> q = !q
                ch == ',' && !q -> { out += sb.toString(); sb.clear() }
                else -> sb.append(ch)
            }
            i++
        }
        out += sb.toString(); return out
    }
}
