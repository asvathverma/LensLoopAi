package com.example.ui.shop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    productId: String,
    viewModel: ShopViewModel,
    onNavigateBack: () -> Unit,
    onOrderComplete: () -> Unit
) {
    val product = viewModel.getProduct(productId)
    var isProcessing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { paddingValues ->
        if (product != null) {
            val tax = product.price * 0.08
            val total = product.price + tax

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Order Summary
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Order Summary", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(product.name)
                            Text("$${String.format("%.2f", product.price)}")
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Estimated Tax (8%)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$${String.format("%.2f", tax)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", fontWeight = FontWeight.Bold)
                            Text("$${String.format("%.2f", total)}", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text("Shipping Address", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Address") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("City") }, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("Zip Code") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }

                Text("Payment Method", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                
                // Mock digital wallets
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)) {
                        Text("G Pay")
                    }
                    Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)) {
                        Text("Apple Pay")
                    }
                    Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)) {
                        Text("Klarna")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Or pay with card", modifier = Modifier.align(Alignment.CenterHorizontally), style = MaterialTheme.typography.labelSmall)
                
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Card Number") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("MM/YY") }, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("CVC") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        isProcessing = true
                        // Simulate network call
                        // onOrderComplete()
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp),
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Icon(Icons.Default.LocalShipping, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Place Order ($${String.format("%.2f", total)})", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
