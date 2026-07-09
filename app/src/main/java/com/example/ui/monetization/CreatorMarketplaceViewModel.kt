package com.example.ui.monetization

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MarketplaceCampaign(
    val id: String,
    val brandName: String,
    val title: String,
    val description: String,
    val payout: Double,
    val category: String,
    val requirements: String
)

data class CreatorMarketplaceState(
    val availableCampaigns: List<MarketplaceCampaign> = emptyList(),
    val appliedCampaigns: List<String> = emptyList(),
    val selectedCategory: String = "All"
)

class CreatorMarketplaceViewModel : ViewModel() {
    private val _state = MutableStateFlow(CreatorMarketplaceState())
    val state: StateFlow<CreatorMarketplaceState> = _state.asStateFlow()

    private val allCampaigns = listOf(
        MarketplaceCampaign("mc1", "FitLife", "Summer Fitness Challenge", "Promote our new pre-workout supplement.", 500.0, "Fitness", "Min. 10k followers"),
        MarketplaceCampaign("mc2", "TechGuru", "Laptop Review", "Review our newest gaming laptop.", 1200.0, "Tech", "Tech niche"),
        MarketplaceCampaign("mc3", "StyleBox", "Fall Wardrobe", "Create a Reel styling our new fall collection.", 300.0, "Fashion", "High engagement rate"),
        MarketplaceCampaign("mc4", "Gourmet Kitchen", "Cooking Tutorial", "Use our blender to make a smoothie bowl.", 450.0, "Food", "Min. 5k followers")
    )

    init {
        _state.update { it.copy(availableCampaigns = allCampaigns) }
    }

    fun applyFilter(category: String) {
        _state.update {
            val filtered = if (category == "All") allCampaigns else allCampaigns.filter { c -> c.category == category }
            it.copy(selectedCategory = category, availableCampaigns = filtered)
        }
    }

    fun applyToCampaign(id: String) {
        _state.update {
            if (!it.appliedCampaigns.contains(id)) {
                it.copy(appliedCampaigns = it.appliedCampaigns + id)
            } else {
                it
            }
        }
    }
}
