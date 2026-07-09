package com.example.models

data class MusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val coverUrl: String,
    val durationMs: Long,
    val isTrending: Boolean = false,
    val genre: String = "Pop"
)
