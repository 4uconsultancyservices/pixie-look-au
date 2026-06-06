package com.pixielook.facefocus.ui.tutorial

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.media3.datasource.RawResourceDataSource
import com.pixielook.facefocus.R
import com.pixielook.facefocus.models.*

@Composable
fun TutorialScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit,
    viewModel: TutorialViewModel = viewModel()
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val trackingResult by viewModel.trackingResult.collectAsState()
    val cameraState by viewModel.cameraState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    
    var showSettings by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsDialog(
            settings = settings,
            onSettingsChanged = viewModel::updateSettings,
            onDismiss = { showSettings = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Main split-screen content (Half-Half)
        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    TutorialVideoPlayer(resId = R.raw.hairstyle_tutorial)
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
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    TutorialVideoPlayer(resId = R.raw.hairstyle_tutorial)
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
    resId: Int
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
                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clipToBounds()
    ) {
        if (hasCameraPermission) {
            val smoothedBox = trackingResult.smoothedBox
            val zoom = 1.0f // Zoom locked at 1.0x
            
            // Centralization translation
            val tx = if (smoothedBox != null) (0.5f - smoothedBox.centerX()) * zoom else 0f
            val ty = if (smoothedBox != null) (0.5f - smoothedBox.centerY()) * zoom else 0f

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
                        scaleX = zoom * (if (settings.isCameraMirrored) -1f else 1f)
                        scaleY = zoom
                        // Clamp translation to keep view within bounds
                        val maxT = if (zoom > 1f) (zoom - 1f) * 0.5f else 0f
                        translationX = tx.coerceIn(-maxT, maxT) * size.width
                        translationY = ty.coerceIn(-maxT, maxT) * size.height
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

        // Tracking Details Row at the TOP of Camera Panel (Transparent)
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TrackingDetail("Confidence", "${(trackingResult.face?.confidence?.times(100))?.toInt() ?: 0}%")
            TrackingDetail("FPS", trackingResult.metrics.fps.toString())
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
