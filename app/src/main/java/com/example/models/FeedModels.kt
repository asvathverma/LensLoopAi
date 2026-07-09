package com.example.models

data class UserShort(val id: String, val username: String, val avatarUrl: String?)

enum class PostType { PHOTO, VIDEO, CAROUSEL }

data class FeedMedia(
    val url: String,
    val type: MediaType,
    val slideCaption: String? = null
)

data class Post(
    val id: String,
    val author: UserShort,
    val media: List<FeedMedia>,
    val type: PostType,
    val caption: String,
    val timestamp: Long,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean = false,
    val rankingScore: Double = 0.0,
    val location: LocationTag? = null,
    val isSaved: Boolean = false,
    val savedCount: Int = 0,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val isEdited: Boolean = false,
    val shareCount: Int = 0,
    val allowResharing: Boolean = true,
    val coAuthor: UserShort? = null,
    val isPaidPartnership: Boolean = false,
    val partnerBrand: UserShort? = null,
    val taggedProducts: List<Product> = emptyList()
)
