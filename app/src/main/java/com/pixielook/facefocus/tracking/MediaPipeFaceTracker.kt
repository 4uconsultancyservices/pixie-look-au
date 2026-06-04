package com.pixielook.facefocus.tracking

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult
import com.pixielook.facefocus.models.FaceDetection
import com.pixielook.facefocus.models.Landmark
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MediaPipeFaceTracker(
    private val context: Context,
    private val onResult: (FaceDetection?) -> Unit
) {
    private var faceDetector: FaceDetector? = null
    private val backgroundExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        setupFaceDetector()
    }

    private fun setupFaceDetector() {
        val baseOptionsBuilder = BaseOptions.builder()
            .setDelegate(Delegate.GPU)
            .setModelAssetPath("blaze_face_short_range.tflite")

        val optionsBuilder = FaceDetector.FaceDetectorOptions.builder()
            .setBaseOptions(baseOptionsBuilder.build())
            .setMinDetectionConfidence(0.5f)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result, _ ->
                processResult(result)
            }
            .setErrorListener { error ->
                println("MediaPipe Error: ${error.message}")
            }

        faceDetector = FaceDetector.createFromOptions(context, optionsBuilder.build())
    }

    fun detect(bitmap: Bitmap, timestamp: Long) {
        backgroundExecutor.execute {
            val mpImage = BitmapImageBuilder(bitmap).build()
            faceDetector?.detectAsync(mpImage, timestamp)
        }
    }

    private fun processResult(result: FaceDetectorResult) {
        val face = result.detections().firstOrNull()?.let { detection ->
            val box = detection.boundingBox()
            FaceDetection(
                boundingBox = RectF(box.left, box.top, box.right, box.bottom),
                confidence = detection.categories().firstOrNull()?.score() ?: 0f,
                landmarks = if (detection.keypoints().isPresent) {
                    detection.keypoints().get().map { keypoint ->
                        Landmark(
                            x = keypoint.x(),
                            y = keypoint.y(),
                            label = if (keypoint.label().isPresent) keypoint.label().get() else null
                        )
                    }
                } else {
                    emptyList()
                }
            )
        }
        onResult(face)
    }

    fun release() {
        backgroundExecutor.shutdown()
        faceDetector?.close()
    }
}
