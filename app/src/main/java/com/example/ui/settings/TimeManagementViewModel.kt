package com.example.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DailyUsage(val day: String, val minutes: Int)

data class TimeManagementState(
    val takeABreakReminder: Int? = null, // minutes
    val dailyTimeLimit: Int? = null, // minutes
    val hardStopEnabled: Boolean = false,
    val quietModeEnabled: Boolean = false,
    val quietModeStart: String = "22:00",
    val quietModeEnd: String = "07:00",
    val bedtimeModeEnabled: Boolean = false, // grayscale UI
    val autoReplyEnabled: Boolean = false,
    val exceptionList: List<String> = emptyList(), // usernames
    val weeklyUsage: List<DailyUsage> = emptyList(),
    val averageDailyUsage: Int = 0
)

class TimeManagementViewModel : ViewModel() {
    private val _state = MutableStateFlow(TimeManagementState())
    val state: StateFlow<TimeManagementState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                weeklyUsage = listOf(
                    DailyUsage("Mon", 45),
                    DailyUsage("Tue", 60),
                    DailyUsage("Wed", 30),
                    DailyUsage("Thu", 90),
                    DailyUsage("Fri", 120),
                    DailyUsage("Sat", 150),
                    DailyUsage("Sun", 110)
                ),
                averageDailyUsage = 86,
                exceptionList = listOf("bestie123", "mom")
            )
        }
    }

    fun setTakeABreakReminder(minutes: Int?) {
        _state.update { it.copy(takeABreakReminder = minutes) }
    }

    fun setDailyTimeLimit(minutes: Int?) {
        _state.update { it.copy(dailyTimeLimit = minutes) }
    }

    fun toggleHardStop() {
        _state.update { it.copy(hardStopEnabled = !it.hardStopEnabled) }
    }

    fun toggleQuietMode() {
        _state.update { it.copy(quietModeEnabled = !it.quietModeEnabled) }
    }
    
    fun toggleBedtimeMode() {
        _state.update { it.copy(bedtimeModeEnabled = !it.bedtimeModeEnabled) }
    }
    
    fun toggleAutoReply() {
        _state.update { it.copy(autoReplyEnabled = !it.autoReplyEnabled) }
    }
}
