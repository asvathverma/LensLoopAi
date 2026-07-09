package com.example.ui.live

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveStreamScreen(
    viewModel: LiveStreamViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var commentText by remember { mutableStateOf("") }
    
    val sheetState = rememberModalBottomSheetState()
    var showBadgesMenu by remember { mutableStateOf(false) }
    var showGuestsMenu by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Video Preview (Background)
        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
            if (state.layout == LiveLayout.GRID && state.guests.isNotEmpty()) {
                // Split screen mock
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth().background(Color.Gray)) {
                        Text("Host", modifier = Modifier.align(Alignment.Center), color = Color.White)
                    }
                    Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        state.guests.forEach { guest ->
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.DarkGray).border(1.dp, Color.Black)) {
                                Text(guest.username, modifier = Modifier.align(Alignment.Center), color = Color.White)
                            }
                        }
                    }
                }
            } else if (state.layout == LiveLayout.SPOTLIGHT) {
                 Box(modifier = Modifier.fillMaxSize().background(Color.Gray)) {
                     Text("Guest Spotlight", modifier = Modifier.align(Alignment.Center), color = Color.White)
                 }
            } else {
                Text("Host Video (RTMP/WebRTC)", modifier = Modifier.align(Alignment.Center), color = Color.White)
            }
        }
        
        if (!state.isStreaming && !state.isReplayAvailable) {
            // Pre-stream setup
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Start Live Stream", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Stream Key: ${state.streamKey.take(8)}...", color = Color.White)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { viewModel.startStream() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Go Live")
                }
            }
        } else if (state.isReplayAvailable) {
            // Post-stream summary
             Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Stream Ended", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Total Revenue: $${state.totalRevenue}", color = Color.White)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { viewModel.saveReel(); onNavigateBack() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Save to Reels")
                }
                TextButton(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
                    Text("Discard", color = Color.White)
                }
            }
        } else {
            // Live Stream UI
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.background(Color.Red, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text("LIVE", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${state.viewerCount}", color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                IconButton(onClick = { viewModel.endStream() }) {
                    Icon(Icons.Default.Close, contentDescription = "End Stream", tint = Color.White)
                }
            }
            
            // Layout Controls (if guests exist)
            if (state.guests.isNotEmpty()) {
                Row(
                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 64.dp, end = 16.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = { viewModel.setLayout(LiveLayout.GRID) }) { Icon(Icons.Default.GridView, tint = Color.White, contentDescription = null) }
                    IconButton(onClick = { viewModel.setLayout(LiveLayout.SPOTLIGHT) }) { Icon(Icons.Default.FilterCenterFocus, tint = Color.White, contentDescription = null) }
                }
            }
            
            // Bottom Area (Comments & Input)
            Column(
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(16.dp)
            ) {
                // Pinned Comment
                if (state.pinnedComment != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth().background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp)).padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PushPin, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(state.pinnedComment!!.username, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                            Text(state.pinnedComment!!.text, color = Color.White, style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.pinComment(null) }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            
                // Chat List
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.Transparent),
                    reverseLayout = true
                ) {
                    items(state.comments.reversed()) { comment ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp).clickable { viewModel.pinComment(comment) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (comment.badgeTier > 0) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = if (comment.badgeTier == 3) Color.Yellow else if (comment.badgeTier == 2) Color.LightGray else Color(0xFFCD7F32), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(comment.username, color = Color.LightGray, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(": ${comment.text}", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Input and Actions
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = Modifier.weight(1f).height(50.dp),
                        placeholder = { Text("Add a comment...", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Black.copy(alpha = 0.5f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.5f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(25.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { 
                            if (commentText.isNotBlank()) {
                                viewModel.addComment(commentText)
                                commentText = ""
                            }
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                    IconButton(onClick = { showBadgesMenu = true }) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = "Badges", tint = Color.Yellow)
                    }
                    IconButton(onClick = { showGuestsMenu = true }) {
                        Icon(Icons.Default.GroupAdd, contentDescription = "Guests", tint = Color.White)
                    }
                }
            }
        }
    }
    
    // Bottom Sheets
    if (showBadgesMenu) {
        ModalBottomSheet(onDismissRequest = { showBadgesMenu = false }, sheetState = sheetState) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Support with Badges", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    BadgeOption("Bronze", "$0.99", Color(0xFFCD7F32)) { viewModel.purchaseBadge(1, 0.99); showBadgesMenu = false }
                    BadgeOption("Silver", "$4.99", Color.LightGray) { viewModel.purchaseBadge(2, 4.99); showBadgesMenu = false }
                    BadgeOption("Gold", "$9.99", Color.Yellow) { viewModel.purchaseBadge(3, 9.99); showBadgesMenu = false }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    
    if (showGuestsMenu) {
        ModalBottomSheet(onDismissRequest = { showGuestsMenu = false }, sheetState = sheetState) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Manage Guests", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                if (state.guests.size < 3) {
                    Button(onClick = { viewModel.addGuest(LiveGuest(id = "g_${state.guests.size}", username = "guest_${state.guests.size}")); showGuestsMenu = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Add Random Guest")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                state.guests.forEach { guest ->
                    ListItem(
                        headlineContent = { Text(guest.username) },
                        trailingContent = {
                            IconButton(onClick = { viewModel.removeGuest(guest.id) }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove")
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun BadgeOption(title: String, price: String, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }.padding(8.dp)) {
        Icon(Icons.Default.Star, contentDescription = null, tint = color, modifier = Modifier.size(48.dp))
        Text(title, fontWeight = FontWeight.Bold)
        Text(price, style = MaterialTheme.typography.bodySmall)
    }
}
