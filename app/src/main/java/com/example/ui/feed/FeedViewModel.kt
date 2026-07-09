package com.example.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.Post
import com.example.models.PostType
import com.example.models.UserShort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {
    private val _feedPosts = MutableStateFlow<List<Post>>(emptyList())
    val feedPosts: StateFlow<List<Post>> = _feedPosts.asStateFlow()

    private val _stories = MutableStateFlow<List<com.example.models.UserStory>>(emptyList())
    val stories: StateFlow<List<com.example.models.UserStory>> = _stories.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadFeed()
        viewModelScope.launch {
            com.example.data.PostRepository.localPosts.collect { local ->
                val currentMocks = generateMockPosts()
                _feedPosts.value = rankPosts(local + currentMocks)
            }
        }
    }

    fun loadFeed() {
        viewModelScope.launch {
            _isLoading.value = true
            kotlinx.coroutines.delay(1000)
            val mockPosts = generateMockPosts()
            val local = com.example.data.PostRepository.localPosts.value
            _feedPosts.value = rankPosts(local + mockPosts)
            _stories.value = generateMockStories()
            _isLoading.value = false
        }
    }

    private fun generateMockStories(): List<com.example.models.UserStory> {
        val now = System.currentTimeMillis()
        val hour = 60 * 60 * 1000L
        return listOf(
            com.example.models.UserStory(
                author = UserShort("me", "Your Story", null),
                segments = emptyList(),
                hasUnseen = false
            ),
            com.example.models.UserStory(
                author = UserShort("u2", "mkbhd", null),
                segments = listOf(
                    com.example.models.StorySegment("s1", com.example.models.FeedMedia("https://example.com/s1.jpg", com.example.models.MediaType.IMAGE), now - 2 * hour, now + 22 * hour)
                ),
                hasUnseen = true,
                isCloseFriends = true
            ),
            com.example.models.UserStory(
                author = UserShort("u1", "natgeo", null),
                segments = listOf(
                    com.example.models.StorySegment("s2", com.example.models.FeedMedia("https://example.com/s2.jpg", com.example.models.MediaType.IMAGE), now - 5 * hour, now + 19 * hour)
                ),
                hasUnseen = true
            )
        )
    }

    private fun rankPosts(posts: List<Post>): List<Post> {
        val now = System.currentTimeMillis()
        return posts.filter { !it.isDeleted && !it.isArchived }.map { post ->
            val ageInHours = (now - post.timestamp) / (1000 * 60 * 60.0)
            val recencyScore = 1.0 / (ageInHours + 1.0)
            val affinityScore = if (post.author.username == "natgeo") 0.9 else 0.5
            val typeScore = if (post.type == PostType.PHOTO) 1.2 else 1.0
            val engagementScore = (post.likesCount + post.commentsCount) / 1000.0
            val finalScore = (recencyScore * 0.4) + (affinityScore * 0.3) + (typeScore * 0.2) + (engagementScore * 0.1)
            
            post.copy(rankingScore = finalScore)
        }.sortedByDescending { it.rankingScore }
    }

    private fun generateMockPosts(): List<Post> {
        val now = System.currentTimeMillis()
        val hour = 1000 * 60 * 60L
        return listOf(
            Post("1", UserShort("u1", "natgeo", null), listOf(com.example.models.FeedMedia("https://example.com/1.jpg", com.example.models.MediaType.IMAGE)), PostType.PHOTO, "Beautiful landscape", now - 2 * hour, 1200, 45, 
                coAuthor = UserShort("u5", "photographer", null),
                isPaidPartnership = true,
                partnerBrand = UserShort("u6", "travelgear", null)
            ),
            Post("2", UserShort("u2", "mkbhd", null), listOf(com.example.models.FeedMedia("https://example.com/2.mp4", com.example.models.MediaType.VIDEO)), PostType.VIDEO, "New tech review", now - 5 * hour, 5000, 300,
                taggedProducts = listOf(
                    com.example.models.Product("p1", "Smartphone 15", "Latest tech", 999.0, imageUrl = "https://example.com/phone.jpg", merchant = UserShort("u2", "mkbhd", null)),
                    com.example.models.Product("p2", "Wireless Earbuds", "Noise cancelling", 249.0, imageUrl = "https://example.com/buds.jpg", merchant = UserShort("u2", "mkbhd", null))
                )
            ),
            Post("3", UserShort("u3", "foodie", null), listOf(
                com.example.models.FeedMedia("https://example.com/3_1.jpg", com.example.models.MediaType.IMAGE, "Step 1: Ingredients"),
                com.example.models.FeedMedia("https://example.com/3_2.mp4", com.example.models.MediaType.VIDEO, "Step 2: Mixing"),
                com.example.models.FeedMedia("https://example.com/3_3.jpg", com.example.models.MediaType.IMAGE, "Step 3: Final result")
            ), PostType.CAROUSEL, "Delicious recipe", now - 1 * hour, 800, 20),
            Post("4", UserShort("u4", "travel_bug", null), listOf(com.example.models.FeedMedia("https://example.com/4.mp4", com.example.models.MediaType.VIDEO)), PostType.VIDEO, "Paris vibes", now - 10 * hour, 3000, 150, location = com.example.models.LocationTag("loc1", "Eiffel Tower", "Paris", "Landmark", 48.8584, 2.2945))
        )
    }

    fun toggleLike(postId: String) {
        _feedPosts.update { posts ->
            posts.map { post ->
                if (post.id == postId) {
                    val newLikeStatus = !post.isLiked
                    val newLikesCount = if (newLikeStatus) post.likesCount + 1 else post.likesCount - 1
                    post.copy(isLiked = newLikeStatus, likesCount = newLikesCount)
                } else {
                    post
                }
            }
        }
    }

    fun toggleSave(postId: String) {
        _feedPosts.update { posts ->
            posts.map { post ->
                if (post.id == postId) {
                    val newSaveStatus = !post.isSaved
                    val newSaveCount = if (newSaveStatus) post.savedCount + 1 else post.savedCount - 1
                    post.copy(isSaved = newSaveStatus, savedCount = newSaveCount)
                } else {
                    post
                }
            }
        }
    }
    
    fun archivePost(postId: String) {
        _feedPosts.update { posts ->
            posts.map { post ->
                if (post.id == postId) {
                    post.copy(isArchived = true)
                } else {
                    post
                }
            }
        }
    }
    
    fun deletePost(postId: String) {
        _feedPosts.update { posts ->
            posts.map { post ->
                if (post.id == postId) {
                    post.copy(isDeleted = true)
                } else {
                    post
                }
            }
        }
    }
}
