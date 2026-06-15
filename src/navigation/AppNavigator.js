// src/navigation/AppNavigator.js
import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createStackNavigator } from '@react-navigation/stack';
import { StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { COLORS } from '../utils/theme';

import HomeScreen from '../screens/HomeScreen';
import EnglishScreen from '../screens/EnglishScreen';
import ArabicScreen from '../screens/ArabicScreen';
import GamesScreen from '../screens/GamesScreen';
import ProfileScreen from '../screens/ProfileScreen';
import AlphabetScreen from '../screens/AlphabetScreen';
import VocabScreen from '../screens/VocabScreen';
import FlashcardScreen from '../screens/FlashcardScreen';
import QuizScreen from '../screens/QuizScreen';
import StoryScreen from '../screens/StoryScreen';
import MatchingScreen from '../screens/MatchingScreen';
import PhrasesScreen from '../screens/PhrasesScreen';
import AgeSelectScreen from '../screens/AgeSelectScreen';
import IslamicAlphabetScreen from '../screens/IslamicAlphabetScreen';
import VoiceSettingsScreen from '../screens/VoiceSettingsScreen';

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();

const commonScreens = (Stack) => (
  <>
    <Stack.Screen name="Alphabet" component={AlphabetScreen} />
    <Stack.Screen name="Vocab" component={VocabScreen} />
    <Stack.Screen name="Flashcard" component={FlashcardScreen} />
    <Stack.Screen name="Quiz" component={QuizScreen} />
    <Stack.Screen name="Story" component={StoryScreen} />
    <Stack.Screen name="Phrases" component={PhrasesScreen} />
    <Stack.Screen name="Matching" component={MatchingScreen} />
    <Stack.Screen name="IslamicAlphabet" component={IslamicAlphabetScreen} />
  </>
);

function EnglishStack() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="EnglishHome" component={EnglishScreen} />
      {commonScreens(Stack)}
    </Stack.Navigator>
  );
}

function ArabicStack() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="ArabicHome" component={ArabicScreen} />
      {commonScreens(Stack)}
    </Stack.Navigator>
  );
}

function HomeStack() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="HomeMain" component={HomeScreen} />
      <Stack.Screen name="AgeSelect" component={AgeSelectScreen} />
      <Stack.Screen name="VoiceSettings" component={VoiceSettingsScreen} />
    </Stack.Navigator>
  );
}

export default function AppNavigator() {
  return (
    <NavigationContainer>
      <Tab.Navigator
        screenOptions={{
          headerShown: false,
          tabBarStyle: styles.tabBar,
          tabBarActiveTintColor: COLORS.primary,
          tabBarInactiveTintColor: COLORS.muted,
          tabBarLabelStyle: styles.tabLabel,
        }}
      >
        <Tab.Screen name="Home" component={HomeStack}
          options={{ tabBarIcon: ({ focused, color }) => <Ionicons name={focused ? 'home' : 'home-outline'} size={24} color={color} /> }} />
        <Tab.Screen name="English" component={EnglishStack}
          options={{ tabBarIcon: ({ focused, color }) => <Ionicons name={focused ? 'book' : 'book-outline'} size={24} color={color} /> }} />
        <Tab.Screen name="Arabic" component={ArabicStack}
          options={{ tabBarLabel: 'عربي', tabBarIcon: ({ focused, color }) => <Ionicons name={focused ? 'moon' : 'moon-outline'} size={24} color={color} /> }} />
        <Tab.Screen name="Games" component={GamesScreen}
          options={{ tabBarIcon: ({ focused, color }) => <Ionicons name={focused ? 'game-controller' : 'game-controller-outline'} size={24} color={color} /> }} />
        <Tab.Screen name="Profile" component={ProfileScreen}
          options={{ tabBarIcon: ({ focused, color }) => <Ionicons name={focused ? 'person' : 'person-outline'} size={24} color={color} /> }} />
      </Tab.Navigator>
    </NavigationContainer>
  );
}

const styles = StyleSheet.create({
  tabBar: {
    backgroundColor: '#FFFFFF',
    borderTopWidth: 0,
    elevation: 20,
    shadowColor: '#6C3FF5',
    shadowOffset: { width: 0, height: -4 },
    shadowOpacity: 0.1,
    shadowRadius: 12,
    height: 65,
    paddingBottom: 8,
    paddingTop: 8,
  },
  tabLabel: { fontSize: 11, fontWeight: '700' },
});
