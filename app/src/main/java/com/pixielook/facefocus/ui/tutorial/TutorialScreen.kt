package com.pixielook.facefocus.ui.tutorial

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.pixielook.facefocus.models.CameraState
import com.pixielook.facefocus.models.TrackingResult

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
    val isTrackingEnabled by viewModel.isTrackingEnabled.collectAsState()
    val isAutoZoomEnabled by viewModel.isAutoZoomEnabled.collectAsState()

    Scaffold(
        topBar = {
            TutorialTopBar(
                title = "Smart Mirror Tutorial",
                description = "Learn how to use AI tracking features",
                onBack = onBack,
                isTrackingEnabled = isTrackingEnabled,
                isAutoZoomEnabled = isAutoZoomEnabled,
                onToggleTracking = viewModel::toggleTracking,
                onToggleAutoZoom = viewModel::toggleAutoZoom
            )
        },
        bottomBar = {
            TrackingStatusBar(trackingResult)
        },
        containerColor = Color(0xFF080808)
    ) { paddingValues ->
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    TutorialVideoPlayer(
                        uri = Uri.parse("asset:///tutorial_video.mp4"),
                        onStateUpdate = viewModel::updateVideoState
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    CameraTrackingPanel(
                        viewModel = viewModel,
                        trackingResult = trackingResult,
                        cameraState = cameraState
                    )
                    
                    // Collapsible Tutorial Steps Panel
                    TutorialStepsPanel(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        currentStep = videoState.currentStep,
                        onStepClick = viewModel::updateCurrentStep
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    TutorialVideoPlayer(
                        uri = Uri.parse("asset:///tutorial_video.mp4"),
                        onStateUpdate = viewModel::updateVideoState
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    CameraTrackingPanel(
                        viewModel = viewModel,
                        trackingResult = trackingResult,
                        cameraState = cameraState
                    )
                    
                    // Collapsible Tutorial Steps Panel
                    TutorialStepsPanel(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        currentStep = videoState.currentStep,
                        onStepClick = viewModel::updateCurrentStep
                    )
                }
            }
        }
    }
}

@Composable
fun TutorialStepsPanel(
    modifier: Modifier = Modifier,
    currentStep: Int,
    onStepClick: (Int) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val steps = listOf(
        "Position yourself in front of the camera",
        "Enable tracking to see the AI response",
        "Move your head to test auto-zoom",
        "Adjust settings for optimal performance"
    )

    Card(
        modifier = modifier.widthIn(max = 300.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.8f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Tutorial Steps",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
                TextButton(onClick = { isExpanded = !isExpanded }) {
                    Text(if (isExpanded) "Hide" else "Show", color = Color.Cyan)
                }
            }

            if (isExpanded) {
                steps.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentStep == index,
                            onClick = { onStepClick(index) },
                            colors = RadioButtonDefaults.colors(selectedColor = Color.Cyan)
                        )
                        Text(
                            step,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (currentStep == index) Color.White else Color.Gray
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
    description: String,
    onBack: () -> Unit,
    isTrackingEnabled: Boolean,
    isAutoZoomEnabled: Boolean,
    onToggleTracking: () -> Unit,
    onToggleAutoZoom: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge, color = Color.White)
                Text(description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        },
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tracking", color = Color.White, style = MaterialTheme.typography.labelSmall)
                Switch(checked = isTrackingEnabled, onCheckedChange = { onToggleTracking() })
                Spacer(modifier = Modifier.width(8.dp))
                Text("Auto Zoom", color = Color.White, style = MaterialTheme.typography.labelSmall)
                Switch(checked = isAutoZoomEnabled, onCheckedChange = { onToggleAutoZoom() })
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
    )
}

@Composable
fun TrackingStatusBar(result: TrackingResult) {
    Surface(
        color = Color(0xFF121212),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatusItem("Status", result.metrics.trackingStatus)
            StatusItem("Confidence", "${(result.face?.confidence?.times(100))?.toInt() ?: 0}%")
            StatusItem("FPS", result.metrics.fps.toString())
            StatusItem("Zoom", "${String.format("%.1fx", result.zoomLevel)}")
        }
    }
}

@Composable
fun StatusItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@OptIn(UnstableApi::class)
@Composable
fun TutorialVideoPlayer(
    uri: Uri,
    onStateUpdate: (com.pixielook.facefocus.models.VideoState) -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
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
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
    )
}

@Composable
fun CameraTrackingPanel(
    viewModel: TutorialViewModel,
    trackingResult: TrackingResult,
    cameraState: CameraState
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                if (cameraState == CameraState.IDLE) {
                    viewModel.startCamera(lifecycleOwner, previewView)
                }
            }
        )

        AndroidView(
            factory = { ctx ->
                TrackingOverlayView(ctx)
            },
            modifier = Modifier.fillMaxSize(),
            update = { overlayView ->
                overlayView.updateResult(trackingResult)
            }
        )

        if (cameraState == CameraState.STARTING) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (cameraState == CameraState.ERROR) {
            Text(
                "Camera Error",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
