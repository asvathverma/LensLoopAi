package com.example.ui.story

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class CameraMode { PHOTO, VIDEO, HANDS_FREE, BURST }
enum class FlashMode { OFF, ON, AUTO }

data class ArFilter(
    val id: String,
    val name: String,
    val category: String,
    val isFavorite: Boolean = false
)

data class StoryCameraState(
    val mode: CameraMode = CameraMode.PHOTO,
    val flashMode: FlashMode = FlashMode.OFF,
    val isFrontCamera: Boolean = false,
    val showGrid: Boolean = false,
    val showLevel: Boolean = false,
    val isRecording: Boolean = false,
    val recordingDurationSec: Int = 0,
    val zoomLevel: Float = 1f,
    val exposure: Float = 0f,
    val filters: List<ArFilter> = emptyList(),
    val selectedFilterId: String? = null,
    val beautyIntensity: Float = 0.5f,
    val isFocusLocked: Boolean = false
)

class StoryCameraViewModel : ViewModel() {
    private val _state = MutableStateFlow(StoryCameraState())
    val state: StateFlow<StoryCameraState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                filters = listOf(
                    ArFilter("f1", "Normal", "None"),
                    ArFilter("f2", "Smooth Skin", "Beauty"),
                    ArFilter("f3", "Vintage Film", "Color"),
                    ArFilter("f4", "Cyberpunk", "Color"),
                    ArFilter("f5", "Cat Ears", "3D Mask", isFavorite = true),
                    ArFilter("f6", "Neon Glow", "Environment")
                ),
                selectedFilterId = "f1"
            )
        }
    }

    fun toggleCamera() {
        _state.update { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    fun toggleFlash() {
        _state.update { 
            val nextFlash = when (it.flashMode) {
                FlashMode.OFF -> FlashMode.ON
                FlashMode.ON -> FlashMode.AUTO
                FlashMode.AUTO -> FlashMode.OFF
            }
            it.copy(flashMode = nextFlash)
        }
    }

    fun toggleGrid() {
        _state.update { it.copy(showGrid = !it.showGrid) }
    }

    fun toggleLevel() {
        _state.update { it.copy(showLevel = !it.showLevel) }
    }

    fun setMode(mode: CameraMode) {
        _state.update { it.copy(mode = mode) }
    }

    fun startRecording() {
        _state.update { it.copy(isRecording = true, recordingDurationSec = 0) }
    }

    fun stopRecording() {
        _state.update { it.copy(isRecording = false) }
    }

    fun updateRecordingDuration(sec: Int) {
        _state.update { it.copy(recordingDurationSec = sec) }
    }

    fun setZoom(zoom: Float) {
        _state.update { it.copy(zoomLevel = zoom.coerceIn(1f, 10f)) }
    }

    fun setExposure(exposure: Float) {
        _state.update { it.copy(exposure = exposure.coerceIn(-2f, 2f)) }
    }

    fun selectFilter(filterId: String) {
        _state.update { it.copy(selectedFilterId = filterId) }
    }
    
    fun setBeautyIntensity(intensity: Float) {
        _state.update { it.copy(beautyIntensity = intensity) }
    }
    
    fun toggleFocusLock() {
        _state.update { it.copy(isFocusLocked = !it.isFocusLocked) }
    }
}
