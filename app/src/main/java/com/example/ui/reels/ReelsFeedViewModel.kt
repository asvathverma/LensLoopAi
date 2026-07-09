package com.example.ui.reels

import androidx.lifecycle.ViewModel
import com.example.models.FeedMedia
import com.example.models.MediaType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ReelInfo(
    val id: String,
    val media: FeedMedia,
    val authorUsername: String,
    val description: String,
    val audioTrack: String,
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isRemixable: Boolean = true
)

data class ReelsFeedState(
    val reels: List<ReelInfo> = emptyList(),
    val isLoading: Boolean = true
)

class ReelsFeedViewModel : ViewModel() {
    private val _state = MutableStateFlow(ReelsFeedState())
    val state: StateFlow<ReelsFeedState> = _state.asStateFlow()

    init {
        loadReels()
    }

    private fun loadReels() {
        // Simulated recommendation algorithm using collaborative filtering,
        // watch time signals, etc.
        val mockReels = listOf(
            ReelInfo("r1", FeedMedia("https://example.com/r1.mp4", MediaType.VIDEO), "alice_smith", "My amazing reel! #trending", "Original Audio - alice", 1200, 45, 12),
            ReelInfo("r2", FeedMedia("https://example.com/r2.mp4", MediaType.VIDEO), "bob_jones", "Learning something new", "Trending Song", 3400, 120, 450),
            ReelInfo("r3", FeedMedia("https://example.com/r3.mp4", MediaType.VIDEO), "charlie_d", "Nature vibes", "Chill Lo-Fi", 560, 20, 5)
        )
        _state.update { it.copy(reels = mockReels, isLoading = false) }
    }

    fun toggleLike(id: String) {
        _state.update { currentState ->
            val updated = currentState.reels.map {
                if (it.id == id) it.copy(isLiked = !it.isLiked, likes = if (it.isLiked) it.likes - 1 else it.likes + 1)
                else it
            }
            currentState.copy(reels = updated)
        }
    }

    fun toggleSave(id: String) {
        _state.update { currentState ->
            val updated = currentState.reels.map {
                if (it.id == id) it.copy(isSaved = !it.isSaved)
                else it
            }
            currentState.copy(reels = updated)
        }
    }
}
