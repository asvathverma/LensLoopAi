package com.example.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CloseFriend(val id: String, val username: String, val name: String, val isAdded: Boolean = false)

data class CloseFriendsState(
    val friendsList: List<CloseFriend> = emptyList(),
    val suggestions: List<CloseFriend> = emptyList(),
    val searchQuery: String = ""
)

class CloseFriendsViewModel : ViewModel() {
    private val _state = MutableStateFlow(CloseFriendsState())
    val state: StateFlow<CloseFriendsState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                friendsList = listOf(
                    CloseFriend("1", "bestie123", "Bestie", true),
                    CloseFriend("2", "mom", "Mom", true)
                ),
                suggestions = listOf(
                    CloseFriend("3", "jane_doe", "Jane Doe", false),
                    CloseFriend("4", "john_smith", "John Smith", false)
                )
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun addFriend(id: String) {
        _state.update { s ->
            val addedFriend = s.suggestions.find { it.id == id }?.copy(isAdded = true)
            if (addedFriend != null) {
                s.copy(
                    friendsList = s.friendsList + addedFriend,
                    suggestions = s.suggestions.filter { it.id != id }
                )
            } else s
        }
    }

    fun removeFriend(id: String) {
        _state.update { s ->
            val removedFriend = s.friendsList.find { it.id == id }?.copy(isAdded = false)
            if (removedFriend != null) {
                s.copy(
                    friendsList = s.friendsList.filter { it.id != id },
                    suggestions = s.suggestions + removedFriend
                )
            } else s
        }
    }
}
