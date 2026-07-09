package com.example.ui.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SuggestedAccount(
    val id: String,
    val username: String,
    val reason: String,
    val isFollowed: Boolean = false
)

data class SuggestedAccountsState(
    val suggestions: List<SuggestedAccount> = emptyList(),
    val isRefreshing: Boolean = false
)

class SuggestedAccountsViewModel : ViewModel() {
    private val _state = MutableStateFlow(SuggestedAccountsState())
    val state: StateFlow<SuggestedAccountsState> = _state.asStateFlow()

    init {
        loadSuggestions()
    }

    fun loadSuggestions() {
        val mockData = listOf(
            SuggestedAccount("1", "@creative_soul", "Because you follow @art_daily"),
            SuggestedAccount("2", "@tech_guru", "From your contacts"),
            SuggestedAccount("3", "@nature_lover", "Mutual connection with @alice"),
            SuggestedAccount("4", "@foodie_eats", "Popular in Food category")
        )
        _state.update { it.copy(suggestions = mockData, isRefreshing = false) }
    }
    
    fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        loadSuggestions() // would add random or different data ideally
    }

    fun toggleFollow(id: String) {
        _state.update { s ->
            val updated = s.suggestions.map { if (it.id == id) it.copy(isFollowed = !it.isFollowed) else it }
            s.copy(suggestions = updated)
        }
    }

    fun dismissSuggestion(id: String) {
        _state.update { s ->
            s.copy(suggestions = s.suggestions.filter { it.id != id })
        }
    }

    fun followAll() {
        _state.update { s ->
            s.copy(suggestions = s.suggestions.map { it.copy(isFollowed = true) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestedAccountsScreen(
    viewModel: SuggestedAccountsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover People", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.followAll() }) {
                        Text("Follow All")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Suggested for you", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                TextButton(onClick = { viewModel.refresh() }) {
                    Text("Refresh")
                }
            }

            LazyColumn {
                items(state.suggestions) { account ->
                    SuggestedAccountItem(
                        account = account,
                        onFollowClick = { viewModel.toggleFollow(account.id) },
                        onDismissClick = { viewModel.dismissSuggestion(account.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SuggestedAccountItem(
    account: SuggestedAccount,
    onFollowClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Surface(
            modifier = Modifier.size(50.dp).clip(CircleShape),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {}
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(account.username, fontWeight = FontWeight.Bold)
            Text(
                text = account.reason,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Button(
            onClick = onFollowClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (account.isFollowed) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                contentColor = if (account.isFollowed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(if (account.isFollowed) "Following" else "Follow")
        }
        
        IconButton(onClick = onDismissClick) {
            Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
