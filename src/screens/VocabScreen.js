// src/screens/VocabScreen.js
import React, { useState } from 'react';
import {
  View, Text, StyleSheet, FlatList, TouchableOpacity,
  Dimensions, ScrollView, Modal, Animated,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { speakEnglish, speakArabic, speakRhyme, stopSpeech } from '../utils/tts';
import { COLORS, SHADOWS } from '../utils/theme';
import { VOCAB_CATEGORIES } from '../data/curriculum';

const { width } = Dimensions.get('window');

export default function VocabScreen({ route, navigation }) {
  const { category, language } = route.params || { category: 'animals', language: 'english' };
  const isArabic = language === 'arabic';

  const categories = isArabic ? VOCAB_CATEGORIES.arabic : VOCAB_CATEGORIES.english;
  const [activeCategory, setActiveCategory] = useState(category);
  const [selectedWord, setSelectedWord] = useState(null);

  const currentCat = categories.find(c => c.id === activeCategory) || categories[0];
  const words = currentCat?.words || [];

  const speak = (word) => {
    const text = isArabic ? word.word : word.word;
    Speech.speak(text, { language: isArabic ? 'ar' : 'en', pitch: 1.2, rate: 0.7 });
      };

  const openWord = (word) => {
    setSelectedWord(word);
    speak(word);
  };

  return (
    <View style={styles.container}>
      {/* Header */}
      <LinearGradient
        colors={isArabic ? ['#E65100', '#BF360C'] : ['#7B1FA2', '#9C27B0']}
        style={styles.header}
      >
        <TouchableOpacity onPress={() => navigation.goBack()} style={styles.backBtn}>
          <Ionicons name="arrow-back" size={24} color="#fff" />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>
          {isArabic ? '📚 المفردات · Vocabulary' : '📚 Vocabulary'}
        </Text>
      </LinearGradient>

      {/* Category tabs */}
      <View style={styles.tabsWrapper}>
        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.tabs}>
          {categories.map((cat) => (
            <TouchableOpacity
              key={cat.id}
              style={[styles.tab, activeCategory === cat.id && { backgroundColor: cat.color }]}
              onPress={() => setActiveCategory(cat.id)}
            >
              <Text style={styles.tabEmoji}>{cat.emoji}</Text>
              <Text style={[styles.tabLabel, activeCategory === cat.id && { color: '#fff' }]}>
                {isArabic ? cat.title : cat.title}
              </Text>
            </TouchableOpacity>
          ))}
        </ScrollView>
      </View>

      {/* Words grid */}
      <FlatList
        data={words}
        keyExtractor={(_, i) => i.toString()}
        numColumns={2}
        contentContainerStyle={styles.wordGrid}
        renderItem={({ item }) => (
          <TouchableOpacity
            style={[styles.wordCard, { borderTopColor: currentCat.color }]}
            onPress={() => openWord(item)}
            activeOpacity={0.8}
          >
            <Text style={styles.wordEmoji}>{item.emoji}</Text>
            <Text style={isArabic ? styles.arabicWord : styles.wordText}>
              {isArabic ? item.word : item.word}
            </Text>
            {isArabic && <Text style={styles.translit}>{item.transliteration}</Text>}
            {!isArabic && <Text style={styles.arabicTranslation}>{item.arabic}</Text>}
            <TouchableOpacity
              style={[styles.speakSmall, { backgroundColor: currentCat.color }]}
              onPress={() => speak(item)}
            >
              <Ionicons name="volume-high" size={14} color="#fff" />
            </TouchableOpacity>
          </TouchableOpacity>
        )}
      />

      {/* Word detail modal */}
      <Modal visible={!!selectedWord} transparent animationType="slide">
        <View style={styles.modalOverlay}>
          <View style={styles.modalCard}>
            <TouchableOpacity style={styles.modalClose} onPress={() => setSelectedWord(null)}>
              <Ionicons name="close-circle" size={36} color={COLORS.muted} />
            </TouchableOpacity>

            {selectedWord && (
              <>
                <Text style={styles.modalEmoji}>{selectedWord.emoji}</Text>

                {isArabic ? (
                  <>
                    <Text style={styles.modalArabicWord}>{selectedWord.word}</Text>
                    <Text style={styles.modalTranslit}>/{selectedWord.transliteration}/</Text>
                    <Text style={styles.modalEnglish}>{selectedWord.english}</Text>
                    <View style={styles.sentenceBox}>
                      <Text style={styles.sentenceLabel}>مثال · Example:</Text>
                      <Text style={styles.arabicSentence}>{selectedWord.sentence}</Text>
                      <Text style={styles.sentenceEn}>{selectedWord.sentenceEn}</Text>
                    </View>
                  </>
                ) : (
                  <>
                    <Text style={styles.modalWordEn}>{selectedWord.word}</Text>
                    <Text style={styles.modalIPA}>/{selectedWord.pronunciation}/</Text>
                    <Text style={styles.modalArabicSmall}>{selectedWord.arabic} · Arabic</Text>
                    <View style={styles.sentenceBox}>
                      <Text style={styles.sentenceLabel}>Example sentence:</Text>
                      <Text style={styles.sentence}>{selectedWord.sentence}</Text>
                    </View>
                  </>
                )}

                <View style={styles.funBox}>
                  <Text style={styles.funLabel}>🌟 Fun Fact!</Text>
                  <Text style={styles.funText}>{selectedWord.fun}</Text>
                </View>

                <TouchableOpacity
                  style={[styles.speakBtn, { backgroundColor: currentCat.color }]}
                  onPress={() => speak(selectedWord)}
                >
                  <Ionicons name="volume-high" size={22} color="#fff" />
                  <Text style={styles.speakBtnText}>
                    {isArabic ? 'استمع · Listen' : 'Listen & Repeat'}
                  </Text>
                </TouchableOpacity>
              </>
            )}
          </View>
        </View>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: COLORS.bg },
  header: { paddingTop: 55, paddingBottom: 20, paddingHorizontal: 16 },
  backBtn: { marginBottom: 10 },
  headerTitle: { color: '#fff', fontSize: 20, fontWeight: '900' },
  tabsWrapper: { backgroundColor: '#fff', paddingVertical: 8, ...SHADOWS.small },
  tabs: { paddingHorizontal: 12, gap: 8 },
  tab: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#F0F4FF', borderRadius: 20, paddingVertical: 6, paddingHorizontal: 12, gap: 4 },
  tabEmoji: { fontSize: 16 },
  tabLabel: { fontSize: 12, fontWeight: '700', color: COLORS.text },
  wordGrid: { padding: 12 },
  wordCard: { flex: 1, backgroundColor: '#fff', borderRadius: 16, margin: 6, padding: 16, alignItems: 'center', borderTopWidth: 3, ...SHADOWS.small },
  wordEmoji: { fontSize: 40, marginBottom: 8 },
  wordText: { fontSize: 16, fontWeight: '800', color: COLORS.text },
  arabicWord: { fontSize: 20, fontWeight: '900', color: COLORS.arabic, textAlign: 'right', writingDirection: 'rtl' },
  translit: { fontSize: 11, color: COLORS.muted, marginTop: 2, fontStyle: 'italic' },
  arabicTranslation: { fontSize: 12, color: COLORS.muted, marginTop: 2 },
  speakSmall: { marginTop: 8, width: 30, height: 30, borderRadius: 15, alignItems: 'center', justifyContent: 'center' },
  modalOverlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'flex-end' },
  modalCard: { backgroundColor: '#fff', borderTopLeftRadius: 28, borderTopRightRadius: 28, padding: 28, maxHeight: '80%' },
  modalClose: { alignSelf: 'flex-end', marginBottom: 8 },
  modalEmoji: { fontSize: 64, textAlign: 'center' },
  modalArabicWord: { fontSize: 36, fontWeight: '900', color: COLORS.arabic, textAlign: 'right', writingDirection: 'rtl', marginTop: 8, alignSelf: 'stretch', paddingHorizontal: 8 },
  modalTranslit: { fontSize: 14, color: COLORS.muted, textAlign: 'center', fontStyle: 'italic' },
  modalEnglish: { fontSize: 20, fontWeight: '700', color: COLORS.text, textAlign: 'center', marginTop: 4 },
  modalWordEn: { fontSize: 28, fontWeight: '900', color: COLORS.text, textAlign: 'center', marginTop: 8 },
  modalIPA: { fontSize: 14, color: COLORS.muted, textAlign: 'center', fontStyle: 'italic' },
  modalArabicSmall: { fontSize: 16, color: COLORS.arabic, textAlign: 'center', marginTop: 4 },
  sentenceBox: { backgroundColor: '#F0F4FF', borderRadius: 14, padding: 14, marginTop: 12, width: '100%' },
  sentenceLabel: { fontSize: 11, color: COLORS.muted, fontWeight: '700', marginBottom: 4 },
  arabicSentence: { fontSize: 18, fontWeight: '700', color: COLORS.arabic, textAlign: 'right', writingDirection: 'rtl' },
  sentenceEn: { fontSize: 13, color: COLORS.muted, fontStyle: 'italic', marginTop: 4 },
  sentence: { fontSize: 15, color: COLORS.text, fontStyle: 'italic' },
  funBox: { backgroundColor: '#FFF9C4', borderRadius: 12, padding: 12, marginTop: 10, width: '100%' },
  funLabel: { fontWeight: '800', fontSize: 13, color: '#795548', marginBottom: 4 },
  funText: { fontSize: 13, color: '#5D4037' },
  speakBtn: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', borderRadius: 16, padding: 16, marginTop: 16, gap: 8 },
  speakBtnText: { color: '#fff', fontWeight: '800', fontSize: 16 },
});
