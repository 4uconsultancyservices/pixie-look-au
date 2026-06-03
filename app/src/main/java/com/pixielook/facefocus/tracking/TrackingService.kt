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

    private val frameCounter = AtomicInteger(0)
    private var lastFpsTimestamp = System.currentTimeMillis()
    private var lastProcessTimestamp = System.currentTimeMillis()

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
        val resultData = if (isAutoZoomEnabled) {
            smoother.process(face?.boundingBox)
        } else {
            TrackingResultData(face?.boundingBox, 1.0f)
        }

        _trackingResult.update {
            it.copy(
                face = face,
                smoothedBox = resultData.smoothedBox,
                zoomLevel = resultData.zoomLevel,
                metrics = it.metrics.copy(
                    trackingStatus = if (face != null) "Tracking" else "Searching"
                )
            )
        }
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
