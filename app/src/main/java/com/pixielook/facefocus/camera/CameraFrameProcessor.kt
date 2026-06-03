package com.pixielook.facefocus.camera

import android.graphics.*
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

object CameraFrameProcessor {

    fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        val plane = image.planes[0]
        val buffer = plane.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        
        // This assumes RGBA_8888 or similar if coming from certain analysis configurations.
        // For YUV_420_888, we need more complex conversion.
        // Assuming we configured ImageAnalysis to output RGBA_8888 for simplicity in this prototype.
        
        val bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        
        // Handle rotation
        if (image.imageInfo.rotationDegrees != 0) {
            val matrix = Matrix()
            matrix.postRotate(image.imageInfo.rotationDegrees.toFloat())
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
        
        return bitmap
    }

    fun nv21ToBitmap(nv21: ByteArray, width: Int, height: Int): Bitmap {
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    fun yuv420ToBitmap(image: ImageProxy): Bitmap {
        val nv21 = yuv420ToNv21(image)
        return nv21ToBitmap(nv21, image.width, image.height)
    }

    private fun yuv420ToNv21(image: ImageProxy): ByteArray {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        return nv21
    }
}
