package com.example.ui.monetization

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class VirtualEconomyState(
    val coinBalance: Int = 1500,
    val earningsBalance: Double = 150.0,
    val badgesReceived: Int = 45,
    val giftsReceived: Int = 12
)

class VirtualEconomyViewModel : ViewModel() {
    private val _state = MutableStateFlow(VirtualEconomyState())
    val state: StateFlow<VirtualEconomyState> = _state.asStateFlow()

    fun convertCoinsToCash(coins: Int) {
        val conversionRate = 0.01 // 1 coin = 1 cent
        if (coins > 0 && coins <= _state.value.coinBalance) {
            _state.update {
                it.copy(
                    coinBalance = it.coinBalance - coins,
                    earningsBalance = it.earningsBalance + (coins * conversionRate)
                )
            }
        }
    }
}
