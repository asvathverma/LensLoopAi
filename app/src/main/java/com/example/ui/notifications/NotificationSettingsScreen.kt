package com.example.ui.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    viewModel: NotificationSettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Push Notifications") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
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
            
            // Global Settings
            SettingsSectionTitle("Global Settings")
            SettingsSwitchItem(
                title = "Pause All",
                subtitle = "Temporarily pause all push notifications",
                checked = uiState.pauseAll,
                onCheckedChange = { viewModel.togglePauseAll(it) }
            )
            
            HorizontalDivider()
            
            // Quiet Hours
            SettingsSectionTitle("Quiet Hours")
            SettingsSwitchItem(
                title = "Enable Quiet Hours",
                subtitle = "Mute notifications during scheduled hours",
                checked = uiState.quietHoursEnabled,
                onCheckedChange = { viewModel.toggleQuietHours(it) }
            )
            
            if (uiState.quietHoursEnabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f).clickable { /* Show TimePicker */ }
                    ) {
                        Text("From", style = MaterialTheme.typography.bodySmall)
                        Text(uiState.quietHoursStart, style = MaterialTheme.typography.bodyLarge)
                    }
                    Column(
                        modifier = Modifier.weight(1f).clickable { /* Show TimePicker */ },
                        horizontalAlignment = Alignment.End
                    ) {
                        Text("To", style = MaterialTheme.typography.bodySmall)
                        Text(uiState.quietHoursEnd, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            
            HorizontalDivider()
            
            // Notification Types
            SettingsSectionTitle("Interactions")
            SettingsSwitchItem(
                title = "Likes",
                checked = uiState.likesEnabled,
                onCheckedChange = { viewModel.toggleLikes(it) }
            )
            SettingsSwitchItem(
                title = "Comments",
                checked = uiState.commentsEnabled,
                onCheckedChange = { viewModel.toggleComments(it) }
            )
            SettingsSwitchItem(
                title = "New Followers",
                checked = uiState.newFollowersEnabled,
                onCheckedChange = { viewModel.toggleNewFollowers(it) }
            )
            SettingsSwitchItem(
                title = "Mentions",
                checked = uiState.mentionsEnabled,
                onCheckedChange = { viewModel.toggleMentions(it) }
            )
            
            HorizontalDivider()
            
            SettingsSectionTitle("Messages & Calls")
            SettingsSwitchItem(
                title = "Direct Messages",
                checked = uiState.directMessagesEnabled,
                onCheckedChange = { viewModel.toggleDirectMessages(it) }
            )
            SettingsSwitchItem(
                title = "Delivery Receipts",
                subtitle = "Send tracking receipts when messages are delivered",
                checked = uiState.deliveryReceiptsEnabled,
                onCheckedChange = { viewModel.toggleDeliveryReceipts(it) }
            )
            
            HorizontalDivider()
            
            SettingsSectionTitle("Other")
            SettingsSwitchItem(
                title = "Live Videos",
                checked = uiState.liveVideoEnabled,
                onCheckedChange = { viewModel.toggleLiveVideo(it) }
            )
            SettingsSwitchItem(
                title = "Shopping & Promos",
                checked = uiState.shoppingEnabled,
                onCheckedChange = { viewModel.toggleShopping(it) }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(
                    text = subtitle, 
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
