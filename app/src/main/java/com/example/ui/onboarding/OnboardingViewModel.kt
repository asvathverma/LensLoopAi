package com.example.ui.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.auth.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Account(val id: String, val name: String, val handle: String, val category: String, val isFollowing: Boolean = false)

data class OnboardingState(
    val currentStep: Int = 0,
    val selectedInterests: Set<String> = emptySet(),
    val suggestedAccounts: List<Account> = emptyList()
)

class OnboardingViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    val availableInterests = listOf(
        "Photography", "Travel", "Food", "Fashion", "Technology",
        "Art", "Music", "Fitness", "Design", "Gaming"
    )

    private val allAccounts = listOf(
        Account("1", "National Geographic", "@natgeo", "Photography"),
        Account("2", "Travel Channel", "@travelchannel", "Travel"),
        Account("3", "Gordon Ramsay", "@gordongram", "Food"),
        Account("4", "Vogue", "@voguemagazine", "Fashion"),
        Account("5", "Marques Brownlee", "@mkbhd", "Technology"),
        Account("6", "Tate", "@tate", "Art"),
        Account("7", "Pitchfork", "@pitchfork", "Music"),
        Account("8", "Nike Training", "@niketraining", "Fitness"),
        Account("9", "Behance", "@behance", "Design"),
        Account("10", "IGN", "@ign", "Gaming")
    )

    fun toggleInterest(interest: String) {
        _state.update { currentState ->
            val newInterests = currentState.selectedInterests.toMutableSet()
            if (newInterests.contains(interest)) {
                newInterests.remove(interest)
            } else {
                newInterests.add(interest)
            }
            currentState.copy(selectedInterests = newInterests)
        }
    }

    fun generateSuggestedAccounts() {
        val selected = _state.value.selectedInterests
        val suggested = if (selected.isEmpty()) {
            allAccounts.take(5)
        } else {
            allAccounts.filter { selected.contains(it.category) }.take(5)
        }
        _state.update { it.copy(suggestedAccounts = suggested) }
    }

    fun toggleFollowAccount(accountId: String) {
        _state.update { currentState ->
            val updatedAccounts = currentState.suggestedAccounts.map {
                if (it.id == accountId) it.copy(isFollowing = !it.isFollowing) else it
            }
            currentState.copy(suggestedAccounts = updatedAccounts)
        }
    }

    fun nextStep() {
        _state.update { currentState ->
            if (currentState.currentStep == 0) {
                generateSuggestedAccounts()
            }
            trackStepCompletion(currentState.currentStep)
            currentState.copy(currentStep = currentState.currentStep + 1)
        }
    }
    
    fun skipStep() {
        _state.update { currentState ->
            trackStepSkipped(currentState.currentStep)
            if (currentState.currentStep == 0) {
                 generateSuggestedAccounts()
            }
            currentState.copy(currentStep = currentState.currentStep + 1)
        }
    }

    fun previousStep() {
        _state.update { currentState ->
            if (currentState.currentStep > 0) {
                currentState.copy(currentStep = currentState.currentStep - 1)
            } else {
                currentState
            }
        }
    }

    fun completeOnboarding() {
        trackStepCompletion(2) // Final step
        tokenManager.setOnboardingCompleted(true)
    }
    
    // Mock Analytics Tracking
    private fun trackStepCompletion(step: Int) {
        val stepName = when(step) {
            0 -> "Interests Selection"
            1 -> "Accounts Follow"
            2 -> "Feature Tutorial"
            else -> "Unknown Step"
        }
        Log.d("Analytics", "User completed onboarding step: $stepName")
    }
    
    private fun trackStepSkipped(step: Int) {
        val stepName = when(step) {
            0 -> "Interests Selection"
            1 -> "Accounts Follow"
            2 -> "Feature Tutorial"
            else -> "Unknown Step"
        }
        Log.d("Analytics", "User skipped onboarding step: $stepName")
    }
}
