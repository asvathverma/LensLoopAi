package com.example.models

enum class CollectionPrivacy { PRIVATE, SHARED, PUBLIC }

data class PostCollection(
    val id: String,
    val name: String,
    val coverImageUrl: String?,
    val privacy: CollectionPrivacy,
    val isCollaborative: Boolean,
    val ownerId: String,
    val contributors: List<UserShort> = emptyList(),
    val savedPosts: List<Post> = emptyList()
)
