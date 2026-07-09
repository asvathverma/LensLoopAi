package com.example.ui.notifications

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

enum class NotificationType { LIKE, COMMENT, MENTION, FOLLOW, LIVE, TAG, SHOPPING }

data class NotificationItem(
    val id: String = UUID.randomUUID().toString(),
    val type: NotificationType,
    val actorName: String,
    val avatar: String,
    val content: String,
    val timeStr: String,
    val preview: String? = null,
    val isFollowing: Boolean? = null
)

data class NotificationsState(
    val notifications: List<NotificationItem> = emptyList(),
    val selectedFilter: NotificationType? = null // null means 'All'
)

class NotificationsViewModel : ViewModel() {
    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val mockData = listOf(
            NotificationItem(id = "1", type = NotificationType.LIKE, actorName = "rahul_vlogs", avatar = "R", content = "liked your photo", timeStr = "2m", preview = "📸"),
            NotificationItem(id = "2", type = NotificationType.FOLLOW, actorName = "priya_sharma", avatar = "P", content = "started following you", timeStr = "15m", isFollowing = false),
            NotificationItem(id = "3", type = NotificationType.COMMENT, actorName = "neha_travels", avatar = "N", content = "commented: \"Amazing shot! 🔥\"", timeStr = "1h", preview = "🏔️"),
            NotificationItem(id = "4", type = NotificationType.MENTION, actorName = "tech_guru", avatar = "T", content = "mentioned you in a comment", timeStr = "3h", preview = "💻"),
            NotificationItem(id = "5", type = NotificationType.LIKE, actorName = "foodie_dreams", avatar = "F", content = "liked your reel", timeStr = "5h", preview = "🍕")
        )
        _state.update { it.copy(notifications = mockData) }
    }

    fun setFilter(type: NotificationType?) {
        _state.update { it.copy(selectedFilter = type) }
    }

    fun toggleFollow(id: String) {
        _state.update { state ->
            val updated = state.notifications.map {
                if (it.id == id && it.isFollowing != null) {
                    it.copy(isFollowing = !it.isFollowing)
                } else it
            }
            state.copy(notifications = updated)
        }
    }
}
