package com.example.ui.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LongFormVideoUploadScreen(
    viewModel: LongFormVideoViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    var chapterTime by remember { mutableStateOf("") }
    var chapterTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Long-Form Video") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.uploadVideo() },
                        enabled = state.videoSelected && state.title.isNotBlank() && !state.isUploading
                    ) {
                        Text("Upload")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isUploading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Uploading...", style = MaterialTheme.typography.titleMedium)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Video Selection
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.videoSelected) {
                        Text("Video Selected (60:00 max)", style = MaterialTheme.typography.titleMedium)
                    } else {
                        Button(onClick = { viewModel.selectVideo() }) {
                            Icon(Icons.Default.VideoLibrary, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Select Video")
                        }
                    }
                }
                
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )
                
                // Series Selection
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text("Add to Series") },
                        supportingContent = { Text(state.seriesId ?: "None selected") },
                        trailingContent = { Icon(Icons.Default.Add, contentDescription = null) }
                    )
                }
                
                Divider()
                
                // Chapters
                Text("Chapters", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = chapterTime,
                        onValueChange = { chapterTime = it },
                        label = { Text("MM:SS") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = chapterTitle,
                        onValueChange = { chapterTitle = it },
                        label = { Text("Chapter Title") },
                        modifier = Modifier.weight(2f),
                        singleLine = true
                    )
                    Button(
                        onClick = { 
                            // Parse MM:SS to seconds (simplified)
                            val parts = chapterTime.split(":")
                            if (parts.size == 2) {
                                val sec = (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)
                                viewModel.addChapter(sec, chapterTitle)
                                chapterTime = ""
                                chapterTitle = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text("Add")
                    }
                }
                
                if (state.chapters.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        state.chapters.forEach { chapter ->
                            val mm = chapter.timeSec / 60
                            val ss = chapter.timeSec % 60
                            val timeStr = String.format("%02d:%02d", mm, ss)
                            Text("$timeStr - ${chapter.title}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                
                Divider()
                
                // Monetization
                if (state.isMonetizationEligible) {
                    ListItem(
                        headlineContent = { Text("Monetization") },
                        supportingContent = { Text("Insert mid-roll ads into this video") },
                        leadingContent = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        trailingContent = {
                            Switch(
                                checked = state.hasAdsEnabled,
                                onCheckedChange = { viewModel.toggleAds() }
                            )
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
