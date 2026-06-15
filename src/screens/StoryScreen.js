// src/screens/StoryScreen.js
import React, { useState, useRef } from 'react';
import {
  View, Text, StyleSheet, TouchableOpacity, Animated,
  Dimensions, ScrollView, FlatList,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { speakEnglish, speakArabic, speakRhyme, stopSpeech } from '../utils/tts';
import { COLORS, SHADOWS } from '../utils/theme';
import { STORIES } from '../data/curriculum';

const { width, height } = Dimensions.get('window');
const PAGE_WIDTH = width;

export default function StoryScreen({ route, navigation }) {
  const { storyId } = route.params || { storyId: 's1' };
  const [selectedStory, setSelectedStory] = useState(storyId ? STORIES.find(s => s.id === storyId) : null);
  const [page, setPage] = useState(0);
  const [showQuiz, setShowQuiz] = useState(false);
  const [quizAnswers, setQuizAnswers] = useState([]);
  const [quizDone, setQuizDone] = useState(false);
  const flatRef = useRef(null);
  const fadeAnim = useRef(new Animated.Value(1)).current;

  const BKGS = ['#FFF3E0', '#E8F5E9', '#E3F2FD', '#FCE4EC', '#F3E5F5'];

  const speakPage = (p) => {
    if (!selectedStory) return;
    const text = selectedStory.pages[p]?.text || '';
    if (selectedStory.language === 'arabic') speakArabic(text, 0.7);
    else speakEnglish(text, 0.7);
  };

  const turnPage = (dir) => {
    const newPage = page + dir;
    if (newPage < 0 || newPage >= selectedStory.pages.length) {
      if (dir > 0) setShowQuiz(true);
      return;
    }
    Animated.sequence([
      Animated.timing(fadeAnim, { toValue: 0, duration: 150, useNativeDriver: true }),
      Animated.timing(fadeAnim, { toValue: 1, duration: 300, useNativeDriver: true }),
    ]).start();
    setPage(newPage);
    speakPage(newPage);
      };

  const answerQ = (qIdx, aIdx) => {
    if (quizAnswers[qIdx] !== undefined) return;
    const newAns = [...quizAnswers];
    newAns[qIdx] = aIdx;
    setQuizAnswers(newAns);
    const correct = aIdx === selectedStory.comprehension[qIdx].answer;
    if (newAns.filter(a => a !== undefined).length === selectedStory.comprehension.length) {
      setTimeout(() => setQuizDone(true), 600);
    }
  };

  // Story list view
  if (!selectedStory) {
    return (
      <View style={styles.container}>
        <LinearGradient colors={['#880E4F', '#C2185B']} style={styles.header}>
          <TouchableOpacity onPress={() => navigation.goBack()} style={styles.backBtn}>
            <Ionicons name="arrow-back" size={24} color="#fff" />
          </TouchableOpacity>
          <Text style={styles.headerTitle}>📖 Stories</Text>
          <Text style={styles.headerSub}>Read and learn!</Text>
        </LinearGradient>
        <ScrollView contentContainerStyle={{ padding: 16 }}>
          {STORIES.map((story) => (
            <TouchableOpacity
              key={story.id}
              style={styles.storyCard}
              onPress={() => { setSelectedStory(story); setPage(0); setShowQuiz(false); setQuizAnswers([]); setQuizDone(false); }}
            >
              <Text style={styles.storyEmoji}>{story.emoji}</Text>
              <View style={styles.storyInfo}>
                <Text style={styles.storyTitle}>{story.title}</Text>
                <Text style={styles.storySub}>Level {story.level} · Ages {story.ageGroup}</Text>
                <View style={styles.storyTags}>
                  <View style={[styles.tag, { backgroundColor: story.language === 'arabic' ? '#E65100' : '#1565C0' }]}>
                    <Text style={styles.tagText}>{story.language === 'arabic' ? '🌙 Arabic' : '🔤 English'}</Text>
                  </View>
                  <View style={styles.tag}>
                    <Text style={styles.tagText}>{story.pages.length} pages</Text>
                  </View>
                </View>
              </View>
              <Ionicons name="chevron-forward" size={22} color={COLORS.muted} />
            </TouchableOpacity>
          ))}
        </ScrollView>
      </View>
    );
  }

  // Comprehension quiz
  if (showQuiz) {
    const correctCount = quizAnswers.filter((a, i) => a === selectedStory.comprehension[i]?.answer).length;
    return (
      <View style={styles.container}>
        <LinearGradient colors={['#880E4F', '#C2185B']} style={styles.header}>
          <TouchableOpacity onPress={() => setShowQuiz(false)} style={styles.backBtn}>
            <Ionicons name="arrow-back" size={24} color="#fff" />
          </TouchableOpacity>
          <Text style={styles.headerTitle}>🧠 Story Questions</Text>
        </LinearGradient>
        <ScrollView contentContainerStyle={{ padding: 16 }}>
          {quizDone && (
            <LinearGradient colors={['#6C3FF5', '#FF6B9D']} style={styles.resultCard}>
              <Text style={{ fontSize: 48 }}>{'⭐'.repeat(Math.min(correctCount + 1, 3))}</Text>
              <Text style={styles.resultTitle}>{correctCount === selectedStory.comprehension.length ? '🎉 Perfect!' : '👏 Good try!'}</Text>
              <Text style={styles.resultSub}>{correctCount}/{selectedStory.comprehension.length} correct</Text>
              <TouchableOpacity style={styles.resultBtn} onPress={() => { setSelectedStory(null); setShowQuiz(false); setPage(0); setQuizAnswers([]); setQuizDone(false); }}>
                <Text style={styles.resultBtnTxt}>📚 More Stories</Text>
              </TouchableOpacity>
            </LinearGradient>
          )}
          {selectedStory.comprehension.map((q, qi) => (
            <View key={qi} style={styles.qCard}>
              <Text style={styles.qText}>{q.question || q.questionEn}</Text>
              {q.options.map((opt, oi) => {
                const answered = quizAnswers[qi] !== undefined;
                const isCorrect = oi === q.answer;
                const isSelected = quizAnswers[qi] === oi;
                return (
                  <TouchableOpacity
                    key={oi}
                    style={[
                      styles.qOption,
                      answered && isCorrect && styles.correctOption,
                      answered && isSelected && !isCorrect && styles.wrongOption,
                    ]}
                    onPress={() => answerQ(qi, oi)}
                    disabled={answered}
                  >
                    <Text style={styles.qOptionText}>{opt}</Text>
                    {answered && isCorrect && <Ionicons name="checkmark-circle" size={20} color={COLORS.success} />}
                    {answered && isSelected && !isCorrect && <Ionicons name="close-circle" size={20} color={COLORS.error} />}
                  </TouchableOpacity>
                );
              })}
            </View>
          ))}
        </ScrollView>
      </View>
    );
  }

  // Story reading view
  const currentPage = selectedStory.pages[page];
  const isArabic = selectedStory.language === 'arabic';

  return (
    <View style={styles.container}>
      <LinearGradient colors={['#880E4F', '#C2185B']} style={styles.header}>
        <TouchableOpacity onPress={() => setSelectedStory(null)} style={styles.backBtn}>
          <Ionicons name="arrow-back" size={24} color="#fff" />
        </TouchableOpacity>
        <View style={{ flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' }}>
          <Text style={styles.headerTitle}>{selectedStory.title}</Text>
          <Text style={{ color: 'rgba(255,255,255,0.8)', fontSize: 13 }}>{page + 1}/{selectedStory.pages.length}</Text>
        </View>
        <View style={styles.progressDots}>
          {selectedStory.pages.map((_, i) => (
            <View key={i} style={[styles.dot, i <= page && styles.dotActive]} />
          ))}
        </View>
      </LinearGradient>

      <Animated.View style={[styles.pageContainer, { opacity: fadeAnim, backgroundColor: BKGS[page % BKGS.length] }]}>
        <Text style={styles.pageEmoji}>{currentPage.image}</Text>
        <Text style={isArabic ? styles.pageTextAr : styles.pageText}>{currentPage.text}</Text>
        {isArabic && currentPage.textEn && (
          <Text style={styles.pageTextEn}>{currentPage.textEn}</Text>
        )}
        <TouchableOpacity style={styles.speakPageBtn} onPress={() => speakPage(page)}>
          <Ionicons name="volume-high" size={22} color="#fff" />
          <Text style={styles.speakPageTxt}>Read Aloud</Text>
        </TouchableOpacity>
      </Animated.View>

      {/* Navigation */}
      <View style={styles.pageNav}>
        <TouchableOpacity style={[styles.navBtn, page === 0 && { opacity: 0.4 }]} onPress={() => turnPage(-1)} disabled={page === 0}>
          <Ionicons name="chevron-back" size={28} color={COLORS.primary} />
          <Text style={styles.navLabel}>Back</Text>
        </TouchableOpacity>

        <View style={styles.pageIndicator}>
          <Text style={styles.pageNum}>Page {page + 1}</Text>
        </View>

        <TouchableOpacity style={styles.navBtn} onPress={() => turnPage(1)}>
          <Text style={styles.navLabel}>{page === selectedStory.pages.length - 1 ? 'Quiz' : 'Next'}</Text>
          <Ionicons name={page === selectedStory.pages.length - 1 ? 'help-circle' : 'chevron-forward'} size={28} color={COLORS.primary} />
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: COLORS.bg },
  header: { paddingTop: 55, paddingBottom: 16, paddingHorizontal: 16 },
  backBtn: { marginBottom: 8 },
  headerTitle: { color: '#fff', fontSize: 18, fontWeight: '800' },
  headerSub: { color: 'rgba(255,255,255,0.8)', fontSize: 13, marginTop: 4 },
  progressDots: { flexDirection: 'row', gap: 6, marginTop: 10 },
  dot: { width: 8, height: 8, borderRadius: 4, backgroundColor: 'rgba(255,255,255,0.4)' },
  dotActive: { backgroundColor: '#FFD93D' },
  pageContainer: { flex: 1, alignItems: 'center', justifyContent: 'center', padding: 32 },
  pageEmoji: { fontSize: 100, marginBottom: 24, textAlign: 'center' },
  pageText: { fontSize: 24, fontWeight: '800', color: COLORS.text, textAlign: 'center', lineHeight: 36 },
  pageTextAr: { fontSize: 26, fontWeight: '900', color: COLORS.arabic, textAlign: 'right', writingDirection: 'rtl', lineHeight: 42 },
  pageTextEn: { fontSize: 15, color: COLORS.muted, textAlign: 'center', marginTop: 8, fontStyle: 'italic' },
  speakPageBtn: { flexDirection: 'row', alignItems: 'center', gap: 8, backgroundColor: '#880E4F', borderRadius: 20, paddingVertical: 10, paddingHorizontal: 20, marginTop: 20 },
  speakPageTxt: { color: '#fff', fontWeight: '700', fontSize: 14 },
  pageNav: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', padding: 16, backgroundColor: '#fff', ...SHADOWS.small },
  navBtn: { flexDirection: 'row', alignItems: 'center', gap: 4 },
  navLabel: { fontSize: 14, fontWeight: '700', color: COLORS.primary },
  pageIndicator: { backgroundColor: COLORS.primary + '20', borderRadius: 12, paddingHorizontal: 14, paddingVertical: 6 },
  pageNum: { fontSize: 13, fontWeight: '700', color: COLORS.primary },
  storyCard: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#fff', borderRadius: 18, padding: 16, marginBottom: 12, ...SHADOWS.small },
  storyEmoji: { fontSize: 40, marginRight: 14 },
  storyInfo: { flex: 1 },
  storyTitle: { fontSize: 16, fontWeight: '800', color: COLORS.text },
  storySub: { fontSize: 12, color: COLORS.muted, marginTop: 2 },
  storyTags: { flexDirection: 'row', gap: 6, marginTop: 6 },
  tag: { backgroundColor: COLORS.primary, borderRadius: 10, paddingHorizontal: 8, paddingVertical: 3 },
  tagText: { color: '#fff', fontSize: 11, fontWeight: '700' },
  qCard: { backgroundColor: '#fff', borderRadius: 18, padding: 18, marginBottom: 14, ...SHADOWS.small },
  qText: { fontSize: 16, fontWeight: '800', color: COLORS.text, marginBottom: 12 },
  qOption: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', backgroundColor: '#F5F5F5', borderRadius: 12, padding: 14, marginBottom: 8, borderWidth: 2, borderColor: 'transparent' },
  correctOption: { borderColor: COLORS.success, backgroundColor: '#E8F5E9' },
  wrongOption: { borderColor: COLORS.error, backgroundColor: '#FFEBEE' },
  qOptionText: { fontSize: 15, fontWeight: '600', color: COLORS.text },
  resultCard: { borderRadius: 24, padding: 28, alignItems: 'center', marginBottom: 16 },
  resultTitle: { color: '#fff', fontSize: 24, fontWeight: '900', marginTop: 8 },
  resultSub: { color: 'rgba(255,255,255,0.85)', fontSize: 15, marginTop: 4 },
  resultBtn: { backgroundColor: '#fff', borderRadius: 18, paddingVertical: 12, paddingHorizontal: 28, marginTop: 16 },
  resultBtnTxt: { color: COLORS.primary, fontWeight: '900', fontSize: 15 },
});
