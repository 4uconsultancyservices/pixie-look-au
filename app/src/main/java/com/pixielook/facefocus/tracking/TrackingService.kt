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
        TrackingResult(null, null, 1.0f, PerformanceMetrics(0, 0, "Initializing"))
    )
    val trackingResult = _trackingResult.asStateFlow()

    private val faceTracker = MediaPipeFaceTracker(context) { face ->
        processFaceResult(face)
    }
    
    private val smoother = SmoothTracker()
    private var isTrackingEnabled = true
    private var isAutoZoomEnabled = true
    private var currentSettings = TutorialSettings()

    private val frameCounter = AtomicInteger(0)
    private var lastFpsTimestamp = System.currentTimeMillis()

    fun processFrame(cameraFrame: CameraFrame) {
        if (!isTrackingEnabled) {
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
        val faceWithHair = face?.let {
            // Extend bounding box upwards significantly to ensure hair is covered
            // Height increase by 70% of face height upwards
            val height = it.boundingBox.height()
            val newTop = (it.boundingBox.top - height * 0.7f).coerceAtLeast(0f)
            val newBottom = it.boundingBox.bottom // Keep chin as base
            it.copy(boundingBox = RectF(it.boundingBox.left, newTop, it.boundingBox.right, newBottom))
        }

        val resultData = if (isAutoZoomEnabled && faceWithHair != null) {
            smoother.process(faceWithHair.boundingBox)
        } else {
            // If tracking lost, smoothedBox becomes null but smoother.process handles returning to center
            smoother.process(null)
        }

        _trackingResult.update {
            it.copy(
                face = faceWithHair,
                smoothedBox = resultData.smoothedBox,
                zoomLevel = resultData.zoomLevel,
                metrics = it.metrics.copy(
                    trackingStatus = if (face != null) "Tracking" else "Searching"
                )
            )
        }
    }

    fun updateSettings(settings: TutorialSettings) {
        currentSettings = settings
        isTrackingEnabled = settings.isFaceTrackingEnabled
        isAutoZoomEnabled = settings.isAutoZoomEnabled
        // We could pass smoothing/sensitivity to the smoother here if needed
    }

    fun setTrackingEnabled(enabled: Boolean) {
        isTrackingEnabled = enabled
        if (!enabled) smoother.reset()
    }

    fun setAutoZoomEnabled(enabled: Boolean) {
        isAutoZoomEnabled = enabled
        if (!enabled) smoother.reset()
    }

    fun release() {
        faceTracker.release()
        smoother.reset()
    }
}
