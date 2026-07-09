package com.example.ui.monetization

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.WorkspacePremium
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
fun VirtualEconomyScreen(
    viewModel: VirtualEconomyViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var coinsToConvert by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Virtual Economy (Badges & Gifts)") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFC107))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Coin Balance", style = MaterialTheme.typography.labelMedium)
                        Text("${state.coinBalance}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFF4CAF50))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cash Earnings", style = MaterialTheme.typography.labelMedium)
                        Text("$${String.format("%.2f", state.earningsBalance)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Convert Coins to Cash", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = coinsToConvert,
                    onValueChange = { coinsToConvert = it },
                    label = { Text("Amount (Coins)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        val amount = coinsToConvert.toIntOrNull() ?: 0
                        viewModel.convertCoinsToCash(amount)
                        coinsToConvert = ""
                    },
                    enabled = coinsToConvert.isNotBlank() && (coinsToConvert.toIntOrNull() ?: 0) > 0
                ) {
                    Text("Convert")
                }
            }
            Text("Conversion Rate: 100 Coins = $1.00", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))

            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Your Stats", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            ListItem(
                leadingContent = { Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                headlineContent = { Text("Badges Received") },
                trailingContent = { Text("${state.badgesReceived}", fontWeight = FontWeight.Bold) }
            )
            ListItem(
                leadingContent = { Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
                headlineContent = { Text("Virtual Gifts Received") },
                trailingContent = { Text("${state.giftsReceived}", fontWeight = FontWeight.Bold) }
            )
        }
    }
}
