package com.example.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val emailOrPhone: String,
    val passwordHash: String, // Client can send hashed or plain, backend does bcrypt
    val deviceFingerprint: String? = null
)

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val fullName: String,
    val emailOrPhone: String,
    val passwordHash: String,
    val deviceFingerprint: String? = null
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val isVerified: Boolean
)

@JsonClass(generateAdapter = true)
data class OtpRequest(
    val emailOrPhone: String,
    val otp: String
)

@JsonClass(generateAdapter = true)
data class RefreshRequest(
    val refreshToken: String
)

@JsonClass(generateAdapter = true)
data class OAuthRequest(
    val provider: String, // "google", "apple", "facebook"
    val token: String,
    val deviceFingerprint: String? = null
)
