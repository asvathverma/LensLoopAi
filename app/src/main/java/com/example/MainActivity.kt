package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.auth.AuthState
import com.example.auth.AuthViewModel
import com.example.auth.AuthViewModelFactory
import com.example.auth.TokenManager
import com.example.ui.auth.DashboardScreen
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.OtpScreen
import com.example.ui.auth.RegisterScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.onboarding.OnboardingViewModel
import com.example.ui.onboarding.OnboardingViewModelFactory
import com.example.ui.theme.MyApplicationTheme
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.padding
import com.example.ui.components.LensLoopBottomNav

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    LensLoopApp()
                }
            }
        }
    }
}

@Composable
fun LensLoopApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context.applicationContext) }
    
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))
    val authState by viewModel.authState.collectAsState()

    val startDestination = if (authState is AuthState.Authenticated) {
        if (tokenManager.hasCompletedOnboarding()) "dashboard" else "onboarding"
    } else {
        "login"
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""
    val bottomNavRoutes = listOf("dashboard", "feed", "search", "explore", "reels_feed", "create_post", "profile_posts")
    val showBottomNav = bottomNavRoutes.contains(currentRoute)

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                LensLoopBottomNav(
                    currentRoute = currentRoute,
                    onNavigateToRoute = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = {
                    val nextRoute = if (tokenManager.hasCompletedOnboarding()) "dashboard" else "onboarding"
                    navController.navigate(nextRoute) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onOtpRequired = { emailOrPhone ->
                    navController.navigate("otp/$emailOrPhone")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = viewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onOtpRequired = { emailOrPhone ->
                    navController.navigate("otp/$emailOrPhone") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
        composable("otp/{emailOrPhone}") { backStackEntry ->
            val emailOrPhone = backStackEntry.arguments?.getString("emailOrPhone") ?: ""
            OtpScreen(
                viewModel = viewModel,
                emailOrPhone = emailOrPhone,
                onVerificationSuccess = {
                    val nextRoute = if (tokenManager.hasCompletedOnboarding()) "dashboard" else "onboarding"
                    navController.navigate(nextRoute) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("onboarding") {
            val onboardingViewModel: OnboardingViewModel = viewModel(factory = OnboardingViewModelFactory(context))
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onOnboardingComplete = {
                    navController.navigate("dashboard") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("dashboard") {
            val profileViewModel: com.example.ui.profile.ProfileViewModel = viewModel()
            DashboardScreen(
                viewModel = viewModel,
                profileViewModel = profileViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                onNavigateToProfileEdit = { navController.navigate("profile_edit") },
                onNavigateToPrivacySettings = { navController.navigate("privacy_settings") },
                onNavigateToLinkInBio = { navController.navigate("link_in_bio") },
                onNavigateToFollowers = { navController.navigate("follow_list/followers") },
                onNavigateToFollowing = { navController.navigate("follow_list/following") },
                onNavigateToFeed = { navController.navigate("feed") },
                onNavigateToStoryArchive = { navController.navigate("story_archive") },
                onNavigateToReelsFeed = { navController.navigate("reels_feed") },
                onNavigateToLongFormUpload = { navController.navigate("long_form_upload") },
                onNavigateToLiveStream = { navController.navigate("live_stream") },
                onNavigateToDMs = { navController.navigate("dm_list") },
                onNavigateToExplore = { navController.navigate("explore") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToSuggested = { navController.navigate("suggested_accounts") },
                onNavigateToSecurity = { navController.navigate("security_settings") },
                onNavigateToTimeManagement = { navController.navigate("time_management") },
                onNavigateToCloseFriends = { navController.navigate("close_friends") },
                onNavigateToProfilePosts = { navController.navigate("profile_posts") },
                onNavigateToBrandedContent = { navController.navigate("branded_content") },
                onNavigateToAffiliateMarketing = { navController.navigate("affiliate_marketing") },
                onNavigateToCreatorMarketplace = { navController.navigate("creator_marketplace") },
                onNavigateToSubscriptions = { navController.navigate("subscriptions") },
                onNavigateToVirtualEconomy = { navController.navigate("virtual_economy") },
                onNavigateToAdsManager = { navController.navigate("ads_manager") },
                onNavigateToAnalyticsDashboard = { navController.navigate("analytics_dashboard") },
                onNavigateToThemeSettings = { navController.navigate("theme_settings") },
                onNavigateToQRCode = { navController.navigate("qr_code") }
            )
        }
        composable("profile_edit") {
            val profileViewModel: com.example.ui.profile.ProfileViewModel = viewModel()
            com.example.ui.profile.ProfileEditScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("profile_posts") {
            val profilePostsViewModel: com.example.ui.profile.ProfilePostsViewModel = viewModel()
            com.example.ui.profile.ProfilePostsScreen(
                viewModel = profilePostsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("close_friends") {
            val closeFriendsViewModel: com.example.ui.profile.CloseFriendsViewModel = viewModel()
            com.example.ui.profile.CloseFriendsScreen(
                viewModel = closeFriendsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("branded_content") {
            val brandedContentViewModel: com.example.ui.monetization.BrandedContentViewModel = viewModel()
            com.example.ui.monetization.BrandedContentScreen(
                viewModel = brandedContentViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("affiliate_marketing") {
            val affiliateViewModel: com.example.ui.monetization.AffiliateMarketingViewModel = viewModel()
            com.example.ui.monetization.AffiliateMarketingScreen(
                viewModel = affiliateViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("creator_marketplace") {
            val marketplaceViewModel: com.example.ui.monetization.CreatorMarketplaceViewModel = viewModel()
            com.example.ui.monetization.CreatorMarketplaceScreen(
                viewModel = marketplaceViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("subscriptions") {
            val subscriptionsViewModel: com.example.ui.monetization.SubscriptionsViewModel = viewModel()
            com.example.ui.monetization.SubscriptionsScreen(
                viewModel = subscriptionsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("virtual_economy") {
            val virtualEconomyViewModel: com.example.ui.monetization.VirtualEconomyViewModel = viewModel()
            com.example.ui.monetization.VirtualEconomyScreen(
                viewModel = virtualEconomyViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("ads_manager") {
            val adsManagerViewModel: com.example.ui.ads.AdsManagerViewModel = viewModel()
            com.example.ui.ads.AdsManagerScreen(
                viewModel = adsManagerViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("analytics_dashboard") {
            val analyticsViewModel: com.example.ui.analytics.AnalyticsViewModel = viewModel()
            com.example.ui.analytics.AnalyticsDashboardScreen(
                viewModel = analyticsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("theme_settings") {
            val themeSettingsViewModel: com.example.ui.settings.ThemeSettingsViewModel = viewModel()
            com.example.ui.settings.ThemeSettingsScreen(
                viewModel = themeSettingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("qr_code") {
            com.example.ui.profile.QRCodeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("shop_product/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            val shopViewModel: com.example.ui.shop.ShopViewModel = viewModel()
            com.example.ui.shop.ProductDetailScreen(
                productId = productId,
                viewModel = shopViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCheckout = { id -> navController.navigate("checkout/$id") }
            )
        }

        composable("checkout/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            val shopViewModel: com.example.ui.shop.ShopViewModel = viewModel()
            com.example.ui.shop.CheckoutScreen(
                productId = productId,
                viewModel = shopViewModel,
                onNavigateBack = { navController.popBackStack() },
                onOrderComplete = { 
                    navController.popBackStack("feed", false)
                }
            )
        }

        composable("privacy_settings") {
            val privacySafetyViewModel: com.example.ui.settings.PrivacySafetyViewModel = viewModel()
            com.example.ui.settings.PrivacySafetyScreen(
                viewModel = privacySafetyViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToContentModeration = { navController.navigate("content_moderation") },
                onNavigateToParentalControls = { navController.navigate("parental_controls") }
            )
        }
        
        composable("content_moderation") {
            val moderationViewModel: com.example.ui.settings.ContentModerationViewModel = viewModel()
            com.example.ui.settings.ContentModerationScreen(
                viewModel = moderationViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("parental_controls") {
            val parentalViewModel: com.example.ui.settings.ParentalControlsViewModel = viewModel()
            com.example.ui.settings.ParentalControlsScreen(
                viewModel = parentalViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("link_in_bio") {
            val profileViewModel: com.example.ui.profile.ProfileViewModel = viewModel()
            com.example.ui.profile.LinkInBioScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "follow_list/{type}",
            arguments = listOf(androidx.navigation.navArgument("type") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val listTypeStr = backStackEntry.arguments?.getString("type") ?: "followers"
            val listType = if (listTypeStr == "following") com.example.models.FollowListType.FOLLOWING else com.example.models.FollowListType.FOLLOWERS
            val followViewModel: com.example.ui.profile.FollowViewModel = viewModel()
            
            com.example.ui.profile.FollowListScreen(
                viewModel = followViewModel,
                username = "User", // This should be fetched properly, just a mock for now
                initialType = listType,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("feed") {
            val feedViewModel: com.example.ui.feed.FeedViewModel = viewModel()
            com.example.ui.feed.FeedScreen(
                viewModel = feedViewModel,
                onNavigateToCreatePost = { navController.navigate("create_post") },
                onNavigateToComments = { postId -> navController.navigate("comments/$postId") },
                onNavigateToCollections = { navController.navigate("collections") },
                onNavigateToStoryCamera = { navController.navigate("story_camera") },
                onNavigateToStoryViewer = { navController.navigate("story_viewer") },
                onNavigateToReelsCamera = { navController.navigate("reels_camera") },
                onNavigateToProduct = { id -> navController.navigate("shop_product/$id") }
            )
        }
        composable("create_post") {
            com.example.ui.camera.CameraScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "hashtag/{name}",
            arguments = listOf(androidx.navigation.navArgument("name") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val hashtagName = backStackEntry.arguments?.getString("name") ?: ""
            val hashtagViewModel: com.example.ui.hashtag.HashtagViewModel = viewModel()
            com.example.ui.hashtag.HashtagScreen(
                viewModel = hashtagViewModel,
                hashtagName = hashtagName,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHashtag = { navController.navigate("hashtag/$it") }
            )
        }
        dialog(
            route = "comments/{postId}",
            arguments = listOf(androidx.navigation.navArgument("postId") { type = androidx.navigation.NavType.StringType }),
            dialogProperties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            val commentsViewModel: com.example.ui.comments.CommentsViewModel = viewModel()
            com.example.ui.comments.CommentsScreen(
                viewModel = commentsViewModel,
                postId = postId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("collections") {
            val collectionsViewModel: com.example.ui.collections.CollectionsViewModel = viewModel()
            com.example.ui.collections.CollectionsScreen(
                viewModel = collectionsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("story_camera") {
            val storyCameraViewModel: com.example.ui.story.StoryCameraViewModel = viewModel()
            com.example.ui.story.StoryCameraScreen(
                viewModel = storyCameraViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditor = { navController.navigate("story_editor") {
                    popUpTo("story_camera") { inclusive = true }
                } }
            )
        }
        composable("story_viewer") {
            com.example.ui.story.StoryViewerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("story_editor") {
            val storyEditorViewModel: com.example.ui.story.StoryEditorViewModel = viewModel()
            com.example.ui.story.StoryEditorScreen(
                viewModel = storyEditorViewModel,
                onNavigateBack = { navController.popBackStack() },
                onPublish = { 
                    navController.popBackStack("feed", inclusive = false)
                }
            )
        }
        composable("reels_feed") {
            val reelsFeedViewModel: com.example.ui.reels.ReelsFeedViewModel = viewModel()
            com.example.ui.reels.ReelsFeedScreen(
                viewModel = reelsFeedViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRemix = { reelId -> 
                    navController.navigate("reels_remix/$reelId") 
                }
            )
        }
        composable(
            route = "reels_remix/{reelId}",
            arguments = listOf(androidx.navigation.navArgument("reelId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val reelId = backStackEntry.arguments?.getString("reelId") ?: ""
            val reelsCameraViewModel: com.example.ui.story.ReelsCameraViewModel = viewModel()
            reelsCameraViewModel.startRemix("https://example.com/mock_reel_$reelId.mp4")
            
            com.example.ui.story.ReelsCameraScreen(
                viewModel = reelsCameraViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditor = { navController.navigate("story_editor") }
            )
        }
        composable("long_form_upload") {
            val longFormVideoViewModel: com.example.ui.video.LongFormVideoViewModel = viewModel()
            com.example.ui.video.LongFormVideoUploadScreen(
                viewModel = longFormVideoViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "reels_analytics/{reelId}",
            arguments = listOf(androidx.navigation.navArgument("reelId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val reelId = backStackEntry.arguments?.getString("reelId") ?: ""
            val analyticsViewModel: com.example.ui.reels.ReelsAnalyticsViewModel = viewModel()
            com.example.ui.reels.ReelsAnalyticsScreen(
                viewModel = analyticsViewModel,
                reelId = reelId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("live_stream") {
            val liveStreamViewModel: com.example.ui.live.LiveStreamViewModel = viewModel()
            com.example.ui.live.LiveStreamScreen(
                viewModel = liveStreamViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("dm_list") {
            val dmViewModel: com.example.ui.dm.DMViewModel = viewModel()
            com.example.ui.dm.DMListScreen(
                viewModel = dmViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToChat = { id -> navController.navigate("dm_chat/$id") }
            )
        }
        
        composable("dm_chat/{convoId}",
            arguments = listOf(androidx.navigation.navArgument("convoId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val convoId = backStackEntry.arguments?.getString("convoId") ?: ""
            // Assuming DMViewModel is scoped to the activity to share state, or passing data.
            // Using a shared viewmodel for this mock app
            val dmViewModel: com.example.ui.dm.DMViewModel = viewModel()
            com.example.ui.dm.DMChatScreen(
                viewModel = dmViewModel,
                onNavigateBack = { navController.popBackStack() },
                onCall = { isVideo -> /* navigate to call screen if exists */ }
            )
        }
        
        composable("explore") {
            val exploreViewModel: com.example.ui.explore.ExploreViewModel = viewModel()
            com.example.ui.explore.ExploreScreen(
                viewModel = exploreViewModel,
                onNavigateToSearch = { navController.navigate("search") }
            )
        }
        
        composable("search") {
            val searchViewModel: com.example.ui.explore.SearchViewModel = viewModel()
            com.example.ui.explore.SearchScreen(
                viewModel = searchViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("notifications") {
            val notificationsViewModel: com.example.ui.notifications.NotificationsViewModel = viewModel()
            com.example.ui.notifications.NotificationsScreen(
                viewModel = notificationsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate("notification_settings") }
            )
        }

        composable("suggested_accounts") {
            val suggestedViewModel: com.example.ui.explore.SuggestedAccountsViewModel = viewModel()
            com.example.ui.explore.SuggestedAccountsScreen(
                viewModel = suggestedViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("notification_settings") {
            val settingsViewModel: com.example.ui.notifications.NotificationSettingsViewModel = viewModel()
            com.example.ui.notifications.NotificationSettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("security_settings") {
            val securityViewModel: com.example.ui.settings.SecurityViewModel = viewModel()
            com.example.ui.settings.SecurityScreen(
                viewModel = securityViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAccountRecovery = { navController.navigate("account_recovery") }
            )
        }

        composable("time_management") {
            val timeManagementViewModel: com.example.ui.settings.TimeManagementViewModel = viewModel()
            com.example.ui.settings.TimeManagementScreen(
                viewModel = timeManagementViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("account_recovery") {
            val recoveryViewModel: com.example.ui.settings.AccountRecoveryViewModel = viewModel()
            com.example.ui.settings.AccountRecoveryScreen(
                viewModel = recoveryViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
}
