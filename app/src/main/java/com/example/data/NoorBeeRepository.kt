package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoorBeeRepository(private val dao: NoorBeeDao) {

    val allProfiles: Flow<List<ChildProfile>> = dao.getAllProfilesFlow()
    val allProgress: Flow<List<LearningProgress>> = dao.getAllProgressFlow()
    val parentConfig: Flow<ParentConfig> = dao.getParentConfigFlow().map { it ?: ParentConfig() }

    suspend fun getProfileById(id: Int): ChildProfile? = dao.getProfileById(id)

    suspend fun createOrUpdateProfile(profile: ChildProfile): Int {
        val id = dao.insertProfile(profile)
        // Auto unlock basic outfit for new profiles
        if (profile.id == 0) {
            dao.insertOutfit(UnlockedOutfit(profileId = id.toInt(), outfitId = "classic_bee", isEquipped = true))
        }
        return id.toInt()
    }

    suspend fun deleteProfile(profile: ChildProfile) {
        dao.deleteProfile(profile)
        dao.deleteProgressByProfile(profile.id)
    }

    fun getProgressForProfile(profileId: Int): Flow<List<LearningProgress>> =
        dao.getProgressByProfileFlow(profileId)

    suspend fun addProgress(progress: LearningProgress) = dao.insertProgress(progress)

    fun getOutfitsForProfile(profileId: Int): Flow<List<UnlockedOutfit>> = dao.getOutfitsFlow(profileId)

    suspend fun unlockOutfitForProfile(profileId: Int, outfitId: String) {
        dao.insertOutfit(UnlockedOutfit(profileId = profileId, outfitId = outfitId, isEquipped = false))
    }

    suspend fun equipOutfitForProfile(profileId: Int, outfitId: String) {
        dao.equipOutfit(profileId, outfitId)
    }

    suspend fun saveParentConfig(config: ParentConfig) = dao.saveParentConfig(config)
    
    suspend fun getParentConfigInstant(): ParentConfig = dao.getParentConfig() ?: ParentConfig()

    // User Session Operations
    val activeUser: Flow<User?> = dao.getActiveUserFlow()
    val allUsers: Flow<List<User>> = dao.getAllUsersFlow()

    suspend fun getUserByEmail(email: String): User? = dao.getUserByEmail(email)
    suspend fun getUserByPhone(phone: String): User? = dao.getUserByPhone(phone)

    suspend fun registerUser(user: User) {
        dao.insertUser(user)
    }

    suspend fun loginUser(email: String) {
        dao.clearAllSessions()
        dao.setSessionActive(email, System.currentTimeMillis())
    }

    suspend fun loginUserByPhone(phone: String) {
        dao.clearAllSessions()
        dao.setSessionActiveByPhone(phone, System.currentTimeMillis())
    }

    suspend fun logoutUser() {
        dao.clearAllSessions()
    }

    suspend fun getActiveUserInstant(): User? = dao.getActiveUser()
}
