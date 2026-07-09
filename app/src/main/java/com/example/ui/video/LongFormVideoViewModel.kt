package com.example.ui.video

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Chapter(val timeSec: Int, val title: String)

data class LongFormVideoState(
    val title: String = "",
    val description: String = "",
    val seriesId: String? = null,
    val previewClipStartSec: Int = 0,
    val chapters: List<Chapter> = emptyList(),
    val isMonetizationEligible: Boolean = true,
    val hasAdsEnabled: Boolean = false,
    val videoSelected: Boolean = false,
    val isUploading: Boolean = false
)

class LongFormVideoViewModel : ViewModel() {
    private val _state = MutableStateFlow(LongFormVideoState())
    val state: StateFlow<LongFormVideoState> = _state.asStateFlow()

    fun updateTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun updateDescription(desc: String) {
        _state.update { it.copy(description = desc) }
    }

    fun selectVideo() {
        _state.update { it.copy(videoSelected = true) }
    }
    
    fun toggleAds() {
        _state.update { it.copy(hasAdsEnabled = !it.hasAdsEnabled) }
    }
    
    fun addChapter(timeSec: Int, title: String) {
        _state.update { 
            val newChapters = (it.chapters + Chapter(timeSec, title)).sortedBy { c -> c.timeSec }
            it.copy(chapters = newChapters)
        }
    }

    fun uploadVideo() {
        _state.update { it.copy(isUploading = true) }
    }
}
