package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "child_profiles")
data class ChildProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val gender: String = "Male", // "Male" or "Female"
    val level: Int = 1, // e.g. 1 to 6 representing different skill levels
    val age: Int = 5, // actual child age (independent of level)
    val avatar: String = "bee_default", // e.g. "bee_default", "bee_painter", "bee_space", "bee_chef"
    val stars: Int = 0,
    val coins: Int = 0,
    val currentLanguage: String = "English", // "English" or "Arabic"
    val completedActivities: Int = 0
)

@Entity(tableName = "learning_progress")
data class LearningProgress(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val profileId: Int,
    val dateString: String, // e.g. "2026-06-20"
    val topic: String, // e.g. "ABC Alphabet", "Basic Words", "Colors & Shapes", "Common Phrases"
    val activityType: String, // e.g. "Match", "Listening", "Speaking", "Quiz"
    val score: Int,
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "unlocked_outfits")
data class UnlockedOutfit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val profileId: Int,
    val outfitId: String, // e.g. "classic_bee", "super_bee", "space_helmet", "royal_crown", "painter_hat", "chef_hat"
    val isEquipped: Boolean = false
)

@Entity(tableName = "parent_configurations")
data class ParentConfig(
    @PrimaryKey val configKey: String = "globals",
    val speechRate: Float = 1.0f,
    val musicEnabled: Boolean = true,
    val soundsEnabled: Boolean = true,
    val arabicRTLMirror: Boolean = false,
    val isPremium: Boolean = false,
    val parentPin: String = "1234"
)

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val phoneNumber: String = "",
    val passwordHash: String,
    val fullName: String = "",
    val registrationDateTime: String = "",
    val deviceInfo: String = "",
    val appVersion: String = "1.0.0",
    val lastLoginTimestamp: Long = 0L,
    val isSessionActive: Boolean = false
)

@Dao
interface NoorBeeDao {
    // User Authentication
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): User?

    @Query("SELECT * FROM users ORDER BY lastLoginTimestamp DESC")
    fun getAllUsersFlow(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("UPDATE users SET isSessionActive = 0")
    suspend fun clearAllSessions()

    @Query("UPDATE users SET isSessionActive = 1, lastLoginTimestamp = :timestamp WHERE email = :email")
    suspend fun setSessionActive(email: String, timestamp: Long)

    @Query("UPDATE users SET isSessionActive = 1, lastLoginTimestamp = :timestamp WHERE phoneNumber = :phone")
    suspend fun setSessionActiveByPhone(phone: String, timestamp: Long)

    @Query("SELECT * FROM users WHERE isSessionActive = 1 LIMIT 1")
    fun getActiveUserFlow(): Flow<User?>

    @Query("SELECT * FROM users WHERE isSessionActive = 1 LIMIT 1")
    suspend fun getActiveUser(): User?

    // Child Profiles
    @Query("SELECT * FROM child_profiles")
    fun getAllProfilesFlow(): Flow<List<ChildProfile>>

    @Query("SELECT * FROM child_profiles WHERE id = :id")
    suspend fun getProfileById(id: Int): ChildProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ChildProfile): Long

    @Update
    suspend fun updateProfile(profile: ChildProfile)

    @Delete
    suspend fun deleteProfile(profile: ChildProfile)

    // Learning Progress
    @Query("SELECT * FROM learning_progress WHERE profileId = :profileId ORDER BY timestamp DESC")
    fun getProgressByProfileFlow(profileId: Int): Flow<List<LearningProgress>>

    @Query("SELECT * FROM learning_progress ORDER BY timestamp DESC")
    fun getAllProgressFlow(): Flow<List<LearningProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: LearningProgress)

    @Query("DELETE FROM learning_progress WHERE profileId = :profileId")
    suspend fun deleteProgressByProfile(profileId: Int)

    // Outfits
    @Query("SELECT * FROM unlocked_outfits WHERE profileId = :profileId")
    fun getOutfitsFlow(profileId: Int): Flow<List<UnlockedOutfit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: UnlockedOutfit)

    @Query("UPDATE unlocked_outfits SET isEquipped = (outfitId = :outfitId) WHERE profileId = :profileId")
    suspend fun equipOutfit(profileId: Int, outfitId: String)

    // Parent Configs
    @Query("SELECT * FROM parent_configurations WHERE configKey = 'globals'")
    fun getParentConfigFlow(): Flow<ParentConfig?>

    @Query("SELECT * FROM parent_configurations WHERE configKey = 'globals'")
    suspend fun getParentConfig(): ParentConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveParentConfig(config: ParentConfig)
}

@Database(
    entities = [ChildProfile::class, LearningProgress::class, UnlockedOutfit::class, ParentConfig::class, User::class],
    version = 5,
    exportSchema = false
)
abstract class NoorBeeDatabase : RoomDatabase() {
    abstract val dao: NoorBeeDao

    companion object {
        @Volatile
        private var INSTANCE: NoorBeeDatabase? = null

        fun getDatabase(context: Context): NoorBeeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoorBeeDatabase::class.java,
                    "noor_bee_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
