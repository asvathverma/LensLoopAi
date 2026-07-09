package com.example.ui.monetization

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Subscriber(
    val id: String,
    val username: String,
    val tier: String,
    val joinDate: String
)

data class SubscriptionsState(
    val isSubscriptionsEnabled: Boolean = true,
    val activeSubscribers: List<Subscriber> = emptyList(),
    val monthlyRevenue: Double = 0.0,
    val subscriptionPrice: Double = 4.99
)

class SubscriptionsViewModel : ViewModel() {
    private val _state = MutableStateFlow(SubscriptionsState())
    val state: StateFlow<SubscriptionsState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                activeSubscribers = listOf(
                    Subscriber("s1", "superfan99", "Premium", "Oct 1, 2023"),
                    Subscriber("s2", "daily_viewer", "Standard", "Oct 15, 2023"),
                    Subscriber("s3", "love_your_content", "Standard", "Nov 2, 2023")
                ),
                monthlyRevenue = 14.97
            )
        }
    }

    fun toggleSubscriptions(enabled: Boolean) {
        _state.update { it.copy(isSubscriptionsEnabled = enabled) }
    }

    fun updatePrice(price: Double) {
        _state.update { it.copy(subscriptionPrice = price) }
    }
}
