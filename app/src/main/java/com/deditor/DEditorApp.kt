package com.deditor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.FilterVintage
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.ViewTimeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DarkColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

private val AppColors = darkColorScheme(
    background = Color(0xFF020617),
    surface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFF111827),
    primary = Color(0xFF8B5CF6),
    secondary = Color(0xFF06B6D4),
    tertiary = Color(0xFFF97316),
    onBackground = Color.White,
    onSurface = Color.White,
    onPrimary = Color.White
)

enum class Screen {
    Home,
    Editor,
    Photo,
    Video,
    Camera,
    Tools,
    Settings
}

data class EditorClip(
    val id: String,
    val title: String,
    val type: String,
    val color: Color
)

data class ToolItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
fun DEditorApp() {
    var screen by remember { mutableStateOf(Screen.Home) }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val videoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedVideoUri = uri
            screen = Screen.Editor
        }
    }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedPhotoUri = uri
            screen = Screen.Photo
        }
    }

    MaterialTheme(colorScheme = AppColors) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                BottomNav(screen) { screen = it }
            }
        ) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                color = MaterialTheme.colorScheme.background
            ) {
                AnimatedContent(targetState = screen, label = "screen") { current ->
                    when (current) {
                        Screen.Home -> HomeScreen(
                            onNewProject = { screen = Screen.Editor },
                            onPickVideo = {
                                videoPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            },
                            onPickPhoto = {
                                photoPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            onTools = { screen = Screen.Tools }
                        )

                        Screen.Editor -> EditorScreen(
                            selectedVideoUri = selectedVideoUri,
                            onPickVideo = {
                                videoPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            }
                        )

                        Screen.Photo -> PhotoScreen(
                            selectedPhotoUri = selectedPhotoUri,
                            onPickPhoto = {
                                photoPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        )

                        Screen.Video -> VideoScreen(
                            selectedVideoUri = selectedVideoUri,
                            onPickVideo = {
                                videoPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            }
                        )

                        Screen.Camera -> CameraScreen()
                        Screen.Tools -> ToolsScreen()
                        Screen.Settings -> SettingsScreen()
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(
    onNewProject: () -> Unit,
    onPickVideo: () -> Unit,
    onPickPhoto: () -> Unit,
    onTools: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text("D Editor", style = MaterialTheme.typography.displaySmall)
        Text("Offline creator studio", color = Color(0xFFCBD5E1))

        HeroCard(
            title = "New Project",
            subtitle = "Timeline editor, video preview, tracks, overlays and export pipeline.",
            icon = Icons.Rounded.Add,
            onClick = onNewProject
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickCard(
                title = "Video",
                subtitle = "Start from clip",
                icon = Icons.Rounded.Movie,
                modifier = Modifier.weight(1f),
                onClick = onPickVideo
            )
            QuickCard(
                title = "Photo",
                subtitle = "Edit image",
                icon = Icons.Rounded.Image,
                modifier = Modifier.weight(1f),
                onClick = onPickPhoto
            )
        }

        SectionTitle("Creator packs")
        FeatureCloud(
            listOf(
                "Trim", "Split", "Filters", "Effects", "Text", "Stickers",
                "Audio", "Canvas", "Transitions", "Export", "Templates", "Camera"
            )
        )

        HeroCard(
            title = "Toolbox",
            subtitle = "Compress, extract audio, convert, collage, meme maker and batch tools.",
            icon = Icons.Rounded.AutoAwesome,
            onClick = onTools
        )
    }
}

@Composable
private fun EditorScreen(
    selectedVideoUri: Uri?,
    onPickVideo: () -> Unit
) {
    val clips = remember {
        mutableStateListOf(
            EditorClip("1", "Intro", "Video", Color(0xFF8B5CF6)),
            EditorClip("2", "Text", "Text", Color(0xFF06B6D4)),
            EditorClip("3", "Music", "Audio", Color(0xFFF97316)),
            EditorClip("4", "FX", "Effect", Color(0xFF22C55E))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EditorTopBar(onPickVideo = onPickVideo)

        PreviewPanel(selectedVideoUri)

        TimelinePanel(clips)

        ToolDock()
    }
}

@Composable
private fun EditorTopBar(onPickVideo: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0F172A))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Project 01", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)

        SmallPill("Import", Icons.Rounded.FileUpload, onClick = onPickVideo)
        SmallPill("Export", Icons.Rounded.AutoAwesome, onClick = {})
    }
}

@Composable
private fun PreviewPanel(videoUri: Uri?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.Black)
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(28.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (videoUri == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(64.dp))
                Text("Import a video to preview")
            }
        } else {
            Media3Player(videoUri)
        }
    }
}

@Composable
private fun Media3Player(uri: Uri) {
    val context = LocalContext.current
    val player = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(uri) {
        onDispose {
            player.release()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            PlayerView(it).apply {
                this.player = player
                useController = true
            }
        }
    )
}

@Composable
private fun TimelinePanel(clips: List<EditorClip>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(178.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF0F172A))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.ViewTimeline, contentDescription = null)
            Text(" Timeline", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(clips) { clip ->
                ClipBlock(clip)
            }
        }

        Spacer(Modifier.height(12.dp))

        Canvas(modifier = Modifier.fillMaxWidth().height(30.dp)) {
            val center = size.width / 2f
            drawLine(
                color = Color.White,
                start = androidx.compose.ui.geometry.Offset(center, 0f),
                end = androidx.compose.ui.geometry.Offset(center, size.height),
                strokeWidth = 4f
            )
        }
    }
}

