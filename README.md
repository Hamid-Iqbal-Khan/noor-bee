# Noor Bee — Bilingual Islamic Learning App for Children
### English & Arabic · Ages 1–7 · React Native (Expo)

---

## 📱 What's Inside

| Feature | Details |
|---|---|
| Islamic A–Z | A–Z with Islamic words, Arabic, meanings and rhymes |
| Arabic Alphabet | 28 letters with isolated, initial, medial and final forms |
| Vocabulary | 6 English categories (51 words) + 5 Arabic categories (37 words) |
| Flashcards | Swipe-to-learn with flip animations (bilingual) |
| Quiz | MCQ with animations, stars, XP rewards |
| Stories | 3 illustrated stories (2 English, 1 Arabic) with TTS + comprehension quiz |
| Matching Game | Emoji-to-word with timer and scoring |
| Phrases | Greetings, classroom, daily phrases with tap-to-speak (Arabic phrases with English meanings) |
| Profile | Avatars, XP, levels, streak, 8 badges |
| Islamic Words | Bismillah, Alhamdulillah, Salam and more |

---

## 🚀 Quick Start (Local Preview)

### Prerequisites
- Node.js 18+
- npm or yarn
- Expo Go app on your phone (iOS or Android)

```bash
# 1. Install dependencies
npm install

# 2. Start the development server
npx expo start

# 3. Scan the QR code with:
#    - Android: Expo Go app
#    - iPhone: Camera app → tap the banner

# Or run in a browser (used for automated UAT):
npx expo start --web
```

### Optional: better voices (Google Cloud TTS)
By default the device's built-in voice is used. A parent/teacher can paste a Google Cloud
Text-to-Speech API key in **Profile → Voice Settings** (Google requires a billing account;
usage above the free quota is charged). For builds, `EXPO_PUBLIC_GOOGLE_TTS_API_KEY` can be set
instead. Any key on a device can be extracted — for a public release, proxy TTS through your own
backend or ship pre-recorded audio.

---

## 📦 Build APK (Android — shareable file)

### Step 1: Install EAS CLI
```bash
npm install -g eas-cli
```

### Step 2: Create a free Expo account
Go to https://expo.dev and sign up (free).

### Step 3: Log in and link project
```bash
eas login
eas init   # links your project and sets the projectId
```

### Step 4: Build the APK
```bash
# For a shareable APK (sideload / share link)
eas build --platform android --profile preview

# This takes ~10 minutes on Expo's cloud servers.
# You'll get a download link when done!
```

### Share the APK
After the build finishes:
- Download the `.apk` file from the Expo dashboard
- Share it via WhatsApp, email, Google Drive etc.
- Recipient taps it on Android → installs immediately
- (They may need to allow "Install from unknown sources" in Settings)

---

## 🏪 Publish to Google Play Store

```bash
# Build AAB (required by Play Store)
eas build --platform android --profile production

# Then submit directly
eas submit --platform android
```

**What you need:**
- Google Play Developer account ($25 one-time fee)
- App signing key (EAS handles this automatically)

---

## 🍎 Publish to Apple App Store

```bash
eas build --platform ios
eas submit --platform ios
```

**What you need:**
- Apple Developer account ($99/year)
- Mac or use EAS cloud build (no Mac needed!)

---

## 🏗️ Project Structure

