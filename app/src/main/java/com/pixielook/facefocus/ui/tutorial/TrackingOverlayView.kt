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
    
    private val boxPaint = Paint().apply {
        color = Color.parseColor("#A020F0") // Purple
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }

    private val accentPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
    }

    private val dotPaint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 32f
        typeface = Typeface.DEFAULT_BOLD
    }

    fun updateResult(result: TrackingResult) {
        this.trackingResult = result
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

        // Draw Purple Rounded Box
        canvas.drawRoundRect(rect, 24f, 24f, boxPaint)

        // Draw Corner Accents
        val cornerLen = 40f
        // Top-Left
        canvas.drawLine(rect.left, rect.top, rect.left + cornerLen, rect.top, accentPaint)
        canvas.drawLine(rect.left, rect.top, rect.left, rect.top + cornerLen, accentPaint)
        // Top-Right
        canvas.drawLine(rect.right, rect.top, rect.right - cornerLen, rect.top, accentPaint)
        canvas.drawLine(rect.right, rect.top, rect.right, rect.top + cornerLen, accentPaint)
        // Bottom-Left
        canvas.drawLine(rect.left, rect.bottom, rect.left + cornerLen, rect.bottom, accentPaint)
        canvas.drawLine(rect.left, rect.bottom, rect.left, rect.bottom - cornerLen, accentPaint)
        // Bottom-Right
        canvas.drawLine(rect.right, rect.bottom, rect.right - cornerLen, rect.bottom, accentPaint)
        canvas.drawLine(rect.right, rect.bottom, rect.right, rect.bottom - cornerLen, accentPaint)

        // Draw Landmarks
        face.landmarks.forEach { landmark ->
            canvas.drawCircle(landmark.x * width, landmark.y * height, 6f, dotPaint)
        }

        // Draw Confidence Label
        canvas.drawText(
            "Face: ${(face.confidence * 100).toInt()}%",
            rect.left,
            rect.top - 20f,
            textPaint
        )

        // Draw Crosshair in center of box
        val cx = rect.centerX()
        val cy = rect.centerY()
        canvas.drawLine(cx - 20f, cy, cx + 20f, cy, accentPaint)
        canvas.drawLine(cx, cy - 20f, cx, cy + 20f, accentPaint)

        // Draw Zoom Indicator
        canvas.drawText(
            "Zoom: ${String.format("%.1fx", result.zoomLevel)}",
            20f,
            height - 40f,
            textPaint
        )
    }
}
