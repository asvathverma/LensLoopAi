package com.example.models

enum class FollowStatus { REQUESTED, FOLLOWING, BLOCKED }

data class FollowRelationship(
    val followerId: String,
    val followingId: String,
    val status: FollowStatus,
    val isCloseFriend: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class FollowUser(
    val id: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val isMutual: Boolean = false,
    val followStatus: FollowStatus? = null,
    val isVerified: Boolean = false
)

enum class FollowListType { FOLLOWERS, FOLLOWING }
