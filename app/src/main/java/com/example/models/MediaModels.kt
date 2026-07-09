package com.example.models

enum class MediaType { IMAGE, VIDEO }

data class MediaItem(
    val id: String,
    val uri: String,
    val type: MediaType,
    val slideCaption: String = "",
    val thumbnailUrl: String? = null
)
