package com.example.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.models.LinkItem
import com.example.models.ProfileCategory
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()
    val usernameAvailability by viewModel.usernameAvailability.collectAsState()

    var username by remember { mutableStateOf(profile.username) }
    var displayName by remember { mutableStateOf(profile.displayName) }
    var bio by remember { mutableStateOf(profile.bio) }
    var externalLink by remember { mutableStateOf(profile.externalLink) }
    var category by remember { mutableStateOf(profile.category) }
    var accentColor by remember { mutableStateOf(profile.accentColor) }
    var bioLinks by remember { mutableStateOf(profile.bioLinks) }

    LaunchedEffect(username) {
        viewModel.checkUsername(username)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        viewModel.updateProfile(
                            profile.copy(
                                username = username,
                                displayName = displayName,
                                bio = bio,
                                externalLink = externalLink,
                                category = category,
                                accentColor = accentColor,
                                bioLinks = bioLinks
                            )
                        )
                        onNavigateBack()
                    }) {
                        Text("Save")
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    UserAvatar(
                        displayName = displayName.ifBlank { "User" },
                        avatarUrl = profile.avatarUrl,
                        profileFrame = profile.profileFrame,
                        size = 100.dp
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { /* Trigger image picker */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Avatar", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                supportingText = {
                    when (usernameAvailability) {
                        true -> Text("Username is available", color = Color(0xFF4CAF50))
                        false -> Text("Username is unavailable or invalid", color = MaterialTheme.colorScheme.error)
                        null -> Text("Alphanumeric and underscores only")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { if (it.length <= 150) bio = it },
                label = { Text("Bio") },
                supportingText = { Text("${bio.length}/150 (@mentions and #hashtags supported)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            OutlinedTextField(
                value = externalLink,
                onValueChange = { externalLink = it },
                label = { Text("External Link") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProfileCategory.values().forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = category == cat,
                                borderColor = if(category == cat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Theme Accent Color", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    val colors = listOf(0xFF6750A4, 0xFFE91E63, 0xFF2196F3, 0xFF4CAF50, 0xFFFF9800)
                    colors.forEach { colorVal ->
                        val color = Color(colorVal)
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    3.dp,
                                    if (accentColor == colorVal.toInt()) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    CircleShape
                                )
                                .clickable { accentColor = colorVal.toInt() },
                            contentAlignment = Alignment.Center
                        ) {
                            if (accentColor == colorVal.toInt()) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Link in Bio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { 
                        bioLinks = bioLinks + LinkItem(id = UUID.randomUUID().toString(), title = "New Link", url = "")
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Link")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Link")
                    }
                }

                if (bioLinks.isEmpty()) {
                    Text("No links added yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    bioLinks.forEachIndexed { index, link ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = link.title,
                                        onValueChange = { newTitle ->
                                            val newList = bioLinks.toMutableList()
                                            newList[index] = link.copy(title = newTitle)
                                            bioLinks = newList
                                        },
                                        label = { Text("Title") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    IconButton(onClick = {
                                        val newList = bioLinks.toMutableList()
                                        newList.removeAt(index)
                                        bioLinks = newList
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Link", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                                OutlinedTextField(
                                    value = link.url,
                                    onValueChange = { newUrl ->
                                        val newList = bioLinks.toMutableList()
                                        newList[index] = link.copy(url = newUrl)
                                        bioLinks = newList
                                    },
                                    label = { Text("URL") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
