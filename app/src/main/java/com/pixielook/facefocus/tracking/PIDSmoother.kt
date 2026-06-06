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
            return 0f
        }

        val dt = (now - lastTime) / 1000f
        if (dt <= 0.001f) return 0f 

        val error = target - current
        integral += error * dt
        
        // Anti-windup
        integral = max(minOutput / ki.coerceAtLeast(0.001f), min(maxOutput / ki.coerceAtLeast(0.001f), integral))

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
    private val xSmoother = PIDSmoother(0.05f, 0.001f, 0.25f) 
    private val ySmoother = PIDSmoother(0.05f, 0.001f, 0.25f)

    private var currentX = 0.5f
    private var currentY = 0.5f
    
    private var targetX = 0.5f
    private var targetY = 0.5f

    // State Locking Logic - HIGH THRESHOLD for stability
    private var isUserPresent = false
    private var presenceCounter = 0
    private var absenceCounter = 0
    private val PRESENCE_REQUIRED_FRAMES = 10 
    private val ABSENCE_REQUIRED_FRAMES = 90  

    fun process(targetBox: RectF?, confidence: Float): TrackingResultData {
        val detected = targetBox != null && confidence > 0.40f 
        
        if (detected) {
            absenceCounter = 0
            presenceCounter++
            if (presenceCounter >= PRESENCE_REQUIRED_FRAMES) {
                isUserPresent = true
            }
            
            if (isUserPresent) {
                val alpha = 0.05f 
                targetX = targetX * (1 - alpha) + targetBox!!.centerX() * alpha
                targetY = targetY * (1 - alpha) + targetBox.centerY() * alpha
            }
        } else {
            presenceCounter = 0
            absenceCounter++
            if (absenceCounter >= ABSENCE_REQUIRED_FRAMES) {
                isUserPresent = false
                targetX = 0.5f
                targetY = 0.5f
            }
        }

        // PID update for X/Y movement
        val dx = xSmoother.update(targetX, currentX)
        val dy = ySmoother.update(targetY, currentY)
        currentX += dx
        currentY += dy

        // Clamp positions
        currentX = currentX.coerceIn(0.2f, 0.8f)
        currentY = currentY.coerceIn(0.2f, 0.8f)

        // Return box at fixed 1.0x zoom framing (Face specific)
        val smoothWidth = 0.2f
        val smoothHeight = 0.2f
        
        val smoothedBox = RectF(
            currentX - smoothWidth / 2,
            currentY - smoothHeight / 2,
            currentX + smoothWidth / 2,
            currentY + smoothHeight / 2
        )

        return TrackingResultData(smoothedBox, false)
    }

    fun reset() {
        xSmoother.reset()
        ySmoother.reset()
        currentX = 0.5f
        currentY = 0.5f
        targetX = 0.5f
        targetY = 0.5f
    }
}

data class TrackingResultData(
    val smoothedBox: RectF?,
    val isHeavyMotion: Boolean
)
