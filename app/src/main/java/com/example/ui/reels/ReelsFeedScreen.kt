package com.example.ui.reels

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ReelsFeedScreen(
    viewModel: ReelsFeedViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRemix: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val pagerState = rememberPagerState(pageCount = { state.reels.size })

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val reel = state.reels[page]
                ReelPage(
                    reel = reel,
                    onLike = { viewModel.toggleLike(reel.id) },
                    onSave = { viewModel.toggleSave(reel.id) },
                    onRemix = { onNavigateToRemix(reel.id) }
                )
            }
        }
    }
}

@Composable
fun ReelPage(
    reel: ReelInfo,
    onLike: () -> Unit,
    onSave: () -> Unit,
    onRemix: () -> Unit
) {
    var showHeart by remember { mutableStateOf(false) }

    // Heart Animation
    val scale by animateFloatAsState(
        targetValue = if (showHeart) 1.3f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "HeartScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (showHeart) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "HeartAlpha"
    )

    // Spinning animation for disc
    val transition = rememberInfiniteTransition(label = "Spin")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SpinAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
        // Video Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (!reel.isLiked) {
                                onLike()
                            }
                            showHeart = true
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text("🎬", fontSize = 80.sp)

            // Double Tap Heart
            if (showHeart) {
                Text(
                    text = "❤️",
                    fontSize = 120.sp,
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                )
            }
            LaunchedEffect(showHeart) {
                if (showHeart) {
                    delay(800)
                    showHeart = false
                }
            }
        }
        
        // Right Side Actions
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Like
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onLike) {
                    Text(text = if (reel.isLiked) "❤️" else "🤍", fontSize = 28.sp)
                }
                Text(text = "${reel.likes}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            // Comment
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { /* Comments */ }) {
                    Text(text = "💬", fontSize = 28.sp)
                }
                Text(text = "${reel.comments}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            // Share
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { /* Share */ }) {
                    Text(text = "📤", fontSize = 28.sp)
                }
                Text(text = "${reel.shares}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            // Save
            IconButton(onClick = onSave) {
                Text(text = "🔖", fontSize = 28.sp)
            }
            // Remix
            if (reel.isRemixable) {
                IconButton(onClick = onRemix) {
                    Text(text = "🔄", fontSize = 28.sp)
                }
            }
            // Avatar / Audio Disc
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFFF09433), Color(0xFFE6683C), Color(0xFFDC2743))))
                    .rotate(rotation),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = reel.authorUsername.firstOrNull()?.uppercase() ?: "",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // Bottom Info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 80.dp, bottom = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFFF09433), Color(0xFFE6683C), Color(0xFFDC2743)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = reel.authorUsername.firstOrNull()?.uppercase() ?: "",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = reel.authorUsername,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.White, RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clickable { /* Follow */ }
                ) {
                    Text("Follow", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Text(
                text = reel.description,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 19.6.sp, // 1.4 * 14
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF333333))
                        .rotate(rotation),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎵", fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = reel.audioTrack, color = Color(0xFFA8A8A8), fontSize = 12.sp)
            }
        }
    }
}
