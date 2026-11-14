// src/screens/GamesScreen.js
import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, useWindowDimensions } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { COLORS, SHADOWS } from '../utils/theme';


const GAMES = [
  { name: 'Flashcards', emoji: '🃏', desc: 'Swipe to learn words', color: '#7B1FA2', tab: 'English', screen: 'Flashcard', params: { category: 'animals', language: 'english' } },
  { name: 'Arabic Flashcards', emoji: '🌙', desc: 'Learn Arabic words', color: '#E65100', tab: 'Arabic', screen: 'Flashcard', params: { category: 'ar_animals', language: 'arabic' } },
  { name: 'English Quiz', emoji: '❓', desc: 'Test your English', color: '#1B5E20', tab: 'English', screen: 'Quiz', params: { category: 'english_animals', language: 'english' } },
  { name: 'Colors Quiz', emoji: '🎨', desc: 'Can you name the colors?', color: '#880E4F', tab: 'English', screen: 'Quiz', params: { category: 'english_colors', language: 'english' } },
  { name: 'Arabic Quiz', emoji: '⭐', desc: 'Test your Arabic', color: '#BF360C', tab: 'Arabic', screen: 'Quiz', params: { category: 'arabic_animals', language: 'arabic' } },
  { name: 'Match Words', emoji: '🧩', desc: 'Match emoji to word', color: '#0D47A1', tab: 'English', screen: 'Matching', params: { language: 'english' } },
  { name: 'Arabic Match', emoji: '🔡', desc: 'Match Arabic words', color: '#1A237E', tab: 'Arabic', screen: 'Matching', params: { language: 'arabic' } },
  { name: 'Read a Story', emoji: '📖', desc: 'Fun illustrated stories', color: '#4A148C', tab: 'English', screen: 'Story', params: { storyId: 's1' } },
];

export default function GamesScreen({ navigation }) {
  const { width } = useWindowDimensions();
  const cols = width >= 900 ? 4 : width >= 600 ? 3 : 2;
  const cardWidth = (width - 32 - 10 * (cols - 1)) / cols;
  return (
    <View style={{ flex: 1, backgroundColor: COLORS.bg }}>
      <LinearGradient colors={['#FF6B35', '#FF8A65']} style={{ paddingTop: 55, paddingBottom: 24, paddingHorizontal: 16 }}>
        <Text style={{ color: '#fff', fontSize: 26, fontWeight: '900' }}>🎮 Games</Text>
        <Text style={{ color: 'rgba(255,255,255,0.85)', fontSize: 14, marginTop: 4 }}>Learn while having fun!</Text>
      </LinearGradient>

      <ScrollView contentContainerStyle={{ padding: 16 }}>
        <Text style={{ fontSize: 16, fontWeight: '800', color: COLORS.text, marginBottom: 12 }}>🕹️ Choose a Game</Text>
        <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 10 }}>
          {GAMES.map((g, i) => (
            <TouchableOpacity
              key={i}
              accessibilityRole="button"
              accessibilityLabel={`${g.name}. ${g.desc}`}
              style={{ width: cardWidth, backgroundColor: g.color, borderRadius: 20, padding: 18, ...SHADOWS.small }}
              onPress={() => navigation.navigate(g.tab, { screen: g.screen, params: g.params })}
              activeOpacity={0.85}
            >
              <Text style={{ fontSize: 36, marginBottom: 8 }}>{g.emoji}</Text>
              <Text style={{ color: '#fff', fontSize: 15, fontWeight: '800' }}>{g.name}</Text>
              <Text style={{ color: 'rgba(255,255,255,0.8)', fontSize: 12, marginTop: 4 }}>{g.desc}</Text>
            </TouchableOpacity>
          ))}
        </View>
        <View style={{ height: 100 }} />
      </ScrollView>
    </View>
  );
}
