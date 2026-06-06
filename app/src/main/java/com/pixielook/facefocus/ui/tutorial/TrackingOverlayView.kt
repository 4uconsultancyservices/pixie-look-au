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
    private var showLandmarks: Boolean = false // Default to false
    
    private val boxPaint = Paint().apply {
        color = Color.parseColor("#A020F0") // Purple
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    private val accentPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 3f
        isAntiAlias = true
    }

    private val dotPaint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 28f
        typeface = Typeface.DEFAULT_BOLD
    }

    fun updateResult(result: TrackingResult, showLandmarks: Boolean = false) {
        this.trackingResult = result
        this.showLandmarks = showLandmarks
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val result = trackingResult ?: return
        val face = result.face ?: return
        val smoothedBox = result.smoothedBox ?: face.boundingBox

        // Map normalized coordinates to view coordinates
        val rect = RectF(
            smoothedBox.left * width,
            smoothedBox.top * height,
            smoothedBox.right * width,
            smoothedBox.bottom * height
        )

        // Draw Subtle Purple Rounded Box
        canvas.drawRoundRect(rect, 16f, 16f, boxPaint)

        // Draw Landmarks ONLY if explicitly enabled
        if (showLandmarks) {
            face.landmarks.forEach { landmark ->
                canvas.drawCircle(landmark.x * width, landmark.y * height, 4f, dotPaint)
            }
        }

        // Confidence label - very subtle
        canvas.drawText(
            "Face ${(face.confidence * 100).toInt()}%",
            rect.left,
            rect.top - 10f,
            textPaint
        )
    }
}
