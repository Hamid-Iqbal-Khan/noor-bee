// src/screens/QuizScreen.js
import React, { useState, useRef, useEffect } from 'react';
import {
  View, Text, StyleSheet, TouchableOpacity, Animated,
  Dimensions, ScrollView,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { speakEnglish, speakArabic, speakRhyme, stopSpeech } from '../utils/tts';
import { COLORS, SHADOWS } from '../utils/theme';
import { QUIZ_BANK } from '../data/curriculum';
import { Storage } from '../utils/storage';
import { markActivity } from '../utils/progress';

const { width } = Dimensions.get('window');

export default function QuizScreen({ route, navigation }) {
  const { category, language } = route.params || { category: 'english_animals', language: 'english' };
  const questions = QUIZ_BANK[category] || QUIZ_BANK['english_animals'];
  const isArabic = language === 'arabic';

  const [qIndex, setQIndex] = useState(0);
  const [selected, setSelected] = useState(null);
  const [score, setScore] = useState(0);
  const [done, setDone] = useState(false);
  const [answers, setAnswers] = useState([]);

  const bounceAnim = useRef(new Animated.Value(1)).current;
  const shakeAnim = useRef(new Animated.Value(0)).current;
  const progressAnim = useRef(new Animated.Value(0)).current;

  const current = questions[qIndex];

  useEffect(() => {
    Animated.timing(progressAnim, {
      toValue: (qIndex / questions.length),
      duration: 300,
      useNativeDriver: false,
    }).start();
  }, [qIndex]);

  const bounce = () => {
    Animated.sequence([
      Animated.spring(bounceAnim, { toValue: 1.1, useNativeDriver: true, tension: 300 }),
      Animated.spring(bounceAnim, { toValue: 1, useNativeDriver: true }),
    ]).start();
  };

  const shake = () => {
    Animated.sequence([
      Animated.timing(shakeAnim, { toValue: 10, duration: 60, useNativeDriver: true }),
      Animated.timing(shakeAnim, { toValue: -10, duration: 60, useNativeDriver: true }),
      Animated.timing(shakeAnim, { toValue: 10, duration: 60, useNativeDriver: true }),
      Animated.timing(shakeAnim, { toValue: 0, duration: 60, useNativeDriver: true }),
    ]).start();
  };

  const answer = (idx) => {
    if (selected !== null) return;
    setSelected(idx);
    const correct = idx === current.answer;

    if (correct) {
            bounce();
      setScore(s => s + 10);
    } else {
            shake();
    }

    setAnswers(a => [...a, { question: current.q, correct }]);

    setTimeout(() => {
      setSelected(null);
      if (qIndex + 1 >= questions.length) {
        const finalScore = score + (correct ? 10 : 0);
        setDone(true);
        Storage.addXP(finalScore);
        markActivity('quiz');
        if (finalScore === questions.length * 10) Storage.awardBadge('quiz_perfect');
      } else {
        setQIndex(i => i + 1);
      }
    }, 1400);
  };

  const getOptionStyle = (idx) => {
    if (selected === null) return styles.option;
    if (idx === current.answer) return [styles.option, styles.optionCorrect];
    if (idx === selected && idx !== current.answer) return [styles.option, styles.optionWrong];
    return [styles.option, styles.optionDimmed];
  };

  if (done) {
    const pct = Math.round((score / (questions.length * 10)) * 100);
    const stars = pct >= 90 ? 3 : pct >= 60 ? 2 : 1;
    return (
      <View style={styles.doneContainer}>
        <LinearGradient colors={['#6C3FF5', '#FF6B9D']} style={{ flex: 1, alignItems: 'center', justifyContent: 'center', padding: 32 }}>
          <Text style={{ fontSize: 80 }}>{'⭐'.repeat(stars)}</Text>
          <Text style={styles.doneTitle}>Quiz Complete!</Text>
          <Text style={styles.doneSub}>
            {pct >= 90 ? '🏆 Amazing! You\'re a star!' : pct >= 60 ? '👏 Good job! Keep practicing!' : '💪 Keep trying, you\'ll get there!'}
          </Text>

          <View style={styles.scoreCircle}>
            <Text style={styles.scoreNum}>{score}</Text>
            <Text style={styles.scoreLabel}>XP Earned</Text>
          </View>

          <View style={styles.doneStats}>
            <View style={styles.statItem}>
              <Text style={styles.statNum}>{answers.filter(a => a.correct).length}</Text>
              <Text style={styles.statLab}>✅ Correct</Text>
            </View>
            <View style={styles.statItem}>
              <Text style={styles.statNum}>{answers.filter(a => !a.correct).length}</Text>
              <Text style={styles.statLab}>❌ Wrong</Text>
            </View>
            <View style={styles.statItem}>
              <Text style={styles.statNum}>{pct}%</Text>
              <Text style={styles.statLab}>🎯 Score</Text>
            </View>
          </View>

          <TouchableOpacity style={styles.doneBtn} onPress={() => { setQIndex(0); setScore(0); setDone(false); setAnswers([]); }}>
            <Text style={styles.doneBtnTxt}>🔁 Try Again</Text>
          </TouchableOpacity>
          <TouchableOpacity style={[styles.doneBtn, { marginTop: 10, backgroundColor: 'rgba(255,255,255,0.2)' }]} onPress={() => navigation.goBack()}>
            <Text style={styles.doneBtnTxt}>⬅️ Back</Text>
          </TouchableOpacity>
        </LinearGradient>
      </View>
    );
  }

  if (!current) return null;

  return (
    <View style={styles.container}>
      <LinearGradient colors={isArabic ? ['#E65100', '#BF360C'] : ['#1B5E20', '#388E3C']} style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()} style={styles.backBtn} accessibilityRole="button" accessibilityLabel="Go back">
          <Ionicons name="arrow-back" size={24} color="#fff" />
        </TouchableOpacity>
        <View style={styles.headerRow}>
          <Text style={styles.headerTitle}>
            {isArabic ? '🌙 اختبار · Quiz' : '❓ Fun Quiz'}
          </Text>
          <View style={styles.scoreBadge}>
            <Text style={styles.scoreText}>⭐ {score}</Text>
          </View>
        </View>
        {/* Progress */}
        <View style={styles.progressBg}>
          <Animated.View style={[styles.progressFill, {
            width: progressAnim.interpolate({ inputRange: [0, 1], outputRange: ['0%', '100%'] })
          }]} />
        </View>
        <Text style={styles.qCounter}>{qIndex + 1} of {questions.length}</Text>
      </LinearGradient>

      <ScrollView contentContainerStyle={styles.content}>
        {/* Question card */}
        <Animated.View style={[styles.qCard, { transform: [{ scale: bounceAnim }, { translateX: shakeAnim }] }]}>
          <Text style={styles.qEmoji}>{current.emoji}</Text>
          <Text style={isArabic ? styles.questionAr : styles.question}>{current.q}</Text>
        </Animated.View>

        {/* Options */}
        <View style={styles.options}>
          {current.options.map((opt, i) => (
            <TouchableOpacity key={i} style={getOptionStyle(i)} onPress={() => answer(i)} activeOpacity={0.8} accessibilityRole="button" accessibilityLabel={`Answer ${['A', 'B', 'C', 'D'][i]}: ${opt}`}>
              <View style={[styles.optionLetter, {
                backgroundColor: selected !== null
                  ? i === current.answer ? COLORS.success : i === selected ? COLORS.error : '#E0E0E0'
                  : COLORS.primary
              }]}>
                <Text style={styles.optionLetterText}>{['A', 'B', 'C', 'D'][i]}</Text>
              </View>
              <Text style={[styles.optionText, isArabic && styles.optionTextAr]}>{opt}</Text>
              {selected !== null && i === current.answer && (
                <Ionicons name="checkmark-circle" size={24} color={COLORS.success} />
              )}
              {selected === i && i !== current.answer && (
                <Ionicons name="close-circle" size={24} color={COLORS.error} />
              )}
            </TouchableOpacity>
          ))}
        </View>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: COLORS.bg },
  header: { paddingTop: 55, paddingBottom: 20, paddingHorizontal: 16 },
  backBtn: { marginBottom: 8 },
  headerRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 },
  headerTitle: { color: '#fff', fontSize: 18, fontWeight: '800' },
  scoreBadge: { backgroundColor: 'rgba(255,255,255,0.25)', borderRadius: 16, paddingHorizontal: 12, paddingVertical: 6 },
  scoreText: { color: '#fff', fontWeight: '800', fontSize: 14 },
  progressBg: { height: 6, backgroundColor: 'rgba(255,255,255,0.3)', borderRadius: 3, overflow: 'hidden' },
  progressFill: { height: '100%', backgroundColor: '#FFD93D', borderRadius: 3 },
  qCounter: { color: 'rgba(255,255,255,0.8)', fontSize: 12, marginTop: 6 },
  content: { padding: 16 },
  qCard: { backgroundColor: '#fff', borderRadius: 24, padding: 28, alignItems: 'center', marginBottom: 20, ...SHADOWS.medium },
  qEmoji: { fontSize: 64, marginBottom: 12 },
  question: { fontSize: 20, fontWeight: '800', color: COLORS.text, textAlign: 'center' },
  questionAr: { fontSize: 22, fontWeight: '900', color: COLORS.arabic, textAlign: 'center' },
  options: { gap: 10 },
  option: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#fff', borderRadius: 16, padding: 16, gap: 12, ...SHADOWS.small, borderWidth: 2, borderColor: 'transparent' },
  optionCorrect: { borderColor: COLORS.success, backgroundColor: '#F1FFF4' },
  optionWrong: { borderColor: COLORS.error, backgroundColor: '#FFF1F1' },
  optionDimmed: { opacity: 0.5 },
  optionLetter: { width: 36, height: 36, borderRadius: 18, alignItems: 'center', justifyContent: 'center' },
  optionLetterText: { color: '#fff', fontWeight: '900', fontSize: 14 },
  optionText: { flex: 1, fontSize: 16, fontWeight: '600', color: COLORS.text },
  optionTextAr: { textAlign: 'right', fontSize: 18, fontWeight: '800', color: COLORS.arabic },
  doneContainer: { flex: 1 },
  doneTitle: { color: '#fff', fontSize: 28, fontWeight: '900', marginTop: 12 },
  doneSub: { color: 'rgba(255,255,255,0.85)', fontSize: 15, textAlign: 'center', marginTop: 8 },
  scoreCircle: { width: 120, height: 120, borderRadius: 60, backgroundColor: 'rgba(255,255,255,0.25)', alignItems: 'center', justifyContent: 'center', marginTop: 24 },
  scoreNum: { color: '#fff', fontSize: 36, fontWeight: '900' },
  scoreLabel: { color: 'rgba(255,255,255,0.8)', fontSize: 12 },
  doneStats: { flexDirection: 'row', gap: 16, marginTop: 24 },
  statItem: { alignItems: 'center' },
  statNum: { color: '#fff', fontSize: 28, fontWeight: '900' },
  statLab: { color: 'rgba(255,255,255,0.8)', fontSize: 12 },
  doneBtn: { backgroundColor: '#fff', borderRadius: 20, paddingVertical: 14, paddingHorizontal: 36, marginTop: 24 },
  doneBtnTxt: { color: COLORS.primary, fontWeight: '900', fontSize: 16 },
});
