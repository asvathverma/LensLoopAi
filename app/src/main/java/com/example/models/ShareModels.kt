package com.example.models

enum class ShareDestination { STORY, CLOSE_FRIENDS_STORY, DIRECT_MESSAGE, EXTERNAL_LINK }

data class ShareAction(
    val postId: String,
    val destination: ShareDestination,
    val targetUsers: List<UserShort> = emptyList()
)
