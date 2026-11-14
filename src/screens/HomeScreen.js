// src/screens/HomeScreen.js
import React, { useState, useEffect, useRef, useCallback } from 'react';
import {
  View, Text, StyleSheet, ScrollView, TouchableOpacity,
  Animated, StatusBar, useWindowDimensions,
} from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { COLORS, SHADOWS } from '../utils/theme';
import { Storage } from '../utils/storage';
import { AGE_GROUPS } from '../data/curriculum';
import { getTodayActivities } from '../utils/progress';

const DAILY_TIPS = [
  "🌟 Practice 5 minutes every day!",
  "🎵 Sing the alphabet song to remember letters!",
  "📖 Read one story today!",
  "🎮 Play a matching game to learn new words!",
  "🌙 Practice Arabic greetings with family!",
  "👏 Great job learning today!",
];

export default function HomeScreen({ navigation }) {
  const [profile, setProfile] = useState(null);
  const [xp, setXP] = useState(0);
  const [streak, setStreak] = useState(0);
  const [doneToday, setDoneToday] = useState([]);
  const { width } = useWindowDimensions();
  const cols = width >= 900 ? 6 : width >= 600 ? 4 : 3;
  const quickWidth = (width - 32 - 10 * (cols - 1)) / cols;
  const [tip] = useState(DAILY_TIPS[Math.floor(Math.random() * DAILY_TIPS.length)]);
  const fadeAnim = useRef(new Animated.Value(0)).current;
  const slideAnim = useRef(new Animated.Value(30)).current;

  // Reload whenever the tab gains focus so XP/streak/name are never stale.
  useFocusEffect(useCallback(() => { loadData(); }, []));

  useEffect(() => {
    Animated.parallel([
      Animated.timing(fadeAnim, { toValue: 1, duration: 600, useNativeDriver: true }),
      Animated.timing(slideAnim, { toValue: 0, duration: 500, useNativeDriver: true }),
    ]).start();
  }, []);

  const loadData = async () => {
    const p = await Storage.getProfile();
    const x = await Storage.getXP();
    const s = await Storage.updateStreak();
    const d = await getTodayActivities();
    setDoneToday(d);
    setProfile(p);
    setXP(x);
    setStreak(s);
  };

  const level = Math.floor(xp / 100) + 1;
  const xpToNext = 100 - (xp % 100);
  const progressPct = ((xp % 100) / 100);

  const QUICK_ACTIONS = [
    { label: 'English\nAlphabet', emoji: '🔤', color: '#1565C0', screen: 'English', subScreen: 'Alphabet', params: { language: 'english' } },
    { label: 'Arabic\nAlphabet', emoji: '🌙', color: '#E65100', screen: 'Arabic', subScreen: 'Alphabet', params: { language: 'arabic' } },
    { label: 'Word\nFlashcards', emoji: '🃏', color: '#7B1FA2', screen: 'English', subScreen: 'Flashcard', params: { category: 'animals', language: 'english' } },
    { label: 'Fun\nQuiz', emoji: '❓', color: '#1B5E20', screen: 'English', subScreen: 'Quiz', params: { category: 'english_colors', language: 'english' } },
    { label: 'Read\nStories', emoji: '📖', color: '#880E4F', screen: 'English', subScreen: 'Story', params: { storyId: 's1' } },
    { label: 'Match\nGame', emoji: '🧩', color: '#BF360C', screen: 'Games', subScreen: null, params: {} },
  ];

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Header */}
        <LinearGradient colors={['#6C3FF5', '#FF6B9D']} style={styles.header}>
          <Animated.View style={{ opacity: fadeAnim, transform: [{ translateY: slideAnim }] }}>
            <View style={styles.headerTop}>
              <View style={{ flex: 1, marginRight: 12 }}>
                <Text style={styles.greeting} numberOfLines={1}>Hello, {profile?.name || 'Learner'} 👋</Text>
                <Text style={styles.subGreeting}>Ready to learn today?</Text>
              </View>
              <TouchableOpacity
                style={styles.streakBadge}
                onPress={() => navigation.navigate('Profile')}
                accessibilityRole="button"
                accessibilityLabel={`${streak} day streak. Open profile`}
              >
                <Text style={styles.streakText}>🔥 {streak}</Text>
                <Text style={styles.streakLabel}>streak</Text>
              </TouchableOpacity>
            </View>

            {/* XP Bar */}
            <View style={styles.xpContainer}>
              <View style={styles.xpRow}>
                <Text style={styles.levelText}>⭐ Level {level}</Text>
                <Text style={styles.xpText}>{xp} XP · {xpToNext} to next</Text>
              </View>
              <View style={styles.xpBar}>
                <Animated.View style={[styles.xpFill, { width: `${progressPct * 100}%` }]} />
              </View>
            </View>
          </Animated.View>
        </LinearGradient>

        {/* Daily Tip */}
        <View style={styles.tipCard}>
          <Text style={styles.tipText}>{tip}</Text>
        </View>

        {/* Quick Actions */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>⚡ Quick Start</Text>
          <View style={styles.quickGrid}>
            {QUICK_ACTIONS.map((action, i) => (
              <TouchableOpacity
                key={i}
                style={[styles.quickCard, { backgroundColor: action.color, width: quickWidth }]}
                accessibilityRole="button"
                accessibilityLabel={action.label.replace('\n', ' ')}
                onPress={() => {
                  if (action.subScreen) {
                    navigation.navigate(action.screen, {
                      screen: action.subScreen,
                      params: action.params,
                    });
                  } else {
                    navigation.navigate(action.screen);
                  }
                }}
                activeOpacity={0.85}
              >
                <Text style={styles.quickEmoji}>{action.emoji}</Text>
                <Text style={styles.quickLabel}>{action.label}</Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>

        {/* Age Groups */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>👶 Learning Path by Age</Text>
          <ScrollView horizontal showsHorizontalScrollIndicator={false}>
            {AGE_GROUPS.map((ag, i) => (
              <TouchableOpacity
                key={i}
                style={[styles.ageCard, { backgroundColor: ag.color }]}
                onPress={() => navigation.navigate('AgeSelect', { ageGroup: ag })}
              >
                <Text style={styles.ageEmoji}>{ag.emoji}</Text>
                <Text style={styles.ageRange}>Age {ag.range}</Text>
                <Text style={styles.ageLabel}>{ag.label}</Text>
                <Text style={styles.ageMins}>⏱ {ag.dailyMins} min/day</Text>
              </TouchableOpacity>
            ))}
          </ScrollView>
        </View>

        {/* Today's Plan */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>📅 Today's Learning Plan</Text>
          {[
            { icon: '🔤', title: 'English Alphabet', sub: 'Learn 3 new letters', activity: 'alphabet_en', tab: 'English', screen: 'Alphabet', params: { language: 'english' } },
            { icon: '🐾', title: 'Animal Vocabulary', sub: '10 words to practice', activity: 'vocab_animals', tab: 'English', screen: 'Vocab', params: { category: 'animals', language: 'english' } },
            { icon: '🌙', title: 'Arabic Letters', sub: 'Ba, Ta, Tha today', activity: 'alphabet_ar', tab: 'Arabic', screen: 'Alphabet', params: { language: 'arabic' } },
            { icon: '🎮', title: 'Quiz Time', sub: '5 questions', activity: 'quiz', tab: 'English', screen: 'Quiz', params: { category: 'english_animals', language: 'english' } },
          ].map((it) => ({ ...it, done: doneToday.includes(it.activity) })).map((item, i) => (
            <TouchableOpacity
              key={i}
              style={[styles.planItem, item.done && styles.planDone]}
              onPress={() => navigation.navigate(item.tab, { screen: item.screen, params: item.params })}
            >
              <Text style={styles.planIcon}>{item.icon}</Text>
              <View style={styles.planInfo}>
                <Text style={[styles.planTitle, item.done && styles.planTitleDone]}>{item.title}</Text>
                <Text style={styles.planSub}>{item.sub}</Text>
              </View>
              <Ionicons
                name={item.done ? 'checkmark-circle' : 'chevron-forward'}
                size={22}
                color={item.done ? COLORS.success : COLORS.muted}
              />
            </TouchableOpacity>
          ))}
        </View>

        {/* Motivational */}
        <LinearGradient colors={['#FFD93D', '#FF6B35']} style={styles.motivCard}>
          <Text style={styles.motivEmoji}>🏆</Text>
          <Text style={styles.motivTitle}>You're doing great!</Text>
          <Text style={styles.motivSub}>Keep learning every day to earn amazing badges!</Text>
        </LinearGradient>

        <View style={{ height: 100 }} />
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: COLORS.bg },
  header: { paddingTop: 50, paddingBottom: 28, paddingHorizontal: 20 },
  headerTop: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 },
  greeting: { color: '#fff', fontSize: 22, fontWeight: '800' },
  subGreeting: { color: 'rgba(255,255,255,0.8)', fontSize: 14, marginTop: 2 },
  streakBadge: { backgroundColor: 'rgba(255,255,255,0.25)', borderRadius: 16, padding: 10, alignItems: 'center' },
  streakText: { color: '#fff', fontSize: 18, fontWeight: '900' },
  streakLabel: { color: 'rgba(255,255,255,0.8)', fontSize: 10 },
  xpContainer: { marginTop: 4 },
  xpRow: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 6 },
  levelText: { color: '#fff', fontWeight: '700', fontSize: 13 },
  xpText: { color: 'rgba(255,255,255,0.8)', fontSize: 12 },
  xpBar: { height: 8, backgroundColor: 'rgba(255,255,255,0.3)', borderRadius: 4, overflow: 'hidden' },
  xpFill: { height: '100%', backgroundColor: COLORS.accent, borderRadius: 4 },
  tipCard: { margin: 16, backgroundColor: '#FFF9C4', borderRadius: 16, padding: 14, borderLeftWidth: 4, borderLeftColor: '#FFD600' },
  tipText: { fontSize: 14, color: '#5D4037', fontWeight: '600' },
  section: { paddingHorizontal: 16, marginBottom: 8 },
  sectionTitle: { fontSize: 17, fontWeight: '800', color: COLORS.text, marginBottom: 12 },
  quickGrid: { flexDirection: 'row', flexWrap: 'wrap', gap: 10 },
  quickCard: { borderRadius: 16, padding: 14, alignItems: 'center', ...SHADOWS.small },
  quickEmoji: { fontSize: 28, marginBottom: 6 },
  quickLabel: { color: '#fff', fontSize: 11, fontWeight: '700', textAlign: 'center' },
  ageCard: { borderRadius: 20, padding: 16, marginRight: 12, alignItems: 'center', width: 120, ...SHADOWS.small },
  ageEmoji: { fontSize: 32, marginBottom: 6 },
  ageRange: { color: '#fff', fontWeight: '900', fontSize: 16 },
  ageLabel: { color: 'rgba(255,255,255,0.9)', fontSize: 10, textAlign: 'center', marginTop: 2 },
  ageMins: { color: 'rgba(255,255,255,0.8)', fontSize: 10, marginTop: 4 },
  planItem: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#fff', borderRadius: 14, padding: 14, marginBottom: 8, ...SHADOWS.small },
  planDone: { opacity: 0.6 },
  planIcon: { fontSize: 26, marginRight: 12 },
  planInfo: { flex: 1 },
  planTitle: { fontSize: 15, fontWeight: '700', color: COLORS.text },
  planTitleDone: { textDecorationLine: 'line-through', color: COLORS.muted },
  planSub: { fontSize: 12, color: COLORS.muted, marginTop: 2 },
  motivCard: { margin: 16, borderRadius: 20, padding: 20, alignItems: 'center' },
  motivEmoji: { fontSize: 40, marginBottom: 8 },
  motivTitle: { color: '#fff', fontSize: 20, fontWeight: '900' },
  motivSub: { color: 'rgba(255,255,255,0.9)', fontSize: 13, textAlign: 'center', marginTop: 4 },
});
