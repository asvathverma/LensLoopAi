package com.example.ui.explore

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SearchResult(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: String // user, hashtag, location, audio
)

data class SearchFilters(
    val accountType: String? = null, // "Personal", "Business", "Verified"
    val contentType: String? = null, // "Photo", "Video", "Reels"
    val dateRange: String? = null, // "Today", "This Week", "This Month", "Anytime"
    val locationRadiusKm: Int = 50,
    val minLikes: Int = 0,
    val sortBy: String = "Top" // "Top", "Recent"
)

data class SearchState(
    val query: String = "",
    val recentSearches: List<String> = listOf("nature photography", "viral audio", "new york", "#travel"),
    val selectedTab: String = "Top",
    val tabs: List<String> = listOf("Top", "Accounts", "Audio", "Tags", "Places"),
    val results: List<SearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val filters: SearchFilters = SearchFilters(),
    val showFilters: Boolean = false,
    val savedSearchAlerts: List<String> = emptyList()
)

class SearchViewModel : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    fun updateQuery(query: String) {
        _state.update { it.copy(query = query, isSearching = query.isNotBlank()) }
        if (query.isNotBlank()) {
            performSearch()
        } else {
            _state.update { it.copy(results = emptyList()) }
        }
    }

    fun selectTab(tab: String) {
        _state.update { it.copy(selectedTab = tab) }
        performSearch()
    }
    
    fun clearRecentSearches() {
        _state.update { it.copy(recentSearches = emptyList()) }
    }
    
    fun removeRecentSearch(query: String) {
        _state.update { it.copy(recentSearches = it.recentSearches.filter { r -> r != query }) }
    }

    fun toggleFilters() {
        _state.update { it.copy(showFilters = !it.showFilters) }
    }

    fun updateFilters(newFilters: SearchFilters) {
        _state.update { it.copy(filters = newFilters) }
        performSearch()
    }

    fun saveSearchAlert() {
        val query = _state.value.query
        if (query.isNotBlank() && !_state.value.savedSearchAlerts.contains(query)) {
            _state.update { it.copy(savedSearchAlerts = it.savedSearchAlerts + query) }
        }
    }

    private fun performSearch() {
        val query = _state.value.query
        if (query.isBlank()) return
        
        val allMockResults = listOf(
            SearchResult("u1", "alice_smith", "Alice Smith", "user"),
            SearchResult("h1", "#nature", "12M posts", "hashtag"),
            SearchResult("l1", "New York City", "City in New York", "location"),
            SearchResult("a1", "Trending Song", "Original Audio", "audio")
        )
        
        val filteredByType = when (_state.value.selectedTab) {
            "Accounts" -> allMockResults.filter { it.type == "user" }
            "Tags" -> allMockResults.filter { it.type == "hashtag" }
            "Places" -> allMockResults.filter { it.type == "location" }
            "Audio" -> allMockResults.filter { it.type == "audio" }
            else -> allMockResults
        }
        
        _state.update { it.copy(results = filteredByType) }
    }
}
