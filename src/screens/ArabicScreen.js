// src/screens/ArabicScreen.js
import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, Dimensions } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { COLORS, SHADOWS } from '../utils/theme';
import { VOCAB_CATEGORIES } from '../data/curriculum';

const { width } = Dimensions.get('window');

const MODULES = [
  { title: 'Arabic Alphabet', subtitle: '28 letters · Islamic words', emoji: '✦', color: '#1B3A6B', screen: 'Alphabet', params: { language: 'arabic' } },
  { title: 'Islamic Words', subtitle: 'Daily Islamic phrases', emoji: '🌙', color: '#1B5E20', screen: 'Vocab', params: { category: 'ar_islamic', language: 'arabic' } },
  { title: 'Vocabulary', subtitle: '5 categories · 40+ words', emoji: '📚', color: '#4E342E', screen: 'Vocab', params: { language: 'arabic' } },
  { title: 'Flashcards', subtitle: 'Swipe to learn', emoji: '🃏', color: '#4A148C', screen: 'Flashcard', params: { category: 'ar_animals', language: 'arabic' } },
  { title: 'Arabic Quiz', subtitle: 'Test your knowledge', emoji: '❓', color: '#880E4F', screen: 'Quiz', params: { category: 'arabic_animals', language: 'arabic' } },
  { title: 'Phrases', subtitle: 'Greetings & daily use', emoji: '💬', color: '#311B92', screen: 'Phrases', params: { language: 'arabic' } },
  { title: 'Match Game', subtitle: 'Match Arabic words', emoji: '🧩', color: '#BF360C', screen: 'Matching', params: { language: 'arabic' } },
  { title: 'Arabic Story', subtitle: 'قصة القطة الصغيرة', emoji: '📖', color: '#004D40', screen: 'Story', params: { storyId: 's3' } },
];

export default function ArabicScreen({ navigation }) {
  const categories = VOCAB_CATEGORIES.arabic;
  return (
    <View style={styles.container}>
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Header */}
        <LinearGradient colors={['#0F1F3D', '#1A3A6B', '#1B3A6B']} style={styles.header}>
          <Text style={styles.headerArabicTitle}>✦ Arabic Alphabet ✦</Text>
          <Text style={styles.headerSubArabic}>الحروف العربية مع الكلمات الإسلامية</Text>
          <Text style={styles.headerSub}>Arabic letters with Islamic words</Text>
        </LinearGradient>

        {/* Islamic greeting */}
        <LinearGradient colors={['#1B5E20', '#2E7D32']} style={styles.islamBanner}>
          <Text style={styles.islamText}>بِسمِ اللّٰه الرَّحمٰنِ الرَّحيم</Text>
          <Text style={styles.islamTextEn}>In the name of Allah, the Most Gracious, the Most Merciful</Text>
        </LinearGradient>

        {/* Modules */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>📚 وحدات التعلم · Learning Modules</Text>
          <View style={styles.grid}>
            {MODULES.map((mod, i) => (
              <TouchableOpacity
                key={i}
                style={[styles.moduleCard, { backgroundColor: mod.color }]}
                onPress={() => navigation.navigate(mod.screen, mod.params)}
                activeOpacity={0.85}
              >
                <Text style={styles.moduleEmoji}>{mod.emoji}</Text>
                <Text style={styles.moduleTitle}>{mod.title}</Text>
                <Text style={styles.moduleSub}>{mod.subtitle}</Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>

        {/* Categories */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>🗂️ فئات المفردات · Vocabulary</Text>
          {categories.map((cat, i) => (
            <TouchableOpacity
              key={i}
              style={styles.catRow}
              onPress={() => navigation.navigate('Vocab', { category: cat.id, language: 'arabic' })}
            >
              <View style={[styles.catIcon, { backgroundColor: cat.color + '20' }]}>
                <Text style={styles.catEmoji}>{cat.emoji}</Text>
              </View>
              <View style={styles.catInfo}>
                <Text style={styles.catTitleAr}>{cat.title}</Text>
                <Text style={styles.catTitleEn}>{cat.titleEn}</Text>
                <Text style={styles.catSub}>{cat.words.length} كلمة · words</Text>
              </View>
              <View style={styles.catRight}>
                {[0, 1, 2].map(j => (
                  <Text key={j} style={styles.catPreview}>{cat.words[j]?.emoji || ''}</Text>
                ))}
                <Ionicons name="chevron-forward" size={18} color={COLORS.muted} />
              </View>
            </TouchableOpacity>
          ))}
        </View>
        <View style={{ height: 100 }} />
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: COLORS.bg },
  header: { paddingTop: 55, paddingBottom: 28, alignItems: 'center', paddingHorizontal: 16 },
  headerArabicTitle: { color: '#f4c430', fontSize: 22, fontWeight: '900', letterSpacing: 2, textTransform: 'uppercase' },
  headerSubArabic: { color: '#c9a84c', fontSize: 16, fontWeight: '700', marginTop: 6, textAlign: 'center', writingDirection: 'rtl' },
  headerSub: { color: 'rgba(255,255,255,0.7)', fontSize: 12, marginTop: 4, textAlign: 'center' },
  islamBanner: { margin: 16, borderRadius: 16, padding: 16, alignItems: 'center' },
  islamText: { color: '#fff', fontSize: 18, fontWeight: '800', textAlign: 'center', writingDirection: 'rtl' },
  islamTextEn: { color: 'rgba(255,255,255,0.8)', fontSize: 11, textAlign: 'center', marginTop: 4, fontStyle: 'italic' },
  section: { paddingHorizontal: 16, marginVertical: 8 },
  sectionTitle: { fontSize: 15, fontWeight: '800', color: COLORS.text, marginBottom: 12 },
  grid: { flexDirection: 'row', flexWrap: 'wrap', gap: 10 },
  moduleCard: { width: (width - 52) / 2, borderRadius: 18, padding: 16, ...SHADOWS.small },
  moduleEmoji: { fontSize: 28, marginBottom: 8 },
  moduleTitle: { color: '#fff', fontSize: 14, fontWeight: '800' },
  moduleSub: { color: 'rgba(255,255,255,0.8)', fontSize: 10, marginTop: 2 },
  catRow: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#fff', borderRadius: 14, padding: 14, marginBottom: 8, ...SHADOWS.small },
  catIcon: { width: 48, height: 48, borderRadius: 14, alignItems: 'center', justifyContent: 'center', marginRight: 12 },
  catEmoji: { fontSize: 24 },
  catInfo: { flex: 1 },
  catTitleAr: { fontSize: 16, fontWeight: '800', color: '#1B3A6B', textAlign: 'right', writingDirection: 'rtl' },
  catTitleEn: { fontSize: 12, color: COLORS.muted },
  catSub: { fontSize: 11, color: COLORS.muted, marginTop: 2 },
  catRight: { flexDirection: 'row', alignItems: 'center', gap: 2 },
  catPreview: { fontSize: 16, marginRight: 2 },
});
