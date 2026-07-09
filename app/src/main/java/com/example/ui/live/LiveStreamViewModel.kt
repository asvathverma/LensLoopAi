package com.example.ui.live

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class LiveComment(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val text: String,
    val isModerator: Boolean = false,
    val badgeTier: Int = 0 // 0: none, 1: bronze, 2: silver, 3: gold
)

data class LiveGuest(
    val id: String,
    val username: String,
    val videoUrl: String = ""
)

enum class LiveLayout {
    SOLO, GRID, SPOTLIGHT, SIDEBAR
}

data class LiveStreamState(
    val isStreaming: Boolean = false,
    val streamKey: String = UUID.randomUUID().toString(),
    val viewerCount: Int = 0,
    val comments: List<LiveComment> = emptyList(),
    val pinnedComment: LiveComment? = null,
    val isSlowMode: Boolean = false,
    val floatingEmojis: List<String> = emptyList(),
    val topContributors: List<String> = emptyList(),
    val guests: List<LiveGuest> = emptyList(),
    val layout: LiveLayout = LiveLayout.SOLO,
    val totalRevenue: Double = 0.0,
    val isReplayAvailable: Boolean = false
)

class LiveStreamViewModel : ViewModel() {
    private val _state = MutableStateFlow(LiveStreamState())
    val state: StateFlow<LiveStreamState> = _state.asStateFlow()

    fun startStream() {
        _state.update { it.copy(isStreaming = true, viewerCount = 1) }
    }

    fun endStream() {
        _state.update { it.copy(isStreaming = false, isReplayAvailable = true) }
    }

    fun addComment(text: String, username: String = "user_me", badgeTier: Int = 0) {
        val comment = LiveComment(username = username, text = text, badgeTier = badgeTier)
        _state.update { it.copy(comments = it.comments + comment) }
    }

    fun pinComment(comment: LiveComment?) {
        _state.update { it.copy(pinnedComment = comment) }
    }

    fun toggleSlowMode() {
        _state.update { it.copy(isSlowMode = !it.isSlowMode) }
    }

    fun sendEmoji(emoji: String) {
        // Simplified floating emoji handling
        val currentEmojis = _state.value.floatingEmojis
        _state.update { it.copy(floatingEmojis = currentEmojis + emoji) }
    }
    
    fun clearFloatingEmojis() {
        _state.update { it.copy(floatingEmojis = emptyList()) }
    }

    fun purchaseBadge(tier: Int, amount: Double) {
        _state.update { 
            it.copy(
                totalRevenue = it.totalRevenue + amount
            )
        }
        addComment("Sent a badge!", "viewer_1", badgeTier = tier)
    }

    fun addGuest(guest: LiveGuest) {
        _state.update { 
            val newGuests = it.guests + guest
            val newLayout = if (newGuests.isNotEmpty()) LiveLayout.GRID else LiveLayout.SOLO
            it.copy(guests = newGuests, layout = newLayout)
        }
    }

    fun removeGuest(guestId: String) {
        _state.update { 
            val newGuests = it.guests.filter { g -> g.id != guestId }
            val newLayout = if (newGuests.isNotEmpty()) it.layout else LiveLayout.SOLO
            it.copy(guests = newGuests, layout = newLayout)
        }
    }

    fun setLayout(layout: LiveLayout) {
        _state.update { it.copy(layout = layout) }
    }
    
    fun saveReel() {
        // mock save to reel functionality
    }
}
