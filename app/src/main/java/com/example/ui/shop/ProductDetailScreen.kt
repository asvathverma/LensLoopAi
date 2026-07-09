package com.example.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: ShopViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCheckout: (String) -> Unit
) {
    val product = viewModel.getProduct(productId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "Product") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { paddingValues ->
        if (product != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(product.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("$${product.price}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Sold by @${product.merchant.username}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(product.description, style = MaterialTheme.typography.bodyMedium)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = { onNavigateToCheckout(product.id) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = product.inStock
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (product.inStock) "Buy Now" else "Out of Stock", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Product not found")
            }
        }
    }
}
