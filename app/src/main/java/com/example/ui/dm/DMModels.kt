package com.example.ui.dm

import java.util.UUID

enum class MessageStatus { SENT, DELIVERED, READ }
enum class MessageType { TEXT, PHOTO_VIEW_ONCE, VIDEO, VOICE, GIF, STICKER }

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val senderId: String,
    val text: String = "",
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,
    val status: MessageStatus = MessageStatus.SENT,
    val timestamp: Long = System.currentTimeMillis(),
    val isVanishMode: Boolean = false,
    val durationSec: Int? = null,
    val reactions: Map<String, String> = emptyMap(), // userId to emoji
    val replyToMessageId: String? = null
)

data class Conversation(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val iconUrl: String? = null,
    val isGroup: Boolean = false,
    val participants: List<String>,
    val messages: List<Message> = emptyList(),
    val unreadCount: Int = 0,
    val isE2EEnabled: Boolean = false,
    val isVanishModeActive: Boolean = false,
    val typingUsers: List<String> = emptyList(),
    val isAdmin: Boolean = false,
    val isRequest: Boolean = false,
    val isSpam: Boolean = false,
    val isOnline: Boolean = false,
    val lastActiveTime: Long? = null,
    val readReceiptsEnabled: Boolean = true
)
