package com.example.ui.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.Comment
import com.example.models.UserShort
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CommentsState(
    val postId: String = "",
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = true,
    val replyToCommentId: String? = null,
    val filterMode: String = "Top" // Top, Recent, Controversial
)

class CommentsViewModel : ViewModel() {
    private val _state = MutableStateFlow(CommentsState())
    val state: StateFlow<CommentsState> = _state.asStateFlow()

    fun loadComments(postId: String) {
        viewModelScope.launch {
            _state.update { it.copy(postId = postId, isLoading = true) }
            delay(500)
            val mockComments = listOf(
                Comment("c1", postId, UserShort("u2", "mkbhd", null), "Great post! Really insightful.", System.currentTimeMillis() - 3600000, 152, true, isPinned = true, isAuthorVerified = true),
                Comment("c2", postId, UserShort("u3", "foodie", null), "Love this! 😍", System.currentTimeMillis() - 7200000, 45, isTopFan = true, replies = listOf(
                    Comment("c2_1", postId, UserShort("u1", "natgeo", null), "Thank you!", System.currentTimeMillis() - 1800000, 12, isAuthorVerified = true)
                )),
                Comment("c3", postId, UserShort("u4", "travel_bug", null), "Where is this taken?", System.currentTimeMillis() - 86400000, 5)
            )
            _state.update { it.copy(comments = mockComments, isLoading = false) }
        }
    }

    fun toggleCommentLike(commentId: String) {
        _state.update { currentState ->
            val updated = toggleLikeInList(currentState.comments, commentId)
            currentState.copy(comments = updated)
        }
    }
    
    private fun toggleLikeInList(comments: List<Comment>, targetId: String): List<Comment> {
        return comments.map { comment ->
            if (comment.id == targetId) {
                val newStatus = !comment.isLiked
                val newCount = if (newStatus) comment.likesCount + 1 else comment.likesCount - 1
                comment.copy(isLiked = newStatus, likesCount = newCount)
            } else if (comment.replies.isNotEmpty()) {
                comment.copy(replies = toggleLikeInList(comment.replies, targetId))
            } else {
                comment
            }
        }
    }

    fun setReplyTo(commentId: String?) {
        _state.update { it.copy(replyToCommentId = commentId) }
    }

    fun postComment(text: String) {
        viewModelScope.launch {
            val newComment = Comment(
                id = "c_new_${System.currentTimeMillis()}",
                postId = _state.value.postId,
                author = UserShort("me", "currentUser", null),
                text = text,
                timestamp = System.currentTimeMillis()
            )
            
            _state.update { currentState ->
                if (currentState.replyToCommentId != null) {
                    val updated = addReplyToList(currentState.comments, currentState.replyToCommentId, newComment)
                    currentState.copy(comments = updated, replyToCommentId = null)
                } else {
                    currentState.copy(comments = listOf(newComment) + currentState.comments)
                }
            }
        }
    }
    
    private fun addReplyToList(comments: List<Comment>, targetId: String, newReply: Comment): List<Comment> {
        return comments.map { comment ->
            if (comment.id == targetId) {
                comment.copy(replies = comment.replies + newReply)
            } else if (comment.replies.isNotEmpty()) {
                comment.copy(replies = addReplyToList(comment.replies, targetId, newReply))
            } else {
                comment
            }
        }
    }
    
    fun deleteComment(commentId: String) {
        _state.update { currentState ->
            val updated = removeCommentFromList(currentState.comments, commentId)
            currentState.copy(comments = updated)
        }
    }
    
    private fun removeCommentFromList(comments: List<Comment>, targetId: String): List<Comment> {
        return comments.filter { it.id != targetId }.map { comment ->
            if (comment.replies.isNotEmpty()) {
                comment.copy(replies = removeCommentFromList(comment.replies, targetId))
            } else {
                comment
            }
        }
    }
}
