// src/screens/FlashcardScreen.js
import React, { useState, useRef, useEffect } from 'react';
import {
  View, Text, StyleSheet, TouchableOpacity, Animated,
  Dimensions, PanResponder, Alert,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { speakEnglish, speakArabic, speakRhyme, stopSpeech } from '../utils/tts';
import { COLORS, SHADOWS } from '../utils/theme';
import { VOCAB_CATEGORIES } from '../data/curriculum';
import { Storage } from '../utils/storage';

const { width, height } = Dimensions.get('window');

export default function FlashcardScreen({ route, navigation }) {
  const { category, language } = route.params || { category: 'animals', language: 'english' };
  const isArabic = language === 'arabic';

  const cats = isArabic ? VOCAB_CATEGORIES.arabic : VOCAB_CATEGORIES.english;
  const cat = cats.find(c => c.id === category) || cats[0];
  const words = cat?.words || [];

  const [index, setIndex] = useState(0);
  const [flipped, setFlipped] = useState(false);
  const [known, setKnown] = useState(0);
  const [unknown, setUnknown] = useState(0);
  const [done, setDone] = useState(false);

  const flipAnim = useRef(new Animated.Value(0)).current;
  const swipeX = useRef(new Animated.Value(0)).current;
  const swipeY = useRef(new Animated.Value(0)).current;

  const current = words[index];

  const speak = (item) => {
    if (isArabic) speakArabic(item.word, 0.7);
    else speakEnglish(item.word, 0.7);
  };

  useEffect(() => {
    if (current) speak(current);
  }, [index]);

  const flipCard = () => {
    const toValue = flipped ? 0 : 1;
    Animated.spring(flipAnim, { toValue, useNativeDriver: true, tension: 100, friction: 8 }).start();
    setFlipped(!flipped);
      };

  const swipeTo = (direction) => {
    const toX = direction === 'right' ? width : -width;
        if (direction === 'right') setKnown(k => k + 1);
    else setUnknown(u => u + 1);

    Animated.timing(swipeX, { toValue: toX, duration: 250, useNativeDriver: true }).start(() => {
      swipeX.setValue(0);
      swipeY.setValue(0);
      setFlipped(false);
      flipAnim.setValue(0);
      if (index + 1 >= words.length) {
        setDone(true);
      } else {
        setIndex(i => i + 1);
      }
    });
  };

  const panResponder = PanResponder.create({
    onMoveShouldSetPanResponder: (_, g) => Math.abs(g.dx) > 10,
    onPanResponderMove: (_, g) => {
      swipeX.setValue(g.dx);
      swipeY.setValue(g.dy * 0.3);
    },
    onPanResponderRelease: (_, g) => {
      if (g.dx > 80) swipeTo('right');
      else if (g.dx < -80) swipeTo('left');
      else {
        Animated.spring(swipeX, { toValue: 0, useNativeDriver: true }).start();
        Animated.spring(swipeY, { toValue: 0, useNativeDriver: true }).start();
      }
    },
  });

  const frontInterpolate = flipAnim.interpolate({ inputRange: [0, 1], outputRange: ['0deg', '180deg'] });
  const backInterpolate = flipAnim.interpolate({ inputRange: [0, 1], outputRange: ['180deg', '360deg'] });
  const rotate = swipeX.interpolate({ inputRange: [-width, 0, width], outputRange: ['-15deg', '0deg', '15deg'] });

  const knowOpacity = swipeX.interpolate({ inputRange: [0, 60], outputRange: [0, 1] });
  const dontOpacity = swipeX.interpolate({ inputRange: [-60, 0], outputRange: [1, 0] });

  if (done) {
    const total = known + unknown;
    const pct = Math.round((known / total) * 100);
    return (
      <View style={styles.doneContainer}>
        <LinearGradient colors={['#6C3FF5', '#FF6B9D']} style={styles.doneBg}>
          <Text style={styles.doneEmoji}>🎉</Text>
          <Text style={styles.doneTitle}>Great Job!</Text>
          <Text style={styles.doneSub}>You finished the flashcards!</Text>
          <View style={styles.doneStats}>
            <View style={styles.statCard}>
              <Text style={styles.statNum}>{known}</Text>
              <Text style={styles.statLabel}>✅ I Know</Text>
            </View>
            <View style={styles.statCard}>
              <Text style={styles.statNum}>{pct}%</Text>
              <Text style={styles.statLabel}>🎯 Score</Text>
            </View>
            <View style={styles.statCard}>
              <Text style={styles.statNum}>{unknown}</Text>
              <Text style={styles.statLabel}>🔄 Review</Text>
            </View>
          </View>
          <TouchableOpacity style={styles.doneBtn} onPress={() => { setIndex(0); setKnown(0); setUnknown(0); setDone(false); }}>
            <Text style={styles.doneBtnText}>🔁 Play Again</Text>
          </TouchableOpacity>
          <TouchableOpacity style={[styles.doneBtn, { backgroundColor: 'rgba(255,255,255,0.2)', marginTop: 10 }]} onPress={() => navigation.goBack()}>
            <Text style={styles.doneBtnText}>⬅️ Back</Text>
          </TouchableOpacity>
        </LinearGradient>
      </View>
    );
  }

  if (!current) return null;

  return (
    <View style={styles.container}>
      <LinearGradient colors={isArabic ? ['#E65100', '#BF360C'] : ['#7B1FA2', '#9C27B0']} style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()} style={styles.backBtn}>
          <Ionicons name="arrow-back" size={24} color="#fff" />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>
          {isArabic ? `${cat?.title} Flashcards` : `${cat?.title} Flashcards`}
        </Text>
        <Text style={styles.progress}>{index + 1} / {words.length}</Text>
      </LinearGradient>

      {/* Progress bar */}
      <View style={styles.progressBar}>
        <View style={[styles.progressFill, { width: `${((index) / words.length) * 100}%`, backgroundColor: cat?.color }]} />
      </View>

      {/* Card */}
      <View style={styles.cardArea}>
        {/* Know / Don't Know indicators */}
        <Animated.View style={[styles.indicator, styles.knowIndicator, { opacity: knowOpacity }]}>
          <Text style={styles.indicatorText}>✅ Know!</Text>
        </Animated.View>
        <Animated.View style={[styles.indicator, styles.dontIndicator, { opacity: dontOpacity }]}>
          <Text style={styles.indicatorText}>🔄 Review</Text>
        </Animated.View>

        <Animated.View
          {...panResponder.panHandlers}
          style={[styles.cardWrapper, { transform: [{ translateX: swipeX }, { translateY: swipeY }, { rotate }] }]}
        >
          {/* Front of card */}
          <Animated.View style={[styles.card, { backfaceVisibility: 'hidden', transform: [{ rotateY: frontInterpolate }], backgroundColor: cat?.color }]}>
            <TouchableOpacity onPress={flipCard} style={styles.cardInner} activeOpacity={1}>
              <Text style={styles.cardEmoji}>{current.emoji}</Text>
              <Text style={isArabic ? styles.cardArabicWord : styles.cardWord}>
                {isArabic ? current.word : current.word}
              </Text>
              {isArabic && <Text style={styles.cardTranslit}>{current.transliteration}</Text>}
              <Text style={styles.flipHint}>Tap to flip 👆</Text>
            </TouchableOpacity>
          </Animated.View>

          {/* Back of card */}
          <Animated.View style={[styles.card, styles.cardBack, { backfaceVisibility: 'hidden', transform: [{ rotateY: backInterpolate }] }]}>
            <TouchableOpacity onPress={flipCard} style={styles.cardInner} activeOpacity={1}>
              <Text style={styles.cardEmoji}>{current.emoji}</Text>
              <Text style={styles.cardBackWord}>
                {isArabic ? current.english : current.word}
              </Text>
              {!isArabic && (
                <>
                  <Text style={styles.cardIPA}>/{current.pronunciation}/</Text>
                  <Text style={styles.cardArabicSmall}>{current.arabic}</Text>
                </>
              )}
              <View style={styles.sentenceBox}>
                <Text style={styles.sentenceText}>
                  {isArabic ? current.sentence : current.sentence}
                </Text>
              </View>
              <Text style={styles.funFact}>💡 {current.fun}</Text>
            </TouchableOpacity>
          </Animated.View>
        </Animated.View>
      </View>

      {/* Action buttons */}
      <View style={styles.actions}>
        <TouchableOpacity style={[styles.actionBtn, { backgroundColor: COLORS.error }]} onPress={() => swipeTo('left')}>
          <Ionicons name="refresh" size={24} color="#fff" />
          <Text style={styles.actionLabel}>Review</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.speakBtn} onPress={() => speak(current)}>
          <Ionicons name="volume-high" size={28} color={cat?.color} />
        </TouchableOpacity>
        <TouchableOpacity style={[styles.actionBtn, { backgroundColor: COLORS.success }]} onPress={() => swipeTo('right')}>
          <Ionicons name="checkmark" size={24} color="#fff" />
          <Text style={styles.actionLabel}>I Know!</Text>
        </TouchableOpacity>
      </View>
      <Text style={styles.swipeHint}>← Swipe left to review · Swipe right if you know it →</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: COLORS.bg },
  header: { paddingTop: 55, paddingBottom: 16, paddingHorizontal: 16 },
  backBtn: { marginBottom: 8 },
  headerTitle: { color: '#fff', fontSize: 18, fontWeight: '800' },
  progress: { color: 'rgba(255,255,255,0.8)', fontSize: 13, marginTop: 4 },
  progressBar: { height: 4, backgroundColor: '#E0E0E0' },
  progressFill: { height: '100%', borderRadius: 2 },
  cardArea: { flex: 1, alignItems: 'center', justifyContent: 'center', padding: 20 },
  indicator: { position: 'absolute', top: 40, zIndex: 10, paddingHorizontal: 20, paddingVertical: 10, borderRadius: 16, borderWidth: 3 },
  knowIndicator: { right: 20, borderColor: COLORS.success, backgroundColor: COLORS.success + '20' },
  dontIndicator: { left: 20, borderColor: COLORS.error, backgroundColor: COLORS.error + '20' },
  indicatorText: { fontWeight: '900', fontSize: 16, color: COLORS.text },
  cardWrapper: { width: width - 48, height: height * 0.45 },
  card: { position: 'absolute', width: '100%', height: '100%', borderRadius: 24, ...SHADOWS.medium },
  cardBack: { backgroundColor: '#fff' },
  cardInner: { flex: 1, alignItems: 'center', justifyContent: 'center', padding: 24 },
  cardEmoji: { fontSize: 72, marginBottom: 12 },
  cardWord: { fontSize: 28, fontWeight: '900', color: '#fff' },
  cardArabicWord: { fontSize: 36, fontWeight: '900', color: '#fff', textAlign: 'center', writingDirection: 'rtl' },
  cardTranslit: { fontSize: 14, color: 'rgba(255,255,255,0.85)', marginTop: 4, fontStyle: 'italic' },
  flipHint: { fontSize: 12, color: 'rgba(255,255,255,0.7)', marginTop: 20, position: 'absolute', bottom: 20 },
  cardBackWord: { fontSize: 26, fontWeight: '900', color: COLORS.text },
  cardIPA: { fontSize: 14, color: COLORS.muted, fontStyle: 'italic', marginTop: 4 },
  cardArabicSmall: { fontSize: 18, color: COLORS.arabic, marginTop: 4 },
  sentenceBox: { backgroundColor: '#F0F4FF', borderRadius: 12, padding: 12, marginTop: 12, width: '100%' },
  sentenceText: { fontSize: 14, color: COLORS.text, textAlign: 'center', fontStyle: 'italic' },
  funFact: { fontSize: 12, color: COLORS.muted, marginTop: 8, textAlign: 'center' },
  actions: { flexDirection: 'row', justifyContent: 'space-around', alignItems: 'center', paddingHorizontal: 24, paddingBottom: 16 },
  actionBtn: { flexDirection: 'row', alignItems: 'center', borderRadius: 20, paddingVertical: 12, paddingHorizontal: 20, gap: 6, ...SHADOWS.small },
  actionLabel: { color: '#fff', fontWeight: '800', fontSize: 15 },
  speakBtn: { width: 56, height: 56, borderRadius: 28, backgroundColor: '#fff', alignItems: 'center', justifyContent: 'center', ...SHADOWS.medium },
  swipeHint: { textAlign: 'center', color: COLORS.muted, fontSize: 11, paddingBottom: 20 },
  doneContainer: { flex: 1 },
  doneBg: { flex: 1, alignItems: 'center', justifyContent: 'center', padding: 32 },
  doneEmoji: { fontSize: 80, marginBottom: 12 },
  doneTitle: { color: '#fff', fontSize: 32, fontWeight: '900' },
  doneSub: { color: 'rgba(255,255,255,0.85)', fontSize: 16, marginTop: 4 },
  doneStats: { flexDirection: 'row', gap: 16, marginTop: 32 },
  statCard: { backgroundColor: 'rgba(255,255,255,0.2)', borderRadius: 20, padding: 20, alignItems: 'center', flex: 1 },
  statNum: { fontSize: 32, fontWeight: '900', color: '#fff' },
  statLabel: { fontSize: 12, color: 'rgba(255,255,255,0.85)', marginTop: 4, textAlign: 'center' },
  doneBtn: { backgroundColor: 'rgba(255,255,255,0.95)', borderRadius: 20, paddingVertical: 14, paddingHorizontal: 32, marginTop: 24 },
  doneBtnText: { fontWeight: '900', fontSize: 16, color: COLORS.primary },
});
