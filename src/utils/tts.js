// src/utils/tts.js
// Enhanced TTS with Google Cloud Text-to-Speech API
// Falls back to expo-speech if API key not set

import * as Speech from 'expo-speech';
import * as Haptics from 'expo-haptics';

// API key: set in-app via Profile → Voice Settings (stored on device), or
// optionally baked in at build time with EXPO_PUBLIC_GOOGLE_TTS_API_KEY.
// NOTE: any key shipped to or stored on a client device can be extracted.
// For a production release, proxy TTS through your own backend or ship
// pre-recorded audio instead (see UAT report).
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Platform } from 'react-native';

const BUILD_TIME_KEY = process.env.EXPO_PUBLIC_GOOGLE_TTS_API_KEY || '';
const TTS_KEY_STORAGE = 'google_tts_key';
let cachedKey = null; // null = not loaded yet

async function getApiKey() {
  if (cachedKey === null) {
    try { cachedKey = (await AsyncStorage.getItem(TTS_KEY_STORAGE)) || BUILD_TIME_KEY; }
    catch { cachedKey = BUILD_TIME_KEY; }
  }
  return cachedKey;
}

/** Call after saving/removing the key in Voice Settings so it applies immediately. */
export function setTTSApiKey(key) { cachedKey = key || BUILD_TIME_KEY; }

// Haptics are unavailable on web and on some devices; never let them throw.
function tapHaptic() {
  if (Platform.OS === 'web') return;
  Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light).catch(() => {});
}

// ─── Voice configurations ─────────────────────────────────────────────────────
const VOICES = {
  // Indian English Male — warm, clear, child-friendly
  english: {
    languageCode: 'en-IN',
    name: 'en-IN-Wavenet-C',   // Indian male voice (WaveNet = most natural)
    ssmlGender: 'MALE',
  },
  // Arabic — native Arabic male voice, proper makhraj
  arabic: {
    languageCode: 'ar-XA',
    name: 'ar-XA-Wavenet-B',   // Arabic male voice
    ssmlGender: 'MALE',
  },
  // English for rhymes — slightly slower, warmer
  english_slow: {
    languageCode: 'en-IN',
    name: 'en-IN-Wavenet-C',
    ssmlGender: 'MALE',
  },
};

// ─── Audio cache to avoid re-fetching same words ─────────────────────────────
const audioCache = {};
let currentSound = null;
let pendingTimer = null;

// ─── Google Cloud TTS fetch ───────────────────────────────────────────────────
async function fetchGoogleTTS(text, voiceConfig, speakingRate = 0.85, pitch = 0) {
  const apiKey = await getApiKey();
  if (!apiKey) return null;
  const cacheKey = `${text}_${voiceConfig.name}_${speakingRate}`;
  if (audioCache[cacheKey]) return audioCache[cacheKey];

  try {
    const response = await fetch(
      'https://texttospeech.googleapis.com/v1/text:synthesize',
      {
        method: 'POST',
        // Key in a header (not the URL) so it does not end up in proxy/server logs.
        headers: { 'Content-Type': 'application/json', 'X-Goog-Api-Key': apiKey },
        body: JSON.stringify({
          input: { text },
          voice: voiceConfig,
          audioConfig: {
            audioEncoding: 'MP3',
            speakingRate,
            pitch,
            effectsProfileId: ['small-bluetooth-speaker-class-device'],
          },
        }),
      }
    );

    const data = await response.json();
    if (data.audioContent) {
      audioCache[cacheKey] = data.audioContent; // base64 MP3
      return data.audioContent;
    }
    return null;
  } catch (e) {
    console.warn('Google TTS error:', e);
    return null;
  }
}

