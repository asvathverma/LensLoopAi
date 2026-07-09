package com.example.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.models.Post
import com.example.models.UserShort

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    post: Post,
    onDismiss: () -> Unit,
    onShareToStory: () -> Unit,
    onShareToCloseFriends: () -> Unit,
    onSendToDirect: (UserShort) -> Unit,
    onCopyLink: () -> Unit
) {
    val shareOptions = listOf(
        ShareOption("💬", "WhatsApp", Color(0xFF25D366)),
        ShareOption("📨", "Messenger", Color(0xFF0084FF)),
        ShareOption("🐦", "Twitter", Color(0xFF1DA1F2)),
        ShareOption("🔗", "Copy Link", Color(0xFF8E8E8E)),
        ShareOption("✉️", "SMS", Color(0xFF34B7F1))
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF121212),
        scrimColor = Color.Black.copy(alpha = 0.7f),
        dragHandle = null, // Custom handle in the content
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp, top = 16.dp, end = 0.dp, bottom = 32.dp)
        ) {
            // Handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF444444))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Share to Story
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        onShareToStory()
                        onDismiss()
                    }
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFFF09433), Color(0xFFE6683C), Color(0xFFDC2743)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text("➕", fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text("Add post to your story", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("Share this post as a story", color = Color(0xFF8E8E8E), fontSize = 12.sp)
                }
            }

            HorizontalDivider(color = Color(0xFF262626))

            // Share Options
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = "Share to",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    shareOptions.forEach { option ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .widthIn(min = 64.dp)
                                .clickable {
                                    if (option.label == "Copy Link") {
                                        onCopyLink()
                                    }
                                    onDismiss()
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(option.color.copy(alpha = 0.12f)), // ~20 opacity
                                contentAlignment = Alignment.Center
                            ) {
                                Text(option.icon, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(option.label, color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }

            // More Options
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF262626), RoundedCornerShape(12.dp))
                        .clickable { onDismiss() }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("More options...", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cancel
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF262626))
                        .clickable { onDismiss() }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cancel", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
    }
}

data class ShareOption(val icon: String, val label: String, val color: Color)
