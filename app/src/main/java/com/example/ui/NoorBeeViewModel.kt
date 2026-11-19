package com.example.ui

import kotlinx.coroutines.sync.withLock

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

// Word Model for kids activities
data class WordItem(
    val id: String,
    val english: String,
    val arabic: String,
    val englishPhonetic: String,
    val arabicPhonetic: String,
    val category: String, // "Alphabet", "Animals", "Fruits", "Objects"
    val emoji: String,
    val audioFilename: String
)

class NoorBeeViewModel(application: Application) : AndroidViewModel(application) {
    // Declared first: init {} below launches coroutines on Main.immediate that may use them.
    private val rewardMutex = kotlinx.coroutines.sync.Mutex()
    private val configMutex = kotlinx.coroutines.sync.Mutex()

    private val database = NoorBeeDatabase.getDatabase(application)
    private val repository = NoorBeeRepository(database.dao)

    // User Authentication state
    val activeUser: StateFlow<User?> = repository.activeUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _authState = MutableStateFlow<UiState<User>?>(null)
    val authState: StateFlow<UiState<User>?> = _authState.asStateFlow()

    fun register(
        email: String,
        phone: String,
        name: String,
        pass: String,
        deviceInfo: String,
        appVersion: String
    ) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            if (email.isBlank() || phone.isBlank() || name.isBlank() || pass.length < 6) {
                _authState.value = UiState.Error("Password must be at least 6 characters and all fields are mandatory.")
                return@launch
            }
            try {
                val existingEmail = repository.getUserByEmail(email)
                if (existingEmail != null) {
                    _authState.value = UiState.Error("An account with this email already exists.")
                    return@launch
                }
                val existingPhone = repository.getUserByPhone(phone)
                if (existingPhone != null) {
                    _authState.value = UiState.Error("An account with this phone number already exists.")
                    return@launch
                }
                val passHash = secureHash(pass)
                val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                val regDate = formatter.format(java.util.Date())
                val newUser = User(
                    email = email,
                    phoneNumber = phone,
                    passwordHash = passHash,
                    fullName = name,
                    registrationDateTime = regDate,
                    deviceInfo = deviceInfo,
                    appVersion = appVersion,
                    lastLoginTimestamp = System.currentTimeMillis()
                )
                repository.registerUser(newUser)
                repository.loginUser(email)
                _authState.value = UiState.Success(newUser)
            } catch (e: Exception) {
                _authState.value = UiState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun login(emailOrPhone: String, pass: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            if (emailOrPhone.isBlank() || pass.isBlank()) {
                _authState.value = UiState.Error("Fields cannot be empty")
                return@launch
            }
            try {
                val user = if (emailOrPhone.contains("@")) {
                    repository.getUserByEmail(emailOrPhone)
                } else {
                    repository.getUserByPhone(emailOrPhone)
                }
                if (user == null || user.passwordHash != secureHash(pass)) {
                    _authState.value = UiState.Error("Invalid credentials.")
                    return@launch
                }
                if (emailOrPhone.contains("@")) {
                    repository.loginUser(user.email)
                } else {
                    repository.loginUserByPhone(user.phoneNumber)
                }
                _authState.value = UiState.Success(user)
            } catch (e: Exception) {
                _authState.value = UiState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun resetPassword(emailOrPhone: String, newPass: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            if (emailOrPhone.isBlank() || newPass.length < 6) {
                _authState.value = UiState.Error("Password must be at least 6 characters.")
                return@launch
            }
            try {
                val user = if (emailOrPhone.contains("@")) {
                    repository.getUserByEmail(emailOrPhone)
                } else {
                    repository.getUserByPhone(emailOrPhone)
                }
                if (user == null) {
                    _authState.value = UiState.Error("User with this email or phone number not found.")
                    return@launch
                }
                val updatedUser = user.copy(
                    passwordHash = secureHash(newPass),
                    lastLoginTimestamp = System.currentTimeMillis()
                )
                repository.registerUser(updatedUser)
                _authState.value = UiState.Success(updatedUser)
            } catch (e: Exception) {
                _authState.value = UiState.Error(e.message ?: "Reset password failed")
            }
        }
    }

    fun resetAuthState() {
        _authState.value = null
    }

    fun logout() {
        viewModelScope.launch {
            repository.logoutUser()
            _authState.value = null
        }
    }

    private fun secureHash(input: String): String {
        return try {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            input.hashCode().toString()
        }
    }

    // Profiles state
    val allProfiles: StateFlow<List<ChildProfile>> = repository.allProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentProfile = MutableStateFlow<ChildProfile?>(null)
    val currentProfile: StateFlow<ChildProfile?> = _currentProfile.asStateFlow()

    // Config state
    val parentConfig: StateFlow<ParentConfig> = repository.parentConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ParentConfig())

    // Progress history for graphics
    val currentProfileProgress: LiveFlowList = currentProfile
        .flatMapLatest { profile ->
            if (profile != null) repository.getProgressForProfile(profile.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Unlocked outfits
    val currentProfileOutfits: StateFlow<List<UnlockedOutfit>> = currentProfile
        .flatMapLatest { profile ->
            if (profile != null) repository.getOutfitsForProfile(profile.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Interactive state - active vocabulary items
    val vocabularyList = listWithStaticContent()

    private val _selectedWord = MutableStateFlow<WordItem>(vocabularyList.first())
    val selectedWord: StateFlow<WordItem> = _selectedWord.asStateFlow()

    private val _sessionWords = MutableStateFlow<List<WordItem>>(emptyList())
    val sessionWords: StateFlow<List<WordItem>> = _sessionWords.asStateFlow()

    private val _sessionIndex = MutableStateFlow(0)
    val sessionIndex: StateFlow<Int> = _sessionIndex.asStateFlow()

    // Interactive Quiz State
    private val _quizQuestions = MutableStateFlow<List<WordItem>>(emptyList())
    val quizQuestions: StateFlow<List<WordItem>> = _quizQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _quizAnswers = MutableStateFlow<List<String>>(emptyList()) // multi choices
    val quizAnswers: StateFlow<List<String>> = _quizAnswers.asStateFlow()

    private val _quizAnswerChecked = MutableStateFlow<Boolean?>(null) // null = unanswered, true = correct, false = wrong
    val quizAnswerChecked: StateFlow<Boolean?> = _quizAnswerChecked.asStateFlow()

    private val _mascotColor = MutableStateFlow("default")
    val mascotColor: StateFlow<String> = _mascotColor.asStateFlow()

    fun updateMascotColor(color: String) {
        _mascotColor.value = color
    }

    // Selected customized stickers playground
    private val _stickersOnPlayground = MutableStateFlow<List<StickerNode>>(emptyList())
    val stickersOnPlayground: StateFlow<List<StickerNode>> = _stickersOnPlayground.asStateFlow()

    init {
        // Ensure a default offline user exists
        viewModelScope.launch {
            val currentActiveUser = repository.getActiveUserInstant()
            if (currentActiveUser == null) {
                val existingOfflineUser = repository.getUserByEmail("parent@noorbee.local")
                if (existingOfflineUser == null) {
                    val defaultUser = User(
                        email = "parent@noorbee.local",
                        phoneNumber = "+966500000000",
                        passwordHash = "offline",
                        fullName = "Parent",
                        registrationDateTime = "2026-06-29 00:00:00",
                        deviceInfo = "Offline Mode",
                        appVersion = "1.0.0",
                        lastLoginTimestamp = System.currentTimeMillis(),
                        isSessionActive = true
                    )
                    repository.registerUser(defaultUser)
                    repository.loginUser("parent@noorbee.local")
                } else {
                    repository.loginUser("parent@noorbee.local")
                }
            }
        }

        // Collect child profiles to dynamically load the active profile once created or available
        viewModelScope.launch {
            repository.allProfiles.collect { profiles ->
                if (profiles.isNotEmpty() && _currentProfile.value == null) {
                    _currentProfile.value = profiles.first()
                }
            }
        }
        // Populate initial parent configurations only on first run; never overwrite saved settings
        viewModelScope.launch {
            configMutex.withLock {
                if (repository.getParentConfigOrNull() == null) {
                    repository.saveParentConfig(ParentConfig())
                }
            }
        }
    }

    fun selectProfileFlow(profile: ChildProfile) {
        _currentProfile.value = profile
    }

    fun createChildProfile(name: String, gender: String, age: Int, level: Int, avatar: String) {
        viewModelScope.launch {
            val newProfile = ChildProfile(name = name, gender = gender, age = age, level = level, avatar = avatar)
            val pid = repository.createOrUpdateProfile(newProfile)
            val loaded = repository.getProfileById(pid)
            if (loaded != null) {
                _currentProfile.value = loaded
            }
        }
    }

    fun updateChildProfile(profile: ChildProfile) {
        viewModelScope.launch {
            repository.createOrUpdateProfile(profile)
            if (_currentProfile.value?.id == profile.id) {
                _currentProfile.value = profile
            }
        }
    }

    fun deleteChildProfile(profile: ChildProfile) {
        viewModelScope.launch {
            repository.deleteProfile(profile)
            if (_currentProfile.value?.id == profile.id) {
                // Pick another profile if possible, otherwise clear or default
                val remaining = repository.allProfiles.first()
                if (remaining.isNotEmpty()) {
                    _currentProfile.value = remaining.first()
                } else {
                    _currentProfile.value = null
                }
            }
        }
    }

    fun updateLanguage(lang: String) {
        val current = _currentProfile.value ?: return
        viewModelScope.launch {
            val updated = current.copy(currentLanguage = lang)
            repository.createOrUpdateProfile(updated)
            _currentProfile.value = updated
        }
    }

    fun updateLevel(level: Int) {
        val current = _currentProfile.value ?: return
        viewModelScope.launch {
            val updated = current.copy(level = level)
            repository.createOrUpdateProfile(updated)
            _currentProfile.value = updated
        }
    }

    fun updateAge(age: Int) {
        val current = _currentProfile.value ?: return
        viewModelScope.launch {
            val updated = current.copy(age = age)
            repository.createOrUpdateProfile(updated)
            _currentProfile.value = updated
        }
    }

    fun earnCoinsAndStars(coinsGained: Int, starsGained: Int, actionText: String = "Lesson Complete") {
        if (_currentProfile.value == null) return
        viewModelScope.launch {
          rewardMutex.withLock {
            // Read the latest profile inside the lock so rapid rewards are never lost
            val current = _currentProfile.value ?: return@withLock
            val updated = current.copy(
                coins = current.coins + coinsGained,
                stars = current.stars + starsGained,
                completedActivities = current.completedActivities + 1
            )
            repository.createOrUpdateProfile(updated)
            _currentProfile.value = updated

            // Insert statistics
            repository.addProgress(
                LearningProgress(
                    profileId = current.id,
                    dateString = getCurrentDateStr(),
                    topic = _selectedWord.value.category,
                    activityType = actionText,
                    score = starsGained,
                    durationMinutes = 0 // real duration not measured; do not show invented minutes
                )
            )
          }
        }
    }


    /** Read-modify-write of parent settings, serialised so quick changes don't overwrite each other. */
    private fun updateConfig(change: (ParentConfig) -> ParentConfig) {
        viewModelScope.launch {
            configMutex.withLock {
                repository.saveParentConfig(change(repository.getParentConfigInstant()))
            }
        }
    }

    // Wardrobe clothing customization
    fun unlockAndEquipOutfit(outfitId: String, cost: Int) {
        val current = _currentProfile.value ?: return
        if (current.coins < cost) return

        viewModelScope.launch {
            // Deduct coins
            val updated = current.copy(coins = current.coins - cost)
            repository.createOrUpdateProfile(updated)
            _currentProfile.value = updated

            // Add outfit and equip
            repository.unlockOutfitForProfile(current.id, outfitId)
            repository.equipOutfitForProfile(current.id, outfitId)
        }
    }

    fun equipOutfit(outfitId: String) {
        val current = _currentProfile.value ?: return
        viewModelScope.launch {
            repository.equipOutfitForProfile(current.id, outfitId)
        }
    }

    // App Preferences Parent parameters
    fun updateSpeechRate(rate: Float) {
        updateConfig { it.copy(speechRate = rate) }
    }

    fun toggleRTLMirror(enabled: Boolean) {
        updateConfig { it.copy(arabicRTLMirror = enabled) }
    }

    fun purchasePremiumTier() {
        updateConfig { it.copy(isPremium = true) }
    }

    fun addStickerToPlayground(emoji: String, x: Float, y: Float) {
        val list = _stickersOnPlayground.value.toMutableList()
        list.add(StickerNode(emoji = emoji, posX = x, posY = y))
        _stickersOnPlayground.value = list
    }

    fun clearStickers() {
        _stickersOnPlayground.value = emptyList()
    }

    // Learning Loop controllers
    fun selectWord(word: WordItem) {
        _selectedWord.value = word
    }

    fun getActiveVocabularyList(): List<WordItem> {
        val level = _currentProfile.value?.level ?: 1
        return when {
            level == 1 -> getLevelOneVocabularyList()
            level == 2 -> getLevelTwoVocabularyList()
            level == 3 -> getLevelThreeVocabularyList()
            level == 4 -> getAgeSixVocabularyList()
            level == 5 -> getAgeSevenVocabularyList()
            level >= 6 -> getAgeEightVocabularyList()
            else -> getLevelOneVocabularyList()
        }
    }

    fun selectNextRandomWord() {
        val currentItem = _selectedWord.value
        val list = getActiveVocabularyList().filter { it.category == currentItem.category }
        if (list.isNotEmpty()) {
            val next = list.random()
            _selectedWord.value = next
        }
    }

    fun startSessionForSubtopic(subtopicId: String) {
        val category = when {
            subtopicId.contains("Fruits") -> "Fruits"
            subtopicId.contains("Animals") -> "Animals"
            subtopicId.contains("Objects") -> "Objects"
            else -> "Fruits"
        }
        val wordsOfCategory = getActiveVocabularyList().filter { it.category.equals(category, ignoreCase = true) }
        _sessionWords.value = wordsOfCategory
        _sessionIndex.value = 0
        if (wordsOfCategory.isNotEmpty()) {
            _selectedWord.value = wordsOfCategory.first()
        }
    }

    fun nextSessionWord(): Boolean {
        val nextIdx = _sessionIndex.value + 1
        if (nextIdx < _sessionWords.value.size) {
            _sessionIndex.value = nextIdx
            _selectedWord.value = _sessionWords.value[nextIdx]
            return true
        }
        return false
    }

    // Generate dynamic Quiz Questions from vocab items
    fun startNewQuiz() {
        val list = getActiveVocabularyList().shuffled().take(20)
        _quizQuestions.value = list
        _currentQuestionIndex.value = 0
        _quizScore.value = 0
        _quizAnswerChecked.value = null
        loadQuestionChoices()
    }

    private fun loadQuestionChoices() {
        val index = _currentQuestionIndex.value
        val questions = _quizQuestions.value
        if (index < questions.size) {
            val correctWord = questions[index]
            // Pick 2 random wrong answers
            val wrongs = getActiveVocabularyList().filter { it.id != correctWord.id }.shuffled().take(2)
            val choices = (wrongs + correctWord).shuffled().map {
                if (currentProfile.value?.currentLanguage == "Arabic") it.english else it.arabic
            }
            _quizAnswers.value = choices
            _quizAnswerChecked.value = null
        }
    }

    fun getAgeSixVocabularyList(): List<WordItem> {
        return listOf(
            // --- Fruits (20 Complex Fruits for Age 6) ---
            WordItem("grapefruit", "Grapefruit", "ليمون هندي", "Grayp-froot", "Lay-moon hin-dee", "Fruits", "🍊", "grapefruit.mp3"),
            WordItem("blackberry", "Blackberry", "عليق", "Blak-ber-ee", "Ul-layq", "Fruits", "🫐", "blackberry.mp3"),
            WordItem("cranberry", "Cranberry", "توت بري", "Kran-ber-ee", "Toot bar-ree", "Fruits", "🍒", "cranberry.mp3"),
            WordItem("raspberry", "Raspberry", "توت العليق", "Raz-ber-ee", "Toot al-ul-layq", "Fruits", "🍓", "raspberry.mp3"),
            WordItem("avocado", "Avocado", "أفوكادو", "Ah-vo-kah-doh", "Ah-foo-kah-doo", "Fruits", "🥑", "avocado.mp3"),
            WordItem("pineapple", "Pineapple", "أناناس", "Pine-ap-ple", "Ah-nah-nas", "Fruits", "🍍", "pineapple.mp3"),
            WordItem("cantaloupe", "Cantaloupe", "شمام كَنْتَالُوب", "Kan-tah-lohp", "Sha-mam kan-tah-loob", "Fruits", "🍈", "cantaloupe.mp3"),
            WordItem("dragonfruit", "Dragonfruit", "فاكهة التنين", "Dra-gon-froot", "Fah-ki-hat at-tin-neen", "Fruits", "🐉", "dragonfruit.mp3"),
            WordItem("passionfruit", "Passionfruit", "فاكهة الآلام", "Pa-shon-froot", "Fah-ki-hat al-ah-lahm", "Fruits", "🍇", "passionfruit.mp3"),
            WordItem("tangerine", "Tangerine", "يوسفي", "Tan-jeh-reen", "Yoo-su-fee", "Fruits", "🍊", "tangerine.mp3"),
            WordItem("starfruit", "Starfruit", "فاكهة النجمة", "Star-froot", "Fah-ki-hat an-naj-mah", "Fruits", "⭐", "starfruit.mp3"),
            WordItem("persimmon", "Persimmon", "خرما", "Per-sim-on", "Khar-mah", "Fruits", "🍅", "persimmon.mp3"),
            WordItem("boysenberry", "Boysenberry", "توت بويزن", "Boy-zen-ber-ee", "Toot boy-zen", "Fruits", "🫐", "boysenberry.mp3"),
            WordItem("gooseberry", "Gooseberry", "عنب الثعلب", "Gooz-ber-ee", "Inab ath-tha'lab", "Fruits", "🍇", "gooseberry.mp3"),
            WordItem("elderberry", "Elderberry", "بيلسان", "El-der-ber-ee", "Bay-la-san", "Fruits", "🍒", "elderberry.mp3"),
            WordItem("mulberry", "Mulberry", "توت أسود", "Mul-ber-ee", "Toot as-wad", "Fruits", "🫐", "mulberry.mp3"),
            WordItem("honeydew", "Honeydew", "شمام عسلي", "Hun-ee-doo", "Sha-mam a-sa-lee", "Fruits", "🍈", "honeydew.mp3"),
            WordItem("nectarine", "Nectarine", "دراق أملس", "Nek-tah-reen", "Dur-raq am-las", "Fruits", "🍑", "nectarine.mp3"),
            WordItem("guava", "Guava", "جوافة", "Gwah-vah", "Ja-wah-fah", "Fruits", "🍏", "guava.mp3"),
            WordItem("papaya", "Papaya", "بابايا", "Pah-pah-yah", "Bah-bah-yah", "Fruits", "🥭", "papaya.mp3"),

            // --- Animals (20 Complex Animals for Age 6) ---
            WordItem("chameleon", "Chameleon", "حرباء", "Ka-mee-lee-on", "Hir-ba'", "Animals", "🦎", "chameleon.mp3"),
            WordItem("kangaroo", "Kangaroo", "كانغر", "Kan-gah-roo", "Kan-ghar", "Animals", "🦘", "kangaroo.mp3"),
            WordItem("crocodile", "Crocodile", "تمساح", "Krok-o-dial", "Tim-sah", "Animals", "🐊", "crocodile.mp3"),
            WordItem("hippopotamus", "Hippopotamus", "فرس النهر", "Hip-po-pot-a-mus", "Fa-ras an-nahr", "Animals", "🦛", "hippopotamus.mp3"),
            WordItem("chimpanzee", "Chimpanzee", "شيمبانزي", "Chim-pan-zee", "Shim-ban-zee", "Animals", "🐒", "chimpanzee.mp3"),
            WordItem("flamingo", "Flamingo", "نحام", "Flah-ming-go", "Nu-ham", "Animals", "🦩", "flamingo.mp3"),
            WordItem("platypus", "Platypus", "منقار البط", "Plat-ee-pus", "Min-qar al-bat", "Animals", "🦆", "platypus.mp3"),
            WordItem("cheetah", "Cheetah", "فهد", "Chee-tah", "Fahd", "Animals", "🐆", "cheetah.mp3"),
            WordItem("leopard", "Leopard", "نمر منقط", "Lep-ard", "Na-mir mu-naq-qat", "Animals", "🐆", "leopard.mp3"),
            WordItem("gorilla", "Gorilla", "غوريلا", "Go-ril-lah", "Gho-ree-la", "Animals", "🦍", "gorilla.mp3"),
            WordItem("jellyfish", "Jellyfish", "قنديل البحر", "Jel-ee-fish", "Qin-deel al-bahr", "Animals", "🪼", "jellyfish.mp3"),
            WordItem("starfish", "Starfish", "نجم البحر", "Star-fish", "Najm al-bahr", "Animals", "⭐", "starfish.mp3"),
            WordItem("woodpecker", "Woodpecker", "نقار الخشب", "Wood-pek-er", "Naq-qar al-kha-shab", "Animals", "🐦", "woodpecker.mp3"),
            WordItem("squirrel", "Squirrel", "سنجاب", "Skwir-rel", "Sin-jab", "Animals", "🐿️", "squirrel.mp3"),
            WordItem("hedgehog", "Hedgehog", "قنفذ", "Hej-hog", "Qun-futh", "Animals", "🦔", "hedgehog.mp3"),
            WordItem("dolphin", "Dolphin", "دلفين", "Dol-fin", "Dil-feen", "Animals", "🐬", "dolphin.mp3"),
            WordItem("octopus", "Octopus", "أخطبوط", "Ok-to-pus", "Akh-ta-boot", "Animals", "🐙", "octopus.mp3"),
            WordItem("penguin", "Penguin", "بطريق", "Pen-gwin", "Bat-reeq", "Animals", "🐧", "penguin.mp3"),
            WordItem("peacock", "Peacock", "طاووس", "Pee-kok", "Ta-woos", "Animals", "🦚", "peacock.mp3"),
            WordItem("rhinoceros", "Rhinoceros", "وحيد القرن", "Ry-nos-er-os", "Wa-heed al-qarn", "Animals", "🦏", "rhinoceros.mp3"),

            // --- Objects (20 Complex Objects for Age 6) ---
            WordItem("telescope", "Telescope", "مرقاب", "Tel-eh-skohp", "Mir-qab", "Objects", "🔭", "telescope.mp3"),
            WordItem("microscope", "Microscope", "مجهر", "My-kro-skohp", "Mij-har", "Objects", "🔬", "microscope.mp3"),
            WordItem("thermometer", "Thermometer", "مقياس الحرارة", "Ther-mom-e-ter", "Miq-yas al-ha-ra-rah", "Objects", "🌡️", "thermometer.mp3"),
            WordItem("binoculars", "Binoculars", "منظار", "Bih-nok-yoo-lerz", "Min-zar", "Objects", "🔭", "binoculars.mp3"),
            WordItem("calculator", "Calculator", "حاسبة", "Kal-kyoo-lay-tor", "Ha-si-bah", "Objects", "🧮", "calculator.mp3"),
            WordItem("helicopter", "Helicopter", "مروحية", "Hel-i-kop-ter", "Mir-wa-hee-yah", "Objects", "🚁", "helicopter.mp3"),
            WordItem("submarine", "Submarine", "غواصة", "Sub-ma-reen", "Gha-wa-sah", "Objects", "🚢", "submarine.mp3"),
            WordItem("compass", "Compass", "بوصلة", "Kum-pas", "Baw-sa-lah", "Objects", "🧭", "compass.mp3"),
            WordItem("hourglass", "Hourglass", "ساعة رملية", "Ow-er-glas", "Sa'ah ram-lee-yah", "Objects", "⏳", "hourglass.mp3"),
            WordItem("calendar", "Calendar", "تقويم", "Kal-en-der", "Taq-weem", "Objects", "📅", "calendar.mp3"),
            WordItem("keyboard", "Keyboard", "لوحة المفاتيح", "Kee-bord", "Law-hat al-ma-fa-teeh", "Objects", "⌨️", "keyboard.mp3"),
            WordItem("projector", "Projector", "جهاز عرض", "Pro-jek-tor", "Ji-haz ard", "Objects", "📽️", "projector.mp3"),
            WordItem("umbrella", "Umbrella", "مظلة", "Um-brel-lah", "Mi-dal-lah", "Objects", "☂️", "umbrella.mp3"),
            WordItem("backpack", "Backpack", "حقيبة ظهر", "Bak-pak", "Ha-qee-bat zahr", "Objects", "🎒", "backpack.mp3"),
            WordItem("headphones", "Headphones", "سماعات رأس", "Hed-fohnz", "Sam-ma-'at ra's", "Objects", "🎧", "headphones.mp3"),
            WordItem("flashlight", "Flashlight", "مصباح يدوي", "Flash-lite", "Mis-bah ya-da-wee", "Objects", "橫", "flashlight.mp3"),
            WordItem("globe", "Globe", "كرة أرضية", "Glohb", "Ku-rah ar-dee-yah", "Objects", "🌐", "globe.mp3"),
            WordItem("toothbrush", "Toothbrush", "فرشاة أسنان", "Tooth-brush", "Fur-shat as-nan", "Objects", "🪥", "toothbrush.mp3"),
            WordItem("screwdriver", "Screwdriver", "مفك براغي", "Skroo-dri-ver", "Mi-fak ba-ra-ghee", "Objects", "🪛", "screwdriver.mp3"),
            WordItem("dictionary", "Dictionary", "قاموس", "Dik-shon-er-ee", "Qa-moos", "Objects", "📕", "dictionary.mp3")
        )
    }

    fun getAgeSevenVocabularyList(): List<WordItem> {
        return listOf(
            // --- Fruits (Level 5 / Age 7 Advanced Vocabulary) ---
            WordItem("pomegranate", "Pomegranate", "رُمَّان", "Pom-gran-at", "Rum-man", "Fruits", "🍎", "pomegranate.mp3"),
            WordItem("fig", "Fig", "تِين", "Fig", "Teen", "Fruits", "🍇", "fig.mp3"),
            WordItem("date", "Date", "تَمْر", "Date", "Tamr", "Fruits", "🌴", "date.mp3"),
            WordItem("apricot", "Apricot", "مِشْمِش", "Ap-ri-kot", "Mish-mish", "Fruits", "🍑", "apricot.mp3"),
            WordItem("kiwi", "Kiwi", "كِيْوِي", "Kee-wee", "Kee-wee", "Fruits", "🥝", "kiwi.mp3"),
            WordItem("lychee", "Lychee", "لِيْتْشِي", "Lee-chee", "Lee-tchee", "Fruits", "🍒", "lychee.mp3"),
            WordItem("coconut", "Coconut", "جَوْزُ الْهِنْد", "Koh-koh-nut", "Jawz al-hind", "Fruits", "🥥", "coconut.mp3"),
            WordItem("plum", "Plum", "خَوْخ", "Plum", "Khawkh", "Fruits", "🍑", "plum.mp3"),
            WordItem("olive", "Olive", "زَيْتُون", "Ol-iv", "Zay-toon", "Fruits", "🫒", "olive.mp3"),
            WordItem("quince", "Quince", "سَفَرْجَل", "Kwins", "Sa-far-jal", "Fruits", "🍏", "quince.mp3"),

            // --- Animals (Level 5 / Age 7 Advanced Vocabulary) ---
            WordItem("eagle", "Eagle", "نَسْر", "Ee-gul", "Nasr", "Animals", "🦅", "eagle.mp3"),
            WordItem("falcon", "Falcon", "صَقْر", "Fal-kon", "Saqr", "Animals", "🦅", "falcon.mp3"),
            WordItem("koala", "Koala", "كُوالا", "Koh-ah-lah", "Koo-ah-lah", "Animals", "🐨", "koala.mp3"),
            WordItem("camel", "Camel", "جَمَل", "Kam-el", "Ja-mal", "Animals", "🐪", "camel.mp3"),
            WordItem("whale", "Whale", "حُوت", "Wayl", "Hoot", "Animals", "🐋", "whale.mp3"),
            WordItem("beaver", "Beaver", "قُنْدُس", "Bee-ver", "Qun-dus", "Animals", "🦫", "beaver.mp3"),
            WordItem("peacock", "Peacock", "طَاوُوس", "Pee-kok", "Ta-woos", "Animals", "🦚", "peacock.mp3"),
            WordItem("leopard", "Leopard", "فَهْدٌ مُنَقَّط", "Leh-pard", "Fahd mu-naq-qat", "Animals", "🐆", "leopard.mp3"),
            WordItem("gazelle", "Gazelle", "غَزَال", "Gah-zel", "Gha-zal", "Animals", "🦌", "gazelle.mp3"),
            WordItem("ostrich", "Ostrich", "نَعَامَة", "Os-trich", "Na'a-mah", "Animals", "🦤", "ostrich.mp3"),

            // --- Objects (Level 5 / Age 7 Advanced Vocabulary) ---
            WordItem("barometer", "Barometer", "مِقْيَاسُ الضَّغْط", "Bah-rom-e-ter", "Miq-yas ad-daght", "Objects", "🌡️", "barometer.mp3"),
            WordItem("telescope", "Telescope", "تِلِسْكُوب", "Tel-eh-skohp", "Te-les-koob", "Objects", "🔭", "telescope.mp3"),
            WordItem("stethoscope", "Stethoscope", "سَمَّاعَةُ الطَّبِيب", "Steth-o-skohp", "Sam-ma'at at-ta-beeb", "Objects", "🩺", "stethoscope.mp3"),
            WordItem("pendulum", "Pendulum", "بَنْدُول", "Pen-dyoo-lum", "Ban-dool", "Objects", "⏱️", "pendulum.mp3"),
            WordItem("generator", "Generator", "مُوَلِّد", "Jen-er-ay-tor", "Mu-wal-lid", "Objects", "⚙️", "generator.mp3"),
            WordItem("projector", "Projector", "عَارِضَة", "Pro-jek-tor", "A'a-ri-dah", "Objects", "📽️", "projector.mp3"),
            WordItem("compass", "Compass", "بوصلة", "Kum-pas", "Baw-sa-lah", "Objects", "🧭", "compass.mp3"),
            WordItem("thermometer", "Thermometer", "مِيزَانُ الْحَرَارَة", "Ther-mom-e-ter", "Mee-zan al-ha-ra-rah", "Objects", "🌡️", "thermometer.mp3"),
            WordItem("hourglass", "Hourglass", "سَاعَةٌ رَمْلِيَّة", "Ow-er-glas", "Sa'ah ram-lee-yah", "Objects", "⏳", "hourglass.mp3"),
            WordItem("microscope", "Microscope", "مِجْهَر", "My-kro-skohp", "Mij-har", "Objects", "🔬", "microscope.mp3")
        )
    }

    fun getAgeEightVocabularyList(): List<WordItem> {
        return listOf(
            // --- Fruits (Level 6 / Age 8 Expert Vocabulary) ---
            WordItem("mangosteen", "Mangosteen", "مَنْغُوسْتِين", "Man-go-steen", "Man-ghoos-teen", "Fruits", "🥭", "mangosteen.mp3"),
            WordItem("rambutan", "Rambutan", "رَامْبُوتَان", "Ram-boo-tan", "Ram-boo-tan", "Fruits", "🍇", "rambutan.mp3"),
            WordItem("durian", "Durian", "دُورْيَان", "Doo-ree-an", "Doo-ree-an", "Fruits", "🍈", "durian.mp3"),
            WordItem("persimmon", "Persimmon", "كَاكِي", "Per-sim-on", "Kah-kee", "Fruits", "🍑", "persimmon.mp3"),
            WordItem("papaya", "Papaya", "بَابَايَا", "Pah-pah-yah", "Bah-bah-yah", "Fruits", "🥭", "papaya.mp3"),
            WordItem("passionfruit", "Passionfruit", "فَاكِهَةُ الشَّغَف", "Pa-shon-froot", "Fah-ki-hat ash-sha-ghaf", "Fruits", "🍇", "passionfruit.mp3"),
            WordItem("dragonfruit", "Dragonfruit", "فَاكِهَةُ التِّنِّين", "Dra-gon-froot", "Fah-ki-hat at-tin-neen", "Fruits", "🐉", "dragonfruit.mp3"),
            WordItem("boysenberry", "Boysenberry", "تُوتُ بَوْيْزِنْ", "Boy-zen-ber-ee", "Toot boy-zen", "Fruits", "🫐", "boysenberry.mp3"),
            WordItem("chestnut", "Chestnut", "أَبُو فَرْوَة", "Chest-nut", "Aboo far-wah", "Fruits", "🌰", "chestnut.mp3"),
            WordItem("mulberry", "Mulberry", "تُوتٌ أَسْوَدُ", "Mul-ber-ee", "Toot as-wad", "Fruits", "🫐", "mulberry.mp3"),

            // --- Animals (Level 6 / Age 8 Expert Vocabulary) ---
            WordItem("platypus", "Platypus", "مِنْقَارُ الْبَطِّ", "Plat-ee-pus", "Min-qar al-bat", "Animals", "🦆", "platypus.mp3"),
            WordItem("chameleon", "Chameleon", "حِرْبَاءُ مُتَلَوِّنَة", "Ka-mee-lee-on", "Hir-ba' mu-ta-law-wi-nah", "Animals", "🦎", "chameleon.mp3"),
            WordItem("komodo", "Komodo Dragon", "تِنِّينُ كُومُودُو", "Ko-mo-doh dra-gon", "Tin-neen koo-moo-doo", "Animals", "🦎", "komodo.mp3"),
            WordItem("hippopotamus", "Hippopotamus", "فَرَسُ النَّهْرِ", "Hip-po-pot-a-mus", "Fa-ras an-nahr", "Animals", "🦛", "hippopotamus.mp3"),
            WordItem("cheetah", "Cheetah", "فَهْدٌ سَرِيعٌ", "Chee-tah", "Fahd sa-ree'", "Animals", "🐆", "cheetah.mp3"),
            WordItem("gorilla", "Gorilla", "غُورِيلَّا", "Go-ril-lah", "Gho-ree-la", "Animals", "🦍", "gorilla.mp3"),
            WordItem("chimpanzee", "Chimpanzee", "شِمْبَانْزِي", "Chim-pan-zee", "Shim-ban-zee", "Animals", "🐒", "chimpanzee.mp3"),
            WordItem("jellyfish", "Jellyfish", "قِنْدِيلُ الْبَحْرِ", "Jel-ee-fish", "Qin-deel al-bahr", "Animals", "🪼", "jellyfish.mp3"),
            WordItem("starfish", "Starfish", "نَجْمُ الْبَحْرِ", "Star-fish", "Najm al-bahr", "Animals", "⭐", "starfish.mp3"),
            WordItem("kangaroo", "Kangaroo", "كَانْغَر", "Kan-gah-roo", "Kan-ghar", "Animals", "🦘", "kangaroo.mp3"),

            // --- Objects (Level 6 / Age 8 Expert Vocabulary) ---
            WordItem("seismograph", "Seismograph", "مِقْيَاسُ الزَّلازِل", "Size-mo-graf", "Miq-yas az-za-la-zil", "Objects", "📊", "seismograph.mp3"),
            WordItem("gyroscope", "Gyroscope", "جَايْرُوسْكُوب", "Jy-ro-skohp", "Jay-roo-skoob", "Objects", "⚙️", "gyroscope.mp3"),
            WordItem("calorimeter", "Calorimeter", "مِقْيَاسُ الْحَرَارَة", "Kal-o-rim-e-ter", "Miq-yas al-ha-ra-rah", "Objects", "🧪", "calorimeter.mp3"),
            WordItem("speedometer", "Speedometer", "مِقْيَاسُ السُّرْعَة", "Spee-dom-e-ter", "Miq-yas as-sur-'ah", "Objects", "⏱️", "speedometer.mp3"),
            WordItem("anemometer", "Anemometer", "مِقْيَاسُ الرِّيَاح", "An-e-mom-e-ter", "Miq-yas ar-ri-yah", "Objects", "💨", "anemometer.mp3"),
            WordItem("syringe", "Syringe", "حُقْنَةٌ طِبِّيَّة", "Sih-rinj", "Huq-nah tib-bee-yah", "Objects", "💉", "syringe.mp3"),
            WordItem("calculator", "Calculator", "حَاسِبَةٌ إِلكترُونِيَّة", "Kal-kyoo-lay-tor", "Ha-si-bah", "Objects", "🧮", "calculator.mp3"),
            WordItem("telescope", "Telescope", "تِلِسْكُوبٌ فَلَكِيّ", "Tel-eh-skohp", "Te-les-koob", "Objects", "🔭", "telescope.mp3"),
            WordItem("microscope", "Microscope", "مِجْهَرٌ دَقِيق", "My-kro-skohp", "Mij-har", "Objects", "🔬", "microscope.mp3"),
            WordItem("globe", "Globe", "كُرَةٌ أَرْضِيَّة", "Glohb", "Ku-rah ar-dee-yah", "Objects", "🌐", "globe.mp3")
        )
    }

    fun submitQuizAnswer(selectedAns: String) {
        val index = _currentQuestionIndex.value
        val questions = _quizQuestions.value
        if (index < questions.size) {
            val correctWord = questions[index]
            val actualLangValue = if (currentProfile.value?.currentLanguage == "Arabic") correctWord.english else correctWord.arabic
            val isCorrect = selectedAns == actualLangValue
            _quizAnswerChecked.value = isCorrect
            if (isCorrect) {
                _quizScore.value = _quizScore.value + 10
                earnCoinsAndStars(coinsGained = 4, starsGained = 10, actionText = "Quiz Match")
            } else {
                earnCoinsAndStars(coinsGained = 1, starsGained = 2, actionText = "Quiz Match")
            }
        }
    }

    fun nextQuizQuestion(): Boolean { // Returns true if has more, false if finished
        val nextIdx = _currentQuestionIndex.value + 1
        if (nextIdx < _quizQuestions.value.size) {
            _currentQuestionIndex.value = nextIdx
            loadQuestionChoices()
            return true
        }
        return false
    }

    private fun getCurrentDateStr(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    fun getLevelOneVocabularyList(): List<WordItem> {
        return listOf(
            // --- Fruits (7 Extremely Simple Fruits for Level 1) ---
            WordItem("apple", "Apple", "تفاحة", "A-pul", "Tuf-faha", "Fruits", "🍎", "apple.mp3"),
            WordItem("banana", "Banana", "موز", "Bah-na-nah", "Mawz", "Fruits", "🍌", "banana.mp3"),
            WordItem("orange", "Orange", "برتقال", "Or-inj", "Bur-tu-qal", "Fruits", "🍊", "orange.mp3"),
            WordItem("grapes", "Grapes", "عنب", "Grayps", "Inab", "Fruits", "🍇", "grapes.mp3"),
            WordItem("lemon", "Lemon", "ليمون", "Lem-on", "Lay-moon", "Fruits", "🍋", "lemon.mp3"),
            WordItem("fig", "Fig", "تين", "Fig", "Teen", "Fruits", "🍇", "fig.mp3"),
            WordItem("peach", "Peach", "خوخ", "Peech", "Khawkh", "Fruits", "🍑", "peach.mp3"),

            // --- Animals (7 Extremely Simple Animals for Level 1) ---
            WordItem("cat", "Cat", "قطة", "Kat", "Qit-tah", "Animals", "🐱", "cat.mp3"),
            WordItem("dog", "Dog", "كلب", "Dog", "Kalb", "Animals", "🐶", "dog.mp3"),
            WordItem("bee", "Bee", "نحلة", "Bee", "Nah-lah", "Animals", "🐝", "bee.mp3"),
            WordItem("bird", "Bird", "طائر", "Burd", "Ta-er", "Animals", "🐦", "bird.mp3"),
            WordItem("cow", "Cow", "بقرة", "Kow", "Ba-qa-rah", "Animals", "🐮", "cow.mp3"),
            WordItem("fish", "Fish", "سمكة", "Fish", "Sa-ma-kah", "Animals", "🐟", "fish.mp3"),
            WordItem("duck", "Duck", "بطة", "Duk", "Bat-tah", "Animals", "🦆", "duck.mp3"),

            // --- Objects (7 Extremely Simple Objects for Level 1) ---
            WordItem("sun", "Sun", "شمس", "Suhn", "Shams", "Objects", "☀️", "sun.mp3"),
            WordItem("moon", "Moon", "قمر", "Moon", "Qa-mar", "Objects", "🌙", "moon.mp3"),
            WordItem("car", "Car", "سيارة", "Kahr", "Say-ya-rah", "Objects", "🚗", "car.mp3"),
            WordItem("book", "Book", "كتاب", "Buk", "Ki-tab", "Objects", "📖", "book.mp3"),
            WordItem("pen", "Pen", "قلم", "Pen", "Qa-lam", "Objects", "🖊️", "pen.mp3"),
            WordItem("key", "Key", "مفتاح", "Kee", "Mif-tah", "Objects", "🔑", "key.mp3"),
            WordItem("cup", "Cup", "كوب", "Kup", "Koob", "Objects", "🥛", "cup.mp3")
        )
    }

    fun getLevelTwoVocabularyList(): List<WordItem> {
        return listOf(
            // --- Fruits (7 Intermediate Fruits for Level 2) ---
            WordItem("pear", "Pear", "كمثرى", "Pair", "Ku-me-thra", "Fruits", "🍐", "pear.mp3"),
            WordItem("melon", "Melon", "شمام", "Mel-on", "Sha-mam", "Fruits", "🍈", "melon.mp3"),
            WordItem("cherry", "Cherry", "كرز", "Cher-ree", "Ka-raz", "Fruits", "🍒", "cherry.mp3"),
            WordItem("mango", "Mango", "مانجو", "Man-go", "Man-jo", "Fruits", "🥭", "mango.mp3"),
            WordItem("kiwi", "Kiwi", "كيوي", "Kee-wee", "Kee-wee", "Fruits", "🥝", "kiwi.mp3"),
            WordItem("dates", "Dates", "تمر", "Dayts", "Tamr", "Fruits", "🌴", "dates.mp3"),
            WordItem("plum", "Plum", "برقوق", "Plum", "Bar-qooq", "Fruits", "🍑", "plum.mp3"),

            // --- Animals (7 Intermediate Animals for Level 2) ---
            WordItem("lion", "Lion", "أسد", "Ly-on", "A-sad", "Animals", "🦁", "lion.mp3"),
            WordItem("rabbit", "Rabbit", "أرنب", "Rab-bit", "Ar-nab", "Animals", "🐰", "rabbit.mp3"),
            WordItem("fox", "Fox", "ثعلب", "Foks", "Tha'lab", "Animals", "🦊", "fox.mp3"),
            WordItem("sheep", "Sheep", "خروف", "Sheep", "Kha-roof", "Animals", "🐑", "sheep.mp3"),
            WordItem("bear", "Bear", "دب", "Bair", "Dubb", "Animals", "🐻", "bear.mp3"),
            WordItem("frog", "Frog", "ضفدع", "Frog", "Dif-da'", "Animals", "🐸", "frog.mp3"),
            WordItem("horse", "Horse", "حصان", "Hors", "Hi-san", "Animals", "🐴", "horse.mp3"),

            // --- Objects (7 Intermediate Objects for Level 2) ---
            WordItem("tree", "Tree", "شجرة", "Tree", "Sha-ja-rah", "Objects", "🌳", "tree.mp3"),
            WordItem("chair", "Chair", "كرسي", "Chair", "Kur-see", "Objects", "🪑", "chair.mp3"),
            WordItem("table", "Table", "طاولة", "Tay-bul", "Ta-wi-lah", "Objects", "🪵", "table.mp3"),
            WordItem("house", "House", "بيت", "Hows", "Bayt", "Objects", "🏠", "house.mp3"),
            WordItem("window", "Window", "نافذة", "Win-dow", "Na-fi-thah", "Objects", "🪟", "window.mp3"),
            WordItem("door", "Door", "باب", "Dor", "Bab", "Objects", "🚪", "door.mp3"),
            WordItem("bag", "Bag", "حقيبة", "Bag", "Ha-qee-bah", "Objects", "💼", "bag.mp3"),
            WordItem("shoe", "Shoe", "حذاء", "Shoo", "Hi-tha'", "Objects", "👟", "shoe.mp3")
        )
    }

    fun getLevelThreeVocabularyList(): List<WordItem> {
        return listOf(
            // --- Fruits (7 Advanced Intermediate Fruits for Level 3) ---
            WordItem("strawberry", "Strawberry", "فراولة", "Straw-ber-ree", "Fa-raw-lah", "Fruits", "🍓", "strawberry.mp3"),
            WordItem("watermelon", "Watermelon", "بطيخ", "Wah-ter-mel-on", "Bat-teekh", "Fruits", "🍉", "watermelon.mp3"),
            WordItem("pineapple", "Pineapple", "أناناس", "Pine-ap-pul", "A-na-nas", "Fruits", "🍍", "pineapple.mp3"),
            WordItem("blueberry", "Blueberry", "توت أزرق", "Bloo-ber-ree", "Toot az-raq", "Fruits", "🫐", "blueberry.mp3"),
            WordItem("pomegranate", "Pomegranate", "رمان", "Pom-gran-at", "Rum-man", "Fruits", "🍎", "pomegranate.mp3"),
            WordItem("apricot", "Apricot", "مشمش", "Ay-pri-kot", "Mish-mish", "Fruits", "🍑", "apricot.mp3"),
            WordItem("coconut", "Coconut", "جوز الهند", "Ko-ko-nut", "Jawz al-hind", "Fruits", "🥥", "coconut.mp3"),

            // --- Animals (7 Advanced Intermediate Animals for Level 3) ---
            WordItem("elephant", "Elephant", "فيل", "El-eh-fant", "Feel", "Animals", "🐘", "elephant.mp3"),
            WordItem("monkey", "Monkey", "قرد", "Mun-kee", "Qird", "Animals", "🐒", "monkey.mp3"),
            WordItem("giraffe", "Giraffe", "زرافة", "Ji-raf", "Za-ra-fah", "Animals", "🦒", "giraffe.mp3"),
            WordItem("camel", "Camel", "جمل", "Kam-el", "Ja-mal", "Animals", "🐫", "camel.mp3"),
            WordItem("turtle", "Turtle", "سلحفاة", "Tur-tul", "Su-lah-fah", "Animals", "🐢", "turtle.mp3"),
            WordItem("butterfly", "Butterfly", "فراشة", "But-ter-fly", "Fa-ra-shah", "Animals", "🦋", "butterfly.mp3"),
            WordItem("squirrel", "Squirrel", "سنجاب", "Skwir-rel", "Sin-jab", "Animals", "🐿️", "squirrel.mp3"),

            // --- Objects (7 Advanced Intermediate Objects for Level 3) ---
            WordItem("clock", "Clock", "ساعة", "Klok", "Sa'ah", "Objects", "⏰", "clock.mp3"),
            WordItem("phone", "Phone", "هاتف", "Fohn", "Ha-tif", "Objects", "📱", "phone.mp3"),
            WordItem("computer", "Computer", "حاسوب", "Kom-pew-ter", "Ha-soob", "Objects", "💻", "computer.mp3"),
            WordItem("window", "Window", "نافذة", "Win-dow", "Na-fi-thah", "Objects", "🪟", "window.mp3"),
            WordItem("spoon", "Spoon", "ملعقة", "Spoon", "Mil'aqah", "Objects", "🥄", "spoon.mp3"),
            WordItem("umbrella", "Umbrella", "مظلة", "Um-brel-lah", "Mi-dal-lah", "Objects", "☂️", "umbrella.mp3"),
            WordItem("lightbulb", "Lightbulb", "مصباح", "Lite-bulb", "Mis-bah", "Objects", "💡", "lightbulb.mp3")
        )
    }

    private fun listWithStaticContent(): List<WordItem> {
        return getLevelOneVocabularyList()
    }
}

typealias LiveFlowList = StateFlow<List<LearningProgress>>

data class StickerNode(
    val emoji: String,
    val posX: Float,
    val posY: Float
)
