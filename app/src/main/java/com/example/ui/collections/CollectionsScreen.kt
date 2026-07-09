package com.example.ui.collections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.models.CollectionPrivacy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(
    viewModel: CollectionsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Collections", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "New Collection")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.collections) { collection ->
                        ListItem(
                            headlineContent = { Text(collection.name, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("${collection.savedPosts.size} posts") },
                            leadingContent = {
                                Box(modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Folder, contentDescription = null)
                                }
                            },
                            trailingContent = {
                                Icon(
                                    imageVector = when(collection.privacy) {
                                        CollectionPrivacy.PRIVATE -> Icons.Default.Lock
                                        CollectionPrivacy.SHARED -> Icons.Default.Group
                                        CollectionPrivacy.PUBLIC -> Icons.Default.Public
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
        
        if (showCreateDialog) {
            var name by remember { mutableStateOf("") }
            var privacy by remember { mutableStateOf(CollectionPrivacy.PRIVATE) }
            
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("New Collection") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            singleLine = true
                        )
                        Text("Privacy", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = privacy == CollectionPrivacy.PRIVATE,
                                onClick = { privacy = CollectionPrivacy.PRIVATE },
                                label = { Text("Private") }
                            )
                            FilterChip(
                                selected = privacy == CollectionPrivacy.SHARED,
                                onClick = { privacy = CollectionPrivacy.SHARED },
                                label = { Text("Shared") }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (name.isNotBlank()) {
                            viewModel.createCollection(name, privacy)
                            showCreateDialog = false
                        }
                    }) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
