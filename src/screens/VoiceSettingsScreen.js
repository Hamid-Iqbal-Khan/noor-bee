// src/screens/VoiceSettingsScreen.js
import React, { useState, useEffect } from 'react';
import {
  View, Text, StyleSheet, ScrollView, TouchableOpacity,
  TextInput, Alert, Linking,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { COLORS, SHADOWS } from '../utils/theme';
import { speakEnglish, speakArabic, speakRhyme } from '../utils/tts';

const VOICES = [
  {
    id: 'google',
    name: 'Google Cloud TTS',
    tag: 'BEST QUALITY',
    tagColor: '#1B5E20',
    description: 'Indian English male + native Arabic\nRequires free API key',
    englishVoice: 'en-IN-Wavenet-C (Indian Male)',
    arabicVoice: 'ar-XA-Wavenet-B (Native Arabic)',
    free: '1 million characters/month free',
    icon: '🎙️',
  },
  {
    id: 'device',
    name: 'Device Voice',
    tag: 'NO SETUP',
    tagColor: '#E65100',
    description: 'Uses your phone\'s built-in TTS\nQuality depends on device',
    englishVoice: 'en-IN (if available)',
    arabicVoice: 'ar (device Arabic)',
    free: 'Always free, works offline',
    icon: '📱',
  },
];

export default function VoiceSettingsScreen({ navigation }) {
  const [apiKey, setApiKey] = useState('');
  const [saved, setSaved] = useState(false);
  const [testing, setTesting] = useState(false);

  useEffect(() => {
    loadKey();
  }, []);

  const loadKey = async () => {
    const key = await AsyncStorage.getItem('google_tts_key');
    if (key) { setApiKey(key); setSaved(true); }
  };

  const saveKey = async () => {
    if (!apiKey.trim()) {
      Alert.alert('Empty Key', 'Please enter your Google Cloud TTS API key.');
      return;
    }
    await AsyncStorage.setItem('google_tts_key', apiKey.trim());
    setSaved(true);
    Alert.alert('✅ Saved!', 'Google TTS API key saved. Restart the app to apply the Indian English male voice and proper Arabic pronunciation.');
  };

  const removeKey = async () => {
    await AsyncStorage.removeItem('google_tts_key');
    setApiKey('');
    setSaved(false);
    Alert.alert('Removed', 'API key removed. Device voice will be used.');
  };

  const testEnglish = async () => {
    setTesting(true);
    await speakEnglish('A for Allah. Allah is the One God. Bismillah before we eat.', 0.82);
    setTimeout(() => setTesting(false), 3000);
  };

  const testArabic = async () => {
    setTesting(true);
    await speakArabic('بِسمِ اللّٰه الرَّحمٰنِ الرَّحيم', 0.70);
    setTimeout(() => setTesting(false), 3000);
  };

  const testRhyme = async () => {
    setTesting(true);
    await speakRhyme('A for Allah, kind and near,\nWe thank Allah every year.');
    setTimeout(() => setTesting(false), 4000);
  };

  return (
    <View style={styles.container}>
      <LinearGradient colors={['#1B3A6B', '#0F1F3D']} style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()} style={styles.backBtn}>
          <Ionicons name="arrow-back" size={24} color="#fff" />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>🎙️ Voice Settings</Text>
        <Text style={styles.headerSub}>Choose voice quality for English & Arabic</Text>
      </LinearGradient>

      <ScrollView contentContainerStyle={styles.content}>

        {/* Voice comparison */}
        {VOICES.map((v) => (
          <View key={v.id} style={styles.voiceCard}>
            <View style={styles.voiceHeader}>
              <Text style={styles.voiceIcon}>{v.icon}</Text>
              <View style={styles.voiceInfo}>
                <Text style={styles.voiceName}>{v.name}</Text>
                <View style={[styles.tag, { backgroundColor: v.tagColor }]}>
                  <Text style={styles.tagText}>{v.tag}</Text>
                </View>
              </View>
            </View>
            <Text style={styles.voiceDesc}>{v.description}</Text>
            <View style={styles.voiceDetails}>
              <Text style={styles.voiceDetail}>🔤 English: {v.englishVoice}</Text>
              <Text style={[styles.voiceDetail, { writingDirection: 'ltr' }]}>🌙 Arabic: {v.arabicVoice}</Text>
              <Text style={styles.voiceFree}>✅ {v.free}</Text>
            </View>
          </View>
        ))}

        {/* API Key section */}
        <View style={styles.apiCard}>
          <Text style={styles.apiTitle}>🔑 Google Cloud TTS API Key</Text>
          <Text style={styles.apiDesc}>
            Get a FREE API key in 3 minutes — gives you the Indian English male voice and native Arabic pronunciation.
          </Text>

          {/* Steps */}
          {[
            { n: '1', text: 'Go to Google Cloud Console (link below)' },
            { n: '2', text: 'Create a free account (no credit card needed for free tier)' },
            { n: '3', text: 'Enable "Cloud Text-to-Speech API"' },
            { n: '4', text: 'Go to Credentials → Create API Key' },
            { n: '5', text: 'Paste the key below and tap Save' },
          ].map((step) => (
            <View key={step.n} style={styles.step}>
              <View style={styles.stepNum}>
                <Text style={styles.stepNumText}>{step.n}</Text>
              </View>
              <Text style={styles.stepText}>{step.text}</Text>
            </View>
          ))}

          <TouchableOpacity
            style={styles.linkBtn}
            onPress={() => Linking.openURL('https://console.cloud.google.com/apis/library/texttospeech.googleapis.com')}
          >
            <Ionicons name="open-outline" size={16} color="#1565C0" />
            <Text style={styles.linkBtnText}>Open Google Cloud Console</Text>
          </TouchableOpacity>

          <TextInput
            style={styles.input}
            value={apiKey}
            onChangeText={setApiKey}
            placeholder="Paste your API key here (AIza...)"
            placeholderTextColor={COLORS.muted}
            autoCapitalize="none"
            autoCorrect={false}
            secureTextEntry={false}
          />

          <View style={styles.btnRow}>
            <TouchableOpacity
              style={[styles.saveBtn, { backgroundColor: saved ? COLORS.success : COLORS.primary }]}
              onPress={saveKey}
            >
              <Ionicons name={saved ? 'checkmark-circle' : 'save'} size={18} color="#fff" />
              <Text style={styles.saveBtnText}>{saved ? 'Key Saved ✓' : 'Save Key'}</Text>
            </TouchableOpacity>
            {saved && (
              <TouchableOpacity style={styles.removeBtn} onPress={removeKey}>
                <Ionicons name="trash-outline" size={18} color={COLORS.error} />
                <Text style={styles.removeBtnText}>Remove</Text>
              </TouchableOpacity>
            )}
          </View>
        </View>

        {/* Test voices */}
        <View style={styles.testCard}>
          <Text style={styles.testTitle}>🧪 Test Voices</Text>
          <Text style={styles.testDesc}>Tap to hear the current voice quality:</Text>

          <TouchableOpacity
            style={[styles.testBtn, { backgroundColor: '#1565C0' }]}
            onPress={testEnglish}
            disabled={testing}
          >
            <Ionicons name="volume-high" size={18} color="#fff" />
            <Text style={styles.testBtnText}>🔤 Test English (Indian Male)</Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[styles.testBtn, { backgroundColor: '#1B5E20' }]}
            onPress={testArabic}
            disabled={testing}
          >
            <Ionicons name="volume-high" size={18} color="#fff" />
            <Text style={styles.testBtnText}>🌙 Test Arabic (Native Voice)</Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[styles.testBtn, { backgroundColor: '#4A148C' }]}
            onPress={testRhyme}
            disabled={testing}
          >
            <Ionicons name="musical-notes" size={18} color="#fff" />
            <Text style={styles.testBtnText}>🎵 Test Rhyme (A for Allah…)</Text>
          </TouchableOpacity>

          {testing && (
            <View style={styles.playingBadge}>
              <Text style={styles.playingText}>🔊 Playing…</Text>
            </View>
          )}
        </View>

        <View style={{ height: 60 }} />
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: COLORS.bg },
  header: { paddingTop: 55, paddingBottom: 20, paddingHorizontal: 16 },
  backBtn: { marginBottom: 10 },
  headerTitle: { color: '#fff', fontSize: 20, fontWeight: '900' },
  headerSub: { color: 'rgba(255,255,255,0.8)', fontSize: 13, marginTop: 4 },
  content: { padding: 16 },

  voiceCard: { backgroundColor: '#fff', borderRadius: 18, padding: 16, marginBottom: 12, ...SHADOWS.small },
  voiceHeader: { flexDirection: 'row', alignItems: 'center', marginBottom: 10 },
  voiceIcon: { fontSize: 32, marginRight: 12 },
  voiceInfo: { flex: 1 },
  voiceName: { fontSize: 16, fontWeight: '800', color: COLORS.text },
  tag: { alignSelf: 'flex-start', borderRadius: 8, paddingHorizontal: 8, paddingVertical: 3, marginTop: 4 },
  tagText: { color: '#fff', fontSize: 10, fontWeight: '800' },
  voiceDesc: { fontSize: 13, color: COLORS.muted, marginBottom: 10, lineHeight: 20 },
  voiceDetails: { backgroundColor: '#F5F5F5', borderRadius: 12, padding: 12, gap: 4 },
  voiceDetail: { fontSize: 12, color: COLORS.text, fontWeight: '600' },
  voiceFree: { fontSize: 12, color: COLORS.success, fontWeight: '700', marginTop: 4 },

  apiCard: { backgroundColor: '#fff', borderRadius: 18, padding: 18, marginBottom: 12, ...SHADOWS.small },
  apiTitle: { fontSize: 16, fontWeight: '900', color: COLORS.text, marginBottom: 8 },
  apiDesc: { fontSize: 13, color: COLORS.muted, marginBottom: 14, lineHeight: 20 },
  step: { flexDirection: 'row', alignItems: 'flex-start', marginBottom: 10 },
  stepNum: { width: 24, height: 24, borderRadius: 12, backgroundColor: COLORS.primary, alignItems: 'center', justifyContent: 'center', marginRight: 10, marginTop: 1 },
  stepNumText: { color: '#fff', fontWeight: '900', fontSize: 12 },
  stepText: { flex: 1, fontSize: 13, color: COLORS.text, lineHeight: 20 },
  linkBtn: { flexDirection: 'row', alignItems: 'center', gap: 6, backgroundColor: '#E3F2FD', borderRadius: 12, padding: 12, marginVertical: 12 },
  linkBtnText: { color: '#1565C0', fontWeight: '700', fontSize: 13 },
  input: { borderWidth: 2, borderColor: COLORS.primary, borderRadius: 12, padding: 14, fontSize: 13, color: COLORS.text, marginBottom: 12, fontFamily: 'monospace' },
  btnRow: { flexDirection: 'row', gap: 10 },
  saveBtn: { flex: 1, flexDirection: 'row', alignItems: 'center', justifyContent: 'center', borderRadius: 14, padding: 14, gap: 8 },
  saveBtnText: { color: '#fff', fontWeight: '800', fontSize: 14 },
  removeBtn: { flexDirection: 'row', alignItems: 'center', borderRadius: 14, padding: 14, gap: 6, backgroundColor: '#FFEBEE' },
  removeBtnText: { color: COLORS.error, fontWeight: '700' },

  testCard: { backgroundColor: '#fff', borderRadius: 18, padding: 18, ...SHADOWS.small },
  testTitle: { fontSize: 16, fontWeight: '900', color: COLORS.text, marginBottom: 6 },
  testDesc: { fontSize: 13, color: COLORS.muted, marginBottom: 14 },
  testBtn: { flexDirection: 'row', alignItems: 'center', borderRadius: 14, padding: 14, marginBottom: 10, gap: 10 },
  testBtnText: { color: '#fff', fontWeight: '700', fontSize: 14 },
  playingBadge: { backgroundColor: '#E8F5E9', borderRadius: 12, padding: 10, alignItems: 'center' },
  playingText: { color: COLORS.success, fontWeight: '800', fontSize: 14 },
});
