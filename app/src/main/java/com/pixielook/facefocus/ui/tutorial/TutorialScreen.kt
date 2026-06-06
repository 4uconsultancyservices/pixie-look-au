package com.pixielook.facefocus.ui.tutorial

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.pixielook.facefocus.models.CameraState
import com.pixielook.facefocus.models.TrackingResult

import androidx.compose.ui.draw.clipToBounds
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.media3.datasource.RawResourceDataSource
import com.pixielook.facefocus.R
import com.pixielook.facefocus.models.TutorialSettings

@Composable
fun TutorialScreen(
    onBack: () -> Unit,
    viewModel: TutorialViewModel = viewModel()
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val trackingResult by viewModel.trackingResult.collectAsState()
    val cameraState by viewModel.cameraState.collectAsState()
    val videoState by viewModel.videoState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    
    var showSettings by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsDialog(
            settings = settings,
            onSettingsChanged = viewModel::updateSettings,
            onDismiss = { showSettings = false }
        )
    }

    Scaffold(
        topBar = {
            TutorialTopBar(
                title = "Settings",
                onBack = onBack,
                onOpenSettings = { showSettings = true }
            )
        },
        containerColor = Color(0xFF080808)
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (isLandscape) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        TutorialVideoPlayer(
                            resId = R.raw.hairstyle_tutorial,
                            onStateUpdate = viewModel::updateVideoState
                        )
                        // Tutorial Badge
                        Surface(
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFF6200EE),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "TUTORIAL",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CameraTrackingPanel(
                            viewModel = viewModel,
                            trackingResult = trackingResult,
                            cameraState = cameraState,
                            settings = settings
                        )
                        
                        // LIVE Badge
                        Surface(
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFFFF5722),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "LIVE",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Zoom Indicator
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                            color = Color.Black.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "${String.format("%.1f", trackingResult.zoomLevel)}x",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        TutorialVideoPlayer(
                            resId = R.raw.hairstyle_tutorial,
                            onStateUpdate = viewModel::updateVideoState
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CameraTrackingPanel(
                            viewModel = viewModel,
                            trackingResult = trackingResult,
                            cameraState = cameraState,
                            settings = settings
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorialTopBar(
    title: String,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit
) {
    TopAppBar(
        title = {
            Text(title, style = MaterialTheme.typography.titleLarge, color = Color.White)
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        },
        actions = {
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
    )
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
                SettingsSlider("Tracking Smoothing", "Higher = smoother but slower", settings.trackingSmoothing) {
                    onSettingsChanged(settings.copy(trackingSmoothing = it))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Auto Zoom", color = Color.Yellow, style = MaterialTheme.typography.labelLarge)
                SettingsToggle("Enable Auto Zoom", "Keep face centered", settings.isAutoZoomEnabled) {
                    onSettingsChanged(settings.copy(isAutoZoomEnabled = it))
                }
                SettingsSlider("Zoom Sensitivity", "How aggressively to zoom", settings.zoomSensitivity) {
                    onSettingsChanged(settings.copy(zoomSensitivity = it))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Camera", color = Color.Green, style = MaterialTheme.typography.labelLarge)
                SettingsToggle("Mirror Camera", "Flip horizontally", settings.isCameraMirrored) {
                    onSettingsChanged(settings.copy(isCameraMirrored = it))
                }
                
                Button(
                    onClick = { 
                        val nextLens = (settings.lensFacing + 1) % 4
                        onSettingsChanged(settings.copy(lensFacing = nextLens))
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    val cameraLabel = when(settings.lensFacing) {
                        0 -> "Front Camera"
                        1 -> "Back Camera"
                        2 -> "External/USB Camera"
                        else -> "IP Camera (Mock)"
                    }
                    Text("Switch Camera (Current: $cameraLabel)")
                }
                
                if (settings.lensFacing == 3) {
                    Text("IP Camera URL (RTSP/MJPEG)", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    // In a real app, we'd add an OutlinedTextField here to save the URL to settings
                }

                // Add more settings as needed
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Save Settings") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Reset Defaults") }
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

@Composable
fun SettingsSlider(title: String, subtitle: String, value: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(title, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }
            Text(String.format("%.2f", value), color = Color.Cyan, style = MaterialTheme.typography.labelSmall)
        }
        Slider(value = value, onValueChange = onValueChange)
    }
}

@OptIn(UnstableApi::class)
@Composable
fun TutorialVideoPlayer(
    resId: Int,
    onStateUpdate: (com.pixielook.facefocus.models.VideoState) -> Unit
) {
    val context = LocalContext.current
    val videoUri = RawResourceDataSource.buildRawResourceUri(resId)
    
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = true
                setBackgroundColor(android.graphics.Color.BLACK)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun CameraTrackingPanel(
    viewModel: TutorialViewModel,
    trackingResult: TrackingResult,
    cameraState: CameraState,
    settings: TutorialSettings
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    
    val hasCameraPermission = remember {
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            viewModel.stopCamera()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clipToBounds()
    ) {
        if (hasCameraPermission) {
            // Apply Dynamic Centering and Zoom
            val smoothedBox = trackingResult.smoothedBox
            val zoom = if (settings.isAutoZoomEnabled) trackingResult.zoomLevel else 1.0f
            
            // Calculate translation to keep subject centralized
            // smoothedBox coords are 0..1. Center is 0.5, 0.5.
            val tx = if (smoothedBox != null && settings.isAutoZoomEnabled) {
                (0.5f - smoothedBox.centerX()) * zoom
            } else 0f
            
            val ty = if (smoothedBox != null && settings.isAutoZoomEnabled) {
                (0.5f - smoothedBox.centerY()) * zoom
            } else 0f

            val cameraModifier = if (settings.lensFacing == 3) {
                // IP Camera View Placeholder (Could use ExoPlayer for RTSP)
                Modifier.fillMaxSize()
            } else {
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = zoom * (if (settings.isCameraMirrored) -1f else 1f)
                        scaleY = zoom
                        // Clamp translation to prevent showing black edges
                        val maxTx = (zoom - 1f) * 0.5f
                        val maxTy = (zoom - 1f) * 0.5f
                        translationX = tx.coerceIn(-maxTx, maxTx) * size.width
                        translationY = ty.coerceIn(-maxTy, maxTy) * size.height
                    }
            }

            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                modifier = cameraModifier,
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

        if (settings.showTrackingOverlay) {
            AndroidView(
                factory = { ctx ->
                    TrackingOverlayView(ctx)
                },
                modifier = Modifier.fillMaxSize(),
                update = { overlayView ->
                    overlayView.updateResult(trackingResult, settings.showFaceLandmarks)
                }
            )
        }

        if (cameraState == CameraState.STARTING) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
