package com.example.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun UserAvatar(
    displayName: String,
    avatarUrl: String?,
    profileFrame: String?,
    size: Dp = 80.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        val initials = displayName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }

        val gradient = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.tertiary
            )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(if (profileFrame != null) 4.dp else 0.dp)
                .clip(CircleShape)
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            if (avatarUrl.isNullOrEmpty()) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.35f).sp
                )
            } else {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.matchParentSize()
                )
            }
        }

        if (profileFrame != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(3.dp, MaterialTheme.colorScheme.secondary, CircleShape)
            )
        }
    }
}

@Composable
fun RichTextBio(
    bio: String,
    onMentionClick: (String) -> Unit,
    onHashtagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val mentionRegex = Regex("(@\\w+)")
    val hashtagRegex = Regex("(#\\w+)")
    val words = bio.split(Regex("(?<=\\s)|(?=\\s)"))

    val annotatedString = buildAnnotatedString {
        words.forEach { word ->
            when {
                word.matches(mentionRegex) -> {
                    pushStringAnnotation(tag = "MENTION", annotation = word)
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                        append(word)
                    }
                    pop()
                }
                word.matches(hashtagRegex) -> {
                    pushStringAnnotation(tag = "HASHTAG", annotation = word)
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)) {
                        append(word)
                    }
                    pop()
                }
                else -> {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                        append(word)
                    }
                }
            }
        }
    }

    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "MENTION", start = offset, end = offset)
                .firstOrNull()?.let { onMentionClick(it.item) }
            
            annotatedString.getStringAnnotations(tag = "HASHTAG", start = offset, end = offset)
                .firstOrNull()?.let { onHashtagClick(it.item) }
        },
        modifier = modifier
    )
}
