package com.example.ui.story

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun StoryCameraScreen(
    viewModel: StoryCameraViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditor: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(state.isRecording) {
        if (state.isRecording) {
            var time = 0
            while (time < 60) {
                delay(1000)
                time++
                viewModel.updateRecordingDuration(time)
            }
            viewModel.stopRecording()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Mock Camera Preview
        Box(
            modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { viewModel.toggleCamera() },
                    onLongPress = { viewModel.toggleFocusLock() }
                )
            }
        ) {
            // Camera preview visual mock
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1E1E1E))) {
                if (state.showGrid) {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                        HorizontalDivider(color = Color.White.copy(alpha = 0.3f))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.3f))
                    }
                    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        VerticalDivider(color = Color.White.copy(alpha = 0.3f))
                        VerticalDivider(color = Color.White.copy(alpha = 0.3f))
                    }
                }
                
                if (state.showLevel) {
                    Box(modifier = Modifier.align(Alignment.Center).width(100.dp).height(2.dp).background(if (Math.random() > 0.5) Color.Green else Color.White))
                }
                
                if (state.isFocusLocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(60.dp)
                            .border(2.dp, Color.Yellow)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Yellow, modifier = Modifier.align(Alignment.TopEnd).offset(x = 12.dp, y = (-12).dp).size(16.dp))
                    }
                }
            }
        }

        // Top Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IconButton(onClick = { viewModel.toggleFlash() }) {
                    Icon(
                        imageVector = when (state.flashMode) {
                            FlashMode.OFF -> Icons.Default.FlashOff
                            FlashMode.ON -> Icons.Default.FlashOn
                            FlashMode.AUTO -> Icons.Default.FlashAuto
                        },
                        contentDescription = "Flash",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { viewModel.toggleGrid() }) {
                    Icon(Icons.Default.Grid3x3, contentDescription = "Grid", tint = if (state.showGrid) Color.Yellow else Color.White)
                }
                IconButton(onClick = { viewModel.toggleLevel() }) {
                    Icon(Icons.Default.HorizontalRule, contentDescription = "Level", tint = if (state.showLevel) Color.Yellow else Color.White)
                }
                IconButton(onClick = { /* Open Settings */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }
        }

        // Left sidebar for AR / Beauty settings if Beauty filter selected
        val selectedFilter = state.filters.find { it.id == state.selectedFilterId }
        if (selectedFilter?.category == "Beauty") {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .width(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.FaceRetouchingNatural, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                // Vertical slider mock
                Slider(
                    value = state.beautyIntensity,
                    onValueChange = { viewModel.setBeautyIntensity(it) },
                    modifier = Modifier.height(150.dp).width(20.dp),
                    colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White)
                )
            }
        }
        
        // Right sidebar for Camera Switch
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        ) {
            IconButton(onClick = { viewModel.toggleCamera() }, modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape)) {
                Icon(Icons.Default.Cameraswitch, contentDescription = "Switch Camera", tint = Color.White)
            }
        }

        // Bottom Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isRecording) {
                Text(
                    text = String.format("00:%02d", state.recordingDurationSec),
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp).background(Color.Black.copy(alpha = 0.5f), shape = MaterialTheme.shapes.small).padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Filters Carousel
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(state.filters) { filter ->
                    val isSelected = filter.id == state.selectedFilterId
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(if (isSelected) 72.dp else 56.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.White.copy(alpha = 0.3f) else Color.Transparent)
                            .border(if (isSelected) 3.dp else 1.dp, Color.White, CircleShape)
                            .clickable { viewModel.selectFilter(filter.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(filter.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            // Capture Button Area
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Outer ring
                Box(modifier = Modifier.size(80.dp).border(4.dp, if (state.isRecording) Color.Red else Color.White, CircleShape))
                
                // Inner button
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (state.isRecording) Color.Red else Color.White)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    if (state.mode == CameraMode.PHOTO || state.mode == CameraMode.BURST) {
                                        onNavigateToEditor()
                                    } else if (state.mode == CameraMode.HANDS_FREE) {
                                        if (state.isRecording) {
                                            viewModel.stopRecording()
                                            onNavigateToEditor()
                                        } else {
                                            viewModel.startRecording()
                                        }
                                    }
                                },
                                onPress = {
                                    if (state.mode == CameraMode.VIDEO || state.mode == CameraMode.PHOTO) {
                                        viewModel.startRecording()
                                        tryAwaitRelease()
                                        viewModel.stopRecording()
                                        onNavigateToEditor()
                                    }
                                }
                            )
                        }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Camera Modes
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val modes = CameraMode.values()
                items(modes) { mode ->
                    Text(
                        text = mode.name,
                        color = if (mode == state.mode) Color.White else Color.Gray,
                        fontWeight = if (mode == state.mode) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable { viewModel.setMode(mode) }
                    )
                }
            }
        }
    }
}
