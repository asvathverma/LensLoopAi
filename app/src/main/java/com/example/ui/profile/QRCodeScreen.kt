package com.example.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeScreen(
    onNavigateBack: () -> Unit
) {
    var selectedColor by remember { mutableStateOf(Color(0xFFE91E63)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nametag & QR Code") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = { /* Share QR Code */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Nametag / QR Code Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.8f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = selectedColor)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("@alex_photography", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(32.dp))
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .background(Color.White, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.QrCode2,
                                contentDescription = "QR Code",
                                modifier = Modifier.size(180.dp),
                                tint = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Scan to follow", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Customize Color", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ColorButton(Color(0xFFE91E63)) { selectedColor = it }
                ColorButton(Color(0xFF9C27B0)) { selectedColor = it }
                ColorButton(Color(0xFF3F51B5)) { selectedColor = it }
                ColorButton(Color(0xFF009688)) { selectedColor = it }
                ColorButton(Color(0xFFFF9800)) { selectedColor = it }
            }
        }
    }
}

@Composable
fun ColorButton(color: Color, onClick: (Color) -> Unit) {
    Button(
        onClick = { onClick(color) },
        modifier = Modifier.size(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(0.dp)
    ) { }
}
