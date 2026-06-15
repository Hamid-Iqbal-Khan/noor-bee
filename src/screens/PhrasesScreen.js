// src/screens/PhrasesScreen.js
import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, FlatList } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { speakEnglish, speakArabic, speakRhyme, stopSpeech } from '../utils/tts';
import { COLORS, SHADOWS } from '../utils/theme';
import { PHRASES } from '../data/curriculum';

export default function PhrasesScreen({ route, navigation }) {
  const { language } = route.params || { language: 'english' };
  const isArabic = language === 'arabic';
  const data = isArabic ? PHRASES.arabic : PHRASES.english;
  const [activeCategory, setActiveCategory] = useState(0);
  const [speaking, setSpeaking] = useState(null);

  const speak = (text, idx) => {
    setSpeaking(idx);
    Speech.speak(text, {
      language: isArabic ? 'ar' : 'en',
      pitch: 1.1,
      rate: 0.75,
      onDone: () => setSpeaking(null),
    });
      };

  const currentCat = data[activeCategory];

  return (
    <View style={{ flex: 1, backgroundColor: COLORS.bg }}>
      <LinearGradient colors={isArabic ? ['#E65100', '#BF360C'] : ['#1A237E', '#283593']} style={{ paddingTop: 55, paddingBottom: 20, paddingHorizontal: 16 }}>
        <TouchableOpacity onPress={() => navigation.goBack()} style={{ marginBottom: 10 }}>
          <Ionicons name="arrow-back" size={24} color="#fff" />
        </TouchableOpacity>
        <Text style={{ color: '#fff', fontSize: 20, fontWeight: '900' }}>
          {isArabic ? '💬 عبارات مفيدة · Phrases' : '💬 Useful Phrases'}
        </Text>
      </LinearGradient>

      <ScrollView horizontal showsHorizontalScrollIndicator={false} style={{ maxHeight: 56 }} contentContainerStyle={{ padding: 8, gap: 8 }}>
        {data.map((cat, i) => (
          <TouchableOpacity
            key={i}
            style={{ paddingHorizontal: 16, paddingVertical: 8, borderRadius: 20, backgroundColor: i === activeCategory ? COLORS.primary : '#E0E0E0' }}
            onPress={() => setActiveCategory(i)}
          >
            <Text style={{ fontWeight: '700', color: i === activeCategory ? '#fff' : COLORS.text, fontSize: 13 }}>
              {isArabic ? cat.category : cat.category}
            </Text>
          </TouchableOpacity>
        ))}
      </ScrollView>

      <FlatList
        data={currentCat?.phrases || []}
        keyExtractor={(_, i) => i.toString()}
        contentContainerStyle={{ padding: 16 }}
        renderItem={({ item, index }) => (
          <TouchableOpacity
            style={{ backgroundColor: '#fff', borderRadius: 18, padding: 18, marginBottom: 10, flexDirection: 'row', alignItems: 'center', ...SHADOWS.small }}
            onPress={() => speak(item.text, index)}
            activeOpacity={0.85}
          >
            <Text style={{ fontSize: 28, marginRight: 12 }}>{item.emoji}</Text>
            <View style={{ flex: 1 }}>
              <Text style={isArabic ? { fontSize: 20, fontWeight: '900', color: COLORS.arabic, textAlign: 'right', writingDirection: 'rtl' } : { fontSize: 16, fontWeight: '800', color: COLORS.text }}>
                {item.text}
              </Text>
              {isArabic && item.transliteration && (
                <Text style={{ fontSize: 12, color: COLORS.muted, fontStyle: 'italic' }}>{item.transliteration}</Text>
              )}
              <Text style={{ fontSize: 12, color: COLORS.muted, marginTop: 4 }}>💬 {item.response}</Text>
            </View>
            <Ionicons
              name={speaking === index ? 'volume-high' : 'volume-medium-outline'}
              size={22}
              color={speaking === index ? COLORS.primary : COLORS.muted}
            />
          </TouchableOpacity>
        )}
        ListFooterComponent={<View style={{ height: 80 }} />}
      />
    </View>
  );
}
