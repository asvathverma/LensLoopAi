package com.example.network

import com.example.auth.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModule {
    private const val BASE_URL = "https://api.lensloop.com/" // Replace with actual API

    fun provideAuthApi(tokenManager: TokenManager): AuthApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            
            // Add access token to headers if available
            tokenManager.getAccessToken()?.let { token ->
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            
            val response = chain.proceed(requestBuilder.build())
            
            // Handle 401 Unauthorized for token refresh
            if (response.code == 401) {
                synchronized(this) {
                    val currentToken = tokenManager.getAccessToken()
                    val refreshToken = tokenManager.getRefreshToken()
                    
                    if (refreshToken != null) {
                        // In a real app, you would make a synchronous call here to refresh the token.
                        // For simplicity in this demo, we simulate a failed refresh if not handled.
                        // If refresh successful -> tokenManager.saveTokens(new, new)
                        // And retry request:
                        // return chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Bearer $new").build())
                    }
                }
            }
            
            response
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}
