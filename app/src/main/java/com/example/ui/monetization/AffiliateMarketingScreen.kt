package com.example.ui.monetization

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AffiliateMarketingScreen(
    viewModel: AffiliateMarketingViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Affiliate Marketing") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleCreateDialog() }) {
                        Icon(Icons.Default.Add, contentDescription = "Generate Link")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Affiliate Earnings", style = MaterialTheme.typography.labelMedium)
                    Text("$${state.totalEarnings}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }

            Text("Your Trackable Links", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.links) { link ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Link, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(link.productName, fontWeight = FontWeight.Bold)
                                }
                                IconButton(onClick = { /* Copy link */ }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(20.dp))
                                }
                            }
                            Text(link.url, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("${link.clicks}", fontWeight = FontWeight.Bold)
                                    Text("Clicks", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column {
                                    Text("${link.conversions}", fontWeight = FontWeight.Bold)
                                    Text("Conversions", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column {
                                    Text("$${link.earnings}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text("Earnings", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (state.showCreateDialog) {
            var productName by remember { mutableStateOf("") }
            var originalUrl by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { viewModel.toggleCreateDialog() },
                title = { Text("Generate Affiliate Link") },
                text = {
                    Column {
                        OutlinedTextField(value = productName, onValueChange = { productName = it }, label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = originalUrl, onValueChange = { originalUrl = it }, label = { Text("Original Product URL") }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (productName.isNotBlank() && originalUrl.isNotBlank()) {
                                viewModel.generateLink(productName, originalUrl)
                            }
                        }
                    ) {
                        Text("Generate")
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
