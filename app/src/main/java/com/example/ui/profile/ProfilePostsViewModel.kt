package com.example.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileGridPost(
    val id: String,
    val imageUrl: String,
    val isPinned: Boolean = false
)

data class ProfilePostsState(
    val posts: List<ProfileGridPost> = emptyList()
)

class ProfilePostsViewModel : ViewModel() {
    private val _state = MutableStateFlow(ProfilePostsState())
    val state: StateFlow<ProfilePostsState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                posts = listOf(
                    ProfileGridPost("1", "https://picsum.photos/400/400?1", true),
                    ProfileGridPost("2", "https://picsum.photos/400/400?2"),
                    ProfileGridPost("3", "https://picsum.photos/400/400?3"),
                    ProfileGridPost("4", "https://picsum.photos/400/400?4"),
                    ProfileGridPost("5", "https://picsum.photos/400/400?5"),
                    ProfileGridPost("6", "https://picsum.photos/400/400?6"),
                    ProfileGridPost("7", "https://picsum.photos/400/400?7"),
                    ProfileGridPost("8", "https://picsum.photos/400/400?8"),
                    ProfileGridPost("9", "https://picsum.photos/400/400?9")
                )
            )
        }
    }

    fun togglePin(postId: String) {
        _state.update { s ->
            val post = s.posts.find { it.id == postId } ?: return@update s
            if (!post.isPinned && s.posts.count { it.isPinned } >= 3) {
                // Max 3 pins
                return@update s
            }
            s.copy(
                posts = s.posts.map {
                    if (it.id == postId) it.copy(isPinned = !it.isPinned) else it
                }
            )
        }
    }
}
