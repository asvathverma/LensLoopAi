package com.example.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(
    viewModel: ThemeSettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theme & Accessibility") },
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
            Text("Appearance", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
            
            ListItem(
                headlineContent = { Text("Use System Theme") },
                trailingContent = {
                    Switch(checked = state.useSystemTheme, onCheckedChange = { viewModel.toggleSystemTheme(it) })
                }
            )
            
            ListItem(
                headlineContent = { Text("Dark Mode") },
                trailingContent = {
                    Switch(checked = state.isDarkMode, onCheckedChange = { viewModel.toggleDarkMode(it) }, enabled = !state.useSystemTheme)
                }
            )

            Text("Accent Color", modifier = Modifier.padding(16.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Default", "Blue", "Green", "Pink").forEach { color ->
                    FilterChip(
                        selected = state.accentColor == color,
                        onClick = { viewModel.setAccentColor(color) },
                        label = { Text(color) }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Text("Accessibility", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))

            ListItem(
                headlineContent = { Text("High Contrast Mode") },
                trailingContent = {
                    Switch(checked = state.highContrast, onCheckedChange = { viewModel.toggleHighContrast(it) })
                }
            )
            
            ListItem(
                headlineContent = { Text("Reduce Motion") },
                trailingContent = {
                    Switch(checked = state.reducedMotion, onCheckedChange = { viewModel.toggleReducedMotion(it) })
                }
            )

            Text("Layout Density", modifier = Modifier.padding(16.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Compact", "Comfortable").forEach { density ->
                    FilterChip(
                        selected = state.layoutDensity == density,
                        onClick = { viewModel.setLayoutDensity(density) },
                        label = { Text(density) }
                    )
                }
            }
        }
    }
}
