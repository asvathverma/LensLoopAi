package com.example.ui.hashtag

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.feed.PostItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HashtagScreen(
    viewModel: HashtagViewModel,
    hashtagName: String,
    onNavigateBack: () -> Unit,
    onNavigateToHashtag: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(hashtagName) {
        viewModel.loadHashtag(hashtagName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("#$hashtagName", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                state.analytics?.let { analytics ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(80.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(40.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Tag, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                                
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "%,d".format(analytics.hashtag.postCount),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "posts",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                Button(
                                    onClick = { viewModel.toggleFollowHashtag() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (analytics.hashtag.isFollowing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                                        contentColor = if (analytics.hashtag.isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text(if (analytics.hashtag.isFollowing) "Following" else "Follow")
                                }
                            }
                        }
                        
                        item {
                            Text("Related Hashtags", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(analytics.relatedHashtags) { related ->
                                    AssistChip(
                                        onClick = { onNavigateToHashtag(related.name) },
                                        label = { Text("#${related.name}") }
                                    )
                                }
                            }
                        }
                        
                        item {
                            HorizontalDivider()
                            Text("Top Posts", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                        }
                        
                        items(analytics.topPosts) { post ->
                            PostItem(
                                post = post,
                                onLikeClick = { /* Handle like in hashtag screen */ },
                                onSaveClick = { /* Handle save */ },
                                onDeleteClick = { /* Handle delete */ },
                                onArchiveClick = { /* Handle archive */ },
                                onNavigateToComments = { /* Handle navigate */ },
                                onShareClick = { /* Handle share */ },
                                onNavigateToProduct = { /* Handle product navigate */ }
                            )
                        }
                    }
                }
            }
        }
    }
}
