package com.example.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class OtpRequired(val emailOrPhone: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _demoMode = MutableStateFlow(repository.isDemoMode)
    val demoMode = _demoMode.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        // If access token exists, consider them authenticated for demo purposes
        if (tokenManager.getAccessToken() != null) {
            _authState.value = AuthState.Authenticated(User("1", "Cached User", "cached@example.com", null, true))
        }
    }
    
    fun toggleDemoMode(enabled: Boolean) {
        repository.isDemoMode = enabled
        _demoMode.value = enabled
    }

    fun login(emailOrPhone: String, passwordHash: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = repository.login(emailOrPhone, passwordHash)) {
                is AuthResult.Success -> {
                    if (result.data.isVerified) {
                        _authState.value = AuthState.Authenticated(result.data)
                    } else {
                        _authState.value = AuthState.OtpRequired(emailOrPhone)
                    }
                }
                is AuthResult.Error -> {
                    _authState.value = AuthState.Error(result.message)
                }
            }
        }
    }

    fun register(fullName: String, emailOrPhone: String, passwordHash: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = repository.register(fullName, emailOrPhone, passwordHash)) {
                is AuthResult.Success -> {
                    if (result.data.isVerified) {
                        _authState.value = AuthState.Authenticated(result.data)
                    } else {
                        _authState.value = AuthState.OtpRequired(emailOrPhone)
                    }
                }
                is AuthResult.Error -> {
                    _authState.value = AuthState.Error(result.message)
                }
            }
        }
    }

    fun verifyOtp(emailOrPhone: String, otp: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = repository.verifyOtp(emailOrPhone, otp)) {
                is AuthResult.Success -> {
                    _authState.value = AuthState.Authenticated(result.data)
                }
                is AuthResult.Error -> {
                    _authState.value = AuthState.Error(result.message)
                }
            }
        }
    }

    fun oauthLogin(provider: String, token: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = repository.oauthLogin(provider, token)) {
                is AuthResult.Success -> {
                    _authState.value = AuthState.Authenticated(result.data)
                }
                is AuthResult.Error -> {
                    _authState.value = AuthState.Error(result.message)
                }
            }
        }
    }

    fun logout() {
        repository.logout()
        _authState.value = AuthState.Idle
    }
    
    fun resetError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }
}
