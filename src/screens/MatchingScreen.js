// src/screens/MatchingScreen.js
import React, { useState, useEffect, useRef } from 'react';
import {
  View, Text, StyleSheet, TouchableOpacity, Animated,
  Dimensions, ScrollView,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import * as Haptics from 'expo-haptics';
import { COLORS, SHADOWS } from '../utils/theme';
import { VOCAB_CATEGORIES } from '../data/curriculum';

const { width } = Dimensions.get('window');

const shuffle = (arr) => [...arr].sort(() => Math.random() - 0.5);

export default function MatchingScreen({ route, navigation }) {
  const { language } = route.params || { language: 'english' };
  const isArabic = language === 'arabic';
  const cats = isArabic ? VOCAB_CATEGORIES.arabic : VOCAB_CATEGORIES.english;
  const cat = cats[0];
  const allWords = cat.words.slice(0, 6);

  const [leftItems] = useState(shuffle(allWords.map((w, i) => ({ id: i, emoji: w.emoji, type: 'emoji' }))));
  const [rightItems] = useState(shuffle(allWords.map((w, i) => ({ id: i, text: isArabic ? w.word : w.word, type: 'word' }))));
  const [selectedLeft, setSelectedLeft] = useState(null);
  const [selectedRight, setSelectedRight] = useState(null);
  const [matched, setMatched] = useState([]);
  const [errors, setErrors] = useState(0);
  const [done, setDone] = useState(false);
  const [time, setTime] = useState(0);
  const timerRef = useRef(null);

  const shakeAnim = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    timerRef.current = setInterval(() => setTime(t => t + 1), 1000);
    return () => clearInterval(timerRef.current);
  }, []);

  useEffect(() => {
    if (matched.length === allWords.length) {
      clearInterval(timerRef.current);
      setDone(true);
    }
  }, [matched]);

  useEffect(() => {
    if (selectedLeft !== null && selectedRight !== null) {
      if (selectedLeft === selectedRight) {
        Haptics.notificationAsync(Haptics.NotificationFeedbackType.Success);
        setMatched(m => [...m, selectedLeft]);
        setSelectedLeft(null);
        setSelectedRight(null);
      } else {
        Haptics.notificationAsync(Haptics.NotificationFeedbackType.Error);
        setErrors(e => e + 1);
        Animated.sequence([
          Animated.timing(shakeAnim, { toValue: 10, duration: 50, useNativeDriver: true }),
          Animated.timing(shakeAnim, { toValue: -10, duration: 50, useNativeDriver: true }),
          Animated.timing(shakeAnim, { toValue: 10, duration: 50, useNativeDriver: true }),
          Animated.timing(shakeAnim, { toValue: 0, duration: 50, useNativeDriver: true }),
        ]).start();
        setTimeout(() => { setSelectedLeft(null); setSelectedRight(null); }, 800);
      }
    }
  }, [selectedLeft, selectedRight]);

  const restart = () => {
    setSelectedLeft(null);
    setSelectedRight(null);
    setMatched([]);
    setErrors(0);
    setDone(false);
    setTime(0);
    timerRef.current = setInterval(() => setTime(t => t + 1), 1000);
  };

  const fmt = (s) => `${Math.floor(s / 60).toString().padStart(2, '0')}:${(s % 60).toString().padStart(2, '0')}`;

  if (done) {
    const stars = errors === 0 ? 3 : errors <= 2 ? 2 : 1;
    return (
      <LinearGradient colors={['#6C3FF5', '#FF6B9D']} style={{ flex: 1, alignItems: 'center', justifyContent: 'center', padding: 32 }}>
        <Text style={{ fontSize: 80 }}>{'⭐'.repeat(stars)}</Text>
        <Text style={{ color: '#fff', fontSize: 28, fontWeight: '900', marginTop: 8 }}>All Matched!</Text>
        <Text style={{ color: 'rgba(255,255,255,0.85)', fontSize: 15, marginTop: 4, textAlign: 'center' }}>
          {errors === 0 ? '🏆 Perfect Match! No mistakes!' : `You made ${errors} mistake${errors !== 1 ? 's' : ''}`}
        </Text>
        <View style={{ flexDirection: 'row', gap: 20, marginTop: 28 }}>
          {[
            { num: fmt(time), label: '⏱ Time' },
            { num: `${allWords.length}/${allWords.length}`, label: '✅ Matched' },
            { num: errors, label: '❌ Errors' },
          ].map((s, i) => (
            <View key={i} style={{ backgroundColor: 'rgba(255,255,255,0.2)', borderRadius: 16, padding: 16, alignItems: 'center' }}>
              <Text style={{ color: '#fff', fontSize: 24, fontWeight: '900' }}>{s.num}</Text>
              <Text style={{ color: 'rgba(255,255,255,0.8)', fontSize: 12, marginTop: 4 }}>{s.label}</Text>
            </View>
          ))}
        </View>
        <TouchableOpacity onPress={restart} style={{ backgroundColor: '#fff', borderRadius: 20, paddingVertical: 14, paddingHorizontal: 36, marginTop: 28 }}>
          <Text style={{ color: COLORS.primary, fontWeight: '900', fontSize: 16 }}>🔁 Play Again</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => navigation.goBack()} style={{ backgroundColor: 'rgba(255,255,255,0.2)', borderRadius: 20, paddingVertical: 14, paddingHorizontal: 36, marginTop: 10 }}>
          <Text style={{ color: '#fff', fontWeight: '900', fontSize: 16 }}>⬅️ Back</Text>
        </TouchableOpacity>
      </LinearGradient>
    );
  }

  return (
    <View style={styles.container}>
      <LinearGradient colors={['#BF360C', '#E64A19']} style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()} style={styles.backBtn}>
          <Ionicons name="arrow-back" size={24} color="#fff" />
        </TouchableOpacity>
        <View style={styles.headerRow}>
          <Text style={styles.headerTitle}>🧩 Match Game</Text>
          <View style={{ flexDirection: 'row', gap: 12 }}>
            <View style={styles.badge}><Text style={styles.badgeTxt}>⏱ {fmt(time)}</Text></View>
            <View style={styles.badge}><Text style={styles.badgeTxt}>✅ {matched.length}/{allWords.length}</Text></View>
          </View>
        </View>
        <Text style={styles.headerSub}>Match the emoji to its word!</Text>
      </LinearGradient>

      <Animated.View style={[styles.gameArea, { transform: [{ translateX: shakeAnim }] }]}>
        <View style={styles.column}>
          <Text style={styles.colLabel}>Emoji</Text>
          {leftItems.map((item) => {
            const isMatched = matched.includes(item.id);
            const isSelected = selectedLeft === item.id;
            return (
              <TouchableOpacity
                key={item.id}
                style={[
                  styles.card,
                  isSelected && styles.cardSelected,
                  isMatched && styles.cardMatched,
                ]}
                onPress={() => { if (!isMatched) setSelectedLeft(item.id); }}
                disabled={isMatched}
              >
                <Text style={styles.cardEmoji}>{item.emoji}</Text>
              </TouchableOpacity>
            );
          })}
        </View>

        <View style={styles.divider}>
          {leftItems.map((_, i) => (
            <View key={i} style={styles.line} />
          ))}
        </View>

        <View style={styles.column}>
          <Text style={styles.colLabel}>Word</Text>
          {rightItems.map((item) => {
            const isMatched = matched.includes(item.id);
            const isSelected = selectedRight === item.id;
            return (
              <TouchableOpacity
                key={item.id}
                style={[
                  styles.card,
                  styles.wordCard,
                  isSelected && styles.cardSelected,
                  isMatched && styles.cardMatched,
                ]}
                onPress={() => { if (!isMatched) setSelectedRight(item.id); }}
                disabled={isMatched}
              >
                <Text style={isArabic ? styles.arabicText : styles.wordText} numberOfLines={1}>{item.text}</Text>
              </TouchableOpacity>
            );
          })}
        </View>
      </Animated.View>

      {errors > 0 && (
        <View style={styles.errorBar}>
          <Ionicons name="close-circle" size={16} color={COLORS.error} />
          <Text style={styles.errorTxt}>{errors} wrong match{errors !== 1 ? 'es' : ''} — keep trying!</Text>
        </View>
      )}
    </View>
  );
}

