package com.example.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import com.example.ui.share.ShareBottomSheet
import androidx.compose.material.icons.filled.Send
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.models.Post
import com.example.models.PostType
import com.example.models.MediaType
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.text.ClickableText

@Composable
fun RichTextCaption(
    text: String,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    onHashtagClick: (String) -> Unit,
    onMentionClick: (String) -> Unit
) {
    val displayText = if (text.length > 100 && !isExpanded) text.take(100) + "... more" else text
    
    val annotatedString = buildAnnotatedString {
        val words = displayText.split(Regex("(?<=\\s)|(?=\\s)"))
        for (word in words) {
            when {
                word.startsWith("#") && word.length > 1 -> {
                    pushStringAnnotation(tag = "HASHTAG", annotation = word.substring(1))
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                        append(word)
                    }
                    pop()
                }
                word.startsWith("@") && word.length > 1 -> {
                    pushStringAnnotation(tag = "MENTION", annotation = word.substring(1))
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                        append(word)
                    }
                    pop()
                }
                word == "... more" -> {
                    pushStringAnnotation(tag = "MORE", annotation = "more")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                        append(word)
                    }
                    pop()
                }
                else -> append(word)
            }
        }
    }
    
    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "HASHTAG", start = offset, end = offset).firstOrNull()?.let {
                onHashtagClick(it.item)
            }
            annotatedString.getStringAnnotations(tag = "MENTION", start = offset, end = offset).firstOrNull()?.let {
                onMentionClick(it.item)
            }
            annotatedString.getStringAnnotations(tag = "MORE", start = offset, end = offset).firstOrNull()?.let {
                onExpand()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToComments: (String) -> Unit,
    onNavigateToCollections: () -> Unit,
    onNavigateToStoryCamera: () -> Unit,
    onNavigateToStoryViewer: () -> Unit,
    onNavigateToReelsCamera: () -> Unit,
    onNavigateToProduct: (String) -> Unit
) {
    val posts by viewModel.feedPosts.collectAsState()
    val stories by viewModel.stories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var postToShare by remember { mutableStateOf<Post?>(null) }
    
    if (postToShare != null) {
        ShareBottomSheet(
            post = postToShare!!,
            onDismiss = { postToShare = null },
            onShareToStory = { postToShare = null },
            onShareToCloseFriends = { postToShare = null },
            onSendToDirect = { friend -> postToShare = null },
            onCopyLink = { postToShare = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LensLoop", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                actions = {
                    IconButton(onClick = onNavigateToCollections) {
                        Icon(Icons.Default.Bookmark, contentDescription = "Collections")
                    }
                    IconButton(onClick = onNavigateToCreatePost) {
                        Icon(Icons.Default.AddBox, contentDescription = "Create Post")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    item {
                        if (stories.isNotEmpty()) {
                            androidx.compose.foundation.lazy.LazyRow(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(stories) { story ->
                                    StoryItem(story = story, onClick = { 
                                        if (story.author.username == "me" && story.segments.isEmpty()) {
                                            onNavigateToStoryCamera()
                                        } else {
                                            onNavigateToStoryViewer()
                                        }
                                    })
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                    
                    items(posts, key = { it.id }) { post ->
                        PostItem(
                            post = post,
                            onLikeClick = { viewModel.toggleLike(post.id) },
                            onSaveClick = { viewModel.toggleSave(post.id) },
                            onDeleteClick = { viewModel.deletePost(post.id) },
                            onArchiveClick = { viewModel.archivePost(post.id) },
                            onNavigateToComments = { onNavigateToComments(post.id) },
                            onShareClick = { postToShare = post },
                            onNavigateToProduct = onNavigateToProduct
                        )
                    }
                }
            }
        }
    }
}

fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 10_000 -> String.format("%.1fK", count / 1000.0)
        count >= 1_000 -> String.format("%,d", count)
        else -> count.toString()
    }
}

@Composable
fun StoryItem(story: com.example.models.UserStory, onClick: () -> Unit) {
    val ringColor = when {
        story.isCloseFriends && story.hasUnseen -> Color(0xFF4CAF50)
        story.hasUnseen -> MaterialTheme.colorScheme.primary
        else -> Color.Gray
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    ringColor,
                    shape = androidx.compose.foundation.shape.CircleShape
                )
                .padding(3.dp)
                .background(MaterialTheme.colorScheme.background, shape = androidx.compose.foundation.shape.CircleShape)
                .padding(2.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (story.author.username == "me" && story.segments.isEmpty()) {
                Icon(Icons.Default.AddBox, contentDescription = "Add Story")
            } else {
                Text(story.author.username.first().uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(story.author.username, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun PostItem(
    post: Post,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onArchiveClick: () -> Unit,
    onNavigateToComments: () -> Unit,
    onShareClick: () -> Unit,
    onNavigateToProduct: (String) -> Unit
) {
    var showOptions by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(post.author.username.first().uppercase(), color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(post.author.username, fontWeight = FontWeight.Bold)
                        if (post.coAuthor != null) {
                            Text(" & ", fontWeight = FontWeight.Medium)
                            Text(post.coAuthor.username, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (post.isPaidPartnership) {
                        val brandName = post.partnerBrand?.username ?: "partner"
                        Text("Paid partnership with $brandName", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    } else if (post.location != null) {
                        Text(post.location.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Box {
                IconButton(onClick = { showOptions = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
                DropdownMenu(expanded = showOptions, onDismissRequest = { showOptions = false }) {
                    DropdownMenuItem(text = { Text("Edit Caption") }, onClick = { showOptions = false })
                    DropdownMenuItem(text = { Text("Archive") }, onClick = { showOptions = false; onArchiveClick() })
                    DropdownMenuItem(text = { Text("Delete") }, onClick = { showOptions = false; onDeleteClick() })
                }
            }
        }

        val pagerState = rememberPagerState(pageCount = { post.media.size })
        var showLikeAnimation by remember { mutableStateOf(false) }
        
        Box(
            modifier = Modifier.fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (!post.isLiked) {
                                onLikeClick()
                            }
                            showLikeAnimation = true
                        }
                    )
                }
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) { page ->
                val media = post.media[page]
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (media.url.startsWith("content://") || media.url.startsWith("http")) {
                        coil.compose.AsyncImage(
                            model = media.url,
                            contentDescription = null,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        if (media.type == MediaType.VIDEO) {
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Videocam, contentDescription = "Video", tint = Color.White, modifier = Modifier.size(64.dp))
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (media.type == MediaType.VIDEO) Icons.Default.Videocam else Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (media.type == MediaType.VIDEO) "Video Player Placeholder" else "Image Placeholder",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (!media.slideCaption.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = media.slideCaption,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp)).padding(8.dp)
                        )
                    }

                    // Product Tags Overlay
                    if (post.taggedProducts.isNotEmpty() && page == 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                                .clickable { onNavigateToProduct(post.taggedProducts.first().id) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalMall, contentDescription = "Products", tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("View ${post.taggedProducts.size} Products", color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (post.media.size > 1) {
                Row(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(post.media.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(6.dp)
                        )
                    }
                }
                
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1}/${post.media.size}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            if (showLikeAnimation) {
                LaunchedEffect(Unit) {
                    delay(800)
                    showLikeAnimation = false
                }
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onLikeClick) {
                Icon(
                    imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (post.isLiked) Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onNavigateToComments) {
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Comment")
            }
            IconButton(onClick = onShareClick) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Share")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text("Score: ${String.format("%.2f", post.rankingScore)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            IconButton(onClick = onSaveClick) {
                Icon(
                    imageVector = if (post.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Save",
                    tint = if (post.isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text(
            text = "${formatCount(post.likesCount)} likes",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        var isCaptionExpanded by remember { mutableStateOf(false) }
        var isTranslated by remember { mutableStateOf(false) }
        
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Text(post.author.username, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                RichTextCaption(
                    text = if (isTranslated) "${post.caption} (Translated)" else post.caption,
                    isExpanded = isCaptionExpanded,
                    onExpand = { isCaptionExpanded = true },
                    onHashtagClick = { /* Handle navigation to hashtag */ },
                    onMentionClick = { /* Handle navigation to profile */ }
                )
                Text(
                    text = if (isTranslated) "See Original" else "See Translation",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { isTranslated = !isTranslated }.padding(top = 2.dp)
                )
            }
        }
        
        Text(
            text = "View all ${formatCount(post.commentsCount)} comments",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable { onNavigateToComments() }
        )
    }
}
