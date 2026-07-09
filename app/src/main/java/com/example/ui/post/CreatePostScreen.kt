package com.example.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.models.MediaType
import java.util.UUID
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: CreatePostViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.addMedia(uri.toString(), MediaType.IMAGE)
            }
        }
    )

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.addMedia(uri.toString(), MediaType.VIDEO)
            }
        }
    )

    LaunchedEffect(state.uploadStatus) {
        if (state.uploadStatus == UploadStatus.SUCCESS) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Post", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.submitPost() },
                        enabled = state.mediaItems.isNotEmpty() && state.uploadStatus == UploadStatus.IDLE
                    ) {
                        Text("Share", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            if (state.uploadStatus != UploadStatus.IDLE) {
                LinearProgressIndicator(
                    progress = { state.uploadProgress },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = state.processingMessage,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(state.mediaItems) { index, item ->
                    Column(
                        modifier = Modifier.width(200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            if (item.uri.startsWith("content://") || item.uri.startsWith("http")) {
                                AsyncImage(
                                    model = item.uri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (item.type == MediaType.VIDEO) {
                                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Videocam, contentDescription = "Video", tint = Color.White, modifier = Modifier.size(48.dp))
                                    }
                                }
                            } else {
                                Icon(
                                    imageVector = if (item.type == MediaType.VIDEO) Icons.Default.Videocam else Icons.Default.Image,
                                    contentDescription = null,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            
                            IconButton(
                                onClick = { viewModel.removeMedia(index) },
                                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        
                        OutlinedTextField(
                            value = item.slideCaption,
                            onValueChange = { viewModel.updateSlideCaption(index, it) },
                            placeholder = { Text("Slide caption...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                if (state.mediaItems.size < 10) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    .clickable { 
                                        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Image, contentDescription = "Add Image")
                                    Text("Add Image", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    .clickable { 
                                        videoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Videocam, contentDescription = "Add Video")
                                    Text("Add Video", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
            
            Text("${state.mediaItems.size}/10 media items added", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Aspect Ratio", modifier = Modifier.align(Alignment.CenterVertically))
                listOf("1:1", "4:5", "16:9").forEach { ratio ->
                    FilterChip(
                        selected = state.aspectRatio == ratio,
                        onClick = { viewModel.setAspectRatio(ratio) },
                        label = { Text(ratio) }
                    )
                }
            }

            if (state.mediaItems.any { it.type == MediaType.VIDEO }) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Auto-Generate Captions")
                    Switch(
                        checked = state.autoGeneratedCaptions,
                        onCheckedChange = { viewModel.toggleAutoGeneratedCaptions(it) }
                    )
                }

                val hasVideo = state.mediaItems.any { it.type == MediaType.VIDEO }
                if (!hasVideo) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = state.altText,
                        onValueChange = { viewModel.updateAltText(it) },
                        label = { Text("Write Alt Text (Accessibility)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }

            HorizontalDivider()
            
            Text("Cross-Post to Other Apps", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
            state.crossPostPlatforms.forEach { platform ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(platform.name)
                    Switch(
                        checked = platform.enabled,
                        onCheckedChange = { viewModel.toggleCrossPost(platform.id) }
                    )
                }
            }

            HorizontalDivider()
            
            val currentLoc = state.selectedLocation
            ListItem(
                headlineContent = { Text(currentLoc?.name ?: "Add Location") },
                supportingContent = if (currentLoc != null) { { Text(currentLoc.address) } } else null,
                leadingContent = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                trailingContent = { 
                    if (currentLoc != null) {
                        IconButton(onClick = { viewModel.removeLocation() }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove location")
                        }
                    } 
                },
                modifier = Modifier.clickable { viewModel.toggleLocationSearch() }
            )
            
            if (state.showLocationSearch) {
                OutlinedTextField(
                    value = state.locationQuery,
                    onValueChange = { viewModel.updateLocationQuery(it) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    placeholder = { Text("Search places...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )
                
                if (state.locationSuggestions.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).padding(horizontal = 16.dp)
                    ) {
                        items(state.locationSuggestions) { loc ->
                            ListItem(
                                headlineContent = { Text(loc.name) },
                                supportingContent = { Text(loc.address) },
                                modifier = Modifier.clickable { viewModel.selectLocation(loc) }
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            // Advanced settings (Co-author, Partnership, Products)
            ListItem(
                headlineContent = { Text("Collaborator (Co-author)") },
                supportingContent = { Text(if (state.coAuthor != null) state.coAuthor!!.username else "Invite a collaborator") },
                leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.clickable { 
                    viewModel.setCoAuthor(com.example.models.UserShort("2", "mkbhd", null))
                }
            )

            ListItem(
                headlineContent = { Text("Paid Partnership") },
                supportingContent = { Text("Add paid partnership label") },
                leadingContent = { Icon(Icons.Default.Storefront, contentDescription = null) },
                trailingContent = { Switch(checked = state.isPaidPartnership, onCheckedChange = { viewModel.togglePaidPartnership() }) },
                modifier = Modifier.clickable { viewModel.togglePaidPartnership() }
            )

            if (state.isPaidPartnership) {
                ListItem(
                    headlineContent = { Text("Brand Partner") },
                    supportingContent = { Text(if (state.partnerBrand != null) state.partnerBrand!!.username else "Tag brand partner") },
                    modifier = Modifier.padding(start = 40.dp).clickable {
                        viewModel.setPartnerBrand(com.example.models.UserShort("5", "nike", null))
                    }
                )
            }

            ListItem(
                headlineContent = { Text("Tag Products") },
                supportingContent = { Text("${state.taggedProducts.size}/5 products tagged") },
                leadingContent = { Icon(Icons.Default.LocalMall, contentDescription = null) },
                modifier = Modifier.clickable { 
                    if (state.taggedProducts.size < 5) {
                        viewModel.addTaggedProduct(com.example.models.Product("p1", "Cool Sneakers", "Awesome shoes", 120.0, imageUrl = "https://picsum.photos/200", merchant = com.example.models.UserShort("5", "nike", null)))
                    }
                }
            )

            HorizontalDivider()

            if (state.activeSuggestionMode == SuggestionMode.MENTION && state.mentionSuggestions.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.mentionSuggestions) { user ->
                        AssistChip(
                            onClick = { viewModel.applySuggestion(user.username) },
                            label = { Text(user.username) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            } else if (state.activeSuggestionMode == SuggestionMode.HASHTAG && state.hashtagSuggestions.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.hashtagSuggestions) { hashtag ->
                        AssistChip(
                            onClick = { viewModel.applySuggestion(hashtag.name) },
                            label = { Text("#${hashtag.name}") },
                            leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.globalCaption,
                onValueChange = { viewModel.updateGlobalCaption(it) },
                placeholder = { Text("Write a global caption...") },
                supportingText = { Text("${state.globalCaption.length}/2200", modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.End) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )
        }
    }
}
