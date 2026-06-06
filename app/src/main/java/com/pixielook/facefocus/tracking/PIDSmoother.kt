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
    private val xSmoother = PIDSmoother(0.20f, 0.02f, 0.08f)
    private val ySmoother = PIDSmoother(0.20f, 0.02f, 0.08f)
    private val zoomSmoother = PIDSmoother(0.12f, 0.008f, 0.04f)

    private var currentX = 0.5f
    private var currentY = 0.5f
    private var currentZoom = 1.0f
    
    private var lastValidBox: RectF? = null
    private var framesSinceLastDetection = 0
    private val MAX_LOST_FRAMES = 15 // Hold tracking for ~0.5s if lost

    fun process(targetBox: RectF?): TrackingResultData {
        val activeBox = if (targetBox != null) {
            framesSinceLastDetection = 0
            lastValidBox = targetBox
            targetBox
        } else {
            framesSinceLastDetection++
            if (framesSinceLastDetection < MAX_LOST_FRAMES) {
                lastValidBox // Keep using last valid box
            } else {
                null // Actually lost
            }
        }

        if (activeBox == null) {
            // Slowly return to center/zoom 1.0 if lost
            val dx = xSmoother.update(0.5f, currentX)
            val dy = ySmoother.update(0.5f, currentY)
            val dZoom = zoomSmoother.update(1.0f, currentZoom)
            currentX += dx
            currentY += dy
            currentZoom += dZoom
        } else {
            val targetX = activeBox.centerX()
            val targetY = activeBox.centerY()
            val boxHeight = activeBox.height()
            val targetZoom = (0.5f / boxHeight.coerceAtLeast(0.1f)).coerceIn(1.0f, 3.0f)

            val dx = xSmoother.update(targetX, currentX)
            val dy = ySmoother.update(targetY, currentY)
            val dZoom = zoomSmoother.update(targetZoom, currentZoom)

            currentX += dx
            currentY += dy
            currentZoom += dZoom
        }

        // Clamp values
        currentX = currentX.coerceIn(0f, 1f)
        currentY = currentY.coerceIn(0f, 1f)
        currentZoom = currentZoom.coerceIn(1.0f, 3.0f)

        val smoothWidth = 0.4f / currentZoom
        val smoothHeight = 0.6f / currentZoom
        
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
