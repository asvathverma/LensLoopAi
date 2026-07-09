package com.example.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpRequest): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<AuthResponse>
    
    @POST("auth/oauth")
    suspend fun oauthLogin(@Body request: OAuthRequest): Response<AuthResponse>
}
