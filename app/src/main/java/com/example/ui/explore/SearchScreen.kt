package com.example.ui.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        OutlinedTextField(
                            value = state.query,
                            onValueChange = { viewModel.updateQuery(it) },
                            placeholder = { Text("Search") },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = { viewModel.toggleFilters() }) {
                                    Icon(Icons.Default.Tune, contentDescription = "Filters")
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                    }
                )
                if (state.query.isNotBlank()) {
                    TabRow(selectedTabIndex = state.tabs.indexOf(state.selectedTab)) {
                        state.tabs.forEach { tab ->
                            Tab(
                                selected = state.selectedTab == tab,
                                onClick = { viewModel.selectTab(tab) },
                                text = { Text(tab) }
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.query.isBlank()) {
                // Recent Searches & Alerts
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    if (state.savedSearchAlerts.isNotEmpty()) {
                        Text("Saved Alerts", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        LazyRow(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.savedSearchAlerts) { alert ->
                                InputChip(
                                    selected = true,
                                    onClick = { viewModel.updateQuery(alert) },
                                    label = { Text(alert) },
                                    leadingIcon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recent", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { viewModel.clearRecentSearches() }) {
                            Text("Clear all")
                        }
                    }
                    LazyColumn {
                        items(state.recentSearches) { recent ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.updateQuery(recent) }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(recent, modifier = Modifier.weight(1f))
                                IconButton(onClick = { viewModel.removeRecentSearch(recent) }) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            } else {
                // Search Results
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${state.results.size} results for '${state.query}'", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (!state.savedSearchAlerts.contains(state.query)) {
                            TextButton(onClick = { viewModel.saveSearchAlert() }) {
                                Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Create Alert")
                            }
                        }
                    }
                    
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.results) { result ->
                            ListItem(
                                headlineContent = { Text(result.title) },
                                supportingContent = { Text(result.subtitle) },
                                leadingContent = {
                                    val icon = when (result.type) {
                                        "user" -> Icons.Default.Person
                                        "hashtag" -> Icons.Default.Tag
                                        "location" -> Icons.Default.Place
                                        "audio" -> Icons.Default.MusicNote
                                        else -> Icons.Default.Search
                                    }
                                    Icon(icon, contentDescription = null)
                                },
                                modifier = Modifier.clickable { /* Handle click */ }
                            )
                        }
                    }
                }
            }
        }
    }

    if (state.showFilters) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.toggleFilters() },
            sheetState = sheetState
        ) {
            SearchFiltersContent(state.filters, onFiltersChanged = { viewModel.updateFilters(it) })
        }
    }
}

@Composable
fun SearchFiltersContent(filters: SearchFilters, onFiltersChanged: (SearchFilters) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).padding(bottom = 32.dp)) {
        Text("Advanced Filters", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Sort By", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Top", "Recent").forEach { option ->
                FilterChip(selected = filters.sortBy == option, onClick = { onFiltersChanged(filters.copy(sortBy = option)) }, label = { Text(option) })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("Account Type", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Personal", "Business", "Verified").forEach { option ->
                FilterChip(selected = filters.accountType == option, onClick = { onFiltersChanged(filters.copy(accountType = if(filters.accountType == option) null else option)) }, label = { Text(option) })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Text("Content Type", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Photo", "Video", "Reels").forEach { option ->
                FilterChip(selected = filters.contentType == option, onClick = { onFiltersChanged(filters.copy(contentType = if(filters.contentType == option) null else option)) }, label = { Text(option) })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("Date Range", fontWeight = FontWeight.SemiBold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("Today", "This Week", "This Month", "Anytime")) { option ->
                FilterChip(selected = filters.dateRange == option, onClick = { onFiltersChanged(filters.copy(dateRange = if(filters.dateRange == option) null else option)) }, label = { Text(option) })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Text("Minimum Likes: ${if (filters.minLikes == 0) "Any" else filters.minLikes}", fontWeight = FontWeight.SemiBold)
        Slider(
            value = filters.minLikes.toFloat(),
            onValueChange = { onFiltersChanged(filters.copy(minLikes = it.toInt())) },
            valueRange = 0f..10000f,
            steps = 100
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("Location Radius: ${filters.locationRadiusKm} km", fontWeight = FontWeight.SemiBold)
        Slider(
            value = filters.locationRadiusKm.toFloat(),
            onValueChange = { onFiltersChanged(filters.copy(locationRadiusKm = it.toInt())) },
            valueRange = 5f..100f,
            steps = 20
        )
    }
}
