package com.pixielook.facefocus.ui.tutorial

import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.pixielook.facefocus.models.CameraState
import com.pixielook.facefocus.models.TrackingResult
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.media3.datasource.RawResourceDataSource
import com.pixielook.facefocus.R
import com.pixielook.facefocus.models.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.TransformOrigin
import kotlin.math.roundToInt

@Composable
fun TutorialScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit,
    viewModel: TutorialViewModel = viewModel(),
) {
    val trackingResult by viewModel.trackingResult.collectAsState()
    val cameraState by viewModel.cameraState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val videoState by viewModel.videoState.collectAsState()
    
    var showSettings by remember { mutableStateOf(value = false) }

    // Floating Video Frame State
    val videoScale = remember { Animatable(1f) }
    val videoOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val coroutineScope = rememberCoroutineScope()

    val currentSubtitle = remember(videoState.currentPosition) {
        tutorialSubtitles.lastOrNull { it.first <= videoState.currentPosition }?.second ?: ""
    }

    if (showSettings) {
        SettingsDialog(
            settings = settings,
            onSettingsChanged = viewModel::updateSettings,
            onDismiss = { showSettings = false }
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val fullWidthPx = constraints.maxWidth.toFloat()
        val fullHeightPx = constraints.maxHeight.toFloat()
        val fullWidthDp = maxWidth
        val fullHeightDp = maxHeight

        val isDocked = videoOffset.value == Offset.Zero && videoScale.value == 1f

        // 1. Camera Tracking (Adapts between Split and Full Screen)
        val cameraWidth = if (isDocked) fullWidthDp / 2 else fullWidthDp
        val cameraX = if (isDocked) fullWidthDp / 2 else 0.dp

        Box(
            modifier = Modifier
                .offset(x = cameraX)
                .size(cameraWidth, fullHeightDp)
        ) {
            CameraTrackingPanel(
                viewModel = viewModel,
                trackingResult = trackingResult,
                cameraState = cameraState,
                settings = settings,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 2. Interactive Floating Video Player Overlay
        val videoBaseWidthDp = fullWidthDp / 2
        val videoBaseHeightDp = fullHeightDp
        val videoBaseWidthPx = fullWidthPx / 2
        val videoBaseHeightPx = fullHeightPx

        Box(
            modifier = Modifier
                .offset { IntOffset(videoOffset.value.x.roundToInt(), videoOffset.value.y.roundToInt()) }
                .graphicsLayer {
                    scaleX = videoScale.value
                    scaleY = videoScale.value
                    transformOrigin = TransformOrigin(0f, 0f)
                }
                .size(videoBaseWidthDp, videoBaseHeightDp)
                .background(Color.Black.copy(alpha = 0.2f))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoomFactor, _ ->
                        coroutineScope.launch {
                            val nextScale = (videoScale.value * zoomFactor).coerceIn(0.2f, 2f)
                            videoScale.snapTo(nextScale)

                            val nextOffset = videoOffset.value + pan
                            
                            val currentWidth = videoBaseWidthPx * nextScale
                            val currentHeight = videoBaseHeightPx * nextScale
                            
                            // Prevent dragging completely off-screen
                            val clampedX = nextOffset.x.coerceIn(-currentWidth * 0.8f, fullWidthPx - currentWidth * 0.2f)
                            val clampedY = nextOffset.y.coerceIn(-currentHeight * 0.8f, fullHeightPx - currentHeight * 0.2f)
                            
                            videoOffset.snapTo(Offset(clampedX, clampedY))
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = {
                        coroutineScope.launch {
                            launch { videoOffset.animateTo(Offset.Zero) }
                            launch { videoScale.animateTo(1f) }
                        }
                    })
                }
        ) {
            TutorialVideoPlayer(
                resId = R.raw.intro_with_sound,
                onProgress = { pos, dur ->
                    viewModel.updateVideoState(videoState.copy(currentPosition = pos, duration = dur))
                }
            )
            SubtitleOverlay(text = currentSubtitle)
            
            if (cameraState == CameraState.ERROR) {
                Box(Modifier.fillMaxSize().background(Color.Red.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Text("Camera Error", color = Color.White)
                }
            }

            // Reset Button inside the player (visible when player is moved/scaled)
            if (!isDocked) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            launch { videoOffset.animateTo(Offset.Zero) }
                            launch { videoScale.animateTo(1f) }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Cyan.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset Player", tint = Color.Black)
                }
            }
        }

        // 3. UI Layer (Buttons and Tracking Stats)
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TrackingDetail("Confidence", "${(trackingResult.face?.confidence?.times(100))?.toInt() ?: 0}%")
            TrackingDetail("FPS", trackingResult.metrics.fps.toString())
        }

        // Floating Back Button (Top Left)
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        // Floating Settings Button (Top Right)
        IconButton(
            onClick = { showSettings = true },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
        }

        // Floating Finish Button (Bottom Right)
        Button(
            onClick = onFinish,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Finish Tutorial", color = Color.White)
        }
    }
}

