package com.example.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// We can define it locally or use the one from PrivacySafetyScreen
@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentalControlsScreen(
    viewModel: ParentalControlsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parental Supervision") },
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
            if (state.isSupervisionActive) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FamilyRestroom, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Supervised Account", fontWeight = FontWeight.Bold)
                            Text("Supervised by ${state.supervisorName}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                
                SettingsSectionTitle("Time Limits")
                ListItem(
                    headlineContent = { Text("Daily Time Limit") },
                    supportingContent = { Text("${state.dailyTimeLimitMinutes / 60} hours ${state.dailyTimeLimitMinutes % 60} minutes") }
                )
                ListItem(
                    headlineContent = { Text("Downtime") },
                    supportingContent = { Text("${state.downtimeStart} - ${state.downtimeEnd}") }
                )
                
                HorizontalDivider()
                
                SettingsSectionTitle("Content & Privacy")
                ListItem(
                    headlineContent = { Text("Filter Sensitive Content") },
                    supportingContent = { Text("Hide potentially inappropriate content") },
                    trailingContent = { Switch(checked = state.filterSensitiveContent, onCheckedChange = { viewModel.toggleContentFilter() }) },
                    modifier = Modifier.clickable { viewModel.toggleContentFilter() }
                )
                ListItem(
                    headlineContent = { Text("Restrict DMs") },
                    supportingContent = { Text("Only allow messages from approved followers") },
                    trailingContent = { Switch(checked = state.restrictDMsToFollowers, onCheckedChange = { viewModel.toggleDMRestriction() }) },
                    modifier = Modifier.clickable { viewModel.toggleDMRestriction() }
                )
                
            } else {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FamilyRestroom, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Set up Parental Supervision", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Link this account to a parent or guardian.", textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(vertical = 8.dp))
                        Button(onClick = { /* Send invite */ }, modifier = Modifier.padding(top = 16.dp)) {
                            Text("Invite Parent")
                        }
                    }
                }
            }
        }
    }
}
