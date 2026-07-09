package com.example.ui.dm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.text.format.DateFormat
import java.util.Date
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import android.view.WindowManager

@Composable
fun DMChatScreen(
    viewModel: DMViewModel,
    onNavigateBack: () -> Unit,
    onCall: (Boolean) -> Unit
) {
    val state by viewModel.chatState.collectAsState()
    val convo = state.conversation ?: return
    val context = LocalContext.current
    
    // Header
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .background(Color.Black),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier
                    .clickable { onNavigateBack() }
                    .padding(end = 12.dp)
            )
            
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF667eea), Color(0xFF764ba2)))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = convo.iconUrl ?: convo.name.firstOrNull()?.toString() ?: "",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = convo.name,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = if (convo.isOnline) "Active now" else "Offline",
                    color = if (convo.isOnline) Color(0xFF00D26A) else Color(0xFF8E8E8E),
                    fontSize = 12.sp
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(text = "📞", fontSize = 20.sp, modifier = Modifier.clickable { onCall(false) }.padding(horizontal = 8.dp))
            Text(text = "📹", fontSize = 20.sp, modifier = Modifier.clickable { onCall(true) })
        }
        
        HorizontalDivider(color = Color(0xFF262626))

        // Messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(convo.messages) { msg ->
                val isSent = msg.senderId == state.currentUserId
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isSent) Alignment.End else Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .wrapContentWidth(if (isSent) Alignment.End else Alignment.Start)
                    ) {
                        Text(
                            text = msg.text,
                            color = Color.White,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(
                                    topStart = 20.dp,
                                    topEnd = 20.dp,
                                    bottomStart = if (isSent) 20.dp else 4.dp,
                                    bottomEnd = if (isSent) 4.dp else 20.dp
                                ))
                                .background(
                                    if (isSent) Brush.linearGradient(listOf(Color(0xFF833AB4), Color(0xFFFD1D1D)))
                                    else Brush.linearGradient(listOf(Color(0xFF262626), Color(0xFF262626)))
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = if (isSent) Arrangement.End else Arrangement.Start
                    ) {
                        val timeStr = DateFormat.format("hh:mm a", Date(msg.timestamp)).toString()
                        Text(text = timeStr, color = Color(0xFF8E8E8E), fontSize = 11.sp)
                        
                        if (isSent) {
                            Spacer(modifier = Modifier.width(4.dp))
                            val isRead = msg.status == MessageStatus.READ
                            Text(
                                text = if (isRead) "✓✓" else "✓",
                                color = if (isRead) Color(0xFF00D26A) else Color(0xFF8E8E8E),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Input
        HorizontalDivider(color = Color(0xFF262626))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "📎", fontSize = 24.sp, modifier = Modifier.clickable {}.padding(end = 12.dp))
            Text(text = "📷", fontSize = 24.sp, modifier = Modifier.clickable {}.padding(end = 12.dp))
            
            TextField(
                value = state.inputText,
                onValueChange = { viewModel.updateInput(it) },
                placeholder = { Text("Message...", color = Color.White, fontSize = 14.sp) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { viewModel.sendMessage() }),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFF262626), RoundedCornerShape(20.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF262626),
                    unfocusedContainerColor = Color(0xFF262626),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            
            Text(
                text = "Send",
                color = Color(0xFF0095F6),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable { viewModel.sendMessage() }
                    .padding(start = 12.dp)
            )
        }
    }
}
