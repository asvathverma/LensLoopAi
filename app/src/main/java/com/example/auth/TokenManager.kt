package com.example.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit()
            .putString("access_token", accessToken)
            .putString("refresh_token", refreshToken)
            .apply()
    }

    fun getAccessToken(): String? = sharedPreferences.getString("access_token", null)

    fun getRefreshToken(): String? = sharedPreferences.getString("refresh_token", null)

    fun clearTokens() {
        sharedPreferences.edit()
            .remove("access_token")
            .remove("refresh_token")
            .apply()
    }

    fun setOnboardingCompleted(completed: Boolean) {
        sharedPreferences.edit()
            .putBoolean("onboarding_completed", completed)
            .apply()
    }

    fun hasCompletedOnboarding(): Boolean = sharedPreferences.getBoolean("onboarding_completed", false)

    fun saveDeviceFingerprint(fingerprint: String) {
        sharedPreferences.edit()
            .putString("device_fingerprint", fingerprint)
            .apply()
    }

    fun getDeviceFingerprint(): String? = sharedPreferences.getString("device_fingerprint", null)
}
