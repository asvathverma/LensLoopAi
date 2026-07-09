package com.example.models

sealed class StorySticker {
    data class Poll(val question: String, val optionA: String, val optionB: String) : StorySticker()
    data class Quiz(val question: String, val options: List<String>, val correctOptionIndex: Int) : StorySticker()
    data class Question(val prompt: String) : StorySticker()
    data class Countdown(val title: String, val timestamp: Long) : StorySticker()
    data class Slider(val question: String, val emoji: String) : StorySticker()
    data class Location(val name: String) : StorySticker()
    data class Hashtag(val tag: String) : StorySticker()
    data class Mention(val username: String) : StorySticker()
    data class Music(val trackId: String, val title: String, val artist: String, val coverUrl: String) : StorySticker()
    data class Link(val url: String, val text: String, val colorHex: String = "#FFFFFF") : StorySticker()
}

data class StorySegment(
    val id: String,
    val media: FeedMedia,
    val timestamp: Long,
    val expiresAt: Long,
    val viewersCount: Int = 0,
    val stickers: List<StorySticker> = emptyList(),
    val isCloseFriends: Boolean = false
)

data class UserStory(
    val author: UserShort,
    val segments: List<StorySegment>,
    val hasUnseen: Boolean = true,
    val isCloseFriends: Boolean = false
)

data class StoryHighlight(
    val id: String,
    val title: String,
    val coverImageUrl: String?,
    val segments: List<StorySegment> = emptyList(),
    val isPrivate: Boolean = false
)