// ─── Play base64 audio using expo-av ─────────────────────────────────────────
async function playBase64Audio(base64Audio) {
  try {
    const { Audio } = await import('expo-av');
    await Audio.setAudioModeAsync({ playsInSilentModeIOS: true });
    if (currentSound) { currentSound.unloadAsync().catch(() => {}); currentSound = null; }
    const { sound } = await Audio.Sound.createAsync(
      { uri: `data:audio/mp3;base64,${base64Audio}` },
      { shouldPlay: true }
    );
    currentSound = sound;
    sound.setOnPlaybackStatusUpdate((status) => {
      if (status.didJustFinish) { sound.unloadAsync(); if (currentSound === sound) currentSound = null; }
    });
  } catch (e) {
    console.warn('Audio play error:', e);
  }
}

// ─── FALLBACK: expo-speech with best available settings ──────────────────────
function speakFallback(text, language = 'en') {
  Speech.stop();
  if (language === 'ar') {
    Speech.speak(text, {
      language: 'ar',
      pitch: 1.0,
      rate: 0.65,   // slower for Arabic clarity
    });
  } else {
    // English — use en-IN if available, fallback to en
    Speech.speak(text, {
      language: 'en-IN',
      pitch: 1.0,
      rate: 0.80,
    });
  }
}

// ─── MAIN SPEAK FUNCTIONS (export these) ─────────────────────────────────────

/**
 * Speak English text with Indian male voice
 * @param {string} text
 * @param {number} rate - 0.5 slow, 1.0 normal (default 0.85)
 */
export async function speakEnglish(text, rate = 0.85) {
  if (!text) return;
  tapHaptic();

  {
    const audio = await fetchGoogleTTS(text, VOICES.english, rate, 0);
    if (audio) { await playBase64Audio(audio); return; }
  }
  speakFallback(text, 'en');
}

/**
 * Speak Arabic text with native Arabic male voice
 * @param {string} text
 * @param {number} rate - 0.5 slow, 1.0 normal (default 0.75)
 */
export async function speakArabic(text, rate = 0.75) {
  if (!text) return;
  tapHaptic();

  {
    const audio = await fetchGoogleTTS(text, VOICES.arabic, rate, 0);
    if (audio) { await playBase64Audio(audio); return; }
  }
  speakFallback(text, 'ar');
}

/**
 * Speak a rhyme slowly and clearly (Indian English male)
 * @param {string} text
 */
export async function speakRhyme(text) {
  if (!text) return;
  tapHaptic();

  {
    const audio = await fetchGoogleTTS(text, VOICES.english_slow, 0.75, -1);
    if (audio) { await playBase64Audio(audio); return; }
  }
  speakFallback(text, 'en');
}

/**
 * Speak letter + Islamic word (e.g. "A for Allah")
 * @param {string} letter
 * @param {string} islamicWord
 * @param {string} meaning
 */
export async function speakIslamicLetter(letter, islamicWord, meaning) {
  const text = `${letter} for ${islamicWord}. ${islamicWord} means ${meaning}.`;
  await speakEnglish(text, 0.80);
}

/**
 * Speak Arabic letter name + word
 * @param {string} letterName  e.g. "Alif"
 * @param {string} arabicWord  e.g. "الله"
 * @param {string} wordEn      e.g. "Allah"
 */
export async function speakArabicLetter(letterName, arabicWord, wordEn) {
  // Speak name in English first
  await speakEnglish(`${letterName}.`, 0.80);
  // Short pause then Arabic word
  clearTimeout(pendingTimer);
  pendingTimer = setTimeout(() => { pendingTimer = null; speakArabic(arabicWord, 0.70); }, 800);
}

/**
 * Stop all speech
 */
export function stopSpeech() {
  clearTimeout(pendingTimer);
  pendingTimer = null;
  Speech.stop();
  if (currentSound) { currentSound.stopAsync().catch(() => {}); currentSound = null; }
}

// ─── SETUP INSTRUCTIONS (shown in app if no API key) ─────────────────────────
export async function isGoogleTTSConfigured() { return !!(await getApiKey()); }
export const TTS_SETUP_URL = 'https://console.cloud.google.com/apis/library/texttospeech.googleapis.com';
