package com.example.ui.monetization

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorMarketplaceScreen(
    viewModel: CreatorMarketplaceViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val categories = listOf("All", "Fitness", "Tech", "Fashion", "Food")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Creator Marketplace") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            
            Text("Discover Campaigns", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))

            LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = state.selectedCategory == cat,
                        onClick = { viewModel.applyFilter(cat) },
                        label = { Text(cat) }
                    )
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                items(state.availableCampaigns) { campaign ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(campaign.brandName, fontWeight = FontWeight.Bold)
                                }
                                Text("$${campaign.payout}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(campaign.title, style = MaterialTheme.typography.titleSmall)
                            Text(campaign.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Requirements: ${campaign.requirements}", style = MaterialTheme.typography.labelMedium)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            val isApplied = state.appliedCampaigns.contains(campaign.id)
                            Button(
                                onClick = { viewModel.applyToCampaign(campaign.id) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isApplied
                            ) {
                                Text(if (isApplied) "Applied" else "Apply Now")
                            }
                        }
                    }
                }
            }
        }
    }
}
