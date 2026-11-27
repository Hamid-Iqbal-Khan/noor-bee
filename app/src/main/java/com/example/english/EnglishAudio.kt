package com.example.english

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.File

/**
 * Plays real human recordings:
 *  - single words: Wikimedia Commons / Lingua Libre, fetched by CI into assets/audio/en (credits in CREDITS.csv);
 *  - conversation lines: VOA Learning English (VoaData.clips);
 *  - letter sounds: recorded on this phone by a parent or teacher (no free recordings of single sounds exist).
 * Anything without a recording falls back to the phone's voice. Also records the child for "listen and repeat".
 */
object EnglishAudio {
    private const val DIR = "audio/en"
    /** Tests turn this off so every tap reaches the voice callback they listen to. */
    @Volatile var enabled = true
    private var index: Set<String>? = null
    private var player: MediaPlayer? = null
    private var recorder: MediaRecorder? = null
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private val asIn = Regex("^(\\S+), as in (.+)$")
    /** Prefix for a sound tile: "§sh" means "the sound of sh". */
    const val SOUND = "§"

    fun key(text: String) = text.trim().trim('.', ',', '!', '?', '"', '\'', ' ').lowercase().replace(' ', '_')
    /** Letters and digits only, so straight and curly apostrophes or spacing never stop a match. */
    fun loose(text: String) = text.lowercase().filter { it.isLetterOrDigit() }
    private val looseClips by lazy { VoaData.clips.entries.associate { loose(it.key) to it.value } }
    /** True when [text] is spoken in one of the VOA conversations (so it has a real recording). */
    fun isVoaLine(text: String) = VoaData.clips.containsKey(key(text)) || looseClips.containsKey(loose(text))

    private fun index(ctx: Context): Set<String> = index ?: runCatching {
        ctx.assets.open("$DIR/index.txt").bufferedReader().readLines().map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }.getOrDefault(emptySet()).also { index = it }

    /** Asset path of the recording for [text]: a VOA conversation line, or a single-word recording. */
    private fun clip(ctx: Context, text: String): String? {
        if (text.trim().length <= 1) return null
        val k = key(text)
        return VoaData.clips[k] ?: looseClips[loose(text)] ?: if (k in index(ctx)) "$DIR/$k.ogg" else null
    }

    fun has(ctx: Context, text: String): Boolean = clip(ctx, text) != null

    // ---- letter sounds recorded by a grown-up ---------------------------------------------------------
    private fun soundFile(ctx: Context, grapheme: String) =
        File(File(ctx.filesDir, "english_sounds").apply { mkdirs() }, "${grapheme.lowercase().replace("_", "-")}.m4a")
    fun hasSound(ctx: Context, grapheme: String) = soundFile(ctx, grapheme).let { it.exists() && it.length() > 0 }
    fun deleteSound(ctx: Context, grapheme: String) { soundFile(ctx, grapheme).delete() }
    fun startSoundRecording(ctx: Context, grapheme: String) = startRecording(ctx, soundFile(ctx, grapheme))
    fun playSound(ctx: Context, grapheme: String) = playFile(soundFile(ctx, grapheme))

    // ---- the child's own voice (listen and repeat) ------------------------------------------------------
    private fun myVoice(ctx: Context) = File(ctx.cacheDir, "english_my_voice.m4a")
    fun startMyVoice(ctx: Context) = startRecording(ctx, myVoice(ctx))
    fun hasMyVoice(ctx: Context) = myVoice(ctx).let { it.exists() && it.length() > 0 }
    fun playMyVoice(ctx: Context) = playFile(myVoice(ctx))
    fun clearMyVoice(ctx: Context) { myVoice(ctx).delete() }

    /** Say [text]: a recording if there is one, else [voice]. "s, as in sun" = the letter sound, then the word. */
    fun speak(ctx: Context, text: String, voice: (String) -> Unit) {
        if (text.startsWith(SOUND)) {   // a sound tile
            val g = text.drop(1)
            if (!enabled || !(hasSound(ctx, g) && playSound(ctx, g))) voice(soundOf(g))
            return
        }
        if (!enabled) { voice(text); return }
        handler.removeCallbacksAndMessages(null)
        val m = asIn.find(text)
        if (m != null) {
            val (letter, word) = m.destructured
            if (!(hasSound(ctx, letter) && playSound(ctx, letter))) voice(letter)
            handler.postDelayed({ if (!play(ctx, word)) voice(word) }, 1000)
            return
        }
        if (!play(ctx, text)) voice(text)
    }

    private fun play(ctx: Context, text: String): Boolean {
        val path = clip(ctx, text) ?: return false
        return try {
            stopPlayer()
            val afd = ctx.assets.openFd(path)
            player = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                setOnCompletionListener { it.release(); if (player === it) player = null }
                prepare(); start()
            }
            true
        } catch (e: Exception) {
            Log.w("EnglishAudio", "could not play $path", e); false
        }
    }

    private fun playFile(f: File): Boolean {
        if (!f.exists() || f.length() == 0L) return false
        return try {
            stopPlayer()
            player = MediaPlayer().apply {
                setDataSource(f.absolutePath)
                setOnCompletionListener { it.release(); if (player === it) player = null }
                prepare(); start()
            }
            true
        } catch (e: Exception) {
            Log.w("EnglishAudio", "could not play ${f.name}", e); false
        }
    }

    private fun stopPlayer() { runCatching { player?.release() }; player = null }

    private fun startRecording(ctx: Context, out: File): Boolean {
        stopRecording(); stopPlayer()
        return try {
            @Suppress("DEPRECATION")
            recorder = (if (Build.VERSION.SDK_INT >= 31) MediaRecorder(ctx) else MediaRecorder()).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(44100); setAudioEncodingBitRate(96000)
                setMaxDuration(15_000)
                setOutputFile(out.absolutePath)
                prepare(); start()
            }
            true
        } catch (e: Exception) {
            Log.w("EnglishAudio", "could not record", e); recorder = null; false
        }
    }

    /** Stops a recording in progress; returns true if one was saved. */
    fun stopRecording(): Boolean {
        val r = recorder ?: return false
        recorder = null
        return runCatching { r.stop(); true }.getOrDefault(false).also { runCatching { r.release() } }
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
