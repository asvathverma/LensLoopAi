package com.example.data

import com.example.models.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PostRepository {
    private val _localPosts = MutableStateFlow<List<Post>>(emptyList())
    val localPosts: StateFlow<List<Post>> = _localPosts.asStateFlow()

    fun addPost(post: Post) {
        _localPosts.value = listOf(post) + _localPosts.value
    }
}
