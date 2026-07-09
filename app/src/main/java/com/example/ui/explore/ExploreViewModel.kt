package com.example.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExplorePost(
    val id: String,
    val imageUrl: String,
    val type: String = "image" // image, video, reel
)

data class TrendingItem(
    val id: String,
    val name: String,
    val type: String, // hashtag, audio, effect, creator
    val velocity: String,
    val isHot: Boolean = false
)

data class ExploreState(
    val selectedChannel: String = "For You",
    val channels: List<String> = listOf("For You", "Trending", "Sports", "Fashion", "Travel", "Art", "Music"),
    val posts: List<ExplorePost> = emptyList(),
    val trendingItems: List<TrendingItem> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false
)

class ExploreViewModel : ViewModel() {
    private val _state = MutableStateFlow(ExploreState())
    val state: StateFlow<ExploreState> = _state.asStateFlow()

    init {
        loadPosts()
        loadTrendingData()
        startTrendingRefreshTimer()
    }

    fun selectChannel(channel: String) {
        _state.update { it.copy(selectedChannel = channel, isLoading = true) }
        loadPosts()
    }

    fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        loadPosts()
        loadTrendingData()
    }

    private fun loadTrendingData() {
        val mockTrending = listOf(
            TrendingItem("t1", "#SummerVibes", "hashtag", "+200% in 4h", isHot = true),
            TrendingItem("t2", "Chill Beat", "audio", "+150% in 4h", isHot = true),
            TrendingItem("t3", "Neon Glow", "effect", "+80% in 4h", isHot = false),
            TrendingItem("t4", "@alex_dance", "creator", "+300% in 4h", isHot = true)
        )
        _state.update { it.copy(trendingItems = mockTrending) }
    }

    private fun startTrendingRefreshTimer() {
        viewModelScope.launch {
            while (true) {
                delay(15 * 60 * 1000L) // 15 minutes
                loadTrendingData()
            }
        }
    }

    private fun loadPosts() {
        // Mock data loading with a slight delay
        val mockPosts = (1..20).map { i ->
            ExplorePost(
                id = "post_${_state.value.selectedChannel}_$i",
                imageUrl = "https://example.com/image_$i.jpg",
                type = if (i % 5 == 0) "reel" else if (i % 3 == 0) "video" else "image"
            )
        }
        _state.update { 
            it.copy(
                posts = mockPosts.shuffled(),
                isLoading = false,
                isRefreshing = false
            ) 
        }
    }
}
