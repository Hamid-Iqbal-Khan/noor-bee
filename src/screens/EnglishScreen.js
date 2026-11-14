// src/screens/EnglishScreen.js
import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, useWindowDimensions } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { COLORS, SHADOWS } from '../utils/theme';
import { VOCAB_CATEGORIES } from '../data/curriculum';


const MODULES = [
  { title: '🌙 Islamic A–Z', subtitle: 'A for Allah · B for Bismillah…', emoji: '☪️', color: '#1B5E20', screen: 'Alphabet', params: { language: 'english' } },
  { title: 'Vocabulary', subtitle: `${VOCAB_CATEGORIES.english.length} categories · ${VOCAB_CATEGORIES.english.reduce((n, c) => n + c.words.length, 0)} words`, emoji: '📚', color: '#7B1FA2', screen: 'Vocab', params: { language: 'english' } },
  { title: 'Flashcards', subtitle: 'Swipe to learn words', emoji: '🃏', color: '#00695C', screen: 'Flashcard', params: { category: 'animals', language: 'english' } },
  { title: 'Fun Quiz', subtitle: 'Test your knowledge', emoji: '❓', color: '#E65100', screen: 'Quiz', params: { category: 'english_animals', language: 'english' } },
  { title: 'Read Stories', subtitle: '2 stories with pictures', emoji: '📖', color: '#880E4F', screen: 'Story', params: { storyId: 's1' } },
  { title: 'Phrases', subtitle: 'Greetings & conversations', emoji: '💬', color: '#1A237E', screen: 'Phrases', params: { language: 'english' } },
  { title: 'Match Game', subtitle: 'Match words to pictures', emoji: '🧩', color: '#BF360C', screen: 'Matching', params: { language: 'english' } },
  { title: 'Colors Quiz', subtitle: 'Can you name the colors?', emoji: '🎨', color: '#AD1457', screen: 'Quiz', params: { category: 'english_colors', language: 'english' } },
];

export default function EnglishScreen({ navigation }) {
  const { width } = useWindowDimensions();
  const cols = width >= 900 ? 4 : width >= 600 ? 3 : 2;
  const cardWidth = (width - 32 - 10 * (cols - 1)) / cols;
  const categories = VOCAB_CATEGORIES.english;
  return (
    <View style={styles.container}>
      <ScrollView showsVerticalScrollIndicator={false}>
        <LinearGradient colors={['#1B5E20', '#2E7D32', '#43A047']} style={styles.header}>
          <Text style={styles.headerEmoji}>☪️</Text>
          <Text style={styles.headerTitle}>Islamic English Learning</Text>
          <Text style={styles.headerSub}>A for Allah · B for Bismillah · C for Charity</Text>
        </LinearGradient>

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>📚 Learning Modules</Text>
          <View style={styles.grid}>
            {MODULES.map((mod, i) => (
              <TouchableOpacity
                key={i}
                style={[styles.moduleCard, { backgroundColor: mod.color, width: cardWidth }]}
                accessibilityRole="button"
                accessibilityLabel={`${mod.title}. ${mod.subtitle}`}
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

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>🗂️ Vocabulary Categories</Text>
          {categories.map((cat, i) => (
            <TouchableOpacity
              key={i}
              style={styles.catRow}
              onPress={() => navigation.navigate('Vocab', { category: cat.id, language: 'english' })}
            >
              <View style={[styles.catIcon, { backgroundColor: cat.color + '20' }]}>
                <Text style={styles.catEmoji}>{cat.emoji}</Text>
              </View>
              <View style={styles.catInfo}>
                <Text style={styles.catTitle}>{cat.title}</Text>
                <Text style={styles.catSub}>{cat.words.length} words</Text>
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
  header: { paddingTop: 55, paddingBottom: 28, alignItems: 'center' },
  headerEmoji: { fontSize: 48, marginBottom: 8 },
  headerTitle: { color: '#fff', fontSize: 22, fontWeight: '900' },
  headerSub: { color: 'rgba(255,255,255,0.85)', fontSize: 12, marginTop: 4, textAlign: 'center' },
  section: { paddingHorizontal: 16, marginVertical: 8 },
  sectionTitle: { fontSize: 17, fontWeight: '800', color: COLORS.text, marginBottom: 12 },
  grid: { flexDirection: 'row', flexWrap: 'wrap', gap: 10 },
  moduleCard: { borderRadius: 18, padding: 16, ...SHADOWS.small },
  moduleEmoji: { fontSize: 30, marginBottom: 8 },
  moduleTitle: { color: '#fff', fontSize: 14, fontWeight: '800' },
  moduleSub: { color: 'rgba(255,255,255,0.8)', fontSize: 11, marginTop: 2 },
  catRow: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#fff', borderRadius: 14, padding: 14, marginBottom: 8, ...SHADOWS.small },
  catIcon: { width: 48, height: 48, borderRadius: 14, alignItems: 'center', justifyContent: 'center', marginRight: 12 },
  catEmoji: { fontSize: 24 },
  catInfo: { flex: 1 },
  catTitle: { fontSize: 15, fontWeight: '700', color: COLORS.text },
  catSub: { fontSize: 12, color: COLORS.muted, marginTop: 2 },
  catRight: { flexDirection: 'row', alignItems: 'center', gap: 2 },
  catPreview: { fontSize: 16, marginRight: 2 },
});
