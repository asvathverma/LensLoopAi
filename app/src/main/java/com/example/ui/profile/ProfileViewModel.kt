package com.example.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.PrivacySettings
import com.example.models.ProfileCategory
import com.example.models.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _profile = MutableStateFlow(
        UserProfile(
            id = "1",
            username = "new_user",
            displayName = "New User",
            bio = "Hello, world! Check out @lensloop and #photography",
            externalLink = "https://lensloop.com",
            category = ProfileCategory.PERSONAL,
            accentColor = 0xFF6750A4.toInt(),
            layoutOption = 0,
            avatarUrl = null,
            profileFrame = null,
            bioLinks = listOf(
                com.example.models.LinkItem("1", "My Portfolio", "https://portfolio.com"),
                com.example.models.LinkItem("2", "My Store", "https://store.com")
            )
        )
    )
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val _privacySettings = MutableStateFlow(
        PrivacySettings(
            isPrivate = false,
            hiddenFromStoryUserIds = emptyList(),
            showActivityStatus = true,
            commentFilteringEnabled = false,
            requireTagApproval = false,
            blockedUserIds = emptyList()
        )
    )
    val privacySettings: StateFlow<PrivacySettings> = _privacySettings.asStateFlow()

    private val _usernameAvailability = MutableStateFlow<Boolean?>(null)
    val usernameAvailability: StateFlow<Boolean?> = _usernameAvailability.asStateFlow()

    private val reservedUsernames = setOf("admin", "lensloop", "support", "help", "system")

    private val _highlights = MutableStateFlow<List<com.example.models.StoryHighlight>>(
        listOf(
            com.example.models.StoryHighlight("h1", "Travel", null),
            com.example.models.StoryHighlight("h2", "Food", null),
            com.example.models.StoryHighlight("h3", "OOTD", null)
        )
    )
    val highlights: StateFlow<List<com.example.models.StoryHighlight>> = _highlights.asStateFlow()

    fun checkUsername(username: String) {
        viewModelScope.launch {
            if (username.isEmpty()) {
                _usernameAvailability.value = null
                return@launch
            }
            delay(500)
            val isFormatValid = username.matches(Regex("^[a-zA-Z0-9_]+$"))
            if (!isFormatValid || reservedUsernames.contains(username.lowercase())) {
                _usernameAvailability.value = false
            } else {
                _usernameAvailability.value = true
            }
        }
    }

    fun updateProfile(newProfile: UserProfile) {
        _profile.value = newProfile
    }

    fun updatePrivacySettings(newSettings: PrivacySettings) {
        _privacySettings.value = newSettings
    }
}
