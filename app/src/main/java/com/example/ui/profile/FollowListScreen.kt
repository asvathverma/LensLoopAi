package com.example.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.models.FollowListType
import com.example.models.FollowStatus
import com.example.models.FollowUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowListScreen(
    viewModel: FollowViewModel,
    username: String,
    initialType: FollowListType,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(initialType) }

    LaunchedEffect(selectedTab) {
        viewModel.loadList(selectedTab)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(username, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            TabRow(
                selectedTabIndex = if (selectedTab == FollowListType.FOLLOWERS) 0 else 1,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Tab(
                    selected = selectedTab == FollowListType.FOLLOWERS,
                    onClick = { selectedTab = FollowListType.FOLLOWERS },
                    text = { Text("Followers") }
                )
                Tab(
                    selected = selectedTab == FollowListType.FOLLOWING,
                    onClick = { selectedTab = FollowListType.FOLLOWING },
                    text = { Text("Following") }
                )
            }

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.searchUsers(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchUsers("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(percent = 50),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.users.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No users found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.users, key = { it.id }) { user ->
                        FollowUserItem(
                            user = user,
                            listType = selectedTab,
                            onToggleFollow = { viewModel.toggleFollow(user.id) },
                            onRemoveFollower = { viewModel.removeFollower(user.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FollowUserItem(
    user: FollowUser,
    listType: FollowListType,
    onToggleFollow: () -> Unit,
    onRemoveFollower: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(
            displayName = user.displayName,
            avatarUrl = user.avatarUrl,
            profileFrame = null,
            size = 56.dp
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = user.displayName, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            Text(text = "@${user.username}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (listType == FollowListType.FOLLOWERS) {
            Button(
                onClick = onRemoveFollower,
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text("Remove")
            }
        } else {
            val buttonText = when (user.followStatus) {
                FollowStatus.FOLLOWING -> "Following"
                FollowStatus.REQUESTED -> "Requested"
                else -> "Follow"
            }
            val buttonColor = if (user.followStatus != null) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
            val contentColor = if (user.followStatus != null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary

            Button(
                onClick = onToggleFollow,
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = contentColor
                ),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(buttonText)
            }
        }

        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Mute") },
                    onClick = { expanded = false }
                )
                DropdownMenuItem(
                    text = { Text("Block") },
                    onClick = { expanded = false }
                )
            }
        }
    }
}
