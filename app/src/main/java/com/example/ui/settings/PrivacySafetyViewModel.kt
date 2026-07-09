package com.example.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BlockedUser(val id: String, val username: String, val name: String, val relation: String = "Blocked")

data class PrivacySafetyState(
    val isPrivateAccount: Boolean = false,
    val showActivityStatus: Boolean = true,
    val hideSensitiveContent: Boolean = true,
    val ageGatingEnabled: Boolean = true,
    val parentalControlsEnabled: Boolean = false,
    val blockedUsers: List<BlockedUser> = emptyList(),
    val restrictedUsers: List<BlockedUser> = emptyList(),
    val mutedUsers: List<BlockedUser> = emptyList()
)

class PrivacySafetyViewModel : ViewModel() {
    private val _state = MutableStateFlow(PrivacySafetyState())
    val state: StateFlow<PrivacySafetyState> = _state.asStateFlow()

    init {
        // Load mock data
        _state.update {
            it.copy(
                blockedUsers = listOf(
                    BlockedUser("1", "toxic_user99", "Toxic User"),
                    BlockedUser("2", "spambot001", "Spam Bot")
                ),
                restrictedUsers = listOf(
                    BlockedUser("3", "annoying_cousin", "Annoying Cousin")
                ),
                mutedUsers = listOf(
                    BlockedUser("4", "loud_brand", "Loud Brand")
                )
            )
        }
    }

    fun togglePrivateAccount() {
        _state.update { it.copy(isPrivateAccount = !it.isPrivateAccount) }
    }

    fun toggleActivityStatus() {
        _state.update { it.copy(showActivityStatus = !it.showActivityStatus) }
    }

    fun toggleHideSensitiveContent() {
        _state.update { it.copy(hideSensitiveContent = !it.hideSensitiveContent) }
    }

    fun unblockUser(userId: String) {
        _state.update { s -> s.copy(blockedUsers = s.blockedUsers.filter { it.id != userId }) }
    }
    
    fun unrestrictUser(userId: String) {
        _state.update { s -> s.copy(restrictedUsers = s.restrictedUsers.filter { it.id != userId }) }
    }
    
    fun unmuteUser(userId: String) {
        _state.update { s -> s.copy(mutedUsers = s.mutedUsers.filter { it.id != userId }) }
    }
}
