package com.example.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
fun PrivacySafetyScreen(
    viewModel: PrivacySafetyViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToContentModeration: () -> Unit,
    onNavigateToParentalControls: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy & Safety", fontWeight = FontWeight.Bold) },
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
            // Account Privacy
            SettingsSectionTitle("Account Privacy")
            ListItem(
                headlineContent = { Text("Private Account") },
                supportingContent = { Text("Only approved followers can see your posts and stories") },
                leadingContent = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingContent = { Switch(checked = state.isPrivateAccount, onCheckedChange = { viewModel.togglePrivateAccount() }) },
                modifier = Modifier.clickable { viewModel.togglePrivateAccount() }
            )
            ListItem(
                headlineContent = { Text("Activity Status") },
                supportingContent = { Text("Allow accounts you follow to see when you're active") },
                leadingContent = { Icon(Icons.Default.Visibility, contentDescription = null) },
                trailingContent = { Switch(checked = state.showActivityStatus, onCheckedChange = { viewModel.toggleActivityStatus() }) },
                modifier = Modifier.clickable { viewModel.toggleActivityStatus() }
            )

            HorizontalDivider()

            // Interactions Control
            SettingsSectionTitle("Interactions")
            ListItem(
                headlineContent = { Text("Blocked Accounts") },
                supportingContent = { Text("${state.blockedUsers.size} accounts") },
                leadingContent = { Icon(Icons.Default.Block, contentDescription = null) },
                trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
                modifier = Modifier.clickable { /* Show blocked */ }
            )
            ListItem(
                headlineContent = { Text("Restricted Accounts") },
                supportingContent = { Text("${state.restrictedUsers.size} accounts") },
                leadingContent = { Icon(Icons.Default.RemoveCircleOutline, contentDescription = null) },
                trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
                modifier = Modifier.clickable { /* Show restricted */ }
            )
            ListItem(
                headlineContent = { Text("Muted Accounts") },
                supportingContent = { Text("${state.mutedUsers.size} accounts") },
                leadingContent = { Icon(Icons.Default.VolumeOff, contentDescription = null) },
                trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
                modifier = Modifier.clickable { /* Show muted */ }
            )

            HorizontalDivider()

            // Content & Moderation
            SettingsSectionTitle("Content & Moderation")
            ListItem(
                headlineContent = { Text("Sensitive Content Warnings") },
                supportingContent = { Text("Blur potentially sensitive photos and videos") },
                leadingContent = { Icon(Icons.Default.Warning, contentDescription = null) },
                trailingContent = { Switch(checked = state.hideSensitiveContent, onCheckedChange = { viewModel.toggleHideSensitiveContent() }) },
                modifier = Modifier.clickable { viewModel.toggleHideSensitiveContent() }
            )
            ListItem(
                headlineContent = { Text("Reporting & Moderation") },
                supportingContent = { Text("View reported content and appeals") },
                leadingContent = { Icon(Icons.Default.Report, contentDescription = null) },
                trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateToContentModeration() }
            )

            HorizontalDivider()

            // Safety & Family
            SettingsSectionTitle("Safety & Family")
            ListItem(
                headlineContent = { Text("Age Verification") },
                supportingContent = { Text("Verified 18+") },
                leadingContent = { Icon(Icons.Default.VerifiedUser, contentDescription = null) },
                trailingContent = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green) },
                modifier = Modifier.clickable { /* Show age verification */ }
            )
            ListItem(
                headlineContent = { Text("Parental Supervision") },
                supportingContent = { Text("Manage limits and schedules") },
                leadingContent = { Icon(Icons.Default.FamilyRestroom, contentDescription = null) },
                trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateToParentalControls() }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
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
