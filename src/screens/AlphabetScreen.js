// src/screens/AlphabetScreen.js
import React, { useState, useRef } from 'react';
import {
  View, Text, StyleSheet, FlatList, TouchableOpacity,
  useWindowDimensions, Animated, Modal, ScrollView,
} from 'react-native';
import { markLetterSeen } from '../utils/progress';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';


import { COLORS, SHADOWS } from '../utils/theme';
import { speakEnglish, speakArabic, speakRhyme, speakIslamicLetter, speakArabicLetter, stopSpeech } from '../utils/tts';
import { ENGLISH_ALPHABET, ARABIC_ALPHABET } from '../data/curriculum';


export default function AlphabetScreen({ route, navigation }) {
  const { language } = route.params || { language: 'english' };
  const isArabic = language === 'arabic';
  const alphabet = isArabic ? ARABIC_ALPHABET : ENGLISH_ALPHABET;
  const { width } = useWindowDimensions();
  // 4 columns on phones, more on tablets/desktop (cards ~148px incl. margins), max 10
  const numCols = Math.max(4, Math.min(10, Math.floor((width - 24) / 148)));
  // grid padding 12*2 + card margins 4*2 per card; cap so cards stay finger-sized, not poster-sized
  const cardSize = Math.min((width - 24) / numCols - 8, 140);

  const [selected, setSelected] = useState(null);
  const [modalVisible, setModalVisible] = useState(false);
  const [rhymeVisible, setRhymeVisible] = useState(false);
  const scaleAnim = useRef(new Animated.Value(0.8)).current;

  // TTS helpers using enhanced voice system
  const speak = (text, lang) => {
    if (lang === 'ar') speakArabic(text);
    else speakEnglish(text);
  };

  const openLetter = (item) => {
    setSelected(item);
    setModalVisible(true);
    setRhymeVisible(false);
    markLetterSeen(language, item.letter);
    Animated.spring(scaleAnim, { toValue: 1, useNativeDriver: true, tension: 100, friction: 8 }).start();
    if (isArabic) {
      speakArabicLetter(item.name, item.word, item.wordEn);
    } else {
      speakIslamicLetter(item.letter, item.islamicWord, item.meaning);
    }
  };

  // ── English card: shows Letter + Islamic word + emoji ──
  const renderEnglishCard = ({ item }) => (
    <TouchableOpacity
      onPress={() => openLetter(item)}
      style={[styles.card, { backgroundColor: item.color, width: cardSize, height: cardSize + 12 }]}
      activeOpacity={0.82}
      accessibilityRole="button"
      accessibilityLabel={`${item.letter} for ${item.islamicWord}`}
    >
      <Text style={styles.cardLetter}>{item.letter}</Text>
      <Text style={styles.cardEmoji}>{item.emoji}</Text>
      <Text style={styles.cardIslamicWord} numberOfLines={1}>{item.islamicWord}</Text>
    </TouchableOpacity>
  );

  // ── Arabic card: shows Arabic letter + Roman name + Islamic word ──
  const renderArabicCard = ({ item }) => (
    <TouchableOpacity
      onPress={() => openLetter(item)}
      style={[styles.card, { backgroundColor: item.color, width: cardSize, height: cardSize + 12 }]}
      activeOpacity={0.82}
      accessibilityRole="button"
      accessibilityLabel={`${item.name}, ${item.wordEn}`}
    >
      <Text style={styles.arabicCardLetter}>{item.letter}</Text>
      <Text style={styles.arabicCardRoman}>{item.roman}</Text>
      <Text style={styles.arabicCardWord} numberOfLines={1}>{item.word}</Text>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <LinearGradient
        colors={isArabic ? ['#1B3A6B', '#0F1F3D'] : ['#1B5E20', '#2E7D32']}
        style={styles.header}
      >
        <TouchableOpacity onPress={() => navigation.goBack()} style={styles.backBtn} accessibilityRole="button" accessibilityLabel="Go back">
          <Ionicons name="arrow-back" size={24} color="#fff" />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>
          {isArabic ? '✦ الحروف العربية ✦' : '🌙 Islamic A to Z'}
        </Text>
        <Text style={[styles.headerSub, !isArabic && { writingDirection: 'ltr' }]}>
          {isArabic
            ? 'الحروف العربية مع الكلمات الإسلامية'
            : 'A for Allah · B for Bismillah · C for Charity…'}
        </Text>
        <Text style={styles.headerHint}>Tap any letter to learn!</Text>
      </LinearGradient>

      <FlatList
        data={alphabet}
        keyExtractor={(item) => item.letter}
        renderItem={isArabic ? renderArabicCard : renderEnglishCard}
        key={`cols-${numCols}`}
        numColumns={numCols}
        columnWrapperStyle={{ justifyContent: 'center' }}
        contentContainerStyle={styles.grid}
        showsVerticalScrollIndicator={false}
      />

      {/* ── Detail Modal ── */}
      <Modal visible={modalVisible} transparent animationType="fade" onRequestClose={() => { setModalVisible(false); stopSpeech(); }}>
        <View style={styles.overlay}>
          <Animated.View style={[styles.modal, { transform: [{ scale: scaleAnim }] }]}>
            <TouchableOpacity
              style={styles.closeBtn}
              onPress={() => { setModalVisible(false); stopSpeech(); }}
              accessibilityRole="button"
              accessibilityLabel="Close"
            >
              <Ionicons name="close-circle" size={34} color={COLORS.muted} />
            </TouchableOpacity>

            {selected && (
              <ScrollView showsVerticalScrollIndicator={false}>
                {/* Banner */}
                <LinearGradient
                  colors={[selected.color, selected.color + 'AA']}
                  style={styles.banner}
                >
                  {isArabic ? (
                    <>
                      <Text style={styles.bannerArabicLetter}>{selected.letter}</Text>
                      <Text style={styles.bannerEmoji}>{selected.emoji}</Text>
                    </>
                  ) : (
                    <>
                      <Text style={styles.bannerLetter}>{selected.letter}</Text>
                      <Text style={styles.bannerEmoji}>{selected.emoji}</Text>
                    </>
                  )}
                </LinearGradient>

                {isArabic ? (
                  // ── Arabic detail ──
                  <>
                    <Text style={styles.mainWord}>{selected.roman}</Text>
                    <Text style={styles.pronunciation}>Sound: /{selected.sound}/</Text>

                    {/* Arabic word RTL */}
                    <TouchableOpacity
                      style={[styles.arabicWordBox, { borderColor: selected.color }]}
                      onPress={() => speakArabic(selected.word, 0.65)}
                    >
                      <Text style={styles.arabicWordBig}>{selected.word}</Text>
                      <Text style={styles.arabicWordEn}>{selected.wordEn}</Text>
                      <View style={styles.listenRow}>
                        <Ionicons name="volume-high" size={16} color={selected.color} />
                        <Text style={[styles.listenTxt, { color: selected.color }]}>
                          اضغط للاستماع · Tap to hear
                        </Text>
                      </View>
                    </TouchableOpacity>

                    {/* Letter forms */}
                    <View style={styles.formsContainer}>
                      <Text style={styles.formsTitle}>Letter Forms (أشكال الحرف)</Text>
                      <View style={styles.formsRow}>
                        {[
                          { label: 'Isolated\nمنفصل', form: selected.letter },
                          { label: 'Initial\nأول', form: selected.forms?.initial },
                          { label: 'Medial\nوسط', form: selected.forms?.medial },
                          { label: 'Final\nآخر', form: selected.forms?.final },
                        ].map((f, i) => (
                          <View key={i} style={[styles.formCard, { backgroundColor: selected.color + '20' }]}>
                            <Text style={[styles.formLetter, { color: selected.color }]}>{f.form}</Text>
                            <Text style={styles.formLabel}>{f.label}</Text>
                          </View>
                        ))}
                      </View>
                    </View>
                  </>
                ) : (
                  // ── English/Islamic detail ──
                  <>
                    <Text style={styles.mainWord}>{selected.islamicWord}</Text>
                    <Text style={styles.pronunciation}>/{selected.pronunciation}/</Text>

                    {/* Arabic translation RTL */}
                    <TouchableOpacity
                      style={[styles.arabicWordBox, { borderColor: selected.color }]}
                      onPress={() => speakArabic(selected.arabic, 0.65)}
                    >
                      <Text style={styles.arabicWordBig}>{selected.arabic}</Text>
                      <View style={styles.listenRow}>
                        <Ionicons name="volume-high" size={16} color={selected.color} />
                        <Text style={[styles.listenTxt, { color: selected.color }]}>
                          Tap to hear Arabic
                        </Text>
                      </View>
                    </TouchableOpacity>

                    {/* Meaning */}
                    <View style={styles.meaningBox}>
                      <Text style={styles.meaningLabel}>📖 Meaning</Text>
                      <Text style={styles.meaningText}>{selected.meaning}</Text>
                    </View>

                    {/* Rhyme */}
                    {!rhymeVisible ? (
                      <TouchableOpacity
                        style={[styles.rhymeBtn, { backgroundColor: selected.color }]}
                        onPress={() => {
                          setRhymeVisible(true);
                          speakRhyme(selected.rhyme);
                        }}
                      >
                        <Ionicons name="musical-notes" size={18} color="#fff" />
                        <Text style={styles.rhymeBtnText}>🎵 Hear the Rhyme!</Text>
                      </TouchableOpacity>
                    ) : (
                      <View style={[styles.rhymeCard, { borderColor: selected.color }]}>
                        <Text style={styles.rhymeTitle}>🎵 Rhyme</Text>
                        <Text style={styles.rhymeText}>{selected.rhyme}</Text>
                        <TouchableOpacity
                          style={styles.listenRow}
                          onPress={() => speakRhyme(selected.rhyme)}
                        >
                          <Ionicons name="volume-high" size={16} color={selected.color} />
                          <Text style={[styles.listenTxt, { color: selected.color }]}>Repeat</Text>
                        </TouchableOpacity>
                      </View>
                    )}
                  </>
                )}

                {/* Speak button */}
                <TouchableOpacity
                  style={[styles.speakBtn, { backgroundColor: selected.color }]}
                  onPress={() => {
                    if (isArabic) speakArabicLetter(selected.name, selected.word, selected.wordEn);
                    else speakIslamicLetter(selected.letter, selected.islamicWord, selected.meaning);
                  }}
                >
                  <Ionicons name="volume-high" size={22} color="#fff" />
                  <Text style={styles.speakBtnText}>Listen Again</Text>
                </TouchableOpacity>
                <View style={{ height: 20 }} />
              </ScrollView>
            )}
          </Animated.View>
        </View>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: COLORS.bg },
  header: { paddingTop: 55, paddingBottom: 20, paddingHorizontal: 16 },
  backBtn: { marginBottom: 10 },
  headerTitle: { color: '#fff', fontSize: 20, fontWeight: '900', textAlign: 'center' },
  headerSub: { color: 'rgba(255,255,255,0.85)', fontSize: 12, textAlign: 'center', marginTop: 4, writingDirection: 'rtl' },
  headerHint: { color: 'rgba(255,255,255,0.7)', fontSize: 11, textAlign: 'center', marginTop: 4 },
  grid: { padding: 10, paddingBottom: 40 },

  // ── Cards ──
  card: {
    margin: 4,
    borderRadius: 14, alignItems: 'center', justifyContent: 'center', ...SHADOWS.small,
  },
  cardLetter: { fontSize: 22, fontWeight: '900', color: '#fff' },
  cardEmoji: { fontSize: 14, marginTop: 2 },
  cardIslamicWord: { fontSize: 8, color: 'rgba(255,255,255,0.92)', fontWeight: '700', textAlign: 'center', paddingHorizontal: 2, marginTop: 2 },
  arabicCardLetter: { fontSize: 26, fontWeight: '900', color: '#fff', writingDirection: 'rtl' },
  arabicCardRoman: { fontSize: 8, color: 'rgba(255,255,255,0.85)', fontWeight: '700', textTransform: 'uppercase', letterSpacing: 1 },
  arabicCardWord: { fontSize: 9, color: 'rgba(255,255,255,0.9)', fontWeight: '800', textAlign: 'center', writingDirection: 'rtl', paddingHorizontal: 2, marginTop: 1 },

  // ── Modal ──
  overlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.65)', justifyContent: 'center', alignItems: 'center', padding: 16 },
  modal: { backgroundColor: '#fff', borderRadius: 28, width: '100%', maxHeight: '88%', overflow: 'hidden' },
  closeBtn: { position: 'absolute', top: 12, right: 12, zIndex: 10 },
  banner: { height: 120, alignItems: 'center', justifyContent: 'center', flexDirection: 'row', gap: 16 },
  bannerLetter: { fontSize: 68, fontWeight: '900', color: '#fff' },
  bannerArabicLetter: { fontSize: 72, fontWeight: '900', color: '#fff', writingDirection: 'rtl' },
  bannerEmoji: { fontSize: 44 },

  mainWord: { fontSize: 24, fontWeight: '900', color: COLORS.text, textAlign: 'center', marginTop: 14, paddingHorizontal: 16 },
  pronunciation: { fontSize: 13, color: COLORS.muted, textAlign: 'center', fontStyle: 'italic', marginTop: 2 },

  // Arabic RTL box
  arabicWordBox: { margin: 16, borderRadius: 16, borderWidth: 2, padding: 16, backgroundColor: '#F9FBE7', alignItems: 'center' },
  arabicWordBig: { fontSize: 34, fontWeight: '900', color: '#1B5E20', textAlign: 'right', writingDirection: 'rtl', alignSelf: 'stretch' },
  arabicWordEn: { fontSize: 14, color: COLORS.muted, marginTop: 4, fontStyle: 'italic' },
  listenRow: { flexDirection: 'row', alignItems: 'center', gap: 6, marginTop: 8 },
  listenTxt: { fontSize: 12, fontWeight: '700' },

  // Letter forms
  formsContainer: { marginHorizontal: 16, marginBottom: 10 },
  formsTitle: { fontSize: 12, fontWeight: '700', color: COLORS.muted, textAlign: 'center', marginBottom: 8 },
  formsRow: { flexDirection: 'row', justifyContent: 'space-around' },
  formCard: { alignItems: 'center', padding: 8, borderRadius: 12, flex: 1, marginHorizontal: 3 },
  formLetter: { fontSize: 26, fontWeight: '900', writingDirection: 'rtl' },
  formLabel: { fontSize: 9, color: COLORS.muted, textAlign: 'center', marginTop: 4 },

  // Meaning
  meaningBox: { marginHorizontal: 16, backgroundColor: '#E3F2FD', borderRadius: 14, padding: 14, marginBottom: 10 },
  meaningLabel: { fontSize: 12, fontWeight: '800', color: '#1565C0', marginBottom: 4 },
  meaningText: { fontSize: 15, color: '#0D47A1', fontWeight: '600' },

  // Rhyme
  rhymeBtn: { marginHorizontal: 16, borderRadius: 16, padding: 14, flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 8, marginBottom: 10 },
  rhymeBtnText: { color: '#fff', fontWeight: '800', fontSize: 14 },
  rhymeCard: { marginHorizontal: 16, borderRadius: 16, borderWidth: 2, padding: 16, backgroundColor: '#F3E5F5', marginBottom: 10 },
  rhymeTitle: { fontSize: 13, fontWeight: '800', color: COLORS.text, marginBottom: 6 },
  rhymeText: { fontSize: 16, color: COLORS.text, lineHeight: 26, fontStyle: 'italic' },

  // Speak
  speakBtn: { marginHorizontal: 16, borderRadius: 16, padding: 14, flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 8 },
  speakBtnText: { color: '#fff', fontWeight: '800', fontSize: 15 },
});
