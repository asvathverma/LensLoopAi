package com.example.models

data class Comment(
    val id: String,
    val postId: String,
    val author: UserShort,
    val text: String,
    val timestamp: Long,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val isPinned: Boolean = false,
    val replies: List<Comment> = emptyList(),
    val isAuthorVerified: Boolean = false,
    val isTopFan: Boolean = false,
    val isHidden: Boolean = false
)
