package com.example.ui.dm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.text.format.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DMListScreen(
    viewModel: DMViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val state by viewModel.listState.collectAsState()

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Messages", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White) }
                },
                actions = {
                    Text(
                        text = "✏️",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { /* New message */ }
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            // Search Bar
            Box(modifier = Modifier.padding(12.dp, 8.dp, 12.dp, 16.dp)) {
                TextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Search", color = Color.White, fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF262626),
                        unfocusedContainerColor = Color(0xFF262626),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.White
                    )
                )
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.conversations) { convo ->
                        ConversationItem(convo, onClick = {
                            viewModel.openConversation(convo.id)
                            onNavigateToChat(convo.id)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationItem(convo: Conversation, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar Box
        Box(modifier = Modifier.size(56.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF667eea), Color(0xFF764ba2)))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = convo.iconUrl ?: convo.name.firstOrNull()?.toString() ?: "",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (convo.isOnline) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00D26A))
                        .border(2.dp, Color.Black, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = convo.name,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                
                val lastMsg = convo.messages.lastOrNull()
                val timeStr = if (lastMsg != null) {
                    val diff = System.currentTimeMillis() - lastMsg.timestamp
                    when {
                        diff < 60000 -> "Just now"
                        diff < 3600000 -> "${diff / 60000}m"
                        diff < 86400000 -> "${diff / 3600000}h"
                        else -> "${diff / 86400000}d"
                    }
                } else ""
                
                Text(
                    text = timeStr,
                    color = Color(0xFF8E8E8E),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val lastMsg = convo.messages.lastOrNull()
                val preview = when (lastMsg?.type) {
                    MessageType.TEXT -> lastMsg.text
                    MessageType.VOICE -> "🎤 Voice message"
                    MessageType.PHOTO_VIEW_ONCE -> "📷 Photo (View Once)"
                    MessageType.VIDEO -> "📹 Video"
                    else -> ""
                }
                
                Text(
                    text = preview,
                    color = if (convo.unreadCount > 0) Color.White else Color(0xFF8E8E8E),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )

                if (convo.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0095F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = convo.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
