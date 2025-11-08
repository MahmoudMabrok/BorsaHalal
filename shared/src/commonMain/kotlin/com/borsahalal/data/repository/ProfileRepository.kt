package com.borsahalal.data.repository

import com.borsahalal.data.database.dao.ProfileDao
import com.borsahalal.data.database.entities.Profile
import kotlinx.coroutines.flow.Flow

class ProfileRepository(
    private val profileDao: ProfileDao
) {
    fun getAllProfiles(): Flow<List<Profile>> = profileDao.getAllProfiles()

    suspend fun getProfileById(profileId: Long): Profile? =
        profileDao.getProfileById(profileId)

    fun getProfileByIdFlow(profileId: Long): Flow<Profile?> =
        profileDao.getProfileByIdFlow(profileId)

    suspend fun getActiveProfile(): Profile? = profileDao.getActiveProfile()

    fun getActiveProfileFlow(): Flow<Profile?> = profileDao.getActiveProfileFlow()

    suspend fun createProfile(name: String, currency: String = "USD"): Long {
        val profile = Profile(
            name = name,
            currency = currency,
            createdAt = System.currentTimeMillis(),
            isActive = false
        )
        return profileDao.insertProfile(profile)
    }

    suspend fun updateProfile(profile: Profile) {
        profileDao.updateProfile(profile)
    }

    suspend fun deleteProfile(profile: Profile) {
        profileDao.deleteProfile(profile)
    }

    suspend fun setActiveProfile(profileId: Long) {
        // Deactivate all profiles first
        profileDao.deactivateAllProfiles()
        // Then activate the selected profile
        profileDao.setActiveProfile(profileId)
    }

    suspend fun getProfileCount(): Int = profileDao.getProfileCount()

    suspend fun hasProfiles(): Boolean = getProfileCount() > 0

    suspend fun ensureActiveProfile() {
        if (getActiveProfile() == null) {
            val profiles = getAllProfiles()
            // This is a flow, so we need to collect it first
            // For simplicity in this case, we'll create a default profile if none exist
            if (getProfileCount() == 0) {
                val profileId = createProfile("Default Portfolio")
                setActiveProfile(profileId)
            }
        }
    }
}
