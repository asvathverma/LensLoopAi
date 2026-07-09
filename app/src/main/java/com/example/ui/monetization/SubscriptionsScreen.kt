package com.example.ui.monetization

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    viewModel: SubscriptionsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscriptions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            
            ListItem(
                headlineContent = { Text("Enable Subscriptions") },
                supportingContent = { Text("Allow fans to subscribe for exclusive content") },
                trailingContent = {
                    Switch(
                        checked = state.isSubscriptionsEnabled,
                        onCheckedChange = { viewModel.toggleSubscriptions(it) }
                    )
                }
            )

            if (state.isSubscriptionsEnabled) {
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AttachMoney, contentDescription = null)
                            Text("Estimated Monthly Revenue", style = MaterialTheme.typography.labelMedium)
                        }
                        Text("$${state.monthlyRevenue}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("${state.activeSubscribers.size} Active Subscribers", style = MaterialTheme.typography.bodySmall)
                    }
                }

                var priceText by remember { mutableStateOf(state.subscriptionPrice.toString()) }
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { 
                        priceText = it 
                        it.toDoubleOrNull()?.let { p -> viewModel.updatePrice(p) }
                    },
                    label = { Text("Monthly Price ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )

                Text("Recent Subscribers", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.activeSubscribers) { sub ->
                        ListItem(
                            leadingContent = {
                                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            headlineContent = { Text(sub.username, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("Joined ${sub.joinDate} • ${sub.tier}") }
                        )
                    }
                }
            }
        }
    }
}
