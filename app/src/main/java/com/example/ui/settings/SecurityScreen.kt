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
fun SecurityScreen(
    viewModel: SecurityViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAccountRecovery: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security & Login", fontWeight = FontWeight.Bold) },
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
            // Two-Factor Authentication
            SecuritySectionTitle("Two-Factor Authentication (2FA)")
            ListItem(
                headlineContent = { Text("Two-Factor Authentication") },
                supportingContent = { Text(if (state.is2FAEnabled) "On - ${state.twoFactorMethod}" else "Off") },
                leadingContent = { Icon(Icons.Default.Security, contentDescription = null) },
                trailingContent = { Switch(checked = state.is2FAEnabled, onCheckedChange = { viewModel.toggle2FA() }) },
                modifier = Modifier.clickable { viewModel.toggle2FA() }
            )
            if (state.is2FAEnabled) {
                ListItem(
                    headlineContent = { Text("Backup Codes") },
                    supportingContent = { Text("10 codes remaining") },
                    leadingContent = { Icon(Icons.Default.Password, contentDescription = null) },
                    modifier = Modifier.clickable { /* Show backup codes */ }.padding(start = 16.dp)
                )
            }

            HorizontalDivider()

            // Login Activity Monitoring
            SecuritySectionTitle("Login Activity")
            ListItem(
                headlineContent = { Text("Where you're logged in") },
                supportingContent = { Text("${state.loginSessions.size} active sessions") },
                leadingContent = { Icon(Icons.Default.Devices, contentDescription = null) }
            )
            
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                state.loginSessions.forEach { session ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (session.deviceName.contains("Mac") || session.deviceName.contains("PC")) Icons.Default.Computer else Icons.Default.Smartphone,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(session.deviceName, fontWeight = FontWeight.Bold)
                                Text("${session.location} • ${session.ipAddress}", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = session.lastActive,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (session.isCurrentSession) Color.Green else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (!session.isCurrentSession) {
                                IconButton(onClick = { viewModel.terminateSession(session.id) }) {
                                    Icon(Icons.Default.Logout, contentDescription = "Log out device", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
                if (state.loginSessions.size > 1) {
                    TextButton(
                        onClick = { viewModel.terminateAllOtherSessions() },
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp)
                    ) {
                        Text("Log out of all other devices", color = Color.Red)
                    }
                }
            }

            HorizontalDivider()

            // Email Notifications
            SecuritySectionTitle("Email Notifications")
            ListItem(
                headlineContent = { Text("Email Notifications") },
                supportingContent = { Text("Security alerts & updates") },
                leadingContent = { Icon(Icons.Default.Email, contentDescription = null) },
                trailingContent = { Switch(checked = state.emailNotificationsEnabled, onCheckedChange = { viewModel.toggleEmailNotifications() }) },
                modifier = Modifier.clickable { viewModel.toggleEmailNotifications() }
            )
            ListItem(
                headlineContent = { Text("Weekly Digest") },
                supportingContent = { Text("Top posts, new followers, missed stories") },
                leadingContent = { Icon(Icons.Default.MarkEmailUnread, contentDescription = null) },
                trailingContent = { Switch(checked = state.weeklyDigestEnabled, onCheckedChange = { viewModel.toggleWeeklyDigest() }) },
                modifier = Modifier.clickable { viewModel.toggleWeeklyDigest() }
            )

            HorizontalDivider()

            // Account Recovery
            SecuritySectionTitle("Account Support")
            ListItem(
                headlineContent = { Text("Account Recovery Options") },
                supportingContent = { Text("Trusted contacts, ID verification") },
                leadingContent = { Icon(Icons.Default.HealthAndSafety, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.KeyboardArrowRight, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateToAccountRecovery() }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SecuritySectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}
