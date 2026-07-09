package com.example.models

data class Hashtag(
    val name: String,
    val postCount: Int,
    val isFollowing: Boolean = false,
    val isBanned: Boolean = false,
    val trendingScore: Double = 0.0
)

data class HashtagAnalytics(
    val hashtag: Hashtag,
    val relatedHashtags: List<Hashtag>,
    val topPosts: List<Post>
)
