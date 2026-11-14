// src/utils/storage.js
import AsyncStorage from '@react-native-async-storage/async-storage';

const KEYS = {
  USER_PROFILE: 'user_profile',
  PROGRESS: 'progress',
  XP: 'xp',
  STREAK: 'streak',
  LAST_ACTIVE: 'last_active',
  COMPLETED_LESSONS: 'completed_lessons',
  BADGES: 'badges',
  SETTINGS: 'settings',
  SEEN_LETTERS_EN: 'seen_letters_en',
  SEEN_LETTERS_AR: 'seen_letters_ar',
  SEEN_WORDS: 'seen_words',
  TTS_KEY: 'google_tts_key',
};

export const STORAGE_KEYS = KEYS;

// Single source of truth for badges (used by Profile and by awardBadge).
export const ALL_BADGES = [
  { id: 'first_login', emoji: '🌟', name: 'First Step', desc: 'Started the journey!', xp: 10 },
  { id: 'streak_3', emoji: '🔥', name: '3-Day Streak', desc: '3 days in a row!', xp: 50 },
  { id: 'streak_7', emoji: '🏆', name: '7-Day Streak', desc: 'A full week!', xp: 150 },
  { id: 'alphabet_en', emoji: '🔤', name: 'Alphabet Ace', desc: 'Learned all 26 letters!', xp: 100 },
  { id: 'alphabet_ar', emoji: '🌙', name: 'Arabic Star', desc: 'Learned all 28 letters!', xp: 150 },
  { id: 'quiz_perfect', emoji: '💎', name: 'Perfect Quiz', desc: '100% on a quiz!', xp: 200 },
  { id: 'story_1', emoji: '📖', name: 'Story Reader', desc: 'Read your first story!', xp: 75 },
  { id: 'vocab_50', emoji: '📚', name: 'Word Explorer', desc: 'Learned 50 words!', xp: 250 },
];
const BADGE_XP = Object.fromEntries(ALL_BADGES.map(b => [b.id, b.xp]));

export const Storage = {
  async getProfile() {
    try {
      const data = await AsyncStorage.getItem(KEYS.USER_PROFILE);
      return data ? JSON.parse(data) : null;
    } catch { return null; }
  },

  async saveProfile(profile) {
    try {
      await AsyncStorage.setItem(KEYS.USER_PROFILE, JSON.stringify(profile));
    } catch {}
  },

  async getXP() {
    try {
      const xp = await AsyncStorage.getItem(KEYS.XP);
      return xp ? parseInt(xp) : 0;
    } catch { return 0; }
  },

  async addXP(amount) {
    try {
      const current = await this.getXP();
      const newXP = current + amount;
      await AsyncStorage.setItem(KEYS.XP, newXP.toString());
      return newXP;
    } catch { return 0; }
  },

  async getStreak() {
    try {
      const data = await AsyncStorage.getItem(KEYS.STREAK);
      return data ? parseInt(data) : 0;
    } catch { return 0; }
  },

  async updateStreak() {
    try {
      const lastActive = await AsyncStorage.getItem(KEYS.LAST_ACTIVE);
      const today = new Date().toDateString();
      if (lastActive === today) return await this.getStreak();
      const yesterday = new Date(Date.now() - 86400000).toDateString();
      let streak = await this.getStreak();
      if (lastActive === yesterday) {
        streak += 1;
      } else if (lastActive !== today) {
        streak = 1;
      }
      await AsyncStorage.setItem(KEYS.STREAK, streak.toString());
      await AsyncStorage.setItem(KEYS.LAST_ACTIVE, today);
      if (streak >= 3) await this.awardBadge('streak_3');
      if (streak >= 7) await this.awardBadge('streak_7');
      return streak;
    } catch { return 1; }
  },

  async markLessonComplete(lessonId) {
    try {
      const data = await AsyncStorage.getItem(KEYS.COMPLETED_LESSONS);
      const completed = data ? JSON.parse(data) : [];
      if (!completed.includes(lessonId)) {
        completed.push(lessonId);
        await AsyncStorage.setItem(KEYS.COMPLETED_LESSONS, JSON.stringify(completed));
      }
    } catch {}
  },

  async getCompletedLessons() {
    try {
      const data = await AsyncStorage.getItem(KEYS.COMPLETED_LESSONS);
      return data ? JSON.parse(data) : [];
    } catch { return []; }
  },

  // Generic "seen set" used for alphabet / vocabulary progress tracking.
  async addToSet(key, value) {
    try {
      const data = await AsyncStorage.getItem(key);
      const set = data ? JSON.parse(data) : [];
      if (!set.includes(value)) {
        set.push(value);
        await AsyncStorage.setItem(key, JSON.stringify(set));
      }
      return set;
    } catch { return []; }
  },

  async getBadges() {
    try {
      const data = await AsyncStorage.getItem(KEYS.BADGES);
      return data ? JSON.parse(data) : [];
    } catch { return []; }
  },

  async awardBadge(badgeId) {
    try {
      const badges = await this.getBadges();
      if (!badges.includes(badgeId)) {
        badges.push(badgeId);
        await AsyncStorage.setItem(KEYS.BADGES, JSON.stringify(badges));
        // Badge cards advertise "+N XP" — actually credit it.
        if (BADGE_XP[badgeId]) await this.addXP(BADGE_XP[badgeId]);
        return true;
      }
      return false;
    } catch { return false; }
  },

  async getSettings() {
    try {
      const data = await AsyncStorage.getItem(KEYS.SETTINGS);
      return data ? JSON.parse(data) : { sound: true, vibration: true, language: 'both' };
    } catch { return { sound: true, vibration: true, language: 'both' }; }
  },

  async saveSettings(settings) {
    try {
      await AsyncStorage.setItem(KEYS.SETTINGS, JSON.stringify(settings));
    } catch {}
  },

  async clearAll() {
    try {
      await AsyncStorage.clear();
    } catch {}
  },
};
