package com.example.ui.story

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.models.StorySticker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryEditorScreen(
    viewModel: StoryEditorViewModel,
    onNavigateBack: () -> Unit,
    onPublish: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
        // Mock Media Preview Background
        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))
        
        // Render Stickers
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 100.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                state.selectedStickers.forEach { sticker ->
                    StickerPreview(sticker)
                }
            }
        }

        // Top Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IconButton(onClick = { viewModel.toggleStickerPicker() }) {
                    Icon(Icons.Default.EmojiEmotions, contentDescription = "Stickers", tint = Color.White)
                }
                IconButton(onClick = { viewModel.openMusicPicker() }) {
                    Icon(Icons.Default.MusicNote, contentDescription = "Music", tint = Color.White)
                }
                IconButton(onClick = { /* Add Text */ }) {
                    Icon(Icons.Default.TextFields, contentDescription = "Text", tint = Color.White)
                }
            }
        }

        // Bottom Controls
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onPublish,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Your Story")
            }
            Button(
                onClick = onPublish,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.padding(end = 4.dp).size(16.dp))
                Text("Close Friends")
            }
            Button(
                onClick = { /* Save/Send */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black, modifier = Modifier.padding(end = 4.dp).size(16.dp))
            }
        }

        // Sticker Picker Bottom Sheet
        if (state.showStickerPicker) {
            ModalBottomSheet(onDismissRequest = { viewModel.toggleStickerPicker() }) {
                StickerPickerSheet(
                    onSelectType = { viewModel.openStickerConfig(it) },
                    onSelectMusic = { viewModel.openMusicPicker() }
                )
            }
        }

        // Music Picker Bottom Sheet
        if (state.showMusicPicker) {
            ModalBottomSheet(onDismissRequest = { viewModel.closeMusicPicker() }) {
                MusicPickerSheet(
                    state = state,
                    onSearch = { viewModel.updateSearchQuery(it) },
                    onSelectTrack = { track -> 
                        viewModel.addSticker(StorySticker.Music(track.id, track.title, track.artist, track.coverUrl))
                    }
                )
            }
        }
        
        // Sticker Config Dialog
        if (state.activeStickerConfig != null) {
            StickerConfigDialog(
                type = state.activeStickerConfig!!,
                onDismiss = { viewModel.closeStickerConfig() },
                onAdd = { viewModel.addSticker(it) }
            )
        }
    }
}

@Composable
fun StickerPreview(sticker: StorySticker) {
    Box(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        when (sticker) {
            is StorySticker.Poll -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(sticker.question, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {}) { Text(sticker.optionA) }
                        Button(onClick = {}) { Text(sticker.optionB) }
                    }
                }
            }
            is StorySticker.Quiz -> {
                Column {
                    Text(sticker.question, fontWeight = FontWeight.Bold)
                    sticker.options.forEachIndexed { index, opt ->
                        Text("${index + 1}. $opt", modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
            is StorySticker.Question -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(sticker.prompt, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = "", onValueChange = {}, placeholder = { Text("Type something...") }, enabled = false, modifier = Modifier.padding(top = 8.dp))
                }
            }
            is StorySticker.Countdown -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(sticker.title, fontWeight = FontWeight.Bold)
                    Text("23:59:59", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
            is StorySticker.Slider -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(sticker.question, fontWeight = FontWeight.Bold)
                    Slider(value = 0.5f, onValueChange = {}, enabled = false)
                    Text(sticker.emoji, style = MaterialTheme.typography.headlineMedium)
                }
            }
            is StorySticker.Location -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(sticker.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            is StorySticker.Hashtag -> {
                Text("#${sticker.tag}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
            }
            is StorySticker.Mention -> {
                Text("@${sticker.username}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
            }
            is StorySticker.Music -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.Black)
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(sticker.title, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(sticker.artist, style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                    }
                }
            }
            is StorySticker.Link -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor(sticker.colorHex)), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Link, contentDescription = null, tint = Color.Black)
                    Text(sticker.text, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}

