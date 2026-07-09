package com.example.ui.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.example.models.Comment

@Composable
fun CommentsScreen(
    viewModel: CommentsViewModel,
    postId: String,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var commentText by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        viewModel.loadComments(postId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                onClick = onNavigateBack,
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .background(
                    color = Color.Black,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .clickable(
                    onClick = {},
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                )
        ) {
            // Handle
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF444444))
                    .align(Alignment.CenterHorizontally)
            )

            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Comments",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { onNavigateBack() }
                )
            }

            HorizontalDivider(color = Color(0xFF262626))

            // Comments List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(state.comments, key = { it.id }) { comment ->
                            CommentItem(
                                comment = comment,
                                onLikeClick = { viewModel.toggleCommentLike(it) },
                                onReplyClick = { viewModel.setReplyTo(it) },
                                isNested = false
                            )
                        }
                    }
                }
            }

            // Input Area
            HorizontalDivider(color = Color(0xFF262626))
            
            if (state.replyToCommentId != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Replying to comment...", color = Color(0xFFA8A8A8), fontSize = 12.sp)
                    Icon(
                        Icons.Default.Close, 
                        contentDescription = "Cancel reply", 
                        tint = Color.White,
                        modifier = Modifier.size(16.dp).clickable { viewModel.setReplyTo(null) }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF667EEA), Color(0xFF764BA2)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Y", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                TextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Add a comment...", color = Color(0xFFA8A8A8), fontSize = 14.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF262626),
                        unfocusedContainerColor = Color(0xFF262626),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                Text(
                    text = "Post",
                    color = if (commentText.isNotBlank()) Color(0xFF0095F6) else Color(0xFF0095F6).copy(alpha = 0.5f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(enabled = commentText.isNotBlank()) {
                        viewModel.postComment(commentText)
                        commentText = ""
                    }
                )
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    onLikeClick: (String) -> Unit,
    onReplyClick: (String) -> Unit,
    isNested: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (isNested) 44.dp else 0.dp, bottom = 16.dp)
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFFF09433), Color(0xFFE6683C), Color(0xFFDC2743)))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.author.username.firstOrNull()?.uppercase() ?: "",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = comment.author.username,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = comment.text,
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.2.sp // 1.4 * 13
                )
            }

            Row(
                modifier = Modifier.padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("2h", color = Color(0xFF8E8E8E), fontSize = 12.sp)
                Text("${comment.likesCount} likes", color = Color(0xFF8E8E8E), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Reply",
                    color = Color(0xFF8E8E8E),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onReplyClick(comment.id) }
                )
            }

            // Replies
            if (comment.replies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                comment.replies.forEach { reply ->
                    CommentItem(
                        comment = reply,
                        onLikeClick = onLikeClick,
                        onReplyClick = onReplyClick,
                        isNested = true
                    )
                }
            }
        }

        // Like Button
        IconButton(
            onClick = { onLikeClick(comment.id) },
            modifier = Modifier.size(24.dp)
        ) {
            Text(
                text = if (comment.isLiked) "❤️" else "🤍",
                fontSize = 14.sp
            )
        }
    }
}
