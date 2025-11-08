package com.borsahalal.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.borsahalal.data.database.entities.Profile
import com.borsahalal.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val allProfiles: StateFlow<List<Profile>> = profileRepository.getAllProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeProfile: StateFlow<Profile?> = profileRepository.getActiveProfileFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            profileRepository.ensureActiveProfile()
        }
    }

    fun createProfile(name: String, currency: String = "USD") {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val profileId = profileRepository.createProfile(name, currency)
                // If this is the first profile, make it active
                if (activeProfile.value == null) {
                    setActiveProfile(profileId)
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(profile: Profile) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                profileRepository.updateProfile(profile)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteProfile(profile: Profile) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                profileRepository.deleteProfile(profile)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setActiveProfile(profileId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                profileRepository.setActiveProfile(profileId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