@Composable
private fun ClipBlock(clip: EditorClip) {
    Column(
        modifier = Modifier
            .size(width = 116.dp, height = 76.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(clip.color.copy(alpha = 0.9f))
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(clip.title, style = MaterialTheme.typography.titleSmall)
        Text(clip.type, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ToolDock() {
    val tools = listOf(
        ToolItem("Edit", "Trim split crop", Icons.Rounded.ContentCut),
        ToolItem("Audio", "Music voice effects", Icons.Rounded.MusicNote),
        ToolItem("Text", "Titles captions", Icons.Rounded.TextFields),
        ToolItem("Stickers", "Images emojis", Icons.Rounded.AutoAwesome),
        ToolItem("Effects", "Glitch blur glow", Icons.Rounded.FilterVintage),
        ToolItem("Adjust", "Color light detail", Icons.Rounded.Tune),
        ToolItem("Canvas", "Ratio background", Icons.Rounded.Movie),
        ToolItem("Export", "Render MP4", Icons.Rounded.FileUpload)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF0F172A))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(tools) { tool ->
            ToolButton(tool)
        }
    }
}

@Composable
private fun ToolButton(tool: ToolItem) {
    Column(
        modifier = Modifier
            .size(width = 92.dp, height = 82.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF1E293B))
            .clickable {}
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(tool.icon, contentDescription = tool.title)
        Spacer(Modifier.height(6.dp))
        Text(tool.title, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun PhotoScreen(selectedPhotoUri: Uri?, onPickPhoto: () -> Unit) {
    StudioScreenTemplate(
        title = "Photo Studio",
        subtitle = "Filters, adjust, crop, draw, stickers, text and export.",
        action = "Open Photo",
        onAction = onPickPhoto,
        tools = listOf(
            "Crop", "Rotate", "Flip", "Text", "Brush", "Sticker",
            "Brightness", "Contrast", "Saturation", "Blur", "Sharpen", "Export"
        )
    )
}

@Composable
private fun VideoScreen(selectedVideoUri: Uri?, onPickVideo: () -> Unit) {
    StudioScreenTemplate(
        title = "Video Studio",
        subtitle = "Preview, trim, split, audio, overlays, speed and export.",
        action = "Open Video",
        onAction = onPickVideo,
        tools = listOf(
            "Trim", "Split", "Speed", "Mute", "Music", "Text",
            "Overlay", "Filter", "Transition", "Canvas", "Compress", "Export"
        )
    )
}

@Composable
private fun CameraScreen() {
    StudioScreenTemplate(
        title = "Camera",
        subtitle = "CameraX capture screen will be wired in Part 2.",
        action = "Open Camera",
        onAction = {},
        tools = listOf("Photo", "Video", "Timer", "Flash", "HDR", "Grid")
    )
}

@Composable
private fun ToolsScreen() {
    StudioScreenTemplate(
        title = "Tools",
        subtitle = "Offline utility hub.",
        action = "Create",
        onAction = {},
        tools = listOf(
            "Compress", "Convert", "Extract Audio", "GIF Maker",
            "Collage", "Meme", "Thumbnail", "Watermark",
            "Batch Resize", "Slideshow", "Voiceover", "Templates"
        )
    )
}

@Composable
private fun SettingsScreen() {
    StudioScreenTemplate(
        title = "Settings",
        subtitle = "Quality, export, cache, permissions and offline assets.",
        action = "Manage",
        onAction = {},
        tools = listOf(
            "Export Quality", "Storage", "Permissions", "Cache",
            "Offline Assets", "Templates", "About", "Privacy"
        )
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StudioScreenTemplate(
    title: String,
    subtitle: String,
    action: String,
    onAction: () -> Unit,
    tools: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(title, style = MaterialTheme.typography.displaySmall)
        Text(subtitle, color = Color(0xFFCBD5E1))

        Button(
            onClick = onAction,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
        ) {
            Text(action)
        }

        SectionTitle("Features")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            tools.forEach {
                FilterChip(
                    selected = false,
                    onClick = {},
                    label = { Text(it) }
                )
            }
        }
    }
}

@Composable
private fun HeroCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF7C3AED), Color(0xFF06B6D4))
                    )
                )
                .padding(22.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = title, modifier = Modifier.size(42.dp))
                Spacer(Modifier.size(18.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.headlineSmall)
                    Text(subtitle, color = Color.White.copy(alpha = 0.84f))
                }
            }
        }
    }
}

@Composable
private fun QuickCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(132.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(34.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(subtitle, color = Color(0xFFCBD5E1))
            }
        }
    }
}

@Composable
private fun SmallPill(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(start = 8.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFF1E293B))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = text, modifier = Modifier.size(18.dp))
        Spacer(Modifier.size(6.dp))
        Text(text)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FeatureCloud(items: List<String>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEach {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            ) {
                Text(it)
            }
        }
    }
}

@Composable
private fun BottomNav(
    current: Screen,
    onChange: (Screen) -> Unit
) {
    NavigationBar(
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = Color(0xFF020617)
    ) {
        val items = listOf(
            Screen.Home to Icons.Rounded.Home,
            Screen.Editor to Icons.Rounded.ViewTimeline,
            Screen.Photo to Icons.Rounded.Image,
            Screen.Video to Icons.Rounded.Movie,
            Screen.Tools to Icons.Rounded.AutoAwesome,
            Screen.Settings to Icons.Rounded.Settings
        )

        items.forEach { (screen, icon) ->
            NavigationBarItem(
                selected = current == screen,
                onClick = { onChange(screen) },
                icon = { Icon(icon, contentDescription = screen.name) },
                label = { Text(screen.name) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = Color(0xFF7C3AED)
                )
            )
        }
    }
}
