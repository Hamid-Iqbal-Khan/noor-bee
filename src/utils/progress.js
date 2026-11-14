// src/utils/progress.js
// Progress tracking that drives XP, badges and "Today's Learning Plan".
import { Platform, Alert } from 'react-native';
import { Storage, STORAGE_KEYS } from './storage';
import { ENGLISH_ALPHABET, ARABIC_ALPHABET } from '../data/curriculum';

const today = () => new Date().toDateString();

// Record that an activity (e.g. 'alphabet_en', 'quiz', 'vocab_animals') was done today.
export async function markActivity(id) {
  await Storage.markLessonComplete(`${today()}|${id}`);
}

export async function getTodayActivities() {
  const all = await Storage.getCompletedLessons();
  const prefix = `${today()}|`;
  return all.filter(x => x.startsWith(prefix)).map(x => x.slice(prefix.length));
}

export async function markLetterSeen(language, letter) {
  const isAr = language === 'arabic';
  const key = isAr ? STORAGE_KEYS.SEEN_LETTERS_AR : STORAGE_KEYS.SEEN_LETTERS_EN;
  const seen = await Storage.addToSet(key, letter);
  await markActivity(isAr ? 'alphabet_ar' : 'alphabet_en');
  const total = isAr ? ARABIC_ALPHABET.length : ENGLISH_ALPHABET.length;
  if (seen.length >= total) await Storage.awardBadge(isAr ? 'alphabet_ar' : 'alphabet_en');
}

export async function markWordSeen(categoryId, word) {
  const seen = await Storage.addToSet(STORAGE_KEYS.SEEN_WORDS, `${categoryId}:${word}`);
  await markActivity(`vocab_${categoryId}`);
  if (seen.length >= 50) await Storage.awardBadge('vocab_50');
}

// Cross-platform confirm: Alert.alert buttons are a no-op on react-native-web.
export function confirmAction(title, message, onConfirm, confirmText = 'OK') {
  if (Platform.OS === 'web') {
    // eslint-disable-next-line no-alert
    if (typeof window !== 'undefined' && window.confirm(`${title}\n\n${message}`)) onConfirm();
    return;
  }
  Alert.alert(title, message, [
    { text: 'Cancel', style: 'cancel' },
    { text: confirmText, style: 'destructive', onPress: onConfirm },
  ]);
}

export function notify(title, message) {
  if (Platform.OS === 'web') {
    // eslint-disable-next-line no-alert
    if (typeof window !== 'undefined') window.alert(`${title}\n\n${message}`);
    return;
  }
  Alert.alert(title, message);
}
