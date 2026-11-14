// src/screens/IslamicAlphabetScreen.js
import React, { useState, useRef } from 'react';
import {
  View, Text, StyleSheet, FlatList, TouchableOpacity,
  Dimensions, Animated, Modal, ScrollView, I18nManager,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { speakEnglish, speakArabic, speakRhyme, stopSpeech } from '../utils/tts';
import { COLORS, SHADOWS } from '../utils/theme';
import { ISLAMIC_ALPHABET } from '../data/curriculum';

const { width } = Dimensions.get('window');
const CARD_SIZE = (width - 56) / 4;

export default function IslamicAlphabetScreen({ navigation }) {
  const [selected, setSelected] = useState(null);
  const [modalVisible, setModalVisible] = useState(false);
  const [rhymeVisible, setRhymeVisible] = useState(false);
  const scaleAnim = useRef(new Animated.Value(0.85)).current;
  const rhymeAnim = useRef(new Animated.Value(0)).current;

  const openLetter = (item) => {
    setSelected(item);
    setModalVisible(true);
    setRhymeVisible(false);
    Animated.spring(scaleAnim, {
      toValue: 1, useNativeDriver: true, tension: 100, friction: 8,
    }).start();
    speakEnglish(`${item.letter} for ${item.islamicWord}. ${item.meaning}.`);
  };

  const showRhyme = () => {
    setRhymeVisible(true);
    Animated.timing(rhymeAnim, { toValue: 1, duration: 400, useNativeDriver: true }).start();
    if (selected) speakRhyme(selected.rhyme);
  };

  const renderCard = ({ item }) => (
    <TouchableOpacity
      onPress={() => openLetter(item)}
      style={[styles.card, { backgroundColor: item.color }]}
      activeOpacity={0.82}
    >
      <Text style={styles.cardLetter}>{item.letter}</Text>
      <Text style={styles.cardEmoji}>{item.emoji}</Text>
      <Text style={styles.cardWord} numberOfLines={1}>{item.islamicWord}</Text>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      {/* Header */}
      <LinearGradient colors={['#1B5E20', '#2E7D32', '#388E3C']} style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()} style={styles.backBtn} accessibilityRole="button" accessibilityLabel="Go back">
          <Ionicons name="arrow-back" size={24} color="#fff" />
        </TouchableOpacity>
        <View style={styles.headerContent}>
          <Text style={styles.headerTitle}>🌙 Islamic A–Z</Text>
          <Text style={styles.headerArabic}>الأبجدية الإسلامية</Text>
          <Text style={styles.headerSub}>Tap any letter to learn!</Text>
        </View>
      </LinearGradient>

      <FlatList
        data={ISLAMIC_ALPHABET}
        keyExtractor={(item) => item.letter}
        renderItem={renderCard}
        numColumns={4}
        contentContainerStyle={styles.grid}
        showsVerticalScrollIndicator={false}
        ListHeaderComponent={
          <View style={styles.intro}>
            <Text style={styles.introText}>
              🌟 Learn A–Z through beautiful Islamic words with Arabic, rhymes & meanings!
            </Text>
          </View>
        }
      />

      {/* Detail Modal */}
      <Modal visible={modalVisible} transparent animationType="fade">
        <View style={styles.overlay}>
          <Animated.View style={[styles.modal, { transform: [{ scale: scaleAnim }] }]}>
            {/* Close */}
            <TouchableOpacity style={styles.closeBtn} onPress={() => { setModalVisible(false); stopSpeech(); }}>
              <Ionicons name="close-circle" size={34} color={COLORS.muted} />
            </TouchableOpacity>

            {selected && (
              <ScrollView showsVerticalScrollIndicator={false}>
                {/* Big letter banner */}
                <LinearGradient
                  colors={[selected.color, selected.color + 'AA']}
                  style={styles.letterBanner}
                >
                  <Text style={styles.bigLetter}>{selected.letter}</Text>
                  <Text style={styles.bigEmoji}>{selected.emoji}</Text>
                </LinearGradient>

                {/* Islamic word */}
                <Text style={styles.islamicWord}>{selected.islamicWord}</Text>
                <Text style={styles.pronunciation}>/{selected.pronunciation}/</Text>

                {/* Arabic — RIGHT TO LEFT */}
                <TouchableOpacity
                  style={[styles.arabicBox, { borderColor: selected.color }]}
                  onPress={() => speakArabic(selected.arabic)}
                >
                  <View style={styles.arabicRow}>
                    <Ionicons name="volume-high" size={18} color={selected.color} />
                    <Text style={styles.arabicText}>{selected.arabic}</Text>
                  </View>
                  <Text style={styles.arabicHint}>اضغط للاستماع · Tap to hear Arabic</Text>
                </TouchableOpacity>

                {/* Meaning */}
                <View style={styles.meaningBox}>
                  <Text style={styles.meaningLabel}>📖 Meaning</Text>
                  <Text style={styles.meaningText}>{selected.meaning}</Text>
                </View>

                {/* Picture prompt */}
                {!!selected.picturePrompt && (
                <View style={styles.pictureBox}>
                  <Text style={styles.pictureLabel}>🖼️ Picture to imagine</Text>
                  <Text style={styles.pictureText}>{selected.picturePrompt}</Text>
                </View>
                )}

                {/* Rhyme button / display */}
                {!rhymeVisible ? (
                  <TouchableOpacity
                    style={[styles.rhymeBtn, { backgroundColor: selected.color }]}
                    onPress={showRhyme}
                  >
                    <Ionicons name="musical-notes" size={20} color="#fff" />
                    <Text style={styles.rhymeBtnText}>🎵 Hear the Rhyme!</Text>
                  </TouchableOpacity>
                ) : (
                  <Animated.View style={[styles.rhymeCard, { opacity: rhymeAnim, borderColor: selected.color }]}>
                    <Text style={styles.rhymeTitle}>🎵 Rhyme</Text>
                    <Text style={styles.rhymeText}>{selected.rhyme}</Text>
                    <TouchableOpacity
                      style={{ marginTop: 8, flexDirection: 'row', alignItems: 'center', gap: 6 }}
                      onPress={() => speakRhyme(selected.rhyme)}
                    >
                      <Ionicons name="volume-high" size={16} color={selected.color} />
                      <Text style={[styles.repeatTxt, { color: selected.color }]}>Repeat rhyme</Text>
                    </TouchableOpacity>
                  </Animated.View>
                )}

                {/* Speak button */}
                <TouchableOpacity
                  style={[styles.speakBtn, { backgroundColor: selected.color }]}
                  onPress={() => speakEnglish(`${selected.letter} for ${selected.islamicWord}. ${selected.meaning}.`)}
                >
                  <Ionicons name="volume-high" size={22} color="#fff" />
                  <Text style={styles.speakBtnText}>Listen Again</Text>
                </TouchableOpacity>

                <View style={{ height: 16 }} />
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
  headerContent: { alignItems: 'center' },
  headerTitle: { color: '#fff', fontSize: 22, fontWeight: '900' },
  headerArabic: { color: 'rgba(255,255,255,0.9)', fontSize: 18, fontWeight: '800', marginTop: 2, writingDirection: 'rtl' },
  headerSub: { color: 'rgba(255,255,255,0.8)', fontSize: 13, marginTop: 4 },
  grid: { padding: 12, paddingBottom: 40 },
  intro: { backgroundColor: '#E8F5E9', borderRadius: 14, padding: 12, marginBottom: 12 },
  introText: { fontSize: 13, color: '#1B5E20', fontWeight: '600', textAlign: 'center' },
  card: { width: CARD_SIZE, height: CARD_SIZE + 10, margin: 4, borderRadius: 14, alignItems: 'center', justifyContent: 'center', ...SHADOWS.small },
  cardLetter: { fontSize: 20, fontWeight: '900', color: '#fff' },
  cardEmoji: { fontSize: 16, marginTop: 2 },
  cardWord: { fontSize: 8, color: 'rgba(255,255,255,0.9)', fontWeight: '700', textAlign: 'center', paddingHorizontal: 2, marginTop: 2 },

  // Modal
  overlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.65)', justifyContent: 'center', alignItems: 'center', padding: 16 },
  modal: { backgroundColor: '#fff', borderRadius: 28, width: '100%', maxHeight: '90%', overflow: 'hidden' },
  closeBtn: { position: 'absolute', top: 12, right: 12, zIndex: 10 },
  letterBanner: { height: 120, alignItems: 'center', justifyContent: 'center', flexDirection: 'row', gap: 16 },
  bigLetter: { fontSize: 72, fontWeight: '900', color: '#fff' },
  bigEmoji: { fontSize: 48 },
  islamicWord: { fontSize: 26, fontWeight: '900', color: COLORS.text, textAlign: 'center', marginTop: 16, paddingHorizontal: 16 },
  pronunciation: { fontSize: 14, color: COLORS.muted, textAlign: 'center', fontStyle: 'italic', marginTop: 2 },

  // Arabic RTL box
  arabicBox: { margin: 16, borderRadius: 16, borderWidth: 2, padding: 16, backgroundColor: '#F9FBE7' },
  arabicRow: { flexDirection: 'row-reverse', alignItems: 'center', justifyContent: 'center', gap: 10 },
  arabicText: {
    fontSize: 32, fontWeight: '900', color: '#1B5E20',
    textAlign: 'right',          // RTL text alignment
    writingDirection: 'rtl',     // RTL writing direction
    fontFamily: 'System',
  },
  arabicHint: { fontSize: 11, color: COLORS.muted, textAlign: 'center', marginTop: 6, fontStyle: 'italic' },

  // Meaning
  meaningBox: { marginHorizontal: 16, backgroundColor: '#E3F2FD', borderRadius: 14, padding: 14, marginBottom: 10 },
  meaningLabel: { fontSize: 13, fontWeight: '800', color: '#1565C0', marginBottom: 4 },
  meaningText: { fontSize: 15, color: '#0D47A1', fontWeight: '600' },

  // Picture
  pictureBox: { marginHorizontal: 16, backgroundColor: '#FFF8E1', borderRadius: 14, padding: 14, marginBottom: 10 },
  pictureLabel: { fontSize: 13, fontWeight: '800', color: '#E65100', marginBottom: 4 },
  pictureText: { fontSize: 13, color: '#BF360C', fontStyle: 'italic' },

  // Rhyme
  rhymeBtn: { marginHorizontal: 16, borderRadius: 16, padding: 14, flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 8, marginBottom: 10 },
  rhymeBtnText: { color: '#fff', fontWeight: '800', fontSize: 15 },
  rhymeCard: { marginHorizontal: 16, borderRadius: 16, borderWidth: 2, padding: 16, backgroundColor: '#F3E5F5', marginBottom: 10 },
  rhymeTitle: { fontSize: 13, fontWeight: '800', color: COLORS.text, marginBottom: 6 },
  rhymeText: { fontSize: 16, color: COLORS.text, lineHeight: 26, fontStyle: 'italic' },
  repeatTxt: { fontSize: 13, fontWeight: '700' },

  // Speak
  speakBtn: { marginHorizontal: 16, borderRadius: 16, padding: 14, flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 8, marginBottom: 4 },
  speakBtnText: { color: '#fff', fontWeight: '800', fontSize: 15 },
});
