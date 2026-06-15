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
};

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
