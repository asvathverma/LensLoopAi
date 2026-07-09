package com.example.ui.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun CameraScreen(
    onNavigateBack: () -> Unit
) {
    var mode by remember { mutableStateOf("POST") }
    var flash by remember { mutableStateOf(false) }
    var cameraFront by remember { mutableStateOf(false) }
    var aspectRatio by remember { mutableStateOf("1:1") }

    val modes = listOf("POST", "STORY", "REELS", "LIVE")
    val aspectRatios = listOf("1:1", "4:5", "16:9")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(), start = 16.dp, end = 16.dp, bottom = 16.dp)
                .zIndex(10f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                IconButton(onClick = { flash = !flash }) {
                    Icon(
                        if (flash) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Toggle Flash",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { cameraFront = !cameraFront }) {
                    Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Flip Camera", tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
        }

        // Camera Preview Placeholder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 140.dp) // Leave space for bottom controls
                .background(Color(0xFF1A1A1A)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "📷", fontSize = 60.sp, color = Color.White.copy(alpha = 0.3f))
            
            if (mode == "POST") {
                val ratioModifier = when (aspectRatio) {
                    "1:1" -> Modifier.aspectRatio(1f)
                    "4:5" -> Modifier.aspectRatio(4f/5f)
                    "16:9" -> Modifier.aspectRatio(9f/16f)
                    else -> Modifier.aspectRatio(1f)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .then(ratioModifier)
                        .border(2.dp, Color.White.copy(alpha = 0.5f))
                )
            } else if (mode == "STORY") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, Color.White.copy(alpha = 0.5f))
                )
            }
            
            // Aspect Ratio Selector for POST
            if (mode == "POST") {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    aspectRatios.forEach { ratio ->
                        Text(
                            text = ratio,
                            color = if (aspectRatio == ratio) Color.White else Color(0xFF8E8E8E),
                            fontSize = 12.sp,
                            fontWeight = if (aspectRatio == ratio) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .clickable { aspectRatio = ratio }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Bottom Controls Container
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black)
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
        ) {
            // Mode Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                modes.forEach { m ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { mode = m }
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = m,
                            color = if (mode == m) Color.White else Color(0xFF8E8E8E),
                            fontSize = 13.sp,
                            fontWeight = if (mode == m) FontWeight.Bold else FontWeight.Normal
                        )
                        if (mode == m) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // Capture Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery Thumbnail
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF667eea), Color(0xFF764ba2))))
                        .clickable { /* Open gallery */ }
                )

                // Capture Button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .then(
                            if (mode == "LIVE") Modifier.background(Color.Red)
                            else Modifier.background(Brush.linearGradient(listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFF77737))))
                        )
                        .padding(4.dp)
                        .clickable { /* Capture action */ },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }

                // Effects Toggle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF333333))
                        .clickable { /* Toggle effects */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✨", fontSize = 20.sp)
                }
            }
        }
    }
}
