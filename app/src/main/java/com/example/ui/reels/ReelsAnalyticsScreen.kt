package com.example.ui.reels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelsAnalyticsScreen(
    viewModel: ReelsAnalyticsViewModel,
    reelId: String,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(reelId) {
        viewModel.loadAnalytics(reelId)
    }
    
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reel Insights") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                state.analytics?.let { analytics ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Overview Cards
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InsightCard(title = "Plays", value = analytics.totalPlays.toString(), modifier = Modifier.weight(1f))
                            InsightCard(title = "Likes", value = analytics.likes.toString(), modifier = Modifier.weight(1f))
                            InsightCard(title = "Comments", value = analytics.comments.toString(), modifier = Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InsightCard(title = "Shares", value = analytics.shares.toString(), modifier = Modifier.weight(1f))
                            InsightCard(title = "Saves", value = analytics.saves.toString(), modifier = Modifier.weight(1f))
                            InsightCard(title = "Watch Time", value = "${analytics.avgWatchTimeSec}s", modifier = Modifier.weight(1f))
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Text("Audience Retention", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Completion Rate: ${(analytics.completionRate * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
                        
                        // Retention Graph (Mocked with a Row of bars)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            analytics.retentionData.forEach { value ->
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .fillMaxHeight(value)
                                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                )
                            }
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Text("Traffic Sources", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        
                        TrafficSourceRow("Feed", analytics.trafficFeed)
                        TrafficSourceRow("Audio Page", analytics.trafficAudio)
                        TrafficSourceRow("Hashtags", analytics.trafficHashtag)
                        TrafficSourceRow("Profile", analytics.trafficProfile)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Optimization Tips
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Optimization Tip", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Your hook (first 3s) retained 95% of viewers! Try to use similar intros in future reels.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InsightCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun TrafficSourceRow(source: String, percentage: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(source, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        LinearProgressIndicator(
            progress = { percentage.toFloat() / 100f },
            modifier = Modifier.weight(2f).height(8.dp),
            color = MaterialTheme.colorScheme.primary,
        )
        Text("$percentage%", modifier = Modifier.width(48.dp).padding(start = 8.dp), style = MaterialTheme.typography.bodySmall)
    }
}