const CARD_HEIGHT = 56;
const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: COLORS.bg },
  header: { paddingTop: 55, paddingBottom: 16, paddingHorizontal: 16 },
  backBtn: { marginBottom: 8 },
  headerRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  headerTitle: { color: '#fff', fontSize: 18, fontWeight: '800' },
  headerSub: { color: 'rgba(255,255,255,0.8)', fontSize: 13, marginTop: 4 },
  badge: { backgroundColor: 'rgba(255,255,255,0.2)', borderRadius: 12, paddingHorizontal: 10, paddingVertical: 4 },
  badgeTxt: { color: '#fff', fontWeight: '700', fontSize: 13 },
  gameArea: { flex: 1, flexDirection: 'row', padding: 16, gap: 8 },
  column: { flex: 1 },
  colLabel: { fontSize: 13, fontWeight: '800', color: COLORS.muted, textAlign: 'center', marginBottom: 10 },
  divider: { width: 2, marginTop: 34, gap: 8 },
  line: { height: CARD_HEIGHT, backgroundColor: '#E0E0E0', width: 2, borderRadius: 1 },
  card: { height: CARD_HEIGHT, backgroundColor: '#fff', borderRadius: 14, alignItems: 'center', justifyContent: 'center', marginBottom: 8, borderWidth: 2, borderColor: 'transparent', ...SHADOWS.small },
  wordCard: { paddingHorizontal: 8 },
  cardSelected: { borderColor: COLORS.primary, backgroundColor: COLORS.primary + '15', transform: [{ scale: 1.04 }] },
  cardMatched: { borderColor: COLORS.success, backgroundColor: '#E8F5E9', opacity: 0.85 },
  cardEmoji: { fontSize: 28 },
  wordText: { fontSize: 14, fontWeight: '800', color: COLORS.text, textAlign: 'center' },
  arabicText: { fontSize: 18, fontWeight: '900', color: COLORS.arabic, textAlign: 'center' },
  errorBar: { flexDirection: 'row', alignItems: 'center', gap: 6, padding: 12, backgroundColor: '#FFEBEE', justifyContent: 'center' },
  errorTxt: { color: COLORS.error, fontWeight: '600', fontSize: 13 },
});
