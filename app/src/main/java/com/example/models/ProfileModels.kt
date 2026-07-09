package com.example.models

enum class ProfileCategory { CREATOR, BUSINESS, PERSONAL }

data class LinkItem(
    val id: String,
    val title: String,
    val url: String,
    val clickCount: Int = 0
)

data class UserProfile(
    val id: String,
    val username: String,
    val displayName: String,
    val bio: String,
    val externalLink: String,
    val category: ProfileCategory,
    val accentColor: Int,
    val layoutOption: Int,
    val avatarUrl: String? = null,
    val profileFrame: String? = null,
    val bioLinks: List<LinkItem> = emptyList()
)

data class PrivacySettings(
    val isPrivate: Boolean,
    val hiddenFromStoryUserIds: List<String>,
    val showActivityStatus: Boolean,
    val commentFilteringEnabled: Boolean,
    val requireTagApproval: Boolean,
    val blockedUserIds: List<String>
)
