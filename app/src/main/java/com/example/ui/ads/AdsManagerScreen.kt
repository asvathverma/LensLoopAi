package com.example.ui.ads

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdsManagerScreen(
    viewModel: AdsManagerViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ads Manager") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleCreateDialog() }) {
                        Icon(Icons.Default.Add, contentDescription = "Create Campaign")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Spent (This Month)", style = MaterialTheme.typography.labelMedium)
                    Text("$${state.totalSpent}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }

            Text("Your Campaigns", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.campaigns) { campaign ->
                    val statusColor = when (campaign.status) {
                        "Active" -> Color(0xFF4CAF50)
                        "Paused" -> Color(0xFFFF9800)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Campaign, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(campaign.name, fontWeight = FontWeight.Bold)
                                }
                                Text(campaign.status, color = statusColor, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Objective: ${campaign.objective}", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { (campaign.spent / campaign.budget).toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("$${campaign.spent} spent", style = MaterialTheme.typography.labelSmall)
                                Text("$${campaign.budget} budget", style = MaterialTheme.typography.labelSmall)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${campaign.impressions} Impressions", style = MaterialTheme.typography.bodySmall)
                                Text("${campaign.clicks} Clicks", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        if (state.showCreateDialog) {
            var name by remember { mutableStateOf("") }
            var objective by remember { mutableStateOf("Traffic") }
            var budget by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { viewModel.toggleCreateDialog() },
                title = { Text("Create Ad Campaign") },
                text = {
                    Column {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Campaign Name") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        // Simple dropdown alternative
                        Text("Objective", style = MaterialTheme.typography.labelSmall)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(selected = objective == "Traffic", onClick = { objective = "Traffic" }, label = { Text("Traffic") })
                            FilterChip(selected = objective == "Conversions", onClick = { objective = "Conversions" }, label = { Text("Conversions") })
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = budget,
                            onValueChange = { budget = it },
                            label = { Text("Budget ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val b = budget.toDoubleOrNull() ?: 0.0
                            if (name.isNotBlank() && b > 0) {
                                viewModel.createCampaign(name, objective, b)
                            }
                        }
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.toggleCreateDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
