package com.pixielook.facefocus.ui.tutorial

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.pixielook.facefocus.models.TrackingResult

class TrackingOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var trackingResult: TrackingResult? = null
    private var showLandmarks: Boolean = true // Default to true
    
    private val dotPaint = Paint().apply {
        color = Color.CYAN
        alpha = 180 // Increased visibility
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    fun updateResult(result: TrackingResult, showLandmarks: Boolean = true) {
        this.trackingResult = result
        this.showLandmarks = showLandmarks
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val result = trackingResult ?: return
        val face = result.face ?: return

        // Draw Thin Dots (Landmarks)
        if (showLandmarks) {
            face.landmarks.forEach { landmark ->
                canvas.drawCircle(landmark.x * width, landmark.y * height, 3f, dotPaint)
            }
        }
    }
}
