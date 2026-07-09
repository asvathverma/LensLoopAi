package com.example.ui.reels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ReelAnalytics(
    val reelId: String,
    val totalPlays: Int,
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val saves: Int,
    val avgWatchTimeSec: Int,
    val completionRate: Float,
    val trafficFeed: Int,
    val trafficAudio: Int,
    val trafficHashtag: Int,
    val trafficProfile: Int,
    val retentionData: List<Float> // 10 data points for % of video length
)

data class ReelsAnalyticsState(
    val analytics: ReelAnalytics? = null,
    val isLoading: Boolean = true
)

class ReelsAnalyticsViewModel : ViewModel() {
    private val _state = MutableStateFlow(ReelsAnalyticsState())
    val state: StateFlow<ReelsAnalyticsState> = _state.asStateFlow()

    fun loadAnalytics(reelId: String) {
        // Mock data loading
        val mockData = ReelAnalytics(
            reelId = reelId,
            totalPlays = 14500,
            likes = 3200,
            comments = 150,
            shares = 890,
            saves = 1100,
            avgWatchTimeSec = 12,
            completionRate = 0.65f,
            trafficFeed = 60,
            trafficAudio = 15,
            trafficHashtag = 15,
            trafficProfile = 10,
            retentionData = listOf(1.0f, 0.95f, 0.9f, 0.85f, 0.8f, 0.75f, 0.7f, 0.65f, 0.65f, 0.6f)
        )
        _state.value = ReelsAnalyticsState(analytics = mockData, isLoading = false)
    }
}
