package com.example.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.FollowListType
import com.example.models.FollowStatus
import com.example.models.FollowUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FollowListState(
    val type: FollowListType = FollowListType.FOLLOWERS,
    val users: List<FollowUser> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class FollowViewModel : ViewModel() {
    private val _state = MutableStateFlow(FollowListState())
    val state: StateFlow<FollowListState> = _state.asStateFlow()

    private val allMockUsers = listOf(
        FollowUser("2", "alice_wonder", "Alice", null, isMutual = true, followStatus = FollowStatus.FOLLOWING),
        FollowUser("3", "bob_builder", "Bob", null, isMutual = false, followStatus = null),
        FollowUser("4", "charlie_chap", "Charlie", null, isMutual = true, followStatus = FollowStatus.FOLLOWING),
        FollowUser("5", "david_bowie", "David", null, isMutual = false, followStatus = FollowStatus.REQUESTED),
        FollowUser("6", "eve_adams", "Eve", null, isMutual = false, followStatus = null)
    )

    fun loadList(type: FollowListType) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, type = type) }
            // Mock fetching data
            kotlinx.coroutines.delay(500)
            val users = if (type == FollowListType.FOLLOWERS) {
                allMockUsers.shuffled()
            } else {
                allMockUsers.filter { it.followStatus == FollowStatus.FOLLOWING }
            }
            _state.update { it.copy(users = users, isLoading = false) }
        }
    }

    fun searchUsers(query: String) {
        _state.update { it.copy(searchQuery = query) }
        // Local search for simplicity
        val filtered = allMockUsers.filter {
            it.username.contains(query, ignoreCase = true) || it.displayName.contains(query, ignoreCase = true)
        }
        _state.update { it.copy(users = filtered) }
    }

    fun toggleFollow(userId: String) {
        _state.update { currentState ->
            val updatedUsers = currentState.users.map { user ->
                if (user.id == userId) {
                    val newStatus = when (user.followStatus) {
                        FollowStatus.FOLLOWING -> null // Unfollow
                        FollowStatus.REQUESTED -> null // Cancel request
                        null -> FollowStatus.FOLLOWING // Follow (mock public account)
                        else -> user.followStatus
                    }
                    user.copy(followStatus = newStatus)
                } else {
                    user
                }
            }
            currentState.copy(users = updatedUsers)
        }
    }
    
    fun removeFollower(userId: String) {
        _state.update { currentState ->
            val updatedUsers = currentState.users.filterNot { it.id == userId }
            currentState.copy(users = updatedUsers)
        }
    }
}
