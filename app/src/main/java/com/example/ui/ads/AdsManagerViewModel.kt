package com.example.ui.ads

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AdCampaign(
    val id: String,
    val name: String,
    val objective: String,
    val status: String,
    val spent: Double,
    val budget: Double,
    val impressions: Int,
    val clicks: Int
)

data class AdsManagerState(
    val campaigns: List<AdCampaign> = emptyList(),
    val totalSpent: Double = 0.0,
    val showCreateDialog: Boolean = false
)

class AdsManagerViewModel : ViewModel() {
    private val _state = MutableStateFlow(AdsManagerState())
    val state: StateFlow<AdsManagerState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                campaigns = listOf(
                    AdCampaign("c1", "Summer Promo", "Traffic", "Active", 120.50, 500.0, 15000, 350),
                    AdCampaign("c2", "App Install Drive", "App Installs", "Paused", 450.00, 1000.0, 45000, 1200)
                ),
                totalSpent = 570.50
            )
        }
    }

    fun toggleCreateDialog() {
        _state.update { it.copy(showCreateDialog = !it.showCreateDialog) }
    }

    fun createCampaign(name: String, objective: String, budget: Double) {
        _state.update {
            val newCampaign = AdCampaign(
                id = "c${System.currentTimeMillis()}",
                name = name,
                objective = objective,
                status = "Pending Review",
                spent = 0.0,
                budget = budget,
                impressions = 0,
                clicks = 0
            )
            it.copy(
                campaigns = it.campaigns + newCampaign,
                showCreateDialog = false
            )
        }
    }
}
