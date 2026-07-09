package com.example.ui.story

import androidx.lifecycle.ViewModel
import com.example.models.MusicTrack
import com.example.models.StorySticker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class StoryEditorState(
    val selectedStickers: List<StorySticker> = emptyList(),
    val showStickerPicker: Boolean = false,
    val showMusicPicker: Boolean = false,
    val trendingMusic: List<MusicTrack> = emptyList(),
    val searchQuery: String = "",
    val activeStickerConfig: StickerConfigType? = null
)

enum class StickerConfigType { POLL, QUIZ, QUESTION, COUNTDOWN, SLIDER, LOCATION, HASHTAG, MENTION, LINK }

class StoryEditorViewModel : ViewModel() {
    private val _state = MutableStateFlow(StoryEditorState())
    val state: StateFlow<StoryEditorState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                trendingMusic = listOf(
                    MusicTrack("m1", "Blinding Lights", "The Weeknd", "", 200000, true),
                    MusicTrack("m2", "Watermelon Sugar", "Harry Styles", "", 180000, true),
                    MusicTrack("m3", "Levitating", "Dua Lipa", "", 210000, true)
                )
            )
        }
    }

    fun toggleStickerPicker() {
        _state.update { it.copy(showStickerPicker = !it.showStickerPicker) }
    }

    fun openMusicPicker() {
        _state.update { it.copy(showStickerPicker = false, showMusicPicker = true) }
    }

    fun closeMusicPicker() {
        _state.update { it.copy(showMusicPicker = false) }
    }
    
    fun openStickerConfig(type: StickerConfigType) {
        _state.update { it.copy(showStickerPicker = false, activeStickerConfig = type) }
    }
    
    fun closeStickerConfig() {
        _state.update { it.copy(activeStickerConfig = null) }
    }

    fun addSticker(sticker: StorySticker) {
        _state.update { it.copy(selectedStickers = it.selectedStickers + sticker, activeStickerConfig = null, showStickerPicker = false, showMusicPicker = false) }
    }
    
    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }
}