@Composable
fun StickerPickerSheet(
    onSelectType: (StickerConfigType) -> Unit,
    onSelectMusic: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Stickers", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
        
        val items = listOf(
            "Location" to StickerConfigType.LOCATION,
            "Mention" to StickerConfigType.MENTION,
            "Hashtag" to StickerConfigType.HASHTAG,
            "Music" to null, // Special case
            "Link" to StickerConfigType.LINK,
            "Poll" to StickerConfigType.POLL,
            "Questions" to StickerConfigType.QUESTION,
            "Quiz" to StickerConfigType.QUIZ,
            "Countdown" to StickerConfigType.COUNTDOWN,
            "Slider" to StickerConfigType.SLIDER
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { (name, type) ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            if (name == "Music") onSelectMusic()
                            else type?.let { onSelectType(it) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(name, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MusicPickerSheet(
    state: StoryEditorState,
    onSearch: (String) -> Unit,
    onSelectTrack: (com.example.models.MusicTrack) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Music", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
        
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearch,
            placeholder = { Text("Search music...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Trending", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
        
        LazyColumn {
            items(state.trendingMusic) { track ->
                ListItem(
                    headlineContent = { Text(track.title) },
                    supportingContent = { Text(track.artist) },
                    leadingContent = {
                        Box(modifier = Modifier.size(48.dp).background(Color.Gray, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White)
                        }
                    },
                    modifier = Modifier.clickable { onSelectTrack(track) }
                )
            }
        }
    }
}

@Composable
fun StickerConfigDialog(
    type: StickerConfigType,
    onDismiss: () -> Unit,
    onAdd: (StorySticker) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add ${type.name.lowercase().capitalize()} Sticker") },
        text = {
            when (type) {
                StickerConfigType.POLL -> {
                    var q by remember { mutableStateOf("Ask a question...") }
                    var a by remember { mutableStateOf("Yes") }
                    var b by remember { mutableStateOf("No") }
                    Column {
                        OutlinedTextField(value = q, onValueChange = { q = it }, label = { Text("Question") })
                        OutlinedTextField(value = a, onValueChange = { a = it }, label = { Text("Option 1") })
                        OutlinedTextField(value = b, onValueChange = { b = it }, label = { Text("Option 2") })
                        Button(onClick = { onAdd(StorySticker.Poll(q, a, b)) }) { Text("Add") }
                    }
                }
                StickerConfigType.QUESTION -> {
                    var q by remember { mutableStateOf("Ask me anything") }
                    Column {
                        OutlinedTextField(value = q, onValueChange = { q = it }, label = { Text("Prompt") })
                        Button(onClick = { onAdd(StorySticker.Question(q)) }) { Text("Add") }
                    }
                }
                StickerConfigType.COUNTDOWN -> {
                    var t by remember { mutableStateOf("My Event") }
                    Column {
                        OutlinedTextField(value = t, onValueChange = { t = it }, label = { Text("Title") })
                        Button(onClick = { onAdd(StorySticker.Countdown(t, System.currentTimeMillis() + 86400000)) }) { Text("Add") }
                    }
                }
                StickerConfigType.SLIDER -> {
                    var q by remember { mutableStateOf("How much do you love this?") }
                    var e by remember { mutableStateOf("😍") }
                    Column {
                        OutlinedTextField(value = q, onValueChange = { q = it }, label = { Text("Question") })
                        OutlinedTextField(value = e, onValueChange = { e = it }, label = { Text("Emoji") })
                        Button(onClick = { onAdd(StorySticker.Slider(q, e)) }) { Text("Add") }
                    }
                }
                StickerConfigType.LOCATION -> {
                    var l by remember { mutableStateOf("New York") }
                    Column {
                        OutlinedTextField(value = l, onValueChange = { l = it }, label = { Text("Location") })
                        Button(onClick = { onAdd(StorySticker.Location(l)) }) { Text("Add") }
                    }
                }
                StickerConfigType.HASHTAG -> {
                    var h by remember { mutableStateOf("vibe") }
                    Column {
                        OutlinedTextField(value = h, onValueChange = { h = it }, label = { Text("Hashtag") })
                        Button(onClick = { onAdd(StorySticker.Hashtag(h)) }) { Text("Add") }
                    }
                }
                StickerConfigType.MENTION -> {
                    var m by remember { mutableStateOf("username") }
                    Column {
                        OutlinedTextField(value = m, onValueChange = { m = it }, label = { Text("Username") })
                        Button(onClick = { onAdd(StorySticker.Mention(m)) }) { Text("Add") }
                    }
                }
                StickerConfigType.QUIZ -> {
                    var q by remember { mutableStateOf("Guess my favorite color") }
                    var opt1 by remember { mutableStateOf("Red") }
                    var opt2 by remember { mutableStateOf("Blue") }
                    Column {
                        OutlinedTextField(value = q, onValueChange = { q = it }, label = { Text("Question") })
                        OutlinedTextField(value = opt1, onValueChange = { opt1 = it }, label = { Text("Option 1") })
                        OutlinedTextField(value = opt2, onValueChange = { opt2 = it }, label = { Text("Option 2 (Correct)") })
                        Button(onClick = { onAdd(StorySticker.Quiz(q, listOf(opt1, opt2), 1)) }) { Text("Add") }
                    }
                }
                StickerConfigType.LINK -> {
                    var url by remember { mutableStateOf("https://") }
                    var text by remember { mutableStateOf("Visit Link") }
                    var color by remember { mutableStateOf("#FFFFFF") }
                    Column {
                        OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("URL") })
                        OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Sticker Text") })
                        Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("#FFFFFF", "#FF0000", "#00FF00", "#0000FF").forEach { c ->
                                Box(modifier = Modifier.size(32.dp).background(Color(android.graphics.Color.parseColor(c)), CircleShape).border(2.dp, if(color == c) Color.Black else Color.Transparent, CircleShape).clickable { color = c })
                            }
                        }
                        Button(onClick = { onAdd(StorySticker.Link(url, text, color)) }, modifier = Modifier.padding(top = 8.dp)) { Text("Add") }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
