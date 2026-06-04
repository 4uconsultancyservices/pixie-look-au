package com.pixielook.facefocus.camera

import android.content.Context
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

    fun bindCamera(previewView: PreviewView, isFrontCamera: Boolean = true) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val cameraSelector = if (isFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

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
                camera = cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        val bitmap = CameraFrameProcessor.yuv420ToBitmap(imageProxy)
        val frame = CameraFrame(
            bitmap = bitmap,
            timestamp = imageProxy.imageInfo.timestamp,
            orientation = imageProxy.imageInfo.rotationDegrees
        )
        trackingService.processFrame(frame)
        imageProxy.close()
    }

    fun release() {
        cameraExecutor.shutdown()
        cameraProvider?.unbindAll()
    }
}
