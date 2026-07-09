package com.example.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ParentalControlsState(
    val isSupervisionActive: Boolean = false,
    val supervisorName: String? = null,
    val dailyTimeLimitMinutes: Int = 120, // 2 hours default
    val downtimeStart: String = "22:00",
    val downtimeEnd: String = "07:00",
    val filterSensitiveContent: Boolean = true,
    val restrictDMsToFollowers: Boolean = true
)

class ParentalControlsViewModel : ViewModel() {
    private val _state = MutableStateFlow(ParentalControlsState())
    val state: StateFlow<ParentalControlsState> = _state.asStateFlow()

    init {
        // Mock active supervision for demo
        _state.update {
            it.copy(
                isSupervisionActive = true,
                supervisorName = "Jane Doe (Parent)"
            )
        }
    }

    fun updateTimeLimit(minutes: Int) {
        _state.update { it.copy(dailyTimeLimitMinutes = minutes) }
    }
    
    fun toggleContentFilter() {
        _state.update { it.copy(filterSensitiveContent = !it.filterSensitiveContent) }
    }
    
    fun toggleDMRestriction() {
        _state.update { it.copy(restrictDMsToFollowers = !it.restrictDMsToFollowers) }
    }
}
