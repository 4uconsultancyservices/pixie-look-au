package com.pixielook.facefocus.tracking

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import com.pixielook.facefocus.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicInteger

class TrackingService(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    private val _trackingResult = MutableStateFlow(
        TrackingResult(null, null, false, PerformanceMetrics(0, 0, "Initializing"))
    )
    val trackingResult = _trackingResult.asStateFlow()

    private val faceTracker: MediaPipeFaceTracker? = try {
        MediaPipeFaceTracker(context) { face ->
            processFaceResult(face)
        }
    } catch (e: Throwable) {
        println("TrackingService: Failed to initialize MediaPipeFaceTracker: ${e.message}")
        null
    }
    
    private val smoother = SmoothTracker()
    private var isTrackingEnabled = true
    private var currentSettings = TutorialSettings()

    private val frameCounter = AtomicInteger(0)
    private var lastFpsTimestamp = System.currentTimeMillis()

    fun processFrame(cameraFrame: CameraFrame) {
        if (!isTrackingEnabled || faceTracker == null) {
            _trackingResult.update { it.copy(face = null, smoothedBox = null) }
            return
        }

        val now = System.currentTimeMillis()
        val latency = now - cameraFrame.timestamp
        
        // Update FPS
        frameCounter.incrementAndGet()
        if (now - lastFpsTimestamp >= 1000) {
            val fps = frameCounter.getAndSet(0)
            lastFpsTimestamp = now
            _trackingResult.update { it.copy(metrics = it.metrics.copy(fps = fps, latencyMs = latency)) }
        }

        faceTracker.detect(cameraFrame.bitmap, cameraFrame.timestamp)
    }

    private fun processFaceResult(face: FaceDetection?) {
        // Use the smoothed result including the confidence threshold
        val confidence = face?.confidence ?: 0f
        val resultData = smoother.process(face?.boundingBox, confidence)

        _trackingResult.update {
            it.copy(
                face = face,
                smoothedBox = resultData.smoothedBox,
                isHeavyMotion = resultData.isHeavyMotion,
                metrics = it.metrics.copy(
                    trackingStatus = if (face != null) "Face Tracking" else "Searching"
                )
            )
        }
    }

    fun updateSettings(settings: TutorialSettings) {
        currentSettings = settings
        isTrackingEnabled = settings.isFaceTrackingEnabled
        // We could pass smoothing/sensitivity to the smoother here if needed
    }

    fun setTrackingEnabled(enabled: Boolean) {
        isTrackingEnabled = enabled
        if (!enabled) smoother.reset()
    }

    fun release() {
        faceTracker?.release()
        smoother.reset()
    }
}
