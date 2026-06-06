package com.pixielook.facefocus.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.pixielook.facefocus.models.CameraFrame
import com.pixielook.facefocus.tracking.TrackingService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraPipeline(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val trackingService: TrackingService
) {
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun bindCamera(previewView: PreviewView, lensFacing: Int = CameraSelector.LENS_FACING_FRONT) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }

            try {
                cameraProvider?.unbindAll()
                
                // Check if the requested camera exists, if not fallback to any
                val hasCamera = cameraProvider?.hasCamera(cameraSelector) ?: false
                val selector = if (hasCamera) cameraSelector else CameraSelector.DEFAULT_BACK_CAMERA

                camera = cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    selector,
                    preview,
                    imageAnalyzer
                )

                Log.d("CameraPipeline", "Camera bound successfully: ${if (hasCamera) lensFacing else "fallback to back"}")
            } catch (e: Exception) {
                Log.e("CameraPipeline", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        try {
            val bitmap = CameraFrameProcessor.yuv420ToBitmap(imageProxy)
            val frame = CameraFrame(
                bitmap = bitmap,
                timestamp = imageProxy.imageInfo.timestamp,
                orientation = imageProxy.imageInfo.rotationDegrees
            )
            trackingService.processFrame(frame)
        } catch (e: Exception) {
            Log.e("CameraPipeline", "Error processing image proxy", e)
        } finally {
            imageProxy.close()
        }
    }

    fun release() {
        cameraExecutor.shutdown()
        cameraProvider?.unbindAll()
    }
}
