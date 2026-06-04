package com.pixielook.facefocus.tracking

import android.graphics.RectF
import kotlin.math.max
import kotlin.math.min

class PIDSmoother(
    private val kp: Float,
    private val ki: Float,
    private val kd: Float,
    private val minOutput: Float = -1f,
    private val maxOutput: Float = 1f
) {
    private var lastError = 0f
    private var integral = 0f
    private var lastTime = -1L

    fun update(target: Float, current: Float): Float {
        val now = System.currentTimeMillis()
        if (lastTime == -1L) {
            lastTime = now
            return current
        }

        val dt = (now - lastTime) / 1000f
        if (dt <= 0) return current

        val error = target - current
        integral += error * dt
        
        // Anti-windup
        integral = max(minOutput / ki.coerceAtLeast(0.01f), min(maxOutput / ki.coerceAtLeast(0.01f), integral))

        val derivative = (error - lastError) / dt
        val output = (kp * error) + (ki * integral) + (kd * derivative)

        lastError = error
        lastTime = now

        return max(minOutput, min(maxOutput, output))
    }

    fun reset() {
        lastError = 0f
        integral = 0f
        lastTime = -1L
    }
}

class SmoothTracker {
    private val xSmoother = PIDSmoother(0.15f, 0.01f, 0.05f)
    private val ySmoother = PIDSmoother(0.15f, 0.01f, 0.05f)
    private val zoomSmoother = PIDSmoother(0.1f, 0.005f, 0.02f)

    private var currentX = 0.5f
    private var currentY = 0.5f
    private var currentZoom = 1.0f

    fun process(targetBox: RectF?): TrackingResultData {
        if (targetBox == null) {
            return TrackingResultData(null, currentZoom)
        }

        val targetX = targetBox.centerX()
        val targetY = targetBox.centerY()
        
        // Target zoom based on box size (smaller box = higher zoom)
        val boxSize = max(targetBox.width(), targetBox.height())
        val targetZoom = (0.3f / boxSize.coerceAtLeast(0.1f)).coerceIn(1.0f, 3.0f)

        val dx = xSmoother.update(targetX, currentX)
        val dy = ySmoother.update(targetY, currentY)
        val dZoom = zoomSmoother.update(targetZoom, currentZoom)

        currentX += dx
        currentY += dy
        currentZoom += dZoom

        // Clamp values
        currentX = currentX.coerceIn(0f, 1f)
        currentY = currentY.coerceIn(0f, 1f)
        currentZoom = currentZoom.coerceIn(1.0f, 3.0f)

        val smoothWidth = 0.2f / currentZoom
        val smoothHeight = 0.2f / currentZoom
        
        val smoothedBox = RectF(
            currentX - smoothWidth / 2,
            currentY - smoothHeight / 2,
            currentX + smoothWidth / 2,
            currentY + smoothHeight / 2
        )

        return TrackingResultData(smoothedBox, currentZoom)
    }

    fun reset() {
        xSmoother.reset()
        ySmoother.reset()
        zoomSmoother.reset()
    }
}

data class TrackingResultData(
    val smoothedBox: RectF?,
    val zoomLevel: Float
)
