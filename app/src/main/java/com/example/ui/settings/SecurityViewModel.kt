package com.example.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class LoginSession(
    val id: String = UUID.randomUUID().toString(),
    val deviceName: String,
    val location: String,
    val ipAddress: String,
    val lastActive: String,
    val isCurrentSession: Boolean = false
)

data class SecurityState(
    val is2FAEnabled: Boolean = false,
    val twoFactorMethod: String? = null, // "Authenticator App", "SMS"
    val loginSessions: List<LoginSession> = emptyList(),
    val emailNotificationsEnabled: Boolean = true,
    val weeklyDigestEnabled: Boolean = true
)

class SecurityViewModel : ViewModel() {
    private val _state = MutableStateFlow(SecurityState())
    val state: StateFlow<SecurityState> = _state.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val mockSessions = listOf(
            LoginSession(deviceName = "Pixel 8 Pro", location = "San Francisco, CA", ipAddress = "192.168.1.1", lastActive = "Active now", isCurrentSession = true),
            LoginSession(deviceName = "MacBook Pro", location = "San Francisco, CA", ipAddress = "192.168.1.5", lastActive = "2 hours ago"),
            LoginSession(deviceName = "Windows PC", location = "Seattle, WA", ipAddress = "10.0.0.4", lastActive = "2 days ago")
        )
        _state.update { it.copy(loginSessions = mockSessions, is2FAEnabled = true, twoFactorMethod = "Authenticator App") }
    }

    fun toggle2FA() {
        _state.update { it.copy(is2FAEnabled = !it.is2FAEnabled, twoFactorMethod = if (!it.is2FAEnabled) "Authenticator App" else null) }
    }
    
    fun toggleEmailNotifications() {
        _state.update { it.copy(emailNotificationsEnabled = !it.emailNotificationsEnabled) }
    }

    fun toggleWeeklyDigest() {
        _state.update { it.copy(weeklyDigestEnabled = !it.weeklyDigestEnabled) }
    }

    fun terminateSession(sessionId: String) {
        _state.update { s ->
            s.copy(loginSessions = s.loginSessions.filter { it.id != sessionId })
        }
    }
    
    fun terminateAllOtherSessions() {
        _state.update { s ->
            s.copy(loginSessions = s.loginSessions.filter { it.isCurrentSession })
        }
    }
}