```
noor-bee/
├── App.js                        ← Entry point
├── app.json                      ← Expo config (name, icons, bundle ID)
├── eas.json                      ← Build profiles (APK / AAB)
├── package.json                  ← Dependencies
├── babel.config.js               ← Babel config
└── src/
    ├── assets/
    │   ├── icon.png              ← App icon (1024×1024)
    │   ├── splash.png            ← Splash screen
    │   ├── adaptive-icon.png     ← Android adaptive icon
    │   └── favicon.png           ← Web favicon
    ├── data/
    │   └── curriculum.js         ← ALL lesson content
    ├── navigation/
    │   └── AppNavigator.js       ← Tab + Stack navigation
    ├── screens/
    │   ├── HomeScreen.js         ← Dashboard
    │   ├── EnglishScreen.js      ← English hub
    │   ├── ArabicScreen.js       ← Arabic hub
    │   ├── AlphabetScreen.js     ← A–Z / Arabic alphabet
    │   ├── VocabScreen.js        ← Vocabulary browser
    │   ├── FlashcardScreen.js    ← Swipe flashcards
    │   ├── QuizScreen.js         ← MCQ quiz
    │   ├── StoryScreen.js        ← Story reader + quiz
    │   ├── MatchingScreen.js     ← Matching game
    │   ├── GamesScreen.js        ← Games hub
    │   ├── PhrasesScreen.js      ← Phrases + TTS
    │   ├── ProfileScreen.js      ← Profile + badges
    │   ├── AgeSelectScreen.js    ← Age group curriculum
    │   ├── VoiceSettingsScreen.js← Google TTS key + voice test
    │   └── IslamicAlphabetScreen.js ← (unused duplicate of Islamic A–Z)
    └── utils/
        ├── theme.js              ← Colors, fonts, shadows
        ├── storage.js            ← AsyncStorage (progress, XP, streaks, badges)
        ├── progress.js           ← Activity/letter/word tracking, badge triggers
        └── tts.js                ← Speech (device voice or Google Cloud TTS)
```

---

## 🔧 Customizing the App

### Change app name / bundle ID
Edit `app.json`:
```json
"name": "Your App Name",
"ios": { "bundleIdentifier": "com.yourcompany.appname" },
"android": { "package": "com.yourcompany.appname" }
```

### Add more vocabulary
Edit `src/data/curriculum.js` — the `VOCAB_CATEGORIES` object.  
Each word follows this pattern:
```js
{ word: 'Cat', emoji: '🐱', pronunciation: 'kæt', sentence: 'The cat meows.', arabic: 'قطة', fun: 'Cats sleep 16 hours!' }
```

### Add more stories
Add to the `STORIES` array in `curriculum.js`:
```js
{
  id: 's4',
  title: 'My New Story',
  emoji: '🌟',
  level: 1,
  ageGroup: '4-5',
  language: 'english',
  pages: [
    { text: 'Once upon a time...', image: '🌟', bg: '#FFF3E0' },
  ],
  comprehension: [
    { question: 'What happened?', options: ['A','B','C','D'], answer: 0 },
  ],
}
```

### Add quiz questions
Add to `QUIZ_BANK` in `curriculum.js`:
```js
english_fruits: [
  { q: 'Which fruit is yellow?', options: ['Apple','Banana','Cherry','Grapes'], answer: 1, emoji: '🍌' },
]
```

---

## 🌐 Tech Stack

| Library | Purpose |
|---|---|
| Expo ~50 | Build toolchain, OTA updates |
| React Native 0.73 | Cross-platform UI |
| React Navigation 6 | Tabs + stack navigation |
| expo-speech | Text-to-speech (English + Arabic) |
| expo-haptics | Tactile feedback |
| expo-linear-gradient | Gradient backgrounds |
| AsyncStorage | Offline progress saving |
| EAS Build | Cloud builds → APK / AAB / IPA |

---

## ✅ Tested Features

- [x] English A–Z alphabet with tap-to-hear
- [x] Arabic alphabet with all positional forms
- [x] 10 English vocabulary categories
- [x] 5 Arabic vocabulary categories including Islamic words
- [x] Bilingual swipe flashcards
- [x] MCQ quiz with animations and star rating
- [x] Story reader with TTS and comprehension quiz
- [x] Emoji-to-word matching game with timer
- [x] Phrases with tap-to-speak
- [x] XP system, levels, and badge system
- [x] Progress saved offline (AsyncStorage)
- [x] Works on Android and iOS

---

## 📞 Support

If you have issues building:
1. Run `npx expo doctor` to check for problems
2. Check https://docs.expo.dev/build/introduction/
3. Join Expo Discord: https://discord.com/invite/expo
