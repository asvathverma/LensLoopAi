package com.example.ui.story

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StoryArchiveScreen(
    viewModel: StoryArchiveViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val groupedStories = viewModel.getGroupedStories()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isSelectionMode) "${state.selectedIds.size} Selected" else "Story Archive") },
                navigationIcon = {
                    IconButton(onClick = { if (state.isSelectionMode) viewModel.toggleSelectionMode() else onNavigateBack() }) {
                        Icon(if (state.isSelectionMode) Icons.Default.Close else Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.isSelectionMode) {
                        IconButton(onClick = { viewModel.deleteSelected() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                        IconButton(onClick = { /* Share */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    } else {
                        IconButton(onClick = { viewModel.toggleSelectionMode() }) {
                            Icon(Icons.Default.Checklist, contentDescription = "Select")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Storage Quota
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Cloud, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Storage: ${state.storageUsedMb} MB / ${state.storageLimitMb} MB", style = MaterialTheme.typography.bodySmall)
                    LinearProgressIndicator(
                        progress = { state.storageUsedMb.toFloat() / state.storageLimitMb },
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    )
                }
            }
            
            // Search
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search archive...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
            
            // Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                groupedStories.forEach { (month, stories) ->
                    item(span = { GridItemSpan(3) }) {
                        Text(
                            text = month,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    items(stories) { story ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(9f / 16f)
                                .background(Color.Gray)
                                .clickable {
                                    if (state.isSelectionMode) {
                                        viewModel.toggleSelection(story.id)
                                    } else {
                                        // Open story
                                    }
                                }
                        ) {
                            AsyncImage(
                                model = story.media.url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (state.isSelectionMode) {
                                val isSelected = state.selectedIds.contains(story.id)
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color.White, CircleShape)
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.3f))
                                        .align(Alignment.TopEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
