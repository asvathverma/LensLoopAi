package com.example.ui.story

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class MockStory(
    val id: String,
    val username: String,
    val avatar: String,
    val time: String,
    val content: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryViewerScreen(
    initialIndex: Int = 0,
    onNavigateBack: () -> Unit
) {
    val stories = remember {
        listOf(
            MockStory("1", "priya_sharma", "P", "2h", "📸"),
            MockStory("2", "rahul_vlogs", "R", "4h", "🎥"),
            MockStory("3", "neha_travels", "N", "5h", "🌴"),
            MockStory("4", "tech_guru", "T", "6h", "💻")
        )
    }

    var currentIndex by remember { mutableStateOf(initialIndex.coerceIn(0, stories.size - 1)) }
    var progress by remember { mutableStateOf(0f) }
    var isPaused by remember { mutableStateOf(false) }

    val currentStory = stories[currentIndex]
    val storyDuration = 5000L

    // Progress animation
    LaunchedEffect(currentIndex, isPaused) {
        if (!isPaused) {
            val startTime = System.currentTimeMillis() - (progress * storyDuration).toLong()
            while (progress < 1f && !isPaused) {
                delay(16) // ~60fps
                progress = (System.currentTimeMillis() - startTime).toFloat() / storyDuration
            }
            if (progress >= 1f) {
                if (currentIndex < stories.size - 1) {
                    currentIndex++
                    progress = 0f
                } else {
                    onNavigateBack()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount -> 
                        if (dragAmount.y > 50) {
                            onNavigateBack()
                        }
                    }
                )
            }
    ) {
        // Story Content Background (Dark overlay handled by Brush later)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPaused = true
                            val success = tryAwaitRelease()
                            isPaused = false
                        },
                        onTap = { offset ->
                            val width = size.width
                            if (offset.x < width / 3) {
                                // Previous
                                if (currentIndex > 0) {
                                    currentIndex--
                                    progress = 0f
                                } else {
                                    progress = 0f
                                }
                            } else {
                                // Next
                                if (currentIndex < stories.size - 1) {
                                    currentIndex++
                                    progress = 0f
                                } else {
                                    onNavigateBack()
                                }
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = currentStory.content, fontSize = 100.sp)
        }

        // Overlay Gradients
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Black.copy(alpha = 0.6f),
                        0.2f to Color.Transparent,
                        0.8f to Color.Transparent,
                        1.0f to Color.Black.copy(alpha = 0.6f)
                    )
                )
        )

        // Progress Bars & Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
        ) {
            // Progress bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                stories.forEachIndexed { idx, _ ->
                    val segmentProgress = when {
                        idx < currentIndex -> 1f
                        idx == currentIndex -> progress
                        else -> 0f
                    }
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(Color.White.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(segmentProgress)
                                .background(Color.White)
                        )
                    }
                }
            }

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFFF09433), Color(0xFFE6683C), Color(0xFFDC2743))))
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentStory.avatar,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentStory.username,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = currentStory.time,
                        color = Color(0xFFA8A8A8),
                        fontSize = 12.sp
                    )
                }

                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }

        // Bottom Input
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Reply to ${currentStory.username}...", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.White
                )
            )

            Icon(
                Icons.Default.FavoriteBorder,
                contentDescription = "Like",
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { /* Like */ }
            )

            Icon(
                Icons.Default.Send,
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { /* Send */ }
            )
        }
    }
}
