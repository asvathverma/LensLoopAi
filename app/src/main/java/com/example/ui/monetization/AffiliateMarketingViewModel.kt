package com.example.ui.monetization

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AffiliateLink(
    val id: String,
    val productName: String,
    val url: String,
    val clicks: Int,
    val conversions: Int,
    val earnings: Double
)

data class AffiliateMarketingState(
    val links: List<AffiliateLink> = emptyList(),
    val totalEarnings: Double = 0.0,
    val showCreateDialog: Boolean = false
)

class AffiliateMarketingViewModel : ViewModel() {
    private val _state = MutableStateFlow(AffiliateMarketingState())
    val state: StateFlow<AffiliateMarketingState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                links = listOf(
                    AffiliateLink("l1", "Noise Cancelling Headphones", "lensloop.link/h12", 450, 12, 120.0),
                    AffiliateLink("l2", "4K Camera Drone", "lensloop.link/d4k", 1200, 5, 250.0)
                ),
                totalEarnings = 370.0
            )
        }
    }

    fun toggleCreateDialog() {
        _state.update { it.copy(showCreateDialog = !it.showCreateDialog) }
    }

    fun generateLink(productName: String, originalUrl: String) {
        val newLink = AffiliateLink(
            id = "l${System.currentTimeMillis()}",
            productName = productName,
            url = "lensloop.link/${productName.take(3).lowercase()}${System.currentTimeMillis() % 1000}",
            clicks = 0,
            conversions = 0,
            earnings = 0.0
        )
        _state.update {
            it.copy(
                links = it.links + newLink,
                showCreateDialog = false
            )
        }
    }
}
