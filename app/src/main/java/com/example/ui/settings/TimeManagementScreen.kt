package com.example.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeManagementScreen(
    viewModel: TimeManagementViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Activity & Time", fontWeight = FontWeight.Bold) },
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
            // Dashboard
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Daily Average", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Text(
                        text = "${state.averageDailyUsage / 60}h ${state.averageDailyUsage % 60}m",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("This week", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val maxMins = state.weeklyUsage.maxOfOrNull { it.minutes }?.coerceAtLeast(1) ?: 1
                        state.weeklyUsage.forEach { usage ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                                Surface(
                                    modifier = Modifier.width(20.dp).height((usage.minutes * 80 / maxMins).dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.small
                                ) {}
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(usage.day.take(1), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            // Time Management
            SettingsSectionTitle("Time Management")
            
            var showBreakDialog by remember { mutableStateOf(false) }
            ListItem(
                headlineContent = { Text("Take a Break Reminder") },
                supportingContent = { Text(if (state.takeABreakReminder == null) "Off" else "Every ${state.takeABreakReminder} minutes") },
                leadingContent = { Icon(Icons.Default.HourglassEmpty, contentDescription = null) },
                modifier = Modifier.clickable { showBreakDialog = true }
            )
            
            var showLimitDialog by remember { mutableStateOf(false) }
            ListItem(
                headlineContent = { Text("Daily Time Limit") },
                supportingContent = { 
                    val limit = state.dailyTimeLimit
                    Text(if (limit == null) "Off" else "${limit / 60}h ${limit % 60}m") 
                },
                leadingContent = { Icon(Icons.Default.Timer, contentDescription = null) },
                modifier = Modifier.clickable { showLimitDialog = true }
            )
            
            if (state.dailyTimeLimit != null) {
                ListItem(
                    headlineContent = { Text("Hard Stop") },
                    supportingContent = { Text("App locks after limit is reached") },
                    leadingContent = { Icon(Icons.Default.Block, contentDescription = null) },
                    trailingContent = { Switch(checked = state.hardStopEnabled, onCheckedChange = { viewModel.toggleHardStop() }) },
                    modifier = Modifier.clickable { viewModel.toggleHardStop() }
                )
            }

            HorizontalDivider()

            // Quiet Mode
            SettingsSectionTitle("Quiet Mode & Do Not Disturb")
            ListItem(
                headlineContent = { Text("Quiet Mode") },
                supportingContent = { Text("Silence notifications during scheduled hours") },
                leadingContent = { Icon(Icons.Default.NotificationsOff, contentDescription = null) },
                trailingContent = { Switch(checked = state.quietModeEnabled, onCheckedChange = { viewModel.toggleQuietMode() }) },
                modifier = Modifier.clickable { viewModel.toggleQuietMode() }
            )
            if (state.quietModeEnabled) {
                ListItem(
                    headlineContent = { Text("Quiet Hours") },
                    supportingContent = { Text("${state.quietModeStart} - ${state.quietModeEnd}") },
                    modifier = Modifier.padding(start = 40.dp).clickable { /* edit time */ }
                )
                ListItem(
                    headlineContent = { Text("Auto-Reply to DMs") },
                    supportingContent = { Text("Send auto-reply indicating Quiet Mode") },
                    trailingContent = { Switch(checked = state.autoReplyEnabled, onCheckedChange = { viewModel.toggleAutoReply() }) },
                    modifier = Modifier.padding(start = 40.dp).clickable { viewModel.toggleAutoReply() }
                )
                ListItem(
                    headlineContent = { Text("Exceptions") },
                    supportingContent = { Text("${state.exceptionList.size} contacts can interrupt") },
                    modifier = Modifier.padding(start = 40.dp).clickable { /* edit exceptions */ }
                )
            }

            ListItem(
                headlineContent = { Text("Bedtime Mode") },
                supportingContent = { Text("Grayscale UI during Quiet Mode to reduce stimulation") },
                leadingContent = { Icon(Icons.Default.Bedtime, contentDescription = null) },
                trailingContent = { Switch(checked = state.bedtimeModeEnabled, onCheckedChange = { viewModel.toggleBedtimeMode() }) },
                modifier = Modifier.clickable { viewModel.toggleBedtimeMode() }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Dialogs
            if (showBreakDialog) {
                AlertDialog(
                    onDismissRequest = { showBreakDialog = false },
                    title = { Text("Take a Break Reminder") },
                    text = {
                        Column {
                            listOf(null, 10, 20, 30, 60).forEach { mins ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        viewModel.setTakeABreakReminder(mins)
                                        showBreakDialog = false
                                    }.padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = state.takeABreakReminder == mins, onClick = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (mins == null) "Off" else "Every $mins minutes")
                                }
                            }
                        }
                    },
                    confirmButton = { TextButton(onClick = { showBreakDialog = false }) { Text("Close") } }
                )
            }
            
            if (showLimitDialog) {
                AlertDialog(
                    onDismissRequest = { showLimitDialog = false },
                    title = { Text("Daily Time Limit") },
                    text = {
                        Column {
                            listOf(null, 30, 60, 120, 180).forEach { mins ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        viewModel.setDailyTimeLimit(mins)
                                        showLimitDialog = false
                                    }.padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = state.dailyTimeLimit == mins, onClick = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (mins == null) "Off" else "${mins / 60}h ${mins % 60}m")
                                }
                            }
                        }
                    },
                    confirmButton = { TextButton(onClick = { showLimitDialog = false }) { Text("Close") } }
                )
            }
        }
    }
}

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
