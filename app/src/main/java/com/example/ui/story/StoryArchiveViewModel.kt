package com.example.ui.story

import androidx.lifecycle.ViewModel
import com.example.models.FeedMedia
import com.example.models.MediaType
import com.example.models.StorySegment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class StoryArchiveState(
    val archivedStories: List<StorySegment> = emptyList(),
    val searchQuery: String = "",
    val isSelectionMode: Boolean = false,
    val selectedIds: Set<String> = emptySet(),
    val storageUsedMb: Int = 145,
    val storageLimitMb: Int = 500
)

class StoryArchiveViewModel : ViewModel() {
    private val _state = MutableStateFlow(StoryArchiveState())
    val state: StateFlow<StoryArchiveState> = _state.asStateFlow()

    init {
        val now = System.currentTimeMillis()
        val day = 86400000L
        _state.update {
            it.copy(
                archivedStories = listOf(
                    StorySegment("a1", FeedMedia("https://example.com/a1.jpg", MediaType.IMAGE), now - 2 * day, now - 1 * day),
                    StorySegment("a2", FeedMedia("https://example.com/a2.jpg", MediaType.IMAGE), now - 5 * day, now - 4 * day),
                    StorySegment("a3", FeedMedia("https://example.com/a3.mp4", MediaType.VIDEO), now - 40 * day, now - 39 * day),
                    StorySegment("a4", FeedMedia("https://example.com/a4.jpg", MediaType.IMAGE), now - 300 * day, now - 299 * day)
                )
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun toggleSelectionMode() {
        _state.update { it.copy(isSelectionMode = !it.isSelectionMode, selectedIds = emptySet()) }
    }

    fun toggleSelection(id: String) {
        _state.update {
            val newSelection = if (it.selectedIds.contains(id)) {
                it.selectedIds - id
            } else {
                it.selectedIds + id
            }
            it.copy(selectedIds = newSelection)
        }
    }

    fun deleteSelected() {
        _state.update {
            val remaining = it.archivedStories.filterNot { s -> it.selectedIds.contains(s.id) }
            it.copy(archivedStories = remaining, isSelectionMode = false, selectedIds = emptySet())
        }
    }
    
    fun getGroupedStories(): Map<String, List<StorySegment>> {
        val format = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val filtered = state.value.archivedStories.filter { it.id.contains(state.value.searchQuery, ignoreCase = true) }
        return filtered.groupBy { format.format(Date(it.timestamp)) }
    }
}
