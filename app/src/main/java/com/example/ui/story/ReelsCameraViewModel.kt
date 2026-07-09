package com.example.ui.story

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ReelClip(val id: String, val durationSec: Int)

data class ReelsCameraState(
    val isRecording: Boolean = false,
    val clips: List<ReelClip> = emptyList(),
    val maxDurationSec: Int = 15,
    val currentDurationSec: Int = 0,
    val showTimer: Boolean = false,
    val timerSeconds: Int = 0,
    val isTimerActive: Boolean = false,
    val recordingSpeed: Float = 1.0f,
    val isDualCamera: Boolean = false,
    val showGrid: Boolean = false,
    val alignGhosting: Boolean = false,
    val flashOn: Boolean = false,
    val isFrontCamera: Boolean = true,
    val hasDrafts: Boolean = true,
    val selectedMusicId: String? = null,
    val selectedMusicTitle: String? = null,
    val isVoiceOverEnabled: Boolean = false,
    val hasExternalMic: Boolean = false,
    val isAudioDuckingEnabled: Boolean = false,
    val showAudioMenu: Boolean = false,
    val showEffectsMenu: Boolean = false,
    val showTemplatesMenu: Boolean = false,
    val activeEffect: String? = null,
    val activeTemplate: String? = null,
    val isRemixMode: Boolean = false,
    val remixLayout: String = "LeftRight", // LeftRight, TopBottom, PiP
    val originalReelUrl: String? = null
)

class ReelsCameraViewModel : ViewModel() {
    private val _state = MutableStateFlow(ReelsCameraState())
    val state: StateFlow<ReelsCameraState> = _state.asStateFlow()

    fun toggleCamera() {
        _state.update { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    fun toggleFlash() {
        _state.update { it.copy(flashOn = !it.flashOn) }
    }

    fun toggleGrid() {
        _state.update { it.copy(showGrid = !it.showGrid) }
    }

    fun toggleAlignGhosting() {
        _state.update { it.copy(alignGhosting = !it.alignGhosting) }
    }

    fun cycleSpeed() {
        _state.update {
            val nextSpeed = when (it.recordingSpeed) {
                0.3f -> 0.5f
                0.5f -> 1f
                1f -> 2f
                2f -> 3f
                else -> 0.3f
            }
            it.copy(recordingSpeed = nextSpeed)
        }
    }

    fun cycleTimer() {
        _state.update {
            val nextTimer = when (it.timerSeconds) {
                0 -> 3
                3 -> 10
                else -> 0
            }
            it.copy(timerSeconds = nextTimer, showTimer = nextTimer > 0)
        }
    }
    
    fun toggleDualCamera() {
        _state.update { it.copy(isDualCamera = !it.isDualCamera) }
    }
    
    fun selectMusic(id: String, title: String) {
        _state.update { it.copy(selectedMusicId = id, selectedMusicTitle = title) }
    }
    
    fun toggleExternalMic() {
        _state.update { it.copy(hasExternalMic = !it.hasExternalMic) }
    }
    
    fun toggleAudioMenu() {
        _state.update { it.copy(showAudioMenu = !it.showAudioMenu) }
    }
    
    fun toggleEffectsMenu() {
        _state.update { it.copy(showEffectsMenu = !it.showEffectsMenu) }
    }
    
    fun toggleTemplatesMenu() {
        _state.update { it.copy(showTemplatesMenu = !it.showTemplatesMenu) }
    }
    
    fun setEffect(effect: String?) {
        _state.update { it.copy(activeEffect = effect, showEffectsMenu = false) }
    }
    
    fun setTemplate(template: String?) {
        _state.update { it.copy(activeTemplate = template, showTemplatesMenu = false) }
    }

    fun startRemix(url: String) {
        _state.update { it.copy(isRemixMode = true, originalReelUrl = url) }
    }
    
    fun cycleRemixLayout() {
        _state.update { 
            val nextLayout = when (it.remixLayout) {
                "LeftRight" -> "TopBottom"
                "TopBottom" -> "PiP"
                else -> "LeftRight"
            }
            it.copy(remixLayout = nextLayout)
        }
    }

    fun toggleVoiceOver() {
        _state.update { it.copy(isVoiceOverEnabled = !it.isVoiceOverEnabled) }
    }
    
    fun toggleAudioDucking() {
        _state.update { it.copy(isAudioDuckingEnabled = !it.isAudioDuckingEnabled) }
    }

    fun setMaxDuration(duration: Int) {
        _state.update { it.copy(maxDurationSec = duration) }
    }

    fun startTimer() {
        _state.update { it.copy(isTimerActive = true) }
    }
    
    fun timerFinished() {
        _state.update { it.copy(isTimerActive = false) }
        startRecording()
    }
    
    fun startRecording() {
        _state.update { it.copy(isRecording = true) }
    }

    fun stopRecording(clipDurationSec: Int) {
        _state.update { 
            val newClip = ReelClip("c${it.clips.size + 1}", clipDurationSec)
            val newDuration = it.currentDurationSec + clipDurationSec
            it.copy(isRecording = false, clips = it.clips + newClip, currentDurationSec = newDuration)
        }
    }

    fun deleteLastClip() {
        _state.update {
            if (it.clips.isNotEmpty()) {
                val lastClip = it.clips.last()
                it.copy(
                    clips = it.clips.dropLast(1),
                    currentDurationSec = it.currentDurationSec - lastClip.durationSec
                )
            } else {
                it
            }
        }
    }
}
