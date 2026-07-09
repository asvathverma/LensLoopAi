package com.example.auth

import com.example.network.AuthApi
import com.example.network.AuthResponse
import com.example.network.LoginRequest
import com.example.network.OAuthRequest
import com.example.network.OtpRequest
import com.example.network.RegisterRequest
import com.example.network.User
import kotlinx.coroutines.delay
import java.net.UnknownHostException

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String, val isRateLimited: Boolean = false) : AuthResult<Nothing>()
}

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {
    // Flag to allow testing UI without a real backend
    var isDemoMode = true

    suspend fun login(emailOrPhone: String, passwordHash: String): AuthResult<User> {
        val fingerprint = tokenManager.getDeviceFingerprint() ?: "device_id_${System.currentTimeMillis()}"
        tokenManager.saveDeviceFingerprint(fingerprint)

        if (isDemoMode) {
            delay(1500)
            if (passwordHash == "wrong") return AuthResult.Error("Invalid credentials")
            val dummyUser = User("1", "John Doe", emailOrPhone, null, true)
            tokenManager.saveTokens("demo_access_jwt", "demo_refresh_jwt")
            return AuthResult.Success(dummyUser)
        }

        return try {
            val response = authApi.login(LoginRequest(emailOrPhone, passwordHash, fingerprint))
            if (response.isSuccessful) {
                response.body()?.let {
                    tokenManager.saveTokens(it.accessToken, it.refreshToken)
                    AuthResult.Success(it.user)
                } ?: AuthResult.Error("Empty response")
            } else if (response.code() == 429) {
                AuthResult.Error("Too many login attempts. Please try again later.", isRateLimited = true)
            } else {
                AuthResult.Error("Login failed: ${response.message()}")
            }
        } catch (e: Exception) {
            handleNetworkError(e)
        }
    }

    suspend fun register(fullName: String, emailOrPhone: String, passwordHash: String): AuthResult<User> {
        if (isDemoMode) {
            delay(1500)
            val dummyUser = User("1", fullName, emailOrPhone, null, false)
            return AuthResult.Success(dummyUser)
        }

        return try {
            val response = authApi.register(RegisterRequest(fullName, emailOrPhone, passwordHash, tokenManager.getDeviceFingerprint()))
            if (response.isSuccessful) {
                response.body()?.let {
                    tokenManager.saveTokens(it.accessToken, it.refreshToken)
                    AuthResult.Success(it.user)
                } ?: AuthResult.Error("Empty response")
            } else {
                AuthResult.Error("Registration failed: ${response.message()}")
            }
        } catch (e: Exception) {
             handleNetworkError(e)
        }
    }

    suspend fun verifyOtp(emailOrPhone: String, otp: String): AuthResult<User> {
        if (isDemoMode) {
            delay(1000)
            if (otp != "123456") return AuthResult.Error("Invalid OTP. Use 123456 for demo.")
            val dummyUser = User("1", "John Doe", emailOrPhone, null, true)
            tokenManager.saveTokens("demo_access_jwt", "demo_refresh_jwt")
            return AuthResult.Success(dummyUser)
        }

        return try {
            val response = authApi.verifyOtp(OtpRequest(emailOrPhone, otp))
            if (response.isSuccessful) {
                response.body()?.let {
                    tokenManager.saveTokens(it.accessToken, it.refreshToken)
                    AuthResult.Success(it.user)
                } ?: AuthResult.Error("Empty response")
            } else {
                AuthResult.Error("OTP verification failed")
            }
        } catch (e: Exception) {
             handleNetworkError(e)
        }
    }

    suspend fun oauthLogin(provider: String, token: String): AuthResult<User> {
        if (isDemoMode) {
            delay(1000)
            val dummyUser = User("1", "Social User", "social@example.com", null, true)
            tokenManager.saveTokens("demo_access_jwt", "demo_refresh_jwt")
            return AuthResult.Success(dummyUser)
        }
        
        return try {
            val response = authApi.oauthLogin(OAuthRequest(provider, token, tokenManager.getDeviceFingerprint()))
            if (response.isSuccessful) {
                response.body()?.let {
                    tokenManager.saveTokens(it.accessToken, it.refreshToken)
                    AuthResult.Success(it.user)
                } ?: AuthResult.Error("Empty response")
            } else {
                AuthResult.Error("OAuth login failed")
            }
        } catch (e: Exception) {
            handleNetworkError(e)
        }
    }

    fun logout() {
        tokenManager.clearTokens()
    }
    
    private fun handleNetworkError(e: Exception): AuthResult.Error {
        return if (e is UnknownHostException) {
            AuthResult.Error("Network error: Unable to reach LensLoop API. (Enable Demo Mode to test UI)")
        } else {
            AuthResult.Error("An error occurred: ${e.localizedMessage}")
        }
    }
}
