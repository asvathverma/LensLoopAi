package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.auth.AuthState
import com.example.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AuthViewModel,
    profileViewModel: com.example.ui.profile.ProfileViewModel,
    onLogout: () -> Unit,
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToPrivacySettings: () -> Unit,
    onNavigateToLinkInBio: () -> Unit,
    onNavigateToFollowers: () -> Unit,
    onNavigateToFollowing: () -> Unit,
    onNavigateToFeed: () -> Unit,
    onNavigateToStoryArchive: () -> Unit,
    onNavigateToReelsFeed: () -> Unit,
    onNavigateToLongFormUpload: () -> Unit,
    onNavigateToLiveStream: () -> Unit,
    onNavigateToDMs: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSuggested: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToTimeManagement: () -> Unit,
    onNavigateToCloseFriends: () -> Unit,
    onNavigateToProfilePosts: () -> Unit,
    onNavigateToBrandedContent: () -> Unit,
    onNavigateToAffiliateMarketing: () -> Unit,
    onNavigateToCreatorMarketplace: () -> Unit,
    onNavigateToSubscriptions: () -> Unit,
    onNavigateToVirtualEconomy: () -> Unit,
    onNavigateToAdsManager: () -> Unit,
    onNavigateToAnalyticsDashboard: () -> Unit,
    onNavigateToThemeSettings: () -> Unit,
    onNavigateToQRCode: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val highlights by profileViewModel.highlights.collectAsState()

    val user = if (authState is AuthState.Authenticated) {
        (authState as AuthState.Authenticated).user
    } else {
        null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LensLoop Dashboard", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(onClick = onNavigateToSuggested) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Discover People")
                    }
                    IconButton(onClick = onNavigateToNotifications) {
                        BadgedBox(
                            badge = {
                                Badge { Text("3") }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }
                    IconButton(onClick = onNavigateToDMs) {
                        Icon(Icons.Default.Send, contentDescription = "Messages")
                    }
                    IconButton(
                        onClick = {
                            viewModel.logout()
                            onLogout()
                        },
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (user != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        com.example.ui.profile.UserAvatar(
                            displayName = user.fullName.ifBlank { "User" },
                            avatarUrl = null, // Can map from profile later
                            profileFrame = null,
                            size = 120.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Welcome, ${user.fullName}!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        user.email?.let {
                            Text(
                                text = "Email: $it",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onNavigateToFollowers() }) {
                                Text("250", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                                Text("Followers", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onNavigateToFollowing() }) {
                                Text("180", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                                Text("Following", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        
                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Text(
                                "Account Verified", 
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                
                if (highlights.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        item {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { /* Create new highlight */ }) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "New Highlight")
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("New", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        item {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onNavigateToStoryArchive() }) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.History, contentDescription = "Archive")
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Archive", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        items(highlights) { highlight ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { /* View highlight */ }) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(highlight.title, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                
                Button(
                    onClick = onNavigateToExplore,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(Icons.Default.Explore, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Explore", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToLiveStream,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color.Red,
                        contentColor = androidx.compose.ui.graphics.Color.White
                    )
                ) {
                    Icon(Icons.Default.LiveTv, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Go Live", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToReelsFeed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                ) {
                    Icon(Icons.Default.MovieFilter, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reels Feed", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToLongFormUpload,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(Icons.Default.VideoLibrary, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Long-Form Video", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToFeed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(Icons.Default.DynamicFeed, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Feed", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToProfileEdit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Profile", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToPrivacySettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Privacy Settings", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToThemeSettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(Icons.Default.Palette, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Theme & Accessibility", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToQRCode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.QrCode2, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("QR Code / Nametag", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToSecurity,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                ) {
                    Icon(Icons.Default.Security, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Security & Login", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToTimeManagement,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Your Activity & Time", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToCloseFriends,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                        contentColor = androidx.compose.ui.graphics.Color.White
                    )
                ) {
                    Icon(Icons.Default.Star, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Close Friends List", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToProfilePosts,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(Icons.Default.GridOn, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Profile Grid", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToBrandedContent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFFFF9800),
                        contentColor = androidx.compose.ui.graphics.Color.White
                    )
                ) {
                    Icon(Icons.Default.BusinessCenter, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Branded Content Tools", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToAffiliateMarketing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFFE91E63),
                        contentColor = androidx.compose.ui.graphics.Color.White
                    )
                ) {
                    Icon(Icons.Default.Storefront, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Affiliate Marketing", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToCreatorMarketplace,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFF9C27B0),
                        contentColor = androidx.compose.ui.graphics.Color.White
                    )
                ) {
                    Icon(Icons.Default.PersonSearch, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Creator Marketplace", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToSubscriptions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFF673AB7),
                        contentColor = androidx.compose.ui.graphics.Color.White
                    )
                ) {
                    Icon(Icons.Default.Star, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Subscriptions & Exclusive", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToVirtualEconomy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFFFFC107),
                        contentColor = androidx.compose.ui.graphics.Color.Black
                    )
                ) {
                    Icon(Icons.Default.CardGiftcard, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Badges & Virtual Gifts", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToAdsManager,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFF03A9F4),
                        contentColor = androidx.compose.ui.graphics.Color.White
                    )
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ads Manager", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToAnalyticsDashboard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFF009688),
                        contentColor = androidx.compose.ui.graphics.Color.White
                    )
                ) {
                    Icon(Icons.Default.Insights, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Professional Analytics", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToLinkInBio,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Preview Link in Bio", fontWeight = FontWeight.Bold)
                }
            } else {
                Text("Not logged in.")
            }
        }
    }
}
