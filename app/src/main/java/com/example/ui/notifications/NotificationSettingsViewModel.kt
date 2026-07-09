package com.example.ui.notifications

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class NotificationSettingsState(
    val pauseAll: Boolean = false,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "07:00",
    
    // Per-notification-type controls
    val likesEnabled: Boolean = true,
    val commentsEnabled: Boolean = true,
    val newFollowersEnabled: Boolean = true,
    val directMessagesEnabled: Boolean = true,
    val mentionsEnabled: Boolean = true,
    val liveVideoEnabled: Boolean = true,
    val shoppingEnabled: Boolean = true,
    
    // Delivery options
    val deliveryReceiptsEnabled: Boolean = true
)

class NotificationSettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationSettingsState())
    val uiState: StateFlow<NotificationSettingsState> = _uiState.asStateFlow()
    
    fun togglePauseAll(enabled: Boolean) {
        _uiState.update { it.copy(pauseAll = enabled) }
    }
    
    fun toggleQuietHours(enabled: Boolean) {
        _uiState.update { it.copy(quietHoursEnabled = enabled) }
    }
    
    fun updateQuietHoursStart(time: String) {
        _uiState.update { it.copy(quietHoursStart = time) }
    }
    
    fun updateQuietHoursEnd(time: String) {
        _uiState.update { it.copy(quietHoursEnd = time) }
    }
    
    fun toggleLikes(enabled: Boolean) {
        _uiState.update { it.copy(likesEnabled = enabled) }
    }
    
    fun toggleComments(enabled: Boolean) {
        _uiState.update { it.copy(commentsEnabled = enabled) }
    }
    
    fun toggleNewFollowers(enabled: Boolean) {
        _uiState.update { it.copy(newFollowersEnabled = enabled) }
    }
    
    fun toggleDirectMessages(enabled: Boolean) {
        _uiState.update { it.copy(directMessagesEnabled = enabled) }
    }
    
    fun toggleMentions(enabled: Boolean) {
        _uiState.update { it.copy(mentionsEnabled = enabled) }
    }
    
    fun toggleLiveVideo(enabled: Boolean) {
        _uiState.update { it.copy(liveVideoEnabled = enabled) }
    }
    
    fun toggleShopping(enabled: Boolean) {
        _uiState.update { it.copy(shoppingEnabled = enabled) }
    }
    
    fun toggleDeliveryReceipts(enabled: Boolean) {
        _uiState.update { it.copy(deliveryReceiptsEnabled = enabled) }
    }
}
