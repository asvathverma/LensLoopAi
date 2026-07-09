package com.example.ui.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.CollectionPrivacy
import com.example.models.PostCollection
import com.example.models.UserShort
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CollectionsState(
    val collections: List<PostCollection> = emptyList(),
    val isLoading: Boolean = true
)

class CollectionsViewModel : ViewModel() {
    private val _state = MutableStateFlow(CollectionsState())
    val state: StateFlow<CollectionsState> = _state.asStateFlow()

    init {
        loadCollections()
    }

    private fun loadCollections() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(500)
            val mockCollections = listOf(
                PostCollection("col1", "All Posts", null, CollectionPrivacy.PRIVATE, false, "me"),
                PostCollection("col2", "Design Inspiration", null, CollectionPrivacy.SHARED, true, "me", listOf(UserShort("u1", "designer_friend", null)))
            )
            _state.update { it.copy(collections = mockCollections, isLoading = false) }
        }
    }

    fun createCollection(name: String, privacy: CollectionPrivacy) {
        val newCol = PostCollection(
            id = "col_${System.currentTimeMillis()}",
            name = name,
            coverImageUrl = null,
            privacy = privacy,
            isCollaborative = privacy == CollectionPrivacy.SHARED,
            ownerId = "me"
        )
        _state.update { it.copy(collections = it.collections + newCol) }
    }
}
