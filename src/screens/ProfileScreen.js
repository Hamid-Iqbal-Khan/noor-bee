// src/screens/ProfileScreen.js
import React, { useState, useCallback } from 'react';
import {
  View, Text, StyleSheet, ScrollView, TouchableOpacity, TextInput, Modal,
} from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import { COLORS, SHADOWS } from '../utils/theme';
import { Storage, ALL_BADGES } from '../utils/storage';
import { AGE_GROUPS } from '../data/curriculum';
import { confirmAction } from '../utils/progress';
import { setTTSApiKey } from '../utils/tts';

const AVATARS = ['🦁', '🐯', '🐻', '🦊', '🐼', '🐨', '🦄', '🐬', '🦋', '🐸'];

const AGE_OPTIONS = AGE_GROUPS.map(a => a.range);

export default function ProfileScreen({ navigation }) {
  const [profile, setProfile] = useState(null);
  const [xp, setXP] = useState(0);
  const [streak, setStreak] = useState(0);
  const [badges, setBadges] = useState([]);
  const [showEdit, setShowEdit] = useState(false);
  const [editName, setEditName] = useState('');
  const [editAvatar, setEditAvatar] = useState('🦁');
  const [editAge, setEditAge] = useState('');

  // Reload on every focus so XP/badges earned in other tabs show immediately.
  useFocusEffect(useCallback(() => { loadAll(); }, []));

  const loadAll = async () => {
    const p = await Storage.getProfile();
    const x = await Storage.getXP();
    const s = await Storage.getStreak();
    const b = await Storage.getBadges();
    setProfile(p || { name: 'Learner', avatar: '🦁', age: '4-5' });
    setXP(x);
    setStreak(s);
    setBadges(b);
    if (!b.includes('first_login')) {
      await Storage.awardBadge('first_login');
      setBadges([...b, 'first_login']);
      setXP(await Storage.getXP());
    }
  };

  const saveProfile = async () => {
    const p = { name: editName.trim().slice(0, 20) || 'Learner', avatar: editAvatar, age: editAge || '4-5' };
    await Storage.saveProfile(p);
    setProfile(p);
    setShowEdit(false);
  };

  const openEdit = () => {
    setEditName(profile?.name || '');
    setEditAvatar(profile?.avatar || '🦁');
    setEditAge(profile?.age || '4-5');
    setShowEdit(true);
  };

  const level = Math.floor(xp / 100) + 1;
  const progressPct = (xp % 100);

  return (
    <View style={{ flex: 1, backgroundColor: COLORS.bg }}>
      <ScrollView showsVerticalScrollIndicator={false}>
        <LinearGradient colors={['#6C3FF5', '#FF6B9D']} style={{ paddingTop: 55, paddingBottom: 40, alignItems: 'center', paddingHorizontal: 16 }}>
          <Text style={{ fontSize: 64 }}>{profile?.avatar || '🦁'}</Text>
          <Text style={{ color: '#fff', fontSize: 22, fontWeight: '900', marginTop: 8, maxWidth: '90%' }} numberOfLines={1}>{profile?.name || 'Learner'}</Text>
          <Text style={{ color: 'rgba(255,255,255,0.8)', fontSize: 13, marginTop: 2 }}>Age Group: {profile?.age || '4-5'}</Text>
          <TouchableOpacity
            style={{ marginTop: 12, backgroundColor: 'rgba(255,255,255,0.2)', borderRadius: 16, paddingHorizontal: 20, paddingVertical: 8, flexDirection: 'row', gap: 6, alignItems: 'center' }}
            onPress={openEdit}
          >
            <Ionicons name="pencil" size={16} color="#fff" />
            <Text style={{ color: '#fff', fontWeight: '700' }}>Edit Profile</Text>
          </TouchableOpacity>
        </LinearGradient>

        {/* Stats */}
        <View style={{ flexDirection: 'row', margin: 16, gap: 10 }}>
          {[
            { label: 'Level', value: level, emoji: '⭐', color: COLORS.primary },
            { label: 'XP Total', value: xp, emoji: '🏅', color: COLORS.secondary },
            { label: 'Streak', value: `${streak}🔥`, emoji: '📅', color: '#FF6D00' },
          ].map((s, i) => (
            <View key={i} style={{ flex: 1, backgroundColor: '#fff', borderRadius: 16, padding: 14, alignItems: 'center', ...SHADOWS.small, borderTopWidth: 3, borderTopColor: s.color }}>
              <Text style={{ fontSize: 22 }}>{s.emoji}</Text>
              <Text style={{ fontSize: 20, fontWeight: '900', color: s.color, marginTop: 4 }}>{s.value}</Text>
              <Text style={{ fontSize: 11, color: COLORS.muted }}>{s.label}</Text>
            </View>
          ))}
        </View>

        {/* XP Progress */}
        <View style={{ marginHorizontal: 16, backgroundColor: '#fff', borderRadius: 16, padding: 16, ...SHADOWS.small, marginBottom: 16 }}>
          <View style={{ flexDirection: 'row', justifyContent: 'space-between', marginBottom: 8 }}>
            <Text style={{ fontWeight: '700', color: COLORS.text }}>⭐ Level {level} Progress</Text>
            <Text style={{ color: COLORS.muted, fontSize: 13 }}>{progressPct}/100 XP</Text>
          </View>
          <View style={{ height: 10, backgroundColor: '#F0F0F0', borderRadius: 5, overflow: 'hidden' }}>
            <View style={{ width: `${progressPct}%`, height: '100%', backgroundColor: COLORS.primary, borderRadius: 5 }} />
          </View>
          <Text style={{ color: COLORS.muted, fontSize: 12, marginTop: 6 }}>{100 - progressPct} XP to Level {level + 1}</Text>
        </View>

        {/* Badges */}
        <View style={{ marginHorizontal: 16 }}>
          <Text style={{ fontSize: 17, fontWeight: '800', color: COLORS.text, marginBottom: 12 }}>🏅 Badges ({badges.length}/{ALL_BADGES.length})</Text>
          <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 10 }}>
            {ALL_BADGES.map((badge) => {
              const earned = badges.includes(badge.id);
              return (
                <View
                  key={badge.id}
                  style={{
                    width: '46%', backgroundColor: earned ? '#fff' : '#F5F5F5',
                    borderRadius: 16, padding: 14, alignItems: 'center',
                    opacity: earned ? 1 : 0.5, ...SHADOWS.small,
                    borderWidth: earned ? 2 : 0, borderColor: COLORS.accent,
                  }}
                >
                  <Text style={{ fontSize: 32, marginBottom: 4 }}>{badge.emoji}</Text>
                  <Text style={{ fontSize: 13, fontWeight: '800', color: COLORS.text, textAlign: 'center' }}>{badge.name}</Text>
                  <Text style={{ fontSize: 10, color: COLORS.muted, textAlign: 'center', marginTop: 2 }}>{badge.desc}</Text>
                  {earned && <Text style={{ fontSize: 11, color: COLORS.accent, fontWeight: '700', marginTop: 4 }}>+{badge.xp} XP</Text>}
                  {!earned && <Text style={{ fontSize: 10, color: COLORS.muted, marginTop: 4 }}>🔒 Locked</Text>}
                </View>
              );
            })}
          </View>
        </View>

        {/* Voice Settings */}
        <TouchableOpacity
          style={{ margin: 16, marginBottom: 0, backgroundColor: '#E3F2FD', borderRadius: 16, padding: 14, flexDirection: 'row', alignItems: 'center', gap: 10 }}
          onPress={() => navigation.navigate('VoiceSettings')}
        >
          <Text style={{ fontSize: 20 }}>🎙️</Text>
          <View style={{ flex: 1 }}>
            <Text style={{ fontWeight: '800', color: '#1565C0' }}>Voice Settings</Text>
            <Text style={{ fontSize: 12, color: COLORS.muted }}>Indian English · Arabic pronunciation</Text>
          </View>
          <Ionicons name="chevron-forward" size={20} color="#1565C0" />
        </TouchableOpacity>

        <TouchableOpacity
          style={{ margin: 16, backgroundColor: '#FFEBEE', borderRadius: 16, padding: 14, alignItems: 'center', marginTop: 20 }}
          accessibilityRole="button"
          onPress={() => confirmAction('Reset Progress', 'Are you sure? This will delete all progress, badges and the saved voice key.',
            async () => { await Storage.clearAll(); setTTSApiKey(''); loadAll(); }, 'Reset')}
        >
          <Text style={{ color: COLORS.error, fontWeight: '700' }}>🗑️ Reset Progress</Text>
        </TouchableOpacity>
        <View style={{ height: 100 }} />
      </ScrollView>

      {/* Edit Modal */}
      <Modal visible={showEdit} transparent animationType="slide" onRequestClose={() => setShowEdit(false)}>
        <View style={{ flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'flex-end' }}>
          <View style={{ backgroundColor: '#fff', borderTopLeftRadius: 28, borderTopRightRadius: 28, padding: 28 }}>
            <Text style={{ fontSize: 20, fontWeight: '900', color: COLORS.text, marginBottom: 20 }}>✏️ Edit Profile</Text>
            <Text style={{ fontWeight: '700', color: COLORS.muted, marginBottom: 8 }}>Name</Text>
            <TextInput
              value={editName}
              onChangeText={setEditName}
              style={{ borderWidth: 2, borderColor: COLORS.primary, borderRadius: 12, padding: 12, fontSize: 16, marginBottom: 16 }}
              placeholder="Your name"
              maxLength={20}
              accessibilityLabel="Name"
            />
            <Text style={{ fontWeight: '700', color: COLORS.muted, marginBottom: 8 }}>Age Group</Text>
            <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 8, marginBottom: 16 }}>
              {AGE_OPTIONS.map(r => (
                <TouchableOpacity
                  key={r}
                  accessibilityRole="button"
                  accessibilityLabel={`Age ${r}`}
                  accessibilityState={{ selected: editAge === r }}
                  style={{ paddingHorizontal: 14, paddingVertical: 8, borderRadius: 14, backgroundColor: editAge === r ? COLORS.primary : '#F5F5F5' }}
                  onPress={() => setEditAge(r)}
                >
                  <Text style={{ fontWeight: '700', color: editAge === r ? '#fff' : COLORS.text }}>{r}</Text>
                </TouchableOpacity>
              ))}
            </View>
            <Text style={{ fontWeight: '700', color: COLORS.muted, marginBottom: 8 }}>Choose Avatar</Text>
            <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 8, marginBottom: 16 }}>
              {AVATARS.map(av => (
                <TouchableOpacity
                  key={av}
                  style={{ width: 44, height: 44, borderRadius: 22, backgroundColor: editAvatar === av ? COLORS.primary + '30' : '#F5F5F5', alignItems: 'center', justifyContent: 'center', borderWidth: editAvatar === av ? 2 : 0, borderColor: COLORS.primary }}
                  onPress={() => setEditAvatar(av)}
                  accessibilityRole="button"
                  accessibilityLabel={`Avatar ${av}`}
                >
                  <Text style={{ fontSize: 24 }}>{av}</Text>
                </TouchableOpacity>
              ))}
            </View>
            <View style={{ flexDirection: 'row', gap: 10 }}>
              <TouchableOpacity style={{ flex: 1, backgroundColor: '#F5F5F5', borderRadius: 16, padding: 14, alignItems: 'center' }} onPress={() => setShowEdit(false)}>
                <Text style={{ fontWeight: '700', color: COLORS.muted }}>Cancel</Text>
              </TouchableOpacity>
              <TouchableOpacity style={{ flex: 1, backgroundColor: COLORS.primary, borderRadius: 16, padding: 14, alignItems: 'center' }} onPress={saveProfile}>
                <Text style={{ fontWeight: '800', color: '#fff' }}>Save</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </View>
  );
}
