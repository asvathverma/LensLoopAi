package com.example.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ReportedContent(val id: String, val type: String, val reason: String, val status: String, val date: String)

data class ContentModerationState(
    val reportedItems: List<ReportedContent> = emptyList(),
    val isAppealing: Boolean = false
)

class ContentModerationViewModel : ViewModel() {
    private val _state = MutableStateFlow(ContentModerationState())
    val state: StateFlow<ContentModerationState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                reportedItems = listOf(
                    ReportedContent("1", "Post", "Spam", "Pending Review", "2 days ago"),
                    ReportedContent("2", "Comment", "Harassment", "Removed", "1 week ago")
                )
            )
        }
    }
    
    fun submitAppeal(reportId: String) {
        _state.update { s ->
            val updatedItems = s.reportedItems.map {
                if (it.id == reportId) it.copy(status = "Appeal Pending") else it
            }
            s.copy(reportedItems = updatedItems)
        }
    }
}
