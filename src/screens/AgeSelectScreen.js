// src/screens/AgeSelectScreen.js
import React from 'react';
import {
  View, Text, StyleSheet, ScrollView, TouchableOpacity,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { COLORS, SHADOWS } from '../utils/theme';
import { Storage } from '../utils/storage';

const AGE_CURRICULUM = {
  'ag1': {
    description: 'Simple sounds, colors, and basic words with lots of repetition.',
    skills: ['Listening to sounds', 'Recognizing colors', 'Simple animal names', 'Body parts (head, hands, feet)', 'Basic Arabic greetings'],
    activities: ['Listen & repeat', 'Point to the picture', 'Simple matching'],
    dailyPlan: ['5 min: Animal sounds', 'Watch colorful alphabet'],
  },
  'ag2': {
    description: 'Short words, numbers 1-5, and interactive listening games.',
    skills: ['Numbers 1–5', 'Colors & shapes', 'Family words (mama, baba)', 'Greetings in English & Arabic', 'Common objects'],
    activities: ['Flashcards', 'Listen & point', 'Simple puzzles'],
    dailyPlan: ['5 min: Flashcards', '5 min: Colors game'],
  },
  'ag3': {
    description: 'Short sentences, stories, and vocabulary across many topics.',
    skills: ['Full alphabet A-Z', 'Numbers 1–10', 'Animals, fruits, food', 'Short sentences', 'Arabic alphabet intro'],
    activities: ['Alphabet quiz', 'Story listening', 'Match game'],
    dailyPlan: ['5 min: Alphabet', '5 min: Story', '5 min: Quiz'],
  },
  'ag4': {
    description: 'Reading readiness, phonics, and bilingual vocabulary building.',
    skills: ['Phonics basics', 'Sight words', 'Full Arabic alphabet', 'Simple reading', 'Conversation phrases'],
    activities: ['Phonics game', 'Word quiz', 'Story reading', 'Phrase practice'],
    dailyPlan: ['5 min: Alphabet', '5 min: Flashcards', '5 min: Story', '5 min: Quiz'],
  },
  'ag5': {
    description: 'Reading short books, writing basics, and Arabic script recognition.',
    skills: ['Reading short books', 'Writing simple words', 'Arabic positional forms', '100+ vocabulary words', 'Complex sentences'],
    activities: ['Read stories', 'Spelling quiz', 'Arabic writing practice', 'Matching game'],
    dailyPlan: ['5 min: Story', '10 min: Vocab', '5 min: Arabic', '5 min: Quiz'],
  },
  'ag6': {
    description: 'Fluency, comprehension, and conversational skills in both languages.',
    skills: ['Story comprehension', 'Creative writing', 'Arabic reading basics', 'Grammar foundations', '200+ words'],
    activities: ['All games', 'Creative stories', 'Comprehension quizzes', 'Conversation practice'],
    dailyPlan: ['10 min: Story', '10 min: Vocab', '5 min: Arabic', '5 min: Quiz'],
  },
};

export default function AgeSelectScreen({ route, navigation }) {
  const { ageGroup } = route.params;
  const curriculum = AGE_CURRICULUM[ageGroup.id] || AGE_CURRICULUM['ag4'];

  return (
    <View style={styles.container}>
      <LinearGradient colors={[ageGroup.color, ageGroup.color + 'BB']} style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()} style={styles.backBtn} accessibilityRole="button" accessibilityLabel="Go back">
          <Ionicons name="arrow-back" size={24} color="#fff" />
        </TouchableOpacity>
        <Text style={styles.emoji}>{ageGroup.emoji}</Text>
        <Text style={styles.title}>Ages {ageGroup.range}</Text>
        <Text style={styles.subtitle}>{ageGroup.label}</Text>
        <View style={styles.timeBadge}>
          <Ionicons name="time-outline" size={14} color="#fff" />
          <Text style={styles.timeText}>{ageGroup.dailyMins} min/day recommended</Text>
        </View>
      </LinearGradient>

      <ScrollView contentContainerStyle={styles.content}>
        <View style={styles.card}>
          <Text style={styles.cardTitle}>📝 About This Level</Text>
          <Text style={styles.cardText}>{curriculum.description}</Text>
        </View>

        <View style={styles.card}>
          <Text style={styles.cardTitle}>✅ Key Skills</Text>
          {curriculum.skills.map((skill, i) => (
            <View key={i} style={styles.skillRow}>
              <Text style={[styles.skillDot, { color: ageGroup.color }]}>●</Text>
              <Text style={styles.skillText}>{skill}</Text>
            </View>
          ))}
        </View>

        <View style={styles.card}>
          <Text style={styles.cardTitle}>🎮 Best Activities</Text>
          <View style={styles.tagsWrap}>
            {curriculum.activities.map((act, i) => (
              <View key={i} style={[styles.tag, { backgroundColor: ageGroup.color + '20', borderColor: ageGroup.color }]}>
                <Text style={[styles.tagText, { color: ageGroup.color }]}>{act}</Text>
              </View>
            ))}
          </View>
        </View>

        <View style={styles.card}>
          <Text style={styles.cardTitle}>📅 Sample Daily Plan</Text>
          {curriculum.dailyPlan.map((item, i) => (
            <View key={i} style={styles.planRow}>
              <Text style={styles.planNum}>{i + 1}</Text>
              <Text style={styles.planText}>{item}</Text>
            </View>
          ))}
        </View>

        <TouchableOpacity
          style={[styles.startBtn, { backgroundColor: ageGroup.color }]}
          accessibilityRole="button"
          onPress={async () => {
            // Remember the chosen age group so Profile reflects it.
            const p = (await Storage.getProfile()) || { name: 'Learner', avatar: '🦁' };
            await Storage.saveProfile({ ...p, age: ageGroup.range });
            navigation.navigate('English');
          }}
        >
          <Text style={styles.startBtnText}>🚀 Start Learning!</Text>
        </TouchableOpacity>
        <View style={{ height: 80 }} />
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: COLORS.bg },
  header: { paddingTop: 55, paddingBottom: 28, paddingHorizontal: 20, alignItems: 'center' },
  backBtn: { alignSelf: 'flex-start', marginBottom: 12 },
  emoji: { fontSize: 56 },
  title: { color: '#fff', fontSize: 26, fontWeight: '900', marginTop: 8 },
  subtitle: { color: 'rgba(255,255,255,0.9)', fontSize: 15, marginTop: 4 },
  timeBadge: { flexDirection: 'row', alignItems: 'center', gap: 6, backgroundColor: 'rgba(255,255,255,0.25)', borderRadius: 14, paddingHorizontal: 12, paddingVertical: 6, marginTop: 12 },
  timeText: { color: '#fff', fontSize: 12, fontWeight: '700' },
  content: { padding: 16 },
  card: { backgroundColor: '#fff', borderRadius: 18, padding: 18, marginBottom: 12, ...SHADOWS.small },
  cardTitle: { fontSize: 16, fontWeight: '800', color: COLORS.text, marginBottom: 10 },
  cardText: { fontSize: 14, color: COLORS.muted, lineHeight: 22 },
  skillRow: { flexDirection: 'row', alignItems: 'flex-start', marginBottom: 6 },
  skillDot: { fontSize: 16, marginRight: 8, marginTop: 1 },
  skillText: { fontSize: 14, color: COLORS.text, flex: 1 },
  tagsWrap: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },
  tag: { paddingHorizontal: 12, paddingVertical: 6, borderRadius: 12, borderWidth: 1.5 },
  tagText: { fontWeight: '700', fontSize: 13 },
  planRow: { flexDirection: 'row', alignItems: 'center', marginBottom: 8, gap: 10 },
  planNum: { width: 24, height: 24, borderRadius: 12, backgroundColor: COLORS.primary, color: '#fff', fontWeight: '900', fontSize: 13, textAlign: 'center', lineHeight: 24 },
  planText: { fontSize: 14, color: COLORS.text, flex: 1 },
  startBtn: { borderRadius: 20, padding: 18, alignItems: 'center', marginTop: 8, ...SHADOWS.medium },
  startBtnText: { color: '#fff', fontSize: 18, fontWeight: '900' },
});
