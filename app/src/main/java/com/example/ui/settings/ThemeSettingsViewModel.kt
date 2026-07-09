package com.example.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ThemeSettingsState(
    val isDarkMode: Boolean = false,
    val useSystemTheme: Boolean = true,
    val accentColor: String = "Default",
    val highContrast: Boolean = false,
    val reducedMotion: Boolean = false,
    val layoutDensity: String = "Comfortable"
)

class ThemeSettingsViewModel : ViewModel() {
    private val _state = MutableStateFlow(ThemeSettingsState())
    val state: StateFlow<ThemeSettingsState> = _state.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) {
        _state.update { it.copy(isDarkMode = enabled, useSystemTheme = false) }
    }

    fun toggleSystemTheme(enabled: Boolean) {
        _state.update { it.copy(useSystemTheme = enabled) }
    }

    fun setAccentColor(color: String) {
        _state.update { it.copy(accentColor = color) }
    }

    fun toggleHighContrast(enabled: Boolean) {
        _state.update { it.copy(highContrast = enabled) }
    }
    
    fun toggleReducedMotion(enabled: Boolean) {
        _state.update { it.copy(reducedMotion = enabled) }
    }
    
    fun setLayoutDensity(density: String) {
        _state.update { it.copy(layoutDensity = density) }
    }
}
