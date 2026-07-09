package com.example.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit // Kept for compatibility if used by caller
) {
    val state by viewModel.state.collectAsState()

    val tabs = listOf(
        null to "All",
        NotificationType.LIKE to "Likes",
        NotificationType.COMMENT to "Comments",
        NotificationType.FOLLOW to "Follows",
        NotificationType.MENTION to "Mentions"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Activity",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(color = Color(0xFF262626))

        // Tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tabs) { (type, label) ->
                val isSelected = state.selectedFilter == type
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color.White else Color(0xFF262626))
                        .clickable { viewModel.setFilter(type) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.Black else Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0xFF262626))

        // Notifications List
        val filteredList = if (state.selectedFilter == null) {
            state.notifications
        } else {
            state.notifications.filter { it.type == state.selectedFilter }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredList) { notif ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Handle click */ }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFFF09433), Color(0xFFE6683C), Color(0xFFDC2743)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = notif.avatar,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Content
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                    append(notif.actorName)
                                }
                                withStyle(SpanStyle(color = Color(0xFFA8A8A8))) {
                                    append(" ${notif.content}")
                                }
                            },
                            color = Color.White,
                            fontSize = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = notif.timeStr,
                            color = Color(0xFF8E8E8E),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Trailing Action/Preview
                    if (notif.type == NotificationType.FOLLOW && notif.isFollowing != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (notif.isFollowing) Color(0xFF262626) else Color(0xFF0095F6))
                                .border(1.dp, if (notif.isFollowing) Color(0xFF262626) else Color(0xFF0095F6), RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleFollow(notif.id) }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (notif.isFollowing) "Following" else "Follow back",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else if (notif.preview != null) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF262626)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = notif.preview, fontSize = 24.sp)
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFF1A1A1A))
            }
        }
    }
}
