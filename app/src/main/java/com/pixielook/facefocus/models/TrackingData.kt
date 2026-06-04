package com.pixielook.facefocus.models

import android.graphics.Bitmap
import android.graphics.RectF

data class Landmark(
    val x: Float,
    val y: Float,
    val label: String? = null
)

data class FaceDetection(
    val boundingBox: RectF,
    val confidence: Float,
    val landmarks: List<Landmark> = emptyList()
)

data class TrackingResult(
    val face: FaceDetection?,
    val smoothedBox: RectF?,
    val zoomLevel: Float,
    val metrics: PerformanceMetrics
)

data class CameraFrame(
    val bitmap: Bitmap,
    val timestamp: Long,
    val orientation: Int
)

data class PerformanceMetrics(
    val fps: Int,
    val latencyMs: Long,
    val trackingStatus: String
)

enum class CameraState {
    IDLE,
    STARTING,
    RUNNING,
    ERROR
}

data class VideoState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isLoading: Boolean = false,
    val currentStep: Int = 0
)
