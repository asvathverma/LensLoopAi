package com.example.ui.hashtag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.Hashtag
import com.example.models.HashtagAnalytics
import com.example.models.Post
import com.example.models.PostType
import com.example.models.UserShort
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HashtagScreenState(
    val analytics: HashtagAnalytics? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class HashtagViewModel : ViewModel() {
    private val _state = MutableStateFlow(HashtagScreenState())
    val state: StateFlow<HashtagScreenState> = _state.asStateFlow()

    fun loadHashtag(name: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            delay(800) // Simulate network
            
            val hashtag = Hashtag(name = name, postCount = 1205000, isFollowing = false, trendingScore = 98.5)
            val mockPosts = listOf(
                Post("h1", UserShort("u1", "photog_master", null), listOf(com.example.models.FeedMedia("url", com.example.models.MediaType.IMAGE)), PostType.PHOTO, "Amazing $name #$name", System.currentTimeMillis(), 5000, 100),
                Post("h2", UserShort("u2", "traveler", null), listOf(com.example.models.FeedMedia("url2", com.example.models.MediaType.VIDEO)), PostType.VIDEO, "Exploring #$name", System.currentTimeMillis() - 3600000, 2000, 45)
            )
            val related = listOf(
                Hashtag("nature", 800000),
                Hashtag("explore", 650000),
                Hashtag("adventure", 450000)
            )
            
            _state.update { 
                it.copy(
                    analytics = HashtagAnalytics(hashtag, related, mockPosts),
                    isLoading = false
                )
            }
        }
    }

    fun toggleFollowHashtag() {
        _state.update { currentState ->
            currentState.analytics?.let { analytics ->
                val newHashtag = analytics.hashtag.copy(isFollowing = !analytics.hashtag.isFollowing)
                currentState.copy(analytics = analytics.copy(hashtag = newHashtag))
            } ?: currentState
        }
    }
}
