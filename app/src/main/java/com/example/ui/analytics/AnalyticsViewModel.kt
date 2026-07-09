package com.example.ui.analytics

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ReachAnalytics(
    val followersReach: Int,
    val nonFollowersReach: Int,
    val paidReach: Int,
    val organicReach: Int
)

data class StoryAnalytics(
    val impressions: Int,
    val reach: Int,
    val replies: Int,
    val exitRate: Double,
    val forwardRate: Double
)

data class ReelsAnalytics(
    val plays: Int,
    val likes: Int,
    val averageWatchTimeSeconds: Int,
    val audioAttribution: String
)

data class AnalyticsData(
    val followersGrowth: Int,
    val reach: Int,
    val engagementRate: Double,
    val profileVisits: Int,
    val topLocations: List<String>,
    val ageDistribution: Map<String, Double>,
    val genderDistribution: Map<String, Double>,
    val reachDetails: ReachAnalytics,
    val storyAnalytics: StoryAnalytics,
    val reelsAnalytics: ReelsAnalytics
)

data class PostPerformance(
    val postId: String,
    val impressions: Int,
    val reach: Int,
    val profileVisits: Int,
    val saves: Int,
    val shares: Int
)

data class AnalyticsState(
    val overview: AnalyticsData? = null,
    val recentPostsPerformance: List<PostPerformance> = emptyList(),
    val isLoading: Boolean = false
)

class AnalyticsViewModel : ViewModel() {
    private val _state = MutableStateFlow(AnalyticsState())
    val state: StateFlow<AnalyticsState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                overview = AnalyticsData(
                    followersGrowth = 450,
                    reach = 15400,
                    engagementRate = 4.8,
                    profileVisits = 1200,
                    topLocations = listOf("New York", "London", "Tokyo", "Paris"),
                    ageDistribution = mapOf("18-24" to 0.4, "25-34" to 0.45, "35-44" to 0.1, "45+" to 0.05),
                    genderDistribution = mapOf("Male" to 0.55, "Female" to 0.4, "Other" to 0.05),
                    reachDetails = ReachAnalytics(
                        followersReach = 10400,
                        nonFollowersReach = 5000,
                        paidReach = 2000,
                        organicReach = 13400
                    ),
                    storyAnalytics = StoryAnalytics(
                        impressions = 24000,
                        reach = 18000,
                        replies = 150,
                        exitRate = 12.5,
                        forwardRate = 65.0
                    ),
                    reelsAnalytics = ReelsAnalytics(
                        plays = 125000,
                        likes = 12000,
                        averageWatchTimeSeconds = 18,
                        audioAttribution = "Trending Audio: Summer Vibes"
                    )
                ),
                recentPostsPerformance = listOf(
                    PostPerformance("p1", 5000, 4800, 150, 45, 12),
                    PostPerformance("p2", 8200, 7500, 300, 120, 50),
                    PostPerformance("p3", 3100, 2900, 80, 20, 5)
                )
            )
        }
    }
}
