package com.example.ui.story

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.items
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelsCameraScreen(
    viewModel: ReelsCameraViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditor: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    var currentClipTime by remember { mutableStateOf(0) }
    var countdownValue by remember { mutableStateOf(0) }
    
    val sheetState = rememberModalBottomSheetState()
    
    if (state.showAudioMenu) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.toggleAudioMenu() },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Audio Controls", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                ListItem(
                    headlineContent = { Text("Music Library") },
                    supportingContent = { Text("Trending audio & sound effects") },
                    leadingContent = { Icon(Icons.Default.LibraryMusic, contentDescription = null) },
                    modifier = Modifier.clickable { /* open full library */ }
                )
                
                ListItem(
                    headlineContent = { Text("Voice-Over") },
                    leadingContent = { Icon(Icons.Default.Mic, contentDescription = null) },
                    trailingContent = {
                        Switch(checked = state.isVoiceOverEnabled, onCheckedChange = { viewModel.toggleVoiceOver() })
                    }
                )
                
                ListItem(
                    headlineContent = { Text("Audio Ducking") },
                    supportingContent = { Text("Auto-lower music when voice detected") },
                    leadingContent = { Icon(Icons.Default.VolumeDown, contentDescription = null) },
                    trailingContent = {
                        Switch(checked = state.isAudioDuckingEnabled, onCheckedChange = { viewModel.toggleAudioDucking() })
                    }
                )
                
                ListItem(
                    headlineContent = { Text("Copyright Check") },
                    supportingContent = { Text("Scan current audio for copyright issues") },
                    leadingContent = { Icon(Icons.Default.Copyright, contentDescription = null) },
                    modifier = Modifier.clickable { /* perform scan */ }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    
    if (state.showEffectsMenu) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.toggleEffectsMenu() },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Effects", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                val effects = listOf("None", "Speed Ramping", "Reverse Playback", "Slow Motion", "Time-Lapse", "Green Screen", "Clone Effect", "Jump Cut", "Morph", "Swipe")
                
                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(effects) { effect ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (state.activeEffect == effect) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                .border(2.dp, if (state.activeEffect == effect) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable { viewModel.setEffect(if (effect == "None") null else effect) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(effect, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    
    if (state.showTemplatesMenu) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.toggleTemplatesMenu() },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Templates", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                val templates = listOf("Trending", "Vlog", "Dance", "Before/After", "Tutorial")
                
                androidx.compose.foundation.lazy.LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(templates) { template ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (state.activeTemplate == template) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { viewModel.setTemplate(if (state.activeTemplate == template) null else template) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.MovieFilter, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(template, fontWeight = FontWeight.Bold)
                                Text("Use this template flow", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    LaunchedEffect(state.isTimerActive) {
        if (state.isTimerActive) {
            countdownValue = state.timerSeconds
            while (countdownValue > 0) {
                delay(1000)
                countdownValue--
            }
            viewModel.timerFinished()
        }
    }

    LaunchedEffect(state.isRecording) {
        if (state.isRecording) {
            currentClipTime = 0
            while (state.currentDurationSec + currentClipTime < state.maxDurationSec) {
                delay(1000)
                currentClipTime++
            }
            if (state.isRecording) {
                viewModel.stopRecording(currentClipTime)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Camera Preview Background
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1E1E1E))) {
            if (state.isRemixMode) {
                // Remix Layout Preview
                when (state.remixLayout) {
                    "LeftRight" -> {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.Gray)) {
                                Text("Original", modifier = Modifier.align(Alignment.Center), color = Color.White)
                            }
                            Box(modifier = Modifier.weight(1f).fillMaxHeight()) // Camera
                        }
                    }
                    "TopBottom" -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth().background(Color.Gray)) {
                                Text("Original", modifier = Modifier.align(Alignment.Center), color = Color.White)
                            }
                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) // Camera
                        }
                    }
                    "PiP" -> {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(top = 120.dp, start = 16.dp)
                                .size(100.dp, 150.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Gray)
                                .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                        ) {
                            Text("Orig", modifier = Modifier.align(Alignment.Center), color = Color.White)
                        }
                    }
                }
            }
            if (state.isDualCamera) {
                // Secondary camera pip
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 120.dp, end = 80.dp)
                        .size(100.dp, 150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray)
                        .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                )
            }
            if (state.alignGhosting && state.clips.isNotEmpty()) {
                // Mock ghosting from previous clip
                Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.1f)))
            }
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
            if (state.isTimerActive && countdownValue > 0) {
                Text(
                    text = countdownValue.toString(),
                    color = Color.White,
                    fontSize = 120.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        
        // Progress Bar for Clips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
                .height(4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val totalTime = state.currentDurationSec + if (state.isRecording) currentClipTime else 0
            
            // Background track
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.DarkGray, RoundedCornerShape(2.dp))) {
                // Foreground progress
                val progress = (totalTime.toFloat() / state.maxDurationSec).coerceIn(0f, 1f)
                Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().background(Color.White, RoundedCornerShape(2.dp)))
                
                // Clip dividers
                var acc = 0
                state.clips.forEach { clip ->
                    acc += clip.durationSec
                    val ratio = acc.toFloat() / state.maxDurationSec
                    Box(modifier = Modifier.fillMaxHeight().width(2.dp).background(Color.Black).align(Alignment.CenterStart).offset(x = (ratio * 100).dp)) // approximation
                }
            }
        }

        // Top Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            
            // Audio Selection
            Row(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clickable { viewModel.toggleAudioMenu() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = "Audio", tint = Color.White, modifier = Modifier.size(16.dp))
                Text(state.selectedMusicTitle ?: "Audio", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(16.dp)).clickable { viewModel.setMaxDuration(if (state.maxDurationSec == 15) 60 else if (state.maxDurationSec == 60) 90 else 15) }.padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("${state.maxDurationSec}s", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // Right Toolbar
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isRemixMode) {
                IconButton(onClick = { viewModel.cycleRemixLayout() }) {
                    Icon(Icons.Default.ViewSidebar, contentDescription = "Remix Layout", tint = Color.Yellow)
                }
            }
            IconButton(onClick = { viewModel.toggleCamera() }) { Icon(Icons.Default.Cameraswitch, contentDescription = "Switch Camera", tint = Color.White) }
            IconButton(onClick = { viewModel.cycleSpeed() }) { 
                Text("${state.recordingSpeed}x", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            IconButton(onClick = { viewModel.toggleEffectsMenu() }) { 
                Icon(Icons.Default.AutoAwesome, contentDescription = "Effects", tint = if (state.activeEffect != null) Color.Yellow else Color.White)
            }
            IconButton(onClick = { viewModel.toggleTemplatesMenu() }) {
                Icon(Icons.Default.MovieFilter, contentDescription = "Templates", tint = if (state.activeTemplate != null) Color.Yellow else Color.White)
            }
            IconButton(onClick = { viewModel.toggleFlash() }) { Icon(if (state.flashOn) Icons.Default.FlashOn else Icons.Default.FlashOff, contentDescription = "Flash", tint = Color.White) }
            IconButton(onClick = { viewModel.toggleAlignGhosting() }) { Icon(Icons.Default.FlipToFront, contentDescription = "Align", tint = if (state.alignGhosting) Color.Yellow else Color.White) }
            IconButton(onClick = { viewModel.toggleGrid() }) { Icon(Icons.Default.Grid3x3, contentDescription = "Grid", tint = if (state.showGrid) Color.Yellow else Color.White) }
            IconButton(onClick = { viewModel.cycleTimer() }) { 
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Timer, contentDescription = "Timer", tint = if (state.timerSeconds > 0) Color.Yellow else Color.White)
                    if (state.timerSeconds > 0) {
                        Text("${state.timerSeconds}", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            IconButton(onClick = { viewModel.toggleDualCamera() }) { 
                Icon(Icons.Default.FlipCameraIos, contentDescription = "Dual", tint = if (state.isDualCamera) Color.Yellow else Color.White)
            }
            IconButton(onClick = { viewModel.toggleExternalMic() }) {
                Icon(Icons.Default.Mic, contentDescription = "Mic", tint = if (state.hasExternalMic) Color.Yellow else Color.White)
            }
        }

        // Bottom Controls
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 48.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Delete last clip or Drafts
            if (state.clips.isNotEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { viewModel.deleteLastClip() }) {
                    Box(modifier = Modifier.size(40.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            } else if (state.hasDrafts) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { /* Open Drafts */ }) {
                    Box(modifier = Modifier.size(40.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Drafts, contentDescription = "Drafts", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            } else {
                Spacer(modifier = Modifier.size(40.dp))
            }
            
            // Record Button
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(if (state.isRecording) Color.Red else Color.White)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                if (state.isRecording) {
                                    viewModel.stopRecording(currentClipTime)
                                } else {
                                    if (state.currentDurationSec < state.maxDurationSec) {
                                        if (state.timerSeconds > 0) {
                                            viewModel.startTimer()
                                        } else {
                                            viewModel.startRecording()
                                        }
                                    }
                                }
                            }
                        )
                    }
            ) {
                if (state.isRecording) {
                    Box(modifier = Modifier.size(24.dp).background(Color.White, RoundedCornerShape(4.dp)).align(Alignment.Center))
                }
            }

            // Next / Done Button
            if (state.clips.isNotEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onNavigateToEditor() }) {
                    Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primary, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            } else {
                Spacer(modifier = Modifier.size(40.dp))
            }
        }
    }
}
