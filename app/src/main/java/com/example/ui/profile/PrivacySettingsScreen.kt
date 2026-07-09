package com.example.ui.profile

import androidx.compose.foundation.background
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
fun PrivacySettingsScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val privacySettings by viewModel.privacySettings.collectAsState()

    var isPrivate by remember { mutableStateOf(privacySettings.isPrivate) }
    var showActivityStatus by remember { mutableStateOf(privacySettings.showActivityStatus) }
    var commentFiltering by remember { mutableStateOf(privacySettings.commentFilteringEnabled) }
    var requireTagApproval by remember { mutableStateOf(privacySettings.requireTagApproval) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        viewModel.updatePrivacySettings(
                            privacySettings.copy(
                                isPrivate = isPrivate,
                                showActivityStatus = showActivityStatus,
                                commentFilteringEnabled = commentFiltering,
                                requireTagApproval = requireTagApproval
                            )
                        )
                        onNavigateBack()
                    }) {
                        Text("Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PrivacyToggle(
                title = "Private Account",
                description = "Only approved followers can see your posts and stories.",
                checked = isPrivate,
                onCheckedChange = { isPrivate = it }
            )
            
            PrivacyToggle(
                title = "Show Activity Status",
                description = "Allow accounts you follow to see when you were last active.",
                checked = showActivityStatus,
                onCheckedChange = { showActivityStatus = it }
            )

            PrivacyToggle(
                title = "Comment Filtering",
                description = "Automatically hide offensive comments and filter specific words.",
                checked = commentFiltering,
                onCheckedChange = { commentFiltering = it }
            )

            PrivacyToggle(
                title = "Require Tag Approval",
                description = "Manually approve posts before they appear on your profile.",
                checked = requireTagApproval,
                onCheckedChange = { requireTagApproval = it }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)

            TextButton(
                onClick = { /* Navigate to block list */ }, 
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text("Manage Blocked Accounts", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            }
            
            TextButton(
                onClick = { /* Navigate to story privacy */ }, 
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text("Story Privacy Settings", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun PrivacyToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked, 
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}