@Composable
fun SubtitleOverlay(text: String) {
    if (text.isEmpty()) return
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 32.dp, start = 32.dp, end = 32.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            color = Color.Black.copy(alpha = 0.6f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun SettingsDialog(
    settings: TutorialSettings,
    onSettingsChanged: (TutorialSettings) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings", color = Color.White) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("AI Tracking", color = Color.Cyan, style = MaterialTheme.typography.labelLarge)
                SettingsToggle("Enable Face Tracking", "MediaPipe face detection", settings.isFaceTrackingEnabled) {
                    onSettingsChanged(settings.copy(isFaceTrackingEnabled = it))
                }
                SettingsToggle("Show Tracking Overlay", "Display tracking rectangles", settings.showTrackingOverlay) {
                    onSettingsChanged(settings.copy(showTrackingOverlay = it))
                }
                SettingsToggle("Show Face Landmarks", "Facial landmark points", settings.showFaceLandmarks) {
                    onSettingsChanged(settings.copy(showFaceLandmarks = it))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Camera", color = Color.Green, style = MaterialTheme.typography.labelLarge)
                SettingsToggle("Mirror Camera", "Flip horizontally", settings.isCameraMirrored) {
                    onSettingsChanged(settings.copy(isCameraMirrored = it))
                }
                
                Button(
                    onClick = { 
                        val nextLens = (settings.lensFacing + 1) % 3 // Match ViewModel's 0,1,2
                        onSettingsChanged(settings.copy(lensFacing = nextLens))
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    val cameraLabel = when(settings.lensFacing) {
                        0 -> "Front Camera"
                        1 -> "Back Camera"
                        else -> "External/USB Camera"
                    }
                    Text("Switch Camera ($cameraLabel)")
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        },
        containerColor = Color(0xFF1E1E1E),
        textContentColor = Color.White,
        titleContentColor = Color.White
    )
}

@Composable
fun SettingsToggle(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@OptIn(UnstableApi::class)
@Composable
fun TutorialVideoPlayer(
    resId: Int,
    onProgress: (Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val videoUri = remember(resId) { RawResourceDataSource.buildRawResourceUri(resId) }
    
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    LaunchedEffect(exoPlayer) {
        while (true) {
            if (exoPlayer.isPlaying) {
                onProgress(exoPlayer.currentPosition, exoPlayer.duration)
            }
            delay(500)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> exoPlayer.play()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = true
                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                setBackgroundColor(android.graphics.Color.BLACK)
                setShutterBackgroundColor(android.graphics.Color.BLACK)
            }
        },
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun CameraTrackingPanel(
    viewModel: TutorialViewModel,
    trackingResult: TrackingResult,
    cameraState: CameraState,
    settings: TutorialSettings,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    
    val hasCameraPermission = remember {
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .clipToBounds()
    ) {
        if (hasCameraPermission) {
            val smoothedBox = trackingResult.smoothedBox
            
            // Centralization translation (AI driven)
            val tx = if (smoothedBox != null) (0.5f - smoothedBox.centerX()) else 0f
            val ty = if (smoothedBox != null) (0.5f - smoothedBox.centerY()) else 0f

            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = if (settings.isCameraMirrored) -1f else 1f
                        
                        // Subtle AI centering
                        translationX = tx * size.width * 0.2f
                        translationY = ty * size.height * 0.2f
                    },
                update = { previewView ->
                    if (cameraState == CameraState.IDLE) {
                        viewModel.startCamera(lifecycleOwner, previewView)
                    }
                }
            )
        } else {
            Text(
                "Camera Permission Required",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (cameraState == CameraState.STARTING) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun TrackingDetail(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
        Text(value, color = Color.Cyan, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

private val tutorialSubtitles = listOf(
    0L to "Welcome to PixieLook! Let's start the hair tutorial.",
    4000L to "On the right, you can see your live camera feed.",
    8000L to "Pinch the camera view to zoom in on details.",
    12000L to "Drag the camera panel to move it around the screen.",
    16000L to "Double tap the camera to reset its position.",
    20000L to "Our AI is tracking your face in real-time.",
    25000L to "Follow the steps to find your perfect hairstyle!"
)
