package com.pixielook.facefocus.ui.screens

import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.pixielook.facefocus.R

/**
 * IntroVideoScreen plays the startup intro video with sound.
 * Improved implementation with lifecycle management, error handling,
 * and optimized rendering to prevent black screen issues.
 */
@OptIn(UnstableApi::class)
@Composable
fun IntroVideoScreen(
    onVideoFinished: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Resource URI for the intro video
    val videoUri = remember { RawResourceDataSource.buildRawResourceUri(R.raw.intro_with_sound) }

    // Initialize ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = true
        }
    }

    // Manage ExoPlayer lifecycle with the screen's lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    exoPlayer.pause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    exoPlayer.play()
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    // Add Player Listeners for completion and error handling
    LaunchedEffect(exoPlayer) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    onVideoFinished()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("IntroVideoScreen", "ExoPlayer Error: ${error.message}", error)
                // Safety: Proceed to next screen if video fails to play
                onVideoFinished()
            }
            
            override fun onRenderedFirstFrame() {
                Log.d("IntroVideoScreen", "First frame rendered successfully")
            }
        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    // RESIZE_MODE_FIT ensures the whole video is visible. 
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    
                    // Prevents shutter/black screen between source switches if any
                    setKeepContentOnPlayerReset(true)
                    
                    // Match video background to app theme
                    setBackgroundColor(android.graphics.Color.BLACK)
                    
                    // Set the shutter background color to black to avoid white flash
                    setShutterBackgroundColor(android.graphics.Color.BLACK)
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { playerView ->
                // Ensure the view is always synced with the current player instance
                if (playerView.player != exoPlayer) {
                    playerView.player = exoPlayer
                }
            }
        )
    }
}
