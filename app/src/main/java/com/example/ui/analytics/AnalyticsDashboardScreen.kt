package com.example.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDashboardScreen(
    viewModel: AnalyticsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Professional Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            state.overview?.let { overview ->
                // Creator Goals
                Text("Your Creator Goals", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Reach 20k Followers", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(progress = { 0.75f }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("15,000 / 20,000 (75%)", style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Account Overview
                Text("Account Overview (Last 30 Days)", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MetricCard(title = "Accounts Reached", value = "${overview.reach}", icon = Icons.Default.Visibility, modifier = Modifier.weight(1f))
                    MetricCard(title = "Follower Growth", value = "+${overview.followersGrowth}", icon = Icons.Default.People, modifier = Modifier.weight(1f))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MetricCard(title = "Profile Visits", value = "${overview.profileVisits}", icon = Icons.Default.Insights, modifier = Modifier.weight(1f))
                    MetricCard(title = "Engagement Rate", value = "${overview.engagementRate}%", icon = Icons.Default.Insights, modifier = Modifier.weight(1f))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Audience Demographics
                Text("Audience Insights", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("Top Locations", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(horizontal = 16.dp))
                overview.topLocations.forEach { loc ->
                    Text(loc, modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp), style = MaterialTheme.typography.bodyMedium)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Age Distribution", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(horizontal = 16.dp))
                overview.ageDistribution.forEach { (age, percentage) ->
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(age, modifier = Modifier.width(60.dp), style = MaterialTheme.typography.bodyMedium)
                        LinearProgressIndicator(progress = { percentage.toFloat() }, modifier = Modifier.weight(1f).height(8.dp))
                        Text("${(percentage * 100).toInt()}%", modifier = Modifier.width(40.dp).padding(start = 8.dp), style = MaterialTheme.typography.bodySmall)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Advanced Reach Analytics
                Text("Reach Breakdown", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Followers", style = MaterialTheme.typography.bodySmall)
                        Text("${overview.reachDetails.followersReach}", fontWeight = FontWeight.Bold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Non-Followers", style = MaterialTheme.typography.bodySmall)
                        Text("${overview.reachDetails.nonFollowersReach}", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Organic Reach", style = MaterialTheme.typography.bodySmall)
                        Text("${overview.reachDetails.organicReach}", fontWeight = FontWeight.Bold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Paid Reach", style = MaterialTheme.typography.bodySmall)
                        Text("${overview.reachDetails.paidReach}", fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Stories & Reels Analytics
                Text("Format Performance", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Stories", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Reach: ${overview.storyAnalytics.reach}", style = MaterialTheme.typography.bodySmall)
                            Text("Impressions: ${overview.storyAnalytics.impressions}", style = MaterialTheme.typography.bodySmall)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Forward Rate: ${overview.storyAnalytics.forwardRate}%", style = MaterialTheme.typography.bodySmall)
                            Text("Exit Rate: ${overview.storyAnalytics.exitRate}%", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("Replies: ${overview.storyAnalytics.replies}", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Reels", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Plays: ${overview.reelsAnalytics.plays}", style = MaterialTheme.typography.bodySmall)
                            Text("Likes: ${overview.reelsAnalytics.likes}", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("Avg. Watch Time: ${overview.reelsAnalytics.averageWatchTimeSeconds}s", style = MaterialTheme.typography.bodySmall)
                        Text("Audio: ${overview.reelsAnalytics.audioAttribution}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Post Performance
                Text("Recent Content Performance", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(8.dp))
                
                state.recentPostsPerformance.forEach { post ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Post ID: ${post.postId}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Impressions: ${post.impressions}", style = MaterialTheme.typography.bodySmall)
                                Text("Reach: ${post.reach}", style = MaterialTheme.typography.bodySmall)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Saves: ${post.saves}", style = MaterialTheme.typography.bodySmall)
                                Text("Shares: ${post.shares}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
