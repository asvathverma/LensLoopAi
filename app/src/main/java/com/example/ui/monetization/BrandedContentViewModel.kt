package com.example.ui.monetization

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CampaignBrief(
    val id: String,
    val brandName: String,
    val title: String,
    val description: String,
    val status: String // "Pending", "Approved", "Revision Requested"
)

data class BrandedContentState(
    val campaigns: List<CampaignBrief> = emptyList(),
    val totalEarnings: Double = 0.0,
    val complianceStatus: String = "Compliant"
)

class BrandedContentViewModel : ViewModel() {
    private val _state = MutableStateFlow(BrandedContentState())
    val state: StateFlow<BrandedContentState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                campaigns = listOf(
                    CampaignBrief("c1", "Nike", "Summer Collection Launch", "Create 1 Reel and 2 Stories wearing the new sneakers.", "Approved"),
                    CampaignBrief("c2", "TechGadgets", "Wireless Earbuds Review", "Honest review of our new noise-cancelling earbuds.", "Pending"),
                    CampaignBrief("c3", "HealthyBites", "Protein Bar Promo", "Show yourself eating the protein bar after a workout.", "Revision Requested")
                ),
                totalEarnings = 1450.0
            )
        }
    }
}
